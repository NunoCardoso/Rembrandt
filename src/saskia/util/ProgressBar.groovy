/* Rembrandt
 * Copyright (C) 2008 Nuno Cardoso. ncardoso@xldb.di.fc.ul.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
 
package saskia.util

/**
 * @author Nuno Cardoso
 * Generates a progress bar when indexing Wikipedia.
 * It requires a progressbar.sh shell script.
 */
class ProgressBar {
	
	 def progressbarsh = "/usr/bin/progressbar.sh"
	 def progressbar  = false 
	 
	 def int totalvalue = 0
	 def int partialvalue = 0	 
	 def int lastpercent = 0
	 def int currentpercent = 0
	 def String fullChar = "*"
	 def String emptyChar = " "	 
	 def String pipeChar = "|" 
	 def int numColumns = 80
	  
	 /**
	  * Set total value of the measurement.
	  * @param i The total value
	  */
	 public setTotalvalue(int i) {
		 this.totalvalue = i
	 }
	 
	 /**
	  * Set total value of the measuremente. Converts String to int.
	  * @param s the total value.
	  */
	 public setTotalvalue(String s) {
		 setTotalvalue(Integer.parseInt(s))
	 }
	 
	 /**
	  * Increment the value of the progress bar
	  * @param value The amount to increase. Default is 1
	  */
	 public incrementValue(int value = 1) {
		 partialvalue+= value
		 currentpercent = (int)(partialvalue*100/totalvalue)
	}
	
	/**
	 * Check if the percent value changed.
	 */  
	 public boolean updatedPercent() {
		 return currentpercent != lastpercent		 
	 }
	
	/** print progress bar if there is a change. */
	 public void printProgressBar()	{
	     if (pb.updatedPercent()) print pb.execProgressBar() 
	 }
	 
	/**
	 * Print a progress bar.
	 * Code adapted from ProgressBar bash script from 
	 * Sune Vuorela <pusling@pusling.com>, Version 0.9
	 */
	 public String execProgressBar() {	
	    
		 lastpercent = currentpercent	
		 def res = ""
		 def left = numColumns*100/20
		 def barLength =  ((numColumns*100) - (2*left - 200))
		 def dot = barLength/100
		 def percent = lastpercent*dot
		 
		 def numchars = 0
		 for (int i=0;i<left;i+=100) {res += " "}
		 res += pipeChar
		 for (int i=0;i<percent;i+=100) {res += fullChar; numchars++}
		 for (int i=percent;(i < barLength && numchars < barLength/100 );i+=100) {res += emptyChar; numchars++}
		 res += pipeChar+" "+lastpercent+"%\r"
		 return res
		 //return "$progressbarsh $currentpercent".execute().text.trim()+" "+currentpercent+"%\r"	  
	 }
	
	/**
	 * reset progress bar values.
	 */
	 public reset() {
		totalvalue = 0
		partialvalue = 0
		lastpercent = 0.
		currentpercent = 0
	 }
}