package com.ihortymkiv.rihn;

import com.ihortymkiv.chemistry.ChemicalElement;

import java.util.*;

/**
 * The Semantic Analyzer for the RIHNParser.
 * <p>
 * This class traverses the {@link Hydrocarbon} AST produced by the {@link Parser}
 * to enforce chemical and IUPAC naming rules (semantics) that cannot be captured by the grammar alone.
 * <p>
 * It checks for:
 * <ul>
 * <li>Carbon valency constraints (no carbon can have more than 4 bonds).</li>
 * <li>Rules for cyclic compounds (must have >= 3 carbons).</li>
 * <li>Locant validation (in range, not duplicated, correct order).</li>
 * <li>The "lowest set of locants" rule.</li>
 * <li>Correct multiplier-to-locant count (e.g., "diene" requires two locants).</li>
 * </ul>
 * <p>
 * It uses the Visitor pattern to traverse the {@link Type} nodes of the AST.
 */
class SemanticAnalyzer implements Type.Visitor<Void> {
    private static int ALKENE_BOND_ORDER = 2;
    private static int ALKYNE_BOND_ORDER = 3;
    private int carbonCount;
    private Map<Integer, Integer> carbonValencies;

    /**
     * Analyzes the {@link Hydrocarbon} AST for semantic and chemical validity.
     *
     * @param hydrocarbon The AST to analyze.
     * @throws SemanticAnalyzerException if any chemical or naming rule is violated.
     */
    public void analyze(Hydrocarbon hydrocarbon) {
        carbonValencies = new HashMap<>();
        carbonCount = hydrocarbon.stem.value;
        if (hydrocarbon.isCyclic && carbonCount < 3) {
            throw new SemanticAnalyzerException("Carbon chain length must be at least 3 for it to be cyclic.");
        }
        hydrocarbon.type.accept(this);
    }

    /**
     * Visits an Alkane type. (Does nothing, as Alkanes have no locants to check).
     */
    @Override
    public Void visit(Type.Alkane alkane) {
        return null;
    }

    /**
     * Visits an Alkene type, analyzing its locants and multipliers.
     */
    @Override
    public Void visit(Type.Alkene alkene) {
        analyzeGroup(alkene.group, ALKENE_BOND_ORDER);
        Locants locants = alkene.group.locants;
        enforceLowestSetRuleForLocants(locants.locants, reverseLocants(locants.locants));
        return null;
    }

    /**
     * Visits an Alkyne type, analyzing its locants and multipliers.
     */
    @Override
    public Void visit(Type.Alkyne alkyne) {
        analyzeGroup(alkyne.group, ALKYNE_BOND_ORDER);
        Locants locants = alkyne.group.locants;
        enforceLowestSetRuleForLocants(locants.locants, reverseLocants(locants.locants));
        return null;
    }

    /**
     * Visits an Enyne type, analyzing combined locant rules for both alkene and alkyne groups.
     */
    @Override
    public Void visit(Type.Enyne enyne) {
        Group alkeneGroup = enyne.alkene.group;
        Group alkyneGroup = enyne.alkyne.group;
        analyzeGroup(alkeneGroup, ALKENE_BOND_ORDER);
        analyzeGroup(alkyneGroup, ALKYNE_BOND_ORDER);
        analyzeLocantsInEnyne(alkeneGroup.locants, alkyneGroup.locants);
        return null;
    }

    private void analyzeLocantsInEnyne(Locants alkeneLocants, Locants alkyneLocants) {
        // Combine locants together
        List<Integer> locants = new ArrayList<>(alkeneLocants.locants);
        locants.addAll(alkyneLocants.locants);
        locants = locants.stream().sorted().toList();

        List<Integer> reversedLocants = reverseLocants(locants);
        if (reversedLocants.equals(locants)) {
            // It's a tie, ensure double bonds have the lowest locants (alkenes)
            int minLocant = locants.getFirst();
            boolean original_is_alkyne = alkyneLocants.locants.contains(minLocant);

            int reversedOriginalLocant = carbonCount - minLocant;
            boolean reversed_is_alkene = alkeneLocants.locants.contains(reversedOriginalLocant);

            if (original_is_alkyne && reversed_is_alkene) {
                throw new SemanticAnalyzerException(
                        String.format(
                                "Alkene locant (%d) expected to be lower than alkyne's (%d).",
                                reversedOriginalLocant, minLocant
                        )
                );
            }
        }
        enforceLowestSetRuleForLocants(locants, reversedLocants);
    }

    private void analyzeGroup(Group group, int bondOrder) {
        analyzeLocants(group.locants, bondOrder);
        if (Objects.nonNull(group.multiplyingAffix)) {
            analyzeMultiplyingAffix(group.multiplyingAffix, group.locants.locants.size());
        }
    }

    private void analyzeLocants(Locants locants, int bondOrder) {
        if (carbonCount < 2) {
            throw new SemanticAnalyzerException("Can't specify locants for a carbon count less than 2");
        }
        if (!locants.locants.stream().sorted().toList().equals(locants.locants)) {
            throw new SemanticAnalyzerException("Locants must be in order of increasing value");
        }

        Set<Integer> seenLocants = new HashSet<>();
        for (int i : locants.locants) {
            if (i < 1 || i > carbonCount - 1) {
                throw new SemanticAnalyzerException(
                        String.format("Invalid locant value %d, must be in range (0, %d)", i, carbonCount)
                );
            }
            if (seenLocants.contains(i)) {
                throw new SemanticAnalyzerException(String.format("Locant %d has already been specified", i));
            }
            seenLocants.add(i);
            updateValency(i, bondOrder);
            updateValency(i + 1, bondOrder);
        }
    }

    private void updateValency(int carbon, int bondOrder) {
        carbonValencies.putIfAbsent(carbon, ChemicalElement.Carbon.normalValence());
        carbonValencies.compute(carbon, (k, v) -> (v - bondOrder));
        if (carbonValencies.get(carbon) < 0) {
            throw new SemanticAnalyzerException(
                    String.format("Carbon #%d has exceeded available valency", carbon)
            );
        }
    }

    private void enforceLowestSetRuleForLocants(List<Integer> locants, List<Integer> reversedLocants) {
        for (int i = 0; i < reversedLocants.size(); i++) {
            int cmp = Integer.compare(reversedLocants.get(i), locants.get(i));
            if (cmp < 0) {
                throw new SemanticAnalyzerException(
                        String.format("Lowest set of locants rule violated, %s could be %s", locants, reversedLocants)
                );
            }
        }
    }

    private List<Integer> reverseLocants(List<Integer> locants) {
        return locants.stream().map(locant -> carbonCount - locant).sorted().toList();
    }

    private void analyzeMultiplyingAffix(MultiplyingAffix multiplyingAffix, int locantsCount) {
        int multiplier = Objects.isNull(multiplyingAffix) ? 1 : multiplyingAffix.value;
        if (multiplier != locantsCount) {
            throw new SemanticAnalyzerException(
                    String.format("Invalid multiplier (%d) for number of locants (%d)", multiplier, locantsCount),
                    multiplyingAffix.token
            );
        }
    }

}
