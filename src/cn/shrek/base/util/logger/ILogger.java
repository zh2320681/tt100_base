package cn.shrek.base.util.logger;

import cn.shrek.base.util.LogLevel;

public interface ILogger {

	void d(String tag, String message);

	void i(String tag, String message);

	void w(String tag, String message);

	void e(String tag, String message);
	
	void d(Object obj, String message);

	void i(Object obj, String message);

	void w(Object obj, String message);

	void e(Object obj, String message);

	void open();

	void close();

	void println(LogLevel mLogLevel, String tag, String message);
}
