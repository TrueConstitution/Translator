package kgg.translator.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;

public class EasyProperties extends Properties{
    public EasyProperties(InputStream in) throws IOException {
        super();
        load(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public String getKeysByValue(String value) {
        Enumeration<?> keys = keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String currentValue = getProperty((String) key);
            if (value.equals(currentValue)) {
                return (String) key;
            }
        }
        return null;
    }
}
