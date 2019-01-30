/*****************************************************************************************
	1. program ID	: Cmr0650.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmr;

//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

//import app.common.AutoSeq;
import app.common.LoggableStatement;

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
public class Cmr2650{


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
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		Object[] returnObjectArray    = null;

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append(" select a.*,b.cm_username,d.cm_codename as name2,\n");
			strQuery.append(" substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,\n");
			strQuery.append("        to_char(a.CR_ACPTDATE, 'yyyy/mm/dd hh24:mi:ss') acptdate,			\n");
			strQuery.append("        to_char(a.CR_PRCDATE, 'yyyy/mm/dd hh24:mi:ss')  prcdate,			\n");
			strQuery.append("        to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,					\n");
			strQuery.append("        a.cr_cnclstep,a.cr_retryyn											\n");
			strQuery.append("   from cmr1000 a,cmm0040 b,cmm0020 d           \n");
			strQuery.append("  where a.cr_acptno=?                           \n");
			strQuery.append("    and a.cr_editor=b.cm_userid                 \n");
			strQuery.append("    and d.cm_macode='REQUEST'                   \n");
			strQuery.append("    and substr(a.cr_acptno,5,2)=d.cm_micode     \n");

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
				//rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				//rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cm_username",rs.getString("cm_username"));
				//rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				//rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				//rst.put("analsw", "N");

				rst.put("cr_acptdate" ,rs.getString("CR_ACPTDATE"));  //요청일자

				rst.put("cm_codename", rs.getString("name2"));
				rst.put("cr_passcd", rs.getString("cr_passcd"));//제목

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
					String strPrcSw = "0";
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

					rst.put("cr_confname", rsconf.get(0).get("confname"));
				}
				rs.close();
				pstmt.close();

				/*strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1001               \n");
				strQuery.append(" where cr_acptno=?                                \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				if (rs.next()){
					if (rs.getInt("cnt") > 0) rst.put("file","1");
					else rst.put("file","0");
				}
				rs.close();
				pstmt.close();*/
			}//end of while-loop statement
			rsval.add(rst);
			rst = null;
			conn.close();

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2650.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2650.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2650.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2650.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)  returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2650.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public Object[] getProgList(String AcptNo, String gbn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray	  = null;
		String 			  gbnname 	  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append(" select a.cd_gbnname,a.cd_detname,a.cd_subname,b.*   \n");
			strQuery.append("   from cmd1600 a,cmr2600 b                          \n");
			strQuery.append("  where b.cr_acptno=?                                \n");
			strQuery.append("    and a.cd_acptdt>=b.cr_reqday                     \n");
			strQuery.append("    and to_char(a.cd_creatdt,'yyyymmdd')<=b.cr_reqday\n");
			strQuery.append("    and a.cd_gbncd=b.cr_gbncd                        \n");
			strQuery.append("    and a.cd_detcd=b.cr_detcd                        \n");
			if(gbn.equals("1")){
			   strQuery.append(" AND SUBSTR(a.CD_GBNNAME,1,3) <> 'OP간'           \n");
			}else{
			   strQuery.append(" AND SUBSTR(a.CD_GBNNAME,1,3) = 'OP간'            \n");
			}

			strQuery.append("  order by a.cd_gbncd,a.cd_detcd                     \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        while (rs.next())
	        {
	        	rst = new HashMap<String, String>();
	        	if(gbn.equals("1")){
					if(rs.getString("cd_gbnname").equals(gbnname) || rs.getString("cd_gbnname") == gbnname){
						rst.put("cd_gbnname", "");
					}else{
						rst.put("cd_gbnname", rs.getString("cd_gbnname"));
					}
					gbnname = rs.getString("cd_gbnname");

					if(!rs.getString("CR_RSTSTA").split(",")[0].equals("") || rs.getString("CR_RSTSTA")!=null){
		        		rst.put("cr_rststa1", rs.getString("CR_RSTSTA").split(",")[0]);
		        		rst.put("cr_rststa2", rs.getString("CR_RSTSTA").split(",")[1]);
		        		rst.put("cr_rststa3", rs.getString("CR_RSTSTA").split(",")[2]);
		        		rst.put("cr_rststa4", rs.getString("CR_RSTSTA").split(",")[3]);
		        		rst.put("cr_rststa5", rs.getString("CR_RSTSTA").split(",")[4]);
		        		rst.put("cr_rststa6", rs.getString("CR_RSTSTA").split(",")[5]);
		        		rst.put("cr_rststa7", rs.getString("CR_RSTSTA").split(",")[6]);
		        		rst.put("cr_rststa8", rs.getString("CR_RSTSTA").split(",")[7]);
		        		rst.put("cr_rststa9", rs.getString("CR_RSTSTA").split(",")[8]);
		        		rst.put("cr_rststa10", rs.getString("CR_RSTSTA").split(",")[9]);
		        		rst.put("cr_rststa11", rs.getString("CR_RSTSTA").split(",")[10]);
		        		rst.put("cr_rststa12", rs.getString("CR_RSTSTA").split(",")[11]);
		        	}else{
		        		rst.put("cr_rststa1", "");
		        		rst.put("cr_rststa2", "");
		        		rst.put("cr_rststa3", "");
		        		rst.put("cr_rststa4", "");
		        		rst.put("cr_rststa5", "");
		        		rst.put("cr_rststa6", "");
		        		rst.put("cr_rststa7", "");
		        		rst.put("cr_rststa8", "");
		        		rst.put("cr_rststa9", "");
		        		rst.put("cr_rststa10", "");
		        		rst.put("cr_rststa11", "");
		        		rst.put("cr_rststa12", "");
		        	}
	        	}else{
	        		if(rs.getString("cd_gbnname").equals(gbnname) || rs.getString("cd_gbnname") == gbnname){
						rst.put("cd_gbnname", "");
					}else{
						rst.put("cd_gbnname", rs.getString("cd_gbnname"));
					}
					gbnname = rs.getString("cd_gbnname");

					if(rs.getString("CR_RSTSTA")==null){
		        		rst.put("cr_rststa13", "");
		        		rst.put("cr_rststa14", "");
		        	}else{
		        		rst.put("cr_rststa13", rs.getString("CR_RSTSTA").split(",")[0]);
		        		rst.put("cr_rststa14", rs.getString("CR_RSTSTA").split(",")[1]);
		        	}
	        	}
	        	rst.put("cd_detname", rs.getString("cd_detname"));
	        	rst.put("cd_subname", rs.getString("cd_subname"));

	        	rst.put("cr_reqday", rs.getString("CR_REQDAY").substring(0,4)+"/"+rs.getString("CR_REQDAY").substring(4,6)+"/"+rs.getString("CR_REQDAY").substring(6,8));// 근무일
	        	rst.put("cr_sendusr", rs.getString("CR_SENDUSR"));//인계자
	        	rst.put("cr_recvusr", rs.getString("CR_RECVUSR"));//인수자
	        	if(rs.getString("CR_JOBGBN").equals("0")){
	        		rst.put("cr_jobgbn", "주간");
	        	}else{
	        		rst.put("cr_jobgbn", "야간");
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

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2650.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2650.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2650.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2650.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null) returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2650.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement



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
		String            strQry     = "";
		String            strErMsg   = "";
		int               cbCnt       = 0;
		int               edCnt       = 0;
		int               rcCnt       = 0;
		boolean           prcSw       = false;

		try {
			strQry = AcptNo.substring(4,6);
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
				if (rs.getString("cr_prcsw").equals("Y")) prcSw = true;

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

			if (AcptNo.substring(4,5).equals("0") || AcptNo.substring(4,5).equals("1")) {
				strQuery.setLength(0);
				strQuery.append("select cr_prcsys,count(*) cnt from cmr1011       \n");
				strQuery.append(" where cr_acptno=?                               \n");
				strQuery.append("   and cr_prcsys in ('SYSCB','SYSED','SYSRC')    \n");
				strQuery.append(" group by cr_prcsys                              \n");
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				while (rs.next()){
					if (rs.getString("cr_prcsys").equals("SYSRC")) rcCnt = rs.getInt("cnt");
					if (rs.getString("cr_prcsys").equals("SYSED")) edCnt = rs.getInt("cnt");
					if (rs.getString("cr_prcsys").equals("SYSCB")) cbCnt = rs.getInt("cnt");
				}
				if (rcCnt > 0 || prcSw == true) {
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
				}else if (edCnt > 0 || prcSw == true) {
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
				}else if (cbCnt > 0) {
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "1");
				} else {
					rst.put("updtsw1", "1");
					rst.put("updtsw2", "1");
				}
	            rs.close();
	            pstmt.close();

				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmr9900                 \n");
				strQuery.append(" where cr_acptno=?                               \n");
				strQuery.append("   and (cr_teamcd not in ('1','2') or            \n");
				strQuery.append("        cr_team='SYSED' or cr_team='SYSRC'       \n");
				strQuery.append("        or cr_prcsw='Y')                         \n");
				strQuery.append("   and cr_status<>'0'                            \n");
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				if (rs.next()){
					if (rs.getInt("cnt") == 0) rst.put("updtsw3", "1");
					else rst.put("updtsw3", "0");
				}
	            rs.close();
	            pstmt.close();

	            if (strTeamCd.equals("1")) {
	            	rst.put("log", "1");
	            	if (!strTeam.equals("SYSCN")) {
		            	strQuery.setLength(0);
		            	strQuery.append("select b.cm_svrip,b.cm_svrname                   \n");
		            	strQuery.append("  from cmm0038 c,cmm0031 b,cmr1010 a             \n");
		            	strQuery.append(" where a.cr_acptno=?                             \n");
		            	strQuery.append("   and a.cr_syscd=b.cm_syscd                     \n");
		            	strQuery.append("   and nvl(b.cm_agent,'OK')='ER'                 \n");
		            	strQuery.append("   and b.cm_svrcd=?                              \n");
		            	strQuery.append("   and b.cm_syscd=c.cm_syscd                     \n");
		            	strQuery.append("   and b.cm_svrcd=c.cm_svrcd                     \n");
		            	strQuery.append("   and b.cm_seqno=c.cm_seqno                     \n");
		            	strQuery.append("   and a.cr_syscd=c.cm_syscd                     \n");
		            	strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                   \n");
		            	strQuery.append(" group by b.cm_svrip,b.cm_svrname                \n");
		            	pstmt = conn.prepareStatement(strQuery.toString());
		    			//pstmt = new LoggableStatement(conn,strQuery.toString());
		    			pstmt.setString(1, AcptNo);
		    			if (strTeam.equals("SYSDN") || strTeam.equals("SYSDNC")  || strTeam.equals("SYSPF") || strTeam.equals("SYSUP")
		    					|| strTeam.equals("SYSAPF") || strTeam.equals("SYSAUP"))
		    				pstmt.setString(2, "01");
		    			else if (strQry.equals("03")) {
		    				if (strTeam.equals("SYSCB") || strTeam.equals("SYSFT"))
		    					pstmt.setString(2, "13");
		    				else pstmt.setString(2, "15");
		    			} else {
		    				if (strTeam.equals("SYSCB") || strTeam.equals("SYSFT"))
		    					pstmt.setString(2, "03");
		    				else pstmt.setString(2, "05");
		    			}
		    	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    	        rs = pstmt.executeQuery();
		    			while (rs.next()){
		    				if (strErMsg.length() == 0) {
		    					strErMsg = "서버장애- ";
		    				} else strErMsg = strErMsg + ",";
		    				strErMsg = strErMsg + rs.getString("cm_svrip") + "("+rs.getString("cm_svrname")+")";
		    			}
		    			rs.close();
		    			pstmt.close();

		    			rst.put("ermsg", strErMsg);
	            	}
	            	boolean findSw = false;

	            	strQuery.setLength(0);
	    			strQuery.append("select count(*) cnt from cmr1011                 \n");
	    			strQuery.append(" where cr_acptno=? and cr_prcsys=?               \n");
	    			strQuery.append("   and nvl(cr_prcrst,'0000')<>'0000'             \n");
	    			strQuery.append("   and nvl(cr_prcrst,'RTRY')<>'RTRY'             \n");
	    			strQuery.append("   and cr_serno in (select cr_serno from cmr1010 \n");
	    			strQuery.append("                     where cr_acptno=?           \n");
	    			strQuery.append("                       and cr_prcdate is null)   \n");
	    			pstmt = conn.prepareStatement(strQuery.toString());
	    			//pstmt = new LoggableStatement(conn,strQuery.toString());
	    			pstmt.setString(1, AcptNo);
	    			pstmt.setString(2, strTeam);
	    			pstmt.setString(3, AcptNo);
	    	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	        rs = pstmt.executeQuery();
	    			if (rs.next()){
	    				if (rs.getInt("cnt") > 0) rst.put("errtry", "1");
	    				else {
	    					rst.put("errtry", "0");

	    					if (strErMsg == null || strErMsg == "") {
		    					strQuery.setLength(0);
		    					strQuery.append("select b.cm_codename from cmr1010 a,cmm0020 b \n");
		    					strQuery.append(" where a.cr_acptno=?                          \n");
		    					strQuery.append("  and a.cr_prcdate is null                    \n");
		    					strQuery.append("   and nvl(a.cr_putcode,'0000')<>'0000'       \n");
		    					strQuery.append("   and nvl(a.cr_putcode,'RTRY')<>'RTRY'       \n");
		    					strQuery.append("   and b.cm_macode='ERRACCT'                  \n");
		    					strQuery.append("   and b.cm_micode=nvl(a.cr_putcode,'0000')   \n");
		    					pstmt2 = conn.prepareStatement(strQuery.toString());
		    					pstmt2.setString(1, AcptNo);
		    					rs2 = pstmt2.executeQuery();
		    					if (rs2.next()) {
		    						rst.put("ermsg", rs2.getString("cm_codename"));
		    					}
		    					rs2.close();
		    					pstmt2.close();
	    					}
	    					findSw = true;
	    				}
	    			}
	                rs.close();
	                pstmt.close();
	                if (strTeam.equals("SYSPDN") || strTeam.equals("SYSPUP")) {
	                	strQuery.setLength(0);
		    			strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b     \n");
		    			strQuery.append(" where a.cr_acptno=? and a.cr_prcdate is null    \n");
		    			strQuery.append("   and nvl(a.cr_putcode,'ERR1')<>'0000'          \n");
		    			strQuery.append("   and a.cr_syscd=b.cm_syscd                     \n");
		    			strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                   \n");
		    			if (strTeam.equals("SYSDN")) {
		    				strQuery.append("AND substr(b.cm_info,3,1)='0'                \n");
		    			} else {
		    				strQuery.append("and substr(b.cm_info,24,1)='1'               \n");
		    			}
		    			pstmt = conn.prepareStatement(strQuery.toString());
		    			pstmt = new LoggableStatement(conn,strQuery.toString());
		    			pstmt.setString(1, AcptNo);
		    	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    	        rs = pstmt.executeQuery();
		    			if (rs.next()){
		    				if (rs.getInt("cnt") > 0) rst.put("errtry", "1");
		    			}
		    			rs.close();
		    			pstmt.close();
	                }
	                //ecamsLogger.debug("+++++++++step start+++");
	                if (findSw == true && CnclStep != null && CnclStep != "" && !RetryYn.equals("Y")) {
	                	strQuery.setLength(0);
	                    strQuery.append("select sign(to_date(?,'yyyymmddhh24miss')  \n");
	                    strQuery.append("       - max(cr_prcdate)) as diff          \n");
	                    strQuery.append("  from cmr1011                             \n");
	                    strQuery.append(" where cr_acptno=? and cr_prcsys=?         \n");
	                    strQuery.append("   and cr_prcdate is not null              \n");
		                pstmt = conn.prepareStatement(strQuery.toString());
	                    pstmt.setString(1, CnclDat);
	                    pstmt.setString(2, AcptNo);
	                    pstmt.setString(3, strTeam);
	                    rs = pstmt.executeQuery();
	                    if (rs.next()) {
	                    	//ecamsLogger.debug("+++++++++step return+++"+Integer.toString(rs.getInt("diff")));
	                    	if (rs.getInt("diff") > 0) {
	                    		rst.put("sttry", "1");
	                    		//ecamsLogger.debug("++++++++++sttry1++++++++");
	                    	}
	                    	else {
	                    			rst.put("sttry", "0");
	                    			//ecamsLogger.debug("++++++++++step2++++++++");
	                    	}
	                    }
	                    rs.close();
	                    pstmt.close();
	                } else rst.put("sttry", "0");
	            }
				else {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt                                                 \n");
					strQuery.append("  from cmr1011                                                      \n");
					strQuery.append(" where cr_acptno=?                                                  \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, AcptNo);
			        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();
					if (rs.next()){
						if (rs.getInt("cnt") > 0) rst.put("log", "1");
						else rst.put("log", "0");
					}
		            rs.close();
		            pstmt.close();
				}
			} else {
				rst.put("updtsw1", "0");
				rst.put("updtsw2", "0");
				rst.put("log", "0");
				rst.put("errtry", "0");
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

            rsval.add(rst);
            rst = null;

    		return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr2650.confLocat_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2650.confLocat_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr2650.confLocat_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2650.confLocat_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of confLocat_conn() method statement


}