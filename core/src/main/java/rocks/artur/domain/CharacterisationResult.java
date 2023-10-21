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
}
