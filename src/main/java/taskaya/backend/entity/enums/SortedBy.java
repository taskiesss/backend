package taskaya.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortedBy {
    RATE("Rate"),
    PRICE_PER_HOUR("pricePerHour"),
    POSTED_AT("postedAT"),       //only for jobs
    ;

    private final String value;

    SortedBy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SortedBy fromValue(String value) {
        for (SortedBy sortedBy : SortedBy.values()) {
            if (sortedBy.value.equalsIgnoreCase(value)) {
                return sortedBy;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}