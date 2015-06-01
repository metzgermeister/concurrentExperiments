package dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExperimentStrategy {
    MIN_MIN,
    MIN_MAX,
    MAX_MIN,
    MAX_MAX;
    
    @JsonCreator
    public static ExperimentStrategy forValue(String value) {
        return ExperimentStrategy.valueOf(value);
    }
    
    @JsonValue
    public String toValue() {
        return toString();
    }
}
