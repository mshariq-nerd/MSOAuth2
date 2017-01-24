package com.nerdapplabs.forumapp.oauth.constant;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by mohd on 23/01/17.
 */

public class ReadForumProperties {
    InputStream inputStream;
    public Properties getPropertiesValues(Context context) throws IOException {
        Properties properties = new Properties();
        String propertiesFielName = "app.properties";
        inputStream = context.getAssets().open(propertiesFielName);
        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propertiesFielName + "' not found in the classpath");
        }
        return properties;
    }
}
