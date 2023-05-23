package com.lightdev6.zinc;

import java.util.HashMap;
import java.util.Map;

public class ZincObject {
    private ZincStructure structure;
    private final Map<String, Object> fields;

    public ZincObject(ZincStructure structure, Map<String, Object> fields){
        this.structure = structure;
        this.fields = fields;
    }



    Object get(Token name){
        if (!fields.containsKey(name.lexeme))
            throw new RuntimeError(name, "Field '" + name.lexeme + "' does not exist in struct '" + structure.toString() + "'.");

        return fields.get(name.lexeme);
    }


    void set(Token name, Object value){
        if (!fields.containsKey(name.lexeme))
            throw new RuntimeError(name, "Field '" + name.lexeme + "' does not exist in struct '" + structure.toString() + "'.");
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return structure.toString() + " object";
    }

    public Map<String, Object> getFields(){
        return fields;
    }
    public ZincStructure getStructure(){
        return structure;
    }
}
