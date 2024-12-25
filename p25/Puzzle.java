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
  public record Key(List<Integer> pins) {}
  public record Lock(List<Integer> pins) {}
    
  public static void main(String[] args) throws Exception {
    List<String> lines = Helper.loadFile("dev_advent/p25/input.txt");
    lines.add("");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Key> keys = new ArrayList<>();
    List<Lock> locks = new ArrayList<>();
    char[][] grid = new char[7][5];
    int y = 0;
    for (String line : lines) {
      if (line.isEmpty()) {
        // Convert

        if (grid[0][0] == '#') {
          // Lock

          List<Integer> pins = new ArrayList<>();
          for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 7; j++) {
              if (grid[j][i] == '.') {
                pins.add(j - 1);
                break;
              }
            }
          }
          locks.add(new Lock(pins));
        } else {
          // Key
          List<Integer> pins = new ArrayList<>();
          for (int i = 0; i < 5; i++) {
            for (int j = 5; j >= 0; j--) {
              if (grid[j][i] == '.') {
                pins.add(5 - j);
                break;
              }
            }
          }
          keys.add(new Key(pins));
        }

        grid = new char[7][5];
        y = 0;
        continue;
      }

      for (int i = 0; i < line.length(); i++) {
        grid[y][i] = line.charAt(i);
      }
      y++;
    }

    //System.out.println(locks);
    //System.out.println(keys);

    int answer = 0;
    for (Lock lock : locks) {
      for (Key key : keys) {
        boolean match = true;
        for (int i = 0; i < 5; i++) {
          if (key.pins.get(i) + lock.pins.get(i) > 5) {
            match = false;
          }
        }
        if (match) {
          answer++;
        }
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
