-- MySQL dump 10.13  Distrib 5.5.35, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mule
-- ------------------------------------------------------
-- Server version	5.5.35-2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Account`
--

DROP TABLE IF EXISTS `Account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Account` (
  `Id` varchar(255) NOT NULL,
  `AccountNumber` varchar(255) DEFAULT NULL,
  `AccountSource` varchar(255) DEFAULT NULL,
  `AnnualRevenue` varchar(255) DEFAULT NULL,
  `BillingCity` varchar(255) DEFAULT NULL,
  `BillingCountry` varchar(255) DEFAULT NULL,
  `BillingPostalCode` varchar(255) DEFAULT NULL,
  `BillingState` varchar(255) DEFAULT NULL,
  `BillingStreet` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Fax` varchar(255) DEFAULT NULL,
  `Industry` varchar(255) DEFAULT NULL,
  `LastModifiedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Name` varchar(255) DEFAULT NULL,
  `NumberOfEmployees` int(11) DEFAULT NULL,
  `Ownership` varchar(255) DEFAULT NULL,
  `Phone` varchar(255) DEFAULT NULL,
  `Rating` varchar(255) DEFAULT NULL,
  `ShippingCity` varchar(255) DEFAULT NULL,
  `ShippingCountry` varchar(255) DEFAULT NULL,
  `ShippingPostalCode` varchar(255) DEFAULT NULL,
  `ShippingState` varchar(255) DEFAULT NULL,
  `ShippingStreet` varchar(255) DEFAULT NULL,
  `Sic` varchar(255) DEFAULT NULL,
  `SicDesc` varchar(255) DEFAULT NULL,
  `Site` varchar(255) DEFAULT NULL,
  `TickerSymbol` varchar(255) DEFAULT NULL,
  `Type` varchar(255) DEFAULT NULL,
  `Website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-05-02 10:38:19
