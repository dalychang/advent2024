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
  public static final Pattern REGEX = Pattern.compile("(\\d+)A");

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
      if (newRow == 3 && currentCol == 0) {
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
      results.add(hor + vert + "A");
      if (currentCol != 0 && newCol != 0) {
        results.add(vert + hor + "A");
      }

      currentValue = value;
      currentRow = newRow;
      currentCol = newCol;
      return results;
    }
  }
  
  public static long calculate(String s) {
    return 1;
  }

  public static Set<String> runDPad(Set<String> presses) {
    Set<String> results = new HashSet<>();
    for (String input : presses) {
      DirectionPad dpad = new DirectionPad();
      List<String> dPresses = new ArrayList<>();
      dPresses.add("");
      for (int i = 0; i < input.length(); i++) {
        List<String> newDPresses = new ArrayList<>();
        for (String s : dpad.goTo(input.charAt(i))) {
          for (String cp : dPresses) {
            newDPresses.add(cp + s);
          }
        }
        dPresses = newDPresses;
      }
      results.addAll(dPresses);
    }

    return results;
  }

  public static long calculateLength(Code code) {
    Numpad numpad = new Numpad();
    List<String> numPresses = new ArrayList<>();
    numPresses.add("");
    StringBuilder numPressSb = new StringBuilder();
    for (int i = 0; i < code.digits.length; i++) {
      List<String> newPresses = new ArrayList<>();
      for (String s : numpad.goTo(code.digits[i])) {
        for (String cp : numPresses) {
          newPresses.add(cp + s);
        }
      }
      numPresses = newPresses;
    }

    Set<String> dPress0 = runDPad(new HashSet(numPresses));
    Set<String> dPress1 = runDPad(dPress0);
    
    long length = Long.MAX_VALUE;
    for (String s : dPress1) {
      length = Math.min(length, s.length());
    }
    return length;
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

    long answer = 0;
    for (Code code : codes) {
      long length = calculateLength(code);
      long complexity = (length * code.number); 
      System.out.println(code.number + " " + length + " " + complexity);
      answer += complexity;
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
