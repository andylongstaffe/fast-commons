package com.hollywood.fast.commons.configuration.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hollywood.fast.commons.configuration.FastConfiguration;


public class DefaultFastConfiguration implements FastConfiguration {

  private CompositeConfiguration config;
  private Set<String> envTypes = new HashSet<String>();
  private final String[] defaultEnvTypes = new String[] { "dev",
      "functionaltest", "integration", "qa", "preprod", "prod" };
  private String envtype;
  private final Logger log = LoggerFactory.getLogger(DefaultFastConfiguration.class);

  public DefaultFastConfiguration(String configFilename) throws ConfigurationException {
    this(configFilename, "dev");
  }

  public DefaultFastConfiguration(String configFilename, String defaultEnvType) throws ConfigurationException {
    log.info("Loading configuration");
	for (String env : defaultEnvTypes) {
      addEnvType(env);
    }
    loadConfig(configFilename, defaultEnvType);
    setSpringProfile();
    log.info("Configuration set up successfully");
  }

  private void setSpringProfile() {
	String springProfileProperty = "spring.profiles.active";
	String springProfile = config.getString(springProfileProperty);
	if ( springProfileIsNotSet(springProfile) ) {
		log.debug("Set spring profile based on envtype");
		System.setProperty(springProfileProperty, getCurrentEnv());
		
		// config.getConfiguration(1).setProperty(springProfile, getCurrentEnv());
	}
	log.info("spring.profiles.active: " + getCurrentEnv());
}

private boolean springProfileIsNotSet(String p) {
	if ( p == null || p.isEmpty() ) {
		return true;
	}
	else {
		return false;
	}
}

private void loadConfig(String configFilename, String defaultEnvType) throws ConfigurationException {
    envtype = getEnvType(defaultEnvType);
    if (!envTypes.contains(envtype) && !envtype.equals("")) {
      throw new ConfigurationException("Unknown envtype of " + envtype);
    }

    String configFile = getConfigFileLocation(envtype, configFilename);

    config = new CompositeConfiguration();
    config.addConfiguration(new EnvironmentConfiguration());
    config.addConfiguration(new SystemConfiguration());
    config.addConfiguration(new PropertiesConfiguration(configFile));
    log.debug("Configuration loaded");

    if ( log.isTraceEnabled() ) {
    	Iterator<String> keys = config.getKeys();
    	log.trace("Configuration:");
    	while ( keys.hasNext() ) {
    		String k = keys.next();
    		log.trace(k + " " + config.getString(k));
    	}
    }
  }

  private String getConfigFileLocation(String envtype, String configFilename) {
    String configFile =
    "configuration" + File.separator + envtype + File.separator
    + configFilename;

    /*
     * if (!configFile.exists()) { throw new
     * IllegalStateException("Configuration file not found: " +
     * configFile.getAbsolutePath()); }
     */
    return configFile;
  }

  private String getEnvType(String defaultEnvType) {
    if ((envtype = System.getProperty("envtype")) != null) {
      log.info("Environment set to " + envtype + " via system property envtype");
    }
    else if ((envtype = System.getenv("envtype")) != null) {
      log.info("Environment set to " + envtype + " via environment variable envtype");
    }
    else {
      log.info("No envtype found defaulting to " + defaultEnvType);
      envtype = defaultEnvType;
    }
    return envtype;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.hollywood.feeservice.rest.configuration.FastConfiguration#getConfig()
   */
  @Override
  public Configuration getConfig() {
    return config;
  }

  @Override
  public Set<String> addEnvType(String envType) {
    envTypes.add(envType);
    return Collections.unmodifiableSet(this.envTypes);
  }

  @Override
  public Set<String> setEnvTypes(Set<String> envTypes) {
    this.envTypes = new HashSet<String>(envTypes);
    return Collections.unmodifiableSet(this.envTypes);
  }

  @Override
  public String getCurrentEnv() {
    return envtype;
  }

}
