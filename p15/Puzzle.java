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
  public enum Move {
    UP(0, -1, '^'),
    DOWN(0, 1, 'v'),
    LEFT(-1, 0, '<'),
    RIGHT(1, 0, '>');

    public final int dx;
    public final int dy;
    public final char letter;

    Move(int dx, int dy, char letter) {
      this.dx = dx;
      this.dy = dy;
      this.letter = letter;
    }

    public static Move toMove(char c) {
      for (Move m : Move.values()) {
        if (c == m.letter) {
          return m;
        }
      }
      return null;
    }
  }
  
  public static void run(char[][] grid, Position startPosition, List<Move> moves, int maxX, int maxY) {
    Position p = startPosition;
    for (Move m : moves) {
      char c = grid[p.y + m.dy][p.x + m.dx];
      if (c == '.') {
        p = new Position(p.x + m.dx, p.y + m.dy);
      } else if (c == '#') {
        // Do nothing.
      } else {
        // Box.
        int x = p.x + m.dx;
        int y = p.y + m.dy;
        while (grid[y][x] == 'O') {
          x += m.dx;
          y += m.dy;
        }
        if (grid[y][x] == '.') {
          grid[y][x] = 'O';
          grid[p.y + m.dy][p.x + m.dx] = '.';
          p = new Position(p.x + m.dx, p.y + m.dy);
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p15/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    int maxY = 0;
    for (String line : lines) {
      if (line.trim().isEmpty()) {
        break;
      }
      maxY++;
    }
    
    int maxX = lines.get(0).length();
    char[][] grid = new char[maxY][maxX];

    Position bot = null;
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        char c = lines.get(y).charAt(x);
        if (c == '@') {
          bot = new Position(x, y);
          grid[y][x] = '.';
        } else {
          grid[y][x] = c;
        }
      }
    }
    
    List<Move> moves = new ArrayList<>();
    for (int y = maxY; y < lines.size(); y++) {
      String line = lines.get(y);
      for (int i = 0; i < line.length(); i++) {
        moves.add(Move.toMove(line.charAt(i)));
      }
    }

    run(grid, bot, moves, maxX, maxY);
    
    //Helper.printCharMap(grid);
    //System.out.println(moves);

    long answer = 0;
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        if (grid[y][x] == 'O') {
          answer += (y * 100 + x);
        }
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
