package ru.cyberbiology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import ru.cyberbiology.Const;

public class ProjectProperties extends Properties {

    private static final String PROPERTIES_FILE = "properties.xml";
    private static ProjectProperties instance;
    private String fileName = PROPERTIES_FILE;

    public static ProjectProperties getInstance() {
        if (!(instance instanceof ProjectProperties)) {
            instance = new ProjectProperties(PROPERTIES_FILE);
        }
        return instance;
    }

    private ProjectProperties(String fileName) {
        this.fileName = fileName;
        this.load();
    }

    public void setFileDirectory(String name) {
        if (name == null) {
            name = "";
        }
        if (name.length() > 0 && !name.endsWith(File.separator)) {
            name += File.separator;
        }

        this.setProperty("FileDirectory", name);
        this.save();
    }

    public String getFileDirectory() {
        return this.getProperty("FileDirectory");
    }

    public int botSize() {
        return Integer.parseInt(getProperty("botSize", "" + Const.DEFAULT_BOT_SIZE));
    }

    public void load() {
        try {
            this.loadFromXML(new FileInputStream(this.fileName));
        } catch (IOException e) {
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        InputStream propertiesStream = getClass().getResourceAsStream("/" + PROPERTIES_FILE);
        if (null == propertiesStream) {
            System.err.println("ERROR: default.properties file not found!");
            System.exit(1);
        }
        try {
            loadFromXML(propertiesStream);
        } catch (IOException ex) {
            ex.printStackTrace();
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
}
