package crawler 
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class Initialize extends Simulation {

	val httpConf = httpConfig
			.baseURL("http://ec2-54-197-147-237.compute-1.amazonaws.com")
			.acceptHeader("*/*")
			.userAgentHeader("curl/7.29.0")


	val headers_1 = Map(
			"Content-Type" -> """application/x-www-form-urlencoded"""
	)


	val scn = scenario("Scenario Name")
		.exec(http("request_1")
					.post("/xmlrpc.php")
					.headers(headers_1)
						.param("""<?xml version""", """"1.0"?> <methodCall> <methodName>blogger.getUsersBlogs</methodName> <params> <param> <value>not empty</value> </param> <param> <value>crawler-admin</value> </param> <param> <value>crawler-admin</value> </param> </params> </methodCall>""")
			)

	setUp(scn.users(1).protocolConfig(httpConf))
}
