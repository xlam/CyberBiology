package ru.cyberbiology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import ru.cyberbiology.Constant;

public class ProjectProperties extends Properties {

    private static final String PROPERTIES_FILE = "properties.xml";
    private static ProjectProperties instance;

    public static ProjectProperties getInstance() {
        if (!(instance instanceof ProjectProperties)) {
            instance = new ProjectProperties(PROPERTIES_FILE);
        }
        return instance;
    }

    private String fileName = PROPERTIES_FILE;

    private ProjectProperties(String fileName) {
        defaults = new Properties();
        this.fileName = fileName;
        this.loadDefaults();
        this.loadUser();
    }

    public void setFileDirectory(String name) {
        String dirName = "";
        if (name != null) {
            if (name.length() > 0 && !name.endsWith(File.separator)) {
                dirName = name + File.separator;
            }
        }
        this.setProperty("FileDirectory", dirName);
    }

    public String getFileDirectory() {
        return this.getProperty("FileDirectory");
    }

    public int botSize() {
        return Integer.parseInt(getProperty("botSize", "" + Constant.DEFAULT_BOT_SIZE));
    }

    private void loadUser() {
        try {
            this.loadFromXML(new FileInputStream(this.fileName));
        } catch (IOException e) {
            System.err.println("WARNING: user settings can not be loaded, using defaults!");
        }
    }

    private void loadDefaults() {
        InputStream propertiesStream = getClass().getResourceAsStream("/" + PROPERTIES_FILE);
        if (null == propertiesStream) {
            System.err.println("ERROR: default settings can not be loaded!");
            System.exit(1);
        }
        try {
            defaults.loadFromXML(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.storeToXML(new FileOutputStream(this.fileName), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object setProperty(String key, String value) {
        Object oldValue = super.setProperty(key, value);
        save();
        return oldValue;
    }

    public boolean getBoolean(String property) {
        return "true".equals(getProperty(property, "false"));
    }
}
