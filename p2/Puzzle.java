package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
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
  
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p2/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    List<List<Integer>> input = new ArrayList<>();
    
    for (String line : lines) {
      String[] split = line.split("\\s+");
      List<Integer> values = Arrays.asList(split).stream()
          .map(v -> Integer.parseInt(v))
          .toList();
      input.add(values);
      System.out.println(values);
    }

    int answer = 0;
    for (List<Integer> values : input) {
      int currentValue = values.get(0);
      boolean increasing = currentValue < values.get(1);
      boolean safe = true;
      for (int i = 1; i < values.size(); i++) {
        int delta = Math.abs(values.get(i) - currentValue);
        if (delta < 1 || delta > 3) {
          safe = false;
          break;
        }
        if (increasing && currentValue > values.get(i)) {
          safe = false;
          break;
        } else if (!increasing && currentValue < values.get(i)) {
          safe = false;
          break;
        }
        currentValue = values.get(i);
      }
      if (safe) {
        answer++;
      }
    }

    System.out.println("answer is " + answer); 
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
