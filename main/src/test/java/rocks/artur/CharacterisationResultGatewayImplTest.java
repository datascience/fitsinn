package rocks.artur;


import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rocks.artur.domain.Entry;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.SamplingAlgorithms;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;
import rocks.artur.endpoints.CriteriaParser;
import rocks.artur.jpa.CharacterisationResultGatewayJpaImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest
class CharacterisationResultGatewayImplTest {

    @Autowired
    CharacterisationResultGatewayJpaImpl characterisationResultGatewaySqlImpl;

    @Test
    void getAllTest() {

        Iterable<CharacterisationResult> characterisationResults =
                characterisationResultGatewaySqlImpl.getCharacterisationResults(null);

        List<CharacterisationResult> list = new ArrayList<>();
        characterisationResults.forEach(list::add);
        Assert.assertEquals(14, list.size());
    }


    @Test
    void getPropertyDistributionTest() {
        List<PropertyStatistic> propertyDistribution = characterisationResultGatewaySqlImpl.getPropertyDistribution(null);
        Assert.assertEquals(4, propertyDistribution.size());
    }

    @Test
    void getPropertyValueDistributionWithFilterTest() throws ParseException {
        String typeFilter = "FORMAT=\"Portable Document Format\"";
        CriteriaParser parser = new CriteriaParser();
        FilterCriteria parse = parser.parse(typeFilter);
        List<PropertyValueStatistic> propertyValueDistribution = characterisationResultGatewaySqlImpl.getPropertyValueDistribution(Property.FORMAT, parse);
        System.out.println(propertyValueDistribution);
        Assert.assertEquals(1, propertyValueDistribution.size());
    }

    @Test
    void getPropertyValueDistributionWithoutFilterTest() {

        List<PropertyValueStatistic> propertyValueDistribution = characterisationResultGatewaySqlImpl.getPropertyValueDistribution(Property.FORMAT, null);
        System.out.println(propertyValueDistribution);
        List<CharacterisationResult> characterisationResults = characterisationResultGatewaySqlImpl.getCharacterisationResults(null);
        System.out.println(characterisationResults);
        Assert.assertEquals(3, propertyValueDistribution.size());
    }


    @Test
    void getPropertyValueFloatDistributionWithoutFilterTest() {

        List<PropertyValueStatistic> propertyValueDistribution = characterisationResultGatewaySqlImpl.getPropertyValueDistribution(Property.SIZE, null);
        Assert.assertEquals(2, propertyValueDistribution.size());
    }

    @Test
    void getPropertyValueDistributionWithoutFilterCONFLICTTest() {

        List<PropertyValueStatistic> propertyValueDistribution = characterisationResultGatewaySqlImpl.getPropertyValueDistribution(Property.MIMETYPE, null);
        Assert.assertEquals(2, propertyValueDistribution.size());
        boolean conflict = propertyValueDistribution.stream().anyMatch(propertyValueStatistic -> propertyValueStatistic.getValue().equals("CONFLICT"));
        Assert.assertFalse(conflict);
    }

    @Test
    void getCharacterisationResultsByFilepathTest() {
        Iterable<CharacterisationResult> propertyValueStatistics =
                characterisationResultGatewaySqlImpl.getCharacterisationResultsByFilepath("/home/artur");

        List<CharacterisationResult> list = new ArrayList<>();
        propertyValueStatistics.forEach(list::add);
        Assert.assertEquals(5, list.size());
    }


    @Test
    void getCollectionStatisticsWithoutFilterTest() throws ParseException {
        String typeFilter = "FORMAT=\"Portable Document Format\"";
        CriteriaParser parser = new CriteriaParser();
        FilterCriteria parse = parser.parse(typeFilter);
        Map<String, Double> sizeStatistics = characterisationResultGatewaySqlImpl.getSizeStatistics(null);
        Assert.assertEquals(10047.0, sizeStatistics.get("totalSize"), 0.1);
        System.out.println(sizeStatistics);
    }

    @Test
    void getCollectionStatisticsWithFilterTest() throws ParseException {
        String typeFilter = "FORMAT=\"Portable Document Format\"";
        CriteriaParser parser = new CriteriaParser();
        FilterCriteria parse = parser.parse(typeFilter);
        Map<String, Double> sizeStatistics = characterisationResultGatewaySqlImpl.getSizeStatistics(parse);
        Assert.assertEquals(4.0, sizeStatistics.get("totalSize"), 0.1);
        System.out.println(sizeStatistics);
    }


    @Test
    void getRandomSamplesTest() {
        List<Property> properties = new ArrayList<>();
        properties.add(Property.FORMAT);
        List<String[]> samples = characterisationResultGatewaySqlImpl.getSamples(null, SamplingAlgorithms.RANDOM, properties);
        Assert.assertEquals(5, samples.size());
    }

    @Test
    void getSFDSamplesTest() {
        List<Property> properties = new ArrayList<>();
        properties.add(Property.FORMAT);
        List<String[]> samples = characterisationResultGatewaySqlImpl.getSamples(null, SamplingAlgorithms.SELECTIVE_FEATURE_DISTRIBUTION, properties);
        Assert.assertEquals(3, samples.size());
    }

    @Test
    void getConflictRateTest() {

        double conflictRate = characterisationResultGatewaySqlImpl.getConflictRate();
        Assert.assertEquals(0.4,conflictRate, 0.01);
    }


    @Test
    void getConflictsByFilepathTest() {
        List<Entry> filepathProperty = characterisationResultGatewaySqlImpl.getConflictEntries();
        Assert.assertEquals(2,filepathProperty.size());
    }


    @Test
    void getCharacterisationResultsByFilepathPropertyTest() {
        List<Entry> filepathProperty = characterisationResultGatewaySqlImpl.getEntries();

        List<CharacterisationResult> results = new ArrayList<>();

        for (Entry strings : filepathProperty) {
            List<CharacterisationResult> characterisationResultsByFilepathProperty = characterisationResultGatewaySqlImpl.getCharacterisationResultsByEntry(strings);
            results.addAll(characterisationResultsByFilepathProperty);
        }

        Assert.assertEquals(14,results.size());
    }
}