package cn.tt100.base.util.logger;

import cn.tt100.base.util.LogLevel;

public interface ILogger {

	void d(String tag, String message);

	void i(String tag, String message);

	void w(String tag, String message);

	void e(String tag, String message);

	void open();

	void close();

	void println(LogLevel mLogLevel, String tag, String message);
}
