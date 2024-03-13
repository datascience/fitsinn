package rocks.artur.jpa;


import jakarta.transaction.Transactional;
import org.h2.jdbc.JdbcBatchUpdateException;
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

import java.util.*;
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
        CharacterisationResultJPA toSave = new CharacterisationResultJPA(characterisationResult);
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
                        characterisationResultViewRepository.getPropertyValueTimeStampDistribution(filter);
                collect = propertyValueDistribution.stream().filter(stat ->  property.name().equalsIgnoreCase((String) stat[0]))
                        .map(stat -> new PropertyValueStatistic((Long) stat[2], (String) stat[1]))
                        .collect(Collectors.toList());
                collect.sort(Comparator.comparingLong(PropertyValueStatistic::getCount).reversed());
                return collect;
            }
            case INTEGER:
            case FLOAT: {
                List<Object[]> propertyValueDistribution =
                        characterisationResultViewRepository.getPropertyValueDistribution(filter);

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
                        characterisationResultViewRepository.getPropertyValueDistribution(filter);
                collect = propertyValueDistribution.stream().filter(stat -> property.name().equalsIgnoreCase((String) stat[0]))
                        .map(stat -> new PropertyValueStatistic((Long) stat[2], (String) stat[1]))
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

    @Override
    public List<CharacterisationResult> getCharacterisationResultsByEntry(Entry entry) {
        List<CharacterisationResultJPA> allJPAByFilePath = characterisationResultRepository.findAllByFilePath(entry.getFilepath());
        List<CharacterisationResult> result = allJPAByFilePath.stream().filter(item -> item.getProperty().equals(entry.getProperty().toString())).map(item -> new CharacterisationResult(Property.valueOf(item.getProperty()), item.getValue(),
                ValueType.valueOf(item.getValueType()), item.getSource(), item.getFilePath())).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<Entry> getConflictEntries() {
        List<String[]> conflictEntries = characterisationResultViewRepository.getConflictEntries();
        List<Entry> result = conflictEntries.stream().map(item -> new Entry(item[0], item[1])).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<Entry> getEntries() {
        List<Object[]> filepathProperty = characterisationResultRepository.getFilepathProperty();
        List<Entry> result = filepathProperty.stream().map(item -> new Entry(item[0].toString(), item[1].toString())).collect(Collectors.toList());
        return result;
    }


    public List<CharacterisationResult> getConflictsByFilepath(String filepath) {
        List<CharacterisationResultViewJPA> allJPAByFilePath = characterisationResultViewRepository.findAllByFilePath(filepath);
        List<CharacterisationResult> result = allJPAByFilePath.stream().filter(item -> item.getValue().equals("CONFLICT")).map(item -> new CharacterisationResult(Property.valueOf(item.getProperty()), item.getValue(),
                ValueType.valueOf(item.getValueType()), null, item.getFilePath())).collect(Collectors.toList());
        return result;
    }

    @Override
    public Map<String, Double> getCollectionStatistics(FilterCriteria filterCriteria) {
        Map<String, Double> result = new HashMap<>();

        double[] sizeStatistics = characterisationResultViewRepository.getSizeStatistics(filterCriteria);
        result.put("totalSize", sizeStatistics[0]);
        result.put("minSize", sizeStatistics[1]);
        result.put("maxSize", sizeStatistics[2]);
        result.put("avgSize", sizeStatistics[3]);
        result.put("totalCount", sizeStatistics[4]);

        double[] conflictStatistics = characterisationResultViewRepository.getConflictStatistics(filterCriteria);
        result.put("conflictRate", conflictStatistics[1]);
        result.put("conflictCount", conflictStatistics[0]);
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
        List<CharacterisationResultJPA> tmp = new ArrayList<>();
        characterisationResults.stream().forEach(item -> {
            if (null == item) {
                LOG.error("Bad characterisation result: " + item);
            } else {
                CharacterisationResultJPA characterisationResultJPA = new CharacterisationResultJPA(item);
                String value = characterisationResultJPA.getValue();
                if (value != null) {
                    if (value.length() > 255) {
                        characterisationResultJPA.setValue(value.substring(0, 255));
                    }
                    tmp.add(characterisationResultJPA);
                }
            }
        });
        try {
            characterisationResultRepository.saveFast(tmp);
        } catch (RuntimeException e) {
            LOG.error("Some characterisation results have already been persisted. Batch insert is not possible. Uploaded items with NULL values:" );
            List<CharacterisationResultJPA> collect = tmp.stream().filter(item -> item.getSource() == null || item.getProperty() == null || item.getFilePath() == null).collect(Collectors.toList());
            LOG.error(collect.toString());
            e.printStackTrace();
            throw new IllegalArgumentException("Some characterisation results have already been persisted. Batch insert is not possible.");

        }
    }

    @Override
    public double getConflictRate() {
        Long totalCount = characterisationResultViewRepository.getTotalCount();
        Long conflictCount = characterisationResultViewRepository.getConflictCount();
        return conflictCount / (double) totalCount;
    }

    @Override
    public void delete(CharacterisationResult characterisationResult) {
        characterisationResultRepository.delete(new CharacterisationResultJPA(characterisationResult));
    }

}
