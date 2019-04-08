package sms.launcher;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.giveup.SplitUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsAsyncQueue implements Runnable {

	private Initiator initiator;
	private Constant constant;
	public static Constant defaultConstant;

	private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder().readTimeout(1, TimeUnit.MILLISECONDS)
			.build();

	private static Logger logger = Logger.getLogger(SmsAsyncQueue.class);

	private static BlockingQueue<Payload> queue = new LinkedBlockingDeque<Payload>();

	public SmsAsyncQueue(Initiator initiator) {
		this.initiator = initiator;
		this.constant = initiator.constant;
		SmsAsyncQueue.defaultConstant = constant;
	}

	public static void setDefaultConstant(Constant constant) {
		SmsAsyncQueue.defaultConstant = constant;
	}

	public static void smsSend(String[] phones, String content) {
		Payload payload = new Payload();
		payload.phones = phones;
		payload.contents = new String[] { content };
		payload.type = "smsSend";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static void smsSendTemplate(String[] phones, String[] contents, String business) {
		Payload payload = new Payload();
		payload.phones = phones;
		payload.contents = contents;
		payload.business = business;
		payload.type = "smsSendTemplate";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static JSONObject verificationSendSync(String phone) throws IOException {
		OkHttpClient okHttpClient = new OkHttpClient();
		String url = new StringBuilder(defaultConstant.verification2SendUrl).append("?client=")
				.append(defaultConstant.client).append("&phone=").append(phone).toString();
		Request okHttpRequest = new Request.Builder().url(url).build();
		logger.debug("call out api：" + url);
		Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
		String responseBody = okHttpResponse.body().string();
		okHttpResponse.close();

		return JSON.parseObject(responseBody);
	}

	public static void verificationSend(String phone) {
		Payload payload = new Payload();
		payload.phones = new String[] { phone };
		payload.type = "verificationSend";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static void verificationSendDiy(String phone, String vcode) {
		Payload payload = new Payload();
		payload.phones = new String[] { phone };
		payload.contents = new String[] { vcode };
		payload.type = "verificationSendDiy";
		try {
			queue.put(payload);
		} catch (InterruptedException e) {
			logger.info(ExceptionUtils.getStackTrace(e));
		}
	}

	public static JSONObject verificationVerify(String phone, String vcode) throws IOException {
		OkHttpClient okHttpClient = new OkHttpClient();
		String url = new StringBuilder(defaultConstant.verification2VerifyUrl).append("?client=")
				.append(defaultConstant.client).append("&phone=").append(phone).append("&verification_code=")
				.append(vcode).toString();
		Request okHttpRequest = new Request.Builder().url(url).build();
		logger.debug("call out api：" + url);
		Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
		String responseBody = okHttpResponse.body().string();
		okHttpResponse.close();

		return JSON.parseObject(responseBody);
	}

	public static class Payload {
		private String[] phones;
		private String business;
		private String type;
		private String[] contents;
	}

	@Override
	public void run() {
		while (true) {
			Payload payload = null;
			try {
				payload = queue.take();
				logger.debug("发现新的短信任务");
				if (payload.type.equals("smsSend")) {
					String url = new StringBuilder(constant.sms2SendUrl).append("?client=").append(constant.client)
							.append("&phones=").append(SplitUtils.toSplit(payload.phones, ",", true))
							.append("&content=").append(payload.contents[0]).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();

				} else if (payload.type.equals("smsSendTemplate")) {
					String url = new StringBuilder(constant.sms2SendTemplateUrl).append("?client=")
							.append(constant.client).append("&phones=")
							.append(SplitUtils.toSplit(payload.phones, ",", true)).append("&business=")
							.append(payload.business).append("&contents=")
							.append(SplitUtils.toSplit(payload.contents, ",", true)).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();
				} else if (payload.type.equals("verificationSend")) {
					String url = new StringBuilder(constant.verification2SendUrl).append("?client=")
							.append(constant.client).append("&phone=").append(payload.phones[0]).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();
				} else if (payload.type.equals("verificationSendDiy")) {
					String url = new StringBuilder(constant.verification2SendDiyUrl).append("?client=")
							.append(constant.client).append("&phone=").append(payload.phones[0]).append("&vcode=")
							.append(payload.contents[0]).toString();
					Request okHttpRequest = new Request.Builder().url(url).build();
					logger.debug("call out api：" + url);
					Response okHttpResponse = okHttpClient.newCall(okHttpRequest).execute();
					okHttpResponse.close();
				}
			} catch (SocketTimeoutException e) {
			} catch (Exception e) {
				logger.info(ExceptionUtils.getStackTrace(e));
			}
		}

	}

}
