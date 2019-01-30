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

public class Cmr4100 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	public ArrayList<HashMap<String, String>> getDocList(String stdate, String eddate, String cbocd, String txt) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("SELECT  ReqDept, ReqMn, DocNo, Tit, trim(ReqDt) ReqDt, DdagMn,   	\n");
			strQuery.append("        DocGb, PrcGb, TSTRSLT, trim(ENDDT) ENDDT, '111'            \n");
			strQuery.append("  FROM  kpComPrcReqDoc                                 \n");
			strQuery.append(" WHERE  ReqDt BETWEEN ? AND ?                          \n");
			if(txt != "" && txt != null){
				if(cbocd.equals("ReqDept")){
					strQuery.append(" and  upper(ReqDept) like upper(?)            	\n");
				}else if(cbocd.equals("ReqMn")){
					strQuery.append(" and  upper(ReqMn) like  upper(?)             	\n");
				}else if(cbocd.equals("DdagMn")){
					strQuery.append(" and  upper(DdagMn) like  upper(?)            	\n");
				}else{
					strQuery.append(" and  Tit like  upper(?)                  		\n");
				}
			}
			strQuery.append("ORDER BY ReqDt DESC                         			\n");
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, stdate);
			pstmt.setString(2, eddate);

			if(txt != "" && txt != null){
				pstmt.setString(3, "%"+txt+"%");
			}

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("reqdept", rs.getString("ReqDept"));
				rst.put("reqmn", rs.getString("ReqMn"));
				rst.put("docno", rs.getString("DocNo"));
				rst.put("tit", rs.getString("Tit"));
				rst.put("reqdt", rs.getString("ReqDt").substring(0,4)+"/"+rs.getString("ReqDt").substring(4,6)+"/"+rs.getString("ReqDt").substring(6,8));
				rst.put("r_reqdt", rs.getString("ReqDt"));
				rst.put("ddagmn", rs.getString("DdagMn"));
				rst.put("docgb", rs.getString("DocGb"));
				rst.put("prcgb", rs.getString("PrcGb"));
				rst.put("tstrslt", rs.getString("TSTRSLT"));
				if(rs.getString("ENDDT") != null && rs.getString("ENDDT") != ""){
					rst.put("enddt", rs.getString("ENDDT").substring(0,4)+"/"+rs.getString("ENDDT").substring(4,6)+"/"+rs.getString("ENDDT").substring(6,8));
					rst.put("r_enddt", rs.getString("ENDDT"));
				}else{
					rst.put("enddt", "");
					rst.put("r_enddt", "");
				}
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr4100.getDocList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr4100.getDocList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr4100.getDocList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr4100.getDocList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null) 	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr4100.getDocList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String request_develop(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList, ArrayList<HashMap<String,String>> reqList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
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
			strQuery.append("INSERT INTO CMR1000 									\n");
			strQuery.append("(CR_ACPTNO,CR_ACPTDATE,CR_STATUS,CR_SYSCD,CR_TEAMCD,	\n");
			strQuery.append("CR_QRYCD,CR_PASSOK,CR_PASSCD,CR_NOAUTO,CR_PRCREQ,		\n");
			strQuery.append("CR_EDDATE,CR_PRCDATE,CR_EDITOR,CR_GYULJAE,CR_BEFJOB,	\n");
			strQuery.append("CR_SAYU,CR_RELDOC,CR_SYSGB,RM_BASENO,CR_SAYUCD,		\n");
			strQuery.append("CR_DEPT,CR_REQDATE,CR_REQDOC,CR_ETC)					\n");
			strQuery.append("VALUES (												\n");
			strQuery.append("?, SYSDATE, '0', ?, ?,									\n");
			strQuery.append("?, '0', ?, '', '',										\n");
			strQuery.append("'', '', ?, '0', '0',									\n");
			strQuery.append("?, '', ?, ?, '', 										\n");
			strQuery.append("'', '', '', '')										\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("syscd"));
        	pstmt.setString(pstmtcount++, strTeam);
        	pstmt.setString(pstmtcount++, etcData.get("reqcd"));
        	pstmt.setString(pstmtcount++, etcData.get("sayu"));
        	pstmt.setString(pstmtcount++, etcData.get("userid"));
        	pstmt.setString(pstmtcount++, etcData.get("rsrc"));
        	pstmt.setString(pstmtcount++, etcData.get("sysgb"));
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.executeUpdate();
        	pstmt.close();


        	for(int i=0; i<reqList.size(); i++){
        		strQuery.setLength(0);
        		strQuery.append(" INSERT INTO CMR4100 			\n");
        		strQuery.append(" ( CR_ACPTNO        			\n");
        		strQuery.append(" , CR_SERNO               		\n");
        		strQuery.append(" , CR_ACPTDATE       			\n");
        		strQuery.append(" , CR_TEAMCD         			\n");
        		strQuery.append(" , CR_JOBCD          			\n");
        		strQuery.append(" , CR_EDITOR         			\n");
        		strQuery.append(" , CR_PRCDATE              	\n");
        		strQuery.append(" , CR_PRCRST         			\n");
        		strQuery.append(" , CR_STATUS               	\n");
        		strQuery.append(" , CR_DOCNO          			\n");
        		strQuery.append(" , CR_DOCTITLE             	\n");
        		strQuery.append(" , CR_REQDATE        			\n");
        		strQuery.append(" )                         	\n");
        		strQuery.append(" VALUES                    	\n");
        		strQuery.append(" ( ?         					\n");
        		strQuery.append(" , ? 							\n");
        		strQuery.append(" , SYSDATE                 	\n");
        		strQuery.append(" , ?       					\n");
        		strQuery.append(" , ''                			\n");
        		strQuery.append(" , ?       					\n");
        		strQuery.append(" , ''                      	\n");
        		strQuery.append(" , ''                      	\n");
        		strQuery.append(" , '0'                     	\n");
        		strQuery.append(" , ?                      	 	\n");
        		strQuery.append(" , ?                       	\n");
        		strQuery.append(" , ?                       	\n");
        		strQuery.append("  )                        	\n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(pstmtcount++, AcptNo);
        		pstmt2.setString(pstmtcount++, String.format("%04d", i+1)); //i에 4자리 왼쪽부터 0으로 채우기
        		pstmt2.setString(pstmtcount++, strTeam);
        		pstmt2.setString(pstmtcount++, etcData.get("userid"));
        		pstmt2.setString(pstmtcount++, reqList.get(i).get("docno"));
        		pstmt2.setString(pstmtcount++, reqList.get(i).get("doctitle"));
        		pstmt2.setString(pstmtcount++, reqList.get(i).get("prcdate"));

            	pstmt2.executeUpdate();
            	pstmt2.close();
        	}

        	retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("syscd"),etcData.get("reqcd"),etcData.get("userid"),true,ConfList,conn);

        	if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			} else {
				conn.commit();
			}

        	conn.close();
        	rs = null;
        	pstmt = null;
        	pstmt2 = null;
        	conn = null;

        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr1000.request_PreSystem() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1000.request_PreSystem() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1000.request_PreSystem() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1000.request_PreSystem() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
//			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr1000.request_PreSystem() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}