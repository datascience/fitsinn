DROP VIEW IF EXISTS characterisationresultview;
DROP TABLE IF EXISTS characterisationresult;

CREATE TABLE characterisationresult (
id varchar(255) NOT NULL,
file_path varchar(255)  NOT NULL,
property varchar(255)  NOT NULL,
source varchar(255)  NOT NULL,
property_value varchar(255)  NOT NULL,
value_type  varchar(255)  NOT NULL,
PRIMARY KEY ( id )
);

CREATE INDEX idx_characterisationresult_1
    ON characterisationresult (file_path);

CREATE INDEX idx_characterisationresult_2
    ON characterisationresult ( property);

CREATE INDEX idx_characterisationresult_3
    ON characterisationresult ( property_value);

CREATE INDEX idx_characterisationresult_4
    ON characterisationresult ( value_type);

CREATE VIEW characterisationresultview AS
SELECT t.file_path, t.property, t.value_type,
       CASE
           WHEN COUNT(distinct t.property_value) = 1 THEN MIN(t.property_value)
           ELSE 'CONFLICT'
       END AS property_value
FROM characterisationresult t
GROUP BY t.file_path, t.property,t.value_type;

