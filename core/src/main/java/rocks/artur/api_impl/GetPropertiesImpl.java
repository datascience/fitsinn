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
    public List<PropertyStatistic> getProperties(String datasetName) {
        List<PropertyStatistic> propertyDistribution = characterisationResultGateway.getPropertyDistribution(null, datasetName);
        return propertyDistribution;
    }

    @Override
    public List<PropertyStatistic> getProperties(FilterCriteria filter, String datasetName) {
        List<PropertyStatistic> propertyDistribution = characterisationResultGateway.getPropertyDistribution(filter, datasetName);
        return propertyDistribution;
    }
}
