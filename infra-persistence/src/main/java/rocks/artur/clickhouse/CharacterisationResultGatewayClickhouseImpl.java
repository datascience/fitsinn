package rocks.artur.clickhouse;

import org.springdoc.core.customizers.ActuatorOperationCustomizer;
import rocks.artur.api_impl.filter.SingleFilterCriteria;
import rocks.artur.domain.*;
import rocks.artur.domain.statistics.BinningAlgorithms;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;
import rocks.artur.jpa.view.CharacterisationResultViewJPA;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CharacterisationResultGatewayClickhouseImpl implements CharacterisationResultGateway {

    CharacterisationResultClickhouseRepository repository;
    public CharacterisationResultGatewayClickhouseImpl(CharacterisationResultClickhouseRepository repository) {
        this.repository = repository;
    }



    @Override
    public void addCharacterisationResult(CharacterisationResult characterisationResult) {
        repository.save(characterisationResult);
        repository.cleanAggregation();
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
        return repository.getCharacterisationResultsByFilepath(filePath);
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
        repository.aggregateResults();
        List<CharacterisationResult> results = new ArrayList<>();
        List<CharacterisationResult> allJPAByFilePath = getCharacterisationResultsByFilepath(filepath);
        List<Property> properties = allJPAByFilePath.stream().map(item -> item.getProperty()).collect(Collectors.toList());

        for (Property property : properties) {
            List<CharacterisationResult> collect = allJPAByFilePath.stream().filter(item -> item.getProperty().equals(property)).toList();
            if (collect.stream().map(CharacterisationResult::getValue).distinct().count() > 1) {
                results.addAll(collect);
            }
        }
        return results;
    }

    @Override
    public Map<String, Double> getCollectionStatistics(FilterCriteria filterCriteria) {
        repository.aggregateResults();
        Map<String, Double> result = new HashMap<>();

        double[] sizeStatistics = repository.getSizeStatistics(filterCriteria);
        result.put("totalSize", sizeStatistics[0]);
        result.put("minSize", sizeStatistics[1]);
        result.put("maxSize", sizeStatistics[2]);
        result.put("avgSize", sizeStatistics[3]);
        result.put("totalCount", sizeStatistics[4]);

        double[] conflictStatistics = repository.getConflictStatistics(filterCriteria);
        result.put("conflictRate", conflictStatistics[1]);
        result.put("conflictCount", conflictStatistics[0]);
        return result;
    }

    @Override
    public List<PropertyValueStatistic> getPropertyValueDistribution(Property property, FilterCriteria<CharacterisationResult> filter) {
        repository.aggregateResults();
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
        return repository.getObjects(filterCriteria);
    }

    @Override
    public List<String[]> getSamples(FilterCriteria filterCriteria, SamplingAlgorithms algorithm, List<Property> properties) {
        repository.aggregateResults();
        switch (algorithm) {
            case RANDOM -> {
                List<String[]> samples = repository.getRandomSamples(filterCriteria, 10);
                return samples;
            }
            case SELECTIVE_FEATURE_DISTRIBUTION -> {
                List<String[]> selectiveFeatureDistributionSamples = repository.getSelectiveFeatureDistributionSamples(filterCriteria, properties);
                //List<String> examples = selectiveFeatureDistributionSamples.stream().map(arr -> arr[1]).collect(Collectors.toList());
                return selectiveFeatureDistributionSamples;
            }
        }
        return null;
    }

    @Override
    public void addCharacterisationResults(List<CharacterisationResult> characterisationResults) {
        repository.saveAll(characterisationResults);
        repository.cleanAggregation();
    }

    @Override
    public double getConflictRate() {
        repository.aggregateResults();
        Long totalCount = repository.getDigitalObjectCount();
        Long conflictCount = repository.getConflictCount();
        return conflictCount / (double) totalCount;
    }

    @Override
    public void delete(CharacterisationResult characterisationResult) {

    }

    @Override
    public void resolveConflictsNative() {
        repository.resolveConflictsSimple();
        repository.aggregateResults();
    }
}
