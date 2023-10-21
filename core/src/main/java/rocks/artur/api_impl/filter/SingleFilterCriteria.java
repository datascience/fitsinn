package rocks.artur.api_impl.filter;

import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;

import java.util.ArrayList;
import java.util.List;

public class SingleFilterCriteria implements FilterCriteria<CharacterisationResult> {
    Property searchKey;
    FilterOperation operation;
    String searchValue;


    public Property getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(Property searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public FilterOperation getOperation() {
        return operation;
    }

    public void setOperation(FilterOperation operation) {
        this.operation = operation;
    }

    public SingleFilterCriteria(Property propertyKey, FilterOperation operation, String propertyValue) {
        this.searchKey = propertyKey;
        this.operation = operation;
        this.searchValue = propertyValue;
    }

    @Override
    public List<CharacterisationResult> meetCriteria(List<CharacterisationResult> items) {
        List<CharacterisationResult> results = new ArrayList<>();
        for (CharacterisationResult item : items) {
            switch (operation) {
                case EQUAL:
                    if (item.getProperty() == searchKey) {
                        results.add(item);
                    }
                    break;
                default:
                    throw new TypeNotPresentException(searchKey.toString(), null);
            }
        }
        return results;
    }

    @Override
    public String toString() {
        return "SingleFilterCriteria{" +
                "searchKey=" + searchKey +
                ", operation=" + operation +
                ", searchValue='" + searchValue + '\'' +
                '}';
    }
}
