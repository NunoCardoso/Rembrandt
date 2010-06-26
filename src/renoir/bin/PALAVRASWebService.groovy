package renoir.bin

import org.apache.log4j.*
import renoir.util.SHA1

/* Esta classe é para ficar em UTF-8! */
class PALAVRASWebService {

 static String parse(String sentence) {
	def key 
 	def passwd
 	def auth_chap
 	def auth_passwd
 	def auth_status
 	def VISLsessionID
	def Logger log = Logger.getLogger("RenoirMain")
 
	new File('private/palavras_key').withReader { key = it.readLine().trim() } 
	new File('private/palavras_passwd').withReader { passwd = it.readLine().trim() } 

	def file1 = new URL("http://beta.visl.sdu.dk/tools/remoting/?auth_key=$key").getText()
	file1.find(/.*VISLSessionID=([a-z0-9]+)\nauth_chap=([a-z0-9]+).*/) {all, g1, g2 -> 
      VISLsessionID=g1
      auth_chap=g2
	}

	log.trace "Got VISLsessionID: $VISLsessionID"
	log.trace "Got auth_chap: $auth_chap"
	if (!VISLsessionID || !auth_chap) {
   		log.fatal "No auth_chap or VISLsessionID... Exiting... "
   		System.exit(0)
	}

	auth_passwd = SHA1.convert(SHA1.convert(passwd) + auth_chap);
	log.trace "Auth_passwd: $auth_passwd"

	def url2 = "http://beta.visl.sdu.dk/tools/remoting/?VISLSessionID="+
     java.net.URLEncoder.encode(VISLsessionID)+"&auth_password="+	
     java.net.URLEncoder.encode(auth_passwd)

	log.trace "Fetching $url2"
	def file2 = new URL(url2).getText()
	file2.find(/.*auth_status=([a-z0-9]+).*/) {all, g1 ->
    	auth_status=g1
	}

	log.trace "Got status: $auth_status"
	if (auth_status != "1") {
   		log.fatal "Authentication error!"
   		System.exit(0)
	}
	def url3 = 'http://beta.visl.sdu.dk/tools/remoting/?VISLSessionID='+
    	java.net.URLEncoder.encode(VISLsessionID)+
		'&lang=pt&mode=flat&text='+java.net.URLEncoder.encode(sentence)
	log.trace "Fetching url: $url3"
	def file3 = new URL(url3).getText()
	return file3.split(/\#\d+\->\d+/).toList().collect{it.trim()}
	
  }

	static main(args) {
		println PALAVRASWebService.parse(args[0])
	/*	println PALAVRASWebService.parse("Que rápidos aparecem nos filmes adaptados da obra \"O último dos moicanos\"")
		println PALAVRASWebService.parse("Indique membros do círculo de Viena que nasceram fora do império austro-húngaro ou da Alemanha ")
		println PALAVRASWebService.parse("Rios portugueses que passam por cidades com mais de 150 mil habitantes ")
		println PALAVRASWebService.parse("Cantões suíços que façam fronteira com a Alemanha ") 
		println PALAVRASWebService.parse("Que guerras foram travadas na Grécia? ")
		println PALAVRASWebService.parse("Montanhas da Austrália com mais de 2000 m de altitude ")
		println PALAVRASWebService.parse("Capitais africanas com mais de 2 milhões de habitantes ")
		println PALAVRASWebService.parse("Pontes pênseis brasileiras")
		println PALAVRASWebService.parse("Compositores renascentistas nascidos na Alemanha")
		println PALAVRASWebService.parse("Ilhas da Polinésia com mais de 5000 habitantes")
		println PALAVRASWebService.parse("Peças de Shakespeare passadas na Itália ")
		println PALAVRASWebService.parse("Locais onde Goethe viveu")
		println PALAVRASWebService.parse("Rios navegáveis no Afeganistão com uma extensão maior do que 1000 km. ")
		println PALAVRASWebService.parse("Arquitectos brasileiros com construções na Europa")
		println PALAVRASWebService.parse("Que pontes francesas foram construídas na década de 1980-1990?")	
*/	}
}

//System.properties.putAll( ["http.proxyHost":"proxy-host", "http.proxyPort":"proxy-port","http.proxyUserName":"user-name", "http.proxyPassword":"proxy-passwd"] )

 
