package cn.tt100.base.example;

import java.util.List;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.tt100.base.BaseActivity;
import cn.tt100.base.annotation.AutoInitialize;
import cn.tt100.base.annotation.AutoOnClick;
import cn.tt100.base.example.bean.City;

public class RestActivity extends BaseActivity {
	@AutoInitialize(idFormat = "rest_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button testBtn,jsonTestBtn;
	
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
		String url = "http://www.dcai100.com:8080/rs/cities";

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			String result = restTemplate.getForObject(url, String.class, "");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n"+result);
		}
	}

	
	class RestJsonGETTask extends AsyncTask<Void, Void, City[]>{
		String url = "http://www.dcai100.com:8080/rs/cities";

		@Override
		protected City[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			RestTemplate restTemplate = new RestTemplate();
			ObjectMapper m;
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			City[] result = restTemplate.getForObject(url, City[].class);
			return result;
		}

		@Override
		protected void onPostExecute(City[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n"+result);
		}
	}
}
