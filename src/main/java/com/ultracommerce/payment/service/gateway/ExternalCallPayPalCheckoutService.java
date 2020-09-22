/*
 * #%L
 * UltraCommerce PayPal
 * %%
 * Copyright (C) 2009 - 2016 Ultra Commerce
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

import com.ultracommerce.common.money.Money;
import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.common.payment.dto.PaymentResponseDTO;
import com.ultracommerce.common.vendor.service.exception.PaymentException;
import com.ultracommerce.vendor.paypal.api.AgreementToken;
import com.ultracommerce.vendor.paypal.service.payment.PayPalRequest;
import com.ultracommerce.vendor.paypal.service.payment.PayPalResponse;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.base.rest.APIContext;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface ExternalCallPayPalCheckoutService {

    PayPalCheckoutConfiguration getConfiguration();

    void setCommonDetailsResponse(AgreementToken response, PaymentResponseDTO responseDTO, Money amount,
                                  String orderId, boolean checkoutComplete);

    /**
     * Converts a PayPal payment into a PaymentResponseDTO
     * 
     * @param response A PayPal payment that should be used to be converted into a PaymentResponseDTO
     * @param responseDTO The response dto that should be used to copy information from the PayPal payment
     */
    void setCommonDetailsResponse(Payment response, PaymentResponseDTO responseDTO);

    ShippingAddress getPayPalShippingAddress(PaymentRequestDTO paymentRequestDTO);

    ItemList getPayPalItemListFromOrder(PaymentRequestDTO paymentRequestDTO, boolean shouldPopulateShipping);

    Amount getPayPalAmountFromOrder(PaymentRequestDTO paymentRequestDTO);

    /**
     * Makes a request to PayPal
     * 
     * @param paymentRequest The payment request that should be executed. The operation that is executed is depedent on which implementation of {@link PayPalRequest} is sent
     * @return the respective PayPalResponse that corresponds to the given PayPalRequest
     * @throws PaymentException
     */
    PayPalResponse call(PayPalRequest paymentRequest) throws PaymentException;

    APIContext constructAPIContext(PaymentRequestDTO paymentRequestDTO);
}
