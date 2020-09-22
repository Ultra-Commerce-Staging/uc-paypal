/*
 * #%L
 * UltraCommerce PayPal
 * %%
 * Copyright (C) 2009 - 2014 Ultra Commerce
 * %%
 * Licensed under the Ultra Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.ultracommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Ultra in which case
 * the Ultra End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.ultracommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Ultra Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.ultracommerce.payment.service.gateway;

import com.ultracommerce.common.payment.PaymentTransactionType;
import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.common.payment.dto.PaymentResponseDTO;
import com.ultracommerce.common.payment.service.AbstractPaymentGatewayTransactionConfirmationService;
import com.ultracommerce.common.payment.service.PaymentGatewayTransactionConfirmationService;
import com.ultracommerce.common.payment.service.PaymentGatewayTransactionService;
import com.ultracommerce.common.vendor.service.exception.PaymentException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("ucPayPalCheckoutTransactionConfirmationService")
public class PayPalCheckoutTransactionConfirmationServiceImpl extends AbstractPaymentGatewayTransactionConfirmationService implements PaymentGatewayTransactionConfirmationService {

    @Resource(name = "ucPayPalCheckoutConfiguration")
    protected PayPalCheckoutConfiguration configuration;

    @Resource(name = "ucPayPalCheckoutTransactionService")
    protected PaymentGatewayTransactionService transactionService;

    @Override
    public PaymentResponseDTO confirmTransaction(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        PaymentResponseDTO responseDTO = null;
        if (configuration.isPerformAuthorizeAndCapture()){
            responseDTO = transactionService.authorizeAndCapture(paymentRequestDTO);
            responseDTO.paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        } else {
            responseDTO =  transactionService.authorize(paymentRequestDTO);
            responseDTO.paymentTransactionType(PaymentTransactionType.AUTHORIZE);
        }

        return responseDTO;

    }

}
