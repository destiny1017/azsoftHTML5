
/*****************************************************************************************
	1. program ID	: MainVO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS VO
*****************************************************************************************/

package com.ecams.service.main.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cr_acptdate  = null;
	private String cr_passcd    = null;
	public String getCr_acptdate() {
		return cr_acptdate;
	}
	public void setCr_acptdate(String cr_acptdate) {
		this.cr_acptdate = cr_acptdate;
	}
	public String getCr_passcd() {
		return cr_passcd;
	}
	public void setCr_passcd(String cr_passcd) {
		this.cr_passcd = cr_passcd;
	}
	/**
		 * 
		 * @return 
		 * @author 
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("MainVO[");
			buffer.append("cr_acptdate = ").append(cr_acptdate);
			buffer.append(" cr_passcd = ").append(cr_passcd);
			buffer.append("]");
			return buffer.toString();
		}
	
}//end of class