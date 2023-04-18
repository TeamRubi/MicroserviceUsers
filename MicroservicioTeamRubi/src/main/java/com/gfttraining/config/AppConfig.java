package com.gfttraining.config;

import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

@Configuration
@Data
@Component
public class AppConfig {

	@Value("${paths.user}")
	private String userPath;

	@Value("${paths.favorite}")
	private String favoritePath;

	@Value("${paths.user-carts}")
	private String userCartsPath;

	@Value("${feature-flags.enablefavorites}")
	private boolean flagEnableFavorites;

	@Value("${feature-flags.promotion}")
	private boolean flagPromotion;


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
