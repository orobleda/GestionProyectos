
/* Drop Tables */

DROP TABLE [CONCEPTO];
DROP TABLE [Coste];
DROP TABLE [TRANS_ESTADOS];
DROP TABLE [ESTADO_PROYECTO];
DROP TABLE [ESTIMACIONESHORAS];
DROP TABLE [FESTIVO];
DROP TABLE [IMPUTACIONES];
DROP TABLE [REL_RECURSO_AREA];
DROP TABLE [TOPE_IMPUTACION];
DROP TABLE [META_AREA];
DROP TABLE [REL_BLOQUE_CONCEPTO];
DROP TABLE [META_BLOQUE];
DROP TABLE [REL_CONCEPTO_FORMATO];
DROP TABLE [META_CONCEPTO];
DROP TABLE [META_FORMATO_PROYECTO];
DROP TABLE [META_JORNADA];
DROP TABLE [PARM_PROYECTO];
DROP TABLE [META_PARM_PROY];
DROP TABLE [REL_TARIFA_RECURSO];
DROP TABLE [PARM_RECURSO];
DROP TABLE [META_PARM_REC];
DROP TABLE [PRESUPUESTO];
DROP TABLE [META_TIPO_PRESUPUESTO];
DROP TABLE [TARIFA];
DROP TABLE [PROVEEDOR];
DROP TABLE [PROYECTO];
DROP TABLE [VACACIONES];
DROP TABLE [RECURSO];
DROP TABLE [TIPO_PARM_PROY];
DROP TABLE [TIPO_PROYECTO];




/* Create Tables */

CREATE TABLE [META_AREA]
(
	[id] integer NOT NULL UNIQUE,
	[codigo] text NOT NULL,
	[responsable] integer,
	[descripcion] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [META_TIPO_PRESUPUESTO]
(
	[id] integer NOT NULL,
	[descripcion] text,
	[codigo] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [PROYECTO]
(
	[id] integer NOT NULL,
	[Nombre] text NOT NULL,
	PRIMARY KEY ([id])
);


CREATE TABLE [PRESUPUESTO]
(
	[id] integer NOT NULL UNIQUE,
	[version] integer NOT NULL,
	[fxAlta] text,
	[idProyecto] integer NOT NULL,
	[idTipoPresupuesto] integer NOT NULL,
	[descripcion] text,
	FOREIGN KEY ([idTipoPresupuesto])
	REFERENCES [META_TIPO_PRESUPUESTO] ([id]),
	FOREIGN KEY ([idProyecto])
	REFERENCES [PROYECTO] ([id])
);


CREATE TABLE [Coste]
(
	[id] integer NOT NULL,
	[descripcion] text NOT NULL,
	[version] integer NOT NULL,
	[idArea] integer NOT NULL,
	[idPresupuesto] integer NOT NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([idArea])
	REFERENCES [META_AREA] ([id]),
	FOREIGN KEY ([idPresupuesto])
	REFERENCES [PRESUPUESTO] ([id])
);


CREATE TABLE [META_CONCEPTO]
(
	[id] integer NOT NULL,
	[codigo] text NOT NULL,
	[descripcion] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [CONCEPTO]
(
	[id] integer NOT NULL UNIQUE,
	[valor] real,
	[valorEstimado] real,
	[horas] real,
	[horasEstimado] real,
	[porcentaje] integer,
	[esPorcentaje] integer,
	[respectoPorcentaje] integer,
	[idCoste] integer NOT NULL,
	[tipoConcepto] integer NOT NULL,
	[idTarifa] integer,
	[baseCalculo] integer,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([idCoste])
	REFERENCES [Coste] ([id]),
	FOREIGN KEY ([tipoConcepto])
	REFERENCES [META_CONCEPTO] ([id])
);


CREATE TABLE [ESTADO_PROYECTO]
(
	[id] integer NOT NULL UNIQUE,
	[codigo] integer NOT NULL,
	[descripcion] text NOT NULL,
	PRIMARY KEY ([id])
);


CREATE TABLE [META_BLOQUE]
(
	[id] integer NOT NULL UNIQUE,
	[grupo] integer NOT NULL,
	[codigo] text NOT NULL,
	[descripcion] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [RECURSO]
(
	[id] integer NOT NULL,
	[Nombre] text NOT NULL,
	PRIMARY KEY ([id])
);


CREATE TABLE [ESTIMACIONESHORAS]
(
	[id] integer NOT NULL PRIMARY KEY AUTOINCREMENT,
	[recurso] integer NOT NULL,
	[proyecto] integer NOT NULL,
	[sistema] integer NOT NULL UNIQUE,
	[gerencia] integer NOT NULL UNIQUE,
	[natCoste] integer NOT NULL,
	[fxInicio] text,
	[tsInicio] real,
	[fxFin] text,
	[tsFin] real,
	[horas] real,
	[importe] real,
	FOREIGN KEY ([sistema])
	REFERENCES [META_AREA] ([id]),
	FOREIGN KEY ([gerencia])
	REFERENCES [META_BLOQUE] ([id]),
	FOREIGN KEY ([natCoste])
	REFERENCES [META_CONCEPTO] ([id]),
	FOREIGN KEY ([proyecto])
	REFERENCES [PROYECTO] ([id]),
	FOREIGN KEY ([recurso])
	REFERENCES [RECURSO] ([id])
);


CREATE TABLE [FESTIVO]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[fxFestivo] text
);


CREATE TABLE [IMPUTACIONES]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[recurso] integer NOT NULL,
	[proyecto] integer NOT NULL,
	[gerencia] integer NOT NULL UNIQUE,
	[sistema] integer NOT NULL UNIQUE,
	[natCoste] integer NOT NULL,
	[pedido] text,
	[fxInicio] text,
	[tsInicio] real,
	[fxFin] text,
	[tsFin] real,
	[horas] real,
	[importe] real,
	[tarifa] real,
	[OT] text,
	[Estado] integer,
	[fxEnvioSAP] text,
	[tsEnvioSAP] real,
	FOREIGN KEY ([sistema])
	REFERENCES [META_AREA] ([id]),
	FOREIGN KEY ([gerencia])
	REFERENCES [META_BLOQUE] ([id]),
	FOREIGN KEY ([natCoste])
	REFERENCES [META_CONCEPTO] ([id]),
	FOREIGN KEY ([proyecto])
	REFERENCES [PROYECTO] ([id]),
	FOREIGN KEY ([recurso])
	REFERENCES [RECURSO] ([id])
);


CREATE TABLE [META_FORMATO_PROYECTO]
(
	[id] integer NOT NULL UNIQUE,
	[codigo] text,
	[descripcion] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [META_JORNADA]
(
	[id] integer NOT NULL,
	[codJornada] integer NOT NULL,
	[diaSemana] integer,
	[horas] real,
	[horasVerano] real,
	[inicioVerano] text,
	[finVerano] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [TIPO_PARM_PROY]
(
	[id] integer NOT NULL UNIQUE,
	[descripcion] text NOT NULL,
	[selector] text,
	PRIMARY KEY ([id])
);


CREATE TABLE [META_PARM_PROY]
(
	[id] integer NOT NULL UNIQUE,
	[Descripcion] text NOT NULL,
	[tipo] text,
	[tipoDato] integer NOT NULL UNIQUE,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([tipoDato])
	REFERENCES [TIPO_PARM_PROY] ([id])
);


CREATE TABLE [META_PARM_REC]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[Descripcion] text,
	[tipo] text,
	[tipoDato] integer
);


CREATE TABLE [PARM_PROYECTO]
(
	[id] integer NOT NULL,
	[idProyecto] integer NOT NULL,
	[cod_parm] integer NOT NULL,
	[valorTexto] text,
	[valorEntero] integer,
	[valorReal] real,
	[valorFecha] text,
	[fecFinVig] text,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([cod_parm])
	REFERENCES [META_PARM_PROY] ([id]),
	FOREIGN KEY ([idProyecto])
	REFERENCES [PROYECTO] ([id])
);


CREATE TABLE [PARM_RECURSO]
(
	[id] integer NOT NULL,
	[idRecurso] integer NOT NULL,
	[valorTexto] text,
	[valorEntero] integer,
	[valorReal] real,
	[valorFecha] text,
	[fecFinVig] text,
	[cod_parm] integer NOT NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([cod_parm])
	REFERENCES [META_PARM_REC] ([id]),
	FOREIGN KEY ([idRecurso])
	REFERENCES [RECURSO] ([id])
);


CREATE TABLE [PROVEEDOR]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[descripcion] text,
	[nombreCorto] text
);


CREATE TABLE [REL_BLOQUE_CONCEPTO]
(
	[id] integer NOT NULL UNIQUE,
	[idBloque] integer NOT NULL,
	[codRel] text NOT NULL,
	[idConcepto] integer NOT NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([idBloque])
	REFERENCES [META_BLOQUE] ([id]),
	FOREIGN KEY ([idConcepto])
	REFERENCES [META_CONCEPTO] ([id])
);


CREATE TABLE [REL_CONCEPTO_FORMATO]
(
	[id] integer NOT NULL,
	[idConcepto] integer NOT NULL,
	[idFormato] integer NOT NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([idConcepto])
	REFERENCES [META_CONCEPTO] ([id]),
	FOREIGN KEY ([idFormato])
	REFERENCES [META_FORMATO_PROYECTO] ([id])
);


CREATE TABLE [REL_RECURSO_AREA]
(
	[id] integer NOT NULL,
	[idRecurso] integer NOT NULL,
	[idArea] integer NOT NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([idArea])
	REFERENCES [META_AREA] ([id]),
	FOREIGN KEY ([idRecurso])
	REFERENCES [RECURSO] ([id])
);


CREATE TABLE [TARIFA]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[desarrollo] integer,
	[mantenimiento] integer,
	[idProveedor] integer NOT NULL,
	[valorHora] real,
	[fInicioVigencia] text,
	[fFinVigencia] text,
	[tsIniVigencia] real,
	[tsFinVigencia] real,
	FOREIGN KEY ([idProveedor])
	REFERENCES [PROVEEDOR] ([id])
);


CREATE TABLE [REL_TARIFA_RECURSO]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[idRecurso] integer NOT NULL,
	[idTarifa] integer NOT NULL,
	[fInicioVig] text,
	[fFInVig] text,
	[tsInicioVig] real,
	[tsFinVig] real,
	FOREIGN KEY ([idRecurso])
	REFERENCES [PARM_RECURSO] ([id]),
	FOREIGN KEY ([idTarifa])
	REFERENCES [TARIFA] ([id])
);


CREATE TABLE [TIPO_PROYECTO]
(
	[id] integer NOT NULL UNIQUE,
	[codigo] integer NOT NULL,
	[descripcion] text NOT NULL,
	PRIMARY KEY ([id])
);


CREATE TABLE [TOPE_IMPUTACION]
(
	[id] integer NOT NULL PRIMARY KEY AUTOINCREMENT,
	[idProyecto] integer NOT NULL,
	[idConcepto] integer,
	[anio] integer,
	[cantidad] real,
	[porcentaje] real,
	[resto] integer,
	[version] integer,
	[dsVersion] text,
	[idSistema] integer NOT NULL UNIQUE,
	FOREIGN KEY ([idSistema])
	REFERENCES [META_AREA] ([id]),
	FOREIGN KEY ([idConcepto])
	REFERENCES [META_CONCEPTO] ([id]),
	FOREIGN KEY ([idProyecto])
	REFERENCES [PROYECTO] ([id])
);


CREATE TABLE [TRANS_ESTADOS]
(
	[ID] integer NOT NULL UNIQUE,
	[tipoProyecto] integer NOT NULL UNIQUE,
	[estadoInicial] integer NOT NULL UNIQUE,
	[estadoFinal] integer NOT NULL UNIQUE,
	PRIMARY KEY ([ID]),
	FOREIGN KEY ([estadoFinal])
	REFERENCES [ESTADO_PROYECTO] ([id]),
	FOREIGN KEY ([estadoInicial])
	REFERENCES [ESTADO_PROYECTO] ([id]),
	FOREIGN KEY ([tipoProyecto])
	REFERENCES [TIPO_PROYECTO] ([id])
);


CREATE TABLE [VACACIONES]
(
	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
	[ausencia] integer NOT NULL,
	[horas] real,
	[fxDia] text NOT NULL,
	[tsDia] real NOT NULL,
	[idRecurso] integer NOT NULL,
	FOREIGN KEY ([idRecurso])
	REFERENCES [RECURSO] ([id])
);



