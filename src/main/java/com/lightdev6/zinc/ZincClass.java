package com.lightdev6.zinc;

import java.util.List;
import java.util.Map;

class ZincClass implements ZincCallable {
    final String name;
    private final Map<String, ZincFunction> methods;
    final ZincClass superclass;
    ZincClass(String name, ZincClass superclass, Map<String, ZincFunction> methods){
        this.name = name;
        this.methods = methods;
        this.superclass = superclass;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments, Token paren){
        ZincInstance instance = new ZincInstance(this);
        ZincFunction initializer = findMethod("init");
        if (initializer != null){
            initializer.bind(instance).call(interpreter, arguments, paren);
        }
        return instance;
    }

    @Override
    public int arity(){
        ZincFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    ZincFunction findMethod(String name){
        if (methods.containsKey(name)){
            return methods.get(name);
        }
        if(superclass != null){
            return superclass.findMethod(name);
        }
        return null;
    }
}
