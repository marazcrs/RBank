package com.exist.rbank.reference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum TaskStatus implements Serializable {

    NEW("NEW","New"),
    STARTED("STARTED","Started"),
    COMPLETED("COMPLETED","Completed");

    private String code;

    private String description;

    TaskStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<String, TaskStatus> LOOKUP = new HashMap<>();

    static {
        for (TaskStatus status : TaskStatus.values()) {
            LOOKUP.put(status.code.toLowerCase(), status);
        }
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static TaskStatus get(String code) {
        return LOOKUP.get(code.toLowerCase());
    }
}
