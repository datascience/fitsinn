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
    public void addCharacterisationResult(CharacterisationResult characterisationResult, String datasetName) {
        repository.save(characterisationResult, datasetName);
        repository.cleanAggregation(datasetName);
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResults(FilterCriteria<CharacterisationResult> filter, String datasetName) {
        return repository.getCharacterisationResults(filter, datasetName);
    }

    @Override
    public List<PropertyStatistic> getPropertyDistribution(FilterCriteria<CharacterisationResult> filter, String datasetName) {
        return repository.getPropertyDistribution(datasetName);
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResultsByFilepath(String filePath, String datasetName) {
        return repository.getCharacterisationResultsByFilepath(filePath,datasetName);
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResultsByEntry(Entry entry, String datasetName) {
        return null;
    }

    @Override
    public List<Entry> getConflictEntries(String datasetName) {
        return null;
    }

    @Override
    public List<Entry> getEntries(String datasetName) {
        return null;
    }

    @Override
    public List<CharacterisationResult> getConflictsByFilepath(String filepath, String datasetName) {
        repository.aggregateResults(datasetName);
        List<CharacterisationResult> results = new ArrayList<>();
        List<CharacterisationResult> allJPAByFilePath = getCharacterisationResultsByFilepath(filepath, datasetName);
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
    public Map<String, Double> getCollectionStatistics(FilterCriteria filterCriteria, String datasetName) {
        repository.aggregateResults(datasetName);
        Map<String, Double> result = new HashMap<>();

        double[] sizeStatistics = repository.getSizeStatistics(filterCriteria, datasetName);
        result.put("totalSize", sizeStatistics[0]);
        result.put("minSize", sizeStatistics[1]);
        result.put("maxSize", sizeStatistics[2]);
        result.put("avgSize", sizeStatistics[3]);
        result.put("totalCount", sizeStatistics[4]);

        double[] conflictStatistics = repository.getConflictStatistics(filterCriteria, datasetName);
        result.put("conflictRate", conflictStatistics[1]);
        result.put("conflictCount", conflictStatistics[0]);
        return result;
    }

    @Override
    public List<PropertyValueStatistic> getPropertyValueDistribution(Property property, FilterCriteria<CharacterisationResult> filter, String datasetName) {
        repository.aggregateResults(datasetName);
        switch (property.getValueType()) {
            case TIMESTAMP: {
                List<PropertyValueStatistic> collect = null;
                List<Object[]> propertyValueDistribution =
                        repository.getPropertyValueTimeStampDistribution(property.name(), filter, datasetName);
                collect = propertyValueDistribution.stream().filter(stat ->  property.name().equalsIgnoreCase((String) stat[0]))
                        .map(stat -> new PropertyValueStatistic((Long) stat[2], (String) stat[1]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
            }
            case INTEGER:
            case FLOAT: {
                List<Object[]> propertyValueDistribution =
                        repository.getPropertyValueDistribution(property.name(), filter, datasetName);

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
                        repository.getPropertyValueDistribution(property.name(), filter, datasetName);
                collect = propertyValueDistribution.stream().filter(stat -> property.name().equalsIgnoreCase((String) stat[0]))
                        .map(stat -> new PropertyValueStatistic((Long) stat[2], (String) stat[1]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
        }
    }

    @Override
    public List<String> getSources(String datasetName) {
        return repository.getSources(datasetName);
    }

    @Override
    public List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria, String datasetName) {
        return repository.getObjects(filterCriteria, datasetName);
    }

    @Override
    public List<String[]> getSamples(FilterCriteria filterCriteria, SamplingAlgorithms algorithm, List<Property> properties, String datasetName) {
        repository.aggregateResults(datasetName);
        switch (algorithm) {
            case RANDOM -> {
                List<String[]> samples = repository.getRandomSamples(filterCriteria, 10, datasetName);
                return samples;
            }
            case SELECTIVE_FEATURE_DISTRIBUTION -> {
                List<String[]> selectiveFeatureDistributionSamples = repository.getSelectiveFeatureDistributionSamples(filterCriteria, properties, datasetName);
                //List<String> examples = selectiveFeatureDistributionSamples.stream().map(arr -> arr[1]).collect(Collectors.toList());
                return selectiveFeatureDistributionSamples;
            }
        }
        return null;
    }

    @Override
    public void addCharacterisationResults(List<CharacterisationResult> characterisationResults, String datasetName) {
        repository.saveAll(characterisationResults, datasetName);
        repository.cleanAggregation(datasetName);
    }

    @Override
    public double getConflictRate(String datasetName) {
        repository.aggregateResults(datasetName);
        Long totalCount = repository.getDigitalObjectCount(datasetName);
        Long conflictCount = repository.getConflictCount(datasetName);
        return conflictCount / (double) totalCount;
    }

    @Override
    public void delete(CharacterisationResult characterisationResult, String datasetName) {

    }

    @Override
    public void resolveConflictsNative(String datasetName) {
        repository.resolveConflictsSimple(datasetName);
        repository.aggregateResults(datasetName);
    }
}
