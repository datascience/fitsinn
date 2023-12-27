package rocks.artur.api_impl;

import rocks.artur.domain.Property;

public class Entry {
    String filepath;
    Property property;

    public Entry(String filepath, Property property) {
        this.filepath = filepath;
        this.property = property;
    }

    public Entry() {
    }

    public Entry(String filepath, String property) {
        this.filepath = filepath;
        this.property = Property.valueOf(property.toUpperCase());
    }

    public String getFilepath() {
        return filepath;
    }

    public Property getProperty() {
        return property;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (!filepath.equals(entry.getFilepath())) return false;
        return property == entry.getProperty();
    }

    @Override
    public int hashCode() {
        if (filepath == null || property == null) {
            return 0;
        }
        int result = filepath.hashCode();
        result = 31 * result + property.hashCode();
        return result;
    }
}
