package rocks.artur.api_impl.filter;

import rocks.artur.domain.FilterCriteria;

import java.util.List;

public class OrFilterCriteria<T> implements FilterCriteria<T> {
    private FilterCriteria criteria;

    public FilterCriteria getCriteria() {
        return criteria;
    }

    public FilterCriteria getOtherCriteria() {
        return otherCriteria;
    }

    private FilterCriteria otherCriteria;

    public OrFilterCriteria(FilterCriteria criteria, FilterCriteria otherCriteria) {
        this.criteria = criteria;
        this.otherCriteria = otherCriteria;
    }

    @Override
    public List<T> meetCriteria(List<T> items) {
        List<T> firstItemsMet = criteria.meetCriteria(items);
        List<T> otherItemsMet = otherCriteria.meetCriteria(items);

        for (T t : otherItemsMet) {
            if (!firstItemsMet.contains(t)) {
                firstItemsMet.add(t);
            }
        }
        return firstItemsMet;
    }

    @Override
    public String toString() {
        return "OrFilterCriteria{" +
                "criteria=" + criteria +
                ", otherCriteria=" + otherCriteria +
                '}';
    }
}
