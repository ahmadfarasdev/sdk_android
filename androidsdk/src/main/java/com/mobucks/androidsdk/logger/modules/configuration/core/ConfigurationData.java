package com.mobucks.androidsdk.logger.modules.configuration.core;

import java.util.Arrays;
import java.util.List;

import com.mobucks.androidsdk.logger.models.LogSeverity;

public class ConfigurationData {
    private List<LogSeverity> level;
    private int bufferDelay;

    public ConfigurationData() {
        this(Arrays.asList(LogSeverity.values()),5000);
    }

    public ConfigurationData(List<LogSeverity> level, int bufferDelay) {
        this.level = level;
        this.bufferDelay = bufferDelay;
    }

    public List<LogSeverity> getLevel() {
        return level;
    }

    public void setLevel(List<LogSeverity> level) {
        this.level = level;
    }

    public int getBufferDelay() {
        return bufferDelay;
    }

    public void setBufferDelay(int bufferDelay) {
        this.bufferDelay = bufferDelay;
    }

}
