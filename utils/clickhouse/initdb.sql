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
