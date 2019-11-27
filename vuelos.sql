#--------------------------------------
#---------BD---------------------------
#--------------------------------------

#crear la base de datos
CREATE database vuelos;

#se selecciona la base de datos a usar
USE vuelos;

#--------------------------------------
#---------TABLAS-----------------------
#--------------------------------------

CREATE TABLE pasajeros (
	doc_tipo VARCHAR(16) NOT NULL,
	doc_nro INT UNSIGNED NOT NULL,
	apellido VARCHAR(32) NOT NULL,
	nombre VARCHAR(32) NOT NULL,
	direccion VARCHAR(64) NOT NULL,
	telefono VARCHAR(32) NOT NULL,
	nacionalidad VARCHAR(32) NOT NULL,
  
	CONSTRAINT pk_pasajeros_doc
	PRIMARY KEY (doc_tipo,doc_nro)
) ENGINE=InnoDB;

CREATE TABLE empleados (
	legajo INT UNSIGNED AUTO_INCREMENT NOT NULL,
	password VARCHAR(32) NOT NULL,
	doc_tipo VARCHAR(16) NOT NULL,
	doc_nro INT UNSIGNED NOT NULL,
	apellido VARCHAR(32) NOT NULL, 
	nombre VARCHAR(32) NOT NULL,
	direccion VARCHAR(64) NOT NULL,
	telefono VARCHAR(32) NOT NULL,
   
	CONSTRAINT pk_empleados
	PRIMARY KEY (legajo)
) ENGINE=InnoDB;

CREATE TABLE modelos_avion (
  modelo VARCHAR(32) NOT NULL,
  fabricante VARCHAR(32) NOT NULL,
  cabinas SMALLINT UNSIGNED NOT NULL,
  cant_asientos SMALLINT UNSIGNED NOT NULL,

  CONSTRAINT pk_modelosavion
  PRIMARY KEY (modelo)
) ENGINE=InnoDB;

CREATE TABLE clases(
	nombre VARCHAR(32) NOT NULL,
	porcentaje DECIMAL (2,2) UNSIGNED NOT NULL,
  
	CONSTRAINT pk_clases 
	PRIMARY KEY (nombre),
	
	CONSTRAINT chk_clases_porcentaje
	CHECK (porcentaje BETWEEN 0.00 AND 0.99)
) ENGINE=InnoDB;

CREATE TABLE comodidades (
	codigo SMALLINT UNSIGNED NOT NULL,
	descripcion TEXT(500) NOT NULL,

	CONSTRAINT pk_comodidades
	PRIMARY KEY (codigo)
) ENGINE=InnoDB;

CREATE TABLE ubicaciones(
	pais VARCHAR(32) NOT NULL,
	estado VARCHAR(32) NOT NULL,
	ciudad VARCHAR(32) NOT NULL,
	huso SMALLINT NOT NULL,

	CONSTRAINT pk_ubicaciones
	PRIMARY KEY (pais,estado,ciudad),
	
	CONSTRAINT chk_ubicaciones_huso
	CHECK (huso BETWEEN -12 AND 12)
) ENGINE=InnoDB;

CREATE TABLE aeropuertos(
	codigo VARCHAR(32) NOT NULL,
	nombre VARCHAR(32) NOT NULL,
	telefono VARCHAR(32) NOT NULL,
	direccion VARCHAR(32) NOT NULL,
	pais VARCHAR(32) NOT NULL,
	estado VARCHAR(32) NOT NULL,
	ciudad VARCHAR(32) NOT NULL,

	CONSTRAINT pk_aeropuertos 
	PRIMARY KEY (codigo),
	
	CONSTRAINT FK_aeropuertos
	FOREIGN KEY (pais,estado,ciudad) REFERENCES ubicaciones (pais,estado,ciudad) 
		ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE vuelos_programados(
	numero VARCHAR(32) NOT NULL,
	aeropuerto_salida VARCHAR(64) NOT NULL,
	aeropuerto_llegada VARCHAR(64) NOT NULL,
	
	CONSTRAINT pk_vuelos_programados
	PRIMARY KEY (numero),
	
	FOREIGN KEY (aeropuerto_salida) REFERENCES aeropuertos (codigo)
		ON DELETE RESTRICT ON UPDATE CASCADE,
		
	FOREIGN KEY (aeropuerto_llegada) REFERENCES aeropuertos (codigo)
		ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE salidas(
	vuelo VARCHAR(32) NOT NULL,
	dia ENUM ('Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa') NOT NULL,
	hora_sale TIME NOT NULL,
	hora_llega TIME NOT NULL,
	modelo_avion VARCHAR(32) NOT NULL,
	
	CONSTRAINT pk_salidas_vuelo
	PRIMARY KEY (vuelo,dia),
	
	CONSTRAINT FK_modelo_avion
	FOREIGN KEY (modelo_avion) REFERENCES  modelos_avion (modelo)
		ON DELETE RESTRICT ON UPDATE CASCADE,
	
	CONSTRAINT FK_vuelo_programado
	FOREIGN KEY (vuelo) REFERENCES vuelos_programados (numero)
		ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE instancias_vuelo(
	vuelo VARCHAR(32) NOT NULL,
	dia ENUM ('Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa') NOT NULL,
	fecha DATE NOT NULL,
	estado VARCHAR(32),
	
	CONSTRAINT pk_instancias_vuelo
	PRIMARY KEY (vuelo,fecha),
	
	CONSTRAINT FK_vuelo
	FOREIGN KEY (vuelo,dia) REFERENCES salidas(vuelo,dia)
	ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reservas (
	numero INT UNSIGNED AUTO_INCREMENT NOT NULL,
	fecha DATE NOT NULL,
	vencimiento DATE NOT NULL,
	estado VARCHAR(32) NOT NULL,
	doc_tipo VARCHAR(16) NOT NULL,
	doc_nro INT UNSIGNED NOT NULL,
	legajo INT UNSIGNED NOT NULL,
	
	CONSTRAINT pk_reservas
	PRIMARY KEY (numero),
	
	CONSTRAINT FK_reservas_doc
	FOREIGN KEY (doc_tipo,doc_nro) REFERENCES pasajeros (doc_tipo,doc_nro)
	ON DELETE RESTRICT ON UPDATE CASCADE,
	
	CONSTRAINT FK_reservas_legajo
	FOREIGN KEY (legajo) REFERENCES empleados (legajo)
	ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE brinda (
	vuelo VARCHAR(32) NOT NULL,
	dia ENUM ('Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa') NOT NULL,
	clase VARCHAR(32) NOT NULL, 
	precio DECIMAL (7,2) UNSIGNED NOT NULL,
	cant_asientos SMALLINT unsigned NOT NULL,
	
	CONSTRAINT pk_brinda
	PRIMARY KEY (vuelo, dia, clase),

	CONSTRAINT FK_brinda_salidas
	FOREIGN KEY (vuelo, dia) REFERENCES salidas (vuelo, dia)
	ON DELETE RESTRICT ON UPDATE CASCADE,
  
	CONSTRAINT FK_brinda_clases
	FOREIGN KEY (clase) REFERENCES clases (nombre)
	ON DELETE RESTRICT ON UPDATE CASCADE  
) ENGINE=InnoDB;

CREATE TABLE posee (
	clase VARCHAR(32) NOT NULL,
	comodidad SMALLINT unsigned NOT NULL,
  
	CONSTRAINT pk_posee
	PRIMARY KEY (clase, comodidad),
	
	CONSTRAINT FK_posee_clase
	FOREIGN KEY (clase) REFERENCES clases (nombre)
	ON DELETE RESTRICT ON UPDATE CASCADE,
	
	CONSTRAINT FK_posee_comodidad
	FOREIGN KEY (comodidad) REFERENCES comodidades (codigo)
	ON DELETE RESTRICT ON UPDATE CASCADE 
) ENGINE=InnoDB;

CREATE TABLE reserva_vuelo_clase (
	numero INT UNSIGNED AUTO_INCREMENT NOT NULL,
	vuelo VARCHAR(32) NOT NULL,
	fecha_vuelo DATE NOT NULL,
	clase VARCHAR(32) NOT NULL,
  
	CONSTRAINT pk_reserva_vuelo_clase
	PRIMARY KEY (numero, vuelo, fecha_vuelo),
	
	CONSTRAINT FK_reserva_vuelo_clase_numero
	FOREIGN KEY (numero) REFERENCES reservas (numero)
	ON DELETE RESTRICT ON UPDATE CASCADE,
	
	CONSTRAINT FK_reserva_vuelo_clase_vuelo_fechaVuelo
	FOREIGN KEY (vuelo, fecha_vuelo) REFERENCES instancias_vuelo (vuelo, fecha)
	ON DELETE RESTRICT ON UPDATE CASCADE,
  
	CONSTRAINT FK_reserva_vuelo_clase_nombre
	FOREIGN KEY (clase) REFERENCES clases (nombre)
	ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;  

CREATE TABLE asientos_reservados (
	vuelo VARCHAR(50) NOT NULL,
	fecha DATE NOT NULL,
	clase VARCHAR(20) NOT NULL,
	cantidad INT UNSIGNED NOT NULL,

	CONSTRAINT pk_asientos_reservados
	PRIMARY KEY (vuelo, fecha, clase),
		
	FOREIGN KEY (vuelo, fecha) REFERENCES instancias_vuelo(vuelo, fecha)
	ON DELETE RESTRICT ON UPDATE CASCADE, 
	
	FOREIGN KEY (clase) REFERENCES clases (nombre)
	ON DELETE RESTRICT ON UPDATE CASCADE	

) ENGINE=InnoDB; 

#--------------------------------------
#---------VISTA------------------------
#--------------------------------------

CREATE VIEW vuelos_disponibles AS 
SELECT  DISTINCT	vp.numero AS Numero, 
        s.modelo_avion AS Modelo,
		iv.fecha AS Fecha,
		s.dia AS Dia,
   		s.hora_sale AS HoraSalida,
	    s.hora_llega AS HoraLlegada,											
        CAST(IF(s.hora_llega < s.hora_sale, (CAST('24:00:00' AS TIME) - s.hora_sale + s.hora_llega), s.hora_llega - s.hora_sale) AS TIME) AS duracion,							
        asalida.codigo AS ASalida_codigo,
		asalida.nombre AS ASalida_nombre,
		asalida.ciudad AS ASalida_ciudad,
		asalida.estado AS ASalida_estado,
		asalida.pais AS ASalida_pais,
		allegada.codigo AS ALlegada_codigo,
		allegada.nombre AS ALlegada_nombre,
		allegada.ciudad AS ALlegada_ciudad,
		allegada.estado AS ALlegada_estado,
		allegada.pais AS ALlegada_pais,
		b.clase AS NombreClase,
		b.precio AS Precio,										
		b.cant_asientos + ROUND(b.cant_asientos * porcentaje) - (SELECT count(*) FROM reserva_vuelo_clase rvc WHERE b.vuelo = rvc.vuelo AND rvc.fecha_vuelo = iv.fecha AND rvc.clase = b.clase) AS asientos_disponibles
															
FROM vuelos_programados AS vp JOIN aeropuertos AS allegada ON vp.aeropuerto_llegada = allegada.codigo
    JOIN aeropuertos AS asalida ON vp.aeropuerto_salida = asalida.codigo
	JOIN instancias_vuelo AS iv ON vp.numero = iv.vuelo
	JOIN salidas AS s ON vp.numero = s.vuelo and s.dia = iv. dia 
	JOIN brinda AS b ON vp.numero = b.vuelo AND s.dia = b.dia 
	JOIN clases AS c ON b.clase = c.nombre
	LEFT JOIN reserva_vuelo_clase AS rvc ON vp.numero = rvc.vuelo AND iv.fecha = rvc.fecha_vuelo   

WHERE iv.fecha > NOW();

#--------------------------------------
#---------STORED-PROCEDURE-------------
#--------------------------------------
	
delimiter !

create procedure reservaVueloIda(IN numero VARCHAR(50), IN clase VARCHAR(20), IN fecha DATE, IN tipo_doc VARCHAR(5), IN numero_doc INT, IN legajo INT)
begin	
	
	#declaracion de variables
	DECLARE asientosReservados INT;
	DECLARE asientosBrindados INT;
	DECLARE asientosDisponibles INT;
	DECLARE estado CHAR(100);	
	DECLARE numeroReserva INT;
	
	#Si se produce una SQLEXCEPTION, se retrocede la transacción con ROLLBACK
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN 
	SELECT 'SQLEXCEPTION!, transaccion abortada' as Resultado;
	ROLLBACK;
	END;
	
	#Recupero la cantidad de reservas realizadas para esa clase en ese vuelo
	SELECT asientos_disponibles INTO asientosDisponibles FROM vuelos_disponibles as vp WHERE vp.Numero = numero AND vp.Fecha = fecha AND vp.NombreClase = clase;
	SELECT b.cant_asientos INTO asientosBrindados 
		FROM brinda as b JOIN instancias_vuelo as iv ON b.vuelo = iv.vuelo AND b.dia = iv.dia 
		WHERE b.vuelo = numero AND b.clase = clase AND iv.fecha = fecha;
	
	SELECT cantidad INTO asientosReservados FROM asientos_reservados as ar WHERE ar.vuelo = numero AND ar.fecha = fecha AND ar.clase = clase FOR UPDATE;
	
	if NOT EXISTS(SELECT * FROM empleados as e WHERE e.legajo = legajo) then
		 SELECT 'El empleado ingresado no existe' as Resultado;
	else 
		if NOT EXISTS(SELECT * FROM instancias_vuelo as iv WHERE fecha = iv.fecha AND iv.vuelo = numero) then
			SELECT 'El vuelo ingresado para esa fecha no existe' as Resultado;
		else 
			if NOT EXISTS(SELECT * FROM pasajeros as p WHERE p.doc_tipo = tipo_doc AND p.doc_nro = numero_doc) then
			SELECT 'El pasajero no existe' as Resultado;
			else 
				if asientosDisponibles < 0 then
					SELECT 'No hay mas asientos disponibles' as Resultado;
				else
					#Todos los datos son correctos y se crea la reserva
					if asientosBrindados < asientosReservados then
						SET estado = 'en Espera';
					else
						SET estado = 'Confirmada';
					end if;			
						START TRANSACTION;
							
							# Ingreso la reserva en reservas
								INSERT INTO reservas(numero, doc_tipo, doc_nro, legajo, fecha, vencimiento, estado) 
								VALUES (numeroReserva,tipo_doc , numero_doc, legajo, fecha , DATE_ADD(fecha, INTERVAL -15 day), estado);
							# Ingreso la reserva en reserva_vuelo_clase
								INSERT INTO reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
								VALUES (LAST_INSERT_ID(), numero, fecha,clase);
							
							UPDATE asientos_reservados as ar SET ar.cantidad = asientosReservados + 1  WHERE ar.vuelo = numero AND ar.fecha = fecha AND ar.clase = clase; 
							SELECT 'La reserva se ingreso correctamente' as Resultado;
														
						COMMIT;
				end if;
			end if;
		end if;
	end if;
		
end;!  
delimiter ;

delimiter !
create procedure reservaVueloIdaVuelta(IN numeroIda VARCHAR(50), IN claseIda VARCHAR(20), IN fechaIda DATE,IN numeroVuelta VARCHAR(50), IN claseVuelta VARCHAR(20), IN fechaVuelta DATE, IN tipo_doc VARCHAR(5), IN numero_doc INT, IN legajo INT)
begin	
	
	#declaracion de variables
	DECLARE asientosReservadosIda INT;
	DECLARE asientosBrindadosIda INT;
	DECLARE asientosDisponiblesIda INT;
	DECLARE estadoIda CHAR(100);	
	DECLARE asientosReservadosVuelta INT;
	DECLARE asientosBrindadosVuelta INT;
	DECLARE asientosDisponiblesVuelta INT;
	DECLARE estadoVuelta CHAR(100);
	DECLARE numeroReserva INT;
	
	#Si se produce una SQLEXCEPTION, se retrocede la transacción con ROLLBACK
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN 
	SELECT 'SQLEXCEPTION!, transaccion abortada' as Resultado;
	ROLLBACK;
	END;

	#Recupero datos del vuelo Ida
	SELECT asientos_disponibles INTO asientosDisponiblesIda FROM vuelos_disponibles as vp WHERE vp.Numero = numeroIda AND vp.Fecha = fechaIda AND vp.NombreClase = claseIda;
	SELECT b.cant_asientos INTO asientosBrindadosIda 
		FROM brinda as b JOIN instancias_vuelo as iv ON b.vuelo = iv.vuelo AND b.dia = iv.dia 
		WHERE b.vuelo = numeroIda AND b.clase = claseIda AND iv.fecha = fechaIda;
	SELECT cantidad INTO asientosReservadosIda FROM asientos_reservados as ar WHERE ar.vuelo = numeroIda AND ar.fecha = fechaIda AND ar.clase = claseIda FOR UPDATE;
		
	#Recupero datos del vuelo Vuelta
	SELECT asientos_disponibles INTO asientosDisponiblesVuelta FROM vuelos_disponibles as vp WHERE vp.Numero = numeroVuelta AND vp.Fecha = fechaVuelta AND vp.NombreClase = claseVuelta;
	SELECT b.cant_asientos INTO asientosBrindadosVuelta
		FROM brinda as b JOIN instancias_vuelo as iv ON b.vuelo = iv.vuelo AND b.dia = iv.dia 
		WHERE b.vuelo = numeroVuelta AND b.clase = claseVuelta AND iv.fecha = fechaVuelta;
	SELECT cantidad INTO asientosReservadosVuelta FROM asientos_reservados as ar WHERE ar.vuelo = numeroVuelta AND ar.fecha = fechaVuelta AND ar.clase = claseVuelta FOR UPDATE;
	
	
	if NOT EXISTS(SELECT * FROM empleados as e WHERE e.legajo = legajo) then
		 SELECT 'El empleado ingresado no existe' as Resultado;
	else 
		if NOT EXISTS(SELECT * FROM pasajeros as p WHERE p.doc_tipo = tipo_doc AND p.doc_nro = numero_doc) then
			SELECT 'El pasajero no existe' as Resultado;
		else 
			if NOT EXISTS(SELECT * FROM instancias_vuelo as iv WHERE fechaIda = iv.fecha AND iv.vuelo = numeroVuelta) then
				SELECT 'El vuelo ingresado de Ida para esa fecha no existe' as Resultado;
			else 
				if NOT EXISTS(SELECT * FROM instancias_vuelo as iv WHERE fechaVuelta = iv.fecha AND iv.vuelo = numeroVuelta) then
					SELECT 'El vuelo ingresado de Vuelta para esa fecha no existe' as Resultado;
				else 
					if asientosDisponiblesIda < 0 then
						SELECT 'No hay mas asientos disponibles en el vuelo de ida' as Resultado;
					else
						if asientosDisponiblesVuelta < 0 then
						SELECT 'No hay mas asientos disponibles en el vuelo de vuelta' as Resultado;
						else 
							#Todos los datos son correctos y se crea la reserva
							#Se crea la reserva para la Ida y Vuelta
							START TRANSACTION;
								if asientosBrindadosIda < asientosReservadosIda then
									SET estadoIda = 'en Espera';
								else
									SET estadoIda = 'Confirmada';
								end if;									
									# Ingreso la reserva en reservas
										INSERT INTO reservas(numero, doc_tipo, doc_nro, legajo, fecha, vencimiento, estado) 
										VALUES (numeroReserva, tipo_doc , numero_doc, legajo, fechaIda , DATE_ADD(fechaIda, INTERVAL -15 day), estadoIda);
									# Declaro el numero de la reserva a ingresar
									SET numeroReserva = LAST_INSERT_ID();
									# Ingreso la reserva en reserva_vuelo_clase
										INSERT INTO reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
										VALUES (numeroReserva, numeroIda, fechaIda,claseIda);
									UPDATE asientos_reservados as ar SET ar.cantidad = asientosReservadosIda + 1  WHERE ar.vuelo = numeroIda AND ar.fecha = fecha AND ar.clase = clase; 
									
								if asientosBrindadosVuelta < asientosReservadosVuelta then
									SET estadoVuelta = 'en Espera';
								else
									SET estadoVuelta = 'Confirmada';
								end if;									
									# Ingreso la reserva en reserva_vuelo_clase
										INSERT INTO reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
										VALUES (numeroReserva, numeroVuelta, fechaVuelta,claseVuelta);
									UPDATE asientos_reservados as ar SET ar.cantidad = asientosReservadosVuelta + 1  WHERE ar.vuelo = numeroVuelta AND ar.fecha = fecha AND ar.clase = clase; 
																										
									SELECT 'Las reservas se ingresaron correctamente' as Resultado;
										
							COMMIT;
							
						end if;						
					end if;
				end if;
			end if;
		end if;
	end if;
		
end;!  
delimiter ;


	
	
#--------------------------------------
#---------USUARIOS---------------------
#--------------------------------------

#-------------Creacion usuario admin
  
CREATE USER admin@localhost IDENTIFIED BY 'admin';
# usuario admin con pw admin

GRANT ALL PRIVILEGES ON vuelos.* TO admin@localhost WITH GRANT OPTION;
# usuario admin con todos los privilegios


#-------------Creacion usuario empleado

CREATE USER empleado@'%' IDENTIFIED BY 'empleado';
# usuario empleado con pw empleado

GRANT SELECT ON vuelos.* TO empleado@'%';

GRANT UPDATE, DELETE, INSERT ON vuelos.reservas TO empleado@'%';

GRANT UPDATE, DELETE, INSERT ON vuelos.pasajeros TO empleado@'%';

GRANT UPDATE, DELETE, INSERT ON vuelos.reserva_vuelo_clase TO empleado@'%';


#-------------Creacion usuario cliente

CREATE USER cliente@'%' IDENTIFIED BY 'cliente';
# usuario cliente con pw cliente

GRANT SELECT ON vuelos.vuelos_disponibles TO cliente@'%';
# usuario cliente con privilegios restringidos