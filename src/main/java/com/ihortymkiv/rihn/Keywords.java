package com.ihortymkiv.rihn;

import java.util.Map;

/**
 * A utility class holding static maps for IUPAC keywords.
 * This provides a single source of truth for stems (e.g., "meth" -> 1)
 * and multiplying affixes (e.g., "di" -> 2).
 */
final class Keywords {
    /**
     * Map with stems as keys and respective numbers as values.
     */
    static final Map<String, Integer> STEMS = Map.of(
            "meth", 1,
            "eth", 2,
            "prop", 3,
            "but", 4,
            "pent", 5,
            "hex", 6,
            "hept", 7,
            "oct", 8,
            "non", 9,
            "dec", 10
    );

    /**
     * Map with multiplying affixes as keys and respective numbers as values.
     */
    static final Map<String, Integer> MULTIPLYING_AFFIXES = Map.of(
            "di", 2,
            "tri", 3,
            "tetra", 4,
            "penta", 5,
            "hexa", 6,
            "hepta", 7,
            "octa", 8,
            "nona", 9
    );

    private Keywords() {} // avoid instantiation
}
