/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aldair
 */
public class ConfigAccess {

    private static ConfigAccess recurso;
    private static final String PROPERTIES = "config/config.properties";
    private final Properties property = new Properties();
    private static InputStream stream = ConfigAccess.class.getClassLoader().getResourceAsStream(PROPERTIES);

    private ConfigAccess() {

        // this is how we load file within editor (eg eclipse)

        try {
            property.load(stream);
        } catch (IOException ex) {
            Logger.getLogger(ConfigAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ConfigAccess getRecurso() {
        if (recurso == null) {
            recurso = new ConfigAccess();
        }
        return recurso;
    }

    public String getValue(String key){

        return property.getProperty(key);
    }

    public byte[] getFile(String value) throws IOException {
        InputStream is = ConfigAccess.class.getClassLoader().getResourceAsStream("private/" + value);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int read;
        while ((read = is.read(bytes)) != -1) {
            baos.write(bytes, 0, read);
        }
        return baos.toByteArray();
    }
}
