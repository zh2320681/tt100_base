package cn.shrek.base.util.rest.converter;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MultiValueMap;

public class FormJsonConverter extends FormHttpMessageConverter {

	@Override
	public boolean canRead(Class<?> arg0, MediaType arg1) {
		// TODO Auto-generated method stub
		if(arg1 != null && arg1.includes(MediaType.APPLICATION_JSON)){
			return true;
		}
		return super.canRead(arg0, arg1);
	}
	
	
	@Override
	public MultiValueMap<String, String> read(
			Class<? extends MultiValueMap<String, ?>> arg0,
			HttpInputMessage arg1) throws IOException,
			HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		MultiValueMap<String, String> map = super.read(arg0, arg1);
		return map;
	}
}
