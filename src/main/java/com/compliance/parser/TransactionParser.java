package com.compliance.parser;
import com.compliance.model.Transaction;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
public class TransactionParser {
 private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
 public List<Transaction> parse(Path path) throws IOException {
  JsonNode root = mapper.readTree(path.toFile());
  if (root == null || !root.path("transactions").isArray())
   throw new IllegalArgumentException("Expected JSON object with transactions array");
  List<Transaction> result = new ArrayList<>(); Set<String> ids = new HashSet<>();
  for (JsonNode node : root.path("transactions")) {
   if (!node.isObject()) throw new IllegalArgumentException("Transaction must be an object: " + node);
   Transaction t = mapper.treeToValue(node, Transaction.class);
   if (t.transactionId() == null || t.transactionId().isBlank() || t.timestamp() == null ||
       t.amount() == null || t.currency() == null || t.currency().isBlank() ||
       t.country() == null || t.country().isBlank() || t.customerId() == null || t.customerId().isBlank() ||
       t.accountType() == null || t.accountType().isBlank() || t.transactionType() == null || t.transactionType().isBlank())
    throw new IllegalArgumentException("Missing required transaction data: " + node);
   if (!ids.add(t.transactionId())) throw new IllegalArgumentException("Duplicate transaction ID: " + t.transactionId());
   result.add(t);
  }
  return result;
 }
}
