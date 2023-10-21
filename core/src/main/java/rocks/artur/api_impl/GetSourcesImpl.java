package rocks.artur.api_impl;

import rocks.artur.api.GetSources;
import rocks.artur.domain.CharacterisationResultGateway;

import java.util.List;

public class GetSourcesImpl implements GetSources {
    private CharacterisationResultGateway characterisationResultGateway;

    public GetSourcesImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public List<String> getSources() {
        List<String> sources = characterisationResultGateway.getSources();
        return sources;
    }
}
