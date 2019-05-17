package sms.launcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @ClassName: ApiDemo
 * @Description: TODO
 *
 */
public class SmsLauncher {
	private static Logger logger = Logger.getLogger(SmsLauncher.class);

	MsLauncher msLauncher = null;
	Map<String, String> msTpls = new HashMap();

	public void registerMeisheng(String account, String password, String veryCode) {
		msLauncher = new MsLauncher(account, password, veryCode);
	}

	public void registerMeishengTpl(String businessCode, String msTplId) {
		msTpls.put(businessCode, msTplId);
	}

	public void sendSms(String mobile, String content) {
		msLauncher.sendSms(mobile, content);
	}

	public void sendTplSms(List<String> mobiles, String businessCode, List<String> contents) {
		String mobile = "";
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
		Pattern p = Pattern.compile(regex);
		for (int i = 0; i < mobiles.size(); i++) {
			if (mobiles.get(i) == null || mobiles.get(i).length() != 11)
				return;
			if (!p.matcher(mobiles.get(i)).matches())
				return;
			if (i == 0)
				mobile = mobile + mobiles.get(i);
			else
				mobile = mobile + "," + mobiles.get(i);
		}
		msLauncher.sendTplSms(mobile, msTpls.get(businessCode), contents);
	}

	public void sendTplSms(String[] mobiles, String businessCode, List<String> contents) {
		sendTplSms(Arrays.asList(mobiles), msTpls.get(businessCode), contents);
	}

	public void sendTplSms(String[] mobiles, String businessCode, String... contents) {
		sendTplSms(Arrays.asList(mobiles), msTpls.get(businessCode), Arrays.asList(contents));
	}

	public void sendTplSms(String mobile, String businessCode, List<String> contents) {
		msLauncher.sendTplSms(mobile, msTpls.get(businessCode), contents);
	}

	public void sendTplSms(String mobile, String businessCode, String... contents) {
		sendTplSms(mobile, msTpls.get(businessCode), Arrays.asList(contents));
	}

}
