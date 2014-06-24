CREATE TRIGGER MOVIMENTATION_SPECS_INS AFTER INSERT ON MOVIMENTATION_SPECS 
	REFERENCING NEW ROW AS oldrow FOR EACH ROW
	CALL DOMOV (oldrow.SITE, oldrow.MOVIMENTATION_TYPE, oldrow.ID_ART, oldrow.QUANTITY , 1);
	
CREATE TRIGGER MOVIMENTATION_SPECS_DEL AFTER DELETE ON MOVIMENTATION_SPECS 
	REFERENCING OLD ROW AS oldrow FOR EACH ROW
	CALL DOMOV (oldrow.SITE, oldrow.MOVIMENTATION_TYPE, oldrow.ID_ART, oldrow.QUANTITY , -1);
	  
CREATE TRIGGER MOVIMENTATION_SPECS_AF_UPD AFTER UPDATE ON MOVIMENTATION_SPECS 
  	REFERENCING OLD ROW AS oldrow NEW ROW AS newrow FOR EACH ROW
   	CALL DOUPDATE (oldrow.SITE, oldrow.MOVIMENTATION_TYPE, oldrow.ID_ART, oldrow.QUANTITY, newrow.SITE, newrow.MOVIMENTATION_TYPE, newrow.ID_ART, newrow.QUANTITY );
   	
   	
   	
   	
   	