package webserver.util;

import java.util.HashMap;

public class CaseInsensitiveMap extends HashMap<String, String> {
    @Override
    public String put(String key, String value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) return false;
        return super.containsKey(((String) key).toLowerCase());
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof String)) return null;
        return super.get(((String) key).toLowerCase());
    }
}
