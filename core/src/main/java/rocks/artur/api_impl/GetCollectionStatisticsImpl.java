package rocks.artur.api_impl;

import rocks.artur.api.GetCollectionStatistics;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.FilterCriteria;

import java.util.Map;

public class GetCollectionStatisticsImpl implements GetCollectionStatistics {

    private CharacterisationResultGateway characterisationResultGateway;

    public GetCollectionStatisticsImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public Map<String, Double> getSizeStatistics(FilterCriteria filterCriteria) {
        Map<String, Double> sizeStatistics = characterisationResultGateway.getSizeStatistics(filterCriteria);
        return sizeStatistics;
    }

    @Override
    public double getConflictRate() {
        return characterisationResultGateway.getConflictRate();
    }
}
