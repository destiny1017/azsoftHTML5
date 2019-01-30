
/*****************************************************************************************
	1. program ID	: eCmd3100.java
	2. create date	: 2008.07. 10
	3. auth		    : YOYO.JJANG
	4. update date	:
	5. auth		    :
	6. description	: eCmd3100
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import app.eCmr.Cmr0200;



/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd1200{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * project 등록
	 * @param  pPrjno,pDocname,pDocPath
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

    public Object[] getBldCd() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			String svBldGbn = "";

			strQuery.setLength(0);
			strQuery.append("select b.cm_gbncd,b.cm_bldcd from cmm0022 b \n");
			strQuery.append("group by b.cm_gbncd,b.cm_bldcd              \n");
			strQuery.append("order by b.cm_gbncd,b.cm_bldcd              \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				if (svBldGbn.length() == 0 || !svBldGbn.equals(rs.getString("cm_gbncd"))) {
					rst = new HashMap<String, String>();
					rst.put("cm_micode", "00");
					rst.put("cm_bldgbn", rs.getString("cm_gbncd"));
					rst.put("cm_codename", "유형신규등록");
		           	rsval.add(rst);
		           	rst = null;
		           	svBldGbn = rs.getString("cm_gbncd");
				}
				rst = new HashMap<String, String>();
				rst.put("cm_bldgbn", rs.getString("cm_gbncd"));
				rst.put("cm_micode", rs.getString("cm_bldcd"));
				rst.put("cm_codename", "처리유형" + rs.getString("cm_bldcd"));
	           	rsval.add(rst);
	           	rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCbo_BldCd0() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCbo_BldCd0() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCbo_BldCd0() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCbo_BldCd0() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCbo_BldCd0() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getCbo_BldCd0() method statement

    public Object[] getPrgCd(String syscd, String prgname) throws SQLException, Exception {
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
			strQuery.append("select a.cr_itemid, a.cr_rsrcname, a.cr_rsrccd, a.cr_jobcd, c.cm_dirpath, d.cm_codename   \n");
			strQuery.append("  from cmr0020 a, cmm0036 b, cmm0070 c, cmm0020 d							\n");
			strQuery.append(" where substr(b.cm_info,29,1) = '1' 										\n");
			strQuery.append("   and a.cr_syscd = b.cm_syscd 											\n");
			strQuery.append("   and a.cr_syscd = ? 														\n");
			if(prgname != null && prgname != ""){
				strQuery.append("   and upper(a.cr_rsrcname) like ?                                     \n");
			}
			strQuery.append("   and a.cr_rsrccd = b.cm_rsrccd 											\n");
			strQuery.append("   and a.cr_syscd = c.cm_syscd 											\n");
			strQuery.append("   and a.cr_dsncd = c.cm_dsncd 											\n");
			strQuery.append("   and d.cm_macode = 'JAWON'                                               \n");
			strQuery.append("   and a.cr_rsrccd = d.cm_micode                                           \n");
			strQuery.append("   order by a.cr_rsrcname, d.cm_micode                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, syscd);
            if(prgname != null && prgname != ""){
            	pstmt.setString(2, "%"+prgname.toUpperCase()+"%");
            }
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
				rst.put("cr_jobcd", rs.getString("cr_jobcd"));
				rst.put("rsrcname", rs.getString("cr_rsrcname")+" ["+rs.getString("cm_codename")+"]");
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cm_codename", rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cnd1200.getPrgCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getPrgCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getPrgCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getPrgCd() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getPrgCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getPrgCd() method statement

    public Object[] getScript(String Cbo_BldGbn_code,String Cbo_BldCd0_code) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_seq,cm_cmdname,cm_errword,    \n");
			strQuery.append("       NVL(cm_runtype,'R') cm_runtype,  \n");
			strQuery.append("       NVL(cm_viewyn,'N') cm_viewyn     \n");
			strQuery.append("  from cmm0022  \n");
			strQuery.append("where cm_gbncd=?  \n");//Cbo_BldGbn_code
			strQuery.append("  and cm_bldcd=? \n");//Cbo_BldCd0_code
			strQuery.append("order by cm_seq \n");
            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, Cbo_BldGbn_code);
            pstmt.setString(2, Cbo_BldCd0_code);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", "Lv_File0");
				rst.put("cm_seq", rs.getString("cm_seq"));
				rst.put("cm_cmdname", rs.getString("cm_cmdname"));
				rst.put("cm_errword", rs.getString("cm_errword"));
				rst.put("cm_runtype", rs.getString("cm_runtype"));
				rst.put("cm_viewyn", rs.getString("cm_viewyn"));
	           	rsval.add(rst);
	           	rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getSql_Qry_Sub() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getSql_Qry_Sub() method statement

    public Object[] getQryCd(String SysCd,String TstSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		String            svReq = "";
		boolean           findSw = false;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
	        strQuery.append("select a.cm_reqcd,a.cm_jobcd,b.cm_codename, \n");
	        strQuery.append("       c.cm_codename prcsys                 \n");
	        strQuery.append("  from cmm0020 c,cmm0020 b,cmm0060 a        \n");
	        strQuery.append(" where a.cm_syscd=? and a.cm_manid='1'      \n");
	        strQuery.append("   and a.cm_gubun='1'                       \n");
	        if (TstSw.equals("1"))
	        	strQuery.append("and a.cm_reqcd in ('01','02','03','04','06','07','11','12') \n");
	        else
	        	strQuery.append("and a.cm_reqcd in ('01','02','04','06','07','11') \n");
	        strQuery.append("   and b.cm_macode='REQUEST'                \n");
	        strQuery.append("   and b.cm_micode=a.cm_reqcd               \n");
	        strQuery.append("   and c.cm_macode='SYSGBN'                 \n");
	        strQuery.append("   and c.cm_micode=a.cm_jobcd               \n");
	        strQuery.append(" order by a.cm_reqcd,a.cm_seqno             \n");
            pstmt = conn.prepareStatement(strQuery.toString());
//            pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
//            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while(rs.next()){
	        	findSw = false;

	        	if (svReq.length() == 0) findSw = true;
	        	else if (!svReq.equals(rs.getString("cm_reqcd"))) {
	        		findSw = true;

	        		if (svReq.equals("04") || svReq.equals("03")) {
	        			if (svReq.equals("04")) {
		        			rst = new HashMap<String, String>();
		    		        rst.put("ID", "PRCSYS");
		    		        rst.put("cm_reqcd", svReq);
		    		        rst.put("cm_jobcd", "SYSCN");
		    				rst.put("prcsys", "취소처리");
		    				rsval.add(rst);
	        			}
	    				rst = new HashMap<String, String>();
	    		        rst.put("ID", "PRCSYS");
	    		        rst.put("cm_reqcd", svReq);
	    		        rst.put("cm_jobcd", "SYSCED");
	    				rst.put("prcsys", "원복처리");
	    				rsval.add(rst);
	        		} else if (svReq.equals("06")) {
	        			rst = new HashMap<String, String>();
	    		        rst.put("ID", "PRCSYS");
	    		        rst.put("cm_reqcd", svReq);
	    		        rst.put("cm_jobcd", "SYSCN");
	    				rst.put("prcsys", "취소처리");
	    				rsval.add(rst);
	        		}
	        	}

	        	rst = new HashMap<String, String>();
	        	if (findSw == true) {
	        		rst.put("ID", "QRYCD");
	        		svReq = rs.getString("cm_reqcd");
	        	} else {
	        		rst.put("ID", "PRCSYS");
	        	}
	        	rst.put("cm_reqcd", rs.getString("cm_reqcd"));
	        	rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("prcsys", rs.getString("prcsys"));
	           	rsval.add(rst);
	           	rst = null;
	        }
	        rs.close();
	        pstmt.close();

	        if (rsval.size()>0) {
	        	if (svReq.equals("04") || svReq.equals("03")) {
	        		if (svReq.equals("04")) {
				        rst = new HashMap<String, String>();
				        rst.put("ID", "PRCSYS");
				        rst.put("cm_reqcd", svReq);
				        rst.put("cm_jobcd", "SYSCN");
						rst.put("prcsys", "취소처리");
						rsval.add(rst);
	        		}
	        	} else if (svReq.equals("06")) {
					rst = new HashMap<String, String>();
			        rst.put("ID", "PRCSYS");
			        rst.put("cm_reqcd", svReq);
			        rst.put("cm_jobcd", "SYSCN");
					rst.put("prcsys", "취소처리");
					rsval.add(rst);
	        	}
	        }
			rtObj = rsval.toArray();
			rsval = null;

			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getQryCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getQryCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getQryCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getQryCd() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getQryCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCbo_SysCd_Click() method statement


    public Object[] getBldList(String SysCd,String TstSw,String QryCd,String SysInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String            sysConf     = "";

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
	        strQuery.append("select cm_jobcd from cmm0060     \n");
	        strQuery.append(" where cm_syscd=? and cm_reqcd=? \n");
	        strQuery.append("   and cm_gubun='1'              \n");
	        strQuery.append(" group by cm_jobcd               \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, QryCd);
            rs = pstmt.executeQuery();
	        while(rs.next()){
	        	sysConf = sysConf + rs.getString("cm_jobcd")+",";
	        }
	        rs.close();
	        pstmt.close();

	        strQuery.setLength(0);
	        strQuery.append("select b.cm_micode,b.cm_codename  \n");
	        strQuery.append("  from cmm0020 b,cmm0036 a        \n");
	        strQuery.append(" where a.cm_syscd=?               \n");//Cbo_SysCd_code
	        strQuery.append("   and b.cm_macode='JAWON'        \n");//Cbo_SysCd_code
	        strQuery.append("   and a.cm_rsrccd=b.cm_micode    \n");//Cbo_SysCd_code
	        if (QryCd.equals("01") || QryCd.equals("02")) {
	        	strQuery.append("and (substr(a.cm_info,6,1)='1'   \n");
	        	strQuery.append("  or substr(a.cm_info,14,1)='1') \n");
	        } else if (QryCd.equals("07"))  {
	        	strQuery.append("and (substr(a.cm_info,18,1)='1' \n"); //폐기시파일삭제
	        	if (sysConf.indexOf("SYSUP")>=0 && !QryCd.equals("06")) {
	        		strQuery.append("or substr(a.cm_info,22,1)='1' \n"); //형상관리저장스크립트
	        	}
	        	if (sysConf.indexOf("SYSCB")>=0) {
	        		strQuery.append("or substr(a.cm_info,39,1)='1'  \n"); //컴파일
	        	}
	        	if (sysConf.indexOf("SYSED")>=0 || sysConf.indexOf("SYSCED")>=0) {
	        		strQuery.append("or substr(a.cm_info,51,1)='1' \n"); //릴리즈스크립트
	        	}
	        	if (sysConf.indexOf("SYSRC")>=0) {
	        		strQuery.append("or substr(a.cm_info,59,1)='1' \n"); //적용스크립트
	        	}
	        	strQuery.append("or substr(a.cm_info,17,1)='1') \n"); //체크인취소쉘실행
	        } else if (QryCd.equals("03"))  {
	        	strQuery.append("and (substr(a.cm_info,18,1)='1' \n"); //폐기시파일삭제
	        	if (sysConf.indexOf("SYSUP")>=0 && !QryCd.equals("06")) {
	        		strQuery.append("or substr(a.cm_info,22,1)='1' \n"); //형상관리저장스크립트
	        	}
	        	if (sysConf.indexOf("SYSCB")>=0) {
	        		strQuery.append("or substr(a.cm_info,61,1)='1'  \n"); //컴파일
	        	}
	        	if (sysConf.indexOf("SYSED")>=0 || sysConf.indexOf("SYSCED")>=0) {
	        		strQuery.append("or substr(a.cm_info,64,1)='1' \n"); //릴리즈스크립트
	        	}
	        	if (sysConf.indexOf("SYSRC")>=0) {
	        		strQuery.append("or substr(a.cm_info,67,1)='1' \n"); //적용스크립트
	        	}
	        	strQuery.append("or substr(a.cm_info,17,1)='1') \n"); //체크인취소쉘실행
	        } else {
	        	strQuery.append("and (substr(a.cm_info,18,1)='1' \n"); //폐기시파일삭제
	        	if (QryCd.equals("12")) {
	        		strQuery.append("or substr(a.cm_info,6,1)='1'    \n"); //Lock관리
	        	} else if (sysConf.indexOf("SYSRK")>=0) {
	        		strQuery.append("or substr(a.cm_info,6,1)='1'   \n"); //Lock관리
	        	}
	        	if (sysConf.indexOf("SYSFT")>=0) {
	        		strQuery.append("or substr(a.cm_info,28,1)='1' \n"); //웹취약성검증
	        	}
	        	if (sysConf.indexOf("SYSUP")>=0 && !QryCd.equals("06")) {
	        		strQuery.append("or substr(a.cm_info,22,1)='1' \n"); //형상관리저장스크립트
	        	}
	        	if (sysConf.indexOf("SYSCB")>=0) {
	        		strQuery.append("or substr(a.cm_info,1,1)='1'  \n"); //컴파일
	        	}
	        	if (sysConf.indexOf("SYSED")>=0 || sysConf.indexOf("SYSCED")>=0) {
	        		strQuery.append("or substr(a.cm_info,21,1)='1' \n"); //릴리즈스크립트
	        	}
	        	if (sysConf.indexOf("SYSRC")>=0) {
	        		strQuery.append("or substr(a.cm_info,35,1)='1' \n"); //적용스크립트
	        	}
	        	if (QryCd.equals("04")) {
	        		strQuery.append("or substr(a.cm_info,17,1)='1') \n"); //체크인취소쉘실행
	        	} else {
	        		strQuery.append(") \n");
	        	}
	        }
	        strQuery.append(" and a.cm_closedt is null \n");
	        strQuery.append(" order by b.cm_seqno \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", "RSRCCD");
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
	           	rsval.add(rst);
	           	rst = null;
	        }
	        rs.close();
	        pstmt.close();

			strQuery.setLength(0);
		    strQuery.append("select c.cm_codename,a.cm_jobcd,a.cm_rsrccd,a.cm_bldcd,   \n");
		    if (SysInfo.substring(7,8).equals("1")) strQuery.append("b.cm_jobname,     \n");
		    strQuery.append("       a.cm_prcsys,d.cm_codename prcsys,a.cm_bldgbn,      \n");
		    strQuery.append("       a.CM_RUNGBN,a.CM_RUNPOS,a.CM_SEQYN,a.CM_TOTYN,     \n");
		    strQuery.append("       a.CM_USERYN                                        \n");
    	    strQuery.append("  from cmm0020 d,cmm0020 c,cmm0033 a                      \n");
    	    if (SysInfo.substring(7,8).equals("1")) strQuery.append(",cmm0102 b        \n");
    	    strQuery.append(" where a.cm_syscd=? and a.cm_qrycd=?                      \n");//Cbo_SysCd_code
    	    if (SysInfo.substring(7,8).equals("1"))
    	    	strQuery.append("   and a.cm_jobcd=b.cm_jobcd                          \n");
    	    strQuery.append("   and a.cm_itemid='************'                         \n");
    	    strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cm_rsrccd    \n");
    	    strQuery.append("   and d.cm_macode='SYSGBN' and d.cm_micode=a.cm_prcsys   \n");
    	    strQuery.append("order by c.cm_codename,d.cm_seqno,a.cm_jobcd              \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, QryCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

	        while(rs.next()){
	        	rst = new HashMap<String, String>();

				rst.put("ID", "BLDLIST");
				rst.put("cm_codename", rs.getString("cm_codename"));
				if (SysInfo.substring(7,8).equals("1")) rst.put("cm_jobname", rs.getString("cm_jobname"));
				else rst.put("cm_jobname", "모든업무");
				rst.put("prcsys", rs.getString("prcsys"));
				rst.put("bldcd", "처리유형"+rs.getString("cm_bldCD"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_bldgbn", rs.getString("cm_bldgbn"));
				rst.put("cm_bldcd", rs.getString("cm_bldcd"));
				rst.put("cm_prcsys", rs.getString("cm_prcsys"));

				rst.put("CM_RUNGBN", rs.getString("CM_RUNGBN"));
				rst.put("CM_RUNPOS", rs.getString("CM_RUNPOS"));
				rst.put("CM_SEQYN", rs.getString("CM_SEQYN"));
				rst.put("CM_TOTYN", rs.getString("CM_TOTYN"));
				rst.put("CM_USERYN", rs.getString("CM_USERYN"));

				if (rs.getString("CM_RUNGBN").equals("B")) rst.put("RUNGBN", "파일송수신전");
				else rst.put("RUNGBN", "파일송수신후");

				if (rs.getString("CM_RUNPOS").equals("L")) rst.put("RUNPOS", "Local");
				else rst.put("RUNPOS", "Remote");

				if (rs.getString("CM_SEQYN").equals("Y")) rst.put("SEQYN", "YES");
				else rst.put("SEQYN", "NO");

				if (rs.getString("CM_TOTYN").equals("Y")) rst.put("TOTYN", "YES");
				else rst.put("TOTYN", "NO");
                
				if (rs.getString("CM_USERYN").equals("Y")) rst.put("USERYN", "YES");
				else rst.put("USERYN", "NO");
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
			ecamsLogger.error("## Cnd1200.getSql_Qry_Tab2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getSql_Qry_Tab2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry_Tab2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getSql_Qry_Tab2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getSql_Qry_Tab2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCbo_SysCd_Click() method statement

    public Object[] getPrgList(String SysCd, String qrycd) throws SQLException, Exception {
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
			strQuery.append("select a.cm_jobcd, a.cm_rsrccd, a.cm_prcsys, 					\n");
			strQuery.append("   	a.cm_bldcd, a.cm_rungbn, a.cm_runpos, a.cm_useryn,  	\n");
			strQuery.append("   	a.cm_noexecyn,									 		\n");
			strQuery.append("   	a.cm_seqyn, a.cm_totyn, a.cm_itemid, a.cm_bldgbn, 		\n");
			strQuery.append("   	b.cm_codename, c.cm_codename prcsys, d.cr_rsrcname		\n");
			strQuery.append("  from cmm0033 a, cmm0020 b, cmm0020 c, cmr0020 d				\n");
			strQuery.append(" where a.cm_syscd = ? 											\n");
			strQuery.append("   and a.cm_rsrccd = b.cm_micode 								\n");
			strQuery.append("   and b.cm_macode = 'JAWON' 									\n");
			strQuery.append("   and a.cm_prcsys = c.cm_micode 								\n");
			strQuery.append("   and c.cm_macode = 'SYSGBN' 									\n");
			strQuery.append("   and a.cm_qrycd = ? 											\n");
			strQuery.append("   and a.cm_syscd = d.cr_syscd                                 \n");
			strQuery.append("   and a.cm_itemid = d.cr_itemid                               \n");
			strQuery.append("   and a.cm_itemid <> '************'                           \n");
			strQuery.append(" order by b.cm_codename,c.cm_seqno,a.cm_jobcd   				\n");


            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, qrycd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

	        while(rs.next()){
	        	rst = new HashMap<String, String>();

	        	rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
	        	rst.put("cm_itemid", rs.getString("cm_itemid"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_jobname", "모든업무");
				rst.put("prcsys", rs.getString("prcsys"));
				rst.put("bldcd", "처리유형"+rs.getString("cm_bldCD"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_bldgbn", rs.getString("cm_bldgbn"));
				rst.put("cm_bldcd", rs.getString("cm_bldcd"));
				rst.put("cm_prcsys", rs.getString("cm_prcsys"));

				rst.put("CM_RUNGBN", rs.getString("CM_RUNGBN"));
				rst.put("CM_RUNPOS", rs.getString("CM_RUNPOS"));
				rst.put("CM_SEQYN", rs.getString("CM_SEQYN"));
				rst.put("CM_TOTYN", rs.getString("CM_TOTYN"));
				rst.put("CM_NOEXECYN", rs.getString("CM_NOEXECYN"));
				

				if (rs.getString("CM_RUNGBN").equals("B")) rst.put("RUNGBN", "파일송수신전");
				else rst.put("RUNGBN", "파일송수신후");

				if (rs.getString("CM_RUNPOS").equals("L")) rst.put("RUNPOS", "Local");
				else rst.put("RUNPOS", "Remote");

				if (rs.getString("CM_SEQYN").equals("Y")) rst.put("SEQYN", "YES");
				else rst.put("SEQYN", "NO");

				if (rs.getString("CM_TOTYN").equals("Y")) rst.put("TOTYN", "YES");
				else rst.put("TOTYN", "NO");

				if (rs.getString("CM_USERYN").equals("Y")) rst.put("USERYN", "YES");
				else rst.put("USERYN", "NO");
				
				if (rs.getString("CM_NOEXECYN").equals("Y")) rst.put("NOEXECYN", "YES");
				else rst.put("NOEXECYN", "NO");				

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
			ecamsLogger.error("## Cnd1200.getPrgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getPrgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getPrgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getPrgList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getPrgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPrgList() method statement

    public Object[] getSql_Qry_Sub_Tab2(String Cbo_BldCd_code,String index) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);

		    strQuery.append("select cm_seq,cm_cmdname  \n");
		    strQuery.append("  from cmm0022   \n");
    		strQuery.append("where cm_bldcd=? \n");//Cbo_BldCd_code
    		strQuery.append("  and cm_gbncd=? \n");//index
    		strQuery.append("order by cm_seq  \n");

    		//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, Cbo_BldCd_code);
            pstmt.setString(2, index);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_seq", rs.getString("cm_seq"));
				rst.put("cm_cmdname", rs.getString("cm_cmdname"));
	           	rsval.add(rst);
	           	rst = null;
	        }
	        rs.close();
	        pstmt.close();
	        conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub_Tab2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub_Tab2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub_Tab2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getSql_Qry_Sub_Tab2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getSql_Qry_Sub_Tab2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getSql_Qry_Sub_Tab2() method statement

    public Object[] getSql_Qry(String Cbo_BldGbn, String msg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select distinct a.cm_gbncd, a.cm_bldcd \n");
			strQuery.append("  from cmm0022 a                       \n");
			strQuery.append(" where a.cm_gbncd=?                    \n");
			if(!"".equals(msg) && null != msg) {
				strQuery.append("   and upper(a.cm_cmdname) like ?  \n");
			}
			strQuery.append(" order by a.cm_bldcd                   \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, Cbo_BldGbn);
            if(!"".equals(msg) && null != msg) {
            	pstmt.setString(2, "%"+msg.toUpperCase()+"%");
            }
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
				strQuery.setLength(0);
				strQuery.append("select a.cm_bldcd,a.cm_seq,a.cm_cmdname,a.cm_errword, \n");
				strQuery.append("       nvl(a.cm_runtype,'R') cm_runtype,              \n");
				strQuery.append("       nvl(a.cm_viewyn,'N') cm_viewyn                 \n");
				strQuery.append("  from cmm0022 a                                      \n");
				strQuery.append(" where a.cm_gbncd=?  and a.cm_bldcd=?                 \n");
				strQuery.append(" order by a.cm_bldcd,a.cm_seq                         \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("cm_gbncd"));
	            pstmt2.setString(2, rs.getString("cm_bldcd"));
	            rs2 = pstmt2.executeQuery();
	            while(rs2.next()){
					rst = new HashMap<String, String>();
					rst.put("cm_codename", "처리유형"+rs2.getString("cm_bldcd"));
					rst.put("cm_errword", rs2.getString("cm_errword"));
					rst.put("cm_seq", rs2.getString("cm_seq"));
					rst.put("cm_cmdname", rs2.getString("cm_cmdname"));
					rst.put("cm_bldcd", rs2.getString("cm_bldcd"));
					rst.put("cm_runtype", rs2.getString("cm_runtype"));
					rst.put("cm_viewyn", rs2.getString("cm_viewyn"));
		           	rsval.add(rst);
		           	rst = null;
	            }
		    	rs2.close();
		    	pstmt2.close();
		    }
		    rs.close();
		    pstmt.close();
		    conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getSql_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getSql_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getSql_Qry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getSql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getSql_Qry_Sub_Tab2() method statement

    public int getCmm0022_Del(String Cbo_BldGbn,String Cbo_BldCd0) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
	    	strQuery.append("DELETE CMM0022 WHERE CM_GBNCD=? \n");//Cbo_BldGbn
	    	strQuery.append("AND CM_BLDCD=? \n");//Cbo_BldCd0
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, Cbo_BldGbn);
            pstmt.setString(2, Cbo_BldCd0);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();
			conn.close();

			pstmt = null;
			conn = null;
            return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0022_Del() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0022_Del() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0022_Del() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0022_Del() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0022_Del() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0022_Del() method statement

    public int getCmm0022_Copy(String Cbo_BldGbn,String Cbo_BldCd0,String NewBld) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

		    strQuery.setLength(0);
		    strQuery.append("insert into cmm0022 (CM_GBNCD,CM_BLDCD,CM_SEQ,CM_CMDNAME,CM_ERRWORD,CM_RUNTYPE,CM_VIEWYN) \n");
		    strQuery.append("(SELECT CM_GBNCD,?,CM_SEQ,CM_CMDNAME,CM_ERRWORD,CM_RUNTYPE,CM_VIEWYN \n");//NewBld
		    strQuery.append("   FROM CMM0022    \n");
		    strQuery.append("  WHERE CM_GBNCD=? \n");//Cbo_BldGbn
		    strQuery.append("    AND CM_BLDCD=?) \n");//Cbo_BldCd0
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, NewBld);
            pstmt.setString(2, Cbo_BldGbn);
            pstmt.setString(3, Cbo_BldCd0);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();
            conn.commit();
            conn.close();
			rs = null;
			pstmt = null;
			conn = null;
            return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0022_Copy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0022_Copy() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0022_Copy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0022_Copy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0022_Copy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0022_Copy() method statement

    public int getCmm0022_DBProc(String Cbo_BldGbn,String Cbo_BldCd0,String Txt_Comp2,String runType,ArrayList<HashMap<String,String>> Lv_File0_dp) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("delete cmm0022 where cm_gbncd=? \n");//Cbo_BldGbn
		    strQuery.append("and cm_bldcd=? \n");//Cbo_BldCd0
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, Cbo_BldGbn);
            pstmt.setString(2, Cbo_BldCd0);
            rtn_cnt = pstmt.executeUpdate();

            pstmt.close();

            int x = 0;
		    for (x = 0 ; x<Lv_File0_dp.size() ; x++){
		    	strQuery.setLength(0);
		    	strQuery.append("insert into cmm0022 (CM_BLDCD,CM_SEQ,CM_CMDNAME,CM_GBNCD,CM_ERRWORD,CM_RUNTYPE,CM_VIEWYN) values (\n");
		    	strQuery.append("?, \n");//Cbo_BldCd0
		    	strQuery.append("?, \n");//Lv_File0.cm_seq
		        strQuery.append("?, \n");//replace(CM_CMDNAME)
		        strQuery.append("?, \n");//Cbo_BldGbn
		        strQuery.append("?, \n");//replace(Txt_Comp2)
		        strQuery.append("?, \n");//runtype
		        strQuery.append("?) \n");//VIEWYN

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());

				pstmt.setString(1, Cbo_BldCd0);
	            pstmt.setString(2, Lv_File0_dp.get(x).get("cm_seq"));
	            pstmt.setString(3, Lv_File0_dp.get(x).get("cm_cmdname"));
	            pstmt.setString(4, Cbo_BldGbn);
	            pstmt.setString(5, Txt_Comp2);
	            pstmt.setString(6, runType);
	            pstmt.setString(7, Lv_File0_dp.get(x).get("cm_viewyn"));

				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rtn_cnt = pstmt.executeUpdate();
	            pstmt.close();
		    }

		    conn.commit();
		    conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		    return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0022_DBProc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0022_DBProc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0022_DBProc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0022_DBProc() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0022_DBProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0022_DBProc() method statement

    public int getCmm0033_DBProc(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmm0033                \n");
			strQuery.append(" where cm_syscd=? and cm_qrycd=?  \n");
			strQuery.append("   and cm_prcsys=?                \n");
			strQuery.append("   and instr(?,cm_rsrccd)>0       \n");
			strQuery.append("   and instr(?,cm_jobcd)>0        \n");
			strQuery.append("   and cm_rungbn = ?              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, etcData.get("cm_syscd"));
            pstmt.setString(2, etcData.get("cm_qrycd"));
            pstmt.setString(3, etcData.get("cm_prcsys"));
            pstmt.setString(4, etcData.get("cm_rsrccd"));
            pstmt.setString(5, etcData.get("cm_jobcd"));
            pstmt.setString(6, etcData.get("CM_RUNGBN"));
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

            String svRsrc[] = etcData.get("cm_rsrccd").split(",");
            String svJob[] = etcData.get("cm_jobcd").split(",");
            int x = 0;
            int y = 0;
		    for (x=0;svRsrc.length>x;x++){
		    	for (y=0;svJob.length>y;y++) {
			    	strQuery.setLength(0);
			    	strQuery.append("insert into cmm0033 \n");
			    	strQuery.append(" (CM_SYSCD,CM_RSRCCD,CM_JOBCD,CM_QRYCD,CM_PRCSYS,CM_BLDGBN,\n");
			    	strQuery.append("  CM_BLDCD,CM_RUNGBN,CM_RUNPOS,CM_SEQYN,CM_TOTYN,CM_ITEMID,\n");
			    	strQuery.append("  CM_USERYN) \n");
			    	strQuery.append("values (\n");
			    	strQuery.append("?, \n");//CM_SYSCD
			    	strQuery.append("?, \n");//CM_RSRCCD
			        strQuery.append("?, \n");//CM_JOBCD
			    	strQuery.append("?, \n");//CM_QRYCD
			    	strQuery.append("?, \n");//CM_PRCSYS
			    	strQuery.append("?, \n");//CM_BLDGBN
			        strQuery.append("?, \n");//CM_BLDCD
			        strQuery.append("?, \n");//CM_RUNGBN
			        strQuery.append("?, \n");//CM_RUNPOS
			        strQuery.append("?, \n");//CM_SEQYN
			        strQuery.append("?, \n");//CM_TOTYN
			        strQuery.append("?, \n");//CM_ITEMID
			        strQuery.append("?) \n");//CM_USERYN

					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt =  new LoggableStatement(conn, strQuery.toString());

					pstmt.setString(1, etcData.get("cm_syscd"));
		            pstmt.setString(2, svRsrc[x]);
		            pstmt.setString(3, svJob[y]);
		            pstmt.setString(4, etcData.get("cm_qrycd"));
		            pstmt.setString(5, etcData.get("cm_prcsys"));
		            pstmt.setString(6, etcData.get("cm_bldgbn"));
		            pstmt.setString(7, etcData.get("cm_bldcd"));
		            pstmt.setString(8, etcData.get("CM_RUNGBN"));
		            pstmt.setString(9, etcData.get("CM_RUNPOS"));
		            pstmt.setString(10, etcData.get("CM_SEQYN"));
		            pstmt.setString(11, etcData.get("CM_TOTYN"));
		            pstmt.setString(12, "************");
		            pstmt.setString(13, etcData.get("CM_USERYN"));

					////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rtn_cnt = pstmt.executeUpdate();
		            pstmt.close();
		    	}
		    }

		    conn.commit();
		    conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		    return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0033_DBProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0033_DBProc() method statement

    public int getCmm0033_DBProc2(HashMap<String,String> etcData, ArrayList<HashMap<String,String>> delList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

            int i = 0;
		    for(i=0; i<delList.size(); i++){
				strQuery.setLength(0);
				strQuery.append("delete cmm0033                		\n");
				strQuery.append(" where cm_syscd=?    				\n");
				strQuery.append("   and cm_qrycd=?    				\n");
				strQuery.append("   and cm_prcsys=?                 \n");
				strQuery.append("   and cm_rsrccd=?       			\n");
				strQuery.append("   and cm_itemid=?        			\n");
				strQuery.append("   and cm_rungbn=?        			\n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, etcData.get("cm_syscd"));
	            pstmt.setString(2, etcData.get("cm_qrycd"));
	            pstmt.setString(3, etcData.get("cm_prcsys"));
	            pstmt.setString(4, delList.get(i).get("cr_rsrccd"));
	            pstmt.setString(5, delList.get(i).get("cr_itemid"));
	            pstmt.setString(6, etcData.get("CM_RUNGBN"));//실행시점(B:Before,A:After)
	            //pstmt.setString(6, delList.get(i).get("CM_RUNGBN"));//실행시점(B:Before,A:After)
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            pstmt.executeUpdate();
	            pstmt.close();

		    	strQuery.setLength(0);
		    	strQuery.append("insert into cmm0033 \n");
		    	strQuery.append(" (CM_SYSCD,CM_RSRCCD,CM_JOBCD,CM_QRYCD,CM_PRCSYS,CM_BLDGBN,\n");
		    	strQuery.append("  CM_BLDCD,CM_RUNGBN,CM_RUNPOS,CM_SEQYN,CM_TOTYN,CM_ITEMID,\n");
		    	strQuery.append("  CM_USERYN, CM_NOEXECYN) \n");
		    	strQuery.append("values (\n");
		    	strQuery.append("?, \n");//CM_SYSCD
		    	strQuery.append("?, \n");//CM_RSRCCD
		        strQuery.append("?, \n");//CM_JOBCD
		    	strQuery.append("?, \n");//CM_QRYCD
		    	strQuery.append("?, \n");//CM_PRCSYS
		    	strQuery.append("?, \n");//CM_BLDGBN
		        strQuery.append("?, \n");//CM_BLDCD
		        strQuery.append("?, \n");//CM_RUNGBN
		        strQuery.append("?, \n");//CM_RUNPOS
		        strQuery.append("?, \n");//CM_SEQYN
		        strQuery.append("?, \n");//CM_TOTYN
		        strQuery.append("?, \n");//CM_ITEMID
		        strQuery.append("?, \n");//CM_USERYN
		        strQuery.append("?) \n");//CM_NOEXECYN

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());

				pstmt.setString(1, etcData.get("cm_syscd"));
	            pstmt.setString(2, delList.get(i).get("cr_rsrccd"));
	            pstmt.setString(3, "****");
	            pstmt.setString(4, etcData.get("cm_qrycd"));
	            pstmt.setString(5, etcData.get("cm_prcsys"));
	            pstmt.setString(6, etcData.get("cm_bldgbn"));
	            pstmt.setString(7, etcData.get("cm_bldcd"));
	            pstmt.setString(8, etcData.get("CM_RUNGBN"));
	            pstmt.setString(9, etcData.get("CM_RUNPOS"));
	            pstmt.setString(10, etcData.get("CM_SEQYN"));
	            pstmt.setString(11, etcData.get("CM_TOTYN"));
	            pstmt.setString(12, delList.get(i).get("cr_itemid"));
	            pstmt.setString(13, etcData.get("CM_USERYN"));
	            pstmt.setString(14, etcData.get("CM_NOEXECYN"));

				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rtn_cnt = pstmt.executeUpdate();
	            pstmt.close();
		    }

		    conn.commit();
		    conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		    return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0033_DBProc2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0033_DBProc2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0033_DBProc2() method statement

    public int getCmm0033_Del(ArrayList<HashMap<String,String>> delList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			for (int i=0;delList.size()>i;i++) {
				strQuery.setLength(0);
		    	strQuery.append("DELETE cmm0033    \n");
		    	strQuery.append(" WHERE CM_syscd=?     \n");
		    	strQuery.append("   AND CM_qrycd=?     \n");
		    	strQuery.append("   AND CM_prcsys=?    \n");
		    	strQuery.append("   AND CM_rsrccd=?    \n");
		    	strQuery.append("   AND CM_jobcd=?     \n");
		    	strQuery.append("   AND CM_rungbn=?    \n");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, delList.get(i).get("cm_syscd"));
	            pstmt.setString(2, delList.get(i).get("cm_qrycd"));
	            pstmt.setString(3, delList.get(i).get("cm_prcsys"));
	            pstmt.setString(4, delList.get(i).get("cm_rsrccd"));
	            pstmt.setString(5, delList.get(i).get("cm_jobcd"));
	            pstmt.setString(6, delList.get(i).get("cm_rungbn"));
	            pstmt.executeUpdate();
	            pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
            return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_Del() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0033_Del() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_Del() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0033_Del() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0022_Del() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0022_Del() method statement

    public int getCmm0033_Del2(ArrayList<HashMap<String,String>> delList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			
			for (int i=0;delList.size()>i;i++) {
				strQuery.setLength(0);
		    	strQuery.append("DELETE cmm0033    		\n");
		    	strQuery.append(" WHERE CM_syscd=?      \n");
		    	strQuery.append("   AND CM_qrycd=?      \n");
		    	strQuery.append("   AND CM_prcsys=?     \n");
		    	strQuery.append("   AND CM_rsrccd=?     \n");
		    	strQuery.append("   AND CM_ITEMID=?     \n");
		    	strQuery.append("   AND CM_RUNGBN=?     \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, delList.get(i).get("cm_syscd"));
	            pstmt.setString(2, delList.get(i).get("cm_qrycd"));
	            pstmt.setString(3, delList.get(i).get("cm_prcsys"));
	            pstmt.setString(4, delList.get(i).get("cm_rsrccd"));
	            pstmt.setString(5, delList.get(i).get("cm_itemid"));
	            pstmt.setString(6, delList.get(i).get("cm_rungbn"));
	            pstmt.executeUpdate();
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
            return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_Del2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getCmm0033_Del2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getCmm0033_Del2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getCmm0033_Del2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getCmm0033_Del2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCmm0033_Del2() method statement

    public Object[] getProgScript(String sysCD,String reqCD,ArrayList<HashMap<String, String>> progList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		HashMap<String, String>			  rst2	      = null;
		Object[]		  rtObj		  = null;
		String            strCmdName  = "";
		String            retMsg      = "";
		Cmr0200 cmr0200 = new Cmr0200();
		ConnectionContext connectionContext = new ConnectionResource();
		int  i = 0;
		int  j = 0;
		boolean findSw = false;
		boolean syscbSw = false;
		boolean sysedSw = false;
				
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cm_jobcd        \n");
			strQuery.append(" from cmm0060          \n");
			strQuery.append(" where cm_syscd=?      \n");
			strQuery.append("   and cm_reqcd=?      \n");
			strQuery.append("   and cm_gubun='1'    \n");
			strQuery.append("   and cm_manid='1'    \n");
			strQuery.append("   and cm_jobcd in ('SYSCB','SYSED')  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, sysCD);
            pstmt.setString(2, reqCD);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				if (rs.getString("cm_jobcd").equals("SYSCB")) syscbSw = true;
				else sysedSw = true;
			}
			rs.close();
			pstmt.close();
			
			
			for (i=0;progList.size()>i;i++) {	 
				if (syscbSw && progList.get(i).get("compyes").equals("Y")) {
					findSw = false;
					strQuery.setLength(0);
					strQuery.append("select c.cm_cmdname,e.cm_useryn,e.cm_prcsys,e.cm_rungbn,e.cm_bldgbn,e.cm_bldcd,e.cm_noexecyn  \n");
					strQuery.append("  from cmm0033 e,cmm0022 c                      \n");
					strQuery.append(" where e.cm_syscd=? and e.cm_rsrccd=?           \n");
					strQuery.append("   and e.cm_qrycd=?                             \n");
					strQuery.append("   and decode(?,'1',?,'************')=e.cm_itemid \n");
					strQuery.append("   and e.cm_prcsys='SYSCB'                      \n");
					strQuery.append("   and e.cm_bldgbn=c.cm_gbncd                   \n");
					strQuery.append("   and e.cm_bldcd=c.cm_bldcd                    \n");
					strQuery.append("   and c.cm_viewyn='Y'                          \n");
					strQuery.append(" order by c.cm_seq                              \n");
		            pstmt = conn.prepareStatement(strQuery.toString());
		            //pstmt = new LoggableStatement(conn,strQuery.toString());
		            pstmt.setString(1, progList.get(i).get("cr_syscd"));
		            pstmt.setString(2, progList.get(i).get("cr_rsrccd"));
		            pstmt.setString(3, reqCD);
		            pstmt.setString(4, progList.get(i).get("progshl"));
		            pstmt.setString(5, progList.get(i).get("cr_itemid"));
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
					while (rs.next()){
						findSw = true;
						rst = new HashMap<String, String>();
						rst = progList.get(i);
						rst.put("cm_dirpath", progList.get(i).get("cm_dirpath"));
						rst.put("cr_itemid", progList.get(i).get("cr_itemid"));
						rst.put("baseitem", progList.get(i).get("baseitem"));
						rst.put("cr_rsrcname", progList.get(i).get("cr_rsrcname"));
						rst.put("compyn", "Y");
						rst.put("deployyn", "N");
						rst.put("prcseq", progList.get(i).get("prcseq"));
						if (rs.getString("cm_noexecyn").equals("Y")) {
							rst.put("compshl", "해당무");
							rst.put("compchg", "해당무");
							rst.put("edityn1", "N");
						} else {
							strCmdName = rs.getString("cm_cmdname");
							if (strCmdName.lastIndexOf("1>")>0) strCmdName = strCmdName.substring(0,strCmdName.lastIndexOf("1>"));
							strCmdName = strCmdName.replace("?#PARM#", "##PARM##");
							if (strCmdName.indexOf("?#")>=0) {
								rst2 = new HashMap<String,String>();
								rst2.put("rsrcname",progList.get(i).get("cr_rsrcname"));
								rst2.put("dirpath",progList.get(i).get("cm_dirpath"));
								rst2.put("reqcd",reqCD);
								rst2.put("rsrccd",progList.get(i).get("cr_rsrccd"));
								rst2.put("prcsys","SYSCB");
								rst2.put("syscd",progList.get(i).get("cr_syscd"));
								rst2.put("acptno", "");
								rst2.put("samename", strCmdName);
								rst2.put("itemid", progList.get(i).get("cr_itemid"));
								rst2.put("baseitem", progList.get(i).get("baseitem"));
								retMsg = cmr0200.nameChange(rst2, conn);
								if (retMsg.equals("ERROR")) {
									rst.put("compshl", rs.getString("cm_cmdname"));
									rst.put("compchg", rs.getString("cm_cmdname"));
								} else {
									rst.put("compshl", retMsg);
									rst.put("compchg", retMsg);
								}
								rst2 = null;
							} else {
								rst.put("compshl", strCmdName);
								rst.put("compchg", strCmdName);
							}
							if (rs.getString("cm_cmdname").indexOf("?#PARM#")>=0) {
								rst.put("edityn1", "Y");
							} else {
								rst.put("edityn1", "N");
							}
						}
						rst.put("deployshl", "해당무");
						rst.put("deploychg", "해당무");
						rst.put("cm_rungbn", rs.getString("cm_rungbn"));
						rst.put("compbldgbn", rs.getString("cm_bldgbn"));
						rst.put("compbldcd", rs.getString("cm_bldcd"));
						if (rs.getString("cm_rungbn").equals("B")) rst.put("execpos", "파일송신전");
						else rst.put("execpos", "파일송신후");
						
						rst.put("cm_useryn", "N");
						rst.put("chguseryn", "N");
						rst.put("deployerr", "N");
						rst.put("comperr", "N");
			           	rsval.add(rst);
			           	rst = null;
					}
					rs.close();
					pstmt.close();
					
					if (!findSw) {
						rst = new HashMap<String, String>();
						rst = progList.get(i);
						rst.put("compshl", "스크립트정보없음");
						rst.put("compchg", "스크립트정보없음");
						rst.put("comperr", "Y");
						rsval.add(rst);
			           	rst = null;
					}
				}
				if (sysedSw && progList.get(i).get("deployyes").equals("Y")) {
					findSw = false;
					strQuery.setLength(0);
					strQuery.append("select c.cm_cmdname,e.cm_useryn,e.cm_prcsys,e.cm_rungbn,e.cm_bldgbn,e.cm_bldcd,e.cm_noexecyn \n");
					strQuery.append("  from cmm0033 e,cmm0022 c                      \n");
					strQuery.append(" where e.cm_syscd=? and e.cm_rsrccd=?           \n");
					strQuery.append("   and e.cm_qrycd=?                             \n");
					strQuery.append("   and decode(?,'1',?,'************')=e.cm_itemid \n");
					strQuery.append("   and e.cm_prcsys='SYSED'                      \n");
					strQuery.append("   and e.cm_bldgbn=c.cm_gbncd                   \n");
					strQuery.append("   and e.cm_bldcd=c.cm_bldcd                    \n");
					strQuery.append("   and c.cm_viewyn='Y'                          \n");
					strQuery.append(" order by c.cm_seq                              \n");
		            
					//pstmt = conn.prepareStatement(strQuery.toString());
		            pstmt = new LoggableStatement(conn,strQuery.toString());
		            pstmt.setString(1, progList.get(i).get("cr_syscd"));
		            pstmt.setString(2, progList.get(i).get("cr_rsrccd"));
		            pstmt.setString(3, reqCD);
		            pstmt.setString(4, progList.get(i).get("progshl"));
		            pstmt.setString(5, progList.get(i).get("cr_itemid"));
		            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
					while (rs.next()){
						findSw = false;
						rst = new HashMap<String, String>();
						for (j=0;rsval.size()>j;j++) {
							if (rsval.get(j).get("cr_itemid").equals(progList.get(i).get("cr_itemid")) && rsval.get(j).get("cm_rungbn").equals(rs.getString("cm_rungbn"))) {						
								rst = rsval.get(j);
								findSw = true;
								break;
							}
						}
						if (!findSw) {
							rst.put("cm_dirpath", progList.get(i).get("cm_dirpath"));
							rst.put("cr_itemid", progList.get(i).get("cr_itemid"));
							rst.put("baseitem", progList.get(i).get("baseitem"));
							rst.put("cr_rsrcname", progList.get(i).get("cr_rsrcname"));
							rst.put("cm_rungbn", rs.getString("cm_rungbn"));
							rst.put("prcseq", progList.get(i).get("prcseq"));
							rst.put("comperr", "N");
							if (rs.getString("cm_rungbn").equals("B")) rst.put("execpos", "파일송신전");
							else rst.put("execpos", "파일송신후");
							rst.put("compshl", "해당무");
							rst.put("compchg", "해당무");
							rst.put("compyn", "N");
						}

						rst.put("deploybldgbn", rs.getString("cm_bldgbn"));
						rst.put("deploybldcd", rs.getString("cm_bldcd"));
						if (rs.getString("cm_noexecyn").equals("Y")) {
							rst.put("deployshl", "해당무");
							rst.put("deploychg", "해당무");
							rst.put("edityn2", "N");
							rst.put("cm_useryn", "N");
						} else {
							strCmdName = rs.getString("cm_cmdname");
							if (strCmdName.lastIndexOf("1>")>0) strCmdName = strCmdName.substring(0,strCmdName.lastIndexOf("1>"));
							strCmdName = strCmdName.replace("?#PARM#", "##PARM##");
							if (strCmdName.indexOf("?#")>=0) {
								rst2 = new HashMap<String,String>();
								rst2.put("rsrcname",progList.get(i).get("cr_rsrcname"));
								rst2.put("dirpath",progList.get(i).get("cm_dirpath"));
								rst2.put("reqcd",reqCD);
								rst2.put("rsrccd",progList.get(i).get("cr_rsrccd"));
								rst2.put("prcsys","SYSED");
								rst2.put("syscd",progList.get(i).get("cr_syscd"));
								rst2.put("acptno", "");
								rst2.put("samename", strCmdName);
								rst2.put("itemid", progList.get(i).get("cr_itemid"));
								rst2.put("baseitem", progList.get(i).get("baseitem"));
								retMsg = cmr0200.nameChange(rst2, conn);
								if (retMsg.equals("ERROR")) {
									rst.put("deployshl", rs.getString("cm_cmdname"));
									rst.put("deploychg", rs.getString("cm_cmdname"));
								} else {
									rst.put("deployshl", retMsg);
									rst.put("deploychg", retMsg);
								}
								rst2 = null;
							} else {
								rst.put("deployshl", strCmdName);
								rst.put("deploychg", strCmdName);
							}
							if (rs.getString("cm_cmdname").indexOf("?#PARM#")>=0) {
								rst.put("edityn2", "Y");
							} else {
								rst.put("edityn2", "N");
							}
							rst.put("cm_useryn", rs.getString("cm_useryn"));
						}
						
						
						rst.put("chguseryn", "Y");
						rst.put("deployerr", "N");
						rst.put("deployyn", "Y");
						if (!findSw) {
							rsval.add(rst);
						} else {
							rsval.set(i,rst);
						}
			           	rst = null;
			           	findSw = true;
					}
					rs.close();
					pstmt.close();
					
					if (!findSw) {
						rst = new HashMap<String, String>();
						rst = progList.get(i);
						rst.put("deployshl", "스크립트정보없음");
						rst.put("deploychg", "스크립트정보없음");
						rst.put("deployerr", "Y");
						rsval.add(rst);
			           	rst = null;
					}
				}
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getProgScript() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cnd1200.getProgScript() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cnd1200.getProgScript() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cnd1200.getProgScript() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cnd1200.getProgScript() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getProgScript() method statement
    public Object[] getProgInfo(String sysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		int               cnt         = 0;
		int               i,j,k       = 0;
		ArrayList<HashMap<String, String>>  svrList = new ArrayList<HashMap<String, String>>();
		

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_svrname,a.cm_svrcd,b.cm_rsrccd,c.cm_dirbase,b.cm_volpath \n");
			strQuery.append("  from cmm0038 b,cmm0031 a,cmm0030 c   \n");
			strQuery.append(" where c.cm_syscd=?                    \n");
			strQuery.append("   and c.cm_syscd=a.cm_syscd           \n");
			strQuery.append("   and a.cm_closedt is null            \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd           \n");
			strQuery.append("   and a.cm_svrcd=b.cm_svrcd           \n");
			strQuery.append("   and a.cm_seqno=b.cm_seqno           \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	rst = new HashMap<String, String>();
		    	rst.put("cm_svrcd", rs.getString("cm_svrcd"));
		    	rst.put("cm_svrname", rs.getString("cm_svrname"));
		    	rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
		    	if (rs.getString("cm_dirbase").equals(rs.getString("cm_svrcd"))) {
		    		rst.put("dirbase", "Y");
		    	} else {
		    		rst.put("dirbase", "N");
		    	}
		    	rst.put("cm_volpath", rs.getString("cm_volpath"));
		    	svrList.add(rst);
		    	rst = null;
		    }
		    rs.close();
		    pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select b.cm_micode,b.cm_codename,a.cm_info \n");
			strQuery.append("  from cmm0020 b,cmm0036 a             \n");
			strQuery.append(" where a.cm_syscd=?                    \n");
			strQuery.append("   and a.cm_closedt is null            \n");
			strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037  \n");
			strQuery.append("                            where cm_syscd=a.cm_syscd)      \n");
			strQuery.append("   and b.cm_macode='JAWON'             \n");
			strQuery.append("   and b.cm_micode=a.cm_rsrccd         \n");
			strQuery.append(" order by b.cm_codename                \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	cnt = 0;
		    	rst = new HashMap<String, String>();
		    	rst.put("cm_codename", rs.getString("cm_codename"));
		    	rst.put("cm_rsrccd", rs.getString("cm_micode"));
		    	rst.put("cm_info",rs.getString("cm_info"));
		    	rst.put("etc1","");
		    	if (rs.getString("cm_info").substring(3,4).equals("1")) {
		    		cnt = 0;
		    		strQuery.setLength(0);
		    		strQuery.append("select b.cm_micode,b.cm_codename,a.cm_info,c.cm_samedir,c.cm_basedir,c.cm_basename,c.cm_samename \n");
		    		strQuery.append("  from cmm0020 b,cmm0036 a,cmm0037 c   \n");
					strQuery.append(" where c.cm_syscd=?                    \n");
					strQuery.append("   and c.cm_rsrccd=?                   \n");
					strQuery.append("   and c.cm_factcd='04'                \n");
					strQuery.append("   and c.cm_syscd=a.cm_syscd           \n");
					strQuery.append("   and c.cm_samersrc=a.cm_rsrccd       \n");
					strQuery.append("   and a.cm_closedt is null            \n");
					strQuery.append("   and b.cm_macode='JAWON'             \n");
					strQuery.append("   and b.cm_micode=a.cm_rsrccd         \n");
					strQuery.append(" order by b.cm_codename                \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            //pstmt2 =  new LoggableStatement(conn, strQuery.toString());
		            pstmt2.setString(1, sysCd);
		            pstmt2.setString(2, rs.getString("cm_micode"));
		            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
				    while(rs2.next()){
				    	++cnt;
				    	if (cnt>1) {
				    		rst = new HashMap<String, String>();
					    	rst.put("cm_codename", "");
					    	rst.put("cm_rsrccd", rs.getString("cm_micode"));
					    	rst.put("cm_info",rs.getString("cm_info"));
				    	} 
				    	rst.put("cm_samersrc", rs2.getString("cm_micode"));
				    	rst.put("module", rs2.getString("cm_codename"));
				    	rst.put("cm_sameinfo", rs2.getString("cm_info"));
				    	
				    	//ecamsLogger.error("+++ cm_samedir +++"+rs2.getString("cm_basedir")+"/"+rs2.getString("cm_basename")+"-->"+
				    	//		rs2.getString("cm_samedir")+"/"+rs2.getString("cm_samename"));
				    	if (rs2.getString("cm_samedir").indexOf("?#")<0) {
					    	if (rs2.getString("cm_basedir").equals("*")) {
				    			rst.put("etc1", rs2.getString("cm_samedir"));
				    		} else if (rs2.getString("cm_samedir").indexOf("*")<0) {
				    			rst.put("etc1", rs2.getString("cm_basedir")+"->"+rs2.getString("cm_samedir"));
				    		} 
					    	if (rs2.getString("cm_samename").indexOf("?#")<0) {
						    	if (rs2.getString("cm_samename").indexOf("*")<0) {
						    		rst.put("etc1", rst.get("etc1")+"/"+rs2.getString("cm_samename"));
						    	} else if (rs2.getString("cm_samename").equals("*")) {
						    		rst.put("etc1", rst.get("etc1")+"/확장자제외");
						    	} else if (rs2.getString("cm_samename").equals("*.*")) {
						    		rst.put("etc1", rst.get("etc1")+"/"+"리소스명");				    				
						    	} else if (rs2.getString("cm_samename").indexOf("*")>=0) {
						    		rst.put("etc1", rst.get("etc1")+"/"+rs2.getString("cm_samename").replace("*", "확장자제외"));				    				
						    	}
					    	}
				    	}
				    	rsval.add(rst);
				    	rst = null;
				    }
				    rs2.close();
				    pstmt2.close();
		    	}
		    	
		    	if (rs.getString("cm_info").substring(8,9).equals("1")) {
		    		strQuery.setLength(0);
		    		strQuery.append("select b.cm_micode,b.cm_codename,a.cm_info \n");
		    		strQuery.append("  from cmm0020 b,cmm0036 a,cmm0037 c   \n");
					strQuery.append(" where c.cm_syscd=?                    \n");
					strQuery.append("   and c.cm_rsrccd=?                   \n");
					strQuery.append("   and c.cm_factcd='09'                \n");
					strQuery.append("   and c.cm_syscd=a.cm_syscd           \n");
					strQuery.append("   and c.cm_samersrc=a.cm_rsrccd       \n");
					strQuery.append("   and a.cm_closedt is null            \n");
					strQuery.append("   and b.cm_macode='JAWON'             \n");
					strQuery.append("   and b.cm_micode=a.cm_rsrccd         \n");
					strQuery.append(" order by b.cm_codename                \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            //pstmt =  new LoggableStatement(conn, strQuery.toString());
		            pstmt2.setString(1, sysCd);
		            pstmt2.setString(2, rs.getString("cm_micode"));
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs2 = pstmt2.executeQuery();
				    while(rs2.next()){	
			    		++cnt;
				    	if (cnt>1) {				    		
				    		rst = new HashMap<String, String>();
					    	rst.put("cm_codename", "");
					    	rst.put("cm_rsrccd", rs.getString("cm_micode"));
					    	rst.put("cm_info",rs.getString("cm_info"));
				    	} 
				    	rst.put("cm_samersrc", rs2.getString("cm_micode"));
				    	rst.put("module", rs2.getString("cm_codename"));
				    	rst.put("cm_sameinfo", rs2.getString("cm_info"));
				    	rst.put("etc1", "실행모듈맵핑");
				    	if (rs2.getString("cm_info").substring(32,33).equals("1")) {
				    		if (rst.get("etc1") != null || rst.get("etc1") != "") {
				    			rst.put("etc1", rst.get("etc1")+",실행모듈선택반영");
				    		} else {
				    			rst.put("etc1", "실행모듈선택반영");
				    		}
				    	}
				    	rsval.add(rst);
				    	rst = null;
				    }
				    rs2.close();
				    pstmt2.close();
		    	}
		    	if (rst != null) {
		    		rsval.add(rst);
		    		rst = null;
		    	}
		    }
		    rs.close();
		    pstmt.close();
		    
		    String svrCd = "";
		    String sameSvrCd = "";
		    for (i=0;rsval.size()>i;i++) {
		    	rst = new HashMap<String, String>();
		    	rst = rsval.get(i);
		    	rst.put("devsvr1", "");
		    	rst.put("build1", "");
		    	//rst.put("module", "");
		    	rst.put("deploy1", "");
		    	rst.put("script1", "");
		    	rst.put("deploy2", "");
		    	rst.put("script2", "");
		    	rst.put("etc2", "");
		    	svrCd = "";
		    	if (rsval.get(i).get("cm_info") != null){
			    	if (rsval.get(i).get("cm_info").substring(0,1).equals("1")) { //운영컴파일
			    		rst.put("build2", "Y");
			    	}
			    	if (rsval.get(i).get("cm_info").substring(10,11).equals("1")) { //운영배포서버파일Copy
			    		svrCd = "05";
			    	} 
			    	if (rsval.get(i).get("cm_info").substring(12,13).equals("1")) { //운영빌드서버파일Copy
			    		if (svrCd.length()>0) svrCd = svrCd + ",";
			    		svrCd = svrCd+"05";
			    	} 
			    	if (rsval.get(i).get("cm_info").substring(20,21).equals("1")) { //운영배포스크립트
			    		rst.put("script2", "Y");
			    	} 
			    	if (rsval.get(i).get("cm_info").substring(22,23).equals("1")) { //운영tmp배포
			    		rst.put("etc2", "tmp배포");
			    	}
			    	if (rsval.get(i).get("cm_info").substring(60,61).equals("1")) { //테스트컴파일
			    		rst.put("build1", "Y");
			    	}
			    	if (rsval.get(i).get("cm_info").substring(62,63).equals("1")) { //테스트배포서버파일Copy
			    		if (svrCd.length()>0) svrCd = svrCd + ",";
			    		svrCd = svrCd + "25";
			    	} 
			    	if (rsval.get(i).get("cm_info").substring(61,62).equals("1")) { //테스트빌드서버파일Copy
			    		if (svrCd.length()>0) svrCd = svrCd + ",";
			    		svrCd = svrCd + "23";
			    	} 
			    	if (rsval.get(i).get("cm_info").substring(63,64).equals("1")) { //테스트배포스크립트
			    		rst.put("script1", "Y");
			    	} 
			    	if (rsval.get(i).get("cm_info").substring(64,65).equals("1")) { //테스트tmp배포
			    		
			    	}
		    	}
		    	
		    	//동시모듈 또는 실행모듈
		    	if(rsval.get(i).get("cm_sameinfo") != null){
			    	if (rsval.get(i).get("cm_sameinfo").substring(0,1).equals("1")) { //운영컴파일
			    		rst.put("build2", "Y");
			    	}
			    	if (rsval.get(i).get("cm_sameinfo").substring(10,11).equals("1")) { //운영배포서버파일Copy
			    		if (sameSvrCd.length()>0) sameSvrCd = sameSvrCd + ",";
			    		sameSvrCd = sameSvrCd + "05";
			    	} 
			    	if (rsval.get(i).get("cm_sameinfo").substring(12,13).equals("1")) { //운영빌드서버파일Copy
			    		if (svrCd.length()>0) svrCd = svrCd + ",";
			    		svrCd = svrCd+"05";
			    	} 
			    	if (rsval.get(i).get("cm_sameinfo").substring(20,21).equals("1")) { //운영배포스크립트
			    		rst.put("script2", "Y");
			    	} 
			    	if (rsval.get(i).get("cm_sameinfo").substring(22,23).equals("1")) { //운영tmp배포
			    		rst.put("etc2", "tmp배포");
			    	}
			    	if (rsval.get(i).get("cm_sameinfo").substring(60,61).equals("1")) { //테스트컴파일
			    		rst.put("build1", "Y");
			    	}
			    	if (rsval.get(i).get("cm_sameinfo").substring(62,63).equals("1")) { //테스트배포서버파일Copy
			    		if (sameSvrCd.length()>0) sameSvrCd = sameSvrCd + ",";
			    		sameSvrCd = sameSvrCd + "15";
			    	} 
			    	if (rsval.get(i).get("cm_sameinfo").substring(61,62).equals("1")) { //테스트빌드서버파일Copy
			    		if (sameSvrCd.length()>0) sameSvrCd = sameSvrCd + ",";
			    		sameSvrCd = sameSvrCd + "13";
			    	} 
			    	if (rsval.get(i).get("cm_sameinfo").substring(63,64).equals("1")) { //테스트배포스크립트
			    		rst.put("script1", "Y");
			    	} 
			    	if (rsval.get(i).get("cm_sameinfo").substring(64,65).equals("1")) { //테스트tmp배포
			    		
			    	}
		    	}
		    	if (rsval.get(i).get("cm_info").substring(28,29).equals("1") || 
		    		(rsval.get(i).get("cm_sameinfo") != null && rsval.get(i).get("cm_sameinfo").substring(28,29).equals("1"))) {
		    		if (rst.get("etc2") != null && !rst.get("etc2").equals("")) {
		    			rst.put("etc2", rst.get("etc2")+",프로그램별스크립트유형정의");
		    		} else {
		    			rst.put("etc2", "프로그램별스크립트유형정의");
		    		}
		    	}
		    	if (svrCd.length()>0) {
		    		for (j=0;svrList.size()>j;j++) {
		    			if (svrList.get(j).get("cm_rsrccd").equals(rsval.get(i).get("cm_rsrccd"))) {
		    				if (svrCd.indexOf(svrList.get(j).get("cm_svrcd"))>=0) {
		    					if (svrList.get(j).get("cm_svrcd").equals("13")) {
		    						if (rst.get("devsvr1") != null && !"".equals(rst.get("devsvr1"))) rst.put("devsvr1", rst.get("devsvr1") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("devsvr1", svrList.get(j).get("cm_svrname"));
		    					} else if (svrList.get(j).get("cm_svrcd").equals("15")) {
		    						if (rst.get("deploy1") != null && !"".equals(rst.get("deploy1"))) rst.put("deploy1", rst.get("deploy1") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("deploy1", svrList.get(j).get("cm_svrname"));
		    						if (svrList.get(j).get("dirbase").equals("N")) {
		    							for (k=0;svrList.size()>k;k++) {
		    								if (svrList.get(k).get("dirbase").equals("Y") &&
		    									svrList.get(k).get("cm_rsrccd").equals(svrList.get(j).get("cm_rsrccd"))) {
		    									if (!svrList.get(k).get("cm_volpath").equals(svrList.get(j).get("cm_volpath"))) {
		    										if (rst.get("etc1") != null && rst.get("etc1") != "") {
			    										rst.put("etc1", rst.get("etc1")+","+svrList.get(k).get("cm_volpath")+"->"+
			    												svrList.get(j).get("cm_volpath"));
		    										} else {
			    										rst.put("etc1", svrList.get(k).get("cm_volpath")+"->"+
			    												svrList.get(j).get("cm_volpath"));
		    										}
		    										break;
		    									}
		    								}
		    							}
		    						}
		    					} else if (svrList.get(j).get("cm_svrcd").equals("03")) {
		    						if (rst.get("realsvr1") != null && !"".equals(rst.get("realsvr1"))) rst.put("realsvr1", rst.get("realsvr1") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("realsvr1", svrList.get(j).get("cm_svrname"));
		    					} else if (svrList.get(j).get("cm_svrcd").equals("05")) {
		    						if (rst.get("deploy2") != null && !"".equals(rst.get("deploy2"))) rst.put("deploy2", rst.get("deploy2") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("deploy2", svrList.get(j).get("cm_svrname"));
		    						if (svrList.get(j).get("dirbase").equals("N")) {
		    							for (k=0;svrList.size()>k;k++) {
		    								if (svrList.get(k).get("dirbase").equals("Y") &&
		    									svrList.get(k).get("cm_rsrccd").equals(svrList.get(j).get("cm_rsrccd"))) {
		    									if (!svrList.get(k).get("cm_volpath").equals(svrList.get(j).get("cm_volpath"))) {
		    										if (rst.get("etc2") != null && rst.get("etc2") != "") {
		    											rst.put("etc2", rst.get("etc2")+","+svrList.get(k).get("cm_volpath")+"->"+
			    												svrList.get(j).get("cm_volpath"));
		    										} else {		    											
		    											rst.put("etc2",svrList.get(k).get("cm_volpath")+"->"+
		    												svrList.get(j).get("cm_volpath"));
		    										}
		    										break;
		    									}
		    								}
		    							}
		    						}
		    					} 
		    				}
		    			}
		    		}
		    	}
		    	if (sameSvrCd.length()>0) {
		    		for (j=0;svrList.size()>j;j++) {
		    			if (svrList.get(j).get("cm_rsrccd").equals(rsval.get(i).get("cm_samersrc"))) {
		    				if (sameSvrCd.indexOf(svrList.get(j).get("cm_svrcd"))>=0) {
		    					if (svrList.get(j).get("cm_svrcd").equals("13")) {
		    						if (rst.get("devsvr1") != null && !"".equals(rst.get("devsvr1"))) rst.put("devsvr1", rst.get("devsvr1") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("devsvr1", svrList.get(j).get("cm_svrname"));
		    					} else if (svrList.get(j).get("cm_svrcd").equals("15")) {
		    						if (rst.get("deploy1") != null && !"".equals(rst.get("deploy1"))) rst.put("deploy1", rst.get("deploy1") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("deploy1", svrList.get(j).get("cm_svrname"));
		    						if (svrList.get(j).get("dirbase").equals("N")) {
		    							for (k=0;svrList.size()>k;k++) {
		    								if (svrList.get(k).get("dirbase").equals("Y") &&
		    									svrList.get(k).get("cm_rsrccd").equals(svrList.get(j).get("cm_rsrccd"))) {
		    									if (!svrList.get(k).get("cm_volpath").equals(svrList.get(j).get("cm_volpath"))) {
		    										if (rst.get("etc1") != null && rst.get("etc1") != "") {
			    										rst.put("etc1", rst.get("etc1")+","+svrList.get(k).get("cm_volpath")+"->"+
			    												svrList.get(j).get("cm_volpath"));
		    										} else {
			    										rst.put("etc1", svrList.get(k).get("cm_volpath")+"->"+
			    												svrList.get(j).get("cm_volpath"));
		    										}
		    										break;
		    									}
		    								}
		    							}
		    						}
		    					} else if (svrList.get(j).get("cm_svrcd").equals("03")) {
		    						if (rst.get("realsvr1") != null && !"".equals(rst.get("realsvr1"))) rst.put("realsvr1", rst.get("realsvr1") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("realsvr1", svrList.get(j).get("cm_svrname"));
		    					} else if (svrList.get(j).get("cm_svrcd").equals("05")) {
		    						if (rst.get("deploy2") != null && !"".equals(rst.get("deploy2"))) rst.put("deploy2", rst.get("deploy2") + ","+svrList.get(j).get("cm_svrname"));
		    						else rst.put("deploy2", svrList.get(j).get("cm_svrname"));
		    						if (svrList.get(j).get("dirbase").equals("N")) {
		    							for (k=0;svrList.size()>k;k++) {
		    								if (svrList.get(k).get("dirbase").equals("Y") &&
		    									svrList.get(k).get("cm_rsrccd").equals(svrList.get(j).get("cm_rsrccd"))) {
		    									if (!svrList.get(k).get("cm_volpath").equals(svrList.get(j).get("cm_volpath"))) {
		    										if (rst.get("etc2") != null && rst.get("etc2") != "") {
		    											rst.put("etc2", rst.get("etc2")+","+svrList.get(k).get("cm_volpath")+"->"+
			    												svrList.get(j).get("cm_volpath"));
		    										} else {		    											
		    											rst.put("etc2",svrList.get(k).get("cm_volpath")+"->"+
		    												svrList.get(j).get("cm_volpath"));
		    										}
		    										break;
		    									}
		    								}
		    							}
		    						}
		    					} 
		    				}
		    			}
		    		}
		    	}
		    	rsval.set(i,rst);
		    	rst = null;
		    }
		    conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getProgInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1200.getProgInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getProgInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1200.getProgInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1200.getProgInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getProgInfo() method statement
    public Object[] getRsrccdScript(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		HashMap<String, String>			  rst2		  = null;
		Object[]		  rtObj		  = null;
		String            retMsg      = "";
		Cmr0200           cmr0200     = new Cmr0200();		

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
            
			strQuery.setLength(0);
			strQuery.append("select c.cm_cmdname,e.cm_prcsys,e.cm_rsrccd,    \n");
			strQuery.append("       e.cm_prcsys,e.cm_qrycd,c.cm_seq,         \n");
			strQuery.append("       a.cm_codename,e.cm_bldcd,d.cm_info       \n");
			strQuery.append("  from cmm0033 e,cmm0022 c,cmm0020 a,cmm0036 d  \n");
			strQuery.append(" where e.cm_syscd=?                             \n");
			strQuery.append("   and e.cm_qrycd in ('03','04')                \n");			
			strQuery.append("   and e.cm_itemid='************'               \n");			
			strQuery.append("   and e.cm_jobcd='****'                        \n");
			strQuery.append("   and e.cm_bldgbn=c.cm_gbncd                   \n");
			strQuery.append("   and e.cm_bldcd=c.cm_bldcd                    \n");
			strQuery.append("   and a.cm_macode = 'JAWON'                    \n");
			strQuery.append("   and e.cm_rsrccd = a.cm_micode                \n");
			strQuery.append("   and e.cm_syscd=d.cm_syscd                    \n");
			strQuery.append("   and e.cm_rsrccd=d.cm_rsrccd                  \n");
			strQuery.append("   and d.cm_closedt is null                     \n");
			strQuery.append(" order by e.cm_qrycd desc, e.cm_prcsys, e.cm_rsrccd, c.cm_seq \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, etcData.get("cm_syscd"));
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
		    	rst = new HashMap<String, String>();
		    	rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
		    	rst.put("rsrcname", rs.getString("cm_codename"));
		    	rst.put("cm_prcsys", rs.getString("cm_prcsys"));
		    	rst.put("cm_qrycd", rs.getString("cm_qrycd"));
		    	rst.put("cm_bldcd", rs.getString("cm_bldcd"));
		    	rst.put("cm_seq", rs.getString("cm_seq"));
		    	if (rs.getString("cm_qrycd").equals("04")) {
		    		if (rs.getString("cm_prcsys").equals("SYSCB")) rst.put("gbncd", "운영컴파일");
		    		else if (rs.getString("cm_prcsys").equals("SYSED")) rst.put("gbncd", "운영적용");
		    	}else {
		    		if (rs.getString("cm_prcsys").equals("SYSCB")) rst.put("gbncd", "테스트컴파일");
		    		else if (rs.getString("cm_prcsys").equals("SYSED")) rst.put("gbncd", "테스트적용");
		    	}
		    	retMsg = rs.getString("cm_cmdname");
				if (retMsg.indexOf("1>")>=0) {
					retMsg = retMsg.substring(0,retMsg.indexOf("1>"));
				} else if (retMsg.indexOf(">")>=0) {
					retMsg = retMsg.substring(0,retMsg.indexOf(">"));
				}
				retMsg = rs.getString("cm_cmdname");
				if (retMsg.indexOf("1>")>=0) {
					retMsg = retMsg.substring(0,retMsg.indexOf("1>"));
				} else if (retMsg.indexOf(">")>=0) {
					retMsg = retMsg.substring(0,retMsg.indexOf(">"));
				}
		    	rst.put("cm_cmdname", retMsg);
		    	if (retMsg.indexOf("?#")>=0) {
		    		strQuery.setLength(0);		    		
	    			strQuery.append("select b.cm_dirpath,a.cr_rsrcname,a.cr_itemid,a.cr_rsrccd  \n");
	    			strQuery.append("  from cmm0070 b,cmr0020 a                     \n");
	    			strQuery.append(" where a.cr_syscd=? and a.cr_rsrccd=?          \n");
	    			strQuery.append("   and a.cr_status not in ('3','9')            \n");
	    			strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
	    			strQuery.append("   and a.cr_dsncd=b.cm_dsncd                   \n");
	    			strQuery.append("   and rownum=1                                \n");
	    			pstmt2 = conn.prepareStatement(strQuery.toString());
	    			pstmt2.setString(1, etcData.get("cm_syscd"));
	    			pstmt2.setString(2, rs.getString("cm_rsrccd"));
	    			rs2 = pstmt2.executeQuery();
	    			if (rs2.next()) {
	    				rst2 = new HashMap<String,String>();
						rst2.put("rsrcname",rs2.getString("cr_rsrcname"));
						rst2.put("dirpath",rs2.getString("cm_dirpath"));
						rst2.put("reqcd",rs.getString("cm_qrycd"));
						rst2.put("rsrccd",rs2.getString("cr_rsrccd"));
						rst2.put("prcsys",rs.getString("cm_prcsys"));
						rst2.put("syscd",etcData.get("cm_syscd"));
						rst2.put("acptno", "");
						rst2.put("itemid", rs2.getString("cr_itemid"));
						if (retMsg.indexOf("?#BASEDIR")>=0) {
							strQuery.setLength(0);    		
			    			strQuery.append("select a.cd_itemid                             \n");
							strQuery.append("  from cmd0011 a                               \n");
			    			strQuery.append(" where a.cd_prcitem=?                          \n");
			    			strQuery.append("   and rownum=1                                \n");
			    			pstmt3 = conn.prepareStatement(strQuery.toString());
			    			pstmt3.setString(1, rs2.getString("cr_itemid"));
			    			rs3 = pstmt3.executeQuery();
			    			if (rs3.next()) {
			    				rst2.put("baseitem", rs3.getString("cd_itemid"));
			    			}
			    			rs3.close();
			    			pstmt3.close();
			    			
			    			if (rst2.get("baseitem") == null) {
			    				strQuery.setLength(0);    		
				    			strQuery.append("select a.cr_baseitem                           \n");
								strQuery.append("  from cmr1010 a                               \n");
				    			strQuery.append(" where a.cr_itemid=?                           \n");
				    			strQuery.append("   and a.cr_baseitem is not null               \n");
				    			strQuery.append("   and rownum=1                                \n");
				    			pstmt3 = conn.prepareStatement(strQuery.toString());
				    			pstmt3.setString(1, rs2.getString("cr_itemid"));
				    			rs3 = pstmt3.executeQuery();
				    			if (rs3.next()) {
				    				rst2.put("baseitem", rs3.getString("cr_baseitem"));
				    			}
				    			rs3.close();
				    			pstmt3.close();
			    			}
						}
						if (rst2.get("baseitem") == null) rst2.put("baseitem","");
						rst2.put("samename", retMsg);
						retMsg = cmr0200.nameChange(rst2, conn);
						if (retMsg.equals("ERROR")) {
							rst.put("cm_cmdname", rst2.get("samename"));
						} else {
							rst.put("cm_cmdname", retMsg);
						}
						rst2 = null;
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
			rs2 = null;
			pstmt2 = null;
			rs3 = null;
			pstmt3 = null;
			conn = null;
			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getRsrccdScript() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1200.getRsrccdScript() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getRsrccdScript() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1200.getRsrccdScript() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs3 != null)    try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null) try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1200.getRsrccdScript() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getRsrccdScript() method statement

    public Object[] getProgInfo_Chk(String sysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		HashMap<String, String>			  rst2		  = null;
		Object[]		  rtObj		  = null;
		int               cnt         = 0;
		int               i,j,k       = 0;
		String            svrCd       = "";
		String            errMsg      = "정상";
		String            retMsg      = "";
		boolean           jobScrSw    = false;
		boolean           jobSvrSw    = false;
		boolean           devCbSw     = false;
		boolean           devEdSw     = false;
		boolean           realCbSw    = false;
		boolean           realEdSw    = false;
		boolean           eclipseSw   = false;
		boolean           localSw     = false;
		boolean           analSw      = false;
		boolean           findSw      = false;
		String            confName    = "";
		String            svReqCd     = "";
		String[]          arrayStr    = null;
		ArrayList<HashMap<String, String>>  svrList = new ArrayList<HashMap<String, String>>();
		

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_svrname,a.cm_svrcd,b.cm_rsrccd,c.cm_dirbase,b.cm_volpath,c.cm_sysinfo \n");
			strQuery.append("  from cmm0038 b,cmm0031 a,cmm0030 c   \n");
			strQuery.append(" where c.cm_syscd=?                    \n");
			strQuery.append("   and c.cm_syscd=a.cm_syscd           \n");
			strQuery.append("   and a.cm_closedt is null            \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd           \n");
			strQuery.append("   and a.cm_svrcd=b.cm_svrcd           \n");
			strQuery.append("   and a.cm_seqno=b.cm_seqno           \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	rst = new HashMap<String, String>();
		    	rst.put("cm_svrcd", rs.getString("cm_svrcd"));
		    	rst.put("cm_svrname", rs.getString("cm_svrname"));
		    	rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
		    	if (rs.getString("cm_dirbase").equals(rs.getString("cm_svrcd"))) {
		    		rst.put("dirbase", "Y");
		    	} else {
		    		rst.put("dirbase", "N");
		    	}
		    	rst.put("cm_volpath", rs.getString("cm_volpath"));
		    	if (rs.getString("cm_sysinfo").substring(7,8).equals("1")) jobScrSw = true;	
		    	if (rs.getString("cm_sysinfo").substring(8,9).equals("1")) jobSvrSw = true;	
		    	if (rs.getString("cm_sysinfo").substring(12,13).equals("1")) eclipseSw = true;		    	
		    	svrList.add(rst);
		    	rst = null;
		    }
		    rs.close();
		    pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select a.cm_rsrccd,b.cm_codename,a.cm_info \n");
			strQuery.append("  from cmm0020 b,cmm0036 a             \n");
			strQuery.append(" where a.cm_syscd=?                    \n");
			strQuery.append("   and a.cm_closedt is null            \n");
			strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037  \n");
			strQuery.append("                            where cm_syscd=a.cm_syscd)      \n");
			strQuery.append("   and b.cm_macode='JAWON'             \n");
			strQuery.append("   and b.cm_micode=a.cm_rsrccd         \n");
			strQuery.append(" order by a.cm_prcstep                 \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	cnt = 0;
		    	errMsg = "";
		    	svrCd = "";
		    	rst = new HashMap<String, String>();
		    	rst.put("rsrcname", rs.getString("cm_codename"));
		    	rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
		    	rst.put("cm_info",rs.getString("cm_info"));
		    	rst.put("errsw", "N");
		    	
		    	//propertyChk(String cmInfo,String sysCd,String RsrcCd,boolean jobSw,boolean etcSw,Connection conn)
		    	rst2 = propertyChk(rs.getString("cm_info"),sysCd,rs.getString("cm_rsrccd"),jobScrSw,conn);
		    	if (rst2.get("localSw").equals("1")) localSw = true;
		    	if (rst2.get("analSw").equals("1")) analSw = true;
		    	if (rst2.get("devCbSw").equals("1")) devCbSw = true;
		    	if (rst2.get("devEdSw").equals("1")) devEdSw = true;
		    	if (rst2.get("realCbSw").equals("1")) realCbSw = true;
		    	if (rst2.get("realEdSw").equals("1")) realEdSw = true;
		    	svrCd = rst2.get("svrcd");
		    	errMsg = rst2.get("errmsg");
		    	rst2 = null;
		    	
		    	if (rs.getString("cm_info").substring(3,4).equals("1") || rs.getString("cm_info").substring(8,9).equals("1")) {
		    		if (rs.getString("cm_info").substring(1,2).equals("0")) {
		    			errMsg = errMsg + "체크아웃속성누락,";
		    		}
		    	} else if (rs.getString("cm_info").substring(1,2).equals("0")) {
		    		strQuery.setLength(0);
		    		strQuery.append("select count(*) cnt from cmm0036 a,cmm0037 b \n");
		    		strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null \n");
		    		strQuery.append("   and (substr(a.cm_info,4,1)='1' or         \n");
		    		strQuery.append("        substr(a.cm_info,9,1)='1')           \n");
		    		strQuery.append("   and a.cm_syscd=b.cm_syscd                 \n");
		    		strQuery.append("   and a.cm_rsrccd=b.cm_rsrccd               \n");
		    		strQuery.append("   and b.cm_samersrc=?                       \n");
		    		pstmt2 = conn.prepareStatement(strQuery.toString());
		    		pstmt2.setString(1, sysCd);
		    		pstmt2.setString(2, rs.getString("cm_rsrccd"));
		    		rs2 = pstmt2.executeQuery();
		    		if (rs2.next()) {
		    			if (rs2.getInt("cnt")==0) {
		    				errMsg = errMsg + "체크아웃속성누락,";
		    			}
		    		}
		    		rs2.close();
		    		pstmt2.close();		    		
		    	}
		    	if (svrCd.length()>0) {
		    		for (j=0;svrList.size()>j;j++) {
		    			if (svrList.get(j).get("cm_rsrccd").equals(rs.getString("cm_rsrccd"))) {
		    				if (svrCd.indexOf(svrList.get(j).get("cm_svrcd"))>=0) svrCd = svrCd.replace(svrList.get(j).get("cm_svrcd")+",","");
		    				if (svrCd.length()==0) break;
		    			}
		    		}
		    		if (svrCd.length()>0) {
		    			if (svrCd.indexOf("13,")>=0) errMsg = errMsg + "테스트컴파일서버연결누락,";
		    			if (svrCd.indexOf("15,")>=0) errMsg = errMsg + "테스트배포서버연결누락,";
		    			if (svrCd.indexOf("03,")>=0) errMsg = errMsg + "운영컴파일서버연결누락,";
		    			if (svrCd.indexOf("05,")>=0) errMsg = errMsg + "운영배포서버연결누락,";		    			
		    		}
		    	}
		    	if (errMsg.length()==0) errMsg = "정상";
		    	else {
		    		rst.put("errsw", "Y");
		    		if (errMsg.substring(errMsg.length()-1).equals(",")) errMsg = errMsg.substring(0,errMsg.length()-1);
		    	}
		    	rst.put("errmsg", errMsg);
		    	rsval.add(rst);
		    	rst = null;		    	
		    	
		    	if (rs.getString("cm_info").substring(3,4).equals("1")) {
		    		cnt = 0;
		    		strQuery.setLength(0);
		    		strQuery.append("select a.cm_rsrccd,b.cm_codename,a.cm_info,c.cm_samedir,c.cm_basedir,c.cm_basename,c.cm_samename \n");
		    		strQuery.append("  from cmm0020 b,cmm0036 a,cmm0037 c   \n");
					strQuery.append(" where c.cm_syscd=?                    \n");
					strQuery.append("   and c.cm_rsrccd=?                   \n");
					strQuery.append("   and c.cm_factcd='04'                \n");
					strQuery.append("   and c.cm_syscd=a.cm_syscd           \n");
					strQuery.append("   and c.cm_samersrc=a.cm_rsrccd       \n");
					strQuery.append("   and a.cm_closedt is null            \n");
					strQuery.append("   and b.cm_macode='JAWON'             \n");
					strQuery.append("   and b.cm_micode=a.cm_rsrccd         \n");
					strQuery.append(" order by b.cm_codename                \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            //pstmt2 =  new LoggableStatement(conn, strQuery.toString());
		            pstmt2.setString(1, sysCd);
		            pstmt2.setString(2, rs.getString("cm_rsrccd"));
		            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
				    while(rs2.next()){
				    	errMsg = "";
				    	svrCd = "";
			    		rst = new HashMap<String, String>();
				    	rst.put("rsrcname", rs2.getString("cm_codename"));
				    	rst.put("cm_rsrccd", rs2.getString("cm_rsrccd"));
				    	rst.put("cm_info",rs2.getString("cm_info"));
				    	rst.put("errsw", "N");
				    	
				    	rst2 = propertyChk(rs2.getString("cm_info"),sysCd,rs2.getString("cm_rsrccd"),jobScrSw,conn);
				    	if (rst2.get("localSw").equals("1")) localSw = true;
				    	if (rst2.get("analSw").equals("1")) analSw = true;
				    	if (rst2.get("devCbSw").equals("1")) devCbSw = true;
				    	if (rst2.get("devEdSw").equals("1")) devEdSw = true;
				    	if (rst2.get("realCbSw").equals("1")) realCbSw = true;
				    	if (rst2.get("realEdSw").equals("1")) realEdSw = true;
				    	svrCd = rst2.get("svrcd");
				    	errMsg = rst2.get("errmsg");
				    	rst2 = null;
				    					    	
				    	if (svrCd.length()>0) {
				    		for (j=0;svrList.size()>j;j++) {
				    			if (svrList.get(j).get("cm_rsrccd").equals(rs2.getString("cm_rsrccd"))) {
				    				if (svrCd.indexOf(svrList.get(j).get("cm_svrcd"))>=0) svrCd = svrCd.replace(svrList.get(j).get("cm_svrcd")+",","");
				    				if (svrCd.length()==0) break;
				    			}
				    		}
				    		if (svrCd.length()>0) {
				    			if (svrCd.indexOf("13,")>=0) errMsg = "테스트컴파일서버연결누락,";
				    			if (svrCd.indexOf("15,")>=0) errMsg = "테스트배포서버연결누락,";
				    			if (svrCd.indexOf("03,")>=0) errMsg = "운영컴파일서버연결누락,";
				    			if (svrCd.indexOf("05,")>=0) errMsg = "운영배포서버연결누락,";		    			
				    		}
				    	}
				    	
				    	if (errMsg.length()==0) errMsg = "정상";
				    	else {
				    		rst.put("errsw", "Y");
				    		if (errMsg.substring(errMsg.length()-1).equals(",")) errMsg = errMsg.substring(0,errMsg.length()-1);
				    	}
					    	
				    	rst.put("errmsg", errMsg);
				    	rsval.add(rst);
				    	rst = null;
				    }
				    rs2.close();
				    pstmt2.close();
		    	}
		    	
		    	if (rs.getString("cm_info").substring(8,9).equals("1")) {
		    		strQuery.setLength(0);
		    		strQuery.append("select a.cm_rsrccd,b.cm_codename,a.cm_info \n");
		    		strQuery.append("  from cmm0020 b,cmm0036 a,cmm0037 c   \n");
					strQuery.append(" where c.cm_syscd=?                    \n");
					strQuery.append("   and c.cm_rsrccd=?                   \n");
					strQuery.append("   and c.cm_factcd='09'                \n");
					strQuery.append("   and c.cm_syscd=a.cm_syscd           \n");
					strQuery.append("   and c.cm_samersrc=a.cm_rsrccd       \n");
					strQuery.append("   and a.cm_closedt is null            \n");
					strQuery.append("   and b.cm_macode='JAWON'             \n");
					strQuery.append("   and b.cm_micode=a.cm_rsrccd         \n");
					strQuery.append(" order by b.cm_codename                \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            //pstmt =  new LoggableStatement(conn, strQuery.toString());
		            pstmt2.setString(1, sysCd);
		            pstmt2.setString(2, rs.getString("cm_rsrccd"));
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs2 = pstmt2.executeQuery();
				    while(rs2.next()){				    		
			    		rst = new HashMap<String, String>();
				    	rst.put("rsrcname", rs2.getString("cm_codename"));
				    	rst.put("cm_rsrccd", rs2.getString("cm_rsrccd"));
				    	rst.put("cm_info",rs2.getString("cm_info"));
				    	rst.put("errsw", "N");
				    	
				    	rst2 = propertyChk(rs2.getString("cm_info"),sysCd,rs2.getString("cm_rsrccd"),jobScrSw,conn);
				    	if (rst2.get("localSw").equals("1")) localSw = true;
				    	if (rst2.get("analSw").equals("1")) analSw = true;
				    	if (rst2.get("devCbSw").equals("1")) devCbSw = true;
				    	if (rst2.get("devEdSw").equals("1")) devEdSw = true;
				    	if (rst2.get("realCbSw").equals("1")) realCbSw = true;
				    	if (rst2.get("realEdSw").equals("1")) realEdSw = true;
				    	svrCd = rst2.get("svrcd");
				    	errMsg = rst2.get("errmsg");
				    	rst2 = null;
				    	
				    	if (svrCd.length()>0) {
				    		for (j=0;svrList.size()>j;j++) {
				    			if (svrList.get(j).get("cm_rsrccd").equals(rs2.getString("cm_rsrccd"))) {
				    				if (svrCd.indexOf(svrList.get(j).get("cm_svrcd"))>=0) svrCd = svrCd.replace(svrList.get(j).get("cm_svrcd")+",","");
				    				if (svrCd.length()==0) break;
				    			}
				    		}
				    		if (svrCd.length()>0) {
				    			if (svrCd.indexOf("13,")>=0) errMsg = "테스트컴파일서버연결누락,";
				    			if (svrCd.indexOf("15,")>=0) errMsg = "테스트배포서버연결누락,";
				    			if (svrCd.indexOf("03,")>=0) errMsg = "운영컴파일서버연결누락,";
				    			if (svrCd.indexOf("05,")>=0) errMsg = "운영배포서버연결누락,";		    			
				    		}
				    	}
				    	
				    	if (rs2.getString("cm_info").substring(25,26).equals("0")) {
					    	strQuery.setLength(0);
					    	strQuery.append("select count(*) cnt from (                \n");
					    	strQuery.append("select cr_itemid from cmr0020             \n");
					    	strQuery.append(" where cr_syscd=? and cr_rsrccd=?         \n");
					    	strQuery.append("   and cr_lstver>0                        \n");
					    	strQuery.append("   and cr_status not in ('3','9','C')     \n");
					    	strQuery.append(" minus  \n");
					    	strQuery.append("select cd_itemid from cmd0011 a,cmr0020 b \n");
					    	strQuery.append(" where b.cr_syscd=? and b.cr_rsrccd=?     \n");
					    //	strQuery.append("   and b.cr_lstver>0                      \n");
					    	strQuery.append("   and b.cr_status not in ('9','C')       \n");
					    	strQuery.append("   and b.cr_itemid=a.cd_prcitem           \n");
					    	strQuery.append(")                                         \n");
					    	//pstmt3 = conn.prepareStatement(strQuery.toString());
				            pstmt3 =  new LoggableStatement(conn, strQuery.toString());
				            pstmt3.setString(1, sysCd);
				            pstmt3.setString(2, rs.getString("cm_rsrccd"));
				            pstmt3.setString(3, sysCd);
				            pstmt3.setString(4, rs2.getString("cm_rsrccd"));
				            ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
				            rs3 = pstmt3.executeQuery();
				            if (rs3.next()) {
				            	if (rs3.getInt("cnt")>0) {
				            		errMsg = errMsg + "실행모듈맵핑누락존재,";
				            	}
				            }
				            rs3.close();
				            pstmt3.close();
				    	}
				    	if (errMsg.length()==0) errMsg = "정상";
				    	else {
				    		rst.put("errsw", "Y");
				    		if (errMsg.substring(errMsg.length()-1).equals(",")) errMsg = errMsg.substring(0,errMsg.length()-1);
				    	}
				    	rst.put("errmsg", errMsg);
				    	
				    	rsval.add(rst);
				    	rst = null;
				    }
				    rs2.close();
				    pstmt2.close();
		    	}
		    }
		    rs.close();
		    pstmt.close();
		    
		    errMsg = "";
		    retMsg = "";
		    strQuery.setLength(0);
		    strQuery.append("select b.cm_micode,b.cm_codename,1 cnt   \n");
		    strQuery.append("  from cmm0020 b,cmm0036 a               \n");
		    strQuery.append(" where a.cm_syscd=?                      \n");
		    strQuery.append("   and a.cm_closedt is null              \n");
		    strQuery.append("   and b.cm_macode='JAWON'               \n");
		    strQuery.append("   and b.cm_micode=a.cm_rsrccd           \n");
		    strQuery.append(" minus                                   \n");
		    strQuery.append("select b.cm_micode,b.cm_codename,count(*) cnt   \n");
		    strQuery.append("  from cmm0020 b,cmm0038 a,cmm0031 c,cmm0030 d \n");
		    strQuery.append(" where d.cm_syscd=?                      \n");
		    strQuery.append("   and d.cm_syscd=a.cm_syscd             \n");
		    strQuery.append("   and d.cm_dirbase=a.cm_svrcd           \n");
		    strQuery.append("   and a.cm_syscd=c.cm_syscd             \n");
		    strQuery.append("   and a.cm_svrcd=c.cm_svrcd             \n");
		    strQuery.append("   and a.cm_seqno=c.cm_seqno             \n");
		    strQuery.append("   and c.cm_closedt is null              \n");
		    strQuery.append("   and nvl(c.cm_cmpsvr,'N')='Y'          \n");
		    strQuery.append("   and b.cm_macode='JAWON'               \n");
		    strQuery.append("   and b.cm_micode=a.cm_rsrccd           \n");
		    strQuery.append(" group by b.cm_micode,b.cm_codename      \n");
		    pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            pstmt.setString(2, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	strQuery.setLength(0);
			    strQuery.append("select count(*) cnt                      \n");
			    strQuery.append("  from cmm0038 a,cmm0031 c,cmm0030 b     \n");
			    strQuery.append(" where b.cm_syscd=?                      \n");
			    strQuery.append("   and b.cm_syscd=a.cm_syscd             \n");
			    strQuery.append("   and b.cm_dirbase=a.cm_svrcd           \n");
			    strQuery.append("   and a.cm_rsrccd=?                     \n");
			    strQuery.append("   and a.cm_syscd=c.cm_syscd             \n");
			    strQuery.append("   and a.cm_svrcd=c.cm_svrcd             \n");
			    strQuery.append("   and a.cm_seqno=c.cm_seqno             \n");
			    strQuery.append("   and c.cm_closedt is null              \n");
			    strQuery.append("   and nvl(c.cm_cmpsvr,'N')='Y'          \n");
		    	pstmt2 = conn.prepareStatement(strQuery.toString());
		    	pstmt2.setString(1, sysCd);
		    	pstmt2.setString(2, rs.getString("cm_micode"));
		    	rs2 = pstmt2.executeQuery();
		    	if (rs2.next()) {
		    		if (rs2.getInt("cnt")==0) {
		    			if (retMsg.length()>0) retMsg = retMsg + ",";
		    			retMsg = retMsg + "누락[" + rs.getString("cm_codename")+"]";
		    		} else {
		    			if (retMsg.length()>0) retMsg = retMsg + ",";
		    			retMsg = retMsg + "다수[" + rs.getString("cm_codename")+"]";
		    		}
		    	}
		    	rs2.close();
		    	pstmt2.close();
		    }
		    rs.close();
		    pstmt.close();
		    
		    if (retMsg.length()>0) errMsg = errMsg + "디렉토리기준서버:"+retMsg;
		    
		    retMsg = "";
		    strQuery.setLength(0);
			strQuery.append("select a.cm_svrcd,a.cm_seqno,a.cm_svruse,a.cm_svrname,b.cm_codename \n");
			strQuery.append("  from cmm0031 a,cmm0020 b               \n");
			strQuery.append(" where a.cm_syscd=?                      \n");
			strQuery.append("   and a.cm_closedt is null              \n");
			strQuery.append("   and substr(a.cm_svruse,1,1)='1'       \n");	
			strQuery.append("   and b.cm_macode='SERVERCD'            \n");	
			strQuery.append("   and b.cm_micode=a.cm_svrcd            \n");			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	strQuery.setLength(0);
		    	strQuery.append("select count(*) cnt from cmm0035     \n");
		    	strQuery.append(" where cm_syscd=? and cm_svrcd=?     \n");
		    	strQuery.append("   and cm_seqno=?                    \n");
		    	strQuery.append("   and cm_permission is not null     \n");
		    	pstmt2 = conn.prepareStatement(strQuery.toString());
		    	pstmt2.setString(1, sysCd);
		    	pstmt2.setString(2, rs.getString("cm_svrcd"));
		    	pstmt2.setString(3, rs.getString("cm_seqno"));
		    	rs2 = pstmt2.executeQuery();
		    	if (rs2.next()) {
		    		if (rs2.getInt("cnt")==0) {
		    			if (retMsg.length()>0) retMsg = retMsg + ",";
		    			retMsg = retMsg + rs.getString("cm_codename")+"_"+rs.getString("cm_svrname");
		    		}
		    	}
		    	rs2.close();
		    	pstmt2.close();
		    }
		    rs.close();
		    pstmt.close();
		    
		    if (retMsg.length()>0) {
		    	if (errMsg.length()>0) errMsg = errMsg+", ";
		    	errMsg = errMsg + "파일속성변경누락["+retMsg + "]";
		    }
		    
		    retMsg = "";
		    if (jobSvrSw) {
		    	retMsg = "";
		    	strQuery.setLength(0);
				strQuery.append("select a.cm_svrcd,b.cm_jobname,d.cm_codename   \n");
				strQuery.append("  from cmm0031 a,cmm0102 b,cmm0034 c,cmm0020 d \n");
				strQuery.append(" where a.cm_syscd=?                            \n");
				strQuery.append("   and a.cm_closedt is null                    \n");
				strQuery.append("   and a.cm_syscd=c.cm_syscd                   \n");	
				strQuery.append("   and c.cm_closedt is null                    \n");	
				strQuery.append("   and d.cm_macode='SERVERCD'                  \n");	
				strQuery.append("   and d.cm_micode=a.cm_svrcd                  \n");	
				strQuery.append(" group by a.cm_svrcd,b.cm_jobname,d.cm_codename\n");	
				strQuery.append(" minus   \n");	
				strQuery.append("select a.cm_svrcd,b.cm_jobname,d.cm_codename   \n");
				strQuery.append("  from cmm0031 a,cmm0102 b,cmm0035 c,cmm0020 d \n");
				strQuery.append(" where a.cm_syscd=?                            \n");
				strQuery.append("   and a.cm_closedt is null                    \n");
				strQuery.append("   and a.cm_syscd=c.cm_syscd                   \n");	
				strQuery.append("   and a.cm_svrcd=c.cm_svrcd                   \n");	
				strQuery.append("   and a.cm_seqno=c.cm_seqno                   \n");	
				strQuery.append("   and a.cm_jobcd<>'****'                      \n");		
				strQuery.append("   and d.cm_macode='SERVERCD'                  \n");	
				strQuery.append("   and d.cm_micode=a.cm_svrcd                  \n");
				strQuery.append(" group by a.cm_svrcd,b.cm_jobname,d.cm_codename \n");	
				//pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, sysCd);
	            pstmt.setString(2, sysCd);
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
			    while(rs.next()){
			    	if (retMsg.length()==0) retMsg = retMsg + ",";
			    	retMsg = retMsg + rs.getString("cm_codename")+"["+rs.getString("cm_jobname")+"]";
			    }
			    rs.close();
			    pstmt.close();
		    } else {
		    	/*strQuery.setLength(0);
				strQuery.append("select a.cm_svrcd,d.cm_codename                \n");
				strQuery.append("  from cmm0031 a,cmm0020 d                     \n");
				strQuery.append(" where a.cm_syscd=?                            \n");
				strQuery.append("   and a.cm_closedt is null                    \n");	
				strQuery.append("   and d.cm_macode='SERVERCD'                  \n");	
				strQuery.append("   and d.cm_micode=a.cm_svrcd                  \n");	
				strQuery.append(" group by a.cm_svrcd,d.cm_codename             \n");	
				strQuery.append(" minus   \n");	
				strQuery.append("select a.cm_svrcd,d.cm_codename                \n");
				strQuery.append("  from cmm0031 a,cmm0035 c,cmm0020 d           \n");
				strQuery.append(" where a.cm_syscd=?                            \n");
				strQuery.append("   and a.cm_closedt is null                    \n");
				strQuery.append("   and a.cm_syscd=c.cm_syscd                   \n");	
				strQuery.append("   and a.cm_svrcd=c.cm_svrcd                   \n");	
				strQuery.append("   and a.cm_seqno=c.cm_seqno                   \n");	
				strQuery.append("   and c.cm_jobcd='****'                       \n");		
				strQuery.append("   and d.cm_macode='SERVERCD'                  \n");	
				strQuery.append("   and d.cm_micode=a.cm_svrcd                  \n");
				strQuery.append(" group by a.cm_svrcd,d.cm_codename             \n");	
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, sysCd);
	            pstmt.setString(2, sysCd);
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
			    while(rs.next()){
			    	if (retMsg.length()==0) retMsg = retMsg + ",";
			    	retMsg = retMsg + rs.getString("cm_codename");
			    }
			    rs.close();
			    pstmt.close();*/
		    }
		    if (retMsg.length()>0) {
		    	if (errMsg.length()>0) errMsg = errMsg+", ";
		    	errMsg = "서버와업무연결누락:"+retMsg;
		    }
		    retMsg = "";
		    svReqCd = "01,03,07,11,04,13,16,";
		    
		    if (eclipseSw) retMsg = "01SYSEDN,02SYSEDN,11SYSENC,07SYSEUP,";
		    if (devCbSw) retMsg = retMsg + "03SYSCB,";
		    if (devEdSw) retMsg = retMsg + "03SYSED,";
		    if (realCbSw) retMsg = retMsg + "04SYSCB,";
		    if (realEdSw) retMsg = retMsg + "04SYSED,";
		    if (localSw) retMsg = retMsg + "01SYSFMK,01SYSPDN,02SYSPDN,02SYSFMK,07SYSPUP,";
		    
		    strQuery.setLength(0);
			strQuery.append("select a.cm_reqcd,a.cm_jobcd           \n");
			strQuery.append("  from cmm0060 a                       \n");
			strQuery.append(" where a.cm_syscd=?                    \n");
			strQuery.append("   and a.cm_manid='1'                  \n");
			strQuery.append("   and a.cm_gubun='1'                  \n");
			strQuery.append(" order by a.cm_reqcd,a.cm_seqno        \n");			
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
		    while(rs.next()){
		    	if (svReqCd.indexOf(rs.getString("cm_reqcd"))>=0) svReqCd = svReqCd.replace(rs.getString("cm_reqcd")+",", "");
	    		if (rs.getString("cm_jobcd").equals("SYSPF")) {
	    			if (errMsg.length()>0) errMsg = errMsg + ", ";
	    			errMsg = errMsg + "결재단계삭제필요(체크인목록작성)";
	    		}
	    		confName = confName + rs.getString("cm_jobcd");
		    	svrCd = rs.getString("cm_reqcd")+rs.getString("cm_jobcd")+",";
		    	if (retMsg.indexOf(svrCd)>=0) {
		    		retMsg = retMsg.replace(svrCd, "");
		    	}
		    }
		    rs.close();
		    pstmt.close();
		    
		    if (retMsg.length()>0 || svReqCd.length()>0) {
		    	svrList.clear();
		    	strQuery.setLength(0);
				strQuery.append("select a.cm_macode,a.cm_micode,a.cm_codename  \n");
				strQuery.append("  from cmm0020 a                              \n");
				strQuery.append(" where a.cm_macode in ('REQUEST','SYSGBN')    \n");
				strQuery.append("   and a.cm_closedt is null                   \n");
				strQuery.append(" order by a.cm_macode,a.cm_micode             \n");			
				pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt =  new LoggableStatement(conn, strQuery.toString());
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
			    while(rs.next()){
			    	rst = new HashMap<String, String>();
			    	rst.put("cm_macode", rs.getString("cm_macode"));
			    	rst.put("cm_micode", rs.getString("cm_micode"));
			    	rst.put("cm_codename", rs.getString("cm_codename"));
			    	svrList.add(rst);
			    	rst = null;
			    }
			    rs.close();
			    pstmt.close();
		        
			    confName = "";
			    if (svReqCd.length()>0) {			    	
		    		for (j=0;svrList.size()>j;j++) {
		    			if (svrList.get(j).get("cm_macode").equals("REQUEST") && svReqCd.indexOf(svrList.get(j).get("cm_micode")+",")>=0) {
		    				if (confName.length()>0) confName = confName + ",";
		    				confName = confName + svrList.get(j).get("cm_codename");
		    			} 
		    		}
		    		if (confName.length()>0) {
		    			if (errMsg.length()>0) errMsg = errMsg + ", ";
		    			errMsg = errMsg + "결재정보누락:"+confName;
		    		}
		    		confName = "";
			    }
			    
			    if (retMsg.length()>0) {
			    	arrayStr = retMsg.split(",");
			    	if (errMsg.length()>0) errMsg = errMsg + ", ";
			    	errMsg = errMsg + "결재단계추가[";
			    	for (i=0;arrayStr.length>i;i++) {
			    		svReqCd = arrayStr[i].substring(0,2);
			    		confName = arrayStr[i].substring(2);
			    		for (j=0;svrList.size()>j;j++) {
			    			if (svrList.get(j).get("cm_macode").equals("REQUEST") && svrList.get(j).get("cm_micode").equals(svReqCd)) {
			    				errMsg = errMsg + svrList.get(j).get("cm_codename")+"_";
			    				break;
			    			}  
			    		}	
			    		for (j=0;svrList.size()>j;j++) {
			    			if (svrList.get(j).get("cm_macode").equals("SYSGBN") && svrList.get(j).get("cm_micode").equals(confName)) {
			    				errMsg = errMsg + svrList.get(j).get("cm_codename")+"";
			    				break;
			    			} 
			    		}	
			    		errMsg = errMsg + ",";
			    	}
			    	errMsg = errMsg + "]";
			    	errMsg = errMsg.replace(",]", "]");
			    }
		    }
		    
		    if (analSw) {
		    	strQuery.setLength(0);
				strQuery.append("select count(*) cnt                           \n");
				strQuery.append("  from cmm0039 a                              \n");
				strQuery.append(" where a.cm_syscd=? and a.cm_dircd='AN'       \n");			
				pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, sysCd);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
			    if (rs.next()){
			    	if (rs.getInt("cnt")==0) {
			    		if (errMsg.length()>0) errMsg = errMsg + ", ";
			    		errMsg = errMsg + "영향분석호출정보누락[시스템정보_공통디렉토리]";
			    	}
			    }
			    rs.close();
			    pstmt.close();
		    }
		    
		    if (errMsg.length()>0) {
		    	if (errMsg.substring(errMsg.length()-1).equals(",")) errMsg = errMsg.substring(0,errMsg.length()-1);
		    	rst = new HashMap<String, String>();
		    	rst.put("cm_micode", "00");
		    	rst.put("rsrcname", "공통사항");
		    	rst.put("errsw", "Y");
		    	rst.put("errmsg", errMsg);
		    	rsval.add(0,rst);
		    	rst = null;
		    }	
		    conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			rs3 = null;
			pstmt3 = null;
			conn = null;
			rtObj = rsval.toArray();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getProgInfo_Chk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1200.getProgInfo_Chk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getProgInfo_Chk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1200.getProgInfo_Chk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs3 != null)    try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null) try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1200.getProgInfo_Chk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getProgInfo_Chk() method statement
    public String getBldInfoYn(String sysCd,String qryCd,String prcSys,String RsrcCd,boolean jobSw,boolean etcSw,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               cnt         = 0;	
		String            retMsg      = "OK";

		try {
			strQuery.setLength(0);
			if (jobSw && !etcSw) {
				strQuery.append("select b.cm_jobcd,b.cm_jobname         \n");
				strQuery.append("  from cmr0020 a,cmm0102 b             \n");
				strQuery.append(" where a.cr_syscd=?                    \n");
				strQuery.append("   and a.cr_rsrccd=?                   \n");
				strQuery.append("   and a.cr_lstver>0                   \n");
				strQuery.append("   and a.cr_status not in ('3','9','C')\n");
				strQuery.append("   and a.cr_jobcd=b.cm_jobcd           \n");
				strQuery.append(" group by b.cm_jobcd,b.cm_jobname      \n");
				strQuery.append(" minus                                 \n");
				strQuery.append("select b.cm_jobcd,b.cm_jobname         \n");
				strQuery.append("  from cmm0033 a,cmm0022 b,cmm0102 c   \n");
				strQuery.append(" where a.cm_syscd=?                    \n");
				strQuery.append("   and a.cm_qrycd=?                    \n");
				strQuery.append("   and a.cm_prcsys=?                   \n");
				strQuery.append("   and a.cm_rsrccd=?                   \n");
				strQuery.append("   and a.cm_jobcd<>'****'              \n");
				strQuery.append("   and a.cm_itemid='************'      \n");
				strQuery.append("   and a.cm_bldgbn=b.cm_gbncd          \n");
				strQuery.append("   and a.cm_bldcd=b.cm_bldcd           \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, sysCd);
	            pstmt.setString(2, RsrcCd);
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
			    while (rs.next()){
			    	if (retMsg.equals("OK")) retMsg = "";
			    	else retMsg = retMsg + ",";
			    	retMsg = retMsg + rs.getString("cm_jobname")+"["+rs.getString("cm_jobcd")+"]";
			    }
			    rs.close();
			    pstmt.close();
			} else {
				if (!etcSw && !jobSw) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt                    \n");
					strQuery.append("  from cmm0033 a,cmm0022 b             \n");
					strQuery.append(" where a.cm_syscd=?                    \n");
					strQuery.append("   and a.cm_qrycd=?                    \n");
					strQuery.append("   and a.cm_prcsys=?                   \n");
					strQuery.append("   and a.cm_rsrccd=?                   \n");
					strQuery.append("   and a.cm_jobcd='****'               \n");
					strQuery.append("   and a.cm_itemid='************'      \n");
					strQuery.append("   and a.cm_bldgbn=b.cm_gbncd          \n");
					strQuery.append("   and a.cm_bldcd=b.cm_bldcd           \n");
					//pstmt = conn.prepareStatement(strQuery.toString());
		            pstmt =  new LoggableStatement(conn, strQuery.toString());
		            pstmt.setString(1, sysCd);
		            pstmt.setString(2, qryCd);
		            pstmt.setString(3, prcSys);
		            pstmt.setString(4, RsrcCd);
		            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
				    if (rs.next()){
				    	if (rs.getInt("cnt")==0) retMsg = "ERROR";
				    }
				    rs.close();
				    pstmt.close();
				} else if (etcSw) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from (             \n");
					strQuery.append("select a.cr_itemid                     \n");
					strQuery.append("  from cmr0020 a                       \n");
					strQuery.append(" where a.cr_syscd=?                    \n");
					strQuery.append("   and a.cr_rsrccd=?                   \n");
					strQuery.append("   and a.cr_lstver>0                   \n");
					strQuery.append("   and a.cr_status not in ('3','9','C')\n");
					strQuery.append(" minus                                 \n");
					strQuery.append("select a.cm_itemid                     \n");
					strQuery.append("  from cmm0033 a,cmm0022 b             \n");
					strQuery.append(" where a.cm_syscd=?                    \n");
					strQuery.append("   and a.cm_qrycd=?                    \n");
					strQuery.append("   and a.cm_prcsys=?                   \n");
					strQuery.append("   and a.cm_rsrccd=?                   \n");
					strQuery.append("   and a.cm_itemid<>'************'     \n");
					strQuery.append("   and a.cm_bldgbn=b.cm_gbncd          \n");
					strQuery.append("   and a.cm_bldcd=b.cm_bldcd           \n");
					strQuery.append(")                                      \n");
					pstmt = conn.prepareStatement(strQuery.toString());
		            //pstmt =  new LoggableStatement(conn, strQuery.toString());
		            pstmt.setString(1, sysCd);
		            pstmt.setString(2, RsrcCd);
		            pstmt.setString(3, sysCd);
		            pstmt.setString(4, qryCd);
		            pstmt.setString(5, prcSys);
		            pstmt.setString(6, RsrcCd);
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
				    if (rs.next()){
				    	if (rs.getInt("cnt")>0) retMsg = "ERROR";
				    }
				    rs.close();
				    pstmt.close();
				}
			}
			rs = null;
			pstmt = null;
			return retMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getBldInfoYn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1200.getBldInfoYn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1200.getBldInfoYn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1200.getBldInfoYn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}

	}//end of getBldInfoYn() method statement
    public HashMap<String, String> propertyChk(String cmInfo,String sysCd,String RsrcCd,boolean jobSw,Connection conn) throws SQLException, Exception {
		int               cnt         = 0;	
		String            errMsg      = "";
		String            retMsg      = "";
		String            svrCd       = "";
		boolean           etcSw       = false;
		boolean           devCbSw     = false;
		boolean           devEdSw     = false;
		boolean           realCbSw    = false;
		boolean           realEdSw    = false;
		boolean           localSw     = false;
		boolean           analSw      = false;
		HashMap<String, String> rst = null;

		try {
			rst = new HashMap<String, String>();
			
			if (cmInfo.substring(28,29).equals("1")) etcSw = true;
	    	if (cmInfo.substring(44,45).equals("1")) localSw = true;
	    	if (cmInfo.substring(33,34).equals("1")) analSw = true;
	    		    	
	    	svrCd = "";
	    	if (cmInfo.substring(0,1).equals("1") || cmInfo.substring(12,13).equals("1")) { 
	    		//운영컴파일,운영빌드서버파일Copy 
	    		realCbSw = true;
	    		svrCd = "03,";	
	    		if (cmInfo.substring(0,1).equals("1")) {
	    			retMsg = getBldInfoYn(sysCd,"04","SYSCB",RsrcCd,jobSw,etcSw,conn);
	    			if (!retMsg.equals("OK")) {		    				
	    				if (etcSw) errMsg = errMsg + "운영컴파일스크립트맵핑누락(프로그램별),";
	    				else if (jobSw) errMsg = errMsg + "운영컴파일스크립트맵핑누락["+retMsg+"],";
	    				else errMsg = errMsg + "운영컴파일스크립트맵핑누락,";
	    			}
	    		}
	    	}
	    	if (cmInfo.substring(10,11).equals("1") || cmInfo.substring(20,21).equals("1")) { 
	    		//운영배포서버파일Copy,릴리즈스크립트실행(운영)
	    		realEdSw = true;
	    		svrCd = svrCd + "05,";
	    		if (cmInfo.substring(20,21).equals("1")) {
	    			retMsg = getBldInfoYn(sysCd,"04","SYSED",RsrcCd,jobSw,etcSw,conn);
	    			if (!retMsg.equals("OK")) {		    				
	    				if (etcSw) errMsg = errMsg + "운영릴리즈스크립트맵핑누락(프로그램별),";
	    				else if (jobSw) errMsg = errMsg + "운영릴리즈스크립트맵핑누락["+retMsg+"],";
	    				else errMsg = errMsg + "운영릴리즈스크립트맵핑누락,";
	    			}
	    		}
	    	} 
	    	if (cmInfo.substring(22,23).equals("1") && cmInfo.substring(10,11).equals("0")) { 
	    		//운영tmp배포인데 운영서버Copy가 없음
	    		errMsg = "운영서버Copy속성누락,";
	    	}
	    	if (cmInfo.substring(60,61).equals("1") || cmInfo.substring(61,62).equals("1")) { 
	    		//테스트컴파일,테스트빌드서버파일Copy
	    		devCbSw = true;
	    		svrCd = svrCd + "13,";
	    		if (cmInfo.substring(60,61).equals("1")) {
	    			retMsg = getBldInfoYn(sysCd,"03","SYSCB",RsrcCd,jobSw,etcSw,conn);
	    			if (!retMsg.equals("OK")) {		    				
	    				if (etcSw) errMsg = errMsg + "테스트컴파일스크립트맵핑누락(프로그램별),";
	    				else if (jobSw) errMsg = errMsg + "테스트컴파일스크립트맵핑누락["+retMsg+"],";
	    				else errMsg = errMsg + "테스트컴파일스크립트맵핑누락,";
	    			}
	    		}
	    	}
	    	if (cmInfo.substring(62,63).equals("1") || cmInfo.substring(63,64).equals("1")) {
	    		//테스트배포서버파일Copy,테스트배포스크립트
	    		realEdSw = true;
	    		svrCd = svrCd + "15,";
	    		if (cmInfo.substring(50,51).equals("1")) {
	    			retMsg = getBldInfoYn(sysCd,"03","SYSED",RsrcCd,jobSw,etcSw,conn);
	    			if (!retMsg.equals("OK")) {		    				
	    				if (etcSw) errMsg = errMsg + "테스트릴리즈스크립트맵핑누락(프로그램별),";
	    				else if (jobSw) errMsg = errMsg + "테스트릴리즈스크립트맵핑누락["+retMsg+"],";
	    				else errMsg = errMsg + "테스트릴리즈스크립트맵핑누락,";
	    			}
	    		}
	    	} 
	    	if (cmInfo.substring(64,65).equals("1") && cmInfo.substring(62,63).equals("0")) { 
	    		//테스트tmp배포인데 테스트서버Copy가 없음
	    		errMsg = errMsg + "테스트서버Copy속성누락,";
	    	}
	    	rst.put("errmsg", errMsg);
	    	rst.put("svrcd", svrCd);
	    	if (analSw) rst.put("analSw", "1");
	    	else rst.put("analSw", "0");
	    	if (localSw) rst.put("localSw", "1");
	    	else rst.put("localSw", "0");
	    	if (devCbSw) rst.put("devCbSw", "1");
	    	else rst.put("devCbSw", "0");
	    	if (devEdSw) rst.put("devEdSw", "1");
	    	else rst.put("devEdSw", "0");
	    	if (realCbSw) rst.put("realCbSw", "1");
	    	else rst.put("realCbSw", "0");
	    	if (realEdSw) rst.put("realEdSw", "1");
	    	else rst.put("realEdSw", "0");
			return rst;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1200.propertyChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1200.propertyChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1200.propertyChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1200.propertyChk() Exception END ##");
			throw exception;
		}finally{
		}

	}//end of propertyChk() method statement
}//end of Cmd1200 class statement
