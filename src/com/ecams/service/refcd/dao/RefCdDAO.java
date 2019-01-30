
/*****************************************************************************************
	1. program ID	: PrjDAO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: 1. 메인화면 및  메뉴 DAO
*****************************************************************************************/

package com.ecams.service.refcd.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.refcd.valueobject.RefCdRsltVO;
//import com.ecams.service.refcd.valueobject.RefCdVO;



/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RefCdDAO{


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
	public ArrayList selectRefCdList(String req_str) throws SQLException, Exception {
		Connection        conn          = null;
		PreparedStatement pstmt         = null;
		ResultSet         rs            = null;
		StringBuffer      strQuery      = new StringBuffer();
		RefCdRsltVO       refcdrsltvo = null;
		ArrayList         arraylist     = new ArrayList();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@9.11.1.155:2521:comm", "apan", "apan");
				strQuery.append("SELECT PRJ_CD PRJ_CD,PRJ_KOR_NM PRJ_KOR_NM FROM apan.TBCN1056 ORDER BY PRJ_KOR_NM \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
	            rs = pstmt.executeQuery();

				while (rs.next()){
					refcdrsltvo = new RefCdRsltVO();

					refcdrsltvo.setPJT_CODE(rs.getString("PRJ_CD"));
					refcdrsltvo.setTITLE(rs.getString("PRJ_KOR_NM"));

					arraylist.add(refcdrsltvo);
					conn.close();
					conn = null;
				}//end of while-loop statement
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjDAO.selectRefCdList() SQLException START ##");
			ecamsLogger.error("## access vector size : " + "[" + arraylist.size() +"]");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## RefCdDAO.selectRefCdList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## RefCdDAO.selectRefCdList() Exception START ##");
			ecamsLogger.error("## access vector size : " + "[" + arraylist.size() +"]");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## RefCdDAO.selectRefCdList() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## RefCdDAO.selectRefCdList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return arraylist;

	}//end of selectRefAnalList() method statement

	public int insertRefCdList(String CD_SEQNO, String PrjCd, String PrjNm) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        int               rtn_cnt     = 0;
        Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("DELETE FROM CMD0014 WHERE CD_SEQNO = ? ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, CD_SEQNO);
            rtn_cnt = pstmt.executeUpdate();
            //ecamsLogger.debug(rtn_cnt+"건 DELETE ");
	        pstmt = null;
            strQuery.setLength(0);
			strQuery.append("INSERT INTO CMD0014 VALUES (?, ?, ?) \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, CD_SEQNO);
            pstmt.setString(2, PrjCd);
            pstmt.setString(3, PrjNm);
            rtn_cnt = pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            pstmt = null;
            conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## RefCdDAO.insertPrjList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## RefCdDAO.insertPrjList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## RefCdDAO.insertPrjList() Exception START ##");
			ecamsLogger.error("## RefCdDAO.insertPrjList() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## RefCdDAO.insertPrjList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return rtn_cnt;

	}//end of insertRefAnalList() method statement

}//end of RefAnalDAO class statement
