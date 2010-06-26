-- MySQL dump 10.13  Distrib 5.1.32, for apple-darwin9.5.0 (i386)
--
-- Host: localhost    Database: rembrandtpool
-- ------------------------------------------------------
-- Server version	5.1.32

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
-- Table structure for table `collection`
--

DROP TABLE IF EXISTS `collection`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `collection` (
  `col_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `col_name` varchar(255) NOT NULL,
  `col_comment` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`col_id`),
  KEY `idx_col_name` (`col_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `collection`
--

LOCK TABLES `collection` WRITE;
/*!40000 ALTER TABLE `collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collection_has_doc`
--

DROP TABLE IF EXISTS `collection_has_doc`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `collection_has_doc` (
  `chd_collection` int(10) unsigned NOT NULL,
  `chd_document` int(10) unsigned NOT NULL,
  PRIMARY KEY (`chd_collection`,`chd_document`),
  KEY `chd_document` (`chd_document`),
  CONSTRAINT `collection_has_doc_ibfk_2` FOREIGN KEY (`chd_document`) REFERENCES `doc` (`doc_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `collection_has_doc_ibfk_1` FOREIGN KEY (`chd_collection`) REFERENCES `collection` (`col_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `collection_has_doc`
--

LOCK TABLES `collection_has_doc` WRITE;
/*!40000 ALTER TABLE `collection_has_doc` DISABLE KEYS */;
/*!40000 ALTER TABLE `collection_has_doc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc`
--

DROP TABLE IF EXISTS `doc`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `doc` (
  `doc_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `doc_original_id` int(10) unsigned NOT NULL,
  `doc_tag` int(10) unsigned DEFAULT NULL,
  `doc_lang` varchar(2) DEFAULT NULL,
  `doc_entity` int(10) unsigned DEFAULT NULL,
  `doc_date` datetime DEFAULT NULL,
  `doc_status` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`doc_id`),
  UNIQUE KEY `idx_doc_original_id` (`doc_original_id`),
  KEY `doc_tag` (`doc_tag`),
  KEY `doc_entity` (`doc_entity`),
  CONSTRAINT `doc_ibfk_2` FOREIGN KEY (`doc_entity`) REFERENCES `entity` (`ent_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `doc_ibfk_1` FOREIGN KEY (`doc_tag`) REFERENCES `rembrandt_tag` (`tag_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `doc`
--

LOCK TABLES `doc` WRITE;
/*!40000 ALTER TABLE `doc` DISABLE KEYS */;
INSERT INTO `doc` VALUES (15,220,1,'pt',NULL,'2009-07-02 19:03:29','OS'),(16,224,1,'pt',NULL,'2009-07-02 19:09:15','OS'),(17,226,1,'pt',NULL,'2009-07-02 19:11:52','OS'),(18,228,1,'pt',NULL,'2009-07-02 19:13:02','OS'),(19,229,1,'pt',NULL,'2009-07-02 19:14:08','OS'),(20,230,1,'pt',NULL,'2009-07-03 09:25:06','OS'),(21,232,1,'pt',NULL,'2009-07-03 09:32:00','OS'),(22,235,1,'pt',NULL,'2009-07-03 09:32:32','OS'),(23,236,1,'pt',NULL,'2009-07-03 09:32:52','OS'),(24,237,1,'pt',NULL,'2009-07-03 09:33:39','OS'),(25,808,1,'pt',NULL,'2009-07-03 10:24:04','OS'),(26,238,1,'pt',1,'2009-07-06 22:23:36','OS'),(27,239,1,'pt',2,'2009-07-06 22:24:00','OS'),(28,240,1,'pt',NULL,'2009-07-06 22:30:02','OS'),(29,241,1,'pt',NULL,'2009-07-06 22:30:05','OS'),(30,242,1,'pt',NULL,'2009-07-06 22:30:21','OS'),(31,243,1,'pt',NULL,'2009-07-06 22:30:27','OS'),(32,244,1,'pt',NULL,'2009-07-06 22:31:11','OS'),(33,246,1,'pt',NULL,'2009-07-06 22:31:29','OS'),(34,247,1,'pt',NULL,'2009-07-06 22:31:31','OS'),(35,248,1,'pt',3,'2009-07-06 22:31:59','OS'),(36,249,1,'pt',4,'2009-07-06 22:37:40','OS');
/*!40000 ALTER TABLE `doc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_has_geoscope`
--

DROP TABLE IF EXISTS `doc_has_geoscope`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `doc_has_geoscope` (
  `dhg_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dhg_doc_id` int(10) unsigned NOT NULL,
  `dhg_geo_id` int(10) unsigned NOT NULL,
  `dhg_score` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`dhg_id`),
  KEY `dhg_doc_id` (`dhg_doc_id`),
  KEY `dhg_geo_id` (`dhg_geo_id`),
  CONSTRAINT `doc_has_geoscope_ibfk_2` FOREIGN KEY (`dhg_geo_id`) REFERENCES `geoscope` (`geo_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `doc_has_geoscope_ibfk_1` FOREIGN KEY (`dhg_doc_id`) REFERENCES `doc` (`doc_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `doc_has_geoscope`
--

LOCK TABLES `doc_has_geoscope` WRITE;
/*!40000 ALTER TABLE `doc_has_geoscope` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_has_geoscope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_has_nre`
--

DROP TABLE IF EXISTS `doc_has_nre`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `doc_has_nre` (
  `dhn_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `dhn_doc` int(10) unsigned NOT NULL,
  `dhn_nre` int(10) unsigned NOT NULL,
  `dhn_sentence` int(11) NOT NULL,
  `dhn_term` int(11) NOT NULL,
  PRIMARY KEY (`dhn_id`),
  KEY `dhn_doc` (`dhn_doc`),
  KEY `dhn_nre` (`dhn_nre`),
  CONSTRAINT `doc_has_nre_ibfk_2` FOREIGN KEY (`dhn_nre`) REFERENCES `ne_rel_entity` (`nre_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `doc_has_nre_ibfk_1` FOREIGN KEY (`dhn_doc`) REFERENCES `doc` (`doc_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `doc_has_nre`
--

LOCK TABLES `doc_has_nre` WRITE;
/*!40000 ALTER TABLE `doc_has_nre` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_has_nre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_has_type`
--

DROP TABLE IF EXISTS `doc_has_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `doc_has_type` (
  `dht_document` int(10) unsigned NOT NULL,
  `dht_type` int(10) unsigned NOT NULL,
  PRIMARY KEY (`dht_document`,`dht_type`),
  KEY `dht_type` (`dht_type`),
  CONSTRAINT `doc_has_type_ibfk_2` FOREIGN KEY (`dht_type`) REFERENCES `type` (`typ_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `doc_has_type_ibfk_1` FOREIGN KEY (`dht_document`) REFERENCES `doc` (`doc_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `doc_has_type`
--

LOCK TABLES `doc_has_type` WRITE;
/*!40000 ALTER TABLE `doc_has_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_has_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `entity` (
  `ent_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ent_wikipedia_page` text,
  `ent_dbpedia_resource` text,
  `ent_dbpedia_class` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ent_id`),
  KEY `idx_ent_dbpedia_class` (`ent_dbpedia_class`)
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
INSERT INTO `entity` VALUES (1,NULL,'Hadrian',NULL),(2,NULL,'Albert',NULL),(3,NULL,'Darwin%27s_Dangerous_Idea','Book'),(4,NULL,'Agriculture',NULL),(5,NULL,'Illinois','Area'),(6,NULL,'Idaho','Area'),(7,NULL,'United_States','Country'),(8,NULL,'Madrid','Area'),(9,NULL,'Spanish_Civil_War','MilitaryConflict'),(10,NULL,'World_War_II',NULL),(11,NULL,'Cuba','Country'),(12,NULL,'Paris','WorldHeritageSite'),(13,NULL,'Gertrude_Stein','Writer'),(14,NULL,'Hemingway',NULL),(15,NULL,'Nobel_Prize_in_Literature',NULL),(16,NULL,'Spain','Country'),(17,NULL,'Pamplona','Area'),(18,NULL,'Europe',NULL),(19,NULL,'Italy','Country'),(20,NULL,'Ezra_Pound','Writer'),(21,NULL,'Key_West%2C_Florida','Area'),(22,NULL,'Florida','Area'),(23,NULL,'Havana','Area'),(24,NULL,'Martha_Gellhorn','Writer'),(25,NULL,'Martha','Saint'),(26,NULL,'Ernest_Hemingway','Writer'),(27,NULL,'The_Sunday_Times','Newspaper'),(28,NULL,'Washington','Area'),(29,NULL,'Africa',NULL),(30,NULL,'China',NULL),(31,NULL,'England','Country'),(32,NULL,'Stream',NULL),(33,NULL,'Party',NULL),(34,NULL,'Anarcho-capitalism',NULL),(35,NULL,'John_Locke','Philosopher'),(36,NULL,'Cicero','Writer'),(37,NULL,'Aristotle','Philosopher'),(38,NULL,'Ayn_Rand','Writer'),(39,NULL,'France','Country'),(40,NULL,'Henry_David_Thoreau','Philosopher'),(41,NULL,'Murray_Rothbard','Philosopher'),(42,NULL,'Utilitarianism',NULL),(43,NULL,'Statute',NULL),(44,NULL,'State',NULL),(45,NULL,'Max_Stirner','Philosopher'),(46,NULL,'Anarchism',NULL),(47,NULL,'Anarchy_%28disambiguation%29',NULL),(48,NULL,'William_Godwin','Writer'),(49,NULL,'Revolution',NULL),(50,NULL,'Communism',NULL),(51,NULL,'Karl_Marx','Philosopher'),(52,NULL,'Action',NULL),(53,NULL,'Peter_Kropotkin','Person'),(54,NULL,'Globalization',NULL),(55,NULL,'Pierre-Joseph_Proudhon','Philosopher'),(56,NULL,'Brazil','Country'),(57,NULL,'Errico_Malatesta','Person'),(58,NULL,'Marie_Fran%C3%A7ois_Sadi_Carnot','OfficeHolder'),(59,NULL,'Religion',NULL),(60,NULL,'Nestor_Makhno','Person'),(61,NULL,'Ukraine','Country'),(62,NULL,'United_Kingdom','Country'),(63,NULL,'Seattle%2C_Washington',NULL),(64,NULL,'S%C3%A3o_Francisco_do_Sul',NULL),(65,NULL,'Fourier',NULL),(66,NULL,'Palmital',NULL),(67,NULL,'Santa_Catarina_%28state%29','Area'),(68,NULL,'Arecaceae','Species'),(69,NULL,'Paran%C3%A1_%28state%29',NULL),(70,NULL,'S%C3%A3o_Paulo_%28state%29',NULL),(71,NULL,'Portugal','Country'),(72,NULL,'Oiapoque',NULL),(73,NULL,'Russia','Country'),(74,NULL,'Get%C3%BAlio_Vargas','OfficeHolder'),(75,NULL,'Rio_Grande_do_Sul',NULL),(76,NULL,'Bahia',NULL),(77,NULL,'Confedera%C3%A7%C3%A3o_Geral_dos_Trabalhadores','TradeUnion'),(78,NULL,'Buenaventura_Durruti','Person'),(79,NULL,'Noam_Chomsky','Philosopher'),(80,NULL,'Moura','Municipality'),(81,NULL,'Astronomy',NULL),(82,NULL,'Earth','Planet'),(83,NULL,'Sun',NULL),(84,NULL,'Moon','Planet'),(85,NULL,'Astrophysics',NULL),(86,NULL,'Physics',NULL),(87,NULL,'Science',NULL),(88,NULL,'Mede_%28Italy%29','City'),(89,NULL,'Solar_System',NULL),(90,NULL,'Bible',NULL),(91,NULL,'Aryabhata',NULL),(92,NULL,'Middle_Ages',NULL),(93,NULL,'Tehran','Area'),(94,NULL,'Omar_Khayy%C3%A1m','Philosopher'),(95,NULL,'Umar','Monarch'),(96,NULL,'Renaissance',NULL),(97,NULL,'Vatican_City','WorldHeritageSite'),(98,NULL,'Galileo_Galilei','Scientist'),(99,NULL,'Johannes_Kepler','Scientist'),(100,NULL,'Kepler_%28disambiguation%29',NULL),(101,NULL,'Isaac_Newton','Scientist'),(102,NULL,'History',NULL),(103,NULL,'Lisbon','Municipality'),(104,NULL,'Oscar_Wilde','Writer'),(105,NULL,'Tiberius',NULL),(106,NULL,'Death',NULL),(107,NULL,'Jules_Verne','Writer'),(108,NULL,'Academy',NULL),(109,NULL,'Republic',NULL),(110,NULL,'Mantua','WorldHeritageSite'),(111,NULL,'Almeida%2C_Portugal','Municipality'),(112,NULL,'Bocage',NULL),(113,NULL,'Angola','Country'),(114,NULL,'Latin','Language'),(115,NULL,'Christ',NULL),(116,NULL,'Western_Europe',NULL),(117,NULL,'Caesar',NULL),(118,NULL,'United_Nations',NULL),(119,NULL,'Universal_Postal_Union',NULL),(120,NULL,'Jesus','Person'),(121,NULL,'People%27s_Republic_of_China','Country'),(122,NULL,'Roman_Empire','Country'),(123,NULL,'Empire',NULL),(124,NULL,'Town',NULL),(125,NULL,'Rome','WorldHeritageSite'),(126,NULL,'Livy',NULL),(127,NULL,'Anos',NULL),(128,NULL,'Augustus',NULL),(129,NULL,'Roman_Republic','Country'),(130,NULL,'Gaul',NULL),(131,NULL,'Egypt','Country'),(132,NULL,'Holy_See',NULL),(133,NULL,'Justinian_I',NULL),(134,NULL,'Byzantine_Empire','Country'),(135,NULL,'AM',NULL),(136,NULL,'Eusebius_of_Caesarea',NULL),(137,NULL,'Constantine_I',NULL),(138,NULL,'Eus%C3%A9bio','FootballPlayer'),(139,NULL,'Alexandria','Area'),(140,NULL,'Herod',NULL),(141,NULL,'Josephus',NULL),(142,NULL,'Bede','Saint'),(143,NULL,'Charlemagne',NULL),(144,NULL,'Diocletian',NULL),(145,NULL,'Ethiopian_Orthodox_Tewahedo_Church',NULL),(146,NULL,'Lactantius',NULL),(147,NULL,'Christian',NULL),(148,NULL,'Turnhout','Municipality'),(149,NULL,'Dionysius_Exiguus','Saint'),(150,NULL,'Braga','Municipality'),(151,NULL,'Achilles',NULL),(152,NULL,'Education',NULL),(153,NULL,'Patroclus',NULL),(154,NULL,'Troy','WorldHeritageSite'),(155,NULL,'Calchas',NULL),(156,NULL,'Neoptolemus',NULL),(157,NULL,'Pyrrhus_of_Epirus',NULL),(158,NULL,'Apollo',NULL),(159,NULL,'Andromache',NULL),(160,NULL,'Hector',NULL),(161,NULL,'Priam',NULL),(162,NULL,'Aurora',NULL),(163,NULL,'Paris_%28mythology%29',NULL),(164,NULL,'Helena',NULL),(165,NULL,'Branca',NULL),(166,NULL,'Aquileia','WorldHeritageSite'),(167,NULL,'Iliad',NULL),(168,NULL,'Racine',NULL),(169,NULL,'World_Meteorological_Organization',NULL),(170,NULL,'Kyoto_Protocol',NULL),(171,NULL,'Arctic',NULL),(172,NULL,'North_America',NULL),(173,NULL,'Eurasia',NULL),(174,NULL,'Mexico','Country'),(175,NULL,'Caribbean',NULL),(176,NULL,'Australia','Country'),(177,NULL,'Malaysia','Country'),(178,NULL,'Indonesia','Country'),(179,NULL,'Darwin',NULL),(180,NULL,'Joseph_Fourier','Scientist'),(181,NULL,'Svante_Arrhenius','Scientist'),(182,NULL,'Siberia',NULL),(183,NULL,'Duke',NULL),(184,NULL,'Viking',NULL),(185,NULL,'Great_Britain','Island'),(186,NULL,'Finland','Country'),(187,NULL,'Iceland','Country'),(188,NULL,'New_York_City','Area'),(189,NULL,'Staten_Island','Area'),(190,NULL,'Indian_Ocean',NULL),(191,NULL,'Model',NULL),(192,NULL,'Hansen%2C_Idaho','Area'),(193,NULL,'Scandinavia',NULL),(194,NULL,'PSD',NULL),(195,NULL,'JPEG_2000',NULL),(196,NULL,'PCD_%28album%29','Album'),(197,NULL,'Portable_Network_Graphics',NULL),(198,NULL,'Scalable_Vector_Graphics',NULL),(199,NULL,'Bitmap',NULL),(200,NULL,'Award',NULL),(201,NULL,'Winnie-the-Pooh','Book'),(202,NULL,'Douglas_Adams','Writer'),(203,NULL,'John_Adams','OfficeHolder'),(204,NULL,'Bryan_Adams','MusicalArtist'),(205,NULL,'North_Dakota','Area'),(206,NULL,'Minnesota','Area'),(207,NULL,'Nebraska','Area'),(208,NULL,'Oregon','Area'),(209,NULL,'Tennessee','Area'),(210,NULL,'Wisconsin','Area'),(211,NULL,'Pennsylvania','Area'),(212,NULL,'Trajan',NULL),(213,NULL,'Syria','Country'),(214,NULL,'Senate',NULL),(215,NULL,'Principality',NULL),(216,NULL,'Dacia',NULL),(217,NULL,'Romania','Country'),(218,NULL,'Carpathian_Mountains',NULL),(219,NULL,'Cassius_Dio',NULL),(220,NULL,'Germania',NULL),(221,NULL,'Hadrian%27s_Wall',NULL),(222,NULL,'Scotland','Country'),(223,NULL,'Marcus_Vipsanius_Agrippa','MilitaryPerson'),(224,NULL,'Hadrian%27s_Villa','WorldHeritageSite'),(225,NULL,'Athens','Area'),(226,NULL,'Theseus',NULL),(227,NULL,'Nero',NULL),(228,NULL,'Jerusalem',NULL),(229,NULL,'Army',NULL),(230,NULL,'Emperor',NULL),(231,NULL,'Palestine',NULL),(232,NULL,'Santo_%C3%82ngelo',NULL),(233,NULL,'Antoninus_Pius',NULL),(234,NULL,'Marcus_Aurelius',NULL),(235,NULL,'Lucius_Verus',NULL),(236,NULL,'Alberto_Santos-Dumont','Person'),(237,NULL,'Mainz','City'),(238,NULL,'Prussia','Country'),(239,NULL,'Albert_Einstein','Scientist'),(240,NULL,'Alex',NULL),(241,NULL,'Leka%2C_Norway','Area'),(242,NULL,'Albania','Country'),(243,NULL,'Skanderbeg',NULL),(244,NULL,'Catal%C3%A3o','City'),(245,NULL,'Sandy',NULL),(246,NULL,'Hindi','Language'),(247,NULL,'Ale',NULL),(248,NULL,'Sanskrit','Language'),(249,NULL,'School',NULL),(250,NULL,'Eastern_Orthodox_Church',NULL),(251,NULL,'Lily_Allen','MusicalArtist'),(252,NULL,'Paul_Allen','Person'),(253,NULL,'Microsoft',NULL),(254,NULL,'South_Dakota','Area'),(255,NULL,'Kansas','Area'),(256,NULL,'Kentucky','Area'),(257,NULL,'Michigan','Area'),(258,NULL,'Oklahoma','Area'),(259,NULL,'Texas','Area'),(260,NULL,'Allen_Park%2C_Michigan','Area'),(261,NULL,'Alfonso',NULL),(262,NULL,'Isabel',NULL),(263,NULL,'Avis_%28Portugal%29','Municipality'),(264,NULL,'John_II',NULL),(265,NULL,'Santar%C3%A9m',NULL),(266,NULL,'S%C3%A3o_Tom%C3%A9_and_Pr%C3%ADncipe','Country'),(267,NULL,'Pope',NULL),(268,NULL,'Beja_%28Portugal%29','Municipality'),(269,NULL,'God',NULL),(270,NULL,'Stephen_Jay_Gould','Scientist'),(271,NULL,'Roger_Penrose','Scientist'),(272,NULL,'Richard_Dawkins','Scientist'),(273,NULL,'John_Maynard_Smith','Scientist'),(274,NULL,'Tiger','Species'),(275,NULL,'Central_America',NULL),(276,NULL,'India','Country'),(277,NULL,'Politics',NULL),(278,NULL,'Reforma','Newspaper'),(279,NULL,'Europe_%28band%29','MusicalArtist'),(280,NULL,'Cambridge_University_Press','Company');
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `geoscope`
--

DROP TABLE IF EXISTS `geoscope`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `geoscope` (
  `geo_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `geo_name` varchar(255) NOT NULL,
  `geo_latitude` double DEFAULT NULL,
  `geo_longitude` double DEFAULT NULL,
  `geo_population` int(11) DEFAULT NULL,
  `geo_geoname` int(11) DEFAULT NULL,
  `geo_geonet` int(11) DEFAULT NULL,
  `geo_entity` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`geo_id`),
  KEY `geo_entity` (`geo_entity`),
  CONSTRAINT `geoscope_ibfk_1` FOREIGN KEY (`geo_entity`) REFERENCES `entity` (`ent_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `geoscope`
--

LOCK TABLES `geoscope` WRITE;
/*!40000 ALTER TABLE `geoscope` DISABLE KEYS */;
/*!40000 ALTER TABLE `geoscope` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ne`
--

DROP TABLE IF EXISTS `ne`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ne` (
  `ne_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ne_name` int(10) unsigned DEFAULT NULL,
  `ne_category` int(10) unsigned DEFAULT NULL,
  `ne_type` int(10) unsigned DEFAULT NULL,
  `ne_subtype` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ne_id`),
  KEY `ne_category` (`ne_category`),
  KEY `ne_type` (`ne_type`),
  KEY `ne_subtype` (`ne_subtype`),
  KEY `ne_name` (`ne_name`),
  CONSTRAINT `ne_ibfk_4` FOREIGN KEY (`ne_name`) REFERENCES `ne_name` (`nen_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `ne_ibfk_1` FOREIGN KEY (`ne_category`) REFERENCES `ne_category` (`nec_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `ne_ibfk_2` FOREIGN KEY (`ne_type`) REFERENCES `ne_type` (`net_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `ne_ibfk_3` FOREIGN KEY (`ne_subtype`) REFERENCES `ne_subtype` (`nes_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ne`
--

LOCK TABLES `ne` WRITE;
/*!40000 ALTER TABLE `ne` DISABLE KEYS */;
/*!40000 ALTER TABLE `ne` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ne_category`
--

DROP TABLE IF EXISTS `ne_category`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ne_category` (
  `nec_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nec_category` varchar(255) NOT NULL,
  PRIMARY KEY (`nec_id`),
  UNIQUE KEY `idx_category` (`nec_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ne_category`
--

LOCK TABLES `ne_category` WRITE;
/*!40000 ALTER TABLE `ne_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `ne_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ne_name`
--

DROP TABLE IF EXISTS `ne_name`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ne_name` (
  `nen_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nen_name` text NOT NULL,
  `nen_nr_terms` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`nen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ne_name`
--

LOCK TABLES `ne_name` WRITE;
/*!40000 ALTER TABLE `ne_name` DISABLE KEYS */;
/*!40000 ALTER TABLE `ne_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ne_rel_entity`
--

DROP TABLE IF EXISTS `ne_rel_entity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ne_rel_entity` (
  `nre_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nre_ne` int(10) unsigned NOT NULL,
  `nre_relation` int(10) unsigned NOT NULL,
  `nre_entity` int(10) unsigned NOT NULL,
  PRIMARY KEY (`nre_id`),
  UNIQUE KEY `idx_ne_rel_entity` (`nre_ne`,`nre_relation`,`nre_entity`) USING BTREE,
  KEY `nre_relation` (`nre_relation`),
  KEY `nre_entity` (`nre_entity`),
  CONSTRAINT `ne_rel_entity_ibfk_1` FOREIGN KEY (`nre_ne`) REFERENCES `ne` (`ne_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ne_rel_entity_ibfk_2` FOREIGN KEY (`nre_relation`) REFERENCES `relation` (`rel_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ne_rel_entity_ibfk_3` FOREIGN KEY (`nre_entity`) REFERENCES `entity` (`ent_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ne_rel_entity`
--

LOCK TABLES `ne_rel_entity` WRITE;
/*!40000 ALTER TABLE `ne_rel_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `ne_rel_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ne_subtype`
--

DROP TABLE IF EXISTS `ne_subtype`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ne_subtype` (
  `nes_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nes_subtype` varchar(255) NOT NULL,
  PRIMARY KEY (`nes_id`),
  UNIQUE KEY `idx_subtype` (`nes_subtype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ne_subtype`
--

LOCK TABLES `ne_subtype` WRITE;
/*!40000 ALTER TABLE `ne_subtype` DISABLE KEYS */;
/*!40000 ALTER TABLE `ne_subtype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ne_type`
--

DROP TABLE IF EXISTS `ne_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ne_type` (
  `net_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `net_type` varchar(255) NOT NULL,
  PRIMARY KEY (`net_id`),
  UNIQUE KEY `idx_type` (`net_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `ne_type`
--

LOCK TABLES `ne_type` WRITE;
/*!40000 ALTER TABLE `ne_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `ne_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page`
--

DROP TABLE IF EXISTS `page`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `page` (
  `page_id` int(10) unsigned NOT NULL,
  `page_lang` varchar(2) NOT NULL,
  `page_html` mediumblob,
  PRIMARY KEY (`page_id`,`page_lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `page`
--

LOCK TABLES `page` WRITE;
/*!40000 ALTER TABLE `page` DISABLE KEYS */;
/*!40000 ALTER TABLE `page` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `relation`
--

DROP TABLE IF EXISTS `relation`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `relation` (
  `rel_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rel_relation` varchar(255) NOT NULL,
  PRIMARY KEY (`rel_id`),
  UNIQUE KEY `idx_relation` (`rel_relation`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `relation`
--

LOCK TABLES `relation` WRITE;
/*!40000 ALTER TABLE `relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rembrandt_tag`
--

DROP TABLE IF EXISTS `rembrandt_tag`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rembrandt_tag` (
  `tag_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tag_version` varchar(45) NOT NULL,
  `tag_comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `idx_tag_version` (`tag_version`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `rembrandt_tag`
--

LOCK TABLES `rembrandt_tag` WRITE;
/*!40000 ALTER TABLE `rembrandt_tag` DISABLE KEYS */;
INSERT INTO `rembrandt_tag` VALUES (1,'0.8.7','');
/*!40000 ALTER TABLE `rembrandt_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rembrandted_doc`
--

DROP TABLE IF EXISTS `rembrandted_doc`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `rembrandted_doc` (
  `rdoc_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rdoc_doc` int(10) unsigned NOT NULL,
  `rdoc_title` text,
  `rdoc_body_rembrandted` text,
  PRIMARY KEY (`rdoc_id`),
  KEY `rdoc_doc` (`rdoc_doc`),
  FULLTEXT KEY `idx_body_title` (`rdoc_title`),
  FULLTEXT KEY `idx_body_rembrandted` (`rdoc_body_rembrandted`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `rembrandted_doc`
--

LOCK TABLES `rembrandted_doc` WRITE;
/*!40000 ALTER TABLE `rembrandted_doc` DISABLE KEYS */;
/*!40000 ALTER TABLE `rembrandted_doc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suggestion`
--

DROP TABLE IF EXISTS `suggestion`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `suggestion` (
  `sug_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sug_name` varchar(255) NOT NULL,
  `sug_type` varchar(2) DEFAULT NULL,
  `sug_lang` varchar(2) DEFAULT NULL,
  `sug_score` int(11) DEFAULT '0',
  PRIMARY KEY (`sug_id`),
  KEY `idx_suggestion_name` (`sug_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `suggestion`
--

LOCK TABLES `suggestion` WRITE;
/*!40000 ALTER TABLE `suggestion` DISABLE KEYS */;
/*!40000 ALTER TABLE `suggestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type`
--

DROP TABLE IF EXISTS `type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `type` (
  `typ_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `typ_name` varchar(255) NOT NULL,
  PRIMARY KEY (`typ_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `type`
--

LOCK TABLES `type` WRITE;
/*!40000 ALTER TABLE `type` DISABLE KEYS */;
/*!40000 ALTER TABLE `type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user` (
  `usr_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `usr_firstname` varchar(255) NOT NULL,
  `usr_lastname` varchar(255) NOT NULL,
  `usr_email` varchar(255) NOT NULL,
  `usr_password` varchar(255) NOT NULL,
  PRIMARY KEY (`usr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_on_collection`
--

DROP TABLE IF EXISTS `user_on_collection`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `user_on_collection` (
  `uoc_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uoc_user` int(10) unsigned NOT NULL,
  `uoc_collection` int(10) unsigned NOT NULL,
  `uoc_can_read` tinyint(1) DEFAULT '0',
  `uoc_can_write` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`uoc_id`),
  KEY `uoc_user` (`uoc_user`),
  KEY `uoc_collection` (`uoc_collection`),
  CONSTRAINT `user_on_collection_ibfk_2` FOREIGN KEY (`uoc_collection`) REFERENCES `collection` (`col_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_on_collection_ibfk_1` FOREIGN KEY (`uoc_user`) REFERENCES `user` (`usr_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `user_on_collection`
--

LOCK TABLES `user_on_collection` WRITE;
/*!40000 ALTER TABLE `user_on_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_on_collection` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-07-08 10:52:39
