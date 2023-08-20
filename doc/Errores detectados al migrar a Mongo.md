# Errores detectados al migrar a Mongo

Se listan a continuación errores detectados al migrar de la versión base de SQL a la versión Mongo

1. Esto es un error de diseño por desconocimiento y es más una lección aprendida: El ID de una entidad debería haber sido de tipo `String` (con un hash auto generado). Esto habría simplificado la migración a Mongo dónde los IDs son si o si de tipo `String`
2. Hay varios _endpoints_ (de test) que no están usando DTOs sin simplemente un map en dónde existe un propiedad "ids". Si tengo tiempo voy a generar un DTO para estos casos.
3. El método `findAllIds` de un repositorio se puede eliminar (a costa de una posible pérdida de rendimiento que no medí). En vez de tener dicho método con una _query_ asociada para traer de la base sólo los campos necesarios (el _id_ en este caso), se puede usar el método `findAll` desde el servicio asociado y mapear la colección obtenida para seleccionar solamente el campo de interés. Esto facilita la migración a Mongo ya que `findAllIds` tiene una query HQL que no es compatible con Mongo.
4. Hay muchas colecciones no inicializadas a una lista vacía en el constructor (como Bank.cards, Card.purchases, CardHolder.cards). 
5. El generador de datos no genera bien las quotas para compras de tipo `Cash`. siempre debería generar una única cuota, pero en ocaciones ajusta el número de cuotas dependiendo del vencimiento (algo que se tiene que hacer para compras de tipo `Credit`, pero no para `Cash`)
6. El generador de datos no estaba generando un valor aleatorio de tarjetas por usuario, sino el valor máximo fijado para el valor aleatorio. Ej: en vez de generar entre 1 y 3 tajetas, generaba directamente 3 tarjetas.
7. Se mejora el test `CardPurchasesControllerTests.testPurchasesCreditGetTotalPriceHappyPath` ya que el mismo tenía _hardcodeado_ un ID de compra a crédito. Ahora, mediante un nuevo _endpoint_ de prueba se busca una compra cualquiera a crédito para ser usada. El hecho de que los IDs en Mongo sean cadenas hash y no enteros auto numéricos generados, hace imposible tener un ID _hardcodeado_ ya que los mismos cambian en cada ejecución.
8. Se mejora/simplifica/extiende el test `CardPurchasesControllerTests.testStoresGetAvailblePromotions` (fue reemplazado por `testStoresGetAvailblePromotionsHappyPath` y `testStoresGetAvailblePromotionsNotFound`)
