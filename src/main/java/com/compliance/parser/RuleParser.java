package com.compliance.parser;
import com.compliance.model.Rule;
import org.apache.commons.csv.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.math.BigDecimal;
import java.util.*;
public class RuleParser {
 private static final Set<String> FIELDS = Set.of("transaction_id","timestamp","timestamp.hour","amount","currency","country","customer_id","account_type","transaction_type");
 private static final Set<String> OPERATORS = Set.of("gt","lt","eq","neq","in");
 private static final Set<String> SEVERITIES = Set.of("LOW","MEDIUM","HIGH","CRITICAL");
 public List<Rule> parse(Path path) throws IOException {
  List<Rule> rules = new ArrayList<>(); Set<String> ids = new HashSet<>();
  CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get();
  try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8); CSVParser csv = format.parse(reader)) {
   for (String header : List.of("rule_id","rule_name","field","operator","value","severity"))
    if (!csv.getHeaderMap().containsKey(header)) throw new IllegalArgumentException("Missing CSV header: " + header);
   for (CSVRecord row : csv) {
    if (!row.isConsistent()) throw new IllegalArgumentException("Invalid CSV row " + row.getRecordNumber());
    Rule rule = new Rule(row.get("rule_id"),row.get("rule_name"),row.get("field"),row.get("operator"),row.get("value"),row.get("severity"));
    validate(rule);
    if (!ids.add(rule.ruleId())) throw new IllegalArgumentException("Duplicate rule ID: " + rule.ruleId());
    rules.add(rule);
   }
  }
  return rules;
 }
 private void validate(Rule r) {
  if (r.ruleId().isBlank() || r.ruleName().isBlank() || r.value().isBlank()) throw new IllegalArgumentException("Blank rule data: " + r.ruleId());
  if (!FIELDS.contains(r.field())) throw new IllegalArgumentException("Unsupported field: " + r.field());
  if (!OPERATORS.contains(r.operator())) throw new IllegalArgumentException("Unsupported operator: " + r.operator());
  if (!SEVERITIES.contains(r.severity())) throw new IllegalArgumentException("Unsupported severity: " + r.severity());
  if ((r.operator().equals("gt") || r.operator().equals("lt")) && !Set.of("amount","timestamp.hour").contains(r.field()))
   throw new IllegalArgumentException("Numeric operator requires amount or timestamp.hour: " + r.ruleId());
  if ((r.operator().equals("gt") || r.operator().equals("lt") || (r.field().equals("amount") && Set.of("eq","neq").contains(r.operator()))) ||
      (r.field().equals("timestamp.hour") && !r.operator().equals("in"))) {
   try { new BigDecimal(r.value()); } catch (NumberFormatException ex) { throw new IllegalArgumentException("Invalid numeric rule value: " + r.ruleId(),ex); }
  }
  if (r.field().equals("timestamp.hour") && r.operator().equals("in")) {
   for (String item : r.value().split(",",-1)) try { Integer.parseInt(item.trim()); } catch (NumberFormatException ex) { throw new IllegalArgumentException("Invalid UTC hour list: " + r.ruleId(),ex); }
  }
 }
}
