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
  public static final int MAX_COORD = 71;
  public static final int FIRST_STEPS = 1024;

  public record Position(int x, int y) {}
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

  public record Path(Position cp, int steps) {}

  public static boolean isValid(Position p, boolean[][] grid) {
    return p.x >= 0 && p.y >= 0 && p.x < MAX_COORD && p.y < MAX_COORD && !grid[p.y][p.x];
  }
  
  public static long traverse(boolean[][] grid, Position start, Position end) {
    int[][] bestPath = new int[MAX_COORD][MAX_COORD];
    for (int y = 0; y < MAX_COORD; y++) {
      for (int x = 0; x < MAX_COORD; x++) {
        bestPath[y][x] = Integer.MAX_VALUE;
      }
    }

    List<Path> paths = new ArrayList<>();
    int bestScore = Integer.MAX_VALUE;
    paths.add(new Path(start, 0));

    while (!paths.isEmpty()) {
      Path path = paths.remove(paths.size() - 1);
      for (Direction d : Direction.values()) {
        Position nPos = new Position(path.cp.x + d.dx, path.cp.y + d.dy);
        if (nPos.equals(end)) {
          bestScore = Math.min(bestScore, path.steps + 1);
          continue;
        }

        if (isValid(nPos, grid) && bestPath[nPos.y][nPos.x] > path.steps + 1) {
          bestPath[nPos.y][nPos.x] = path.steps + 1;
          paths.add(new Path(nPos, path.steps + 1));
        }
      }
    }

    return bestScore;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p18/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Position> positions = new ArrayList<>();
    for (String line : lines) {
      String[] split = line.split(",");
      positions.add(new Position(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
    }

    System.out.println(positions);
    System.out.println(positions.size());

    boolean[][] grid = new boolean[MAX_COORD][MAX_COORD];
    int count = 0;
    for (Position p : positions) {
      if (count == FIRST_STEPS) break;
      grid[p.y][p.x] = true;
      count++;
    }

    Helper.printBitmap(grid, '#', ' ');

    for (int i = FIRST_STEPS; i < positions.size(); i++) {
      System.out.println("Trying " + i);
      grid[positions.get(i).y][positions.get(i).x] = true;
      long answer = traverse(grid, new Position(0, 0), new Position(MAX_COORD - 1, MAX_COORD - 1));
      if (answer == Integer.MAX_VALUE) {
        System.out.println("answer is " + positions.get(i).x + "," + positions.get(i).y);
        break;
      }
    }
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
