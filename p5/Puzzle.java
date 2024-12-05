package dev.advent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

public class Puzzle {
  
  public static boolean isOk(Multimap<Integer, Integer> beforeMap, List<Integer> update) {
    for (int i = 0; i < update.size(); i++) {
      int currentInt = update.get(i);
      for (int j = i + 1; j < update.size(); j++) {
        if (beforeMap.get(currentInt).contains(update.get(j))) {
          return false;
        }
      }
    }
    return true;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p5/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    Multimap<Integer, Integer> beforeMap = HashMultimap.create();
    Multimap<Integer, Integer> afterMap = HashMultimap.create();
    List<List<Integer>> updates = new ArrayList<>();

    boolean isMapping = true;
    for (String line : lines) {
      if (line.isEmpty()) {
        isMapping = false;
        continue;
      }

      if (isMapping) {
        String[] split = line.split("\\|");
        int before = Integer.parseInt(split[0]);
        int after = Integer.parseInt(split[1]);

        beforeMap.put(after, before);
        afterMap.put(before, after);
      } else {
        String[] split = line.split(",");
        updates.add(Arrays.asList(split).stream()
            .map(v -> Integer.parseInt(v))
            .toList());
      }
    }

    int answer = 0;
    for (List<Integer> update : updates) {
      if (isOk(beforeMap, update)) {
        int middle = update.get(update.size() / 2);
        answer += middle;
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
