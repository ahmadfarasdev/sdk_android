package com.mobucks.androidsdk.logger.modules.configuration.core;

import com.mobucks.androidsdk.logger.utils.JobCallable;


public interface ConfigurationSource {
     void loadConfigurationData(JobCallable<Boolean> configurationLoaded);
     ConfigurationData getConfigurationData();

}
