package jcf.sua.ux.gauce.context;

import java.util.Map;

import com.gauce.GauceContext;
import com.gauce.common.GauceLocale;
import com.gauce.common.GauceVariable;
import com.gauce.db.DBProfile;
import com.gauce.log.Logger;
import com.ixync.common.crypto.IXyncProvider;

/**
 * 테스트를 위한 Dummy GauceContext
 *
 * @author nolang
 *
 */
public class DummyGauceContext implements GauceContext {
	// <global compressed="false" fragment="40" gdml="false">

	public boolean enableCompress() {
		return false;
	}

	public long getCreationTime() {
		return 0;
	}

	public DBProfile getDBProfile() {
		return null;
	}

	public DBProfile getDBProfile(String arg0) {
		return null;
	}

	public Map getDBProfiles() {
		return null;
	}

	public String getDomain() {
		return "";
	}

	public int getFirstRowSize() {
		return 0;
	}

	public GauceVariable getGauceVariable() {
		/*
		 * <col-def integer="6" decimal="7.3" string="20" round="R"/>
		 */
		return new GauceVariable(6, 7, 3, 'R', 255);
	}

	public IXyncProvider getIXyncProvider() {
		return new IXyncProvider("");
	}

	public GauceLocale getLocale() {
		// <charset default="utf-8" get="utf-8" post="utf-8"/>
		return new GauceLocale("euc-kr", "euc-kr", "euc-kr");
	}

	public Logger getLogger() {
		return null;
	}

	public String getMonitorLogPath() {
		return "";
	}

	public String getParameterPutType() {
		return "";
	}

	public int getPoolSize() {
		return 0;
	}

	public int getRequestProtocol() {
		return 0;
	}

	public String getRequestWrapperName() {
		return "";
	}

	public int getResponseProtocol() {
		return 0;
	}

	public String getResponseWrapperName() {
		return "";
	}

	public int getUseProtocol() {
		return 0;
	}

	public boolean isCheckAgent() {
		return false;
	}

	public boolean isMonitorLog() {
		return false;
	}

	public boolean isSendHeader() {
		return false;
	}

	public boolean isUseGdml() {
		return false;
	}
}
