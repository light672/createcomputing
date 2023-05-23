package com.lightdev6.zinc;

import java.util.Map;

public class ZincStructureConversionObject {
    private final String structureName;
    private final Map<String, Object> fields;

    public ZincStructureConversionObject(String structureName, Map<String, Object> fields){
        this.structureName = structureName;
        this.fields = fields;
    }

    public String getStructureName(){
        return structureName;
    }
    public Map<String, Object> getFields(){
        return fields;
    }
}
