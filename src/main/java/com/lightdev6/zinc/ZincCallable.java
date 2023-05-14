package com.lightdev6.zinc;

import java.util.List;

interface ZincCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments, Token paren);

}
