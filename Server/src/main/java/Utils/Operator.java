package Utils;

public enum Operator {
    eq;

    public static Operator fromString(String operator) {
        switch (operator) {
            case "=":
                return Operator.eq;
            default:
                return null;
        }
    }
}
