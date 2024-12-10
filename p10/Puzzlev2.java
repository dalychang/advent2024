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
  
  public static long calculate(String s) {
    return 1;
  }

  public static boolean isValid(int x, int y, int maxX, int maxY) {
    return x >= 0 && x < maxX && y >= 0 && y < maxY;
  }

  public static int countPosition(int[][] grid, int[][] paths, int target, int x, int y, int maxX, int maxY) {
    if (isValid(x, y, maxX, maxY) && grid[x][y] == target) {
      return paths[x][y];
    }

    return 0;
  }

  public static int find(int[][] grid, int[][] paths, int target, int x, int y, int maxX, int maxY) {
    int positions = countPosition(grid, paths, target + 1, x - 1, y, maxX, maxY)
                  + countPosition(grid, paths, target + 1, x + 1, y, maxX, maxY)
                  + countPosition(grid, paths, target + 1, x, y - 1, maxX, maxY)
                  + countPosition(grid, paths, target + 1, x, y + 1, maxX, maxY);
    
    return positions;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p10/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    int maxX = lines.get(0).length();
    int maxY = lines.size();
    int[][] grid = new int[maxX][maxY];
    int[][] paths = new int[maxX][maxY];

    for (int y = 0; y < maxY; y++) {
      String line = lines.get(y);
      for (int x = 0; x < maxX; x++) {
        grid[x][y] = Integer.parseInt(String.valueOf(line.charAt(x)));
        if (grid[x][y] == 9) {
          paths[x][y] = 1;
        }
      }
    }

    for (int i = 8; i >= 0; i--) {
      for (int x = 0; x < maxX; x++) {
        for (int y = 0; y < maxY; y++) {
          if (grid[x][y] == i) {
            paths[x][y] = find(grid, paths, i, x, y, maxX, maxY);
          }
        }
      }
    }

    long answer = 0;
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (grid[x][y] == 0) {
          answer += paths[x][y];
        }
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
