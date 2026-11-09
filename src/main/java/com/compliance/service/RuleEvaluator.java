package com.compliance.service;
import com.compliance.model.*;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.*;
public class RuleEvaluator {
 public List<Rule> evaluate(Transaction transaction, List<Rule> rules) {
  return rules.stream().filter(rule -> matches(transaction,rule)).sorted(Comparator.comparing(Rule::ruleId)).toList();
 }
 public boolean matches(Transaction transaction, Rule rule) {
  String actual = fieldValue(transaction,rule.field()); String expected = rule.value();
  return switch(rule.operator()) {
   case "gt" -> number(actual).compareTo(number(expected)) > 0;
   case "lt" -> number(actual).compareTo(number(expected)) < 0;
   case "eq" -> numericEquality(rule) ? number(actual).compareTo(number(expected)) == 0 : actual.equals(expected);
   case "neq" -> numericEquality(rule) ? number(actual).compareTo(number(expected)) != 0 : !actual.equals(expected);
   case "in" -> Arrays.stream(expected.split(",",-1)).anyMatch(item -> actual.equals(item.trim()));
   default -> throw new IllegalArgumentException("Unsupported operator: " + rule.operator());
  };
 }
 private boolean numericEquality(Rule rule) { return rule.field().equals("amount") || rule.field().equals("timestamp.hour"); }
 private BigDecimal number(String value) { return new BigDecimal(value); }
 private String fieldValue(Transaction t, String field) {
  return switch(field) {
   case "transaction_id" -> t.transactionId();
   case "timestamp" -> t.timestamp().toString();
   case "timestamp.hour" -> String.valueOf(t.timestamp().withOffsetSameInstant(ZoneOffset.UTC).getHour());
   case "amount" -> t.amount().toPlainString();
   case "currency" -> t.currency();
   case "country" -> t.country();
   case "customer_id" -> t.customerId();
   case "account_type" -> t.accountType();
   case "transaction_type" -> t.transactionType();
   default -> throw new IllegalArgumentException("Unsupported field: " + field);
  };
 }
}
