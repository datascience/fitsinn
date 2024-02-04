package rocks.artur.api;

import rocks.artur.domain.statistics.PropertyValueStatistic;

import java.util.List;

/**
 * This interface enables getting a property value distribution given a filter.
 */
public interface GetPropertyValueDistributionWithFilter {
    List<PropertyValueStatistic> getPropertyValueDistributionWithFilter(String propertyName, String filter);
}
