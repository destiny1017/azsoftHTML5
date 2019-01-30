/*****************************************************************************************
	1. program ID	: Cmm0300.java
	2. create date	: 2008.12.08
	3. auth		    : is.choi
	4. update date	: 2008.12.22
	5. auth		    : No Name
	6. description	: [관리자] -> [결재정보]
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import app.common.LoggableStatement;

import org.apache.log4j.Logger;
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
public class Cmm0300{


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
    public Object[] getConfInfo_List(String SysCd,String ReqCd,String ManId,String SeqNo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2        = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			int parmCnt = 0;
			String[] strJob = null;
			String strWork = null;
			boolean whereSw = false;
			int i = 0;
			int j = 0;
			String strSysMsg = "";
			String strSysCd  = "";
			String strReqCd = "";
			String strReqMsg = "";

			conn = connectionContext.getConnection();

			strQuery.append("select a.cm_syscd,a.cm_reqcd,a.cm_seqno,a.cm_manid,   \n");
			strQuery.append("       a.cm_common,a.cm_blank,a.cm_holiday,a.cm_emg,  \n");
			strQuery.append("       a.cm_emg2,a.cm_jobcd,a.cm_position,a.cm_rsrccd,\n");
			strQuery.append("       a.cm_prcsw,a.cm_name,a.cm_gubun,a.cm_pgmtype,  \n");
			strQuery.append("       b.cm_codename common,c.cm_codename blank,      \n");
			strQuery.append("       d.cm_codename holi,e.cm_codename emg,          \n");
			strQuery.append("       f.cm_codename emg2,g.cm_codename,a.cm_orgstep  \n");
			strQuery.append("  from cmm0020 g,cmm0020 f,cmm0020 e,cmm0020 d,       \n");
			strQuery.append("       cmm0020 c,cmm0020 b,cmm0060 a                  \n");
			if (SysCd != null && SysCd != "") {
				strQuery.append("where a.cm_syscd=?    \n");
				whereSw = true;
			}
			if (ReqCd != null && ReqCd != "") {
				if (whereSw == true) strQuery.append("and a.cm_reqcd=?            \n");
				else {
					strQuery.append("where a.cm_reqcd=?                           \n");
					whereSw = true;
				}
			}
			if (ManId != null && ManId != "" && !ManId.equals("9")) {
				if (whereSw == true) strQuery.append("and a.cm_manid=?            \n");
				else {
					strQuery.append("where a.cm_manid=?                           \n");
					whereSw = true;
				}
			}
			if (SeqNo != null && SeqNo != "") {
				if (whereSw == true) strQuery.append("and a.cm_seqno=?            \n");
				else {
					strQuery.append("where a.cm_seqno=?                           \n");
					whereSw = true;
				}
			}
			if (whereSw == false) strQuery.append("where b.cm_macode='SGNCD'      \n");
			else  strQuery.append("and b.cm_macode='SGNCD'                        \n");
			strQuery.append("and b.cm_micode=a.cm_common                          \n");
			strQuery.append("and c.cm_macode='SGNCD' and c.cm_micode=a.cm_blank   \n");
			strQuery.append("and d.cm_macode='SGNCD' and d.cm_micode=a.cm_holiday \n");
			strQuery.append("and e.cm_macode='SGNCD' and e.cm_micode=a.cm_emg     \n");
			strQuery.append("and f.cm_macode='SGNCD' and f.cm_micode=a.cm_emg2    \n");
			strQuery.append("and g.cm_macode='SGNGBN' and g.cm_micode=a.cm_gubun  \n");
			strQuery.append("order by a.cm_reqcd,a.cm_syscd,a.cm_manid,a.cm_seqno \n");

			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            if (SysCd != null && SysCd != "") pstmt.setString(++parmCnt, SysCd);
            if (ReqCd != null && ReqCd != "") pstmt.setString(++parmCnt, ReqCd);
            if (ManId != null && ManId != "" && !ManId.equals("9")) pstmt.setString(++parmCnt, ManId);
			if (SeqNo != null && SeqNo != "") pstmt.setInt(++parmCnt, Integer.parseInt(SeqNo));
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_reqcd", rs.getString("cm_reqcd"));
				if (rs.getString("cm_syscd").equals("99999")) rst.put("cm_sysmsg", "시스템무관");
				else if (strSysCd == "" || strSysCd == null || !strSysCd.equals(rs.getString("cm_syscd"))) {
					strQuery.setLength(0);
					strQuery.append("select cm_sysmsg from cmm0030 where cm_syscd=?   \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cm_syscd"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("cm_sysmsg", rs2.getString("cm_sysmsg"));
					}
					rs2.close();
					pstmt2.close();
				} else {
					rst.put("cm_sysmsg", strSysMsg);
				}

				strSysCd = rs.getString("cm_syscd");
				if (strReqCd == "" || strReqCd == null || !strReqCd.equals(rs.getString("cm_reqcd"))) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020                    \n");
					strQuery.append(" where cm_macode='REQUEST' and cm_micode=?         \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cm_reqcd"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("reqname", rs2.getString("cm_codename"));
					}
					rs2.close();
					pstmt2.close();
				} else {
					rst.put("reqname", strReqMsg);
				}
				strReqCd = rs.getString("cm_reqcd");

				if (rs.getString("cm_manid").equals("1")) rst.put("manid", "직원");
				else  rst.put("manid", "외주");

				rst.put("ID", Integer.toString(rs.getInt("cm_seqno")));
				rst.put("cm_name", rs.getString("cm_name"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("common", rs.getString("common"));
				rst.put("blank", rs.getString("blank"));
				rst.put("holi", rs.getString("holi"));
				rst.put("emg", rs.getString("emg"));
				rst.put("emg2", rs.getString("emg2"));
				rst.put("cm_common", rs.getString("cm_common"));
				rst.put("cm_blank", rs.getString("cm_blank"));
				rst.put("cm_holiday", rs.getString("cm_holiday"));
				rst.put("cm_emg", rs.getString("cm_emg"));
				rst.put("cm_emg2", rs.getString("cm_emg2"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_position", rs.getString("cm_position"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_prcsw", rs.getString("cm_prcsw"));
				rst.put("cm_manid", rs.getString("cm_manid"));
				rst.put("cm_gubun", rs.getString("cm_gubun"));
				rst.put("cm_pgmtype", rs.getString("cm_pgmtype"));
				rst.put("cm_orgstep", rs.getString("cm_orgstep"));
				rst.put("cm_seqno", Integer.toString(rs.getInt("cm_seqno")));

				if (rs.getString("cm_jobcd") != null) {
					if (rs.getString("cm_gubun").equals("1")) {
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020    \n");
						strQuery.append(" where cm_macode='SYSGBN'          \n");
						strQuery.append("   and cm_micode=?                 \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, rs.getString("cm_jobcd"));
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()) {
			            	rst.put("deptcd", rs2.getString("cm_codename"));
			            }
			            rs2.close();
			            pstmt2.close();
					} else if (rs.getString("cm_gubun").equals("9")) {
						strQuery.setLength(0);
						strQuery.append("select cm_deptname from cmm0100    \n");
						strQuery.append(" where cm_deptcd=?                 \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, rs.getString("cm_jobcd"));
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()) {
			            	rst.put("deptcd", rs2.getString("cm_deptname"));
			            }
			            rs2.close();
			            pstmt2.close();
					} else {
						strWork = null;
						strJob = rs.getString("cm_jobcd").split(",");
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020    \n");
						strQuery.append(" where cm_macode=?                 \n");
						strQuery.append("   and cm_micode in( \n");
						for (i=0;strJob.length>i;i++) {
							if (i == 0) strQuery.append("?");
							else strQuery.append(",?");
						}
						strQuery.append(")                                  \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						j = 0;
						if (rs.getString("cm_gubun").equals("P")) pstmt2.setString(++j, "PRJJIK");
						else pstmt2.setString(++j, "RGTCD");
						for (i=0;strJob.length>i;i++) {
							pstmt2.setString(++j, strJob[i]);
						}
			            rs2 = pstmt2.executeQuery();
			            while (rs2.next()) {
			            	if (strWork != null) strWork = strWork + ",";
			            	else strWork = "";
			            	strWork = strWork + rs2.getString("cm_codename");
			            }
			            rs2.close();
			            pstmt2.close();
			            rst.put("deptcd", strWork);
					}
				}
				if (rs.getString("cm_position") != null) {
					strWork = null;
					strJob = rs.getString("cm_position").split(",");
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020    \n");
					strQuery.append(" where cm_macode='RGTCD'           \n");
					strQuery.append("   and cm_micode in( \n");
					for (i=0;strJob.length>i;i++) {
						if (i == 0) strQuery.append("?");
						else strQuery.append(",?");
					}
					strQuery.append(")                                  \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					j = 0;
					for (i=0;strJob.length>i;i++) {
						pstmt2.setString(++j, strJob[i]);
					}
		            rs2 = pstmt2.executeQuery();
		            while (rs2.next()) {
		            	if (strWork != null) strWork = strWork + ",";
		            	else strWork = "";
		            	strWork = strWork + rs2.getString("cm_codename");
		            }
		            rs2.close();
		            pstmt2.close();
		            rst.put("rgtcd", strWork);
		            rs2.close();
		            pstmt2.close();
				}
				if (rs.getString("cm_pgmtype") != null) {
					strWork = null;
					strJob = rs.getString("cm_pgmtype").split(",");
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020    \n");
					strQuery.append(" where cm_macode='PGMTYPE'           \n");
					strQuery.append("   and cm_micode in( \n");
					for (i=0;strJob.length>i;i++) {
						if (i == 0) strQuery.append("?");
						else strQuery.append(",?");
					}
					strQuery.append(")                                  \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					j = 0;
					for (i=0;strJob.length>i;i++) {
						pstmt2.setString(++j, strJob[i]);
					}
		            rs2 = pstmt2.executeQuery();
		            while (rs2.next()) {
		            	if (strWork != null) strWork = strWork + ",";
		            	else strWork = "";
		            	strWork = strWork + rs2.getString("cm_codename");
		            }
		            rs2.close();
		            pstmt2.close();
		            rst.put("pgmtype", strWork);
		            rs2.close();
		            pstmt2.close();
				}
				if (rs.getString("cm_rsrccd") != null) {
					strWork = null;
					strJob = rs.getString("cm_rsrccd").split(",");
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020    \n");
					strQuery.append(" where cm_macode='JAWON'           \n");
					strQuery.append("   and cm_micode in( \n");
					for (i=0;strJob.length>i;i++) {
						if (i == 0) strQuery.append("?");
						else strQuery.append(",?");
					}
					strQuery.append(")                                  \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					j = 0;
					for (i=0;strJob.length>i;i++) {
						pstmt2.setString(++j, strJob[i]);
					}
		            rs2 = pstmt2.executeQuery();
		            while (rs2.next()) {
		            	if (strWork != null) strWork = strWork + ",";
		            	else strWork = "";
		            	strWork = strWork + rs2.getString("cm_codename");
		            }
		            rs2.close();
		            pstmt2.close();
		            rst.put("rsrccd", strWork);
		            rs2.close();
		            pstmt2.close();
				}
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300.getConfInfo_List() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300.getConfInfo_List() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300.getConfInfo_List() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300.getConfInfo_List() Exception END ##");
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
					ecamsLogger.error("## Cmm0300.getConfInfo_List() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String confInfo_Updt(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmm0060                                          \n");
			strQuery.append(" where cm_reqcd=? and cm_syscd=? and cm_seqno=?         \n");
			if (!etcData.get("cm_manid").equals("9"))
			   strQuery.append(" and cm_manid=?                                      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_reqcd"));
			pstmt.setString(2, etcData.get("cm_syscd"));
			pstmt.setInt(3, Integer.parseInt(etcData.get("cm_step")));
			if (!etcData.get("cm_manid").equals("9")) pstmt.setString(4, etcData.get("cm_manid"));
			pstmt.executeUpdate();
			pstmt.close();
			
			parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("insert into cmm0060                                        \n");
			strQuery.append("  (CM_SYSCD,CM_REQCD,CM_SEQNO,CM_MANID,CM_NAME,CM_GUBUN,   \n");
			strQuery.append("   CM_COMMON,CM_BLANK,CM_HOLIDAY,CM_EMG,CM_EMG2,CM_ORGSTEP,\n");
			strQuery.append("   CM_POSITION,CM_JOBCD,CM_RSRCCD,CM_PRCSW,CM_PGMTYPE)     \n");
			strQuery.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
			pstmt.setString(++parmCnt, etcData.get("cm_reqcd"));
			pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_step")));
			if (etcData.get("cm_manid").equals("9")) pstmt.setString(++parmCnt, "1");
			else pstmt.setString(++parmCnt, etcData.get("cm_manid"));
			pstmt.setString(++parmCnt, etcData.get("cm_name"));
			pstmt.setString(++parmCnt, etcData.get("cm_gubun"));
			pstmt.setString(++parmCnt, etcData.get("cm_common"));
			pstmt.setString(++parmCnt, etcData.get("cm_blank"));
			pstmt.setString(++parmCnt, etcData.get("cm_holi"));
			pstmt.setString(++parmCnt, etcData.get("cm_emg"));
			pstmt.setString(++parmCnt, etcData.get("cm_emg2"));
			pstmt.setString(++parmCnt, etcData.get("delsw"));
			pstmt.setString(++parmCnt, etcData.get("cm_rgtcd"));
			if (etcData.get("cm_gubun").equals("1")) pstmt.setString(++parmCnt, etcData.get("cm_sysgbn"));
			else if (etcData.get("cm_gubun").equals("9")) pstmt.setString(++parmCnt, etcData.get("cm_deptcd"));
			else pstmt.setString(++parmCnt, etcData.get("cm_etcrgt"));
			pstmt.setString(++parmCnt, etcData.get("cm_rsrccd"));
			pstmt.setString(++parmCnt, etcData.get("cm_prcsw"));
			pstmt.setString(++parmCnt, etcData.get("cm_pgmtype"));
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

			if (etcData.get("cm_manid").equals("9")) {
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("insert into cmm0060                                      \n");
				strQuery.append("  (CM_SYSCD,CM_REQCD,CM_SEQNO,CM_MANID,CM_NAME,CM_GUBUN, \n");
				strQuery.append("   CM_COMMON,CM_BLANK,CM_HOLIDAY,CM_EMG,CM_EMG2,CM_ORGSTEP,\n");
				strQuery.append("   CM_POSITION,CM_JOBCD,CM_RSRCCD,CM_PRCSW,CM_PGMTYPE)   \n");
				strQuery.append("(select CM_SYSCD,CM_REQCD,CM_SEQNO,'2',CM_NAME,CM_GUBUN, \n");
				strQuery.append("   CM_COMMON,CM_BLANK,CM_HOLIDAY,CM_EMG,CM_EMG2,CM_ORGSTEP,\n");
				strQuery.append("   CM_POSITION,CM_JOBCD,CM_RSRCCD,CM_PRCSW,CM_PGMTYPE    \n");
				strQuery.append("   from cmm0060                                          \n");
				strQuery.append("  where cm_syscd=? and cm_reqcd=? and cm_seqno=?         \n");
				strQuery.append("    and cm_manid='1')                                    \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_reqcd"));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_step")));
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
			}
			pstmt.close();
			conn.close();

			pstmt = null;
			conn = null;

			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300.confInfo_Updt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300.confInfo_Updt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300.confInfo_Updt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300.confInfo_Updt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300.confInfo_Updt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String confInfo_Close(String SysCd,String ReqCd,String ManId,String SeqNo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("delete cmm0060                                          \n");
			strQuery.append(" where cm_reqcd=? and cm_syscd=? and cm_seqno=?         \n");
			if (!ManId.equals("9"))
			   strQuery.append(" and cm_manid=?                                      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, ReqCd);
			pstmt.setString(2, SysCd);
			pstmt.setInt(3, Integer.parseInt(SeqNo));
			if (!ManId.equals("9")) pstmt.setString(4, ManId);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;


			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300.confInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300.confInfo_Close() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300.confInfo_Close() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300.confInfo_Close() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300.confInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of confInfo_Close

}//end of Cmm0300 class statement
