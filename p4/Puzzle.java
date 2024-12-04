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

  private static final String WORD = "XMAS";

  public static boolean findWord(List<String> lines, int x, int y, String word, int dx, int dy) {
    if (x < 0 || x >= lines.get(0).length()) {
      return false;
    }
    if (y < 0 || y >= lines.size()) {
      return false;
    }
    char c = word.charAt(0);
    if (lines.get(y).charAt(x) != c) {
      return false;
    }

    String newWord = word.substring(1);
    if (newWord.isEmpty()) {
      return true;
    }
    return findWord(lines, x + dx, y + dy, newWord, dx, dy);
  }

  public static int countWords(List<String> lines, int x, int y) {
    int count = 0;

    // X - 1
    count += findWord(lines, x, y, WORD, -1, -1) ? 1 : 0;
    count += findWord(lines, x, y, WORD, -1, 0) ? 1 : 0;
    count += findWord(lines, x, y, WORD, -1, 1) ? 1 : 0;

    // X
    count += findWord(lines, x, y, WORD, 0, -1) ? 1 : 0;
    count += findWord(lines, x, y, WORD, 0, 1) ? 1 : 0;

    // X + 1
    count += findWord(lines, x, y, WORD, 1, -1) ? 1 : 0;
    count += findWord(lines, x, y, WORD, 1, 0) ? 1 : 0;
    count += findWord(lines, x, y, WORD, 1, 1) ? 1 : 0;

    return count;
  }

  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p4/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    int answer = 0;
    for (int y = 0; y < lines.size(); y++) {
      String line = lines.get(y);
      for (int x = 0; x < line.length(); x++) {
        answer += countWords(lines, x, y);
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
