package com.mobucks.androidsdk.network.callbacks;

/**
 * Network call interface used in async network calls.
 * @param <T>
 */
public interface NetworkCall<T> {
    /**
     * When successfully completed
     * @param result
     */
    void onComplete(T result);

    /**
     * On error
     * @param error
     */
    void onError(Exception error);
}
