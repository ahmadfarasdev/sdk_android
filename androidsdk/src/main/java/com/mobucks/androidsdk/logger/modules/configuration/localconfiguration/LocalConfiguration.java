package com.mobucks.androidsdk.logger.modules.configuration.localconfiguration;

import com.mobucks.androidsdk.logger.models.LogSeverity;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationData;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationSource;
import com.mobucks.androidsdk.logger.utils.JobCallable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalConfiguration implements ConfigurationSource {
    private ConfigurationData configurationData;

    public LocalConfiguration( LogSeverity logSeverity,int bufferDelay) {
        configurationData =new ConfigurationData();
        configurationData.setBufferDelay(bufferDelay);
        List<LogSeverity> levelsToServer = new ArrayList<>();
        for ( LogSeverity ls: LogSeverity.values()){
            if(ls.ordinal()>= logSeverity.ordinal()){
                levelsToServer.add(logSeverity);
            }
        }
        configurationData.setLevel(levelsToServer);
    }

    @Override
    public void loadConfigurationData(JobCallable<Boolean> configurationLoaded) {
        configurationLoaded.call(true);
    }

    @Override
    public ConfigurationData getConfigurationData() {
        return configurationData;
    }
}
