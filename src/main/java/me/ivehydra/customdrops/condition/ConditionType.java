package me.ivehydra.customdrops.condition;

public enum ConditionType {

    EQUALS_STRING("equals") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return value1.equals(value2);
        }
    },
    NOT_EQUALS_STRING("!equals") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return !value1.equals(value2);
        }
    },
    EQUALS_IGNORE_CASE("equalsIgnoreCase") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return value1.equalsIgnoreCase(value2);
        }
    },
    NOT_EQUALS_IGNORE_CASE("!equalsIgnoreCase") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return !value1.equalsIgnoreCase(value2);
        }
    },
    STARTS_WITH("startsWith") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return value1.startsWith(value2);
        }
    },
    NOT_STARTS_WITH("!startsWith") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return !value1.startsWith(value2);
        }
    },
    ENDS_WITH("endsWith") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return value1.endsWith(value2);
        }
    },
    NOT_ENDS_WITH("!endsWith") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return !value1.endsWith(value2);
        }
    },
    CONTAINS("contains") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return value1.contains(value2);
        }
    },
    NOT_CONTAINS("!contains") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return !value1.contains(value2);
        }
    },
    EQUALS("==") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return Double.parseDouble(value1) == Double.parseDouble(value2);
        }
    },
    NOT_EQUALS("!=") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return Double.parseDouble(value1) != Double.parseDouble(value2);
        }
    },
    GREATER(">") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return Double.parseDouble(value1) > Double.parseDouble(value2);
        }
    },
    GREATER_EQUALS(">=") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return Double.parseDouble(value1) >= Double.parseDouble(value2);
        }
    },
    LOWER("<") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return Double.parseDouble(value1) < Double.parseDouble(value2);
        }
    },
    LOWER_EQUALS("<=") {
        @Override
        public boolean evaluate(String value1, String value2) {
            return Double.parseDouble(value1) <= Double.parseDouble(value2);
        }
    };


    private final String operator;

    ConditionType(String operator) {
        this.operator = operator;
    }

    public abstract boolean evaluate(String value1, String value2);

    public static ConditionType fromString(String operator) {
        for (ConditionType type : ConditionType.values())
            if (type.operator.equals(operator)) return type;
        throw new IllegalArgumentException("[CustomDrops] Unknown Operator: " + operator);
    }

    public String getOperator() { return operator; }

}