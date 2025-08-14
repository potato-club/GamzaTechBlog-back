package org.gamja.gamzatechblog.core.config.async;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

	@Bean(name = "postExecutor")
	public Executor postExecutor() {
		ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
		ex.setCorePoolSize(4);
		ex.setMaxPoolSize(8);
		ex.setQueueCapacity(200); // 요청 몰릴 경우 대비
		ex.setThreadNamePrefix("post-async-");
		ex.initialize();
		return ex;
	}

	@Override
	public Executor getAsyncExecutor() {
		return postExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) ->
			log.error("비동기 처리 중 예외 발생 → method={}, params={}", method.getName(), params, ex);
	}
}
