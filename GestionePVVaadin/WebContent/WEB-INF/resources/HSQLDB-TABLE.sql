SET AUTOCOMMIT TRUE;

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

CREATE TRIGGER SITES_VERSION BEFORE UPDATE ON SITES
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER USERS_VERSION BEFORE UPDATE ON USERS
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER COLORS_VERSION BEFORE UPDATE ON COLORS
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER SEASONS_VERSION BEFORE UPDATE ON SEASONS
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER SIZES_VERSION BEFORE UPDATE ON SIZES
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER CATEGORIES_VERSION BEFORE UPDATE ON CATEGORIES
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER ARTICLES_VERSION BEFORE UPDATE ON ARTICLES 
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER REGISTRY_VERSION BEFORE UPDATE ON REGISTRY
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER MOVIMENTATION_TYPES_VERSION BEFORE UPDATE ON MOVIMENTATION_TYPES
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER MOVIMENTATIONS_VERSION BEFORE UPDATE ON MOVIMENTATIONS
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER MOVIMENTATION_SPECS_VERSION BEFORE UPDATE ON MOVIMENTATION_SPECS
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;
	
CREATE TRIGGER INVENTORY_VERSION BEFORE UPDATE ON INVENTORY
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	SET newrow.VERSIONID = oldrow.VERSIONID+1;

INSERT INTO SEASONS VALUES (NULL, 'N/A',0);
INSERT INTO COLORS VALUES (NULL, 'N/A',0);
INSERT INTO SIZES VALUES (NULL, 'N/A',0);
INSERT INTO MOVIMENTATION_TYPES VALUES (NULL, 'SELL' , -1 ,0 );
INSERT INTO MOVIMENTATION_TYPES VALUES (NULL, 'BUY' , +1 ,0 );
INSERT INTO SITES VALUES (NULL, 'CENTRAL','DEFAULT SITE',0)
INSERT INTO SITES VALUES (NULL, 'SECONDARY','SECONDARY SITE',0)
INSERT INTO USERS VALUES (NULL,'admin','70b3f062173dafebc53c2ef9bf20e17d9b0e63d2',99,0,0);
INSERT INTO USERS VALUES (NULL,'user','70b3f062173dafebc53c2ef9bf20e17d9b0e63d2',99,0,0);
INSERT INTO CATEGORIES VALUES (NULL,NULL,'MAIN',0);
INSERT INTO ARTICLES VALUES (NULL, 0, 'ART1','TEST1', 0, 0, 0, 10, 0, 0);

CREATE PROCEDURE DOMOV (site_id int, art_id int, q int, effect int, mul int) 
	MODIFIES SQL DATA
	BEGIN ATOMIC
		IF NOT EXISTS (SELECT 1 FROM INVENTORY WHERE SITE = site_id AND ARTICLE = art_id) THEN
			INSERT INTO INVENTORY (SITE, ARTICLE) VALUES (site_id,art_id );
		END IF;
		UPDATE INVENTORY SET QUANTITY = QUANTITY + ( q * effect * mul) WHERE SITE = site_id AND ARTICLE = art_id;
		UPDATE ARTICLES SET QUANTITY = QUANTITY + ( q * effect * mul) WHERE ID = art_id;
	END
	
COMMIT;

CREATE TRIGGER MOVIMENTATION_SPECS_INS AFTER INSERT ON MOVIMENTATION_SPECS
	REFERENCING NEW ROW AS newrow FOR EACH ROW
	BEGIN ATOMIC
	DECLARE SITEID, ARTID, Q, EFFECT INT;
	SET SITEID  = SELECT MOVIMENTATIONS.SITE FROM MOVIMENTATION_SPECS
				JOIN MOVIMENTATIONS ON MOVIMENTATION_SPECS.ID_HEAD = MOVIMENTATIONS.ID
     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATIONS.MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
     		WHERE MOVIMENTATION_SPECS.ID = newrow.id;
    SET ARTID = newrow.id;
    SET Q = newrow.QUANTITY;
    SET EFFECT = SELECT MOVIMENTATION_TYPES.QUANTITY_EFFECT FROM MOVIMENTATION_SPECS 
     			JOIN MOVIMENTATIONS ON MOVIMENTATION_SPECS.ID_HEAD = MOVIMENTATIONS.ID
     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATIONS.MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
     		WHERE MOVIMENTATION_SPECS.ID = newrow.id;
    CALL DOMOV (SITEID, ARTID, Q, EFFECT, 1);
    END
    
  CREATE TRIGGER MOVIMENTATION_SPECS_DEL AFTER DELETE ON MOVIMENTATION_SPECS
	REFERENCING OLD ROW AS newrow FOR EACH ROW
	BEGIN ATOMIC
	DECLARE SITEID, ARTID, Q, EFFECT INT;
	SET SITEID  = SELECT MOVIMENTATIONS.SITE FROM MOVIMENTATION_SPECS
				JOIN MOVIMENTATIONS ON MOVIMENTATION_SPECS.ID_HEAD = MOVIMENTATIONS.ID
     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATIONS.MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
     		WHERE MOVIMENTATION_SPECS.ID = newrow.id;
    SET ARTID = newrow.id;
    SET Q = newrow.QUANTITY;
    SET EFFECT = SELECT MOVIMENTATION_TYPES.QUANTITY_EFFECT FROM MOVIMENTATION_SPECS 
     			JOIN MOVIMENTATIONS ON MOVIMENTATION_SPECS.ID_HEAD = MOVIMENTATIONS.ID
     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATIONS.MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
     		WHERE MOVIMENTATION_SPECS.ID = newrow.id;
    CALL DOMOV (SITEID, ARTID, Q, EFFECT, -1);
    END  

  CREATE TRIGGER MOVIMENTATION_SPECS_UPD AFTER UPDATE ON MOVIMENTATION_SPECS
	REFERENCING NEW ROW AS newrow OLD ROW AS oldrow FOR EACH ROW
	BEGIN ATOMIC
	DECLARE SITEID, ARTID, Q_OLD, Q_NEW, EFFECT INT;
	SET SITEID  = SELECT MOVIMENTATIONS.SITE FROM MOVIMENTATION_SPECS
				JOIN MOVIMENTATIONS ON MOVIMENTATION_SPECS.ID_HEAD = MOVIMENTATIONS.ID
     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATIONS.MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
     		WHERE MOVIMENTATION_SPECS.ID = newrow.id;
    SET ARTID = newrow.id;
    SET Q_OLD = oldrow.QUANTITY;
    SET Q_NEW = newrow.QUANTITY;
    SET EFFECT = SELECT MOVIMENTATION_TYPES.QUANTITY_EFFECT FROM MOVIMENTATION_SPECS 
     			JOIN MOVIMENTATIONS ON MOVIMENTATION_SPECS.ID_HEAD = MOVIMENTATIONS.ID
     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATIONS.MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
     		WHERE MOVIMENTATION_SPECS.ID = newrow.id;
    CALL DOMOV (SITEID, ARTID, Q_OLD, EFFECT, -1);
    CALL DOMOV (SITEID, ARTID, Q_NEW, EFFECT, 1);
  END
  
  COMMIT;
  
  
  