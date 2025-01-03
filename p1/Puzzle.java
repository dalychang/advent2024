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
  
   public static long calculate(String s) {
    return 1;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p1/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Integer> list1 = new ArrayList<>();
    List<Integer> list2 = new ArrayList<>();

    for (String line : lines) {
      line = line.replaceAll("\\s+", " ");
      String[] lineSplit = line.split("\\s");
      list1.add(Integer.parseInt(lineSplit[0]));
      list2.add(Integer.parseInt(lineSplit[1]));
    }

    Collections.sort(list1);
    Collections.sort(list2);

    int differences = 0;
    for (int i = 0; i < list1.size(); i++) {
      differences += Math.abs(list1.get(i) - list2.get(i));
    }
        
    System.out.println("answer is " + differences);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
