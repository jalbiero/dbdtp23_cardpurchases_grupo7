# dbd_tp2022-23
Trabajo final de Diseño de Bases de Datos 2022/23 - **GRUPO 7**

## Introducción

Esta es una aplicación que expone funcionalidad mediante una API REST, la misma puede ser accedida mediante algún cliente de prueba tal como [Postman](https://www.postman.com/) o [JMeter](https://jmeter.apache.org/). La base de datos usada es MySQL 8.0

Para simplificar el desarrollo los tests unitarios son de integración es decir que la aplicación se prueba desde la API REST misma (podría hacerse desde los servicios, pero para evitar cierta duplicación en las pruebas, se prueba directamente desde la capa más externa)

## Requerimientos

- Desarrollo
  - Docker
  - Docker compose
  - Java 19
  - Maven
  - curl (*)
- Sólo ejecución (TODO)
  - Docker
  - Docker compose

(*) El desarrollo se hizo bajo Linux (openSUSE 15.4), no se probó en otras plataformas (macOS, Windows), pero debería funcionar sin problemas con una única salvedad en Windows: Al ejecutar la aplicación, docker compose se corre automáticamente desde maven para levantar el docker de MySQL, el cual es validado mediante el comando _curl_ que no está disponible en Windows a menos que sea instalado. El instalador se puede descargar desde [acá](https://curl.se/windows/)


## Instalación y Ejecución (en modo desarrollo)

```bash
$ git clone -b sql_version git@github.com:jalbiero/dbdtp23_cardpurchases.git
$ cd dbdtp23_cardpurchases
$ mvn spring-boot:run
```

## Decisiones de desarrollo

TODO