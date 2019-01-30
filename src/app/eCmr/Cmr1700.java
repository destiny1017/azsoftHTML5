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

public class Cmr1700 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public ArrayList<HashMap<String, String>> getErrReqList(String dat) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt1       = null;
		PreparedStatement pstmt2       = null;
		PreparedStatement pstmt3       = null;
		ResultSet         rs          = null;
		ResultSet         rs1          = null;
		ResultSet         rs2          = null;
		ResultSet         rs3          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("Select a.*,b.cd_rcontents rcontents,b.cd_delay delay \n");
			strQuery.append("  from cmd0061 b,cmd0060 a                           \n");
			strQuery.append(" where a.cd_acptdate<= ?                             \n");
			strQuery.append("   and a.cd_state= '0'                               \n");
			strQuery.append("   and a.cd_comgb=b.cd_comgb                         \n");
			strQuery.append("   and a.cd_acptdate=b.cd_acptdate                   \n");
			strQuery.append("   and a.cd_seqno=b.cd_seqno                         \n");
			strQuery.append(" order by a.cd_acptdate,a.cd_seqno,b.cd_subseq       \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, dat);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();

			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("CD_SEQNO", rs.getString("CD_SEQNO"));
				rst.put("CD_ACPTTIME", rs.getString("CD_ACPTTIME"));
				rst.put("CD_PRCTIME", rs.getString("CD_PRCTIME"));
				rst.put("CD_JIJUMNA", rs.getString("CD_JIJUMNA"));
				rst.put("CD_CHAUSER", rs.getString("CD_CHAUSER"));
				rst.put("CD_TELNO", rs.getString("CD_TELNO"));
				rst.put("CD_RECUSER", rs.getString("CD_RECUSER"));
				rst.put("CD_ITEM", rs.getString("CD_ITEM"));
				rst.put("CD_JCONTENTS", rs.getString("CD_JCONTENTS"));
				rst.put("CD_RCONTENTS", rs.getString("CD_RCONTENTS"));
				rst.put("CD_DELAY", rs.getString("CD_DELAY"));
				rst.put("CD_PRCUSER", rs.getString("CD_PRCUSER"));
				rst.put("CD_PRCRST", rs.getString("CD_PRCRST"));
				rst.put("CD_TIME", rs.getString("CD_TIME"));
				rst.put("CD_STATE", rs.getString("CD_STATE"));
				rst.put("CD_EDITOR", rs.getString("CD_EDITOR"));
				rst.put("CD_INDATE", rs.getString("CD_INDATE"));
				rst.put("RCONTENTS", rs.getString("RCONTENTS"));
				rst.put("DELAY", rs.getString("DELAY"));
				rst.put("CD_ACPTDATE", rs.getString("CD_ACPTDATE"));
				rst.put("CD_PRCDATE", rs.getString("CD_PRCDATE"));
				rst.put("ACPTDATE", rs.getString("CD_ACPTDATE").substring(4, 5)+"/"+rs.getString("CD_ACPTDATE").substring(6, 7));
				rst.put("PRCDATE", rs.getString("CD_PRCDATE").substring(4, 5)+"/"+rs.getString("CD_PRCDATE").substring(6, 7));

				if(rs.getString("CD_COMGB").equals("0")){ // 장애구분
					rst.put("errgbn", "지점");
				}else{
					rst.put("errgbn", "본사");
				}

				if(!rs.getString("CD_GUBUN").equals("") || rs.getString("CD_GUBUN")!=null){ //H/W구분
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020  				\n");
					strQuery.append(" where cm_macode = 'HWCD'                      \n");
					strQuery.append("   and cm_micode = ?                 			\n");
					pstmt1 = conn.prepareStatement(strQuery.toString());
					//pstmt1 =  new LoggableStatement(conn, strQuery.toString());
					pstmt1.setString(1, rs.getString("CD_GUBUN"));
					//ecamsLogger.error(((LoggableStatement)pstmt1).getQueryString());
		            rs1 = pstmt1.executeQuery();
					if(rs1.next()){
						rst.put("hwgbn", rs1.getString("cm_codename"));
					}
				}

				if(!rs.getString("CD_SWGBN").equals("") || rs.getString("CD_SWGBN")!=null){ //S/W구분
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020  				\n");
					strQuery.append(" where cm_macode = 'SWCD'                      \n");
					strQuery.append("   and cm_micode = ?                 			\n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 =  new LoggableStatement(conn, strQuery.toString());
					pstmt2.setString(1, rs.getString("CD_SWGBN"));
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
					if(rs2.next()){
						rst.put("swgbn", rs2.getString("cm_codename"));
					}
				}

				if(rs.getString("CD_STATUS").equals("0")){ //처리여부
					rst.put("status", "Y");
				}else{
					rst.put("status", "N");
				}

				if(!rs.getString("CD_SUCCESS").equals("") || rs.getString("CD_SUCCESS")!=null){ //만족도
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020  				\n");
					strQuery.append(" where cm_macode = 'SCGB'                      \n");
					strQuery.append("   and cm_micode = ?                 			\n");
					pstmt3 = conn.prepareStatement(strQuery.toString());
					//pstmt3 =  new LoggableStatement(conn, strQuery.toString());
					pstmt3.setString(1, rs.getString("CD_SUCCESS"));
					//ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
		            rs3 = pstmt3.executeQuery();
					if(rs3.next()){
						rst.put("success", rs3.getString("cm_codename"));
					}
				}

				rtList.add(rst);

				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			rs1.close();
			pstmt1.close();
			rs2.close();
			pstmt2.close();
			rs3.close();
			pstmt3.close();

			conn.close();

			rs = null;
			pstmt = null;
			rs1 = null;
			pstmt1 = null;
			rs2 = null;
			pstmt2 = null;
			rs3 = null;
			pstmt3 = null;

			conn = null;

			return rtList;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr1000.getPre_SysList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1000.getPre_SysList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1000.getPre_SysList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1000.getPre_SysList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr1000.getPre_SysList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String request_ErrRefuse(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>>ConfList, ArrayList<HashMap<String,String>> reqList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt1      = null;
		PreparedStatement pstmt2      = null;
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
			strQuery.append("INSERT INTO CMR1000 							\n");
			strQuery.append("(CR_ACPTNO,CR_ACPTDATE  						\n");
			strQuery.append(",CR_STATUS,CR_SYSCD,CR_TEAMCD					\n");
			strQuery.append(",CR_QRYCD,CR_PASSOK,CR_PASSCD					\n");
			strQuery.append(",CR_NOAUTO,CR_PRCREQ,CR_EDDATE,CR_PRCDATE		\n");
			strQuery.append(",CR_EDITOR,CR_GYULJAE,CR_BEFJOB,CR_SAYU		\n");
			strQuery.append(",CR_SYSGB,CR_JOBCD)							\n");
			strQuery.append("VALUES											\n");
			strQuery.append("(?, SYSDATE 									\n");
			strQuery.append(",'0', ? , ?									\n");
			strQuery.append(", ?, '0', ?									\n");
			strQuery.append(", '', '', '', '' 								\n");
			strQuery.append(", ?, 0, 0, '' 									\n");
			strQuery.append(", ?, ?)										\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
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
	        	strQuery.append("select cr_acptno from cmr2100  \n");
	        	strQuery.append("where cr_acptno = ?            \n");
	        	strQuery.append("and cr_serno = ?               \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, String.format("%04d", i+1));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

	        	if(rs.next()){
	        		conn.rollback();
					conn.close();
					throw new Exception("동일한 내용의 DATA가 있습니다.");
	        	}else{
	        		strQuery.setLength(0);
	        		strQuery.append("insert into cmr2100 (cr_acptno,cr_serno,cr_acptdate,cr_teamcd,cr_jobcd,cr_editor,	\n");
	        		strQuery.append("cr_status,cr_runday,cr_runseq,cr_runacpt)                     						\n");
	        		strQuery.append("values(?, ?, SYSDATE, ?, ?, ?,                                 					\n");
	        		strQuery.append("'0', ?, ?, ?)                                                      				\n");
	        		pstmt1 = conn.prepareStatement(strQuery.toString());
	        		pstmt1.setString(pstmtcount1++, AcptNo);
	        		pstmt1.setString(pstmtcount1++, String.format("%04d", i+1));
	        		pstmt1.setString(pstmtcount1++, strTeam);
	        		pstmt1.setString(pstmtcount1++, etcData.get("jobcd"));
	        		pstmt1.setString(pstmtcount1++, etcData.get("userid"));
	        		pstmt1.setString(pstmtcount1++, etcData.get("txtDate")); //기준일
	        		pstmt1.setString(pstmtcount1++, reqList.get(i).get("CD_SEQNO")); //일련번호
	        		pstmt1.setString(pstmtcount1++, reqList.get(i).get("CD_ACPTDATE")); //접수일
	        		pstmt1.executeUpdate();
	        		pstmt1.close();
	        	}

	        	//================================
	            // 원본 TAble State 변경 신청시 : 3
	            //================================
	        	strQuery.setLength(0);
	        	strQuery.append("Update cmd0060				\n");
	        	strQuery.append("   Set cd_state = '3'		\n");
	        	strQuery.append(" where cd_acptdate = ?		\n");
	        	strQuery.append("   and cd_seqno = ?		\n");
	        	pstmt2 = conn.prepareStatement(strQuery.toString());
	        	pstmt2.setString(1, reqList.get(i).get("CD_ACPTDATE")); //접수일
	        	pstmt2.setString(2, reqList.get(i).get("CD_SEQNO")); //일련번호
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

        	rs.close();
        	pstmt.close();

        	conn.close();

        	rs = null;
        	pstmt = null;
        	pstmt1 = null;
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
			if (rtObj != null)  rtObj = null;
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