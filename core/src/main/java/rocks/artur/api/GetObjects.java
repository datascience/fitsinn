package rocks.artur.api;

import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;

import java.util.List;

/**
 * This interface enables getting characterisation results.
 */
public interface GetObjects {
    List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filterCriteria);
    Iterable<CharacterisationResult> getObject(String filePath);
}
