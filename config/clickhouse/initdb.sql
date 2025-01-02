CREATE TABLE IF NOT EXISTS characterisationresult
(
    file_path String,
    property String,
    source String,
    property_value String,
    value_type String
)
    ENGINE = ReplacingMergeTree
    PRIMARY KEY (source, property, file_path)
    ORDER BY (source, property, file_path);

CREATE TABLE IF NOT EXISTS agg_characterisationresult
(
    property String,
    file_path String,
    unique_values AggregateFunction(uniq, String),
    any_value AggregateFunction(any, String)
)
    ENGINE = AggregatingMergeTree
    ORDER BY (property, file_path);

CREATE MATERIALIZED VIEW IF NOT EXISTS mv_characterisationresult
    TO agg_characterisationresult
AS
SELECT
    property,
    file_path,
    uniqState(property_value) AS unique_values,
    anyState(property_value)  AS any_value
FROM characterisationresult
GROUP BY property, file_path;

CREATE VIEW IF NOT EXISTS characterisationresultaggregated
AS
SELECT
    property,
    file_path,
    CASE
        WHEN finalizeAggregation(unique_values) = 1
            THEN finalizeAggregation(any_value)
        ELSE 'CONFLICT'
        END AS property_value
FROM agg_characterisationresult;
