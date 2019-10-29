package org.broadleafcommerce.payment.service.gateway;

import com.broadleafcommerce.paymentgateway.domain.PaymentRequest;
import com.broadleafcommerce.paymentgateway.domain.PaymentResponse;
import com.broadleafcommerce.paymentgateway.domain.enums.DefaultTransactionTypes;
import com.broadleafcommerce.paymentgateway.service.PaymentGatewayTransactionService;
import com.broadleafcommerce.paymentgateway.service.exception.PaymentException;

import lombok.RequiredArgsConstructor;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@RequiredArgsConstructor
public class DefaultPayPalCheckoutTransactionConfirmationService
        implements PayPalCheckoutTransactionConfirmationService {

    private final PayPalGatewayConfiguration gatewayConfiguration;
    private final PaymentGatewayTransactionService transactionService;

    @Override
    public PaymentResponse confirmTransaction(PaymentRequest paymentRequest)
            throws PaymentException {
        PaymentResponse responseDTO = null;
        if (gatewayConfiguration.isPerformAuthorizeAndCapture()) {
            responseDTO = transactionService.authorizeAndCapture(paymentRequest);
            responseDTO.transactionType(DefaultTransactionTypes.AUTHORIZE_AND_CAPTURE);
        } else {
            responseDTO = transactionService.authorize(paymentRequest);
            responseDTO.transactionType(DefaultTransactionTypes.AUTHORIZE);
        }

        return responseDTO;

    }

    @Override
    public String getGatewayType() {
        return PayPalCheckoutPaymentGatewayType.PAYPAL_CHECKOUT.name();
    }

}