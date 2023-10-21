package rocks.artur.jpa.table;

import java.io.Serializable;

public class CharacterisationResultID implements Serializable {
    private String filePath;
    private String source;
    private String property;
    public void setProperty(String property) {
        this.property = property;
    }




    public String getFilePath() {
        return filePath;
    }

    public String getProperty() {
        return property;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
