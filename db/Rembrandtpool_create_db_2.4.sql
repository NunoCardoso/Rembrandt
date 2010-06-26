SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `RembrandtPool` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `RembrandtPool`;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`doc` (
  `doc_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `doc_original_id` VARCHAR(255) NULL ,
  `doc_lang` VARCHAR(2) NULL DEFAULT NULL ,
  `doc_date_created` DATETIME NULL DEFAULT NULL ,
  `doc_date_tagged` DATETIME NULL DEFAULT NULL ,
  `doc_proc` VARCHAR(2) NULL DEFAULT NULL,
  `doc_sync` VARCHAR(2) NULL DEFAULT NULL,
  `doc_edit` VARCHAR(2) NULL DEFAULT NULL,
  `doc_edit_date` DATETIME NULL DEFAULT NULL,
  `doc_latest_geo_signature` INT UNSIGNED DEFAULT NULL,
  `doc_latest_time_signature` INT UNSIGNED DEFAULT NULL)
 ENGINE = InnoDB;

CREATE INDEX `idx_doc_original_id` ON `RembrandtPool`.`doc` (`doc_original_id`) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`rembrandted_doc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`rembrandted_doc` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`rembrandted_doc` (
  `rdoc_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `rdoc_doc` INT UNSIGNED NOT NULL ,
  `rdoc_title` TEXT NULL ,
  `rdoc_body` MEDIUMTEXT NULL,
  `rdoc_comment` TEXT NULL)
ENGINE = MyISAM;

CREATE FULLTEXT INDEX `idx_rdoc_title` ON `RembrandtPool`.`rembrandted_doc` (`rdoc_title` ASC) ;
CREATE FULLTEXT INDEX `idx_rdoc_body` ON `RembrandtPool`.`rembrandted_doc` (`rdoc_body` ASC) ;

ALTER TABLE `RembrandtPool`.`rembrandted_doc` ADD CONSTRAINT FOREIGN KEY (`rdoc_doc` )
  REFERENCES `RembrandtPool`.`doc` (`doc_id` ) 
  ON DELETE CASCADE ON UPDATE CASCADE ;


-- -----------------------------------------------------
-- Table `RembrandtPool`.`tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`tag` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`tag` (
  `tag_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `tag_version` VARCHAR(45) NOT NULL,
  `tag_comment` VARCHAR(255) NULL DEFAULT NULL )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_tag_version` ON `RembrandtPool`.`tag` (`tag_version` ASC) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc_has_tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc_has_tag` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`doc_has_tag` (
  `dtg_document` INT UNSIGNED NOT NULL,
  `dtg_tag` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`dtg_document`,`dtg_tag`) )
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`doc_has_tag` ADD CONSTRAINT FOREIGN KEY (`dtg_document` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `RembrandtPool`.`doc_has_tag` ADD CONSTRAINT FOREIGN KEY (`dtg_tag` ) 
 REFERENCES `RembrandtPool`.`tag` (`tag_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- ----------------------------------------------------- 
-- Table `RembrandtPool`.`type`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `RembrandtPool`.`type` ;

CREATE TABLE `RembrandtPool`.`type` (
	`typ_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
	`typ_name` VARCHAR(255) NOT NULL) 
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc_has_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc_has_type` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`doc_has_type` (
  `dht_document` INT UNSIGNED NOT NULL,
  `dht_type` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`dht_document`,`dht_type`) )
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`doc_has_type` ADD CONSTRAINT FOREIGN KEY (`dht_document` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `RembrandtPool`.`doc_has_type` ADD CONSTRAINT FOREIGN KEY (`dht_type` ) 
 REFERENCES `RembrandtPool`.`type` (`typ_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- ----------------------------------------------------- 
-- Table `RembrandtPool`.`collection`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `RembrandtPool`.`collection` ;

CREATE TABLE `RembrandtPool`.`collection` (
   `col_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
   `col_name` VARCHAR(255) NOT NULL, 
   `col_comment` VARCHAR(255) NULL DEFAULT NULL,
   `col_new_user_can_read` TINYINT(1) DEFAULT FALSE) 
ENGINE = InnoDB;

CREATE INDEX `idx_col_name` ON `RembrandtPool`.`collection` (`col_name` ASC) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`collection_has_doc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`collection_has_doc` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`collection_has_doc` (
  `chd_collection` INT UNSIGNED NOT NULL,
  `chd_document` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`chd_collection`,`chd_document`) )
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`collection_has_doc` ADD CONSTRAINT FOREIGN KEY (`chd_collection` ) 
 REFERENCES `RembrandtPool`.`collection` (`col_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `RembrandtPool`.`collection_has_doc` ADD CONSTRAINT FOREIGN KEY (`chd_document` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;


-- -----------------------------------------------------
-- Table `RembrandtPool`.`source_doc`
-- -----------------------------------------------------
-- BINARY forces a case sensitive index

DROP TABLE IF EXISTS `RembrandtPool`.`source_doc` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`source_doc` (
  `sdoc_id` VARCHAR(255) BINARY NOT NULL,
  `sdoc_collection` INT UNSIGNED NOT NULL, 
  `sdoc_lang` VARCHAR(2) NOT NULL,
  `sdoc_html` MEDIUMBLOB NULL,
  `sdoc_comment` TEXT NULL,
  `sdoc_date` DATETIME NULL,
  `sdoc_doc` INT UNSIGNED NULL,
  `sdoc_proc` VARCHAR(2) NOT NULL,
  `sdoc_edit` VARCHAR(2) NOT NULL,
  `sdoc_edit_date` DATETIME NULL,
  PRIMARY KEY (`sdoc_id`,`sdoc_collection`,`sdoc_lang`) )
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`source_doc` ADD CONSTRAINT  FOREIGN KEY (`sdoc_collection` ) 
 REFERENCES `RembrandtPool`.`collection` (`col_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

CREATE INDEX `idx_sdoc_proc` ON `RembrandtPool`.`source_doc` (`sdoc_proc`) ;
CREATE INDEX `idx_sdoc_edit` ON `RembrandtPool`.`source_doc` (`sdoc_edit`) ;

-- ----------------------------------------------------- 
-- Table `RembrandtPool`.`user`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `RembrandtPool`.`user` ;

CREATE TABLE `RembrandtPool`.`user` (
	`usr_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
	`usr_login` VARCHAR(255) NOT NULL, 
	`usr_enabled` TINYINT(1) DEFAULT FALSE, 
	`usr_superuser` TINYINT(1) DEFAULT FALSE, 
	`usr_firstname` VARCHAR(255) NOT NULL, 
	`usr_lastname` VARCHAR(255) NOT NULL,
	`usr_email` VARCHAR(255) NOT NULL,
	`usr_password` VARCHAR(255) NOT NULL,
	`usr_api_key` VARCHAR(255) NULL,
	`usr_tmp_api_key` VARCHAR(255) NULL
) 
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `RembrandtPool`.`user_on_collection`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`user_on_collection` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`user_on_collection` (
  `uoc_user` INT UNSIGNED NOT NULL ,
  `uoc_collection` INT UNSIGNED NOT NULL,
  `uoc_can_read` TINYINT(1) DEFAULT FALSE, 
  `uoc_can_write` TINYINT(1) DEFAULT FALSE,
  `uoc_can_admin` TINYINT(1) DEFAULT FALSE, 
	PRIMARY KEY (`uoc_user`, `uoc_collection`)
 ) ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`user_on_collection` ADD CONSTRAINT FOREIGN KEY (`uoc_user` ) 
 REFERENCES `RembrandtPool`.`user` (`usr_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `RembrandtPool`.`user_on_collection` ADD CONSTRAINT FOREIGN KEY (`uoc_collection` ) 
 REFERENCES `RembrandtPool`.`collection` (`col_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`ne_category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ne_category` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`ne_category` (
  `nec_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `nec_category` VARCHAR(255) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_category` ON `RembrandtPool`.`ne_category` (`nec_category` ASC) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`ne_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ne_type` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`ne_type` (
  `net_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `net_type` VARCHAR(255) NULL )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_type` ON `RembrandtPool`.`ne_type` (`net_type` ASC) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`ne_subtype`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ne_subtype` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`ne_subtype` (
  `nes_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `nes_subtype` VARCHAR(255) NULL )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_subtype` ON `RembrandtPool`.`ne_subtype` (`nes_subtype` ASC) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`ne_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ne_name` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`ne_name` (
  `nen_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `nen_name` TEXT NOT NULL ,
  `nen_nr_terms` TINYINT NULL )
ENGINE = InnoDB;

CREATE INDEX `idx_nen_name` ON `RembrandtPool`.`ne_name` (`nen_name`(50)) ;


-- -----------------------------------------------------
-- Table `RembrandtPool`.`ne`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ne` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`ne` (
  `ne_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `ne_name` INT UNSIGNED  NULL ,
  `ne_lang` varchar(2) NULL,
  `ne_category` INT UNSIGNED  NULL ,
  `ne_type` INT UNSIGNED  NULL ,
  `ne_subtype` INT UNSIGNED  NULL ,
  `ne_entity` INT UNSIGNED NULL)
ENGINE = InnoDB;


ALTER TABLE `RembrandtPool`.`ne` ADD CONSTRAINT  FOREIGN KEY (`ne_entity` ) 
 REFERENCES `RembrandtPool`.`entity` (`ent_id` )
 ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`ne` ADD CONSTRAINT  FOREIGN KEY (`ne_category` )
 REFERENCES `RembrandtPool`.`ne_category` (`nec_id` ) 
 ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`ne` ADD CONSTRAINT  FOREIGN KEY (`ne_type` )
 REFERENCES `RembrandtPool`.`ne_type` (`net_id` ) 
 ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`ne` ADD CONSTRAINT  FOREIGN KEY (`ne_subtype` )
 REFERENCES `RembrandtPool`.`ne_subtype` (`nes_id` ) 
ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`ne` ADD CONSTRAINT  FOREIGN KEY (`ne_name` )
 REFERENCES `RembrandtPool`.`ne_name` (`nen_id` ) 
ON DELETE SET NULL ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc_has_ne`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc_has_ne` ;

-- this one has to have its own id - doc+ne are not unique 
CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`doc_has_ne` (
  `dhn_id` INT UNSIGNED PRiMARY KEY NOT NULL AUTO_INCREMENT ,
  `dhn_doc` INT UNSIGNED NOT NULL ,
  `dhn_ne` INT UNSIGNED NOT NULL ,
  `dhn_section` CHAR(1) DEFAULT NULL, 
  `dhn_sentence` INT NOT NULL ,
  `dhn_term` INT NOT NULL,
 )
ENGINE = InnoDB;

-- CREATE INDEX `idx_doc_sentence` ON `RembrandtPool`.`doc_has_ne` (`dhn_doc` ASC, `dhn_sentence` ASC) ;

ALTER TABLE `RembrandtPool`.`doc_has_ne` ADD CONSTRAINT  FOREIGN KEY (`dhn_doc` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`doc_has_ne` ADD CONSTRAINT  FOREIGN KEY (`dhn_ne` ) 
 REFERENCES `RembrandtPool`.`ne` (`ne_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`entity` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`entity` (
  `ent_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `ent_wikipedia_page` TEXT NULL DEFAULT NULL ,
  `ent_dbpedia_resource` TEXT NULL DEFAULT NULL, 
  `ent_dbpedia_class` VARCHAR(255) NULL DEFAULT NULL )
ENGINE = InnoDB;

CREATE INDEX `idx_ent_dbpedia_class` ON `RembrandtPool`.`entity` (`ent_dbpedia_class` ASC) ;


-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc_is_entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc_is_entity` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`doc_is_entity` (
  `die_document` INT UNSIGNED NOT NULL,
  `die_entity` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`die_document`,`die_entity`) )
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`doc_is_entity` ADD CONSTRAINT FOREIGN KEY (`die_document` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `RembrandtPool`.`doc_is_entity` ADD CONSTRAINT FOREIGN KEY (`die_entity` ) 
 REFERENCES `RembrandtPool`.`entity` (`ent_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`relation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`relation` ;

CREATE  TABLE IF NOT EXISTS `RembrandtPool`.`relation` (
  `rel_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `rel_relation` VARCHAR(255) NOT NULL)
  ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_relation` USING BTREE ON `RembrandtPool`.`relation` (`rel_relation` ASC) ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`ent_rel_ent`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ent_rel_ent` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`ent_rel_ent` (
  `ere_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `ere_ent1` INT UNSIGNED NOT NULL ,
  `ere_relation` INT UNSIGNED NOT NULL ,
  `ere_ent2` INT UNSIGNED NOT NULL ,
  `ere_time INT UNSIGNED DEFAULT NULL)
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`ent_rel_ent` ADD CONSTRAINT FOREIGN KEY (`ere_ent1` ) 
 REFERENCES `RembrandtPool`.`entity` (`ent_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`ent_rel_ent` ADD CONSTRAINT  FOREIGN KEY (`ere_relation` ) 
 REFERENCES `RembrandtPool`.`relation` (`rel_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`ent_rel_ent` ADD CONSTRAINT  FOREIGN KEY (`ere_ent2` ) 
 REFERENCES `RembrandtPool`.`entity` (`ent_id` )
 ON DELETE CASCADE ON UPDATE CASCADE ;


-- -----------------------------------------------------
-- Table `RembrandtPool`.`geoscope`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`geoscope` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`geoscope` (
  `geo_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `geo_name` VARCHAR(255) NOT NULL ,
  `geo_woeid` INT UNSIGNED NOT NULL,
  `geo_woeid_place` text,
  `geo_woeid_parent` text,
  `geo_woeid_ancestors` text,
  `geo_woeid_belongsto` text,
  `geo_woeid_neighbors` text,
  `geo_woeid_siblings` text,
  `geo_woeid_children` text,
  PRIMARY KEY (`geo_id`)

)   ENGINE = InnoDB;

alter table geoscope add constraint unique key geo_woeid (geo_woeid); 

-- -----------------------------------------------------
-- Table `RembrandtPool`.`entity_has_geoscope`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`entity_has_geoscope` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`entity_has_geoscope` (
  `ehg_entity` INT UNSIGNED NOT NULL,
  `ehg_geoscope` INT UNSIGNED NOT NULL, 
   PRIMARY KEY (`ehg_entity`,`ehg_geoscope` ))
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`entity_has_geoscope` ADD CONSTRAINT FOREIGN KEY (`ehg_entity` ) 
 REFERENCES `RembrandtPool`.`entity` (`ent_id` ) 
ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`entity_has_geoscope` ADD CONSTRAINT  FOREIGN KEY (`ehg_geoscope` ) 
REFERENCES `RembrandtPool`.`geoscope` (`geo_id` ) 
ON DELETE CASCADE ON UPDATE CASCADE ;  
 
-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc_geo_signature`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc_geo_signature` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`doc_geo_signature` (
  `dgs_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `dgs_document` INT UNSIGNED NOT NULL ,
  `dgs_signature` TEXT DEFAULT NULL,
  `dgs_tag` INT UNSIGNED DEFAULT NULL,
  `dgs_date_created` DATETIME DEFAULT NULL)
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`doc_geo_signature` ADD CONSTRAINT FOREIGN KEY (`dgs_document` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`doc_geo_signature` ADD CONSTRAINT FOREIGN KEY (`dgs_tag` ) 
 REFERENCES `RembrandtPool`.`tag` (`tag_id` ) ON DELETE SET NULL ON UPDATE CASCADE ; 

-- add constraint to doc

ALTER TABLE `RembrandtPool`.`doc` ADD CONSTRAINT FOREIGN KEY (`doc_latest_geo_signature` ) 
 REFERENCES `RembrandtPool`.`doc_geo_signature` (`dgs_id`) ON DELETE SET NULL ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`doc_time_signature`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`doc_time_signature` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`doc_time_signature` (
  `dts_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `dts_document` INT UNSIGNED NOT NULL ,
  `dts_signature` TEXT DEFAULT NULL,
  `dts_tag` INT UNSIGNED DEFAULT NULL,
  `dts_date_created` DATETIME DEFAULT NULL)
ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`doc_time_signature` ADD CONSTRAINT FOREIGN KEY (`dts_document` ) 
 REFERENCES `RembrandtPool`.`doc` (`doc_id` ) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `RembrandtPool`.`doc_time_signature` ADD CONSTRAINT FOREIGN KEY (`dts_tag` ) 
 REFERENCES `RembrandtPool`.`tag` (`tag_id` ) ON DELETE SET NULL ON UPDATE CASCADE ; 

-- add constraint to doc

ALTER TABLE `RembrandtPool`.`doc` ADD CONSTRAINT FOREIGN KEY (`doc_latest_time_signature` ) 
 REFERENCES `RembrandtPool`.`doc_time_signature` (`dts_id`) ON DELETE SET NULL ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `RembrandtPool`.`ne_time`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RembrandtPool`.`ne_time` ;

CREATE TABLE IF NOT EXISTS `RembrandtPool`.`ne_time` (
  `nti_ne` INT UNSIGNED PRIMARY KEY NOT NULL ,
  `nti_time` VARCHAR(255) NOT NULL 
) ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`ne_time` ADD CONSTRAINT FOREIGN KEY (`nti_ne` ) 
 REFERENCES `RembrandtPool`.`ne` (`ne_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;


-- ----------------------------------------------------- 
-- Table `RembrandtPool`.`suggestion`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `RembrandtPool`.`suggestion` ;

CREATE TABLE `RembrandtPool`.`suggestion` (
   `sug_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
   `sug_name` VARCHAR(255) NOT NULL, 
   `sug_type` VARCHAR(2) DEFAULT NULL, 
   `sug_lang` VARCHAR(2) DEFAULT NULL,
   `sug_desc` VARCHAR(255) DEFAULT NULL, 
   `sug_ground` VARCHAR(255) DEFAULT NULL, 
   `sug_score` INT DEFAULT 0
) ENGINE = InnoDB;

CREATE INDEX `idx_sug_name` ON `RembrandtPool`.`suggestion` (`sug_name`) ;

-- ----------------------------------------------------- 
-- Table `RembrandtPool`.`cache`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `RembrandtPool`.`cache` ;

CREATE TABLE `RembrandtPool`.`cache` (
   `cac_id` VARCHAR(255) NOT NULL, 
   `cac_collection` INT UNSIGNED NOT NULL, 
   `cac_lang` VARCHAR(2) NOT NULL,
   `cac_date` DATETIME NOT NULL,
   `cac_expire` DATETIME NOT NULL, 
   `cac_obj` MEDIUMBLOB DEFAULT NULL, 
	PRIMARY KEY (`cac_id`, `cac_collection`, `cac_lang`)
) ENGINE = InnoDB;

ALTER TABLE `RembrandtPool`.`cache` ADD CONSTRAINT FOREIGN KEY (`cac_collection` )
  REFERENCES `RembrandtPool`.`collection` (`col_id` ) 
  ON DELETE CASCADE ON UPDATE CASCADE ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
