package cn.shrek.base.util.rest.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import cn.shrek.base.util.ZWLogger;

public class StringJSONConverter implements HttpMessageConverter<String> {

	@Override
	public boolean canRead(Class<?> arg0, MediaType arg1) {
		// TODO Auto-generated method stub
		if(arg1 != null && arg1.includes(MediaType.APPLICATION_JSON) ){
			return true;
		}
		return false;
	}

	@Override
	public boolean canWrite(Class<?> arg0, MediaType arg1) {
		// TODO Auto-generated method stub
		if(arg1 != null && arg1.includes(MediaType.APPLICATION_JSON) ){
			return true;
		}
		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String read(Class<? extends String> arg0, HttpInputMessage arg1)
			throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(arg1.getBody(),"utf-8"));
		String line = "";
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			// line = new String(line.getBytes(), "utf-8");
			sb.append(line);
		}
		reader.close();
		
//		Result result = JSON.parseObject(sb.toString(), arg0);
		ZWLogger.printLog(this, "接收到的JSON数据:"+sb.toString());
		return sb.toString();
	}

	@Override
	public void write(String arg0, MediaType arg1, HttpOutputMessage arg2)
			throws IOException, HttpMessageNotWritableException {
		// TODO Auto-generated method stub
		ZWLogger.printLog(this, "输入的body数据:"+arg0);
		BufferedWriter bWriter = null;
		try{
		bWriter =  new BufferedWriter(new OutputStreamWriter(arg2.getBody()));
		bWriter.write(arg0);
		bWriter.flush();
		}finally{
			if(bWriter != null) bWriter.close();
		}
	}


}
