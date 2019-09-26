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
	porcentaje DECIMAL (2,2) NOT NULL,
  
	CONSTRAINT pk_clases 
	PRIMARY KEY (nombre),
	
	CONSTRAINT chk_clases_porcentaje
	CHECK (porcentaje BETWEEN 0.00 AND 0.99)
) ENGINE=InnoDB;

CREATE TABLE comodidades (
	codigo SMALLINT UNSIGNED NOT NULL,
	descripcion VARCHAR(500) NOT NULL,

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
	hora_sale TIME,
	hora_llega TIME,
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
	precio DECIMAL (8,2) NOT NULL,
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


#--------------------------------------
#---------VISTA------------------------
#--------------------------------------

CREATE VIEW vuelos_disponibles AS
	SELECT DISTINCT vp.numero as NroVuelo,
		iv.fecha as Fecha,
		s.modelo_avion as ModeloAvion,
		s.dia as Dia,
		s.hora_sale as Salida,
		s.hora_llega as Llegada,
		CAST(IF(s.hora_llega < s.hora_sale, (CAST('24:00:00' AS TIME) - s.hora_sale + s.hora_llega), s.hora_llega - s.hora_sale) AS TIME) AS Duracion_Estimado,
		aeroS.codigo as As_codigo,
		aeroS.nombre as As_nombre,
		aeroS.pais as As_pais,
		aeroS.ciudad as As_ciudad,
		aeroL.codigo as Al_codigo,
		aeroL.nombre as Al_nombre,
		aeroL.pais as Al_pais,
		aeroL.ciudad as Al_ciudad,
		b.clase as NombreClase,
		b.precio as Precio, 
		b.cant_asientos + ROUND(b.cant_asientos * porcentaje) - (SELECT count(*) FROM reserva_vuelo_clase rvc WHERE b.vuelo = rvc.vuelo AND rvc.fecha_vuelo = iv.fecha AND rvc.clase = b.clase) AS Asientos_Disponibles 
		
	FROM (((((((vuelos_programados AS vp JOIN aeropuertos AS aeroS ON vp.aeropuerto_salida = aeroS.codigo) 
		JOIN aeropuertos AS aeroL ON vp.aeropuerto_llegada = aeroL.codigo) 
		LEFT JOIN instancias_vuelo AS iv ON vp.numero = iv.vuelo) 
		JOIN salidas AS s ON vp.numero = s.vuelo) 
		JOIN brinda AS b ON vp.numero = b.vuelo AND s.dia = b.dia) 
		JOIN clases AS c ON b.clase = c.nombre) 
		JOIN reserva_vuelo_clase AS rvc ON vp.numero = rvc.vuelo AND iv.fecha = rvc.fecha_vuelo) 
		
WHERE iv.fecha > NOW() AND (b.cant_asientos + ROUND(b.cant_asientos * porcentaje) - (SELECT count(*) FROM reserva_vuelo_clase rvc WHERE b.vuelo = rvc.vuelo AND rvc.fecha_vuelo = iv.fecha)) > 0; 
	
	
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