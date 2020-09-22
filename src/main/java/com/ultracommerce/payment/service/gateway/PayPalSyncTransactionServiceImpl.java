/*-
 * #%L
 * UltraCommerce PayPal
 * %%
 * Copyright (C) 2009 - 2019 Ultra Commerce
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

import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.vendor.paypal.api.ReportingTransactions;
import com.ultracommerce.vendor.paypal.domain.ReportingTransactionResponse;
import com.ultracommerce.vendor.paypal.domain.TransactionInfo;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.Map;

import javax.annotation.Resource;

/**
 * This takes the {@link PaymentRequestDTO} and calls the {@link ReportingTransactions#get(Map, APIContext)} to read all the
 * {@link ReportingTransactionResponse} and then filters the results by matching the {@link TransactionInfo#getPaypal_reference_id()}
 * and {@link TransactionInfo#getCustom_field()}
 *
 * Note: in the PayPal payload, the {@link TransactionInfo#getCustom_field()} is used to capture and custom info that we want.
 * This class assumes that a UC-produced transaction id is being sent to PayPal.
 *
 * @author venkat
 *
 */
public class PayPalSyncTransactionServiceImpl implements PayPalSyncTransactionService {

    @Resource(name = "ucExternalCallPayPalCheckoutService")
    protected ExternalCallPayPalCheckoutService payPalCheckoutService;

    @Override
    public ReportingTransactionResponse lookupTransactionsByQueryParams(
            Map<String, String> queryFilter) throws PayPalRESTException {
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
        APIContext apiContext = payPalCheckoutService.constructAPIContext(paymentRequestDTO);
        return executeTransactionSearch(apiContext, queryFilter);
    }

    /**
     * This calls the {@link ReportingTransactions#get(Map, APIContext)} to fetch the reporting transactions of type
     * {@link ReportingTransactions}
     * @param apiContext
     * @param queryParamsMap
     * @return
     * @throws PayPalRESTException
     */
    protected ReportingTransactionResponse executeTransactionSearch(APIContext apiContext,
                                                                    Map<String,String> queryParamsMap) throws PayPalRESTException {
        ReportingTransactions reportingTransactions = new ReportingTransactions();

        return reportingTransactions.get(queryParamsMap, apiContext);
    }

}
