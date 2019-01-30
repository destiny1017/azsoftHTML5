
/*****************************************************************************************
	1. program ID	: MenuactDAO.java
	2. create date	: 2008.02. 01
	3. auth		    : c.i.s
	4. update date	:
	5. auth		    :
	6. description	: 1. TOP ¸Þ´º set
*****************************************************************************************/

package com.ecams.service.menuact.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MenuactDAO{

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
	public String selectMenu(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        String MenuList = "TOP&";
		ConnectionContext connectionContext = new ConnectionResource();
		//ecamsLogger.debug("selectMenu user_id-->" + user_id);
		try {
			conn = connectionContext.getConnection();

			strQuery.append("SELECT b.CM_MENUCD,b.CM_MENUIMG,b.CM_ALTIMG           \n");
			strQuery.append("  FROM CMM0080 a,CMM0081 b                             \n");
			strQuery.append(" WHERE a.CM_BEFMENU=0                                  \n");
			strQuery.append("   AND a.CM_MENUCD IN (SELECT DISTINCT C.CM_BEFMENU    \n");
			strQuery.append("                         FROM CMM0080 C,CMM0090 B,CMM0043 A \n");
			strQuery.append("                        WHERE A.CM_USERID= ?           \n");
			strQuery.append("                          AND A.CM_RGTCD=B.CM_RGTCD    \n");
			strQuery.append("                          AND B.CM_MENUCD=C.CM_MENUCD) \n");
			strQuery.append("   AND a.CM_MENUCD=b.CM_MENUCD                         \n");
			strQuery.append(" ORDER BY a.CM_ORDER                                   \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, user_id);
            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				MenuList = MenuList + ";" + rs.getString("CM_MENUCD");
				MenuList = MenuList + "@" + rs.getString("CM_MENUIMG");
				MenuList = MenuList + "@" + rs.getString("CM_ALTIMG");
			}//end of while-loop statement


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuactDAO.selectMenu(1) SQLException START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MenuactDAO.selectMenu(1) SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuactDAO.selectMenu(1) Exception START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MenuactDAO.selectMenu(1) Exception END ##");
			throw exception;
		}

		try {
			MenuList = MenuList + "&TOPSUB&";
			strQuery.setLength(0);
			strQuery.append("SELECT B.CM_ORDER    CM_ORDER                                 \n");
			strQuery.append("              , B.CM_MANAME   CM_MANAME                       \n");
			strQuery.append("              , B.CM_BEFMENU  CM_BEFMENU                      \n");
			strQuery.append("              , B.CM_FILENAME CM_FILENAME                     \n");
			strQuery.append("  FROM CMM0080 B                                              \n");
			strQuery.append("     , CMM0090 A                                              \n");
		    strQuery.append(" WHERE A.CM_RGTCD IN(SELECT CM_RGTCD                          \n");
			strQuery.append("                              FROM CMM0043                    \n");
			strQuery.append("              WHERE CM_USERID= ?)                             \n");
            strQuery.append("   AND A.CM_MENUCD  = B.CM_MENUCD                             \n");
            strQuery.append("   AND B.CM_BEFMENU <> 0                                      \n");
            strQuery.append("group by b.cm_order,b.cm_maname,b.cm_befmenu,b.cm_filename    \n");
           	strQuery.append("order by b.cm_befmenu,cm_order                                \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);

            rs = pstmt.executeQuery();

			while (rs.next()){
				MenuList = MenuList + ";" + rs.getString("CM_BEFMENU");
				MenuList = MenuList + "@" + rs.getString("CM_ORDER");
				MenuList = MenuList + "@" + rs.getString("CM_MANAME");
				MenuList = MenuList + "@" + rs.getString("CM_FILENAME");
			}//end of while-loop statement


			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuactDAO.selectMenu() SQLException START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MenuactDAO.selectMenu() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuactDAO.selectMenu() Exception START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MenuactDAO.selectMenu() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## MenuactDAO.selectMenu() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		return MenuList;

	}//end of selectMenu() method statement
}//end of MenuactDAO class statement
