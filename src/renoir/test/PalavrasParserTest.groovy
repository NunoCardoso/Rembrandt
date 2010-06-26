package renoir.test

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import saskia.bin.Configuration

/**
 * Check the detection of question types
 * note: do "echo "XXX" | native2ascii -encoding UTF-8"  to get the \\u XXXX code
 * @author Nuno Cardoso
 */
class TestPalavrasParser extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	
	public TestPalavrasParser() {

def text = "quantos habitantes tem Lisboa?"

def target_palavras_text = ""+
"quantos 		[quanto] <quant> <interr> DET M P @>N #1->2"+
"habitantes 		[habitante] N M P @SUBJ> #2->3"+
"tem 		[ter] <fmc> V PR 3S IND VFIN @FS-QUE #3->0"+
"Lisboa 		[Lisboa] PROP F S @<ACC #4->3? #5->0"

def target = """quantos	quanto	DET_interr_quant	0	P	M	>N	0
		habitantes	habitante	N_Hnat	0	P	M	SUBJ>	0
		tem	ter	V_fmc	PR_IND	3S	0	FMV	0
		Lisboa	Lisboa	PROP_civ	0	S	F	<ACC	0
		?	?	PU	0	0	0	PONT	0"""

//	def palavras_text = PALAVRASWebService.parse(text)
//	assert palavras_text == target_palavras_text

	def linguateca_text = PALAVRAS2LinguatecaPoS.parse(target_palavras_text)
	println linguateca_text
	}
}
		
