package dev.advent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.BigInteger;
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
  private static final Pattern BUTTON_PATTERN = Pattern.compile("Button \\w: X[+-](\\d+), Y[+-](\\d+)");
  private static final Pattern PRIZE_PATTERN = Pattern.compile("Prize: X=(-?\\d+), Y=(-?\\d+)");

  public record Position(long x, long y) {}

  public record Machine(Position a, Position b, Position goal) {}
  
  public static BigInteger calculate(Machine m) {
    double b = (m.goal.y - ((double)m.a.y / m.a.x)*m.goal.x) / (m.b.y - ((double)m.a.y / m.a.x)*m.b.x);
    double a = (m.goal.x - ((double)m.b.x * b)) / m.a.x;

    BigDecimal bigA = BigDecimal.valueOf(a);
    BigDecimal bigB = BigDecimal.valueOf(b);
    BigInteger iA = bigA.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    BigInteger iB = bigB.setScale(0, RoundingMode.HALF_UP).toBigInteger();

    if (iA.longValue() * m.a.x + iB.longValue() * m.b.x == m.goal.x
        && iA.longValue() * m.a.y + iB.longValue() * m.b.y == m.goal.y) {
      BigDecimal total = BigDecimal.valueOf(3).multiply(bigA).add(bigB);
      return total.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }
    
    return BigInteger.ZERO;
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
          new Position(Long.parseLong(buttonAMatcher.group(1)), Long.parseLong(buttonAMatcher.group(2))),
          new Position(Long.parseLong(buttonBMatcher.group(1)), Long.parseLong(buttonBMatcher.group(2))),
          new Position(Long.parseLong(prizeMatcher.group(1)) + 10000000000000L, Long.parseLong(prizeMatcher.group(2)) + 10000000000000L));
      machines.add(m);
    }

    BigInteger answer = BigInteger.ZERO;
    for (Machine m : machines) {
      answer = answer.add(calculate(m));
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
