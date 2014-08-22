package com.hollywood.fast.commons.configuration.impl;



import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hollywood.fast.commons.configuration.FastConfiguration;

public class DefaultFastConfigurationTest {

  private String currEnvType;

  @Before
  public void before() {
    currEnvType = System.getProperty("envtype");
  }

  @After
  public void after() {
    if (currEnvType == null) {
      System.clearProperty("envtype");
    }
    else {
      System.setProperty("envtype", currEnvType);
    }
  }
  
  private class CucumberConfig extends DefaultFastConfiguration {

	public CucumberConfig(String configFilename) throws ConfigurationException {
		super(configFilename);
		// TODO Auto-generated constructor stub
	}
	  
  }

  @Test
  public void testprop() throws Exception {	  
    FastConfiguration testConfig = new CucumberConfig("testing.properties");
    System.out.println("autotest.browsertype from prop:" + testConfig.getConfig().getString("cucumber.browsertype"));
    System.out.println("current env:" + testConfig.getCurrentEnv());
    System.out.println();
  }

  @Test(expected = ConfigurationException.class)
  public void testmissingpropfile() throws Exception {
    FastConfiguration testConfig = new DefaultFastConfiguration("testing1.properties");
    testConfig.getConfig().getString("cucumber.browsertype");
  }

  // @Test
  public void getCurrEnvType() throws Exception {
    FastConfiguration testConfig = new DefaultFastConfiguration("testing.properties");
    Assert.assertEquals("dev", testConfig.getCurrentEnv());
  }

  @Test
  public void integrationEnvTest() throws Exception {
    System.setProperty("envtype", "integration");
    FastConfiguration testConfig = new DefaultFastConfiguration("testing.properties");
    Assert.assertEquals("integration", testConfig.getCurrentEnv());
  }

  @Test
  public void unknownEnvTest() {
    System.setProperty("envtype", "unknownEnv");
    try {
      FastConfiguration testConfig = new DefaultFastConfiguration("testing.properties");
      Assert.fail("Exception not thrown");
    } catch (ConfigurationException e) {
      // expected
      Assert.assertTrue(e.getMessage().startsWith("Unknown envtype of"));
    }
  }

}
