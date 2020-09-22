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
package com.ultracommerce.vendor.paypal.service.payment;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;


public class PayPalCreatePaymentRequest extends PayPalRequest {

    protected Payment payment;

    public PayPalCreatePaymentRequest(Payment payment, APIContext apiContext) {
        super(apiContext);
        this.payment = payment;
    }

    @Override
    protected PayPalResponse executeInternal() throws PayPalRESTException {
        return new PayPalCreatePaymentResponse(payment.create(apiContext));
    }

    @Override
    protected boolean isRequestValid() {
        boolean baseCaseValid = payment != null && payment.getPayer() != null && CollectionUtils.isNotEmpty(payment.getTransactions());
        if (baseCaseValid) {
            Transaction transaction = payment.getTransactions().get(0);
            if (transaction != null && transaction.getAmount() != null) {
                Amount amount = transaction.getAmount();
                Details details = amount.getDetails();
                boolean amountConditions = (amount.getCurrency() == null || StringUtils.isNotBlank(amount.getCurrency())) && StringUtils.isNotBlank(amount.getTotal());
                boolean detailsConditions = details != null && StringUtils.isNotBlank(details.getSubtotal()) && (details.getShipping() == null || StringUtils.isNotBlank(details.getShipping())) && (details.getTax() == null || StringUtils.isNotBlank(details.getTax()));
                return amountConditions && detailsConditions;
            }
        }
        return false;
    }

}
