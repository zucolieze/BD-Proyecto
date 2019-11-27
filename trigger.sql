#--------------------------------------
#---------TRIGGER----------------------
#--------------------------------------

delimiter !

CREATE TRIGGER triggerInstancias
AFTER INSERT ON salidas
FOR EACH ROW

BEGIN

DECLARE dias INT;
DECLARE fechaActual DATE;
DECLARE diaSem varchar(2);
DECLARE claseVuelo VARCHAR(20);

SET dias = 0;
SET fechaActual = CURDATE();

CASE DAYOFWEEK(fechaActual)

	WHEN 2 THEN SET diaSem = 'Lu';
	WHEN 3 THEN SET diaSem = 'Ma';
	WHEN 4 THEN SET diaSem = 'Mi';
	WHEN 5 THEN SET diaSem = 'Ju';
	WHEN 6 THEN SET diaSem = 'Vi';
	WHEN 7 THEN SET diaSem = 'Sa';

	ELSE
		SET diaSem = 'Do';

END CASE;

WHILE (diaSem <> NEW.dia) DO

	SET fechaActual = DATE_ADD(fechaActual, INTERVAL 1 DAY);
	SET dias = dias + 1;

	CASE DAYOFWEEK(fechaActual)

		WHEN 2 THEN SET diaSem = 'Lu';
		WHEN 3 THEN SET diaSem = 'Ma';
		WHEN 4 THEN SET diaSem = 'Mi';
		WHEN 5 THEN SET diaSem = 'Ju';
		WHEN 6 THEN SET diaSem = 'Vi';
		WHEN 7 THEN SET diaSem = 'Sa';

		ELSE
			SET diaSem = 'Do';
	END CASE;
END WHILE;

SELECT b.clase INTO claseVuelo FROM brinda as b WHERE b.vuelo = NEW.vuelo AND b.dia = diaSem;

WHILE (dias < 365) DO

	INSERT INTO instancias_vuelo(vuelo, fecha, dia, estado) VALUES (NEW.vuelo, fechaActual, diaSem, "A tiempo");
	INSERT INTO asientos_reservados(vuelo, fecha, clase, cantidad) VALUES (NEW.vuelo, fechaActual, claseVuelo, 0);
	SET dias = dias + 7;
	SET fechaActual = DATE_ADD(fechaActual, INTERVAL 7 DAY);

END WHILE;

END; !

delimiter ;