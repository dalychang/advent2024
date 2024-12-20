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
  public record Position(int x, int y) {}
  public record Path(Position p, Long score) {}
  public record Cheat(Position start, Position end) {}

  public enum Direction {
    NORTH(0, -1),
    SOUTH(0, 1),
    WEST(-1, 0),
    EAST(1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }
  }
  
  public static long traverse(char[][] grid, int maxX, int maxY, Position start, Position end, Map<Position, Long> scoreMap) {
    List<Path> paths = new ArrayList<>();
    paths.add(new Path(start, 0L));
    scoreMap.put(start, 0L);

    long bestScore = Long.MAX_VALUE;
    while (!paths.isEmpty()) {
      Path path = paths.remove(paths.size() - 1);
      for (Direction d : Direction.values()) {
        Position nextPosition = new Position(path.p.x + d.dx, path.p.y + d.dy);
        if (grid[nextPosition.y][nextPosition.x] == '#') {
          continue;
        }

        long newScore = path.score + 1;
        if (nextPosition.equals(end)) {
          bestScore = Math.min(bestScore, newScore);
          scoreMap.put(nextPosition, newScore);
          continue;
        }
        
        if (scoreMap.getOrDefault(nextPosition, Long.MAX_VALUE) < newScore) {
          continue;
        }

        scoreMap.put(nextPosition, newScore);
        paths.add(new Path(nextPosition, newScore));
      }
    }

    return bestScore;
  }

  public static boolean inBounds(Position p, int maxX, int maxY) {
    return p.x >= 0 && p.y >=0 && p.x < maxX && p.y < maxY;
  }

  public static void calculateCheats(char[][] grid, int maxX, int maxY, Position start, Position end, Map<Position, Long> scoreMap, Map<Cheat, Long> cheatDeltaMap) {
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        if (grid[y][x] == '#' || grid[y][x] == 'E') {
          continue;
        }

        Position cheatStart = new Position(x, y);
        for (Direction sd : Direction.values()) {
          Position cheatPosition = new Position(x + sd.dx, y + sd.dy);
          if (grid[cheatPosition.y][cheatPosition.x] != '#') {
            continue;
          }

          for (Direction ed : Direction.values()) {
            Position cheatEnd = new Position(cheatPosition.x + ed.dx, cheatPosition.y + ed.dy);
            if (!inBounds(cheatEnd, maxX, maxY) || grid[cheatEnd.y][cheatEnd.x] == '#') {
              continue;
            }

            long scoreEnd = scoreMap.get(cheatEnd);
            long scoreStart = scoreMap.get(cheatStart);
            long delta = scoreEnd - scoreStart - 2;
            cheatDeltaMap.put(new Cheat(cheatStart, cheatEnd), delta);
          }
        }
      }
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p20/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Position start = null;
    Position end = null;
    char[][] grid = Helper.readCharGrid(lines);
    int maxX = grid[0].length;
    int maxY = grid.length;

    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        if (grid[y][x] == 'S') {
          start = new Position(x, y);
        } else if (grid[y][x] == 'E') {
          end = new Position(x, y);
        }
      }
    }

    Map<Position, Long> scoreMap = new HashMap<>();

    long normalScore = traverse(grid, maxX, maxY, start, end, scoreMap);
    Map<Cheat, Long> cheatDeltaMap = new HashMap<>();

    calculateCheats(grid, maxX, maxY, start, end, scoreMap, cheatDeltaMap);
    
    long answer = 0;
    for (Cheat cheat : cheatDeltaMap.keySet()) {
      long delta = cheatDeltaMap.get(cheat);
      if (delta >= 100) {
        answer++;
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
