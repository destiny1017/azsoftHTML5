
/*****************************************************************************************
	1. program ID	: MenuDAO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 1. 메인화면 및  메뉴 DAO
*****************************************************************************************/

package com.ecams.service.menu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.menu.valueobject.MenuVO;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MenuDAO{	


    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * @param user_id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<MenuVO> selectMenuList(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		MenuVO            mvo         = null;
		ArrayList<MenuVO>         arraylist   = new ArrayList<MenuVO>();

		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();		
			strQuery.append("SELECT B.CM_ORDER    CM_ORDER");
			strQuery.append("              , B.CM_MANAME   CM_MANAME");
			strQuery.append("              , B.CM_BEFMENU  CM_BEFMENU");
			strQuery.append("              , B.CM_FILENAME CM_FILENAME");			
			strQuery.append("  FROM CMM0080 B  ");
			strQuery.append("     , CMM0090 A  ");
			strQuery.append(" WHERE A.CM_RGTCD IN(SELECT CM_RGTCD ");
			strQuery.append("                              FROM CMM0043 ");
			strQuery.append("              WHERE CM_USERID= ?)   ");
            strQuery.append("   AND A.CM_MENUCD  = B.CM_MENUCD          ");	
            strQuery.append("  GROUP BY B.CM_BEFMENU, B.CM_ORDER, B.CM_MANAME, B.CM_FILENAME   ");	
            strQuery.append("  ORDER BY B.CM_BEFMENU, B.CM_ORDER, B.CM_MANAME, B.CM_FILENAME   ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();
			while (rs.next()){
				mvo = new MenuVO();
				mvo.setCm_befmenu(rs.getString("CM_BEFMENU"));
				mvo.setCm_order(rs.getString("CM_ORDER"));
				mvo.setCm_maname(rs.getString("CM_MANAME"));
				mvo.setCm_filename(rs.getString("CM_FILENAME"));
				mvo.setMenulen(Integer.toString(rs.getString("CM_MANAME").length()));
				mvo.setCm_menu_cat("");
				arraylist.add(mvo);
				mvo = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return arraylist;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuDAO.selectMenuList() SQLException START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## MenuDAO.selectMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuDAO.selectMenuList() Exception START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MenuDAO.selectMenuList() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (arraylist != null) try{arraylist = null;}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MenuDAO.selectCmm0010() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of selectMenuList() method statement
	
	
	/**
	 * 상단 1DEPTH MENU LIST
	 * @param user_id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Vector<String> selectTopMenuList() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Vector<String>            vector      = new Vector<String>();

		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_MENUCD FROM CMM0080 WHERE CM_BEFMENU=0 ORDER BY CM_ORDER");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
            //int i = 0;
			while (rs.next()){
				vector.add(rs.getRow()-1, rs.getString("CM_MENUCD"));
				//i++;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return vector;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuDAO.selectTopMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MenuDAO.selectTopMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuDAO.selectTopMenuList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## MenuDAO.selectTopMenuList() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch(Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch(Exception ex2){ex2.printStackTrace();}
			if (vector != null) try{vector =null;}catch(Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MenuDAO.selectTopMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of selectTopMenuList() method statement
	
}//end of MenuDAO class statement
