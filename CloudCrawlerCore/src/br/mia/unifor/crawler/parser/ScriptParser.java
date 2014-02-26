package br.mia.unifor.crawler.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.mia.unifor.crawler.executer.artifact.Scenario;
import br.mia.unifor.crawler.executer.artifact.Scriptlet;

public class ScriptParser {
	static Logger logger = Logger.getLogger(ScriptParser.class.getName());
	
	public static Scriptlet parse(Scenario scenario, Scriptlet scriptlet, Object oThis){
		final String BEGIN = "${";
		final String END = "}";
		Scriptlet parsed = new Scriptlet();
		
		parsed.setScripts(new ArrayList<String>(scriptlet.getScripts().size()));
		for (int i = 0; i < scriptlet.getScripts().size(); i++) {
			String script = null;
			try{
				script = scriptlet.getScripts().get(i);
			}catch(ClassCastException e){
				//TODO - brutal code
				Object obj = scriptlet.getScripts().get(i);
				script = ((HashMap) obj).keySet().iterator().next()+": " + (String)((HashMap) obj).values().iterator().next();
				 
			}
			
		
			String strParsed = new String(script);
			
			
			int index = script.indexOf(BEGIN);
			while(index != -1){
				int indexEnd = strParsed.indexOf(END);
				String method = strParsed.substring(index+BEGIN.length(), indexEnd);
				logger.info(method);
				Object value = null;
				
				if(method.contains("scenarioScope")){
					if(scenario == null){
						throw new NullPointerException("the script must be executed in a scenario context, instead of Global context to use ${scenarioScope}.");
					}
					value = callMethod(method.substring("scenarioScope.".length()), scenario);
				}else{
					value = callMethod(method, oThis);
				}
				
				strParsed = strParsed.substring(0, index) + value.toString() +strParsed.substring(indexEnd+1); 
				
				index = strParsed.indexOf(BEGIN);
			}
			
			parsed.getScripts().add(strParsed);
		}
		
		return parsed;
		
	}
	
	public static Object callMethod(String method, Object target){
		
		
		String[] methods = method.split("\\.");
		
		return callMethod(0, methods, target);
	}
	
	public static Object callMethod(int index, String[] methods, Object target){
		Object retorno = null;
		
		if(index < methods.length-1){
			retorno = callMethodAction(methods[index], target);
			return callMethod(index+1, methods, retorno);
		}else{
			retorno = callMethodAction(methods[index], target);
			return retorno;
		}
	}
	
	private static Object callMethodAction(String strMethod, Object target){
		Object retorno = null;
		logger.info("method "+strMethod);
		try {
			
			Integer position = null;
			String[] substring = substring(strMethod, "[", "]");
			if(substring != null){
				position = new Integer(substring[0]);
				strMethod = substring[1];
			}
			
			String key = null;
			substring = substring(strMethod, "(", ")");
			if(substring != null){
				key = new String(substring[0]);
				strMethod = substring[1];
			}
			
			String nameMethod = "get"+strMethod.substring(0, 1).toUpperCase()+strMethod.substring(1);
			Method method = target.getClass().getMethod(nameMethod, null);
			
			if(position != null){
				retorno = ((Object[])method.invoke(target, null))[position];
			}else if(key != null){
				Map<String, Object> map = ((Map)method.invoke(target, null));
				
				return map.get(key);
					
			}else{
				retorno = method.invoke(target, null);
			}
			
			
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, "erro ao localizar o metodo", e);
		} catch (NoSuchMethodException e) {
			logger.log(Level.SEVERE, "metodo nao localizado", e);
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "nao foi possivel invocar o method", e);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "nao foi possivel invocar o method", e);
		} catch (InvocationTargetException e) {
			logger.log(Level.SEVERE, "nao foi possivel invocar o method", e);
		}
		
		return retorno;
	}
	
	private static String[] substring(String property, String beginPattern,
			String endPattern) {
		int beginIndex = property.indexOf(beginPattern);
		if (beginIndex != -1) {
			int endIndex = property.indexOf(endPattern, beginIndex);
			return new String[]{property.substring(beginIndex + beginPattern.length(),
					endIndex), property.substring(0, beginIndex)};
		} else {
			return null;
		}
	}
}
