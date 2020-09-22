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

import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.common.payment.service.CurrentOrderPaymentRequestService;
import com.ultracommerce.common.vendor.service.exception.PaymentException;
import com.ultracommerce.payment.service.gateway.ExternalCallPayPalCheckoutService;
import com.ultracommerce.vendor.paypal.api.AgreementToken;
import com.ultracommerce.vendor.paypal.service.payment.MessageConstants;
import com.ultracommerce.vendor.paypal.service.payment.PayPalCreateAgreementTokenRequest;
import com.ultracommerce.vendor.paypal.service.payment.PayPalCreateAgreementTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Plan;
import javax.annotation.Resource;

@Service("ucPayPalAgreementTokenService")
public class PayPalAgreementTokenServiceImpl implements PayPalAgreementTokenService {

    @Resource(name = "ucExternalCallPayPalCheckoutService")
    protected ExternalCallPayPalCheckoutService externalCallService;

    @Resource(name = "ucPayPalWebProfileService")
    protected PayPalWebProfileService webProfileService;

    @Autowired(required = false)
    protected CurrentOrderPaymentRequestService currentOrderPaymentRequestService;

    /**
     * To support PayPal Reference Transactions and Billing Agreement Tokens
     * {@see https://developer.paypal.com/docs/limited-release/reference-transactions}
     *
     * @param performCheckoutOnReturn
     * @return
     * @throws PaymentException
     */
    @Override
    public AgreementToken createPayPalAgreementTokenForCurrentOrder(boolean performCheckoutOnReturn) throws PaymentException {
        PaymentRequestDTO paymentRequestDTO = getPaymentRequestForCurrentOrder();

        // Create Agreement Token
        String agreementDescription = constructAgreementDescription(paymentRequestDTO);
        Payer payer = constructPayer(paymentRequestDTO);
        Plan plan = constructPlan(paymentRequestDTO, performCheckoutOnReturn);
        AgreementToken agreementToken = new AgreementToken(agreementDescription, payer, plan);
        return createAgreementToken(agreementToken, paymentRequestDTO);
    }

    protected Plan constructPlan(PaymentRequestDTO paymentRequestDTO, boolean performCheckoutOnReturn) {
        Plan plan = new Plan();
        plan.setType(MessageConstants.PLAN_TYPE_MERCHANTINITIATEDBILLING);

        // Set up merchant preferences
        MerchantPreferences merchantPreferences = new MerchantPreferences();
        merchantPreferences.setCancelUrl(externalCallService.getConfiguration().getCancelUrl());
        String returnUrl = externalCallService.getConfiguration().getReturnUrl();
        if (performCheckoutOnReturn) {
            returnUrl += "?" + MessageConstants.CHECKOUT_COMPLETE + "=true";
        }
        merchantPreferences.setReturnUrl(returnUrl);
        merchantPreferences.setAcceptedPaymentType(MessageConstants.MERCHANTPREF_ACCEPTEDPAYMENTTYPE_INSTANT);
        plan.setMerchantPreferences(merchantPreferences);
        return plan;
    }

    protected Payer constructPayer(PaymentRequestDTO paymentRequestDTO) {
        Payer payer = new Payer();
        payer.setPaymentMethod(MessageConstants.PAYER_PAYMENTMETHOD_PAYPAL);
        return payer;
    }

    protected AgreementToken createAgreementToken(AgreementToken agreementToken, PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        PayPalCreateAgreementTokenResponse response = (PayPalCreateAgreementTokenResponse) externalCallService.call(
                new PayPalCreateAgreementTokenRequest(agreementToken,
                        externalCallService.constructAPIContext(paymentRequestDTO)));
        return response.getAgreementToken();
    }

    protected String constructAgreementDescription(PaymentRequestDTO paymentRequestDTO) {
        return externalCallService.getConfiguration().getPaymentDescription();
    }

    @Override
    public PaymentRequestDTO getPaymentRequestForCurrentOrder() throws PaymentException {
        if (currentOrderPaymentRequestService != null) {
            return currentOrderPaymentRequestService.getPaymentRequestFromCurrentOrder();
        } else {
            throw new PaymentException("Unable to get PaymentRequestDTO for current order");
        }
    }

    @Override
    public String getPayPalBillingAgreementIdFromCurrentOrder() throws PaymentException {
        if (currentOrderPaymentRequestService != null) {
            return currentOrderPaymentRequestService.retrieveOrderAttributeFromCurrentOrder(MessageConstants.BILLINGAGREEMENTID);
        } else {
            throw new PaymentException("Unable to retrieve PayPal Billing Agreement ID for current order");
        }
    }

    @Override
    public void setPayPalBillingAgreementIdOnCurrentOrder(String billingAgreementId) throws PaymentException {
        if (currentOrderPaymentRequestService != null) {
            currentOrderPaymentRequestService.addOrderAttributeToCurrentOrder(MessageConstants.BILLINGAGREEMENTID, billingAgreementId);
        } else {
            throw new PaymentException("Unable to set PayPal Billing Agreement ID on current order");
        }
    }

    @Override
    public String getPayPalAgreementTokenFromCurrentOrder() throws PaymentException {
        if (currentOrderPaymentRequestService != null) {
            return currentOrderPaymentRequestService.retrieveOrderAttributeFromCurrentOrder(MessageConstants.AGREEMENTTOKENID);
        } else {
            throw new PaymentException("Unable to retrieve PayPal Agreement Token for current order");
        }
    }

    @Override
    public void setPayPalAgreementTokenOnCurrentOrder(String agreementToken) throws PaymentException {
        if (currentOrderPaymentRequestService != null) {
            currentOrderPaymentRequestService.addOrderAttributeToCurrentOrder(MessageConstants.AGREEMENTTOKENID, agreementToken);
        } else {
            throw new PaymentException("Unable to set PayPal Agreement token on current order");
        }
    }

}
