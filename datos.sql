#---------------------------Conductores-------------------------------
INSERT INTO Conductores VALUES(41168790,'Aitor', 'Ortuño Rossetto', 'Belgrano 1500','2915327204',1134567);
INSERT INTO Conductores VALUES(11987854,'Pedro', 'Rodriguez', 'Peru 700','292234504',8765431);
INSERT INTO Conductores VALUES(4567890,'Tomas', 'Gutierrez', 'Aguado 1500','2917809',1134678);
INSERT INTO Conductores VALUES(423451,'Agustina', 'Salvatori', 'Chiclana 1200','291567542',9014567);
INSERT INTO Conductores VALUES(404567,'Martin', 'Perez', 'Alem 1500','29235577',8765124);
/*  ----------------------Automoviles-------------------------------------------------------*/
/*Automoviles(patente, marca, modelo, color, dni)*/
INSERT INTO Automoviles VALUES('RTY135','Ford','Kuga','Rojo',41168790);
INSERT INTO Automoviles VALUES('TJK745','Ford','Fiesta','Negro',11987854);
INSERT INTO Automoviles VALUES('PSU123','Chevrolet','Prisma','Azul',4567890);
INSERT INTO Automoviles VALUES('YTJ731','Jeep','Gladiator','Blanco',423451);
INSERT INTO Automoviles VALUES('KPI986','Audi','A3','Negro',404567);
/*  ----------------------Tipos Tarjetas-------------------------------------------------------*/
/*Tipos Tarjetas(tipo, descuento)*/
INSERT INTO Tipos_Tarjeta VALUES('tipo1',0.17);
INSERT INTO Tipos_Tarjeta VALUES('tipo2',0.25);
INSERT INTO Tipos_Tarjeta VALUES('tipo3',0.55);
INSERT INTO Tipos_Tarjeta VALUES('tipo4',0.33);
INSERT INTO Tipos_Tarjeta VALUES('tipo5',0.45);
/*  ----------------------Tarjetas-------------------------------------------------------*/
/*Tarjetas(saldo, tipo, patente)*/
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES(200,'tipo1','PSU123');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES(100,'tipo2','YTJ731');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES(300,'tipo3','KPI986');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES(400,'tipo4','TJK745');
INSERT INTO Tarjetas(saldo, tipo, patente) VALUES(500,'tipo5','RTY135');
/*  ----------------------Inspectores-------------------------------------------------------*/
/*Inspectores(legajo, dni, nombre, apellido, password)*/
INSERT INTO Inspectores VALUES(10,472891,'Claudio','Dominguez',MD5('Inspector1'));
INSERT INTO Inspectores VALUES(11,124656,'Facundo','Gimenez',MD5('Inspector2'));
INSERT INTO Inspectores VALUES(12,786511,'Rocio','Cazenave',MD5('Inspector3'));
INSERT INTO Inspectores VALUES(13,412577,'Hernan','Hernandez',MD5('Inspector4'));
INSERT INTO Inspectores VALUES(14,389087,'Giuliana','Disalvo',MD5('Inspector5'));
/*  ----------------------Ubicaciones-------------------------------------------------------*/
/*Ubicaciones(calle, altura, tarifa)*/
INSERT INTO Ubicaciones VALUES('Alem',1200,'1.10');
INSERT INTO Ubicaciones VALUES('Belgrano',200,'0.90');
INSERT INTO Ubicaciones VALUES('Lamadrid',1300,'0.80');
INSERT INTO Ubicaciones VALUES('Aguado',800,'0.75');
INSERT INTO Ubicaciones VALUES('Eduardo Gonzalez',500,'0.95');
/*  ----------------------Parquimetros-------------------------------------------------------*/
/*Parquimetros(numero, calle, altura)*/
INSERT INTO Parquimetros(numero, calle, altura) VALUES(15,'Belgrano','200');
INSERT INTO Parquimetros(numero, calle, altura) VALUES(20,'Alem','1200');
INSERT INTO Parquimetros(numero, calle, altura) VALUES(27,'Aguado','800');
INSERT INTO Parquimetros(numero, calle, altura) VALUES(33,'Eduardo Gonzalez','500');
INSERT INTO Parquimetros(numero, calle, altura) VALUES(45,'Lamadrid','1300');
/*  ----------------------Estacionamientos-------------------------------------------------------*/
/*Estacionamientos (id Tarjetas, fecha ent, hora ent,fecha sal, hora sal)*/
INSERT INTO Estacionamientos VALUES(1,1,'2019/02/04','09:25:20','2019/02/04','13:12:50');
INSERT INTO Estacionamientos VALUES(2,2,'2020/11/27','20:13:05',NULL,NULL);
INSERT INTO Estacionamientos VALUES(3,3,'2020/11/27','17:35:14',NULL,NULL);
INSERT INTO Estacionamientos VALUES(4,4,'2020/11/27','21:13:19',NULL,NULL);
INSERT INTO Estacionamientos VALUES(5,5,'2019/12/31','10:32:37','2019/12/31','16:48:36');
/*  ----------------------Accede-------------------------------------------------------*/
/*Accede(legajo, fecha, hora)*/
INSERT INTO Accede VALUES(10,1,'2019/02/04','09:25:20');
INSERT INTO Accede VALUES(11,2,'2018/04/01','14:35:14');
INSERT INTO Accede VALUES(12,3,'2020/05/03','11:13:05');
INSERT INTO Accede VALUES(13,4,'2019/12/31','10:32:37');
INSERT INTO Accede VALUES(14,5,'2020/07/31','08:27:19');
/*  ----------------------Asociado_con-------------------------------------------------------*/
/*Asociado con(id asociado con, legajo, calle, altura, dia, turno)*/
INSERT INTO Asociado_con VALUES(1011,11,'Aguado',800,'Ma','T');
INSERT INTO Asociado_con VALUES(1014,14,'Lamadrid',1300,'Mi','T');
INSERT INTO Asociado_con VALUES(1012,12,'Eduardo Gonzalez',500,'Sa','M');
INSERT INTO Asociado_con VALUES(1013,13,'Belgrano',200,'Do','T');
INSERT INTO Asociado_con VALUES(1010,10,'Alem',1200,'Lu','M');
/*  ----------------------Multa-------------------------------------------------------*/
/*Multa(fecha, hora, patente, id asociado con)*/
INSERT INTO Multa(fecha, hora, patente, id_asociado_con) VALUES('2020/05/03','11:13:05','PSU123',1011);
INSERT INTO Multa(fecha, hora, patente, id_asociado_con) VALUES('2019/12/31','10:32:37','RTY135',1014);
INSERT INTO Multa(fecha, hora, patente, id_asociado_con) VALUES('2020/07/31','08:27:19','TJK745',1012);
INSERT INTO Multa(fecha, hora, patente, id_asociado_con) VALUES('2018/04/01','14:35:14','KPI986',1013);
INSERT INTO Multa(fecha, hora, patente, id_asociado_con) VALUES('2019/02/04','09:25:20','YTJ731',1010);
