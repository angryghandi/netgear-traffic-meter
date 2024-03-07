package com.angryghandi.network.traffic.service.impl;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class NetgearCookieJarTest {

    @Mock
    private HttpUrl httpUrlMock;

    private List<Cookie> cookies;

    private NetgearCookieJar cut;

    @BeforeEach
    void beforeEach() {
        cut = new NetgearCookieJar();
        cookies = List.of();
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(httpUrlMock);
    }

    @Test
    void loadForRequest() {
        final List<Cookie> results = cut.loadForRequest(httpUrlMock);

        assertThat(results).isEmpty();
    }

    @Test
    void saveFromResponse() {
        cut.saveFromResponse(httpUrlMock, cookies);
        final List<Cookie> results = cut.loadForRequest(httpUrlMock);

        assertThat(results).isEqualTo(cookies);
    }
}
