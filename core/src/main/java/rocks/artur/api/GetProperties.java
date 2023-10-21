package rocks.artur.api;

import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.statistics.PropertyStatistic;

import java.util.List;

/**
 * This interface enables getting a property distribution.
 */
public interface GetProperties {
    List<PropertyStatistic> getProperties();
    List<PropertyStatistic> getProperties(FilterCriteria filter);
}
