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
  private static final long PRUNE_NUMBER = 16777216L;
  private static final int ITERATIONS = 2000;

  public record Monkey(long initialSecret, List<Secret> secrets) {}
  public record Secret(int price, int delta) {}
  public record SeqKey(long initialSecret, String seq) {}
  
  public static long calculateNext(long number) {
    long result = number;
    number = (number * 64) ^ number;
    number = Math.floorMod(number, PRUNE_NUMBER);
    number = (number / 32) ^ number;
    number = Math.floorMod(number, PRUNE_NUMBER);
    number = (number * 2048) ^ number;
    number = Math.floorMod(number, PRUNE_NUMBER);

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
    long answer = 0;
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

    Map<SeqKey, Integer> sequenceMap = new HashMap<>();
    for (Monkey m : monkeys) {
      List<Integer> deltaSeq = new ArrayList<>();
      deltaSeq.add(m.secrets.get(0).delta);
      deltaSeq.add(m.secrets.get(1).delta);
      deltaSeq.add(m.secrets.get(2).delta);
      for (int i = 3; i < ITERATIONS; i++) {
        deltaSeq.add(m.secrets.get(i).delta);
        if (deltaSeq.size() > 4) {
          deltaSeq.remove(0);
        }
        SeqKey key = new SeqKey(m.initialSecret, Joiner.on(",").join(deltaSeq));
        if (!sequenceMap.containsKey(key)) {
          sequenceMap.put(key, m.secrets.get(i).price);
        }
      }
    }

    Multimap<Long, SeqKey> seqKeysMap = HashMultimap.create();
    for (SeqKey key : sequenceMap.keySet()) {
      seqKeysMap.put(key.initialSecret, key);
    }

    Set<String> triedSequences = new HashSet<>();

    long bestPrice = 0;
    for (int i = 0; i < monkeys.size(); i++) {
      System.out.println("Monkey " + i + "/" + monkeys.size());
      Monkey m = monkeys.get(i);
      for (SeqKey key : seqKeysMap.get(m.initialSecret)) {
        if (triedSequences.contains(key.seq)) {
          continue;
        }

        long currentPrice = sequenceMap.get(key);
        for (int j = i + 1; j < monkeys.size(); j++) {
          SeqKey nextKey = new SeqKey(monkeys.get(j).initialSecret, key.seq);
          if (sequenceMap.containsKey(nextKey)) {
            currentPrice += sequenceMap.get(nextKey);
          }
        }

        triedSequences.add(key.seq);
        bestPrice = Math.max(bestPrice, currentPrice);
      }
    }

    System.out.println("answer is " + bestPrice);
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
