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

package rembrandt.obj

import java.util.HashMap
import java.util.regex.Pattern

import org.apache.log4j.Logger

/**
 * @author Nuno Cardoso
 * 
 * The DocumentIndex class mantains an index of terms, pointing to the sentence and term index.
 */
public class DocumentIndex {

	HashMap<String,List<Integer>> index
	static Logger log = Logger.getLogger("Document")

	/** add the given sentences to a HashMap position index and a RAMDirectory Lucene term index
	 * @param sentences The sentences to parse and add to the index
	 */
	void indexSentences(List<Sentence> sentences) {

		index = [:]
		log.trace "DocumentIndex: Got ${sentences.size()} sentences to index."
		sentences.each{s ->
			s.each{t ->
				// adds term to this sentence-term index
				if (!t.hidden) {
					List key = index[t.text]
					if (key) index[t.text] << [s.index, t.index]
					else index[t.text] = [[s.index, t.index]]
				}
			}
		}
	}

	/**
	 * Get a list of indexes for sentence-term where a term occurs.
	 * @param key the string representation of the term (normally, the text)
	 * @return List of indexes, or null if not found.
	 */
	public List<List<Integer>> getIndexesForTerm(String key) {
		return index[key]
	}


	public String dump() {
		StringBuffer s = new StringBuffer()
		index.each{key, value -> s.append "$key: $value\n"}
		return s.toString()
	}
	/**
	 * Get a Map of terms -> indexes for sentence-term for a given pattern.
	 */
	public Map<String,List<Integer>> getIndexesForPattern(Pattern pattern) {
		Map res = [:]
		List<String> list =  getTermsForPattern(pattern)
		list.each{l -> res[l] = getIndexesForTerm(l)  }
		return res
	}

	/**
	 * Get a List indexes for sentence-term for a given pattern.
	 * This joins all indexes into one single list.
	 */
	public List<List<Integer>> getJointIndexesForPattern(Pattern pattern) {
		List res = []
		List<String> list =  getTermsForPattern(pattern)
		list.each{l ->  getIndexesForTerm(l).each{res << it}  }
		// sort by sentence index, then by term index
		if (res) log.trace "Indexes for pattern $pattern are $res"

		return res.sort({a, b -> ((a[0] != b[0])? a[0] <=> b[0] : a[1] <=> b[1])})
	}

	/**
	 * Get a Map of subconcepts -> indexes for sentence-term for a given pattern.
	 */
	public Map<Object,List<Integer>> getSubConceptsAndIndexesForConcept(List concepts) {

		Map res = [:]

		concepts.each{c ->

			List l
			if (c instanceof String) {
				// list of indexes for this simple term
				l = getIndexesForTerm(c)
				if (l) res[c] = l
			}

			if (c instanceof Pattern) {
				// list of joint indexes from the terms matched by the pattern
				l = getJointIndexesForPattern(c)
				if (l) res[c] = l
			}


			if (c instanceof List) {
				// For a list, let's try only the first element, which is only a String or a Pattern.

				if (c[0] instanceof String) {
					// list of indexes for this simple term
					l = getIndexesForTerm(c[0])
					if (l) res[c] = l
				}

				if (c[0] instanceof Pattern) {
					// list of indexes for this simple term
					l =  getJointIndexesForPattern(c[0])
					if (l) res[c] = l
				}
			}
		}
		return res
	}

	/**
	 * same as concepts, but we will look on the needle key, and return the map including the answer
	 */
	public Map<Object,List<Integer>> getSubMeaningsAndIndexesForMeaning(List<Map> meanings) {

		Map res = [:]

		meanings.each{meaning ->

			meaning.needle.each{c ->

				List l
				if (c instanceof String) {
					// list of indexes for this simple term
					l = getIndexesForTerm(c)
					if (l) res[meaning] = l
				}

				if (c instanceof Pattern) {
					// list of joint indexes from the terms matched by the pattern
					l = getJointIndexesForPattern(c)
					if (l) res[meaning] = l
				}


				if (c instanceof List) {
					// For a list, let's try only the first element, which is only a String or a Pattern.

					if (c[0] instanceof String) {
						// list of indexes for this simple term
						l = getIndexesForTerm(c[0])
						if (l) res[meaning] = l
					}

					if (c[0] instanceof Pattern) {
						// list of indexes for this simple term
						l =  getJointIndexesForPattern(c[0])
						if (l) res[meaning] = l
					}
				}
			}
		}
		return res
	}


	/**
	 * as getSubConceptsAndIndexesForConcept generates a Map<subconcept,List<index>> for a list<subconcept>,
	 * this static method will generate a Map<index>,List<subconcept>
	 * Refer to the JUnit test to better understand this index inversion  
	 */
	static Map invertConceptIndex(Map map) {

		Map res = [:]
		map.each{subconcept, indexes ->
			indexes.each{index ->
				if (res.containsKey(index)) res[index].add(subconcept)
				else res[index] = [subconcept]
			}
		}
		return res
	}

	/**
	 * same as invertConceptIndex, but using Meanings
	 */
	static Map invertMeaningsIndex(Map map) {

		Map res = [:]
		map.each{meaning, indexes ->
			indexes.each{index ->
				if (res.containsKey(index)) res[index].add(meaning)
				else res[index] = [meaning]
			}
		}
		return res
	}

	/**
	 * The inverted concept index, generated by the  static Map invertConceptIndex(Map map) method,
	 * has all sentence/term indexes as the hash key. This method sorts and uniques those keys
	 */
	static List sortKeysOfInvertedConceptIndex(Map map) {
		return  map.keySet().unique().sort({a, b ->
			((a[0] != b[0])? a[0] <=> b[0] : a[1] <=> b[1])}).toList()

	}

	/**
	 * Same with sortKeysOfInvertedConceptIndex, but with meanings
	 */
	static List sortKeysOfInvertedMeaningsIndex(Map map) {
		return  map.keySet().unique().sort({a, b ->
			((a[0] != b[0])? a[0] <=> b[0] : a[1] <=> b[1])}).toList()

	}

	/**
	 * Get the terms on the document index that match the pattern
	 * @pattern the pattern to match
	 * @return A list of terms in String format
	 */
	public List<String> getTermsForPattern(Pattern pattern) {
		if (!pattern) return null

		List<String> terms
		terms = index.keySet().grep(pattern).toList().sort(
				{a,b -> (index[a][0][0] != index[b][0][0]) ? index[a][0][0] <=> index[b][0][0] : index[a][0][1] <=> index[b][0][1]} )
		if (terms) log.debug "For pattern "+pattern.pattern()+" I've got terms: $terms"
		return terms
	}
}
