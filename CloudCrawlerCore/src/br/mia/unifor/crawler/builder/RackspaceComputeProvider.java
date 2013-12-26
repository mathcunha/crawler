package br.mia.unifor.crawler.builder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jclouds.compute.domain.Template;

import br.mia.unifor.crawler.executer.artifact.Provider;
import br.mia.unifor.crawler.executer.artifact.VirtualMachine;

public class RackspaceComputeProvider extends ComputeProvider {

	protected String authToken;
	protected String serverUrl;

	public RackspaceComputeProvider(Provider provider) throws IOException {
		super(provider);
		loadCredentials(provider);
	}

	private void loadCredentials(Provider provider)
			throws ClientProtocolException, IOException {

		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			Properties properties = new Properties();
			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(provider.getCredentialPath());
			if(input == null){			
				input = new FileInputStream(provider.getCredentialPath());
			}
			properties.load(input);

			HttpGet httpget = new HttpGet(
					"https://auth.api.rackspacecloud.com/v1.0");

			httpget.setHeader("X-Auth-Key", properties.getProperty("secretKey"));
			httpget.setHeader("X-Auth-User",
					properties.getProperty("accessKey"));

			HttpResponse response = httpclient.execute(httpget);

			authToken = response.getFirstHeader("X-Auth-Token").getValue();
			serverUrl = response.getFirstHeader("X-Server-Management-Url")
					.getValue();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	@Override
	protected void startInstanceAction(VirtualMachine instance) {
		context.getComputeService().rebootNode(instance.getId());
	}

	@Override
	protected void stopInstanceAction(VirtualMachine instance) {
		context.getComputeService().rebootNode(instance.getId());
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	@Override
	public boolean changeInstanceType(VirtualMachine instance) {
		String body = "{\"resize\" : {\"flavorId\" : "+instance.getType().getProviderProfile()+"}}";
		return doAction(body, "/servers/"+instance.getId()+"/action") ;
	}
	public boolean changeInstanceType(String id) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			HttpGet httpget = new HttpGet(serverUrl + "/servers/"+id);

			httpget.setHeader("X-Auth-Token", authToken);

			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
				System.out.println("Response content length: "
						+ entity.getContentLength());
				
				System.out.println(convertStreamToString(entity.getContent()));
			}
			EntityUtils.consume(entity);

		} catch (ClientProtocolException e) {
			logger.log(Level.SEVERE, "", e);
			return false;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
			return false;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return true;
	}
	
	public String getServerDetail(String id) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			HttpGet httpget = new HttpGet(serverUrl + "/servers/"+id);

			httpget.setHeader("X-Auth-Token", authToken);

			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
				System.out.println("Response content length: "
						+ entity.getContentLength());
				String retorno = convertStreamToString(entity.getContent());
				System.out.println(retorno);
				return retorno;
			}
			EntityUtils.consume(entity);

		} catch (ClientProtocolException e) {
			logger.log(Level.SEVERE, "", e);			
		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return "";
	}
	
	public boolean doAction(String body, String action) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String url = serverUrl + action;
			System.out.println(url);
			HttpPost httpost = new HttpPost(url);

			StringEntity stringEntity = new StringEntity(body, "UTF-8");
			
			stringEntity.setContentType("application/json");
			
			httpost.setEntity(stringEntity);
			httpost.setHeader("X-Auth-Token", authToken);

			HttpResponse response = httpclient.execute(httpost);

			HttpEntity entity = response.getEntity();

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
				System.out.println("Response content length: "
						+ entity.getContentLength());
				
				System.out.println(convertStreamToString(entity.getContent()));
			}
			EntityUtils.consume(entity);

		} catch (ClientProtocolException e) {
			logger.log(Level.SEVERE, "", e);
			return false;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "", e);
			return false;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return true;
	}
	
	public void waitState(String id, String state){
		while(getServerDetail(id).indexOf(state) == -1){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		Provider prov = new Provider();
		prov.setName("cloudservers-us");
		prov.setCredentialPath("/br/RackspaceCredentials.properties");
		try {
			RackspaceComputeProvider rack = new RackspaceComputeProvider(prov);
			
			rack.changeInstanceType("20400657");
			
			//String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><reboot xmlns=\"http://docs.rackspacecloud.com/servers/api/v1.0\" type=\"HARD\"/>";
			//String body = "{\"reboot\" : {\"type\" : \"SOFT\"}}";
			//String body = "{\"resize\" : {\"flavor\" : "+3+"}}";
			String body = "{\"resize\" : {\"flavorId\" : 3}}";
			//String body = "{\"revertResize\" : null}";
			//String body = "{\"confirmResize\" : null}";

			
			//rack.doAction(body, "/servers/20400657/action");
			
			rack.waitState("20400657","VERIFY_RESIZE");
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getTemplate(Template template) {

	}
	
}
