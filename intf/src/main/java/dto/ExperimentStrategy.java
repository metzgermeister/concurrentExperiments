package dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExperimentStrategy {
    MINMIN,
    MINMAX,
    MAXMIN,
    MAXMAX;
    
    @JsonCreator
    public static ExperimentStrategy forValue(String value) {
        return ExperimentStrategy.valueOf(value);
    }
    
    @JsonValue
    public String toValue() {
        return toString();
    }
}
