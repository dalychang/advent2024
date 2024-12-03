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

public class Puzzlev2 {

  final static Pattern regex1 = Pattern.compile("(mul\\(\\d+,\\d+\\))");
  final static Pattern regex2 = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
  
  public static long calculate(String s) {
    long answer = 0;
    Matcher matcher = regex1.matcher(s);
    while (matcher.find()) {
      String intermediate = matcher.group(1);
      Matcher matcher2 = regex2.matcher(intermediate);
      matcher2.find();
      long value1 = Long.parseLong(matcher2.group(1));
      long value2 = Long.parseLong(matcher2.group(2));
      long product = value1 * value2;
      answer += product;
    }
    return answer;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p3/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    long answer = 0;
    String bigLine = Joiner.on("\n").join(lines);
    String lineLeft = bigLine;
    boolean doMath = true;
    while (!lineLeft.isEmpty()) {
      int dontIndex = lineLeft.indexOf("don't()");
      int doIndex = lineLeft.indexOf("do()");

      if (dontIndex == -1) {
        dontIndex = lineLeft.length();
      }
      dontIndex = Math.min(dontIndex + "don't()".length(), lineLeft.length());
      if (doIndex == -1) {
        doIndex = lineLeft.length();
      }
      doIndex = Math.min(doIndex + "do()".length(), lineLeft.length());

      int index = Math.min(doIndex, dontIndex);
      if (doMath) {
        String part = lineLeft.substring(0, index);
        answer += calculate(part);
      } else {
        String part = lineLeft.substring(0, index);
      }
      lineLeft = lineLeft.substring(index);

      if (dontIndex < doIndex) {
        doMath = false;
      } else {
        doMath = true;
      }
    }

    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
