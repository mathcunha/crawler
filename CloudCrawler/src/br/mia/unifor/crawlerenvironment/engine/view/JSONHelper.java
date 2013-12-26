package br.mia.unifor.crawlerenvironment.engine.view;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import br.mia.unifor.crawlerenvironment.mom.event.CloudCrawlerEnvironmentEvent;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONHelper {
	public static String getJSON(List list){
		return JSONArray.fromObject(list).toString();
	}
	
	public static String getJSON(Object obj){
		return JSONObject.fromObject(obj).toString();
	}
	
	public static CloudCrawlerEnvironmentEvent getCloudCrawlerEnvironmentEvent(String json){
		JSONObject jsonObject = JSONObject.fromObject( json );  
		Object bean = JSONObject.toBean( jsonObject );
		
		CloudCrawlerEnvironmentEvent event;
		
		try {
			event = new CloudCrawlerEnvironmentEvent( (String)PropertyUtils.getProperty( bean, "action" ));
			
			return event;
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
