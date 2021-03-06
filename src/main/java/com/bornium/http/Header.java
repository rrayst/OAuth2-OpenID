package com.bornium.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Xorpherion on 25.08.2016.
 */
public class Header {

    static Pattern delimiter = Pattern.compile(Pattern.quote(","));
    private HashMap<String, String> rawHeaders = new HashMap<>();

    public HashMap<String, String> getRawHeaders() {
        return rawHeaders;
    }

    public void setRawHeaders(HashMap<String, String> rawHeaders) {
        this.rawHeaders = rawHeaders;
    }

    public void add(String name, String value) {
        rawHeaders.put(name.toLowerCase(), value);
    }

    public String getValue(String name) {
        return rawHeaders.get(name.toLowerCase());
    }

    public void append(String name, String value) {
        if (!rawHeaders.containsKey(name)) {
            add(name, value);
            return;
        }
        String newHeaderVal = value + ", " + getValue(name);
        add(name, newHeaderVal);
    }

    public String remove(String name) {
        return rawHeaders.remove(name.toLowerCase());
    }

    public Set<String> getHeaderNames() {
        return rawHeaders.keySet();
    }

    public Collection<String> getHeaderValues() {
        return rawHeaders.values();
    }

    public String[] getValues(String name) {
        String val = getValue(name);
        if (val != null)
            return delimiter.split(getValue(name));
        return null;
    }

    public void append(Header other) {
        other.getRawHeaders().keySet().stream().forEach(s -> this.append(s,other.getValue(s)));
    }

    public void add(Header other){
        this.setRawHeaders(other.getRawHeaders());
    }
}
