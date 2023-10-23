package rocks.artur.jpa;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.artur.domain.*;
import rocks.artur.domain.statistics.BinningAlgorithms;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;
import rocks.artur.jpa.table.CharacterisationResultJPA;
import rocks.artur.jpa.table.CharacterisationResultRepository;
import rocks.artur.jpa.view.CharacterisationResultViewJPA;
import rocks.artur.jpa.view.CharacterisationResultViewRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CharacterisationResultGatewayJpaImpl implements CharacterisationResultGateway {
    private static final Logger LOG = LoggerFactory.getLogger(CharacterisationResultGatewayJpaImpl.class);
    CharacterisationResultRepository characterisationResultRepository;
    CharacterisationResultViewRepository characterisationResultViewRepository;

    CharacterisationResultGatewayJpaImpl(CharacterisationResultRepository characterisationResultRepository,
                                         CharacterisationResultViewRepository characterisationResultViewRepository) {
        this.characterisationResultRepository = characterisationResultRepository;
        this.characterisationResultViewRepository = characterisationResultViewRepository;
    }

    @Override
    @Transactional
    public void addCharacterisationResult(CharacterisationResult characterisationResult) {
        CharacterisationResultJPA toSave = new CharacterisationResultJPA();
        toSave.setProperty(characterisationResult.getProperty().toString());
        toSave.setSource(characterisationResult.getSource());
        toSave.setValue(characterisationResult.getValue());
        toSave.setFilePath(characterisationResult.getFilePath());
        toSave.setValueType(characterisationResult.getValueType().toString());
        LOG.debug("saving " + toSave.toString());
        characterisationResultRepository.save(toSave);
    }

    @Override
    public List<CharacterisationResult> getCharacterisationResults(FilterCriteria<CharacterisationResult> filter) {
        List<CharacterisationResultJPA> all = characterisationResultRepository.findAll();
        List<CharacterisationResult> result = all.stream().map(item -> new CharacterisationResult(Property.valueOf(item.getProperty()), item.getValue(),
                ValueType.valueOf(item.getValueType()), item.getSource(), item.getFilePath())).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<PropertyStatistic> getPropertyDistribution(FilterCriteria filter) {

        //Specification<CharacterisationResultViewJPA> convert = convert(filter);
        //final Map<String, Long> result =
        //        characterisationResultViewRepository.groupAndCount(CharacterisationResultViewJPA_.property,
        //                convert);
        //collect = result.entrySet().stream().map(item -> new PropertyStatistic(item.getValue(), Property.valueOf(item.getKey())))
        //        .collect(Collectors.toList());

        List<Object[]> distribution = characterisationResultViewRepository.getPropertyDistribution();
        List<PropertyStatistic> collect = distribution.stream()
                .map(stat -> new PropertyStatistic((Long) stat[1], Property.valueOf(stat[0].toString())))
                .collect(Collectors.toList());
        collect.sort(Comparator.comparingLong(PropertyStatistic::getCount).reversed());
        return collect;

    }

    public List<PropertyValueStatistic> getPropertyValueDistribution(Property property, FilterCriteria<CharacterisationResult> filter) {

        switch (property.getValueType()) {
            case TIMESTAMP: {
                List<PropertyValueStatistic> collect = null;
                List<Object[]> propertyValueDistribution =
                        characterisationResultViewRepository.getPropertyValueTimeStampDistribution(property.toString(), filter);
                collect = propertyValueDistribution.stream()
                        .map(stat -> new PropertyValueStatistic((Long) stat[1], (String) stat[0]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
            }
            case INTEGER:
            case FLOAT: {
                List<Object[]> propertyValueDistribution =
                        characterisationResultViewRepository.getPropertyValueDistribution(property.toString(), filter);

                List<Float> floats = propertyValueDistribution.stream()
                        .map(stat -> Float.parseFloat(stat[0].toString())).sorted(Float::compare).collect(Collectors.toList());

                List<PropertyValueStatistic> propertyValueStatistics = BinningAlgorithms.runBinning(floats);
                return propertyValueStatistics;
            }
            default:
                List<PropertyValueStatistic> collect = null;
                List<Object[]> propertyValueDistribution =
                        characterisationResultViewRepository.getPropertyValueDistribution(property.toString(), filter);
                collect = propertyValueDistribution.stream()
                        .map(stat -> new PropertyValueStatistic((Long) stat[1], (String) stat[0]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
        }


    }

    @Override
    public List<CharacterisationResult> getCharacterisationResultsByFilepath(String filepath) {
        List<CharacterisationResultJPA> allJPAByFilePath = characterisationResultRepository.findAllByFilePath(filepath);
        List<CharacterisationResult> result = allJPAByFilePath.stream().map(item -> new CharacterisationResult(Property.valueOf(item.getProperty()), item.getValue(),
                ValueType.valueOf(item.getValueType()), item.getSource(), item.getFilePath())).collect(Collectors.toList());
        return result;
    }


    public List<CharacterisationResult> getConflictsByFilepath(String filepath) {
        List<CharacterisationResultViewJPA> allJPAByFilePath = characterisationResultViewRepository.findAllByFilePath(filepath);
        List<CharacterisationResult> result = allJPAByFilePath.stream().filter(item -> item.getValue().equals("CONFLICT")).map(item -> new CharacterisationResult(Property.valueOf(item.getProperty()), item.getValue(),
                ValueType.valueOf(item.getValueType()), null, item.getFilePath())).collect(Collectors.toList());
        return result;
    }

    @Override
    public Map<String, Object> getSizeStatistics() {
        Map<String, Object> result = new HashMap<>();

        Long totalSize = characterisationResultViewRepository.getTotalSize();
        result.put("totalSize", totalSize);
        Long minSize = characterisationResultViewRepository.getMinSize();
        result.put("minSize", minSize);
        Long maxSize = characterisationResultViewRepository.getMaxSize();
        result.put("maxSize", maxSize);

        Long avgSize = characterisationResultViewRepository.getAvgSize();
        result.put("avgSize", avgSize);

        Long totalCount = characterisationResultViewRepository.getTotalCount();
        result.put("totalCount", totalCount);

        Long conflictRate = characterisationResultViewRepository.getConflictCount();
        result.put("conflictRate", conflictRate);

        List<Object[]> sizeDistribution = characterisationResultViewRepository.getSizeDistribution();

        List<PropertyValueStatistic> collect = sizeDistribution.stream()
                .map(stat -> new PropertyValueStatistic((Long) stat[1], (String) stat[0]))
                .collect(Collectors.toList());

        result.put("sizeDistribution", collect);
        return result;
    }

    @Override
    public List<String> getSources() {
        List<String> sources = characterisationResultViewRepository.getSources();
        return sources;
    }

    @Override
    public List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria) {


        List<Object[]> propertyValueDistribution =
                characterisationResultViewRepository.getObjects(filterCriteria);
        List<PropertiesPerObjectStatistic> collect = propertyValueDistribution.stream()
                .map(stat -> new PropertiesPerObjectStatistic((Long) stat[1], (String) stat[0]))
                .collect(Collectors.toList());


        return collect;
    }



    @Override
    public List<String[]> getSamples(FilterCriteria filterCriteria, SamplingAlgorithms algorithm, List<Property> properties) {
        switch (algorithm) {
            case RANDOM -> {
                List<String[]> samples = characterisationResultViewRepository.getRandomSamples(filterCriteria, 10);
                return samples;
            }
            case SELECTIVE_FEATURE_DISTRIBUTION -> {
                List<String[]> selectiveFeatureDistributionSamples = characterisationResultViewRepository.getSelectiveFeatureDistributionSamples(filterCriteria, properties);
                //List<String> examples = selectiveFeatureDistributionSamples.stream().map(arr -> arr[1]).collect(Collectors.toList());
                return selectiveFeatureDistributionSamples;
            }
        }
        return null;
    }

    @Override
    public void addCharacterisationResults(List<CharacterisationResult> characterisationResults) {


        List<CharacterisationResultJPA> collect = characterisationResults.parallelStream().map(res -> {
            CharacterisationResultJPA toSave = new CharacterisationResultJPA();
            toSave.setProperty(res.getProperty().toString());
            toSave.setSource(res.getSource());
            toSave.setValue(res.getValue());
            toSave.setFilePath(res.getFilePath());
            toSave.setValueType(res.getValueType().toString());
            return toSave;
        }).collect(Collectors.toList());


        LOG.debug("saving " + collect);
        characterisationResultRepository.saveAll(collect);
    }

    @Override
    public double getConflictRate() {
        Long totalCount = characterisationResultViewRepository.getTotalCount();
        Long conflictCount = characterisationResultViewRepository.getConflictCount();
        return conflictCount/(double)totalCount;
    }
}
