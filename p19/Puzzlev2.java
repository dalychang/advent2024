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
  public static long combinations(String design, List<String> towels, Map<String, Long> cache) {
    if (design.isEmpty()) {
      return 1;
    }

    if (cache.containsKey(design)) {
      return cache.get(design);
    }

    long totalCombos = 0;
    for (String t : towels) {
      if (t.length() > design.length()) {
        continue;
      }

      boolean towelOk = true;
      for (int i = 0; i < t.length(); i++) {
        if (t.charAt(i) != design.charAt(i)) {
          towelOk = false;
          break;
        }
      }

      if (!towelOk) {
        continue;
      }

      String remainingDesign = design.substring(t.length(), design.length());
      long combos = combinations(remainingDesign, towels, cache);
      totalCombos += combos;
    }

    cache.put(design, totalCombos);
    return totalCombos;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p19/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<String> towels = new ArrayList<>();
    List<String> designs = new ArrayList<>();
    boolean readDesign = false;
    for (String line : lines) {
      if (line.isEmpty()) {
        readDesign = true;
        continue;
      }

      if (readDesign) {
        designs.add(line);
      } else {
        String[] split = line.split(",\\s*");
        for (String s : split) {
          towels.add(s);
        }
      }
    }

    long answer = 0;
    Map<String, Long> cache = new HashMap<>();
    for (String design : designs) {
      answer += combinations(design, towels, cache);
    }

    System.out.println("answer is " + answer);     
   
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
