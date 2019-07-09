package com.mobucks.androidsdk.logger.modules.delivery.core;

import java.util.List;

import com.mobucks.androidsdk.logger.models.LogEvent;
import com.mobucks.androidsdk.logger.modules.delivery.core.models.DeliveryJob;
import com.mobucks.androidsdk.logger.utils.JobCallable;

public interface DeliveryService {
        String getServiceUUID();
        void deliverEvents(List<LogEvent> events,JobCallable<DeliveryJob> responseStatus);
}
