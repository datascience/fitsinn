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
    public List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria) {
        List<PropertiesPerObjectStatistic> objects = characterisationResultGateway.getObjects(filterCriteria);
        return objects;
    }

    @Override
    public Iterable<CharacterisationResult> getObject(String filePath) {
        Iterable<CharacterisationResult> characterisationResultsByFilepath = characterisationResultGateway.getCharacterisationResultsByFilepath(filePath);
        return characterisationResultsByFilepath;
    }

    @Override
    public List<CharacterisationResult> getConflictsFromObject(String filePath) {
        List<CharacterisationResult> characterisationResultsByFilepath = characterisationResultGateway.getConflictsByFilepath(filePath);
        return characterisationResultsByFilepath;
    }


}
