package com.lightdev6.zinc;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;

import java.util.List;

public class Zinc {

    boolean hadError = false;
    boolean hadRuntimeError = false;
    private ComputerBlockEntity computer;

    public Zinc(String source, ComputerBlockEntity computer){
        this.computer = computer;
        run(source);
    }

    public Zinc(String source, ComputerBlockEntity computer, String functionName, List<Object> arguments){
        this.computer = computer;
        runFunction(source, functionName, arguments);
    }

    private void run (String source){
        Scanner scanner = new Scanner(source, this);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, this);
        List<Stmt> statements = parser.parse();

        if (hadError) return;
        final Interpreter interpreter = new Interpreter(this);
        Resolver resolver = new Resolver(interpreter, this);
        resolver.resolve(statements);
        if (hadError) return;
        interpreter.interpret(statements);
    }
    private void runFunction (String source, String functionName, List<Object> arguments){
        Scanner scanner = new Scanner(source, this);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, this);
        List<Stmt> statements = parser.parse();

        if (hadError) return;
        final FunctionCallInterpreter interpreter = new FunctionCallInterpreter(this);
        Resolver resolver = new Resolver(interpreter, this);
        resolver.resolve(statements);
        if (hadError) return;
        interpreter.callFunction(statements, functionName,arguments);
    }

    void error(int line, String message){
        report(line, "", message);
    }
    void error(Token token, String message){
        if (token.type == TokenType.EOF){
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    private void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    void runtimeError(RuntimeError error){
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    public void log(String message){
        computer.setTerminal(computer.getTerminal() + message + "\n");
    }

}
