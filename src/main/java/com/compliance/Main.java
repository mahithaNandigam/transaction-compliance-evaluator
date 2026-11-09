package com.compliance;
import com.compliance.model.*;
import com.compliance.parser.*;
import com.compliance.service.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
public class Main {
 public static void main(String[] args) {
  if (args.length > 2) { System.err.println("Usage: [transactions.json] [rules.csv]"); System.exit(1); }
  Path transactions = Path.of(args.length > 0 ? args[0] : "data/transactions.json");
  Path rules = Path.of(args.length > 1 ? args[1] : "data/rules.csv");
  try {
   List<Transaction> items = new TransactionParser().parse(transactions);
   List<Rule> conditions = new RuleParser().parse(rules);
   RuleEvaluator evaluator = new RuleEvaluator();
   System.out.println("Transaction    Breached Rules");
   for (Transaction item : items) {
    List<Rule> breaches = evaluator.evaluate(item, conditions);
    String output = breaches.isEmpty() ? "none" : breaches.stream()
       .map(r -> r.ruleId() + " (" + r.severity() + ")").collect(Collectors.joining(", "));
    System.out.println(item.transactionId() + "    " + output);
   }
  } catch (Exception ex) { System.err.println("Assessment failed: " + ex.getMessage()); System.exit(1); }
 }
}
