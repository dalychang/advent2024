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
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This one doesn't finish running in a reasonable time.
public class Puzzlev2 {
  private static final Pattern REGISTER_PATTERN = Pattern.compile("Register (\\w): (\\d+)");
  private static final Pattern PROGRAM_PATTERN = Pattern.compile("Program: (.*)");

  public static int getComboOperand(Map<Character, Integer> registers, int operand) {
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
    BXC(4),  // Bitwise OR
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

    public OpResult run(Map<Character, Integer> registers, int operand) {
      switch (this) {
        case ADV:
          {
            int comboOperand = getComboOperand(registers, operand);
            int numerator = registers.get('A');
            int denominator = (int) Math.pow(2, comboOperand);
            int result = (int) numerator / denominator;
            registers.put('A', result);
            return OpResult.noOut();
          }
        case BDV:
          {
            int comboOperand = getComboOperand(registers, operand);
            int numerator = registers.get('A');
            int denominator = (int) Math.pow(2, comboOperand);
            int result = (int) numerator / denominator;
            registers.put('B', result);
            return OpResult.noOut();
          }
        case CDV:
          {
            int comboOperand = getComboOperand(registers, operand);
            int numerator = registers.get('A');
            int denominator = (int) Math.pow(2, comboOperand);
            int result = (int) numerator / denominator;
            registers.put('C', result);
            return OpResult.noOut();
          }
        case BXL:
          {
            int result = registers.get('B') ^ operand;
            registers.put('B', result);
            return OpResult.noOut();
          }
        case BXC:
          {
            int result = registers.get('B') ^ registers.get('C');
            registers.put('B', result);
            return OpResult.noOut();
          }  
        case OUT:
          {
            int comboOperand = getComboOperand(registers, operand);
            int result = comboOperand % 8;
            if (result < 0) result += 8;
            return OpResult.out(result);
          }
        case BST:
          {
            int comboOperand = getComboOperand(registers, operand);
            int result = comboOperand % 8;
            registers.put('B', result);
            return OpResult.noOut();
          }
        case JNZ:
          {
            int testValue = registers.get('A');
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

  public static boolean testA(int a, int b, int c, List<Integer> program) {
    Map<Character, Integer> registers = new HashMap<>();
    registers.put('A', a);
    registers.put('B', b);
    registers.put('C', c);
    int outputIndex = 0;
    List<Integer> output = new ArrayList<>();
    for (int i = 0; i < program.size(); i+=2) {
      OpCode opCode = OpCode.getOpCode(program.get(i));
      int operand = program.get(i + 1);

      //System.out.println("Run " + opCode + " on " + operand);       
      OpResult result = opCode.run(registers, operand);
      if (result.output != null) {
        output.add(result.output);
        outputIndex++;
      }
      if (result.instruction != null) {
        i = result.instruction - 2;
      }

      if (outputIndex >= program.size()) {
        System.out.println(a + " - " + output);
        return false;
      }
    }

    System.out.println(a + " - " + output);
    return outputIndex == program.size();
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p17/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    boolean onRegisters = true;
    List<Integer> program = new ArrayList<>();
    Map<Character, Integer> registers = new HashMap<>();
    for (String line : lines) {
      if (line.isEmpty()) {
        onRegisters = false;
        continue;
      }
      if (onRegisters) {
        Matcher m = REGISTER_PATTERN.matcher(line);
        m.find();
        registers.put(m.group(1).charAt(0), Integer.parseInt(m.group(2)));
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

    int testValue = 0;
    while (true) {
      if (testValue % 1000000 == 0) {
        System.out.println("Iter " + testValue);
      }
      boolean result = testA(testValue, registers.get('B'), registers.get('C'), program);
      if (result) {
        break;
      }
      testValue++;
    }

    int answer = testValue;
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
