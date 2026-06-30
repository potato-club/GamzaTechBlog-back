package org.gamja.gamzatechblog.core.config.security;

import java.util.List;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

/**
 * 로그인 임계경로(코드→토큰 교환, 유저정보 조회)의 GitHub 호출을 커넥션 풀 재사용 + 타임아웃으로 튜닝한다.
 * 기본 클라이언트는 풀링이 없어 매 로그인마다 TLS 핸드셰이크를 새로 하고 타임아웃이 없어 GitHub 지연 시 매달린다.
 */
@Configuration
public class OAuth2ClientConfig {

	@Bean
	public ClientHttpRequestFactory oauthClientHttpRequestFactory() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(50);
		cm.setDefaultMaxPerRoute(20);

		RequestConfig rc = RequestConfig.custom()
			.setConnectTimeout(Timeout.ofSeconds(2))
			.setConnectionRequestTimeout(Timeout.ofSeconds(2))
			.setResponseTimeout(Timeout.ofSeconds(5))
			.build();

		CloseableHttpClient httpClient = HttpClients.custom()
			.setConnectionManager(cm)
			.setDefaultRequestConfig(rc)
			.evictIdleConnections(TimeValue.ofSeconds(30))
			.build();

		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Bean
	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> githubTokenResponseClient(
		ClientHttpRequestFactory oauthClientHttpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate(List.of(
			new FormHttpMessageConverter(),
			new OAuth2AccessTokenResponseHttpMessageConverter()
		));
		restTemplate.setRequestFactory(oauthClientHttpRequestFactory);
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
		client.setRestOperations(restTemplate);
		return client;
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> githubOAuth2UserService(
		ClientHttpRequestFactory oauthClientHttpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(oauthClientHttpRequestFactory);
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

		DefaultOAuth2UserService userService = new DefaultOAuth2UserService();
		userService.setRestOperations(restTemplate);
		return userService;
	}
}
