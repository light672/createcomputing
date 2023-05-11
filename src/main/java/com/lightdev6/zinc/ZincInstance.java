package com.lightdev6.zinc;

import java.util.HashMap;
import java.util.Map;

public class ZincInstance {
    private ZincClass clas;
    private final Map<String, Object> fields = new HashMap<>();

    ZincInstance(ZincClass clas){
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

        ZincFunction method = clas.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property " + name.lexeme + "'.");
    }

    void set(Token name, Object value){
        fields.put(name.lexeme, value);
    }
}
