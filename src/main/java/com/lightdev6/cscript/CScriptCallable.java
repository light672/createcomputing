package com.lightdev6.cscript;

import java.util.List;

interface CScriptCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);

}
