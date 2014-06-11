package cn.shrek.base.example.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.client.HttpMessageConverterExtractor;

import com.alibaba.fastjson.JSON;

public class MineJSONConverter implements HttpMessageConverter<MineBo> {

	@Override
	public boolean canRead(Class<?> arg0, MediaType arg1) {
		// TODO Auto-generated method stub
		if(arg1 != null && (arg1.includes(MediaType.APPLICATION_JSON) 
				|| arg1.includes(MediaType.TEXT_HTML)) ){
			return true;
		}
		return false;
	}

	@Override
	public boolean canWrite(Class<?> arg0, MediaType arg1) {
		// TODO Auto-generated method stub
		System.out.println("111111111canWrite");
		return false;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		// TODO Auto-generated method stub
		System.out.println("111111111getSupportedMediaTypes");
		return null;
	}

	@Override
	public MineBo read(Class<? extends MineBo> arg0, HttpInputMessage arg1)
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
		
		MineBo mMineBo = JSON.parseObject(sb.toString(), arg0);
		
		return mMineBo;
	}

	@Override
	public void write(MineBo arg0, MediaType arg1, HttpOutputMessage arg2)
			throws IOException, HttpMessageNotWritableException {
		// TODO Auto-generated method stub
		System.out.println("111111111write");
	}


}
