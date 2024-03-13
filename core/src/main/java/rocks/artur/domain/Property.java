package rocks.artur.domain;

/**
 * The list of supported properties.
 */
public enum Property {
    FORMAT(ValueType.STRING),
    FORMAT_VERSION(ValueType.STRING),
    MIMETYPE(ValueType.STRING),
    EXTERNALIDENTIFIER(ValueType.STRING),
    SIZE(ValueType.INTEGER),
    FSLASTMODIFIED(ValueType.TIMESTAMP),
    CREATED(ValueType.TIMESTAMP),
    LASTMODIFIED(ValueType.TIMESTAMP),
    CREATINGAPPLICATIONVERSION(ValueType.STRING),
    CREATINGOS(ValueType.STRING),
    INHIBITORTYPE(ValueType.STRING),

    CREATINGAPPLICATIONNAME(ValueType.STRING),

    VALID(ValueType.STRING),

    WELLFORMED(ValueType.STRING),

    LINEBREAK(ValueType.STRING),
    CHARSET(ValueType.STRING),
    PAGECOUNT(ValueType.INTEGER),
    WORDCOUNT(ValueType.INTEGER),
    CHARACTERCOUNT(ValueType.INTEGER),
    HASANNOTATIONS(ValueType.STRING),
    ISTAGGED(ValueType.STRING),
    HASFORMS(ValueType.STRING),
    HASOUTLINE(ValueType.STRING),
    ISPROTECTED(ValueType.STRING),
    ISRIGHTSMANAGED(ValueType.STRING),
    MARKUPBASISVERSION(ValueType.STRING),

    LANGUAGE(ValueType.STRING),
    MARKUPBASIS(ValueType.STRING);
    private final ValueType valueType;

    Property(ValueType valueType) {
        this.valueType = valueType;
    }

    public ValueType getValueType() {
        return valueType;
    }
}
