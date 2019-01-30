
/*****************************************************************************************
	1. program ID	: Bbs_DAO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS DAO
*****************************************************************************************/

package com.ecams.service.dashboard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.dashboard.valueobject.DashBoardVO;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DashBoardDAO{
	
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
	public ArrayList<DashBoardVO> selectDashBoard(String UserId, int cnt) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<DashBoardVO>         arraylist   = new ArrayList<DashBoardVO>();
		DashBoardVO       dashboardvo = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();	
			strQuery.append("SELECT '요청건' GBN,A.CR_ACPTNO              \n");
            strQuery.append("     , TO_CHAR(A.CR_ACPTDATE, 'YYYY-MM-DD') CR_ACPTDATE \n");
            strQuery.append("     , SUBSTR(A.CR_SAYU,0,55)               CR_TITLE    \n");
            strQuery.append("     , B.CM_CODENAME REQUEST                \n");
            strQuery.append("     , C.CR_CONFNAME USERNAME               \n");
            strQuery.append("  FROM CMR9900 C,CMM0020 B,CMR1000 A        \n");
            strQuery.append(" WHERE A.CR_EDITOR=? AND A.CR_STATUS='0'    \n");
            strQuery.append("   AND A.CR_ACPTNO=C.CR_ACPTNO              \n");
            strQuery.append("   AND C.CR_LOCAT='00'                      \n");
            strQuery.append("   AND B.CM_MACODE='REQUEST'                \n");
            strQuery.append("   AND B.CM_MICODE=A.CR_QRYCD               \n");
            strQuery.append("UNION             \n");
			strQuery.append("SELECT '결재대상' GBN,A.CR_ACPTNO             \n");
            strQuery.append("     , TO_CHAR(A.CR_ACPTDATE, 'YYYY-MM-DD') CR_ACPTDATE \n");
            strQuery.append("     , SUBSTR(A.CR_SAYU,0,55)               CR_TITLE    \n");
            strQuery.append("     , B.CM_CODENAME REQUEST                \n");
            strQuery.append("     , C.CM_USERNAME USERNAME               \n");
            strQuery.append("  FROM CMR9900 D,CMM0040 C,CMM0020 B,CMR1000 A \n");
            strQuery.append(" WHERE A.CR_STATUS NOT IN ('3','9')         \n");
            strQuery.append("   AND A.CR_ACPTNO=D.CR_ACPTNO              \n");
            strQuery.append("   AND D.CR_LOCAT='00' AND D.CR_STATUS='0'  \n");
            strQuery.append("   AND D.CR_TEAMCD IN ('2','3','5','6','7') \n");
            strQuery.append("   AND D.CR_TEAM=?                          \n");
            strQuery.append("   AND B.CM_MACODE='REQUEST'                \n");
            strQuery.append("   AND B.CM_MICODE=A.CR_QRYCD               \n");
            strQuery.append("   AND A.CR_EDITOR=C.CM_USERID              \n");
            strQuery.append("UNION             \n");
			strQuery.append("SELECT '결재대상' GBN,A.CR_ACPTNO             \n");
            strQuery.append("     , TO_CHAR(A.CR_ACPTDATE, 'YYYY-MM-DD') CR_ACPTDATE \n");
            strQuery.append("     , SUBSTR(A.CR_SAYU,0,55)               CR_TITLE    \n");
            strQuery.append("     , B.CM_CODENAME REQUEST                \n");
            strQuery.append("     , C.CM_USERNAME USERNAME               \n");
            strQuery.append("  FROM CMM0043 E,CMR9900 D,CMM0040 C,CMM0020 B,CMR1000 A \n");
            strQuery.append(" WHERE A.CR_STATUS NOT IN ('3','9')         \n");
            strQuery.append("   AND A.CR_ACPTNO=D.CR_ACPTNO              \n");
            strQuery.append("   AND D.CR_LOCAT='00' AND D.CR_STATUS='0'  \n");
            strQuery.append("   AND E.CM_USERID=?                        \n");
            strQuery.append("   AND D.CR_TEAMCD NOT IN ('2','3','5','6','7') \n");
            strQuery.append("   AND D.CR_TEAM=E.CM_RGTCD                 \n");
            strQuery.append("   AND B.CM_MACODE='REQUEST'                \n");
            strQuery.append("   AND B.CM_MICODE=A.CR_QRYCD               \n");
            strQuery.append("   AND A.CR_EDITOR=C.CM_USERID              \n");
            strQuery.append("   AND ROWNUM     <  ?                      \n");
            strQuery.append("   order by  GBN,CR_ACPTDATE desc             \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, UserId);
            pstmt.setString(2, UserId);
            pstmt.setString(3, UserId);
            pstmt.setInt(4, cnt);
            
            rs = pstmt.executeQuery();
            //ecamsLogger.debug(rs.getRow()+"");
			while (rs.next()){
			//ecamsLogger.debug(rs.getString("CM_TITLE"));				
				dashboardvo = new DashBoardVO();
				dashboardvo.setCR_GBNCD(rs.getString("GBN"));
				dashboardvo.setCR_ACPTNO(rs.getString("CR_ACPTNO"));
				dashboardvo.setCR_ACPTDATE(rs.getString("CR_ACPTDATE"));
				dashboardvo.setCR_TITLE(rs.getString("CR_TITLE"));
				dashboardvo.setCR_REQUEST(rs.getString("REQUEST"));
				dashboardvo.setCR_EDITOR(rs.getString("USERNAME"));
				arraylist.add(dashboardvo);
				
				dashboardvo = null;
				if (arraylist.size()>=cnt) break;
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
			ecamsLogger.error("## DashBoardDAO.selectDashBoard() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## DashBoardDAO.selectDashBoard() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## DashBoardDAO.selectDashBoard() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## DashBoardDAO.selectDashBoard() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (arraylist != null) try{arraylist = null;}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## DashBoardDAO.selectDashBoard() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of selectDashBoard() method statement
	
}//end of DashBoardDAO class statement
