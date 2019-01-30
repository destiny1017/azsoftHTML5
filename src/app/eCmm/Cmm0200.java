/*****************************************************************************************
	1. program ID	: Cmr0200.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

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
public class Cmm0200{


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
    public Object[] getSysInfo_List(boolean clsSw,String SysCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysopen,\n");
			strQuery.append("       a.cm_scmopen,a.cm_sysinfo,a.cm_basesys,        \n");
			strQuery.append("       a.cm_stopst,a.cm_stoped,c.cm_sysmsg basesys,   \n");
			strQuery.append("       a.cm_closedt,a.cm_prccnt,b.cm_codename,a.cm_systime,  \n");
			strQuery.append("       c.cm_dirbase, d.cm_codename servername \n");
			strQuery.append("  from cmm0030 a,cmm0020 b,cmm0030 c,cmm0020 d        \n");
			strQuery.append(" where b.cm_macode='SYSGB' and b.cm_micode=a.cm_sysgb \n");
			if (!clsSw) strQuery.append("and a.cm_closedt is null     	           \n");
			if (SysCd != null && SysCd != "") {
				strQuery.append("and ( a.cm_syscd like ? or upper(a.cm_sysmsg) like upper(?) )   \n");
			}
			strQuery.append("   and nvl(a.cm_basesys,a.cm_syscd)=c.cm_syscd        \n");
			strQuery.append("   and d.cm_macode = 'SERVERCD' and c.cm_dirbase = d.cm_micode \n");
			strQuery.append("order by a.cm_syscd                                   \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            if (SysCd != null && SysCd != "") {
            	pstmt.setString(1, ("%"+SysCd+"%"));
            	pstmt.setString(2, ("%"+SysCd+"%").toUpperCase());
            }
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb", rs.getString("cm_sysgb"));
				rst.put("cm_dirbase", rs.getString("cm_dirbase"));
				rst.put("servername", rs.getString("servername"));
				rst.put("sysgb", rs.getString("cm_codename"));
				rst.put("cm_sysinfo", rs.getString("cm_sysinfo"));
				rst.put("cm_scmopen", rs.getString("cm_scmopen"));
				rst.put("cm_sysopen", rs.getString("cm_sysopen"));
				rst.put("cm_basesys", rs.getString("cm_basesys"));
				rst.put("cm_systime", rs.getString("cm_systime"));
				rst.put("basesys", rs.getString("basesys"));
				rst.put("sysopen", rs.getString("cm_sysopen").substring(0,4)+"/"+
						           rs.getString("cm_sysopen").substring(4,6)+"/"+
						           rs.getString("cm_sysopen").substring(6,8));
				rst.put("scmopen", rs.getString("cm_scmopen").substring(0,4)+"/"+
				           rs.getString("cm_scmopen").substring(4,6)+"/"+
				           rs.getString("cm_scmopen").substring(6,8));
				rst.put("cm_prccnt", Integer.toString(rs.getInt("cm_prccnt")));
				if (rs.getString("cm_closedt") != null) rst.put("closeSw", "Y");
				else  rst.put("closeSw", "N");
				if (rs.getString("cm_sysinfo").substring(3,4).equals("1")) {
					rst.put("cm_stdate", rs.getString("cm_stopst"));
					rst.put("cm_eddate", rs.getString("cm_stoped"));
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
			ecamsLogger.error("## Cmm0200.getSysInfo_List() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.getSysInfo_List() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.getSysInfo_List() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.getSysInfo_List() Exception END ##");
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
					ecamsLogger.error("## Cmm0200.getSysInfo_List() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public Object[] getSvrInfo(String SysCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select b.cm_micode,b.cm_codename                      \n");
			strQuery.append("  from cmm0031 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='SERVERCD'                         \n");
			strQuery.append("   and b.cm_micode=a.cm_svrcd                         \n");
			strQuery.append("group by b.cm_micode,b.cm_codename                    \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cmm0200.getSvrInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.getSvrInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.getSvrInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.getSvrInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0200.getSvrInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String factUpdt() throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select cm_macode,max(cm_micode) max                   \n");
			strQuery.append("  from cmm0020                                        \n");
			strQuery.append(" where cm_macode in ('SYSINFO','SVRINFO','RSCHKITEM') \n");
			strQuery.append("   and cm_closedt is null                             \n");
			strQuery.append("   and cm_micode<>'****'                              \n");
			strQuery.append(" group by cm_macode        	                       \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				if (rs.getString("cm_macode").equals("SYSINFO")) {
					strQuery.setLength(0);
					strQuery.append("update cmm0030 set                            \n");
					strQuery.append("       cm_sysinfo=rpad(cm_sysinfo,?,'0')      \n");
					strQuery.append(" where length(cm_sysinfo)<?                   \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setInt(1, Integer.parseInt(rs.getString("max")));
					pstmt2.setInt(2, Integer.parseInt(rs.getString("max")));
					pstmt2.executeUpdate();
					pstmt2.close();
				} else if (rs.getString("cm_macode").equals("SVRINFO")) {
					strQuery.setLength(0);
					strQuery.append("update cmm0031 set                            \n");
					strQuery.append("       cm_svruse=rpad(cm_svruse,?,'0')        \n");
					strQuery.append(" where length(cm_svruse)<?                    \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setInt(1, Integer.parseInt(rs.getString("max")));
					pstmt2.setInt(2, Integer.parseInt(rs.getString("max")));
					pstmt2.executeUpdate();
					pstmt2.close();
				} else if (rs.getString("cm_macode").equals("RSCHKITEM")) {
					strQuery.setLength(0);
					strQuery.append("update cmm0036 set                            \n");
					strQuery.append("       cm_info=rpad(cm_info,?,'0')            \n");
					strQuery.append(" where length(cm_info)<?                      \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setInt(1, Integer.parseInt(rs.getString("max")));
					pstmt2.setInt(2, Integer.parseInt(rs.getString("max")));
					pstmt2.executeUpdate();
					pstmt2.close();
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200.factUpdt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.factUpdt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.factUpdt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.factUpdt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200.factUpdt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
    public String sysInfo_Updt(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           isrtSw      = false;
		String            strSysCd    = "";
		int               parmCnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			if (etcData.get("cm_syscd") != null && etcData.get("cm_syscd") != "") {
				strQuery.append("select count(*) cnt from cmm0030      \n");
				strQuery.append(" where cm_syscd=?                     \n");
				//pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, etcData.get("cm_syscd"));
	            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				if (rs.next() && rs.getInt("cnt") == 0){
					isrtSw = true;
				}
				rs.close();
				pstmt.close();
				strSysCd = etcData.get("cm_syscd");
			} else {
				strQuery.append("select lpad(nvl(max(cm_syscd),'00001') + 1,5,'0') max \n");
				strQuery.append("  from cmm0030                                        \n");
				strQuery.append(" where substr(cm_sysinfo,1,1)='0'                     \n");
				//pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt = conn.prepareStatement(strQuery.toString());
	            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				if (rs.next()){
					strSysCd = rs.getString("max");
				}
				rs.close();
				pstmt.close();
			}
			if ( isrtSw ) {
				strQuery.setLength(0);
				strQuery.append("insert into cmm0030                                      \n");
				strQuery.append("  (CM_SYSCD,CM_SYSMSG,CM_SYSGB,CM_CREATDT,CM_LASTUPDT,   \n");
				strQuery.append("   CM_ONLINE,CM_SYSFC1,CM_SYSFC2,CM_DIRBASE,CM_SYSINFO,  \n");
				strQuery.append("   CM_PRCCNT,CM_STOPST,CM_STOPED,CM_SYSTIME,             \n");
				strQuery.append("   CM_SYSOPEN,CM_SCMOPEN,CM_BASESYS,CM_PRJNAME) values   \n");
				strQuery.append("(?, ?, ?, SYSDATE, SYSDATE, 'N', 'CD', 'DC', ?,          \n");
				strQuery.append("   ?, ?, ?, ?, ?, ?, ?, ?, ?)                            \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, strSysCd);
				pstmt.setString(++parmCnt, etcData.get("cm_sysmsg"));
				pstmt.setString(++parmCnt, etcData.get("cm_sysgb"));
				pstmt.setString(++parmCnt, etcData.get("cm_dirbase"));
				pstmt.setString(++parmCnt, etcData.get("cm_sysinfo"));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prccnt")));
				pstmt.setString(++parmCnt, etcData.get("cm_stdate"));
				pstmt.setString(++parmCnt, etcData.get("cm_eddate"));
				if (etcData.get("cm_sysinfo").substring(5,6).equals("1"))
					pstmt.setString(++parmCnt, etcData.get("cm_systime"));
				else
				    pstmt.setString(++parmCnt, "");
				pstmt.setString(++parmCnt, etcData.get("sysopen"));
				pstmt.setString(++parmCnt, etcData.get("scmopen"));
				if (etcData.get("basesys") == null && etcData.get("basesys") == "") {
					pstmt.setString(++parmCnt, strSysCd);
				} else {
					pstmt.setString(++parmCnt, etcData.get("basesys"));
				}
				
				pstmt.setString(++parmCnt, etcData.get("prjname"));
				
			} else {
				strQuery.setLength(0);
				strQuery.append("select cm_syscd from cmm0030				\n");
				strQuery.append(" where cm_syscd=? and cm_closedt is null	\n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();

				if ( rs.next() ){
					pstmt.close();
					strQuery.setLength(0);
					strQuery.append("update cmm0030 set cm_sysmsg=?,cm_closedt='',            \n");
					strQuery.append("   CM_SYSGB=?,CM_LASTUPDT=SYSDATE,CM_SYSINFO=?,          \n");
					strQuery.append("   CM_PRCCNT=?,CM_STOPST=?,CM_STOPED=?,CM_SYSTIME=?,     \n");
					strQuery.append("   CM_SYSOPEN=?,CM_SCMOPEN=?,CM_BASESYS=?,CM_PRJNAME=?   \n");
					strQuery.append("   ,CM_DIRBASE=? \n");
					strQuery.append("where cm_syscd=?                                         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("cm_sysmsg"));
					pstmt.setString(++parmCnt, etcData.get("cm_sysgb"));
					pstmt.setString(++parmCnt, etcData.get("cm_sysinfo"));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prccnt")));
					pstmt.setString(++parmCnt, etcData.get("cm_stdate"));
					pstmt.setString(++parmCnt, etcData.get("cm_eddate"));
					if (etcData.get("cm_sysinfo").substring(5,6).equals("1"))
						pstmt.setString(++parmCnt, etcData.get("cm_systime"));
					else
					    pstmt.setString(++parmCnt, "");
					pstmt.setString(++parmCnt, etcData.get("sysopen"));
					pstmt.setString(++parmCnt, etcData.get("scmopen"));
					pstmt.setString(++parmCnt, etcData.get("basesys"));
					pstmt.setString(++parmCnt, etcData.get("prjname"));
					pstmt.setString(++parmCnt, etcData.get("cm_dirbase"));
					pstmt.setString(++parmCnt, strSysCd);
				}else {
					pstmt.close();
					if(etcData.get("closesw").equals("true")) {
						strQuery.setLength(0);
						strQuery.append("update cmm0030 set cm_sysmsg=?,				          \n");
						strQuery.append("   CM_SYSGB=?,CM_LASTUPDT=SYSDATE,CM_SYSINFO=?,          \n");
						strQuery.append("   CM_PRCCNT=?,CM_STOPST=?,CM_STOPED=?,CM_SYSTIME=?,     \n");
						strQuery.append("   CM_SYSOPEN=?,CM_SCMOPEN=?,CM_BASESYS=?,CM_PRJNAME=?   \n");
						strQuery.append("   ,CM_DIRBASE=? \n");
						strQuery.append("where cm_syscd=?                                         \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(++parmCnt, etcData.get("cm_sysmsg"));
						pstmt.setString(++parmCnt, etcData.get("cm_sysgb"));
						pstmt.setString(++parmCnt, etcData.get("cm_sysinfo"));
						pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prccnt")));
						pstmt.setString(++parmCnt, etcData.get("cm_stdate"));
						pstmt.setString(++parmCnt, etcData.get("cm_eddate"));
						if (etcData.get("cm_sysinfo").substring(5,6).equals("1"))
							pstmt.setString(++parmCnt, etcData.get("cm_systime"));
						else
						    pstmt.setString(++parmCnt, "");
						pstmt.setString(++parmCnt, etcData.get("sysopen"));
						pstmt.setString(++parmCnt, etcData.get("scmopen"));
						pstmt.setString(++parmCnt, etcData.get("basesys"));
						pstmt.setString(++parmCnt, etcData.get("prjname"));
						pstmt.setString(++parmCnt, etcData.get("cm_dirbase"));
						pstmt.setString(++parmCnt, strSysCd);						
					} else {
						return "failed";
					}
				}
				rs.close();
			}
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
//			strQuery.setLength(0);
//			strQuery.append("update cmm0030 set cm_prccnt=?                  \n");
//			strQuery.append(" where cm_sysgb=?                               \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt.setInt(1, Integer.parseInt(etcData.get("cm_prccnt")));
//			pstmt.setString(2, etcData.get("cm_sysgb"));
//			pstmt.executeUpdate();
//			pstmt.close();

			parmCnt = 0;
			if (etcData.get("cm_jobcd") != "" && etcData.get("cm_jobcd") != null) {
				strQuery.setLength(0);
				strQuery.append("update cmm0034 set cm_closedt=SYSDATE                    \n");
				strQuery.append("where cm_syscd=?                                         \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, strSysCd);
				pstmt.executeUpdate();
				pstmt.close();

				String[] strJob = etcData.get("cm_jobcd").split(",");
				int      i = 0;

				for (i=0;strJob.length>i;i++) {
					strQuery.setLength(0);
					parmCnt = 0;
					strQuery.append("select count(*) cnt from cmm0034         \n");
					strQuery.append(" where cm_syscd=? and cm_jobcd=?         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(++parmCnt, strSysCd);
					pstmt.setString(++parmCnt, strJob[i]);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if (rs.getInt("cnt") == 0) isrtSw = true;
						else isrtSw = false;
					}
					pstmt.close();

					strQuery.setLength(0);
					parmCnt = 0;
					if (isrtSw == true) {
						strQuery.append("insert into cmm0034                                 \n");
						strQuery.append(" (cm_syscd,cm_jobcd,cm_creatdt,cm_lastdt,cm_editor) \n");
						strQuery.append(" values (?, ?, SYSDATE, SYSDATE, ?)                 \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(++parmCnt, strSysCd);
						pstmt.setString(++parmCnt, strJob[i]);
						pstmt.setString(++parmCnt, etcData.get("cm_editor"));
					} else {
						strQuery.append("update cmm0034 set cm_lastdt=SYSDATE,cm_editor=?,   \n");
						strQuery.append("                   cm_closedt=''                    \n");
						strQuery.append(" where cm_syscd=? and cm_jobcd=?                    \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(++parmCnt, etcData.get("cm_editor"));
						pstmt.setString(++parmCnt, strSysCd);
						pstmt.setString(++parmCnt, strJob[i]);
					}
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return strSysCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200.sysInfo_Updt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.sysInfo_Updt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.sysInfo_Updt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.sysInfo_Updt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200.sysInfo_Updt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String sysInfo_Close(String SysCd,String UserId) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("update cmm0030 set cm_closedt=SYSDATE,          \n");
			strQuery.append("   cm_lastupdt=SYSDATE                          \n");
			strQuery.append("where cm_syscd=?                                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.executeUpdate();
			pstmt.close();

			conn.close();
			pstmt = null;
			conn = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200.sysInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.sysInfo_Close() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.sysInfo_Close() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.sysInfo_Close() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200.sysInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of sysInfo_Close


    /** ï¿½Ã½ï¿½ï¿½Û¼Ó¼ï¿½[ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½]ï¿½ï¿½ï¿½ï¿½ ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½Ã°ï¿½ ï¿½ï¿½È¸
	 * @return ArrayList<HashMap<String, String>>
	 * @throws SQLException
	 * @throws Exception
     */
    public Object[] getReleaseTime() throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_syscd,cm_sysmsg,cm_systime, \n");
			strQuery.append("       decode(substr(cm_sysinfo,6,1),'1','true','false') checked \n");
			strQuery.append("  from cmm0030 \n");
			strQuery.append(" where cm_closedt is null \n");
			strQuery.append(" order by cm_syscd \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("checked", rs.getString("checked"));
				if (rs.getString("cm_systime") != null && rs.getString("cm_systime") != ""){
					rst.put("cm_systime", rs.getString("cm_systime").substring(0,2)+":"+rs.getString("cm_systime").substring(2));
				}else {
					rst.put("cm_systime", "");
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

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200.getReleaseTime() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.getReleaseTime() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.getReleaseTime() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.getReleaseTime() Exception END ##");
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
					ecamsLogger.error("## Cmm0200.getReleaseTime() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	/** ï¿½Ã½ï¿½ï¿½Û¼Ó¼ï¿½[ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½] Ã¼Å© ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½Ã°ï¿½ ï¿½ï¿½ï¿½ï¿½
	 * @param chkSysList : ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ ï¿½ï¿½ ï¿½Ã½ï¿½ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½Æ®
	 * @param releaseTime : ï¿½ï¿½ï¿½ï¿½ï¿½Ã°ï¿½
	 * @return 0 : ï¿½ï¿½ï¿½ï¿½
	 * @throws SQLException
	 * @throws Exception
	 */
	public int setReleaseTime(ArrayList<HashMap<String,String>> sysList,String releaseTime) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

		    //ï¿½ï¿½ï¿½Ã½ï¿½ï¿½Û¿ï¿½ ï¿½ï¿½ï¿½Ø¼ï¿½ ï¿½Ã½ï¿½ï¿½Û¼Ó¼ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ Ã¼Å© ï¿½ï¿½ï¿½ï¿½ï¿½Ï°ï¿½ CM_SYSTIME ï¿½ï¿½ï¿½ï¿½ Å¬ï¿½ï¿½ï¿½ï¿½ï¿½Ñ´ï¿½.
			/*strQuery.setLength(0);
			strQuery.append("UPDATE CMM0030 set CM_SYSTIME='', \n");
			strQuery.append("       CM_SYSINFO = SUBSTR(CM_SYSINFO,1,5) || '0' || substr(CM_SYSINFO,7) \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.executeUpdate();
			pstmt.close();
			*/

			//ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ï´ï¿½ ï¿½Ã½ï¿½ï¿½Û¸ï¿½ ï¿½Ã½ï¿½ï¿½Û¼Ó¼ï¿½ Ã¼Å©ï¿½Ï°ï¿½, CM_SYSTIME ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½ï¿½Ñ´ï¿½.
			for (int i = 0 ; sysList.size() > i; i++) {
				strQuery.setLength(0);
				strQuery.append("UPDATE CMM0030 set \n");
				strQuery.append("       CM_SYSTIME=?,CM_SYSINFO = SUBSTR(CM_SYSINFO,1,5) || ? || substr(CM_SYSINFO,7) \n");
				strQuery.append(" WHERE CM_SYSCD=? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				if (sysList.get(i).get("checked").equals("true")){
					pstmt.setString(1, releaseTime);
					pstmt.setString(2, "1");
				} else{
					pstmt.setString(1, "");
					pstmt.setString(2, "0");
				}
				pstmt.setString(3, sysList.get(i).get("cm_syscd"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.commit();
	        conn.close();

	        return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200.setReleaseTime() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.setReleaseTime() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.setReleaseTime() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.setReleaseTime() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200.setReleaseTime() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setReleaseTime() method statement
	
	public String process_Gb(String syscd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		ConnectionContext connectionContext = new ConnectionResource();
		String			  rst		  = null;

		try {

			conn = connectionContext.getConnection();

			Boolean testGb = false;
			Boolean runGb = false;
			Boolean checkGb = false;
			
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt	   \n");
			strQuery.append("  from cmm0031 		   \n");
			strQuery.append("where cm_syscd = ?		   \n");
			strQuery.append("  and cm_svrcd = '15'	   \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, syscd);
            rs = pstmt.executeQuery();

			if( rs.next() ) {
				if( rs.getInt("cnt") > 0 ){
					testGb = true;
				}
			}
			pstmt.close();
			rs.close();
			
			
			strQuery.setLength(0);
			strQuery.append("select substr(cm_sysinfo, 10,1) Gb	\n");
			strQuery.append("  from cmm0030 		   			\n");
			strQuery.append("where cm_syscd = ?		   			\n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, syscd);
            rs = pstmt.executeQuery();

			if( rs.next() ) {
				if( rs.getInt("Gb") == 0 ){
					runGb = true;
				}
			}
			pstmt.close();
			rs.close();
			
			strQuery.setLength(0);
			strQuery.append("select substr(cm_sysinfo, 12,1) checkGb \n");
			strQuery.append("  from cmm0030 		   			     \n");
			strQuery.append(" where cm_closedt is null 			     \n");
			strQuery.append("   and cm_syscd = ?		   		     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, syscd);
            rs = pstmt.executeQuery();

			if( rs.next() ) {
				if( rs.getInt("checkGb") == 1 ){
					checkGb = true;
				}
			}
			pstmt.close();
			rs.close();
			
			if( runGb ){
				if( testGb ) {
					if ( checkGb ) {
						rst = "ÇÁ·Î¼¼½º 1, Å×½ºÆ®¹èÆ÷[O] °³¹ß°Ë¼ö[O]";
					} else {
						rst = "ÇÁ·Î¼¼½º 2, Å×½ºÆ®¹èÆ÷[O] °³¹ß°Ë¼ö[X]";
					}
				} else {
					if ( checkGb ) {
						rst = "ÇÁ·Î¼¼½º 3, Å×½ºÆ®¹èÆ÷[X] °³¹ß°Ë¼ö[O]";
					} else {
						rst = "ÇÁ·Î¼¼½º 4, Å×½ºÆ®¹èÆ÷[X] °³¹ß°Ë¼ö[X]";
					}
				}
			} else {
				rst = "ÇÁ·Î¼¼½º 5, SR »ç¿ë¾ÈÇÔ";
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rst;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200.process_Gb() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200.process_Gb() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200.process_Gb() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200.process_Gb() Exception END ##");
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
					ecamsLogger.error("## Cmm0200.process_Gb() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}//end of process_Gb() method statement
	}

}//end of Cmm0200 class statement
