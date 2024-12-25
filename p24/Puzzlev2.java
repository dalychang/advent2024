package dev.advent;

import com.google.common.base.Joiner;
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

public class Puzzlev2 {
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

  public static long toLong(Map<String, Boolean> results) {
    int[] zs = new int[63];
    for (int i = 0; i < 63; i++) {
      String key = "z" + String.format("%02d", i);
      if (results.containsKey(key)) {
        zs[i] = results.get(key) ? 1 : 0;
      }
    }

    long answer = 0;
    for (int i = 62; i >= 0; i--) {
      answer = (answer << 1) + zs[i];
    }
    return answer;
  }

  public static int findMaxBits(Map<String, Boolean> results) {
    for (int i = 0; i < 64; i++) {
      String key = "x" + String.format("%02d", i);
      if (!results.containsKey(key)) {
        return i + 1;
      }
    }

    return 100;
  }

  public static void runMachine(List<Gate> gates, Map<String, Boolean> results) {
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
  }

  public static void populate(String prefix, Map<String, Boolean> inputs, long value) {
    int index = 0;
    while (value > 0) {
      String key = prefix + String.format("%02d", index);
      boolean bit = value % 2 == 0 ? false : true;
      inputs.put(key, bit);
      value = value >> 1;
      index++;
    }
  }

  public static long extract(Map<String, Boolean> inputs) {
    long value = 0;
    for (int i = 62; i >= 0; i--) {
      String key = "z" + String.format("%02d", i);
      int bit = inputs.getOrDefault(key, false) ? 1 : 0;
      value = (value << 1) + bit;
    }
    return value;
  }

  private static void doRenames(Map<String, String> renameMap, Map<String, Gate> gateMap) {
    List<Gate> updateGates = new ArrayList<>(gateMap.values());
    for (Gate gate : updateGates) {
      Gate newGate = new Gate(
        renameMap.getOrDefault(gate.a, gate.a),
        renameMap.getOrDefault(gate.b, gate.b),
        gate.op,
        renameMap.getOrDefault(gate.out, gate.out));
      gateMap.remove(gate.out);
      gateMap.put(newGate.out, newGate);
    }
  }

  public static void swap(Map<String, Gate> gateMap, String a, String b) {
    Gate gateA = gateMap.get(a);
    Gate gateB = gateMap.get(b);
    gateMap.put(a, new Gate(gateB.a, gateB.b, gateB.op, a));
    gateMap.put(b, new Gate(gateA.a, gateA.b, gateA.op, b));
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p24/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    boolean readingIns = true;
    Map<String, Boolean> inputs = new HashMap<>();
    Map<String, Gate> gateMap = new HashMap<>();
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
        Gate gate = new Gate(m.group(1), m.group(3), Op.valueOf(m.group(2)), m.group(4));
        gateMap.put(gate.out, gate);
      }
    }

    swap(gateMap, "fsq", "dvb");
    swap(gateMap, "z10", "vcf");
    swap(gateMap, "z17", "fhg");
    swap(gateMap, "z39", "tnc");

    List<Gate> gates = new ArrayList<>();
    gates.addAll(gateMap.values());

    int maxBits = findMaxBits(inputs);
    System.out.println("maxBits: " + maxBits);
    List<Gate> andGates = new ArrayList<>();
    Map<String, Gate> andGatesMap = new HashMap<>();
    List<Gate> xorGates = new ArrayList<>();
    Map<String, Gate> xorGatesMap = new HashMap<>();

    List<Gate> carryGates = new ArrayList<>();
    List<Gate> carryAndGates = new ArrayList<>();
    List<Gate> outputGates = new ArrayList<>();

    Map<String, String> renames = new HashMap<>();

    for (Gate gate : gates) {
      if ((gate.a.charAt(0) == 'x' || gate.a.charAt(0) == 'y')
          && (gate.b.charAt(0) == 'x' || gate.b.charAt(0) == 'y')) {
        int i = Integer.parseInt(gate.a.substring(1, 3));
        if (gate.op == Op.XOR) {
          xorGates.add(gate);
          xorGatesMap.put(gate.out, gate);
        } else if (gate.op == Op.AND) {
          andGates.add(gate);
          andGatesMap.put(gate.out, gate);
        } else {
          System.out.println(gate);
        }
      }
    }

    for (Gate gate : xorGates) {
      if (gate.out.charAt(0) == 'z') continue;
      String newName = "xor" + gate.a.substring(1,3);
      renames.put(gate.out, newName);
      gateMap.remove(gate.out);
      Gate newGate = new Gate(gate.a, gate.b, gate.op, newName);
      gateMap.put(newName, newGate);
      xorGatesMap.remove(gate.out);
      xorGatesMap.put(newName, newGate);
    }

    for (Gate gate : andGates) {
      if (gate.out.charAt(0) == 'z') continue;
      String newName = "and" + gate.a.substring(1,3);
      renames.put(gate.out, newName);
      gateMap.remove(gate.out);
      Gate newGate = new Gate(gate.a, gate.b, gate.op, newName);
      gateMap.put(newName, newGate);
      andGatesMap.remove(gate.out);
      andGatesMap.put(newName, newGate);
    }

    doRenames(renames, gateMap);
    List<Gate> remainingGates = new ArrayList<>();
    for (Gate gate : gateMap.values()) {
      if ((gate.a.charAt(0) == 'x' || gate.a.charAt(0) == 'y')
          && (gate.b.charAt(0) == 'x' || gate.b.charAt(0) == 'y')) {
        continue;
      }

      remainingGates.add(gate);
    }

    for (Gate gate : remainingGates) {
      if (gate.out.charAt(0) == 'z') {
        outputGates.add(gate);
        Gate orGate = gateMap.get(gate.a).op == Op.OR ? gateMap.get(gate.a) : gateMap.get(gate.b);
        Gate xorGate = gateMap.get(gate.a).op == Op.XOR ? gateMap.get(gate.a) : gateMap.get(gate.b);
        if (orGate.op != Op.OR || xorGate.op != Op.XOR) {
          System.out.println("BZI: " + gate + " " +  gateMap.get(gate.a) + " " +  gateMap.get(gate.b));
          continue;
        }
        if (gate.op != Op.XOR) {
          System.out.println("Wrong op: " + gate);
        }

        String index = xorGate.out.substring(3, xorGate.out.length());
        if (!gate.out.substring(1,3).equals(index)) {
          System.out.println("BZ: " + gate + " " + xorGate + " " + orGate);
        }
      }
    }

    List<Gate> remainingGates2 = new ArrayList<>();
    for (Gate gate : remainingGates) {
      if (gate.op == Op.OR && gateMap.get(gate.a).op == Op.AND && gateMap.get(gate.b).op == Op.AND) {
        carryGates.add(gate);
      } else {
        remainingGates2.add(gate);
      }
    }

    List<Gate> remainingGates3 = new ArrayList<>();
    for (Gate gate : remainingGates2) {
      if (gate.op != Op.AND) {
        continue;
      }
      Op aOp = gateMap.get(gate.a).op;
      Op bOp = gateMap.get(gate.b).op;

      if ((aOp == Op.OR && bOp == Op.XOR) || (bOp == Op.OR && aOp == Op.XOR)) {
        carryAndGates.add(gate);
      } else {
        remainingGates3.add(gate);
      }
    }

    System.out.println("Renames: " + renames);

    System.out.println(String.format("\nCarry gates (%d): %s", carryGates.size(), carryGates));
    System.out.println(String.format("\nOutput gates (%d): %s", outputGates.size(), outputGates));
    System.out.println(String.format("\nCarry AND gates (%d): %s", carryAndGates.size(), carryAndGates));

    System.out.println(String.format("\nInput AND gates (%d): %s", andGates.size(), andGates));
    System.out.println(String.format("\nInput XOR gates (%d): %s", xorGates.size(), xorGates));

    System.out.println(String.format("\nRemaining gates (%d): %s", remainingGates3.size(), remainingGates3));

    System.out.println("\nAll: " + gateMap.values());

    for (Gate gate : remainingGates3) {
      System.out.println("B " + gate + " a:" + gateMap.get(gate.a).op + " b:" + gateMap.get(gate.b).op);
      if (xorGatesMap.keySet().contains(gate.a) || xorGatesMap.keySet().contains(gate.b)) {
        System.out.println("\tXOR " + xorGatesMap.get(gate.a) + " " + xorGatesMap.get(gate.b));
      } 
      if (andGatesMap.keySet().contains(gate.a) || andGatesMap.keySet().contains(gate.b)) {
        System.out.println("\tAND " + andGatesMap.get(gate.a) + " " + andGatesMap.get(gate.b));
      }
    }

    Map<String, Boolean> emptyInputs = new HashMap<>();
    for (String key : inputs.keySet()) {
      if (key.charAt(0) == 'x' || key.charAt(0) == 'y') {
        emptyInputs.put(key, false);
      } else {
        emptyInputs.put(key, inputs.get(key));
      }
    }

    gates = new ArrayList<>(gateMap.values());

    System.out.println("Do some math");
    long multiplier = 43200893108L;
    for (long i = 0; i < 100; i++) {
      long x = Math.round(Math.random() * multiplier);
      long y = Math.round(Math.random() * multiplier);
      long z = x + y;
      Map<String, Boolean> results = new HashMap<>();
      results.putAll(emptyInputs);
      populate("x", results, x);
      populate("y", results, y);
      runMachine(gates, results);
      long nz = extract(results);
      if (nz != z){
        System.out.println("x:" + x + " y:" + y + " ze: " + z + " za: " + nz);
      }
    }

    List<String> swapList = new ArrayList<>(List.of(
       "fsq", "dvb",
       "z10", "vcf",
       "z17", "fhg",
       "z39", "tnc"
    ));
    Collections.sort(swapList);

    System.out.println("Answer is " + Joiner.on(",").join(swapList));

    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
