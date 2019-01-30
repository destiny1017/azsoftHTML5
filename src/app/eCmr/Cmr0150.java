/*****************************************************************************************
	1. program ID	: Cmr2200.java
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
//import app.common.LoggableStatement;
import app.common.LoggableStatement;
import app.common.SysInfo;
import app.common.SystemPath;
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
public class Cmr0150{


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
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>  rsconf = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String           strPrcSw       = "N";
		Object[] returnObjectArray    = null;

		try {

			conn = connectionContext.getConnection();

			strQuery.append("select substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,   \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') acptdate,        \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') prcdate,          \n");
			strQuery.append("       a.cr_acptno,a.cr_syscd,a.cr_editor,a.cr_sayu,a.cr_status,    \n");
			strQuery.append("       to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,           \n");
			strQuery.append("       a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,a.cr_teamcd,        \n");
			strQuery.append("       a.cr_qrycd,b.cm_sysmsg,c.cm_username,a.cr_emgcd,			 \n");//,d.cm_codename emgcd
			//strQuery.append("       a.cr_dept, a.cr_reqdate, a.cr_reqdoc, a.cr_etc, a.cr_reqtitle,\n");
			strQuery.append("       a.cr_emrgb, a.cr_emrcd, a.cr_emrmsg, a.cr_passcd, a.cr_sayucd, a.cr_itsmid, a.cr_itsmtitle	\n");
			strQuery.append("  from cmr1000 a,cmm0030 b,cmm0040 c                     \n");//,cmm0020 d
			strQuery.append(" where a.cr_acptno=?                                                \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_editor=c.cm_userid            \n");
			//strQuery.append("   and a.cr_emgcd=d.cm_micode and d.cm_macode='REQCD' \n");

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
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("cr_sayucd", rs.getString("cr_sayucd")); //사유구분
				if (rs.getString("cr_itsmid") != null) rst.put("cr_itsmid", rs.getString("cr_itsmid")); //cr_itsmid
				if (rs.getString("cr_itsmtitle") != null) rst.put("cr_itsmtitle", rs.getString("cr_itsmtitle")); //cr_itsmtitle
				if (rs.getString("cr_itsmid") != null) rst.put("cr_itsm", "["+rs.getString("cr_itsmid")+"] "+rs.getString("cr_itsmtitle")); //cr_itsm
				/*
				if (rs.getString("CR_DEPT") != null) rst.put("cr_dept", rs.getString("CR_DEPT"));
				if (rs.getString("CR_REQDATE") != null) rst.put("cr_reqdate", rs.getString("CR_REQDATE"));
				if (rs.getString("CR_REQDOC") != null) rst.put("cr_reqdoc", rs.getString("CR_REQDOC"));
				if (rs.getString("CR_REQTITLE") != null) rst.put("cr_rettitle", rs.getString("CR_REQTITLE"));
				if (rs.getString("CR_ETC") != null) rst.put("cr_etc", rs.getString("CR_ETC"));
				*/

				if (rs.getString("CR_PASSCD") != null) rst.put("cr_passcd", rs.getString("CR_PASSCD"));
				if (rs.getString("cr_sayu") != null) rst.put("cr_sayu", rs.getString("cr_sayu"));

				if(rs.getString("cr_emgcd") != null){
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020 \n");
					strQuery.append(" where cm_micode=? and cm_macode='REQCD' \n");
			        pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_emgcd"));
			        rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("emgcd", rs2.getString("cm_codename"));
					}else{
						rst.put("emgcd", "변경구분없음");
					}
					rs2.close();
					pstmt2.close();
				}else{
					rst.put("emgcd", "");
				}

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

					//rsconf = confLocat(AcptNo,strPrcSw,rs.getString("cr_cnclstep"),rs.getString("cncldat"),rs.getString("cr_retryyn"));
					rsconf = confLocat_conn(AcptNo,strPrcSw,rs.getString("cr_cnclstep"),rs.getString("cncldat"),rs.getString("cr_retryyn"),conn);

					rst.put("signteam", rsconf.get(0).get("signteam"));
					rst.put("signteamcd", rsconf.get(0).get("signteamcd"));
					rst.put("confusr", rsconf.get(0).get("confusr"));
					rst.put("cr_prcsw", rsconf.get(0).get("prcsw"));
					rst.put("cr_confname", rsconf.get(0).get("confname"));
					rst.put("errtry", rsconf.get(0).get("errtry"));
					rst.put("updtsw", rsconf.get(0).get("updtsw"));
					rst.put("log", rsconf.get(0).get("log"));
					rst.put("sttry", rsconf.get(0).get("sttry"));
					rst.put("ermsg", rsconf.get(0).get("ermsg"));
					rsconf = null;
				}
				rs.close();
				pstmt.close();

				strQuery.setLength(0);
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
				pstmt.close();


				/**
				 * 영향분석 사용 여부 체크 시작
				 */
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmr1010 a, cmm0036 b \n");
				strQuery.append(" where a.cr_acptno=? and a.cr_syscd = b.cm_syscd \n");
				strQuery.append("   and a.cr_rsrccd = b.cm_rsrccd and substr(b.cm_info, 34,1) = '1' \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, AcptNo);
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0){
						rst.put("analsw","Y");
					} else{
						rst.put("analsw","N");
					}
				}
				rs2.close();
				pstmt2.close();
				/**
				 * 영향분석 사용 여부 체크 끝
				 */

				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0150.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0150.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)  returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	public Object[] getProgList(String UserId,String AcptNo,String chkYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		PreparedStatement pstmt3       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		ResultSet         rs3         = null;
		String            strDevHome  = null;
		ArrayList<HashMap<String, String>>		  svrList	  = new ArrayList<HashMap<String, String>>();
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			
			conn = connectionContext.getConnection();

			String            strVolPath = "";
			String            strDirPath = "";
			
			//로컬 프로그램에 필요한 정보 조회 시작
			strQuery.setLength(0);
			strQuery.append("select a.cd_syscd,a.cd_devhome from cmd0300 a,cmr1000 b \n");
			strQuery.append(" where a.cd_syscd = b.cr_syscd   \n");
			strQuery.append("   and a.cd_userid = b.cr_editor \n");
			strQuery.append("   and b.cr_acptno = ? \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, AcptNo);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				strDevHome = rs.getString("cd_devhome")+"\\";
				svrList = new SysInfo().getHomePath_conn(rs.getString("cd_syscd"), conn);
			}
			rs.close();
			pstmt.close();
			//로컬 프로그램에 필요한 정보 조회 끝
			
			strQuery.setLength(0);
			strQuery.append("select a.cr_rsrcname,a.cr_serno,a.cr_putcode,a.cr_rsrccd,a.cr_editcon,  \n");
			strQuery.append("       a.cr_status,a.cr_confno,a.cr_itemid,a.cr_baseitem,a.cr_dsncd,    \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,a.cr_editor,  \n");
			strQuery.append("       a.cr_jobcd,a.cr_syscd,                                           \n");
			strQuery.append("       a.cr_version,b.cm_dirpath,c.cm_codename,e.cm_jobname,d.cm_info,  \n");
		    strQuery.append("       nvl(a.cr_pgmtype,'02') cr_pgmtype,f.cm_codename pgmtype          \n");
		    strQuery.append("       ,replace(b.cm_dirpath, (select distinct aa.cm_volpath            \n");
		    strQuery.append("                                 from cmm0031 aa,cmm0030 bb,cmm0038 cc  \n");
		    strQuery.append("                                where aa.cm_svrcd = bb.cm_dirbase       \n");
		    strQuery.append("                                  and cc.cm_svrcd = bb.cm_dirbase       \n");
		    strQuery.append("                                  and cc.cm_svrcd = aa.cm_svrcd         \n");
		    strQuery.append("                                  and cc.cm_seqno = aa.cm_seqno         \n");
		    strQuery.append("                                  and cc.cm_rsrccd = a.cr_rsrccd        \n");
		    strQuery.append("                                  and aa.cm_syscd = a.cr_syscd          \n");
		    strQuery.append("                                  and aa.cm_syscd = bb.cm_syscd         \n");
		    strQuery.append("                                  and cc.cm_syscd = bb.cm_syscd         \n");
		    strQuery.append("                                  and aa.cm_closedt is null             \n");
		    strQuery.append("                                  and bb.cm_closedt is null),           \n");
		    strQuery.append("                              (select replace(cm_volpath, '##USER##'    \n");
		    strQuery.append("                                                    ,'k'||a.cr_editor)  \n");
		    strQuery.append("                                 from cmm0038                           \n");
		    strQuery.append("                                where cm_syscd = a.cr_syscd             \n");
		    strQuery.append("                                  and cm_rsrccd = a.cr_rsrccd           \n");
		    strQuery.append("                                  and cm_svrcd = '01')                  \n");
		    strQuery.append("                ) chkpath                                               \n");
			strQuery.append("  from cmm0020 f, cmm0102 e,cmm0036 d,cmm0070 b,cmm0020 c,cmr1010 a     \n");
			strQuery.append(" where a.cr_acptno=?                                                    \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd                  \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd                  \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd                \n");
			strQuery.append("   and a.cr_jobcd=e.cm_jobcd                                            \n");
			strQuery.append("   and f.cm_closedt is null                                             \n");
			strQuery.append("   and f.cm_macode='PGMTYPE' and nvl(a.cr_pgmtype,'02')=f.cm_micode     \n");
			strQuery.append(" order by a.cr_serno                                                    \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_confno",rs.getString("cr_confno"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_baseitem",rs.getString("cr_baseitem"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("chkpath", rs.getString("chkpath"));
				//20140828 neo. 로컬개발일 경우  로컬경로로 변경하는 작업 시작~
				if ( rs.getString("cm_info").substring(44, 45).equals("1") && strDevHome != null ) {
					for (int j=0;svrList.size()>j;j++) {
						if (svrList.get(j).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
							strVolPath = svrList.get(j).get("cm_volpath");
							strDirPath = rs.getString("cm_dirpath");
							if (strVolPath != null && strVolPath != "") {
								if(strDirPath.length() >= strVolPath.length()){
									if (strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
										rst.put("pcdir1", strDevHome + strDirPath.substring(strVolPath.length()).replace("/","\\"));
									}
								}
							} else {
								rst.put("pcdir1", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
							}
						}
					}
					rst.put("cm_dirpath",rst.get("pcdir1").replace("\\\\", "\\") );
					rst.put("chkpath", rst.get("cm_dirpath") );
				}
				//neo. 로컬개발일 경우  로컬경로로 변경하는 작업 끝~
				
				
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cr_pgmtype", rs.getString("cr_pgmtype"));
				rst.put("pgmtype", rs.getString("pgmtype"));
				

				if (rs.getString("cr_editor").equals(UserId)) rst.put("secusw", "Y");
				else {
					strQuery.setLength(0);
					strQuery.append("select count(*) as cnt from cmm0044       \n");
					strQuery.append(" where cm_userid=?                        \n");
					strQuery.append("   and cm_syscd=? and cm_jobcd=?          \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, UserId);
					pstmt2.setString(2, rs.getString("cr_syscd"));
					pstmt2.setString(3, rs.getString("cr_jobcd"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getInt("cnt") > 0) rst.put("secusw", "Y");
						else rst.put("secusw", "N");
					}
					rs2.close();
					pstmt2.close();
				}

				rst.put("cr_version",Integer.toString(rs.getInt("cr_version")));
				if (rs.getString("prcdate") != null) rst.put("prcdate", rs.getString("prcdate"));				rst.put("cr_serno",Integer.toString(rs.getInt("cr_serno")));
				if (rs.getString("cr_putcode") != null) rst.put("cr_putcode",rs.getString("cr_putcode"));
				if (rs.getString("cr_editcon") != null) rst.put("cr_editcon",rs.getString("cr_editcon"));
				if (!rs.getString("cr_status").equals("3")) {
					if (rs.getString("cr_putcode") != null && !rs.getString("cr_putcode").equals("0000") && !rs.getString("cr_putcode").equals("RTRY"))
						rst.put("ColorSw","5");
					else if (rs.getString("cr_itemid").equals(rs.getString("cr_baseitem")))  {
						strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmr1011                 \n");
						strQuery.append(" where cr_acptno=?                                  \n");
						strQuery.append("   and cr_serno in (select cr_serno from cmr1010    \n");
						strQuery.append("                     where cr_acptno=?              \n");
						strQuery.append("                       and cr_baseitem=?)           \n");
						strQuery.append("   and nvl(cr_putcode,'RTRY')<>'RTRY'               \n");
						strQuery.append("   and nvl(cr_putcode,'0000')<>'0000'               \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn,strQuery.toString());
						pstmt2.setString(1, AcptNo);
						pstmt2.setString(2, AcptNo);
						pstmt2.setString(3, rs.getString("cr_baseitem"));
				        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				        rs2 = pstmt2.executeQuery();
				        if (rs2.next()) {
				        	if (rs2.getInt("cnt") > 0) rst.put("ColorSw", "5");
				        	else if (rs.getString("cr_status").equals("9") || rs.getString("prcdate") != null) rst.put("ColorSw","0");
				        }
				        rs2.close();
				        pstmt2.close();
					} else if (rs.getString("cr_status").equals("9") || rs.getString("prcdate") != null) rst.put("ColorSw","9");
					else rst.put("ColorSw","0");
				} else rst.put("ColorSw","3");

				strQuery.setLength(0);
				strQuery.append("select cr_putcode from cmr1011                      \n");
				strQuery.append(" where cr_acptno=? and cr_serno=?                   \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
				pstmt2.setInt(2, rs.getInt("cr_serno"));
		        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
		        if (rs2.next()) {
		        	if (rs2.getString("cr_putcode") != null) {

		        		strQuery.setLength(0);
		        		strQuery.append("select cm_codename from cmm0020              \n");
						strQuery.append(" where cm_macode='ERRACCT' and cm_micode=?   \n");
						pstmt3 = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt3.setString(1, rs2.getString("cr_putcode"));
				        //ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
				        rs3 = pstmt3.executeQuery();
				        if (rs3.next()) {
				        	rst.put("prcrst",rs3.getString("cm_codename"));
				        } else {
				        	rst.put("prcrst",rs2.getString("cr_putcode"));
				        }
				        rs3.close();
				        pstmt3.close();
		        	}
		        	else
		        		rst.put("prcrst","처리중");
		        }
		        else
		        	rst.put("prcrst","미처리");
		        rs2.close();
		        pstmt2.close();

		        rst.put("diffacpt", "false");
		        strQuery.setLength(0);
		        strQuery.append("select count(*) cnt from cmr0021  \n");
		        strQuery.append(" where cr_itemid=?                \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
		        pstmt2.setString(1, rs.getString("cr_itemid"));
		        rs2 = pstmt2.executeQuery();
		        if (rs2.next()) {
		        	if (rs2.getInt("cnt")>1) rst.put("diffacpt", "true");
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
			rs3 = null;
			pstmt3 = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0150.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0150.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null) rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs3 != null)     try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement


	public String svrProc(String AcptNo,String prcCd,String prcSys) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		Runtime  run = null;
		Process p = null;
		SystemPath		  systemPath = new SystemPath();
		String  binpath;
		String[] chkAry;
		int		cmdrtn;

		try {
			binpath = systemPath.getTmpDir("15");

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = binpath+"/procck2";
			chkAry[2] = "ecams_acct";
			chkAry[3] = AcptNo;

			p = run.exec(chkAry);
			p.waitFor();

			cmdrtn = p.exitValue();
			if (cmdrtn > 0) {
				ecamsLogger.error(chkAry[0]+" " + chkAry[1] + " " + chkAry[2]);
				ecamsLogger.error(cmdrtn);
				return "2";
			}

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			if (prcCd.equals("Retry")) {
				strQuery.setLength(0);
				strQuery.append("delete cmr1011                                          \n");
				strQuery.append(" where cr_acptno=?                                      \n");
				strQuery.append("   and (cr_serno in (select cr_serno from cmr1010       \n");
				strQuery.append("                     where cr_acptno=?                  \n");
				strQuery.append("                       and cr_prcdate is null           \n");
				strQuery.append("                       and cr_status='0')               \n");
				strQuery.append("                 or cr_serno=0)                         \n");
				strQuery.append("   and cr_prcsys=?                                      \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, AcptNo);
				pstmt.setString(3, prcSys);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();

		        pstmt.close();

				strQuery.setLength(0);
				strQuery.append("update cmr1010                                              \n");
				strQuery.append("   set cr_putcode='',cr_pid='',cr_sysstep=0,cr_srccmp='Y'   \n");
				strQuery.append(" where cr_acptno=? and cr_prcdate is null                   \n");
				strQuery.append("   and cr_status='0'                                        \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1,AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();
			} else if (prcCd.equals("Sttry")) {
				strQuery.setLength(0);
				strQuery.append("update cmr1010 set cr_pid='',cr_srccmp='Y',cr_putcode=''    \n");
	            strQuery.append(" where cr_acptno=? and cr_status='0'                        \n");
	            strQuery.append("   and cr_serno not in (select cr_serno from cmr1011        \n");
	            strQuery.append("                         where cr_acptno=?                  \n");
	            strQuery.append("                           and cr_prcsys=?                  \n");
	            strQuery.append("                           and nvl(cr_prcrst,'NA')!='0000') \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,AcptNo);
	            pstmt.setString(3,prcSys);
	            pstmt.executeUpdate();
		        pstmt.close();
			} else {
				strQuery.setLength(0);
				strQuery.append("delete cmr1011                                             \n");
				strQuery.append(" where cr_acptno=?                                         \n");
				strQuery.append("   and cr_serno in (select cr_serno from cmr1010           \n");
				strQuery.append("                     where cr_acptno=?                     \n");
				strQuery.append("                       and cr_prcdate is null              \n");
				strQuery.append("                       and cr_status='0'                   \n");
				strQuery.append("                       and nvl(cr_putcode,'0000')<>'0000') \n");
				strQuery.append("   and cr_prcsys=?                                         \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2,AcptNo);
				pstmt.setString(3,prcSys);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();

				strQuery.setLength(0);
				strQuery.append("update cmr1010                                              \n");
				strQuery.append("   set cr_putcode='',cr_pid=''                              \n");
				strQuery.append(" where cr_acptno=? and cr_prcdate is null                   \n");
				strQuery.append("   and cr_status='0' and nvl(cr_putcode,'0000')<>'0000'     \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1,AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();

				strQuery.setLength(0);
				strQuery.append("update cmr1010                                              \n");
				strQuery.append("   set cr_srccmp='Y'                                        \n");
				strQuery.append(" where cr_acptno=? and cr_prcdate is null                   \n");
				strQuery.append("   and cr_status='0'                                        \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1,AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();
			}

			strQuery.setLength(0);
			strQuery.append("update cmr1000 set cr_notify='0',cr_retryyn='Y'             \n");
			strQuery.append(" where cr_acptno=?                                          \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
	        pstmt.close();

	        conn.commit();
	        conn.close();

	        rs = null;
	        pstmt = null;
	        conn = null;

	        return "0";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.svrProc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.svrProc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.svrProc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.putDeploy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of putDeploy() method statement

	public ArrayList<HashMap<String, String>> confLocat(String AcptNo,String PrcSw,String CnclStep,String CnclDat,String RetryYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		String            strTeamCd   = "";
		String            strTeam     = "";
		String            strQry     = "";
		String            strErMsg   = "";
		int               cbCnt       = 0;
		int               edCnt       = 0;
		int               rcCnt       = 0;
		boolean           prcSw       = false;

		rsval.clear();
		try {
			conn = connectionContext.getConnection();

			strQry = AcptNo.substring(4,6);
			strQuery.append("select cr_team,cr_teamcd,cr_confname,cr_prcsw,cr_confusr            \n");
			strQuery.append("  from cmr9900                                                      \n");
			strQuery.append(" where cr_acptno=? and cr_locat='00'                                \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("signteam", rs.getString("cr_team"));
				rst.put("signteamcd", rs.getString("cr_teamcd"));
				rst.put("confusr", rs.getString("cr_confusr"));
				rst.put("prcsw", rs.getString("cr_prcsw"));
				if (rs.getString("cr_prcsw").equals("Y")) prcSw = true;
				strTeamCd = rs.getString("cr_teamcd");
				strTeam = rs.getString("cr_team");
				if (rs.getString("cr_teamcd").equals("1")) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020             \n");
					strQuery.append(" where cm_macode='SYSGBN' and cm_micode=?   \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1,rs.getString("cr_team"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("confname", rs2.getString("cm_codename"));
					}
					rs2.close();
					pstmt2.close();
				}else if (rs.getString("cr_teamcd").equals("2") || rs.getString("cr_teamcd").equals("3")
						|| rs.getString("cr_teamcd").equals("6") || rs.getString("cr_teamcd").equals("7")
						|| rs.getString("cr_teamcd").equals("8")) {
					strQuery.setLength(0);
					strQuery.append("select cm_username from cmm0040             \n");
					strQuery.append(" where cm_userid=?                          \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1,rs.getString("cr_team"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("confname", "["+rs2.getString("cm_username") + "]님 결재대기 중");
					}
					rs2.close();
					pstmt2.close();
				}else {
					rst.put("confname", rs.getString("cr_confname"));
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
					if (rs.getString("cr_prcsys").equals("SYSED")) rcCnt = rs.getInt("cnt");
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
		            	strQuery.append(" where a.cr_acptno=? and a.cr_status='0'         \n");
		            	strQuery.append("   and a.cr_syscd=b.cm_syscd                     \n");
		            	strQuery.append("   and nvl(b.cm_agent,'OK')='ER'                 \n");
		            	strQuery.append("   and b.cm_svrcd=? and b.cm_closedt is null     \n");
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
		    				if (strTeam.equals("SYSCB") || strTeam.equals("SYSFT") || strTeam.equals("SYSAC"))
		    					pstmt.setString(2, "03");
		    				else if (strTeam.equals("SYSCF")) pstmt.setString(2,"04");
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
            rsval.add(rst);
            rst = null;

            conn.close();
            rs = null;
            rs2 = null;
            pstmt = null;
            pstmt2 = null;
            conn = null;

    		return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.confLocat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.confLocat() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.confLocat() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.confLocat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.confLocat() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.confLocat() Exception END ##");
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
					ecamsLogger.error("## Cmr0150.confLocat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of confLocat() method statement


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
			strQuery.append("select cr_team,cr_teamcd,cr_confname,    \n");
			strQuery.append("       cr_prcsw,cr_confusr,cr_congbn     \n");
			strQuery.append("  from cmr9900                           \n");
			strQuery.append(" where cr_acptno=? and cr_locat='00'     \n");
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
					if (rs.getString("cr_prcsys").equals("SYSED")) rcCnt = rs.getInt("cnt");
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
					ecamsLogger.error("111111111111111111");
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
				strQuery.append("        or cr_team='SYSAR' or cr_prcsw='Y')      \n");
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
		            	strQuery.append("   and b.cm_closedt is null                      \n");
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
	    			strQuery.append(" where cr_acptno=? and instr(?,cr_prcsys)>0      \n");
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
	    					findSw = true;
	    				}
	    				if (rs.getInt("cnt")>0) {
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
	    				}
	    			}
	                rs.close();
	                pstmt.close();
	                //ecamsLogger.debug("+++++++++step start+++");
	                if (findSw == true && CnclStep != null && CnclStep != "" && !RetryYn.equals("Y")) {
	                	strQuery.setLength(0);
	                    strQuery.append("select sign(to_date(?,'yyyymmddhh24miss')  \n");
	                    strQuery.append("       - max(cr_prcdate)) as diff          \n");
	                    strQuery.append("  from cmr1011                             \n");
	                    strQuery.append(" where cr_acptno=? and instr(?,cr_prcsys)>0\n");
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
            rsval.add(rst);
            rst = null;

            rs = null;
            pstmt = null;
            rs2 = null;
            pstmt2 = null;

    		return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr0150.confLocat_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.confLocat_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr0150.confLocat_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.confLocat_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of confLocat_conn() method statement


	public Object[] updtYn(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray	= null;
		ConnectionContext connectionContext = new ConnectionResource();

		String            strUpdtSw   = "";
		rsval.clear();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) as cnt from cmr1010               \n");
			strQuery.append(" where cr_acptno=? and cr_confno is null          \n");
			strQuery.append("   and cr_prcdate is not null and cr_status<>'3'  \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getInt("cnt") > 0) strUpdtSw = "1";
				else strUpdtSw = "0";
			}
			rs.close();
			pstmt.close();

			if (strUpdtSw.equals("1")) {
				strQuery.setLength(0);

				strQuery.append("select a.cm_userid,a.cm_username                       \n");
				strQuery.append("  from cmm0040 a,cmm0044 b,cmr1010 c                   \n");
				strQuery.append(" where c.cr_acptno=? and c.cr_confno is null           \n");
				strQuery.append("   and c.cr_status<>'3' and c.cr_prcdate is not null   \n");
				strQuery.append("   and c.cr_syscd=b.cm_syscd and c.cr_jobcd=b.cm_jobcd \n");
				strQuery.append("   and b.cm_closedt is null                            \n");
				strQuery.append("   and b.cm_userid=a.cm_userid                         \n");
				strQuery.append("   and a.cm_active='1' and a.cm_userid<>c.cr_editor    \n");
				strQuery.append("group by a.cm_userid,a.cm_username                     \n");
				strQuery.append("order by a.cm_username                                 \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				while (rs.next()){
					if (rs.getRow() == 1 ) {
						rst = new HashMap<String,String>();
						rst.put("cm_username", "선택하세요");
						rst.put("cm_userid", "00000000");
						rsval.add(rst);
						rst = null;
					}
					rst = new HashMap<String, String>();
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("cm_userid", rs.getString("cm_userid"));
					rsval.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();

			}
			conn.close();
			conn = null;
			rs = null;
			pstmt = null;

			returnObjectArray = rsval.toArray();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.updtYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.updtYn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.updtYn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.updtYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.updtYn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.updtYn() Exception END ##");
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
					ecamsLogger.error("## Cmr0150.updtYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtYn() method statement

	public String updtEditor(String AcptNo,String editUser,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
            strQuery.append("update cmr1000 set cr_editor=?                         \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,editUser);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr1010 set cr_editor=?                         \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,editUser);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr0020 set cr_editor=?                         \n");
            strQuery.append(" where cr_itemid in (select cr_itemid from cmr1010     \n");
            strQuery.append("                      where cr_acptno=?)               \n");
            strQuery.append("   and cr_status='5'                                   \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,editUser);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            strErMsg = "0";
            conn.commit();
            conn.close();
            pstmt = null;
            conn = null;
            return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.updtEditor() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.updtEditor() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.updtEditor() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.updtEditor() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.updtEditor() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.updtEditor() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.updtEditor() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtEditor() method statement

	public String delReq(String AcptNo,String ItemId,String SignTeam,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";
		Cmr3100           gyulProc    = new Cmr3100();

		ConnectionContext connectionContext = new ConnectionResource();

		Runtime  run = null;
		Process p = null;
		SystemPath		  systemPath = new SystemPath();
		String  binpath;
		String[] chkAry;
		int		cmdrtn;

		try {
			binpath = systemPath.getTmpDir("15");

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = binpath+"/procck2";
			chkAry[2] = "ecams_acct";
			chkAry[3] = AcptNo;

			p = run.exec(chkAry);
			p.waitFor();

			cmdrtn = p.exitValue();
			if (cmdrtn > 1) {
				ecamsLogger.error(chkAry[0]+" " + chkAry[1] + " " + chkAry[2]);
				ecamsLogger.error(cmdrtn);
				return "2";
			}
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
            strQuery.append("delete cmr1011 where cr_acptno=?                     \n");
            strQuery.append("   and cr_serno in (select cr_serno from cmr1010     \n");
            strQuery.append("                     where cr_acptno=?               \n");
            strQuery.append("                       and cr_baseitem=?)            \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,AcptNo);
            pstmt.setString(3,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr0020 set cr_status='0'                      \n");
            strQuery.append(" where cr_itemid in (select cr_itemid from cmr1010    \n");
            strQuery.append("                      where cr_acptno=?               \n");
            strQuery.append("                        and cr_baseitem=?)            \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete cmr1010 where cr_acptno=? and cr_baseitem=?    \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,ItemId);
            pstmt.executeUpdate();
            pstmt.close();

            strErMsg = "0";
            strQuery.setLength(0);
            strQuery.append("select count(*) as cnt from cmr1010                    \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	if (rs.getInt("cnt") == 0) {
            		strQuery.setLength(0);
                    strQuery.append("delete cmr9920 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr9910 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr9900 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr1001 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr1000 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();
            	}
            } else {
	            strQuery.setLength(0);
	            strQuery.append("select count(*) as cnt from cmr1010                    \n");
	            strQuery.append(" where cr_acptno=?                                     \n");
	            strQuery.append("   and (cr_putcode is null or cr_putcode != '0000')    \n");
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	            pstmt2.setString(1,AcptNo);
	            rs2 = pstmt2.executeQuery();
	            if (rs2.next()) {
	            	if (rs2.getInt("cnt") == 0) {
	            		strErMsg = gyulProc.nextConf(AcptNo, SignTeam, "삭제 후 자동완료", "1", ReqCd);
	            	}
	            }
	            rs2.close();
	            pstmt2.close();
            }
            rs.close();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr1000 set cr_cnclstep=?,           \n");
            strQuery.append("       cr_cncllstd=SYSDATE,cr_retryyn='N'   \n");
            strQuery.append(" where cr_acptno=?                          \n");
            pstmt2 = conn.prepareStatement(strQuery.toString());
            pstmt2.setString(1, SignTeam);
            pstmt2.setString(2, AcptNo);
            pstmt2.executeUpdate();
            pstmt2.close();

            gyulProc = null;



            if (strErMsg.equals("0")){
            	conn.commit();
            }
            else{
            	conn.rollback();
            }

            conn.close();
            rs = null;
            rs2 = null;
            pstmt = null;
            pstmt2 = null;
            conn = null;

            return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.delReq() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.delReq() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0150.delReq() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.delReq() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0150.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delReq() method statement

	public Object[] getFileList(String AcptNo,String GbnCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;			  	rst	 	= null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select CR_ACPTNO,CR_SEQNO,CR_FILENAME,CR_RELDOC \n");
			strQuery.append("  from cmr1001 			                     \n");
			strQuery.append(" where cr_acptno=?  	                         \n");	//AcptNo
			strQuery.append("   and cr_gubun=?   		                     \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, GbnCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
            rtList.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cr_acptno", rs.getString("CR_ACPTNO")); 		//신청번호
				rst.put("cr_seqno", rs.getString("CR_SEQNO")); 			//일련번호
				rst.put("orgname", rs.getString("CR_FILENAME"));		//파일멸
				rst.put("savename", rs.getString("CR_RELDOC"));	        //저장파일
				rtList.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

    		rtObj =  rtList.toArray();
    		rtList = null;

    		return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0150.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0150.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0150.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr0150.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement

}//end of Cmr0150 class statement
