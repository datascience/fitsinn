package rocks.artur.jpa.view;

import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;

import java.util.List;

public interface CustomCharacterisationResultViewRepository {
    List getPropertyValueDistribution(FilterCriteria<CharacterisationResult> filter);
    List getPropertyValueTimeStampDistribution(FilterCriteria<CharacterisationResult> filter);

    List<Object[]> getObjects(FilterCriteria filterCriteria);

    double[] getConflictStatistics(FilterCriteria filterCriteria);

    List<String[]> getRandomSamples(FilterCriteria filterCriteria, int sampleSize);

    List<String[]> getSelectiveFeatureDistributionSamples(FilterCriteria filterCriteria, List<Property> properties);

    double[] getSizeStatistics(FilterCriteria filterCriteria);
}
