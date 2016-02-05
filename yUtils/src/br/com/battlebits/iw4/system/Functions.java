package br.com.battlebits.iw4.system;

import java.util.HashMap;
import java.util.Map.Entry;

import br.com.battlebits.iw4.json.JSONArray;

/**
 *
 * @author Renato
 */
public class Functions {

    public static HashMap<Integer, String> jArrayToHashMap(JSONArray array) {
        HashMap<Integer, String> pairs = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            pairs.put(i, (String) array.get(i));
        }
        return pairs;
    }

    public static String mapStringReplace(String aString, HashMap<String, Object> map) {
        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                String prefix = ("{" + entry.getKey() + "}");
                String value = entry.getValue().toString();
                if (aString.contains(prefix)) {
                    aString = aString.replace(prefix, value);
                }
            }
        }
        return aString;
    }

    public static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

}
