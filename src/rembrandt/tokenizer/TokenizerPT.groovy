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
package rembrandt.tokenizer

import org.apache.log4j.*
import rembrandt.obj.Sentence
import rembrandt.obj.Term
import rembrandt.obj.Document
import rembrandt.io.RembrandtWriter
import rembrandt.io.RembrandtStyleTag
/**
 * Inspired from Linguateca Tokenizer, it's now heavily changed but hopefully much well organized
 */
class TokenizerPT {

	static TokenizerPT _this = null
	static Logger log = Logger.getLogger("Tokenizer")
	static Level level = log.getLevel()
	static boolean debug = ((level == Level.DEBUG) || (level == Level.TRACE) ? true : false)

	static String uppercaseAlpha = "[A-ZÁÀÃÂÄÅÆÉÈÊÍÖÓÒÔÕØÚÜÇ]"
	static String lowercaseAlpha = "[a-záàâãäåæéèêíöóòôõøúüç]"
	static String notUppercaseAlpha = "[^A-ZÁÀÃÂÄÅÆÉÈÊÍÖÓÒÔÕØÚÜÇ]"
	static String notLowercaseAlpha = "[^a-záàâãäåæéèêíöóòôõøúüç]"
	static String anyAlpha = "[A-ZÁÀÃÂÄÅÆÉÈÊÍÖÓÒÔÕØÚÜÇa-záàâãäåæéèêíöóòôõøúüç]"
	static String anyAlphaOrDot = "[A-ZÁÀÃÂÄÅÆÉÈÊÍÖÓÒÔÕØÚÜÇa-záàâãäåæéèêíöóòôõøúüç\\.]"
	static String anyAlphaNum = "[A-Z0-9ÁÀÃÂÄÅÆÉÈÊÍÖÓÒÔÕØÚÜÇa-záàâãäåæéèêíöóòôõøúüç]"
	static String anyAlphaNumWithExtras = "[A-Z0-9ÁÀÃÂÄÅÆÉÈÊÍÖÓÒÔÕØÚÜÇa-záàâãäåæéèêíöóòôõøúüç\\-_\\.]"
	static String quotationLeft = "[«‘“`]"
	static String quotationRight = "[»’”´]"

	//  [\w_.-]+ \@ [\w_.-]+\w                    # emails
	//  \w+\.?[ºª°]\.?                            # ordinals
	//  ((https?|ftp|gopher)://|www)[\w_./~:-]+\w # urls

	// other symbols: † ‡ √  © ® ™ ∞ • ◊ ı
	// operators:÷ ≠ ‰ ± ≈ ≤ ≥ ‹ › · – ⁄ ¬
	// currency: € £ ¥ ¢
	// punctuation: ¡ § ¿ ¶ … ¸ ‚ ˛
	// quotations: simple: "'`´ complex: “ ” « ¨ ‘ ’
	// greek: 	∑ Ø ∏ ß ∂ Ω ∫ µ π
	// accents: ˜ ˙ ˇ ¯	ˆ ˚ ˚ ˝ °

	// reminder: (?s) makes the '.' regex operator match newlines. (?i) makes case insensitive.
	// (?x) allows whitespace and # as comment. The (?m) makes ^ and $ match around line terminators also, not only the srting ends.

	public TokenizerPT newInstance() {
		if (!_this) _this = new TokenizerPT()
		return _this
	}

	private List linguatecaTokenize(String t) {

		if (!t) return null

		/***************************
		 *  Início do tokenizeOslo *
		 ***************************/

		/****  Proteger :
		 '...' por '†', 
		 '.' por '‡', 
		 '?' por '◊', 
		 '!' por 'ı', 
		 '/' por §
		 '//' por ∂
		 ',' por ∞
		 ':' por •
		 'quebra de frase' por ¶
		 *******/

		t = t.replaceAll(/†/,"††") // proteger o próprio †
		t = t.replaceAll(/(?<!\.)\.\.\.(?!\.)/,"†")
		t = t.replaceAll(/‡/,"‡‡") // proteger o próprio ‡
		t = t.replaceAll(/◊/,"◊◊") // proteger o próprio ◊
		t = t.replaceAll(/ı/,"ıı") // proteger o próprio ı
		t = t.replaceAll(/§/,"§§") // proteger o próprio §
		t = t.replaceAll(/∂/,"∂∂") // proteger o próprio §
		t = t.replaceAll(/∞/,"∞∞") // proteger o próprio ∞
		t = t.replaceAll(/•/,"••") // proteger o próprio •
		t = t.replaceAll(/¶/,"¶¶") // proteger o próprio ¶

		/*** typical closures. x marks the dagger, w marks a whitespace */
		Closure g1x = {all, g1 -> "${g1}‡"}
		Closure g1w = {all, g1 -> " ${g1} "}
		Closure xg1 = {all, g1 -> "‡${g1}"}
		Closure wg1 = {all, g1 -> " ${g1}"}
		Closure wg1x = {all, g1 -> " ${g1}‡"}
		Closure g1xw = {all, g1 -> "${g1}‡ "}
		Closure wxg1 = {all, g1 -> " ‡${g1}"}
		Closure wg1xw = {all, g1 -> " ${g1}‡ "}
		Closure g1g2 = {all, g1, g2 -> "${g1}${g2}"}
		Closure g1xg2 = {all, g1, g2 -> "${g1}‡${g2}"}
		Closure g1wg2 = {all, g1, g2 -> "${g1} ${g2}"}
		Closure g1xwg2 = {all, g1, g2 -> "${g1}‡ ${g2}"}
		Closure g1g2g3 = {all, g1, g2, g3 -> "${g1}${g2}${g3}"}
		Closure g1g2wg3 = {all, g1, g2, g3 -> "${g1}${g2} ${g3}"}

		// Estratégia: alargar aspas, pelicas e parênteses, depois proteger átomos com .,:!?/ que não são quebras (claro, o '.' tem protagonismo)
		// Já no final, quando só sobrar ?!.etc que são quebras, proteger os restantes '.' e adicionar pontos finais como marcas de quebras.
		// depois recuperar as protecções.

		/*********************
		 * 0.1 URLs e e-mails *
		 *********************/

		if (debug) log.debug "0 > URL etc T: $t".trim()

		if (debug) log.trace "0.1 > URL T: $t".trim()


		// email= url@url...
		// aceito endereços seguidos de /~?hgdha/hdga.html ou /?asp=0&x=1&...
		// seguidos de /~hgdha/hdga.html
		// TEM DE SER ANTES DO PROCESSADOR DE BARRAS

		// this URL pattern has no collecters
		// coloquei [a-z0-9_-][a-z0-9_-][a-z0-9_-]+\.) no primeiro (tipo www) para evitar que encontre op.cit.
		t = t.replaceAll(/(?xi)  (?:(?:https?|ftp):\/\/)? \
        	(?:[a-z0-9_-][a-z0-9_-][a-z0-9_-]+\.) \
        	(?:[a-z0-9_-][a-z0-9_-]+\.)+  \
        	(?:[a-z]{2,4}) \
        	(?:\/~?[a-z0-9][a-z0-9.-]+)*  \
        	(?:\?[a-z]+=[a-z0-9-]+  \
        	(?:\&[a-z]+=[a-z0-9-]+)*  )*  /) {all ->
					all = all.replaceAll(/\./,"‡")
					all = all.replaceAll(/\/\//,"∂")
					all = all.replaceAll(/\//,"§")
					all = all.replaceAll(/\?/,"◊")
					all = all.replaceAll(/:/,'•')
					all = all.replaceAll(/‡$/, ".") // last one is a dot
					return all
				}

		if (debug) log.trace "0.2 > mail T: $t".trim()

		// emails
		t = t.replaceAll(/(?i)(?:[\w_.-]+@[\w_.-]+\w)/) {all ->
			all = all.replaceAll(/\./,"‡")
			all = all.replaceAll(/‡$/, ".") // last one is a dot
			return all
		}

		if (debug) log.trace "0.3 > file T: $t".trim()

		// files
		t = t.replaceAll(/(${anyAlphaNumWithExtras}+?)\.(exe|html?|docx?|pdf|xlsx?|pptx?|eps|ps|tex|txt|rdf|php|java|iso)/,g1xg2)

		/*********************
		 /* 0.4 Separar as aspas e os parêntesis do texto
		 ********************/ 

		if (debug) log.trace "0.4 > quotations T: $t".trim()

		// do one at a time, so that we don't mix different ones.
		t = t.replaceAll(/(«+)/, g1w) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(‘+)/, g1w) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(“+)/, g1w) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(`+)/, g1w) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(»+)/, wg1) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(’+)/, wg1) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(”+)/, wg1) // espaço à direita para aspas da esquerda
		t = t.replaceAll(/(´+)/, wg1) // espaço à direita para aspas da esquerda

		// 0.5 aspas "
		if (debug) log.trace "0.5 > aspas T: $t".trim()

		// aspas da esquerda - dar espaço (no atomizador da Linguateca, subtituí-se por «)
		t = t.replaceAll(/( "+)/, g1w)
		// aspas da direita - dar espaço (no atomizador da Linguateca, subtituí-se por »)
		t = t.replaceAll(/("+ )/, wg1)
		// aspas da esquerda - dar espaço (no atomizador da Linguateca, subtituí-se por «)
		t = t.replaceAll(/(?m)^("+)/, g1w)
		// aspas da direita - dar espaço (no atomizador da Linguateca, subtituí-se por »)
		t = t.replaceAll(/(?m)("+)$/, wg1)
		// aspas seguidos de uma pontuação - a pontuação é provavelmente fora do texto entre aspas. É seguro dar mais um espaço à esquerda, ex: "Tareco".
		// no fundo, é um 0.4, mas com mais cuidado
		t = t.replaceAll(/("+[\.,:;!?])/, wg1)

		// 0.2.2.5 quando plicas fingem que são aspas ('')
		t = t.replaceAll(/( '')/, g1w)
		t = t.replaceAll(/('' )/, wg1)

		// 0.6 apóstrofes (')

		if (debug) log.trace "0.6 > apóstrofe T: $t".trim()

		// dá espaço à esquerda e à direita do ', caso no outro lado tenha espaço, pontuação diversa
		t = t.replaceAll(/(')([\s,:.?!])/, g1wg2)
		t = t.replaceAll(/([\s,:.?!])(')/, g1wg2)
		// expressões inglesas. it's I'ld We'd don't I'll I'm Ain't You're We've
		t = t.replaceAll(/(${anyAlpha})('(?:s|ld|d|nt|ll|m|t|re|ve))\b/, g1wg2)

		// colocar o apóstrofo à esquerda em d' (d'amor, d'Araújo)
		t = t.replaceAll(/\b([Dd]')(${anyAlpha})/, g1wg2)

		// quando os apóstrofos são tipo aspas, como 'pelicas' (nota que ainda estão agarrados)
		// Cuidado com O'Brien's, fora das películas tem de ser \b
		t = t.replaceAll(/(?<=\s\')(${anyAlphaNumWithExtras}+)(?=\'\s)/) {all, g1 -> " ${g1} "}
		// apóstrofes seguidos de uma pontuação - a pontuação é provavelmente fora do texto entre aspas. É seguro dar mais um espaço à esquerda, ex: 'Tareco'.
		// no fundo, é um 0.4, mas com mais cuidado
		t = t.replaceAll(/('+[\.,:;!?])/, wg1)


		// 0.7 parentesis ( )
		if (debug) log.trace "0.7 > parentensis ([]) T: $t".trim()

		// separa o parêntesis esquerdo em todos os casos
		t = t.replaceAll(/(\()/, g1w)
		// separa o parêntesis direito em todos os casos
		t = t.replaceAll(/(\))/, wg1)

		// desfaz a separação dos parênteses para B), a) ou 1)
		t = t.replaceAll(/(?m)^(\s*[0-9A-Za-z]) (\))/, g1g2)
		// desfaz a separação dos parênteses para (a)
		t = t.replaceAll(/(?m)^(\s*\() (\s*[0-9A-Za-z]\))/, g1g2)

		// separa casos como Rocha(1) para Rocha (1)
		t = t.replaceAll(/(${lowercaseAlpha})(\([0-9])/, g1wg2)
		// separa casos como dupla finalidade:1)
		t = t.replaceAll(/:([0-9]\))/) {all, g1 -> " : $g1"}

		// parentesis [ ]

		// separa o parêntesis esquerdo em todos os casos
		t = t.replaceAll(/(\[)/, g1w)
		// separa o parêntesis direito em todos os casos
		t = t.replaceAll(/(\])/, wg1)

		// parentesis { }

		// separa o parêntesis esquerdo em todos os casos
		t = t.replaceAll(/(\{)/, g1w)
		// separa o parêntesis direito em todos os casos
		t = t.replaceAll(/(\})/, wg1)

		// 0.8 reticencias
		if (debug) log.trace "0.8 > reticencias T: $t".trim()

		// dar espaço
		t = t.replaceAll(/(?<!†)†(?!†)/) {" † "}
		// jutar se ( ... ) ou [ ... ]
		// separa o parêntesis recto esquerdo desde que não [...
		t = t.replaceAll(/([\[\(])\s+(†)\s+([\]\)])/, g1g2g3)

		// 0.9 - hífenes -
		if (debug) log.trace "0.9 > hifen T: $t"

		// separa casos como ( Itália )-Juventus para Itália ) -
		t = t.replaceAll(/\)\-(${uppercaseAlpha})/) {all, g1 -> ") - $g1"}
		// separa casos como Portugal-Hungria, F.C.Porto-S.L.Benfica.
		// nota: e Hewlett-Packard? usar o gluer para juntar.

		t = t.replaceAll(/(${uppercaseAlpha}${anyAlphaOrDot}+${lowercaseAlpha})\-(${uppercaseAlpha}${anyAlphaOrDot}+${lowercaseAlpha})/) {all, g1, g2 -> "${g1} - ${g2}"}

		// separa casos como 1-universidade
		// COMENTADO. Estraga coisas como 20-Fev-1997.
		//t = t.replaceAll(/([0-9]\-)([^0-9\s])/, g1wg2)

		// 0.10 - trata das barras /
		if (debug) log.trace "0.10 > barra / T: $t".trim()

		//se houver palavras que nao sao todas em maiusculas, separa
		// nota: não é boa ideia proteger o '/' com caracter especial, senão deixo de poder apanhar palavras com os padrões A-Z de cima.
		t = t.replaceAll(/(?:${lowercaseAlpha}+\/)+(?:${anyAlpha}${lowercaseAlpha}+)/) {all ->
			// cada exp_com_barras é um array[matchedPattern, matchedGroup1, matchedGroup2, etc],
			if (! ((all ==~ /[a-z]+a\/o$/) || (all ==~ /[a-z]+o\/a$/) || (all ==~ /[a-z]+r\/a$/) ) ) {
				// Ambicioso/a            // cozinheira/o     // desenhador/a
				return all.replaceAll(/\//," / ")
			}
		}

		t = t.replaceAll(/\be \/ ou\b/,"e/ou")
		t = t.replaceAll(/\b([Kk])m \/ h/) {all, g1 -> "${g1}m/h"}
		t = t.replaceAll(/\bmg \/ kg/,"mg/kg")
		t = t.replaceAll(/\br \/ c/,"r/c")
		t = t.replaceAll(/\b(r\/c)\./, g1x)
		t = t.replaceAll(/\bm \/ f/, "m/f")
		t = t.replaceAll(/\bf \/ m/, "f/m")

		t = t.replaceAll(/\s+/," ")

		/*******************************
		 /* 1. tratar_pontuacao_interna *
		 *******************************/

		if (debug) log.debug "1.0 > pont.interna T: $t".trim()
		//println "T1: $t"
		// nota: aspas, parênteses, barras, etc. estão separados com espaços.


		/***** 1. tratar dos pontos nas abreviaturas ******/
		// tem de estar ANTES das pessoas, para rectificar Eng.º, etc
		t = t.replaceAll(/\.([º°ª])/, xg1)
		t = t.replaceAll(/([º°ª])\./, g1x)

		//só mudar se não for ambíguo com ponto final: Exemplo: 3º. lugar
		// comentado: mas como distingo '3º. lugar' de '3º. Encontro'?
		//t = t.replaceAll(/([º°ª])\. +([^A-ZÀÁÉÍÓÚÂÊ\«])/) {all, g1, g2 -> "${g1}‡ ${g2}"}

		/****** 1.3 PESSOAS: formas de tratamento *****/
		// não colocar espaço no final, pois o tratamento pode seguir com vírgula, como Drs.,


		if (debug) log.trace "1.2 > PESSOA T: $t".trim()

		// 1.3.1 prepostos
		t = t.replaceAll(/\b([Ee]x)\./, g1x)// Ex. ex. e não colocar espaço para ex.
		t = t.replaceAll(/\b([DdSs]rt?a?s?)\./, g1x)// Sr, Sra, Srs, Sras, Dr, Dra, Drs, Dras, Srta, etc ex. PUBLICO-19950224-056
		t = t.replaceAll(/\b([Ee]xm?[oa]?s?)\./, g1x) // Exa., Exas., Exmºs, etc
		t = t.replaceAll(/\b([Ee]ng[oa]?s?)\./, g1x)// Eng., Enga.
		t = t.replaceAll(/\b([Aa]rq[oa]?s?)\./, g1x) // arq., Arqs., Arqa.
		t = t.replaceAll(/\b([Pp]ro?fa?s?)\./, g1x)  // Prf. Prof., Profs. Profa., Profas. prof., profs. profa., profas.
		t = t.replaceAll(/\b([Mm]rs?)\./, g1x) // Mr. Mrs.

		t = t.replaceAll(/\b(Pe)\./, g1x) // Padre
		// S. Paulo, St. Peter. Cuidado com "Baker St.", este ponto pode ser fim de frase
		t = t.replaceAll(/\b([Ss][Tt]?)\. (${uppercaseAlpha})/, g1xwg2) // ST. PETER,  FSP941230-124.html
		t = t.replaceAll(/\b(PR)\./, g1x) // sim, há PR como Presidente da República... ver PUBLICO-19940116-081

		t = t.replaceAll(/\b([Rr]ev)\./,g1x) // Rev. de Reverendo, LA092494-0018
		t = t.replaceAll(/\b(Sen|C[oe]l|Gen|Maj)\./, g1x)// senador (vem sempre depois de Av. ou R. ...), coronel, general (PUBLICO-19950330-162), major
		t = t.replaceAll(/\b(d)\./,g1x) //d. Luciano. Nota que o D. pode atrapalhar alguns acrónimos

		// 1.3.2 pospostos
		t = t.replaceAll(/\b([Jj][Rr])\./, g1x) // Jr.
		t = t.replaceAll(/\bPh\.D\./,"Ph‡D‡")

		// 1.3.3 Abreviaturas francesas
		t = t.replaceAll(/\b(Mme)\./, g1x)

		/****** 1.4 LOCAIS *********/

		if (debug) log.trace "1.4 > LOCAL T: $t".trim()

		// 1.4.1: moradas
		t = t.replaceAll(/\b(R|[Aa]v|[Pp]rç?|[Aa]l|[Ee]str?|[Ll]go?|[Tt](?:r|ra)?v|Pq|Jd|Ft|[CcLl]j)\./, g1x)
		// R. Av. Pr. (PUBLICO-19940330-121), Prç.  Al. Gabriel Monteiro da Silva, FSP940925-179,
		// Est. Estr. (est. também), Lg. lgo. Trav., Trv., Tv. Pq. Jd. Ft. Cj. cj. lj. (loja)

		// 1.4.2: contactos
		t = t.replaceAll(/\b([Tt]el(?:e[fm])?)\./, g1x)// tel. Tel. Telef, telef, Telem, telem
		t = t.replaceAll(/\b([Ff]ax)\./, g1x) // fax. Fax.
		t = t.replaceAll(/\b(cx)\./, g1x) // caixa cx.
		t = t.replaceAll(/\bP.O.(\s?)Box/) {all, g1 -> "P‡O‡${g1}Box"} // P.O. Box, P.O.Box

		/******* 1.5 ORGANIZAÇÕES / PAÍSES *******/

		if (debug) log.trace "1.5 > ORGANIZACOES T: $t".trim()

		// 1.5.1 prepostos
		t = t.replaceAll(/\b([Dd]ept?o?)\./, g1x) // Dep. dept. Depto.
		t = t.replaceAll(/\b(Adm)\./, g1x)// Adm. como Administração, FSP950507-125.html
		t = t.replaceAll(/\b([Oo]rg|[Mm]in|[Ii]nst|[Rr]ep)\./, g1x) // Org. Min. Inst. Rep.

		// 1.5.2 pospostos
		t = t.replaceAll(/\b([Ll]da|[Cc]ia)\./, g1x) // lda. Lda. cia. Cia.
		t = t.replaceAll(/\bS\.A\./,"S‡A‡") // S.A.
		t = t.replaceAll(/\b(Cf)\./, g1x)// Cf. , ver  PUBLICO-19950514-085

		// 1.5.3 pospostos ingleses
		t = t.replaceAll(/ ([Bb]ros|[Cc]om?|[Cc]orp|[Ii]nc|[Ll]td|[Ll]abs?)\./,wg1x)  // Lab. FSP950929-082.html

		// 1.5.4 nomes estranhos
		t = t.replaceAll(/\bYahoo!/,"Yahooı")

		/***** 1.6 OBRAS / ARTIGOS *******/

		if (debug) log.trace "1.6 > OBRAS T: $t".trim()

		t = t.replaceAll(/\b([Aa]rts?)\./, g1x)// Art. como Artigo, FSP950316-075
		t = t.replaceAll(/\b([Dd]ec)\./, g1x) // Dec. aparece em LA020294-0233 como Decreto (Dec. 24), mas tb pode ser mês/ano Dec.
		t = t.replaceAll(/\b(Op\.)/, g1x) // Concerto Op. 64, FSP940921-089


		/***** 1.7 abreviaturas de texto ******/

		if (debug) log.trace "1.7 > ABREVIATURAS T: $t".trim()

		// 1.7.1 abreviaturas greco-latinas
		// AC, BC, AD, DC
		t = t.replaceAll(/\b([AaBbDd])\.([Cc])\./) {all, g1, g2 -> "${g1}‡${g2}‡"} // a.c.
		t = t.replaceAll(/\b([AaBbDd][Cc])\./, g1x) // ac.
		t = t.replaceAll(/\b([Aa])\.?([Dd])\./) {all, g1, g2 -> "${g1}‡${g2}‡"} // a.d.
		t = t.replaceAll(/\b(ca)\./, g1x) // ca.

		t = t.replaceAll(/\betc\.([.,;])/) {all, g1 -> "etc‡${g1}"} // etc.. etc., etc.;
		t = t.replaceAll(/\betc\.\)([.,;])/) {all, g1 -> "etc‡)${g1}"} // etc.). etc.),
		t = t.replaceAll(/\betc\. --( *${lowercaseAlpha})/) {all, g1 -> "etc‡ --${g1}"} // etc. --( batatas
		t = t.replaceAll(/\betc\.(\)*) (${lowercaseAlpha})/) {all, g1, g2 -> "etc‡${g1} ${g2}"} // etc.) batatas, etc. batatas
		// o al. já foi alterado como alameda...
		t = t.replaceAll(/\bet\.(\s*)al‡/) {all, g1 -> "et‡${g1}al‡"}

		t = t.replaceAll(/\bq\.b\./,"q‡b‡") // q.b.
		t = t.replaceAll(/\bi\.e\./,"i‡e‡") //i.e.
		t = t.replaceAll(/\b((?:ib)?id)\./, g1x) // id. ibid.
		t = t.replaceAll(/\bop\.(\s*)cit\./) {all, g1 -> "op‡${g1}cit‡"} // op.cit op. cit.
		t = t.replaceAll(/\bP\.S\./,"P‡S‡") //P.S.
		t = t.replaceAll(/\b([Oo]bs)\./, g1x) // Obs.

		// 1.7.2 outras abreviaturas
		t = t.replaceAll(/\bp\.ex\./,"p‡ex‡") //p.ex.
		t = t.replaceAll(/\b(p[aá]?gs?)\./, g1x) // pg, pgs, pág págs pag pags
		t = t.replaceAll(/\b(pp?)\./, g1x)// pp. p.

		t = t.replaceAll(/\b([Vv]ols?)\./, g1x) // PUBLICO-19940519-006.html - outro uso para Vol. que não Volume
		t = t.replaceAll(/\b(v)\.(\s+[0-9])/, g1xg2) // abreviatura de volume no ANCIB
		t = t.replaceAll(/(\(v)\.(\s+[0-9])/, g1xg2) // abreviatura de volume no ANCIB

		t = t.replaceAll(/\(([Ee]ds*)\.\)/) {all, g1 -> "(${g1}‡)"}// (eds.)
		t = t.replaceAll(/\(([Oo]rgs*)\.\)/) {all, g1 -> "(${g1}‡)"}//(orgs.)
		t = t.replaceAll(/\b([Ee]ds?)\./, g1x) // Ed. ed. Eds. eds.

		/***** 1.8 TEMPO DATAS *****/

		if (debug) log.trace "1.8 > TEMPO T: $t".trim()

		// 1.8.1 prepostoss
		t = t.replaceAll(/\b([Ss][ée]c)\./, g1x)
		t = t.replaceAll(/\b(c)\. ([0-9])/, g1xwg2) // c. 1830

		// meses
		// meses sem conflitos: jan fev feb abr apr mai jun jul aug sep oct nov dec
		// dec pode aparecer como Dec. de Decreto, e já foi previamente protegido.
		t = t.replaceAll(/\b([Jj]an|[Ff]e[vb]|[Aa][bp]r|[Mm]ai|[Jj]u[nl]|[Aa]ug|[Ss]ep|[Oo]ct|[Nn]ov|[Dd]ec)\./, g1x)

		// meses com conflitos: mar ago set out dez
		// testar "mar. 97", "mar. de"
		t = t.replaceAll(/\b([Mm]ar|[Aa]go|[Ss]et|[Oo]ut|[Dd]ez)\.(\s*[0-9]+)/, g1xg2)
		t = t.replaceAll(/\b([Mm]ar|[Aa]go|[Ss]et|[Oo]ut|[Dd]ez)\.(\s+(?:de|a|of)\s+)/, g1xg2)

		// dias da semana sem conflitos: seg qua qui sáb dom
		t = t.replaceAll(/\b([Ss]eg|[Qq]u[ai]|[Ss]áb|[Dd]om)\./, g1x)
		// dias da semana com conflitos: ter sex
		// testar com vírgula ou [Ff]eira
		t = t.replaceAll(/\b([Tt]er|[Ss]ex)\.(,)/, g1xwg2) // adiciono espaço entre ponto e a vírgula
		t = t.replaceAll(/\b([Tt]er|[Ss]ex)\. ([Ff]eira)/, g1xwg2) // adiciono espaço entre ponto e a vírgula

		// Abreviaturas especiais do Diário do Minho
		t = t.replaceAll(/\b(Hab|habilit|Mot|\-Ang|Sp|Un|Univ)\./, g1x) //Hab. habilit. Mot. Sp. (Sporting) Un. (Universidade)
		// Abreviaturas especiais do Folha
		t = t.replaceAll(/\b(O[rc])\./, g1x) // alemanha Oriental e Ocidental
		t = t.replaceAll(/d' Or‡/, "d' Or.") // Se for d'Or,  desfaz.

		/****** 1.9 VALORES ***/

		if (debug) log.trace "1.9 > VALOR T: $t".trim()

		// 1.8.1 litaral de número
		t = t.replaceAll(/\bn\.o(s?) /) {all, g1 -> " n‡o${g1} "}// abreviatura de numero no MLCC-DEB
		t = t.replaceAll(/\b([Nn][or]?)\.(s?\s*[0-9])/, g1xg2)// no., No. N. no.s No.s N.s Nr.
		t = t.replaceAll(/\b([Nn]um)\.(\s+[0-9])/, g1xg2) // abreviatura de numero num. no ANCIB

		// 1.8.2 unidades de medida
		t = t.replaceAll(/([0-9]\s*(?:hr?|m)s?)\. (${notUppercaseAlpha})/, g1xwg2) //19h. 19 hrs 24m.
		t = t.replaceAll(/\b(min)\./, g1x) // 15 min.
		// Nota: para <<14H. A hora>> o H. A vai ser confundido no detector de iniciais mais à frente.
		// Como tal, separar 14H. e 15M. Esse ponto final é término de frase assim.
		t = t.replaceAll(/(?<=\b[0-9]{1,2}\s?[HM])\.(?=\s+${uppercaseAlpha}\b)/, " .") //14H. {A bcd...}. 15M. {B cde...}


		t = t.replaceAll(/([0-9]\s*[kdcmµn]ms?)\. (${notUppercaseAlpha})/, g1xwg2) // distância: 20km., 24mm.

		// 1.8.3 tratamento de numerais 300.000 até 9.460.800.000.000
		t = t.replaceAll(/[0-9]+\.(?:[0-9]+\.)*[0-9]+/) {all -> return all.replaceAll(/\./, "‡")} // pontos
		t = t.replaceAll(/[0-9]+,(?:[0-9]+,)*[0-9]+/) {all -> return all.replaceAll(/,/, "∞")} // vírgulas
		// horas: 13:00:00
		t = t.replaceAll(/[0-9]+:(?:[0-9]+:)*[0-9]+/) {all -> return all.replaceAll(/:/, "•")} // dois pontos

		// para <<14H. A hora>> o H. A é confundido com
		// 1.8.4 tratamento de numerais cardinais
		// - tratar dos números com ponto no início da frase
		t = t.replaceAll(/(?m)^([0-9]+)\. /, g1xw)
		// - tratar dos números com ponto antes de minúsculas
		t = t.replaceAll(/([0-9]+)\.(\s+${lowercaseAlpha})/, g1xg2)
		// tratamento de numerais ordinais acabados em .o
		t = t.replaceAll(/([0-9]+)\.([oa]s?)/, g1xg2)
		// ou expressos como 9a.
		t = t.replaceAll(/([0-9]+[oa]s?)\./, g1x)

		/***************************************
		 * 2. tratar dos conjuntos de iniciais *
		 ***************************************/
		if (debug) log.debug "2 > INICIAIS T: $t".trim()

		// 2.1 U.S.A. D.C. L.A.P.D. etc

		List<String> sigl = []
		// siglas no início da frase
		t.findAll(/(?m)^((?:${uppercaseAlpha}\.\s*)+${uppercaseAlpha}\.?)/) {all, g1 -> sigl << g1}
		// siglas no final da frase
		t.findAll(/(?m)((?:${uppercaseAlpha}\.\s*)+${uppercaseAlpha}\.?)$/) {all, g1 -> sigl << g1}
		// siglas a meio. Cuidado com "século XX{I. A} nova frase"... o padrão tem de começar sem ter maiúscula antes.
		t.findAll(/(?x) (?<!${uppercaseAlpha}) ( (?:${uppercaseAlpha}\.\s*)+ (?:${uppercaseAlpha}\.?) ) (?=[\s,;:!?\/])/) {all, g1 -> sigl << g1}


		if (debug) log.trace "2.1 > SIGL: $sigl".trim()
		sigl.each{s->
			def escaped_s = s.replaceAll(/\./, "\\\\"+".")
			def tgt_s = s.replaceAll(/\./,"‡")
			t = t.replaceAll(/${escaped_s}/, tgt_s)
			//	t = t.replaceAll( escaped_s,  ) }
		}
		if (debug) log.trace "2.2 > INICIAIS2 T: $t".trim()

		// 2.2 tratar de pares de iniciais ligadas por hífen (à francesa: A.-F.)

		t = t.replaceAll(/\b(${uppercaseAlpha})\.\-(${uppercaseAlpha})\. /) {all, g1, g2 ->"${g1}‡-${g2}‡ "}
		// tratar de iniciais (únicas?) seguidas por ponto
		t = t.replaceAll(/\b(${uppercaseAlpha})\. /, g1xw)

		if (debug) log.trace "2.3 > INICIAIS3 T: $t".trim()


		// 2.3 F.C.Porto e S.L.Benfica não são acróminos, mas precisam de ser separados em F. C. Porto e S. L. Benfica
		// há que proteger os '.' pois não são marcadores de quebra de frase.
		t = t.replaceAll(/\b(?:${uppercaseAlpha}\.)+${uppercaseAlpha}${lowercaseAlpha}+\b/) {all -> return all.replaceAll(/\./,"‡ ")}

		/**************************
		 * 3. espaçamentos finais *
		 **************************/

		if (debug) log.debug "3.0 > Espaçamento T: $t".trim()

		t = t.replaceAll(/\s+/," ")

		Closure g1p = {all, g1 -> "${g1}¶"}
		Closure xg1p = {all, g1 -> "‡${g1}¶"}
		Closure g1pg2 = {all, g1, g2 -> "${g1}¶${g2}"}

		//ponto seguido de uma vírgula, é abreviatura...
		t = t.replaceAll(/\.(\s*),/) {all, g1 -> "‡${g1},"}
		//ponto seguido de outro ponto, é abreviatura... o outro é pontuação
		// notar que tem de haver um espaço, senão entra em conflito com ‡‡
		t = t.replaceAll(/\.(\s+)\./) {all, g1 -> "‡${g1}‡¶"}

		//        String t2
		t = t.replaceAll(/([!\?]+)/, wg1) // espaço à esquerda para grupos de ?!

		t = t.replaceAll(/([\.,:;])/, wg1) // espaço à esquerda para . , ; : (posso usar :, já os protegi)
		//       if (debug) if (t != t2) println "5.1:\n$t\n$t2"


		// a partir daqui, vamos introduzir símbolos ¶, que marca quebras de frases.
		// Os pontos finais restantes vão ser úteis para sugerir quebras, e quando sao, serão protegidos por ‡ e à frente coloca-se um ¶.
		// Estas regras têm em atenção casos raros de finais de frases em parênteses / aspas .

		// Nota : os parênteses , reticências, aspas já estão espaçados!
		// Usar esse espaço no padrão, para não perturber o que já teve ( ) e foi protegido/processado antes.

		//println "T: $t2"
		// os ?! vão receber um '.' para indicar que são fins de frase.
		// usar apenas se não for eguido de aspas a fechar, vírgulas, parênteses a fechar, reticências, etc
		// << bananas ? Bananas >> -> << bananas ?¶ Bananas >>
		// << Mas como " fazer ( aqui ? ) " hã ? Esta >> -> << Mas como " fazer ( aqui ? ) " hã ?¶ Esta >>
		// o \s+ não pode ser \s*, senão o exexplo de cima deixa de funcionar!
		t = t.replaceAll(/(?x) ( [\?!]+ ) \s+ (?![-»"'´,†\)])/, g1p)

		//  reticências, com possibilidade de parênteses e aspas no meio, e começa uma nova frase
		// Remember: (?= is a zero-width positive lookahead. (?x) will allow whitespace, to be a clearer pattern)
		// nota: apanho reticências simples † e reticências (...), como \(†\)
		// << bananas ... ) Nova frase >> -> << bananas ... )¶ Nova frase >>
		// << bananas ? ... ) não é nova frase >> -> << bananas ? ... ) não é nova frase >>
		t = t.replaceAll(/(?x) ( \s+ \(?†\)? \s+ (?:\s*[»"´'\)])* ) \s* (?= (?:\(\s|\-\-\s|"\s)? ${uppercaseAlpha})/, g1p)



		// tratar de ? e ! seguidos de fecha aspas / fecha parêntenses quando seguidos de abre parênteses ou por travessão e
		// maiúscula
		// << bananas ? ' ( A teoria >> -> << bananas ? '¶ ( A teoria >>

		t = t.replaceAll(/(?x) ( [?!]+ \s* (?:\s*[»"´'\)])+? ) \s* (?= (?:\(\s|\-\-\s|"\s)? ${uppercaseAlpha})/, g1p)

		// mesma coisa, mas com '.' e acrescento '"' à lista de coisas que podem começar a nova frase
		// notar o +? para que o () sozinho encontre o segundo " (nota o segundo exemplo) senão é ganancioso.
		// << bananas . ' ( A teoria >> -> << bananas ‡ '¶ ( A teoria >>
		// << bananas . " " A teoria >> -> << bananas ‡ "¶ " A teoria >>

		t = t.replaceAll(/(?x) \. ( (?:\s*[»"´'\)])+? ) \s* (?= (?:\(\s|\-\-\s|"\s) ${uppercaseAlpha})/, xg1p)

		// separar os pontos antes de parênteses, aspas ou se forem seguidos de nova frase
		// remember: (?<=) is a zero-width positive lookbehind
		//  << bananas . ) Nova frase >> -> << bananas ‡ )¶ Nova frase >>
		t = t.replaceAll(/(?x) \. ( (?:\s*[\»"´'\)])+ ) (?=\s*${uppercaseAlpha})/, xg1p)

		// tratar dos pontos antes de aspas ou parênteses.
		// << bananas . ' Qualquer >> -> << bananas ‡ '¶ Qualquer >>
		// << bananas . ) Qualquer >> -> << bananas ‡ )¶ Qualquer >>
		t = t.replaceAll(/(?x) \. ( (?:\s*["\»'´\)])+ ) (?=[^.!\?])/, xg1p)

		// tratar das aspas quando seguidas de novas aspas
		// << batatas» `batatas >> -> << batatas»¶ `batatas >>
		t = t.replaceAll(/(?x) (${quotationRight}) (?=\s*${quotationLeft})/, g1p)


		/* fins de parágrafo. */

		// trata ?, ! e †(...) antes de possíveis fecha parênteses / aspas se forem o fim do parágrafo
		// << batatas ? ) $ >>  -> << batatas ? ) ¶$ >>
		t = t.replaceAll(/(?xm) (?<= ${anyAlphaNum} ) ([?!†]+ (?:\s*[»"´'\)])* \s*) $/, g1p)

		// tratar do ponto '.' antes de possíveis fecha parênteses / aspas se forem o fim do parágrafo
		// << batatas . $ >> -> << batatas ‡ ¶$ >>
		// << batatas . " $ >> -> << batatas ‡ " ¶$ >>
		t = t.replaceAll(/(?xm) \. ( (?:\s*[»"´'\)])* \s* ) $/, xg1p)

		// se o parágrafo acaba em "‡", deve-se juntar "¶" outra vez.
		// << batatas ‡ $ >> -> << batatas ‡ ¶$ >>
		t = t.replaceAll(/(?xm) (?<!‡) (‡\s*) $/, g1p)

		// tratar dos dois pontos se eles acabam o parágrafo (é preciso pôr um espaço)
		// << batatas : $ >> -> << batatas :¶$ >>
		t = t.replaceAll(/(?xm) : \s* $/,":¶")

		// se o parágrafo acaba em abreviatura (‡) seguido de aspas ou parêntesis, deve-se juntar "¶"
		// << ‡ » $ >> -> << ‡ » ¶$ >>
		t = t.replaceAll(/(?xm) (?<!‡) (‡ (?:\s*["»'´\)])* \s*) $/, g1p)

		//dandling " . " are to be broken into  ‡¶ "
		t = t.replaceAll(/\s\.\s/," ‡¶ ")

		//pontos finais tem de ser separados de palabras, se estiverem ligados. Já foi tudo bem protegido. Estes são mesmo pontos finais."
		t = t.replaceAll(/(?xm) (?<=${anyAlphaNum})\./," ‡¶")

		/********************
		 * 4 dividir frases *
		 ********************/


		List<String> s = t.trim().split(/(?<!¶)¶(?!¶)/) // ¶ sozinho

		if (debug) log.debug "sentences:\n"
		if (debug) log.debug s.join("\n")

		s.eachWithIndex{it, i ->

			s[i] = s[i].replaceAll(/¶¶/,"¶")

			s[i] = s[i].replaceAll(/(?<!◊)◊(?!◊)/, "?")
			s[i] = s[i].replaceAll(/◊◊/,"◊")
			s[i] = s[i].replaceAll(/(?<!ı)ı(?!ı)/, "!")
			s[i] = s[i].replaceAll(/ıı/,"ı")
			s[i] = s[i].replaceAll(/(?<!§)§(?!§)/, "/")
			s[i] = s[i].replaceAll(/§§/,"§")
			s[i] = s[i].replaceAll(/(?<!∂)∂(?!∂)/, "//")
			s[i] = s[i].replaceAll(/∂∂/,"∂")
			s[i] = s[i].replaceAll(/(?<!∞)∞(?!∞)/, ",")
			s[i] = s[i].replaceAll(/∞∞/,"∞")
			s[i] = s[i].replaceAll(/(?<!•)•(?!•)/, ":")
			s[i] = s[i].replaceAll(/••/,"•")
			s[i] = s[i].replaceAll(/(?<!‡)‡(?!‡)/, ".")
			s[i] = s[i].replaceAll(/‡‡/,"‡")
			s[i] = s[i].replaceAll(/(?<!†)†(?!†)/, "...")
			s[i] = s[i].replaceAll(/††/,"†")
			s[i] = s[i].trim()

		}
		return s.findAll{!it || !(it ==~ /^\s*$/)}
	}

	public List<Sentence> parse(String text) {

		if (!text) return null
		def sentences = []
		linguatecaTokenize(text).eachWithIndex{frase, i ->
			Sentence s = new Sentence(i)
			frase = frase.split(/\s+/).eachWithIndex{t, i2 -> s << new Term(t, i2) }
			sentences << s
		}
		//println "sentences: "+sentences.join('\n')
		return sentences
	}

	public static void main(args) {

		TokenizerPT t = TokenizerPT.newInstance()
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
		StringBuffer sb = new StringBuffer()
		RembrandtWriter w = new RembrandtWriter(new RembrandtStyleTag("pt"))
		String l
		while (l = br.readLine()) sb.append l+"\n"
		Document doc = new Document()
		doc.body_sentences = t.parse(sb.toString())
		println w.printDocumentBodyContent(doc)
	}
}