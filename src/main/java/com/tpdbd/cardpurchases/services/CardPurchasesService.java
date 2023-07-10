package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import com.tpdbd.cardpurchases.dto.RequestDTO;
import com.tpdbd.cardpurchases.dto.ResponseDTO;

public interface CardPurchasesService {
    // 1 Agregar una nueva promoción de tipo descuento a un banco dado
    void banksAddDiscountPromotion(String cuit, RequestDTO.Discount discount);

    // 2 Editar las fecha de vencimiento de un pago con cierto código.
    void paymentsUpdateDates(String code, LocalDate firstExpiration, LocalDate secondExpiration);

    // 3 Generar el total de pago de un mes dado, informando las compras
    // correspondientes
    ResponseDTO.MonthlyPayment cardsGetMonthtlyPayment(String cardNumber, int year, int month);
  
    // 4 Obtener el listado de tarjetas que vencen en los siguientes 30 días.
    // > Para mayor flexibilidad y facilidad de prueba, se optó por generalizar 
    // > este servicio (es decir posibilidad de pasar la fecha y los días de vencimiento) 
    // > como parámetro.
    List<ResponseDTO.Card> cardsGetSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate);

    // 5 Obtener la información de una compra, incluyendo el listado de cuotas si
    // esta posee.
    ResponseDTO.Purchase purchasesGetInfo(long purchaseId);

    // 6 Eliminar una promoción a traves de su código (tener en cuenta que esta
    // puede haber sido aplicada alguna compra)
    void promotionsDelete(String code);

    // 7 Obtener el precio total a pagar de una compra en cuotas (tener en cuenta
    // que pueden existir promociones aplicadas)
    //
    // > No entiendo la aclaración (me confunde), se supone que el total calculado para una 
    // > compra (sea de contado, en cuotas, con o sin promociones) ya está asentado en la 
    // > base de datos mediante Purchase.finalAmount. Para más detalles ver lo siguiente:
    // >    - TestDataGeneratorService.generateCashPurchases
    // >    - TestDataGeneratorService.generateCreditPurchases
    // > Dicho lo anterior, este servicio no tiene mucho sentido ya que consultando una
    // > compra con 'purchasesGetInfo' se obtiene el valor de 'finalAmount'
    float purchasesGetTotalPrice(long purchaseId);

    // 8 Obtener el listado de las promociones disponibles de un local entre dos
    // fechas
    List<ResponseDTO.Promotion> storesGetAvailblePromotions(String cuitStore, LocalDate from, LocalDate to);

    // 9 Obtener los titulares de las 10 tarjetas con más compras.
    // > Asumo que es total, no de un período específico
    List<ResponseDTO.PurchaserCardHolder> cardsGetTop10Purchasers();

    // 10 Obtener la promoción mas utilizada en las compras registradas
    ResponseDTO.Promotion promotionsGetTheMostUsed();

    // 11 Obtener el nombre y cuit del local, que mas facturo en cierto mes
    ResponseDTO.Store storesGetBestSeller(int year, int month);

    // 12 Obtener el banco que registre la mayor sumatoria de los importes en pagos
    // con su tarjeta.
    ResponseDTO.Bank banksGetTheOneWithMostPaymentValues();
}
