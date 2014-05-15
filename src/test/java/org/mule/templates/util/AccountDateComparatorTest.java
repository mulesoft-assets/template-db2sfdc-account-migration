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

	private static final Timestamp TEST_DATETIME_TIMESTAMP = new Timestamp(ISO_DATE_FORMATTER.parseDateTime(TEST_DATETIME_STRING).toDate().getTime());
	
	private static final Timestamp TEST_DATETIME_TIMESTAMP2 = new Timestamp(ISO_DATE_FORMATTER.parseDateTime(TEST_DATETIME_STRING2).toDate().getTime());

	@Test(expected = IllegalArgumentException.class)
	public void nullAccountA() {
		Map<String, Object> accountA = null;

		Map<String, Object> accountB = new HashMap<String, Object>();
		accountB.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		AccountDateComparator.isAfter(accountA, accountB);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullAccountB() {
		Map<String, Object> accountA = new HashMap<String, Object>();
		accountA.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> accountB = null;

		AccountDateComparator.isAfter(accountA, accountB);
	}

	@Test(expected = IllegalArgumentException.class)
	public void malFormedAccountA() {
		Map<String, Object> accountA = new HashMap<String, Object>();

		Map<String, Object> accountB = new HashMap<String, Object>();
		accountB.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		AccountDateComparator.isAfter(accountA, accountB);
	}

	public void emptyAccountB() {
		Map<String, Object> accountA = new HashMap<String, Object>();
		accountA.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> accountB = new HashMap<String, Object>();

		Assert.assertTrue("The account A should be after the account B", AccountDateComparator.isAfter(accountA, accountB));
	}

	@Test
	public void accountAIsAfterAccountB() {
		Map<String, Object> accountA = new HashMap<String, Object>();
		accountA.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP2);

		Map<String, Object> accountB = new HashMap<String, Object>();
		accountB.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		Assert.assertTrue("The account A should be after the account B", AccountDateComparator.isAfter(accountA, accountB));
	}

	@Test
	public void accountAIsNotAfterAccountB() {
		Map<String, Object> accountA = new HashMap<String, Object>();
		accountA.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> accountB = new HashMap<String, Object>();
		accountB.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		Assert.assertFalse("The account A should not be after the account B", AccountDateComparator.isAfter(accountA, accountB));
	}

	@Test
	public void accountAIsTheSameThatAccountB() {
		Map<String, Object> accountA = new HashMap<String, Object>();
		accountA.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_TIMESTAMP);

		Map<String, Object> accountB = new HashMap<String, Object>();
		accountB.put(KEY_LAST_MODIFIED_DATE, TEST_DATETIME_STRING);

		Assert.assertFalse("The account A should not be after the account B", AccountDateComparator.isAfter(accountA, accountB));
	}

}
