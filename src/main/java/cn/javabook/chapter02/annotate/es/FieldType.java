package cn.javabook.chapter02.annotate.es;

/**
 * elastic字段类型枚举
 *
 */
public enum FieldType {
    Auto("auto"),
    Text("text"),
    Keyword("keyword"),
    Long("long");

    public String value;

    private FieldType(final String value) {
        this.value = value;
    }

    public static String getValue(final String value) {
        for (FieldType field : FieldType.values()) {
            if (field.getValue().equalsIgnoreCase(value)) {
                return field.value;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
