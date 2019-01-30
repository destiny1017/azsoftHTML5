
/*****************************************************************************************
	1. program ID	: RefAnalRsltVO.java
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
public class RefAnalRsltVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String CD_SEQNO       = null;
	private String CD_SYSCD       = null;
	private String CD_JOBCD       = null;	
	private String SRC_FILE_NM_1  = null;
	private String ORG_FILE_PTH_1 = null; 
	private String SRC_FILE_NM_2  = null;
	private String ORG_FILE_PTH_2 = null;
	

	public String getCD_SEQNO() {
		return CD_SEQNO;
	}
	public void setCD_SEQNO(String CD_SEQNO) {
		this.CD_SEQNO = CD_SEQNO;
	}
	public String getORG_FILE_PTH_1() {
		return ORG_FILE_PTH_1;
	}
	public void setORG_FILE_PTH_1(String org_file_pth_1) {
		ORG_FILE_PTH_1 = org_file_pth_1;
	}
	public String getORG_FILE_PTH_2() {
		return ORG_FILE_PTH_2;
	}
	public void setORG_FILE_PTH_2(String org_file_pth_2) {
		ORG_FILE_PTH_2 = org_file_pth_2;
	}
	public String getSRC_FILE_NM_1() {
		return SRC_FILE_NM_1;
	}
	public void setSRC_FILE_NM_1(String src_file_nm_1) {
		SRC_FILE_NM_1 = src_file_nm_1;
	}
	public String getSRC_FILE_NM_2() {
		return SRC_FILE_NM_2;
	}
	public void setSRC_FILE_NM_2(String src_file_nm_2) {
		SRC_FILE_NM_2 = src_file_nm_2;
	}
	/**
		 * 
		 * @return 
		 * @author 
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("RefAnalRsltVO[");
			buffer.append("CD_SEQNO = ").append(CD_SEQNO);	
			buffer.append("CD_SYSCD = ").append(CD_SYSCD);		
			buffer.append("CD_JOBCD = ").append(CD_JOBCD);		
			buffer.append("ORG_FILE_PTH_1 = ").append(ORG_FILE_PTH_1);
			buffer.append(" ORG_FILE_PTH_2 = ").append(ORG_FILE_PTH_2);
			buffer.append(" SRC_FILE_NM_1 = ").append(SRC_FILE_NM_1);
			buffer.append(" SRC_FILE_NM_2 = ").append(SRC_FILE_NM_2);
			buffer.append("]");
			return buffer.toString();
		}
	public String getCD_JOBCD() {
		return CD_JOBCD;
	}
	public void setCD_JOBCD(String cd_jobcd) {
		CD_JOBCD = cd_jobcd;
	}
	public String getCD_SYSCD() {
		return CD_SYSCD;
	}
	public void setCD_SYSCD(String cd_syscd) {
		CD_SYSCD = cd_syscd;
	}

}//end of class RefAnalRsltVO