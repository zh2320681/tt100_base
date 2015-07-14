package cn.shrek.base.util.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import cn.shrek.base.ZWApplication;
import cn.shrek.base.util.AndroidVersionCheckUtils;
import cn.shrek.base.util.BaseUtil;
import cn.shrek.base.util.LogLevel;
import cn.shrek.base.util.ZWLogger;

/**
 * @Description ZWPrintToFileLogger是打印到sdcard上面的日志类
 * @author Shrek
 * @date 2014-5-8
 * @version V1.0
 */
public class ZWPrintToFileLogger implements ILogger {
	private String mPath;
	private Writer mWriter;

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH:mm:ss] ");
	private static final String GAP = "###";
	// private String basePath = "";
	private static String LOG_DIR = "log";
	// private static String BASE_FILENAME = "ta.log";
	private File logDir;
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");

	private Context context;

	public ZWPrintToFileLogger(Context context) {
		super();
		this.context = context;
	}

	public void open() {
		if (AndroidVersionCheckUtils.hasFroyo()) {
			logDir = BaseUtil.getDiskCacheDir(context, LOG_DIR);
		} else {
			logDir = BaseUtil.getDiskCacheDir(context, LOG_DIR);
		}
		if (!logDir.exists()) {
			logDir.mkdirs();
			// do not allow media scan
			try {
				new File(logDir, ".nomedia").createNewFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// basePath = logDir.getAbsolutePath() + "/"
		// + ;
		// 检测当前文件是否超过时间
		File[] timeoutFiles = logDir.listFiles();
		Date nowDate = new Date();
		for (File file : timeoutFiles) {
			String fileName = file.getName();
			if (fileName.indexOf(".") == 0) {
				continue;
			}

			boolean isDel = false;
			if (fileName.length() <= 14) {
				isDel = true;
			} else {
				fileName = fileName.substring(fileName.length() - 14,
						fileName.length() - 4);
				try {
					Date laseDate = simpleDateFormat.parse(fileName);
					if (nowDate.getTime() - laseDate.getTime() >= ZWApplication.loggerPrintAvaidTime
							* 1000 * 60 * 60 * 24) {
						isDel = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (isDel) {
					ZWLogger.printLog(this, "日志文件" + file.getName()
							+ "有效期超时，已删除!");
					file.delete();
				}
			}

		}

		try {
			File file = new File(logDir, ZWApplication.loggerPrintName + "_"
					+ getCurrentTimeString() + ".log");
			if (!file.exists()) {
				file.createNewFile();
			}
			mPath = file.getAbsolutePath();
			mWriter = new BufferedWriter(new FileWriter(mPath,true), 2048);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private String getCurrentTimeString() {
		Date now = new Date();
		return simpleDateFormat.format(now);
	}

	public String getPath() {
		return mPath;
	}

	@Override
	public void d(String tag, String message) {
		// TODO Auto-generated method stub
		println(LogLevel.DEBUG, tag, message);
	}

	@Override
	public void e(String tag, String message) {
		println(LogLevel.ERROR, tag, message);
	}

	@Override
	public void p(String tag, String message) {
		println(LogLevel.PRINT, tag, message);
	}

	@Override
	public void i(String tag, String message) {
		println(LogLevel.INFO, tag, message);
	}

	@Override
	public void w(String tag, String message) {
		println(LogLevel.WARNING, tag, message);
	}

	private String getFormatMessage(String level, String tag, String message) {
		 String formatMessage = "%s %s %s %s %s %s %s %s";
		 return String.format(formatMessage, GAP, level, GAP, tag, GAP,
		 context.getPackageName(), GAP, message);
//		return GAP + " " + level + " " + GAP + " " + tag + " " + GAP + " "
//				+ context.getPackageName() + " " + GAP + " " + message;
	}

	@Override
	public void println(LogLevel mLogLevel, String tag, String message) {
		String printMessage = "";
		switch (mLogLevel) {
		case DEBUG:
			// printMessage = "[D]" + tag + " " + context.getPackageName() + " "
			// + message;
			printMessage = getFormatMessage("[D]", tag, message);
			break;
		case INFO:
			// printMessage = "[I] " + tag + " " + context.getPackageName() +
			// " "
			// + message;
			printMessage = getFormatMessage("[I]", tag, message);
			break;
		case WARNING:
			// printMessage = "[W] " + tag + " " + context.getPackageName() +
			// " "
			// + message;
			printMessage = getFormatMessage("[W]", tag, message);
			break;
		case ERROR:
			// printMessage = "[E] " + tag + " " + context.getPackageName() +
			// " "
			// + message;
			printMessage = getFormatMessage("[E]", tag, message);
			break;
		case PRINT:
			// printMessage = "[P] " + tag + " " + context.getPackageName() +
			// " "
			// + message;
			printMessage = getFormatMessage("[P]", tag, message);
			println(printMessage);
		default:

			break;
		}

	}

	public void println(String message) {
		if (mWriter == null) {
			return;
		}
		try {
			mWriter.write(TIMESTAMP_FMT.format(new Date()));
			mWriter.write(message);
			mWriter.write('\n');
			mWriter.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<LoggerBo> getHistoryLogs() {
		List<LoggerBo> bos = new ArrayList<LoggerBo>();

		FileReader fr = null;
		BufferedReader buffReader = null;
		try {
			fr = new FileReader(mPath);
			buffReader = new BufferedReader(fr, 2048);
			String line = null;
			while ((line = buffReader.readLine()) != null) {
				String[] array = line.split(GAP);
				Date logTime = TIMESTAMP_FMT.parse(array[0]);
				LoggerBo bo = new LoggerBo(logTime, array[2], array[3],
						array[4]);
				bos.add(0,bo);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buffReader != null) {
				try {
					buffReader.close();
				} catch (Exception e) {
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception e) {
				}
			}
		}
		return bos;
	}

	public void close() {
		try {
			mWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void d(Object obj, String message) {
		// TODO Auto-generated method stub
		d(obj.getClass().getSimpleName(), message);
	}

	@Override
	public void i(Object obj, String message) {
		// TODO Auto-generated method stub
		i(obj.getClass().getSimpleName(), message);
	}

	@Override
	public void w(Object obj, String message) {
		// TODO Auto-generated method stub
		w(obj.getClass().getSimpleName(), message);
	}

	@Override
	public void e(Object obj, String message) {
		// TODO Auto-generated method stub
		e(obj.getClass().getSimpleName(), message);
	}

	@Override
	public void p(Object obj, String message) {
		p(obj.getClass().getSimpleName(), message);
	}

}
