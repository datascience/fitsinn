package rocks.artur.api_impl;

import rocks.artur.api.GetObjects;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.CharacterisationResultGateway;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;

import java.util.List;

public class GetObjectsImpl implements GetObjects {
    private CharacterisationResultGateway characterisationResultGateway;

    public GetObjectsImpl(CharacterisationResultGateway characterisationResultGateway) {
        this.characterisationResultGateway = characterisationResultGateway;
    }

    @Override
    public List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria, String datasetName) {
        List<PropertiesPerObjectStatistic> objects = characterisationResultGateway.getObjects(filterCriteria, datasetName);
        return objects;
    }

    @Override
    public Iterable<CharacterisationResult> getObject(String filePath, String datasetName) {
        Iterable<CharacterisationResult> characterisationResultsByFilepath = characterisationResultGateway.getCharacterisationResultsByFilepath(filePath, datasetName);
        return characterisationResultsByFilepath;
    }

    @Override
    public List<CharacterisationResult> getConflictsFromObject(String filePath, String datasetName) {
        List<CharacterisationResult> characterisationResultsByFilepath = characterisationResultGateway.getConflictsByFilepath(filePath, datasetName);
        return characterisationResultsByFilepath;
    }


}
