package rocks.artur.api_impl;

import rocks.artur.api.AnalyzePersistFile;
import rocks.artur.api.CharacterisationResultProducer;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;

import java.io.File;
import java.io.IOException;
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
    public Long uploadCharacterisationResults(File file) {
        try {
            List<CharacterisationResult> characterisationResults = characterisationResultProducer.processFile(file);
            characterisationResults.forEach(item -> characterisationResultGateway.addCharacterisationResult(item));
            return Long.valueOf(characterisationResults.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long uploadCharacterisationResults(byte[] file, String filename) {
        try {
            List<CharacterisationResult> characterisationResults = characterisationResultProducer.processFile(file, filename);
            characterisationResultGateway.addCharacterisationResults(characterisationResults);
            return Long.valueOf(characterisationResults.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
