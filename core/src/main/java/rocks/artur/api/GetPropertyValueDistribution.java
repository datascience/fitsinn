package rocks.artur.api;

import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.statistics.PropertyValueStatistic;

import java.util.List;

/**
 * This interface enables getting a property value distribution given a property name.
 */
public interface GetPropertyValueDistribution {
    List<PropertyValueStatistic> getPropertyValueDistribution(Property propertyName, FilterCriteria<CharacterisationResult> filterCriteria);
}
