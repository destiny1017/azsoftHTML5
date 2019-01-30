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
public class Cmr3650{


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
			strQuery.setLength(0);
			strQuery.append(" select a.cr_acptno,a.cr_syscd,a.cr_status,a.cr_editor,a.cr_qrycd, \n");
			strQuery.append("        a.cr_acptdate,a.cr_passcd,a.cr_sayu,                       \n");
			strQuery.append("        a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,                  \n");
			strQuery.append("        to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,         \n");
			strQuery.append("        b.cm_username,c.cm_jobname,e.cm_sysmsg,                    \n");
			strQuery.append("        substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,\n");
			strQuery.append("        to_char(a.CR_ACPTDATE, 'yyyy/mm/dd hh24:mi:ss') acptdate,	\n");
			strQuery.append("        to_char(a.CR_PRCDATE, 'yyyy/mm/dd hh24:mi:ss')  prcdate	\n");
			strQuery.append("   from cmr1000 a, cmm0040 b,cmm0102 c,cmm0030 e                   \n");
			strQuery.append("  where a.cr_acptno = ?                                            \n");
			strQuery.append("    and a.cr_editor=b.cm_userid                                    \n");
			strQuery.append("    and a.cr_syscd = e.cm_syscd                                    \n");
			strQuery.append("    and a.cr_jobcd=c.cm_jobcd                                      \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			if (rs.next()){
				rst = new HashMap<String, String>();
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
				//rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				//rst.put("analsw", "N");

				rst.put("cr_acptdate" ,rs.getString("cr_acptdate"));  //요청일자
				rst.put("cr_passcd", rs.getString("cr_passcd"));//제목
				rst.put("cr_sayu", rs.getString("cr_sayu"));//제목

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
					rst.put("updtsw1", "0");
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
						strQuery.append("select cr_teamcd from cmr9900    \n");
						strQuery.append(" where cr_acptno=?               \n");
						strQuery.append("   and to_number(cr_locat)>to_number(?) \n");
						strQuery.append("   and cr_status='0'             \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, AcptNo);
						pstmt2.setString(2, rsconf.get(0).get("confusr").substring(0,2));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs2.getString("cr_teamcd").equals("2")) rst.put("updtsw1", "1");
						}
						rs2.close();
						pstmt2.close();

						if (rst.get("updtsw1").equals("0")) {
							strQuery.setLength(0);
							strQuery.append("select count(*) cnt from cmr9900 \n");
							strQuery.append(" where cr_acptno=?               \n");
							strQuery.append("   and to_number(cr_locat)>to_number(?) \n");
							strQuery.append("   and cr_teamcd='5'             \n");
							strQuery.append("   and cr_status='0'             \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(1, AcptNo);
							pstmt2.setString(2, rsconf.get(0).get("confusr").substring(0,2));
							rs2 = pstmt2.executeQuery();
							if (rs2.next()) {
								if (rs2.getInt("cnt") == 0) rst.put("updtsw1", "9");
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
			ecamsLogger.error("## Cmr3650.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3650.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3650.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3650.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmr3650.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public ArrayList<HashMap<String, String>> getProgList(String AcptNo,String gbnCd) throws SQLException, Exception {
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
			if (gbnCd.equals("1")) {
				strQuery.append("select CR_SERNO,CR_RUNDAY,CR_SYNSERNM,CR_SYNDBUSER,CR_SYNNM,   \n");
				strQuery.append("       CR_TABSERNM,CR_TABDBNM,CR_TABNM,CR_SYNAUTHS,CR_SYNAUTHU,\n");
				strQuery.append("       CR_SYNAUTHI,CR_SYNAUTHD,CR_SYNAUTHE,cr_reqcd,cr_testdate,cr_chgcd,	\n");
				//strQuery.append("select cr_serno,cr_runday,cr_synsernm,cr_syndbuser,cr_synnm,\n");
				//strQuery.append("       cr_tabsernm,cr_tabdbnm,cr_tabnm,cr_synauths,cr_chgcd,\n");
				//strQuery.append("       cr_synauthu,cr_synauthi,cr_synauthd,cr_testdate,     \n");
				strQuery.append("       to_char(cr_testdate,'yyyy/mm/dd hh24:mi') testdate   \n");
				strQuery.append("  from cmr1201 where cr_acptno = ?                          \n");
				strQuery.append("   and cr_synsernm is not null                              \n");
			} else if (gbnCd.equals("2")) {
				strQuery.append("select CR_SERNO,CR_RUNDAY,CR_CRTVIEW,CR_CRTVIEWDET,cr_reqcd,  \n");
        		strQuery.append("       cr_dbgbn, cr_testdate,cr_chgcd,cr_viewdbuser, 		   \n");
        		//strQuery.append("select cr_serno,cr_runday,cr_crtview,cr_crtviewdet,         \n");
				//strQuery.append("       cr_testdate,cr_chgcd,                                \n");
				strQuery.append("       to_char(cr_testdate,'yyyy/mm/dd hh24:mi') testdate   \n");
				strQuery.append("  from cmr1202 where cr_acptno = ?                          \n");
				strQuery.append("   and cr_crtview is not null                               \n");
			} else if (gbnCd.equals("3")) {
				strQuery.append("select CR_SERNO,CR_RUNDAY,CR_SEQNAME,CR_INCREMENT,CR_START, 	\n");
				strQuery.append("       CR_MINVAL,CR_ORDER,CR_INITYN,CR_INITTERM,CR_RETABLEID,  \n");
				strQuery.append("       CR_YONGDO,cr_reqcd,cr_chgcd,CR_CYCLE,CR_MAXVAL,			\n");
				strQuery.append("       cr_testdate,CR_RETABLENM,cr_dbgbn,						\n");
				//strQuery.append("select cr_serno,cr_runday,cr_secrtseq,cr_seincrement,       \n");
				//strQuery.append("       cr_sestart,cr_semaxval,cr_testdate,cr_secycle,cr_chgcd,\n");
				strQuery.append("       to_char(cr_testdate,'yyyy/mm/dd hh24:mi') testdate   \n");
				strQuery.append("  from cmr1203 where cr_acptno = ?                          \n");
				strQuery.append("   and CR_SEQNAME is not null                              	\n");
			} else{
				strQuery.append("select cr_serno,cr_chgcd, cr_todbusr, cr_tableid, cr_testdate, \n");
				strQuery.append("       cr_runday, cr_qrycd, cr_reqcd, cr_dbgbn, cr_todbgbn, 	\n");
				strQuery.append("       to_char(cr_testdate,'yyyy/mm/dd hh24:mi') testdate   	\n");
				strQuery.append("  from cmr1204 where cr_acptno = ?                          	\n");
			}

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        while (rs.next())
	        {
	        	rst = new HashMap<String, String>();
	        	rst.put("cr_serno", rs.getString("cr_serno"));
	        	rst.put("gbncd", gbnCd);
	        	rst.put("cr_chgcd", rs.getString("cr_chgcd"));
	        	rst.put("cr_runday", rs.getString("cr_runday"));
				if(rs.getString("cr_chgcd").equals("1")){
					rst.put("chgcd", "REAL반영신청");
				}else if (rs.getString("cr_chgcd").equals("3")){
					rst.put("chgcd", "TEST반영취소");
				}
	        	rst.put("runday", rs.getString("cr_runday").substring(0,4)+ "/" + rs.getString("cr_runday").substring(4,6)+"/" + rs.getString("cr_runday").substring(6));
	        	rst.put("cr_testdate", rs.getString("cr_testdate"));
				if (rs.getString("cr_testdate") != null && rs.getString("cr_testdate") != "") {
					rst.put("testdate", rs.getString("testdate"));
				}
				rst.put("chggbn2", rs.getString("cr_reqcd"));
				if(rs.getString("cr_reqcd").equals("01")){
					rst.put("chggbn", "신규");
				}else if(rs.getString("cr_reqcd").equals("02")){
					rst.put("chggbn", "수정");
				}else{
					rst.put("chggbn", "삭제");
				}

	        	if (gbnCd.equals("1")) {
		        	rst.put("cr_synsernm", rs.getString("CR_SYNSERNM"));
		        	rst.put("cr_syndbuser", rs.getString("CR_SYNDBUSER"));
		        	rst.put("cr_synnm", rs.getString("CR_SYNNM"));
		        	rst.put("cr_tabsernm", rs.getString("CR_TABSERNM"));
		        	rst.put("cr_tabdbnm", rs.getString("CR_TABDBNM"));
		        	rst.put("cr_tabnm", rs.getString("CR_TABNM"));
					rst.put("cr_synauths", rs.getString("CR_SYNAUTHS"));
					rst.put("cr_synauthu", rs.getString("CR_SYNAUTHU"));
					rst.put("cr_synauthi", rs.getString("CR_SYNAUTHI"));
					rst.put("cr_synauthd", rs.getString("CR_SYNAUTHD"));
					rst.put("cr_synauthe", rs.getString("CR_SYNAUTHE"));
	        	} else if (gbnCd.equals("2")) {
					rst.put("cr_crtview", rs.getString("CR_CRTVIEW"));
					rst.put("cr_crtviewdet", rs.getString("CR_CRTVIEWDET"));
					rst.put("dbgbn", rs.getString("cr_dbgbn"));
		        	rst.put("cr_viewdbuser", rs.getString("cr_viewdbuser"));
	        	} else if (gbnCd.equals("3")) {
					//rst.put("cr_secrtseq", rs.getString("cr_secrtseq"));
					//rst.put("cr_seincrement", rs.getString("cr_seincrement"));
					//rst.put("cr_sestart", rs.getString("cr_sestart"));
					//rst.put("cr_semaxval", rs.getString("cr_semaxval"));
					//rst.put("cr_secycle", rs.getString("cr_secycle"));
	        		rst.put("cr_secrtseq", rs.getString("CR_SEQNAME"));
					rst.put("cr_seincrement", rs.getString("CR_INCREMENT"));
					rst.put("cr_sestart", rs.getString("CR_START"));
					rst.put("cr_semaxval", rs.getString("CR_MAXVAL"));
					rst.put("cr_secycle", rs.getString("CR_CYCLE"));

					rst.put("cr_minval", rs.getString("CR_MINVAL"));
					rst.put("cr_order", rs.getString("CR_ORDER"));
					rst.put("cr_inityn", rs.getString("CR_INITYN"));
					rst.put("cr_initterm", rs.getString("CR_INITTERM"));
					rst.put("cr_retableid", rs.getString("CR_RETABLEID"));
					rst.put("cr_yongdo", rs.getString("CR_YONGDO"));
					rst.put("cr_retablenm", rs.getString("CR_RETABLENM"));
					rst.put("dbgbn", rs.getString("cr_dbgbn"));
	        	} else {
	        		rst.put("dbusr", rs.getString("cr_todbusr"));
	        		rst.put("tbname", rs.getString("cr_tableid"));
	        		rst.put("tbown", rs.getString("cr_todbgbn"));
	        		rst.put("dbgbn", rs.getString("cr_dbgbn"));
	        		rst.put("cr_qrycd", rs.getString("cr_qrycd"));
	        		if(rs.getString("cr_qrycd").equals("C")){
	        			rst.put("reqgbn", "CDC");
	        		}else{
	        			rst.put("reqgbn", "BCV");
	        		}
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

			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3650.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3650.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3650.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3650.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null) 	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3650.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement


	public String update_chgContent(String acptno, String serno, String gbnCd,HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			if (gbnCd.equals("1")) {
				strQuery.append("update cmr1201           \n");
				strQuery.append("   set cr_synsernm=?,    \n");
				strQuery.append("       cr_syndbuser=?,   \n");
				strQuery.append("       cr_synnm=?,       \n");
				strQuery.append("       cr_tabsernm=?,    \n");
				strQuery.append("       cr_TabDbNm=?,     \n");
				strQuery.append("       cr_TabNm=?,       \n");
				strQuery.append("       cr_synauths=?,    \n");
				strQuery.append("       cr_synauthu=?,    \n");
				strQuery.append("       cr_synauthi=?,    \n");
				strQuery.append("       cr_synauthd=?,    \n");
				strQuery.append("       cr_synauthe=?,    \n");
				strQuery.append("       cr_runday=?,      \n");
				strQuery.append("       cr_reqcd=?        \n");
				strQuery.append(" where cr_acptno=?       \n");
				strQuery.append("   and cr_serno=?        \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synsernm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_syndbuser"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synnm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_tabsernm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_tabdbnm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_tabnm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synauths"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synauthu"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synauthi"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synauthd"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_synauthe"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_runday"));
	        	pstmt.setString(pstmtcount++, etcData.get("chggbn2"));
			} else if (gbnCd.equals("2")) {
				strQuery.append("update cmr1202           \n");
				strQuery.append("   set cr_CrtView=?,     \n");
				strQuery.append("       cr_CrtViewDet=?,  \n");
				strQuery.append("       cr_runday=?,      \n");
				strQuery.append("       cr_dbgbn=?,       \n");
				strQuery.append("       cr_reqcd=?        \n");
				strQuery.append(" where cr_acptno=?       \n");
				strQuery.append("   and cr_serno=?        \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("cr_crtview"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_crtviewdet"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_runday"));
	        	pstmt.setString(pstmtcount++, etcData.get("dbgbn"));
	        	pstmt.setString(pstmtcount++, etcData.get("chggbn2"));
			} else if (gbnCd.equals("3")) {
				strQuery.append("update cmr1203           \n");
				strQuery.append("   set CR_RUNDAY    = ?, \n");
				strQuery.append("       CR_SEQNAME   = ?, \n");
				strQuery.append("       CR_INCREMENT = ?, \n");
				strQuery.append("       CR_START     = ?, \n");
				strQuery.append("       CR_MAXVAL    = ?, \n");
				strQuery.append("       CR_CYCLE     = ?, \n");
				strQuery.append("       CR_MINVAL    = ?, \n");
				strQuery.append("       CR_ORDER     = ?, \n");
				strQuery.append("       CR_INITYN    = ?, \n");
				strQuery.append("       CR_INITTERM  = ?, \n");
				strQuery.append("       CR_RETABLEID = ?, \n");
				strQuery.append("       CR_RETABLENM = ?, \n");
				strQuery.append("       CR_YONGDO    = ?, \n");
				strQuery.append("       cr_reqcd     = ?, \n");
				strQuery.append("       cr_dbgbn     = ?  \n");
				strQuery.append(" where cr_acptno=?       \n");
				strQuery.append("   and cr_serno=?        \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("cr_runday"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_secrtseq"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_seincrement"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_sestart"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_semaxval"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_secycle"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_minval"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_order"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_inityn"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_initterm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_retableid"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_retablenm"));
	        	pstmt.setString(pstmtcount++, etcData.get("cr_yongdo"));
	        	pstmt.setString(pstmtcount++, etcData.get("chggbn2"));
	        	pstmt.setString(pstmtcount++, etcData.get("dbgbn"));
			}else {
				strQuery.append("update cmr1204          \n");
				strQuery.append("   set cr_runday  = ?,  \n");
				strQuery.append("       cr_qrycd   = ?,  \n");
				strQuery.append("       cr_reqcd   = ?,  \n");
				strQuery.append("       cr_dbgbn   = ?,  \n");
				strQuery.append("       cr_todbgbn = ?,  \n");
				strQuery.append("       cr_todbusr = ?,  \n");
				strQuery.append("       cr_tableid = ?   \n");
				strQuery.append(" where cr_acptno=?      \n");
				strQuery.append("   and cr_serno=?       \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("cr_runday"));
	        	pstmt.setString(pstmtcount++, etcData.get("reqgbn2"));
	        	pstmt.setString(pstmtcount++, etcData.get("chggbn2"));
	        	pstmt.setString(pstmtcount++, etcData.get("dbgbn"));
	        	pstmt.setString(pstmtcount++, etcData.get("tbown"));
	        	pstmt.setString(pstmtcount++, etcData.get("dbusr"));
	        	pstmt.setString(pstmtcount++, etcData.get("tbname"));
			}
        	pstmt.setString(pstmtcount++, acptno);
        	pstmt.setString(pstmtcount++, serno);
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();
        	conn.close();
        	pstmt = null;
        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3650.update_chgContent() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3650.update_chgContent() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3650.update_chgContent() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3650.update_chgContent() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3650.update_chgContent() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String updateConfirm(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			if (etcData.get("cr_teamcd").equals("2")) {
				strQuery.setLength(0);
				strQuery.append("update cmr1200           \n");
				strQuery.append("   set cr_chgcd=?        \n");
				strQuery.append(" where cr_acptno=?       \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("chgcd"));
	        	pstmt.setString(pstmtcount++, etcData.get("acptno"));
	        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();
			} else if (etcData.get("cr_teamcd").equals("5") && etcData.get("updtsw1").equals("1")) {
				strQuery.setLength(0);
				strQuery.append("update cmr1200             \n");
				strQuery.append("   set cr_testdate=SYSDATE \n");
				strQuery.append(" where cr_acptno=?         \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	pstmt.setString(pstmtcount++, etcData.get("acptno"));
	        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();
			} else if (etcData.get("cr_teamcd").equals("5") && etcData.get("updtsw1").equals("9")) {
				if (etcData.get("chgcd").equals("3")) {
					strQuery.setLength(0);
					strQuery.append("update cmr1000             \n");
					strQuery.append("   set cr_status='3',      \n");
					strQuery.append("       cr_prcdate=SYSDATE  \n");
					strQuery.append(" where cr_acptno=?         \n");
		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	//pstmt = new LoggableStatement(conn,strQuery.toString());
		        	pstmt.setString(pstmtcount++, etcData.get("acptno"));
		        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        	pstmt.executeUpdate();
		        	pstmt.close();
				} else {
					strQuery.setLength(0);
					strQuery.append("update cmr1000             \n");
					strQuery.append("   set cr_prcdate=SYSDATE  \n");
					strQuery.append(" where cr_acptno=?         \n");
		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	//pstmt = new LoggableStatement(conn,strQuery.toString());
		        	pstmt.setString(pstmtcount++, etcData.get("acptno"));
		        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        	pstmt.executeUpdate();
		        	pstmt.close();

				}
			}
        	conn.close();
        	pstmt = null;
        	conn = null;

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3650.updateConfirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3650.updateConfirm() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3650.updateConfirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3650.updateConfirm() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3650.updateConfirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}