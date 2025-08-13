package org.gamja.gamzatechblog.core.config;

import java.time.Duration;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

	@Bean
	public RestTemplate restTemplate() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(100);
		cm.setDefaultMaxPerRoute(20);

		RequestConfig rc = RequestConfig.custom()
			.setConnectTimeout(Timeout.ofMilliseconds(1000))
			.setConnectionRequestTimeout(Timeout.ofMilliseconds(1000))
			.setResponseTimeout(Timeout.ofMilliseconds(1500))
			.build();

		CloseableHttpClient httpClient = HttpClients.custom()
			.setConnectionManager(cm)
			.setDefaultRequestConfig(rc)
			.evictIdleConnections(TimeValue.ofSeconds(30))
			.build();

		HttpComponentsClientHttpRequestFactory rf =
			new HttpComponentsClientHttpRequestFactory(httpClient);
		rf.setConnectTimeout(Duration.ofMillis(1000));
		rf.setReadTimeout(Duration.ofMillis(1500));

		return new RestTemplate(rf);
	}
}
