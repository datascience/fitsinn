package rocks.artur.domain;

import java.util.List;

/**
 * A generic interface to describe filter criteria.
 *
 * @param <T> types such as CharacterisationResult.
 */
public interface FilterCriteria<T> {
    /**
     * returns all items that match the filter criteria.
     * @param items a list of items of e.g. characterisation results.
     * @return filtered items.
     */
    public List<T> meetCriteria(List<T> items);
}


