package com.ga.mule.util;

public class ExceptionUtil {

	public static String getRootMessage(Throwable throwable) {
		return getRootThrowable(throwable).getMessage();
	}

	public static String getRootThrowableName(Throwable throwable) {
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		return throwable.getClass().getName();
	}

	public static Throwable getRootThrowable(Throwable throwable) {
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		return throwable;
	}

}
