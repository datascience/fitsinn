package rocks.artur.jpa.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.artur.api_impl.filter.AndFilterCriteria;
import rocks.artur.api_impl.filter.OrFilterCriteria;
import rocks.artur.api_impl.filter.SingleFilterCriteria;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;

public class FilterJPA {

    private static final Logger LOG = LoggerFactory.getLogger(FilterJPA.class);

    public String convert(FilterCriteria<CharacterisationResult> filter) {
        if (filter instanceof SingleFilterCriteria) {
            Property property = ((SingleFilterCriteria) filter).getSearchKey();
            String operator = ((SingleFilterCriteria) filter).getOperation().getValue();
            String value = ((SingleFilterCriteria) filter).getSearchValue();
            LOG.info("New Filter value: " + value);
            String result;
            switch (property.getValueType()) {
                case TIMESTAMP:
                    if (!value.equals("CONFLICT")) {
                        result = String.format("select distinct FILE_PATH from characterisationresult where property = '%s' and cast(PROPERTY_VALUE as DATETIME) %s cast('%s' as DATE)", property, operator, value);
                    } else {
                        result = String.format("select distinct FILE_PATH from characterisationresultview where property = '%s' and property_value %s '%s'", property, operator, value);
                    }
                    break;
                default:
                    result = String.format("select distinct FILE_PATH from characterisationresultview where property = '%s' and property_value %s '%s'", property, operator, value);
            }
            return result;
        } else if (filter instanceof AndFilterCriteria) {
            AndFilterCriteria andFilter = (AndFilterCriteria) filter;

            String whereStatement1 = convert(andFilter.getCriteria());
            String whereStatement2 = convert(andFilter.getOtherCriteria());

            String result = String.format("( %s INTERSECT %s )", whereStatement1, whereStatement2);
            return result;

        } else if (filter instanceof OrFilterCriteria) {
            OrFilterCriteria orFilter = (OrFilterCriteria) filter;

            String whereStatement1 = convert(orFilter.getCriteria());
            String whereStatement2 = convert(orFilter.getOtherCriteria());

            String result = String.format("( %s UNION %s )", whereStatement1, whereStatement2);
            return result;
        } else {
            throw new UnsupportedOperationException("this type of FilterCriteria is not supported");
        }
    }
}