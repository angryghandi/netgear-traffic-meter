package com.angryghandi.network.traffic.service.impl;

import com.angryghandi.network.traffic.service.NetgearClient;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static com.angryghandi.network.traffic.TestConstants.HTML;
import static com.angryghandi.network.traffic.TestConstants.TRAFFIC_SOURCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class NetgearClientImplTest {

    @Mock
    private OkHttpClient okHttpClientMock;

    @Mock
    private Call callMock;

    @Mock
    private Response responseMock;

    @Mock
    private ResponseBody responseBodyMock;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    private NetgearClient cut;

    @BeforeEach
    void beforeEach() {
        cut = new NetgearClientImpl(okHttpClientMock);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(okHttpClientMock, callMock, responseMock, responseBodyMock);
    }

    @Test
    @SneakyThrows
    void getTrafficMeter_responseBody_null() {
        when(okHttpClientMock.newCall(requestCaptor.capture())).thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.body()).thenReturn(null);

        final String result = cut.getTrafficMeter(TRAFFIC_SOURCE);

        assertThat(result).isNull();

        verify(okHttpClientMock).newCall(requestCaptor.capture());
        verify(callMock).execute();
        verify(responseMock).body();
        verify(responseMock).close();
    }

    @Test
    @SneakyThrows
    void getTrafficMeter_execute_throws_ioException() {
        when(okHttpClientMock.newCall(requestCaptor.capture())).thenReturn(callMock);
        when(callMock.execute()).thenThrow(new IOException("call failure"));

        final String result = cut.getTrafficMeter(TRAFFIC_SOURCE);

        assertThat(result).isNull();

        verify(okHttpClientMock).newCall(requestCaptor.capture());
        verify(callMock).execute();
    }

    @Test
    @SneakyThrows
    void getTrafficMeter() {
        when(okHttpClientMock.newCall(requestCaptor.capture())).thenReturn(callMock);
        when(callMock.execute()).thenReturn(responseMock);
        when(responseMock.body()).thenReturn(responseBodyMock);
        when(responseBodyMock.string()).thenReturn(HTML);

        final String result = cut.getTrafficMeter(TRAFFIC_SOURCE);

        assertThat(result).isEqualTo(HTML);

        verify(okHttpClientMock).newCall(requestCaptor.capture());
        verify(callMock).execute();
        verify(responseMock).body();
        verify(responseMock).close();
        verify(responseBodyMock).string();
    }
}
