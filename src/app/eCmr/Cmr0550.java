/*****************************************************************************************
	1. program ID	: Cmr0550.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 2009. 05. 20
	5. auth		    : no name
	6. description	: []
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
import java.util.HashMap;

//import app.common.LoggableStatement;
//import app.common.UserInfo;
//import app.common.SystemPath;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr0550{


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
	public Object[] getReqList(String UserId,String AcptNo) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] 		  returnObject = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') acptdate,     \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') prcdate,       \n");
			strQuery.append("       a.cr_acptno,a.cr_editor,a.cr_sayu,a.cr_status,               \n");
			strQuery.append("       a.cr_qrycd,                                                  \n");
			strQuery.append("       to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,           \n");
			strQuery.append("       a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,                    \n");
			strQuery.append("       a.cr_passok,d.cm_username,                                   \n");
			strQuery.append("       (select cm_deptname from cmm0100 where cm_deptcd =           \n");
			strQuery.append("        d.cm_project) as cm_dept 					                 \n");
			strQuery.append("  from cmm0040 d,cmr1000 a                                          \n");
			strQuery.append(" where a.cr_acptno=?                                                \n");
			strQuery.append("   and a.cr_editor=d.cm_userid                                      \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	       // //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptdate",rs.getString("acptdate"));
				if (rs.getString("prcdate") != null){
					rst.put("prcdate",rs.getString("prcdate"));
				}
				rst.put("cm_username",rs.getString("cm_dept")+"  "+rs.getString("cm_username"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_passok",rs.getString("cr_passok"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				if (rs.getString("cr_sayu") != null)
				   rst.put("cr_sayu",rs.getString("cr_sayu"));

				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmr9900                   \n");
				strQuery.append(" where cr_acptno=? and cr_locat<>'00'              \n");
				strQuery.append("   and cr_teamcd not in('1','2')                   \n");
				strQuery.append("   and (cr_team=? or cr_confusr=?)                 \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, AcptNo);
				pstmt2.setString(2, UserId);
				pstmt2.setString(3, UserId);
				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					if (rs2.getInt("cnt")>0) rst.put("confsw", "1");
					else rst.put("confsw", "0");
				}
				rs2.close();
				pstmt2.close();

				if (rs.getString("cr_status").equals("9")) { //|| rs.getString("cr_status").equals("8")) {
					rst.put("confname","처리완료");
					rst.put("prcsw", "1");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("confname","반려");
					rst.put("prcsw", "1");
				} else {
					String strPrcSw = "0";
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
					} else {
						rst.put("prcsw", "0");
					}

					//ecamsLogger.debug("++++++++++RetryYn++++++++"+rs.getString("cncldat"));
					ArrayList<HashMap<String, String>>  rsconf = new ArrayList<HashMap<String, String>>();

					Cmr0150 cmr0150 = new Cmr0150();
					rsconf = cmr0150.confLocat_conn(AcptNo,strPrcSw,rs.getString("cr_cnclstep"),rs.getString("cncldat"),rs.getString("cr_retryyn"),conn);
					cmr0150 = null;

					rst.put("signteam", rsconf.get(0).get("signteam"));
					rst.put("signteamcd", rsconf.get(0).get("signteamcd"));
					rst.put("confusr", rsconf.get(0).get("confusr"));
					rst.put("cr_prcsw", rsconf.get(0).get("prcsw"));
					rst.put("confname", rsconf.get(0).get("confname"));
					rst.put("ermsg", rsconf.get(0).get("ermsg"));

					rsconf.clear();
					rsconf = null;
				}

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1001               \n");
				strQuery.append(" where cr_acptno=? and cr_gubun='1'               \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
		        ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0){
						rst.put("file","1");
					}
					else{
						rst.put("file","0");
					}
				}
				rs2.close();
				pstmt2.close();

				rsval.add(rst);
				rst = null;

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0550.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0550.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0550.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0550.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (returnObject != null)	returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0550.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	public Object[] JoinUserBace(String AcptNo) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		Connection        conn        = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] 		  returnObject = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.CR_ACPTNO,a.CR_EDITOR,a.CR_STATUS,a.CR_USERID,\n");
			strQuery.append("    nvl(a.CR_LIMITDT,'99999999') cr_limitdt,			\n");
			strQuery.append("    a.CR_NAME,a.CR_PROJECT,a.CR_POSITION,a.CR_DUTY,    \n");
			strQuery.append("    a.CR_qrycd,b.cm_codename                           \n");
			strQuery.append("from cmr1520 a,cmm0020 b								\n");
			strQuery.append(" where a.cr_acptno=? 						            \n");
			strQuery.append(" and b.cm_macode='GENREQ' and b.cm_micode=a.cr_qrycd   \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
		//	pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	   //     //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()){
	        	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
            	rst.put("CR_EDITOR", rs.getString("CR_EDITOR"));
				rst.put("CR_STATUS", rs.getString("CR_STATUS"));
				rst.put("CR_LIMITDT", rs.getString("cr_limitdt"));
				rst.put("CR_USERID", rs.getString("CR_USERID"));
				rst.put("CR_NAME", rs.getString("CR_NAME"));
				rst.put("CR_PROJECT", rs.getString("CR_PROJECT"));
				rst.put("CR_POSITION", rs.getString("CR_POSITION"));
				rst.put("CR_DUTY", rs.getString("CR_DUTY"));
				rst.put("CR_qrycd", rs.getString("CR_qrycd"));
				rst.put("qrycd", rs.getString("cm_codename"));

				strQuery.setLength(0);
				strQuery.append("select cm_codename from cmm0020  \n");
				strQuery.append(" where cm_macode='POSITION'      \n");
				strQuery.append("   and cm_micode=?			      \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, rs.getString("CR_POSITION"));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if(rs2.next()){
					rst.put("cm_codename", rs2.getString("cm_codename"));
				}

				rs2.close();
				pstmt2.close();

				strQuery.setLength(0);
				strQuery.append("select cm_codename from cmm0020   \n");
				strQuery.append(" where cm_macode='DUTY'           \n");
				strQuery.append("   and cm_micode=?		           \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, rs.getString("CR_DUTY"));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if(rs2.next()){
					rst.put("cm_dutyname", rs2.getString("cm_codename"));
				}
				rs2.close();
				pstmt2.close();

				if (rs.getString("CR_PROJECT") != null) {
					strQuery.setLength(0);
					strQuery.append("select cm_deptname   \n");
					strQuery.append("  from cmm0100		  \n");
					strQuery.append(" where cm_deptcd=?	  \n");
			        pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("CR_PROJECT"));
			        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs2 = pstmt2.executeQuery();
					if(rs2.next()){
						rst.put("cm_deptname", rs2.getString("cm_deptname"));
					}
					rs2.close();
					pstmt2.close();
				}
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0550.JoinUserBace() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0550.JoinUserBace() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0550.JoinUserBace() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0550.JoinUserBace() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0550.JoinUserBace() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of JoinUserBace() method statement

	public Object[] JoinUserJob(String AcptNo) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		Connection        conn        = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] 		  returnObject = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.CR_ACPTNO,a.CR_SEQNO,a.CR_JOBSEC,a.CR_SYSCD,\n");
			strQuery.append("       b.cm_jobname,c.cm_sysmsg                \n");
			strQuery.append("  from cmr1521 a,cmm0102 b,cmm0030 c			\n");
			strQuery.append(" where a.cr_acptno=?   				        \n");
			strQuery.append("   and a.cr_syscd=c.cm_syscd			        \n");
			strQuery.append("   and a.cr_jobsec=b.cm_jobcd			        \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()){
	        	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
	        	rst.put("CR_SYSCD", rs.getString("CR_SYSCD"));
				rst.put("CR_JOBSEC", rs.getString("CR_JOBSEC"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));

				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0550.JoinUserJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0550.JoinUserJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0550.JoinUserJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0550.JoinUserJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0550.JoinUserJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of chgVolPath() method statement

	public String updtConfirm(String AcptNo,String UserId,String conMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";
		String            newId       = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cr_userid,cr_qrycd from cmr1520   \n");
			strQuery.append(" where cr_acptno=?                       \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				//qryCd = rs.getString("cr_qrycd");
				if (rs.getString("cr_userid") != null) newId = rs.getString("cr_userid");
			}
			rs.close();
			pstmt.close();

			if (newId != null && newId != "") {
				if (!newId.substring(0,1).equals("o") && !newId.substring(0,1).equals("d")) {
					newId = "d" + newId;
				}
			} else {
				strQuery.setLength(0);
				strQuery.append("select '0' || to_char(SYSDATE,'yymmdd') || (nvl(max(substr(cm_userid,8,1)),0)+1) newid \n");
				strQuery.append("  from cmm0040 \n");
				strQuery.append(" where length(cm_userid)>6  \n");
				strQuery.append("   and substr(cm_userid,2,6)=to_char(SYSDATE,'yymmdd') \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					newId = rs.getString("newid");
				}
				rs.close();
				pstmt.close();
			}
			strQuery.setLength(0);
			strQuery.append("update cmr1520 set cr_userid=?,  \n");
			strQuery.append("       cr_prcrst='0000',         \n");
			strQuery.append("       cr_prcuser=?,             \n");
			strQuery.append("       cr_prcdate=SYSDATE,       \n");
			strQuery.append("       cr_status='9'             \n");
			strQuery.append(" where cr_acptno=?               \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, newId);
			pstmt.setString(2, UserId);
			pstmt.setString(3, AcptNo);
			pstmt.executeUpdate();

			Cmr3100 cmr3100 = new Cmr3100();
			strErMsg = cmr3100.nextConf(AcptNo,UserId, conMsg, "1", AcptNo.substring(4,6));
			cmr3100 = null;

	        conn.close();

			rs = null;
			pstmt = null;
			conn = null;

	        return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0550.updtConfirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0550.updtConfirm() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0550.updtConfirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0550.updtConfirm() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0550.updtConfirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtConfirm() method statement
}//end of Cmr0550 class statement