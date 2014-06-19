/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.util;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * The function of this class is to establish a relation happens before between two maps - first representing Database and second Salesforce account.
 * 
 * It's assumed that these maps are well formed maps thus they both contain an entry with the expected key. Never the less validations are being done.
 * 
 * @author martin.zdila
 */
public class AccountDateComparator {
	private static final String LAST_MODIFIED_DATE = "LastModifiedDate";

	/**
	 * Validate which account has the latest last referenced date.
	 * 
	 * @param databaseAccount
	 *            Database account map
	 * @param salesforceAccount
	 *            Salesforce account map
	 * @return true if the last activity date from databaseAccount is after the one from salesforceAccount
	 */
	public static boolean isAfter(Map<String, Object> databaseAccount, Map<String, Object> salesforceAccount) {
		Validate.notNull(databaseAccount, "Database account must not be null");
		Validate.notNull(salesforceAccount, "Salesforce account must not be null");

		Validate.isTrue(databaseAccount.containsKey(LAST_MODIFIED_DATE), "The Database account map should contain the key " + LAST_MODIFIED_DATE);

		if (salesforceAccount.get(LAST_MODIFIED_DATE) == null) {
			return true;
		}
		
		Object maybeDatabaseDate = databaseAccount.get(LAST_MODIFIED_DATE);
		Validate.isTrue(maybeDatabaseDate instanceof Date, "LastModifiedDate of Database account must be java.util.Date or subclass");
		DateTime databaseDate = new DateTime(maybeDatabaseDate);
		
		Object maybeSalesforceDate = salesforceAccount.get(LAST_MODIFIED_DATE);
		Validate.isTrue(maybeSalesforceDate instanceof String, "LastModifiedDate of Salesforce account must be String");
		DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
		DateTime salesforceDate = formatter.parseDateTime((String) maybeSalesforceDate);
		return databaseDate.isAfter(salesforceDate);
	}
}
