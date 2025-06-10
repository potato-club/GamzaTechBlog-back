package org.gamja.gamzatechblog.core.config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${openapi.server-url:}")
	private String serverUrl;

	private SecurityScheme createBearerAuthScheme() {
		return new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	private OpenApiCustomizer createOpenApiCustomizer(String title, String version) {
		return openApi -> {
			openApi.info(new Info().title(title).version(version));
			openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
			openApi.schemaRequirement("bearerAuth", createBearerAuthScheme());
			if (!serverUrl.isBlank()) {
				openApi.setServers(List.of(new Server().url(serverUrl)));
			}
		};
	}

	@Bean
	public GroupedOpenApi allApi() {
		return GroupedOpenApi.builder()
			.group("all")
			.pathsToMatch("/**")
			.displayName("All API")
			.addOpenApiCustomizer(createOpenApiCustomizer("모든 API", "v0.4"))
			.build();
	}

}
