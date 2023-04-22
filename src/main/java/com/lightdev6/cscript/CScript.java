package com.lightdev6.cscript;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CScript {
    private final Interpreter interpreter = new Interpreter(this);
    boolean hadError = false;
    boolean hadRuntimeError = false;
    private final Player player;
    private final String source;

    public CScript(String source, Player player){
        this.source = source;
        this.player = player;
        run(source);
    }

    private void run (String source){
        Scanner scanner = new Scanner(source, this);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens, this);
        List<Stmt> statements = parser.parse();

        if (hadError) return;
        Resolver resolver = new Resolver(interpreter, this);
        resolver.resolve(statements);
        if (hadError) return;
        interpreter.interpret(statements);
        //System.out.println(new AstPrinter().print(expression));
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
        player.sendSystemMessage(Component.literal(message));
    }

}
