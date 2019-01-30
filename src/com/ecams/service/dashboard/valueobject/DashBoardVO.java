
/*****************************************************************************************
	1. program ID	: DashBoardVO.java
	2. create date	: 2010.03. 09
	3. auth		    : j.s.shin
	4. update date	: 
	5. auth		    : 
	6. description	: DashBoard VO
*****************************************************************************************/

package com.ecams.service.dashboard.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DashBoardVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//TABLE CMM0200 CM_GBNCD
	private String CR_GBNCD  = null;
	private String CR_ACPTNO = null;
	private String CR_ACPTDATE = null;
	private String CR_REQUEST  = null;
	private String CR_TITLE  = null;
	private String CR_EDITOR   = null;
	private String CR_CONFUSR  = null;
	public String getCR_GBNCD() {
		return CR_GBNCD;
	}
	public void setCR_GBNCD(String cr_gbncd) {
		CR_GBNCD = cr_gbncd;
	}
	public String getCR_ACPTDATE() {
		return CR_ACPTDATE;
	}
	public void setCR_ACPTDATE(String cr_acptdate) {
		CR_ACPTDATE = cr_acptdate;
	}
	public String getCR_ACPTNO() {
		return CR_ACPTNO;
	}
	public void setCR_ACPTNO(String cr_acptno) {
		CR_ACPTNO = cr_acptno;
	}
	public String getCR_REQUEST() {
		return CR_REQUEST;
	}
	public void setCR_REQUEST(String cr_request) {
		CR_REQUEST = cr_request;
	}
	public String getCR_EDITOR() {
		return CR_EDITOR;
	}
	public String getCR_TITLE() {
		return CR_TITLE;
	}
	public void setCR_TITLE(String cr_title) {
		CR_TITLE = cr_title;
	}
	public void setCR_EDITOR(String cr_editor) {
		CR_EDITOR = cr_editor;
	}
	public String getCR_CONFUSR() {
		return CR_CONFUSR;
	}
	public void setCR_CONFUSR(String cr_confusr) {
		CR_CONFUSR = cr_confusr;
	}
	/**
		 * toString method: creates a String representation of the object
		 * @return the String representation
		 * @author e.sale
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("DashBoardVO[");
			buffer.append("CR_ACPTDATE = ").append(CR_ACPTDATE);
			buffer.append(", CR_ACPTNO = ").append(CR_ACPTNO);
			buffer.append(", CR_REQUEST = ").append(CR_REQUEST);
			buffer.append(", CR_EDITOR = ").append(CR_EDITOR);
			buffer.append(", CR_CONFUSR = ").append(CR_CONFUSR);
			buffer.append("]");
			return buffer.toString();
		}
	
}//end of class DashBoardVO statement