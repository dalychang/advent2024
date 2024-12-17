package dev.advent;

import com.google.common.base.Joiner;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This was an attempt to multithread it, but it also doesn't finish running in a reasonable time.
public class Puzzlev3 {
  private static final Pattern REGISTER_PATTERN = Pattern.compile("Register (\\w): (\\d+)");
  private static final Pattern PROGRAM_PATTERN = Pattern.compile("Program: (.*)");
  private static final long THREAD_DELTA = 100000;

  private static final ExecutorService executorService = Executors.newFixedThreadPool(30);

  public static long getComboOperand(Map<Character, Long> registers, int operand) {
    switch (operand) {
      case 0:
      case 1:
      case 2:
      case 3:
        return operand;
      case 4:
        return registers.get('A');
      case 5:
        return registers.get('B');
      case 6:
        return registers.get('C');
      case 7:
      default:
        throw new RuntimeException("unexpected operand " + operand);  
    }
  }

  public record OpResult(Integer output, Integer instruction) {
    public static OpResult noOut() {
      return new OpResult(null, null);
    }

    public static OpResult out(int value) {
      return new OpResult(value, null);
    }

    public static OpResult instruction(int instruction) {
      return new OpResult(null, instruction);
    }
  }

  public enum OpCode{
    ADV(0),  // Division
    BXL(1),  // Bitwise XOR
    BST(2),  // Mod 8
    JNZ(3),  // Jump not zero
    BXC(4),  // Bitwise XOR
    OUT(5),  // Mod 8, then outputs
    BDV(6),  // Like ADV -> B
    CDV(7);  // Like ADV -> C

    public final int code;

    private OpCode(int code) {
      this.code = code;
    }

    public static OpCode getOpCode(int code) {
      for (OpCode opCode : OpCode.values()) {
        if (opCode.code == code) {
          return opCode;
        }
      }
      return null;
    }

    public OpResult run(Map<Character, Long> registers, int operand) {
      switch (this) {
        case ADV:
          {
            long comboOperand = getComboOperand(registers, operand);
            long numerator = registers.get('A');
            long denominator = (long) Math.pow(2, comboOperand);
            long result = (long) numerator / denominator;
            registers.put('A', result);
            return OpResult.noOut();
          }
        case BDV:
          {
            long comboOperand = getComboOperand(registers, operand);
            long numerator = registers.get('A');
            long denominator = (long) Math.pow(2, comboOperand);
            long result = (long) numerator / denominator;
            registers.put('B', result);
            return OpResult.noOut();
          }
        case CDV:
          {
            long comboOperand = getComboOperand(registers, operand);
            long numerator = registers.get('A');
            long denominator = (long) Math.pow(2, comboOperand);
            long result = (long) numerator / denominator;
            registers.put('C', result);
            return OpResult.noOut();
          }
        case BXL:
          {
            long result = registers.get('B') ^ operand;
            registers.put('B', result);
            return OpResult.noOut();
          }
        case BXC:
          {
            long result = registers.get('B') ^ registers.get('C');
            registers.put('B', result);
            return OpResult.noOut();
          }  
        case OUT:
          {
            long comboOperand = getComboOperand(registers, operand);
            long result = comboOperand % 8;
            if (result < 0) result += 8;
            return OpResult.out((int)result);
          }
        case BST:
          {
            long comboOperand = getComboOperand(registers, operand);
            long result = comboOperand % 8;
            registers.put('B', result);
            return OpResult.noOut();
          }
        case JNZ:
          {
            long testValue = registers.get('A');
            if (testValue == 0) {
              return OpResult.noOut();
            }
            return OpResult.instruction(operand);
          }
        default:
          throw new RuntimeException("Unexpected opcode: " + this);  
      }
    }
  }

  public static boolean testA(long a, long b, long c, List<Integer> program) {
    Map<Character, Long> registers = new HashMap<>();
    registers.put('A', a);
    registers.put('B', b);
    registers.put('C', c);
    int outputIndex = 0;
    for (int i = 0; i < program.size(); i+=2) {
      OpCode opCode = OpCode.getOpCode(program.get(i));
      int operand = program.get(i + 1);

      //System.out.println("Run " + opCode + " on " + operand);       
      OpResult result = opCode.run(registers, operand);
      if (result.output != null) {
        if ((int) result.output == program.get(outputIndex)) {
          outputIndex++;
        } else {
          return false;
        }
      }
      if (result.instruction != null) {
        i = result.instruction - 2;
      }
    }

    return outputIndex == program.size();
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p17/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    final AtomicLong atomicLong = new AtomicLong(0);
    final AtomicLong atomicAnswer = new AtomicLong(0);
    final CountDownLatch latch = new CountDownLatch(1);

    boolean onRegisters = true;
    List<Integer> program = new ArrayList<>();
    Map<Character, Long> registers = new HashMap<>();
    for (String line : lines) {
      if (line.isEmpty()) {
        onRegisters = false;
        continue;
      }
      if (onRegisters) {
        Matcher m = REGISTER_PATTERN.matcher(line);
        m.find();
        registers.put(m.group(1).charAt(0), Long.parseLong(m.group(2)));
      } else {
        Matcher m = PROGRAM_PATTERN.matcher(line);
        m.find();
        String[] split = m.group(1).split(",");
        for (String s : split) {
          program.add(Integer.parseInt(s));
        }
      }
    }

    System.out.println("Registers: " + registers);
    System.out.println("Program: " + program);

    for (int i = 0; i < 30; i++) {
      executorService.execute(new Runnable() {
        @Override
        public void run() {
          while (true) {
            if (latch.getCount() <= 0) {
              return;
            }

            long testValueStart = atomicLong.getAndAdd(THREAD_DELTA);
            for (int j = 0; j < THREAD_DELTA; j++) {
              if (latch.getCount() <= 0) {
                return;
              }

              long testValue = testValueStart + j;
              boolean result = testA(testValue, registers.get('B'), registers.get('C'), program);
              if (result) {
                atomicAnswer.set(testValue);
                latch.countDown();
              }
    
              if (testValue % 1000000 == 0) {
                System.out.println("Iter " + testValue);
              }
            }
          }
        }
      });
    }

    latch.await(); 

    long answer = atomicAnswer.longValue();
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
