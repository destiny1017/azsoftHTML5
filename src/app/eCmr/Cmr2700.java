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

public class Cmr2700 {
	
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getDayBatchList(String txtdate, String userid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String	 		  flg 		  = null;
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select b.cr_status  								\n");
			strQuery.append("  from (select cr_acptno from cmr2700  			\n");
			strQuery.append("         where CR_TIME = ? 						\n");
			strQuery.append("           and cr_editor = ? 						\n");
			strQuery.append("         order by cr_acptdate desc) a, cmr1000 b 	\n");
			strQuery.append(" where rownum = 1 									\n");
			strQuery.append("   and a.cr_acptno = b.cr_acptno					\n");
			   
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, txtdate);
			pstmt.setString(2, userid);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			flg = "X";
			if(rs.next()){
				ecamsLogger.error("==========cr_status "+ rs.getString("cr_status"));
				if(!rs.getString("cr_status").equals("3")){ //마지막 신청한 건이 반려가 아님 -> 신청한내용조회
					flg = "Y";
					strQuery.setLength(0);
					strQuery.append(" SELECT a.cr_acptno, a.cr_serno, a.cr_jobstatus,		\n");
					strQuery.append("        a.cr_jobstart, b.cd_editor, b.cd_jobtime, b.cd_seq, 		\n");
					strQuery.append("        a.cr_jobend, a.cr_runtime, a.cr_bigo, b.cd_jobstep, 		\n");
					strQuery.append("        b.cd_grp, b.cd_gubun, b.cd_check, b.cd_color, c.cr_passcd, \n");
					strQuery.append("        a.CR_JOBUSR, a.CR_NIGHTUSR, a.CR_JOBCD, c.CR_SYSCD			\n");
					strQuery.append("   FROM CMR2700 a, CMD1700 b, cmr1000 c                   			\n");
					strQuery.append("  where a.CR_TIME = ? 												\n");
					strQuery.append("    and a.cr_acptno = c.cr_acptno									\n");
					strQuery.append("    and c.cr_status <> '3'											\n");
					strQuery.append("    and a.cr_seq = b.cd_seq										\n");
					strQuery.append("    and b.cd_closedt is null                             			\n");
					strQuery.append("    and b.cd_acptdt = '99999999'                               	\n");
					strQuery.append("  ORDER BY b.cd_seq												\n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 =  new LoggableStatement(conn, strQuery.toString());
					pstmt2.setString(1, txtdate);
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					rtList.clear();
					while (rs2.next()){
						rst = new HashMap<String,String>();
						rst.put("acptno", rs2.getString("cr_acptno")); 									      
						rst.put("serno", rs2.getString("cr_serno"));  	
						rst.put("cr_status", rs.getString("cr_status")); 
						
						rst.put("CD_SEQ", rs2.getString("cd_seq")); 									      
						rst.put("CD_JOBTIME", rs2.getString("cd_jobtime"));  									  
						rst.put("CD_JOBSTEP", rs2.getString("cd_jobstep"));  
						rst.put("CD_EDITOR", rs2.getString("cd_editor")); 
						rst.put("CR_JOBUSR", rs2.getString("CR_JOBUSR")); 
						rst.put("CR_NIGHTUSR", rs2.getString("CR_NIGHTUSR")); 
						rst.put("CR_JOBCD", rs2.getString("CR_JOBCD")); 
						rst.put("CR_SYSCD", rs2.getString("CR_SYSCD")); 
						rst.put("cr_passcd", rs2.getString("cr_passcd")); 
						
						if(rs2.getString("cd_grp")!=null) rst.put("CD_GRP", rs2.getString("cd_grp")); 
						else rst.put("CD_GRP", ""); 
						
						if(rs2.getString("cd_gubun")!=null) rst.put("CD_GUBUN", rs2.getString("cd_gubun")); 
						else rst.put("CD_GUBUN", ""); 
						
						if(rs2.getString("cd_check")!=null) rst.put("CD_CHECK", rs2.getString("cd_check")); 
						else rst.put("CD_CHECK", ""); 
						
						if(rs2.getString("cd_color")!=null) rst.put("CD_COLOR", rs2.getString("cd_color")); 
						else rst.put("CD_COLOR", ""); 
						
						if(rs2.getString("cr_jobstatus")!=null) rst.put("jobstatus", rs2.getString("cr_jobstatus"));
						else rst.put("jobstatus", "");
						
						if(rs2.getString("cr_jobstart")!=null) rst.put("srtime", rs2.getString("cr_jobstart"));
						else rst.put("srtime", "");
						
						if(rs2.getString("cr_jobend")!=null) rst.put("edtime", rs2.getString("cr_jobend"));
						else rst.put("edtime", "");
						
						if(rs2.getString("cr_runtime")!=null) rst.put("spendtime", rs2.getString("cr_runtime"));
						else rst.put("spendtime", "");
						
						if(rs2.getString("cr_bigo")!=null) rst.put("etc", rs2.getString("cr_bigo"));
						else rst.put("etc", "");

						
						rtList.add(rst);
						
						rst = null;
					}//end of while-loop statement
				}else{
					flg = "Z"; //마지막 신청한 건이 반려됨 
				}
			}else{
				flg = "X"; //기준일에따른 신청건이 없는경우
			}
			
			if(flg == "Z" || flg == "X"){ //!rs.next() && rs.getString("cr_status").equals("3")
				strQuery.setLength(0);
				strQuery.append("select CD_SEQ,CD_JOBTIME,CD_JOBSTEP,CD_GRP,  					\n");
				strQuery.append("       CD_GUBUN,CD_CHECK,CD_EDITOR,CD_COLOR  					\n");
				strQuery.append("  from cmd1700                           						\n");
				strQuery.append(" where cd_closedt is null                             			\n");
				strQuery.append("   and cd_acptdt = '99999999'                               	\n");
				strQuery.append("   order by cd_seq                         					\n");
	            pstmt2 = conn.prepareStatement(strQuery.toString());	
				//pstmt2 =  new LoggableStatement(conn, strQuery.toString());
				
				//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
	            rs2 = pstmt2.executeQuery();
	            rtList.clear();
	            
				while (rs2.next()){
					rst = new HashMap<String,String>();
					rst.put("acptno", ""); 									      
					rst.put("serno", "");  	
					if(flg == "Z"){
						rst.put("cr_status", rs.getString("cr_status"));
					}else{
						rst.put("cr_status", "5");
					}
					rst.put("CR_JOBUSR", ""); 
					rst.put("CR_NIGHTUSR", ""); 
					rst.put("CR_JOBCD", ""); 
					rst.put("CR_SYSCD", ""); 
					rst.put("cr_passcd", ""); 
					
					rst.put("CD_SEQ", rs2.getString("CD_SEQ")); 									      
					rst.put("CD_JOBTIME", rs2.getString("CD_JOBTIME"));  									  
					rst.put("CD_JOBSTEP", rs2.getString("CD_JOBSTEP"));  
					rst.put("CD_EDITOR", rs2.getString("CD_EDITOR")); 
					
					if(rs2.getString("CD_GRP")!=null){
						rst.put("CD_GRP", rs2.getString("CD_GRP")); 
					}else{
						rst.put("CD_GRP", ""); 
					}
					
					if(rs2.getString("CD_GUBUN")!=null){
						rst.put("CD_GUBUN", rs2.getString("CD_GUBUN")); 
					}else{
						rst.put("CD_GUBUN", ""); 
					}
					
					if(rs2.getString("CD_CHECK")!=null){
						rst.put("CD_CHECK", rs2.getString("CD_CHECK")); 
					}else{
						rst.put("CD_CHECK", ""); 
					}
					
					if(rs2.getString("CD_COLOR")!=null){
						rst.put("CD_COLOR", rs2.getString("CD_COLOR")); 
					}else{
						rst.put("CD_COLOR", ""); 
					}
					
					rst.put("jobstatus", "");
					rst.put("srtime", "");
					rst.put("edtime", "");
					rst.put("spendtime", "");
					rst.put("etc", "");
					
					rtList.add(rst);
					
					rst = null;
				}//end of while-loop statement	
			}
			
			
			rs.close();
			rs2.close();
			pstmt.close();
			pstmt2.close();
			
			conn.close();

			rs = null;
			rs2 = null;
			pstmt = null;
			pstmt2 = null;
			
			conn = null;
					
			return rtList.toArray();			
					
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2700.getDayBatchList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr2700.getDayBatchList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2700.getDayBatchList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr2700.getDayBatchList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2700.getDayBatchList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public String request_DayBatch(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList, ArrayList<HashMap<String,String>> reqList, String flg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
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
        	
        	//ecamsLogger.error("@@@@@@@@@@@@@@@@@@@@acptno "+reqList.get(0).get("acptno"));
        	
        	//임시저장 
			if(reqList.get(0).get("cr_status").equals("3")
				|| reqList.get(0).get("acptno") == null || reqList.get(0).get("acptno") == ""){
				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMR1000 							\n");		
				strQuery.append("(CR_ACPTNO,CR_ACPTDATE  						\n");		
				strQuery.append(",CR_STATUS,CR_SYSCD,CR_TEAMCD					\n");	
				strQuery.append(",CR_QRYCD,CR_PASSOK,CR_PASSCD					\n");	
				strQuery.append(",CR_NOAUTO,CR_PRCREQ,CR_EDDATE,CR_PRCDATE		\n");			     
				strQuery.append(",CR_EDITOR,CR_GYULJAE,CR_BEFJOB,CR_SAYU		\n");		       
				strQuery.append(",CR_SYSGB,CR_JOBCD)							\n");			     
				strQuery.append("VALUES											\n");			     
				strQuery.append("(?, SYSDATE 									\n");			     
				strQuery.append(", ?, ? , ?										\n");					 
				strQuery.append(", ?, '0', ?									\n");							
				strQuery.append(", '', '', '', '' 								\n");			     
				strQuery.append(", ?, 0, 0, '' 									\n");
				strQuery.append(", ?, ?)										\n");
				
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount1 = 1;
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	if(flg.equals("5")) pstmt.setString(pstmtcount++, "5");
	        	else pstmt.setString(pstmtcount++, "0");
	        	pstmt.setString(pstmtcount++, etcData.get("syscd"));
	        	pstmt.setString(pstmtcount++, strTeam);
	        	pstmt.setString(pstmtcount++, etcData.get("reqcd"));
	        	pstmt.setString(pstmtcount++, etcData.get("sayu"));
	        	pstmt.setString(pstmtcount++, etcData.get("userid"));
	        	pstmt.setString(pstmtcount++, etcData.get("sysgb"));
	        	pstmt.setString(pstmtcount++, etcData.get("jobcd"));
	        	pstmt.executeUpdate();
	        	pstmt.close();
	        	
	        	for(int i=0; i<reqList.size(); i++){
	        		strQuery.setLength(0);
	        		strQuery.append("insert into cmr2700 (CR_ACPTNO,CR_SERNO,CR_ACPTDATE,CR_TEAMCD,							\n");
	        		strQuery.append("CR_JOBCD,CR_EDITOR,CR_STATUS,CR_JOBUSR,CR_NIGHTUSR,CR_SEQ,                     		\n");
	        		strQuery.append("CR_JOBSTATUS,CR_JOBSTART,CR_JOBEND,CR_RUNTIME,CR_BIGO,CR_TIME)                     	\n");
	        		strQuery.append("values(?, ?, SYSDATE, ?,                                 								\n");
	        		strQuery.append(" ?, ?, ?, ?, ?, ?,                                                      				\n");
	        		strQuery.append(" ?, ?, ?, ?, ?, ?)                                                      				\n");
	        		//pstmt = conn.prepareStatement(strQuery.toString());
	        		pstmt =  new LoggableStatement(conn, strQuery.toString());
	        		pstmtcount1 = 1;
	        		pstmt.setString(pstmtcount1++, AcptNo);
	        		pstmt.setString(pstmtcount1++, String.format("%04d", i+1));
	        		pstmt.setString(pstmtcount1++, strTeam);
	        		pstmt.setString(pstmtcount1++, etcData.get("jobcd"));
	        		pstmt.setString(pstmtcount1++, etcData.get("userid"));
	        		if(flg.equals("5")) pstmt.setString(pstmtcount1++, "5");
		        	else pstmt.setString(pstmtcount1++, "0");
	        		pstmt.setString(pstmtcount1++, etcData.get("name1"));
	        		pstmt.setString(pstmtcount1++, etcData.get("name2"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("CD_SEQ"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("jobstatus"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("srtime"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("edtime"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("spendtime"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("etc"));
	        		pstmt.setString(pstmtcount1++, etcData.get("txtDate")); //기준일
	        		ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        		pstmt.executeUpdate();
	        		pstmt.close();
	        	}
			}else{//이미 임시저장한 후 임시저장 또는 신청 할 경우
				strQuery.setLength(0);
				strQuery.append(" UPDATE CMR1000  										\n");
				strQuery.append("    SET CR_STATUS = ?									\n");
				strQuery.append("        ,CR_PASSCD = ?									\n");
				strQuery.append("        ,CR_SYSCD = ?									\n");
				strQuery.append("        ,CR_SYSGB = ?									\n");
				strQuery.append("        ,CR_JOBCD = ?									\n");
				strQuery.append("  WHERE CR_ACPTNO = ?								  	\n");
				pstmt = conn.prepareStatement(strQuery.toString());
        		if(!flg.equals("5")){ //임시저장후 신청
        			pstmt.setString(1, "0");
        		}else{
        			pstmt.setString(1, "5");
        		}
        		pstmt.setString(2, etcData.get("sayu"));
        		pstmt.setString(3, etcData.get("syscd"));
        		pstmt.setString(4, etcData.get("sysgb"));
        		pstmt.setString(5, etcData.get("jobcd"));
        		pstmt.setString(6, reqList.get(0).get("acptno"));
        		pstmt.executeUpdate();
        		pstmt.close();
        		
				for(int i=0; i<reqList.size(); i++){
	        		strQuery.setLength(0);
	        		strQuery.append("UPDATE cmr2700 SET CR_JOBCD=?,CR_STATUS=?,CR_JOBUSR=?,     \n");
	        		strQuery.append("CR_NIGHTUSR=?,CR_SEQ=?,CR_JOBSTATUS=?,CR_JOBSTART=?,       \n");
	        		strQuery.append("CR_JOBEND=?,CR_RUNTIME=?,CR_BIGO=?,CR_TIME=?               \n");
	        		strQuery.append("WHERE CR_ACPTNO = ?                                        \n");
	        		strQuery.append("  AND CR_SERNO = ?		                                    \n");
	        		
	        		pstmt = conn.prepareStatement(strQuery.toString());
	        		pstmtcount1 = 1;
	        		pstmt.setString(pstmtcount1++, etcData.get("jobcd"));
	        		if(flg.equals("5")) pstmt.setString(pstmtcount1++, "5");
		        	else pstmt.setString(pstmtcount1++, "0");
	        		pstmt.setString(pstmtcount1++, etcData.get("name1"));
	        		pstmt.setString(pstmtcount1++, etcData.get("name2"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("CD_SEQ"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("jobstatus"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("srtime"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("edtime"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("spendtime"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("etc"));
	        		pstmt.setString(pstmtcount1++, etcData.get("txtDate")); //기준일
	        		pstmt.setString(pstmtcount1++, reqList.get(0).get("acptno"));
	        		pstmt.setString(pstmtcount1++, reqList.get(i).get("serno"));
	        		
	        		pstmt.executeUpdate();
	        		pstmt.close();
				}
			}
			
			ecamsLogger.error(flg);
			
			if(flg.equals("5")){
				conn.commit();
			}else{
				ecamsLogger.error(reqList.get(0).get("acptno"));
				
				if(reqList.get(0).get("acptno") == null || reqList.get(0).get("acptno") == ""){
					retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("syscd"),etcData.get("reqcd"),etcData.get("userid"),true,ConfList,conn);
				}else{
					retMsg = cmr0200.request_Confirm(reqList.get(0).get("acptno"),etcData.get("syscd"),etcData.get("reqcd"),etcData.get("userid"),true,ConfList,conn);
				}
	        	if (!retMsg.equals("OK")) {
					conn.rollback();
					conn.close();
					throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
				} else {
					conn.commit();
				}
			}
        	
        	conn.close();
        	
        	pstmt = null;
        	
        	conn = null;
        	
        	if(reqList.get(0).get("acptno") == null || reqList.get(0).get("acptno") == ""){
        		return AcptNo;
        	}else{
        		return reqList.get(0).get("acptno");
        	}
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2700.request_DayBatch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2700.request_DayBatch() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr2700.request_DayBatch() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2700.request_DayBatch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2700.request_DayBatch() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr2700.request_DayBatch() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2700.request_DayBatch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}