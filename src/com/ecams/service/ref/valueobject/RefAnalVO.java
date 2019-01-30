
/*****************************************************************************************
	1. program ID	: RefAnalVO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 
*****************************************************************************************/

package com.ecams.service.ref.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RefAnalVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String CD_SEQNO    = null;
	private String CD_SYSCD    = null; 
	private String CD_JOBCD    = null;
	private String CD_PROGNAME = null;
	private String CD_DIRPATH = null;
	
	public String getCD_JOBCD() {
		return CD_JOBCD;
	}
	public void setCD_JOBCD(String cd_jobcd) {
		CD_JOBCD = cd_jobcd;
	}
	public String getCD_PROGNAME() {
		return CD_PROGNAME;
	}
	public void setCD_PROGNAME(String cd_progname) {
		CD_PROGNAME = cd_progname;
	}
	public String getCD_SEQNO() {
		return CD_SEQNO;
	}
	public void setCD_SEQNO(String cd_seqno) {
		CD_SEQNO = cd_seqno;
	}
	public String getCD_SYSCD() {
		return CD_SYSCD;
	}
	public void setCD_SYSCD(String cd_syscd) {
		CD_SYSCD = cd_syscd;
	}
	public String getCD_DIRPATH() {
		// TODO Auto-generated method stub
		return CD_DIRPATH;
	}
	public void setCD_DIRPATH(String cd_dirpath) {
		CD_DIRPATH = cd_dirpath;
	}	
	/**
		 * 
		 * @return 
		 * @author 
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("RefAnalVO[");
			buffer.append("CD_JOBCD = ").append(CD_JOBCD);
			buffer.append(" CD_PROGNAME = ").append(CD_PROGNAME);
			buffer.append(" CD_SEQNO = ").append(CD_SEQNO);
			buffer.append(" CD_SYSCD = ").append(CD_SYSCD);
			buffer.append("]");
			return buffer.toString();
		}

}//end of class RefAnalVO statement