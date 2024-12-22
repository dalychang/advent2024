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
  private static final long PRUNE_NUMBER = 16777216L;
  
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

    long answer = 0;
    for (long secretNumber : secretNumbers) {
      long number = secretNumber;
      for (int i = 0; i < 2000; i++) {
        number = calculateNext(number);
      }
      answer += number;
      System.out.println(secretNumber + ": " + number);
    }

    //System.out.println(secretNumbers);

    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
