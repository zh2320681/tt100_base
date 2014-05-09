package cn.tt100.base.example;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.tt100.base.ZWActivity;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.example.bean.JSONConverter;
import cn.tt100.base.example.bean.Result;

public class RestActivity extends ZWActivity {
	@AutoInitialize(idFormat = "rest_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button testBtn,jsonTestBtn,custonTestBtn;
	
	@AutoInitialize(idFormat = "rest_?")
	private TextView infoView;

	private OnClickListener mClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0 == testBtn){
				RestGETTask task = new RestGETTask();
				task.execute();
			}else if(arg0 == jsonTestBtn){
				RestJsonGETTask task = new RestJsonGETTask();
				task.execute();
			}else if(arg0 == custonTestBtn){
				RestCustomJsonGETTask task = new RestCustomJsonGETTask();
				task.execute();
			}
		}
	};
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}
	
	
	class RestGETTask extends AsyncTask<Void, Void, String>{
		String url = "http://www.baidu.com";

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			RestTemplate restTemplate = new RestTemplate();
			//ByteArrayHttpMessageConverter
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			String result = restTemplate.getForObject(url, String.class, "");
			
//			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n"+result);
		}
	}

	
	class RestJsonGETTask extends AsyncTask<Void, Void, Result>{
		String url = "http://119.15.137.138:80/rs/showrooms?pageNo=1&pageSize=100";

		@Override
		protected Result doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			ResponseEntity<Result> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Result.class);
			Result result = responseEntity.getBody();
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n"+result);
		}
	}
	
	
	class RestCustomJsonGETTask extends AsyncTask<Void, Void, Result>{
		String url = "http://119.15.137.138:80/rs/showrooms?pageNo=1&pageSize=100";

		@Override
		protected Result doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new JSONConverter());
			ResponseEntity<Result> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Result.class);
			Result result = responseEntity.getBody();
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n"+result);
		}
	}
}
