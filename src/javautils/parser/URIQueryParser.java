package javautils.parser;

import java.util.HashMap;
import java.util.Set;

public class URIQueryParser {

	private HashMap<String, String> values;

	public URIQueryParser(String string) {
		values = new HashMap<>();
		if (string != null && !string.isEmpty()) {
			if (string.startsWith("?")) {
				string = string.substring(1);
			}
			string += " ";
			int lastseperator = -1;
			char character = ' ';
			String segment = "";
			boolean enclosed = false;
			for (int i = 0; i < string.length(); i++) {
				character = string.charAt(i);
				if ((character == '&') || (character == ';' || i == string.length() - 1) && !enclosed) {
					if (i == string.length() - 1) i++;
					segment = string.substring(lastseperator + 1, i);
					if (segment.contains("=") && segment.split("=").length == 2) {
						String value[] = segment.split("=");
						values.put(value[0], value[1]);
					} else {
						values.put(segment, "");
					}
					lastseperator = i;
				} else if (character == '"' && !enclosed) {
					enclosed = true;
				} else if (character == '"' && enclosed) {
					enclosed = false;
				}
			}
		}
	}

	public Set<String> keySet() {
		return values.keySet();
	}

	public int size() {
		return values.size();
	}

	public String getValue(String key) {
		return values.get(key);
	}

}
