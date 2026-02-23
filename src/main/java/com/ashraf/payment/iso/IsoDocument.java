package com.ashraf.payment.iso;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@XmlRootElement(name = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoDocument {


    private static final String NAMESPACE =
            "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08";

    @XmlElement(name = "FIToFICstmrCdtTrf", namespace = NAMESPACE)
    private CreditTransfer creditTransfer;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreditTransfer {

        @XmlElement(name = "GrpHdr", namespace = NAMESPACE)
        private GroupHeader groupHeader;

        @Getter
        @XmlElement(name = "CdtTrfTxInf", namespace = NAMESPACE)
        private Transaction transaction;

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GroupHeader {

        @XmlElement(name = "MsgId", namespace = NAMESPACE)
        private String messageId;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Transaction {

        @XmlElement(name = "IntrBkSttlmAmt", namespace = NAMESPACE)
        private Amount amount;

        public BigDecimal getAmountValue() {
            return amount != null ? amount.value : null;
        }

        public String getCurrency() {
            return amount != null ? amount.currency : null;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Amount {

        @XmlValue
        private BigDecimal value;

        @XmlAttribute(name = "Ccy")
        private String currency;
    }
}