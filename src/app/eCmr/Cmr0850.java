/*****************************************************************************************
	1. program ID	: Cmr0650.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

//import app.common.AutoSeq;
//import app.common.LoggableStatement;
//import app.common.SystemPath;
//import app.common.UserInfo;

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
public class Cmr0850{


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
		String            strPrcSw = "";

		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.append(" select a.cr_acptno,a.cr_syscd,a.cr_status,a.cr_editor,a.cr_qrycd, \n");
			strQuery.append("        a.cr_acptdate,a.cr_passcd,a.cr_sayu,                       \n");
			strQuery.append("        a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,                  \n");
			strQuery.append("        to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,         \n");
			strQuery.append("        b.cm_username,c.cm_jobname,e.cm_sysmsg,			        \n");
			strQuery.append("substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,				\n");
			strQuery.append("       to_char(a.CR_ACPTDATE, 'yyyy/mm/dd hh24:mi:ss') acptdate,	\n");
			strQuery.append("       to_char(a.CR_PRCDATE, 'yyyy/mm/dd hh24:mi:ss')  prcdate		\n");
			strQuery.append("from cmr1000 a, cmm0040 b,cmm0102 c,cmm0030 e 				\n");
			strQuery.append("where a.cr_acptno=?										\n");
			strQuery.append("and a.cr_editor=b.cm_userid								\n");
			strQuery.append("and a.cr_syscd=e.cm_syscd									\n");
			strQuery.append("and a.cr_jobcd=c.cm_jobcd 			    					\n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

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
				rst.put("cr_acptdate" ,rs.getString("CR_ACPTDATE"));  //요청일자
				rst.put("cr_passcd", rs.getString("cr_passcd"));//제목
				rst.put("strprcsw", "0");
				//rst.put("cr_sayucd", rs.getString("cr_sayucd"));//요청구분

				if (rs.getString("cr_status").equals("9")) {
					rst.put("cr_confname","처리완료");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw", "0");
					rst.put("log", "1");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("cr_confname","반려");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw", "0");
					rst.put("log", "1");
				} else {
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
						strPrcSw = "1";
					} else {
						rst.put("prcsw", "0");
						strPrcSw = "0";
					}
					rst.put("endsw", "0");
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
					strQuery.append("select a.cr_team,a.cr_confname   \n");
					strQuery.append("  from cmm0043 b,cmr9900 a       \n");
					strQuery.append(" where a.cr_acptno=?             \n");
					strQuery.append("   and a.cr_locat<>'00'          \n");
					strQuery.append("   and a.cr_locat>=?             \n");
					strQuery.append("   and a.cr_teamcd='5'           \n");
					strQuery.append("   and b.cm_userid=?             \n");
					strQuery.append("   and a.cr_team=b.cm_rgtcd      \n");
					strQuery.append(" order by a.cr_locat             \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, AcptNo);
					pstmt2.setString(2, rsconf.get(0).get("confusr").substring(0,2));
					pstmt2.setString(3, UserId);
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getString("cr_confname").indexOf("계획")>=0) {
							rst.put("strprcsw", "5");
						} else if (rs2.getString("cr_confname").indexOf("결과")>=0) {
							rst.put("strprcsw", "6");
						}
					}
					rs2.close();
					pstmt2.close();

					rsconf.clear();
					rsconf = null;
				}

			}//end of while-loop statement
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmr1801               \n");
			strQuery.append(" where cr_acptno=?                                \n");
	        pstmt2 = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt2.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
	        rs2 = pstmt2.executeQuery();
			if (rs2.next()){
				if (rs2.getInt("cnt") > 0) rst.put("cmr1801","1");
				else rst.put("cmr1801","0");
			}
			rs2.close();
			pstmt2.close();

			rsval.add(rst);
			rst = null;
			conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0850.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0850.getReqList() connection release exception ##");
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

			strQuery.append("select a.cr_proddt,a.cr_reqtit,a.cr_reqsayu,a.cr_reqgb,	\n");
			strQuery.append("     a.cr_medium,a.cr_useperiod,a.cr_prjdate,a.cr_userepeat,\n");
			strQuery.append("     a.cr_chgnm,a.cr_chgreq,a.cr_chggb,a.cr_addition,	    \n");
			strQuery.append("     a.cr_result,a.cr_rstsayu,         	                \n");
			strQuery.append("     to_char(a.cr_prjdate,'yyyy/mm/dd hh24:mi') prjdate,   \n");
			strQuery.append("     b.cm_codename SYSMAN,c.cm_codename as GENREQ, 		\n");
			strQuery.append("     d.cm_codename SYSREQ,e.cm_codename as SYSTERM 		\n");
			strQuery.append("from cmm0020 e,cmm0020 d,cmm0020 c,cmm0020 b,cmr1800 a    	\n");
			strQuery.append("where a.cr_acptno=?			                         	\n");
			strQuery.append("and b.cm_macode='SYSMAN'                         			\n");
			strQuery.append("and a.cr_gbncd=b.cm_micode                         		\n");
			strQuery.append("and c.cm_macode='GENREQ'                         			\n");
			strQuery.append("and a.cr_qrycd=c.cm_micode                                 \n");
			strQuery.append("and d.cm_macode='SYSREQ'                         			\n");
			strQuery.append("and a.cr_reqgb=d.cm_micode                                 \n");
			strQuery.append("and e.cm_macode='SYSTERM'                         			\n");
			strQuery.append("and a.cr_reqgb=e.cm_micode                                 \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
				rst = new HashMap<String, String>();
				rst.put("cr_proddt",rs.getString("cr_proddt"));
				rst.put("cr_reqtit",rs.getString("cr_reqtit"));
				rst.put("cr_reqsayu",rs.getString("cr_reqsayu"));
				rst.put("cr_reqgb",rs.getString("cr_reqgb"));
				rst.put("cr_medium",rs.getString("cr_medium"));
				rst.put("cr_useperiod",rs.getString("cr_useperiod"));
				rst.put("cr_userepeat",rs.getString("cr_userepeat"));
				rst.put("reqsayucd",rs.getString("SYSMAN"));
				rst.put("sincd" ,rs.getString("GENREQ"));
				rst.put("reqcd",rs.getString("SYSREQ"));
				rst.put("bindo",rs.getString("SYSTERM")+ rs.getString("cr_userepeat") + "회");

				rst.put("cr_prjdate",rs.getString("cr_prjdate"));
				rst.put("prjdate",rs.getString("prjdate"));
				rst.put("cr_chgnm",rs.getString("cr_chgnm"));
				rst.put("cr_chgreq",rs.getString("cr_chgreq"));
				rst.put("cr_chggb",rs.getString("cr_chggb"));
				rst.put("cr_addition",rs.getString("cr_addition"));
				rst.put("cr_result",rs.getString("cr_result"));
				rst.put("cr_rstsayu",rs.getString("cr_rstsayu"));
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
			ecamsLogger.error("## Cmr0850.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr0850.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement

	public ArrayList<HashMap<String, String>> get1801List(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.append("select a.cr_acptno,a.cr_serno,a.cr_acptdate,a.cr_title,a.cr_rundate,a.cr_machine,  \n");
			strQuery.append("a.cr_time,a.cr_runmsg,a.cr_runusr,a.cr_recv,a.cr_reldoc,b.cm_username       \n");
			strQuery.append("from cmr1801 a, cmm0040  b                                       	         \n");
			strQuery.append("where a.cr_acptno=?			                            	\n");
			strQuery.append("  and a.cr_runusr=b.cm_userid                         			\n");
			strQuery.append("order by a.cr_serno                             	   	        \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
				rst = new HashMap<String, String>();
				rst.put("cr_serno",rs.getString("cr_serno"));
				rst.put("cr_title",rs.getString("cr_title"));
				rst.put("cr_rundate",rs.getString("cr_rundate"));
				rst.put("rundate",rs.getString("cr_rundate").substring(0,4)+"/"+
						          rs.getString("cr_rundate").substring(4,6)+"/"+
						          rs.getString("cr_rundate").substring(6));
				rst.put("cr_machine",rs.getString("cr_machine"));
				rst.put("cr_time",rs.getString("cr_time"));
				rst.put("cr_runmsg",rs.getString("cr_runmsg"));
				rst.put("cr_runusr",rs.getString("cr_runusr"));
				rst.put("cr_recv",rs.getString("cr_recv"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_reldoc",rs.getString("cr_reldoc"));
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
			ecamsLogger.error("## Cmr0850.get1801List() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.get1801List() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.get1801List() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.get1801List() Exception END ##");
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
					ecamsLogger.error("## Cmr0850.get1801List() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of get1801List() method statement

	public ArrayList<HashMap<String, String>> get1802List(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.append("select a.cr_serno,a.cr_acptdate,a.cr_time,       \n");
			strQuery.append("       a.cr_runmsg,a.cr_runusr,b.cm_username     \n");
			strQuery.append("from cmr1802 a, cmm0040  b                       \n");
			strQuery.append("where a.cr_acptno=?			                  \n");
			strQuery.append("  and a.cr_runusr=b.cm_userid                    \n");
			strQuery.append("order by a.cr_serno                              \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
				rst = new HashMap<String, String>();
				rst.put("cr_serno",rs.getString("cr_serno"));
				rst.put("cr_time",rs.getString("cr_time"));
				rst.put("cr_runmsg",rs.getString("cr_runmsg"));
				rst.put("cr_runusr",rs.getString("cr_runusr"));
				rst.put("cm_username",rs.getString("cm_username"));
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
			ecamsLogger.error("## Cmr0850.get1802List() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.get1802List() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.get1802List() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.get1802List() Exception END ##");
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
					ecamsLogger.error("## Cmr0850.get1802List() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of get1802List() method statement
	public ArrayList<HashMap<String, String>> confLocat_conn(String AcptNo,String PrcSw,String CnclStep,
			String CnclDat,String RetryYn,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strTeamCd   = "";
		String            strTeam     = "";
//		String            strQry     = "";
//		String            strErMsg   = "";
//		boolean           prcSw       = false;

		try {
			//strQry = AcptNo.substring(4,6);
			strQuery.append("select cr_team,cr_teamcd,cr_confname,cr_prcsw,cr_confusr,cr_congbn  \n");
			strQuery.append("  from cmr9900                                                      \n");
			strQuery.append(" where cr_acptno=? and cr_locat='00'                                \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			if (rs.next()){
				rst = new HashMap<String, String>();
				strTeamCd = rs.getString("cr_teamcd");
				strTeam = rs.getString("cr_team");
				rst.put("signteam", strTeam);
				rst.put("signteamcd", strTeamCd);
				rst.put("confusr", rs.getString("cr_confusr"));
				rst.put("prcsw", rs.getString("cr_prcsw"));
				//if (rs.getString("cr_prcsw").equals("Y")) prcSw = true;

				if (strTeamCd.equals("1")) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020             \n");
					strQuery.append(" where cm_macode='SYSGBN' and cm_micode=?   \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1,strTeam);
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("confname", rs2.getString("cm_codename"));
					}
					rs2.close();
					pstmt2.close();
				}else if (strTeamCd.equals("2") || strTeamCd.equals("3")
						|| strTeamCd.equals("6") || strTeamCd.equals("7")){
						//|| rs.getString("cr_teamcd").equals("8")) { //참조자는 "처리완료" 메시지로 보여줌
					strQuery.setLength(0);
					strQuery.append("select cm_username from cmm0040             \n");
					strQuery.append(" where cm_userid=?                          \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1,strTeam);
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("confname", "["+rs2.getString("cm_username") + "]님 결재대기 중");
					}
					rs2.close();
					pstmt2.close();
				}else if (strTeamCd.equals("8")){
					rst.put("confname", "처리완료");
				}else {
					String tmpStr = "";
					if (rs.getString("cr_congbn").equals("4")){
						tmpStr = "(후결)";
					}
					rst.put("confname", rs.getString("cr_confname")+tmpStr);
				}
			}
			rs.close();
			pstmt.close();

			rst.put("updtsw1", "0");
			rst.put("updtsw2", "0");
			rst.put("log", "0");
			rst.put("errtry", "0");

			rsval.add(rst);
            rst = null;

    		return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr0850.confLocat_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.confLocat_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr0850.confLocat_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.confLocat_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of confLocat_conn() method statement


	public String update_chgContent(String acptno, HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("update cmr1800 			\n");
			strQuery.append("set cr_chgnm = ?,			\n");
			strQuery.append("cr_chgreq = ?,			 	\n");
			strQuery.append("cr_chggb = ?,			 	\n");
			strQuery.append("cr_addition = ?,			\n");
			strQuery.append("cr_result = ?, 			\n");
			strQuery.append("cr_rstsayu = ?  			\n");
			strQuery.append("where cr_acptno = ? 		\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(pstmtcount++, etcData.get("chgnm"));
        	pstmt.setString(pstmtcount++, etcData.get("chgreq"));
        	pstmt.setString(pstmtcount++, etcData.get("chggb"));
        	pstmt.setString(pstmtcount++, etcData.get("addition"));
        	pstmt.setString(pstmtcount++, etcData.get("result"));
        	pstmt.setString(pstmtcount++, etcData.get("rstsayu"));
        	pstmt.setString(pstmtcount++, acptno);
        	pstmt.executeUpdate();
        	pstmt.close();

        	conn.close();

        	rs = null;
        	pstmt = null;
        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0850.update_chgContent() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.update_chgContent() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.update_chgContent() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.update_chgContent() Exception END ##");
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
					ecamsLogger.error("## Cmr0850.update_chgContent() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String cmr1801_Update(HashMap<String,String> etcData, ArrayList<HashMap<String,String>>cmr1801List) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  pstmtcount  = 1;
    	int    seq = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
        	strQuery.append("delete cmr1801     \n");
        	strQuery.append(" where cr_acptno=? \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, etcData.get("acptno"));
        	pstmt.executeUpdate();
        	pstmt.close();

			for (int i=0 ; i<cmr1801List.size() ; i++){
            	pstmtcount = 1;
	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr1801 (   \n");
	        	strQuery.append(" CR_ACPTNO  ,           \n");
	        	strQuery.append(" CR_SERNO   ,           \n");
	        	strQuery.append(" CR_ACPTDATE,           \n");
	        	strQuery.append(" CR_TITLE   ,           \n");
	        	strQuery.append(" CR_RUNDATE ,           \n");
	        	strQuery.append(" CR_MACHINE ,           \n");
	        	strQuery.append(" CR_TIME    ,           \n");
	        	strQuery.append(" CR_RUNMSG  ,           \n");
	        	strQuery.append(" CR_RUNUSR  ,           \n");
	        	strQuery.append(" CR_RECV    )           \n");
	        	strQuery.append(" values (?, ?, SYSDATE, ?, ?, ?, ?, ?, ?, ?) \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("acptno"));
            	pstmt.setInt(pstmtcount++, ++seq);
	        	pstmt.setString(pstmtcount++, etcData.get("cr_title"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_rundate"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_machine"));
	        	pstmt.setString(pstmtcount++, cmr1801List.get(i).get("cr_time"));
	        	pstmt.setString(pstmtcount++, cmr1801List.get(i).get("cr_runmsg"));
	        	pstmt.setString(pstmtcount++, etcData.get("userid"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_recv"));
	        	pstmt.executeUpdate();
	        	pstmt.close();
			}

			strQuery.setLength(0);
        	strQuery.append("update cmr1800 set cr_prjdate=SYSDATE ");
        	strQuery.append(" where cr_acptno=? \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, etcData.get("acptno"));
        	pstmt.executeUpdate();
        	pstmt.close();
	  		conn.close();

			pstmt = null;
			conn = null;

			return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0850.cmr1801_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.cmr1801_Update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.cmr1801_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.cmr1801_Update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0850.cmr1801_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String cmr1802_Update(HashMap<String,String> etcData, ArrayList<HashMap<String,String>>cmr1801List) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  pstmtcount  = 1;
    	int    seq = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
        	strQuery.append("delete cmr1802     \n");
        	strQuery.append(" where cr_acptno=? \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, etcData.get("acptno"));
        	pstmt.executeUpdate();

			for (int i=0 ; i<cmr1801List.size() ; i++){
            	pstmtcount = 1;
	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr1802 (   \n");
	        	strQuery.append(" CR_ACPTNO  ,           \n");
	        	strQuery.append(" CR_SERNO   ,           \n");
	        	strQuery.append(" CR_ACPTDATE,           \n");
	        	strQuery.append(" CR_TIME    ,           \n");
	        	strQuery.append(" CR_RUNMSG  ,           \n");
	        	strQuery.append(" CR_RUNUSR  )           \n");
	        	strQuery.append(" values (?, ?, SYSDATE, ?, ?, ?) \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("acptno"));
            	pstmt.setInt(pstmtcount++, ++seq);
	        	pstmt.setString(pstmtcount++, cmr1801List.get(i).get("cr_time"));
	        	pstmt.setString(pstmtcount++, cmr1801List.get(i).get("cr_runmsg"));
	        	pstmt.setString(pstmtcount++, etcData.get("userid"));
	        	pstmt.executeUpdate();
			}

	  		conn.close();

			pstmt = null;
			conn = null;

			return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0850.cmr1802_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0850.cmr1802_Update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0850.cmr1802_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0850.cmr1802_Update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0850.cmr1802_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}