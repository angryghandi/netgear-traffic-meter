package com.angryghandi.network.traffic.service.impl;

import lombok.SneakyThrows;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class RetryInterceptorTest {

    @Mock
    private Interceptor.Chain chainMock;

    @Mock
    private Request requestMock;

    @Mock
    private Response responseMock;

    private RetryInterceptor cut;

    @BeforeEach
    void beforeEach() {
        cut = new RetryInterceptor();
        ReflectionTestUtils.setField(cut, "retryLimit", 3, Integer.class);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(chainMock, requestMock);
    }

    @Test
    @SneakyThrows
    void intercept_fails_once() {
        when(chainMock.request()).thenReturn(requestMock);
        when(chainMock.proceed(requestMock)).thenReturn(responseMock);
        when(responseMock.isSuccessful()).thenReturn(false).thenReturn(true);

        final Response response = cut.intercept(chainMock);

        assertThat(response).isEqualTo(responseMock);

        verify(chainMock).request();
        verify(chainMock, times(2)).proceed(requestMock);
        verify(responseMock, times(2)).isSuccessful();
        verify(responseMock).close();
    }

    @Test
    @SneakyThrows
    void intercept_exhausts_retries() {
        when(chainMock.request()).thenReturn(requestMock);
        when(chainMock.proceed(requestMock)).thenReturn(responseMock);
        when(responseMock.isSuccessful()).thenReturn(false);

        final Response response = cut.intercept(chainMock);

        assertThat(response).isEqualTo(responseMock);

        verify(chainMock).request();
        verify(chainMock, times(4)).proceed(requestMock);
        verify(responseMock, times(4)).isSuccessful();
        verify(responseMock, times(3)).close();
    }

    @Test
    @SneakyThrows
    void intercept_response_successful() {
        when(chainMock.request()).thenReturn(requestMock);
        when(chainMock.proceed(requestMock)).thenReturn(responseMock);
        when(responseMock.isSuccessful()).thenReturn(true);

        final Response response = cut.intercept(chainMock);

        assertThat(response).isEqualTo(responseMock);

        verify(chainMock).request();
        verify(chainMock).proceed(requestMock);
        verify(responseMock).isSuccessful();
    }

}
