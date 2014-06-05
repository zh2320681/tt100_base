package cn.tt100.base.util.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;

import cn.tt100.base.util.ZWLogger;
import cn.tt100.base.util.rest.converter.StringJSONConverter;

/**
 * ���������
 * @author shrek
 *
 */
public class ZWRequestConfig {
	public static final String UTF8_CHARSET = "utf-8";
	public static final String GBK_CHARSET = "gbk";
	public static final String GB2312_CHARSET = "gb2312";
	public static final String ISO_CHARSET = "ISO-8859-1";
	//��ʱ��
	/** ��ȡ��ʱʱ��,��λ:���� */
	public static final int READ_TIME_OUT = 30000;
	/** ���ӳ�ʱʱ�� ,��λ:���� */
	public static final int CONN_TIME_OUT = 30000;
	
	
	private static ZWRequestConfig defaultConfig;
	
	private String uniqueKey;//Ψһʶ����
	
	private String urlCharset; //����
	private Map<String,String> headers;
	private Map<String,Object> maps;  //�����б�
	private Object body;
	public int connTimeOut,readTimeOut;
	
	
	/**
	 * Spring ֧������ ��������
	 */
	private Object[] paras;
	
	
	public HttpMethod httpMethod;
	public HttpMessageConverter<?> converter;
	
	public String url;
	public Class<?> parseClazz;
	//����ʱ�� �Ƿ����б�
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
			throw new IllegalArgumentException("��֧�ֵı��뷽ʽ:"+charset);
		}
	}
	
	
	public ZWRequestConfig(HttpMethod httpMethod){
		this(httpMethod,new StringJSONConverter(),UTF8_CHARSET);
	}
	
	public ZWRequestConfig(HttpMethod httpMethod,HttpMessageConverter<?> converter){
		this(httpMethod,converter,UTF8_CHARSET);
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
		if(isUrlEnCode() && value instanceof String){
			try {
				maps.put(key, URLEncoder.encode(value.toString(), urlCharset));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				ZWLogger.printLog(ZWRequestConfig.this, "���valueʱ������ִ���!");
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
	 * �Ƿ�� url���б���
	 * @return
	 */
	private boolean isUrlEnCode(){
		return urlCharset != null && !"".equals(urlCharset);
	}
	
	/**
	 * �õ�Ψһ ʶ����
	 * ����� "UniqueKey"+��URL+�����в�����+body.toString��.hashCode()
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
