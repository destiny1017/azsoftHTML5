
/*****************************************************************************************
	1. program ID	: RefAnalRsltVO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 
*****************************************************************************************/

package com.ecams.service.prj.valueobject;

 
import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrjRsltVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String PJT_CODE       = null;
	private String TITLE          = null;
	
	public String getPJT_CODE() {
		return PJT_CODE;
	}
	public void setPJT_CODE(String PJT_CODE) {
		this.PJT_CODE = PJT_CODE;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String TITLE) {
		this.TITLE = TITLE;
	}
	
	/**
		 * 
		 * @return 
		 * @author 
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("PrjRsltVO[");
			buffer.append("PJT_CODE = ").append(PJT_CODE);	
			buffer.append("TITLE = ").append(TITLE);		
			buffer.append("]");
			return buffer.toString();
		}

}//end of class RefAnalRsltVO