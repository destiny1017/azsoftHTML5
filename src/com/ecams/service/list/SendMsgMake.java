/*****************************************************************************************
	1. program ID	: SendMsgMake.java
	2. create date	: 2010.03
	3. auth		    : j.s.shin
	4. update date	: 
	5. auth		    : 
	6. description	: sendmsg make
*****************************************************************************************/

package com.ecams.service.list;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


public class SendMsgMake {
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 알림메시지Insert(메인화면)
	 */
	public int msgMake(String SendUser, String RecvUser,String RecvIp,String MsgTitle,String MsgBody,String MsgLink) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		//String strEnPassWd = usr_passwd;
		int parmCnt = 0;
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
   		    strQuery.append("INSERT INTO CMR9920                                   \n");
   		    strQuery.append("  (CR_DESTIP,CR_MAKEDATE,CR_MAKESEQ,CR_MAKETIME,      \n");
   		    strQuery.append("   CR_REQUESTUSR,CR_DOCTITLE,CR_DOCMSG,CR_SENDCNT,    \n");
   		    strQuery.append("   CR_RCVUSR,CR_SENDUSR,CR_LINKURL,CR_MAILFLAG)       \n");
   		    strQuery.append("(select ?, to_char(SYSDATE,'yyyymmdd'),               \n");
   		    strQuery.append("        nvl(max(cr_makeseq),0)+1,                     \n");
   		    strQuery.append("        SYSDATE, ?, ?, ?, 0, ?, ?, ?, '0'             \n");
   		    strQuery.append("   from cmr9920                                       \n");
   		    strQuery.append("  where cr_destip=?                                   \n");
   		    strQuery.append("    and cr_makedate=to_char(sysdate,'yyyymmdd'))      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(++parmCnt, RecvIp);
			pstmt.setString(++parmCnt, SendUser + "," + RecvUser);
			pstmt.setString(++parmCnt, MsgTitle);
			pstmt.setString(++parmCnt, MsgBody);
			pstmt.setString(++parmCnt, RecvUser);
			pstmt.setString(++parmCnt, SendUser);
			pstmt.setString(++parmCnt, MsgLink);
			pstmt.setString(++parmCnt, RecvIp);
			rs = pstmt.executeQuery();
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			//ecamsLogger.debug("++++++retCnt3+++"+Integer.toString(retCnt));
			return 0;
			//if(CNT2 < 1) return 9;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SendMsgMake.msgMake() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## SendMsgMake.msgMake() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SendMsgMake.msgMake() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## SendMsgMake.msgMake() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SendMsgMake.msgMake() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}