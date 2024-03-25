package rocks.artur.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import rocks.artur.api_impl.filter.AndFilterCriteria;
import rocks.artur.api_impl.filter.OrFilterCriteria;
import rocks.artur.api_impl.filter.SingleFilterCriteria;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.FilterCriteria;
import rocks.artur.domain.Property;
import rocks.artur.domain.ValueType;
import rocks.artur.domain.statistics.PropertyStatistic;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class CharacterisationResultClickhouseRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterisationResultClickhouseRepository.class);
    private final JdbcTemplate template;

    /**
     * Creates a new instance.
     *
     * @param template to use to perform JDBC queries to the ClickHouse database.
     */
    public CharacterisationResultClickhouseRepository(JdbcTemplate template) {
        this.template = template;
    }

    public void save(CharacterisationResult characterisationResult) {

        int rowsInserted = template.update("insert into characterisationresult (file_path,property, source, property_value, value_type)" +
                        " values (?,?,?,?,?)",
                characterisationResult.getFilePath(),
                characterisationResult.getProperty().name(),
                characterisationResult.getSource(),
                characterisationResult.getValue(),
                characterisationResult.getValueType().name());

        System.out.println("Number of rows updated = " + rowsInserted);
    }

    public List<PropertyStatistic> getPropertyDistribution() {
        String sql = String.format(
                "select property, count(property_value) as number " +
                        "from characterisationresultview " +
                        "group by property ORDER BY number desc LIMIT 200");

        List<PropertyStatistic> result = template.query(sql, (rs, rowNum) -> {
            PropertyStatistic propstat = new PropertyStatistic(rs.getLong("number"), Property.valueOf(rs.getString("property")));
            return propstat;
        });
        return result;
    }

    public List<Object[]> getPropertyValueDistribution(String property, FilterCriteria<CharacterisationResult> filter) {

        String subquery = "select file_path from characterisationresultview ";
        if (filter != null) {
            subquery = convert(filter);
        }

        String sql = String.format(
                "select property, property_value, count(property_value) as number " +
                        "from characterisationresultview " +
                        "where file_path in (%s) " +
                        "and property = '%s' group by property, property_value ORDER BY number desc LIMIT 200", subquery, property);
        LOG.info(sql);
        List<Object[]> result = template.query(sql, (rs, rowNum) -> {
            Object[] item = new Object[3];
            item[0] = rs.getString("property");
            item[1] = rs.getString("property_value");
            item[2] = rs.getLong("number");
            return item;
        });
        return result;
    }


    public List<Object[]> getPropertyValueTimeStampDistribution(String property, FilterCriteria<CharacterisationResult> filter) {

        String subquery = "select file_path from characterisationresultview ";
        if (filter != null) {
            subquery = convert(filter);
        }

        String sql = String.format(
                "select property, CASE " +
                        "WHEN property_value = 'CONFLICT' THEN property_value " +
                        "ELSE SUBSTRING(property_value,1,4) " +
                        "END as value, count(property) as number " +
                        "from characterisationresultview " +
                        "where file_path in (%s) " +
                        "and property = '%s' group by property, CASE " +
                        "WHEN property_value = 'CONFLICT' THEN property_value " +
                        "ELSE SUBSTRING(property_value,1,4) " +
                        "END  ORDER BY number desc LIMIT 200", subquery, property);

        List<Object[]> result = template.query(sql, (rs, rowNum) -> {
            Object[] item = new Object[3];
            item[0] = rs.getString(1);
            item[1] = rs.getString(2);
            item[2] = rs.getLong(3);
            return item;
        });
        return result;
    }


    public String convert(FilterCriteria<CharacterisationResult> filter) {
        if (filter instanceof SingleFilterCriteria) {
            Property property = ((SingleFilterCriteria) filter).getSearchKey();
            String operator = ((SingleFilterCriteria) filter).getOperation().getValue();
            String value = ((SingleFilterCriteria) filter).getSearchValue();
            String result;
            switch (property.getValueType()) {
                case TIMESTAMP:
                    if (!value.equals("CONFLICT")) {
                        result = String.format("select file_path from characterisationresult where property = '%s' and cast(property_value as DATETIME) %s cast('%s' as DATE)", property, operator, value);
                    } else {
                        result = String.format("select file_path from characterisationresultview where property = '%s' and property_value %s '%s'", property, operator, value);
                    }
                    break;
                default:
                    result = String.format("select file_path from characterisationresultview where property = '%s' and property_value %s '%s'", property, operator, value);
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

    public void saveAll(List<CharacterisationResult> characterisationResults) {

        List<CharacterisationResult> filtered = characterisationResults.stream()
                .filter(item -> item.getFilePath() != null)
                .filter(item -> item.getValue() != null && item.getValue().length() < 300).collect(Collectors.toList());

        template.batchUpdate("insert into characterisationresult (file_path,property, source, property_value, value_type)" +
                        " values (?,?,?,?,?)",
                filtered,
                10000,
                new ParameterizedPreparedStatementSetter<CharacterisationResult>() {
                    @Override
                    public void setValues(PreparedStatement ps, CharacterisationResult cResult) throws SQLException {
                        ps.setString(1, cResult.getFilePath());
                        ps.setString(2, cResult.getProperty().name());
                        ps.setString(3, cResult.getSource());
                        ps.setString(4, cResult.getValue());
                        ps.setString(5, cResult.getValueType().name());
                    }
                });


    }

    public List<CharacterisationResult> getCharacterisationResults(FilterCriteria<CharacterisationResult> filter) {
        String subquery = "select file_path from characterisationresultview ";
        if (filter != null) {
            subquery = convert(filter);
        }

        String sql = String.format(
                "select file_path,property, source, property_value, value_type " +
                        "from characterisationresult " +
                        "where file_path in (%s) ", subquery);

        List<CharacterisationResult> result = template.query(sql, (rs, rowNum) -> {
            CharacterisationResult item = new CharacterisationResult();
            item.setFilePath(rs.getString(1));
            item.setProperty(Property.valueOf(rs.getString(2)));
            item.setSource(rs.getString(3));
            item.setValue(rs.getString(4));
            item.setValueType(ValueType.valueOf(rs.getString(5)));
            return item;
        });
        return result;
    }

    public Long getDigitalObjectCount() {
        String query = String.format(
                "select count(distinct file_path) from characterisationresultview  ");

        Long result = template.queryForObject(query, Long.class);
        return result;
    }

    public Long getConflictCount() {
        String query = String.format(
                "select count(distinct file_path) from characterisationresultview where property_value = 'CONFLICT' ");

        Long result = template.queryForObject(query, Long.class);
        return result;
    }

    public List<String> getSources() {
        String sql = String.format(
                "select distinct source from characterisationresult ");

        List<String> result = template.query(sql, (rs, rowNum) -> {
            return rs.getString(1);
        });
        return result;
    }

    public List<CharacterisationResult> getCharacterisationResultsByFilepath(String filePath) {
        String sql = String.format(
                "select file_path,property, source, property_value, value_type " +
                        "from characterisationresult " +
                        "where file_path=%s ", filePath);

        List<CharacterisationResult> result = template.query(sql, (rs, rowNum) -> {
            CharacterisationResult item = new CharacterisationResult();
            item.setFilePath(rs.getString(1));
            item.setProperty(Property.valueOf(rs.getString(2)));
            item.setSource(rs.getString(3));
            item.setValue(rs.getString(4));
            item.setValueType(ValueType.valueOf(rs.getString(5)));
            return item;
        });
        return result;
    }

    public double[] getSizeStatistics(FilterCriteria filter) {

        String subquery = "select file_path from characterisationresultview ";
        if (filter != null) {
            subquery = convert(filter);
        }

        String sql = String.format(
                "select  sum(toInt32(property_value)) as totalsize,  " +
                        "min(toInt32(property_value)) as minsize, " +
                        "max(toInt32(property_value)) as maxsize, " +
                        "avg(toInt32(property_value)) as avgsize, " +
                        "count(property_value) as count " +
                        "from characterisationresultview " +
                        "where file_path in (%s) " +
                        "and property='SIZE'", subquery);

        List<double[]> result = template.query(sql, (rs, rowNum) -> {
            double sum = rs.getDouble(1);
            double min = rs.getDouble(2);
            double max = rs.getDouble(3);
            double avg = rs.getDouble(4);
            double count = rs.getDouble(5);

            return new double[]{sum, min, max, avg, count};
        });
        return result.get(0);

    }

    public double[] getConflictStatistics(FilterCriteria filter) {
        String subquery = "select file_path from characterisationresultview ";
        if (filter != null) {
            subquery = convert(filter);
        }

        String sql = String.format(
                "select count(distinct file_path) as count " +
                        "from characterisationresultview " +
                        "where file_path in (%s)  " +
                        "and property_value='CONFLICT'", subquery);

        Long conflictsCount = template.queryForObject(sql, Long.class);

        String sql2 = String.format(
                "select count(distinct file_path) as count " +
                        "from characterisationresultview " +
                        "where file_path in (%s)  ", subquery);

        Long totalCount = template.queryForObject(sql2, Long.class);

        double rate = 0d;
        if (totalCount != 0) {
            rate = (double) conflictsCount / totalCount;
        }
        double[] result = new double[]{conflictsCount, rate};
        return result;
    }
}
