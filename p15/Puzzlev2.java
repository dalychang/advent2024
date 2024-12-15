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

  public record Position(int x, int y) {}
  public record Replacement(int x, int y, char c) {}
  public enum Move {
    UP(0, -1, '^', false),
    DOWN(0, 1, 'v', false),
    LEFT(-1, 0, '<', true),
    RIGHT(1, 0, '>', true);

    public final int dx;
    public final int dy;
    public final char letter;
    public boolean isHorizontal;

    Move(int dx, int dy, char letter, boolean isHorizontal) {
      this.dx = dx;
      this.dy = dy;
      this.letter = letter;
      this.isHorizontal = isHorizontal;
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
        if (m.isHorizontal) {
          while (grid[y][x] == '[' || grid[y][x] == ']') {
            x += m.dx;
            y += m.dy;
          }
          if (grid[y][x] == '.') {
            while (x != p.x + m.dx) {
              grid[y][x] = grid[y][x - m.dx];
              x -= m.dx;
            }
            grid[p.y + m.dy][p.x + m.dx] = '.';
            p = new Position(p.x + m.dx, p.y + m.dy);
          }
        } else {
          // Vertical.
          List<Replacement> pushList = new ArrayList<>();
          List<Replacement> emptyList = new ArrayList<>();
          List<Position> pushQueue = new ArrayList<>();
          boolean canPush = true;
          pushQueue.add(new Position(x, y));
          pushList.add(new Replacement(x, y, '.'));
          if (grid[y][x] == '[') {
            pushQueue.add(new Position(x + 1, y));
            emptyList.add(new Replacement(x + 1, y, '.'));
          } else {
            pushQueue.add(new Position(x - 1, y));
            emptyList.add(new Replacement(x - 1, y, '.'));
          }
          while (!pushQueue.isEmpty()) {
            Position q = pushQueue.remove(0);
            emptyList.add(new Replacement(q.x, q.y, '.'));
            pushList.add(new Replacement(q.x + m.dx, q.y + m.dy, grid[q.y][q.x]));
            char nc = grid[q.y + m.dy][q.x + m.dx];
            if (nc == '#') {
              canPush = false;
              break;
            } else if (nc == '[') {
              pushQueue.add(new Position(q.x + m.dx, q.y + m.dy));
              pushQueue.add(new Position(q.x + m.dx + 1, q.y + m.dy));
            } else if (nc == ']') {
              pushQueue.add(new Position(q.x + m.dx, q.y + m.dy));
              pushQueue.add(new Position(q.x + m.dx - 1, q.y + m.dy));
            }
          }
          if (canPush) {
            for (Replacement r : emptyList) {
              grid[r.y][r.x] = r.c;
            }
            for (Replacement r : pushList) {
              grid[r.y][r.x] = r.c;
            }
            p = new Position(p.x + m.dx, p.y + m.dy); 
          }
        }
      }
      //System.out.println(m);
      //Helper.printCharMap(grid);
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
    
    int maxX = lines.get(0).length() * 2;
    char[][] grid = new char[maxY][maxX];

    Position bot = null;
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x+=2) {
        char c = lines.get(y).charAt(x/2);
        if (c == '@') {
          bot = new Position(x, y);
          grid[y][x] = '.';
          grid[y][x+1] = '.';
        } else if (c == 'O') {
          grid[y][x] = '[';
          grid[y][x+1] = ']';
        } else {
          grid[y][x] = c;
          grid[y][x+1] = c;
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
        if (grid[y][x] == '[') {
          answer += (y * 100 + x);
        }
      }
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
