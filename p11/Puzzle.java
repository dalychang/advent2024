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

public class Puzzle {

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

  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p11/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Stone> stones = new ArrayList<>(); 
    String[] split = lines.get(0).split("\\s");
    for (String s : split) {
      stones.add(new Stone(Long.parseLong(s)));
    }

    for (int i = 0; i < 25; i++) {
      List<Stone> updatedStones = new ArrayList<>();
      for (Stone stone : stones) {
        updatedStones.addAll(stone.increment());
      }
      stones = updatedStones;
    }
    
    long answer = stones.size();
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
