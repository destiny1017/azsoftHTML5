
/*****************************************************************************************
	1. program ID	: MenuVO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 1. 메인화면 및  메뉴 VO
*****************************************************************************************/

package com.ecams.service.guage2.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GuageVO2 implements Serializable{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cnt1     = null;  //금일결재건수
	private String cnt2     = null;  //체크아웃건수
	private String cnt3     = null;  //체크인건수
	private String cnt4     = null;  //체크인 건수 중 반려/취소건수
	
	public String  getcnt1() {
		return cnt1;
	}
	public void setCnt1(String cnt) {
		this.cnt1 = cnt;
	}
	public String  getcnt2() {
		return cnt2;
	}
	public void setCnt2(String cnt) {
		this.cnt2 = cnt;
	}
	public String  getcnt3() {
		return cnt3;
	}
	public void setCnt3(String cnt) {
		this.cnt3 = cnt;
	}
	public String  getcnt4() {
		return cnt4;
	}
	public void setCnt4(String cnt) {
		this.cnt4 = cnt;
	}

	/**
	 * 
	 * @return string 
	 * @author teok.kang
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GuageVO2[");
		buffer.append("cnt1 = ").append(cnt1);
		buffer.append("cnt2 = ").append(cnt2);
		buffer.append("cnt3 = ").append(cnt3);
		buffer.append("cnt4 = ").append(cnt4);
		buffer.append("]");
		return buffer.toString();
	}//end of method toString() statement
	
}//end of class MenuVO statement