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
  public static final Pattern REGEX = Pattern.compile("(\\d+)A");

  public static final int NUM_ROBOTS = 25;

  public record Code(char[] digits, int number) {}
  public static class Numpad {
    private char currentValue;
    private int currentRow;
    private int currentCol;

    public Numpad() {
      currentValue = 'A';
      currentRow = getRow(currentValue);
      currentCol = getCol(currentValue);
    }

    private int getRow(char value) {
      if (value == 'A' || value == '0') {
        return 3;
      } else {
        return 2 - ((Integer.parseInt(String.valueOf(value)) - 1) / 3);
      }
    }

    private int getCol(char value) {
      if (value == 'A') {
        return 2;
      } else if (value == '0') {
        return 1;
      } else {
        return Math.floorMod(Integer.parseInt(String.valueOf(value)) - 1, 3);
      }
    }

    public Set<String> goTo(char value) {
      int newRow = getRow(value);
      int newCol = getCol(value);

      String hor = "";
      if (newCol > currentCol) {
        hor = ">".repeat(newCol - currentCol);
      } else if (newCol < currentCol) {
        hor = "<".repeat(currentCol - newCol);
      }

      String vert = "";
      if (newRow > currentRow) {
        vert = "v".repeat(newRow - currentRow);
      } else if (newRow < currentRow) {
        vert = "^".repeat(currentRow - newRow);
      }

      Set<String> results = new HashSet<>();
      if (currentCol == 0 && newRow == 3) {
        results.add(hor + vert + "A");
      } else if (currentRow == 3 && newCol == 0) {
        results.add(vert + hor + "A");
      } else {
        results.add(vert + hor + "A");
        results.add(hor + vert + "A");
      }

      currentValue = value;
      currentRow = newRow;
      currentCol = newCol;

      return results;
    }
  }

  public static class DirectionPad {
    private char currentValue;
    private int currentRow;
    private int currentCol;

    public DirectionPad() {
      currentValue = 'A';
      currentRow = getRow(currentValue);
      currentCol = getCol(currentValue);
    }

    private int getRow(char value) {
      switch (value) {
        case 'A':
        case '^':
          return 0;
        case '<':
        case '>':
        case 'v':
        default:
          return 1;
      }
    }

    private int getCol(char value) {
      switch (value) {
        case '<':
          return 0;
        case 'v':
        case '^':
          return 1;
        case 'A':
        case '>':
        default:
          return 2;
      }
    }

    public Set<String> goTo(char value) {
      int newRow = getRow(value);
      int newCol = getCol(value);

      String hor = "";
      if (newCol > currentCol) {
        hor = ">".repeat(newCol - currentCol);
      } else if (newCol < currentCol) {
        hor = "<".repeat(currentCol - newCol);
      }

      String vert = "";
      if (newRow > currentRow) {
        vert = "v".repeat(newRow - currentRow);
      } else if (newRow < currentRow) {
        vert = "^".repeat(currentRow - newRow);
      }

      Set<String> results = new HashSet<>();
      if (currentRow != newRow) {
        if (newCol == 0) {
          results.add(vert + hor + "A");  
        } else if (currentCol == 0) {
          results.add(hor + vert + "A");
        } else {
          results.add(hor + vert + "A");
          results.add(vert + hor + "A");
        }
      } else {
        results.add(hor + vert + "A");
        results.add(vert + hor + "A");
      }
      
      currentValue = value;
      currentRow = newRow;
      currentCol = newCol;
      return results;
    }
  }

  public static long calculateLength(Code code, Map<Key, Long> cache) {
    Numpad numpad = new Numpad();
    long total = 0;
    for (int i = 0; i < code.digits.length; i++) {
      Set<String> chunks = numpad.goTo(code.digits[i]);
      long value = Long.MAX_VALUE;
      for (String chunk : chunks) {
        value = Math.min(value, calculateExpansion(chunk, NUM_ROBOTS, cache));
      }
      total += value;
    }

    return total;
  }

  public record Key(String s, int n) {}

  public static long calculateExpansion(String s, int n, Map<Key, Long> cache) {
    if (n == 0) {
      return s.length();
    }

    Key key = new Key(s, n);
    if (cache.containsKey(key)) {
      return cache.get(key);
    }

    long answer = 0;
    DirectionPad dpad = new DirectionPad();
    for (int i = 0; i < s.length(); i++) {
      Set<String> chunks = dpad.goTo(s.charAt(i));
      long value = Long.MAX_VALUE;
      for (String chunk : chunks) {
        value = Math.min(value, calculateExpansion(chunk, n - 1, cache));
      }
      answer += value;
    }
    cache.put(key, answer);
    return answer;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p21/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Code> codes = new ArrayList<>();
    for (String line : lines) {
      Matcher m = REGEX.matcher(line);
      m.find();
      String s = m.group(1);
      int number = Integer.parseInt(s);
      char[] digits = new char[4];
      digits[3] = 'A';
      for (int i = 0; i < s.length(); i++) {
        digits[i] = s.charAt(i);
      }

      codes.add(new Code(digits, number));
    }

    Map<Key, Long> cache = new HashMap<>();
    long answer = 0;
    for (Code code : codes) {
      long length = calculateLength(code, cache);
      long complexity = (length * code.number); 
      System.out.println(code.number + " " + length + " " + complexity);
      answer += complexity;
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
