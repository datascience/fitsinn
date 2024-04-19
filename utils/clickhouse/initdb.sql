CREATE TABLE characterisationresult
(
    file_path String,
    property String,
    source String,
    property_value String,
    value_type String
) ENGINE = ReplacingMergeTree
    PRIMARY KEY (source, property, file_path)
    ORDER BY (source, property, file_path);

CREATE TABLE characterisationresultaggregated
(
    file_path String,
    property String,
    property_value String
) ENGINE = SummingMergeTree
    PRIMARY KEY (property, file_path)
    ORDER BY (property, file_path);

CREATE MATERIALIZED VIEW characterisationresultview to characterisationresultaggregated AS
SELECT file_path, property,
       CASE
           WHEN COUNT(distinct property_value) = 1 THEN MIN(property_value)
           ELSE 'CONFLICT'
           END AS property_value
FROM characterisationresult
GROUP BY property, file_path;