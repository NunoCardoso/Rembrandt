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

import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * List of common patterns for Portuguese, that are widely used among clauses, and thus can be recicled
 */
class PatternsPT {
       
    static final Pattern ao = ~/[ao]/ 
    static final Pattern aos = ~/[ao]s/ 
    static final Pattern aos_ = ~/[ao]s?/ 
    static final Pattern aopor = ~/(?:ao|por)/	    
    static final Pattern aouma = ~/(?:[ao]|uma?)/
    static final Pattern daeo = ~/d[aeo]/
    static final Pattern daeos_ = ~/d[aeo]s?/
    static final Pattern dnaeosem = ~/(?:[dn][aeo]s?|em)/
    static final Pattern designacaonome = ~/(?:nome|designação)/
    static final Pattern efoisao = ~/(?:é|foi|são)/
    static final Pattern Eem = ~/[Ee]m/
    static final Pattern emdnaeo = ~/(?:em|[dn][eao])/
    static final Pattern emnao = ~/(?:em|n[ao])/
    static final Pattern emnaos = ~/(?:em|n[ao]s?)/
    static final Pattern porSlash = ~/(?:por|\/)/
}