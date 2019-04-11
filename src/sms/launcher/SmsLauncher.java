package sms.launcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @ClassName: ApiDemo
 * @Description: TODO
 *
 */
public class SmsLauncher {
	private static Logger logger = Logger.getLogger(SmsLauncher.class);

	MsLauncher msLauncher = null;
	Map<String,String> msTpls = new HashMap();
	
	public void registerMeisheng(String account, String password, String veryCode) {
		msLauncher = new MsLauncher(account, password, veryCode);
	}

	public void registerTpl(String tplCode, String msTplId) {
		msTpls.put(tplCode, msTplId);
	}
	
	public void sendSms(String mobile, String content) {
		msLauncher.sendSms(mobile, content);
	}

	public void sendTplSms(String mobile, String tplCode, List<String> contents) {
		msLauncher.sendTplSms(mobile, msTpls.get(tplCode), contents);
	}

}
