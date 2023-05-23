package com.lightdev6.zinc;

import java.util.List;

public abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitStructureStmt(Structure stmt);
    R visitFunctionStmt(Function stmt);
    R visitIfStmt(If stmt);
    R visitReturnStmt(Return stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
    R visitForStmt(For stmt);
  }
  public static class Block extends Stmt {
    public Block(List<Stmt> statements) {
     this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }
  public static class Expression extends Stmt {
    public Expression(Expr expression) {
     this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
  public static class Structure extends Stmt {
    public Structure(Token name, List<Stmt.Var> fields) {
     this.name = name;
     this.fields = fields;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitStructureStmt(this);
    }

    final Token name;
    final List<Stmt.Var> fields;
  }
  public static class Function extends Stmt {
    public Function(Token name, List<Token> params, List<Stmt> body) {
     this.name = name;
     this.params = params;
     this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
  }
  public static class If extends Stmt {
    public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
     this.condition = condition;
     this.thenBranch = thenBranch;
     this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  public static class Return extends Stmt {
    public Return(Token keyword, Expr value) {
     this.keyword = keyword;
     this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
  }
  public static class Var extends Stmt {
    public Var(Token name, Expr initializer) {
     this.name = name;
     this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }
  public static class While extends Stmt {
    public While(Expr condition, Stmt body) {
     this.condition = condition;
     this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }
  public static class For extends Stmt {
    public For(Token variable, Expr left, Token split, Stmt body) {
     this.variable = variable;
     this.left = left;
     this.split = split;
     this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitForStmt(this);
    }

    final Token variable;
    final Expr left;
    final Token split;
    final Stmt body;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
