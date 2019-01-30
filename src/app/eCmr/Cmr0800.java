package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class Cmr0800 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public String request_SystemChange(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
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
			strQuery.append(",CR_NOAUTO,CR_PRCREQ,CR_EDDATE,CR_PRCDATE		\n");
			strQuery.append(",CR_EDITOR,CR_GYULJAE,CR_BEFJOB,CR_SAYU		\n");
			strQuery.append(",CR_SYSGB,CR_JOBCD)                            \n");
			strQuery.append("VALUES											\n");
			strQuery.append("(?, SYSDATE 									\n");
			strQuery.append(",'0', ? , ?									\n");
			strQuery.append(", ?, '0', ?									\n");
			strQuery.append(", '', '', '', '' 								\n");
			strQuery.append(", ?, 0, 0, ?, ?, ?)						    \n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("syscd"));
        	pstmt.setString(pstmtcount++, strTeam);
        	pstmt.setString(pstmtcount++, etcData.get("reqcd"));
        	pstmt.setString(pstmtcount++, etcData.get("sayu"));
        	pstmt.setString(pstmtcount++, etcData.get("userid"));
        	pstmt.setString(pstmtcount++, etcData.get("sayu"));
        	pstmt.setString(pstmtcount++, etcData.get("sysgb"));
        	pstmt.setString(pstmtcount++, etcData.get("jobcd"));
        	pstmt.executeUpdate();
        	pstmt.close();

        	pstmtcount = 1;
    		strQuery.setLength(0);
    		strQuery.append("insert into cmr1800 (cr_acptno,cr_serno,cr_acptdate,cr_teamcd,cr_jobcd,cr_editor,	\n");
    		strQuery.append("cr_status,cr_qrycd,cr_gbncd,cr_proddt,cr_reqtit,cr_reqsayu,cr_reqgb,cr_medium,		\n");
    		strQuery.append("cr_useperiod,cr_userepeat)															\n");
    		strQuery.append("values(?, '0001', SYSDATE, ?, ?, ?,                                 				\n");
    		strQuery.append("'0', ?, ?, ?, ?, ?, ?, ?, ?, ?)                                       				\n");
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmt.setString(pstmtcount++, AcptNo);
    		pstmt.setString(pstmtcount++, strTeam);
    		pstmt.setString(pstmtcount++, etcData.get("jobcd"));
    		pstmt.setString(pstmtcount++, etcData.get("userid"));
    		pstmt.setString(pstmtcount++, etcData.get("singb"));
    		pstmt.setString(pstmtcount++, etcData.get("reqsayucd"));
    		pstmt.setString(pstmtcount++, etcData.get("txtDate"));
    		pstmt.setString(pstmtcount++, etcData.get("gidae"));
    		pstmt.setString(pstmtcount++, etcData.get("detail"));
    		pstmt.setString(pstmtcount++, etcData.get("reqgb"));
    		pstmt.setString(pstmtcount++, etcData.get("maecha"));
    		pstmt.setString(pstmtcount++, etcData.get("bindocd"));
    		pstmt.setString(pstmtcount++, etcData.get("cnt"));

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

        	pstmt.close();

        	conn.close();

        	rs = null;
        	pstmt = null;

        	conn = null;

        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0800.request_SystemChange() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0800.request_SystemChange() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0800.request_SystemChange() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0800.request_SystemChange() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0800.request_SystemChange() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0800.request_SystemChange() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0800.request_SystemChange() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}