package rocks.artur.jpa.view;

import java.io.Serializable;

public class CharacterisationResultViewID implements Serializable {
    private String filePath;

    public void setProperty(String property) {
        this.property = property;
    }

    private String property;


    public String getFilePath() {
        return filePath;
    }

    public String getProperty() {
        return property;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
