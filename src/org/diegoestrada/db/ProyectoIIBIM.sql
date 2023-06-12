/*Diego René Estrada Juárez
2019308
IN5AM
-Fecha de creación:
		28/03/2023
-Fechas de modificaciones:
		--/--/----
*/

Drop database if exists DBTonysKinal2023;

Create database DBTonysKinal2023;
Use DBTonysKinal2023;

-- -------------------------------- DDL ------------------------------------
Create table Empresas(
	codigoEmpresa int auto_increment not null,
    nombreEmpresa varchar(150) not null,
    direccion varchar(150) not null,
    telefono varchar(8),
    primary key PK_codigoEmpresa(codigoEmpresa)
);

Create table TipoEmpleado(
	codigoTipoEmpleado int not null auto_increment,
    descripcion varchar(50) not null,
	primary key PK_codigoTipoEmpleado(codigoTipoEmpleado)
);

Create table TipoPlato(
	codigoTipoPlato int not null auto_increment,
    descripcionTipo varchar(100) not null,
	primary key PK_codigoTipoPlato(codigoTipoPlato)
);

Create table Productos(
	codigoProducto int not null auto_increment, 
    nombreProducto varchar(150) not null,
    cantidad int not null,
	primary key PK_codigoProducto(codigoProducto)
);

Create table Empleados(
	codigoEmpleado int not null auto_increment, 
    numeroEmpleado int not null, 
    apellidosEmpleado varchar(150) not null,
    nombresEmpleado varchar(150) not null,
    direccionEmpleado varchar(150) not null, 
    telefonoContacto varchar(8) not null,
    gradoCocinero varchar(50),
    codigoTipoEmpleado int not null,
    primary key PK_codigoEmpleado(codigoEmpleado),
	constraint FK_Empleados_TipoEmpleado foreign key(codigoTipoEmpleado) 
		references TipoEmpleado(codigoTipoEmpleado)
);

Create table Servicios(
	codigoServicio int not null auto_increment, 
    fechaServicio date not null,
	tipoServicio varchar(150) not null, 
	horaServicio time not null,
	lugarServicio varchar(150) not null,
    telefonoContacto varchar(150) not null,
    codigoEmpresa int not null,
    primary key PK_codigoServicio(codigoServicio),
    constraint FK_Servicios_Empresas foreign key(codigoEmpresa) 
		references Empresas(codigoEmpresa)
);

Create table Presupuestos(
	codigoPresupuesto int not null auto_increment, 
	fechaSolicitud date not null,
	cantidadPresupuesto decimal(10,2) not null,
    codigoEmpresa int not null,
	primary key PK_codigoPresupuesto(codigoPresupuesto),
    constraint FK_Presupuestos_Empresas foreign key (codigoEmpresa)
		references Empresas(codigoEmpresa)
);

Create table Platos(
	codigoPlato int not null auto_increment, 
    cantidad int not null,
	nombrePlato varchar(50) not null, 
    descripcionPlato varchar(150) not null,
	precioPlato decimal(10,2) not null,
    codigoTipoPlato int not null,
	primary key PK_codigoPlato(codigoPlato),
	constraint FK_Platos_TipoPlato foreign key(codigoTipoPlato)
		references TipoPlato(codigoTipoPlato)
);

Create table Productos_has_Platos(
	Productos_codigoProducto int not null,
    codigoPlato int not null,
	codigoProducto int not null,
    primary key PK_Productos_codigoProducto(Productos_codigoProducto),
    constraint FK_Productos_has_Platos_Productos foreign key (codigoProducto)
		references Productos(codigoProducto),
	constraint FK_Productos_has_Platos_Platos foreign key (codigoPlato)
		references Platos(codigoPlato)
);

Create table Servicios_has_Platos(
	Servicios_codigoServicio int not null,
	codigoPlato int not null,
    codigoServicio int not null,
	primary key PK_Servicios_codigoServicio(Servicios_codigoServicio),
	constraint FK_Servicios_has_Platos_Servicios foreign key (codigoServicio)
		references Servicios(codigoServicio),
    constraint FK_Servicios_has_Platos_Platos foreign key (codigoPlato)
		references Platos(codigoPlato)
);

Create table Servicios_has_Empleados(
	Servicios_codigoServicio int not null,
    codigoServicio int not null,
	codigoEmpleado int not null,
	fechaEvento date not null,
	horaEvento time not null,
	lugarEvento varchar(150) not null,
    primary key PK_Servicios_codigoServicio(Servicios_codigoServicio),
    constraint FK_Servicios_has_Empleados_Servicios foreign key (codigoServicio)
		references Servicios(codigoServicio),
    constraint FK_Servicios_has_Empleados_Empleados foreign key (codigoEmpleado)
		references Empleados(codigoEmpleado)
);

Create table Usuario(
	codigoUsuario int not null auto_increment,
    nombreUsuario varchar(100) not null,
    apellidoUsuario varchar(100) not null,
    usuarioLogin varchar(50) not null,
    contrasena varchar(50) not null,
    primary key PK_codigoUsuario(codigoUsuario)
);

Delimiter //
	Create procedure sp_AgregarUsuario(in nombreUsuario varchar(100), in apellidoUsuario varchar(100),
		in usuarioLogin varchar(100), in contrasena varchar(50))
	Begin 
		Insert into Usuario(nombreUsuario, apellidoUsuario, usuarioLogin, contrasena)
			values(nombreUsuario, apellidoUsuario, usuarioLogin, contrasena);
    End//
Delimiter ;

Delimiter //
	Create procedure sp_ListarUsuarios()
		Begin
			Select
				U.codigoUsuario,
				U.nombreUsuario, 
				U.apellidoUsuario, 
				U.usuarioLogin, 
				U.contrasena
			from Usuario U;
        End//
Delimiter ;

call sp_AgregarUsuario('Diego','Estrada','destrada','1234');
call sp_AgregarUsuario('Pedro','Armas','parmas','parmas');

call sp_ListarUsuarios();

Create table Login(
	usuarioMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster(usuarioMaster)

);

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------

-- -------------------------- Procedimientos almacenados -------------------------------

-- -------------------------- Agregar Empresa ----------------------------------------
Delimiter //
	Create procedure sp_AgregarEmpresa(in nombreEmpresa varchar(150),in direccion varchar(150),
		in telefono varchar(8))
			Begin
				Insert into Empresas(nombreEmpresa ,direccion,telefono)
					values (nombreEmpresa ,direccion,telefono);
			End//
Delimiter ;

-- ------------------- ListarEmpresas
Delimiter //
	Create procedure sp_ListarEmpresas()
		Begin
			Select 
				E.codigoEmpresa,
				E.nombreEmpresa,
				E.direccion,
				E.telefono
				from Empresas E;
		End//
Delimiter ;

-- ------------------ Buscar Empresa
Delimiter //
	Create procedure sp_BuscarEmpresa(in codEmpresa int)
		Begin
			Select 
			E.codigoEmpresa,
			E.nombreEmpresa,
            E.direccion,
            E.telefono
            from Empresas E
				where codigoEmpresa = codEmpresa; 
		End//
Delimiter ; 

-- -------------------- Eliminar Empresa
Delimiter //
	Create procedure sp_EliminarEmpresa(in codEmpresa int)
		Begin
			Delete from Empresas
				where codigoEmpresa = codEmpresa;
        End//
Delimiter ;

-- -------------------- Editar Empresa
Delimiter //
	Create procedure sp_EditarEmpresa(in codEmpresa int,
		in nomEmpresa varchar(150),in direc varchar(150),
		in tel varchar(8))
		Begin
			Update Empresas E
				set E.nombreEmpresa = nomEmpresa,
					E.direccion = direc,
                    E.telefono = tel
						where E.codigoEmpresa = codEmpresa;
        End//
Delimiter ;

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------
-- ---------------------- Agregar TipoEmpleado
Delimiter //
	Create procedure sp_AgregarTipoEmpleado(in descripcion varchar(50))
		Begin
			Insert into TipoEmpleado(descripcion)
				values(descripcion);
        End//
Delimiter ;

-- -----------------------Listar TipoEmpleados
Delimiter //
	Create procedure sp_ListarTipoEmpleados()
		Begin
			Select 
				TE.codigoTipoEmpleado,
                TE.descripcion
				from TipoEmpleado TE;
        End//
Delimiter ;

-- ---------------------- Buscar TipoEmpleado
Delimiter //
	Create procedure sp_BuscarTipoEmpleado(in codTipoEmpleado int)
		Begin
			Select 
				TE.codigoTipoEmpleado,
                TE.descripcion
				from TipoEmpleado TE
					where codigoTipoEmpleado = codTipoEmpleado;
        End//
Delimiter ;

-- ---------------------- Eliminar TipoEmpleado
Delimiter //
	Create procedure sp_EliminarTipoEmpleado(in codTipoEmpleado int)
		Begin
			Delete from TipoEmpleado
				where codigoTipoEmpleado = codTipoEmpleado;
        End//
Delimiter ;

-- ---------------------- Editar TipoEmpleado 
Delimiter //
	Create procedure sp_EditarTipoEmpleado(in codTipoEmpleado int,in descri varchar(50))
		Begin
			Update TipoEmpleado TE
				set TE.descripcion = descri
					where TE.codigoTipoEmpleado = codTipoEmpleado;
        End//
Delimiter ;


-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------

-- -------------------------- Agregar TipoPlato 
Delimiter //
	Create procedure sp_AgregarTipoPlato(in descripcionTipo varchar(100))
		Begin
			Insert into TipoPlato(descripcionTipo)
				values(descripcionTipo);
		End//
Delimiter ; 

-- -------------------------- Listar TipoPlatos
Delimiter //
	Create procedure sp_ListarTipoPlatos()
		Begin
			Select 
				TP.codigoTipoPlato,
                TP.descripcionTipo
                from TipoPlato TP;
        End //
Delimiter ;

call sp_ListarTipoPlatos();

-- ------------------------ Buscar TipoPlato 
Delimiter //
	Create procedure sp_BuscarTipoPlato(in codTipoPlato int)
		Begin
			Select 
				TP.codigoTipoPlato,
                TP.descripcionTipo
                from TipoPlato TP
					where codigoTipoPlato = codTipoPlato;
        End//
Delimiter ; 

-- ------------------------ Eliminar TipoPlato 
Delimiter //
	Create procedure sp_EliminarTipoPlato(in codTipoPlato int)
		Begin
			Delete from TipoPlato
				where codigoTipoPlato = codTipoPlato;
		End//
Delimiter ;

-- ------------------------ Editar TipoPlato
Delimiter //
	Create procedure sp_EditarTipoPlato(in codTipoPlato int, in descTipo varchar(100))
		Begin
			Update TipoPlato TP
                set TP.descripcionTipo = descTipo
					where codigoTipoPlato = codTipoPlato;
		End//
Delimiter ;	

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------

-- ---------------------------------- Agregar Producto --------------
Delimiter //
	Create procedure sp_AgregarProducto(in nombreProducto varchar(150), in cantidad int)
		Begin
			Insert into Productos(nombreProducto, cantidad)
				values(nombreProducto, cantidad);
        End//
Delimiter ; 

-- --------------------------------Listar Productos ---------------------------------------------
Delimiter //
	Create procedure sp_ListarProductos()
		Begin
			Select 
				P.codigoProducto,
                P.nombreProducto,
                P.cantidad
					from Productos P;
        End//
Delimiter ;

-- --------------------------------- Buscar Producto -----------------------------------------
Delimiter //
	Create procedure sp_BuscarProducto(in codProducto int)
		Begin
			Select 
				P.codigoProducto,
                P.nombreProducto,
                P.cantidad
					from Productos P
						where P.codigoProducto = codProducto;
		End//
Delimiter ;

-- --------------------------------- Eliminar Producto --------------------------------------------
Delimiter //
	Create procedure sp_EliminarProducto(in codProducto int)
		Begin
			Delete from Productos
				where codigoProducto = codProducto;
		End//
Delimiter ;

-- ---------------------------------Editar Producto
Delimiter //
	Create procedure sp_EditarProducto(in codProducto int,
		in nomProducto varchar(150), in cant int)
        Begin
			Update Productos P
				set P.nombreProducto = nomProducto,
					P.cantidad = cant
                    where codigoProducto = codProducto;
        End//
Delimiter ;

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------

-- ------------------------------- Agregar Empleado
Delimiter //
	Create procedure sp_AgregarEmpleado(in numeroEmpleado int,
		in apellidosEmpleado varchar(150), in nombresEmpleado varchar(150),
		in direccionEmpleado varchar(150), in telefonoContacto varchar(8),
        in gradoCocinero varchar(50), in codigoTipoEmpleado int)
		Begin
			Insert into Empleados(numeroEmpleado,apellidosEmpleado,
					nombresEmpleado,direccionEmpleado,
					telefonoContacto, gradoCocinero, codigoTipoEmpleado)
				values(numeroEmpleado,apellidosEmpleado,
					nombresEmpleado,direccionEmpleado,
					telefonoContacto, gradoCocinero, codigoTipoEmpleado);
        End//
Delimiter ; 

-- ------------------------------ Listar Empleados --------------------

Delimiter //
	Create procedure sp_ListarEmpleados()
		Begin
			Select 
				E.codigoEmpleado,
                E.numeroEmpleado,
                E.apellidosEmpleado,
                E.nombresEmpleado,
                E.direccionEmpleado,
                E.telefonoContacto,
                E.gradoCocinero,
                E.codigoTipoEmpleado
					From Empleados E;
		End//
Delimiter ;

-- ------------------------------ Buscar Empleado --------------------------
Delimiter //
	Create procedure sp_BuscarEmpleado(in codEmpleado int)
		Begin
			Select 
				E.codigoEmpleado,
                E.numeroEmpleado,
                E.apellidosEmpleado,
                E.nombresEmpleado,
                E.direccionEmpleado,
                E.telefonoContacto,
                E.gradoCocinero,
                E.codigoTipoEmpleado
					from Empleados E
						where E.codigoEmpleado = codEmpleado;
		End//
Delimiter ;

-- ----------------------------------- Eliminar Empleado
Delimiter //
	Create procedure sp_EliminarEmpleado(in codEmpleado int)
		Begin
			Delete from Empleados
				where codigoEmpleado = codEmpleado;
        End//
Delimiter ;

-- ----------------------------------- Editar Empleado ---------------------------------
Delimiter //
	Create procedure sp_EditarEmpleado(in codEmpleado int,in numEmpleado int,
		in apeEmpleado varchar(150), in nomEmpleado varchar(150),
		in direcEmpleado varchar(150), in telContacto varchar(8),
        in gradCocinero varchar(50))
		Begin
			Update Empleados E
				set E.numeroEmpleado = numEmpleado,
					E.apellidosEmpleado = apeEmpleado, 
					E.nombresEmpleado = nomEmpleado,
					E.direccionEmpleado = direcEmpleado,
					E.telefonoContacto = telContacto,
					E.gradoCocinero = gradCocinero
						where E.codigoEmpleado = codEmpleado;
		
        End//
Delimiter ;

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------
-- -------------------------- Agregar Servicio------------------------------------------

Delimiter //
	Create procedure sp_AgregarServicio(in fechaServicio date, 
		in tipoServicio varchar(150), in horaServicio time, 
        in lugarServicio varchar(150),in telefonoContacto varchar(150), 
        in codigoEmpresa int)
        Begin 
			Insert into Servicios(fechaServicio, tipoServicio, horaServicio,
            lugarServicio, telefonoContacto, codigoEmpresa)
				values(fechaServicio, tipoServicio, horaServicio,
				lugarServicio, telefonoContacto, codigoEmpresa);
        End//
Delimiter ; 

-- --------------------------- Listar Servicios ---------------------------------------
Delimiter //
	Create procedure sp_ListarServicios()
		Begin 
			Select 
				S.codigoServicio,
                S.fechaServicio,
                S.tipoServicio,
                S.horaServicio,
                S.lugarServicio,
                S.telefonoContacto,
                S.codigoEmpresa
					from Servicios S;
		End//
Delimiter ;

-- ---------------------------- Buscar Servicio ----------------------------
Delimiter //
	Create procedure sp_BuscarServicio(in codServicio int )
		Begin 
			Select 
				S.codigoServicio,
                S.fechaServicio,
                S.tipoServicio,
                S.horaServicio,
                S.lugarServicio,
                S.telefonoContacto,
                S.codigoEmpresa
					from Servicios S
						where S.codigoServicio = codServicio;
		End//
Delimiter ;

-- ----------------------------- Eliminar Servicio -------------------------------------------
Delimiter //
	Create procedure sp_EliminarServicio(in codServicio int)
		Begin
			Delete from Servicios
				where codigoServicio = codServicio;
        End//
Delimiter ;

-- ---------------------------- Editar Servicio --------------------------------------------
Delimiter //
	Create procedure sp_EditarServicio(in codServicio int, in fechServicio date, 
		in tipServicio varchar(150), in hrServicio time, 
        in lgrServicio varchar(150),in telContacto varchar(150))
			Begin
				Update Servicios S
					set S.fechaServicio = fechServicio,
						S.tipoServicio = tipServicio,
						S.horaServicio = hrServicio,
						S.lugarServicio = lgrServicio,
						S.telefonoContacto = telContacto
							where S.codigoServicio = codServicio;
            End//
Delimiter ;

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------
-- -------------------------- Agregar Presupuesto -----------------------------------
Delimiter //
	Create procedure sp_AgregarPresupuesto(in fechaSolicitud date, 
		in cantidadPresupuesto decimal(10,2), in codigoEmpresa int)
		Begin
			Insert into Presupuestos(fechaSolicitud,cantidadPresupuesto, codigoEmpresa)
				values(fechaSolicitud,cantidadPresupuesto, codigoEmpresa);
        End //
Delimiter ;   

-- ---------------------------- Listar Presupuestos ---------------------------------------
Delimiter //
	Create procedure sp_ListarPresupuestos()
		Begin
			Select 
            P.codigoPresupuesto,
            P.fechaSolicitud,
            P.cantidadPresupuesto,
            P.codigoEmpresa
            From Presupuestos P;
        End //
Delimiter ;  

-- -------------------------------- Buscar Presupuesto ------------------------------------
Delimiter //
	Create procedure sp_BuscarPresupuesto(in codPresupuesto int)
		Begin
			Select 
				P.codigoPresupuesto,
				P.fechaSolicitud,
				P.cantidadPresupuesto,
				P.codigoEmpresa
				From Presupuestos P
					where P.codigoPresupuesto = codPresupuesto;
		End //
Delimiter ;

-- ---------------------------------- Eliminar Presupuesto ------------------------------
Delimiter //
	Create procedure sp_EliminarPresupuesto(in codPresupuesto int)
		Begin
			Delete from Presupuestos
				where codigoPresupuesto = codPresupuesto;
        End //
Delimiter ;

-- ----------------------------------- Editar Presupuesto ----------------------------
Delimiter //
	Create procedure sp_EditarPresupuesto(in codPresupuesto int, 
		in fechSolicitud date, in cantPresupuesto decimal(10,2))
		Begin
			Update Presupuestos P
				set P.fechaSolicitud = fechSolicitud,
					P.cantidadPresupuesto = cantPresupuesto
						where P.codigoPresupuesto = codPresupuesto;
        End //
Delimiter ;

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------
-- --------------------------Agregar Plato ---------------------------------------
Delimiter //
	Create procedure sp_AgregarPlato(in cantidad int, in nombrePlato varchar(50),
		in descripcionPlato varchar(150), in precioPlato decimal(10,2),
        in codigoTipoPlato int)
		Begin
			Insert into Platos(cantidad, nombrePlato,descripcionPlato, 
            precioPlato, codigoTipoPlato)
				values(cantidad, nombrePlato,descripcionPlato, 
				precioPlato, codigoTipoPlato);
        End//
Delimiter ;

-- ------------------------ Listar Platos ------------------------------------------
Delimiter //
	Create procedure sp_ListarPlatos()
		Begin
			Select 
				P.codigoPlato,
                P.cantidad,
                P.nombrePlato,
                P.descripcionPlato,
                P.precioPlato,
                P.codigoTipoPlato
                From Platos P;
        End//
Delimiter ;

-- ---------------------------- Buscar Plato- ---------------------
Delimiter //
	Create procedure sp_BuscarPlato(in codPlato int)
		Begin
			Select 
				P.codigoPlato,
                P.cantidad,
                P.nombrePlato,
                P.descripcionPlato,
                P.precioPlato,
                P.codigoTipoPlato
				From Platos P
					where P.codigoPlato = codPlato;
		End //
Delimiter ;

-- -------------------------- Eliminar Plato -------------------------
Delimiter //
	Create procedure sp_EliminarPlato(in codPlato int)
		Begin
			Delete from Platos 
				where codigoPlato = codPlato;
        End //
Delimiter ;

-- ---------------------------- Editar Plato ---------------------------
Delimiter //
	Create procedure sp_EditarPlato(in codPlato int, in cant int,
		in nomPlato varchar(50),in descPlato varchar(150), 
        in precPlato decimal(10,2))
        Begin
			Update Platos P
				set P.cantidad = cant,
					P.nombrePlato = nomPlato,
					P.descripcionPlato = descPlato,
					P.precioPlato = precPlato
						where P.codigoPlato = codPlato;
        End//
Delimiter ;

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------
-- --------------------------Agregar Productos_has_Platos -------------------------------

Delimiter //
	Create procedure sp_AgregarProductos_has_Platos(in Productos_codigoProducto int,in codigoPlato int, in codigoProducto int)
		Begin
			Insert into Productos_has_Platos(Productos_codigoProducto,codigoPlato, codigoProducto)
				values(Productos_codigoProducto,codigoPlato, codigoProducto);
        End //
Delimiter ;   

-- ------------------------Listar Productos_has_Platos --------------------------------
Delimiter //
	Create procedure sp_ListarProductos_has_Platos()
		Begin
			Select 
            PhP.Productos_codigoProducto,
            PhP.codigoPlato,
            PhP.codigoProducto
            From Productos_has_Platos PhP;
        End //
Delimiter ;   

-- --------------------------- Buscar Productos_has_Platos -------------------------------------
Delimiter //
	Create procedure sp_BuscarProductos_has_Platos(in Productos_codProducto int)
		Begin
			Select 
				PhP.Productos_codigoProducto,
				PhP.codigoPlato,
				PhP.codigoProducto
				From Productos_has_Platos PhP
					where PhP.Productos_codigoProducto = Productos_codProducto;
		End //
Delimiter ;

-- ---------------------------------- Eliminar Productos_has_Platos-------------------------
/*
Delimiter //
	Create procedure sp_EliminarProductos_has_Platos(in Productos_codProducto int)
		Begin
			Delete from Productos_has_Platos
				where Productos_codigoProducto = Productos_codProducto;
        End //
Delimiter ;
*/
-- ---------------------------------- Editar Productos_has_Platos ----------------------
/*

Delimiter //
	Create procedure sp_EditarProductos_has_Platos(in Productos_codProducto int,
		in codPlato int, in codProducto int)
		Begin
			Update Productos_has_Platos PhP
				set 
					PhP.codigoPlato = codPlato,
					PhP.codigoProducto = codProducto
						where Productos_codigoProducto = Productos_codProducto;
        End //
Delimiter ;

*/

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------

-- -------------------------- Agregar Servicios_has_Platos --------------------------------

Delimiter //
	Create procedure sp_AgregarServicios_has_Platos(in Servicios_codigoServicio int, in codigoPlato int, codigoServicio int)
		Begin
			Insert into Servicios_has_Platos(Servicios_codigoServicio, codigoPlato, codigoServicio)
				values(Servicios_codigoServicio, codigoPlato, codigoServicio);
        End //
Delimiter ;   

-- --------------------------- Listar Servicios_has_Platos -------------------------------
Delimiter //
	Create procedure sp_ListarServicios_has_Platos()
		Begin
			Select 
				ShP.Servicios_codigoServicio,
                ShP.codigoPlato,
                ShP.codigoServicio
				From Servicios_has_Platos ShP;
        End //
Delimiter ;    
    
-- --------------------------- Buscar Servicios_has_Platos -------------------------------
Delimiter //
	Create procedure sp_BuscarServicios_has_Platos(in Servicios_codServicio int)
		Begin
			Select 
				ShP.Servicios_codigoServicio,
                ShP.codigoPlato,
                ShP.codigoServicio
				From Servicios_has_Platos ShP
					where ShP.Servicios_codigoServicio = Servicios_codServicio;
		End //
Delimiter ;

-- --------------------------- Eliminar Servicios_has_Platos -------------------------------
/*
Delimiter //
	Create procedure sp_EliminarServicios_has_Platos(in Servicios_codServicio int)
		Begin
			Delete from Servicios_has_Platos
				where Servicios_codigoServicio = Servicios_codServicio;
        End //
Delimiter ;
*/
-- --------------------------- Editar Servicios_has_Platos -------------------------------
/*
Delimiter //
	Create procedure sp_EditarServicios_has_Platos(in Servicios_codServicio int,
		in codPlato int, codServicio int)
			Begin
				Update Servicios_has_Platos ShP
					set ShP.Servicios_codigoServicio = Servicios_codServicio,
						ShP.codigoPlato = codPlato,
						ShP.codigoServicio = codServicio
							where ShP.Servicios_codigoServicio = Servicios_codServicio;
            End//
Delimiter ;
*/

-- --------------------------* CRUD Create, Read, Update, Delete(Buscar) *--------------

-- --------------------------- Agregar Servicios_has_Empleados -------------------------------

Delimiter //
	Create procedure sp_AgregarServicios_has_Empleados(in Servicios_codigoServicio int,in codigoServicio int, in codigoEmpleado int,
		in fechaEvento date, in horaEvento time, lugarEvento varchar(150))
		Begin
			Insert into Servicios_has_Empleados(Servicios_codigoServicio,codigoServicio, codigoEmpleado,fechaEvento, horaEvento, lugarEvento)
				values(Servicios_codigoServicio,codigoServicio, codigoEmpleado,fechaEvento, horaEvento, lugarEvento);
        End //
Delimiter ;  

-- --------------------------- Listar Servicios_has_Empleados -------------------------------
Delimiter //
	Create procedure sp_ListarServicios_has_Empleados()
		Begin
			Select 
				ShE.Servicios_codigoServicio,
				ShE.codigoServicio,
				ShE.codigoEmpleado,
				ShE.fechaEvento,
				ShE.horaEvento,
                ShE.lugarEvento
				From Servicios_has_Empleados ShE;
        End //
Delimiter ; 
-- --------------------------- Buscar Servicios_has_Empleados -------------------------------
Delimiter //
	Create procedure sp_BuscarServicios_has_Empleados(in Servicios_codServicio int)
		Begin
			Select 
				ShE.Servicios_codigoServicio,
				ShE.codigoServicio,
				ShE.codigoEmpleado,
				ShE.fechaEvento,
				ShE.horaEvento,
                ShE.lugarEvento
				From Servicios_has_Empleados ShE
					where ShE.Servicios_codigoServicio = Servicios_codServicio;
        End//
Delimiter ;/*
-- --------------------------- Eliminar Servicios_has_Empleados -------------------------------

Delimiter //
	Create procedure sp_EliminarServicios_has_Empleados(in Servicios_codServicio int)
		Begin
			Delete from Servicios_has_Empleados
				where Servicios_codigoServicio = Servicios_codServicio;
        End //
Delimiter ;

-- --------------------------- Editar Servicios_has_Empleados ----------------------------------

Delimiter //
	Create procedure sp_EditarServicios_has_Empleados(in Servicios_codServicio int,
		in fechEvento date,in hrEvento time, lgrEvento varchar(150))
			Begin
				Update Servicios_has_Empleados ShE
					set	ShE.fechaEvento = fechEvento,
						ShE.horaEvento = hrEvento,
						ShE.lugarEvento = lgrEvento
						where ShE.Servicios_codigoServicio = Servicios_codServicio;
            End//
Delimiter ;*/
-- ------------------------- Inner join --------------------------------
Delimiter //
	create procedure sp_ReporteGeneral(in id int)
		Begin
			Select distinct
				E.nombreEmpresa,
                E.direccion,
                P.cantidadPresupuesto,
				S.fechaServicio,
                S.tipoServicio,
                EM.nombresEmpleado,
                EM.apellidosEmpleado,
                TE.descripcion,
                PL.nombrePlato,
				PR.nombreProducto
                from Empresas E inner join Presupuestos P on E.codigoEmpresa = P.codigoEmpresa
					inner join Servicios S on E.codigoEmpresa = S.codigoEmpresa
						inner join Servicios_has_Empleados SE on S.codigoServicio = SE.codigoServicio
							inner join Empleados EM on SE.codigoEmpleado = EM.codigoEmpleado
								inner join TipoEmpleado TE on EM.codigoTipoEmpleado = TE.codigoTipoEmpleado
					inner join Servicios_has_Platos SP on S.codigoServicio = SP.codigoServicio
						inner join Platos PL on SP.codigoPlato = PL.codigoPlato
							inner join Productos_has_Platos PP on PL.codigoPlato = PP.codigoPlato
								inner join Productos PR on PP.codigoProducto = PR.codigoProducto
					where E.codigoEmpresa = id
                    group by S.codigoServicio;
        End//
Delimiter ;
call sp_ReporteGeneral(1);


Delimiter //
	Create procedure sp_ReportePresupuesto(in id int)
		begin
			select * from Empresas E inner join Presupuestos P on
			E.codigoEmpresa = P.codigoEmpresa 
            where E.codigoEmpresa = id group by P.cantidadPresupuesto;
        End //
Delimiter ;
call sp_ReportePresupuesto(1);

Delimiter //
	Create procedure sp_RerpoteServicios(in id int)
		Begin
			select * from Empresas E
				inner join Servicios S
					on E.codigoEmpresa = S.codigoEmpresa
						where E.codigoEmpresa = id
						group by(S.codigoServicio);
        End //
Delimiter ;
call sp_RerpoteServicios(2);

Delimiter //
	Create procedure sp_ReporteEmpleado(in id int)
		Begin
			select EM.nombresEmpleado, EM.apellidosEmpleado from Empresas E
				inner join Servicios S
					on E.codigoEmpresa = S.codigoEmpresa
						inner join Servicios_has_Empleados SE
							on S.codigoServicio = SE.codigoServicio
								inner join Empleados EM
									on SE.codigoEmpleado = EM.codigoEmpleado
										where E.codigoEmpresa = id
                        group by(S.codigoServicio);
        End //
Delimiter ;
call sp_ReporteEmpleado(1);

Delimiter //
	Create procedure sp_ReportePlatoProducto(in id int)
		Begin
			select P.precioPlato, P.nombrePlato, PR.nombreProducto
				from Empresas E
					inner join Servicios S
						on E.codigoEmpresa = S.codigoEmpresa
							inner join Servicios_has_Platos SP
								on S.codigoServicio = SP.codigoServicio
									inner join Platos P
										on SP.codigoPlato = P.codigoPlato
					inner join Productos_has_Platos PP
						on P.codigoPlato = PP.codigoPlato
							inner join Productos PR
								on PP.codigoProducto = PR.codigoProducto
			where E.codigoEmpresa = id group by(S.codigoServicio);
		End //
Delimiter ;

call sp_ReportePlatoProducto(2);

-- ------------------------- DML ---------------------------------------

call sp_AgregarEmpresa('Pizza Hut','Zona 5 Villa Nueva','12345678');
call sp_AgregarEmpresa('Pollo Campero','9 C 10-68 Z-1,Ciudad de Guatemala,Guatemala','54788542');
call sp_AgregarEmpresa('Little Cesar','San Migel Petapa','87459654');
call sp_AgregarEmpresa('Castor Pizza','Fundación kinal','12547854');
call sp_AgregarEmpresa('Mc Donnald´s','El trébol','87563544');
call sp_AgregarEmpresa('Wendy´s','Calzada La Paz','36639158');
call sp_AgregarEmpresa('KFC','La Bolívar','41734462');
call sp_AgregarEmpresa('Pepsi','El Mezquital','47708335');
call sp_AgregarEmpresa('Pinulito','San José Pinula','41534462');
call sp_AgregarEmpresa('One Day','Amatitlán','41959493');

call sp_AgregarProducto('Papas',12);
call sp_AgregarProducto('Lacteos',12);
call sp_AgregarProducto('Carnes',112);
call sp_AgregarProducto('Frutas',1321);
call sp_AgregarProducto('Verduras',132);
call sp_AgregarProducto('Comestibles',43);
call sp_AgregarProducto('Risitos',43);
call sp_AgregarProducto('Semillas',543);
call sp_AgregarProducto('Frutos Secos',76);
call sp_AgregarProducto('Frutos Rojos',2);

call sp_AgregarTipoEmpleado('Gerente');
call sp_AgregarTipoEmpleado('Limpieza');
call sp_AgregarTipoEmpleado('Coordinador');
call sp_AgregarTipoEmpleado('Bartender');
call sp_AgregarTipoEmpleado('Picador');
call sp_AgregarTipoEmpleado('Pelador');
call sp_AgregarTipoEmpleado('Cocinero');
call sp_AgregarTipoEmpleado('Cevichero');
call sp_AgregarTipoEmpleado('Mostacho');
call sp_AgregarTipoEmpleado('Mesero');


call sp_AgregarTipoPlato('El mejor plato de la historia');
call sp_AgregarTipoPlato('Pequeño');
call sp_AgregarTipoPlato('Lechero');
call sp_AgregarTipoPlato('Para meter la basura');
call sp_AgregarTipoPlato('Servidor');
call sp_AgregarTipoPlato('Heladero');
call sp_AgregarTipoPlato('Para caldo ');
call sp_AgregarTipoPlato('Seco');
call sp_AgregarTipoPlato('Caliente');
call sp_AgregarTipoPlato('Plato frio');

call sp_AgregarPresupuesto('2023-02-12',122.2,1);
call sp_AgregarPresupuesto('2023-02-12',1025,2);
call sp_AgregarPresupuesto('2023-02-12',511,2);
call sp_AgregarPresupuesto('2023-02-12',665.2,3);
call sp_AgregarPresupuesto('2023-02-12',69874.2,2);
call sp_AgregarPresupuesto('2023-02-12',745.2,1);
call sp_AgregarPresupuesto('2023-02-12',852.2,2);
call sp_AgregarPresupuesto('2023-02-12',9872.2,3);
call sp_AgregarPresupuesto('2023-02-12',5321.2,4);
call sp_AgregarPresupuesto('2023-02-12',564.24,5);

call sp_AgregarEmpleado(1,'Aguilar','Juan', 'Zona 5 villa nueva','45874589','5to',1);
call sp_AgregarEmpleado(4,'Garcia Herrera','Andy Rafael', 'Zona 5 villa nueva','1234321','5to',1);
call sp_AgregarEmpleado(12,'Fernandez Fernandez','Luis ', 'Zona 5 villa nueva','312','5to',1);
call sp_AgregarEmpleado(3,'Montoya Calderon ','Ariel Estuardo', 'Zona 5 villa nueva','312','5to',1);
call sp_AgregarEmpleado(221,'Estrada Juárez','Diego René', 'Zona 5 villa nueva','3124','5to',1);
call sp_AgregarEmpleado(25,'Juáres','Eulalia', 'Zona 5 villa nueva','312','5to',1);
call sp_AgregarEmpleado(231,'Estrada Morales','Erick René', 'Zona 5 villa nueva','412','6to',1);
call sp_AgregarEmpleado(232,'Juárez','Lilian Fabiola', 'Zona 5 villa nueva','2135412','3to',1);
call sp_AgregarEmpleado(265,'Estrada Juárez','Dulce María', 'Zona 5 villa nueva','4321','55to',1);
call sp_AgregarEmpleado(275,'Morales','Beda', 'Zona 5 villa nueva','123','3to',1);

call sp_AgregarServicio('2023-05-16','comida','11:40','Tikal','78945123',1);
call sp_AgregarServicio('2023-05-16','limpieza','11:40','Palacio de los Capitanes Generales','78945123',2);
call sp_AgregarServicio('2023-05-16','limpieza general','11:40','Ayuntamiento ','78945123',2);
call sp_AgregarServicio('2023-05-16','atención telefonica','11:40',' Catedral de San José','78945123',1);
call sp_AgregarServicio('2023-05-16','Orientacion','11:40','Grutas de Lanquín','78945123',3);
call sp_AgregarServicio('2023-05-16','Soporte Técnico','11:40','La Recolección','78945123',3);
call sp_AgregarServicio('2023-05-16','Asistencia','11:40','Plaza FontaBella','78945123',4);
call sp_AgregarServicio('2023-05-16','Lavado','11:40','Oakland Mall','78945123',3);
call sp_AgregarServicio('2023-05-16','Secado','11:40','Los Proceres','78945123',2);
call sp_AgregarServicio('2023-05-16','Ordenamiento','11:40','La Noria','78945123',1);
call sp_AgregarServicio('2023-05-16','edrfa','11:40','Las felancias','78945123',2);

call sp_AgregarPlato(1,'Palomitas con mantequilla','Pequeño',10.2,1);
call sp_AgregarPlato(1,'Som tam','Muy salado',10.2,1);
call sp_AgregarPlato(1,'Papas fritas','Es muy frio',10.2,1);
call sp_AgregarPlato(1,'Paella de mariscos','Demasiado espeso ',10.2,1);
call sp_AgregarPlato(1,'Tacos','Caliente',10.2,1);
call sp_AgregarPlato(1,'Arroz con pollo	','AgriDulce',10.2,1);
call sp_AgregarPlato(1,'Cangrejo picante','Muy caro',10.2,1);
call sp_AgregarPlato(1,'Ankimo','Perfectamente sabroso',10.2,1);
call sp_AgregarPlato(1,'Fajitas','Muy crujientes',10.2,1);
call sp_AgregarPlato(1,'Lasaña','Demasiado frio',10.2,1);

call sp_AgregarServicios_has_Platos(1,1,1);
call sp_AgregarServicios_has_Platos(2,2,2);
call sp_AgregarServicios_has_Platos(3,2,1);
call sp_AgregarServicios_has_Platos(4,2,1);
call sp_AgregarServicios_has_Platos(5,1,1);
call sp_AgregarServicios_has_Platos(6,1,1);
call sp_AgregarServicios_has_Platos(7,1,1);
call sp_AgregarServicios_has_Platos(8,1,1);
call sp_AgregarServicios_has_Platos(9,1,1);
call sp_AgregarServicios_has_Platos(10,1,1);

call sp_AgregarServicios_has_Empleados(1,1,1,'2022-05-02','11:50','Fuentes del valle 2');
call sp_AgregarServicios_has_Empleados(2,1,1,'1111-11-11','05:50','El frutal');
call sp_AgregarServicios_has_Empleados(3,2,1,'1111-11-11','05:50','Zona 12 la reformita');
call sp_AgregarServicios_has_Empleados(4,2,2,'1111-11-11','05:50','Plaza madero');
call sp_AgregarServicios_has_Empleados(5,3,2,'1111-11-11','05:50','Pradera');
call sp_AgregarServicios_has_Empleados(6,3,2,'1111-11-11','05:50','Mazate');
call sp_AgregarServicios_has_Empleados(7,4,3,'1111-11-11','05:50','Tikal Futura');
call sp_AgregarServicios_has_Empleados(8,4,4,'1111-11-11','05:50','El obelisco');
call sp_AgregarServicios_has_Empleados(9,5,4,'1111-11-11','05:50','La janchura');
call sp_AgregarServicios_has_Empleados(10,5,1,'1111-11-11','05:50','Vistares');

call sp_AgregarProductos_has_Platos(1,1,1);
call sp_AgregarProductos_has_Platos(2,1,1);
call sp_AgregarProductos_has_Platos(3,1,1);
call sp_AgregarProductos_has_Platos(4,1,1);
call sp_AgregarProductos_has_Platos(5,1,1);
call sp_AgregarProductos_has_Platos(6,1,1);
call sp_AgregarProductos_has_Platos(7,1,1);
call sp_AgregarProductos_has_Platos(8,1,1);
call sp_AgregarProductos_has_Platos(9,1,1);
call sp_AgregarProductos_has_Platos(10,1,1);
call sp_AgregarProductos_has_Platos(11,1,1);
