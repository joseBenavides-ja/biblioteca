# Biblioteca Digital UNTEC

## Descripcion del proyecto
Biblioteca Digital UNTEC es una aplicacion web desarrollada en Java orientada a la gestion basica de libros dentro de una biblioteca. El sistema permite iniciar sesion, registrar libros, controlar stock disponible, realizar prestamos y devoluciones, y visualizar un historial de movimientos.

Este proyecto fue desarrollado aplicando conceptos de aplicaciones web dinamicas en Java, utilizando JSP, Servlets, JDBC, DAO, Maven, Tomcat y base de datos H2.

## Objetivo
Desarrollar una aplicacion web funcional que permita administrar libros de manera ordenada, aplicando arquitectura MVC, acceso a datos con JDBC y despliegue en servidor Apache Tomcat.

## Tecnologias utilizadas
- Java 21
- JSP
- Servlets
- JSTL
- JDBC
- Patron DAO
- Maven
- Apache Tomcat 9
- Base de datos H2
- HTML5
- CSS3

## Funcionalidades principales
- Inicio de sesion de usuario
- Registro de libros
- Serial interno de 7 digitos por libro
- Control de stock total y stock disponible
- Prestamo de libros mediante ventana emergente
- Regreso de libros mediante ventana emergente
- Registro de rut del lector en prestamos
- Registro de rut y observaciones en devoluciones
- Historial de movimientos
- Interfaz visual mejorada

## Estructura general del proyecto
- `config`: clases de conexion e inicializacion de base de datos
- `controller`: servlets principales
- `model/dao`: acceso a datos
- `model/dto`: objetos de transferencia de datos
- `webapp`: vistas JSP, estilos CSS y configuracion web

## Requisitos previos
- Java 21 instalado
- Maven instalado
- Apache Tomcat 9 instalado
- Navegador web
- Sistema operativo compatible con Java, como Linux o Windows

## Ejecucion del proyecto
1. Clonar o descargar el proyecto
2. Abrir la carpeta del proyecto en VS Code o Eclipse
3. Ejecutar el comando:

```bash
mvn clean package