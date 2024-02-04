package rocks.artur.api_impl;

import rocks.artur.api.GetProperties;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.statistics.PropertyStatistic;

import java.util.List;

public class GetPropertiesImpl implements GetProperties {
    private CharacterisationResultGateway characterisationResultGateway;

    public GetPropertiesImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public List<PropertyStatistic> getProperties() {
        List<PropertyStatistic> propertyDistribution = characterisationResultGateway.getPropertyDistribution(null);
        return propertyDistribution;
    }

    @Override
    public List<PropertyStatistic> getProperties(FilterCriteria filter) {
        List<PropertyStatistic> propertyDistribution = characterisationResultGateway.getPropertyDistribution(filter);
        return propertyDistribution;
    }
}
