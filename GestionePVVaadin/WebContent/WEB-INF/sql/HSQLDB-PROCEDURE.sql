CREATE PROCEDURE DOMOV (oldrowheadid int, oldrowart int, oldrowq int, mul int) 
	MODIFIES SQL DATA
	BEGIN ATOMIC
		DECLARE OLDSITEID, OLDEFFECT, NEWSITEID, NEWEFFECT  INT;
		SET OLDSITEID  = SELECT SITE FROM MOVIMENTATIONS WHERE ID = oldrowheadid;
	    SET OLDEFFECT = SELECT QUANTITY_EFFECT FROM MOVIMENTATIONS 
	     			JOIN MOVIMENTATION_TYPES ON MOVIMENTATION_TYPE = MOVIMENTATION_TYPES.ID
	     		WHERE MOVIMENTATIONS.ID = oldrowheadid;
	    IF NOT EXISTS (SELECT 1 FROM INVENTORY WHERE SITE = OLDSITEID AND ARTICLE = oldrowart) THEN 
			INSERT INTO INVENTORY (SITE, ARTICLE) VALUES ( OLDSITEID , oldrowart );
		END IF;
		UPDATE INVENTORY SET QUANTITY = QUANTITY + ( oldrowq * OLDEFFECT * mul) WHERE SITE = OLDSITEID AND ARTICLE = oldrowart;
		UPDATE ARTICLES SET QUANTITY = QUANTITY + ( oldrowq * OLDEFFECT * mul) WHERE ID = oldrowart;
	END;
	
.;
	


CREATE PROCEDURE DOUPDATE (oldrowheadid int, oldrowart int, oldrowq int, newrowheadid int, newrowart int, newrowq int) 
	MODIFIES SQL DATA
	BEGIN ATOMIC
		CALL DOMOV (oldrowheadid, oldrowart, oldrowq, -1 );
		CALL DOMOV (newrowheadid, newrowart, newrowq, 1 );
	END;


.;

