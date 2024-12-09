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
  private static final int FREE = -1;

  public record Data(int fileId, int blocks) {
    public boolean isFree() {
      return fileId == FREE;
    }
  }
  
  public static void print(List<Data> dataList) {
    StringBuilder sb = new StringBuilder();
    for (Data data : dataList) {
      String letter = ".";
      if (!data.isFree()) {
        letter = data.fileId() + "";
      }
      for (int i = 0; i < data.blocks; i++) {
        sb.append(letter);
      }
    }
    System.out.println("\n" + sb.toString());
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p9/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();

    if (lines.size() > 1) {
      throw new RuntimeException("Bad input");
    }

    List<Data> dataList = new ArrayList<>();
    String line = lines.get(0);
    int fileId = 0;
    boolean isFile = true;
    for (int i = 0; i < line.length(); i++) {
      int blocks = Integer.parseInt(String.valueOf(line.charAt(i)));
      if (isFile) {
        dataList.add(new Data(fileId, blocks));
        fileId++;
        isFile = false;
      } else {
        dataList.add(new Data(FREE, blocks));
        isFile = true;
      }
    }

    int begin = 0;
    int end = dataList.size() - 1;

    while (dataList.get(end).isFree()) {
      end--;
    }

    while (begin < end) {
      Data data = dataList.get(begin);
      if (data.isFree()) {
        dataList.remove(begin);
        end--;
        int blocks = data.blocks;
        while (blocks > 0 && begin < end) {
          Data fileData = dataList.get(end);
          if (fileData.isFree()) {
            end--;
            continue;
          }
          if (fileData.blocks > blocks) {
            dataList.set(end, new Data(fileData.fileId, fileData.blocks - blocks));
            dataList.add(begin, new Data(fileData.fileId, blocks));
            begin++;
            end++;
            blocks = 0;
          } else {
            dataList.remove(end);
            dataList.add(begin, fileData);
            begin++;
            blocks -= fileData.blocks;
          }
        }
      } else {
        begin++;
      }
    }

    long answer = 0;
    int index = 0;
    for (Data data : dataList) {
      if (data.isFree()) {
        index += data.blocks;
        continue;
      }

      for (int i = 0; i < data.blocks; i++) {
        answer += ((i + index) * data.fileId);
      }
      index += data.blocks;
    }

    // print(dataList);
        
    System.out.println("answer is " + answer);     
    
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}