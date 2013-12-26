package br.mia.unifor.crawlerenvironment;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ConsoleTailerListener extends TailerListenerAdapter {
	
	protected final Properties properties;
	public ConsoleTailerListener(final Properties properties){
		this.properties = properties;
	}
	
	public void handle(String line) {
		String body = "{\"author\":\"system\",\"message\":\""+line+"\"}";
		publishResult(body, "http://" + "127.0.0.1" + ":"+ properties.getProperty("port")+"/CloudCrawler/console");
	}

	public void publishResult(String body,
			String httpEndPoint) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpost = new HttpPost(httpEndPoint);

			StringEntity stringEntity = new StringEntity(body, "UTF-8");

			stringEntity.setContentType("application/json");

			httpost.setEntity(stringEntity);

			HttpResponse response = httpclient.execute(httpost);

			HttpEntity entity = response.getEntity();

			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "", e);

		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "", e);

		} finally {
			httpclient.getConnectionManager().shutdown();
		}

	}
	
	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("crawlerweb.properties"));

		
		(new ConsoleTailerListener(properties)).handle("Toma "+System.currentTimeMillis());
		
	}
}
