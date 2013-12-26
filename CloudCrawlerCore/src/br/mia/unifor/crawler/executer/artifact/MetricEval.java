package br.mia.unifor.crawler.executer.artifact;

import java.util.List;

public class MetricEval extends CrawlerArtifact {
	private Metric metric;
	private String workload;
	private String vmId;
	private List<CollectedMetric> collectedMetrics;

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public String getWorkload() {
		return workload;
	}

	public void setWorkload(String workload) {
		this.workload = workload;
	}

	public String getVmId() {
		return vmId;
	}

	public void setVmId(String vmId) {
		this.vmId = vmId;
	}

	public List<CollectedMetric> getCollectedMetrics() {
		return collectedMetrics;
	}

	public void setCollectedMetrics(List<CollectedMetric> collectedMetrics) {
		this.collectedMetrics = collectedMetrics;
	}

	public static class CollectedMetric {
		private String value;
		private Long timestamp;

		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
