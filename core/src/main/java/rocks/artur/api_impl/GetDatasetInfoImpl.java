package rocks.artur.api_impl;

import rocks.artur.api.GetDatasetInfo;
import rocks.artur.domain.CharacterisationResultGateway;

import java.util.List;

public class GetDatasetInfoImpl implements GetDatasetInfo {
    private CharacterisationResultGateway characterisationResultGateway;

    public GetDatasetInfoImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public List<String> listDatasets() {
        return this.characterisationResultGateway.listDatasets();
    }
}
