package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class Result {
	private List<MetricEval> metrics;

	public List<MetricEval> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<MetricEval> metrics) {
		this.metrics = metrics;
	}
}
