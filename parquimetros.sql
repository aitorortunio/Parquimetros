# Creo de la Base de Datos
DROP DATABASE parquimetros;
CREATE DATABASE parquimetros;
USE parquimetros;

#-------------------------------------------------------------------------

#Creacion Tablas para entidades

#----------------------------Tabla de conductores-----------------------------
CREATE TABLE Conductores (
  dni INT(10) UNSIGNED NOT NULL,
  nombre VARCHAR(45) NOT NULL,
  apellido VARCHAR(45) NOT NULL,
  direccion VARCHAR(45) NOT NULL,
  telefono VARCHAR(45),
  registro INT(10) UNSIGNED NOT NULL,

CONSTRAINT pk_Conductores
PRIMARY KEY (dni)
) ENGINE = InnoDB;

#----------------------Tabla de automoviles-----------------------------------
CREATE TABLE Automoviles(
  patente CHAR(6) NOT NULL,
  marca VARCHAR(45) NOT NULL,
  modelo VARCHAR(45) NOT NULL,
  color VARCHAR(45) NOT NULL,
  dni INT(10) UNSIGNED NOT NULL,

CONSTRAINT pk_Automoviles
PRIMARY KEY (patente),

CONSTRAINT fk_Automoviles_Conductores
FOREIGN KEY(dni) REFERENCES Conductores(dni)
) ENGINE = InnoDB;

#----------------------Tabla de Tipos Tarjetas-----------------------------------
CREATE TABLE Tipos_Tarjeta(
  tipo VARCHAR(45) NOT NULL,
  descuento DECIMAL(3,2) UNSIGNED NOT NULL,

CONSTRAINT chk_descuento
CHECK (descuento >=0 AND descuento <=1),

CONSTRAINT pk_Tipos_Tarjeta
PRIMARY KEY (tipo)
) ENGINE = InnoDB;

#----------------------Tabla de Tarjetas-----------------------------------
CREATE TABLE Tarjetas(
  id_tarjeta INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  saldo DECIMAL(5,2) NOT NULL,
  tipo VARCHAR(45) NOT NULL,
  patente VARCHAR(6) NOT NULL,

CONSTRAINT pk_Tarjetas
PRIMARY KEY(id_tarjeta),

CONSTRAINT fk_Tarjetas_Tipos_Tarjeta
FOREIGN KEY (tipo)
REFERENCES Tipos_Tarjeta(tipo),

CONSTRAINT fk_Tarjetas_Automoviles
FOREIGN KEY(patente)
REFERENCES Automoviles(patente)
)ENGINE = InnoDB;

#----------------------Tabla de Inspectores-----------------------------------
CREATE TABLE Inspectores(
  legajo INT(10) UNSIGNED NOT NULL,
  dni INT(10) UNSIGNED NOT NULL,
  nombre VARCHAR(45) NOT NULL,
  apellido VARCHAR(45) NOT NULL,
  PASSWORD CHAR(32) NOT NULL,

CONSTRAINT pk_Inspectores
PRIMARY KEY (legajo)
)ENGINE = InnoDB;

#----------------------Tabla de Ubicaciones-----------------------------------
CREATE TABLE Ubicaciones(
  calle VARCHAR(45) NOT NULL,
  altura INT(10) UNSIGNED NOT NULL,
  tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

CONSTRAINT pk_Ubicaciones
PRIMARY KEY (calle, altura)
)ENGINE = InnoDB;

#----------------------Tabla de Parquimetros-----------------------------------
CREATE TABLE Parquimetros(
  id_parq INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  numero INT(10) UNSIGNED NOT NULL,
  calle VARCHAR(45) NOT NULL,
  altura INT(10) UNSIGNED NOT NULL,

CONSTRAINT pk_Parquimetros
PRIMARY KEY (id_parq),

CONSTRAINT fk_Parquimetros_Ubicaciones
FOREIGN KEY (calle, altura)
REFERENCES Ubicaciones (calle, altura)
)ENGINE = InnoDB;

#----------------------Tabla de Estacionamientos-----------------------------------
CREATE TABLE Estacionamientos(
  id_tarjeta INT(10) UNSIGNED NOT NULL,
  id_parq INT(10) UNSIGNED NOT NULL,
  fecha_ent DATE NOT NULL,
  hora_ent TIME NOT NULL,
  fecha_sal DATE,
  hora_sal TIME,

CONSTRAINT pk_Estacionamientos
PRIMARY KEY (id_parq, fecha_ent, hora_ent),

CONSTRAINT fk_Estacionamientos_Tarjetas
FOREIGN KEY (id_tarjeta)
REFERENCES Tarjetas (id_Tarjeta)
ON DELETE RESTRICT ON UPDATE CASCADE,

CONSTRAINT fk_Estacionamientos_Parquimetros
FOREIGN KEY (id_parq)
REFERENCES Parquimetros (id_parq)
ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

#----------------------Tabla de Accede-----------------------------------
CREATE TABLE Accede(
  legajo INT(10) UNSIGNED NOT NULL,
  id_parq INT(10) UNSIGNED NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,

CONSTRAINT pk_Accede
PRIMARY KEY (id_parq, fecha, hora),

CONSTRAINT fk_Accede_Inspectores
FOREIGN KEY (legajo)
REFERENCES Inspectores (legajo)
ON DELETE RESTRICT ON UPDATE CASCADE,

CONSTRAINT fk_Accede_Parquimetros
FOREIGN KEY (id_parq)
REFERENCES Parquimetros (id_parq)
ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

#----------------------Tabla de Asociado_con-----------------------------------
CREATE TABLE Asociado_con(
  id_asociado_con INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  legajo INT(10) UNSIGNED NOT NULL,
  calle VARCHAR(45) NOT NULL,
  altura INT(10) UNSIGNED NOT NULL,
  dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa') NOT NULL,
  turno ENUM('M','T') NOT NULL,

CONSTRAINT pk_Asociado_con
PRIMARY KEY(id_asociado_con),

CONSTRAINT fk_Asociado_con_Inspectores
FOREIGN KEY (legajo)
REFERENCES Inspectores(legajo)
ON DELETE RESTRICT ON UPDATE CASCADE,

CONSTRAINT fk_Asociado_con_Ubicaciones
FOREIGN KEY (calle,altura)
REFERENCES Ubicaciones (calle,altura)
ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE = InnoDB;

#----------------------Tabla de Multa-----------------------------------
CREATE TABLE Multa(
  numero INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  patente VARCHAR(6) NOT NULL,
  id_asociado_con INT(10) UNSIGNED NOT NULL,

CONSTRAINT pk_Multa
PRIMARY KEY(numero),

CONSTRAINT fk_Multa_Automoviles
FOREIGN KEY (patente)
REFERENCES Automoviles (patente)
ON DELETE RESTRICT ON UPDATE CASCADE,

CONSTRAINT fk_Multa_Asociado_con
FOREIGN KEY (id_asociado_con)
REFERENCES Asociado_con (id_asociado_con)
ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

#----------------------Tabla de Ventas-----------------------------------
CREATE TABLE Ventas(
	id_tarjeta INT UNSIGNED NOT NULL,
	tipo_tarjeta VARCHAR (45) NOT NULL,
	saldo DECIMAL(5,2) NOT NULL,
	fecha DATE NOT NULL,
	hora TIME NOT NULL,

	CONSTRAINT pk_Ventas
	PRIMARY KEY (id_tarjeta,fecha,hora),

	CONSTRAINT fk_Ventas_tipoTarjeta
	FOREIGN KEY (tipo_tarjeta) REFERENCES Tipos_tarjeta(tipo)
	ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT fk_Ventas_idTarjeta
	FOREIGN KEY (id_tarjeta) REFERENCES Tarjetas(id_tarjeta)
	ON DELETE RESTRICT ON UPDATE CASCADE

)	ENGINE=InnoDB;

#----------------------------------------
#Creacion del trigger
delimiter !

CREATE TRIGGER bitacora_ventas
AFTER INSERT ON Tarjetas
FOR EACH ROW
BEGIN
 INSERT INTO Ventas VALUES (NEW.id_tarjeta,NEW.tipo,NEW.saldo,curdate(),curtime());
END; !
delimiter ;


#-------------------------------------------------------------------------

delimiter !

CREATE PROCEDURE conectar(IN idTarjeta INTEGER , IN idParq INTEGER)
	BEGIN
		#Declaro variables locales para recuperar los errores 
		DECLARE codigo_SQL CHAR(5) DEFAULT '00000';	 
		DECLARE codigo_MYSQL INT DEFAULT 0;
		DECLARE mensaje_error TEXT;
		DECLARE tiempoActual DATETIME DEFAULT LOCALTIME();
		DECLARE fechaEntrada DATE;
		DECLARE horaEntrada TIME;
		DECLARE FechaHoraEntrada DATETIME;
		DECLARE tarjeta_saldo DECIMAL(5,2);
		DECLARE tarjeta_tarifa DECIMAL(5,2);
		DECLARE tarjeta_descuento DECIMAL(3,2);
		DECLARE fechaEntradaDateTime DATETIME;

		# Variables para mostrar en la tabla de resultado
		DECLARE tarjeta_nuevo_saldo DECIMAL(5,2);
		DECLARE tipo_operacion VARCHAR(100);
		DECLARE resultado_operacion VARCHAR(100);
		DECLARE cantidadMinutos INT;

		DECLARE EXIT HANDLER FOR SQLEXCEPTION
			BEGIN
				GET DIAGNOSTICS CONDITION 1  codigo_MYSQL= MYSQL_ERRNO,  
					                         codigo_SQL= RETURNED_SQLSTATE, 
											 mensaje_error= MESSAGE_TEXT;
			    SELECT 'SQLEXCEPTION, la transaccion se aborto' AS tipo_operacion, 
				        codigo_MySQL, codigo_SQL,  mensaje_error;		
		        ROLLBACK;
			END;

		IF EXISTS (SELECT * FROM Tarjetas WHERE id_tarjeta = idTarjeta) AND EXISTS(SELECT * FROM Parquimetros WHERE id_parq = idParq)
		THEN
			START TRANSACTION;
			IF EXISTS (SELECT * FROM Estacionamientos WHERE id_tarjeta = idTarjeta AND fecha_sal IS NULL AND hora_sal IS NULL)
			THEN #Estoy en un estacionamiento abierto.
			BEGIN	
				SET tipo_operacion = 'Cierre de estacionamiento';
				SELECT id_parq,fecha_ent,hora_ent INTO idparq, fechaEntrada,horaEntrada FROM estacionamientos WHERE fecha_sal IS NULL AND hora_sal IS NULL AND id_tarjeta = idTarjeta;
				SET fechaEntradaDateTime = CAST(CONCAT(fechaEntrada, ' ', horaEntrada) AS DATETIME);
            SET cantidadMinutos = TIMESTAMPDIFF(MINUTE, fechaEntradaDateTime, NOW());	
            
				UPDATE Estacionamientos SET fecha_sal = curdate() , hora_sal = curtime() 
				 	WHERE id_tarjeta = idTarjeta AND fecha_sal IS NULL AND hora_sal IS NULL;
				 	
				SELECT saldo, descuento INTO tarjeta_saldo, tarjeta_descuento
					 FROM Tarjetas NATURAL JOIN Tipos_tarjeta WHERE id_tarjeta = idTarjeta;
				
				SELECT tarifa INTO tarjeta_tarifa FROM Ubicaciones NATURAL JOIN Parquimetros 
					 WHERE idparq = Parquimetros.id_parq;
					 
           IF ((tarjeta_saldo - (cantidadMinutos * tarjeta_tarifa * (1 - tarjeta_descuento)))< -999.99)
              THEN 
				  		SET tarjeta_nuevo_saldo = -999.99;
				   ELSE 
						SET tarjeta_nuevo_saldo = tarjeta_saldo - (cantidadMinutos * tarjeta_tarifa * (1 - tarjeta_descuento));
           END IF;
           
           
           UPDATE Tarjetas SET saldo = tarjeta_nuevo_saldo WHERE id_tarjeta = idTarjeta;
				SELECT tipo_operacion, cantidadMinutos AS Tiempo_Transcurrido, tarjeta_nuevo_saldo AS Saldo;
			END;
			ELSE #Estoy en un estacionamiento cerrado.
			BEGIN	
				SET tipo_operacion = 'Apertura de estacionamiento';
				SELECT saldo INTO tarjeta_saldo FROM Tarjetas WHERE id_tarjeta = idTarjeta;
				IF (tarjeta_saldo <= 0)
				THEN
				BEGIN
					SET resultado_operacion = 'Saldo insuficiente.';
					SELECT tipo_operacion, resultado_operacion as resultado;
				END;
				ELSE
				BEGIN
					SET resultado_operacion = 'Transaccion exitosa.';
					SELECT DISTINCT descuento INTO tarjeta_descuento FROM Tarjetas NATURAL JOIN Tipos_tarjeta WHERE id_tarjeta = idTarjeta;
					SELECT DISTINCT tarifa INTO tarjeta_tarifa FROM Parquimetros NATURAL JOIN Ubicaciones WHERE id_parq = idParq;
					SET cantidadMinutos = tarjeta_saldo / (tarjeta_tarifa* (1- tarjeta_descuento));
					INSERT INTO Estacionamientos VALUES (idTarjeta, idParq, curdate(), curtime(), NULL, NULL);
					SELECT tipo_operacion, resultado_operacion as resultado, cantidadMinutos as Cantidad_Total_Minutos;
				END;
				END IF;
			END;	 
		END IF;
	ELSE
		SELECT 'ID de tarjeta o ID de parquimetro erroneo.' 
			        AS tipo_operacion; 
	END IF;
	COMMIT;
END; !

 delimiter ; 

/*----------------------------
  Creacion de vistas
------------------------------*/

CREATE VIEW estacionados AS
  SELECT calle,
         altura,
         patente
	FROM Parquimetros
	NATURAL JOIN Estacionamientos
	NATURAL JOIN Tarjetas
	WHERE fecha_sal IS NULL
		   AND hora_sal IS NULL;

# --------------------------Creacion de usuarios y privilegios----------------------
DROP USER 'admin'@'localhost';
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;
#---------------------------Venta-------------------------------
DROP USER 'venta'@'%';
CREATE USER 'venta'@'%' IDENTIFIED BY 'venta';
GRANT INSERT ON parquimetros.Tarjetas TO 'venta'@'%';
GRANT SELECT ON parquimetros.Tipos_Tarjeta TO 'venta'@'%';
#---------------------------Inspector-------------------------------
DROP USER 'inspector'@'%';
CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';
GRANT SELECT ON parquimetros.estacionados TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Parquimetros TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Inspectores TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Multa TO 'inspector'@'%';
GRANT SELECT ON parquimetros.Accede TO 'inspector'@'%';
GRANT SELECT ON parquimetros.asociado_con TO 'inspector'@'%';
GRANT INSERT ON parquimetros.Multa TO 'inspector'@'%';
GRANT INSERT ON parquimetros.Accede TO 'inspector'@'%';
GRANT SELECT ON parquimetros.automoviles TO 'inspector'@'%';
#------------------------parquimetro-------------------------------
DROP USER 'parquimetro'@'%';
CREATE USER 'parquimetro'@'%' IDENTIFIED BY 'parq';   
GRANT EXECUTE ON PROCEDURE parquimetros.conectar TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.Parquimetros TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.Tarjetas TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.Ubicaciones TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.Tipos_tarjeta TO 'parquimetro'@'%';

