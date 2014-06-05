tt100_base
==========
数据库操作:
操作例子如 DBTestActivity


rest请求
==========
ZWAsyncTask
						.excuteTask(
								RestActivity.this,
								"http://119.15.137.138:801/rs/showrooms?pageNo={pageNo}&pageSize={pageSize}",
								HttpMethod.GET,
								new TypeReference<Result1<List<GalleryDate>>>(){},
								map,
								new DialogTaskHandler<Result1<List<GalleryDate>>>("请求", "请求测试中...") {

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
								});
支持请求数据有效时间内缓存处理

ZWCache缓存类
==========
(主要用户的网络请求的缓存)
new DialogTaskHandler<String>("", "") {

							@Override
							public void preDoing() {
								// TODO Auto-generated method stub
								super.preDoing();
								//开启缓存时间.....
								getTask().cacheSaveTime = 60;
							}

							@Override
							public void postResult(ZWResult<String> result) {
								// TODO Auto-generated method stub
							
							}
						}

多个任务同一个队列请求
==========
ZWAsyncTask.addTaskIntoQueueAndExcute(task1, task2, task3);						