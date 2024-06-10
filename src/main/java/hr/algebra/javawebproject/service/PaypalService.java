package hr.algebra.javawebproject.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import hr.algebra.javawebproject.model.dataModel.TransactionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {

    @Autowired
    private APIContext apiContext;

    public Payment createPayment(
            BigDecimal total,
            String currency,
            String method,
            String intent,
            List<TransactionItem> transactionItems,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {

        List<Item> paypalItems = new ArrayList<>();
        for (TransactionItem item : transactionItems) {
            Item paypalItem = new Item();
            paypalItem.setName(item.getArtPiece().getTitle());
            paypalItem.setCurrency(currency);
            paypalItem.setPrice(String.format("%.2f", item.getArtPiece().getPrice()));
            paypalItem.setQuantity(String.valueOf(item.getQuantity()));
            paypalItems.add(paypalItem);
        }

        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setDescription("Purchase");
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));
        transaction.setAmount(amount);
        transaction.setItemList(new ItemList().setItems(paypalItems));
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecution);
    }
}