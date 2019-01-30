package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

import app.common.AutoSeq;
//import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0900 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();




	public String request_receiveID(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		String			  retMsg	  = null;
		String			  AcptNo	  = null;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo    = new UserInfo();
		Cmr0200 		  cmr0200 	  = new Cmr0200();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			AcptNo = autoseq.getSeqNo(conn,etcData.get("reqcd"));
        	String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("userid"),"cm_project");

			strQuery.setLength(0);
			strQuery.append("INSERT INTO CMR1000 							\n");
			strQuery.append("(CR_ACPTNO,CR_ACPTDATE  						\n");
			strQuery.append(",CR_STATUS,CR_SYSCD,CR_TEAMCD					\n");
			strQuery.append(",CR_QRYCD,CR_PASSOK,CR_PASSCD					\n");
			strQuery.append(",CR_EDITOR,CR_GYULJAE,CR_BEFJOB    		    \n");
			strQuery.append(",CR_SYSGB,CR_JOBCD)							\n");
			strQuery.append("VALUES											\n");
			strQuery.append("(?, SYSDATE 									\n");
			strQuery.append(",'0', ? , ?									\n");
			strQuery.append(", ?, '0', ?									\n");
			strQuery.append(", ?, '0', '0'									\n");
			strQuery.append(", ?, ?) 										\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("syscd"));
        	pstmt.setString(pstmtcount++, strTeam);
        	pstmt.setString(pstmtcount++, etcData.get("reqcd"));
        	pstmt.setString(pstmtcount++, etcData.get("passcd"));
        	pstmt.setString(pstmtcount++, etcData.get("userid"));
        	pstmt.setString(pstmtcount++, etcData.get("sysgb"));
        	pstmt.setString(pstmtcount++, etcData.get("jobcd"));
        	pstmt.executeUpdate();
        	pstmt.close();

        	pstmtcount = 1;
    		strQuery.setLength(0);
    		strQuery.append("insert into cmr1900 (cr_acptno,cr_serno,cr_acptdate,cr_teamcd,cr_jobcd,cr_editor,			\n");
    		strQuery.append("CR_STATUS,cr_qrycd,CR_REQCD,CR_MANID,CR_PRODDT,CR_USEID,CR_USESYS,CR_USESECU,				\n");
    		strQuery.append("CR_USERUN,CR_HOMEDIR, CR_GROUPID, CR_SVRSHELL, CR_UID,CR_IDGBN, CR_SVRGBN) 		        \n");
    		strQuery.append("values(?, 1, SYSDATE, ?, ?, ?,                                 						    \n");
    		strQuery.append("'0', ?, ?, ?, ?, ?, ?, ?,				                                       				\n");
    		strQuery.append("?, ?, ?, ?, ?, ?, ?)  		                               									\n");
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmt.setString(pstmtcount++, AcptNo);
    		pstmt.setString(pstmtcount++, strTeam);
    		pstmt.setString(pstmtcount++, etcData.get("jobcd"));
    		pstmt.setString(pstmtcount++, etcData.get("userid"));

    		pstmt.setString(pstmtcount++, etcData.get("idgbn"));
			pstmt.setString(pstmtcount++, etcData.get("reqgbn"));
			pstmt.setString(pstmtcount++, etcData.get("staff"));
			pstmt.setString(pstmtcount++, etcData.get("datdeploy"));
			pstmt.setString(pstmtcount++, etcData.get("user"));
			pstmt.setString(pstmtcount++, etcData.get("daesys"));
			pstmt.setString(pstmtcount++, etcData.get("secu"));


			pstmt.setString(pstmtcount++, etcData.get("runmsg"));
			pstmt.setString(pstmtcount++, etcData.get("home"));
			pstmt.setString(pstmtcount++, etcData.get("group"));
			pstmt.setString(pstmtcount++, etcData.get("shell"));
			pstmt.setString(pstmtcount++, etcData.get("uid"));
			pstmt.setString(pstmtcount++, etcData.get("dbidgbn"));
			pstmt.setString(pstmtcount++, etcData.get("daesyscd"));
    		pstmt.executeUpdate();
    		pstmt.close();

        	retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("syscd"),etcData.get("reqcd"),etcData.get("userid"),true,ConfList,conn);

        	if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			} else {
				conn.commit();
			}


        	conn.close();

        	pstmt = null;

        	conn = null;

        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3700.request_DBInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0900.request_DBInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0900.request_DBInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3700.request_DBInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0900.request_DBInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0900.request_DBInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0900.request_DBInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}