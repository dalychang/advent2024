package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {
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

    public Direction turnLeft() {
      switch (this) {
        case NORTH:
          return WEST;
        case EAST:
          return NORTH;
        case SOUTH:
          return EAST;
        case WEST:
          return SOUTH;
        default:
          return null;  
      }
    }

    public Direction turnRight() {
      switch (this) {
        case NORTH:
          return EAST;
        case EAST:
          return SOUTH;
        case SOUTH:
          return WEST;
        case WEST:
          return NORTH;
        default:
          return null;  
      }
    }
  }

  public record DirectedPosition(Position p, Direction d) {}
  public record Path(DirectedPosition dp, Set<Position> visited, int score) {}

  public static Path createPath(char[][] grid, int maxX, int maxY, Path cp, Direction nd, int scoreDelta, Map<DirectedPosition, Integer> bestScores) {
    DirectedPosition next = new DirectedPosition(new Position(cp.dp.p.x + nd.dx, cp.dp.p.y + nd.dy), nd);
    if (grid[cp.dp.p.y][cp.dp.p.x] == '#') {
      return null;
    }
    if (cp.visited.contains(next.p)) {
      return null;
    }
    int newScore = cp.score + scoreDelta;
    if (bestScores.getOrDefault(next, Integer.MAX_VALUE) <= newScore) {
      return null;
    }
    bestScores.put(next, newScore);

    Set<Position> nv = new HashSet<>(cp.visited);
    nv.add(next.p);
    return new Path(next, nv, newScore);
  }

  public static int distance(Position p1, Position p2) {
    return (int) Math.sqrt(Math.sqrt(p1.x - p2.x) + Math.sqrt(p1.y - p2.y));
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p16/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    char[][] grid = Helper.readCharGrid(lines);
    Helper.printCharMap(grid);
    
    int maxX = grid[0].length;
    int maxY = grid.length;
    Position startPosition = null;
    Position endPosition = null;
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        if (grid[y][x] == 'E') {
          endPosition = new Position(x, y);
        } else if (grid[y][x] == 'S') {
          startPosition = new Position(x, y);
        }
      }
    }

    DirectedPosition start = new DirectedPosition(startPosition, Direction.EAST);
    PriorityQueue<Path> queue = new PriorityQueue<Path>((i1, i2) -> i1.score - i2.score);
    queue.add(new Path(start, Set.of(startPosition), 0));

    Map<DirectedPosition, Integer> bestScores = new HashMap<>();

    System.out.println("start is " + startPosition);
    System.out.println("end is " + endPosition);
    
    int bestScore = Integer.MAX_VALUE;
    while (!queue.isEmpty()) {
      Path path = queue.poll();
      if (path.score > bestScore) {
        continue;
      }

      // Forward.
      {
        Path np = createPath(grid, maxX, maxY, path, path.dp.d, 1, bestScores);
        if (np != null) {
          if (np.dp.p.equals(endPosition)) {
            bestScore = Math.min(bestScore, np.score);
          } else {
            queue.add(np);
          }
        }
      }
      // Left
      {
        Path np = createPath(grid, maxX, maxY, path, path.dp.d.turnLeft(), 1001, bestScores);
        if (np != null) {
          if (np.dp.p.equals(endPosition)) {
            bestScore = Math.min(bestScore, np.score);
          } else {
            queue.add(np);
          }
        }
      }
      // Right
      {
        Path np = createPath(grid, maxX, maxY, path, path.dp.d.turnRight(), 1001, bestScores);
        if (np != null) {
          if (np.dp.p.equals(endPosition)) {
            bestScore = Math.min(bestScore, np.score);
          } else {
            queue.add(np);
          }
        }
      }
      // Back
      {
        Path np = createPath(grid, maxX, maxY, path, path.dp.d.turnRight().turnRight(), 2001, bestScores);
        if (np != null) {
          if (np.dp.p.equals(endPosition)) {
            bestScore = Math.min(bestScore, np.score);
          } else {
            queue.add(np);
          }
        }
      }
    }

    long answer = bestScore;
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
