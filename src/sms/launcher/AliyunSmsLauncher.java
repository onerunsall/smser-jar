package sms.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * @ClassName: ApiDemo
 * @Description: TODO
 *
 */
public class AliyunSmsLauncher {
	private static Logger logger = Logger.getLogger(AliyunSmsLauncher.class);

	private IAcsClient client = null;

	Map<String, String[]> tplToParamNames = new HashMap();
	boolean open = true;
	String signName = null;

	public AliyunSmsLauncher(String accessKeyId, String accessSecret) {
		super();
		DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessSecret);
		client = new DefaultAcsClient(profile);
	}

	public void sendTplSms(String tplId, String[] contents, String... phones) {
		String[] paramNames = tplToParamNames.get(tplId);
		JSONObject jsonVb = new JSONObject();
		for (int i = 0; i < paramNames.length; i++) {
			jsonVb.put(paramNames[i], StringUtils.defaultIfEmpty(contents[i], "nnnnxxxx"));
		}

		CommonRequest request = new CommonRequest();
		request.setMethod(MethodType.POST);
		request.setDomain("dysmsapi.aliyuncs.com");
		request.setVersion("2017-05-25");
		request.setAction("SendSms");
		request.putQueryParameter("PhoneNumbers", StringUtils.join(phones, ","));
		request.putQueryParameter("TemplateCode", tplId);
		request.putQueryParameter("SignName", signName);
		request.putQueryParameter("TemplateParam", jsonVb.toJSONString());
		try {
			CommonResponse response = client.getCommonResponse(request);
			System.out.println(response.getData());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SmsLauncher sms = new SmsLauncher();
		sms.registerAliyun("LTAIVZmL6LXnxJdC", "BjLo3JzAA4Okw9Cf8bVJPzZSAjP31k");

		sms.registerAliyunTpl("dateLookHouse", "SMS_171117472",
				new String[] { "name", "no", "place", "carNo", "time" });

		sms.setSignName("云房产");
		sms.sendTplSms("dateLookHouse", new String[] { "王五天", "13555488789", "苏宁天润城16区", "苏A5698Q", "明天上午" },
				"17372202877,17352402877");
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
