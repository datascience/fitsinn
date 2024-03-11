package rocks.artur.jpa.view;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomCharacterisationResultViewRepositoryImpl implements CustomCharacterisationResultViewRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CustomCharacterisationResultViewRepositoryImpl.class);
    private final EntityManager entityManager;
    private final FilterJPA filterJPA = new FilterJPA();

    public CustomCharacterisationResultViewRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List getPropertyValueDistribution(String property, FilterCriteria<CharacterisationResult> filter) {

        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filter != null) {
            subquery = filterJPA.convert(filter);
        }

        String query = String.format(
                "select PROPERTY_VALUE, count(*) " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH " +
                        "where PROPERTY= '%s' group by PROPERTY_VALUE", subquery, property);

        List resultList = entityManager.createNativeQuery(query).getResultList();
        return resultList;
    }

    @Override
    public List getPropertyValueTimeStampDistribution(String property, FilterCriteria<CharacterisationResult> filter) {

        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filter != null) {
            subquery = filterJPA.convert(filter);
        }
        //THIS IS H2-SPECIFIC SQL, BECAUSE OF PARSEDATETIME
        String query = String.format(
                "select CASE " +
                        "WHEN PROPERTY_VALUE = 'CONFLICT' THEN PROPERTY_VALUE " +
                        "ELSE SUBSTRING(PROPERTY_VALUE,1,4) " +
                        "END, count(*) " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH " +
                        "where PROPERTY= '%s' group by CASE " +
                        "WHEN PROPERTY_VALUE = 'CONFLICT' THEN PROPERTY_VALUE " +
                        "ELSE SUBSTRING(PROPERTY_VALUE,1,4) " +
                        "END", subquery, property);

        List resultList = entityManager.createNativeQuery(query).getResultList();
        return resultList;
    }

    @Override
    public List<Object[]> getObjects(FilterCriteria filterCriteria) {
        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filterCriteria != null) {
            subquery = filterJPA.convert(filterCriteria);
        }

        String query = String.format(
                "select t.FILE_PATH, count(*) " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH " +
                        "group by t.FILE_PATH", subquery);

        List resultList = entityManager.createNativeQuery(query).getResultList();
        return resultList;
    }

    @Override
    public double[] getSizeStatistics(FilterCriteria filterCriteria) {
        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filterCriteria != null) {
            subquery = filterJPA.convert(filterCriteria);
        }

        String query = String.format(
                "select  IFNULL(sum(cast(t.property_value as SIGNED)),0) as totalsize,  " +
                        "IFNULL(min(cast(t.property_value as SIGNED)),0) as minsize, " +
                        "IFNULL(max(cast(t.property_value as SIGNED)),0) as maxsize, " +
                        "IFNULL(avg(cast(t.property_value as SIGNED)),0) as avgsize, " +
                        "count(t.property_value) as count " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH " +
                        "where t.PROPERTY='SIZE'", subquery);

        Object[] singleResult = (Object[]) entityManager.createNativeQuery(query).getSingleResult();
        Double sum = Double.valueOf(singleResult[0].toString());
        Double min = Double.valueOf(singleResult[1].toString());
        Double max = Double.valueOf(singleResult[2].toString());
        Double avg = Double.valueOf(singleResult[3].toString());
        Double count = Double.valueOf(singleResult[4].toString());
        double[] result = new double[]{sum, min, max, avg, count};
        return result;
    }


    @Override
    public double[] getConflictStatistics(FilterCriteria filterCriteria) {
        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filterCriteria != null) {
            subquery = filterJPA.convert(filterCriteria);
        }

        String query = String.format(
                "select count(distinct t.FILE_PATH) as count " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH " +
                        "where t.PROPERTY_VALUE='CONFLICT'", subquery);

        Long conflictsCount = (Long) entityManager.createNativeQuery(query).getSingleResult();


        String query2 = String.format(
                "select count(distinct t.FILE_PATH) as count " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH ", subquery);

        Long totalCount = (Long) entityManager.createNativeQuery(query2).getSingleResult();

        double rate = 0d;
        if (totalCount != 0) {
            rate = (double) conflictsCount / totalCount;
        }
        double[] result = new double[]{conflictsCount, rate};
        return result;
    }

    @Override
    public List<String[]> getRandomSamples(FilterCriteria filterCriteria, int sampleSize) {
        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filterCriteria != null) {
            subquery = filterJPA.convert(filterCriteria);
        }

        String query = String.format(
                "select t.FILE_PATH " +
                        "from characterisationresultview t " +
                        "join (%s) c on t.FILE_PATH=c.FILE_PATH group by t.FILE_PATH " +
                        "ORDER BY RAND() LIMIT %d  ", subquery, sampleSize);

        List<String> resultList = entityManager.createNativeQuery(query).getResultList();
        List<String[]> collect = resultList.stream().map(item -> new String[]{"1", item}).collect(Collectors.toList());
        return collect;
    }


    public List<String[]> getSelectiveFeatureDistributionSamples(FilterCriteria filterCriteria, List<Property> properties) {
        String subquery = "select distinct FILE_PATH from characterisationresultview ";
        if (filterCriteria != null) {
            subquery = filterJPA.convert(filterCriteria);
        }


        StringBuilder select = new StringBuilder("SELECT ");

        for (int i = 0; i < properties.size(); i++) {
            String currProperty = properties.get(i).name();
            if (i == 0) {
                select.append(String.format("count(%s.file_path) as size, min(%s.file_path) as example, %s.property_value ", currProperty, currProperty, currProperty));
            } else {
                select.append(String.format(", %s.property_value ", currProperty));
            }
        }

        StringBuilder from = new StringBuilder("FROM ");

        for (int i = 0; i < properties.size(); i++) {
            String currProperty = properties.get(i).name();
            if (i == 0) {

                from.append(String.format(" (SELECT v.property_value, v.file_path FROM characterisationresultview v\n" +
                        "join (%s) c on v.FILE_PATH=c.FILE_PATH where v.property='%s' ) %s ", subquery, currProperty, currProperty));
            } else {
                from.append(String.format(" join (SELECT v.property_value, v.file_path FROM characterisationresultview v\n" +
                        "join (%s) c on v.FILE_PATH=c.FILE_PATH where v.property='%s') %s on %s.file_path=%s.file_path ", subquery, currProperty, currProperty, properties.get(0), currProperty));
            }   //TODO: Probably, the join is not required. Check if it is true.
        }

        StringBuilder groupBy = new StringBuilder("GROUP BY ");

        for (int i = 0; i < properties.size(); i++) {
            String currProperty = properties.get(i).name();
            if (i == 0) {
                groupBy.append(String.format(" %s.property_value ", currProperty));
            } else {
                groupBy.append(String.format(", %s.property_value ", currProperty));
            }
        }


        StringBuilder orderBy = new StringBuilder("ORDER BY size DESC");

        String query = String.format(
                "%s %s %s %s", select, from, groupBy, orderBy);

        List<String[]> resultList = entityManager.createNativeQuery(query).getResultList();
        return resultList;
    }


}
