
# Anypoint Template: Database to Salesforce Account Migration

This template moves a large set of accounts from a database to Salesforce. Trigger with an HTTP call either manually or programmatically. 

Accounts are upserted so that the migration can be run multiple times without worrying about creating duplicates. This template uses the Mule batch module to make moving a large set of data easier and more transparent. 

A database table schema is included to make testing this template easier.

![9a83dda1-6ab9-4414-aa56-d61c1178457d-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/9a83dda1-6ab9-4414-aa56-d61c1178457d-image.png)

[//]: # (![]\(https://www.youtube.com/embed/RA7ZA9Z6tFw?wmode=transparent\)

[![YouTube Video](http://img.youtube.com/vi/RA7ZA9Z6tFw/0.jpg)](https://www.youtube.com/watch?v=RA7ZA9Z6tFw)

# License Agreement
This template is subject to the conditions of the [MuleSoft License Agreement](https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf). Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 

# Use Case
This template helps you migrate accounts from a database to a Salesforce instance, specify a filtering criteria, and specify a behavior when an account already exists in the destination instance. 

This template leverages the batch module. The batch job is divided into *Process* and *On Complete* stages.

- Migration process starts from fetching all the existing accounts that match the filter criteria from the Database.
Each database account is filtered depending if the database account also has an existing matching account in Salesforce.
- The last step of the *Process* stage groups the accounts and upserts them into the Salesforce instance based on the Name.
- Finally during the *On Complete* stage, the template outputs statistics data into the console and sends a notification email with the results of the batch execution.

# Considerations

To make this template run, there are certain preconditions that must be considered. All of them deal with the preparations in both source (Database) and destination (Salesforce) systems, that must be made for the application to run smoothly. Failing to do so can lead to unexpected behavior of the template.

This template illustrates the migration use case between a database and Salesforce, thus it requires a database instance to work.

## Database Considerations

This template uses date time or timestamp fields from the database to do comparisons and take further actions.
While the template handles the time zone by sending all such fields in a neutral time zone, it cannot handle time offsets.
We define time offsets as the time difference that may surface between date time and timestamp fields from different systems due to a differences in the system's internal clock.
Take this in consideration and take the actions needed to avoid the time offset.

### As a Data Source

There are no considerations with using a database as a data origin.

## Salesforce Considerations

Here's what you need to know about Salesforce to get this template to work:

- Where can I check that the field configuration for my Salesforce instance is the right one? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US">Salesforce: Checking Field Accessibility for a Particular Field</a>
- How can I modify the Field Access Settings? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US">Salesforce: Modifying Field Access Settings</a>


### As a Data Destination

There are no considerations with using Salesforce as a data destination.

# Run it!
Simple steps to get Database to Salesforce Account Migration running.

**Note:** This template illustrates how to migrate between Salesforce and a database, which
requires a Database instance to work. The template comes packaged with a SQL script to create 
the database table that uses. It is your responsibility to use the script to create the 
table in an available schema and change the configuration accordingly. The SQL script file 
can be found in src/main/resources/account.sql.

This template is customized for MySQL. To use it with different SQL implementation, some changes are necessary:

* Update the SQL script dialect to desired one.
* Replace MySQL driver library (or add another) dependency to desired one in the pom.xml file.
* Update Database Config to suitable connection instead of `db:my-sql-connection` in global elements (config.xml).
* Update connection configurations in `mule.*.properties` file.

## Run On Premises
In this section we help you run your template on your computer.


### Where to Download Anypoint Studio and the Mule Runtime
If you are a newcomer to Mule, here is where to get the tools.

- [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
- [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)


### Import a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your
Anypoint Platform credentials, search for the template, and click **Open**.


### Run on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.

### Run on Mule Standalone
Complete all properties in one of the property files, for example in mule.prod.properties and run your app with the corresponding environment variable. To follow the example, this is `mule.env=prod`. 
After this, to trigger the use case you need to browse to the local HTTP connector with the port you configured in your file. For instance, for `9090` browse to `http://localhost:9090/migrateaccounts` and this outputs a summary report and sends it to the emails configured.

## Run on CloudHub
While creating your application on CloudHub (or you can do it later as a next step), go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the **mule.env**.
Once your app is all set and started, if you choose as a domain name `db2sfdcaccountmigration` to trigger the use case, browse to `http://db2sfdcaccountmigration.cloudhub.io/migrateaccounts` and report is sent to the emails configured.

### Deploy your Anypoint Template on CloudHub
Studio provides an easy way to deploy your template directly to CloudHub, for the specific steps to do so check this


## Properties to Configure
To use this template, configure properties (credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.

### Application Configuration

**HTTP Connector Configuration**

- http.port `9090`

**Batch Aggregator Configuration**

- page.size `1000`

**Database Connector Configuration**

- db.host `localhost`
- db.port `3306`
- db.user `user-nameA`
- db.password `user-passwordA`
- db.databasename `dbnameA`

**SalesForce Connector Configuration**

- sfdc.username `joan.baez@org`
- sfdc.password `JoanBaez456`
- sfdc.securityToken `ces56arl7apQs56XTddf34X`

**SMTP Services configuration**

- smtp.host `smtp.gmail.com`
- smtp.port `587`
- smtp.user `email%40example.com`
- smtp.password `password` 

**Email Details**

- mail.from `batch.migrateaccounts.migration%40mulesoft.com`
- mail.to `your.email@gmail.com`
- mail.subject `Batch Job Finished Report`

# API Calls
Salesforce imposes limits on the number of API calls that can be made. Therefore calculating this amount is important. This template's calls to the API can be calculated using this formula:

- ***1 + X / ${page.size}*** -- Where ***X*** is the number of Accounts to be synchronized on each run. 
- Divide by ***${page.size}*** because, by default, accounts are gathered in groups of ${page.size} for each upsert API call in the commit step. Also consider that this calls are executed repeatedly every polling cycle.	

For instance if 10 records are fetched from an origin instance, then 11 API calls are made (1 + 10).


# Customize It!
This brief guide intends to give a high level idea of how this template is built and how you can change it according to your needs.
As Mule applications are based on XML files, this page describes the XML files used with this template.

More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml


## config.xml
Configuration for connectors and configuration properties are set in this file. Even change the configuration here, all parameters that can be modified are in properties file, which is the recommended place to make your changes. However if you want to do core changes to the logic, you need to modify this file.

In the Studio visual editor, the properties are on the *Global Element* tab.


## businessLogic.xml
The functional aspect of this template e is implemented on this XML, directed by one flow responsible of executing the logic.
For the pourpose of this template, the *mainFlow* executes a batch job that handles all its logic.
This flow has an exception strategy that basically consists on invoking the *defaultChoiseExceptionStrategy* defined in the *errorHandling.xml* file.



## endpoints.xml
This is the file where you find the inbound and outbound sides of your integration app.
This template has only an HTTP Listener connector as the way to trigger the use case.

**HTTP Listener Connector** - Start Report Generation

- `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
- The path configured by default is `migrateaccounts` that you are free to change for the one you prefer.
- The host name for all endpoints in your CloudHub configuration is `localhost`. CloudHub routes requests from your application domain URL to the endpoint.
- The endpoint is a *request-response* and a result of calling it is the response with the total records fetched by the criteria specified.

## errorHandling.xml
This is the right place to handle how your integration reacts depending on the different exceptions. 
This file provides error handling that is referenced by the main flow in the business logic.
