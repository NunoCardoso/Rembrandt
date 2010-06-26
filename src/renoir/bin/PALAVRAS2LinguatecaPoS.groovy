package renoir.bin

class Palavras2LinguatecaPoS {
	
	
// cat Treino_iso.cg | acdc_pos_PALAVRAS1.pl| bick_ref.pl | 
// bick_limpa_linha.pl | expande_unidades_polilexicais.pl -d | 
// bick_reescreve_formato.pl | bick_separa_morfologia.pl | bick_trata_contraccoes.pl | 
// bick_expande_compostos.pl | bick_trata_cliticos.pl | bick_trata_derivacoes.pl | 
// acdc_pos_PALAVRAS2.pl > batatas

 static String parse(String rawtext) {
	
	def act
	def text
	def res1 = []
	
	rawtext.split(/\n/).each{t -> 
		/*****************************
		 * 1.  acdc_pos_PALAVRAS1.pl *
		 *****************************/
		// trata dos ALT
		t = t.replaceAll(/ALT .*?\t/,"\t")
		//TRta dos caracteres espúrios
		//s/\$//;
		t = t.replaceAll(/_more>.*$/, "")
		t = t.replaceAll(/<\/more>/,"")

		t = t.replaceAll(/=removeme=/,"")
		def m = t =~ /.*<lixo_.*/
		if (m.matches()) {
			t = t.replaceAll(/<lixo_/,"") 
			act=1
			t = t.replaceAll(/>\n/,"")
		} else if (act == 1) {
			res1 << "\t[palavra_longa]\tPALAVRALONGA\n";
			act=0
		}
		// reescreve com espaços os valore dos atributos estruturais 
	    // que estejam com _ ou .
	    //s#<s frag>#<s tipo=\"frag\">#;
		//    {} while (s#<(.+?)[._]#<$1 #g);
		res1 << t
 	}
	text = res1.join("\n")
	res1 = []
	
	text.split(/\n/).each{t -> 
		/*****************************
		 * 2.  bick_ref.pl *
		 *****************************/
	    t = t.replaceAll(/<\/s>¶ <s(.*)$/) {all, g1 -> "</s>\n<s${g1}"}
     	t = t.replaceAll(/<\/s>¶ *$/,"</s>")
    	t = t.replaceAll(/> <art id/,">\n<art id")
    	t = t.replaceAll(/<p> <s(.*)>$/) {all, g1 -> "<p>\n<s${g1}>"}
    	t = t.replaceAll(/> <p>/,">\n<p>");
		res1 << t
	}
 	
 	text = res1.join("\n")
	res1 = []
	text.split(/\n/).each{t -> 
		/*****************************
		 * 3.  bick_limpa_linha.pl*
		 *****************************/
	    def m = t =~ /.*=.*/
		def m2 = t =~ /.*[?:].*/
		if (m.matches()) {
			if (m2.matches()) {
				t = t.replaceAll(/([?:])/) {all, g1 -> "=${g1}"}
			}
			def m3 = t =~ /.*\.\.\..*/
			if (m3.matches()) {
	    		t = t.replaceAll(/([^=])\.\.\./) {all, g1 -> "${g1}=..."}
			}
			t = t.replaceAll(/\[=para\+o\/o\]/,"[pró]")
			t = t.replaceAll(/\-=/,"-")
			t = t.replaceAll(/([^\$])\-\-/) {all, g1 -> "${g1}-"}
    	}
    	def m4 = t =~ /\*[áéíóúâêôãõàç]/
		if (m4.matches()) {
			t = t.replaceAll(/\*([áéíóúâêôãõàç])/) {all, g1 -> "\\U${g1}"}
    	}
    	t = t.replaceAll(/\"há\]/,"") //"
    	t = t.replaceAll(/\$START/,"")
    	t = t.replaceAll(/<\$¶>/,"")
    	t = t.replaceAll(/\$¶/,"")
		//    s/<NUM\-ord>/<NUMord>/g;
    	t = t.replaceAll(/\s*<hyfen>/,"")
    	t = t.replaceAll(/<\*[1-9]> /,"")
    	t = t.replaceAll(/\"«/,"«"); //"
    	t = t.replaceAll(/\"»/,"»"); //"
    	t = t.replaceAll(/^\($/,"\$(") // por causa de ( sozinhos no ANCIBANOT
    	t = t.replaceAll(/^\)$/,"\$)") // por causa de ) sozinhos no ANCIBANOT
   		t = t.replaceAll(/\(\(/,"(")
    	t = t.replaceAll(/\)\)/,")")
    	t = t.replaceAll(/Br\.\t+\[br\.\]/,"Br\t[br]")
    	t = t.replaceAll(/<(\$.*)>/) {all, g1 -> "$g1"} // por causa dos "<$(>   no SCANOT
    	t = t.replaceAll(/<\$\\<>/,"") // por causa dos <$\<>  no SCANOT
    	t = t.replaceAll(/<\$\/>/,"") // por causa dos <$/>
    	t = t.replaceAll(/<([0-9][0-9.,: \/hªº-]*)>/) {all, g1 -> "$g1"} // por causa dos <$/>
    	t = t.replaceAll(/^<$/,"")	// por causa de < sozinho
    	t = t.replaceAll(/<n>\s+<n>/,"<n>") // por causa da repetição de <n>
    	t = t.replaceAll(/<n>\s+/,"<n> ")	// por causa de excesso de espaços
    	t = t.replaceAll(/^([\w=-]+)\s+\[\-\]/) {all, g1 -> "${g1}\t[\\l${g1}]"} // por causa de lemas [-]
    	def m5 = t =~ /^\s*$/
		if (!m5.matches()) res1 << t
	}
	
	text = res1.join("\n")
	res1 = []
//	text.split(/\n/).each{t -> 
		/*****************************
		 * 4.  bick_limpa_linha.pl*
		 *****************************/
	}	
}