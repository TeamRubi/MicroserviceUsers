package com.gfttraining.config;

import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();

		modelMapper.getConfiguration()
		.setPropertyCondition(Conditions.isNotNull())
		.setPropertyCondition(skipEmptyLists());

		return modelMapper;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	private Condition<?, ?> skipEmptyLists() {
		return ctx -> {
			Object sourceValue = ctx.getSource();
			if (sourceValue instanceof Collection) {
				return !((Collection<?>) sourceValue).isEmpty();
			} else {
				return sourceValue != null;
			}
		};
	}

}
