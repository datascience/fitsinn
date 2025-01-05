package rocks.artur.api_impl;

import rocks.artur.api.RemoveDataset;
import rocks.artur.domain.CharacterisationResultGateway;

public class RemoveDatasetImpl implements RemoveDataset {

    private CharacterisationResultGateway characterisationResultGateway;

    public RemoveDatasetImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public Boolean removeDataset(String datasetName) {
        return characterisationResultGateway.removeDataset(datasetName);
    }
}
