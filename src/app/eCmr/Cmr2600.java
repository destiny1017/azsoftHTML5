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

public class Cmr2600 {
	
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
	
	public Object[] getSystemList(String txtdate, String gbn, String radio, String userid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			          rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String			  gbnname	        = null;
		String	 		  flg 		        = null;
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select b.cr_status  								\n");
			strQuery.append("  from (select cr_acptno from cmr2600  			\n");
			strQuery.append("         where CR_REQDAY = ? 						\n");
			if(radio.equals("0")){
				strQuery.append("           and cr_jobgbn = '0' 				\n");
			}else{
				strQuery.append("           and cr_jobgbn = '1' 				\n");
			}
			strQuery.append("           and cr_editor = ? 						\n");
			strQuery.append("         order by cr_acptdate desc) a, cmr1000 b 	\n");
			strQuery.append(" where rownum = 1 									\n");
			strQuery.append("   and a.cr_acptno = b.cr_acptno					\n");
			   
			pstmt = conn.prepareStatement(strQuery.toString());
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
					strQuery.append(" SELECT a.CR_GBNCD, a.CR_DETCD, a.CR_EDITOR, a.CR_RSTSTA, a.cr_acptno,	\n");
					strQuery.append("        b.cd_acptdt, b.CD_GBNNAME, b.CD_DETNAME, b.CD_SUBNAME, a.cr_serno,			\n");
					strQuery.append("        a.CR_SENDUSR, a.CR_RECVUSR, c.cr_passcd			\n");
					strQuery.append("   FROM CMR2600 a, CMD1600 b, cmr1000 c                    \n");
					strQuery.append("  where a.CR_REQDAY = ? 									\n");
					strQuery.append("    and a.cr_acptno = c.cr_acptno							\n");
					strQuery.append("    and c.cr_status <> '3'									\n");
					strQuery.append("    and a.cr_gbncd = b.cd_gbncd							\n");
					strQuery.append("    and a.cr_detcd = b.cd_detcd							\n");
					strQuery.append("    and b.CD_CLOSEDT IS NULL								\n");
					if(radio.equals("0")){
						strQuery.append("    and a.cr_jobgbn = '0'	   							\n");
					}else{
						strQuery.append("    and a.cr_jobgbn = '1'	   							\n");
					}
					if(gbn.equals("1")){                                                      
					   strQuery.append(" AND SUBSTR(b.CD_GBNNAME,1,3) <> 'OP간'           		\n");
					}else{                                                                   
					   strQuery.append(" AND SUBSTR(b.CD_GBNNAME,1,3) = 'OP간'            		\n");
					}
					strQuery.append("  ORDER BY a.Cr_GBNCD, a.Cr_DETCD							\n");
					
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 =  new LoggableStatement(conn, strQuery.toString());
					pstmt2.setString(1, txtdate);
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					rtList.clear();
					
					while(rs2.next()){
						rst = new HashMap<String,String>();
						rst.put("acptno", rs2.getString("cr_acptno"));
						rst.put("serno", rs2.getString("cr_serno"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("sendusr", rs2.getString("CR_SENDUSR"));
						rst.put("recvusr", rs2.getString("CR_RECVUSR"));
						rst.put("passcd", rs2.getString("cr_passcd"));
						
						if(gbn.equals("1")){    
							if(rs2.getString("CD_GBNNAME").equals(gbnname) || rs2.getString("CD_GBNNAME")==gbnname){
								rst.put("CD_GBNNAME", "");
							}else{
								rst.put("CD_GBNNAME", rs2.getString("CD_GBNNAME"));
							}
							gbnname = rs2.getString("CD_GBNNAME");
							                                   
							rst.put("CD_ACPTDT", rs2.getString("CD_ACPTDT"));                                                                                                                                                 
							rst.put("CD_GBNCD", rs2.getString("CR_GBNCD"));
							rst.put("CD_DETCD", rs2.getString("CR_DETCD"));
							rst.put("CD_SUBNAME", rs2.getString("CD_SUBNAME"));
							rst.put("CD_EDITOR", rs2.getString("cR_editor"));
							rst.put("CD_DETNAME", rs2.getString("CD_DETNAME"));
						    
							rst.put("detname1", rs2.getString("CR_RSTSTA").split(",")[0]);
							rst.put("detname2", rs2.getString("CR_RSTSTA").split(",")[1]);
							rst.put("detname3", rs2.getString("CR_RSTSTA").split(",")[2]);
							rst.put("detname4", rs2.getString("CR_RSTSTA").split(",")[3]);
							rst.put("detname5", rs2.getString("CR_RSTSTA").split(",")[4]);
							rst.put("detname6", rs2.getString("CR_RSTSTA").split(",")[5]);
							rst.put("detname7", rs2.getString("CR_RSTSTA").split(",")[6]);
							rst.put("detname8", rs2.getString("CR_RSTSTA").split(",")[7]);
							rst.put("detname9", rs2.getString("CR_RSTSTA").split(",")[8]);
							rst.put("detname10", rs2.getString("CR_RSTSTA").split(",")[9]);
							rst.put("detname11", rs2.getString("CR_RSTSTA").split(",")[10]);
							rst.put("detname12", rs2.getString("CR_RSTSTA").split(",")[11]);
						}else{
							if(rs2.getString("CD_GBNNAME").equals(gbnname) ||  rs2.getString("CD_GBNNAME")==gbnname){
								rst.put("CD_GBNNAME1", "");
							}else{
								rst.put("CD_GBNNAME1", rs2.getString("CD_GBNNAME"));
							}
							gbnname = rs2.getString("CD_GBNNAME");
							                                   
							rst.put("CD_ACPTDT1", rs2.getString("CD_ACPTDT"));                                                                                                                                                 
							rst.put("CD_GBNCD1", rs2.getString("CR_GBNCD"));
							rst.put("CD_DETCD1", rs2.getString("CR_DETCD"));
							rst.put("CD_SUBNAME1", rs2.getString("CD_SUBNAME"));
							rst.put("CD_EDITOR1", rs2.getString("CR_EDITOR"));
							rst.put("CD_DETNAME1", rs2.getString("CD_DETNAME"));
						    
							if(rs2.getString("CR_RSTSTA") != null){
								if(rs2.getString("CR_RSTSTA").split(",")[0] == null || rs2.getString("CR_RSTSTA").split(",")[0] == ""){
									rst.put("detname13", "");
								}else{
									rst.put("detname13", rs2.getString("CR_RSTSTA").split(",")[0]);
								}
								if(rs2.getString("CR_RSTSTA").split(",")[1] == null || rs2.getString("CR_RSTSTA").split(",")[1] == ""){
									rst.put("detname14", "");
								}else{
									rst.put("detname14", rs2.getString("CR_RSTSTA").split(",")[1]);
								}
							}else{
								rst.put("detname13", "");
								rst.put("detname14", "");
							}
						}
						rtList.add(rst);
						
						rst = null;
					}
				}else{
					flg = "Z"; //마지막 신청한 건이 반려됨 
				}
			}else{
				flg = "X"; //기준일에따른 신청건이 없는경우
			}
			if(flg == "Z" || flg == "X"){ //!rs.next() || rs.getString("cr_status").equals("3")
				strQuery.setLength(0);
				strQuery.append(" SELECT CD_ACPTDT, CD_GBNCD, CD_DETCD, CD_GBNNAME, \n");
				strQuery.append("        CD_DETNAME, CD_SUBNAME, CD_EDITOR          \n");
				strQuery.append("   FROM CMD1600                                    \n");
				strQuery.append("  WHERE CD_ACPTDT > ?                              \n");
				strQuery.append("    AND TO_CHAR(CD_CREATDT,'YYYYMMDD') <= ?        \n");
				strQuery.append("    AND CD_CLOSEDT IS NULL                         \n");
				                                                                         
				if(gbn.equals("1")){                                                      
				   strQuery.append(" AND SUBSTR(CD_GBNNAME,1,3) <> 'OP간'           \n");
				}else{                                                                   
				   strQuery.append(" AND SUBSTR(CD_GBNNAME,1,3) = 'OP간'            \n");
				}                                                                        
				                                                                         
				strQuery.append(" ORDER BY CD_GBNCD,CD_DETCD              			\n");

	            pstmt2 = conn.prepareStatement(strQuery.toString());	
				pstmt2 =  new LoggableStatement(conn, strQuery.toString());
	            pstmt2.setString(1, txtdate);
	            pstmt2.setString(2, txtdate);
				ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				
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
					rst.put("sendusr", "");
					rst.put("recvusr", "");
					rst.put("passcd", "");
					
					if(gbn.equals("1")){    
						if(rs2.getString("CD_GBNNAME").equals(gbnname)){
							rst.put("CD_GBNNAME", "");
						}else{
							rst.put("CD_GBNNAME", rs2.getString("CD_GBNNAME"));
						}
						gbnname = rs2.getString("CD_GBNNAME");
						                                   
						rst.put("CD_ACPTDT", rs2.getString("CD_ACPTDT"));                                                                                                                                                 
						rst.put("CD_GBNCD", rs2.getString("CD_GBNCD"));
						rst.put("CD_DETCD", rs2.getString("CD_DETCD"));
						rst.put("CD_SUBNAME", rs2.getString("CD_SUBNAME"));
						rst.put("CD_EDITOR", rs2.getString("CD_EDITOR"));
						rst.put("CD_DETNAME", rs2.getString("CD_DETNAME"));
					       
						if(rs2.getString("CD_DETNAME").equals("항온항습기")){
							for(int i=1; i<=12; i++){
								rst.put("detname"+i, "22/38");
							}
						}else{
							for(int i=1; i<=12; i++){
								rst.put("detname"+i, "0");
							}
						}
					}else{
						if(rs2.getString("CD_GBNNAME").equals(gbnname)){
							rst.put("CD_GBNNAME1", "");
						}else{
							rst.put("CD_GBNNAME1", rs2.getString("CD_GBNNAME"));
						}
						gbnname = rs2.getString("CD_GBNNAME");
						                                   
						rst.put("CD_ACPTDT1", rs2.getString("CD_ACPTDT"));                                                                                                                                                 
						rst.put("CD_GBNCD1", rs2.getString("CD_GBNCD"));
						rst.put("CD_DETCD1", rs2.getString("CD_DETCD"));
						rst.put("CD_SUBNAME1", rs2.getString("CD_SUBNAME"));
						rst.put("CD_EDITOR1", rs2.getString("CD_EDITOR"));
						rst.put("CD_DETNAME1", rs2.getString("CD_DETNAME"));
					       
						rst.put("detname13", "");
						rst.put("detname14", "");
					}
					
					rtList.add(rst);
					
					rst = null;
					
				}
			}
			
			rs.close();
			pstmt.close();

			rs2.close();
			pstmt2.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			
			conn = null;
			
			
			return rtList.toArray();			
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2600.getSystemList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr2600.getSystemList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2600.getSystemList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr2600.getSystemList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2600.getSystemList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public String request_system(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList, ArrayList<HashMap<String,String>> reqList1, ArrayList<HashMap<String,String>> reqList2, String flg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		String			  retMsg	  = null;
		String			  AcptNo	  = null;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo    = new UserInfo();
		Cmr0200 		  cmr0200 	  = new Cmr0200();
		int 			  Listsize    = 0;		
		int				  k			  = 0;
		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			AcptNo = autoseq.getSeqNo(conn,etcData.get("reqcd"));
        	String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("userid"),"cm_project");
        	//ecamsLogger.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@flg \n"
        	//		+flg+"/"+reqList1.get(0).get("acptno")+"\n"
		    //		+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        	
        	//임시저장 
			if(reqList1.get(0).get("cr_status").equals("3")
				|| reqList1.get(0).get("acptno") == null || reqList1.get(0).get("acptno") == ""){
				strQuery.setLength(0);
				strQuery.append(" INSERT INTO CMR1000  									\n");
				strQuery.append(" (CR_ACPTNO,CR_ACPTDATE,CR_STATUS,CR_SYSCD,CR_TEAMCD,  \n");
				strQuery.append(" CR_QRYCD,CR_PASSOK,CR_PASSCD,CR_NOAUTO,CR_PRCREQ,     \n");
				strQuery.append(" CR_EDDATE,CR_PRCDATE,CR_EDITOR,CR_GYULJAE,CR_BEFJOB,  \n");
				strQuery.append(" CR_SAYU,CR_SYSGB,CR_JOBCD)							\n");
				strQuery.append(" VALUES                                                \n");
				strQuery.append(" (?, SYSDATE, ?, ?, ?,                         		\n");
				strQuery.append(" ?, '0', ?, '', '',                                    \n");
				strQuery.append(" '', '', ?, '0', '0',                                  \n");
				strQuery.append(" '', ?, ?)                                    			\n");					
				
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount  = 1;
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
	        	
	        	
				Listsize = reqList1.size() + reqList2.size();
				
    			k = 0;
	        	for(int i=0; i<Listsize; i++){
	        		strQuery.setLength(0);
	        		strQuery.append(" INSERT INTO CMR2600								  \n");
	        		strQuery.append(" (CR_ACPTNO,CR_SERNO,CR_ACPTDATE,CR_TEAMCD,CR_JOBCD, \n");
	        		strQuery.append(" CR_EDITOR,CR_STATUS,CR_REQDAY,CR_SENDUSR,CR_RECVUSR,\n");
	        		strQuery.append(" CR_GBNCD,CR_DETCD,CR_RSTSTA,CR_JOBGBN)              \n");
	        		strQuery.append(" VALUES                                              \n");
	        		strQuery.append(" (                                                   \n");
	        		strQuery.append(" ?, ?, SYSDATE, ?, ?,                    		      \n");
	        		strQuery.append(" ?, ?, ?, ?, ?,                                      \n");
	        		strQuery.append(" ?, ?, ?, ?                                          \n");
	        		strQuery.append(" )                                                   \n");
	        		pstmt = conn.prepareStatement(strQuery.toString());
	        		//pstmt =  new LoggableStatement(conn, strQuery.toString());
	        		pstmtcount  = 1;
	        		pstmt.setString(pstmtcount++, AcptNo);
	        		pstmt.setString(pstmtcount++, String.format("%04d", i+1)); //i에 4자리 왼쪽부터 0으로 채우기
	        		pstmt.setString(pstmtcount++, strTeam);
	        		pstmt.setString(pstmtcount++, etcData.get("jobcd"));
	        		pstmt.setString(pstmtcount++, etcData.get("userid"));
	        		if(flg.equals("5")) pstmt.setString(pstmtcount++, "5"); //status = "5" 임시저장
	        		else pstmt.setString(pstmtcount++, "0"); //status = "0" 신청
	        		pstmt.setString(pstmtcount++, etcData.get("txtDate"));
	        		pstmt.setString(pstmtcount++, etcData.get("name1"));
	        		pstmt.setString(pstmtcount++, etcData.get("name2"));
	        		
	        		String strDetname1 = "";
	        		String strDetname2 = "";
	        		if(i < reqList1.size()){
	        			pstmt.setString(pstmtcount++, reqList1.get(i).get("CD_GBNCD"));
	        			pstmt.setString(pstmtcount++, reqList1.get(i).get("CD_DETCD"));
	        			for(int j=1; j<=12; j++){
	        				if (strDetname1 != null && strDetname1 != "") {
	        					strDetname1 = strDetname1+","+reqList1.get(i).get("detname"+j);
	        				}else{
	        					strDetname1 = reqList1.get(i).get("detname"+j);
	        				}
	        			}
	        			pstmt.setString(pstmtcount++, strDetname1);
	        		}else{
	        			pstmt.setString(pstmtcount++, reqList2.get(k).get("CD_GBNCD1"));
	        			pstmt.setString(pstmtcount++, reqList2.get(k).get("CD_DETCD1"));
	        			if(reqList2.get(k).get("detname13") == null || reqList2.get(k).get("detname13") == ""){
	        				strDetname2 = "";
	        			}else{
	        				strDetname2 = reqList2.get(k).get("detname13");
	        			}
        				if (strDetname2 != "") {
        					if(reqList2.get(k).get("detname14") == null || reqList2.get(k).get("detname14") == ""){
        						strDetname2 = strDetname2 + ", ";
        					}else{
        						strDetname2 = strDetname2 + "," + reqList2.get(k).get("detname14");
        					}
        				}else{
        					if(reqList2.get(k).get("detname14") == null || reqList2.get(k).get("detname14") == ""){
        						strDetname2 = "";
        					}else{
        						strDetname2 = "," + reqList2.get(k).get("detname14");
        					}
        				}
        				pstmt.setString(pstmtcount++, strDetname2);
	        			k = k + 1;
	        		}
	        		pstmt.setString(pstmtcount++, etcData.get("jobgbn")); //'0'주간 , '1'야간
	        		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        		pstmt.executeUpdate();
	        		pstmt.close();
	        	}
	        	
			}else{ //이미 임시저장한 후 임시저장 또는 신청 할 경우
				strQuery.setLength(0);
				strQuery.append(" UPDATE CMR1000  										\n");
				strQuery.append("    SET CR_STATUS = ?									\n");			
				strQuery.append("       ,CR_PASSCD = ?									\n");
				strQuery.append("  WHERE CR_ACPTNO = ?								  	\n");
				pstmt = conn.prepareStatement(strQuery.toString());
        		if(!flg.equals("5")){ //임시저장후 신청
        			pstmt.setString(1, "0");
        		}else{
        			pstmt.setString(1, "5");
        		}
        		pstmt.setString(2, etcData.get("sayu"));
        		pstmt.setString(3, reqList1.get(0).get("acptno"));
        		pstmt.executeUpdate();
        		pstmt.close();
				
	        	Listsize = reqList1.size() + reqList2.size();

    			k = 0;
				for(int i=0; i<Listsize; i++){
	        		strQuery.setLength(0);
	        		strQuery.append(" UPDATE CMR2600								  		\n");
	        		strQuery.append(" SET CR_REQDAY=?,CR_SENDUSR=?,CR_RECVUSR=?,CR_STATUS=?,\n");
	        		strQuery.append(" CR_GBNCD=?,CR_DETCD=?,CR_RSTSTA=?,CR_JOBGBN=?         \n");
	        		strQuery.append(" WHERE CR_ACPTNO = ?								  	\n");
	        		strQuery.append("   AND CR_SERNO = ?								  	\n");
	        		pstmt = conn.prepareStatement(strQuery.toString());
	        		//pstmt =  new LoggableStatement(conn, strQuery.toString());
	        		pstmtcount  = 1;
	        		pstmt.setString(pstmtcount++, etcData.get("txtDate"));
	        		pstmt.setString(pstmtcount++, etcData.get("name1"));
	        		pstmt.setString(pstmtcount++, etcData.get("name2"));
	        		if(flg.equals("5")) pstmt.setString(pstmtcount++, "5");
	        		else pstmt.setString(pstmtcount++, "0");
	        		
	        		String strDetname1 = "";
	        		String strDetname2 = "";
	        		if(i < reqList1.size()){
	        			pstmt.setString(pstmtcount++, reqList1.get(i).get("CD_GBNCD"));
	        			pstmt.setString(pstmtcount++, reqList1.get(i).get("CD_DETCD"));
	        			for(int j=1; j<=12; j++){
	        				if (strDetname1 != null && strDetname1 != "") {
	        					strDetname1 = strDetname1+","+reqList1.get(i).get("detname"+j);
	        				}else{
	        					strDetname1 = reqList1.get(i).get("detname"+j);
	        				}
	        			}
	        			pstmt.setString(pstmtcount++, strDetname1);
	        			pstmt.setString(pstmtcount++, etcData.get("jobgbn")); //'0'주간 , '1'야간
	        			pstmt.setString(pstmtcount++, reqList1.get(0).get("acptno"));
	        			pstmt.setString(pstmtcount++, reqList1.get(i).get("serno"));
	        		}else{
	        			pstmt.setString(pstmtcount++, reqList2.get(k).get("CD_GBNCD1"));
	        			pstmt.setString(pstmtcount++, reqList2.get(k).get("CD_DETCD1"));
	        			if(reqList2.get(k).get("detname13") == null || reqList2.get(k).get("detname13") == ""){
	        				strDetname2 = "";
	        			}else{
	        				strDetname2 = reqList2.get(k).get("detname13");
	        			}
        				if (strDetname2 != "") {
        					if(reqList2.get(k).get("detname14") == null || reqList2.get(k).get("detname14") == ""){
        						strDetname2 = strDetname2 + ", ";
        					}else{
        						strDetname2 = strDetname2 + "," + reqList2.get(k).get("detname14");
        					}
        				}else{
        					if(reqList2.get(k).get("detname14") == null || reqList2.get(k).get("detname14") == ""){
        						strDetname2 = "";
        					}else{
        						strDetname2 = "," + reqList2.get(k).get("detname14");
        					}
        				}
        				pstmt.setString(pstmtcount++, strDetname2);
        				pstmt.setString(pstmtcount++, etcData.get("jobgbn")); //'0'주간 , '1'야간
        				pstmt.setString(pstmtcount++, reqList1.get(0).get("acptno"));
        				pstmt.setString(pstmtcount++, reqList2.get(k).get("serno"));
	        			k = k + 1;
	        		}
	        		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        		pstmt.executeUpdate();
	        		pstmt.close();
	        	}
			}
			
			if(flg.equals("5")){
				conn.commit();
			}else{
				ecamsLogger.error(reqList1.get(0).get("acptno"));
				
				if(reqList1.get(0).get("acptno") == null || reqList1.get(0).get("acptno") == ""){
					retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("syscd"),etcData.get("reqcd"),etcData.get("userid"),true,ConfList,conn);
				}else{
					retMsg = cmr0200.request_Confirm(reqList1.get(0).get("acptno"),etcData.get("syscd"),etcData.get("reqcd"),etcData.get("userid"),true,ConfList,conn);
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
        	
        	rs = null;
        	pstmt = null;
        	
        	conn = null;
        	
        	if(reqList1.get(0).get("acptno") == null || reqList1.get(0).get("acptno") == ""){
        		return AcptNo;
        	}else{
        		return reqList1.get(0).get("acptno");
        	}
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2600.request_system() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2600.request_system() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr2600.request_system() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2600.request_system() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2600.request_system() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr2600.request_system() Exception END ##");				
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
					ecamsLogger.error("## Cmr2600.request_system() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
}