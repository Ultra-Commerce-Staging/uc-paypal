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

import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.List;

public class PayPalUpdatePaymentRequest extends PayPalRequest {

    protected Payment payment;
    protected List<Patch> patches;

    public PayPalUpdatePaymentRequest(Payment payment, List<Patch> patches, APIContext apiContext) {
        super(apiContext);
        this.payment = payment;
        this.patches = patches;
    }

    @Override
    protected PayPalResponse executeInternal() throws PayPalRESTException {
        payment.update(apiContext, patches);
        return new PayPalUpdatePaymentResponse();
    }

    @Override
    protected boolean isRequestValid() {
        boolean paymentValid = payment != null && StringUtils.isNotBlank(payment.getId());
        if (paymentValid && CollectionUtils.isNotEmpty(patches)) {
            for (Patch patch : patches) {
                if (patch == null || StringUtils.isBlank(patch.getPath()) || StringUtils.isBlank(patch.getOp())) {
                    return false;
                }
                if (patch.getOp() != "remove" && patch.getValue() == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
