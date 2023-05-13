package com.lightdev6.cscript;

import java.util.HashMap;
import java.util.Map;

public class CScriptInstance {
    private CScriptClass clas;
    private final Map<String, Object> fields = new HashMap<>();

    CScriptInstance(CScriptClass clas){
        this.clas = clas;
    }

    @Override
    public String toString(){
        return clas.name + " instance";
    }

    Object get(Token name){
        if (fields.containsKey(name.lexeme)){
            return fields.get(name.lexeme);
        }

        CScriptFunction method = clas.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property " + name.lexeme + "'.");
    }

    void set(Token name, Object value){
        fields.put(name.lexeme, value);
    }
}
