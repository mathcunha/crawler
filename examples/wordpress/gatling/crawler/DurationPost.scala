package crawler
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class DurationPost extends Simulation{


        val httpConf = httpConfig
                        .baseURL("http://10.167.159.120/")
                        .acceptHeader("*/*")
                        .acceptEncodingHeader("gzip,deflate,sdch")
                        .acceptLanguageHeader("en,pt-BR;q=0.8,pt;q=0.6,en-US;q=0.4")
                        .userAgentHeader("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36")

        val scn = scenario("Scenario Name")
                .duration(300){
                        exec(http("new post")
                                        .post("/xmlrpc.php")
                                        .body("<?xml version='1.0'?> <methodCall> <methodName>blogger.newPost</methodName> <params> <param> <value>not empty</value> </param> <param> <value>not empty</value> </param> <param> <value>crawler-admin</value> </param> <param> <value>crawler-admin</value> </param>  <param> <value> segundo post xmlrpc ${post_id} matheus</value> </param>  <param> <value>true</value> </param> </params> </methodCall>")
                                        .check(regex("""<int>([^"]*)</int>""").saveAs("post_id"))
                        )
                        .pause(0, 2)
                        .exec(http("view post")
                                .post("/xmlrpc.php")
                                .body("<?xml version='1.0'?> <methodCall> <methodName>blogger.getPost</methodName> <params> <param> <value>not empty</value> </param> <param> <value>${post_id}</value> </param> <param> <value>crawler-admin</value> </param> <param> <value>crawler-admin</value> </param> </params> </methodCall>")
                                .check(regex("""<string>${post_id}</string>""").exists)
                        )
                        .pause(0, 4)
                        .exec(http("edit post")
                                .post("/xmlrpc.php")
                                .body("<?xml version='1.0'?> <methodCall> <methodName>blogger.editPost</methodName> <params> <param> <value>not empty</value> </param> <param> <value>${post_id}</value> </param> <param> <value>crawler-admin</value> </param> <param> <value>crawler-admin</value> </param>  <param> <value>editado ${post_id} segundo post xmlrpc </value> </param>  <param> <value>true</value> </param> </params> </methodCall>")
                                .check(regex("""<boolean>1</boolean>""").exists)
                        )
                        .exec(http("find posts")
                                .post("/xmlrpc.php")
                                .body("<?xml version='1.0'?> <methodCall> <methodName>blogger.getRecentPosts</methodName> <params> <param> <value>not empty</value> </param> <param> <value>not empty</value> </param> <param> <value>crawler-admin</value> </param> <param> <value>crawler-admin</value> </param>  <param> <value>20</value> </param> </params> </methodCall>")
                                .check(regex("""<member><name>postid</name><value><string>[0-9]*</string></value></member>""").saveAs("post_id_found"))
                        )
                        .pause(0, 1)
                        .exec(http("edit post")
                                .post("/xmlrpc.php")
                                .body("<?xml version='1.0'?> <methodCall> <methodName>blogger.editPost</methodName> <params> <param> <value>not empty</value> </param> <param> <value>${post_id_found}</value> </param> <param> <value>crawler-admin</value> </param> <param> <value>crawler-admin</value> </param>  <param> <value>editado POR ${post_id} terceiro post xmlrpc</value> </param>  <param> <value>true</value> </param> </params> </methodCall>")
                                .check(regex("""<boolean>1</boolean>""").exists)
                        )
                }

        setUp(scn.users(1000).ramp(100).protocolConfig(httpConf))
}
