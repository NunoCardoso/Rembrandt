SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- ----------------------------------------------------- 
-- Table `user`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `user` ;

CREATE TABLE `user` (
	`usr_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
	`usr_login` VARCHAR(255) NOT NULL, 
	`usr_enabled` TINYINT(1) DEFAULT FALSE, 
	`usr_groups` varchar(255) DEFAULT NULL,
	`usr_superuser` TINYINT(1) DEFAULT FALSE, 
	`usr_firstname` VARCHAR(255) NOT NULL, 
	`usr_lastname` VARCHAR(255) NOT NULL,
	`usr_email` VARCHAR(255) NOT NULL,
	`usr_password` VARCHAR(255) NOT NULL,
	`usr_tmp_password` VARCHAR(255) NOT NULL,
	`usr_api_key` VARCHAR(255) NULL,
	`usr_tmp_api_key` VARCHAR(255) NULL,
	`usr_pub_key` VARCHAR(255) NULL,
	`usr_max_number_collections` TINYINT DEFAULT 0,
	`usr_max_number_tasks` TINYINT DEFAULT 1,
	`usr_max_docs_per_collection` INT DEFAULT 0, 
	`usr_max_daily_api_calls` int DEFAULT 0,
	`usr_current_daily_api_calls` int DEFAULT 0,
	`usr_total_api_calls` int unsigned DEFAULT 0,
	`usr_date_last_api_call` datetime default null) 
ENGINE = InnoDB;

-- ----------------------------------------------------- 
-- Table `collection`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `collection` ;

CREATE TABLE `collection` (
   `col_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
   `col_name` VARCHAR(255) NOT NULL, 
   `col_owner` int(10) unsigned DEFAULT NULL,
   `col_lang` VARCHAR(2) DEFAULT NULL, 
   `col_permission` char(9) DEFAULT NULL,
   `col_comment` VARCHAR(255) NULL DEFAULT NULL
) 
ENGINE = InnoDB;

CREATE INDEX `idx_col_name_owner` ON `collection` (`col_name` ASC, `col_owner`) ;
ALTER TABLE `collection` ADD CONSTRAINT FOREIGN KEY (`col_owner`) 
 REFERENCES `user` (`usr_id`) ON DELETE SET NULL ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `doc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc` ;

CREATE  TABLE IF NOT EXISTS `doc` (
  `doc_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `doc_original_id` VARCHAR(255) NULL,
  `doc_collection` int(10) unsigned NOT NULL,
  `doc_webstore` VARCHAR(255) NULL,
  `doc_version` int(11) DEFAULT 1,
  `doc_lang` VARCHAR(2) NULL DEFAULT NULL ,
  `doc_date_created` DATETIME NULL DEFAULT NULL ,
  `doc_date_tagged` DATETIME NULL DEFAULT NULL ,
  `doc_proc` VARCHAR(2) NULL DEFAULT NULL,
  `doc_sync` VARCHAR(2) NULL DEFAULT NULL,
  `doc_latest_geo_signature` INT UNSIGNED DEFAULT NULL,
  `doc_latest_time_signature` INT UNSIGNED DEFAULT NULL)
 ENGINE = InnoDB;

CREATE INDEX `idx_doc_original_id` ON `doc` (`doc_original_id`) ;
CREATE INDEX `idx_doc_proc` ON `doc` (`doc_proc`) ;
CREATE INDEX `idx_doc_sync` ON `doc` (`doc_sync`) ;

ALTER TABLE `doc` ADD CONSTRAINT FOREIGN KEY (`doc_collection`) 
 REFERENCES `collection` (`col_id`) ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tag` ;

CREATE  TABLE IF NOT EXISTS `tag` (
  `tag_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `tag_version` VARCHAR(45) NOT NULL,
  `tag_comment` VARCHAR(255) NULL DEFAULT NULL )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_tag_version` ON `tag` (`tag_version` ASC) ;

-- -----------------------------------------------------
-- Table `doc_has_tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc_has_tag` ;

CREATE TABLE IF NOT EXISTS `doc_has_tag` (
  `dtg_document` INT UNSIGNED NOT NULL,
  `dtg_tag` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`dtg_document`,`dtg_tag`) )
ENGINE = InnoDB;

ALTER TABLE `doc_has_tag` ADD CONSTRAINT FOREIGN KEY (`dtg_document` ) 
 REFERENCES `doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `doc_has_tag` ADD CONSTRAINT FOREIGN KEY (`dtg_tag` ) 
 REFERENCES `tag` (`tag_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- ----------------------------------------------------- 
-- Table `type`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `type` ;

CREATE TABLE `type` (
	`typ_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
	`typ_name` VARCHAR(255) NOT NULL) 
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `doc_has_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc_has_type` ;

CREATE TABLE IF NOT EXISTS `doc_has_type` (
  `dht_document` INT UNSIGNED NOT NULL,
  `dht_type` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`dht_document`,`dht_type`) )
ENGINE = InnoDB;

ALTER TABLE `doc_has_type` ADD CONSTRAINT FOREIGN KEY (`dht_document` ) 
 REFERENCES `doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `doc_has_type` ADD CONSTRAINT FOREIGN KEY (`dht_type` ) 
 REFERENCES `type` (`typ_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;



-- -----------------------------------------------------
-- Table `source_doc`
-- -----------------------------------------------------
-- BINARY forces a case sensitive index

DROP TABLE IF EXISTS `source_doc` ;

CREATE  TABLE IF NOT EXISTS `source_doc` (
  `sdoc_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `sdoc_original_id` VARCHAR(255) BINARY NOT NULL,
  `sdoc_webstore` VARCHAR(255) NULL,
  `sdoc_collection` INT UNSIGNED NOT NULL, 
  `sdoc_lang` VARCHAR(2) NOT NULL,
  `sdoc_comment` TEXT NULL,
  `sdoc_date` DATETIME NULL,
  `sdoc_doc` INT UNSIGNED NULL,
  `sdoc_proc` VARCHAR(2) NOT NULL) 
ENGINE = InnoDB;

ALTER TABLE `source_doc` ADD CONSTRAINT  FOREIGN KEY (`sdoc_collection` ) 
 REFERENCES `collection` (`col_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

CREATE INDEX `idx_sdoc_original_id` ON `source_doc` (`sdoc_original_id`) ;
CREATE INDEX `idx_sdoc_doc` ON `source_doc` (`sdoc_doc`) ;
CREATE INDEX `idx_sdoc_proc` ON `source_doc` (`sdoc_proc`) ;

-- -----------------------------------------------------
-- Table `ne_category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ne_category` ;

CREATE  TABLE IF NOT EXISTS `ne_category` (
  `nec_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `nec_category` VARCHAR(255) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_category` ON `ne_category` (`nec_category` ASC) ;

-- -----------------------------------------------------
-- Table `ne_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ne_type` ;

CREATE  TABLE IF NOT EXISTS `ne_type` (
  `net_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `net_type` VARCHAR(255) NULL )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_type` ON `ne_type` (`net_type` ASC) ;

-- -----------------------------------------------------
-- Table `ne_subtype`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ne_subtype` ;

CREATE  TABLE IF NOT EXISTS `ne_subtype` (
  `nes_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `nes_subtype` VARCHAR(255) NULL )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_subtype` ON `ne_subtype` (`nes_subtype` ASC) ;

-- -----------------------------------------------------
-- Table `ne_name`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ne_name` ;

CREATE  TABLE IF NOT EXISTS `ne_name` (
  `nen_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `nen_name` TEXT NOT NULL ,
  `nen_nr_terms` TINYINT NULL )
ENGINE = InnoDB;

CREATE INDEX `idx_nen_name` ON `ne_name` (`nen_name`(50)) ;

-- -----------------------------------------------------
-- Table `entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `entity` ;

CREATE  TABLE IF NOT EXISTS `entity` (
  `ent_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `ent_name` VARCHAR(255) NULL DEFAULT NULL,
  `ent_dbpedia_resource` TEXT NULL DEFAULT NULL, 
  `ent_dbpedia_class` VARCHAR(255) NULL DEFAULT NULL )
ENGINE = InnoDB;

CREATE INDEX `idx_ent_dbpedia_class` ON `entity` (`ent_dbpedia_class` ASC) ;
CREATE INDEX `idx_ent_dbpedia_resource` ON entity (ent_dbpedia_resource(50));

-- -----------------------------------------------------
-- Table `ne`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ne` ;

CREATE  TABLE IF NOT EXISTS `ne` (
  `ne_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `ne_name` INT UNSIGNED  NULL ,
  `ne_lang` varchar(2) NULL,
  `ne_category` INT UNSIGNED  NULL ,
  `ne_type` INT UNSIGNED  NULL ,
  `ne_subtype` INT UNSIGNED  NULL ,
  `ne_entity` INT UNSIGNED NULL)
ENGINE = InnoDB;


ALTER TABLE `ne` ADD CONSTRAINT  FOREIGN KEY (`ne_entity` ) 
 REFERENCES `entity` (`ent_id` )
 ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `ne` ADD CONSTRAINT  FOREIGN KEY (`ne_category` )
 REFERENCES `ne_category` (`nec_id` ) 
 ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `ne` ADD CONSTRAINT  FOREIGN KEY (`ne_type` )
 REFERENCES `ne_type` (`net_id` ) 
 ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `ne` ADD CONSTRAINT  FOREIGN KEY (`ne_subtype` )
 REFERENCES `ne_subtype` (`nes_id` ) 
ON DELETE SET NULL ON UPDATE CASCADE ;
ALTER TABLE `ne` ADD CONSTRAINT  FOREIGN KEY (`ne_name` )
 REFERENCES `ne_name` (`nen_id` ) 
ON DELETE SET NULL ON UPDATE CASCADE ;

ALTER TABLE `ne` ADD KEY `ne_lang`( `ne_lang`);

-- -----------------------------------------------------
-- Table `doc_has_ne`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc_has_ne` ;

-- this one has to have its own id - doc+ne are not unique 
CREATE  TABLE IF NOT EXISTS `doc_has_ne` (
  `dhn_id` INT UNSIGNED PRiMARY KEY NOT NULL AUTO_INCREMENT ,
  `dhn_doc` INT UNSIGNED NOT NULL ,
  `dhn_ne` INT UNSIGNED NOT NULL ,
  `dhn_section` CHAR(1) DEFAULT NULL, 
  `dhn_sentence` INT NOT NULL ,
  `dhn_term` INT NOT NULL
)
ENGINE = InnoDB;

-- CREATE INDEX `idx_doc_sentence` ON `doc_has_ne` (`dhn_doc` ASC, `dhn_sentence` ASC) ;

ALTER TABLE `doc_has_ne` ADD CONSTRAINT  FOREIGN KEY (`dhn_doc` ) 
 REFERENCES `doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `doc_has_ne` ADD CONSTRAINT  FOREIGN KEY (`dhn_ne` ) 
 REFERENCES `ne` (`ne_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `doc_is_entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc_is_entity` ;

CREATE TABLE IF NOT EXISTS `doc_is_entity` (
  `die_document` INT UNSIGNED NOT NULL,
  `die_entity` INT UNSIGNED NOT NULL ,
   PRIMARY KEY (`die_document`,`die_entity`) )
ENGINE = InnoDB;

ALTER TABLE `doc_is_entity` ADD CONSTRAINT FOREIGN KEY (`die_document` ) 
 REFERENCES `doc` (`doc_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

ALTER TABLE `doc_is_entity` ADD CONSTRAINT FOREIGN KEY (`die_entity` ) 
 REFERENCES `entity` (`ent_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `relation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `relation` ;

CREATE  TABLE IF NOT EXISTS `relation` (
  `rel_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `rel_relation` VARCHAR(255) NOT NULL)
  ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_relation` USING BTREE ON `relation` (`rel_relation` ASC) ;

-- -----------------------------------------------------
-- Table `ent_rel_ent`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ent_rel_ent` ;

CREATE TABLE IF NOT EXISTS `ent_rel_ent` (
  `ere_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `ere_ent1` INT UNSIGNED NOT NULL ,
  `ere_relation` INT UNSIGNED NOT NULL ,
  `ere_ent2` INT UNSIGNED NOT NULL,
  `ere_time` INT UNSIGNED DEFAULT NULL)
ENGINE = InnoDB;

ALTER TABLE `ent_rel_ent` ADD CONSTRAINT FOREIGN KEY (`ere_ent1` ) 
 REFERENCES `entity` (`ent_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `ent_rel_ent` ADD CONSTRAINT  FOREIGN KEY (`ere_relation` ) 
 REFERENCES `relation` (`rel_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `ent_rel_ent` ADD CONSTRAINT  FOREIGN KEY (`ere_ent2` ) 
 REFERENCES `entity` (`ent_id` )
 ON DELETE CASCADE ON UPDATE CASCADE ;


-- -----------------------------------------------------
-- Table `geoscope`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `geoscope` ;

CREATE TABLE IF NOT EXISTS `geoscope` (
  `geo_id` INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  `geo_name` VARCHAR(255) NOT NULL ,
  `geo_woeid` INT UNSIGNED NOT NULL,
  `geo_woeid_type` SMALLINT UNSIGNED DEFAULT NULL,
  `geo_woeid_place` text,
  `geo_woeid_parent` text,
  `geo_woeid_ancestors` text,
  `geo_woeid_belongsto` text,
  `geo_woeid_neighbors` text,
  `geo_woeid_siblings` text,
  `geo_woeid_children` text
)   ENGINE = InnoDB;

alter table geoscope add constraint unique key geo_woeid (geo_woeid); 

-- -----------------------------------------------------
-- Table `entity_has_geoscope`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `entity_has_geoscope` ;

CREATE TABLE IF NOT EXISTS `entity_has_geoscope` (
  `ehg_entity` INT UNSIGNED NOT NULL,
  `ehg_geoscope` INT UNSIGNED NOT NULL, 
   PRIMARY KEY (`ehg_entity`,`ehg_geoscope` ))
ENGINE = InnoDB;

ALTER TABLE `entity_has_geoscope` ADD CONSTRAINT FOREIGN KEY (`ehg_entity` ) 
 REFERENCES `entity` (`ent_id` ) 
ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `entity_has_geoscope` ADD CONSTRAINT  FOREIGN KEY (`ehg_geoscope` ) 
REFERENCES `geoscope` (`geo_id` ) 
ON DELETE CASCADE ON UPDATE CASCADE ;  
 
-- -----------------------------------------------------
-- Table `doc_geo_signature`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc_geo_signature` ;

CREATE TABLE IF NOT EXISTS `doc_geo_signature` (
  `dgs_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `dgs_document` INT UNSIGNED NOT NULL ,
  `dgs_signature` MEDIUMTEXT DEFAULT NULL,
  `dgs_tag` INT UNSIGNED DEFAULT NULL,
  `dgs_date_created` DATETIME DEFAULT NULL)
ENGINE = InnoDB;

ALTER TABLE `doc_geo_signature` ADD CONSTRAINT FOREIGN KEY (`dgs_document` ) 
 REFERENCES `doc` (`doc_id` ) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `doc_geo_signature` ADD CONSTRAINT FOREIGN KEY (`dgs_tag` ) 
 REFERENCES `tag` (`tag_id` ) ON DELETE SET NULL ON UPDATE CASCADE ; 

-- add constraint to doc

ALTER TABLE `doc` ADD CONSTRAINT FOREIGN KEY (`doc_latest_geo_signature` ) 
 REFERENCES `doc_geo_signature` (`dgs_id`) ON DELETE SET NULL ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `doc_time_signature`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `doc_time_signature` ;

CREATE TABLE IF NOT EXISTS `doc_time_signature` (
  `dts_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `dts_document` INT UNSIGNED NOT NULL ,
  `dts_signature` TEXT DEFAULT NULL,
  `dts_tag` INT UNSIGNED DEFAULT NULL,
  `dts_date_created` DATETIME DEFAULT NULL)
ENGINE = InnoDB;

ALTER TABLE `doc_time_signature` ADD CONSTRAINT FOREIGN KEY (`dts_document` ) 
 REFERENCES `doc` (`doc_id` ) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `doc_time_signature` ADD CONSTRAINT FOREIGN KEY (`dts_tag` ) 
 REFERENCES `tag` (`tag_id` ) ON DELETE SET NULL ON UPDATE CASCADE ; 

-- add constraint to doc

ALTER TABLE `doc` ADD CONSTRAINT FOREIGN KEY (`doc_latest_time_signature` ) 
 REFERENCES `doc_time_signature` (`dts_id`) ON DELETE SET NULL ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `subject`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `subject` ;

CREATE TABLE IF NOT EXISTS `subject` (
  `sbj_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `sbj_subject` TEXT NOT NULL)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `subject_ground`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `subject_ground` ;

CREATE TABLE IF NOT EXISTS `subject_ground` (
  `sgr_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  `sgr_subject` INT UNSIGNED DEFAULT NULL,
  `sgr_geoscope` INT UNSIGNED DEFAULT NULL,
  `sgr_dbpedia_resource` TEXT NULL DEFAULT NULL, 
  `sgr_dbpedia_class` TEXT NULL DEFAULT NULL, 
  `sgr_wikipedia_category` TEXT NULL DEFAULT NULL, 
  `sgr_comment` TEXT NULL DEFAULT NULL) 
ENGINE = InnoDB;

ALTER TABLE `subject_ground` ADD CONSTRAINT FOREIGN KEY (`sgr_subject`) 
 REFERENCES `subject` (`sbj_id`) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `subject_ground` ADD CONSTRAINT FOREIGN KEY (`sgr_geoscope`) 
 REFERENCES `geoscope` (`geo_id`) ON DELETE CASCADE ON UPDATE CASCADE ;

-- -----------------------------------------------------
-- Table `ne_time`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ne_time` ;

CREATE TABLE IF NOT EXISTS `ne_time` (
  `nti_ne` INT UNSIGNED PRIMARY KEY NOT NULL ,
  `nti_time` VARCHAR(255) NOT NULL 
) ENGINE = InnoDB;

ALTER TABLE `ne_time` ADD CONSTRAINT FOREIGN KEY (`nti_ne` ) 
 REFERENCES `ne` (`ne_id` ) 
 ON DELETE CASCADE ON UPDATE CASCADE ;


-- ----------------------------------------------------- 
-- Table `suggestion`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `suggestion` ;

CREATE TABLE `suggestion` (
   `sug_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
   `sug_name` VARCHAR(255) NOT NULL, 
   `sug_type` VARCHAR(2) DEFAULT NULL, 
   `sug_lang` VARCHAR(2) DEFAULT NULL,
   `sug_desc` VARCHAR(255) DEFAULT NULL, 
   `sug_ground` VARCHAR(255) DEFAULT NULL, 
   `sug_score` INT DEFAULT 0
) ENGINE = InnoDB;

CREATE INDEX `idx_sug_name` ON `suggestion` (`sug_name`) ;

-- ----------------------------------------------------- 
-- Table `cache`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `cache` ;

CREATE TABLE `cache` (
   `cac_id` VARCHAR(255) NOT NULL, 
   `cac_collection` INT UNSIGNED NOT NULL, 
   `cac_lang` VARCHAR(2) NOT NULL,
   `cac_date` DATETIME NOT NULL,
   `cac_expire` DATETIME NOT NULL, 
   `cac_obj` MEDIUMBLOB DEFAULT NULL, 
	PRIMARY KEY (`cac_id`, `cac_collection`, `cac_lang`)
) ENGINE = InnoDB;

ALTER TABLE `cache` ADD CONSTRAINT FOREIGN KEY (`cac_collection` )
  REFERENCES `collection` (`col_id` ) 
  ON DELETE CASCADE ON UPDATE CASCADE ;

-- ----------------------------------------------------- 
-- Table `task`
-- -----------------------------------------------------

CREATE TABLE `task` (
	 `tsk_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
	 `tsk_task` VARCHAR(255) NOT NULL,
    `tsk_user` INT UNSIGNED NOT NULL, 
    `tsk_collection` INT UNSIGNED NOT NULL, 
	 `tsk_type` CHAR(3) NOT NULL,
    `tsk_priority` INT DEFAULT 0,
    `tsk_limit` INT DEFAULT NULL,
    `tsk_offset` INT UNSIGNED DEFAULT NULL,
    `tsk_done` INT DEFAULT NULL,
    `tsk_scope` CHAR(3) DEFAULT NULL, 
	 `tsk_persistence` CHAR(3) DEFAULT NULL,  
    `tsk_status` CHAR(3) DEFAULT NULL,
    `tsk_comment` varchar(255) DEFAULT NULL
) ENGINE = InnoDB;

ALTER TABLE `task` ADD CONSTRAINT FOREIGN KEY (`tsk_user`) 
 REFERENCES `user` (`usr_id`) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE `task` ADD CONSTRAINT FOREIGN KEY (`tsk_collection`) 
 REFERENCES `collection` (`col_id`) ON DELETE CASCADE ON UPDATE CASCADE ;

-- ----------------------------------------------------- 
-- Table `job`
-- -----------------------------------------------------

CREATE TABLE `job` (
	 `job_id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, 
    `job_task` INT UNSIGNED NOT NULL, 
    `job_worker` VARCHAR(255) DEFAULT NULL, 
 	 `job_doc_type` CHAR(4) DEFAULT NULL,
	 `job_doc_id` INT UNSIGNED NOT NULL, 
	 `job_doc_edit` CHAR(2) DEFAULT NULL,
    `job_doc_edit_date` DATETIME NOT NULL
) ENGINE = InnoDB;
	
ALTER TABLE `job` ADD CONSTRAINT FOREIGN KEY (`job_task`) 
 REFERENCES `task` (`tsk_id`) ON DELETE CASCADE ON UPDATE CASCADE ;

    
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------
-- ADD NULL ENTRIES 
-- -----------

INSERT INTO `geoscope`(geo_name, geo_woeid) VALUES("",0);
