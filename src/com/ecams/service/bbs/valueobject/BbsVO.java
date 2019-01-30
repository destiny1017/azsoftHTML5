
/*****************************************************************************************
	1. program ID	: BbsVO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS VO
*****************************************************************************************/

package com.ecams.service.bbs.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BbsVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//TABLE CMM0200 CM_GBNCD
	private String CM_ACPTNO   = null;
	private String CM_GBNCD    = null;
	private String CM_NOTIYN   = null;
	private String CM_ACPTDATE = null;
	private String CM_TITLE    = null;
	private String CM_EDITOR   = null;
	private String CM_CONTENTS = null;
	//TABLE CMM0210
	private String CM_ANSTIT   = null;
	private String CM_ANSCONT  = null;
	private String CM_LASTDATE = null;
	//TABLE CMM0220
	private String CM_SEQNO    = null;
	private String CM_ATTFILE  = null;
	private String CM_SVFILE   = null;
	
	private String CM_STDATE   = null;
	private String CM_EDDATE   = null;
	
	public String getCM_STDATE() {
		return CM_STDATE;
	}
	public void setCM_STDATE(String cm_stdate) {
		CM_STDATE = cm_stdate;
	}
	public String getCM_EDDATE() {
		return CM_EDDATE;
	}
	public void setCM_EDDATE(String cm_eddate) {
		CM_EDDATE = cm_eddate;
	}
	public String getCM_ACPTDATE() {
		return CM_ACPTDATE;
	}
	public void setCM_ACPTDATE(String cm_acptdate) {
		CM_ACPTDATE = cm_acptdate;
	}
	public String getCM_ACPTNO() {
		return CM_ACPTNO;
	}
	public void setCM_ACPTNO(String cm_acptno) {
		CM_ACPTNO = cm_acptno;
	}
	public String getCM_ANSCONT() {
		return CM_ANSCONT;
	}
	public void setCM_ANSCONT(String cm_anscont) {
		CM_ANSCONT = cm_anscont;
	}
	public String getCM_ANSTIT() {
		return CM_ANSTIT;
	}
	public void setCM_ANSTIT(String cm_anstit) {
		CM_ANSTIT = cm_anstit;
	}
	public String getCM_ATTFILE() {
		return CM_ATTFILE;
	}
	public void setCM_ATTFILE(String cm_attfile) {
		CM_ATTFILE = cm_attfile;
	}
	public String getCM_CONTENTS() {
		return CM_CONTENTS;
	}
	public void setCM_CONTENTS(String cm_contents) {
		CM_CONTENTS = cm_contents;
	}
	public String getCM_EDITOR() {
		return CM_EDITOR;
	}
	public void setCM_EDITOR(String cm_editor) {
		CM_EDITOR = cm_editor;
	}
	public String getCM_GBNCD() {
		return CM_GBNCD;
	}
	public void setCM_GBNCD(String cm_gbncd) {
		CM_GBNCD = cm_gbncd;
	}
	public String getCM_NOTIYN() {
		return CM_NOTIYN;
	}
	public void setCM_NOTIYN(String cm_notiyn) {
		CM_NOTIYN = cm_notiyn;
	}
	public String getCM_LASTDATE() {
		return CM_LASTDATE;
	}
	public void setCM_LASTDATE(String cm_lastdate) {
		CM_LASTDATE = cm_lastdate;
	}
	public String getCM_SEQNO() {
		return CM_SEQNO;
	}
	public void setCM_SEQNO(String cm_seqno) {
		CM_SEQNO = cm_seqno;
	}
	public String getCM_SVFILE() {
		return CM_SVFILE;
	}
	public void setCM_SVFILE(String cm_svfile) {
		CM_SVFILE = cm_svfile;
	}
	public String getCM_TITLE() {
		return CM_TITLE;
	}
	public void setCM_TITLE(String cm_title) {
		CM_TITLE = cm_title;
	}
	/**
		 * toString method: creates a String representation of the object
		 * @return the String representation
		 * @author e.sale
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("BbsVO[");
			buffer.append("CM_ACPTDATE = ").append(CM_ACPTDATE);
			buffer.append(", CM_ACPTNO = ").append(CM_ACPTNO);
			buffer.append(", CM_ANSCONT = ").append(CM_ANSCONT);
			buffer.append(", CM_ANSTIT = ").append(CM_ANSTIT);
			buffer.append(", CM_ATTFILE = ").append(CM_ATTFILE);
			buffer.append(", CM_CONTENTS = ").append(CM_CONTENTS);
			buffer.append(", CM_EDITOR = ").append(CM_EDITOR);
			buffer.append(", CM_GBNCD = ").append(CM_GBNCD);
			buffer.append(", CM_NOTIYN = ").append(CM_NOTIYN);
			buffer.append(", CM_LASTDATE = ").append(CM_LASTDATE);
			buffer.append(", CM_SEQNO = ").append(CM_SEQNO);
			buffer.append(", CM_SVFILE = ").append(CM_SVFILE);
			buffer.append(", CM_TITLE = ").append(CM_TITLE);
			buffer.append("]");
			return buffer.toString();
		}
	
}//end of class MemberVO statement