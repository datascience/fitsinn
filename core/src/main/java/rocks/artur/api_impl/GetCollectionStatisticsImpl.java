package rocks.artur.api_impl;

import rocks.artur.api.GetCollectionStatistics;
import rocks.artur.domain.CharacterisationResultGateway;

import java.util.Map;

public class GetCollectionStatisticsImpl implements GetCollectionStatistics {

    private CharacterisationResultGateway characterisationResultGateway;

    public GetCollectionStatisticsImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public Map<String, Object> getSizeStatistics() {
        Map<String, Object> sizeStatistics = characterisationResultGateway.getSizeStatistics();
        return sizeStatistics;
    }

    @Override
    public Double getConflictRate() {
        return 17.0;
    }
}
