/*****************************************************************************************
	1. program ID	: Cmr0558.java
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
public class Cmr0558{


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
			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') acptdate,        \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') prcdate,          \n");
			strQuery.append("       a.cr_acptno,a.cr_syscd,a.cr_editor,a.cr_sayu,a.cr_status,    \n");
			strQuery.append("       a.cr_qrycd,a.cr_passsub,a.cr_sayucd,a.cr_docno,a.cr_gyuljae, \n");
			strQuery.append("       a.cr_svrcd,a.cr_svrseq,             \n");
			strQuery.append("       to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,           \n");
			strQuery.append("       a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,                    \n");
			strQuery.append("       a.cr_passok,b.cm_sysmsg,b.cm_sysinfo,d.cm_username,          \n");
			strQuery.append("       (select cm_deptname from cmm0100 where cm_deptcd =           \n");
			strQuery.append("        d.cm_project) as cm_dept,					                 \n");
			strQuery.append("       g.cm_codename reqpass,f.cm_codename reqgbn,a.cr_prjno,a.cr_devno \n");
			strQuery.append("  from cmm0020 g,cmm0020 f,cmm0040 d,cmm0030 b,cmr1000 a            \n");
			strQuery.append(" where a.cr_acptno=?                                                \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_editor=d.cm_userid            \n");
			strQuery.append("   and f.cm_macode='REQUEST' and f.cm_micode=a.cr_qrycd              \n");
			strQuery.append("   and g.cm_macode='REQPASS' and g.cm_micode=a.cr_passok            \n");

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
				else if (rs.getString("cr_gyuljae").equals("1")){
					rst.put("prcdate","우선적용 중");
				}
				rst.put("cr_gyuljae",rs.getString("cr_gyuljae"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_dept")+"  "+rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_passok",rs.getString("cr_passok"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("reqpass",rs.getString("reqpass"));
				rst.put("analsw", "N");
				rst.put("srcview", "Y");
				if (rs.getString("cr_qrycd").equals("03") && rs.getString("prcdate") != null)
					rst.put("srcview", "N");
				if (rs.getString("cr_docno") != null){
					rst.put("reqgbn", rs.getString("reqgbn") + " ("+ rs.getString("cr_docno") + ")");
				}
				else{
					rst.put("reqgbn", rs.getString("reqgbn"));
				}

				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				if (rs.getString("cr_prjno") != null && rs.getString("cr_prjno") != ""){
					rst.put("cr_prjno",rs.getString("cr_prjno"));
					rst.put("cr_devno",rs.getString("cr_devno"));
				}else{
					rst.put("cr_prjno","");
					rst.put("cr_devno","");
				}
				if (rs.getString("cr_sayu") != null)
				   rst.put("cr_sayu",rs.getString("cr_sayu"));
				else{
					rst.put("passsub", "정상");
				}

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
					rst.put("endsw", "1");
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
					rst.put("updtsw3", "0");
					rst.put("log", "1");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("confname","반려");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
					rst.put("updtsw3", "0");
					rst.put("log", "1");
				} else {
					String strPrcSw = "0";
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
						strPrcSw = "1";
					} else {
						rst.put("prcsw", "0");
						strPrcSw = "0";

						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmr9900                   \n");
						strQuery.append(" where cr_acptno=? and cr_locat<>'00'              \n");
						strQuery.append("   and cr_teamcd='1' and cr_team='SYSUP'           \n");
						strQuery.append("   and cr_status='9'                               \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, AcptNo);
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs2.getInt("cnt")==0) rst.put("srcview", "N");
						}
						rs2.close();
						pstmt2.close();
					}
					rst.put("endsw", "0");

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
					rst.put("updtsw1", rsconf.get(0).get("updtsw1"));
					rst.put("updtsw2", rsconf.get(0).get("updtsw2"));
					rst.put("updtsw3", rsconf.get(0).get("updtsw3"));
					rst.put("errtry", rsconf.get(0).get("errtry"));
					rst.put("log", rsconf.get(0).get("log"));
					rst.put("sttry", rsconf.get(0).get("sttry"));
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

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1030               \n");
				strQuery.append(" where cr_acptno=?                                \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
		        ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0){
						rst.put("befjob","1");
					}
					else{
						rst.put("befjob","0");
					}
				}
				rs2.close();
				pstmt2.close();

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1010 a,cmm0036 b   \n");
				strQuery.append(" where a.cr_acptno=? and a.cr_status<>'3'         \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd                      \n");
				strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                    \n");
				strQuery.append("   and substr(b.cm_info,34,1)='1'                 \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0) rst.put("analsw","Y");
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
			ecamsLogger.error("## Cmr0558.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0558.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0558.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0558.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmr0558.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	public Object[] JoinSysBace(String AcptNo) throws SQLException, Exception {
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
			strQuery.append("select CR_ACPTNO,CR_EDITOR,CR_STATUS,CR_JOBGBN,CR_SYSCD,CR_SYSMSG   \n");
			strQuery.append("from cmr1560										\n");
			strQuery.append(" where cr_acptno=? 						         \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()){
	        	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
            	rst.put("CR_EDITOR", rs.getString("CR_EDITOR"));
				rst.put("CR_STATUS", rs.getString("CR_STATUS"));
				rst.put("CR_JOBGBN", rs.getString("CR_JOBGBN"));
				rst.put("CR_SYSCD", rs.getString("CR_SYSCD"));
				rst.put("CR_SYSMSG", rs.getString("CR_SYSMSG"));
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
			ecamsLogger.error("## Cmr0558.JoinSysBace() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0558.JoinSysBace() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0558.JoinSysBace() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0558.JoinSysBace() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chgVolPath() method statement

	public Object[] JoinSysJob(String AcptNo) throws SQLException, Exception {
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
			strQuery.append("select CR_ACPTNO,CR_SEQNO,CR_JOBCD,CR_JOBNAME   \n");
			strQuery.append("from cmr1561										\n");
			strQuery.append(" where cr_acptno=? 						         \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()){
	        	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
            	rst.put("CR_SEQNO", rs.getString("CR_SEQNO"));
				rst.put("CR_JOBCD", rs.getString("CR_JOBCD"));
				rst.put("CR_JOBNAME", rs.getString("CR_JOBNAME"));

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
			ecamsLogger.error("## Cmr0558.JoinSysJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0558.JoinSysJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0558.JoinSysJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0558.JoinSysJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chgVolPath() method statement

	public Object[] JoinSysSvr(String AcptNo) throws SQLException, Exception {
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
			strQuery.append("select CR_ACPTNO,CR_SEQNO,CR_SVRCD,CR_HOSTNAME,CR_SVRIP,CR_SVRUSR,CR_SVRPWD   \n");
			strQuery.append("from cmr1562										\n");
			strQuery.append(" where cr_acptno=? 						         \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()){
	        	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
            	rst.put("CR_SEQNO", rs.getString("CR_SEQNO"));
				rst.put("CR_SVRCD", rs.getString("CR_SVRCD"));
				rst.put("CR_HOSTNAME", rs.getString("CR_HOSTNAME"));
				rst.put("CR_SVRIP", rs.getString("CR_SVRIP"));
				rst.put("CR_SVRUSR", rs.getString("CR_SVRUSR"));
				rst.put("CR_SVRPWD", rs.getString("CR_SVRPWD"));

				strQuery.setLength(0);
				strQuery.append("select cm_codename   \n");
				strQuery.append("from cmm0020									\n");
				strQuery.append(" where cm_macode='RIMSSVR'			         \n");
				strQuery.append(" and cm_micode=?					         \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, rs.getString("CR_SVRCD"));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if(rs2.next()){
					rst.put("cm_svrname", rs2.getString("cm_codename"));
				}

				rs2.close();
				pstmt2.close();

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
			ecamsLogger.error("## Cmr0558.JoinSysSvr() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0558.JoinSysSvr() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0558.JoinSysSvr() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0558.JoinSysSvr() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chgVolPath() method statement

	public Object[] JoinSysRsrc(String AcptNo) throws SQLException, Exception {
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
			strQuery.append("select CR_ACPTNO,CR_SEQNO,CR_RSRCCD,CR_VOLPATH   \n");
			strQuery.append("from cmr1563										\n");
			strQuery.append(" where cr_acptno=? 						         \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()){
	        	rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
            	rst.put("CR_SEQNO", rs.getString("CR_SEQNO"));
				rst.put("CR_RSRCCD", rs.getString("CR_RSRCCD"));
				rst.put("CR_VOLPATH", rs.getString("CR_VOLPATH"));

				strQuery.setLength(0);
				strQuery.append("select cm_codename   \n");
				strQuery.append("from cmm0020									\n");
				strQuery.append(" where cm_macode='JAWON'			         \n");
				strQuery.append(" and cm_micode=?					         \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, rs.getString("CR_RSRCCD"));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if(rs2.next()){
					rst.put("cm_rsrcname", rs2.getString("cm_codename"));
				}

				rs2.close();
				pstmt2.close();

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
			ecamsLogger.error("## Cmr0558.JoinSysRsrc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0558.JoinSysRsrc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0558.JoinSysRsrc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0558.JoinSysRsrc() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chgVolPath() method statement

	public String updtConfirm(String AcptNo,String UserId,String conMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("update cmr1560 set cr_prcrst='0000',  \n");
			strQuery.append("       cr_prcuser=?,             \n");
			strQuery.append("       cr_prcdate=SYSDATE,       \n");
			strQuery.append("       cr_status='9'             \n");
			strQuery.append(" where cr_acptno=?               \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserId);
			pstmt.setString(2, AcptNo);
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
			ecamsLogger.error("## Cmr0558.updtConfirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0558.updtConfirm() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0558.updtConfirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0558.updtConfirm() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0558.updtConfirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtConfirm() method statement

}//end of Cmr0558 class statement