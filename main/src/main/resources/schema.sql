DROP ALL OBJECTS;

CREATE TABLE characterisationresult (
id INTEGER NOT NULL AUTO_INCREMENT,
filePath varchar(400),
property varchar(200),
source varchar(200),
property_value varchar(400),
valueType  varchar(200),
CONSTRAINT PK_Characterisationresult PRIMARY KEY (filePath,property, source)
);

CREATE INDEX idx_characterisationresult_filepath
    ON characterisationresult (filePath);

CREATE VIEW characterisationresultview AS
SELECT t.filePath, t.property, t.valueType,
       CASE
           WHEN COUNT(distinct t.property_value) = 1 THEN MIN(t.property_value)
           ELSE 'CONFLICT'
       END AS property_value
FROM characterisationresult t
GROUP BY t.filePath, t.property,t.valueType;

