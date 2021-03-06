package com.projprogiii.servermail.server.config;

import java.io.*;
import java.util.Properties;

public class ConfigManager {

    private final Properties prop;

    private ConfigManager() {
        prop = new Properties();
        File f = getDir();

        try{
            if (!f.exists()) {
                createPropertiesFile(f);
            } else {
                prop.load(new FileInputStream(f));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static ConfigManager getInstance(){
        return new ConfigManager();
    }

    private void createPropertiesFile(File path) throws IOException {
        prop.setProperty("server.timeout", "2000");
        prop.setProperty("server.threads_number", "5");
        prop.setProperty("server.server_port", "42069");

        prop.store(new FileOutputStream(path),  null);
    }

    public String readProperty(String propName){
        return prop.getProperty(propName);
    }

    private File getDir() {
        String uri = new File("").getAbsolutePath() + "/servermail/src/main/server.properties";
        return new File(uri);
    }
}
