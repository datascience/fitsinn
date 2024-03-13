package rocks.artur.jpa.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@IdClass(CharacterisationResultViewID.class)
@Table(name = "characterisationresultview")
public class CharacterisationResultViewJPA {

    @Id
    @Column(nullable = false, name = "file_path")
    private String filePath;
    @Id
    @Column(nullable = false)
    private String property;
    @Column(nullable = false, name = "property_value")
    private String value;

    @Column(nullable = false, name = "value_type")
    private String valueType;


    public String getProperty() {
        return property;
    }


    public String getValue() {
        return value;
    }


    public String getValueType() {
        return valueType;
    }



    @Override
    public String toString() {
        return "CharacterisationResultViewJPA{" +
                ", property=" + property +
                ", value='" + value + '\'' +
                ", valueType=" + valueType +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getFilePath() {
        return filePath;
    }
}