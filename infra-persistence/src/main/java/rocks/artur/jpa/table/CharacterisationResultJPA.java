package rocks.artur.jpa.table;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;
import rocks.artur.domain.ValueType;


@Entity
@Table(name = "characterisationresult")
public class CharacterisationResultJPA {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true)
    private String id;
    @Column(nullable = false, name = "filepath")
    private String filePath;
    @Column(nullable = false)
    private String property;

    @Column(nullable = false)
    private String source;

    @Column(name = "property_value", nullable = false)
    private String value;

    @Column(name = "valuetype", nullable = false)
    private String valueType;

    public CharacterisationResultJPA(CharacterisationResult characterisationResult) {
        this.filePath = characterisationResult.getFilePath();
        this.source = characterisationResult.getSource();
        this.value = characterisationResult.getValue();
        this.valueType = characterisationResult.getValueType().toString();
        this.property = characterisationResult.getProperty().toString();
    }

    public CharacterisationResultJPA() {
    }

    public static CharacterisationResultJPA deepCopy(CharacterisationResultJPA characterisationResult) {
        CharacterisationResultJPA result = new CharacterisationResultJPA();
        result.setFilePath(characterisationResult.filePath);
        result.setValue(characterisationResult.value);
        result.setProperty(characterisationResult.property);
        result.setSource(characterisationResult.source);
        result.setValueType(characterisationResult.valueType);
        return result;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "CharacterisationResultJPA{" +
                ", id=" + id +
                ", property=" + property +
                ", value='" + value + '\'' +
                ", valueType=" + valueType +
                ", source='" + source + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public CharacterisationResult toCharacterisationResult() {
        CharacterisationResult result = new CharacterisationResult();

        result.setValue(this.value);
        result.setSource(this.source);
        result.setProperty(Property.valueOf(this.property.toUpperCase()));
        result.setValueType(ValueType.valueOf(this.valueType.toUpperCase()));
        result.setFilePath(this.filePath);

        return result;
    }

}