package com.lightdev6.zinc;

import java.util.*;

class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message){
        super(message);
        this.token = token;
    }
}

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    final Environment globals;
    private Environment environment;
    public boolean stopRequested = false;
    private final Map<Expr, Integer> locals = new HashMap<>();
    private final Zinc main;

    public Environment getGlobals(){
        return globals;
    }

    Interpreter(Zinc main, Environment globals){
        this.main = main;
        this.globals = globals;
        environment = this.globals;

    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt){
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }



    @Override
    public Void visitFunctionStmt(Stmt.Function stmt){
        ZincFunction function = new ZincFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitStructureStmt(Stmt.Structure stmt) {
        ZincStructure structure = new ZincStructure(stmt);
        environment.define(stmt.name.lexeme, structure);
        return null;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof ZincObject zObject)
            return zObject.get(expr.name);
        throw new RuntimeError(expr.name, "Only objects have properties.");
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof ZincObject zincObject))
            throw new RuntimeError(expr.name, "Only objects have fields");
        Object value = evaluate(expr.value);
        zincObject.set(expr.name, value);
        return value;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Object right = evaluate(expr.right);
        switch (expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS:
                return -(double)right;
        }
        //Unreachable
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr){
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments){
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof ZincCallable)){
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        ZincCallable function = (ZincCallable)callee;
        if (arguments.size() != function.arity()){
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments, expr.paren);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt){
        Object value = null;
        if (stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr){
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if (distance != null){
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr){
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt){
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr){
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type){
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
            case MINUS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String){
                    return (String)left + (String) right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case CARET:
                checkNumberOperands(expr.operator, left, right);
                return Math.pow((double)left, (double)right);
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
        }
        //Unreachable
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt){
        evaluate(stmt.expression);
        return null;
    }



    @Override
    public Void visitIfStmt(Stmt.If stmt){
        if (isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr){
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR){
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt){
        while(isTruthy(evaluate(stmt.condition))){
            if (stopRequested)
                return null;
            execute(stmt.body);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e){
                break;
            }
        }
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        ZincFor forr = new ZincFor(stmt, environment);
        forr.run(this);
        return null;
    }



    public void throwError(Token t, String message){
        throw new RuntimeError(t, message);
    }

    private Object lookUpVariable(Token name, Expr expr){
        Integer distance = locals.get(expr);
        if (distance != null){
            return environment.getAt(distance, name.lexeme);
        } else{
            return globals.get(name);
        }
    }

    void executeBlock(List<Stmt> statements, Environment environment){
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements){
                if (stopRequested)
                    return;
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private void checkNumberOperand(Token operator, Object operand){
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right){
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers");
    }

    Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private boolean isTruthy(Object object){
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
        /*
            Crafting Interpreters explains this really weird in my opinion so for anyone learning how this mod works here you go:
            If the value is a true or false value, they will obviously return their value
            If the value is something, or not null, this returns true
            If the value is null, it returns false

            Ex:
                (true) will return true
                (false) will return false
                (20.3) will return true
                (null) will return false
        */
    }

    private boolean isEqual(Object a, Object b){
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    void interpret(List<Stmt> statements){
        List<Stmt> structOrFunctionStatements = new ArrayList<>();
        List<Stmt> theRestOfThem = new ArrayList<>();

        for (Stmt statement : statements){
            if (statement instanceof Stmt.Structure || statement instanceof Stmt.Function){
                structOrFunctionStatements.add(statement);
            } else {
                theRestOfThem.add(statement);
            }
        }

        try {
            for (Stmt statement : structOrFunctionStatements){
                execute(statement);
            }
            globals.resolveEnvironment();
            for (Stmt statement : theRestOfThem){
                if (stopRequested)
                    return;
                execute(statement);
            }
        } catch (RuntimeError error){
            main.runtimeError(error);
        }
    }

    private void execute(Stmt stmt){
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth){
        locals.put(expr, depth);
    }

    public static String stringify(Object object){
        if (object == null) return "null";
        if (object instanceof Double){
            String text = object.toString();
            if (text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}
