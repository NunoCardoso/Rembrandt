/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */
package rembrandt.gazetteers.en

import rembrandt.gazetteers.SemanticClassificationDefinitions

/**
 * @author Nuno Cardoso
 * This class maps semantic classification labels from Second HAREM to the English tag labels 
 */
class SecondHAREMClassificationLabelsEN extends SemanticClassificationDefinitions {
 
       static final Map<String,String> label = [
     	"@EM":"NE", 
     	"@PESSOA":"PERSON", 
     	"@LOCAL":"PLACE", 
     	"@ORGANIZACAO":"ORGANIZATION",
     	"@OBRA":"MASTERPIECE", 
     	"@COISA":"THING", 
     	"@ACONTECIMENTO":"EVENT", 
     	"@TEMPO":"TIME",
     	"@VALOR":"VALUE", 
     	"@NUMERO":"NUMBER", 
     	"@ABSTRACCAO":"ABSTRACTION",
 
     	"@INDIVIDUAL":"INDIVIDUAL", 
     	"@CARGO":"POSITION", 
     	"@GRUPOIND":"INDIVIDUALGROUP",
     	"@GRUPOCARGO":"POSITIONGROUP", 
     	"@MEMBRO":"MEMBER", 
     	"@MEMBROGRUPO":"MEMBERGROUP",
     	"@POVO":"PEOPLE",
     	"@FISICO":"PHYSICAL", 
     	"@HUMANO":"HUMAN", 
     	"@VIRTUAL":"VIRTUAL",
     	"@ADMINISTRACAO":"ADMINISTRATION", 
     	"@INSTITUICAO":"INSTITUTION", 
     	"@EVENTO":"PASTEVENT", 
     	"@EMPRESA":"COMPANY", 
     	"@EFEMERIDE":"HAPPENING", 
     	"@ORGANIZADO":"ORGANIZED",
     	"@CLASSE":"CLASS", 
     	"@MEMBROCLASSE":"CLASSMEMBER", 
     	"@OBJECTO":"OBJECT",
     	"@SUBSTANCIA":"SUBSTANCE",
     	"@DISCIPLINA":"DISCIPLINE", 
     	"@ESTADO":"STATE", 
     	"@IDEIA":"IDEA", 
     	"@NOME":"NAME",
     	"@ARTE":"WORKOFART",
     	"@PLANO":"PLAN",
     	"@REPRODUZIDA":"REPRODUCED",
     	"@GENERICO":"GENERIC", 
     	"@DURACAO":"DURATION", 
     	"@FREQUENCIA":"FREQUENCY",
     	"@TEMPO_CALEND":"CALENDAR",
     	"@MOEDA":"CURRENCY", 
     	"@QUANTIDADE":"QUANTITY", 
     	"@CLASSIFICACAO":"CLASSIFICATION",
     	"@ORDINAL":"ORDINAL", 
     	"@TEXTUAL":"TEXTUAL", 
     	"@NUMERAL":"NUMERAL", 
     	"@NUMERO":"NUMBER",
     	"@OUTRO":"OTHER",
     	
     	"@ILHA":"ISLAND", 
     	"@AGUACURSO":"WATERCOURSE", 
     	"@AGUAMASSA":"WATERMASS",
     	"@RELEVO":"RELEVO", 
     	"@MONTANHA":"MOUNTAIN",
     	"@FISICOREGIAO":"REGION", 
    	"@HUMANOREGIAO":"REGION", 
     	"@PLANETA":"PLANET",
     	"@CONSTRUCAO":"CONSTRUCTION", 
     	"@DIVISAO":"DIVISION", 
     	"@RUA":"STREET",
     	"@PAIS":"COUNTRY",
     	"@COMSOCIAL":"MEDIA", 
     	"@SITIO":"SITE", 
     	"@OBRAARTIGO":"ARTICLE",
        "@SUB":"SUB",
        "@HORA":"HOUR", 
        "@INTERVALO":"INTERVAL",
        "@DATA":"DATE"
     	]
 }
