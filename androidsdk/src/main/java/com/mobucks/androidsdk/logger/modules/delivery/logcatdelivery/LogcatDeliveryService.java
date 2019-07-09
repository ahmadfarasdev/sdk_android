package com.mobucks.androidsdk.logger.modules.delivery.logcatdelivery;

import android.util.Log;

import java.util.List;

import com.mobucks.androidsdk.logger.models.LogSeverity;
import com.mobucks.androidsdk.logger.models.LogEvent;
import com.mobucks.androidsdk.logger.modules.delivery.core.DeliveryService;
import com.mobucks.androidsdk.logger.modules.delivery.core.models.DeliveryJob;
import com.mobucks.androidsdk.logger.utils.JobCallable;

public class LogcatDeliveryService implements DeliveryService {


    @Override
    public String getServiceUUID() {
        return "LogCatDelivery";
    }

    /**
     * Delivers Log Events  to LogCat
     * @param events
     * @param responseStatus
     */
    @Override
    public void deliverEvents(List<LogEvent> events, JobCallable<DeliveryJob> responseStatus) {
        for(LogEvent logEvent:events){
            Log.println(getLogPriorityForAvoSeverity(logEvent.getLevel()),getServiceUUID(),logEvent.getMessage());
        }
        DeliveryJob deliveryJob=new DeliveryJob(getServiceUUID());
        deliveryJob.setStatus(true);

        responseStatus.call(deliveryJob);

    }

    /**
     * Convert LogSeverity to android logger priority
     * @param logSeverity
     * @return
     */
    private int getLogPriorityForAvoSeverity(LogSeverity logSeverity){
        int priority=0;
        switch (logSeverity){
            case  INFO:
                priority=Log.INFO;
                break;
            case  WARNING:
                priority=Log.WARN;
                break;
            case  ERROR:
                priority=Log.ERROR;
                break;
        }
        return priority;
    }
}
