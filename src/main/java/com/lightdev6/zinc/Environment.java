package com.lightdev6.zinc;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;

import java.util.*;

public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();


    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    void remove(Token name) {
        values.remove(name);
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    public void resolveEnvironment(){
        for (Map.Entry<String, Object> entry : values.entrySet()){
            if(entry.getValue() instanceof ZincStructureConversionObject o){
                values.replace(entry.getKey(), new ZincObject((ZincStructure)get(createIDToken(o.getStructureName())), o.getFields()));
            }
        }
    }


    public static Environment defaultGlobals(ComputerBlockEntity computer) {
        Environment globals = new Environment();
        globals.define("clock", new ZincCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn> ";
            }
        });
        globals.define("wait", new ZincCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                try {
                    Thread.sleep(Math.round((double) arguments.get(0) * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String toString() {
                return "<native fn> ";
            }
        });
        globals.define("string", new ZincCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                return Interpreter.stringify(arguments.get(0));
            }
        });
        globals.define("print", new ZincCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                Zinc.log(Interpreter.stringify(arguments.get(0)), computer);
                return null;
            }
        });
        globals.define("display", new ZincCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                if (!(arguments.get(0) instanceof Double d)) {
                    interpreter.throwError(paren, "The first argument for 'call' must be a whole number.");
                    return null;
                }
                if (d % 1 != 0){
                    interpreter.throwError(paren, "The first argument for 'call' must be a whole number.");
                    return null;
                }
                computer.setDisplayFreq(Interpreter.stringify(arguments.get(1)), (int) Math.round((double)arguments.get(0)));
                return null;
            }
        });
        globals.define("write", new ZincCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                return computer.findAndModifyPlate(Interpreter.stringify(arguments.get(0)), arguments.get(1));
            }
        });
        globals.define("read", new ZincCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token paren) {
                return computer.readPlate(Interpreter.stringify(arguments.get(0)));
            }
        });
        globals.define("Item", createStructure("Item", Arrays.asList("name", "id", "count")));
        return globals;
    }


    public static Token createIDToken(String name){
        return new Token(TokenType.IDENTIFIER, name, null,0);
    }

    private static Stmt.Var createVarStatement(String name){
        return new Stmt.Var(createIDToken(name), null);
    }

    private static ZincStructure createStructure(String name, List<String> variableNames){
        List<Stmt.Var> variableStatements = new ArrayList<>();
        for (String variableName : variableNames){
            variableStatements.add(createVarStatement(variableName));
        }
        return new ZincStructure(new Stmt.Structure(createIDToken(name), variableStatements));
    }





}
