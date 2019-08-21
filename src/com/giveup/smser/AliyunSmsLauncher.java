package com.giveup.smser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.giveup.InteractRuntimeException;

/**
 * @ClassName: ApiDemo
 * @Description: TODO
 *
 */
public class AliyunSmsLauncher {
	private static Logger logger = Logger.getLogger(AliyunSmsLauncher.class);

	private IAcsClient client = null;
	boolean open = true;
	private Map<String, String> sceneToTpl = new HashMap();
	Map<String, String[]> tplToParamNames = new HashMap();
	private int maxPhoneCount = 1000;
	private String signName = null;

	public String getSignName() {
		return signName;
	}

	public void setSignName(String signName) {
		this.signName = signName;
	}

	public int getMaxPhoneCount() {
		return maxPhoneCount;
	}

	public AliyunSmsLauncher(String accessKeyId, String accessSecret) {
		super();
		DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessSecret);
		client = new DefaultAcsClient(profile);
	}

	public void registerTpl(String scene, String tplId, String... paramNames) {
		sceneToTpl.put(scene, tplId);
		tplToParamNames.put(tplId, paramNames);
	}

	public void sendTplSms(String scene, String[] contents, String... phones) throws ServerException, ClientException {
		if (phones == null || phones.length == 0)
			return;
		if (phones.length > 1000) {
			phones = Arrays.copyOf(phones, 1000);
		}
		String smsId = new Date().getTime() + RandomStringUtils.randomNumeric(6);
		String tplId = sceneToTpl.get(scene);
		if (StringUtils.isEmpty(tplId))
			throw new InteractRuntimeException("短信场景未注册");
		String[] paramNames = tplToParamNames.get(tplId);
		if (paramNames == null)
			throw new InteractRuntimeException("短信模板未注册");

		JSONObject jsonVb = new JSONObject();
		for (int i = 0; i < paramNames.length; i++) {
			jsonVb.put(paramNames[i], StringUtils.defaultIfEmpty(contents[i], "nnnnxxxx"));
		}

		logger.info("send aliyun tpl sms " + smsId + ": signName: " + signName + " contents: "
				+ Arrays.toString(contents) + " phones: " + Arrays.toString(phones));
		CommonRequest request = new CommonRequest();
		request.setMethod(MethodType.POST);
		request.setDomain("dysmsapi.aliyuncs.com");
		request.setVersion("2017-05-25");
		request.setAction("SendSms");
		request.putQueryParameter("PhoneNumbers",
				StringUtils.join(phones, ",", 0, phones.length > maxPhoneCount ? maxPhoneCount : phones.length));
		request.putQueryParameter("TemplateCode", tplId);
		request.putQueryParameter("SignName", signName);
		request.putQueryParameter("TemplateParam", jsonVb.toJSONString());
		CommonResponse response = client.getCommonResponse(request);
		logger.info("send aliyun tpl sms " + smsId + " response: " + response.getData());
	}

	public static void main(String[] args) throws Exception {
		String[] a = new String[] { "1", "2", "3", "4" };
		System.out.println(StringUtils.join(a, ",", 0, a.length > 1000 ? 1000 : a.length));
//		SmsLauncher sms = new SmsLauncher();
//		sms.registerAliyun("LTAIVZmL6LXnxJdC", "BjLo3JzAA4Okw9Cf8bVJPzZSAjP31k");
//
//		sms.registerAliyunTpl("dateLookHouse", "SMS_171117472",
//				new String[] { "name", "no", "place", "carNo", "time" });
//
//		sms.setSignName("云房产");
//		sms.sendTplSms("dateLookHouse", new String[] { "王五天", "13555488789", "苏宁天润城16区", "苏A5698Q", "明天上午" },
//				"17372202877,17352402877");
//
//		DefaultProfile profile = DefaultProfile.getProfile("default", "LTAIVZmL6LXnxJdC",
//				"BjLo3JzAA4Okw9Cf8bVJPzZSAjP31k");
//		IAcsClient client = new DefaultAcsClient(profile);
//		JSONObject jsonVb = new JSONObject();
//		jsonVb.put("name", "1");
//		jsonVb.put("no", "2");
//		jsonVb.put("place", "3");
//		jsonVb.put("carNo", "4");
//		jsonVb.put("time", "5");
//		System.out.println(jsonVb.toJSONString());
//		CommonRequest request = new CommonRequest();
//		request.setMethod(MethodType.POST);
//		request.setDomain("dysmsapi.aliyuncs.com");
//		request.setVersion("2017-05-25");
//		request.setAction("SendSms");
//		request.putQueryParameter("PhoneNumbers", "17372202877,17352402877");
//		request.putQueryParameter("TemplateCode", "SMS_171117472");
//		request.putQueryParameter("SignName", "云房产");
//		request.putQueryParameter("TemplateParam", jsonVb.toJSONString());
//		try {
//			CommonResponse response = client.getCommonResponse(request);
//			System.out.println(response.getData());
//		} catch (ServerException e) {
//			e.printStackTrace();
//		} catch (ClientException e) {
//			e.printStackTrace();
//		}
	}

}
