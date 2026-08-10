package com.libris.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesHelpers {
    
    private static Properties properties;
    private static String linkFile;
    private static FileInputStream file;
    private static FileOutputStream out;
    private static String relPropertiesFilePathDefault = "/src/main/resources/icons/Icons.properties";
    
    private static String getCurrentDir() {
        return System.getProperty("user.dir");
    }
    public static String getValue(String key) {
        String keyValue = null;
        try {
            if (file == null) {
                properties = new Properties();
                linkFile = getCurrentDir() + relPropertiesFilePathDefault;
                file = new FileInputStream(linkFile);
                properties.load(file);
                file.close();
            }
            keyValue = properties.getProperty(key);
        } catch (Exception e) {
            System.err.println("Lỗi đọc file properties: " + e.getMessage());
        }
        return keyValue;
    }
}
