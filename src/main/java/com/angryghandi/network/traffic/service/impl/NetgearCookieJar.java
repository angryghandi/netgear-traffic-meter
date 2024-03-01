package com.angryghandi.network.traffic.service.impl;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Get the session cookies from the first call to the client and make them available to subsequent calls.
 */
@Component
public class NetgearCookieJar implements CookieJar {

    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(@NotNull final HttpUrl httpUrl, @NotNull final List<Cookie> list) {
        cookies = list;
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull final HttpUrl httpUrl) {
        if (isNull(cookies)) {
            cookies = new ArrayList<>();
        }
        return cookies;
    }

}
