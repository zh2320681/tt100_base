tt100_base
==========
数据库操作:
操作例子如 DBTestActivity


rest请求
=======
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
=======
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
=======
ZWAsyncTask.addTaskIntoQueueAndExcute(task1, task2, task3);	

相同任务在同一个activity  能否同时执行 设置assets的isInterceptSameRequest			

图片加载用的是网上的框架
========
第一步：设置Loader的Config

ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

第二步 设置显示的配置

DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
        .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
        .showImageOnFail(R.drawable.ic_error) // resource or drawable
        .resetViewBeforeLoading(false)  // default
        .delayBeforeLoading(1000)
        .cacheInMemory(false) // default
        .cacheOnDisc(false) // default
        .preProcessor(...)
        .postProcessor(...)
        .extraForDownloader(...)
        .considerExifParams(false) // default
        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
        .decodingOptions(...)
        .displayer(new SimpleBitmapDisplayer()) // default
        .handler(new Handler()) // default
        .build();
        
第三步 调用 Loader的显示方法
https://github.com/zh2320681/Android-Universal-Image-Loader
		
