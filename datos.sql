use vuelos;
#--------------------------------------
#---------INSERT-VALUES----------------
#--------------------------------------

INSERT INTO clases VALUES ('primera', 0.35);
INSERT INTO clases VALUES ('business', 0.25);
INSERT INTO clases VALUES ('turista', 0.10);

INSERT INTO comodidades VALUES (1, 'comodidad de tipo 1');
INSERT INTO comodidades VALUES (2, 'comodidad de tipo 2');
INSERT INTO comodidades VALUES (3, 'comodidad de tipo 3');

INSERT INTO pasajeros VALUES ('DNI', 39482498, 'Zucoli', 'Ezequiel', 'Paraguay 91', 2921495254, 'Argentina');
INSERT INTO pasajeros VALUES ('DNI', 39482491, 'Getz', 'Marilina', 'Dorrego 850', 2921495255, 'Brasil');
INSERT INTO pasajeros VALUES ('DNI', 39487858, 'Uslenghi', 'Nora', 'San Martin 423', 2921495256, 'Uruguay');
INSERT INTO pasajeros VALUES ('DNI', 39487859, 'Castano', 'Emilio', 'Belgrano 32', 2921495257, 'Paraguay');
INSERT INTO pasajeros VALUES ('DNI', 39487850, 'Martinez', 'Diego', 'Aguado 12', 2921495258, 'Bolivia');

INSERT INTO empleados VALUES (110863, md5(password), 'DNI', 39486495, 'Gomez', 'Pablo', 'Buggged 45', 2321546753);
INSERT INTO empleados VALUES (110889, md5(password), 'DNI', 25896347, 'Estebanez', 'Joaquin', 'Scrum 859', 2321242862);
INSERT INTO empleados VALUES (108492, md5(password), 'DNI', 26859635, 'Lopez', 'Rodrigo', 'Sprint 123', 2321522485);
INSERT INTO empleados VALUES (104128, md5(password), 'DNI', 28956764, 'Gallego', 'Hernan', 'Tesla 32', 2328545482);
INSERT INTO empleados VALUES (154226, md5(password), 'DNI', 40525645, 'Rosetti', 'Bruno', 'Darwin 54', 2921586395);
INSERT INTO empleados VALUES (151218, md5(password), 'DNI', 38444740, 'De Rossi', 'Daniele', 'Siempre Vivas 1562', 2932568471);

INSERT INTO modelos_avion VALUES ('C130', 'Mirage', 1, 200);
INSERT INTO modelos_avion VALUES ('M3', 'Raptor', 2, 150);
INSERT INTO modelos_avion VALUES ('C134', 'Mirege', 3, 340);

INSERT INTO ubicaciones VALUES ('Argentina', 'Buenos Aires', 'Bahia Blanca', -3);
INSERT INTO ubicaciones VALUES ('Brasil', 'San Pablo', 'Rio do Janeiro', -2);
INSERT INTO ubicaciones VALUES ('Japon', 'Tokio', 'Tokio', 6);

INSERT INTO aeropuertos VALUES ('AB123', 'Bahia Airport', 2921495254, 'Wine 6781', 'Argentina', 'Buenos Aires', 'Bahia Blanca');
INSERT INTO aeropuertos VALUES ('XM2', 'Rio Airport', 559863178, 'Macaco 12', 'Brasil', 'San Pablo', 'Rio do Janeiro');
INSERT INTO aeropuertos VALUES ('RT56', 'Tokio Airport', 1548693592, 'Konichiwa 32', 'Japon', 'Tokio', 'Tokio');

INSERT INTO vuelos_programados VALUES ('Vuelo1', 'AB123', 'XM2');
INSERT INTO vuelos_programados VALUES ('Vuelo2', 'XM2', 'AB123');
INSERT INTO vuelos_programados VALUES ('Vuelo3', 'AB123', 'RT56');
INSERT INTO vuelos_programados VALUES ('Vuelo4', 'XM2', 'RT56');
INSERT INTO vuelos_programados VALUES ('Vuelo5', 'RT56', 'AB123');

INSERT INTO salidas VALUES ('Vuelo3', 'Ju', '23:00:00', '22:00:00', 'C130');
INSERT INTO salidas VALUES ('Vuelo5', 'Ma', '01:00:00', '02:00:00', 'M3');
INSERT INTO salidas VALUES ('Vuelo2', 'Mi', '13:00:00', '20:00:00', 'C134');
INSERT INTO salidas VALUES ('Vuelo4', 'Do', '21:00:00', '14:00:00', 'C130');

INSERT INTO instancias_vuelo VALUES ('Vuelo3', 'Do', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'Disponible');
INSERT INTO instancias_vuelo VALUES ('Vuelo3', 'Ma', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'Disponible');
INSERT INTO instancias_vuelo VALUES ('Vuelo1', 'Sa', ADDDATE(CURDATE(), INTERVAL 15 DAY), 'Disponible');
INSERT INTO instancias_vuelo VALUES ('Vuelo4', 'Lu', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'Disponible');

INSERT INTO reservas VALUES (1, CURDATE(), ADDDATE(CURDATE(), INTERVAL 30 DAY), 'Disponible', 'DNI', 39486495, 110863);
INSERT INTO reservas VALUES (2, CURDATE(), ADDDATE(CURDATE(), INTERVAL 30 DAY), 'Disponible', 'DNI', 25896347, 110889);
INSERT INTO reservas VALUES (3, CURDATE(), ADDDATE(CURDATE(), INTERVAL 15 DAY), 'Disponible', 'DNI', 40525645, 154226);
INSERT INTO reservas VALUES (4, CURDATE(), ADDDATE(CURDATE(), INTERVAL 30 DAY), 'Disponible', 'DNI', 40525645, 154226);

INSERT INTO brinda VALUES ('Vuelo3', 'Do', 'primera', 3000.00, 50);
INSERT INTO brinda VALUES ('Vuelo3', 'Do', 'business', 1500.00, 35);
INSERT INTO brinda VALUES ('Vuelo3', 'Do', 'turista', 1700.00, 65);
INSERT INTO brinda VALUES ('Vuelo3', 'Ma', 'primera', 5450.00, 89);
INSERT INTO brinda VALUES ('Vuelo3', 'Ma', 'business', 2630.00, 101);
INSERT INTO brinda VALUES ('Vuelo3', 'Ma', 'turista', 1200.00, 23);
INSERT INTO brinda VALUES ('Vuelo4', 'Lu', 'primera', 7250.00, 67);
INSERT INTO brinda VALUES ('Vuelo4', 'Lu', 'business', 2400.00, 37);
INSERT INTO brinda VALUES ('Vuelo4', 'Lu', 'turista', 1100.00, 58);
INSERT INTO brinda VALUES ('Vuelo1', 'Sa', 'primera', 4250.00, 20);
INSERT INTO brinda VALUES ('Vuelo1', 'Sa', 'business', 2400.00, 79);
INSERT INTO brinda VALUES ('Vuelo1', 'Sa', 'turista', 1500.00, 83);

INSERT INTO posee VALUES ('primera', 2);
INSERT INTO posee VALUES ('business', 1);
INSERT INTO posee VALUES ('turista', 3);

INSERT INTO reserva_vuelo_clase VALUES (1, 'Vuelo3', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'turista');
INSERT INTO reserva_vuelo_clase VALUES (2, 'Vuelo1', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'primera');
INSERT INTO reserva_vuelo_clase VALUES (3, 'Vuelo4', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'business');
INSERT INTO reserva_vuelo_clase VALUES (3, 'Vuelo3', ADDDATE(CURDATE(), INTERVAL 30 DAY), 'turista');
