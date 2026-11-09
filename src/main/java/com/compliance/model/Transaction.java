package com.compliance.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
public record Transaction(
 @JsonProperty("transaction_id") String transactionId,
 OffsetDateTime timestamp, BigDecimal amount, String currency, String country,
 @JsonProperty("customer_id") String customerId,
 @JsonProperty("account_type") String accountType,
 @JsonProperty("transaction_type") String transactionType) {}
