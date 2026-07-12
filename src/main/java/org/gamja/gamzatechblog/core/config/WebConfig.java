package org.gamja.gamzatechblog.core.config;

import java.util.List;

import org.gamja.gamzatechblog.core.config.http.MultipartJackson2HttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final MultipartJackson2HttpMessageConverter multipartConverter;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(
				"http://localhost:3000",
				"https://localhost:3000",
				"https://gamza.site",
				"https://www.gamza.site",
				"http://gamza.site",
				"http://www.gamza.site",
				"https://app.gamza.site",
				"http://app.gamza.site",
				"https://dev.gamza.site",
				"http://dev.gamza.site",
				"https://dev.gamza.site:3000",
				"http://dev.gamza.site:3000",
				"https://preview.gamza.site",
				"http://preview.gamza.site",
				"https://gamza-tech-blog-front.vercel.app",
				"http://gamza-tech-blog-front.vercel.app"
			)
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowCredentials(true)
			.exposedHeaders("Authorization");
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, multipartConverter);
	}
}
