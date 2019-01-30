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
public class Cmr1050{


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
			ecamsLogger.error("## Cmr1050.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1050.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1050.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1050.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmr1050.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public ArrayList<HashMap<String, String>> getProgList(String AcptNo, String Syscd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append(" select a.CD_CHKTIME1,CD_CHKTIME2,CD_CHKITEM,CD_CHKSTEP,CD_CHKPGM,CD_CHKMETH,      \n");
			strQuery.append("        a.cd_detjob,                                                               \n");
			strQuery.append("        b.cr_jobcd,b.cr_detjob,b.cr_chkseq,b.cr_chkret,b.cr_chketc,b.cr_runday     \n");
			strQuery.append("   from cmd0050 a, cmr2000 b                                                       \n");
			strQuery.append("  where b.cr_acptno=?                                                              \n");
			strQuery.append("    and a.cd_syscd=?                                                            	\n");
			strQuery.append("    and a.cd_jobcd=b.cr_jobcd                                                      \n");
			strQuery.append("    and a.cd_detjob=b.cr_detjob                                                    \n");
			strQuery.append("    and a.cd_seqno=b.cr_chkseq                                                     \n");
			strQuery.append("  order by a.cd_seqno                                                              \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, Syscd);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
	        	rst = new HashMap<String, String>();
	        	if(!rs.getString("CD_CHKTIME1").equals("") || rs.getString("CD_CHKTIME1")!=null){
	        		rst.put("chktime", rs.getString("CD_CHKTIME1")+"~"+rs.getString("CD_CHKTIME2"));
	        	}else{
	        		rst.put("chktime", "");
	        	}
	        	rst.put("chkitem", rs.getString("CD_CHKITEM"));
	        	rst.put("chkstep", rs.getString("CD_CHKSTEP"));
	        	rst.put("chkpgm", rs.getString("CD_CHKPGM"));
	        	rst.put("chkmeth", rs.getString("CD_CHKMETH"));
	        	if(rs.getString("CR_CHKRET").equals("1")){
	        		rst.put("chkret", "정상");
	        	}else if(rs.getString("CR_CHKRET").equals("g")){
	        		rst.put("chkret", "비정상");
	        	}else{
	        		rst.put("chkret", "");
	        	}
	        	rst.put("cd_detjob",rs.getString("cd_detjob"));
	        	rst.put("chketc", rs.getString("CR_CHKETC"));
	        	rst.put("runday", rs.getString("CR_RUNDAY").substring(0,4)+"/"+rs.getString("CR_RUNDAY").substring(4,6)+"/"+rs.getString("CR_RUNDAY").substring(6,8));

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
			ecamsLogger.error("## Cmr1050.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr1050.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr1050.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr1050.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr1050.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement



}