package com.lightdev6.zinc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZincStructure implements ZincCallable{
    private final Stmt.Structure declaration;
    public ZincStructure(Stmt.Structure declaration){
        this.declaration = declaration;
    }


    @Override
    public int arity() {
        return declaration.fields.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
        ZincObject object = new ZincObject(this, statementsToMap(declaration.fields, arguments));

        return object;
    }

    Map<String, Object> statementsToMap(List<Stmt.Var> statements, List<Object> arguments){
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < statements.size(); i++) {
            fields.put(statements.get(i).name.lexeme, arguments.get(i));
        }
        return fields;
    }

    public List<String> getFields(){
        List<String> list = new ArrayList<>();
        for (Stmt.Var var : declaration.fields){
            list.add(var.name.lexeme);
        }
        return list;
    }

    public String getName(){
        return declaration.name.lexeme;
    }

    @Override
    public String toString() {
        return declaration.name.lexeme;
    }
}
