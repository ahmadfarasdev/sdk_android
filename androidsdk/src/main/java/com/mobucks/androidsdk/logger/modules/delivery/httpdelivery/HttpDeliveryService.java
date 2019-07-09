package com.mobucks.androidsdk.logger.modules.delivery.httpdelivery;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.mobucks.androidsdk.logger.models.LogEvent;
import com.mobucks.androidsdk.logger.modules.delivery.core.DeliveryService;
import com.mobucks.androidsdk.logger.modules.delivery.core.models.DeliveryJob;
import com.mobucks.androidsdk.logger.utils.JobCallable;
import com.mobucks.androidsdk.logger.utils.net.HttpHelper;
import com.mobucks.androidsdk.logger.utils.net.exceptions.HttpFailureException;

public class HttpDeliveryService implements DeliveryService{
    private String endPoint;
    private String id;

    public HttpDeliveryService(String serviceUrl) {
        id= UUID.randomUUID().toString();
        endPoint=serviceUrl;
    }

    @Override
    public void deliverEvents(List<LogEvent> events, JobCallable<DeliveryJob> responseStatus) {
            new DeliverEvents(events).execute(responseStatus);
    }

    @Override
    public String getServiceUUID() {
        return id;
    }

    /**
     * Delivers Log events to the Log Server
     */
    private class DeliverEvents extends AsyncTask<JobCallable<DeliveryJob>, Void, DeliveryJob> {
        private JobCallable<DeliveryJob> logEventsSent;
        private List<LogEvent> events;

        public DeliverEvents(List<LogEvent> events) {
            this.events = events;
        }

        @Override
        protected DeliveryJob doInBackground(JobCallable<DeliveryJob>... params) {
            logEventsSent=params[0];
            DeliveryJob deliveryJob=new DeliveryJob(id);
            deliveryJob.setStatus(false);
            try {
                JSONArray jsonArray=new JSONArray();
                for(LogEvent logEvent:events){
                    jsonArray.put(logEvent.toJsonObject());
                }
                HttpHelper.postJsonRequest(endPoint, jsonArray);
                deliveryJob.setStatus(true);
            } catch (IOException e) {
                Log.e(HttpDeliveryService.class.getName(), "IO error on delivery: " + e.getLocalizedMessage());
            } catch (HttpFailureException e) {
                Log.e(HttpDeliveryService.class.getName(), "Buffer send failed http code: " + e.getHttpCode());
            } catch (JSONException e) {
                Log.e(HttpDeliveryService.class.getName(), "Error when converting LogEvents to Json: " + e.getLocalizedMessage());
            }

            return deliveryJob;
        }

        @Override
        protected void onPostExecute(DeliveryJob deliveryJob) {
            logEventsSent.call(deliveryJob);
        }

    }
}
