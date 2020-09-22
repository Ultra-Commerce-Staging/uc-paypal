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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.money.Money;
import com.ultracommerce.common.payment.PaymentTransactionType;
import com.ultracommerce.common.payment.PaymentType;
import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.common.payment.dto.PaymentResponseDTO;
import com.ultracommerce.common.payment.service.AbstractPaymentGatewayWebResponseService;
import com.ultracommerce.common.payment.service.PaymentGatewayReportingService;
import com.ultracommerce.common.payment.service.PaymentGatewayWebResponsePrintService;
import com.ultracommerce.common.payment.service.PaymentGatewayWebResponseService;
import com.ultracommerce.common.vendor.service.exception.PaymentException;
import com.ultracommerce.vendor.paypal.api.AgreementToken;
import com.ultracommerce.vendor.paypal.service.PayPalAgreementTokenService;
import com.ultracommerce.vendor.paypal.service.PayPalPaymentService;
import com.ultracommerce.vendor.paypal.service.payment.MessageConstants;
import com.ultracommerce.vendor.paypal.service.payment.PayPalCheckoutPaymentGatewayType;
import com.ultracommerce.vendor.paypal.service.payment.PayPalExecuteAgreementTokenRequest;
import com.ultracommerce.vendor.paypal.service.payment.PayPalExecuteAgreementTokenResponse;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("ucPayPalCheckoutWebResponseService")
public class PayPalCheckoutWebResponseServiceImpl extends AbstractPaymentGatewayWebResponseService implements PaymentGatewayWebResponseService {

    private static final Log LOG = LogFactory.getLog(PayPalCheckoutWebResponseServiceImpl.class);

    @Resource(name = "ucExternalCallPayPalCheckoutService")
    protected ExternalCallPayPalCheckoutService externalCallService;

    @Resource(name = "ucPaymentGatewayWebResponsePrintService")
    protected PaymentGatewayWebResponsePrintService webResponsePrintService;

    @Resource(name = "ucPayPalCheckoutReportingService")
    protected PaymentGatewayReportingService reportingService;

    @Resource(name = "ucPayPalPaymentService")
    protected PayPalPaymentService paymentService;

    @Resource(name = "ucPayPalAgreementTokenService")
    protected PayPalAgreementTokenService agreementTokenService;

    @Override
    public PaymentResponseDTO translateWebResponse(HttpServletRequest request) throws PaymentException {
        boolean completeCheckout = false;
        if (request.getParameter(MessageConstants.CHECKOUT_COMPLETE) != null) {
            completeCheckout = Boolean.valueOf(request.getParameter(MessageConstants.CHECKOUT_COMPLETE));
        }

        String paymentId = request.getParameter(MessageConstants.HTTP_PAYMENTID);
        String payerId = request.getParameter(MessageConstants.HTTP_PAYERID);
        String token = request.getParameter(MessageConstants.HTTP_TOKEN);
        String billingToken = request.getParameter(MessageConstants.HTTP_BILLINGTOKEN);

        // 1. Handle Reference Transactions - Request that contain a "Billing Agreement Token" in the callback
        if (StringUtils.isNotBlank(billingToken)) {
            String billingAgreementId = agreementTokenService.getPayPalBillingAgreementIdFromCurrentOrder();
            PaymentRequestDTO requestDTO = agreementTokenService.getPaymentRequestForCurrentOrder();

            if (StringUtils.isBlank(billingAgreementId)) {
                AgreementToken agreementToken = new AgreementToken(billingToken);
                agreementToken = executeAgreementToken(agreementToken, requestDTO);

                PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT,
                        PayPalCheckoutPaymentGatewayType.PAYPAL_CHECKOUT)
                        .paymentTransactionType(PaymentTransactionType.UNCONFIRMED);
                externalCallService.setCommonDetailsResponse(agreementToken, responseDTO, new Money(requestDTO.getTransactionTotal()),
                        requestDTO.getOrderId(), completeCheckout);
                responseDTO.responseMap(MessageConstants.BILLINGAGREEMENTID, agreementToken.getId());

                LOG.info("ResponseDTO created: " + ToStringBuilder.reflectionToString(responseDTO, ToStringStyle.MULTI_LINE_STYLE));

                agreementTokenService.setPayPalAgreementTokenOnCurrentOrder(billingToken);
                agreementTokenService.setPayPalBillingAgreementIdOnCurrentOrder(agreementToken.getId());

                return responseDTO;
            } else {
                PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT,
                        PayPalCheckoutPaymentGatewayType.PAYPAL_CHECKOUT)
                        .paymentTransactionType(PaymentTransactionType.UNCONFIRMED);
                externalCallService.setCommonDetailsResponse(null, responseDTO, new Money(requestDTO.getTransactionTotal()),
                        requestDTO.getOrderId(), completeCheckout);
                responseDTO.responseMap(MessageConstants.BILLINGAGREEMENTID, billingAgreementId);

                LOG.info("ResponseDTO created: " + ToStringBuilder.reflectionToString(responseDTO, ToStringStyle.MULTI_LINE_STYLE));

                return responseDTO;
            }

        }

        // 2. Handle Billing Agreement Approvals - Request that contain an EC "token" in the callback
        if (StringUtils.isNotBlank(token)) {
            throw new UnsupportedOperationException("Billing Agreements and Recurring Subscriptions " +
                    "created via the Payments API is not yet supported.");
        }

        // 3. Finally (if not a billing agreement flow), handle payments
        PaymentRequestDTO requestDTO = new PaymentRequestDTO()
                .additionalField(MessageConstants.HTTP_PAYMENTID, paymentId)
                .additionalField(MessageConstants.HTTP_PAYERID, payerId);
        PaymentResponseDTO responseDTO = reportingService.findDetailsByTransaction(requestDTO);

        responseDTO.responseMap(MessageConstants.HTTP_REQUEST, webResponsePrintService.printRequest(request))
                .paymentTransactionType(PaymentTransactionType.UNCONFIRMED);

        paymentService.setPayPalPaymentIdOnCurrentOrder(paymentId);
        paymentService.setPayPalPayerIdOnCurrentOrder(payerId);
        return responseDTO;
    }

    protected AgreementToken executeAgreementToken(AgreementToken agreementToken, PaymentRequestDTO requestDTO) throws PaymentException {
        PayPalExecuteAgreementTokenResponse response = (PayPalExecuteAgreementTokenResponse) externalCallService.call(
                new PayPalExecuteAgreementTokenRequest(agreementToken, externalCallService.constructAPIContext(requestDTO)));
        return response.getAgreementToken();
    }

}
