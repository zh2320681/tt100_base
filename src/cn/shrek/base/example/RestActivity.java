package cn.shrek.base.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.example.bean.Flavor;
import cn.shrek.base.example.bean.GalleryDate;
import cn.shrek.base.example.bean.JSONConverter;
import cn.shrek.base.example.bean.MineBo;
import cn.shrek.base.example.bean.Result;
import cn.shrek.base.example.bean.Result1;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.util.rest.DialogTaskHandler;
import cn.shrek.base.util.rest.DoNothingHandler;
import cn.shrek.base.util.rest.ZWAsyncTask;
import cn.shrek.base.util.rest.ZWRequestConfig;
import cn.shrek.base.util.rest.ZWResult;
import cn.shrek.base.util.rest.converter.FormJsonConverter;
import cn.shrek.base.util.rest.converter.StringJSONConverter;
import cn.tt100.base.R;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

@Controller(layoutId = R.layout.rest)
public class RestActivity extends ZWActivity {
	@AutoInject(clickSelector = "mClick")
	private Button testBtn, jsonTestBtn, custonTestBtn, asyncTestBtn,
			queneTestBtn, mineTestBtn, mine1TestBtn, cacheTestBtn,
			cacheTestBtn11, sameTestBtn, bodyTestBtn, formTestBtn;

	@AutoInject
	private TextView infoView;

	private int pageNo;// 测试1

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
						.excuteTaskWithMap(
								RestActivity.this,
								"http://119.15.137.138:801/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								HttpMethod.GET,
								new TypeReference<Result1<List<GalleryDate>>>() {
								},
								new DialogTaskHandler<Result1<List<GalleryDate>>>(
										"请求", "请求测试中...") {

									@Override
									public void postResult(
											ZWResult<Result1<List<GalleryDate>>> result) {
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
								}, map);

				// ZWAsyncTask
				// .excuteTask(
				// RestActivity.this,
				// "http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
				// new TypeReference<List>(){},
				// new DialogTaskHandler<List<StringBuffer>>("请求",
				// "请求测试中...") {
				//
				// @Override
				// public void postResult(
				// ZWResult<List<StringBuffer>> result) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// });
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
			} else if (arg0 == mineTestBtn) {
				String url = "http://www.weather.com.cn/data/sk/{cityCode}.html";
				ZWAsyncTask.excuteTaskWithParas(RestActivity.this, url,
						HttpMethod.GET, new TypeReference<MineBo[]>() {
						}, new DialogTaskHandler<MineBo[]>("", "") {

							@Override
							public void postResult(ZWResult<MineBo[]> result) {
								// TODO Auto-generated method stub
								MineBo[] bo = result.bodyObj;
								System.out.println("bo=====>" + bo.toString());
							}
						}, "101110101");
			} else if (arg0 == mine1TestBtn) {
				ZWRequestConfig config = new ZWRequestConfig(HttpMethod.GET,
						new StringJSONConverter());
				config.putHeaderValue("accept", "application/json");
				ZWRequestConfig.setDefault(config);

				ZWAsyncTask.excuteTaskWithMap(RestActivity.this,
						"http://192.168.1.208:8080/rs/flavors",
						new TypeReference<List<Flavor>>() {
						}, new DialogTaskHandler<List<Flavor>>("", "") {

							@Override
							public void postResult(ZWResult<List<Flavor>> result) {
								// TODO Auto-generated method stub
								List<Flavor> bo = result.bodyObj;
								for (Flavor f : bo) {
									System.out.println("=====>" + f.toString());
								}
							}
						});
			} else if (arg0 == cacheTestBtn) {
				ZWRequestConfig config = new ZWRequestConfig(HttpMethod.GET,
						new StringJSONConverter());
				config.putHeaderValue("accept", "application/json");
				ZWRequestConfig.setDefault(config);

				ZWAsyncTask.excuteTaskWithMap(RestActivity.this,
						"http://192.168.1.208:8080/rs/flavors",
						new TypeReference<String>() {
						}, new DialogTaskHandler<String>("", "") {

							@Override
							public void preDoing() {
								// TODO Auto-generated method stub
								super.preDoing();
								getTask().cacheSaveTime = 60;
							}

							@Override
							public void postResult(ZWResult<String> result) {
								// TODO Auto-generated method stub
								List<Flavor> bo = JSON.parseArray(
										result.bodyObj, Flavor.class);
								for (Flavor f : bo) {
									System.out.println("=====>" + f.toString());
								}
							}
						});

			} else if (arg0 == cacheTestBtn11) {
				Map<String, String> map = new HashMap<String, String>();
				pageNo++;
				map.put("pageNo", pageNo + "");
				map.put("pageSize", "2");

				ZWAsyncTask
						.excuteTaskWithMap(
								RestActivity.this,
								"http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								HttpMethod.GET, new TypeReference<String>() {
								}, new DialogTaskHandler<String>("请求",
										"请求测试中...") {
									@Override
									public void preDoing() {
										// TODO Auto-generated method stub
										super.preDoing();
										getTask().cacheSaveTime = 60;
									}

									@Override
									public void postResult(
											ZWResult<String> result) {
										// TODO Auto-generated method stub
										infoView.setText(result.bodyObj);
									}
								}, map);
			} else if (arg0 == sameTestBtn) {
				Map<String, String> map = new HashMap<String, String>();
				pageNo++;
				map.put("pageNo", pageNo + "");
				map.put("pageSize", "2");

				ZWAsyncTask
						.excuteTaskWithMap(
								RestActivity.this,
								"http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								HttpMethod.GET, new TypeReference<String>() {
								}, new DialogTaskHandler<String>("请求",
										"请求测试中...") {
									@Override
									public void preDoing() {
										// TODO Auto-generated method stub
										super.preDoing();
										getTask().cacheSaveTime = 60;
									}

									@Override
									public void postResult(
											ZWResult<String> result) {
										// TODO Auto-generated method stub
										infoView.setText(result.bodyObj);
									}
								}, map);

				ZWAsyncTask
						.excuteTaskWithMap(
								RestActivity.this,
								"http://119.15.137.138:80/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								HttpMethod.GET, new TypeReference<String>() {
								}, new DialogTaskHandler<String>("请求",
										"请求测试中...") {
									@Override
									public void preDoing() {
										// TODO Auto-generated method stub
										super.preDoing();
										getTask().cacheSaveTime = 60;
									}

									@Override
									public void postResult(
											ZWResult<String> result) {
										// TODO Auto-generated method stub
										infoView.setText(result.bodyObj);
									}
								}, map);
			} else if (arg0 == bodyTestBtn) {
				ZWRequestConfig config = ZWRequestConfig.copyDefault();
				config.putHeaderValue("accept", "application/json");
				config.putHeaderValue("Content-Type",
						"application/json;charset=UTF-8");

				config.url = "http://192.168.1.4:8080/api/v2/consumptions";
				config.httpMethod = HttpMethod.POST;
				Map<String, Object> bodyMap = new HashMap<String, Object>();
				bodyMap.put("people", 8);
				bodyMap.put("tables", null);
				config.setBody(bodyMap);

				ZWAsyncTask<Void> task = new ZWAsyncTask<Void>(
						RestActivity.this, new DialogTaskHandler<Void>("请求",
								"请求测试中...") {
							@Override
							public void preDoing() {
								// TODO Auto-generated method stub
								super.preDoing();
								getTask().cacheSaveTime = 60;
							}

							@Override
							public void postResult(ZWResult<Void> result) {
								// TODO Auto-generated method stub
								infoView.setText("请求成功了!!!!!!");
							}
						});
				task.execute(config);
			} else if (arg0 == formTestBtn) {
				ZWRequestConfig config = new ZWRequestConfig(HttpMethod.POST,
						new FormJsonConverter());
				config.putHeaderValue("accept", "application/json");
				// config.url = "http://dcai100.com/web/upload/headIcon";
				config.url = "http://dcai100.com/mobile/register";
				// try {
				// config.putMultiValue("file", new
				// UrlResource("file:///storage/emulated/0/UploadImageTemp/head.jpg"));
				config.putMultiValue("phone", "13915422557");
				config.putMultiValue("username", "shrek");
				config.putMultiValue("password", "zh2201593");
				// } catch (MalformedURLException e) {
				// e.printStackTrace();
				// }

				ZWAsyncTask<String> task = new ZWAsyncTask<String>(
						RestActivity.this, new TypeReference<String>() {
						}, new DialogTaskHandler<String>("请求", "请求测试中...") {

							@Override
							public void postResult(ZWResult<String> result) {
								// TODO Auto-generated method stub
								infoView.setText(result.bodyObj);
							}
						});
				task.execute(config);
			}

		}
	};

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		ZWRequestConfig config = new ZWRequestConfig(HttpMethod.GET,
				new StringJSONConverter());
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
