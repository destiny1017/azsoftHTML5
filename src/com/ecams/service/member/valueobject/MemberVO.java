
/*****************************************************************************************
	1. program ID	: MemberVO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 1. 유저정보
*****************************************************************************************/

package com.ecams.service.member.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MemberVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String user_id    = null;
	private String user_name  = null;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	/**
		 * 
		 * @return 
		 * @author 
		 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MemberVO[");
		buffer.append("user_id = ").append(user_id);
		buffer.append(" user_name = ").append(user_name);
		buffer.append("]");
		return buffer.toString();
	} 

}//end of class MemberVO statement