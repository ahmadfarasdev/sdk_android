package com.mobucks.androidsdk.logger;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobucks.androidsdk.logger.models.LogSeverity;
import com.mobucks.androidsdk.logger.models.LogEvent;
import com.mobucks.androidsdk.logger.modules.delivery.core.DeliveryService;
import com.mobucks.androidsdk.logger.modules.delivery.core.models.DeliveryJob;
import com.mobucks.androidsdk.logger.utils.JobCallable;

public class BufferController {
    private Map<String,DeliveryService> deliveryServices=new HashMap<>();
    private List<LogEvent> logEventBuffer=new ArrayList<>();

    private int bufferDelay=5000;
    private int maxBufferTransfer;   //The max number of log events for one delivery, safety measure for max post size
    private int failedTranfersCap;    //The number of failed attempts before we remove the delivery service

    private Handler pushHandler=new Handler(Looper.getMainLooper());
    private Map<String,List<LogEvent>> undeliveredJobs=new HashMap<>();
    private Map<String,Integer> failedJobsPerDeliveryService=new HashMap<>();
    private boolean online=false;
    private JobCallable<DeliveryJob> deliverResponseAction=new JobCallable<DeliveryJob>() {
        @Override
        public boolean call(DeliveryJob deliveryJob) {
            if(deliveryJob.isStatus()){
                failedJobsPerDeliveryService.remove(deliveryJob.getServiceId());
            }else{
                handleUndelivery(deliveryJob);
            }
            return false;
        }
    };

    public BufferController(List<DeliveryService> deliveryServices, int maxBufferTransfer, int failedTranfersCap) {
        for(DeliveryService deliveryService:deliveryServices){
            addService(deliveryService);
        }
        this.maxBufferTransfer = maxBufferTransfer;
        this.failedTranfersCap = failedTranfersCap;
    }

    /**
     * Adds failed deliveries to queue  or deletes the specific delivery
     * service when the failed attempts reach  failedTranfersCap.
     * @param deliveryJob
     */
    private void handleUndelivery(DeliveryJob deliveryJob){
        String serviceId=deliveryJob.getServiceId();
        Integer failAttempts=failedJobsPerDeliveryService.get(serviceId);
        failAttempts=failAttempts==null?1:++failAttempts;
        if(failAttempts>=failedTranfersCap){
            Log.w(BufferController.class.getName(),"Service "+serviceId+" removed due to "+failAttempts+ " failed deliveries");
            removeService(serviceId);
        }else{
            failedJobsPerDeliveryService.put(serviceId,failAttempts);
            List<LogEvent> undeliveredList= undeliveredJobs.get(serviceId);
            if(undeliveredList==null){
                undeliveredList=deliveryJob.getDeliveryData();
            }else{
                for(LogEvent logEvent:deliveryJob.getDeliveryData()){
                    if(!undeliveredList.contains(logEvent)){
                        undeliveredList.add(logEvent);
                    }
                }
            }
            undeliveredJobs.put(serviceId,undeliveredList);
        }
    }

    /**
     * Adds a delivery service to the buffer controller,
     * delivery on this service begins immediately.
     * @param deliveryService
     */
    public void addService(DeliveryService deliveryService){
        deliveryServices.put(deliveryService.getServiceUUID(), deliveryService);
    }

    /**
     * Removes a delivery service and all of the Log Events in its
     * queue.
     * @param serviceId
     */
    public void removeService(String serviceId){
        deliveryServices.remove(serviceId);
        undeliveredJobs.remove(serviceId);
        failedJobsPerDeliveryService.remove(serviceId);;
    }

    /**
     * The task that forces deliveries.
     */
    private Runnable pushTask=new Runnable() {
        @Override
        public void run() {
            List<LogEvent> events=getEventsForDelivery(logEventBuffer);
            for(Map.Entry<String,DeliveryService> entry:deliveryServices.entrySet()){
                entry.getValue().deliverEvents(addUndeliveredJobs(entry.getKey(),events), deliverResponseAction);
            }
        }
    };


    /**
     * Add a log event to the buffer, the event will be delivered
     * in bufferDelay millis if no another arrives in that time.
     * @param logEvent
     */
    public void addLogEventToBuffer(LogEvent logEvent){
        logEventBuffer.add(logEvent);
        updateHandlerJob();
    }

    /**
     * Resets the timer to bufferDelay every time a new
     * log event arrives.
     */
    private void updateHandlerJob(){
        if(online) {
            pushHandler.removeCallbacks(pushTask);
            pushHandler.postDelayed(pushTask, bufferDelay);
        }
    }

    /**
     * Removes the pushTask  from queue.
     */
    private void removeHandlerJob(){
        pushHandler.removeCallbacks(pushTask);
    }

    /**
     * Takes a List with log events as input and constructs a new list
     * with max size equals to maxBufferTransfer. The items are removed from the
     * input list.
     * @param eventPool
     * @return
     */
    private List<LogEvent> getEventsForDelivery(List<LogEvent> eventPool){
        List<LogEvent> eventsFordelivery=new ArrayList<>();
        int eventPoolSize=eventPool.size();
        int bufferSize=eventPoolSize>maxBufferTransfer?maxBufferTransfer:eventPoolSize;
        for(int i=0; i<bufferSize; i++){
            eventsFordelivery.add(eventPool.get(i));
        }
        for(LogEvent logEvent:eventsFordelivery){
            eventPool.remove(logEvent);
        }
        return eventsFordelivery;
    }

    /**
     * Takes a List with log events and adds any log events that the specific
     * service has on queue. The max size of the new list equals to maxBufferTransfer.
     * Log Events on queue are delivered first.
     * @param serviceId
     * @param eventsForDelivery
     * @return
     */
    private List<LogEvent> addUndeliveredJobs(String serviceId,List<LogEvent> eventsForDelivery){
        List<LogEvent> events;
        if(undeliveredJobs.containsKey(serviceId)){
            List<LogEvent>  undeliveredList= undeliveredJobs.get(serviceId);
            undeliveredList.addAll(eventsForDelivery);
            events= getEventsForDelivery(undeliveredList);
        }else{
            events =new ArrayList<>();
            events.addAll(eventsForDelivery);
        }
        return events;
    }


    public int getBufferDelay() {
        return bufferDelay;
    }

    public void setBufferDelay(int bufferDelay) {
        this.bufferDelay = bufferDelay;
    }

    public int getMaxBufferTransfer() {
        return maxBufferTransfer;
    }

    public void setMaxBufferTransfer(int maxBufferTransfer) {
        this.maxBufferTransfer = maxBufferTransfer;
    }

    public int getFailedTranfersCap() {
        return failedTranfersCap;
    }

    public void setFailedTranfersCap(int failedTranfersCap) {
        this.failedTranfersCap = failedTranfersCap;
    }

    public boolean isOnline() {
        return online;
    }

    /**
     * Starts the delivery process
     */
    public void startDeliveryService(){
        online=true;
        if(!logEventBuffer.isEmpty()){
            updateHandlerJob();
        }
    }
    /**
     * Stops the delivery process
     */
    public void stopDeliveryService(){
        online=false;
        removeHandlerJob();
    }

    /**
     * Removes any unwanted  log events that were logged before the loadConfiguration
     * completed.
     * @param severityFilter
     */
    public void wasteUnwantedLogEvents(List<LogSeverity> severityFilter){
        if(!logEventBuffer.isEmpty()){
            List<LogEvent> unwantedLogEvents=new ArrayList<>();
            for(LogEvent logEvent:logEventBuffer){
                if(!severityFilter.contains(logEvent.getLevel())){
                    unwantedLogEvents.add(logEvent);
                }
            }
            for(LogEvent logEvent:unwantedLogEvents){
                logEventBuffer.remove(logEvent);
            }
        }

    }

}
