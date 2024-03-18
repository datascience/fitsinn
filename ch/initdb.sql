CREATE TABLE characterisationresult
(
    file_path String,
    property String,
    source String,
    property_value String,
    value_type String
) ENGINE = MergeTree PRIMARY KEY (file_path, property, source);



CREATE VIEW characterisationresultview AS
SELECT t.file_path, t.property, t.value_type,
       CASE
           WHEN COUNT(distinct t.property_value) = 1 THEN MIN(t.property_value)
           ELSE 'CONFLICT'
           END AS property_value
FROM characterisationresult t
GROUP BY t.file_path, t.property,t.value_type;