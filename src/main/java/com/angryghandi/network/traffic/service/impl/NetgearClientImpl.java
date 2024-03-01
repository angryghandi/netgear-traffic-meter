package com.angryghandi.network.traffic.service.impl;

import com.angryghandi.network.traffic.service.NetgearClient;
import lombok.RequiredArgsConstructor;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class NetgearClientImpl implements NetgearClient {

    @Value("${netgear-traffic-meter.router.url}")
    private String url;

    @Value("${netgear-traffic-meter.router.username}")
    private String username;

    @Value("${netgear-traffic-meter.router.password}")
    private String password;

    private final RetryInterceptor retryInterceptor;

    private final CookieJar cookieJar;

    @Override
    public String getTrafficMeter() {

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(retryInterceptor)
                .cookieJar(cookieJar)
                .build();

        String html;
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", Credentials.basic(username, password))
                .build();

        final Call call = client.newCall(request);
        try (final Response response = call.execute()) {
            if (isNull(response.body())) {
                throw new IOException("response body was null");
            }
            html = response.body().string();

        } catch (final IOException e) {
            html = null;
        }

        return html;
    }
}
