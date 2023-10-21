package rocks.artur.domain.statistics;


/**
 * This class describes a distribution of properties.
 */
public class PropertiesPerObjectStatistic {
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    private Long count;
    private String filepath;

    public PropertiesPerObjectStatistic(Long count, String filepath) {
        this.count = count;
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
