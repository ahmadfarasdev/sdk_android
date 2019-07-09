package com.mobucks.androidsdk.logger;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mobucks.androidsdk.logger.exceptions.DeliveryServiceAlreadyRegisteredException;
import com.mobucks.androidsdk.logger.models.LogSeverity;
import com.mobucks.androidsdk.logger.models.LogEvent;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationData;
import com.mobucks.androidsdk.logger.modules.configuration.core.ConfigurationSource;
import com.mobucks.androidsdk.logger.modules.configuration.httpconfiguration.HttpConfigurationSource;
import com.mobucks.androidsdk.logger.modules.delivery.core.DeliveryService;
import com.mobucks.androidsdk.logger.modules.delivery.httpdelivery.HttpDeliveryService;
import com.mobucks.androidsdk.logger.utils.JobCallable;
import com.mobucks.androidsdk.tools.Tools;


public class LoggerEngine {
    private ConfigurationSource configurationSource;
    private BufferController bufferController;
    private List<LogSeverity> severityFilter = Arrays.asList(LogSeverity.values());
    private String publisherId;


    public LoggerEngine(String publisherId, BufferController bufferController, ConfigurationSource configurationSource) {
        this.publisherId = publisherId;
        this.bufferController = bufferController;
        this.configurationSource = configurationSource;
        requestConfiguration();
    }

    /**
     * Request configuration from the configuration source
     * and initializes buffer controller
     */
    private void requestConfiguration() {
        JobCallable<Boolean> configurationLoaded = new JobCallable<Boolean>() {
            @Override
            public boolean call(Boolean retrievedOk) {
                if (!retrievedOk) {
                    Log.w(LoggerEngine.class.getName(), "Failed to retrieve configuration, default loaded");
                }
                ConfigurationData configurationData = configurationSource.getConfigurationData();
                bufferController.setBufferDelay(configurationData.getBufferDelay());
                severityFilter = configurationData.getLevel();
                bufferController.wasteUnwantedLogEvents(severityFilter);
                bufferController.startDeliveryService();
                return retrievedOk;
            }
        };
        configurationSource.loadConfigurationData(configurationLoaded);
    }

    public void logEvent(LogEvent logEvent) {
        if (severityFilter.contains(logEvent.getLevel())) {
            bufferController.addLogEventToBuffer(logEvent);
        }

    }

    public void logInfoEvent(String msg) {
        LogEvent logEvent =new LogEvent(msg,  LogSeverity.INFO);
        logEvent(logEvent);
    }

    public void logWarningEvent(String msg) {
        LogEvent logEvent =new LogEvent(msg,  LogSeverity.WARNING);
        logEvent(logEvent);
    }

    public void logSugestionEvent(String msg) {
        LogEvent logEvent =new LogEvent(msg,  LogSeverity.SUGGESTION);
        logEvent(logEvent);
    }

    public void logErrorEvent(String msg,Exception e) {
        LogEvent logEvent =new LogEvent(msg,  LogSeverity.ERROR);
        logEvent.setStackTrace(Tools.exceptionToStackTrace(e));
        logEvent(logEvent);
    }

    public void logSystemExceptionEvent(String msg,Exception e) {
        LogEvent logEvent =new LogEvent(msg,  LogSeverity.SYSTEM_EXCEPTION);
        logEvent.setStackTrace(Tools.exceptionToStackTrace(e));
        logEvent(logEvent);
    }

    public static class LoggerBuilder {
        private String publisherId;
        private ConfigurationSource configurationSource;
        private List<DeliveryService> deliveryServices = new ArrayList<>();
        private String delfaultConfigurationUrl = "";
        private String delfaultDeliveryServiceUrl = "http://www.mymobucks.com/logger/";
        private List<String> registeredDeliveryServices = new ArrayList<>();
        private int maxBufferTransfer = 100;
        private int failedTranfersCap = 3;

        public LoggerBuilder() {
        }

        private ConfigurationSource getDefaultConfigurationSource() {
            return new HttpConfigurationSource(delfaultConfigurationUrl, "");

        }

        private DeliveryService getDefaultDeliveryService() {
            return new HttpDeliveryService(delfaultDeliveryServiceUrl);

        }

        public LoggerBuilder addDeliveryService(DeliveryService deliveryService) {
            String serviceId = deliveryService.getServiceUUID();
            if (!registeredDeliveryServices.contains(serviceId)) {
                deliveryServices.add(deliveryService);
            } else {
                try {
                    throw new DeliveryServiceAlreadyRegisteredException("Delivery service " + serviceId + " already registered");
                } catch (DeliveryServiceAlreadyRegisteredException e) {
                    e.printStackTrace();
                }
            }

            return this;
        }

        public LoggerBuilder maxBufferTransfer(int maxBufferTransfer) {
            this.maxBufferTransfer = maxBufferTransfer;
            return this;
        }

        public LoggerBuilder failedTranfersCap(int failedTranfersCap) {
            this.failedTranfersCap = failedTranfersCap;
            return this;
        }

        public LoggerBuilder configurationSource(ConfigurationSource configurationSource) {
            this.configurationSource = configurationSource;
            return this;
        }

        public LoggerEngine build() {
            if (configurationSource == null) {
                configurationSource = getDefaultConfigurationSource();
            }
            if (deliveryServices.isEmpty()) {
                addDeliveryService(getDefaultDeliveryService());
            }
            return new LoggerEngine(publisherId, new BufferController(deliveryServices, maxBufferTransfer, failedTranfersCap), configurationSource);
        }
    }

    public String getPublisherId() {
        return publisherId;
    }

    public ConfigurationSource getConfigurationSource() {
        return configurationSource;
    }

    public BufferController getBufferController() {
        return bufferController;
    }
}
