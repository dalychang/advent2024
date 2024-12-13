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
  private static final Pattern BUTTON_PATTERN = Pattern.compile("Button \\w: X[+-](\\d+), Y[+-](\\d+)");
  private static final Pattern PRIZE_PATTERN = Pattern.compile("Prize: X=(-?\\d+), Y=(-?\\d+)");

  public record Position(int x, int y) {}

  public record Machine(Position a, Position b, Position goal) {}

  public static boolean isInt(double d) {
    return Math.abs((double)Math.round(d) - d) < 0.00000000001;
  }
  
  public static long calculate(Machine m) {
    double b = (m.goal.y - ((double)m.a.y / m.a.x)*m.goal.x) / (m.b.y - ((double)m.a.y / m.a.x)*m.b.x);
    double a = (m.goal.x - ((double)m.b.x * b)) / m.a.x;

    if (isInt(a) && isInt(b)) {
      return 3 * Math.round(a) + Math.round(b);
    }

    return 0;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p13/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Machine> machines = new ArrayList<>();

    for (int i = 0; i < lines.size(); i+= 4) {
      Matcher buttonAMatcher = BUTTON_PATTERN.matcher(lines.get(i));
      Matcher buttonBMatcher = BUTTON_PATTERN.matcher(lines.get(i + 1));
      Matcher prizeMatcher = PRIZE_PATTERN.matcher(lines.get(i + 2));

      buttonAMatcher.find();
      buttonBMatcher.find();
      prizeMatcher.find();

      Machine m = new Machine(
          new Position(Integer.parseInt(buttonAMatcher.group(1)), Integer.parseInt(buttonAMatcher.group(2))),
          new Position(Integer.parseInt(buttonBMatcher.group(1)), Integer.parseInt(buttonBMatcher.group(2))),
          new Position(Integer.parseInt(prizeMatcher.group(1)), Integer.parseInt(prizeMatcher.group(2))));
      machines.add(m);
    }

    long answer = machines.stream()
        .map(Puzzle::calculate)
        .reduce(0L, Long::sum);
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
