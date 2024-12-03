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
  
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p3/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    final Pattern regex1 = Pattern.compile("(mul\\(\\d+,\\d+\\))");
    final Pattern regex2 = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
    
    int answer = 0;
    for (String line : lines) {
      Matcher matcher = regex1.matcher(line);
      while (matcher.find()) {
        String intermediate = matcher.group(1);
        Matcher matcher2 = regex2.matcher(intermediate);
        matcher2.find();
        int value1 = Integer.parseInt(matcher2.group(1));
        int value2 = Integer.parseInt(matcher2.group(2));
        int product = value1 * value2;
        answer += product;
      }
    }

    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
