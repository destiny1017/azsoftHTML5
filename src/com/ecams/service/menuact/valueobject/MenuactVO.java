
/*****************************************************************************************
	1. program ID	: MenuVO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 1. 메인화면 및  메뉴 VO
*****************************************************************************************/

package com.ecams.service.menuact.valueobject;


import java.io.Serializable;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MenuactVO implements Serializable{
	
	/**
	 * 메뉴목록 
	 */
	private static final long serialVersionUID = 1L;
	private String cm_order    = null;
	private String cm_befmenu  = null; //대구분
	private String cm_maname   = null;
	private String cm_menu_cat = null;
	private String cm_filename = null;

	
	public String getCm_filename() {
		return cm_filename;
	}
	public void setCm_filename(String cm_filename) {
		this.cm_filename = cm_filename;
	}	
	public String getCm_befmenu() {
		return cm_befmenu;
	}
	public void setCm_befmenu(String cm_befmenu) {
		this.cm_befmenu = cm_befmenu;
	}
	public String getCm_maname() {
		return cm_maname;
	}
	public void setCm_maname(String cm_maname) {
		this.cm_maname = cm_maname;
	}
	public String getCm_menu_cat() {
		return cm_menu_cat;
	}
	public void setCm_menu_cat(String cm_menu_cat) {
		this.cm_menu_cat = cm_menu_cat;
	}
	public String getCm_order() {
		return cm_order;
	}
	public void setCm_order(String cm_order) {
		this.cm_order = cm_order;
	}
	
	/**
	 * 
	 * @return string 
	 * @author teok.kang
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MenuactVO[");
		buffer.append("cm_befmenu = ").append(cm_befmenu);
		buffer.append(" cm_maname = ").append(cm_maname);
		buffer.append(" cm_menu_cat = ").append(cm_menu_cat);
		buffer.append(" cm_order = ").append(cm_order);
		buffer.append("]");
		return buffer.toString();
	}//end of method toString() statement
	
}//end of class MenuVO statement