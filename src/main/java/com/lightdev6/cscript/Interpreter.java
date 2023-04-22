package com.lightdev6.cscript;

import ca.weblite.objc.Runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message){
        super(message);
        this.token = token;
    }
}

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter(){
        globals.define("clock", new CScriptCallable() {
            @Override
            public int arity() {return 0;}

            @Override public Object call(Interpreter interpreter, List<Object> arguments){
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString(){ return "<native fn> ";}
        });
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt){
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr){
        Object object = evaluate(expr.object);
        if (!(object instanceof CScriptInstance)){
            throw new RuntimeError(expr.name, "Only instances have fields");
        }
        Object value = evaluate(expr.value);
        ((CScriptInstance)object).set(expr.name, value);
        return value;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt){
        CScriptFunction function = new CScriptFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
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
        if (!(callee instanceof CScriptCallable)){
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        CScriptCallable function = (CScriptCallable)callee;
        if (arguments.size() != function.arity()){
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
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
    public Void visitPrintStmt(Stmt.Print stmt){
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
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
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt){
        environment.define(stmt.name.lexeme, null);

        Map<String, CScriptFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods){
            CScriptFunction function = new CScriptFunction(method,environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }


        CScriptClass clas = new CScriptClass(stmt.name.lexeme, methods);
        environment.assign(stmt.name, clas);
        return null;
    }

    @Override
    public Object visitThisExpr(Expr.This expr){
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr){
        Object object = evaluate(expr.object);
        if (object instanceof CScriptInstance){
            return ((CScriptInstance) object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instances have properties.");
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

    private Object evaluate(Expr expr){
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
        try {
            for (Stmt statement : statements){
                execute(statement);
            }
        } catch (RuntimeError error){
            CScript.runtimeError(error);
        }
    }

    private void execute(Stmt stmt){
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth){
        locals.put(expr, depth);
    }

    private String stringify(Object object){
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
