package com.angryghandi.network.traffic.config;

import com.angryghandi.network.traffic.service.impl.NetgearCookieJar;
import com.angryghandi.network.traffic.service.impl.RetryInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public OkHttpClient okHttpClient(final RetryInterceptor retryInterceptor, final NetgearCookieJar netgearCookieJar) {
        return new OkHttpClient.Builder()
                .addInterceptor(retryInterceptor)
                .cookieJar(netgearCookieJar)
                .build();
    }
}
