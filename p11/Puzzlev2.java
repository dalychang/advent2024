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

  public static final int ITERATIONS = 75;

  public record State(long value, int blinks) {}

  public record Stone(long value) {
    public List<Stone> increment() {
      if (value == 0) {
        return List.of(new Stone(1));
      } else {
        String valueString = String.valueOf(value);
        int digits = valueString.length();
        if (digits % 2 == 0) {
          long firstStone = Long.parseLong(valueString.substring(0, digits / 2));
          long secondStone = Long.parseLong(valueString.substring(digits / 2, valueString.length()));
          return List.of(new Stone(firstStone), new Stone(secondStone));
        } else {
          return List.of(new Stone(value * 2024));
        }
      }
    }
  }

  public static void runStones(Stone stone, Map<State, Long> blinkStateMap, int iterationsLeft) {
    if (iterationsLeft == 0) {
      blinkStateMap.put(new State(stone.value, iterationsLeft), 1L);
    }
    State state = new State(stone.value, iterationsLeft);
    if (blinkStateMap.containsKey(state)) {
      return;
    }
    List<Stone> stones = stone.increment();
    long total = 0;
    for (Stone s : stones) {
      runStones(s, blinkStateMap, iterationsLeft - 1);
      State interState = new State(s.value, iterationsLeft - 1);
      total += blinkStateMap.get(interState);
    }
    blinkStateMap.put(state, total);
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p11/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Stone> stones = new ArrayList<>(); 
    Map<State, Long> blinkStateMap = new HashMap<>();

    String[] split = lines.get(0).split("\\s");
    for (String s : split) {
      stones.add(new Stone(Long.parseLong(s)));
    }

    long answer = 0;
    for (Stone stone : stones) {
      long total = 0;
      runStones(stone, blinkStateMap, ITERATIONS);
      answer += blinkStateMap.get(new State(stone.value, ITERATIONS));
    }
            
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
