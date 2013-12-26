package br.mia.unifor.crawlerenvironment.mom.event;

public class CloudCrawlerEnvironmentEvent {
	public static String ACTION_NEW = "new";
	public static String ACTION_SUSPEND = "suspend";
	public static String ACTION_RESUME = "resume";
	public static String ACTION_ABORT = "abort";
	public static String ACTION_END = "end";
	
	private String action;
	
	public CloudCrawlerEnvironmentEvent(){
		
	}
	
	public CloudCrawlerEnvironmentEvent(String action){
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
