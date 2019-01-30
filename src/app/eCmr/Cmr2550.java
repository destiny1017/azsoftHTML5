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
public class Cmr2550{


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
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String           strPrcSw     = "";
		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_status,a.cr_editor,a.cr_qrycd, 	\n");
			strQuery.append("       a.cr_jobcd,a.cr_acptdate,a.cr_passcd,a.cr_sayu,             \n");
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
				rst.put("cr_sayu",rs.getString("cr_sayu"));

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
			conn.close();


			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2550.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2550.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2550.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2550.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2550.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public ArrayList<HashMap<String, String>> getProgList(String AcptNo,String SysCd) throws SQLException, Exception {
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
			strQuery.append("select CR_RUNSYS,CR_RUNMSG,CR_PGMID,CR_STEP,CR_GROUP,CR_ORDER, \n");
			strQuery.append("       CR_PCIRCLE,CR_CONTINUE,CR_CHKPGMID,CR_BATCH,CR_CONTROL, \n");
			strQuery.append("       CR_SRTNM,CR_LSTNM,CR_ARGUMENT,CR_ETC                    \n");
			strQuery.append("  from cmr2200                         \n");
			strQuery.append(" where cr_acptno=?                     \n");
			strQuery.append(" order by cr_serno                     \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
	        	rst = new HashMap<String, String>();
	        	rst.put("cr_runsys", rs.getString("CR_RUNSYS"));
	        	rst.put("cr_runmsg", rs.getString("CR_RUNMSG"));

	        	rst.put("cr_pgmid", rs.getString("CR_PGMID"));
	        	rst.put("cr_step", rs.getString("CR_STEP"));
	        	rst.put("cr_group", rs.getString("CR_GROUP"));
	        	rst.put("cr_order", rs.getString("CR_ORDER"));
	        	rst.put("cr_pcircle", rs.getString("CR_PCIRCLE"));
	        	if(rs.getString("CR_CONTINUE").equals("0")){
	        		rst.put("cr_continue", "계속");
	        	}else{
	        		rst.put("cr_continue", "종료");
	        	}
	        	rst.put("cr_chkpgmid", rs.getString("CR_CHKPGMID"));
	        	if(rs.getString("CR_BATCH").equals("0")){
	        		rst.put("cr_batch", "Yes");
	        	}else{
	        		rst.put("cr_batch", "No");
	        	}
	        	if(rs.getString("CR_CONTROL").equals("0")){
	        		rst.put("cr_control", "Yes");
	        	}else{
	        		rst.put("cr_control", "No");
	        	}
	        	rst.put("cr_srtnm", rs.getString("CR_SRTNM"));
	        	rst.put("cr_lstnm", rs.getString("CR_LSTNM"));
	        	rst.put("cr_argument", rs.getString("CR_ARGUMENT"));
	        	rst.put("cr_etc", rs.getString("CR_ETC"));

	        	strQuery.setLength(0);
	        	strQuery.append("select cr_story		\n");
	        	strQuery.append("from cmr0020			\n");
	        	strQuery.append("where cr_syscd=? 		\n");
	        	strQuery.append("and 					\n");
				if(rs.getString("CR_PGMID").indexOf(".")>0){
					strQuery.append("upper(cr_rsrcname)= ? \n");
				}else{
					strQuery.append("upper(cr_rsrcname) in (?, ?, ?) \n");
				}
				pstmt1 = conn.prepareStatement(strQuery.toString());
				//pstmt1 = new LoggableStatement(conn,strQuery.toString());
				pstmt1.setString(1, SysCd);
				if(rs.getString("CR_PGMID").indexOf(".")>0){
					pstmt1.setString(2, rs.getString("CR_PGMID").toUpperCase());
				}else{
					pstmt1.setString(2, rs.getString("CR_PGMID").toUpperCase()+".LGC");
					pstmt1.setString(3, rs.getString("CR_PGMID").toUpperCase()+".PC");
					pstmt1.setString(4, rs.getString("CR_PGMID").toUpperCase()+".C");
				}
		        //ecamsLogger.error(((LoggableStatement)pstmt1).getQueryString());
		        rs1 = pstmt1.executeQuery();
			    if(rs1.next()){
			    	rst.put("cr_story", rs1.getString("cr_story"));
			    }
	        	rs1.close();
	        	pstmt1.close();

	        	rs1 = null;
	        	pstmt1 = null;

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
			ecamsLogger.error("## Cmr2550.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2550.getProgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2550.getProgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2550.getProgList() Exception END ##");
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
					ecamsLogger.error("## Cmr2550.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgList() method statement



	public String update_Sys(String sys, String msg, String acptno) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("update cmr2200						\n");
			strQuery.append("   set cr_runsys = ?				\n");
			strQuery.append("      ,cr_runmsg = ?				\n");
			strQuery.append(" where cr_acptno = ?				\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, sys);
        	pstmt.setString(2, msg);
        	pstmt.setString(3, acptno);
        	pstmt.executeUpdate();
        	pstmt.close();

        	conn.close();

        	rs = null;
        	pstmt = null;

        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2550.update_Sys() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2550.update_Sys() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2550.update_Sys() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2550.update_Sys() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2550.update_Sys() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2550.update_Sys() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2550.update_Sys() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}