package com.ecams.service.cmr.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CmrVO implements Serializable{
	
	private String cm_stno      = null;
	private String cm_ipaddr    = null;
	private int    cm_port      = 0;
	private String cm_user      = null;
	private String cm_passwd    = null;
	private int    cm_policypwd = 0;
	private String cm_times0    = null;
	private String cm_timee0    = null;
	private int    cm_pwdcnt    = 0;
	private int    cm_pwdterm   = 0;
	private String cm_pwdcd     = null;
	
	/**
	 * @return
	 */
	public String getCm_ipaddr() {
		return cm_ipaddr;
	}
	/**
	 * @param cm_ipaddr
	 */
	public void setCm_ipaddr(String cm_ipaddr) {
		this.cm_ipaddr = cm_ipaddr;
	}
	/**
	 * @return
	 */
	public String getCm_passwd() {
		return cm_passwd;
	}
	/**
	 * @param cm_passwd
	 */
	public void setCm_passwd(String cm_passwd) {
		this.cm_passwd = cm_passwd;
	}
	/**
	 * @return
	 */
	public int getCm_policypwd() {
		return cm_policypwd;
	}
	/**
	 * @param cm_policypwd
	 */
	public void setCm_policypwd(int cm_policypwd) {
		this.cm_policypwd = cm_policypwd;
	}
	/**
	 * @return
	 */
	public int getCm_port() {
		return cm_port;
	}
	/**
	 * @param cm_port
	 */
	public void setCm_port(int cm_port) {
		this.cm_port = cm_port;
	}
	/**
	 * @return
	 */
	public String getCm_pwdcd() {
		return cm_pwdcd;
	}
	/**
	 * @param cm_pwdcd
	 */
	public void setCm_pwdcd(String cm_pwdcd) {
		this.cm_pwdcd = cm_pwdcd;
	}
	/**
	 * @return
	 */
	public int getCm_pwdcnt() {
		return cm_pwdcnt;
	}
	/**
	 * @param cm_pwdcnt
	 */
	public void setCm_pwdcnt(int cm_pwdcnt) {
		this.cm_pwdcnt = cm_pwdcnt;
	}
	/**
	 * @return
	 */
	public int getCm_pwdterm() {
		return cm_pwdterm;
	}
	/**
	 * @param cm_pwdterm
	 */
	public void setCm_pwdterm(int cm_pwdterm) {
		this.cm_pwdterm = cm_pwdterm;
	}
	/**
	 * @return
	 */
	public String getCm_stno() {
		return cm_stno;
	}
	/**
	 * @param cm_stno
	 */
	public void setCm_stno(String cm_stno) {
		this.cm_stno = cm_stno;
	}
	/**
	 * @return
	 */
	public String getCm_timee0() {
		return cm_timee0;
	}
	/**
	 * @param cm_timee0
	 */
	public void setCm_timee0(String cm_timee0) {
		this.cm_timee0 = cm_timee0;
	}
	/**
	 * @return
	 */
	public String getCm_times0() {
		return cm_times0;
	}
	/**
	 * @param cm_times0
	 */
	public void setCm_times0(String cm_times0) {
		this.cm_times0 = cm_times0;
	}
	/**
	 * @return
	 */
	public String getCm_user() {
		return cm_user;
	}
	/**
	 * @param cm_user
	 */
	public void setCm_user(String cm_user) {
		this.cm_user = cm_user;
	}
	
	/**
		 * 
		 * @return 
		 * @author cs kang
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("CmrVO[");
			buffer.append("cm_ipaddr = ").append(cm_ipaddr);
			buffer.append(" cm_passwd = ").append(cm_passwd);
			buffer.append(" cm_policypwd = ").append(cm_policypwd);
			buffer.append(" cm_port = ").append(cm_port);
			buffer.append(" cm_pwdcd = ").append(cm_pwdcd);
			buffer.append(" cm_pwdcnt = ").append(cm_pwdcnt);
			buffer.append(" cm_pwdterm = ").append(cm_pwdterm);
			buffer.append(" cm_stno = ").append(cm_stno);
			buffer.append(" cm_timee0 = ").append(cm_timee0);
			buffer.append(" cm_times0 = ").append(cm_times0);
			buffer.append(" cm_user = ").append(cm_user);
			buffer.append("]");
			return buffer.toString();
		}
	
	
}//end of class CmrVO statement