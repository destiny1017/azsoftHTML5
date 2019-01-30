
/*****************************************************************************************
	1. program ID	: Bbs_DAO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS DAO
*****************************************************************************************/

package com.ecams.service.bbs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.bbs.valueobject.BbsVO;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BbsDAO{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<BbsVO> selectBbsList(String gbn, int cnt) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<BbsVO>         arraylist   = new ArrayList<BbsVO>();
		BbsVO             bbsvo       = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();	
			strQuery.append("SELECT A.CM_ACPTNO      \n");
            strQuery.append("     , A.CM_GBNCD       \n");
            strQuery.append("     , TO_CHAR(A.CM_ACPTDATE, 'YYYY-MM-DD') CM_ACPTDATE   \n");
            strQuery.append("     , SUBSTR(A.CM_TITLE,0,55)            CM_TITLE      \n");
            strQuery.append("     , A.CM_EDITOR      \n");
            strQuery.append("     , A.CM_CONTENTS    \n");
            strQuery.append("  FROM CMM0200 A        \n");
            strQuery.append(" WHERE A.CM_GBNCD =  ?  \n");
            strQuery.append("   AND ROWNUM     <  ?  \n");
            strQuery.append("   order by  a.CM_ACPTNO desc  \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, gbn);
            pstmt.setInt(2, cnt);
            
            rs = pstmt.executeQuery();
            //ecamsLogger.debug(rs.getRow()+"");
			while (rs.next()){
			//ecamsLogger.debug(rs.getString("CM_TITLE"));				
				bbsvo = new BbsVO();
				bbsvo.setCM_ACPTNO(rs.getString("CM_ACPTNO"));
				bbsvo.setCM_GBNCD(rs.getString("CM_GBNCD"));
				bbsvo.setCM_ACPTDATE(rs.getString("CM_ACPTDATE"));
				bbsvo.setCM_TITLE(rs.getString("CM_TITLE"));
				bbsvo.setCM_EDITOR(rs.getString("CM_EDITOR"));
				bbsvo.setCM_CONTENTS(rs.getString("CM_CONTENTS"));
				arraylist.add(bbsvo);
				
				bbsvo = null;
			}//end of while-loop statement
			
			strQuery = null;
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn= null;
			
			return arraylist;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## BbsDAO.selectBbsList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## BbsDAO.selectBbsList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## BbsDAO.selectBbsList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## BbsDAO.selectBbsList() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (arraylist != null) try{arraylist = null;}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## BbsDAO.selectBbsList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of selectBbsList() method statement
	
	/** 팝업공지 조회
	 * @param gbn
	 * @param cnt
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<BbsVO> NoticePOP(String gbn, int cnt) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<BbsVO>         arraylist   = new ArrayList<BbsVO>();
		BbsVO             bbsvo       = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();	
			strQuery.append("SELECT A.CM_ACPTNO      \n");
            strQuery.append("     , A.CM_GBNCD       \n");
            strQuery.append("     , TO_CHAR(A.CM_ACPTDATE, 'YYYY-MM-DD') CM_ACPTDATE   \n");
            strQuery.append("     , SUBSTR(A.CM_TITLE,0,55)            CM_TITLE      \n");
            strQuery.append("     , A.CM_EDITOR      \n");
            strQuery.append("     , A.CM_CONTENTS    \n");
            strQuery.append("     , A.CM_STDATE    \n");
            strQuery.append("     , A.CM_EDDATE    \n");
            strQuery.append("  FROM CMM0200 A        \n");
            strQuery.append(" WHERE A.CM_GBNCD =  ?  \n");
            strQuery.append("   AND A.CM_NOTIYN = 'Y'  \n");
            strQuery.append("   AND a.cm_stdate <= to_char(sysdate,'yyyymmdd') \n");
       		strQuery.append("   AND a.cm_eddate >= to_char(sysdate,'yyyymmdd') \n");                  
            strQuery.append("   AND ROWNUM     <  ?  \n");
            strQuery.append("   order by  a.CM_ACPTNO desc  \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, gbn);
            pstmt.setInt(2, cnt);
            rs = pstmt.executeQuery();
            //ecamsLogger.debug(rs.getRow()+"");
			while (rs.next()){
			//ecamsLogger.debug(rs.getString("CM_TITLE"));				
				bbsvo = new BbsVO();
				bbsvo.setCM_ACPTNO(rs.getString("CM_ACPTNO"));
				bbsvo.setCM_GBNCD(rs.getString("CM_GBNCD"));
				bbsvo.setCM_ACPTDATE(rs.getString("CM_ACPTDATE"));
				bbsvo.setCM_TITLE(rs.getString("CM_TITLE"));
				bbsvo.setCM_EDITOR(rs.getString("CM_EDITOR"));
				bbsvo.setCM_CONTENTS(rs.getString("CM_CONTENTS"));
				bbsvo.setCM_STDATE(rs.getString("CM_STDATE"));
				bbsvo.setCM_EDDATE(rs.getString("CM_EDDATE"));
				arraylist.add(bbsvo);
				bbsvo = null;
			}//end of while-loop statement
			
			strQuery = null;
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			return arraylist;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## BbsDAO.NoticePOP() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## BbsDAO.NoticePOP() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## BbsDAO.NoticePOP() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## BbsDAO.NoticePOP() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (arraylist != null) try{arraylist = null;}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## BbsDAO.NoticePOP() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of NoticePOP() method statement
	
}//end of BbsDAO class statement
