# Java Web Employees MySQL

Aplicación web desarrollada en **Java Web con Servlets, JSP, JDBC y MySQL**.  
El proyecto permite conectarse a una base de datos MySQL, insertar empleados de ejemplo mediante `Statement` cuando la tabla está vacía y consultar todos los empleados registrados mediante un `SELECT`.

**Autor:** Andrick Ivan Almengor Quirós  
**Curso:** 2026C3-G01 - Programación de Computadoras II  
**Universidad:** Universidad Castro Carazo  
**Fecha:** 7 de octubre de 2026

---

## Tabla de contenido

- [1. Descripción del proyecto](#1-descripción-del-proyecto)
- [2. Objetivo](#2-objetivo)
- [3. Tecnologías utilizadas](#3-tecnologías-utilizadas)
- [4. Estructura del proyecto](#4-estructura-del-proyecto)
- [5. Creación de la base de datos](#5-creación-de-la-base-de-datos)
  - [5.1 Crear la base de datos y la tabla](#51-crear-la-base-de-datos-y-la-tabla)
  - [5.2 Crear el usuario de la aplicación](#52-crear-el-usuario-de-la-aplicación)
  - [5.3 Comandos de comprobación](#53-comandos-de-comprobación)
- [6. Funcionamiento del código](#6-funcionamiento-del-código)
  - [6.1 Database.java](#61-databasejava)
  - [6.2 EmployeeServlet.java](#62-employeeservletjava)
  - [6.3 index.jsp](#63-indexjsp)
  - [6.4 employee.jsp](#64-employeejsp)
  - [6.5 Flujo general](#65-flujo-general)
- [7. Configuración de JDBC en NetBeans](#7-configuración-de-jdbc-en-netbeans)
- [8. Configuración del puerto MySQL](#8-configuración-del-puerto-mysql)
  - [8.1 Configuración utilizada en este proyecto: 3305](#81-configuración-utilizada-en-este-proyecto-3305)
  - [8.2 Configuración estándar: 3306](#82-configuración-estándar-3306)
- [9. Problemas encontrados y soluciones](#9-problemas-encontrados-y-soluciones)
  - [9.1 No suitable driver found](#91-no-suitable-driver-found)
  - [9.2 Access denied for user](#92-access-denied-for-user)
  - [9.3 Public Key Retrieval is not allowed](#93-public-key-retrieval-is-not-allowed)
- [10. Resultado esperado](#10-resultado-esperado)
- [11. Referencias](#11-referencias)

---

## 1. Descripción del proyecto

Este proyecto consiste en una aplicación web sencilla para consultar empleados almacenados en una base de datos MySQL.

La aplicación se desarrolló utilizando una estructura básica de separación de responsabilidades:

- `Database.java`: administra la conexión con MySQL.
- `EmployeeServlet.java`: realiza la lógica de consulta e inserción.
- `index.jsp`: presenta la pantalla inicial.
- `employee.jsp`: muestra los empleados obtenidos de la base de datos.

La comunicación entre Java y MySQL se realiza mediante **JDBC** y **MySQL Connector/J**.

---

## 2. Objetivo

El objetivo de la práctica es demostrar el uso de una aplicación Java Web que:

1. Se conecte a una base de datos MySQL mediante JDBC.
2. Utilice una clase independiente para administrar la conexión.
3. Inserte registros mediante `Statement`.
4. Consulte registros mediante `SELECT`.
5. Envíe los resultados desde un Servlet hacia una página JSP.
6. Muestre los empleados en una tabla HTML.

---

## 3. Tecnologías utilizadas

- Java
- Java Web
- Jakarta Servlet
- JSP
- JDBC
- MySQL
- MySQL Connector/J
- Apache Tomcat / TomEE
- Apache NetBeans
- Git
- GitHub

---

## 4. Estructura del proyecto

La estructura principal es la siguiente:

```text
java-web-employees-mysql
│
├── src
│   └── java
│       └── com.empresa
│           ├── Database.java
│           └── EmployeeServlet.java
│
├── web
│   ├── META-INF
│   ├── index.jsp
│   └── employee.jsp
│
├── nbproject
├── build.xml
└── README.md
```

---

## 5. Creación de la base de datos

### 5.1 Crear la base de datos y la tabla

Ejecutar los siguientes comandos en MySQL Workbench:

```sql
CREATE DATABASE empresa;

USE empresa;

CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    departamento VARCHAR(100) NOT NULL
);
```

La tabla contiene los siguientes campos:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | INT | Identificador único generado automáticamente |
| `nombre` | VARCHAR(100) | Nombre del empleado |
| `departamento` | VARCHAR(100) | Departamento del empleado |

---

### 5.2 Crear el usuario de la aplicación

Para no utilizar directamente el usuario `root`, puede crearse un usuario exclusivo para la aplicación:

```sql
CREATE USER 'empresa_app'@'localhost'
IDENTIFIED BY 'Empresa123';

GRANT SELECT, INSERT
ON empresa.*
TO 'empresa_app'@'localhost';

FLUSH PRIVILEGES;
```

> **Nota:** La contraseña anterior se utiliza únicamente como ejemplo para un entorno local de práctica. En un proyecto real no se recomienda almacenar credenciales directamente en el código ni en un repositorio público.

Para comprobar los permisos:

```sql
SHOW GRANTS FOR 'empresa_app'@'localhost';
```

---

### 5.3 Comandos de comprobación

Para revisar la configuración del servidor MySQL:

```sql
SELECT @@hostname AS servidor,
       @@port AS puerto,
       USER() AS conexion,
       CURRENT_USER() AS usuario_autenticado;
```

Para consultar los empleados:

```sql
USE empresa;

SELECT * FROM empleados;
```

También puede comprobarse únicamente el puerto:

```sql
SELECT @@port;
```

---

## 6. Funcionamiento del código

### 6.1 Database.java

La clase `Database.java` concentra la configuración necesaria para conectarse a MySQL.

El driver utilizado es:

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```

La conexión se abre mediante:

```java
DriverManager.getConnection(URL, USER, PASSWORD);
```

En este proyecto, MySQL se encuentra configurado localmente en el puerto **3305**:

```java
private static final String URL =
        "jdbc:mysql://localhost:3305/empresa"
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=UTC";
```

El método `getConnection()` devuelve un objeto `Connection` que posteriormente utiliza el Servlet.

La ventaja de mantener la conexión en una clase separada es que los datos de conexión no tienen que repetirse en otras clases.

---

### 6.2 EmployeeServlet.java

`EmployeeServlet.java` funciona como controlador de la aplicación.

El Servlet está asociado a la ruta:

```java
@WebServlet("/employees")
```

Cuando el usuario abre esa dirección, se ejecuta el método `doGet()`.

Primero se obtiene una conexión y se crea un `Statement`:

```java
Connection connection = Database.getConnection();
Statement statement = connection.createStatement();
```

Después se revisa si la tabla contiene empleados:

```java
ResultSet countResult =
        statement.executeQuery("SELECT COUNT(*) FROM empleados");
```

Si la tabla está vacía, se insertan empleados de ejemplo mediante `Statement` y `executeUpdate()`:

```java
statement.executeUpdate(
    "INSERT INTO empleados (nombre, departamento) " +
    "VALUES ('Carlos Rodríguez', 'Ventas')"
);

statement.executeUpdate(
    "INSERT INTO empleados (nombre, departamento) " +
    "VALUES ('María González', 'Recursos Humanos')"
);

statement.executeUpdate(
    "INSERT INTO empleados (nombre, departamento) " +
    "VALUES ('Andrés López', 'Tecnología')"
);
```

La comprobación previa evita que los mismos empleados se inserten cada vez que se recarga la página.

Finalmente se realiza la consulta principal:

```java
ResultSet result =
        statement.executeQuery(
            "SELECT id, nombre, departamento FROM empleados"
        );
```

Cada registro obtenido se agrega a una lista.

Luego el Servlet envía esa lista a la JSP:

```java
request.setAttribute("empleados", empleados);
```

y transfiere el control a:

```java
request.getRequestDispatcher("/employee.jsp")
       .forward(request, response);
```

---

### 6.3 index.jsp

`index.jsp` es la página inicial de la aplicación.

Su función es mostrar el título del sistema y proporcionar un enlace hacia:

```text
/employees
```

Al seleccionar **Ver empleados**, la solicitud se envía a `EmployeeServlet`.

La página inicial no realiza directamente ninguna consulta a MySQL.

---

### 6.4 employee.jsp

`employee.jsp` recibe la lista de empleados enviada por el Servlet.

Los registros se recorren y se presentan dentro de una tabla HTML con las columnas:

- ID
- Nombre
- Departamento

La JSP también puede mostrar errores enviados desde el Servlet cuando ocurre un problema de conexión o consulta.

---

### 6.5 Flujo general

El funcionamiento completo puede resumirse así:

```text
index.jsp
   │
   ▼
/employees
   │
   ▼
EmployeeServlet.java
   │
   ▼
Database.java
   │
   ▼
MySQL
   │
   ▼
SELECT empleados
   │
   ▼
EmployeeServlet.java
   │
   ▼
employee.jsp
   │
   ▼
Tabla de empleados
```

---

## 7. Configuración de JDBC en NetBeans

MySQL Workbench y MySQL Connector/J son herramientas diferentes.

- **MySQL Workbench** permite administrar MySQL gráficamente.
- **MySQL Connector/J** permite que Java se conecte a MySQL mediante JDBC.

Para este proyecto fue necesario descargar MySQL Connector/J y agregar su archivo `.jar` en NetBeans.

Pasos:

1. Descargar **MySQL Connector/J**.
2. Extraer el archivo descargado.
3. Localizar un archivo parecido a:

```text
mysql-connector-j-xx.x.x.jar
```

4. En NetBeans:

```text
Proyecto
→ Properties
→ Libraries
→ Add JAR/Folder
```

5. Seleccionar el archivo de MySQL Connector/J.
6. Ejecutar:

```text
Clean and Build
```

7. Ejecutar nuevamente la aplicación.

El archivo debe aparecer dentro de las librerías del proyecto y ser incluido en el despliegue de la aplicación web.

---

## 8. Configuración del puerto MySQL

### 8.1 Configuración utilizada en este proyecto: 3305

Durante el desarrollo se utilizó:

```text
localhost:3305
```

La configuración se descubrió ejecutando:

```sql
SELECT @@port;
```

El resultado fue:

```text
3305
```

Por ello, la URL JDBC utilizada es:

```java
jdbc:mysql://localhost:3305/empresa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

---

### 8.2 Configuración estándar: 3306

El puerto predeterminado de MySQL normalmente es:

```text
3306
```

Si se clona este proyecto en una computadora donde MySQL utiliza el puerto estándar, debe modificarse `Database.java`.

Cambiar:

```java
jdbc:mysql://localhost:3305/empresa
```

por:

```java
jdbc:mysql://localhost:3306/empresa
```

La URL completa sería:

```java
jdbc:mysql://localhost:3306/empresa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Antes de cambiar el puerto se recomienda comprobarlo con:

```sql
SELECT @@port;
```

---

## 9. Problemas encontrados y soluciones

### 9.1 No suitable driver found

Error presentado:

```text
No suitable driver found for jdbc:mysql://localhost:3306/empresa
```

o:

```text
No se encontró el driver JDBC de MySQL.
```

#### Causa

El proyecto no tenía agregado **MySQL Connector/J**.

Java recibía una URL `jdbc:mysql`, pero no encontraba ningún controlador capaz de utilizarla.

#### Solución

Agregar a las librerías del proyecto:

```text
mysql-connector-j-xx.x.x.jar
```

Después ejecutar:

```text
Clean and Build
```

y volver a iniciar Tomcat.

---

### 9.2 Access denied for user

Error presentado:

```text
Access denied for user 'empresa_app'@'localhost'
```

Inicialmente se revisó el usuario y la contraseña.

Sin embargo, el problema principal era que Java estaba intentando conectarse a:

```text
localhost:3306
```

mientras que MySQL Workbench estaba conectado a:

```text
localhost:3305
```

Se comprobó utilizando:

```sql
SELECT @@hostname AS servidor,
       @@port AS puerto,
       USER() AS conexion,
       CURRENT_USER() AS usuario_autenticado;
```

La solución fue cambiar la URL JDBC al puerto correcto.

Si el puerto es correcto pero el error continúa, puede restablecerse la contraseña:

```sql
ALTER USER 'empresa_app'@'localhost'
IDENTIFIED BY 'Empresa123';

FLUSH PRIVILEGES;
```

---

### 9.3 Public Key Retrieval is not allowed

Error presentado:

```text
Public Key Retrieval is not allowed
```

Después de conectarse a la instancia correcta de MySQL apareció este error durante la autenticación.

Para el entorno local de la práctica se agregó a la URL JDBC:

```text
allowPublicKeyRetrieval=true
```

La URL utilizada quedó así:

```java
jdbc:mysql://localhost:3305/empresa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Con esto la conexión pudo completarse correctamente.

> Para un entorno de producción se recomienda utilizar una configuración segura con SSL y credenciales administradas fuera del código fuente.

---

## 10. Resultado esperado

Al iniciar el proyecto se presenta una página con:

```text
Sistema de empleados

Consulta los empleados registrados en la empresa.

Ver empleados
```

Al seleccionar **Ver empleados**, se obtiene la información almacenada en MySQL.

Resultado esperado:

| ID | Nombre | Departamento |
|---:|---|---|
| 1 | Carlos Rodríguez | Ventas |
| 2 | María González | Recursos Humanos |
| 3 | Andrés López | Tecnología |

Esto confirma que la aplicación:

- se conecta correctamente a MySQL;
- utiliza JDBC;
- inserta registros mediante `Statement`;
- ejecuta una consulta `SELECT`;
- recupera datos mediante `ResultSet`;
- envía los datos desde el Servlet hacia la JSP;
- muestra los resultados en una tabla HTML.

---

## 11. Referencias

Almengor Quirós, A. I. (2026). *Java Web Employees MySQL* [Código fuente]. GitHub.  
https://github.com/Andri-Almengor/java-web-employees-mysql

Apache Software Foundation. (s. f.). *Apache NetBeans*.  
https://netbeans.apache.org/

Eclipse Foundation. (2022). *Jakarta Servlet Specification, Version 6.0*.  
https://jakarta.ee/specifications/servlet/6.0/

Oracle. (s. f.). *JDBC basics*.  
https://docs.oracle.com/javase/tutorial/jdbc/basics/

Oracle. (s. f.). *MySQL 8.0 Reference Manual*.  
https://dev.mysql.com/doc/refman/8.0/en/

Oracle. (s. f.). *MySQL Connector/J Developer Guide*.  
https://dev.mysql.com/doc/connector-j/en/
