package com.aimms.assignment.Util;

import java.io.File;
import java.net.URL;

public class UtilityClass {

    // get file from classpath, resources folder
    public static File getFileFromResources(String fileName) {
        ClassLoader classLoader = UtilityClass.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }
}
