package com.angryghandi.network.traffic.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RetryInterceptor implements Interceptor {

    @Value("${netgear-traffic-meter.retry-limit}")
    private Integer retryLimit;

    @NotNull
    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();

        // try the request
        Response response = chain.proceed(request);

        int tryCount = 1;
        while (!response.isSuccessful() && tryCount <= retryLimit) {
            log.info("response code: {}, tryCount: {} of retryLimit: {}", response.code(), tryCount, retryLimit);

            tryCount++;
            // retry the request
            response.close();
            response = chain.proceed(request);
        }

        // otherwise just pass the original response on
        return response;
    }
}