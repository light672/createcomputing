package com.lightdev6.zinc;

import java.util.List;

public class FunctionCallInterpreter extends Interpreter{
    FunctionCallInterpreter(Zinc main, Environment globals) {
        super(main, globals);
    }

    void callFunction(List<Stmt> statements, String functionName, List<Object> arguments){
        interpret(statements);
        for (int i = 0; i < arguments.size(); i++) {
            Object o = arguments.get(i);
            if (o instanceof ZincStructureConversionObject c)
                arguments.set(i, new ZincObject((ZincStructure)globals.get(Environment.createIDToken(c.getStructureName())), c.getFields()));
        }
        Object callee = super.evaluate(new Expr.Variable(new Token(TokenType.IDENTIFIER, functionName, null, 0)));

        ZincCallable function = (ZincCallable)callee;
        if (arguments.size() != function.arity()){
            throw new RuntimeError(null, "Signal " + functionName + "needed " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        function.call(this, arguments, null);
    }

    @Override
    void interpret(List<Stmt> statements) {
        statements.removeIf(statement -> !(statement instanceof Stmt.Function));
        super.interpret(statements);
    }
}
