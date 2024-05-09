package rocks.artur.api_impl;

import rocks.artur.api.AnalyzePersistFile;
import rocks.artur.api.CharacterisationResultProducer;
import rocks.artur.api_impl.utils.ByteFile;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;


import java.util.ArrayList;
import java.util.List;

public class AnalyzePersistFileImpl implements AnalyzePersistFile {
    private CharacterisationResultProducer characterisationResultProducer;
    private CharacterisationResultGateway characterisationResultGateway;

    public AnalyzePersistFileImpl(CharacterisationResultProducer characterisationResultProducer,
                                  CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultProducer = characterisationResultProducer;
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public Long uploadCharacterisationResults(ByteFile file, String datasetName) {
        List<CharacterisationResult> characterisationResults = characterisationResultProducer.processFile(file);
        characterisationResultGateway.addCharacterisationResults(characterisationResults, datasetName);
        return Long.valueOf(characterisationResults.size());
    }

    @Override
    public Long uploadCharacterisationResults(List<ByteFile> files, String datasetName) {
        List<CharacterisationResult> characterisationResults = new ArrayList<>();
        files.stream().forEach(file -> {
            List<CharacterisationResult> tmp = characterisationResultProducer.processFile(file);
            characterisationResults.addAll(tmp);
        });
        characterisationResultGateway.addCharacterisationResults(characterisationResults, datasetName);
        return Long.valueOf(characterisationResults.size());
    }
}
