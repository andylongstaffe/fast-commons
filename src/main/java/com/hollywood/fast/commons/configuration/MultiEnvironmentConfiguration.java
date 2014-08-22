package com.hollywood.fast.commons.configuration;

import java.util.Set;


public interface MultiEnvironmentConfiguration {

  Set<String> addEnvType(String envType);

  Set<String> setEnvTypes(Set<String> envTypes);
}
