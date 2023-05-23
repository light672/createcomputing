package com.lightdev6.zinc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>{

    private enum FunctionType{
        NONE,
        FUNCTION,
    }


    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Zinc main;
    private FunctionType currentFunction = FunctionType.NONE;



    Resolver(Interpreter interpreter, Zinc main){
        this.main = main;
        this.interpreter = interpreter;
    }


    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt){
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt){
        declare(stmt.name);
        if(stmt.initializer != null){
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr){
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE){
            main.error(expr.name, "Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr){
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt){
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt){
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitStructureStmt(Stmt.Structure stmt) {
        declare(stmt.name);
        define(stmt.name);
        resolveStructure(stmt);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt){
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }


    @Override
    public Void visitReturnStmt(Stmt.Return stmt){
        if (currentFunction == FunctionType.NONE){
            main.error(stmt.keyword, "Can't return from top-level code.");
        }
        if (stmt.value != null){
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt){
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        resolveFor(stmt);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr){
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr){
        resolve(expr.callee);
        for (Expr argument : expr.arguments){
            resolve(argument);
        }
        return null;
    }



    @Override
    public Void visitGroupingExpr(Expr.Grouping expr){
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr){
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr){
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr){
        resolve(expr.right);
        return null;
    }

    void resolve(List<Stmt> statements){
        for (Stmt statement : statements){
            resolve(statement);
        }
    }

    private void resolveLocal(Expr expr, Token name){
        for (int i = scopes.size() - 1; i >= 0; i--){
            if (scopes.get(i).containsKey(name.lexeme)){
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Stmt.Function function, FunctionType type){
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params){
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void resolveStructure(Stmt.Structure structure){
        beginScope();
        for (Stmt.Var variable : structure.fields){
            declare(variable.name);
            define(variable.name);
        }
        endScope();
    }

    private void resolveFor(Stmt.For forr){
        resolve(forr.left);
        beginScope();
        declare(forr.variable);
        define(forr.variable);
        if (forr.body instanceof Stmt.Block block){
            resolve(block.statements);
        } else {
            resolve(forr.body);
        }
        endScope();
    }

    private void resolve(Stmt stmt){
        stmt.accept(this);
    }
    private void resolve(Expr expr){
        expr.accept(this);
    }

    private void beginScope(){
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope(){
        scopes.pop();
    }

    private void declare(Token name){
        if (scopes.isEmpty()) return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)){
            main.error(name, "Already a variable with this name in this scope");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name){
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
}
