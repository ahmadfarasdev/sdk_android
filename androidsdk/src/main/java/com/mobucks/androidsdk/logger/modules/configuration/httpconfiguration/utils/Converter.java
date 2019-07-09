package com.mobucks.androidsdk.logger.modules.configuration.httpconfiguration.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.mobucks.androidsdk.logger.models.LogSeverity;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationData;

public class Converter {
    /**
     * Converts a json to a ConfigurationData data object
     * @param json
     * @return
     * @throws JSONException
     */
    public static ConfigurationData jsonToConfigurationData(String json ) throws JSONException {
        JSONObject jsonObject=new JSONObject(json);
        JSONArray levels=jsonObject.optJSONArray("level");
        int levelsCount=levels.length();

        List<LogSeverity> levelList=new ArrayList<>(levelsCount);
        for(int i=0; i<levelsCount; i++){
            String level=levels.getString(i);
            levelList.add(LogSeverity.valueOf(level.toUpperCase()));
        }
        int bufferDelay=jsonObject.optInt("bufferDelay");
        return  new ConfigurationData(levelList,bufferDelay);
    }

}
