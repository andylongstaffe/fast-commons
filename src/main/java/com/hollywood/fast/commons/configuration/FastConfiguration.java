package com.hollywood.fast.commons.configuration;

import org.apache.commons.configuration.Configuration;

public interface FastConfiguration extends MultiEnvironmentConfiguration {

  Configuration getConfig();

  String getCurrentEnv();
}
