package com.lightdev6.zinc;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;

import java.util.List;

public class Zinc {

    boolean hadError = false;
    boolean hadRuntimeError = false;
    private ComputerBlockEntity computer;
    public Interpreter interpreter = null;
    public FunctionCallInterpreter functionInterpreter = null;

    public Zinc(ComputerBlockEntity computer){
        this.computer = computer;
        interpreter = new Interpreter(this, Environment.defaultGlobals(computer));
        functionInterpreter = new FunctionCallInterpreter(this, computer.getGlobals());
    }







    public void run (String source){
        Scanner scanner = new Scanner(source, this);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, this);
        List<Stmt> statements = parser.parse();

        if (hadError) return;

        Resolver resolver = new Resolver(interpreter, this);
        resolver.resolve(statements);
        if (hadError) return;
        interpreter.interpret(statements);
        computer.setGlobals(interpreter.getGlobals());
    }
    public void runFunction (String source, String functionName, List<Object> arguments){
        if (computer.getRunning()) {
            Scanner scanner = new Scanner(source, this);
            List<Token> tokens = scanner.scanTokens();
            Parser parser = new Parser(tokens, this);
            List<Stmt> statements = parser.parse();

            if (hadError) return;

            Resolver resolver = new Resolver(functionInterpreter, this);
            resolver.resolve(statements);
            if (hadError) return;
            functionInterpreter.callFunction(statements, functionName, arguments);
            computer.setGlobals(functionInterpreter.getGlobals());
        }
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
        if (error.token != null){
            System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        } else {
            System.err.println(error.getMessage() + "\n[Occurred outside the script]");
        }

        hadRuntimeError = true;
    }

    public static void log(String message, ComputerBlockEntity computer){
        computer.setTerminal(computer.getTerminal() + message + "\n");
    }


}
