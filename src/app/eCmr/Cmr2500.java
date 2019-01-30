package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

import app.common.AutoSeq;
import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr2500 {
	
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	
	public String getProgId(String syscd, String progid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String 			  retMsg 	  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		
		try {
			conn = connectionContext.getConnection();	
			strQuery.setLength(0);
			strQuery.append(" select cr_story from cmr0020 					\n");
			strQuery.append("  where cr_syscd = ?  							\n");
			if(progid.indexOf(".")>0){
				strQuery.append(" and upper(cr_rsrcname)= upper(?)			\n");
			}else{
				strQuery.append(" and upper(cr_rsrcname) in (upper(?), upper(?), upper(?)) \n");
			}
	        pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,syscd);			
			if(progid.indexOf(".")>0){
				pstmt.setString(2,progid.toUpperCase());
			}else{
				pstmt.setString(2,progid.toUpperCase()+".LGC");
				pstmt.setString(3,progid.toUpperCase()+".PC");
				pstmt.setString(4,progid.toUpperCase()+".C");
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        
	        
			if (rs.next()){				
				retMsg = rs.getString("cr_story");
			}else{
				retMsg = "";
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return retMsg;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2500.getProgId() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr2500.getProgId() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2500.getProgId() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr2500.getProgId() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2500.getProgId() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement
	
	
	public String request_Batch(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList, ArrayList<HashMap<String,String>> reqList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		int				  pstmtcount1 = 1;
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
			strQuery.append(" insert into cmr1000  								  \n");
			strQuery.append(" (cr_ACPTNO,cr_ACPTDATE,cr_STATUS,cr_SYSCD,cr_TEAMCD,\n");
			strQuery.append(" cr_QRYCD,cr_passok,cr_PASSCD,cr_NOAUTO,cr_PRCREQ,   \n");
			strQuery.append(" cr_EDDATE,cr_PRCDATE,cr_EDITOR,cr_GYULJAE,cr_BEFJOB,\n");
			strQuery.append(" cr_SAYU,cr_sysgb,cr_jobcd)      					  \n");
			strQuery.append(" values (                                            \n");
			strQuery.append(" ?, SYSDATE, '0', ?, ?,                              \n");
			strQuery.append(" ?, '0', ?, '', '',                                  \n");
			strQuery.append(" '', '', ?, '0', '0',                                \n");
			strQuery.append(" '', ?, ?)                                    		  \n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmtcount1 = 1;
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
        	
        	
        	for(int i=0; i<reqList.size(); i++){
        		strQuery.setLength(0);
        		strQuery.append(" insert into cmr2200 									\n");
        		strQuery.append(" (cr_acptno,cr_serno,cr_acptdate,cr_teamcd,cr_jobcd,   \n");
        		strQuery.append(" cr_editor,cr_status,cr_pgmid,cr_step,cr_group,        \n");
        		strQuery.append(" cr_order,cr_pcircle,cr_continue,cr_chkpgmid,cr_batch, \n");
        		strQuery.append(" cr_control,cr_srtnm,cr_lstnm,cr_argument,cr_etc)      \n");
        		strQuery.append(" values(                                               \n");
        		strQuery.append(" ?, ?, SYSDATE, ?, ?,                                  \n");
        		strQuery.append(" ?, '0', ?, ?, ?,                                      \n");
        		strQuery.append(" ?, ?, ?, ?, ?,                                        \n");
        		strQuery.append(" ?, ?, ?, ?, ?)                                        \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
        		pstmt =  new LoggableStatement(conn, strQuery.toString());
        		pstmtcount1 = 1;
        		pstmt.setString(pstmtcount1++, AcptNo);
        		pstmt.setInt(pstmtcount1++, i+1);
        		pstmt.setString(pstmtcount1++, strTeam);
        		pstmt.setString(pstmtcount1++, etcData.get("jobcd"));
        		pstmt.setString(pstmtcount1++, etcData.get("userid"));
        		
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("progid"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("step"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("group"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("seq"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("term"));
        		
        		if(reqList.get(i).get("contyn").equals("계속")){
        			pstmt.setString(pstmtcount1++, "0");
        		}else{
        			pstmt.setString(pstmtcount1++, "1");
        		}
        		//pstmt1.setString(pstmtcount1++, etcData.get("r_continue"));
        		
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("ckprog"));
        		
        		if(reqList.get(i).get("batch").equals("Yes")){
        			pstmt.setString(pstmtcount1++, "0");
        		}else{
        			pstmt.setString(pstmtcount1++, "1");
        		}
        		//pstmt1.setString(pstmtcount1++, etcData.get("r_batch"));
        		
        		if(reqList.get(i).get("control").equals("계속")){
        			pstmt.setString(pstmtcount1++, "0");
        		}else{
        			pstmt.setString(pstmtcount1++, "1");
        		}
        		//pstmt.setString(pstmtcount1++, etcData.get("r_control"));
        		
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("stpos"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("edpos"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("arg"));
        		pstmt.setString(pstmtcount1++, reqList.get(i).get("etc"));
        		
        		ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        		pstmt.executeUpdate();
        		pstmt.close();
        	}
        	
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
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2500.request_Batch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2500.request_Batch() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr2500.request_Batch() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2500.request_Batch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2500.request_Batch() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr2500.request_Batch() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2500.request_Batch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}