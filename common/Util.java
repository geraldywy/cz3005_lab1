package common;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {
  public static Map<String, Node> buildGraph() {
    JSONObject graphJson = readJsonFile("G.json");

    Map<String, Map<String, Double>> distWeightMap = buildWeightMap("Dist.json");
    Map<String, Map<String, Double>> energyWeightMap = buildWeightMap("Cost.json");

    Map<String, Node> nodesMap = new HashMap<>();

    for (Object keyObj : graphJson.keySet()) {
      JSONArray neighbours = (JSONArray) graphJson.get(keyObj);
      String key = keyObj.toString();
      if (!nodesMap.containsKey(key)) {
        nodesMap.put(key, new Node(key));
      }

      Node cur = nodesMap.get(key);
      Map<Node, EdgeCosts> neighboursNodes = new HashMap<>();
      for (Object neighbour : neighbours) {
        String neighbourKey = neighbour.toString();
        if (!nodesMap.containsKey(neighbourKey)) {
          nodesMap.put(neighbourKey, new Node(neighbourKey));
        }

        neighboursNodes.put(nodesMap.get(neighbourKey),
            new EdgeCosts(distWeightMap.get(key).get(neighbourKey), energyWeightMap.get(key).get(neighbourKey)));
      }

      cur.neighbours = neighboursNodes;
    }

    return nodesMap;
  }

  private static JSONObject readJsonFile(String filepath) {
    try {
      JSONParser parser = new JSONParser();
      // Use JSONObject for simple JSON and JSONArray for array of JSON.
      JSONObject data = (JSONObject) parser.parse(new FileReader(filepath));

      return data;
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }

    return new JSONObject();
  }

  private static Map<String, Map<String, Double>> buildWeightMap(String filepath) {
    JSONObject weightJson = readJsonFile(filepath);

    Map<String, Map<String, Double>> weightMap = new HashMap<>();
    for (Object keyObj : weightJson.keySet()) {
      String[] key = keyObj.toString().split(",");
      Double cost = Double.valueOf(weightJson.get(keyObj).toString());
      if (!weightMap.containsKey(key[0])) {
        weightMap.put(key[0], new HashMap<>());
      }
      weightMap.get(key[0]).put(key[1], cost);
    }

    return weightMap;
  }

  public static String buildPath(Node goalNode) {
    LinkedList<String> res = new LinkedList<>();

    Node temp = goalNode;
    while (temp != null) {
      res.addFirst(temp.id);
      temp = temp.parent;
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < res.size(); i++) {
      sb.append(res.get(i) + (i == res.size() - 1 ? "" : " -> "));
    }

    return sb.toString();
  }
}
