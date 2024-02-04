package rocks.artur.api_impl.filter;

import rocks.artur.domain.FilterCriteria;

import java.util.List;

public class AndFilterCriteria<T> implements FilterCriteria<T> {
    public FilterCriteria getCriteria() {
        return criteria;
    }

    public FilterCriteria getOtherCriteria() {
        return otherCriteria;
    }

    private FilterCriteria criteria;
    private FilterCriteria otherCriteria;

    public AndFilterCriteria(FilterCriteria criteria, FilterCriteria otherCriteria) {
        this.criteria = criteria;
        this.otherCriteria = otherCriteria;
    }

    @Override
    public List<T> meetCriteria(List<T> items) {
        List<T> firstItemsMet = criteria.meetCriteria(items);
        List<T> result = otherCriteria.meetCriteria(firstItemsMet);
        return result;
    }


    @Override
    public String toString() {
        return "AndFilterCriteria{" +
                "criteria=" + criteria +
                ", otherCriteria=" + otherCriteria +
                '}';
    }
}
