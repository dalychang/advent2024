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

public class Draw {

  public record Pair(int area, int perimeter) {}
  
  public static long calculate(String s) {
    return 1;
  }

  public static boolean isValid(char[][] grid, int x, int y) {
    return x >=0 && y >= 0 && x < grid[0].length && y < grid.length;
  }

  public static int findNeighbors(char[][] grid, int x, int y, char letter) {
    int neighbors = 0;
    if (!isValid(grid, x - 1, y) || grid[y][x - 1] != letter) {
      neighbors++;
    }
    if (!isValid(grid, x + 1, y) || grid[y][x + 1] != letter) {
      neighbors++;
    }
    if (!isValid(grid, x, y - 1) || grid[y - 1][x] != letter) {
      neighbors++;
    }
    if (!isValid(grid, x, y + 1) || grid[y + 1][x] != letter) {
      neighbors++;
    }
    return neighbors;
  }

  public static Pair traverse(char[][] grid, boolean[][] visited, int x, int y, char letter) {
    if (visited[y][x]) {
      return new Pair(0, 0);
    }

    int perimeter = 0;
    int area = 1;
    visited[y][x] = true;
    if (isValid(grid, x - 1, y)) {
      if (grid[y][x - 1] == letter) {
        Pair p = traverse(grid, visited, x - 1, y, letter);
        area += p.area;
        perimeter += p.perimeter;
      } else {
        perimeter++;
      }
    } else {
      perimeter++;
    }

    if (isValid(grid, x + 1, y)) {
      if (grid[y][x + 1] == letter) {
        Pair p = traverse(grid, visited, x + 1, y, letter);
        area += p.area;
        perimeter += p.perimeter;
      } else {
        perimeter++;
      }
    } else {
      perimeter++;
    }

    if (isValid(grid, x, y - 1)) {
      if (grid[y - 1][x] == letter) {
        Pair p = traverse(grid, visited, x, y - 1, letter);
        area += p.area;
        perimeter += p.perimeter;
      } else {
        perimeter++;
      }
    } else {
      perimeter++;
    }

    if (isValid(grid, x, y + 1)) {
      if (grid[y + 1][x] == letter) {
        Pair p = traverse(grid, visited, x, y + 1, letter);
        area += p.area;
        perimeter += p.perimeter;
      } else {
        perimeter++;
      }
    } else {
      perimeter++;
    }

    return new Pair(area, perimeter);
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p12/example.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    int maxX = lines.get(0).length();
    int maxY = lines.size();
    char[][] grid = new char[maxY][maxX];
    char[][] edges = new char[maxY + 1][maxX + 1];

    // Load grid
    for (int y = 0; y < maxY; y++) {
      String line = lines.get(y);
      for (int x = 0; x < maxX; x++) {
        grid[y][x] = line.charAt(x);
      }
    }

    // Calculate edges
    for (int y = 0; y < maxY + 1; y++) {
      for (int x = 0; x < maxX + 1; x++) {
        if (x == 0 && y == 0) {
          edges[y][x] = '┌';
          continue;
        } else if (x == maxX && y == maxY) {
          edges[y][x] = '┘';
          continue;
        } else if (x == 0 && y == maxY) {
          edges[y][x] = '└';
          continue;
        } else if (x == maxX && y == 0) {
          edges[y][x] = '┐';
          continue;
        }

        if (x == 0) {
          if (grid[y][x] == grid[y - 1][x]) {
            edges[y][x] = '|';
          } else {
            edges[y][x] = '├';
          }
          continue;
        } else if (x == maxX) {
          if (grid[y][x - 1] == grid[y - 1][x - 1]) {
            edges[y][x] = '|';
          } else {
            edges[y][x] = '┤';
          }
          continue;
        }

        if (y == 0) {
          if (grid[y][x] == grid[y][x - 1]) {
            edges[y][x] = '-';
          } else {
            edges[y][x] = '┬';
          }
          continue;
        } else if (y == maxY) {
          if (grid[y - 1][x] == grid[y - 1][x - 1]) {
            edges[y][x] = '─';
          } else {
            edges[y][x] = '┴';
          }
          continue;
        }

        char tl = grid[y - 1][x - 1];
        char tr = grid[y - 1][x];
        char bl = grid[y][x - 1];
        char br = grid[y][x];
        if (tl == tr && tr == br && br == bl) {
          edges[y][x] = ' ';
        } else if (tl == tr && tr == br) {
          edges[y][x] = '┐';
        } else if (tl == tr && tl == bl) {
          edges[y][x] = '┌';
        } else if (bl == br && tr == br) {
          edges[y][x] = '┘';
        } else if (bl == br && tl == bl) {
          edges[y][x] = '└';
        } else if (bl == br && tr == tl) {
          edges[y][x] = '─';
        } else if (tr == br && tl == bl) {
          edges[y][x] = '|';
        } else if (tr == br) {
          edges[y][x] = '┤';
        } else if (tl == bl) {
          edges[y][x] = '├';
        } else if (tr == tl) {
          edges[y][x] = '┬';
        } else if (br == bl) {
          edges[y][x] = '┴';
        } else {
          edges[y][x] = '┼';
        }
      }
    }

    for (int y = 0; y < maxY + 1; y++) {
      for (int x = 0; x < maxX + 1; x++) {
        System.out.print(edges[y][x]);
      }
      System.out.println();
    }

    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
