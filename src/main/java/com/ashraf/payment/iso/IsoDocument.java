package com.ashraf.payment.iso;

import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@XmlRootElement(
        name = "Document",
        namespace = IsoDocument.NAMESPACE
)
@XmlAccessorType(XmlAccessType.FIELD)
public class IsoDocument {

    static final String NAMESPACE =
            "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08";

    @XmlElement(name = "FIToFICstmrCdtTrf", namespace = NAMESPACE)
    private CreditTransfer creditTransfer;

    public Optional<Transaction> transaction() {
        return Optional.ofNullable(creditTransfer)
                .map(CreditTransfer::transaction);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreditTransfer {

        @XmlElement(name = "GrpHdr", namespace = NAMESPACE)
        private GroupHeader groupHeader;

        @XmlElement(name = "CdtTrfTxInf", namespace = NAMESPACE)
        private Transaction transaction;

        public Transaction transaction() {
            return transaction;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GroupHeader {

        @XmlElement(name = "MsgId", namespace = NAMESPACE)
        private String messageId;

        public String messageId() {
            return messageId;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Transaction {

        @XmlElement(name = "IntrBkSttlmAmt", namespace = NAMESPACE)
        private Amount amount;

        public BigDecimal requireAmount() {
            if (amount == null || amount.value == null) {
                throw new IllegalArgumentException("Missing amount");
            }
            return amount.value;
        }

        public String requireCurrency() {
            if (amount == null || amount.currency == null) {
                throw new IllegalArgumentException("Missing currency");
            }
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