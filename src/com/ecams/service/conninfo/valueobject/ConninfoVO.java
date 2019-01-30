
/*****************************************************************************************
	1. program ID	: ConninfoVO.java
	2. create date	: 2008.03. 14
	3. auth		    : jung
	4. update date	: 
	5. auth		    : 
	6. description	: Conninfo VO
*****************************************************************************************/

package com.ecams.service.conninfo.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConninfoVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	//TABLE CMM0020 코드정보
	private String POSITION	= null;
	private String DUTY	= null;
	
	
	//TABLE CMM0040 사용자정보
	private String CM_USERID	= null;
	private String CM_USERNAME	= null;
	private String CM_LOGINDT	= null;
	private String CM_POSITION	= null;
	private String CM_DUTY		= null;
	private String CM_IPADDRESS	= null;
	private String CM_TELNO1	= null;
		
	public String getPOSITION() {
		return POSITION;
	}
	public void setPOSITION(String position) {
		POSITION = position;
	}	
	public String getDUTY() {
		return DUTY;
	}
	public void setDUTY(String duyt) {
		DUTY = duyt;
	}	
	public String getCM_USERID() {
		return CM_USERID;
	}
	public void setCM_USERID(String cm_userid) {
		CM_USERID = cm_userid;
	}
	public String getCM_USERNAME() {
		return CM_USERNAME;
	}
	public void setCM_USERNAME(String cm_username) {
		CM_USERNAME = cm_username;
	}
	public String getCM_LOGINDT() {
		return CM_LOGINDT;
	}
	public void setCM_LOGINDT(String cm_logindt) {
		CM_LOGINDT = cm_logindt;
	}
	public String getCM_POSITION() {
		return CM_POSITION;
	}
	public void setCM_POSITION(String cm_position) {
		CM_POSITION = cm_position;
	}
	public String getCM_DUTY() {
		return CM_DUTY;
	}
	public void setCM_DUTY(String cm_duty) {
		CM_DUTY = cm_duty;
	}
	public String getCM_IPADDRESS() {
		return CM_IPADDRESS;
	}
	public void setCM_IPADDRESS(String cm_ipaddress) {
		CM_IPADDRESS = cm_ipaddress;
	}
	public String getCM_TELNO1() {
		return CM_TELNO1;
	}
	public void setCM_TELNO1(String cm_telno1) {
		CM_TELNO1 = cm_telno1;
	}
	/**
		 * toString method: creates a String representation of the object
		 * @return the String representation
		 * @author e.sale
		 */	
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("ConninfoVO[");
			buffer.append(", CM_USERID = ").append(CM_USERID);
			buffer.append(", CM_USERNAME = ").append(CM_USERNAME);
			buffer.append(", CM_LOGINDT = ").append(CM_LOGINDT);
			buffer.append(", CM_POSITION = ").append(CM_POSITION);
			buffer.append(", CM_DUTY = ").append(CM_DUTY);
			buffer.append(", CM_IPADDRESS = ").append(CM_IPADDRESS);
			buffer.append(", CM_TELNO1 = ").append(CM_TELNO1);
			buffer.append(", POSITION = ").append(POSITION);
			buffer.append(", DUTY = ").append(DUTY);			
			buffer.append("]");
			return buffer.toString();
			
		}
	
}//end of class ConninfoVO statement