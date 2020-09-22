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
package com.ultracommerce.vendor.paypal.service.payment.type;

import com.ultracommerce.common.UltraEnumerationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extendible enumeration of transaction types.
 * 
 * @author jfischer
 */
public class PayPalTransactionType implements Serializable, UltraEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PayPalTransactionType> TYPES = new HashMap<String, PayPalTransactionType>();

    public static final PayPalTransactionType AUTHORIZE  = new PayPalTransactionType("AUTHORIZE", "Authorize");
    public static final PayPalTransactionType CAPTURE = new PayPalTransactionType("CAPTURE", "Capture");
    public static final PayPalTransactionType AUTHORIZEANDCAPTURE  = new PayPalTransactionType("AUTHORIZEANDCAPTURE", "Authorize and Capture");
    public static final PayPalTransactionType CREDIT = new PayPalTransactionType("CREDIT", "Credit");
    public static final PayPalTransactionType VOIDTRANSACTION = new PayPalTransactionType("VOIDTRANSACTION", "Void Transaction");
    public static final PayPalTransactionType REVERSEAUTHORIZE = new PayPalTransactionType("REVERSEAUTHORIZE", "Reverse Authorize");

    public static PayPalTransactionType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PayPalTransactionType() {
        //do nothing
    }

    public PayPalTransactionType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PayPalTransactionType other = (PayPalTransactionType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
