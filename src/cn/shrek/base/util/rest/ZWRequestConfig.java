package cn.shrek.base.util.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;

import cn.shrek.base.util.ZWLogger;
import cn.shrek.base.util.rest.converter.StringJSONConverter;

/**
 * 请求的配置
 * @author shrek
 *
 */
public class ZWRequestConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6463246596485626413L;
	
	public static final String UTF8_CHARSET = "utf-8";
	public static final String GBK_CHARSET = "gbk";
	public static final String GB2312_CHARSET = "gb2312";
	public static final String ISO_CHARSET = "ISO-8859-1";
	//超时间
	/** 读取超时时间,单位:毫秒 */
	public static final int READ_TIME_OUT = 30000;
	/** 连接超时时间 ,单位:毫秒 */
	public static final int CONN_TIME_OUT = 30000;
	
	
	private static ZWRequestConfig defaultConfig;
	
	private String uniqueKey;//唯一识别码
	
	private String urlCharset; //编码
	private Map<String,String> headers;
	private Map<String,Object> maps;  //参数列表
	private Object body;
	public int connTimeOut,readTimeOut;
	
	
	/**
	 * Spring 支持这样 参数类型
	 */
	private Object[] paras;
	
	
	public HttpMethod httpMethod;
	public HttpMessageConverter<?> converter;
	
	public String url;
	public Class<?> parseClazz;
	//解析时候 是否是列表
//	public boolean isList;
	
	public ZWRequestConfig(HttpMethod httpMethod,HttpMessageConverter<?> converter,String charset){
		super();
		headers = new HashMap<String, String>();
		maps = new HashMap<String, Object>();
		
		this.httpMethod = httpMethod;
		this.converter = converter;
		
		this.connTimeOut = CONN_TIME_OUT;
		this.readTimeOut = READ_TIME_OUT;
		
		if(Charset.isSupported(charset)){
			this.urlCharset = charset;
		}else{
			throw new IllegalArgumentException("不支持的编码方式:"+charset);
		}
	}
	
	
	public ZWRequestConfig(HttpMethod httpMethod){
		this(httpMethod,new StringJSONConverter(),UTF8_CHARSET);
	}
	
	public ZWRequestConfig(HttpMethod httpMethod,HttpMessageConverter<?> converter){
		this(httpMethod,converter,UTF8_CHARSET);
	}
	
	/**
	 * 拷贝默认的 请求
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
//		ByteArrayOutputStream bos = null;
//		ObjectOutputStream oos = null;
//		ByteArrayInputStream bis = null;
//		ObjectInputStream ois = null;
//		try {
//			/* 写入当前对象的二进制流 */  
//			bos = new ByteArrayOutputStream();  
//			oos = new ObjectOutputStream(bos);  
//			oos.writeObject(defaultConfig);  
//			oos.flush();
//			/* 读出二进制流产生的新对象 */  
//			bis = new ByteArrayInputStream(bos.toByteArray());  
//			ois = new ObjectInputStream(bis);  
//			return (ZWRequestConfig)(ois.readObject());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  finally{
//			try{if(bos != null)bos.close();}catch(Exception e){}
//			try{if(oos != null)bos.close();}catch(Exception e){}
//			try{if(bis != null)bos.close();}catch(Exception e){}
//			try{if(ois != null)bos.close();}catch(Exception e){}
//		}
//		return null;
	}
	
	public static void setDefault(ZWRequestConfig config){
		if(config != null)
		defaultConfig = config;
	}
	
	public void putValue(String key,Object value){
		if(isUrlEnCode() && value instanceof String){
			try {
				maps.put(key, URLEncoder.encode(value.toString(), urlCharset));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(ZWRequestConfig.this, "添加value时编码出现错误!");
			}
		}else{
			maps.put(key, value);
		}
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

	
	
	public Object[] getParas() {
		return paras;
	}


	public void setParas(Object[] paras) {
		Object[] objs = new Object[paras.length];
		for (int i = 0; i < paras.length; i++) {
			Object obj = paras[i];
			if(obj instanceof String){
				try {
					objs[i] = URLEncoder.encode(obj.toString(), urlCharset);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}else{
				objs[i] = obj;
			}
		}
		this.paras = objs;
	}


	/**
	 * 是否对 url进行编码
	 * @return
	 */
	private boolean isUrlEnCode(){
		return urlCharset != null && !"".equals(urlCharset);
	}
	
	/**
	 * 得到唯一 识别码
	 * 请求的 "UniqueKey"+（URL+（所有参数）+body.toString）.hashCode()
	 * @return
	 */
	public String getUniqueKey(){
		if(uniqueKey == null){
			StringBuffer sb = new StringBuffer();
			if(paras != null){
				for(Object obj : paras){
					sb.append("$"+obj.toString()+"$");
				}
			}else{
				for(Map.Entry<String, Object> entry : maps.entrySet()){
					sb.append("$"+entry.getValue().toString()+"$");
				}
			}
			uniqueKey ="UniqueKey"+(url+sb.toString()+(body!=null?body.toString():"")).hashCode();
		}
		return uniqueKey;
	}
}
