package br.mia.unifor.crawlerenvironment.engine.view.rest.resources;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class CloudCrawlerEnvironmentResource {

	protected static Logger logger = Logger
			.getLogger(CloudCrawlerEnvironmentResource.class.getName());

	public String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
