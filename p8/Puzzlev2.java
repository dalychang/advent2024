package dev.advent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
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

  public record Position(int x, int y) {}

  public static boolean isValid(char[][] grid, Position p) {
    return p.x >= 0 && p.x < grid.length && p.y >= 0 && p.y < grid[0].length;
  }
  
  public static Set<Position> calculateAntiNodes(char[][] grid, Position p1, Position p2) {
    Set<Position> an = new HashSet<>();
    int dx = p2.x - p1.x;
    int dy = p2.y - p1.y;
    Position ap1 = new Position(p1.x - dx, p1.y - dy);
    Position ap2 = new Position(p2.x + dx, p2.y + dy);

    for (int i = 0;; i++) {
      Position p = new Position(p1.x - i * dx, p1.y - i * dy);
      if (isValid(grid, p)) {
        an.add(p);
      } else {
        break;
      }
    }

    for (int i = 0;; i++) {
      Position p = new Position(p2.x + i * dx, p2.y + i * dy);
      if (isValid(grid, p)) {
        an.add(p);
      } else {
        break;
      }
    }

    return an;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p8/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    Multimap<Character, Position> charMap = HashMultimap.create();
    char[][] grid = new char[lines.size()][lines.get(0).length()];
    for (int y = 0; y < lines.size(); y++) {
      for (int x = 0; x < lines.get(0).length(); x++) {
        grid[x][y] = lines.get(y).charAt(x);
        if (grid[x][y] != '.') {
          charMap.put(grid[x][y], new Position(x, y));
        }
      }
    }
    
    Set<Position> antinodes = new HashSet<>();
    for (char c : charMap.keySet()) {
      List<Position> points = new ArrayList<>(charMap.get(c));

      for (int i = 0; i < points.size(); i++) {
        for (int j = i + 1; j < points.size(); j++) {
          Set<Position> an = calculateAntiNodes(grid, points.get(i), points.get(j));
          antinodes.addAll(an);
        }
      }
    }
        
    System.out.println("answer is " + antinodes.size());     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
