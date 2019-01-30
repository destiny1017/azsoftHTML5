
/*****************************************************************************************
	1. program ID	: Bbs_DAO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS DAO
*****************************************************************************************/

package com.ecams.service.mypage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.mypage.valueobject.MyPageVO;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyPageDAO{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	/**
	 * MyPage를 조회합니다.(구분값을 주어)
	 * @param  userid
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList selectMypageList(String userid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList         arraylist   = new ArrayList();
		MyPageVO          mypagevo       = null;		
		
		ConnectionContext connectionContext = new ConnectionResource();
				
		try {
			conn = connectionContext.getConnection();	
			strQuery.append("SELECT A.CM_MANAME      \n");
            strQuery.append("     , A.CM_FILENAME     \n");
            strQuery.append("  FROM cmm0091 B, cmm0080 A    \n");
            strQuery.append(" WHERE B.CM_USERID =  ?  \n");
            strQuery.append("   AND B.CM_MENUCD = A.CM_MENUCD  \n");
            strQuery.append("   order by  A.CM_MENUCD  \n");
            
            pstmt = conn.prepareStatement(strQuery.toString());	
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();
            
			while (rs.next()){				
				mypagevo = new MyPageVO();
				mypagevo.setCM_USERID(rs.getString("CM_MANAME"));
				mypagevo.setCM_MENUCD(rs.getString("CM_FILENAME"));
				arraylist.add(mypagevo);
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();		
			ecamsLogger.error("## MyPageDAO.selectMypageList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## MyPageDAO.selectMypageList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MyPageDAO.selectMypageList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## MyPageDAO.selectMypageList() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## MyPageDAO.selectMypageList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		
		return arraylist;
		
	}//end of MyPageDAO() method statement
	
}//end of MyPageDAO class statement
