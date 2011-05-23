package renoir.test.obj

import org.junit.*
import org.junit.runner.*
import org.apache.log4j.*
import renoir.obj.*
import renoir.rules.*
import rembrandt.obj.*
import saskia.bin.Configuration

/**
 * Check the detection of question types
 * @author Nuno Cardoso
 */
class SentenceWithPoSTest extends GroovyTestCase {

	def Logger log = Logger.getLogger("UnitTest")
	Configuration conf

	public SentenceWithPoSTest() {
		// initialize
		conf = Configuration.newInstance()
		// Force the
	}

	void testSentence1() {

		/**** 1. Quantos habitantes tem Lisboa? ***/
		String linguateca_line = """quantos	quanto	DET_interr_quant	0	P	M	>N	0
				habitantes	habitante	N_Hnat	0	P	M	SUBJ>	0
				tem	ter	V_fmc	PR_IND	3S	0	FMV	0
				Lisboa	Lisboa	PROP_civ	0	S	F	<ACC	0
				?	?	PU	0	0	0	PONT	0"""

		List palavras_line = [
			"quantos 		[quanto] <quant> <interr> DET M P @>N",
			"habitantes 		[habitante] N M P @SUBJ>",
			"tem 		[ter] <fmc> V PR 3S IND VFIN @FS-QUE",
			"Lisboa 		[Lisboa] PROP F S @<ACC",
			"?"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == [
			"DET",
			"N",
			"V",
			"PROP",
			"PONT"
		], \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == [
			"DET",
			"N",
			"V",
			"PROP",
			"PONT"
		], \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence2() {
		String linguateca_line = """quantos	quanto	DET_interr_quant	0	P	M	>N	0
		anos	ano	N_unit_dur_temp_per	0	P	M	SUBJ>	0
		tem	ter	V_fmc	PR_IND	3S	0	FMV	0
		Jos\u00e9	Jos\u00e9=Saramago	PROP_genre	0	S	M	<ACC	0
		Saramago	Jos\u00e9=Saramago	PROP_genre	0	S	M	<ACC	0
		?	?	PU	0	0	0	PONT	0"""

		List palavras_line = [
			"quantos 		[quanto] <quant> <interr> DET M P @>N",
			"anos 		[ano] N M P @SUBJ>",
			"tem 		[ter] <fmc> V PR 3S IND VFIN @FS-QUE",
			"Jos\u00e9=Saramago 		[Jos\u00e9=Saramago] PROP M S @<ACC",
			"?"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == [
			"DET",
			"N",
			"V",
			"PROP",
			"PROP",
			"PONT"
		], \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == [
			"DET",
			"N",
			"V",
			"PROP",
			"PROP",
			"PONT"
		], \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence3() {
		String linguateca_line ="""
	<mwe pos=DET>
	quanto	quanto	DET_rel	0	S	M	ADV>	0
	pesa	pesar	V	PR_IND	3S	0	FMV	0
	</mwe>
	um	um	DET_arti	0	S	M	>N	0
	Ferrari	Ferrari=F40	PROP_V	0	S	M	NPHR	0
	F40	Ferrari=F40	PROP_V	0	S	M	NPHR	0
	?	?	PU	0	0	0	PONT	0
	"""
		List palavras_line = [
			"[quanto=pesa 		[quanto=pesa] DET M S @<SA",
			"um 		[um] <arti> DET M S @>N",
			"Ferrari=F40 		[Ferrari=F40] PROP M S @NPHR",
			"?"
		]
		List solution_linguateca = [
			"DET",
			"V",
			"DET",
			"PROP",
			"PROP",
			"PONT"
		]
		// a resolver
		List solution_palavras = [
			"DET",
			"DET",
			"DET",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution_linguateca, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution_palavras, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence4() {
		String linguateca_line ="""
		qual	qual	DET_interr	0	S	M/F	SC>	0
		\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
		a	o	DET_artd	0	S	F	>N	0
		popula\u00e7\u00e3o	popula\u00e7\u00e3o	N_HH	0	S	F	<SUBJ	0
		de	de	PRP	0	0	0	N<	0
		Portugal	Portugal	PROP_civ	0	S	M	P<	0
		?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"qual 		[qual] <interr> DET M/F S @SC>",
			"\u00e9 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[o] DET F S @>N",
			"popula\u00e7\u00e3o 		[popula\u00e7\u00e3o] N F S @<SUBJ",
			"de 		[de] PRP @N<",
			"Portugal 		[Portugal] PROP M S @P<",
			"?"
		]

		List solution = [
			"DET",
			"V",
			"DET",
			"N",
			"PRP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence5() {
		String linguateca_line ="""
qual	qual	DET_interr	0	S	M/F	SC>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
a	o	DET_artd	0	S	F	>N	0
capital	capital	N_ac-sign_Lciv	0	S	F	<SUBJ	0
de	de	PRP	0	0	0	N<	0
Portugal	Portugal	PROP_civ	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"qual 		[qual] <interr> DET M/F S @SC>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[o] DET F S @>N",
			"capital 		[capital] N F S @<SUBJ",
			"de 		[de] PRP @N<",
			"Portugal 		[Portugal] PROP M S @P<",
			"?"
		]

		List solution = [
			"DET",
			"V",
			"DET",
			"N",
			"PRP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence6() {
		String linguateca_line ="""
qual	qual	DET_interr	0	S	M/F	SC>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
a	o	DET_artd	0	S	F	>N	0
altura	altura	N_Ltop_f-q_Labs_temp	0	S	F	<SUBJ	0
de	de	PRP	0	0	0	N<	0
um	um	DET_arti	0	S	M	>N	0
Ferrari	Ferrari=F40	PROP_V	0	S	M	P<	0
F40	Ferrari=F40	PROP_V	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"qual 		[qual] <interr> DET M/F S @SC>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[o] DET F S @>N",
			"altura 		[altura] N F S @<SUBJ",
			"de 		[de] PRP @N<",
			"um 		[um] <arti> DET M S @>N",
			"Ferrari=F40 		[Ferrari=F40] PROP M S @P<",
			"?"
		]


		List solution = [
			"DET",
			"V",
			"DET",
			"N",
			"PRP",
			"DET",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence7() {
		String linguateca_line ="""
qual	qual	DET_interr	0	S	M/F	SC>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
a	o	DET_artd	0	S	F	>N	0
nacionalidade	nacionalidade	N_f-psych	0	S	F	<SUBJ	0
de	de	PRP	0	0	0	N<	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	P<	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"qual 		[qual] <interr> DET M/F S @SC>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[o] DET F S @>N",
			"nacionalidade 		[nacionalidade] N F S @<SUBJ",
			"de 	[de] PRP @N<",
			"José=Saramago 		[José=Saramago] PROP M S @P<",
			"?"
		]

		List solution = [
			"DET",
			"V",
			"DET",
			"N",
			"PRP",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence8() {
		String linguateca_line ="""
qual	qual	DET_interr	0	S	M/F	SC>	0
o	o	DET_artd	0	S	M	>N	0
nome	nome	N_ac-cat	0	S	M	NPHR	0
da	de+o	PRP+DET_artd	0	S	F	N<+>N	0
capital	capital	N_ac-sign_Lciv	0	S	F	P<	0
de	de	PRP	0	0	0	N<	0
Portugal	Portugal	PROP_civ	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"qual 		[qual] <interr> DET M/F S @SC>",
			"o 		[o] DET M S @>N",
			"nome 		[nome] N M S @NPHR",
			"de 		[de] <sam-> PRP @N<",
			"a 		[o] <-sam> DET F S @>N",
			"capital [capital] N F S @P<",
			"de 		[de] PRP @N<",
			"Portugal 		[Portugal] PROP M S @P<",
			"?"
		]

		List solution_linguateca = [
			"DET",
			"DET",
			"N",
			"PRP",
			"N",
			"PRP",
			"PROP",
			"PONT"
		]
		List solution_palavras = [
			"DET",
			"DET",
			"N",
			"PRP",
			"DET",
			"N",
			"PRP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution_linguateca, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution_palavras, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}


	void testSentence9() {
		String linguateca_line ="""
o	o	DET_artd	0	S	M	>N	0
que	que	SPEC_interr	0	S	M	SUBJ>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
um	um	DET_arti	0	S	M	>N	0
Ferrari	Ferrari=F40	PROP_V	0	S	M	<SC	0
F40	Ferrari=F40	PROP_V	0	S	M	<SC	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"o 		[o] DET M S @>N",
			"que 		[que] <interr> SPEC M S @SUBJ>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"um 		[um] <arti> DET M S @>N",
			"Ferrari=F40 	[Ferrari=F40] PROP M S @<SC",
			"?"
		]

		List solution = [
			"DET",
			"SPEC",
			"V",
			"DET",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence10() {
		String linguateca_line ="""
o	o	DET_artd	0	S	M	>N	0
que	que	SPEC_interr	0	S	M	SC>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
a	o	DET_artd	0	S	F	>N	0
Google	Google	PROP_org	0	S	F	<SUBJ <SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"o 		[o] DET M S @>N",
			"que 		[que] <interr> SPEC M S @SC>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[o] DET F S @>N",
			"Google 		[Google] PROP F S @<SUBJ",
			"?"
		]

		List solution = [
			"DET",
			"SPEC",
			"V",
			"DET",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence11() {
		String linguateca_line ="""
o	o	DET_artd	0	S	M	>N	0
que	que	SPEC_interr	0	S	M	SC>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0
"""

		List palavras_line = [
			"o 		[o] DET M S @>N",
			"que 		[que] <interr> SPEC M S @SC>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<SUBJ",
			"?"
		]

		List solution = [
			"DET",
			"SPEC",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence12() {
		String linguateca_line ="""
quem	quem	SPEC_interr	0	S/P	M/F	SC>	0
\u00e9	ser	V_fmc	PR_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"quem 		[quem] <interr> SPEC M/F S/P @SC>",
			"é 		[ser] <fmc> V PR 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<SUBJ",
			"?"
		]

		List solution = [
			"SPEC",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence13() {
		String linguateca_line ="""
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	M	>N	0
país	país	N_Lciv	0	S	M	P<	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
Lisboa	Lisboa	PROP_civ	0	S	F	<SUBJ	0
?	?	PU	0	0	0	PONT	0	"""

		List palavras_line = [
			"em 		[em] PRP @ADVL>",
			"que 		[que] <interr> DET M S @>N",
			"país 		[país] N M S @P<",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"Lisboa 		[Lisboa] PROP F S @<SUBJ",
			"?"
		]

		List solution = [
			"PRP",
			"DET",
			"N",
			"V",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence14() {
		String linguateca_line ="""
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	F	>N	0
cidade	cidade	N_Lciv	0	S	F	P<	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Torre	Torre=Eiffel	PROP_top	0	S	M/F	P<	0
Eiffel	Torre=Eiffel	PROP_top	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0
"""

		List palavras_line = [
			"em 		[em] PRP @ADVL>",
			"que 		[que] <interr> DET F S @>N",
			"cidade 		[cidade] N F S @P<",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[a] PRP @<SC",
			"Torre=Eiffel 		[Torre=Eiffel] PROP M/F S @P<",
			"?"
		]

		List solution = [
			"PRP",
			"DET",
			"N",
			"V",
			"PRP",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence15() {
		String linguateca_line ="""
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	F	>N	0
cidade	cidade	N_Lciv	0	S	F	P<	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
situada	situar	V	PCP	S	F	<SC	0
a	o	DET_artd	0	S	F	>N	0
torre	torre	N_build	0	S	F	<SUBJ	0
Eiffel	Eiffel	PROP_top	0	S	F	N<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"em 		[em] PRP @ADVL>",
			"que 		[que] <interr> DET F S @>N",
			"cidade 		[cidade] N F S @P<",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"situada 		[situar] V PCP F S @<SC",
			"a 		[o] DET F S @>N",
			"Torre=Eiffel 		[Torre=Eiffel] PROP F S @<SUBJ",
			"?"
		]

		List solution_linguateca = [
			"PRP",
			"DET",
			"N",
			"V",
			"V",
			"DET",
			"N",
			"PROP",
			"PONT"
		]
		List solution_palavras = [
			"PRP",
			"DET",
			"N",
			"V",
			"V",
			"DET",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution_linguateca, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution_palavras, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence16() {
		String linguateca_line ="""
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	M	>N	0
local	local	N_Lh	0	S	M	P<	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Google	Google	PROP_org	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"em 		[em] PRP @ADVL>",
			"que 		[que] <interr> DET M S @>N",
			"local 		[local] N M S @P<",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[a] PRP @<SC",
			"Google 		[Google] PROP M/F S @P<",
			"?"
		]

		List solution = [
			"PRP",
			"DET",
			"N",
			"V",
			"PRP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence17() {
		String linguateca_line ="""
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Torre	Torre=de=Bel\u00e9m	PROP_top	0	S	M/F	P<	0
de	Torre=de=Bel\u00e9m	PROP_top	0	S	M/F	P<	0
Bel\u00e9m	Torre=de=Bel\u00e9m	PROP_top	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"onde 		[onde] <interr> ADV @SA>",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 	[a] PRP @<SC",
			"Torre=de=Belém 		[Torre=de=Belém] PROP M/F S @P<",
			"?"
		]

		List solution = [
			"ADV",
			"V",
			"PRP",
			"PROP",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence18() {
		String linguateca_line ="""
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
Lisboa	Lisboa	PROP_civ	0	S	F	<SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"onde 		[onde] <interr> ADV @SA>",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"Lisboa	[Lisboa] PROP F S @<SUBJ",
			"?"
		]

		List solution = ["ADV", "V", "PROP", "PONT"]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence19() {
		String linguateca_line ="""
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
Portugal	Portugal	PROP_civ	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"onde 		[onde] <interr> ADV @SA>",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"Portugal	[Portugal] PROP F S @<SUBJ",
			"?"
		]

		List solution = ["ADV", "V", "PROP", "PONT"]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence20() {
		String linguateca_line ="""
onde	onde	ADV_aloc_interr	0	0	0	ADVS>	0
fica	ficar	V_fmc	PR_IND	3S	0	FMV	0
a	a	PRP	0	0	0	<SC	0
Google	Google	PROP_org	0	S	M/F	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"onde 		[onde] <interr> ADV @SA>",
			"fica 		[ficar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 	[a] PRP @<SC",
			"Google 		[Google] PROP M/F S @P<",
			"?"
		]

		List solution = [
			"ADV",
			"V",
			"PRP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence21() {
		String linguateca_line ="""
onde	onde	ADV_aloc_interr	0	0	0	ADVL>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"onde 		[onde] <interr> ADV @ADVL>",
			"nasceu 		[nascer] <fmc> V PS 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<SUBJ",
			"?"
		]

		List solution = [
			"ADV",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence22() {
		String linguateca_line ="""
como	como	ADV_interr	0	0	0	ADVL>	0
se	se	PERS_obj	ACC	3S	M/F	ACC>	0
chama	chamar	V_fmc	PR_IND	3S	0	FMV	0
a	o	DET_artd	0	S	F	>N	0
capital	capital	N_ac-sign_Lciv	0	S	F	<OC	0
de	de	PRP	0	0	0	N<	0
Portugal	Portugal	PROP_civ	0	S	M	P<	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"como 		[como] <interr> ADV @ADVL>",
			"se 		[se] <obj> PERS M/F 3S ACC @ACC>",
			"chama 		[chamar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"a 		[o] DET F S @>N",
			"capital 		[capital] N F S @<OC",
			"de 	[de] PRP @N<",
			"Portugal 		[Portugal] PROP M S @P<",
			"?"
		]

		List solution = [
			"ADV",
			"PERS",
			"V",
			"DET",
			"N",
			"PRP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence23() {
		String linguateca_line ="""
como	como	ADV_interr	0	0	0	ADVL>	0
se	se	PERS_obj	ACC	3S	M/F	ACC>	0
chama	chamar	V_fmc	PR_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<OC	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<OC	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"como 		[como] <interr> ADV @ADVL>",
			"se 		[se] <obj> PERS M/F 3S ACC @ACC>",
			"chama 	[chamar] <fmc> V PR 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<OC",
			"?"
		]

		List solution = [
			"ADV",
			"PERS",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence24() {
		String linguateca_line ="""
quando	quando	ADV_interr	0	0	0	ADVL>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"quando 		[quando] <interr> ADV @ADVL>",
			"nasceu 		[nascer] <fmc> V PS 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<SUBJ",
			"?"
		]

		List solution = [
			"ADV",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence25() {
		String linguateca_line ="""
quando	quando	ADV_interr	0	0	0	ADVL>	0
morreu	morrer	V_fmc	PS_IND	3S	0	FMV	0
Adolf	Adolf=Hitler	PROP_hum	0	S	M	<SUBJ	0
Hitler	Adolf=Hitler	PROP_hum	0	S	M	<SUBJ	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"quando 		[quando] <interr> ADV @ADVL>",
			"morreu 		[morrer] <fmc> V PS 3S IND VFIN @FS-QUE",
			"Adolf=Hitler 		[Adolf=Hitler] PROP M S @<SUBJ",
			"?"
		]

		List solution = [
			"ADV",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
	void testSentence26() {
		String linguateca_line ="""
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	M	>N	0
ano	ano	N_unit_dur_temp_per	0	S	M	SUBJ>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SC	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SC	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"em 		[em] PRP @ADVL>",
			"que 		[que] <interr> DET M S @>N",
			"ano 		[ano] N M S @SUBJ>",
			"nasceu 		[nascer] <fmc> V PS 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<SC",
			"?"
		]

		List solution = [
			"PRP",
			"DET",
			"N",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}

	void testSentence27() {
		String linguateca_line ="""
em	em	PRP	0	0	0	ADVL>	0
que	que	DET_interr	0	S	F	>N	0
cidade	cidade	N_Lciv	0	S	F	SUBJ>	0
nasceu	nascer	V_fmc	PS_IND	3S	0	FMV	0
Jos\u00e9	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SC	0
Saramago	Jos\u00e9=Saramago	PROP_hum	0	S	M	<SC	0
?	?	PU	0	0	0	PONT	0
	"""

		List palavras_line = [
			"em 		[em] PRP @ADVL>",
			"que 		[que] <interr> DET F S @>N",
			"cidade 		[cidade] N F S @SUBJ>",
			"nasceu 		[nascer] <fmc> V PS 3S IND VFIN @FS-QUE",
			"José=Saramago 		[José=Saramago] PROP M S @<SC",
			"?"
		]

		List solution = [
			"PRP",
			"DET",
			"N",
			"V",
			"PROP",
			"PROP",
			"PONT"
		]

		SentenceWithPoS linguateca_sentence = SentenceWithPoS.tokenizePoS(
				linguateca_line.toString().trim(), SentenceWithPoS.FROM_LINGUATECA_PoS)
		SentenceWithPoS palavras_sentence = SentenceWithPoS.tokenizePoS(
				palavras_line, SentenceWithPoS.FROM_PALAVRAS_PoS)
		assert linguateca_sentence*.type == solution, \
		"linguateca_sentence.types = "+linguateca_sentence*.type
		assert palavras_sentence*.type == solution, \
		"palavras_sentence.types = "+palavras_sentence*.type
	}
}
