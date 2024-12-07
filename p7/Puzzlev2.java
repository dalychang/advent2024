package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzlev2 {
  private static final Pattern PATTERN = Pattern.compile("(\\d+):\\s?(.+)");

  public static boolean traverse(long testValue, long currentValue, List<Long> values) {
    if (currentValue > testValue) {
      return false;
    }

    if (values.isEmpty()) {
      return (currentValue == testValue);
    }
    
    List<Long> sublist = values.subList(1, values.size());
    String currentValueString = String.valueOf(currentValue);
    String newValueString = String.valueOf(values.get(0));

    long concatValue = Long.parseLong(currentValueString + newValueString);
    return traverse(testValue, currentValue + values.get(0), sublist) || traverse(testValue, currentValue * values.get(0), sublist) 
        || traverse(testValue, concatValue, sublist);
  }

  public static long calculate(String s) {
    Matcher matcher = PATTERN.matcher(s);
    matcher.find();
    long testValue = Long.parseLong(matcher.group(1));
    List<Long> values = new ArrayList<>();
    String[] split = matcher.group(2).split("\\s");
    for (String v : split) {
      values.add(Long.parseLong(v));
    }

    if (traverse(testValue, values.get(0), values.subList(1, values.size()))) {
      return testValue;
    } else {
      return 0;
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p7/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    long answer = 0;
    for (String s : lines) {
      answer += calculate(s);
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
