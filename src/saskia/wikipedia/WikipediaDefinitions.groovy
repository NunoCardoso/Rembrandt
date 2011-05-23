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
 
package saskia.wikipedia

/**
 * @author Nuno Cardoso
 * Language settings for Wikipedia keywords
 */
class WikipediaDefinitions {
	
     /** Disambiguation string, with upper case */
     public static disambiguationUString = [en:"Disambiguation", pt:"Desambiguação"]

     public static disambiguationLString = [en:"disambiguation", pt:"desambiguação"]
 
     /** Category page */
     public static categoryString = [en:"Category", pt:"Categoria", de:"Vorlage",
	  it:"Categoria", es:"Categoría", nl:"Categorie", no:"Kategori",
	  ro:"Categorie", bg:"\u2013\u00f6\u2013\u221e\u2014\u00c7\u2013\u00b5\u2013\u2265\u2013\u00e6\u2014\u00c4\u2013\u220f\u2014\u00e8", nn:"Kategori"]
    
	 public static imageString = [en:"Image", de:"Bild", pt:"Imagem",
	  it:"Immagine", es:"Imagen", nl:"Afbeelding", no:"Bilde",
	  ro:"Imagine", bg:"\u2013\u00f6\u2013\u221e\u2014\u00c4\u2014\u00c7\u2013\u220f\u2013\u03a9\u2013\u222b\u2013\u221e", nn:"Fil"	 ]
	
	 public static templateString = [en:"Template", de:"Vorlage", pt:"Predefinição",
	  it:"Template", es:"Plantilla", nl:"Sjabloon", no:"Mal",
	  ro:"Format", bg:"\u2013\u00ae\u2013\u221e\u2013\u00b1\u2013\u00aa\u2013\u00e6\u2013\u03a9", nn:"Mal" ]
}