package rocks.artur.api_impl.filter;

import rocks.artur.domain.ValueType;

public enum FilterOperation {
    LESS("<"), LESS_OR_EQUAL ("<="),
    GREATER(">"), GREATER_OR_EQUAL (">="),
    EQUAL("=");

    private final String value;

    FilterOperation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
