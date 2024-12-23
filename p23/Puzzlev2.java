package dev.advent;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;
import java.time.Clock;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
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
  private static final Pattern REGEX = Pattern.compile("(\\w+)\\-(\\w+)");

  public record Connection(String a, String b) {}
  
  public static boolean hasT(Collection<String> c) {
    for (String s : c) {
      if (s.charAt(0) == 't') {
        return true;
      }
    }
    return false;
  }

  public static boolean connectedTo(String s, Set<String> peers, Multimap<String, String> groups) {
    Collection c = groups.get(s);
    for (String p : peers) {
      if (!c.contains(p)) {
        return false;
      }
    }
    return true;
  }
    
  public static void main(String[] args) throws Exception {
    final List<String> lines = Helper.loadFile("dev_advent/p23/input.txt");
    Clock clock = Clock.systemUTC();
    long startTime = clock.millis();
    
    Multimap<String, String> groups = HashMultimap.create();
    List<Connection> connections = new ArrayList<>();
    for (String line : lines) {
      Matcher m = REGEX.matcher(line);
      m.find();
      connections.add(new Connection(m.group(1), m.group(2)));
      groups.put(m.group(1), m.group(2));
      groups.put(m.group(2), m.group(1));
    }
   
    Set<Set<String>> validSets = new HashSet<>();
    for (String s : groups.keySet()) {
      List<String> peers = new ArrayList<>(groups.get(s));
      Set<String> connectedPeers = new HashSet<>();
      connectedPeers.add(s);
      for (int i = 0; i < peers.size(); i++) {
        if (connectedTo(peers.get(i), connectedPeers, groups)) {
          connectedPeers.add(peers.get(i));
        }
        validSets.add(connectedPeers);
      }
    }

    Set<String> largestSet = validSets.iterator().next();
    for (Set<String> s : validSets) {
      if (s.size() > largestSet.size()) {
        largestSet = s;
      }
    }
    List<String> largeList = new ArrayList<>(largestSet);
    Collections.sort(largeList);
    String answer = Joiner.on(",").join(largeList);
        
    System.out.println("answer is " + answer);     
  
    System.out.println("time taken " + (clock.millis() - startTime) + "ms");
  }
}
