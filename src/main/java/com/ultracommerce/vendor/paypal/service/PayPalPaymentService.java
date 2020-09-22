/*
 * #%L
 * UltraCommerce PayPal
 * %%
 * Copyright (C) 2009 - 2018 Ultra Commerce
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
package com.ultracommerce.vendor.paypal.service;

import com.ultracommerce.common.vendor.service.exception.PaymentException;

import com.paypal.api.payments.Payment;

public interface PayPalPaymentService {

    /**
     * Creates a PayPal payment
     * @param performCheckoutOnReturn Indicates if we should start checkout after the user has authorized the payment
     * 
     * @return The new payment
     * @throws PaymentException
     */
    Payment createPayPalPaymentForCurrentOrder(boolean performCheckoutOnReturn) throws PaymentException;

    /**
     * Updates the PayPal payment to be in sync with the order. This method should be used when fulfillment or pricing information changes
     * after Payment creation. For creating payment use {@link #createPayPalPaymentForCurrentOrder(boolean)}
     * 
     * @throws PaymentException
     */
    void updatePayPalPaymentForFulfillment() throws PaymentException;

    String getPayPalPaymentIdFromCurrentOrder() throws PaymentException;

    String getPayPalPayerIdFromCurrentOrder() throws PaymentException;

    void setPayPalPaymentIdOnCurrentOrder(String paymentId) throws PaymentException;

    void setPayPalPayerIdOnCurrentOrder(String payerId) throws PaymentException;

}
