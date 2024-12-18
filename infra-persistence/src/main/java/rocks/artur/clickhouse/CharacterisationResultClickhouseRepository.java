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
import rocks.artur.domain.statistics.PropertiesPerObjectStatistic;
import rocks.artur.domain.statistics.PropertyStatistic;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
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

    public void save(CharacterisationResult characterisationResult, String datasetName) {
        this.createDb(datasetName);
        int rowsInserted = template.update(String.format("insert into %s.characterisationresult (file_path,property, source, property_value, value_type)" +
                        " values (?,?,?,?,?)", datasetName),
                characterisationResult.getFilePath(),
                characterisationResult.getProperty().name(),
                characterisationResult.getSource(),
                characterisationResult.getValue(),
                characterisationResult.getValueType().name());

        System.out.println("Number of rows updated = " + rowsInserted);
    }

    public List<PropertyStatistic> getPropertyDistribution(String datasetName) {
        String sql = String.format(
                "select property, count(property_value) as number " +
                        "from %s.characterisationresultaggregated " +
                        "group by property ORDER BY number desc LIMIT 200", datasetName);

        List<PropertyStatistic> result = template.query(sql, (rs, rowNum) -> {
            PropertyStatistic propstat = new PropertyStatistic(rs.getLong("number"), Property.valueOf(rs.getString("property")));
            return propstat;
        });
        return result;
    }

    public List<Object[]> getPropertyValueDistribution(String property, FilterCriteria<CharacterisationResult> filter, String datasetName) {
        String subquery = "";
        if (filter != null) {
            subquery = convert(filter, datasetName);
            subquery = String.format(" file_path in (%s) and ", subquery);
        }

        String sql = String.format(
                "select property, property_value, count(property_value) as number " +
                        "from %s.characterisationresultaggregated " +
                        "where %s property = '%s' group by property, property_value ORDER BY number desc LIMIT 200", datasetName, subquery, property);
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


    public List<Object[]> getPropertyValueTimeStampDistribution(String property, FilterCriteria<CharacterisationResult> filter, String datasetName) {
        String subquery = "";
        if (filter != null) {
            subquery = convert(filter, datasetName);
            subquery = String.format(" file_path in (%s) and ", subquery);
        }

        String sql = String.format(
                "select property, CASE " +
                        "WHEN property_value = 'CONFLICT' THEN property_value " +
                        "ELSE SUBSTRING(property_value,1,4) " +
                        "END as value, count(property) as number " +
                        "from %s.characterisationresultaggregated " +
                        "where %s property = '%s' group by property, CASE " +
                        "WHEN property_value = 'CONFLICT' THEN property_value " +
                        "ELSE SUBSTRING(property_value,1,4) " +
                        "END  ORDER BY number desc LIMIT 200", datasetName, subquery, property);

        List<Object[]> result = template.query(sql, (rs, rowNum) -> {
            Object[] item = new Object[3];
            item[0] = rs.getString(1);
            item[1] = rs.getString(2);
            item[2] = rs.getLong(3);
            return item;
        });
        return result;
    }


    public String convert(FilterCriteria<CharacterisationResult> filter, String datasetName) {
        if (filter instanceof SingleFilterCriteria) {
            Property property = ((SingleFilterCriteria) filter).getSearchKey();
            String operator = ((SingleFilterCriteria) filter).getOperation().getValue();
            String value = ((SingleFilterCriteria) filter).getSearchValue();
            String result;
            switch (property.getValueType()) {
                case TIMESTAMP:
                    if (!value.equals("CONFLICT")) {
                        result = String.format("select file_path from %s.characterisationresult where property = '%s' and cast(property_value as DATETIME) %s cast('%s' as DATE)", datasetName, property, operator, value);
                    } else {
                        result = String.format("select file_path from %s.characterisationresultaggregated where property = '%s' and property_value %s '%s'", datasetName, property, operator, value);
                    }
                    break;
                default:
                    result = String.format("select file_path from %s.characterisationresultaggregated where property = '%s' and property_value %s '%s'", datasetName, property, operator, value);
            }
            return result;
        } else if (filter instanceof AndFilterCriteria) {
            AndFilterCriteria andFilter = (AndFilterCriteria) filter;

            String whereStatement1 = convert(andFilter.getCriteria(), datasetName);
            String whereStatement2 = convert(andFilter.getOtherCriteria(), datasetName);

            String result = String.format("( (%s) INTERSECT (%s) )", whereStatement1, whereStatement2);
            return result;

        } else if (filter instanceof OrFilterCriteria) {
            OrFilterCriteria orFilter = (OrFilterCriteria) filter;

            String whereStatement1 = convert(orFilter.getCriteria(), datasetName);
            String whereStatement2 = convert(orFilter.getOtherCriteria(), datasetName);

            String result = String.format("( (%s) UNION ALL (%s) )", whereStatement1, whereStatement2);
            return result;
        } else {
            throw new UnsupportedOperationException("this type of FilterCriteria is not supported");
        }
    }

    public void saveAll(List<CharacterisationResult> characterisationResults, String datasetName) {
        this.createDb(datasetName);
        List<CharacterisationResult> filtered = characterisationResults.stream()
                .filter(item -> item.getFilePath() != null)
                .filter(item -> item.getValue() != null && item.getValue().length() < 300).collect(Collectors.toList());

        template.batchUpdate(String.format("insert into %s.characterisationresult (file_path,property, source, property_value, value_type)" +
                        " values (?,?,?,?,?)", datasetName),
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

    public List<CharacterisationResult> getCharacterisationResults(FilterCriteria<CharacterisationResult> filter, String datasetName) {
        String subquery = "";
        if (filter != null) {
            subquery = convert(filter, datasetName);
            subquery = String.format("where file_path in (%s) ", subquery);
        }


        String sql = String.format(
                "select file_path,property, source, property_value, value_type " +
                        "from %s.characterisationresult " +
                        "%s", datasetName, subquery);

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

    public Long getDigitalObjectCount(String datasetName) {
        String query = String.format(
                "select count(distinct file_path) from %s.characterisationresultaggregated  ", datasetName);

        Long result = template.queryForObject(query, Long.class);
        return result;
    }

    public Long getConflictCount(String datasetName) {
        String query = String.format(
                "select count(distinct file_path) from %s.characterisationresultaggregated where property_value = 'CONFLICT' ", datasetName);

        Long result = template.queryForObject(query, Long.class);
        return result;
    }

    public List<String> getSources(String datasetName) {
        String sql = String.format(
                "select distinct source from %s.characterisationresult ", datasetName);

        List<String> result = template.query(sql, (rs, rowNum) -> {
            return rs.getString(1);
        });
        return result;
    }

    public List<CharacterisationResult> getCharacterisationResultsByFilepath(String filePath, String datasetName) {
        String sql = String.format(
                "select file_path, property, source, property_value, value_type " +
                        "from %s.characterisationresult " +
                        "where file_path='%s' ", datasetName, filePath);

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

    public double[] getSizeStatistics(FilterCriteria filter, String datasetName) {
        String subquery = "";
        if (filter != null) {
            subquery = convert(filter, datasetName);
            subquery = String.format(" file_path in (%s) and ", subquery);
        }

        String sql = String.format(
                "select  sum(toInt32(property_value)) as totalsize,  " +
                        "min(toInt32(property_value)) as minsize, " +
                        "max(toInt32(property_value)) as maxsize, " +
                        "avg(toInt32(property_value)) as avgsize, " +
                        "count(property_value) as count " +
                        "from %s.characterisationresultaggregated " +
                        "where %s property='SIZE'", datasetName, subquery);

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

    public double[] getConflictStatistics(FilterCriteria filter, String datasetName) {
        String subquery = "";
        if (filter != null) {
            subquery = convert(filter, datasetName);
            subquery = String.format(" file_path in (%s) and ", subquery);
        }

        String sql = String.format(
                "select count(distinct file_path) as count " +
                        "from %s.characterisationresultaggregated " +
                        "where %s property_value='CONFLICT'", datasetName, subquery);

        Long conflictsCount = template.queryForObject(sql, Long.class);


        String subquery2 = "";
        if (filter != null) {
            subquery2 = convert(filter, datasetName);
            subquery2 = String.format("where file_path in (%s) ", subquery2);
        }

        String sql2 = String.format(
                "select count(distinct file_path) as count " +
                        "from %s.characterisationresultaggregated " +
                        "%s", datasetName, subquery2);

        Long totalCount = template.queryForObject(sql2, Long.class);

        double rate = 0d;
        if (totalCount != 0) {
            rate = (double) conflictsCount / totalCount;
        }
        double[] result = new double[]{conflictsCount, rate};
        return result;
    }

    public List<PropertiesPerObjectStatistic> getObjects(FilterCriteria filter, String datasetName) {
        String subquery = "";
        if (filter != null) {
            subquery = convert(filter, datasetName);
            subquery = String.format(" where file_path in (%s) ", subquery);
        }

        String sql = String.format(
                "select file_path, count(*) " +
                        "from %s.characterisationresultaggregated " +
                        " %s" +
                        "group by file_path", datasetName, subquery);

        List<PropertiesPerObjectStatistic> result = template.query(sql, (rs, rowNum) -> {
            PropertiesPerObjectStatistic statistic = new PropertiesPerObjectStatistic(rs.getLong(2), rs.getString(1));
            return statistic;

        });

        return result;
    }

    public List<String[]> getRandomSamples(FilterCriteria filterCriteria, int sampleSize, String datasetName) {
        String subquery = "";
        if (filterCriteria != null) {
            subquery = convert(filterCriteria, datasetName);
            subquery = String.format(" where file_path in (%s) ", subquery);
        }

        String sql = String.format(
                "select file_path " +
                        "from %s.characterisationresultaggregated " +
                        " %s" +
                        "group by file_path ORDER BY RAND() LIMIT %d  ", datasetName, subquery, sampleSize);

        List<String> resultList = template.query(sql, (rs, rowNum) -> rs.getString(1));
        List<String[]> collect = resultList.stream().map(item -> new String[]{"1", item}).collect(Collectors.toList());

        return collect;

    }

    public List<String[]> getSelectiveFeatureDistributionSamples(FilterCriteria filterCriteria, List<Property> properties, String datasetName) {
        String subquery = "";
        if (filterCriteria != null) {
            subquery = convert(filterCriteria, datasetName);
            subquery = String.format(" where file_path in (%s) ", subquery);
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

                from.append(String.format(" (SELECT v.property_value, v.file_path FROM %s.characterisationresultaggregated v\n" +
                        "where %s v.property='%s' ) as %s ", datasetName, subquery, currProperty, currProperty));
            } else {
                from.append(String.format(" join (SELECT v.property_value, v.file_path FROM %s.characterisationresultaggregated v\n" +
                        "where %s v.property='%s') as %s on %s.file_path=%s.file_path ", datasetName, subquery, currProperty, currProperty, properties.get(0).name(), currProperty));
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

        String sql = String.format(
                "%s %s %s %s", select, from, groupBy, orderBy);
        System.out.println(sql);


        List<String[]> result = template.query(sql, (rs, rowNum) -> {
            return new String[]{rs.getString(1), rs.getString(2)};
        });

        return result;
    }


    public void resolveConflictsSimple(String datasetName){
        /*
        DROP TABLE IF EXISTS to_delete;

        CREATE TABLE to_delete
        (
            file_path      String,
            property       String,
            source         String
        ) ENGINE = Memory;

        insert into to_delete
        with weights as (
            SELECT source,
                   property,
                   COUNT(property_value) as count,
                   COUNT(property_value) * 1.0/ (SELECT count(property_value) FROM characterisationresultaggregated
                                                 WHERE property_value != 'CONFLICT' ) as weight
            FROM characterisationresult
            WHERE file_path in (SELECT file_path FROM characterisationresultaggregated WHERE property_value != 'CONFLICT' )
            GROUP BY source, property
        ),
             tmp_table as (
                 SELECT file_path, property, source, property_value, weight FROM characterisationresult
                                                                                     JOIN weights on characterisationresult.property == weights.property and characterisationresult.source == weights.source
                 WHERE (file_path, property) in (SELECT file_path, property from characterisationresultaggregated WHERE property_value == 'CONFLICT')
             )
        SELECT file_path,property,source FROM tmp_table
        WHERE (file_path, property, weight)  not in (SELECT file_path, property, MAX(weight) FROM tmp_table GROUP BY file_path, property);

        delete from characterisationresult
        where (file_path, property, source) in (select file_path,property,source from to_delete);

        drop table IF EXISTS characterisationresultaggregated;
         */


        String sql =  String.format("DROP TABLE IF EXISTS %s.to_delete;", datasetName);
        int update = template.update(sql);


        sql =  String.format("" +
                    "       CREATE TABLE %s.to_delete\n" +
                    "        (\n" +
                    "            file_path      String,\n" +
                    "            property       String,\n" +
                    "            source         String\n" +
                    "        ) ENGINE = Memory;", datasetName);
        update = template.update(sql);

        sql =  String.format("" +
                "        insert into %s.to_delete\n" +
                "        with weights as (\n" +
                "            SELECT source,\n" +
                "                   property,\n" +
                "                   COUNT(property_value) as count,\n" +
                "                   COUNT(property_value) * 1.0/ (SELECT count(property_value) FROM %s.characterisationresultaggregated\n" +
                "                                                 WHERE property_value != 'CONFLICT' ) as weight\n" +
                "            FROM %s.characterisationresult\n" +
                "            WHERE file_path in (SELECT file_path FROM %s.characterisationresultaggregated WHERE property_value != 'CONFLICT' )\n" +
                "            GROUP BY source, property\n" +
                "        ),\n" +
                "             tmp_table as (\n" +
                "                 SELECT file_path, property, source, property_value, weight FROM %s.characterisationresult\n" +
                "                                                                                     JOIN weights on %s.characterisationresult.property == weights.property and %s.characterisationresult.source == weights.source\n" +
                "                 WHERE (file_path, property) in (SELECT file_path, property from %s.characterisationresultaggregated WHERE property_value == 'CONFLICT')\n" +
                "             )\n" +
                "        SELECT file_path,property,source FROM tmp_table\n" +
                "        WHERE (file_path, property, weight)  not in (SELECT file_path, property, MAX(weight) FROM tmp_table GROUP BY file_path, property);", datasetName, datasetName, datasetName, datasetName, datasetName, datasetName, datasetName, datasetName);
        update = template.update(sql);

        sql =  String.format("" +
                "       delete from %s.characterisationresult\n" +
                "        where (file_path, property, source) in (select file_path,property,source from %s.to_delete);", datasetName, datasetName);
        update = template.update(sql);

        this.cleanAggregation(datasetName);
    }



     void aggregateResults(String datasetName){
        /*
            CREATE TABLE IF NOT EXISTS characterisationresultaggregated
            ENGINE = AggregatingMergeTree
                  ORDER BY (property, file_path) AS
            SELECT file_path, property,
                   CASE
                       WHEN COUNT(distinct property_value) = 1 THEN MIN(property_value)
                       ELSE 'CONFLICT'
                       END AS property_value
            FROM characterisationresult
            GROUP BY property, file_path;
         */
        String sql  = String.format("" +
                "CREATE TABLE IF NOT EXISTS %s.characterisationresultaggregated\n" +
                "ENGINE = AggregatingMergeTree\n" +
                "      ORDER BY (property, file_path) AS\n" +
                "SELECT file_path, property,\n" +
                "       CASE\n" +
                "           WHEN COUNT(distinct property_value) = 1 THEN MIN(property_value)\n" +
                "           ELSE 'CONFLICT'\n" +
                "           END AS property_value\n" +
                "FROM %s.characterisationresult\n" +
                "GROUP BY property, file_path;", datasetName, datasetName
        );
        template.update(sql);
    }

    void cleanAggregation(String datasetName){
        String sql =  String.format("drop table IF EXISTS %s.characterisationresultaggregated", datasetName);
        int update = template.update(sql);
    }


    void createDb(String datasetName) {
        String sql =  String.format("create database if not exists %s", datasetName);
        int update = template.update(sql);



        /*

        CREATE TABLE newdb.characterisationresult
        (
            file_path String,
            property String,
            source String,
            property_value String,
            value_type String
        ) ENGINE = ReplacingMergeTree
              PRIMARY KEY (source, property, file_path)
              ORDER BY (source, property, file_path);

         */
        sql =  String.format("CREATE TABLE IF NOT EXISTS %s.characterisationresult\n" +
                "(\n" +
                "    file_path String,\n" +
                "    property String,\n" +
                "    source String,\n" +
                "    property_value String,\n" +
                "    value_type String\n" +
                ") ENGINE = ReplacingMergeTree\n" +
                "      PRIMARY KEY (source, property, file_path)\n" +
                "      ORDER BY (source, property, file_path);", datasetName);
        update = template.update(sql);
    }

    public List<String> listDatasets() {
        String sql = String.format("SELECT name FROM system.databases");

        List<String> resultList = template.query(sql, (rs, rowNum) -> rs.getString(1));
        List<String> to_remove = Arrays.asList("system", "information_schema", "INFORMATION_SCHEMA");
        resultList.removeAll(to_remove);
        return resultList;
    }
}
