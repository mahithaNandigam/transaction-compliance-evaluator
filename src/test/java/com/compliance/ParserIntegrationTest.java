package com.compliance;
import com.compliance.parser.*;
import com.compliance.service.RuleEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ParserIntegrationTest {
 @TempDir Path temp;
 @Test void suppliedFilesLoadAndMatchAllEightTransactions() throws Exception {
  var transactions = new TransactionParser().parse(Path.of("data/transactions.json"));
  var rules = new RuleParser().parse(Path.of("data/rules.csv"));
  assertEquals(8, transactions.size()); assertEquals(8,rules.size());
  var expected = List.of(
   List.of("R001","R003","R006"),
   List.of("R002","R005","R007","R008"),
   List.of("R005"),
   List.of("R001","R002","R003","R006","R007"),
   List.of("R002","R008"),
   List.of("R005"),
   List.of("R001","R004","R006","R007"),
   List.of("R001","R002","R003","R006","R007","R008")
  );
  var evaluator = new RuleEvaluator();
  for(int i=0;i<8;i++) assertEquals(expected.get(i),evaluator.evaluate(transactions.get(i),rules).stream().map(r->r.ruleId()).toList(),"TXN"+(i+1));
 }
 @Test void rejectsMissingTransactionsArray() throws Exception {
  Path p=Files.writeString(temp.resolve("bad.json"),"{}");
  assertThrows(IllegalArgumentException.class,()->new TransactionParser().parse(p));
 }
 @Test void rejectsInvalidRuleOperator() throws Exception {
  Path p=Files.writeString(temp.resolve("bad.csv"),"rule_id,rule_name,field,operator,value,severity\nR1,test,amount,unknown,100,HIGH\n");
  assertThrows(IllegalArgumentException.class,()->new RuleParser().parse(p));
 }
 @Test void rejectsDuplicateRuleId() throws Exception {
  Path p=Files.writeString(temp.resolve("duplicate.csv"),"rule_id,rule_name,field,operator,value,severity\nR1,test,amount,gt,100,HIGH\nR1,test,amount,lt,200,HIGH\n");
  assertThrows(IllegalArgumentException.class,()->new RuleParser().parse(p));
 }
}
