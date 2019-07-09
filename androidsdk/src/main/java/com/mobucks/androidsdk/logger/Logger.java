package com.mobucks.androidsdk.logger;

import com.mobucks.androidsdk.logger.models.LogSeverity;
import com.mobucks.androidsdk.logger.modules.configuration.localconfiguration.LocalConfiguration;
import com.mobucks.androidsdk.logger.modules.delivery.httpdelivery.HttpDeliveryService;
import com.mobucks.androidsdk.logger.modules.delivery.logcatdelivery.LogcatDeliveryService;

public class Logger {
    private static LoggerEngine logger;
    private static boolean debug =false;
    static {
        logger = new LoggerEngine.LoggerBuilder()
                .configurationSource(new LocalConfiguration(LogSeverity.ERROR, 5000))
                .addDeliveryService(new HttpDeliveryService("http://www.mymobucks.com/logger/"))
                .addDeliveryService(new LogcatDeliveryService())
                .build();
    }

    public static void d(String msg) {
        if(debug){
            logger.logInfoEvent(msg);
        }

    }
    public static void i(String msg) {
        logger.logInfoEvent(msg);
    }

    public static void w(String msg) {
        logger.logWarningEvent(msg);
    }

    public static void s(String msg) {
        logger.logSugestionEvent(msg);
    }

    public static void e(String msg, Exception e) {
        logger.logErrorEvent(msg, e);
    }

    public static void se(String msg, Exception e) {
        logger.logSystemExceptionEvent(msg, e);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }
}
