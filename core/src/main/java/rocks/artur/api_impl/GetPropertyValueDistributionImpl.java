package rocks.artur.api_impl;

import rocks.artur.api.GetPropertyValueDistribution;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.statistics.PropertyValueStatistic;

import java.util.List;

public class GetPropertyValueDistributionImpl implements GetPropertyValueDistribution {
    private CharacterisationResultGateway characterisationResultGateway;

    public GetPropertyValueDistributionImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }


    @Override
    public List<PropertyValueStatistic> getPropertyValueDistribution(Property property, FilterCriteria<CharacterisationResult> filterCriteria) {
        List<PropertyValueStatistic> valueDistributionByProperty = characterisationResultGateway.getPropertyValueDistribution(property, filterCriteria);
        return valueDistributionByProperty;
    }
}
