DROP VIEW IF EXISTS characterisationresultview;
DROP TABLE IF EXISTS characterisationresult;

DROP TABLE IF EXISTS seq;
CREATE TABLE seq (
next_val BIGINT DEFAULT NULL
);

INSERT INTO seq VALUES (1);


CREATE TABLE characterisationresult (
id BIGINT NOT NULL,
filePath varchar(255)  NOT NULL,
property varchar(255)  NOT NULL,
source varchar(255)  NOT NULL,
property_value varchar(255)  NOT NULL,
valueType  varchar(255)  NOT NULL,
PRIMARY KEY ( id )
);

CREATE INDEX idx_characterisationresult_filepath
    ON characterisationresult (filePath, property);

CREATE VIEW characterisationresultview AS
SELECT t.filePath, t.property, t.valueType,
       CASE
           WHEN COUNT(distinct t.property_value) = 1 THEN MIN(t.property_value)
           ELSE 'CONFLICT'
       END AS property_value
FROM characterisationresult t
GROUP BY t.filePath, t.property,t.valueType;

