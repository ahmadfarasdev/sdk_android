package com.mobucks.androidsdk.logger.modules.delivery.core.models;

import java.util.List;

import com.mobucks.androidsdk.logger.models.LogEvent;

public class DeliveryJob {
    private boolean status=false;
    private String serviceId;
    private List<LogEvent> deliveryData;
    public DeliveryJob(String serviceId) {

        this.serviceId = serviceId;
    }



    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<LogEvent> getDeliveryData() {
        return deliveryData;
    }

    public void setDeliveryData(List<LogEvent> deliveryData) {
        this.deliveryData = deliveryData;
    }
}
