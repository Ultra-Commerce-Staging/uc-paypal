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

import com.ultracommerce.common.payment.service.AbstractPaymentGatewayConfigurationService;
import com.ultracommerce.common.payment.service.PaymentGatewayConfiguration;
import com.ultracommerce.common.payment.service.PaymentGatewayConfigurationService;
import com.ultracommerce.common.payment.service.PaymentGatewayHostedService;
import com.ultracommerce.common.payment.service.PaymentGatewayReportingService;
import com.ultracommerce.common.payment.service.PaymentGatewayRollbackService;
import com.ultracommerce.common.payment.service.PaymentGatewayTransactionConfirmationService;
import com.ultracommerce.common.payment.service.PaymentGatewayTransactionService;
import com.ultracommerce.common.payment.service.PaymentGatewayWebResponseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("ucPayPalCheckoutConfigurationService")
public class PayPalCheckoutConfigurationServiceImpl extends AbstractPaymentGatewayConfigurationService implements PaymentGatewayConfigurationService {

    @Resource(name = "ucPayPalCheckoutConfiguration")
    protected PayPalCheckoutConfiguration configuration;

    @Resource(name = "ucPayPalCheckoutTransactionService")
    protected PaymentGatewayTransactionService transactionService;

    @Resource(name = "ucPayPalCheckoutTransactionConfirmationService")
    protected PaymentGatewayTransactionConfirmationService transactionConfirmationService;

    @Resource(name = "ucPayPalCheckoutReportingService")
    protected PaymentGatewayReportingService reportingService;

    @Resource(name = "ucPayPalCheckoutRollbackService")
    protected PaymentGatewayRollbackService rollbackService;

    @Resource(name = "ucPayPalCheckoutWebResponseService")
    protected PaymentGatewayWebResponseService webResponseService;

    @Resource(name = "ucPayPalCheckoutHostedService")
    protected PaymentGatewayHostedService hostedService;

    @Override
    public PaymentGatewayConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public PaymentGatewayTransactionService getTransactionService() {
        return transactionService;
    }

    @Override
    public PaymentGatewayTransactionConfirmationService getTransactionConfirmationService() {
        return transactionConfirmationService;
    }

    @Override
    public PaymentGatewayReportingService getReportingService() {
        return reportingService;
    }

    @Override
    public PaymentGatewayRollbackService getRollbackService() {
        return rollbackService;
    }

    @Override
    public PaymentGatewayWebResponseService getWebResponseService() {
        return webResponseService;
    }

    @Override
    public PaymentGatewayHostedService getHostedService() {
        return hostedService;
    }
}
