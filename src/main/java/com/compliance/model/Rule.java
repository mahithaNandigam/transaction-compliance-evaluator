package com.compliance.model;
public record Rule(String ruleId, String ruleName, String field, String operator, String value, String severity) {}
