package rocks.artur.jpa.view;

import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;

import java.util.List;

public interface CustomCharacterisationResultViewRepository {
    List getPropertyValueDistribution(String property, FilterCriteria<CharacterisationResult> filter);
    List getPropertyValueTimeStampDistribution(String property, FilterCriteria<CharacterisationResult> filter);

    List<Object[]> getObjects(FilterCriteria filterCriteria);

    List<String[]> getRandomSamples(FilterCriteria filterCriteria, int sampleSize);

    List<String[]> getSelectiveFeatureDistributionSamples(FilterCriteria filterCriteria, List<Property> properties);

    double[] getSizeStatistics(FilterCriteria filterCriteria);
}
