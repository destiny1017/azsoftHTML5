/*****************************************************************************************
	1. program ID	: Cmm0400.java
	2. create date	: 2008.12. 03
	3. auth		    : NO name
	4. update date	:
	5. auth		    :
	6. description	: [관리자] -> 사용자정보
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.HashMap;

import app.common.AutoSeq;
//import app.common.LoggableStatement;
//import app.common.UserInfo;


public class Cmr0500{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 사용자정보를 조회합니다.
	 * @param  UserId,UserName
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */

    public Object[] getJobInfo(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select cm_syscd,cm_jobcd        \n");
			strQuery.append("from cmm0044                     \n");
			strQuery.append("where cm_userid= ?                 \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,UserID);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0500.getJobInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0500.getJobInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0500.getJobInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0500.getJobInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0500.getJobInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getJobInfo() method statement
    public Object[] getJobInfo_All(String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}


			strQuery.setLength(0);
			strQuery.append("Select a.cm_jobcd,a.cm_jobname,b.cm_syscd, b.cm_sysmsg \n");
			strQuery.append("From cmm0030 b,cmm0102 a,cmm0034 c \n");
			strQuery.append("Where c.cm_closedt is null \n");
			strQuery.append("And c.cm_jobcd=a.cm_jobcd \n");
			strQuery.append("And a.cm_useyn = 'Y' \n");
			strQuery.append("And c.cm_syscd=b.cm_syscd \n");
			strQuery.append("and b.cm_closedt is null \n");
			strQuery.append("order by cm_syscd,cm_jobcd\n");
            pstmt = conn.prepareStatement(strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_jobcd", "0000");
				   rst.put("cm_jobname", strSelMsg);
				   rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", "");
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0500.getJobInfo_All() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0500.getJobInfo_All() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0500.getJobInfo_All() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0500.getJobInfo_All() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0500.getJobInfo_All() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getJobInfo_All() method statement

    public String request_Check_In(ArrayList<HashMap<String,String>> SinList,HashMap<String,String> etcData,ArrayList<HashMap<String,Object>> ConfList,String confFg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		String			  AcptNo	  = null;
		int				  i=0;

		try {
			conn = connectionContext.getConnection();

	        String strRequest = "";
	        String ReqTeam = "";
	        strQuery.setLength(0);
	        strQuery.append("select cm_codename from cmm0020       \n");
	        strQuery.append(" where cm_macode='REQUEST'            \n");
	        strQuery.append("   and cm_micode=?                    \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, etcData.get("ReqCD"));
	        rs = pstmt.executeQuery();
	        if (rs.next()) strRequest = rs.getString("cm_codename");
	        rs.close();
	        pstmt.close();

	        Cmr0200 cmr0200 = new Cmr0200();

    		autoseq = new AutoSeq();
    		AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));
    		strQuery.setLength(0);
	        strQuery.append("select cm_project from cmm0040       \n");
	        strQuery.append(" where cm_userid=?                    \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, etcData.get("UserID"));
	        rs = pstmt.executeQuery();
	        if (rs.next()) ReqTeam = rs.getString("cm_project");
	        rs.close();
	        pstmt.close();

    		strQuery.setLength(0);
	        strQuery.append("select cm_codename from cmm0020       \n");
	        strQuery.append(" where cm_macode='REQUEST'            \n");
	        strQuery.append("   and cm_micode=?                    \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, etcData.get("ReqCD"));
	        rs = pstmt.executeQuery();
	        if (rs.next()) strRequest = rs.getString("cm_codename");
	        rs.close();
	        pstmt.close();

	        conn.setAutoCommit(false);
    		strQuery.setLength(0);
    		strQuery.append("insert into cmr1000 \n");
    		strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD,CR_PASSOK,\n");
    		strQuery.append("CR_PASSCD,cr_EDITOR,cr_BEFJOB,CR_SAYU) values (\n");
    		strQuery.append("?, '99999', '1', SYSDATE, '0', ?, ?, '0', ?, ?, '0', ?) \n");
    		pstmtcount = 1;
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, ReqTeam);
        	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
        	pstmt.setString(pstmtcount++, strRequest);
        	pstmt.setString(pstmtcount++, etcData.get("UserID"));
        	pstmt.setString(pstmtcount++, etcData.get("Sayu"));
        	pstmt.executeUpdate();
        	pstmt.close();
        	rs.close();

        	strQuery.setLength(0);
			strQuery.append("insert into cmr1520 (CR_ACPTNO, CR_EDITOR, CR_STATUS, CR_LIMITDT, CR_USERID, CR_NAME, CR_PROJECT, CR_POSITION, CR_DUTY, CR_qrycd) VALUES ( \n");
			strQuery.append(" ?, ?, '0', ?, ?, ?, ?, ?, ?, ?) \n");
			pstmtcount = 1;
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(pstmtcount++, AcptNo);
			pstmt.setString(pstmtcount++, etcData.get("UserID"));
			pstmt.setString(pstmtcount++, etcData.get("term"));
			pstmt.setString(pstmtcount++, etcData.get("Txtid"));
			pstmt.setString(pstmtcount++, etcData.get("TxtName"));
			pstmt.setString(pstmtcount++, etcData.get("cboteam"));
			pstmt.setString(pstmtcount++, etcData.get("cbojpos"));
			pstmt.setString(pstmtcount++, etcData.get("cboduty"));
			pstmt.setString(pstmtcount++, etcData.get("cboreq"));
			pstmt.executeUpdate();
			pstmt.close();

			for (i=0;i<SinList.size();i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmr1521 (CR_ACPTNO,CR_SEQNO,CR_JOBSEC,CR_SYSCD) values( \n");
				strQuery.append("?, ?, ?, ?) \n");
				pstmtcount = 1;
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(pstmtcount++, AcptNo);
				pstmt.setInt(pstmtcount++, i);
				pstmt.setString(pstmtcount++, SinList.get(i).get("cm_jobcd"));
				pstmt.setString(pstmtcount++, SinList.get(i).get("cm_syscd"));
				pstmt.executeUpdate();
				pstmt.close();
			}

        	String retMsg = cmr0200.request_Confirm(AcptNo,"99999",etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
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
        	conn = null;

        	//ecamsLogger.error("+++++++++Request E N D+++");
        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0500.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0500.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0500.request_Check_In() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0500.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0500.request_Check_In() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0500.request_Check_In() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0500.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


}