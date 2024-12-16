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

  private static final Pattern PATTERN = Pattern.compile("p=([+-]?\\d+),([+-]?\\d+) v=([+-]?\\d+),([+-]?\\d+)");
  private static final int GRID_X = 101;
  private static final int GRID_Y = 103;
  private static final int TIME_STEPS = 100;

  public record Position(int x, int y) {}
  public record Velocity(int x, int y) {}
  public record Robot(Position p, Velocity v) {}
  
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p14/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Robot> robots = new ArrayList<>();
    for (String line : lines) {
      Matcher m = PATTERN.matcher(line);
      m.find();
      robots.add(new Robot(new Position(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))), new Velocity(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)))));
    }

    List<Robot> movedRobots = new ArrayList<>();
    for (Robot r : robots) {
      int x = (r.p.x + r.v.x * TIME_STEPS) % GRID_X;
      int y = (r.p.y + r.v.y * TIME_STEPS) % GRID_Y;
      if (x < 0) x += GRID_X;
      if (y < 0) y += GRID_Y;
      movedRobots.add(new Robot(new Position(x, y), r.v));
    }

    int[][] quads = new int[2][2];
    for (Robot r : movedRobots) {
      if (r.p.x == GRID_X / 2 || r.p.y == GRID_Y / 2) {
        continue;
      }

      int x = r.p.x > (int)(GRID_X / 2) ? 1 : 0;
      int y = r.p.y > (int)(GRID_Y / 2) ? 1 : 0;
      quads[x][y]++;
    }

    long answer = quads[0][0] * quads[1][0] * quads[0][1] * quads[1][1];

    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}