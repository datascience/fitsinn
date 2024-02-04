package rocks.artur.api;

import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.SamplingAlgorithms;

import java.util.List;

/**
 * This interface enables getting characterisation results.
 */
public interface GetSamples {

    void setAlgorithm(SamplingAlgorithms algorithm);

    void setProperties(List<Property> properties);

    Iterable<String> getObjects(FilterCriteria filterCriteria);

    List<String[]> getSamplingInfo(FilterCriteria filterCriteria);
}
