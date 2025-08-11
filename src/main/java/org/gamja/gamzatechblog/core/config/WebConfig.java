package org.gamja.gamzatechblog.core.config;

import java.util.List;

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
				"https://gamzatech.site",
				"https://www.gamzatech.site",
				"http://gamzatech.site",
				"http://www.gamzatech.site",
				"https://dev.gamzatech.site:3000",
				"http://dev.gamzatech.site:3000",
				"https://app.gamzatech.site",
				"http://app.gamzatech.site",
				"https://gamza-tech-blog-front.vercel.app/",
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
