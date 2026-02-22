package com.ashraf.payment.iso;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlRootElement(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoDocument {

    @XmlElement(name = "FIToFICstmrCdtTrf")
    private CreditTransfer creditTransfer;

    public CreditTransfer getCreditTransfer() {
        return creditTransfer;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreditTransfer {

        @XmlElement(name = "GrpHdr")
        private GroupHeader groupHeader;

        @XmlElement(name = "CdtTrfTxInf")
        private Transaction transaction;

        public Transaction getTransaction() {
            return transaction;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GroupHeader {

        @XmlElement(name = "MsgId")
        private String messageId;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Transaction {

        @XmlElement(name = "IntrBkSttlmAmt")
        private Amount amount;

        public BigDecimal getAmountValue() {
            return amount.value;
        }

        public String getCurrency() {
            return amount.currency;
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