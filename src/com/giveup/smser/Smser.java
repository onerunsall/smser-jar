package com.giveup.smser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @ClassName: ApiDemo
 * @Description: TODO
 *
 */
public class Smser {
	private static Logger logger = Logger.getLogger(Smser.class);

	private MeishengSmsLauncher msLauncher = null;
	private Map<String, String> msSceneToTpl = new HashMap();

	private AliyunSmsLauncher aliyunSmsLauncher = null;
	private Map<String, String> aliyunSceneToTpl = new HashMap();

	public void setSignName(String signName) {
		aliyunSmsLauncher.signName = signName;
	}

	public void closeMeisheng() {
		logger.info("close aliyunSmsLauncher");
		msLauncher.open = false;
	}

	public void registerMeisheng(String account, String password, String veryCode) {
		logger.info("register meishengSmsLauncher");
		msLauncher = new MeishengSmsLauncher(account, password, veryCode);
	}

	public void registerMeishengTpl(String scene, String tplId, String... paramNames) {
		msSceneToTpl.put(scene, tplId);
		msLauncher.tplToParamNames.put(tplId, paramNames);
	}

	public void closeAliyun() {
		logger.info("close aliyunSmsLauncher");
		aliyunSmsLauncher.open = false;
	}

	public void registerAliyun(String accessKeyId, String accessSecret) {
		logger.info("register aliyunSmsLauncher");
		aliyunSmsLauncher = new AliyunSmsLauncher(accessKeyId, accessSecret);
	}

	public void registerAliyunTpl(String scene, String tplId, String... paramNames) {
		aliyunSceneToTpl.put(scene, tplId);
		aliyunSmsLauncher.tplToParamNames.put(tplId, paramNames);
	}

	public void sendSms(String content, String... phones) {
		if (msLauncher != null && msLauncher.open)
			msLauncher.sendSms(content, phones);
	}

	public void sendTplSms(String scene, String[] contents, String... phones) throws Exception {
		String mobile = "";
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
		Pattern p = Pattern.compile(regex);
		for (int i = 0; i < phones.length; i++) {
			if (phones[i] == null || phones[i].length() != 11)
				continue;
			if (!p.matcher(phones[i]).matches())
				continue;
			if (i == 0)
				mobile = mobile + phones[i];
			else
				mobile = mobile + "," + phones[i];
		}
		if (mobile.startsWith(","))
			mobile = mobile.substring(1);
		if (msLauncher != null && msLauncher.open)
			msLauncher.sendTplSms(msSceneToTpl.get(scene), contents, phones);
		else if (aliyunSmsLauncher != null && aliyunSmsLauncher.open)
			aliyunSmsLauncher.sendTplSms(aliyunSceneToTpl.get(scene), contents, phones);
	}

}
