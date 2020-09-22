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

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public abstract class PayPalRequest {

    protected Boolean executed = false;
    protected APIContext apiContext;

    public PayPalRequest(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    public PayPalResponse execute() throws PayPalRESTException {
        if (isValid()) {
            executed = true;
            return executeInternal();
        }
        throw new RuntimeException();
    }

    protected boolean isValid() {
        return apiContext != null && isRequestValid() && !executed;
    }

    protected abstract PayPalResponse executeInternal() throws PayPalRESTException;

    protected abstract boolean isRequestValid();
}
