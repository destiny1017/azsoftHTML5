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


public class Cmr0508{

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

    public Object[] getSysInfo(String UserId) throws SQLException, Exception {
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
			strQuery.append("select DISTINCT a.cm_syscd,a.cm_sysmsg from cmm0030 a,cmm0044 b \n");
			strQuery.append("where b.cm_userid= ? \n");
			strQuery.append("  and b.cm_closedt is null \n");
			strQuery.append("  and b.cm_syscd=a.cm_syscd \n");
			strQuery.append("  and a.cm_closedt is null \n");
			strQuery.append("order by a.cm_sysmsg \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            pstmt.setString(1,UserId);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
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
			ecamsLogger.error("## SysInfo.getJobCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getJobCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getJobCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getJobCd() Exception END ##");
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
					ecamsLogger.error("## SysInfo.getJobCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

    public Object[] getJobInfo(String SelMsg) throws SQLException, Exception {
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
			strQuery.append("select cm_jobcd,cm_jobname from cmm0102 \n");
			strQuery.append("where cm_useyn='Y' \n");
			strQuery.append("order by cm_jobcd \n");
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
			ecamsLogger.error("## SysInfo.getJobCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getJobCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getJobCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getJobCd() Exception END ##");
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
					ecamsLogger.error("## SysInfo.getJobCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

    public String request_Check_In(ArrayList<HashMap<String,String>> chkInList,ArrayList<HashMap<String,String>> SinList,ArrayList<HashMap<String,String>> SinList2,ArrayList<HashMap<String,String>> SinList3,HashMap<String,String> etcData,ArrayList<HashMap<String,String>> befJob,ArrayList<HashMap<String,Object>> ConfList,String confFg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		//UserInfo		  userInfo	  = new UserInfo();
		String			  AcptNo	  = null;
		int				  i=0;

		try {

			conn = connectionContext.getConnection();

	        //String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
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

	        //Cmr0200 cmr0200 = new Cmr0200();
	        //ArrayList<HashMap<String,Object>> conflist = null;

	        int wkC = chkInList.size()/300;
	        int wkD = chkInList.size()%300;
	        if (wkD>0) wkC = wkC + 1;
            String svAcpt[] = null;
            svAcpt = new String [wkC];
            for (int j=0;wkC>j;j++) {
            	do {
    		        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));

    		        i = 0;
    		        strQuery.setLength(0);
    		        strQuery.append("select count(*) as cnt from cmr1000 \n");
    	        	strQuery.append(" where cr_acptno= ?                 \n");

    	        	pstmt = conn.prepareStatement(strQuery.toString());
    	        	pstmt.setString(1, AcptNo);

    	        	rs = pstmt.executeQuery();

    	        	if (rs.next()){
    	        		i = rs.getInt("cnt");
    	        	}
    	        	rs.close();
    	        	pstmt.close();
    	        } while(i>0);
            	svAcpt[j] = AcptNo;
            }
        	String retMsg = "";
            autoseq = null;
            conn.setAutoCommit(false);

        	if (etcData.get("ReqCD").equals("75")){
        		autoseq = new AutoSeq();
        		if(AcptNo == null || AcptNo=="" || AcptNo.equals("null") ){
        			AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));
        		}
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

        		strQuery.setLength(0);
        		strQuery.append("insert into cmr1000 \n");
        		strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD,CR_PASSOK,\n");
        		strQuery.append("CR_PASSCD,cr_EDITOR,cr_BEFJOB,CR_SAYU) values (\n");
        		strQuery.append("?, '99999', '1', SYSDATE, '0', ?, ?, ?, ?, ?, '0', ?) \n");

        		pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, ReqTeam);
            	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
            	pstmt.setString(pstmtcount++, etcData.get("Deploy"));
            	pstmt.setString(pstmtcount++, strRequest);
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, etcData.get("Sayu"));
            	pstmt.executeUpdate();
            	pstmt.close();
            	rs.close();


        		strQuery.setLength(0);
            	strQuery.append("select count(*) as cnt from cmr1560 where cr_acptno= ? ");
            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	while (rs.next()) {
            		if (rs.getInt("cnt") > 0) {
        				throw new Exception("동일한 신청번호로 기 신청내역이 있습니다. 확인 후 다시 처리하십시오.");
            		}
            	}
            	pstmt.close();
            	rs.close();

            	strQuery.setLength(0);
    			strQuery.append("insert into cmr1560 (CR_ACPTNO,CR_EDITOR,CR_STATUS,CR_JOBGBN,CR_SYSCD,CR_SYSMSG) VALUES ( \n");
    			strQuery.append(" ?, ?, '0', ?, ?, ?) \n");
    			pstmtcount = 1;
    			pstmt2 = conn.prepareStatement(strQuery.toString());
    			pstmt2.setString(pstmtcount++, AcptNo);
    			pstmt2.setString(pstmtcount++, etcData.get("UserID"));
    			pstmt2.setString(pstmtcount++, etcData.get("cboreq"));
    			pstmt2.setString(pstmtcount++, etcData.get("SysCode"));
    			pstmt2.setString(pstmtcount++, etcData.get("SysName"));
    			pstmt2.executeUpdate();

    			pstmt2.close();

    			 for (i=0;i<SinList.size();i++){
					 strQuery.setLength(0);
					 strQuery.append("insert into cmr1561 (CR_ACPTNO,CR_SEQNO,CR_JOBCD,CR_JOBNAME) values ( \n");
					 strQuery.append("?, ?, ?, ?) \n");
					 pstmtcount = 1;
	    			 pstmt2 = conn.prepareStatement(strQuery.toString());
	    			 pstmt2.setString(pstmtcount++, AcptNo);
	    			 pstmt2.setInt(pstmtcount++, i);
	    			 if(SinList.get(i).get("cm_jobcd").equals("추가")){
	    				 pstmt2.setString(pstmtcount++, "ADD");
	    			 }else{
	    				 pstmt2.setString(pstmtcount++, SinList.get(i).get("cm_jobcd"));
	    			 }
	    			 pstmt2.setString(pstmtcount++, SinList.get(i).get("cm_jobname"));
	    			 pstmt2.executeUpdate();

    			 }
    			 pstmt2.close();

    			 for (i=0;i<SinList2.size();i++){
					 strQuery.setLength(0);
					 strQuery.append("insert into cmr1562 (CR_ACPTNO,CR_SEQNO,CR_SVRCD,CR_HOSTNAME,CR_SVRIP,CR_SVRUSR,CR_SVRPWD) values ( \n");
					 strQuery.append("?, ?, ?, ?, ?, ?, ?) \n");

					 pstmtcount = 1;
	    			 pstmt2 = conn.prepareStatement(strQuery.toString());
	    			 pstmt2.setString(pstmtcount++, AcptNo);
	    			 pstmt2.setInt(pstmtcount++, i);
	    			 pstmt2.setString(pstmtcount++, SinList2.get(i).get("SeverCd"));
	    			 pstmt2.setString(pstmtcount++, SinList2.get(i).get("HostN"));
	    			 pstmt2.setString(pstmtcount++, SinList2.get(i).get("IPA"));
	    			 pstmt2.setString(pstmtcount++, SinList2.get(i).get("Acc"));
	    			 pstmt2.setString(pstmtcount++, SinList2.get(i).get("Pwd"));
	    			 pstmt2.executeUpdate();

	 			 }
	 			 pstmt2.close();

	 			 for (i=0;i<SinList3.size();i++){
					 strQuery.setLength(0);
					 strQuery.append("insert into cmr1563 (CR_ACPTNO,CR_SEQNO,CR_RSRCCD,CR_VOLPATH) values ( \n");
					 strQuery.append("?, ?, ?, ?) \n");

					 pstmtcount = 1;
	    			 pstmt2 = conn.prepareStatement(strQuery.toString());
	    			 pstmt2.setString(pstmtcount++, AcptNo);
	    			 pstmt2.setInt(pstmtcount++, i);
	    			 pstmt2.setString(pstmtcount++, SinList3.get(i).get("ProgCd"));
	    			 pstmt2.setString(pstmtcount++, SinList3.get(i).get("MainDir"));
	    			 pstmt2.executeUpdate();

	 			 }
	 			 pstmt2.close();
        	}

        	 //ecamsLogger.error("+++++++++CHECK-IN LIST Insert START (cmr9900)+++");
        	retMsg = request_Confirm(AcptNo,"99999",etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
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
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.request_Check_In() SQLException END ##");
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
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.request_Check_In() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.request_Check_In() Exception END ##");
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
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String request_Confirm(String AcptNo,String SysCd,String ReqCd,String UserId,boolean confSw,ArrayList<HashMap<String,Object>> ConfList,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               i = 0;
		int               pstmtcount = 0;
		int               SeqNo = 0;
		ArrayList<HashMap<String,Object>>	rData2 = null;
		try {
        	if (confSw == true) {
        		for (i=0;i<ConfList.size();i++){
    	        	if (ConfList.get(i).get("cm_congbn").equals("1") || ConfList.get(i).get("cm_congbn").equals("2") ||
    	        		ConfList.get(i).get("cm_congbn").equals("3") ||	ConfList.get(i).get("cm_congbn").equals("4") ||
    	        		ConfList.get(i).get("cm_congbn").equals("5") || ConfList.get(i).get("cm_congbn").equals("6")) {
    		        	strQuery.setLength(0);

    		        	strQuery.append("insert into cmr9900                                               \n");
    		        	strQuery.append("      (CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD, \n");
    		        	strQuery.append("       CR_STATUS,CR_CONGBN,CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,  \n");
    		        	strQuery.append("       CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR,CR_PRCSW)                  \n");
    		        	strQuery.append("values (                                                          \n");
    		        	strQuery.append("?, 1, lpad(?,2,'0'), ?, ?, ?, '0', ?, ?, ?, ?, ?, ?, ?, ?, ? )             \n");

    		        	pstmt = conn.prepareStatement(strQuery.toString());
    		        //	pstmt = new LoggableStatement(conn,strQuery.toString());

    		        	pstmtcount = 0;

    	        	    pstmt.setString(++pstmtcount, AcptNo);
    	        	    pstmt.setInt(++pstmtcount, ++SeqNo);
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_name"));
    	        	    rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(i).get("arysv");
    					pstmt.setString(++pstmtcount, (String) rData2.get(0).get("SvUser"));
    					rData2 = null;

    	        	    if (ConfList.get(i).get("cm_gubun").equals("C")){
    	        	    	pstmt.setString(++pstmtcount,"3");
    	        	    }
    	        	    else if (ConfList.get(i).get("cm_gubun").equals("R")){
    	        	    	pstmt.setString(++pstmtcount,"8");
    	        	    }
    	        	    else{
    	        	    	pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_gubun"));
    	        	    }
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_congbn"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_common"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_blank"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_emg"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_holi"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_duty"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_orgstep"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_baseuser"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_prcsw"));

    	        	//    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
    		        	pstmt.executeUpdate();
    		        	pstmt.close();
    		        }
            	}
        	} else {
	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr9900 ");
	        	strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD,CR_STATUS,CR_CONGBN, ");
	        	strQuery.append("CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR, CR_PRCSW) ");
	        	strQuery.append("(SELECT ?,1,lpad(a.CM_seqno,2,'0'),a.CM_NAME,a.CM_JOBCD,a.CM_GUBUN, ");
	        	strQuery.append("'0',a.CM_COMMON,a.CM_COMMON,a.CM_BLANK,a.CM_EMG,a.CM_HOLIDAY,");
	        	strQuery.append("a.CM_POSITION,a.CM_ORGSTEP,a.CM_JOBCD,a.CM_PRCSW ");
	        	strQuery.append("FROM CMm0060 a,cmm0040 b ");
	        	strQuery.append("WHERE a.CM_SYSCD= ? ");
	        	strQuery.append("AND a.CM_REQCD= ? and b.cm_userid=?  ");
	        	strQuery.append("AND a.CM_MANID=decode(b.cm_manid,'N','2','1') ");
	        	if (!ReqCd.equals("16"))
	        		strQuery.append("AND CM_GUBUN='1') ");
	        	else strQuery.append(") ");

	        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount = 1;
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	pstmt.setString(pstmtcount++, SysCd);
	        	pstmt.setString(pstmtcount++, ReqCd);
	        	pstmt.setString(pstmtcount++, UserId);
	        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();


	        	strQuery.setLength(0);
	        	strQuery.append("update cmr9900 set cr_team=cr_sgngbn             \n");
	        	strQuery.append(" where cr_acptno=? and cr_teamcd='4'              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount = 1;
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();


	        	strQuery.setLength(0);
	        	strQuery.append("update cmr9900 set cr_team=?,cr_baseusr=?         \n");
	        	strQuery.append(" where cr_acptno=? and cr_teamcd='2'              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount = 1;
	        	pstmt.setString(pstmtcount++, UserId);
	        	pstmt.setString(pstmtcount++, UserId);
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();
        	}

        	strQuery.setLength(0);
        	strQuery.append("insert into cmr9900 ");
        	strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_STATUS,CR_CONFUSR) ");
        	strQuery.append("values ( ");
        	strQuery.append("?, '1', '00', '0', '9999' ) ");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);

        	pstmt.executeUpdate();
        	pstmt.close();


        	boolean findSw = false;
        	if (ReqCd.equals("16")) {
            	findSw = false;
        		strQuery.setLength(0);
        		strQuery.append("select sum(cnt) cnt from                       \n");
        		strQuery.append("(select count(*) cnt from cmr1010 a,cmm0036 b  \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                 \n");
        		strQuery.append("   and (substr(b.cm_info,1,1)='1' or           \n");
        		strQuery.append("       substr(b.cm_info,25,1)='1')             \n");
        		strQuery.append(" union                                         \n");
        		strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b,cmm0037 c \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=c.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                 \n");
        		strQuery.append("   and c.cm_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and c.cm_samersrc=b.cm_rsrccd               \n");
        		strQuery.append("   and (substr(b.cm_info,1,1)='1' or           \n");
        		strQuery.append("       substr(b.cm_info,25,1)='1'))            \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	if (rs.next()) {
            		if (rs.getString("cnt") == null) findSw = true;
            		else if (rs.getInt("cnt") == 0) findSw = true;
            	}
            	rs.close();
            	pstmt.close();

            	if (findSw == true) {
	            	strQuery.setLength(0);
	            	strQuery.append("delete cmr9900 where cr_acptno=? and cr_teamcd='1' and cr_team='SYSCB' ");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmt.setString(1, AcptNo);
	            	pstmt.executeUpdate();
	            	pstmt.close();
            	}


            	findSw = false;
        		strQuery.setLength(0);
        		strQuery.append("select sum(cnt) cnt from                       \n");
        		strQuery.append("(select count(*) cnt from cmr1010 a,cmm0036 b  \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                 \n");
        		strQuery.append("   and (substr(b.cm_info,11,1)='1' or          \n");
        		strQuery.append("       substr(b.cm_info,21,1)='1')             \n");
        		strQuery.append(" union                                         \n");
        		strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b,cmm0037 c \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=c.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                 \n");
        		strQuery.append("   and c.cm_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and c.cm_samersrc=b.cm_rsrccd               \n");
        		strQuery.append("   and (substr(b.cm_info,11,1)='1' or          \n");
        		strQuery.append("       substr(b.cm_info,21,1)='1'))            \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	if (rs.next()) {
            		if (rs.getString("cnt") == null) findSw = true;
            		else if (rs.getInt("cnt") == 0) findSw = true;
            	}
            	rs.close();
            	pstmt.close();

            	if (findSw == true) {
	            	strQuery.setLength(0);
	            	strQuery.append("delete cmr9900 where cr_acptno=? and cr_teamcd='1' and cr_team='SYSED' ");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmt.setString(1, AcptNo);
	            	pstmt.executeUpdate();
	            	pstmt.close();
            	}

            	findSw = false;
        		strQuery.setLength(0);
        		strQuery.append("select sum(cnt) cnt from                       \n");
        		strQuery.append("(select count(*) cnt from cmr1010 a,cmm0036 b  \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                 \n");
        		strQuery.append("   and substr(b.cm_info,35,1)='1'              \n");
        		strQuery.append(" union                                         \n");
        		strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b,cmm0037 c \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=c.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                 \n");
        		strQuery.append("   and c.cm_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and c.cm_samersrc=b.cm_rsrccd               \n");
        		strQuery.append("   and substr(b.cm_info,35,1)='1')             \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	if (rs.next()) {
            		if (rs.getString("cnt") == null) findSw = true;
            		else if (rs.getInt("cnt") == 0) findSw = true;
            	}
            	rs.close();
            	pstmt.close();

            	if (findSw == true) {
	            	strQuery.setLength(0);
	            	strQuery.append("delete cmr9900 where cr_acptno=? and cr_teamcd='1' and cr_team='SYSRC' ");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmt.setString(1, AcptNo);
	            	pstmt.executeUpdate();
	            	pstmt.close();
            	}

        	}
        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, '000000', '', '9', ?, '1' ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, ReqCd);

        	pstmt.executeUpdate();
        	pstmt.close();
        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;

        	rs = null;
        	pstmt = null;

        	//ecamsLogger.error("+++++++++Request E N D+++");
        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.request_Confirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.request_Confirm() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.request_Confirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.request_Confirm() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}
}