package rocks.artur.domain;

/**
 * This class describes the core entity used within the project. It holds information about the result of
 * a characterisation process. Such results describe digital objects, their properties and metadata.
 */
public class CharacterisationResult {
    @Override
    public String toString() {
        return "CharacterisationResult{" +
                "property=" + property +
                ", value='" + value + '\'' +
                ", valueType=" + valueType +
                ", source='" + source + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    /**
     * The property of the digital object that the characterisation result describes.
     */
    private Property property;
    /**
     * The value for the property of the digital object that the characterisation result describes.
     */
    private String value;
    /**
     * The type of the property value.
     */
    private ValueType valueType;
    /**
     * String representation or ID of the characterisation tool, e.g. FITS, Apache Tika etc.
     */
    private String source;
    /**
     * The file path is used as grouping identifier to distinguish all properties describing one digital object.
     */
    private String filePath;

    public CharacterisationResult() {
    }

    public CharacterisationResult(Property property, String value, ValueType valueType, String source, String filePath) {
        this.property = property;
        this.value = value;
        this.valueType = valueType;
        this.source = source;
        this.filePath = filePath;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterisationResult that = (CharacterisationResult) o;

        if (property != that.property) return false;
        if ( value != null && !value.equals(that.value) ) return false;
        if ( that.value != null && !that.value.equals(value) ) return false;
        if (valueType != that.valueType) return false;
        if ( source != null && !source.equals(that.source) ) return false;
        if ( that.source != null && !that.source.equals(source) ) return false;

        if ( filePath != null && !filePath.equals(that.filePath) ) return false;
        if ( that.filePath != null && !that.filePath.equals(filePath) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = property.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + filePath.hashCode();
        return result;
    }
}
