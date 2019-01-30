
/*****************************************************************************************
	1. program ID	: BbsVO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS VO
*****************************************************************************************/

package com.ecams.service.mypage.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyPageVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//TABLE CMM0200
	private String CM_MANAME   = null;
	private String CM_FILENAME    = null;
	
	public String getCM_MANAME() {
		return CM_MANAME;
	}
	public void setCM_USERID(String cm_maname) {
		CM_MANAME = cm_maname;
	}
	public String getCM_FILENAME() {
		return CM_FILENAME;
	}
	public void setCM_MENUCD(String cm_filename) {
		CM_FILENAME = cm_filename;
	}
	
	/**
		 * toString method: creates a String representation of the object
		 * @return the String representation
		 * @author e.sale
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("MyPageVO[");
			buffer.append("CM_MANAME = ").append(CM_MANAME);
			buffer.append(", CM_FILENAME = ").append(CM_FILENAME);
			buffer.append("]");
			return buffer.toString();
		}
	
}//end of class MemberVO statement