CREATE TABLE SITES (ID IDENTITY, NAME VARCHAR(255) NOT NULL, INFORMATION VARCHAR(255), VERSIONID INT DEFAULT 0 NOT NULL);

CREATE TABLE USERS (ID IDENTITY, USERNAME VARCHAR(255) UNIQUE NOT NULL, PASS VARCHAR(255) NOT NULL, LEVEL INT NOT NULL, SITE INTEGER REFERENCES SITES, VERSIONID INT DEFAULT 0 NOT NULL); 

CREATE TABLE COLORS (ID IDENTITY, NAME VARCHAR(255) NOT NULL, VERSIONID INT DEFAULT 0 NOT NULL);

CREATE TABLE SEASONS (ID IDENTITY, NAME VARCHAR(255) NOT NULL, VERSIONID INT DEFAULT 0 NOT NULL);

CREATE TABLE SIZES(ID IDENTITY, NAME VARCHAR(255) NOT NULL , VERSIONID INT DEFAULT 0 NOT NULL);

CREATE TABLE CATEGORIES (ID IDENTITY, FATHER_ID INTEGER REFERENCES CATEGORIES, CATEGORY VARCHAR(256), VERSIONID INT DEFAULT 0  NOT NULL);

CREATE TABLE ARTICLES (ID IDENTITY, CATEGORY INTEGER REFERENCES CATEGORIES, NAME VARCHAR(255) NOT NULL, DESCRIPTION VARCHAR(255), COLOR INTEGER REFERENCES COLORS, SEASON INTEGER REFERENCES SEASONS, SIZE INTEGER REFERENCES SIZES, PRICE INT, QUANTITY INT DEFAULT 0, VERSIONID INT DEFAULT 0 NOT NULL);

CREATE TABLE REGISTRY (ID IDENTITY, NAME VARCHAR(255), STREET VARCHAR(255), CITY VARCHAR(255), EMAIL VARCHAR(255), PHONE VARCHAR(255) , VERSIONID INT DEFAULT 0  NOT NULL);

CREATE TABLE MOVIMENTATION_TYPES (ID IDENTITY, DESCRIPTION VARCHAR(255), QUANTITY_EFFECT INTEGER, VERSIONID INT DEFAULT 0  NOT NULL);	

CREATE TABLE MOVIMENTATIONS (ID IDENTITY, OPDATE DATE, MOVIMENTATION VARCHAR(255), SITE INTEGER REFERENCES SITES NOT NULL, MOVIMENTATION_TYPE INT NOT NULL REFERENCES MOVIMENTATION_TYPES , INTERNALDESTINATION INT DEFAULT NULL REFERENCES SITES, DESTINATION INT REFERENCES REGISTRY, VERSIONID INT DEFAULT 0  NOT NULL);

CREATE TABLE MOVIMENTATION_SPECS(ID IDENTITY, ID_HEAD INTEGER REFERENCES MOVIMENTATIONS, ID_ART INTEGER REFERENCES ARTICLES, QUANTITY INTEGER, PRICE DECIMAL(10,2), VERSIONID INT DEFAULT 0 NOT NULL);

CREATE TABLE INVENTORY (SITE INT NOT NULL REFERENCES SITES , ARTICLE INT NOT NULL REFERENCES ARTICLES, QUANTITY INT DEFAULT 0, VERSIONID INT DEFAULT 0 NOT NULL, CONSTRAINT SITEART PRIMARY KEY ( SITE, ARTICLE ) );

COMMIT;
  
  
  