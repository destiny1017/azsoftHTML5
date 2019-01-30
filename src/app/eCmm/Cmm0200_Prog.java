/*****************************************************************************************
	1. program ID	: Cmm0200_Prog.java
	2. create date	: 2008.12.15
	3. auth		    : is.choi
	4. update date	: 2008.12.19
	5. auth		    : No Name
	6. description	: [관리자] -> [시스템정보] -> [프로그램종류정보]
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
public class Cmm0200_Prog{


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
	public Object[] codeInfo(String MACODE) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String[] macode = MACODE.split(",");
			strQuery.append("select cm_macode,cm_micode,cm_codename from cmm0020 where \n");
			strQuery.append("cm_macode in ( \n");
			if (macode.length == 1)
				strQuery.append(" ? ");
			else{
				for (int i=0;i<macode.length;i++){
					if (i == macode.length-1)
						strQuery.append(" ? ");
					else
						strQuery.append(" ? ,");
				}
			}
			strQuery.append(" ) \n");
			strQuery.append("and cm_micode <> '****' \n");
	        strQuery.append("and cm_closedt is null \n");
	        strQuery.append("order by cm_macode,cm_codename \n");

            pstmt = conn.prepareStatement(strQuery.toString());
			for (int i=0;i<macode.length;i++){
				pstmt.setString(i+1, macode[i]);
			}

            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_macode", rs.getString("cm_macode"));
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;


			return rtList.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Prog.codeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCodeInfo() method statement

    public Object[] getProgList(String SysCd) throws SQLException, Exception {
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
			strQuery.append("select a.cm_rsrccd,a.cm_info,a.cm_prcstep,a.cm_basedir,\n");
			strQuery.append("       a.cm_stepsta,a.cm_vercnt,a.cm_time,            \n");
			strQuery.append("       a.cm_exename,b.cm_codename                     \n");
			strQuery.append("  from cmm0036 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='JAWON' and b.cm_micode=a.cm_rsrccd\n");
			strQuery.append(" order by a.cm_prcstep                                \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_codename", rs.getString("cm_codename"));

				/*  사용언어 폐기(2010/06/17)
				tmpWork1 = "";
				tmpWork2 = "";
				strQuery.setLength(0);
				strQuery.append("select a.cm_langcd,b.cm_codename                  \n");
				strQuery.append("  from cmm0020 b,cmm0032 a                        \n");
				strQuery.append(" where a.cm_syscd=? and a.cm_rsrccd=?             \n");
				strQuery.append("   and b.cm_macode='LANGUAGE'                     \n");
				strQuery.append("   and b.cm_micode=a.cm_langcd                    \n");
				strQuery.append("  order by a.cm_langcd                            \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, SysCd);
				pstmt2.setString(2, rs.getString("cm_rsrccd"));
				rs2 = pstmt2.executeQuery();
				while (rs2.next()) {
					if (tmpWork1.length()>0) {
						tmpWork1 = tmpWork1 + ",";
						tmpWork2 = tmpWork2 + ",";
					}
					tmpWork1 = tmpWork1 + rs2.getString("cm_codename");
					tmpWork2 = tmpWork2 + rs2.getString("cm_langcd");
				}
				rs2.close();
				pstmt2.close();
				rs2 = null;
				pstmt2 = null;
				rst.put("cm_langcd", tmpWork2);
				rst.put("langcd", tmpWork1);
				*/

				rst.put("cm_time", rs.getString("cm_time"));
				if (rs.getString("cm_exename") != null) {
					if (rs.getString("cm_exename").substring(rs.getString("cm_exename").length()-1).equals(",")) {
						rst.put("cm_exename", rs.getString("cm_exename").substring(0,rs.getString("cm_exename").length()-1));
					} else rst.put("cm_exename", rs.getString("cm_exename"));
				} else {
					rst.put("cm_exename", "");
				}

				rst.put("cm_vercnt", Integer.toString(rs.getInt("cm_vercnt")));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_basedir", rs.getString("cm_basedir"));
				rst.put("cm_prcstep", Integer.toString(rs.getInt("cm_prcstep")));
				rst.put("cm_stepsta", Integer.toString(rs.getInt("cm_stepsta")));
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
			ecamsLogger.error("## Cmm0200_Prog.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.getProgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.getProgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.getProgList() Exception END ##");
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
					ecamsLogger.error("## Cmm0200_Prog.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public Object[] getSameList(String SysCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String tmpWork1 = "";
			strQuery.setLength(0);
			strQuery.append("select a.cm_rsrccd,a.cm_samersrc,a.cm_basename,       \n");
			strQuery.append("       a.cm_samename,a.cm_basedir,a.cm_samedir,       \n");
			strQuery.append("       a.cm_cmdyn,b.cm_codename,a.cm_factcd           \n");
			strQuery.append("  from cmm0037 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=?                                   \n");
			strQuery.append("   and b.cm_macode='JAWON'                            \n");
			strQuery.append("   and b.cm_micode=a.cm_samersrc                      \n");
			strQuery.append("order by a.cm_rsrccd                                  \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_samersrc", rs.getString("cm_samersrc"));
				rst.put("cm_samename", rs.getString("cm_samename"));
				rst.put("cm_basename", rs.getString("cm_basename"));
				rst.put("cm_samedir", rs.getString("cm_samedir"));
				rst.put("cm_basedir", rs.getString("cm_basedir"));
				rst.put("cm_cmdyn", rs.getString("cm_cmdyn"));
				rst.put("cm_factcd", rs.getString("cm_factcd"));
				tmpWork1 = "";
				if (rs.getString("cm_basename") != null) {
					tmpWork1 = rs.getString("cm_basename") + "->";
					if (rs.getString("cm_samename") != null) {
						tmpWork1 = tmpWork1 + rs.getString("cm_samename");
					}
				}
				rst.put("chgname", tmpWork1);
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
			ecamsLogger.error("## Cmm0200_Prog.getSameList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.getSameList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.getSameList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.getSameList() Exception END ##");
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
					ecamsLogger.error("## Cmm0200_Prog.getSameList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String rsrcInfo_Ins(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> sameList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           isrtSw      = false;
		int               parmCnt     = 0;
		int               i           = 0;
		String            strExeName  = etcData.get("cm_exename");
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			if (strExeName != "" && strExeName != null) {
				if (!strExeName.substring(strExeName.length()-1).equals(",")) {
					strExeName = strExeName + ",";
				}
			}
			isrtSw = true;
			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmm0036     \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?       \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_rsrccd"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getInt("cnt")>0) isrtSw = false;
				else isrtSw = true;
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0032                           \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?        \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_rsrccd"));
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0037                           \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?        \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_rsrccd"));
			pstmt.executeUpdate();
			pstmt.close();


			int cm_vercnt = 9999;
			if (etcData.get("cm_vercnt") != "" && etcData.get("cm_vercnt") != null){
				cm_vercnt = Integer.parseInt(etcData.get("cm_vercnt"));
			}
			parmCnt = 0;
			strQuery.setLength(0);
			if (isrtSw == true) {
				strQuery.append("insert into cmm0036                     \n");
				strQuery.append("   (cm_syscd,cm_rsrccd,cm_prcstep,      \n");
				strQuery.append("    cm_stepsta,cm_info,cm_time,         \n");
				strQuery.append("    cm_creatdt,cm_lastdt,cm_editor,     \n");
				strQuery.append("    cm_vercnt,cm_exename)               \n");
				strQuery.append("(select ?, ?, nvl(max(cm_prcstep),0)+1, \n");
				strQuery.append("      (nvl(max(cm_prcstep),0)*100)+1,   \n");
				strQuery.append("      ?, ?, SYSDATE, SYSDATE, ?, ?, ?   \n");
				strQuery.append("   from cmm0036                         \n");
				strQuery.append("  where cm_syscd=?)                     \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_rsrccd"));
				pstmt.setString(++parmCnt, etcData.get("info"));
				pstmt.setString(++parmCnt, etcData.get("cm_time"));
				pstmt.setString(++parmCnt, etcData.get("userid"));
				pstmt.setInt(++parmCnt, cm_vercnt);
				pstmt.setString(++parmCnt, strExeName);
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));

			} else {
				strQuery.append("update cmm0036 set                       \n");
				strQuery.append("   (cm_info,cm_time,cm_lastdt,cm_exename,\n");
				strQuery.append("    CM_closedt,cm_editor,cm_vercnt) =    \n");
				strQuery.append("(select ?, ?, SYSDATE, ?, '', ?, ?       \n");
				strQuery.append("   from cmm0036                          \n");
				strQuery.append("  where cm_syscd=? and cm_rsrccd=?)      \n");
				strQuery.append("where cm_syscd=? and cm_rsrccd=?         \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("info"));
				pstmt.setString(++parmCnt, etcData.get("cm_time"));
				pstmt.setString(++parmCnt, strExeName);
				pstmt.setString(++parmCnt, etcData.get("userid"));
				pstmt.setInt(++parmCnt, cm_vercnt);
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_rsrccd"));
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_rsrccd"));
			}
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			/*
			strQuery.setLength(0);
			strQuery.append("insert into cmm0032                \n");
			strQuery.append(" (CM_SYSCD,CM_RSRCCD) values       \n");
			strQuery.append(" (?, ?)                            \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1,etcData.get("cm_syscd"));
			pstmt.setString(2,etcData.get("cm_rsrccd"));
			pstmt.executeUpdate();
			pstmt.close();
			*/

			for (i=0;sameList.size()>i;i++) {
				if (sameList.get(i).get("cm_rsrccd").equals(etcData.get("cm_rsrccd"))) {
					strQuery.setLength(0);
					parmCnt = 0;
					strQuery.append("insert into cmm0037                     \n");
					strQuery.append(" (CM_SYSCD,CM_RSRCCD,CM_SAMERSRC,       \n");
					strQuery.append("  CM_SAMENAME,CM_BASENAME,CM_BASEDIR,   \n");
					strQuery.append("  CM_SAMEDIR,CM_FACTCD,CM_CREATDT,      \n");
					strQuery.append("  CM_LASTDT,CM_EDITOR,CM_CMDYN) values  \n");
					strQuery.append("(?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,?,?)\n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(++parmCnt,etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt,etcData.get("cm_rsrccd"));
					pstmt.setString(++parmCnt,sameList.get(i).get("cm_samersrc"));
					pstmt.setString(++parmCnt,sameList.get(i).get("cm_samename"));
					pstmt.setString(++parmCnt,sameList.get(i).get("cm_basename"));
					pstmt.setString(++parmCnt,sameList.get(i).get("cm_basedir"));
					pstmt.setString(++parmCnt,sameList.get(i).get("cm_samedir"));
					pstmt.setString(++parmCnt,sameList.get(i).get("cm_factcd"));
					pstmt.setString(++parmCnt, etcData.get("userid"));
					pstmt.setString(++parmCnt, sameList.get(i).get("cm_cmdyn"));
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
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Ins() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Ins() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Ins() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Ins() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String rsrcInfo_Close(String SysCd,String UserId,String RsrcCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmm0037                                   \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0037                                   \n");
			strQuery.append(" where cm_syscd=? and cm_samersrc=?              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0032                                   \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0033                                   \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0038                                   \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0072                                   \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("update cmm0036 set cm_closedt=SYSDATE,          \n");
			strQuery.append("   cm_lastdt=SYSDATE,cm_editor=?                \n");
			strQuery.append("where cm_syscd=? and cm_rsrccd=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserId);
			pstmt.setString(2, SysCd);
			pstmt.setString(3, RsrcCd);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();

			pstmt = null;
			conn = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Close() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Close() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Close() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of rsrcInfo_Close

    public String rsrcInfo_seq(String SysCd,String SecuCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			String[] strRsrc = SecuCd.split(",");

			for (int i=0 ; strRsrc.length>i ; i++) {
				strQuery.setLength(0);
				strQuery.append("update cmm0036 set cm_prcstep=?,           \n");
				strQuery.append("       cm_stepsta=((?-1)*100)+1            \n");
				strQuery.append(" where cm_syscd=? and cm_rsrccd=?          \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setInt(1, i+1);
				pstmt.setInt(2, i+1);
				pstmt.setString(3, SysCd);
				pstmt.setString(4, strRsrc[i]);
				pstmt.executeUpdate();
				pstmt.close();
			}

			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_seq() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_seq() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_seq() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_seq() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Prog.rsrcInfo_seq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] exeInfo() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select b.cm_codename,a.cm_exename from cmm0023 a, cmm0020 b where  \n");
			strQuery.append(" b.cm_macode ='LANGUAGE'       \n");
	        strQuery.append("and b.cm_micode = a.cm_langcd  \n");
	        strQuery.append("order by b.cm_codename         \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
            rtList.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_codename", rs.getString("cm_codename") + "(" + rs.getString("cm_exename") + ")");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;


			return rtList.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Prog.codeInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Prog.codeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCodeInfo() method statement
	public Object[] getRsrcInfo(String MACODE,int sortCd, String useyn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

//			String[] macode = MACODE.split(",");
			strQuery.append("select decode(cm_micode,'****',1,2) flag, cm_macode,cm_micode,cm_codename,cm_useyn from cmm0020 where \n");
			if(!useyn.equals("ALL")){
				strQuery.append("cm_useyn = ? and   \n");
			}
			strQuery.append("cm_macode = ?  \n");
            strQuery.append("and cm_micode<>'****'  \n");
        	strQuery.append("and cm_closedt is null \n");
	        if (sortCd == 0) {
	        	strQuery.append("order by cm_macode, flag, cm_micode \n");
	        } else {
	        	strQuery.append("order by cm_macode, flag, cm_codename \n");
	        }

            pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
			if(!useyn.equals("ALL")){
				pstmt.setString(1, useyn);
				pstmt.setString(2, MACODE);
			}else{
				pstmt.setString(1, MACODE);
			}

           ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getString("cm_micode").equals("****")) {
					if (strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
						rst = new HashMap<String, String>();
						rst.put("ID", "0");
						rst.put("cm_codename", strSelMsg);
						rst.put("cm_micode", "00");
						rst.put("cm_macode", rs.getString("cm_macode"));
						rtList.add(rst);
						rst = null;
					}
				} else {
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));

					if(rs.getString("cm_useyn").equals("N"))
						rst.put("cm_sum","[" + rs.getString("cm_micode") + "] 사용않함");
					else
						rst.put("cm_sum","[" + rs.getString("cm_micode") + "] " + rs.getString("cm_codename"));

					rst.put("cm_micode", rs.getString("cm_micode"));
					rst.put("cm_macode", rs.getString("cm_macode"));
					rst.put("cm_useyn", rs.getString("cm_useyn"));
					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement
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
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CodeInfo.getCodeInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## CodeInfo.getCodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCodeInfo() method statement
}//end of Cmm0200_Prog class statement
