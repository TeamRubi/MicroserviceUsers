package com.gfttraining.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlag {

	private boolean enablePromotion;
	private boolean enableUserExtraInfo;

}
