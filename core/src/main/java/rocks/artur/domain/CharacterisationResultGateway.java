package rocks.artur.domain;

import rocks.artur.api_impl.filter.Entry;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;
import rocks.artur.domain.statistics.PropertyStatistic;
import rocks.artur.domain.statistics.PropertyValueStatistic;

import java.util.List;
import java.util.Map;

/**
 * This interface enables persistence of characterisation results. Implementation of the interface is technology
 * independent. Different approaches such as RDBMS, document stores or cloud DBs, are possible.
 */
public interface CharacterisationResultGateway {

    /**
     * adds a characterisation result to the persistence.
     *
     * @param characterisationResult
     */
    void addCharacterisationResult(CharacterisationResult characterisationResult);

    /**
     * gets all characterisation results
     *
     * @return an iterable of all results stored in the DB.
     */
    List<CharacterisationResult> getCharacterisationResults(FilterCriteria<CharacterisationResult> filter);

    /**
     * gets a distribution of all properties that match the given filter criteria.
     *
     * @param filter a filter criteria
     * @return a list of property statistics
     */
    List<PropertyStatistic> getPropertyDistribution(FilterCriteria<CharacterisationResult> filter);

    /**
     * gets characterisation results describing a digital object identified by the given file path.
     *
     * @return an iterable of characterisation results.
     */
    List<CharacterisationResult> getCharacterisationResultsByFilepath(String filePath);

    List<CharacterisationResult> getCharacterisationResultsByFilepathProperty(String filepath, Property property);

    List<Entry> getFilepathProperty();

    /**
     * gets a list of characterisation results with conflicts for a given digital object.
     *
     * @return an iterable of characterisation results.
     */
    List<CharacterisationResult> getConflictsByFilepath(String filepath);

    Map<String, Object> getSizeStatistics();

    List<PropertyValueStatistic> getPropertyValueDistribution(Property property, FilterCriteria<CharacterisationResult> filter);

    /**
     * gets a list of sources of characterisation results.
     *
     * @return an iterable of characterisation result sources.
     */
    List<String> getSources();

    /**
     * gets a list of objects.
     *
     * @return an iterable of PropertiesPerObjectStatistic.
     */
    List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria);

    /**
     * gets a list of samples.
     *
     * @return an iterable of PropertiesPerObjectStatistic.
     */
    List<String[]> getSamples(FilterCriteria filterCriteria, SamplingAlgorithms algorithm, List<Property> properties);

    void addCharacterisationResults(List<CharacterisationResult> characterisationResults);

    double getConflictRate();
}
