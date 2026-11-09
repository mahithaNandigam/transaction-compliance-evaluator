package com.compliance;
import com.compliance.model.*;
import com.compliance.service.RuleEvaluator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class RuleEvaluatorTest {
 private final RuleEvaluator evaluator = new RuleEvaluator();
 private Transaction txn(String amount,String country,String time) {
  return new Transaction("TXN1",OffsetDateTime.parse(time),new BigDecimal(amount),"USD",country,"CUST1","BUSINESS","WIRE_TRANSFER");
 }
 private Rule rule(String id,String field,String op,String value) { return new Rule(id,"test",field,op,value,"HIGH"); }
 @Test void greaterThanIsStrict() {
  assertTrue(evaluator.matches(txn("10000.01","US","2024-01-15T12:00:00Z"),rule("R1","amount","gt","10000")));
  assertFalse(evaluator.matches(txn("10000.00","US","2024-01-15T12:00:00Z"),rule("R1","amount","gt","10000")));
 }
 @Test void lessThanIsStrict() {
  assertTrue(evaluator.matches(txn("999","US","2024-01-15T12:00:00Z"),rule("R1","amount","lt","1000")));
  assertFalse(evaluator.matches(txn("1000","US","2024-01-15T12:00:00Z"),rule("R1","amount","lt","1000")));
 }
 @Test void numericEqualsIgnoresScale() { assertTrue(evaluator.matches(txn("100.00","US","2024-01-15T12:00:00Z"),rule("R1","amount","eq","100.0"))); }
 @Test void notEqualsWorks() { assertTrue(evaluator.matches(txn("100","US","2024-01-15T12:00:00Z"),rule("R1","country","neq","GB"))); }
 @Test void inMatchesQuotedCsvListValue() { assertTrue(evaluator.matches(txn("100","IR","2024-01-15T12:00:00Z"),rule("R1","country","in","IR,KP,SY"))); }
 @Test void stringsAreCaseSensitive() { assertFalse(evaluator.matches(txn("100","ir","2024-01-15T12:00:00Z"),rule("R1","country","in","IR,KP,SY"))); }
 @Test void utcHourRespectsOffset() { assertTrue(evaluator.matches(txn("100","US","2024-01-16T01:30:00+02:00"),rule("R1","timestamp.hour","gt","22"))); }
 @Test void sortsBreachesByRuleId() {
  var t=txn("15000","IR","2024-01-15T23:00:00Z");
  assertEquals(List.of("R001","R002","R003"),evaluator.evaluate(t,List.of(rule("R003","timestamp.hour","gt","22"),rule("R001","amount","gt","10000"),rule("R002","country","in","IR,KP,SY"))).stream().map(Rule::ruleId).toList());
 }
 @Test void returnsEmptyWhenNoBreach() { assertTrue(evaluator.evaluate(txn("100","US","2024-01-15T12:00:00Z"),List.of(rule("R1","amount","gt","10000"))).isEmpty()); }
}
