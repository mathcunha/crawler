package br.mia.unifor.crawler.executer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import br.mia.unifor.crawler.executer.artifact.Benchmark;
import br.mia.unifor.crawler.executer.artifact.MetricEval;
import br.mia.unifor.crawler.executer.artifact.Scenario;

public class BenchmarkPartialResult {
	protected static Logger logger = Logger
			.getLogger(BenchmarkPartialResult.class.getName());
	private Benchmark benchmark;
	private String description;
	private Boolean success;
	private Long when;
	private String workload;
	private Scenario scenario;
	private List<MetricEval> metricsEval;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Long getWhen() {
		return when;
	}

	public void setWhen(Long when) {
		this.when = when;
	}

	public String getWorkload() {
		return workload;
	}

	public void setWorkload(String workload) {
		this.workload = workload;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public static void publishResult(BenchmarkPartialResult execution,
			String httpEndPoint) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpost = new HttpPost(httpEndPoint);

			String body = "{\"results\":"
					+ JSONObject.fromObject(execution).toString() + "}";

			logger.info(body);

			StringEntity stringEntity = new StringEntity(body, "UTF-8");

			stringEntity.setContentType("application/json");

			httpost.setEntity(stringEntity);

			HttpResponse response = httpclient.execute(httpost);

			HttpEntity entity = response.getEntity();

			logger.info(response.getStatusLine().toString());
			if (entity != null) {
				logger.info(convertStreamToString(entity.getContent()));
			}

			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "", e);

		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "", e);

		} finally {
			httpclient.getConnectionManager().shutdown();
		}

	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	public List<MetricEval> getMetricsEval() {
		return metricsEval;
	}

	public void setMetricsEval(List<MetricEval> metricsEval) {
		this.metricsEval = metricsEval;
	}

	public Benchmark getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(Benchmark benchmark) {
		this.benchmark = benchmark;
	}
}
