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
package saskia.db

/**
 * @author Nuno Cardoso
 * Document status - controls permissions for documents. 
 * It can be divided in:
 * <UL>Readiness to be processed: 
 * <li>READY (OK): Document is good to be processed.</li>
 * <li>NOT_READY (KO): Document is not good to be processed (dirty mode).</li>
 * <li>TOO_BIG (TB): Document is too big for REMBRANDT, skip it.</li>
 * </UL>
 * 
 * <UL>Sync status to the pool: 
 * <li>SYNCED (OS): Document is synced to the NE Pool.</li>
 * <li>NOT_SYNCED (KS): Document is not synced to the NE Pool.</li>
 * </UL>
 * 
 * <UL>Processing status: 
 * <li>UNLOCKED (UL): Document is unlocked, can be used/selected.</li>
 * <li>LOCKED (LK): Document is locked, it's being used now.</li>
 * <li>QUEUED (QU): Document is marked as queued, will be processed in a thread.</li>
 * </UL>
 */
enum DocStatus {
	
///////// Doc processing status

	/** Document OK to be processed, ex: sync the NE pool.
	 *  See it as a virgin document - new or updated. Screaming to be used on following process. 
	 */
	READY("OK"),

	/** Document is not OK to be processed.
	 *  See it as a virgin failed doc
	 */
	NOT_READY("KO"),
    
	/** This one tried to be tagged, and failed */
        FAILED("FA"),
    
	/** Document is not OK to be processed. It's too big for REMBRANDT
	 */
	TOO_BIG("TB"),

	/** Document is a Wikipedia document that's not relevant enougth yet to be processed. 
	 */
	WIKIPEDIA_DOC_NOT_RELEVANT("NR"),
	
	
//////// Doc synchronized status

	/** Document synced with the NE pool.
	 * - Sync to NEPool is pointless (you have to change either the pool or the source doc)
	 * - Sync from NEPool is also pointless
	 */
	SYNCED("SO"),

	/** Document not synced with the NE pool - doc has changes.
	 * - Sync to NEPool is allowed.
	 * - Sync from NEPool is NOT allowed.
	 */
	NOT_SYNCED_DOC_CHANGED("SD"),
	
	/** Document not synced with the NE pool. NE pool has changes.
	 * - Sync to NEPool is NOT allowed.
	 * - Sync from NEPool is allowed.
	 */	
	NOT_SYNCED_NEPOOL_CHANGED("SN"),


///////// Doc edit status

	/** 
	 * Document is unlocked, can be used/selected. 
	 */
	UNLOCKED("UL"),

	/** 
	 * Document is marked for a queue processing, it's not recommended to be selected.
	 */
	QUEUED("QU"),

	/** 
	 * Document is locked, avoid any changes in it. 
	 */
	LOCKED("LK")
	
	private final String text 
	
	static DocStatus getFromValue(String text) {
	   return values().find{it.text == text}
	}

	static DocStatus getFromKey(String text) {
	    return values().find{it.name() == text}
	}

	DocStatus(String text) {  this.text = text	}
	  
	/* tests*/
	boolean isLocked() { return (text == "LK" || text == "QU") }	
	boolean isQueued() { return (text == "QU") }
	boolean isUnlocked() { return (text == "UL") }
	
	boolean isGoodToProcess() {return (text == "OK") }
	boolean isMarkedAsBad() { return (text != "OK") }
	
	boolean isGoodForSyncingNEPool() { return (text == "SD") }
	boolean isSynced() {   return (text == "SO") }
	boolean isGoodToBeSyncedFromNEPool() {   return (text == "SN") }
	
	/* MySQL where conditions */
	static String whereConditionGoodToProcess() {return "('OK')"}
	static String whereConditionUnlocked() {return "('UL')"}
	static String whereConditionGoodToSyncNEPool() {return "('SD')"}
	static String whereConditionGoodToSyncFromNEPool() {return "('SN')"}
	static String whereConditionSynced() {return "('SO')"}
	
	public String text() {return this.text}
	
	public String toString() { return name() }
}