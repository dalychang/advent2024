package dev.advent;

import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
  private static final Pattern INPUT_REGEX = Pattern.compile("(\\w+): (\\d)");
  private static final Pattern COMBO_REGEX = Pattern.compile("(\\w+) (AND|XOR|OR) (\\w+) -> (\\w+)");
  
  public enum Op {
    AND,
    OR,
    XOR
  }

  public record Gate(String a, String b, Op op, String out) {}

  public static long calculate(String s) {
    return 1;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p24/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    boolean readingIns = true;
    Map<String, Boolean> inputs = new HashMap<>();
    List<Gate> gates = new ArrayList<>();
    for (String line : lines) {
      if (line.isEmpty()) {
        readingIns = false;
        continue;
      }

      if (readingIns) {
        Matcher m = INPUT_REGEX.matcher(line);
        m.find();
        inputs.put(m.group(1), m.group(2).equals("1"));
      } else {
        Matcher m = COMBO_REGEX.matcher(line);
        m.find();
        gates.add(new Gate(m.group(1), m.group(3), Op.valueOf(m.group(2)), m.group(4)));
      }
    }

    Map<String, Boolean> results = new HashMap<>();
    results.putAll(inputs);
    List<Gate> pendingGates = new LinkedList<>();
    pendingGates.addAll(gates);

    while (!pendingGates.isEmpty()) {
      Gate gate = pendingGates.remove(0);
      if (!results.containsKey(gate.a) || !results.containsKey(gate.b)) {
        pendingGates.add(gate);
        continue;
      }

      boolean a = results.get(gate.a);
      boolean b = results.get(gate.b);
      boolean out = false;
      switch (gate.op) {
        case AND:
          out = a && b;
          break;
        case XOR:
          out = a ^ b;
          break;
        case OR:
        default:
          out = a || b;
          break;
      }
      results.put(gate.out, out);
    }

    int[] zs = new int[61];
    for (int i = 0; i < 61; i++) {
      String key = "z" + String.format("%02d", i);
      if (results.containsKey(key)) {
        zs[i] = results.get(key) ? 1 : 0;
      }
    }

    long answer = 0;
    for (int i = 60; i >= 0; i--) {
      answer = (answer << 1) + zs[i];
    }
   
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
