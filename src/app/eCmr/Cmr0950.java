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
public class Cmr0950{


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
		String           strPrcSw     = "";
		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_status,a.cr_editor,a.cr_qrycd, 	\n");
			strQuery.append("       a.cr_jobcd,a.cr_acptdate,a.cr_passcd,                       \n");
			strQuery.append("       b.cm_username,e.cm_sysmsg,c.cm_jobname,                     \n");
			strQuery.append("substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,\n");
			strQuery.append("        a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,                  \n");
			strQuery.append("        to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,         \n");
			strQuery.append("       to_char(a.CR_ACPTDATE, 'yyyy/mm/dd hh24:mi:ss') acptdate,	\n");
			strQuery.append("       to_char(a.CR_PRCDATE, 'yyyy/mm/dd hh24:mi:ss')  prcdate		\n");
			strQuery.append("from cmm0102 c,cmr1000 a,cmm0040 b,cmm0030 e                       \n");
			strQuery.append("where a.cr_acptno=?                                                \n");
			strQuery.append("and a.cr_editor=b.cm_userid 										\n");
			strQuery.append("and a.cr_syscd=e.cm_syscd 											\n");
			strQuery.append("and a.cr_jobcd=c.cm_jobcd 							   			    \n");


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

				if (rs.getString("cr_status").equals("9")) {
					rst.put("confname","처리완료");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw", "0");
					rst.put("log", "1");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("confname","반려");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw", "0");
					rst.put("log", "1");
				} else {
					rst.put("updtsw", "0");
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

					if (rsconf.get(0).get("signteamcd").equals("5")) {
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmr9900 \n");
						strQuery.append(" where cr_acptno=?               \n");
						strQuery.append("   and cr_locat<>'00'            \n");
						strQuery.append("   and cr_teamcd='5'             \n");
						strQuery.append("   and cr_locat>?                \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, AcptNo);
						pstmt2.setString(2, rsconf.get(0).get("confusr").substring(0,2));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs2.getInt("cnt")==0) rst.put("updtsw", "1");
						}
						rs2.close();
						pstmt2.close();
					}
					rsconf.clear();
					rsconf = null;
				}
				rs.close();
				pstmt.close();


			}//end of while-loop statement
			rsval.add(rst);
			rst = null;

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn.close();


			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0950.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0950.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0950.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0950.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmr0950.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public ArrayList<HashMap<String, String>> getProgList(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt1       = null;
		ResultSet         rs          = null;
		ResultSet         rs1          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cr_qrycd,a.cr_reqcd,a.cr_manid,            \n");
			strQuery.append("       a.cr_useid,a.cr_usesecu,a.cr_userun,         \n");
			strQuery.append("       a.cr_homedir,cr_groupid,cr_usesys,cr_uid,    \n");
			strQuery.append("       a.cr_svrshell,cr_proddt,                     \n");
			strQuery.append("       nvl(a.cr_idgbn,'0') cr_idgbn                 \n");
			strQuery.append("  from cmr1900 a where a.cr_acptno = ?              \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
	        	rst = new HashMap<String, String>();
	        	rst.put("cr_qrycd", rs.getString("cr_qrycd"));
	        	rst.put("cr_idgbn", rs.getString("cr_idgbn"));
	        	rst.put("cr_reqcd", rs.getString("cr_reqcd"));
	        	rst.put("cr_manid", rs.getString("cr_manid"));
				if(rs.getString("cr_qrycd").equals("1")){
					rst.put("qrycd", "시스템ID");
				}else if(rs.getString("cr_qrycd").equals("2")){
					rst.put("qrycd", "데이타베이스ID");
					if (rs.getString("cr_idgbn").equals("1")) rst.put("idgbn", "개인ID");
					else rst.put("idgbn", "공용ID");
				}

				if(rs.getString("cr_reqcd").equals("1")){
					rst.put("reqcd", "발급");
				}else if(rs.getString("cr_reqcd").equals("2")){
					rst.put("reqcd", "해지");
				}
				if(rs.getString("cr_manid").equals("1")){
					rst.put("manid", "직원");
				}else if(rs.getString("cr_manid").equals("2")){
					rst.put("manid", "외주업체");
				}
				rst.put("cr_proddt",rs.getString("cr_proddt"));
				rst.put("proddt",rs.getString("cr_proddt").substring(0,4)+"/"+rs.getString("cr_proddt").substring(4,6)+"/"+rs.getString("cr_proddt").substring(6));
				rst.put("cr_useid",rs.getString("cr_useid"));
				rst.put("cr_usesecu",rs.getString("cr_usesecu"));
				rst.put("cr_userun",rs.getString("cr_userun"));
				rst.put("cr_homedir",rs.getString("cr_homedir"));
				rst.put("cr_groupid",rs.getString("cr_groupid"));
				rst.put("cr_usesys",rs.getString("cr_usesys"));
				rst.put("cr_svrshell",rs.getString("cr_svrshell"));
				rst.put("cr_uid",rs.getString("cr_uid"));


				if (rs.getString("cr_qrycd").equals("1")) {
					strQuery.setLength(0);
					strQuery.append(" SELECT A.CD_SVRIP, A.CD_APPUSE, A.CD_OSVER,			\n");
					strQuery.append("       (SELECT CM_CODENAME                             \n");
					strQuery.append("          FROM CMM0020                                 \n");
					strQuery.append("         WHERE CM_MACODE = 'SERVEROS'                  \n");
					strQuery.append("           AND CM_MICODE = A.CD_SVROS) AS SVROSNM,     \n");
					strQuery.append("       (SELECT CM_CODENAME                             \n");
					strQuery.append("          FROM CMM0020                                 \n");
					strQuery.append("         WHERE CM_MACODE = 'SVRMODEL'                  \n");
					strQuery.append("           AND CM_MICODE = A.CD_MODEL) AS MODELNM      \n");
					strQuery.append("  FROM CMD1200 A                                       \n");
					strQuery.append(" WHERE A.CD_SVRNM=?                                    \n");
					pstmt1 = conn.prepareStatement(strQuery.toString());
					//pstmt1 = new LoggableStatement(conn,strQuery.toString());
					pstmt1.setString(1, rs.getString("cr_usesys"));
			        //ecamsLogger.error(((LoggableStatement)pstmt1).getQueryString());
			        rs1 = pstmt1.executeQuery();

			        if(rs1.next()){
						rst.put("cd_svrip",rs1.getString("CD_SVRIP"));
						rst.put("cd_appuse",rs1.getString("CD_APPUSE"));
						rst.put("cd_osver",rs1.getString("CD_OSVER"));
						rst.put("svrosnm",rs1.getString("SVROSNM"));
						rst.put("modelnm",rs1.getString("MODELNM"));
			        }
					rs1.close();
					pstmt1.close();
				}


				rsval.add(rst);
	        	rst = null;
	        }
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs1 = null;
			pstmt1 = null;
			conn = null;


			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0950.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0950.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0950.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0950.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null) rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs1 != null)     try{rs1.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt1 != null)  try{pstmt1.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0950.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement


	public String updtData(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int  parmCnt = 0;
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("UPDATE  cmr1900 					\n");
			strQuery.append("   SET  cr_useid   = ? 			\n");
			strQuery.append("      , cr_usesecu = ?    		    \n");
			strQuery.append("      , cr_userun = ?    		    \n");
			if (etcData.get("qrycd").equals("1")) {
				strQuery.append("  , cr_homedir = ?    	        \n");
				strQuery.append("  , cr_groupid = ?    	        \n");
				strQuery.append("  , cr_uid = ?    	            \n");
				strQuery.append("  , cr_svrshell = ?    	    \n");
			}
			strQuery.append(" where cr_acptno = ?				\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(++parmCnt, etcData.get("user"));
			pstmt.setString(++parmCnt, etcData.get("secu"));
			pstmt.setString(++parmCnt, etcData.get("runmsg"));
			if (etcData.get("qrycd").equals("1")) {
				pstmt.setString(++parmCnt, etcData.get("home"));
				pstmt.setString(++parmCnt, etcData.get("group"));
				pstmt.setString(++parmCnt, etcData.get("uid"));
				pstmt.setString(++parmCnt, etcData.get("shell"));
			}
			pstmt.setString(++parmCnt, etcData.get("acptno"));
			pstmt.executeUpdate();
			pstmt.close();
        	conn.close();

        	pstmt = null;

        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0950.updtData() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0950.updtData() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0950.updtData() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0950.updtData() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0950.updtData() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


}