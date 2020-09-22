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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ultracommerce.common.payment.PaymentType;
import com.ultracommerce.common.payment.dto.PaymentRequestDTO;
import com.ultracommerce.common.payment.dto.PaymentResponseDTO;
import com.ultracommerce.common.payment.service.AbstractPaymentGatewayReportingService;
import com.ultracommerce.common.payment.service.PaymentGatewayReportingService;
import com.ultracommerce.common.vendor.service.exception.PaymentException;
import com.ultracommerce.vendor.paypal.service.payment.MessageConstants;
import com.ultracommerce.vendor.paypal.service.payment.PayPalCheckoutPaymentGatewayType;
import com.ultracommerce.vendor.paypal.service.payment.PayPalPaymentRetrievalRequest;
import com.ultracommerce.vendor.paypal.service.payment.PayPalPaymentRetrievalResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.paypal.api.payments.Payment;
import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("ucPayPalCheckoutReportingService")
public class PayPalCheckoutReportingServiceImpl extends AbstractPaymentGatewayReportingService implements PaymentGatewayReportingService {

    private static final Log LOG = LogFactory.getLog(PayPalCheckoutReportingServiceImpl.class);

    @Resource(name = "ucExternalCallPayPalCheckoutService")
    protected ExternalCallPayPalCheckoutService payPalCheckoutService;

    @Override
    public PaymentResponseDTO findDetailsByTransaction(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        Assert.isTrue(paymentRequestDTO.getAdditionalFields().containsKey(MessageConstants.HTTP_PAYERID), "The RequestDTO must contain a payerID");
        Assert.isTrue(paymentRequestDTO.getAdditionalFields().containsKey(MessageConstants.HTTP_PAYMENTID), "The RequestDTO must contain a paymentID");

        PayPalPaymentRetrievalResponse response = (PayPalPaymentRetrievalResponse) payPalCheckoutService.call(
                new PayPalPaymentRetrievalRequest((String) paymentRequestDTO.getAdditionalFields().get(MessageConstants.HTTP_PAYMENTID),
                        payPalCheckoutService.constructAPIContext(paymentRequestDTO)));
        Payment payment = response.getPayment();
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT,
                PayPalCheckoutPaymentGatewayType.PAYPAL_CHECKOUT);
        payPalCheckoutService.setCommonDetailsResponse(payment, responseDTO);
        responseDTO.responseMap(MessageConstants.PAYERID, (String) paymentRequestDTO.getAdditionalFields().get(MessageConstants.HTTP_PAYERID))
                    .responseMap(MessageConstants.PAYMENTID, (String) paymentRequestDTO.getAdditionalFields().get(MessageConstants.HTTP_PAYMENTID));
        LOG.info("ResponseDTO created: " + ToStringBuilder.reflectionToString(responseDTO, ToStringStyle.MULTI_LINE_STYLE));
        return responseDTO;
    }

}
