
/*****************************************************************************************
	1. program ID	: Bbs_DAO.java
	2. create date	: 2008.03. 14
	3. auth		    : jung
	4. update date	: 
	5. auth		    : 
	6. description	: Conninfo DAO
*****************************************************************************************/

package com.ecams.service.conninfo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.conninfo.valueobject.ConninfoVO;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConninfoDAO{
	
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

	public ArrayList<ConninfoVO> Conninfo(String gbn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<ConninfoVO>         arraylist   = new ArrayList<ConninfoVO>();
		ConninfoVO        conninfovo      = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();	
			strQuery.append("SELECT A.CM_USERID   \n");
            strQuery.append("     , A.CM_USERNAME \n");
            strQuery.append("     , A.CM_LOGINDT  \n");
            strQuery.append("     , A.CM_POSITION \n");
            strQuery.append("     , A.CM_DUTY \n");
            strQuery.append("     , A.CM_IPADDRESS \n");
            strQuery.append("     , A.CM_TELNO1 \n");
            strQuery.append("     , B.CM_CODENAME as POSITION \n");
            strQuery.append("     , C.CM_CODENAME as DUTY \n");
            strQuery.append("  FROM CMM0040 A, CMM0020 B, CMM0020 C \n");
            strQuery.append(" WHERE A.CM_STATUS= ? \n");
            strQuery.append("   AND A.CM_CLSDATE is null \n");
            strQuery.append("   AND B.CM_MACODE = 'POSITION' AND B.CM_MICODE = a.CM_POSITION \n");
            strQuery.append("   AND C.CM_MACODE = 'DUTY' AND C.CM_MICODE = a.CM_DUTY \n");
            strQuery.append("   order by  A.CM_LOGINDT  \n");                
            pstmt = conn.prepareStatement(strQuery.toString());	
            pstmt.setString(1, gbn);           
            
            rs = pstmt.executeQuery();
            //ecamsLogger.debug(rs.getRow()+"");
			while (rs.next()){					
				conninfovo = new ConninfoVO();
				conninfovo.setCM_USERID(rs.getString("CM_USERID"));
				conninfovo.setCM_USERNAME(rs.getString("CM_USERNAME"));
				conninfovo.setCM_LOGINDT(rs.getString("CM_LOGINDT"));
				conninfovo.setCM_POSITION(rs.getString("CM_POSITION"));
				conninfovo.setCM_DUTY(rs.getString("CM_DUTY"));
				conninfovo.setCM_IPADDRESS(rs.getString("CM_IPADDRESS"));
				conninfovo.setCM_TELNO1(rs.getString("CM_TELNO1"));
				conninfovo.setPOSITION(rs.getString("POSITION"));
				conninfovo.setDUTY(rs.getString("DUTY"));
				arraylist.add(conninfovo);
				
			}//end of while-loop statement		
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## ConninfoDAO.Conninfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## ConninfoDAO.Conninfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## ConninfoDAO.Conninfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## ConninfoDAO.Conninfo() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## ConninfoDAO.Conninfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		return arraylist;
		
	}//end of Conninfo() method statement
	
}//end of BbsDAO class statement
