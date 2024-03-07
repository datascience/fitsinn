DROP VIEW IF EXISTS characterisationresultview;
DROP TABLE IF EXISTS characterisationresult;

CREATE TABLE characterisationresult (
id uuid default random_uuid(),
filePath varchar(200)  NOT NULL,
property varchar(50)  NOT NULL,
source varchar(30)  NOT NULL,
property_value varchar(400)  NOT NULL,
valueType  varchar(200)  NOT NULL,
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

