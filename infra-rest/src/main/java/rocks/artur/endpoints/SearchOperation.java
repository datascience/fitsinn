package rocks.artur.endpoints;

import rocks.artur.api_impl.filter.FilterOperation;

public enum SearchOperation {
    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, CONTAINS;

    public static final String[] SIMPLE_OPERATION_SET = {"=", "!", ">", "<", ">=", "<="};

    public static final String OR_PREDICATE_FLAG = "'";

    public static final String ZERO_OR_MORE_REGEX = "*";

    public static final String OR_OPERATOR = "OR";

    public static final String AND_OPERATOR = "AND";

    public static final String LEFT_PARANTHESIS = "(";

    public static final String RIGHT_PARANTHESIS = ")";

    public static FilterOperation getSimpleOperation(final String input) {
        switch (input) {
            case "=":
                return FilterOperation.EQUAL;
            case ">":
                return FilterOperation.GREATER;
            case "<":
                return FilterOperation.LESS;
            case ">=":
                return FilterOperation.GREATER_OR_EQUAL;
            case "<=":
                return FilterOperation.LESS_OR_EQUAL;
            default:
                return null;
        }
    }
}
