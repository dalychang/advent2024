package dev.advent;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;
import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A slightly faster version of V2.
public class Puzzlev3 {
  private static final long PRUNE_NUMBER = 16777216L;
  private static final int ITERATIONS = 2000;

  public record Monkey(long initialSecret, List<Secret> secrets) {}
  public record Secret(int price, int delta) {}
  
  public static long calculateNext(long number) {
    number = Math.floorMod(((number * 64) ^ number), PRUNE_NUMBER);
    number = Math.floorMod(((number / 32) ^ number), PRUNE_NUMBER);
    number = Math.floorMod(((number * 2048) ^ number), PRUNE_NUMBER);

    return number;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p22/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Long> secretNumbers = new ArrayList<>();
    for (String line : lines) {
      secretNumbers.add(Long.parseLong(line));
    }

    List<Monkey> monkeys = new ArrayList<>();
    for (long secretNumber : secretNumbers) {
      List<Secret> secretList = new ArrayList<>();
      long number = secretNumber;
      int previousPrice = Math.floorMod(number, 10);
      for (int i = 0; i < ITERATIONS; i++) {
        number = calculateNext(number);
        int price = Math.floorMod(number, 10);
        secretList.add(new Secret(price, price - previousPrice));
        previousPrice = price;
      }
      monkeys.add(new Monkey(secretNumber, secretList));
    }

    Map<Integer, Long> runningTotalMap = new HashMap<>();
    for (Monkey m : monkeys) {
      Set<Integer> seenSeq = new HashSet<>();
      for (int i = 3; i < ITERATIONS; i++) {
        int key = ((m.secrets.get(i - 3).delta & 0xff) << 24)
            | ((m.secrets.get(i - 2).delta & 0xff) << 16)
            | ((m.secrets.get(i - 1).delta & 0xff) << 8)
            | ((m.secrets.get(i).delta & 0xff));
        if (!seenSeq.contains(key)) {
          int price = m.secrets.get(i).price;
          runningTotalMap.put(key, runningTotalMap.getOrDefault(key, 0L) + price);
          seenSeq.add(key);
        }
      }
    }

    long answer = 0;
    for (long v : runningTotalMap.values()) {
      answer = Math.max(answer, v);
    }
    
    System.out.println("answer is " + answer);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
