CREATE DATABASE fitsinn;
CREATE USER 'user'@'%' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON fitsinn.* TO 'user'@'%';
