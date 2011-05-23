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

import rembrandt.obj.Clause
import java.util.regex.Pattern

/**
 * @author Nuno Cardoso
 * This class stores gazetteers for ORGANIZACAO category.
 */
class OrganizationGazetteerPT {
     
     /** LISTS */

   static final Pattern empresasSuffix = ~/(?:[Ss]\.?[Aa]\.?|[Ii]nc\.?|[Ll]da\.?)/
	     
     /** CLAUSES */
     
   static final Clause empresasSuffix1c = Clause.newRegex1Clause(empresasSuffix, "empresas suffix")    
   
   static final Clause empresaClause1nc = Clause.newPlain1Clause("empresa", "empresa",false)

   static final Clause ministroClause1nc = Clause.newRegex1Clause(~/ministr[oa]s?/, "ministr[oa]s?",false)

 }