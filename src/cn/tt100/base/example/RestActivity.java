package cn.tt100.base.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cn.tt100.base.example.bean.GalleryDate;
import cn.tt100.base.example.bean.JSONConverter;
import cn.tt100.base.example.bean.Result;
import cn.tt100.base.util.rest.DialogTaskHandler;
import cn.tt100.base.util.rest.DoNothingHandler;
import cn.tt100.base.util.rest.ZWAsyncTask;
import cn.tt100.base.util.rest.ZWRequestConfig;
import cn.tt100.base.util.rest.ZWResult;

public class RestActivity extends ZWActivity {
	@AutoInitialize(idFormat = "rest_?")
	@AutoOnClick(clickSelector = "mClick")
	private Button testBtn, jsonTestBtn, custonTestBtn, asyncTestBtn,
			queneTestBtn;

	@AutoInitialize(idFormat = "rest_?")
	private TextView infoView;

	private OnClickListener mClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0 == testBtn) {
				RestGETTask task = new RestGETTask();
				task.execute();
			} else if (arg0 == jsonTestBtn) {
				RestJsonGETTask task = new RestJsonGETTask();
				task.execute();
			} else if (arg0 == custonTestBtn) {
				RestCustomJsonGETTask task = new RestCustomJsonGETTask();
				task.execute();
			} else if (arg0 == asyncTestBtn) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("pageNo", "1");
				map.put("pageSize", "2");

				ZWAsyncTask
						.excuteTask(
								RestActivity.this,
								"http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								HttpMethod.GET,
								Result.class,
								map,
								new DialogTaskHandler<Result>("请求", "请求测试中...") {

									@Override
									public void postResult(
											ZWResult<Result> result) {
										// TODO Auto-generated method stub
										StringBuffer sb = new StringBuffer();
										sb.append("请求吗是:"
												+ result.requestCode.value()
												+ "\n");
										for (GalleryDate g : result.bodyObj.data) {
											sb.append("数据：：:" + g.toString()
													+ "\n");
										}
										infoView.setText(sb.toString());
									}
								});

				ZWAsyncTask
						.excuteTask(
								RestActivity.this,
								"http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								List.class,
								new DialogTaskHandler<List<StringBuffer>>("请求",
										"请求测试中...") {

											@Override
											public void postResult(
													ZWResult<List<StringBuffer>> result) {
												// TODO Auto-generated method stub
												
											}

								});
				// ZWAsyncTask.excuteTask(RestActivity.this,
				// "http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
				// HttpMethod.GET,Result.class,map, new
				// DoNothingHandler<Result>(){
				//
				// @Override
				// public void postResult(ZWResult<Result> result) {
				// // TODO Auto-generated method stub
				// super.postResult(result);
				// StringBuffer sb = new StringBuffer();
				// sb.append("请求吗是:"+result.requestCode.value()+"\n");
				// for(GalleryDate g : result.bodyObj.data){
				// sb.append("数据：：:"+g.toString() +"\n");
				// }
				// infoView.setText(sb.toString());
				// }
				//
				// });
			} else if (arg0 == queneTestBtn) {
				ZWAsyncTask<Result> task1 = new ZWAsyncTask<Result>(
						RestActivity.this, new DoNothingHandler<Result>());
				task1.config = ZWRequestConfig.copyDefault();
				task1.config.url = "http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}";
				Map<String, String> map = new HashMap<String, String>();
				map.put("pageNo", "1");
				map.put("pageSize", "2");
				task1.config.getMaps().putAll(map);

				ZWAsyncTask<Result> task2 = new ZWAsyncTask<Result>(
						RestActivity.this, new DialogTaskHandler<Result>("测试",
								"测试测试") {

							@Override
							public void postResult(ZWResult<Result> result) {
								// TODO Auto-generated method stub

							}
						});
				task2.config = task1.config;

				ZWAsyncTask<Result> task3 = new ZWAsyncTask<Result>(
						RestActivity.this, new DoNothingHandler<Result>());
				task3.config = task1.config;

				ZWAsyncTask.addTaskIntoQueueAndExcute(task1, task2, task3);
			}

		}
	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		ZWRequestConfig config = new ZWRequestConfig(HttpMethod.GET,
				new JSONConverter());
		config.putHeaderValue("accept", "application/json");
		ZWRequestConfig.setDefault(config);
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

	class RestGETTask extends AsyncTask<Void, Void, String> {
		String url = "http://www.baidu.com";

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			RestTemplate restTemplate = new RestTemplate();
			// ByteArrayHttpMessageConverter
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String result = restTemplate.getForObject(url, String.class, "");

			// restTemplate.getMessageConverters().add(new
			// StringHttpMessageConverter());
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n" + result);
		}
	}

	class RestJsonGETTask extends AsyncTask<Void, Void, Result> {
		String url = "http://119.15.137.138:80/rs/showrooms?pageNo=1&pageSize=100";

		@Override
		protected Result doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept(Collections.singletonList(new MediaType(
					"application", "json")));
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new MappingJackson2HttpMessageConverter());
			ResponseEntity<Result> responseEntity = restTemplate.exchange(url,
					HttpMethod.GET, requestEntity, Result.class);
			Result result = responseEntity.getBody();
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n" + result);
		}
	}

	class RestCustomJsonGETTask extends AsyncTask<Void, Void, Result> {
		String url = "http://119.15.137.138:80/rs/showrooms?pageNo=1&pageSize=100";

		@Override
		protected Result doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept(Collections.singletonList(new MediaType(
					"application", "json")));
			HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new JSONConverter());
			ResponseEntity<Result> responseEntity = restTemplate.exchange(url,
					HttpMethod.GET, requestEntity, Result.class);
			Result result = responseEntity.getBody();
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			infoView.setText("请求结果：\n" + result);
		}
	}
}
