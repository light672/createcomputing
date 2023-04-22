package com.lightdev6.cscript;

import java.util.List;
import java.util.Map;

class CScriptClass implements CScriptCallable{
    final String name;
    private final Map<String, CScriptFunction> methods;
    CScriptClass(String name, Map<String, CScriptFunction> methods){
        this.name = name;
        this.methods = methods;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        CScriptInstance instance = new CScriptInstance(this);
        CScriptFunction initializer = findMethod("init");
        if (initializer != null){
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity(){
        CScriptFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    CScriptFunction findMethod(String name){
        if (methods.containsKey(name)){
            return methods.get(name);
        }
        return null;
    }
}