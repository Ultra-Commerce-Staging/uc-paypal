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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.payment.PaymentTransactionType;
import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.common.payment.dto.PaymentResponseDTO;
import com.ultracommerce.common.payment.service.AbstractPaymentGatewayHostedService;
import com.ultracommerce.common.payment.service.PaymentGatewayHostedService;
import com.ultracommerce.common.payment.service.PaymentGatewayTransactionService;
import com.ultracommerce.common.vendor.service.exception.PaymentException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("ucPayPalCheckoutHostedService")
public class PayPalCheckoutHostedServiceImpl extends AbstractPaymentGatewayHostedService implements PaymentGatewayHostedService {

    protected static final Log LOG = LogFactory.getLog(PayPalCheckoutHostedServiceImpl.class);

    @Resource(name = "ucExternalCallPayPalCheckoutService")
    protected ExternalCallPayPalCheckoutService payPalCheckoutService;

    @Resource(name = "ucPayPalCheckoutTransactionService")
    protected PaymentGatewayTransactionService transactionService;

    @Override
    public PaymentResponseDTO requestHostedEndpoint(PaymentRequestDTO paymentRequestDTO) throws PaymentException {

        PaymentResponseDTO responseDTO;
        if (payPalCheckoutService.getConfiguration().isPerformAuthorizeAndCapture()) {
            responseDTO = transactionService.authorizeAndCapture(paymentRequestDTO);
            responseDTO.paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        } else {
            responseDTO = transactionService.authorize(paymentRequestDTO);
            responseDTO.paymentTransactionType(PaymentTransactionType.AUTHORIZE);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Request to PayPal Checkout Hosted Endpoint with raw response: " +
                responseDTO.getRawResponse());
        }

        return responseDTO;

    }

}
