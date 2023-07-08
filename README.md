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

### Iniciales

- Se usa Java 19 con habilitación de "preview features" para usar funcionalidad nueva de _pattern matching_, específicamente _switch_ para _instanceof_ (ver [ResponseDTO.java](src/main/java/com/tpdbd/cardpurchases/dto/ResponseDTO.java))
- Se actualizaron tipos de datos discontinuados tales como:
  - Anotaciones JPA: En los ejemplos prácticos se usa `javax.persistence.*`, en este trabajo se usa su actualzación `jakarta.persistence.*`
  - Fecha: `java.util.Date` a `java.time.LocalDate`
- Por cuestiones de claridad las siguientes clases fueron renombradas (ya que al representar compras se confundían con los pagos de las mismas)
  - `CashPayment` a `CashPurchase`
  - `MonthlyPayment` a `CreditPurchase`

### Controladores y servicos

Para aislar la funcionalidad pedida de lo que se necesita para probarla se decidió dividir las capas de controladores y servicios en 2 partes:

1. El controlador `CardPurchasesController` y su servicio asociado `CardPurchasesService` implementan solamente lo que se pide como tarea.
2. El controlador `TestController` y su servicio asociado `TestService` implementan funcionalidad necesaria para probar lo pedido en la tarea. En una aplicación completa lo pedido sería sólo una parte del total, el cual se complementaría con lo que está en `TestController/TestService`.
3. Por cuestiones de tiempo la documentación de los _endpoints_ se hace solamente para el `CardPurchasesController` de una forma sencilla (no se usa swagger por ejemplo). Para más detalles ir directamente al archivo [CardPurchasesController.java](src/main/java/com/tpdbd/cardpurchases/controllers/CardPurchasesController.java)

### Entidades

¿Cómo identificar unívocamente las entidades? En aquellas en los que hubiera un campo que permita hacerlo (CUIT, DNI, Number, etc) se optó por usar los mismos (ej: `Bank`, `CardHolder`, `Card`). En los que no, se usa directamente el ID autogenerado (ej: `Purchases`)

### Otros

- En la clase `Quota`, por conveniencia, se cambiaron los tipos de datos de los attributos `month`y `year`, ambos originalmente `String` a `int`
- Con respecto a los DTO:
  - El _mapper_ más simple y directo de usar es en mi opinión "modelmapper", pero lamentablemente no soporta objetos DTO basados en _records_ (los cuales son muy sencillos de definir y usar)
  -  Por lo dicho anteriomente, opté por hacer el mapeo de los objetos basados en entidades a DTO de manera manual (agregando un para de métodos estáticos a los DTO para la conversión entre y hace entidades del modelo).
  -  Hay mucha discusión sobre en qué capa usar los DTOs, muchos a favor (y con cierta razón) que los mismos deben usarse en la capa de transporte, es decir en la de los controladores. El problema es que en esa capa muchas veces no se cuenta con datos que el servicio posee para generar correctamente el DTO. Por lo anterior tomé la decisión de que la capa de servicio devuelva resultados directamente en DTO para la capa de controladores.

- TODO 
  