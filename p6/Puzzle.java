package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Puzzle {

  public static enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0),
    SOUTH(0, 1);
    
    public int dx;
    public int dy;
    
    Direction(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
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
      }
      throw new RuntimeException();
    }
  };

  public static class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
      Position p = (Position) obj;
      return p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public String toString() {
      return String.format("(%d, %d)", x, y);
    }
  }

  public static class DirectedPosition {
    public final Position position;
    public final Direction direction;

    public DirectedPosition(Position position, Direction direction) {
      this.position = position;
      this.direction = direction;
    }

    public DirectedPosition moveForward() {
      return new DirectedPosition(new Position(position.x + direction.dx, position.y + direction.dy), direction);
    }

    @Override
    public boolean equals(Object obj) {
      DirectedPosition p = (DirectedPosition) obj;
      return p.position.equals(position) && p.direction == direction;
    }

    @Override
    public int hashCode() {
      return Objects.hash(position, direction);
    }

    @Override
    public String toString() {
      return String.format("(%d, %d) - %s", position.x, position.y, direction);
    }
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p6/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    boolean[][] grid = new boolean[lines.size()][lines.get(0).length()];
    
    Set<DirectedPosition> visitedDirectedPositions = new HashSet<>();
    Set<Position> visitedPositions = new HashSet<>();
    DirectedPosition currentPosition = null;
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      for (int j = 0; j < line.length(); j++) {
        if (line.charAt(j) == '#') {
          grid[i][j] = true;
        } else if (line.charAt(j) == '^') {
          Position position = new Position(j, i);
          visitedPositions.add(position);
          currentPosition = new DirectedPosition(position, Direction.NORTH);
        }
      }
    }

    Helper.printBitmap(grid, '#', '.');
    while (!visitedDirectedPositions.contains(currentPosition)) {
      visitedDirectedPositions.add(currentPosition);
      DirectedPosition next = currentPosition.moveForward();
      if (next.position.x < 0 || next.position.x >= grid[0].length || next.position.y < 0 || next.position.y >= grid.length) {
        break;
      } else if (grid[next.position.y][next.position.x]) {
        currentPosition = new DirectedPosition(currentPosition.position, currentPosition.direction.turnRight());
        continue;
      }
      currentPosition = next;
      visitedPositions.add(currentPosition.position);

      //System.out.println(currentPosition);     
    }

    long answer = visitedPositions.size();
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
