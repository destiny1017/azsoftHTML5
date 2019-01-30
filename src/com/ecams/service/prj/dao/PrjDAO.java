
/*****************************************************************************************
	1. program ID	: PrjDAO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: 1. 메인화면 및  메뉴 DAO
*****************************************************************************************/

package com.ecams.service.prj.dao;

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
//import com.ecams.service.prj.valueobject.PrjVO;
import com.ecams.service.prj.valueobject.PrjRsltVO;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrjDAO{


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
	public ArrayList selectPrjList(String req_str) throws SQLException, Exception {
		Connection        conn          = null;
		PreparedStatement pstmt         = null;
		ResultSet         rs            = null;
		StringBuffer      strQuery      = new StringBuffer();
		PrjRsltVO         prjrsltvo = null;
		ArrayList         arraylist     = new ArrayList();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@9.11.1.155:2521:comm", "arispm_sel", "arispm");
				//조회한 리스트를 TEMP에서 PATCH
				strQuery.append("SELECT a.PJT_CODE PJT_CODE, a.TITLE TITLE \n");
				strQuery.append("    FROM arispm.pm_pjt01t a, arispm.pm_usr01t b \n");
				strQuery.append("      WHERE  a.COMP_ID = '1111111111'   \n");
				strQuery.append("         AND a.COMP_ID = b.COMP_ID      \n");
				strQuery.append("         AND a.PM_DEPT = b.DEPT         \n");
				strQuery.append("         AND b.USER_ID = ?              \n");
				strQuery.append(" ORDER BY a.TITLE                       \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, req_str);
	            rs = pstmt.executeQuery();

				while (rs.next()){
					prjrsltvo = new PrjRsltVO();
					prjrsltvo.setPJT_CODE(rs.getString("PJT_CODE"));
					prjrsltvo.setTITLE(rs.getString("TITLE"));

					arraylist.add(prjrsltvo);
				}//end of while-loop statement
				rs.close();
				pstmt.close();
				conn.close();
				rs = null;
				pstmt = null;
				conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjDAO.selectPrjList() SQLException START ##");
			ecamsLogger.error("## access vector size : " + "[" + arraylist.size() +"]");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjDAO.selectPrjList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjDAO.selectPrjList() Exception START ##");
			ecamsLogger.error("## access vector size : " + "[" + arraylist.size() +"]");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjDAO.selectPrjList() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## PrjDAO.selectPrjList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return arraylist;

	}//end of selectRefAnalList() method statement

	public int insertPrjList(String CD_SEQNO, String PrjCd, String PrjNm) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
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
			ecamsLogger.error("## PrjDAO.insertPrjList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjDAO.insertPrjList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjDAO.insertPrjList() Exception START ##");
			ecamsLogger.error("## PrjDAO.insertPrjList() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## PrjDAO.insertPrjList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return rtn_cnt;

	}//end of insertRefAnalList() method statement

}//end of RefAnalDAO class statement
