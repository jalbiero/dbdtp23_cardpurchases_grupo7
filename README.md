# dbd_tp2022-23
Trabajo final de Diseño de Bases de Datos 2022/23 - **GRUPO 7**

## Introducción

Esta es una aplicación que expone funcionalidad mediante una API REST, la misma puede ser accedida mediante algún cliente de prueba tal como [Postman](https://www.postman.com/) o [JMeter](https://jmeter.apache.org/). La base de datos usada es MySQL 8.0

Para simplificar el desarrollo los tests unitarios son de integración es decir que la aplicación se prueba desde la API REST misma (podría hacerse desde los servicios, pero para evitar cierta duplicación en las pruebas, se prueba directamente desde la capa más externa)

## Requerimientos

- Desarrollo
  - Docker
  - Docker compose (ver [pom.xml](pom.xml) para más detalles)
  - Java 19
  - Maven
- Sólo ejecución (TODO)
  - Docker
  - Docker compose

El desarrollo se hizo bajo Linux (openSUSE 15.4), no se probó en otras plataformas (macOS, Windows), pero debería funcionar sin problemas en ambas.

## Instalación y Ejecución (en modo desarrollo)

```bash
$ git clone -b sql_version git@github.com:jalbiero/dbdtp23_cardpurchases_grupo7.git
$ cd dbdtp23_cardpurchases
$ mvn spring-boot:run
```

### Puertos TCP usados

- Aplicación: 
  - **9080** (ej: http://localhost:9080)
- MySQL (via docker container): 
  - **4360**


## Decisiones de desarrollo

- Se actualizaron tipos de datos discontinuados tales como:
  - Anotaciones JPA: En los ejemplos prácticos se usa `javax.persistence.*`, en este trabajo se usa su actualzación `jakarta.persistence.*`
  - Fecha: `java.util.Date` a `java.time.LocalDate`
- Por cuestiones de claridad las siguientes clases fueron renombradas (ya que al representar compras se confundían con los pagos de las mismas)
  - `CashPayment` a `CashPurchase`
  - `MonthlyPayment` a `CreditPurchase`
- Con respecto a los controladores y a los servicios:
  - Para aislar la funcionalidad pedida de lo que se necesita para probarla se decidió dividir las capas de controladores y servicios en 2 partes:
    1. El controlador `CardPurchasesController` y su servicio asociado `CardPurchasesService` implementan solamente lo que se pide como tarea.
    2. El controlador `TestController` y su servicio asociado `TestService` implementan funcionalidad necesaria para probar lo pedido en la tarea. En una aplicación completa lo pedido sería sólo una parte del total, el cual se complementaría con lo que está en `TestController/TestService`.
- En la clase `Quota`, por conveniencia, se cambiaron los tipos de datos de los attributos `month`y `year`, ambos originalmente `String` a `int`
- Con respecto a los DTO:
  - El _mapper_ más simple y directo de usar es en mi opinión "modelmapper", pero lamentablemente no soporta objetos DTO basados en _records_ (los cuales son muy sencillos de definir y usar)
  -  Por lo dicho anteriomente, opté por hacer el mapeo de los objetos basados en entidades a DTO, en los controladores de manera manual.

- TODO 
  