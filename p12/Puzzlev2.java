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

  public record Pair(int area, int perimeter) {}

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

  public static int traverseAgain(char[][] grid, boolean[][] visited, int maxX, int maxY, char letter) {
    int horizontal = 0;

    for (int y = 0; y < maxY; y++) {
      char prevTop = ' ';
      char prevMiddle = ' ';
      char prevBottom =  ' ';
      for (int x = 0; x < maxX; x++) {
        char top = (y == 0) ? '?' : (grid[y - 1][x] == letter ? letter : '?');
        char middle = grid[y][x];
        char bottom = (y == maxY - 1) ? '?' : (grid[y + 1][x] == letter ? letter : '?');

        if (middle == letter && middle == prevMiddle && visited[y][x]) {
          if (top == prevTop && top != letter) {
            horizontal++;
          }
          if (bottom == prevBottom && bottom != letter) {
            horizontal++;
          }
        }
        prevTop = top;
        prevMiddle = middle;
        prevBottom = bottom;
      }
    }

    int vertical = 0;
    for (int x = 0; x < maxX; x++) {
      char prevLeft = ' ';
      char prevMiddle = ' ';
      char prevRight =  ' ';
      for (int y = 0; y < maxY; y++) {      
        char left = (x == 0) ? '?' : (grid[y][x - 1] == letter ? letter : '?');
        char middle = grid[y][x];
        char right = (x == maxX - 1) ? '?' : (grid[y][x + 1] == letter ? letter : '?');

        if (middle == letter && middle == prevMiddle && visited[y][x]) {
          if (left == prevLeft && left != letter) {
            vertical++;
          }
          if (right == prevRight && right != letter) {
            vertical++;
          }
        }
        prevLeft = left;
        prevMiddle = middle;
        prevRight = right;
      }
    }

    return horizontal + vertical;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p12/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    int maxX = lines.get(0).length();
    int maxY = lines.size();
    char[][] grid = new char[maxY][maxX];
    for (int y = 0; y < maxY; y++) {
      String line = lines.get(y);
      for (int x = 0; x < maxX; x++) {
        grid[y][x] = line.charAt(x);
      }
    }

    boolean[][] visited = new boolean[maxY][maxX];
    long answer = 0;
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        char c = grid[y][x];
        if (!visited[y][x]) {
          boolean[][] tempVisited = new boolean[maxY][maxX];
          Pair p = traverse(grid, tempVisited, x, y, c);
          int minusPerimeter = traverseAgain(grid, tempVisited, maxX, maxY, c);
          answer += p.area * (p.perimeter - minusPerimeter);

          for (int ty = 0; ty < maxY; ty++) {
            for (int tx = 0; tx < maxX; tx++) {
              visited[ty][tx] |= tempVisited[ty][tx];
            }
          }
        }
      }
    }

    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
