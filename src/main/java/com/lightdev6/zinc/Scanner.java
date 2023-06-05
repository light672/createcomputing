package com.lightdev6.zinc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.lightdev6.zinc.TokenType.*;

public class Scanner {
    private final String source;
    private final Zinc main;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;


    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("struct", STRUCT);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("true", TRUE);
        keywords.put("for", FOR);
        keywords.put("func", FUN);
        keywords.put("if", IF);
        keywords.put("null", NIL);
        keywords.put("or", OR);
        keywords.put("return", RETURN);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("from", FROM);

    }

    Scanner(String source, Zinc main){
        this.main = main;
        this.source = source;
    }

    List<Token> scanTokens(){
        while (!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd(){
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-':
                addToken(match('=') ? MINUS_EQUAL : MINUS);
                break;
            case '+':
                addToken(match('=') ? PLUS_EQUAL : PLUS);
                break;
            case ';': addToken(SEMICOLON); break;
            case ':': addToken(COLON); break;
            case '*':
                addToken(match('=') ? STAR_EQUAL : STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')){
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(match('=') ? SLASH_EQUAL : SLASH);
                }
                break;
            case '^':
                addToken(match('=') ? CARET_EQUAL : CARET);
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)){
                    identifier();
                } else {
                    main.error(line, "Unexpected character.");
                    break;
                }

        }


    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())){
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext(){
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void string(){
        while (peek() != '"' && !isAtEnd()){
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()){
            main.error(line, "Unterminated string");
            return;
        }

        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek(){
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected){
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current ++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }





}
