# dbd_tp2022-23

Trabajo final de Diseño de Bases de Datos 2022/23 - **GRUPO 7** (versión Mongo DB)

## Introducción 

Esta es una aplicación que expone funcionalidad mediante una API REST, la misma puede ser accedida mediante algún cliente de prueba tal como [Postman](https://www.postman.com/), [JMeter](https://jmeter.apache.org/) o la misma interfaz gráfica expuesta por la aplicación, lo cual se recomienda (ver la sección de [documentación y prueba](#documentación-y-prueba-manual-de-los-endpoints-implementados) para más detalles). La base de datos usada es Mongo 1.6.0

Para simplificar el desarrollo los tests unitarios son de integración es decir que la aplicación se prueba desde la API REST misma (podría hacerse desde los servicios, pero para evitar cierta duplicación en las pruebas, se prueba directamente desde la capa más externa)

## Requerimientos

- Docker
- Docker compose
- Java 19
- Maven
- git

El desarrollo se hizo bajo Linux (openSUSE 15.4), no se probó en otras plataformas (macOS, Windows), pero debería funcionar sin problemas en ambas.

Nota: En el archivo [pom.xml](pom.xml) se agregaron 2 tareas:

1. `Start-up dependant services`: Automáticamente levanta el docker de Mongo mediante `docker compose` tanto al ejecutar la aplicación como al ejecutar sus tests unitarios.
2. `Drop mongo database at startup`: Limpia la base de Mongo, tanto al arrancar la aplicación como al ejecutar los tests unitarios.

## Instalación y ejecución

```bash
$ git clone -b mongo_version git@github.com:jalbiero/dbdtp23_cardpurchases_grupo7.git
$ cd dbdtp23_cardpurchases_grupo7
$ mvn spring-boot:run
```

### Puertos TCP usados

- Aplicación: 
  - **9080** (ej: http://localhost:9080)
- Mongo DB (via docker container): 
  - **27017**

### Documentación y prueba manual de los endpoints implementados

- Documentación concreta: [CardPurchasesController.java](src/main/java/com/tpdbd/cardpurchases/controllers/CardPurchasesController.java)
- Prueba (requiere la aplicación funcionando): http://localhost:9080/swagger-ui/index.html

## Decisiones de desarrollo

### Iniciales

- Se usa Java 19 con habilitación de "preview features" para usar funcionalidad nueva de _pattern matching_, específicamente _switch_ para _instanceof_ (ej: ver [ResponseDTO.java](src/main/java/com/tpdbd/cardpurchases/dto/ResponseDTO.java))
- Se actualizaron tipos de datos discontinuados tales como:
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

### Modelo (Mongo)

En el modelo se tomaron las siguientes decisiones:

- Se optó por no renombrar cada clase documento para mantener la paridad con la versión SQL. Ej: 

  ```java
  @Document // << esto genera un documento llamado "bank"
  class Bank {
    // ...
  }
  ```

  En vez de usar una versión renombrada en plural que sería algo más natural:

  ```java
  @Document(collection = "banks")
  class Bank {
    // ...
  }
  ```

- Al ser Mongo una base de datos NoSQL, las cuales se caracterizan en general por falta de esquema, no se restringió la cantidad de caracteres en los campos de tipo cadena. Además, si se quisiera, la restricción tendría que hacerse sobre la capa de Java, no a nivel base de datos como en el caso de SQL. Es decir que habría que controlar que cada _setter_ de las clases del modelo validen las dimensiones de lo que reciben. Este tipo de restricciones va en contra de la filosofía de una base NoSQL (hablando en general por supuesto).
- Los campos que debían ser únicos se indexaron mediante `@Indexed(unique = true)`
- Con respecto a la herencia se dejó que Spring Boot para Mongo maneje la misma por defecto, es decir que se marcó tanto las clases bases como las derivadas con su respectivas anotaciones `@Document`. Esto genera una única colección con el nombre de la clase base agregando además un campo `_class` donde se anota de que tipo de clase derivada es el documento almacenando. Ejemplo para el caso de las promociones: Habrá una coleccción `promotion` donde el documento almacenado tendrá algo como lo siguiente para distinguir entre los tipos:

  ```json
  [
    {
      "foo": "foo value",
      "_class": "com.tpdbd.cardpurchases.model.Discount"
    },
    {
      "bar": "bar value",
      "_class": "com.tpdbd.cardpurchases.model.Financing"
    }
  ]
  ```
- Para los casos de relaciones 1-n, se optó por usar la anotación `@DocumentReference(lazy = true)`. Ej:

  ```java
  @Document
  class CardHolder {
      @DocumentReference(lazy = true)
      private List<Card> cards;
  }
  ```
- Para relaciones 1-1 se optó, dependiendo del caso, por relacionar 2 documentos (con carga por defecto, es decir `lazy = false`) o por embeber uno en el otro. Este último caso es notorio para simplificar consultas complejas de agregación (ver `PurchaseRepository` o `QuotaRepository`). Para las mismas se evaluó que el documento incrustado sea sencillo y que la posibilidad de ser modificado sea poco probable. En casos que esto no fuera así se usó directamente una referencia a otro documento y se fue a la base en una segunda instancia a buscar el mismo (ver `PaymentRepository` donde en vez de entregar el documento de un banco (como se hacía con la versión SQL) se entrega sólo su ID. Con dicho ID, el servicio `CardPurchasesService.banksGetTheOneWithMostPaymentValues` va a la base a traer los datos del banco en cuestión). 

### Lecciones aprendidas y errores detectados

A medida que fui modificando la versión SQL para transformarla en Mongo me fui dando cuenta de algunas cosas. Las mismas las anoté en un documento aparte [Errores detectados al migrar a Mongo.md](doc/Errores%20detectados%20al%20migrar%20a%20Mongo.md)

### Otros

- En la clase `Quota`, por conveniencia, se cambiaron los tipos de datos de los attributos `month`y `year`, ambos originalmente `String` a `int`
- Con respecto a los DTO:
  - El _mapper_ más simple y directo de usar es en mi opinión "modelmapper", pero lamentablemente no soporta objetos DTO basados en _records_ (los cuales son muy sencillos de definir y usar)
  -  Por lo dicho anteriomente, opté por hacer el mapeo de los objetos basados en entidades a DTO de manera manual (agregando un par de métodos estáticos a los DTO para la conversión entre y hace entidades del modelo).
  -  Hay mucha discusión sobre en qué capa usar los DTOs, muchos a favor (y con cierta razón) que los mismos deben usarse en la capa de transporte, es decir en la de los controladores. El problema es que en esa capa muchas veces no se cuenta con datos que el servicio posee para generar correctamente el DTO. Por lo anterior tomé la decisión de que la capa de servicio devuelva resultados directamente en DTO para la capa de controladores.
  