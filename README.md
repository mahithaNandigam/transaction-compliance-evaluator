# Transaction Compliance Evaluator

Java 21 + Gradle console application implementing the supplied `candidate-brief.md`.

## Setup in IntelliJ IDEA
1. Open the `transaction-compliance-evaluator` folder as a Gradle project.
2. Configure **Project SDK = Java 21** and **Gradle JVM = Java 21**.
3. Allow IntelliJ to import dependencies from Maven Central (internet required on first build).
4. Run `com.compliance.Main` with project root as working directory, or run Gradle's `run` task.

## Commands
Requires Java 21 and Gradle 8.5+ (or generate a wrapper with `gradle wrapper`).

```bash
gradle clean test
gradle run
gradle run --args="data/transactions.json data/rules.csv"
```

The program prints one line per transaction, in input order, with breached rules sorted by rule ID. Compare with `docs/expected-output.txt`.

## Design
- `Transaction` and `Rule`: immutable records.
- `TransactionParser`: Jackson JSON input validation.
- `RuleParser`: Apache Commons CSV parsing and rule validation.
- `RuleEvaluator`: independent condition evaluation; `BigDecimal` for numeric comparisons; UTC hour extraction.
- `Main`: reads inputs, prints required output, reports failures to stderr.

## Assumptions / malformed data
- Fail fast on malformed JSON/CSV, missing required fields, invalid operators, invalid severity, invalid numeric rule operands, or duplicate IDs; return nonzero exit code.
- No silent skipping of malformed customer data.
- Numeric comparisons are used for `gt`, `lt`, and amount/hour `eq` and `neq`; all other equality and membership checks are case-sensitive strings.
- `in` values are split on commas and surrounding whitespace is trimmed.
- `timestamp.hour` uses UTC even when the source timestamp contains a nonzero offset.
- Rules are independent, no currency conversion is performed, and transactions remain in input order.
- Input files are not modified.

## Tests
JUnit tests cover all operators, strict numeric boundaries, decimal equality, case sensitivity, UTC offsets, sorting, no breaches, malformed input, duplicate rules, and integration assertions against all eight supplied transactions.

## With more time
Add fuller schema checks, stronger diagnostics with source row numbers, property-based tests, streaming large files, and configurable error handling. Consider Drools only if the rule language becomes significantly more complex.

## Tools used
This solution was generated with assistance from OpenAI ChatGPT. Java, Gradle, Jackson, Apache Commons CSV, and JUnit are the intended development/runtime tools. Review, understand, and update this disclosure before submitting.

## Time taken
**Fill in your actual time spent reviewing, running, testing, and adapting the assignment.** Do not report a fabricated duration.
