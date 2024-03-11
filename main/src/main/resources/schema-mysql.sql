DROP VIEW IF EXISTS characterisationresultview;
DROP TABLE IF EXISTS characterisationresult;


CREATE TABLE characterisationresult (
id varchar(255) NOT NULL,
file_path varchar(255)  NOT NULL,
property varchar(255)  NOT NULL,
source varchar(255)  NOT NULL,
property_value varchar(255)  NOT NULL,
value_type  varchar(255)  NOT NULL,
PRIMARY KEY ( id ),
UNIQUE (file_path, property, source)
);

CREATE INDEX idx_characterisationresult_filepath
    ON characterisationresult (file_path, property, source);

CREATE VIEW characterisationresultview AS
SELECT t.file_path, t.property, t.value_type,
       CASE
           WHEN COUNT(distinct t.property_value) = 1 THEN MIN(t.property_value)
           ELSE 'CONFLICT'
       END AS property_value
FROM characterisationresult t
GROUP BY t.file_path, t.property, t.value_type;

