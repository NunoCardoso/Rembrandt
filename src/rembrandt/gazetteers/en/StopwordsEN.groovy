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

/**
 * @author Nuno Cardoso
 * List of irrelevant word for English, that when appearing ALONE capitalized, are not an NE
 */
class StopwordsEN {
    
  static final List<String> stopwordNEs = [
 'A','As','An','Above','After','Also','All','Are','And','At','Anyone','Anybody','Anywhere','About',
 'Before', 'But', 'Below','Because','By',
 'Despite',
 'Each','Even',
 'Few','From','For',
 'Go',
 'Has','Have','His','Her','He','How','Here',
 'If','In','It','I','Is',
 'Just',
 'My','May','Maybe','More','Most','Many',
 'Not','No','Now','Nor',
 'On','Only','Of','Our','Oh','OK','Other','Others',
 'Perhaps',
 'Right',
 'Since','So','Some','Says','She','Source','Sources',
 'The','They','These','There','Those','Their','That','Then','This',
 'Until','Us','Under',
 'We','Will','Why','Who','When','Where','Were','Was','With','While','With','Whenever',
 'Wherever','Whether','What','Why','Without',
 'You','Yes','Your','Yours','Yet'
 ] 
  
 static final List<String> beginningStopwordList = ['In','The']
 
}