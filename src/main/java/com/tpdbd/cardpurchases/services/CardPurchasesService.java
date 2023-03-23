package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Discount;

public interface CardPurchasesService {
    // Test services
    List<String> testsBanksGetCuits();
    Bank testsBanksGetBank(String cuit);

    // 1 Agregar una nueva promoción de tipo descuento a un banco dado
    void banksAddDiscountPromotion(String cuit, Discount discount);

    // 2 Editar las fecha de vencimiento de un pago con cierto código.

    // 3 Generar el total de pago de un mes dado, informando las compras
    // correspondientes

    // 4 Obtener el listado de tarjetas que vencen en los siguientes 30 días.

    // 5 Obtener la información de una compra, incluyendo el listado de cuotas si
    // esta posee.

    // 6 Eliminar una promoción a traves de su código (tener en cuenta que esta
    // puede haber sido aplicada alguna compra)

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
