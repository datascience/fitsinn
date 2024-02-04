package rocks.artur.api;

import rocks.artur.domain.FilterCriteria;

import java.util.Map;

public interface GetCollectionStatistics {
    Map<String, Double> getStatistics(FilterCriteria filterCriteria);

}
