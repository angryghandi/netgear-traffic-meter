package com.angryghandi.network.traffic.service.impl;

import com.angryghandi.network.traffic.entity.TrafficSource;
import com.angryghandi.network.traffic.service.NetgearClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetgearClientImpl implements NetgearClient {

    private final OkHttpClient okHttpClient;

    @Override
    public String getTrafficMeter(final TrafficSource trafficSource) {
        String html;
        final Request request = new Request.Builder()
                .url(trafficSource.getUrl())
                .header("Authorization", Credentials.basic(trafficSource.getUsername(), trafficSource.getPassword()))
                .build();

        final Call call = okHttpClient.newCall(request);
        try (final Response response = call.execute()) {
            final ResponseBody responseBody = response.body();
            if (isNull(responseBody)) {
                throw new IOException("response body was null");
            }
            html = responseBody.string();

        } catch (final IOException e) {
            log.error("get traffic meter failed: {}", e.getMessage());
            html = null;
        }

        return html;
    }
}
