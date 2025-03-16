package com.ihortymkiv.web;

import com.ihortymkiv.chemistry.Atom;
import com.ihortymkiv.chemistry.Compound;
import org.json.JSONObject;

import java.util.*;


public final class CompoundJSONGenerator {
    /**
     * Generate JSON from Compound.
     * @param compound Compound
     * @return JSONObject
     */
    public static JSONObject generate(Compound compound) {
        Objects.requireNonNull(compound, "Compound must be non-null");
        List<JSONObject> nodes = new ArrayList<>();
        List<JSONObject> links = new ArrayList<>();
        Set<Atom.Bond> bonds = new HashSet<>();
        for (Atom atom : compound.getAtoms()) {
            JSONObject node = new JSONObject();
            node.put("id", atom.getId());
            node.put("symbol", atom.getChemicalElement().symbol());
            bonds.addAll(atom.getBonds());
            nodes.add(node);
        }
        for (Atom.Bond bond : bonds) {
            JSONObject link = new JSONObject();
            link.put("source", bond.from().getId());
            link.put("target", bond.to().getId());
            link.put("value", bond.bondOrder());
            links.add(link);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nodes", nodes);
        jsonObject.put("links", links);
        return jsonObject;
    }

    private CompoundJSONGenerator() {}
}
