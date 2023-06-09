//Thank you CRAFTING INTERPRETERS for this code
package com.lightdev6.zinc;


public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, COLON, SEMICOLON, SLASH, STAR,
    CARET,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    PLUS_EQUAL, MINUS_EQUAL,
    STAR_EQUAL, SLASH_EQUAL,
    CARET_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, STRUCT, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    RETURN, TRUE, VAR, WHILE, FROM,

    EOF
}
