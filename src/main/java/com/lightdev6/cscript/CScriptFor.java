package com.lightdev6.cscript;

import java.util.Arrays;

public class CScriptFor {
    private final Stmt.For declaration;
    private final Environment closure;

    CScriptFor(Stmt.For declaration, Environment closure){
        this.declaration = declaration;
        this.closure = closure;

    }

    public Void run(Interpreter interpreter){
        Environment environment = new Environment(closure);
        environment.define(declaration.variable.lexeme, 0.0d);
        if (declaration.body instanceof Stmt.Block block){
            for (int i = 0; i < (double)interpreter.evaluate(declaration.left); i++) {
                environment.assign(declaration.variable, i);
                System.out.println(environment.get(declaration.variable));
                interpreter.executeBlock(block.statements, environment);

            }

        } else {
            Stmt.Block block = new Stmt.Block(Arrays.asList(declaration.body));
            for (int i = 0; i < (double)interpreter.evaluate(declaration.left); i++) {
                environment.assign(declaration.variable, i);
                interpreter.executeBlock(block.statements, environment);
            }

        }
        return null;
    }
}
