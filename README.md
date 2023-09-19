# dbd_tp2022-23

Trabajo final de Diseño de Bases de Datos 2022/23 - **GRUPO 7**

## Introducción

Esta es una aplicación que expone funcionalidad mediante una API REST, la misma puede ser accedida mediante algún cliente de prueba tal como [Postman](https://www.postman.com/), [JMeter](https://jmeter.apache.org/) o la misma interfaz gráfica expuesta por la aplicación, lo cual se recomienda (ver la sección de [documentación y prueba](#documentación-y-prueba-manual-de-los-endpoints-implementados) para más detalles). La base de datos usada es MySQL 8.0

Para simplificar el desarrollo los tests unitarios son de integración es decir que la aplicación se prueba desde la API REST misma (podría hacerse desde los servicios, pero para evitar cierta duplicación en las pruebas, se prueba directamente desde la capa más externa)

## Requerimientos

- Docker
- Docker compose
- Java 19
- Maven
- git

El desarrollo se hizo bajo Linux (openSUSE 15.4), no se probó en otras plataformas (macOS, Windows), pero debería funcionar sin problemas en ambas.

Nota: En el archivo [pom.xml](pom.xml) se agregó una tarea (ver `Start-up dependant services`) que automáticamente levanta el docker de SQL mediante `docker compose` tanto al ejecutar la aplicación como al ejecutar sus tests unitarios. Todo es automático.

## Instalación y ejecución

```bash
$ git clone -b sql_version git@github.com:jalbiero/dbdtp23_cardpurchases_grupo7.git
$ cd dbdtp23_cardpurchases_grupo7
$ mvn spring-boot:run
```

### Puertos TCP usados

- Aplicación: 
  - **9080** (ej: http://localhost:9080)
- MySQL (via docker container): 
  - **4360**

### Documentación y prueba manual de los endpoints implementados

- Documentación concreta: [CardPurchasesController.java](src/main/java/com/tpdbd/cardpurchases/controllers/CardPurchasesController.java)
- Prueba (requiere la aplicación funcionando): http://localhost:9080/swagger-ui/index.html

## Decisiones de desarrollo

### Iniciales

- Se usa Java 19 con habilitación de "preview features" para usar funcionalidad nueva de _pattern matching_, específicamente _switch_ para _instanceof_ (ej: ver [ResponseDTO.java](src/main/java/com/tpdbd/cardpurchases/dto/ResponseDTO.java))
- Se actualizaron tipos de datos discontinuados tales como:
  - Anotaciones JPA: En los ejemplos prácticos se usa `javax.persistence.*`, en este trabajo se usa su actualzación `jakarta.persistence.*`
  - Fecha: `java.util.Date` a `java.time.LocalDate`
- Por cuestiones de claridad las siguientes clases fueron renombradas (ya que al representar compras se confundían con los pagos de las mismas)
  - `CashPayment` a `CashPurchase`
  - `MonthlyPayment` a `CreditPurchase`

### Controladores y servicos

Para aislar la funcionalidad pedida de lo que se necesita para probarla se decidió dividir las capas de controladores y servicios en 2 partes:

1. El controlador [CardPurchasesController](src/main/java/com/tpdbd/cardpurchases/controllers/CardPurchasesController.java) y su servicio asociado [CardPurchasesService](src/main/java/com/tpdbd/cardpurchases/services/CardPurchasesService.java) implementan solamente lo que se pide como tarea (sus _tests_ unitarios asociados están en [CardPurchasesControllerTests](src/test/java/com/tpdbd/cardpurchases/CardPurchasesControllerTests.java).
2. El controlador [TestController](src/main/java/com/tpdbd/cardpurchases/controllers/TestController.java) y su servicio asociado [TestService](src/main/java/com/tpdbd/cardpurchases/services/TestService.java) (_tests_ unitarios asociados en [TestControllerTests](src/test/java/com/tpdbd/cardpurchases/TestControllerTests.java)) implementan funcionalidad necesaria para probar lo pedido en la tarea. En una aplicación completa lo pedido sería sólo una parte del total, el cual se complementaría con lo que está en `TestController/TestService`.
3. Por cuestiones de tiempo la documentación de los _endpoints_ se hace solamente para el controlador `CardPurchasesController` de una forma sencilla agregándose además [swagger para listar/probar los mismos en _runtime_](#documentación-y-prueba-manual-de-los-endpoints-implementados) como se comentó anteriormente.

### Pruebas

Para las pruebas (tanto manuales con la aplicación funcionando, como para los _tests_ unitarios) se diseño un servicio ([TestDataGeneratorService](src/main/java/com/tpdbd/cardpurchases/services/TestDataGeneratorService.java)) que se ejecuta al arrancar la aplicación. La funcionalidad del mismo es generar datos de prueba (lo más real posibles) en la base de datos. Inicialmente se evaluó la opción de tener un archivo .sql, pero no iba a escalar ya que:
   
   1. Era muy dependiente de la estructura de datos generada por JPA (un cambio en las anotaciones y la estructura difiere)
   2. Iba a ser necesario tener un archivo similar para Mongo.

En conclusión: El código del servicio trabaja con las entidades del modelo por lo cual es independiente de la base de datos subyacente.

La generación de datos se controla mediante el [archivo de propiedades de la aplicación](src/main/resources/application.properties). Por simplicidad, sobre todo para ciertos tests, y a menos que esas propiedades sean modificadas, los datos generados en cada corrida de la aplicación, o de los _test_ unitarios, van a ser siempre los mismos.

Los _tests_ unitarios se ejecutan con:

```bash
$ mvn test
```

### Modelo

En el modelo se tomaron las siguientes decisiones:

- Se anotó cada atributo con propiedades básicas tales como:
  - Si puede ser nulo o no.
  - Longitud máxima de caracteres en caso de las cadenas.
  - Unicidad en los que se requiera (DNI, CUIT, etc)
- En el caso de colecciones que se mapean a tablas:
  -  se usó `@JoinTable` (además de por ejemplo `@OneToMany`) para simplificar la generación del modelo en la base. Sin `@JoinTable` se generan tablas extras intermedias que no son óptimas desde el punto de vista del rendimiento.
  -  Se usó además el valor por defecto para el _fetch_ (LAZY) y para las operaciones de cascada (desabilitado) ya que no se tuvo necesidad de activar las mismas.
- En cuanto a la herencia: Hay 2 grupos de clases que las usan, `Purchase` con _CashPurchase_ y _CreditPurchase_, y `Promotion` con _Financing_ y _Discount_. En ambos caso se decidió usar una estrategia de tipo `InheritanceType.SINGLE_TABLE`, ya que es la que mayor rendimiento tiene. Su único punto en contra es que en las clases derivadas los atributos deben ser _nullable_. 

### Otros

- En la clase `Quota`, por conveniencia, se cambiaron los tipos de datos de los attributos `month`y `year`, ambos originalmente `String` a `int`
- Con respecto a los DTO:
  - El _mapper_ más simple y directo de usar es en mi opinión "modelmapper", pero lamentablemente no soporta objetos DTO basados en _records_ (los cuales son muy sencillos de definir y usar)
  -  Por lo dicho anteriomente, opté por hacer el mapeo de los objetos basados en entidades a DTO de manera manual (agregando un par de métodos estáticos a los DTO para la conversión entre y hace entidades del modelo).
  -  Hay mucha discusión sobre en qué capa usar los DTOs, muchos a favor (y con cierta razón) que los mismos deben usarse en la capa de transporte, es decir en la de los controladores. El problema es que en esa capa muchas veces no se cuenta con datos que el servicio posee para generar correctamente el DTO. Por lo anterior tomé la decisión de que la capa de servicio devuelva resultados directamente en DTO para la capa de controladores.
  