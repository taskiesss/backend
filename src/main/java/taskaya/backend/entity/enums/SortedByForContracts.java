package taskaya.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortedByForContracts {
    DUE_DATE("dueDate"),
    TITLE("title"),
    START_DATE("startDate");

    private final String value;

    SortedByForContracts(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SortedByForContracts fromValue(String value) {
        for (SortedByForContracts sortedBy : SortedByForContracts.values()) {
            if (sortedBy.value.equalsIgnoreCase(value)) {
                return sortedBy;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}