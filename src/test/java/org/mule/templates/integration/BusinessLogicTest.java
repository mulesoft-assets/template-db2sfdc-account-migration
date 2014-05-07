package org.mule.templates.integration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;
import org.mule.util.UUID;

import com.mulesoft.module.batch.BatchTestHelper;

/**
 * The objective of this class is to validate the correct behavior of the Mule Template that make calls to external systems.
 * 
 * The test will invoke the batch process and afterwards check that the accounts had been correctly created and that the ones that should be filtered are not in
 * the destination sand box.
 * 
 * The test validates that no account will get sync as result of the integration.
 * 
 * @author damiansima
 * @author MartinZdila
 */
public class BusinessLogicTest extends FunctionalTestCase {

	private static final String KEY_ID = "Id";
	private static final String KEY_NAME = "Name";
	private static final String KEY_WEBSITE = "Website";
	private static final String KEY_PHONE = "Phone";
	private static final String KEY_NUMBER_OF_EMPLOYEES = "NumberOfEmployees";
	private static final String KEY_INDUSTRY = "Industry";
	
	private static final String MAPPINGS_FOLDER_PATH = "./mappings";
	private static final String TEST_FLOWS_FOLDER_PATH = "./src/test/resources/flows/";
	private static final String MULE_DEPLOY_PROPERTIES_PATH = "./src/main/app/mule-deploy.properties";

	protected static final int TIMEOUT_SEC = 120;
	protected static final String TEMPLATE_NAME = "account-migration";

	protected SubflowInterceptingChainLifecycleWrapper retrieveAccountFromBFlow;
	private List<Map<String, Object>> createdAccountsInA = new ArrayList<Map<String, Object>>();
	private BatchTestHelper helper;

	@Before
	public void setUp() throws Exception {
		helper = new BatchTestHelper(muleContext);
	
		// Flow to retrieve accounts from target system after sync in g
		retrieveAccountFromBFlow = getSubFlow("retrieveAccountFromBFlow");
		retrieveAccountFromBFlow.initialise();
	
		createTestDataInSandBox();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestAccountsFromSandBoxA(createdAccountsInA);
		deleteTestAccountsFromSandBoxB(createdAccountsInA);
	}

	@Test
	public void testMainFlow() throws Exception {
		runFlow("mainFlow");
	
		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_SEC * 1000, 500);
		helper.assertJobWasSuccessful();
	
		Map<String, Object> payload0 = invokeRetrieveFlow(retrieveAccountFromBFlow, createdAccountsInA.get(0));
		Assert.assertNotNull("The account 0 should have been sync but is null", payload0);
		Assert.assertEquals("The account 0 should have been sync (Website)", createdAccountsInA.get(0).get(KEY_WEBSITE), payload0.get(KEY_WEBSITE));
		Assert.assertEquals("The account 0 should have been sync (Phone)", createdAccountsInA.get(0).get(KEY_PHONE), payload0.get(KEY_PHONE));

		Map<String, Object>  payload1 = invokeRetrieveFlow(retrieveAccountFromBFlow, createdAccountsInA.get(1));
		Assert.assertNotNull("The account 1 should have been sync but is null", payload1);
		Assert.assertEquals("The account 1 should have been sync (Website)", createdAccountsInA.get(1).get(KEY_WEBSITE), payload1.get(KEY_WEBSITE));
		Assert.assertEquals("The account 1 should have been sync (Phone)", createdAccountsInA.get(1).get(KEY_PHONE), payload1.get(KEY_PHONE));
		
		Map<String, Object>  payload2 = invokeRetrieveFlow(retrieveAccountFromBFlow, createdAccountsInA.get(2));
		Assert.assertNull("The account 2 should have not been sync", payload2);
	}

	@Override
	protected String getConfigResources() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(MULE_DEPLOY_PROPERTIES_PATH));
		} catch (IOException e) {
			throw new IllegalStateException(
					"Could not find mule-deploy.properties file on classpath. " +
					"Please add any of those files or override the getConfigResources() method to provide the resources by your own.");
		}

		return props.getProperty("config.resources") + getTestFlows();
	}


	private void createTestDataInSandBox() throws MuleException, Exception {
		// Create object in target system to be updated
		
		String uniqueSuffix = "_" + TEMPLATE_NAME + "_" + UUID.getUUID();
		
		Map<String, Object> account_3_B = new HashMap<String, Object>();
		account_3_B.put(KEY_NAME, "Name_3_B" + uniqueSuffix);
		account_3_B.put(KEY_WEBSITE, "http://example.com");
		account_3_B.put(KEY_PHONE, "112");
		List<Map<String, Object>> createdAccountInB = new ArrayList<Map<String, Object>>();
		createdAccountInB.add(account_3_B);
	
		SubflowInterceptingChainLifecycleWrapper createAccountInBFlow = getSubFlow("createAccountFlowB");
		createAccountInBFlow.initialise();
		createAccountInBFlow.process(getTestEvent(createdAccountInB, MessageExchangePattern.REQUEST_RESPONSE));
	
		Thread.sleep(1001); // this is here to prevent equal LastModifiedDate
		
		// Create accounts in source system to be or not to be synced
	
		// This account should be synced
		Map<String, Object> account_0_A = new HashMap<String, Object>();
		account_0_A.put(KEY_NAME, "Name_0_A" + uniqueSuffix);
		account_0_A.put(KEY_ID, UUID.getUUID().toString());
		account_0_A.put(KEY_WEBSITE, "http://acme.org");
		account_0_A.put(KEY_PHONE, "123");
		account_0_A.put(KEY_NUMBER_OF_EMPLOYEES, 6000);
		account_0_A.put(KEY_INDUSTRY, "Education");
		createdAccountsInA.add(account_0_A);
				
		// This account should be synced (update)
		Map<String, Object> account_1_A = new HashMap<String, Object>();
		account_1_A.put(KEY_NAME,  account_3_B.get(KEY_NAME));
		account_1_A.put(KEY_ID, UUID.getUUID().toString());
		account_1_A.put(KEY_WEBSITE, "http://example.edu");
		account_1_A.put(KEY_PHONE, "911");
		account_1_A.put(KEY_NUMBER_OF_EMPLOYEES, 7100);
		account_1_A.put(KEY_INDUSTRY, "Government");
		createdAccountsInA.add(account_1_A);

		// This account should not be synced because of industry
		Map<String, Object> account_2_A = new HashMap<String, Object>();
		account_2_A.put(KEY_NAME, "Name_2_A" + uniqueSuffix);
		account_2_A.put(KEY_ID, UUID.getUUID().toString());
		account_2_A.put(KEY_WEBSITE, "http://energy.edu");
		account_2_A.put(KEY_PHONE, "333");
		account_2_A.put(KEY_NUMBER_OF_EMPLOYEES, 13204);
		account_2_A.put(KEY_INDUSTRY, "Energetic");
		createdAccountsInA.add(account_2_A);

		SubflowInterceptingChainLifecycleWrapper createAccountInAFlow = getSubFlow("createAccountFlowA");
		createAccountInAFlow.initialise();
	
		createAccountInAFlow.process(getTestEvent(createdAccountsInA, MessageExchangePattern.REQUEST_RESPONSE));
	
		System.out.println("Results after adding: " + createdAccountsInA.toString());
	}

	private String getTestFlows() {
		File[] listOfFiles = new File(TEST_FLOWS_FOLDER_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".xml");
			}
		});
		
		if (listOfFiles == null) {
			return "";
		}
		
		StringBuilder resources = new StringBuilder();
		for (File f : listOfFiles) {
			resources.append(",").append(TEST_FLOWS_FOLDER_PATH).append(f.getName());
		}
		return resources.toString();
	}

	@Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());
		properties.put(
				MuleProperties.APP_HOME_DIRECTORY_PROPERTY,
				new File(MAPPINGS_FOLDER_PATH).getAbsolutePath());
		return properties;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> invokeRetrieveFlow(SubflowInterceptingChainLifecycleWrapper flow, Map<String, Object> payload) throws Exception {
		MuleEvent event = flow.process(getTestEvent(payload, MessageExchangePattern.REQUEST_RESPONSE));
		Object resultPayload = event.getMessage().getPayload();
		return resultPayload instanceof NullPayload ? null : (Map<String, Object>) resultPayload;
	}
	
	private void deleteTestAccountsFromSandBoxA(List<Map<String, Object>> createdAccountsInA) throws InitialisationException, MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper deleteAccountFromAFlow = getSubFlow("deleteAccountFromAFlow");
		deleteAccountFromAFlow.initialise();
		deleteTestEntityFromSandBox(deleteAccountFromAFlow, createdAccountsInA);
	}

	private void deleteTestAccountsFromSandBoxB(List<Map<String, Object>> createdAccountsInA) throws InitialisationException, MuleException, Exception {
		List<Map<String, Object>> createdAccountsInB = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> c : createdAccountsInA) {
			Map<String, Object> account = invokeRetrieveFlow(retrieveAccountFromBFlow, c);
			if (account != null) {
				createdAccountsInB.add(account);
			}
		}
		SubflowInterceptingChainLifecycleWrapper deleteAccountFromBFlow = getSubFlow("deleteAccountFromBFlow");
		deleteAccountFromBFlow.initialise();
		deleteTestEntityFromSandBox(deleteAccountFromBFlow, createdAccountsInB);
	}
	
	private void deleteTestEntityFromSandBox(SubflowInterceptingChainLifecycleWrapper deleteFlow, List<Map<String, Object>> entitities) throws MuleException, Exception {
		List<String> idList = new ArrayList<String>();
		for (Map<String, Object> c : entitities) {
			idList.add(c.get(KEY_ID).toString());
		}
		deleteFlow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
	}

}
