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
  public static Set<Character> VALID_CHARS = Set.of('S', 'M');

  public static boolean findWord(List<String> lines, int x, int y) {
    if (x - 1 < 0 || x + 1 >= lines.get(0).length()) {
      return false;
    }
    if (y - 1 < 0 || y + 1 >= lines.size()) {
      return false;
    }

    if (lines.get(y).charAt(x) != 'A') {
      return false;
    }

    Set<Character> part1 = new HashSet<>();
    Set<Character> part2 = new HashSet<>();

    if (VALID_CHARS.contains(lines.get(y - 1).charAt(x - 1))) {
      part1.add(lines.get(y - 1).charAt(x - 1));
    }
    if (VALID_CHARS.contains(lines.get(y + 1).charAt(x + 1))) {
      part1.add(lines.get(y + 1).charAt(x + 1));
    }

    if (VALID_CHARS.contains(lines.get(y + 1).charAt(x - 1))) {
      part2.add(lines.get(y + 1).charAt(x - 1));
    }
    if (VALID_CHARS.contains(lines.get(y - 1).charAt(x + 1))) {
      part2.add(lines.get(y - 1).charAt(x + 1));
    }

    return part1.size() == 2 && part2.size() == 2;
  }

  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p4/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    int answer = 0;
    for (int y = 0; y < lines.size(); y++) {
      String line = lines.get(y);
      for (int x = 0; x < line.length(); x++) {
        if (findWord(lines, x, y)) {
          answer++;
        }
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
