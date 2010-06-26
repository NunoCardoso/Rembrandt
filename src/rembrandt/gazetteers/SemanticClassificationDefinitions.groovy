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

package rembrandt.gazetteers

/**
 * @author Nuno Cardoso
 * This class defines the whole range of category / type / subtypes that REMBRANDT should 
 * use to tag. Note that since REMBRANDT was born in HAREM, its current range equals HAREM's
 * tagging range, but if one can add more detailed instances (ex: doctors, architects as 
 * subtypes of INDIVIDUAL) one can do here, and the respective converters will fit to the proper 
 * TagStyle writing. 
 */
class SemanticClassificationDefinitions {
    
   static final unknown = "@EM"
   static final Map<String,String> netag = ['pt':"EM", 'en':"NE"]
         
                                            
   static final category = [person:"@PESSOA", place:"@LOCAL", organization:"@ORGANIZACAO",
	masterpiece:"@OBRA", event:"@ACONTECIMENTO", thing:"@COISA", time:"@TEMPO",
	value:"@VALOR", number:"@NUMERO", abstraction:"@ABSTRACCAO"]
	
   static final type = [
  //PERSON
     individual:"@INDIVIDUAL", position:"@CARGO", individualgroup:"@GRUPOIND",
     positiongroup:"@GRUPOCARGO", member:"@MEMBRO", membergroup:"@GRUPOMEMBRO",
     people:"@POVO",
  //LOCAL
     physical:"@FISICO", human:"@HUMANO", virtual:"@VIRTUAL",
  //ORGANIZATION
     administration:"@ADMINISTRACAO", institution:"@INSTITUICAO", 
     company:"@EMPRESA", 
  //EVENT
     pastevent:"@EVENTO", happening:"@EFEMERIDE", organized:"@ORGANIZADO",
   //THING
     'class':"@CLASSE", memberclass:"@MEMBROCLASSE", object:"@OBJECTO",
     substance:"@SUBSTANCIA",
   //ABSTRACTION
     discipline:"@DISCIPLINA", state:"@ESTADO", idea:"@IDEIA", name:"@NOME",
   //MASTERPIECE
     workofart:"@ARTE",plan:"@PLANO",reproduced:"@REPRODUZIDA",
   //TIME
     generic:"@GENERICO", duration:"@DURACAO", frequency:"@FREQUENCIA",
     calendar:"@TEMPO_CALEND",
   //VALUE
     currency:"@MOEDA", quantity:"@QUANTIDADE", classification:"@CLASSIFICACAO",
  //	NUMBER
     ordinal:"@ORDINAL", textual:"@TEXTUAL", numeral:"@NUMERAL", number:"@NUMERO",
     other:"@OUTRO"
   ]
	
   static final subtype = [
// LOCAL PHYSICAL
      island:"@ILHA", watercourse:"@AGUACURSO", watermass:"@AGUAMASSA",
      mountain:"@RELEVO", physicalregion:"@FISICOREGIAO", planet:"@PLANETA",
// LOCAL HUMAN
      construction:"@CONSTRUCAO", division:"@DIVISAO", street:"@RUA", country:"@PAIS", humanregion:"@HUMANOREGIAO", 
  // LOCAL VIRTUAL
      media:"@COMSOCIAL", site:"@SITIO", article:"@OBRAARTIGO",
      //ORGANIZATION 
      sub:"@SUB",
      // TIME CALENDAR
      hour:"@HORA", interval:"@INTERVALO", date:"@DATA",
      other:"@OUTRO"
   ]
}