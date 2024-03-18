package rocks.artur.ch;

import rocks.artur.domain.*;
import rocks.artur.domain.statistics.BinningAlgorithms;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;

import java.util.*;
import java.util.stream.Collectors;

public class CharacterisationResultGatewayClickhouseImpl implements CharacterisationResultGateway {

    CharacterisationResultClickhouseRepository repository;
    public CharacterisationResultGatewayClickhouseImpl(CharacterisationResultClickhouseRepository repository) {
        this.repository = repository;
    }



    @Override
    public void addCharacterisationResult(CharacterisationResult characterisationResult) {
        repository.save(characterisationResult);
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResults(FilterCriteria<CharacterisationResult> filter) {
        return repository.getCharacterisationResults(filter);
    }

    @Override
    public List<PropertyStatistic> getPropertyDistribution(FilterCriteria<CharacterisationResult> filter) {
        return repository.getPropertyDistribution();
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResultsByFilepath(String filePath) {
        return null;
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResultsByEntry(Entry entry) {
        return null;
    }

    @Override
    public List<Entry> getConflictEntries() {
        return null;
    }

    @Override
    public List<Entry> getEntries() {
        return null;
    }

    @Override
    public List<CharacterisationResult> getConflictsByFilepath(String filepath) {
        return null;
    }

    @Override
    public Map<String, Double> getCollectionStatistics(FilterCriteria filterCriteria) {
        return null;
    }

    @Override
    public List<PropertyValueStatistic> getPropertyValueDistribution(Property property, FilterCriteria<CharacterisationResult> filter) {
        switch (property.getValueType()) {
            case TIMESTAMP: {
                List<PropertyValueStatistic> collect = null;
                List<Object[]> propertyValueDistribution =
                        repository.getPropertyValueTimeStampDistribution(property.name(), filter);
                collect = propertyValueDistribution.stream().filter(stat ->  property.name().equalsIgnoreCase((String) stat[0]))
                        .map(stat -> new PropertyValueStatistic((Long) stat[2], (String) stat[1]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
            }
            case INTEGER:
            case FLOAT: {
                List<Object[]> propertyValueDistribution =
                        repository.getPropertyValueDistribution(property.name(), filter);

                List<Float> floats = propertyValueDistribution.stream().filter(stat -> property.name().equalsIgnoreCase((String) stat[0]) && !(stat[1].equals("CONFLICT")))
                        .map(stat -> {
                                    Float val = Float.parseFloat(stat[1].toString());
                                    Long count = (Long) stat[2];

                                    List<Float> result = new ArrayList<>();

                                    for (long l = 0; l < count; l++) {
                                        result.add(val);
                                    }
                                    return result;
                                }
                        ).flatMap(Collection::stream).sorted(Float::compare).collect(Collectors.toList());

                List<PropertyValueStatistic> propertyValueStatistics = BinningAlgorithms.runBinning(floats);

                Optional<Long> conflicts = propertyValueDistribution.stream().filter(stat ->  property.name().equalsIgnoreCase((String) stat[0]) && stat[1].equals("CONFLICT"))
                        .map(stat -> (Long) stat[2]).findAny();

                conflicts.ifPresent(aLong -> propertyValueStatistics.add(new PropertyValueStatistic(aLong, "CONFLICT")));

                return propertyValueStatistics;
            }
            default:
                List<PropertyValueStatistic> collect = null;
                List<Object[]> propertyValueDistribution =
                        repository.getPropertyValueDistribution(property.name(), filter);
                collect = propertyValueDistribution.stream().filter(stat -> property.name().equalsIgnoreCase((String) stat[0]))
                        .map(stat -> new PropertyValueStatistic((Long) stat[2], (String) stat[1]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
        }
    }

    @Override
    public List<String> getSources() {
        return repository.getSources();
    }

    @Override
    public List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria) {
        return null;
    }

    @Override
    public List<String[]> getSamples(FilterCriteria filterCriteria, SamplingAlgorithms algorithm, List<Property> properties) {
        return null;
    }

    @Override
    public void addCharacterisationResults(List<CharacterisationResult> characterisationResults) {
        repository.addAll(characterisationResults);
    }

    @Override
    public double getConflictRate() {
        Long totalCount = repository.getDigitalObjectCount();
        Long conflictCount = repository.getConflictCount();
        return conflictCount / (double) totalCount;
    }

    @Override
    public void delete(CharacterisationResult characterisationResult) {

    }
}
