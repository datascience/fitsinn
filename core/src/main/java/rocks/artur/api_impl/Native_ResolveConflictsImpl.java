package rocks.artur.api_impl;

import rocks.artur.api.ResolveConflicts;
import rocks.artur.domain.CharacterisationResultGateway;

public class Native_ResolveConflictsImpl implements ResolveConflicts {
    private CharacterisationResultGateway characterisationResultGateway;

    public Native_ResolveConflictsImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }
    @Override
    public void run(String datasetName) {
        characterisationResultGateway.resolveConflictsNative(datasetName);
    }
}
