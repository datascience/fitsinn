package rocks.artur.domain.statistics;

import rocks.artur.domain.Property;

/**
 * This class describes a distribution of properties.
 */
public class PropertyStatistic {
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    private Long count;
    private Property property;

    public PropertyStatistic(Long count, Property property) {
        this.count = count;
        this.property = property;
    }
}
