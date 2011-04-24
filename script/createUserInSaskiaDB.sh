#!/usr/bin/env groovy

import saskia.bin.Configuration
import groovy.sql.Sql


println "hello"


INSERT INTO user VALUES(0,'johndoe',1,';mygroup;',1,'John','Doe','john@doe.com','','','','','',1000,1000,10000000,100000,0,0,NOW());
