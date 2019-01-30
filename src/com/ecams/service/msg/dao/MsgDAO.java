
/*****************************************************************************************
	1. program ID	: MsgDAO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: 1. 메인화면 및  메뉴 DAO
*****************************************************************************************/

package com.ecams.service.msg.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class MsgDAO{


    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/**
	 * @param  req_str
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */

	public int updateLoginIp(String UserId, String IpAddr, String Url) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
        Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("UPDATE CMM0040 set CM_LOGINDT = sysdate, CM_STATUS = '1', \n");
			strQuery.append("                  CM_CONURL = ?,                     \n");
			strQuery.append("                  CM_IPADDRESS = ?                  \n");
			strQuery.append("                  WHERE CM_USERID = ?                \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, Url);
            pstmt.setString(2, IpAddr);
            pstmt.setString(3, UserId);

            return pstmt.executeUpdate();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MsgDao.updateLoginIp() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MsgDao.updateLoginIp() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MsgDao.updateLoginIp() Exception START ##");
			ecamsLogger.error("## MsgDao.updateLoginIp() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MsgDao.updateLoginIp() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of insertRefAnalList() method statement


	public int updateLogOut(String UserId) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
        Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
		    conn = connectionContext.getConnection();
		    strQuery.setLength(0);
			strQuery.append("UPDATE CMM0040 set CM_STATUS = '0', CM_LOGOUTDT = sysdate   \n");
			strQuery.append("                  WHERE CM_USERID = ?   					\n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, UserId);

            return pstmt.executeUpdate();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MsgDao.updateLogOut() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MsgDao.updateLogOut() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MsgDao.updateLogOut() Exception START ##");
			ecamsLogger.error("## MsgDao.updateLogOut() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MsgDao.updateLogOut() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of insertRefAnalList() method statement

	/*
	private String substr(String ipAddr, int i) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
}//end of RefAnalDAO class statement
