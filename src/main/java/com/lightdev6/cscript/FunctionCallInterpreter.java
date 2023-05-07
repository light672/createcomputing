package com.lightdev6.cscript;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallInterpreter extends Interpreter{
    FunctionCallInterpreter(CScript main) {
        super(main);
    }

    void callFunction(List<Stmt> statements, String functionName, List<Object> arguments){
        interpret(statements);
        Object callee = super.evaluate(new Expr.Variable(new Token(TokenType.IDENTIFIER, functionName, null, 0)));

        CScriptCallable function = (CScriptCallable)callee;
        if (arguments.size() != function.arity()){
            throw new RuntimeError(null, "Signal " + functionName + "needed " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        function.call(this, arguments);
    }

    @Override
    void interpret(List<Stmt> statements) {
        for (Stmt statement : statements){
            if (statement instanceof Stmt.While || statement instanceof Stmt.Print || statement instanceof Stmt.Return || statement instanceof Stmt.If
                    || statement instanceof Stmt.Expression || statement instanceof Stmt.Block){
                statements.remove(statement);
            }
        }
        super.interpret(statements);
    }
}
