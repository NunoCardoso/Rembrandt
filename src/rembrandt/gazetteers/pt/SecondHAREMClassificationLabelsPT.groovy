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

package rembrandt.gazetteers.pt

import rembrandt.gazetteers.SemanticClassificationDefinitions

/**
 * @author Nuno Cardoso
 * 
 * Gives the proper Portuguese labels for Second HAREM's classification 
 */
class SecondHAREMClassificationLabelsPT extends SemanticClassificationDefinitions {
   
  static final Map<String,String> label = [
	"@EM":"EM", 
	"@PESSOA":"PESSOA", 
	"@LOCAL":"LOCAL", 
	"@ORGANIZACAO":"ORGANIZACAO",
	"@OBRA":"OBRA", 
	"@ACONTECIMENTO":"ACONTECIMENTO", 
	"@COISA":"COISA", 
	"@TEMPO":"TEMPO",
	"@VALOR":"VALOR", 
	"@NUMERO":"NUMERO", 
	"@ABSTRACCAO":"ABSTRACCAO",

	"@INDIVIDUAL":"INDIVIDUAL", 
	"@CARGO":"CARGO", 
	"@GRUPOIND":"GRUPOIND",
	"@GRUPOCARGO":"GRUPOCARGO", 
	"@MEMBRO":"MEMBRO", 
	"@MEMBROGRUPO":"GRUPOMEMBRO",
	"@POVO":"POVO",
	"@FISICO":"FISICO", 
	"@HUMANO":"HUMANO", 
	"@VIRTUAL":"VIRTUAL",
	"@ADMINISTRACAO":"ADMINISTRACAO", 
	"@INSTITUICAO":"INSTITUICAO", 
	"@EMPRESA":"EMPRESA", 
	"@EVENTO":"EVENTO", 
	"@EFEMERIDE":"EFEMERIDE", 
	"@ORGANIZADO":"ORGANIZADO",
	"@CLASSE":"CLASSE", 
	"@MEMBROCLASSE":"MEMBROCLASSE", 
	"@OBJECTO":"OBJECTO",
	"@SUBSTANCIA":"SUBSTANCIA",
	"@DISCIPLINA":"DISCIPLINA", 
	"@ESTADO":"ESTADO", 
	"@IDEIA":"IDEIA", 
	"@NOME":"NOME",
	"@ARTE":"ARTE",
	"@PLANO":"PLANO",
	"@REPRODUZIDA":"REPRODUZIDA",
	"@GENERICO":"GENERICO", 
	"@DURACAO":"DURACAO", 
	"@FREQUENCIA":"FREQUENCIA",
	"@TEMPO_CALEND":"TEMPO_CALEND",
	"@MOEDA":"MOEDA", 
	"@QUANTIDADE":"QUANTIDADE", 
	"@CLASSIFICACAO":"CLASSIFICACAO",
	"@ORDINAL":"ORDINAL", 
	"@TEXTUAL":"TEXTUAL", 
	"@NUMERAL":"NUMERAL", 
	"@NUMERO":"NUMERO",
	"@OUTRO":"OUTRO",
	
	"@ILHA":"ILHA", 
	"@AGUACURSO":"AGUACURSO", 
	"@AGUAMASSA":"AGUAMASSA",
	"@RELEVO":"RELEVO", 
	"@MONTANHA":"MONTANHA",
	"@HUMANOREGIAO":"REGIAO", 
	"@FISICOREGIAO":"REGIAO", 
	"@PLANETA":"PLANETA",
	"@CONSTRUCAO":"CONSTRUCAO", 
	"@DIVISAO":"DIVISAO", 
	"@RUA":"RUA",
	"@PAIS":"PAIS",
	"@COMSOCIAL":"COMSOCIAL", 
	"@SITIO":"SITIO", 
	"@OBRARTIGO":"OBRA",
    	"@SUB":"SUB",
    	"@HORA":"HORA", 
    	"@INTERVALO":"INTERVALO",
    	"@DATA":"DATA"
	]
}