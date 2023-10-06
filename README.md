# dbd_tp2022-23

Trabajo final de Diseño de Bases de Datos 2022/23 - **GRUPO 7** (versión Mongo DB)

- [dbd_tp2022-23](#dbd_tp2022-23)
  - [Introducción](#introducción)
  - [Requerimientos](#requerimientos)
  - [Instalación y ejecución](#instalación-y-ejecución)
    - [Puertos TCP usados](#puertos-tcp-usados)
    - [Documentación y prueba manual de los endpoints implementados](#documentación-y-prueba-manual-de-los-endpoints-implementados)
  - [Decisiones de desarrollo](#decisiones-de-desarrollo)
    - [Iniciales](#iniciales)
    - [Controladores y servicos](#controladores-y-servicos)
    - [Pruebas](#pruebas)
    - [Modelo (Mongo)](#modelo-mongo)
    - [Errores detectados (y lecciones aprendidas)](#errores-detectados-y-lecciones-aprendidas)
    - [Otros](#otros)
  - [Conclusión](#conclusión)

## Introducción

Esta es una aplicación que expone funcionalidad mediante una API REST, la misma puede ser accedida mediante algún cliente de prueba tal como [Postman](https://www.postman.com/), [JMeter](https://jmeter.apache.org/) o la misma interfaz gráfica expuesta por la aplicación, lo cual se recomienda (ver la sección de [documentación y prueba](#documentación-y-prueba-manual-de-los-endpoints-implementados) para más detalles). La base de datos usada es Mongo 1.6.0

Para simplificar el desarrollo, los tests unitarios son de integración es decir que la aplicación se prueba desde la API REST misma (podría hacerse desde los servicios, pero para evitar cierta duplicación en las pruebas, se prueba directamente desde la capa más externa)

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

Para las pruebas (tanto manuales con la aplicación funcionando, como para los _tests_ unitarios) se diseño un servicio ([TestDataGeneratorService](src/main/java/com/tpdbd/cardpurchases/services/TestDataGeneratorService.java)) que se ejecuta al arrancar la aplicación. La funcionalidad del mismo es generar datos de prueba (lo más real posibles) en la base de datos. Inicialmente se evaluó la opción de tener un archivo datos (ej: .sql), pero no iba a escalar ya que:

   1. Era muy dependiente de la estructura de datos generada por JPA/Mongo (un cambio en las anotaciones y la estructura difiere)
   2. Iba a ser necesario tener un archivo similar para Mongo.
   3. Los datos no son triviales, hay muchas relaciones entre ellos, como para plasmarlos en un archivo "plano" a ser insertado en la base.

En conclusión: El código del servicio trabaja con las entidades del modelo por lo cual es independiente de la base de datos subyacente.

La generación de datos se controla mediante el [archivo de propiedades de la aplicación](src/main/resources/application.properties). Por simplicidad, sobre todo para ciertos tests, y a menos que esas propiedades sean modificadas, los datos generados en cada corrida de la aplicación, o de los _test_ unitarios, van a ser siempre los mismos.

Los _tests_ unitarios se ejecutan con:

```bash
$ mvn test
```

### Modelo (Mongo)

En el modelo se tomaron las siguientes decisiones:

- Se optó por no renombrar cada clase documento (*) (a un valor en plural) para mantener la paridad con la versión SQL. Ej:

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

  (*) Sólo en los casos en que se usa herencia hubo que especificar el nombre de la colección para evitar usar una diferente a la de la clase base.

- Al ser Mongo una base de datos NoSQL, las cuales se caracterizan en general por falta de esquema, no se restringió la cantidad de caracteres en los campos de tipo cadena. Además, si se quisiera, la restricción tendría que hacerse sobre la capa de Java, no a nivel base de datos como en el caso de SQL ya que en Mongo no lo soporta (hasta donde yo sé). Es decir que habría que controlar que cada _setter_ de las clases del modelo validen las dimensiones de lo que reciben. Este tipo de restricciones va en contra de la filosofía de una base NoSQL (hablando en general por supuesto).
- Los campos que debían ser únicos se indexaron mediante `@Indexed(unique = true)`
- Con respecto a la herencia se implementó mediante una única colección de documentos en donde cada documento tiene un atributo que especifica de que subtipo es. Para más información sobre la técnica implementada ver [acá](https://www.mongodb.com/developer/languages/java/java-single-collection-springpart2/). Lamentablemente los repositorios de Spring para Mongo implementan una solución parcial para el tema de la herencia ya que hay que subclasificar el repositorio base y agregar el pertinente filtro sbore el atributo para traer los documentos del tipo requerido (ver `PurchaseRepository`, `CashRepository` y `CreditRepository` para más detalles).
- Para los casos de relaciones 1-n, se optó por usar la anotación `@DocumentReference(lazy = true)`. Ej:

  ```java
  @Document
  class CardHolder {
      @DocumentReference(lazy = true)
      private List<Card> cards;
  }
  ```

- Para relaciones 1-1 se optó, dependiendo del caso, por relacionar 2 documentos (con carga por defecto, es decir `lazy = false`) o por embeber uno en el otro. Este último caso es notorio para simplificar consultas complejas de agregación (ver `PurchaseRepository` o `QuotaRepository`). Para las mismas se evaluó que el documento incrustado sea sencillo y que la posibilidad de ser modificado sea poco probable. En casos que esto no fuera así se usó directamente una referencia a otro documento y se fue a la base en una segunda instancia a buscar el mismo (ver `PaymentRepository` donde en vez de entregar el documento de un banco (como se hacía con la versión SQL) se entrega sólo su ID. Con dicho ID, el servicio `CardPurchasesService.banksGetTheOneWithMostPaymentValues` va a la base a traer los datos del banco en cuestión).
- Se implementa de manera un poco más manual el borrado lógico de promociones. En la versión SQL había una anotación (`@Where`) que a cada query le inyectaba el filtro para quitar los registros marcadas con `deleted = true`. En Mongo esa anotación no existe por lo que tuve que agregar dicho filtro a cada método presente en el repositorio de promociones.

### Errores detectados (y lecciones aprendidas)

A medida que fui modificando la versión SQL para transformarla en Mongo me fui dando cuenta de algunos problemas. Los mismos los anoté en un documento aparte [Errores detectados al migrar a Mongo.md](doc/Errores%20detectados%20al%20migrar%20a%20Mongo.md)

### Otros

- En la clase `Quota`, por conveniencia, se cambiaron los tipos de datos de los attributos `month`y `year`, ambos originalmente `String` a `int`
- Con respecto a los DTO:
  - El _mapper_ más simple y directo de usar es en mi opinión "modelmapper", pero lamentablemente no soporta objetos DTO basados en _records_ (los cuales son muy sencillos de definir y usar)
  -  Por lo dicho anteriomente, opté por hacer el mapeo de los objetos basados en entidades a DTO de manera manual (agregando un par de métodos estáticos a los DTO para la conversión entre y hace entidades del modelo).
  -  Hay mucha discusión sobre en qué capa usar los DTOs, muchos a favor (y con cierta razón) que los mismos deben usarse en la capa de transporte, es decir en la de los controladores. El problema es que en esa capa muchas veces no se cuenta con datos que el servicio posee para generar correctamente el DTO. Por lo anterior tomé la decisión de que la capa de servicio devuelva resultados directamente en DTO para la capa de controladores.

## Conclusión

**Nota:** Esta sección es la misma en SQL y Mongo.

Más allá de las conocidas diferencias entre base de datos SQL (relacionales) y bases NoSQL [^1] en las que se puede ver que ambos tipos presentan fortalezas y debilidades, lo importante, desde mi punto de vista, es entender que ambas se pueden complementar perfectamente en un mismo sistema. A continuación una experiencia personal:

Uno de mis últimos trabajos fue una aplicación de citas (que todavía sigue en el mercado), en la cual en su _backend_ actualmente se usan ambos tipos de base de datos.

Originalmente se empezó con una base de datos relacional (MySQL). Hasta el día de hoy dicha base contiene toda la información de los usuarios. La misma está estructurada en una serie de tablas donde lo importante es la consistencia y validación de los datos. Inicialmente la aplicación funcionaba bien (en lo que a velocidad respecta) debido a la poca cantidad de usuarios, así como también a cierta funcionalidad limitada.

Con el paso del tiempo, los usuarios fueron aumentando junto con las nuevas características ideadas por el área de _marketing_ y las relevadas por el aŕea de investigación de mercado. Dichas características incluían filtrar usuarios por todo tipo de atributos, tener filtros reversos (evitar que ciertos usuarios vean "mi" perfil), bloquear usuarios en particular, ranquear a los usuarios que se visualizan en un perfil en base a muchas condiciones tales como tiempo de conexión, completitud del perfil, ubicación geográfica, etc, etc. Todo lo anterior terminaba siendo implementado en consultas SQL que crecían en tamaño y complejidad (eran complicadas de mantener o mejorar), y además, como si fuera poco, se iban haciendo cada vez más lentas (hay que tener en cuenta que en una aplicación móvil el usuario tiene la adictiva opción, que la usa todo el tiempo, de refrescar los datos que ve en pantalla con un simple gesto ([_pull to refresh_](https://en.wikipedia.org/wiki/Pull-to-refresh)) por lo tanto dicha falta de velocidad era cada vez más notable.

Ante esta situación se optó en un momento dado por mantener una vista materializada del perfil de cada usuario en una base de datos NoSQL (Elasticsearch para ser más exacto). Dicha vista era una simple colección de atributos del usuario, tanto reales como meta datos asociados para simplificar las consultas. Esto mejoró notablemente la velocidad, además de simplificar las consultas en si. Por supuesto que aparecieron escenarios de consistencia eventual, pero no fueron un problema en absoluto. Ej: un típico caso es aquel en donde un usuario actualiza su perfil en la base SQL. Esta actualización dispara un proceso que inyecta dichos datos actualizados en Elasticsearch además de generar una reindexación de los mismos. Esto lleva tiempo, por lo cual otros usuarios pueden ver el perfil "desactualizado" por cierto lapso, lo cual no es un problema ya que no saben cuando el otro usuario realmente cambió sus datos. Es como ver un evento deportivo "en vivo", pero por _streaming_, mientras no haya otra fuente de verdad sobre el "vivo", el _delay_ del _streaming_ no es percibido; lo mismo pasa en la aplicación.

En definitiva, no es una base o la otra, pueden ser ambas. La clave es conocer en que escenarios puede una base desempeñarse mejor, para, llegado el caso, usar solamente una de ellas o combinar ambas.

[^1]: ACID vs BASE, escalabilidad vertical vs horizontal, con esquema vs sin esquema (o esquema flexible), etc.
