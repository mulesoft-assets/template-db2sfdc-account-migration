/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

/**
 * 
 * @author unknown
 * @author MartinZdila
 *
 */
public class AccountDateComparatorTest {
	
	private static final String KEY_LAST_MODIFIED_DATE = "LastModifiedDate";
	
	private static final String TEST_DATETIME_STRING = "2013-12-09T22:15:33.001Z";

	private static final String TEST_DATETIME_STRING2 = "2013-12-10T22:15:33.001Z";

	private static final DateTimeFormatter ISO_DATE_FORMATTER = ISODateTimeFormat.dateTimeParser();

	private static final Timestamp TEST_DATETIME_TIMESTAMP
			= new Timestamp(ISO_DATE_FORMATTER.parseDateTime(TEST_DATETIME_STRING).toDate().getTime());
	
	private static final Timestamp TEST_DATETIME_TIMESTAMP2
			= new Timestamp(ISO_DATE_FORMATTER.parseDateTime(TEST_DATETIME_STRING2).toDate().getTime());

	@Test(expected = IllegalArgumentException.class)
	public void nullAccountA() {
		Map<String, Object> databaseAccount = null;

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();
		salesforceAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		AccountDateComparator.isAfter(databaseAccount, salesforceAccount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullAccountB() {
		Map<String, Object> databaseAccount = new HashMap<String, Object>();
		databaseAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> salesforceAccount = null;

		AccountDateComparator.isAfter(databaseAccount, salesforceAccount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void malFormedAccountA() {
		Map<String, Object> databaseAccount = new HashMap<String, Object>();

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();
		salesforceAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		AccountDateComparator.isAfter(databaseAccount, salesforceAccount);
	}

	public void emptyAccountB() {
		Map<String, Object> databaseAccount = new HashMap<String, Object>();
		databaseAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();

		Assert.assertTrue("The Database account should be after the Salesforce account",
				AccountDateComparator.isAfter(databaseAccount, salesforceAccount));
	}

	@Test
	public void databaseAccountIsAfterAccountB() {
		Map<String, Object> databaseAccount = new HashMap<String, Object>();
		databaseAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP2);

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();
		salesforceAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		Assert.assertTrue("The Database account should be after the Salesforce account",
				AccountDateComparator.isAfter(databaseAccount, salesforceAccount));
	}

	@Test
	public void databaseAccountIsNotAfterAccountB() {
		Map<String, Object> databaseAccount = new HashMap<String, Object>();
		databaseAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();
		salesforceAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		Assert.assertFalse("The Database account should not be after the Salesforce account",
				AccountDateComparator.isAfter(databaseAccount, salesforceAccount));
	}

	@Test
	public void databaseAccountIsTheSameThatAccountB() {
		Map<String, Object> databaseAccount = new HashMap<String, Object>();
		databaseAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();
		salesforceAccount.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		Assert.assertFalse("The Database account should not be after the Salesforce account",
				AccountDateComparator.isAfter(databaseAccount, salesforceAccount));
	}

}
