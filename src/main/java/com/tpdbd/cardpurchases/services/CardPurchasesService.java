package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.model.Card;
import com.tpdbd.cardpurchases.model.Purchase;


public interface CardPurchasesService {
    // 1 Agregar una nueva promoción de tipo descuento a un banco dado
    void banksAddDiscountPromotion(String cuit, RequestDTO.Discount discount);

    // 2 Editar las fecha de vencimiento de un pago con cierto código.
    void paymentsUpdateDates(String code, LocalDate firstExpiration, LocalDate secondExpiration);

    // 3 Generar el total de pago de un mes dado, informando las compras
    // correspondientes

    // TODO ¿Total de pago por tarjeta o por usuario o por banco o global a todo?

    // 4 Obtener el listado de tarjetas que vencen en los siguientes 30 días.
    List<Card> cardsGetSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate);

    // 5 Obtener la información de una compra, incluyendo el listado de cuotas si
    // esta posee.
    // TODO ¿Cómo identificar una compra exactamente? Lo normal sería usar la
    //      tarjeta, el comercio y la fecha, pero hay 2 problemas:
    //         1- Igualmente podria haber más de una compra en el mismo día
    //         2- El modelo no tiene fecha para una compra
    //      En base a lo anterior, por el momento se tratará de identificar compras 
    //      usando solo la tarjeta y el comercio.
    List<Purchase> getPurchases(String cuitStore, String cardNumber);

    // 6 Eliminar una promoción a traves de su código (tener en cuenta que esta
    // puede haber sido aplicada alguna compra)

    // TODO ¿Debo eliminar en cascada o no eliminar directamente?

    // 7 Obtener el precio total a pagar de una compra en cuotas (tener en cuenta
    // que pueden existir promociones aplicadas)

    // 8 Obtener el listado de las promociones disponibles de un local entre dos
    // fechas

    // 9 Obtener los titulares de las 10 tarjetas con más compras.

    // 10 Obtener la promoción mas utilizada en las compras registradas

    // 11 Obtener el nombre y cuit del local, que mas facturo en cierto mes

    // 12 Obtener el banco que registre la mayor sumatoria de los importes en pagos
    // con su tarjeta.
}
