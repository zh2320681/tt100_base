package cn.tt100.base.util.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * ���������
 * @author shrek
 *
 */
public class ZWRequestConfig {
	private static ZWRequestConfig defaultConfig;
	
	private Map<String,String> headers;
	private Map<String,Object> maps;  //�����б�
	private Object body;
	public HttpMethod httpMethod;
	public HttpMessageConverter<?> converter;
	
	public String url;
	public Class<?> parseClazz;
	//����ʱ�� �Ƿ����б�
	public boolean isList;
	
	public ZWRequestConfig(HttpMethod httpMethod,HttpMessageConverter<?> converter){
		super();
		headers = new HashMap<String, String>();
		maps = new HashMap<String, Object>();
		
		this.httpMethod = httpMethod;
		this.converter = converter;
	}
	
	/**
	 * ����Ĭ�ϵ� ����
	 * @return
	 */
	public static ZWRequestConfig copyDefault(){
		if(defaultConfig == null){
			throw new NullPointerException("You must set default request config!");
		}
		ZWRequestConfig config = new ZWRequestConfig(defaultConfig.httpMethod,defaultConfig.converter);
		config.headers.putAll(defaultConfig.headers);
		config.maps.putAll(defaultConfig.maps);
		config.body = defaultConfig.body;
		return config;
	}
	
	public static void setDefault(ZWRequestConfig config){
		if(config != null)
		defaultConfig = config;
	}
	
	public void putValue(String key,Object value){
		maps.put(key, value);
	}
	
	public void putHeaderValue(String key,String value){
		headers.put(key, value);
	}
	
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	

	public Map<String, Object> getMaps() {
		return maps;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

}
