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

public class Puzzlev4 {

  public record Position(int x, int y) {}
  
  public static long calculate(String s) {
    return 1;
  }

  public static boolean isValid(int x, int y, int maxX, int maxY) {
    return x >= 0 && x < maxX && y >= 0 && y < maxY;
  }

  public static Set<Position> countPosition(int[][] grid, List<List<Set<Position>>> setGrid, int target, int x, int y, int maxX, int maxY) {
    if (isValid(x, y, maxX, maxY) && grid[y][x] == target) {
      return setGrid.get(y).get(x);
    }

    return Set.of();
  }

  public static void find(int[][] grid, List<List<Set<Position>>> setGrid, int target, int x, int y, int maxX, int maxY) {
    Set<Position> set = setGrid.get(y).get(x);
    set.addAll(countPosition(grid, setGrid, target + 1, x - 1, y, maxX, maxY));
    set.addAll(countPosition(grid, setGrid, target + 1, x + 1, y, maxX, maxY));
    set.addAll(countPosition(grid, setGrid, target + 1, x, y - 1, maxX, maxY));
    set.addAll(countPosition(grid, setGrid, target + 1, x, y + 1, maxX, maxY));
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p10/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    int maxX = lines.get(0).length();
    int maxY = lines.size();
    int[][] grid = new int[maxX][maxY];
    List<List<Set<Position>>> setGrid = new ArrayList<>();

    for (int y = 0; y < maxY; y++) {
      String line = lines.get(y);
      List<Set<Position>> list = new ArrayList<>();
      for (int x = 0; x < maxX; x++) {
        grid[y][x] = Integer.parseInt(String.valueOf(line.charAt(x)));
        Set<Position> set = new HashSet<>();
        if (grid[y][x] == 9) {
          set.add(new Position(x, y));
        }
        list.add(set);
      }
      setGrid.add(list);
    }

    Helper.printIntMap(grid);

    for (int i = 8; i >= 0; i--) {
      for (int x = 0; x < maxX; x++) {
        for (int y = 0; y < maxY; y++) {
          if (grid[y][x] == i) {
            find(grid, setGrid, i, x, y, maxX, maxY);
          }
        }
      }
    }

    long answer = 0;
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (grid[y][x] == 0) {
          answer += setGrid.get(y).get(x).size();
        }
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
