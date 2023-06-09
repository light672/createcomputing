package com.lightdev6.zinc;

import java.util.ArrayList;
import java.util.List;

import static com.lightdev6.zinc.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException{}
    private final List<Token> tokens;
    private final Zinc main;
    private int current = 0;
    Parser(List<Token> tokens, Zinc main){
        this.main = main;
        this.tokens = tokens;
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment(){
        Expr expr = or();

        if (match(EQUAL)){
            Token equals = previous();
            Expr value = assignment();
            if(expr instanceof Expr.Variable){
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Get get){
                return new Expr.Set(get.object, get.name, value);
            }

            error(equals, "Invalid assignment target.");
        } else if (match(PLUS_EQUAL) || match(MINUS_EQUAL) || match(STAR_EQUAL) || match(SLASH_EQUAL) || match(CARET_EQUAL)){
            Token operatorEqual = previous();
            Token operator = createTokenFromOperatorEquals(operatorEqual);
            Expr value = assignment();
            if (expr instanceof Expr.Variable){
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, new Expr.Binary(expr, operator, value));
            }
        }
        return expr;
    }

    private Expr or(){
        Expr expr = and();

        while (match(OR)){
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and(){
        Expr expr = equality();
        while(match(AND)){
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }



    private Expr equality(){
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;

    }

    private boolean match(TokenType... types){
        for (TokenType type : types){
            if (check(type)){
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type){
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance(){
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private Token previous(){
        return tokens.get(current - 1);
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term(){
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = exponent();

        while (match(SLASH, STAR)){
            Token operator = previous();
            Expr right = exponent();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr exponent(){
        Expr expr = unary();

        while (match(CARET)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary(){
        if (match(BANG, MINUS)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }

    private Expr call(){
        Expr expr = primary();
        while (true){
            if (match(LEFT_PAREN)){
                expr = finishCall(expr);
            } else if (match(DOT)){
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee){
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)){
            do {
                if (arguments.size() >= 255){
                    error(peek(), "I don't even want to know why you have 255 arguments. Please, lower the amount you have.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary(){
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)){
            return new Expr.Literal(previous().literal);
        }

        if (match(IDENTIFIER)){
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message){
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message){
        main.error(token, message);
        return new ParseError();
    }

    private void synchronize(){
        advance();

        while(!isAtEnd()){
            if (previous().type == SEMICOLON) return;
            switch (peek().type){
                case STRUCT:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()){
            statements.add(declaration());
        }
        return statements;
    }

    private Stmt statement(){
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt returnStatement(){
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)){
            value = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Token initializer;

        if (match(IDENTIFIER)){
            initializer = previous();
        } else {
            throw error(previous(), "Expect initializer after '('.");
        }

        Token colon = consume(COLON, "Expect ':' after initializer.");
        Expr left = expression();
        consume(RIGHT_PAREN,"Expect ')' after loop amount.");
        Stmt body = statement();

        return new Stmt.For(initializer, left, colon, body);
    }



    private Stmt whileStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;

        if(match(ELSE)){
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }



    private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }


    private Stmt declaration(){
        try {
            if (match(STRUCT)) return struct();
            if(match(FUN)) return function("function");
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error){
            synchronize();
            return null;
        }
    }



    private Stmt.Function function(String kind){
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if(!check(RIGHT_PAREN)){
            do{
                if (parameters.size() >= 255){
                    error(peek(), "Can't have more that 255 parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    private Stmt struct(){
        Token name = consume(IDENTIFIER, "Expect struct name.");
        consume(LEFT_BRACE, "Expect '{' before struct body");
        List<Stmt.Var> fields = new ArrayList<>();
        while (!check(RIGHT_BRACE)){
            consume(VAR, "Can only define variables in structures.");
            Token fieldName = consume(IDENTIFIER, "Expect variable name.");
            consume(SEMICOLON, "Expected ';' after variable name");
            fields.add(new Stmt.Var(fieldName, null));
        }
        consume(RIGHT_BRACE, "Expect '}' after structure body.");
        return new Stmt.Structure(name, fields);
    }

    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)){
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Token createTokenFromOperatorEquals(Token operatorEqual){
        switch (operatorEqual.type){
            case PLUS_EQUAL: return new Token(PLUS, "+", null, operatorEqual.line);
            case MINUS_EQUAL: return new Token(MINUS, "-", null, operatorEqual.line);
            case STAR_EQUAL: return new Token(STAR, "*", null, operatorEqual.line);
            case SLASH_EQUAL: return new Token(SLASH, "/", null, operatorEqual.line);
            case CARET_EQUAL: return new Token(CARET, "^", null, operatorEqual.line);
        }
        //Unreachable
        return null;
    }



}
