package rocks.artur.domain.statistics;

/**
 * This class describes a distribution of property values.
 */
public class    PropertyValueStatistic {
    private Long count;
    private String value;

    public PropertyValueStatistic(Long count, String value) {
        this.count = count;
        this.value = value;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
