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

// This one will iteratively find each digit, though the values for repeating digits and desired program index were kind of finicky.
// The hard coded version of the program runs about 20+ times faster.
public class Puzzlev5 {
  private static final Pattern REGISTER_PATTERN = Pattern.compile("Register (\\w): (\\d+)");
  private static final Pattern PROGRAM_PATTERN = Pattern.compile("Program: (.*)");

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

  public static boolean testA(long a, long b, long c, List<Integer> program, int desiredIndex) {
    int outputIndex = 0;

    Map<Character, Long> registers = new HashMap<>();
    registers.put('A', a);
    registers.put('B', b);
    registers.put('C', c);

    for (int i = 0; i < program.size(); i+=2) {
      OpCode opCode = OpCode.getOpCode(program.get(i));
      int operand = program.get(i + 1);

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

      if (outputIndex == desiredIndex) {
        return true;
      }

      if (outputIndex > program.size()) {
        return false;
      }
    }

    return outputIndex == desiredIndex;
  }

  public static boolean testAHardcoded(long a, long b, long c, List<Integer> program,  int desiredIndex) {
    int outputIndex = 0;

    while (true) {
      b = (a % 8) ^ 1;
      c = a >> b;
      a = a >> 3;
      b = b ^ c ^ 6;

      if ((int) (b % 8) == program.get(outputIndex)) {
        outputIndex++;
      } else {
        return false;
      }

      if (desiredIndex == outputIndex) {
        return true;
      }

      if (a == 0 || outputIndex > program.size()) {
        break;
      }
    }

    return outputIndex == program.size();
  }

  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p17/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

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

    List<Integer> octalDigits = new ArrayList<>();
    long incrementSize = 01000L;
    int incrementOctalShifts = 3;
    long currentIncrement = 1;
    boolean done = false;
    long answer = 0;
    int previousDigit = -1;
    int repeatCount = 0;
    int desiredProgramIndex = 6;
    while (!done) {
      long fixedDigits = 0;
      for (int i = octalDigits.size() - 1; i >= 0; i--) {
        fixedDigits = (fixedDigits << 3) + octalDigits.get(i);
      }
      fixedDigits = fixedDigits << 9;

      for (int i = 0; i < 01000; i++) {
        long testValue = incrementSize * currentIncrement + fixedDigits + i;
        boolean result = testAHardcoded(testValue, registers.get('B'), registers.get('C'), program, desiredProgramIndex);

        if (result && !done) {
          if (desiredProgramIndex == program.size()) {
            answer = testValue;
            done = true;
            break;
          }

          int digit = (int) (testValue >> (incrementOctalShifts * 3)) % 8;
          if (digit == previousDigit) {
            repeatCount++;
          } else {
            previousDigit = digit;
            repeatCount = 0;
          }

          if (repeatCount >= 10) {
            octalDigits.add(digit);
            incrementSize = incrementSize << 3;
            incrementOctalShifts++;
            currentIncrement = 1;
            previousDigit = -1;
            repeatCount = 0;
            desiredProgramIndex = Math.min(desiredProgramIndex + 1, program.size());
            break;
          }
        }
      }
      
      currentIncrement++;
    }
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
