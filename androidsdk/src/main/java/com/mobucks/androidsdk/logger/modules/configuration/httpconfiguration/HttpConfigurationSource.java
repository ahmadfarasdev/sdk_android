package com.mobucks.androidsdk.logger.modules.configuration.httpconfiguration;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import java.io.IOException;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationData;
import com.mobucks.androidsdk.logger.modules.configuration.httpconfiguration.utils.Converter;
import com.mobucks.androidsdk.logger.utils.JobCallable;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationSource;
import com.mobucks.androidsdk.logger.utils.net.HttpHelper;

/**
 * Created by theokir on 23/1/2016.
 */
public class HttpConfigurationSource implements ConfigurationSource {
    private ConfigurationData configurationData;
    private String endPoint;


    public HttpConfigurationSource(String serviceUrl, String publisherId) {
        endPoint=(serviceUrl.endsWith("/")?serviceUrl:serviceUrl+"/")+publisherId;
    }

    @Override
    public void loadConfigurationData(JobCallable configurationLoaded) {
                new GetConfigurationTask().execute(configurationLoaded);
    }

    @Override
    public ConfigurationData getConfigurationData() {
        return configurationData;
    }

    /**
     * Requesting the Configuration from the Configuration Server.
     * If the request fails, the default configuration is loaded.
     */
    private class GetConfigurationTask extends AsyncTask<JobCallable<Boolean>, Void, Boolean> {
        JobCallable<Boolean> configurationLoaded;

        @Override
        protected Boolean doInBackground(JobCallable<Boolean>... params) {
            configurationLoaded=params[0];
            try {
                String response=HttpHelper.getRequest(endPoint);
                configurationData= Converter.jsonToConfigurationData(response);
                return true;
            } catch (IOException e) {
                Log.e(HttpConfigurationSource.class.getName(), "IO error on configuration retrieval: " + e.getLocalizedMessage());
            } catch (JSONException e) {
                Log.e(HttpConfigurationSource.class.getName(), "Error when converting Json to ConfigurationData: " + e.getLocalizedMessage());
            }
            if(configurationData==null){
                configurationData=new ConfigurationData();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean retrievedOk) {
            configurationLoaded.call(retrievedOk);
        }
    }






}
