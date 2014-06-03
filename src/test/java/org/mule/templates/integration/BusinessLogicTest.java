package org.mule.templates.integration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.templates.db.MySQLDbCreator;
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
	
	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	private static final String PATH_TO_SQL_SCRIPT = "src/main/resources/account.sql";
	private static final String DATABASE_NAME = "DB2SFDCAccountMigration" + new Long(new Date().getTime()).toString();
	private static final MySQLDbCreator DBCREATOR = new MySQLDbCreator(DATABASE_NAME, PATH_TO_SQL_SCRIPT, PATH_TO_TEST_PROPERTIES);

	protected static final int TIMEOUT_SEC = 120;
	protected static final String TEMPLATE_NAME = "account-migration";

	protected SubflowInterceptingChainLifecycleWrapper retrieveAccountFromSalesforceFlow;
	private List<Map<String, Object>> createdAccountsInDatabase = new ArrayList<Map<String, Object>>();
	private BatchTestHelper helper;

	@BeforeClass
	public static void init() {
		System.setProperty("db.jdbcUrl", DBCREATOR.getDatabaseUrlWithName());
	}
	
	@Before
	public void setUp() throws Exception {
		helper = new BatchTestHelper(muleContext);
	
		// Flow to retrieve accounts from target system after sync in Salesforce
		retrieveAccountFromSalesforceFlow = getSubFlow("retrieveAccountFromSalesforceFlow");
		retrieveAccountFromSalesforceFlow.initialise();
		
		DBCREATOR.setUpDatabase();
		createTestDataInSandBox();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestAccountsFromSalesforce();
		DBCREATOR.tearDownDataBase();
	}

	@Test
	public void testMainFlow() throws Exception {
		runFlow("mainFlow");
	
		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_SEC * 1000, 500);
		helper.assertJobWasSuccessful();
	
		Map<String, Object> payload0 = invokeRetrieveFlow(retrieveAccountFromSalesforceFlow, createdAccountsInDatabase.get(0));
		Assert.assertNotNull("The account 0 should have been sync but is null", payload0);
		Assert.assertEquals("The account 0 should have been sync (KEY_NAME)", createdAccountsInDatabase.get(0).get(KEY_NAME), payload0.get(KEY_NAME));

		Map<String, Object>  payload1 = invokeRetrieveFlow(retrieveAccountFromSalesforceFlow, createdAccountsInDatabase.get(1));
		Assert.assertNotNull("The account 1 should have been sync but is null", payload1);
		Assert.assertEquals("The account 1 should have been sync (Website)", createdAccountsInDatabase.get(1).get(KEY_NAME), payload1.get(KEY_NAME));
		
		Map<String, Object>  payload2 = invokeRetrieveFlow(retrieveAccountFromSalesforceFlow, createdAccountsInDatabase.get(2));
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
		
		Map<String, Object> salesforceAccount3 = new HashMap<String, Object>();
		salesforceAccount3.put(KEY_NAME, "Name_3_Salesforce" + uniqueSuffix);
		salesforceAccount3.put(KEY_WEBSITE, "http://example.com");
		salesforceAccount3.put(KEY_PHONE, "112");
		List<Map<String, Object>> createdAccountInSalesforce = new ArrayList<Map<String, Object>>();
		createdAccountInSalesforce.add(salesforceAccount3);
	
		SubflowInterceptingChainLifecycleWrapper createAccountInSalesforceFlow = getSubFlow("createAccountFlowSalesforce");
		createAccountInSalesforceFlow.initialise();
		createAccountInSalesforceFlow.process(getTestEvent(createdAccountInSalesforce, MessageExchangePattern.REQUEST_RESPONSE));
	
		Thread.sleep(1001); // this is here to prevent equal LastModifiedDate
		
		// Create accounts in source system to be or not to be synced
	
		// This account should be synced
		Map<String, Object> databaseAccount0 = new HashMap<String, Object>();
		databaseAccount0.put(KEY_NAME, "Name_0_Database" + uniqueSuffix);
		databaseAccount0.put(KEY_ID, UUID.getUUID().toString());
		databaseAccount0.put(KEY_WEBSITE, "http://acme.org");
		databaseAccount0.put(KEY_PHONE, "123");
		databaseAccount0.put(KEY_NUMBER_OF_EMPLOYEES, 6000);
		databaseAccount0.put(KEY_INDUSTRY, "Education");
		createdAccountsInDatabase.add(databaseAccount0);
				
		// This account should be synced (update)
		Map<String, Object> databaseAccount1 = new HashMap<String, Object>();
		databaseAccount1.put(KEY_NAME,  salesforceAccount3.get(KEY_NAME));
		databaseAccount1.put(KEY_ID, UUID.getUUID().toString());
		databaseAccount1.put(KEY_WEBSITE, "http://example.edu");
		databaseAccount1.put(KEY_PHONE, "911");
		databaseAccount1.put(KEY_NUMBER_OF_EMPLOYEES, 7100);
		databaseAccount1.put(KEY_INDUSTRY, "Government");
		createdAccountsInDatabase.add(databaseAccount1);

		// This account should not be synced because of industry
		Map<String, Object> databaseAccount2 = new HashMap<String, Object>();
		databaseAccount2.put(KEY_NAME, "Name_2_Database" + uniqueSuffix);
		databaseAccount2.put(KEY_ID, UUID.getUUID().toString());
		databaseAccount2.put(KEY_WEBSITE, "http://energy.edu");
		databaseAccount2.put(KEY_PHONE, "333");
		databaseAccount2.put(KEY_NUMBER_OF_EMPLOYEES, 13204);
		databaseAccount2.put(KEY_INDUSTRY, "Energetic");
		createdAccountsInDatabase.add(databaseAccount2);

		SubflowInterceptingChainLifecycleWrapper createAccountInAFlow = getSubFlow("createAccountFlowDatabase");
		createAccountInAFlow.initialise();
	
		createAccountInAFlow.process(getTestEvent(createdAccountsInDatabase, MessageExchangePattern.REQUEST_RESPONSE));
	
		System.out.println("Results after adding: " + createdAccountsInDatabase.toString());
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
	
	private void deleteTestAccountsFromDatabase() throws InitialisationException, MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper deleteAccountFromAFlow = getSubFlow("deleteAccountFromDatabaseFlow");
		deleteAccountFromAFlow.initialise();
		deleteTestEntityFromSandBox(deleteAccountFromAFlow, createdAccountsInDatabase);
	}

	private void deleteTestAccountsFromSalesforce() throws InitialisationException, MuleException, Exception {
		List<Map<String, Object>> createdAccountsInSalesforce = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> c : createdAccountsInDatabase) {
			Map<String, Object> account = invokeRetrieveFlow(retrieveAccountFromSalesforceFlow, c);
			if (account != null) {
				createdAccountsInSalesforce.add(account);
			}
		}
		SubflowInterceptingChainLifecycleWrapper deleteAccountFromBFlow = getSubFlow("deleteAccountFromSalesforceFlow");
		deleteAccountFromBFlow.initialise();
		deleteTestEntityFromSandBox(deleteAccountFromBFlow, createdAccountsInSalesforce);
	}
	
	private void deleteTestEntityFromSandBox(SubflowInterceptingChainLifecycleWrapper deleteFlow, List<Map<String, Object>> entitities) throws MuleException, Exception {
		List<String> idList = new ArrayList<String>();
		for (Map<String, Object> c : entitities) {
			idList.add(c.get(KEY_ID).toString());
		}
		deleteFlow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
	}

}
