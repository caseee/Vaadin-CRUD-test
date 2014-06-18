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


.;


