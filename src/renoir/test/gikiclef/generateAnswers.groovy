package renoir.test.gikiclef

import renoir.obj.*
import renoir.bin.*
import saskia.dbpedia.DBpediaAPI

Question q = new Question()
WriteRun wr = new WriteRun()
DBpediaAPI dbpedia = DBpediaAPI.newInstance()

/*1
q.id="GC-2009-01"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("It�lia", "pt")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ernest Hemingway", "pt")
*/

/*2
q.id="GC-2009-02"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pa�s de Gales", "pt")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pa�ses Baixos", "pt")
*/
/*3
q.id="GC-2009-03"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("USA","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("France","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Germany","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Canada","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Congo","en")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Peter Deunov","en")
*/
/*4
q.id="GC-2009-04"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Paul Celan", "pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mihai Eminescu", "pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Tristan Tzara", "pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Isabel de Wied", "pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Andrei Mure\\u015fanu", "pt")
List answer = wr.format(q, "pt")
q.answer = []
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Constantin Ab\\u0103lu\\u0163\\u0103", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Haig Acterian", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Felix Aderca", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Horia Agarici", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("George Alboiu", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vasile Alecsandri", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Grigore Alexandrescu", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ioan Alexandru", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Alexandru Andri\\u0163oiu", "en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Dimitrie Anghel", "en")
List answer2 = wr.format(q, "en")
*/

/*5
q.id="GC-2009-05"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Nosferatu, Eine Symphonie des Grauens","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Nosferatu: Phantom der Nacht","pt")
*/

/*6
q.id="GC-2009-06"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Janine Jansen","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Andr� Rieu","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Harry Sacksioni","pt")
List answer = wr.format(q, "pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Emmy Verhey","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Frank Van Essen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Jaap van Zweden","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lucy van Dael","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mathieu van Bellen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Monica Germino","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pieter Hellendaal","en")
List answer2 = wr.format(q, "en")
*/

// 7 e 8 � para desistir
/*9
q.id="GC-2009-09"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Frankfurt am Main","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Leipzig","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Wetzlar","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Weimar","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("It�lia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Fran�a","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Morre","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Darwin (Austr�lia)","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Darwin (Minnesota)","pt")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Johann Wolfgang von Goethe","pt")
*/

/*16
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Alb�nia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Andorra","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Arm�nia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("�ustria","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Azerbaij�o","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Bielorr�ssia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Bulg�ria","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("B�lgica","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cazaquist�o","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Chipre","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cro�cia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Dinamarca","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Eslov�quia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Eslov�nia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Espanha","pt")
*/
/*17
q.id="GC-2009-17"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Abruzos","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Basilicata","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cal�bria","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Camp�nia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Em�lia-Romanha","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Friuli-Venezia Giulia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lig�ria","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lombardia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("L�cio","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Marche","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Molise","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Piemonte","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Puglia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sardenha","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sic�lia","pt")
*/

/*
q.id="GC-2009-19"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Licancabur","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cord�n del Azufre","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cerro Torre","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cerro Fitzroy","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Tupungato","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cerro Bayo","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cerro Escorial","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Falso Azufre","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lastarria","pt")   
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Llullaillaco","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sierra Nevada de Lagunas Bravas","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Socompa","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vulc�o Parinacota","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pomerape","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Paruma","pt")
*/

/*
q.id="GC-2009-21"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Adda River","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Curone","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Dora Baltea","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Grana del Monferrato","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lambro","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Nure","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Orco","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Rotaldo","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Scrivia","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sesia River","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Staffora","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Terdoppio","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ticino River","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Maira","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Stura del Monferrato","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Varaita","en")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Po River","en")
*/

/*
q.id="GC-2009-25"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Marc Gen�","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Fernando Alonso","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Adri�n Campos","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Luis Perez-Sala","en")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Minardi","pt")
*/
/*
q.id="GC-2009-31"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Canton of Bellinzona","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Canton of Lugano","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Canton of Raetia","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Italian Somalia","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Italy","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("San Marino","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vatican City","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Italian Cyrenaica","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Italian Trans-Juba","en")
*/

/*
q.id="GC-2009-32"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Emil Cioran","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mihai Eminescu","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ion Luca Caragiale","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Isabel de Wied","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Alexandru Macedonski","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Titu Maiorescu","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Maria de Saxe-Coburgo-Gota","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Jacob Levy Moreno","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vasile Oltean","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Oskar Pastior","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Florentin Smarandache","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Alexandru Vlahu\\u0163\\u0103","pt")
*/
/*
q.id="GC-2009-33"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("�ustria","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("It�lia","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Rep�blica Dominicanas","pt")
q.answerJustification << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Alpes","pt")
*/
/*34
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Annapurna","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cho Oyu","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Dhaulagiri","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lhotse","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Makalu","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Manaslu","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mount Everest","en")
*/
/*35
q.id="GC-2009-35"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("C�rpatos","pt")
List answer = wr.format(q, "pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Baiu Mountains","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Caraiman Peak","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Divisions of the Carpathians","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ineu Peak","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Moldoveanu Peak","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Negoiu Peak","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Par�ngu Mare","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Peleaga","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("P\\u0103pu\\u015fa","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Romanian Carpathians","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("T�mpa, Bra\\u015fov","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Transylvanian Mountains","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vi\\u015ftea Mare","en")
List answer2 = wr.format(q, "en")
*/
/*
q.id="GC-2009-36"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Movile Cave","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pe\\u015ftera Muierilor","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pe\\u015ftera Ur\\u015filor","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pe\\u015ftera V�ntului","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Pe\\u015ftera cu Oase","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sc\\u0103ri\\u015foara Cave","en")
List answer = wr.format(q, "en")
*/

/*37
q.id="GC-2009-37"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mustis","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Anita Skorgan","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Arne Bendiksen","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Karoline Kr�ger","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("J�rn Lande","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sissel Kyrkjeb�","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Secthdamon","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Valfar","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lene Grawford Nystr�m","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Tjodalv","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Cyrus (m�sico)","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Memnock","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Elvorn","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Finn Kalvik","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Eirik T. Saltr�","pt")
*/
/*39
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Espelandsfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Kjelfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Langfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("L�tefossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mardalsfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mongefossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("M�nafossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ramnefjellsfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Rjukanfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Seven Sisters Waterfall, Norway","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Skrikjofossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Steinsdalsfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Tyssestrengene","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vettisfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("V�ringfossen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Rjukandefossen","en")
*/
/*40
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Elbe","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Havel","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Rhine","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Spree","en")
*/
/*43
q.id="GC-2009-43"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Bad Gastein","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Filzmoos","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Galt�r","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Hochfilzen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Kitzsteinhorn","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Kitzb�hel","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Lech am Arlberg","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Mayrhofen","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Nassfeld","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Saalbach-Hinterglemm","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Sankt Anton am Arlberg","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Schladming","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Ski Amad�","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("S�lden","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Vandans","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Z�rs","en")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Kulm (venue)","en")
*/
q.id="GC-2009-46"
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Hugo Ball","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Bertolt Brecht","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Georg B\\u00fcchner","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Stefan George","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Hermann Hesse","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Hermann Kesten","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Klabund","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Klaus Mann","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Hieronymus M\\u00fcnzer","pt")
q.answer << dbpedia.getDBpediaResourceFromWikipediaPageTitle("Erich Maria Remarque","pt")
List answer = wr.format(q, "pt")

answer?.each{it -> println it.line}
		// map with keys line, boolAnswer and boolJust
	//	println ""+it.boolAnswer+" "+it.boolJust+" "+it.line
answer2?.each{it -> println it.line}

	