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
  public enum Color {
    RED('r'),
    WHITE('w'),
    BLUE('u'),
    BLACK('b'),
    GREEN('g');

    private final char c;

    private Color(char c) {
      this.c = c;
    }

    public static Color toColor(char c) {
      for (Color color : Color.values()) {
        if (color.c == c) {
          return color;
        }
      }
      return null;
    }
  }

  public record Towel(List<Color> colors) {}
  public record Design(List<Color> colors) {}

  public static boolean shortCheck(List<Color> colors, List<Towel> towels) {
    Set<Color> singleColors = new HashSet<>();
    for (Towel t : towels) {
      if (t.colors.size() == 1) {
        singleColors.add(t.colors.get(0));
      }
    }
    return singleColors.containsAll(colors);
  }
  
  public static boolean isPossible(List<Color> colors, List<Towel> towels) {
    if (colors.isEmpty()) {
      return true;
    }

    for (Towel t : towels) {
      if (t.colors.size() > colors.size()) {
        continue;
      }

      boolean towelOk = true;
      for (int i = 0; i < t.colors.size(); i++) {
        if (t.colors.get(i) != colors.get(i)) {
          towelOk = false;
          break;
        }
      }

      if (towelOk) {
        List<Color> nextColors = colors.subList(t.colors.size(), colors.size());
        if (isPossible(nextColors, towels)) {
          return true;
        }
      }
    }

    return false;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p19/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    List<Towel> towels = new ArrayList<>();
    List<Design> designs = new ArrayList<>();
    boolean readDesign = false;
    for (String line : lines) {
      if (line.isEmpty()) {
        readDesign = true;
        continue;
      }

      if (readDesign) {
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
          char c = line.charAt(i);
          colors.add(Color.toColor(c));
        }
        designs.add(new Design(colors));
      } else {
        String[] split = line.split(",\\s*");
        for (String s : split) {
          List<Color> colors = new ArrayList<>();
          for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            colors.add(Color.toColor(c));
          }
          towels.add(new Towel(colors));
        }
      }
    }

    List<Towel> lessTowels = new ArrayList<>();
    for (Towel t : towels) {
      List<Towel> tempTowels = new ArrayList<>(towels);
      tempTowels.remove(t);
      if (!isPossible(t.colors, tempTowels)) {
        lessTowels.add(t);
      }
    }

    System.out.println(lessTowels.size() + " towels.");
    
    long answer = 0;
    int i = 0;
    for (Design design : designs) {
      i++;
      System.out.println(i + "/" + designs.size());
      if (shortCheck(design.colors, lessTowels) || isPossible(design.colors, lessTowels)) {
        answer++;
      }
    }

    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
