package com.erayoezer.acmeshop.service.email;

import com.erayoezer.acmeshop.model.email.EmailPayload;
import com.erayoezer.acmeshop.model.email.Recipient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.*;

public class MailerSendImplTest {

    @Mock
    private EmailPayload emailPayload;

    private MailerSendImpl mailerSendImpl;
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mailerSendImpl = new MailerSendImpl("test-token", "http://localhost:8089/v1/email");
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testSendEmail() throws IOException {
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/v1/email"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)));

        Recipient[] to = {new Recipient("test@example.com")};
        doReturn(to).when(emailPayload).getTo();

        mailerSendImpl.sendEmail(emailPayload);

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/v1/email"))
                .withHeader("Content-Type", WireMock.equalTo("application/json"))
                .withHeader("X-Requested-With", WireMock.equalTo("XMLHttpRequest"))
                .withHeader("Authorization", WireMock.equalTo("Bearer test-token")));

        verify(emailPayload).getTo();
    }
}
