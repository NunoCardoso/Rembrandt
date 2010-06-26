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
package renoir.suggestion

import renoir.obj.QuestionType

/**
 * @author Nuno Cardoso
 * Just a gazetteer for the Suggestions
 *
 */
class SuggestionGazetteer {
    
    static Map whereQuestions = [
    "pt":["Quando":QuestionType.When, "Quanto":QuestionType.HowMuch,
            "Quantas":QuestionType.HowMuch,"Quantos":QuestionType.HowMuch,
            "Como se chama":QuestionType.What, "Qual":QuestionType.Which, 
            "Quais":QuestionType.Which, "Quem":QuestionType.Who, "Onde":QuestionType.Where, 
            "Em que":QuestionType.Which, "Que":QuestionType.Which, "Porque":QuestionType.Why,
            "Como":QuestionType.How],
    "en":["When":QuestionType.When, "How Much":QuestionType.HowMuch,
            "How Many":QuestionType.HowMuch, "Which":QuestionType.Which, 
            "Who":QuestionType.Who, "Where":QuestionType.Where, 
            "Why":QuestionType.Why, "How":QuestionType.How]        
     ]
    
    static Map predicates = [
     "pt":["nasceu", "morreu", "fica", "fica situado"],
     "en":["born", "died", "located"]
    ]
    
    static Map operators = [
       "pt":["maior que", "menor que"],
       "en":["bigger than", "smaller than"]
    ]
}