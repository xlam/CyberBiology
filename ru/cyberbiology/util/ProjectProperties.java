package ru.cyberbiology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ProjectProperties extends Properties {

    private static ProjectProperties instance;

    public static ProjectProperties getInstance() {
        if (!(instance instanceof ProjectProperties)) {
            instance = new ProjectProperties("properties.xml");
        }
        return instance;
    }

    String fileName = "properties.xml";

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

    public void load() {
        try {
            this.loadFromXML(new FileInputStream(this.fileName));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

	public void save() {
        try {
            this.storeToXML(new FileOutputStream(this.fileName), null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
