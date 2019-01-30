/*****************************************************************************************
	1. program ID	: Cmr0650.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

//import app.common.AutoSeq;
import app.common.LoggableStatement;
//import app.common.SystemPath;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;




/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr3750{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<HashMap<String, String>> getReqList(String UserId,String AcptNo) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String            strPrcSw = "";

		try {

			conn = connectionContext.getConnection();
			UserInfo userinfo = new UserInfo();
			boolean adminSw = userinfo.isAdmin_conn(UserId, conn);
			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_status,a.cr_editor,a.cr_qrycd, 	\n");
			strQuery.append("       a.cr_jobcd,a.cr_acptdate,a.cr_dept,a.cr_etc,                \n");
			strQuery.append("       a.cr_reqdate,a.cr_reqdoc,a.cr_reqtitle,a.cr_passcd,         \n");
			strQuery.append("       a.cr_sayucd,a.cr_sayu,                                      \n");
			strQuery.append("        a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,                  \n");
			strQuery.append("        to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,         \n");
			strQuery.append("       b.cm_username,c.cm_sysmsg,e.cm_jobname,d.cm_codename,       \n");
			strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,	\n");
			strQuery.append("       to_char(a.CR_ACPTDATE, 'yyyy/mm/dd hh24:mi:ss') acptdate,	\n");
			strQuery.append("       to_char(a.CR_PRCDATE, 'yyyy/mm/dd hh24:mi:ss')  prcdate,	\n");
			strQuery.append("       substr(a.CR_REQDATE,1,4) || '-' || substr(a.CR_REQDATE,5,2) || '-' || substr(a.CR_REQDATE,7,2) reqdate \n");
			strQuery.append("from  cmm0020 d,cmm0102 e,cmr1000 a, cmm0040 b,cmm0030 c    	    \n");
			strQuery.append("where a.cr_acptno=? 												\n");
			strQuery.append("and a.cr_editor=b.cm_userid 										\n");
			strQuery.append("and a.cr_syscd=c.cm_syscd 											\n");
			strQuery.append("and a.cr_jobcd=e.cm_jobcd 							   			    \n");
			strQuery.append("and d.cm_macode='REQSAYU' and a.cr_sayucd=d.cm_micode 			    \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();


			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("acptno"));
				rst.put("acptdate",rs.getString("acptdate"));
				if (rs.getString("prcdate") != null) rst.put("prcdate",rs.getString("prcdate"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("cr_jobcd", rs.getString("cr_jobcd"));
				rst.put("sayucd", rs.getString("cm_codename"));

				//rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				//rst.put("analsw", "N");

				rst.put("cr_acptdate" ,rs.getString("CR_ACPTDATE"));  //요청일자
				rst.put("updtsw1", "0");
				rst.put("updtsw2", "0");
				//업무팀요청
				rst.put("cr_dept" ,rs.getString("cr_dept"));  //의뢰부서
				rst.put("cr_reqdate" ,rs.getString("cr_reqdate"));  //의뢰일
				rst.put("reqdate" ,rs.getString("reqdate"));  //의뢰일
				rst.put("cr_reqdoc"  ,rs.getString("cr_reqdoc"));   //문서번호
				if (rs.getString("cr_reqtitle")!= null) rst.put("cr_reqtitle",rs.getString("cr_reqtitle")); //문서제목
				rst.put("cr_passcd", rs.getString("cr_passcd"));//제목
				rst.put("cr_etc"  ,rs.getString("cr_etc"));   //변경사유
				rst.put("cr_sayucd", rs.getString("cr_sayucd"));//요청구분


				if (rs.getString("cr_sayu") != null) rst.put("cr_sayu",rs.getString("cr_sayu")); //요청사유
				if (rs.getString("cr_status").equals("9")) {
					rst.put("confname","처리완료");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("log", "1");
					rst.put("sqlok", "2");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("confname","반려");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("log", "0");
					rst.put("sqlok", "0");

					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmr9900 \n");
					strQuery.append(" where cr_acptno=?               \n");
					strQuery.append("   and cr_locat<>'00'            \n");
					strQuery.append("   and cr_status='9'             \n");
					strQuery.append("   and cr_teamcd='1'             \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, AcptNo);
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("sqlok", rs2.getString("cnt"));
						if (rs2.getInt("cnt")>0) rst.put("log", "1");
					}
					rs2.close();
					pstmt2.close();
				} else {
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
						strPrcSw = "1";
					} else {
						rst.put("prcsw", "0");
						strPrcSw = "0";
					}
					rst.put("endsw", "0");
					rst.put("sqlok", "0");
					rst.put("log", "0");
					ArrayList<HashMap<String, String>>  rsconf = new ArrayList<HashMap<String, String>>();

					Cmr0150 cmr0150 = new Cmr0150();
					rsconf = cmr0150.confLocat_conn(AcptNo,strPrcSw,rs.getString("cr_cnclstep"),rs.getString("cncldat"),rs.getString("cr_retryyn"),conn);
					cmr0150 = null;

					rst.put("signteam", rsconf.get(0).get("signteam"));
					rst.put("signteamcd", rsconf.get(0).get("signteamcd"));
					rst.put("confusr", rsconf.get(0).get("confusr"));
					rst.put("cr_prcsw", rsconf.get(0).get("prcsw"));
					rst.put("confname", rsconf.get(0).get("confname"));

					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmr9900 \n");
					strQuery.append(" where cr_acptno=?               \n");
					strQuery.append("   and cr_locat<>'00'            \n");
					strQuery.append("   and cr_status='9'             \n");
					strQuery.append("   and cr_teamcd='1'             \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, AcptNo);
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("sqlok", rs2.getString("cnt"));
						if (rs2.getInt("cnt")>0) rst.put("log", "1");
					}
					rs2.close();
					pstmt2.close();

					if (strPrcSw.equals("0")) {
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmm0043  \n");
						strQuery.append(" where cm_userid=?                \n");
						strQuery.append("   and cm_rgtcd in ('121','122')  \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, UserId);
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs2.getInt("cnt")>0) rst.put("updtsw2", "1");
						}
						rs2.close();
						pstmt2.close();

						if (rsconf.get(0).get("signteamcd").equals("4") || adminSw == true) {
							strQuery.setLength(0);
							strQuery.append("select cr_team from cmr9900    \n");
							strQuery.append(" where cr_acptno=?             \n");
							strQuery.append("   and cr_locat<>'00'          \n");
							strQuery.append("   and cr_status='9'           \n");
							strQuery.append(" order by cr_locat desc        \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(1, AcptNo);
							rs2 = pstmt2.executeQuery();
							if (rs2.next()) {
								if (rs2.getString("cr_team").equals("SYSDB1")) rst.put("updtsw1", "1");
							}
							rs2.close();
							pstmt2.close();
						}
					}

					rsconf.clear();
					rsconf = null;
				}
				rs.close();
				pstmt.close();

			}//end of while-loop statement

			//ecamsLogger.error("rst="+rst.toString());
			rsval.add(rst);
            rst = null;

			conn.close();
			pstmt = null;
			rs = null;
			pstmt2 = null;
			rs2 = null;

			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## cmr3750.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## cmr3750.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## cmr3750.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## cmr3750.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## cmr3750.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	public ArrayList<HashMap<String, String>> getProgList(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.append("select a.cr_table,a.cr_runuser,a.cr_runtime,a.cr_proddt,    \n");
			strQuery.append("       a.cr_runmsg,a.cr_delaycd,a.cr_delaytbl,a.cr_qry,     \n");
			strQuery.append("       a.cr_runcd,a.cr_prcsys,a.cr_jobtime,a.cr_serno,      \n");
			strQuery.append("       b.cm_codename                                        \n");
			strQuery.append("  from cmr1700 a, cmm0020 b                           		 \n");
			strQuery.append(" where a.cr_acptno=?                            			 \n");
			strQuery.append("   and b.cm_macode='DBREQCD' and a.cr_runcd=b.cm_micode     \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        while (rs.next())
	        {
				rst = new HashMap<String, String>();
				rst.put("cr_table",rs.getString("cr_table"));
				rst.put("cr_runuser",rs.getString("cr_runuser"));
				rst.put("cr_runtime",rs.getString("cr_runtime"));
				rst.put("cr_proddt",rs.getString("cr_proddt"));
				rst.put("cr_runqry",rs.getString("cr_runmsg"));
				rst.put("cr_delaycd",rs.getString("cr_delaycd"));
				rst.put("cr_delaytbl",rs.getString("cr_delaytbl"));
				rst.put("cr_qry",rs.getString("cr_qry"));
				rst.put("cr_runcd",rs.getString("cr_runcd"));
				rst.put("cr_prcsys",rs.getString("cr_prcsys"));
				rst.put("cr_jobtime",rs.getString("cr_jobtime"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cr_serno", rs.getString("cr_serno"));
				rsval.add(rst);
	        	rst = null;
	        }
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## cmr3750.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## cmr3750.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## cmr3750.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## cmr3750.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null) rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## cmr3750.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement

	public String getFileText(String fullPath) throws Exception {
		StringBuffer      strQuery    		= new StringBuffer();

		BufferedReader in1 = null;
		String	readline1 = "";

		try {

			//8859_1, MS949
			File shfile=null;
			shfile = new File(fullPath);
			if(!shfile.isFile()) {
				strQuery.append("파일없음 ["+fullPath+"]");
			} else {
				in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath),"MS949"));

				strQuery.setLength(0);
				while( (readline1 = in1.readLine()) != null ){
					strQuery.append(readline1+"\n");
				}
				in1.close();
				in1 = null;
			}

			return strQuery.toString();

		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	        if (exception instanceof sun.io.MalformedInputException){
				in1.close();
				in1 = null;
				return strQuery.toString();
	   		}else{
	   			throw exception;
			}
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3750.getFileText() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3750.getFileText() Exception END ##");
			throw exception;
		}finally{
			if (in1 != null) in1 = null;
			if (strQuery != null) 	strQuery = null;
		}
	}//end of while-loop statement

	public String setReData(String acptno) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String			  szLocat	  = null;

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("Select cr_locat  from cmr9900 			\n");
			strQuery.append(" where cr_acptno = ?					\n");
			strQuery.append("   and cr_locat != '00' 				\n");
			strQuery.append("   and cr_team   = 'SYSDB1' 			\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, acptno);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				szLocat = rs.getString("cr_locat");
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("UPDATE  CMR9900 					\n");
			strQuery.append("   SET  CR_STATUS   = '0' 			\n");
			strQuery.append("      , CR_CONFDATE = '' 		    \n");
			strQuery.append("      , CR_CONFUSR  = '' 		    \n");
			strQuery.append("      , CR_CONMSG   = '' 		    \n");
			strQuery.append(" where cr_acptno = ?				\n");
			strQuery.append("   and cr_locat  = ?				\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptno);
			pstmt.setString(2, szLocat);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("UPDate  CMR9900															\n");
			strQuery.append("  Set  (cr_team,cr_teamcd,cr_sgngbn,cr_congbn,cr_common,cr_blank,cr_emger, \n");
			strQuery.append("        cr_holi,cr_orgstep,cr_confusr,cr_confname,cr_prcsw,cr_qrycd) =     \n");
			strQuery.append("(select cr_team,cr_teamcd,cr_sgngbn,cr_congbn,cr_common,cr_blank,cr_emger, \n");
			strQuery.append("        cr_holi,cr_orgstep,cr_locat || cr_locat,cr_confname,cr_prcsw,cr_qrycd \n");
			strQuery.append("           from  cmr9900 													\n");
			strQuery.append("          where  cr_acptno = ?												\n");
			strQuery.append("            and  cr_locat = ?) 											\n");
			strQuery.append(" Where  cr_acptno = ?														\n");
			strQuery.append("   And  cr_locat  = '00' 													\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptno);
			pstmt.setString(2, szLocat);
			pstmt.setString(3, acptno);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("UPDATE  CMR1700 					\n");
			strQuery.append("   SET  CR_SYSSTEP   = 0 			\n");
			strQuery.append("      , CR_PID = ''           		\n");
			strQuery.append("      , CR_PUTCODE  = ''    		\n");
			strQuery.append(" where cr_acptno = ?				\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptno);
			pstmt.executeUpdate();
			pstmt.close();

        	conn.close();

        	rs = null;
        	pstmt = null;

        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3700.setReData() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3700.setReData() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3700.setReData() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3700.setReData() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3700.setReData() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String updtReqSayu(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("UPDATE  cmr1000 					\n");
			strQuery.append("   SET  CR_sayucd   = ? 			\n");
			strQuery.append("      , cr_dept = ?     		    \n");
			strQuery.append("      , cr_reqdate  = ? 		    \n");
			strQuery.append("      , cr_reqdoc  = ? 		    \n");
			strQuery.append("      , cr_reqtitle  = ? 		    \n");
			strQuery.append(" where cr_acptno = ?				\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cr_sayucd"));
			pstmt.setString(2, etcData.get("cr_dept"));
			pstmt.setString(3, etcData.get("cr_reqdate"));
			pstmt.setString(4, etcData.get("cr_reqdoc"));
			pstmt.setString(5, etcData.get("cr_reqtitle"));
			pstmt.setString(6, etcData.get("acptno"));
			pstmt.executeUpdate();
			pstmt.close();
        	conn.close();
        	pstmt = null;
        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3750.updtReqSayu() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3750.updtReqSayu() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3750.updtReqSayu() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3750.updtReqSayu() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3750.updtReqSayu() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String updtQry(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("UPDATE  cmr1700 					\n");
			strQuery.append("   SET  cr_runmsg   = ? 			\n");
			strQuery.append("      , cr_qry = ?     		    \n");
			strQuery.append(" where cr_acptno = ?				\n");
			strQuery.append("   and cr_serno = ?				\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, etcData.get("cr_runqry"));
			pstmt.setString(2, etcData.get("cr_qry"));
			pstmt.setString(3, etcData.get("acptno"));
			pstmt.setString(4, etcData.get("cr_serno"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
        	conn.close();
        	pstmt = null;
        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3750.updtQry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3750.updtQry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3750.updtQry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3750.updtQry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3750.updtQry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}