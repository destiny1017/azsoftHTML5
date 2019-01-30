/*****************************************************************************************
	1. program ID	: eCmm1800.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: BBS DAO
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SignInfo {

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	
	public Object[] getMyConf_dept(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_signnm from cmm0045 a,cmm0040 b \n");
			strQuery.append(" where a.cm_userid= ?                       \n");
			strQuery.append("   and a.cm_signuser=b.cm_userid            \n");
			strQuery.append("   and b.cm_active='1'                      \n");
			strQuery.append("group by a.cm_signnm                        \n");


            pstmt = conn.prepareStatement(strQuery.toString());
         //   pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, UserID);
            //pstmt.setInt(2, cnt);

         //   ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();


            rsval.clear();
			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_signnm", "선택하세요");
					rsval.add(rst);
					rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_signnm", rs.getString("cm_signnm"));
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
			ecamsLogger.error("## SignInfo.getMyConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getMyConf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getMyConf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getMyConf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getMyConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getMyConf() method statement
	public Object[] getMyConf(String UserID,String SysCd,String JobCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_signnm from cmm0045 a,cmm0044 b \n");
			strQuery.append(" where a.cm_userid= ?                       \n");
			strQuery.append("   and a.cm_signuser=b.cm_userid            \n");
			strQuery.append("   and b.cm_syscd=? and instr(?,b.cm_jobcd)>0 \n");
			strQuery.append("   and b.cm_closedt is null                 \n");
			strQuery.append("group by a.cm_signnm                        \n");


            pstmt = conn.prepareStatement(strQuery.toString());
         //   pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, UserID);
            pstmt.setString(2, SysCd);
            pstmt.setString(3, JobCd);
            //pstmt.setInt(2, cnt);

         //   ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();


            rsval.clear();
			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_signnm", "선택하세요");
					rsval.add(rst);
					rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_signnm", rs.getString("cm_signnm"));
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
			ecamsLogger.error("## SignInfo.getMyConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getMyConf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getMyConf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getMyConf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getMyConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getMyConf() method statement

	public Object[] getSignInfo_dept(String UserID,String SignNm,String RgtCd,String PosCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
//		String WkUser                 = "";
//		String WkRgtCd                = "";
//		String WkJobCd                = "";
		ConnectionContext connectionContext = new ConnectionResource();
		int parmCnt = 0;
		try {

			conn = connectionContext.getConnection();

			strQuery.append("select distinct a.cm_signuser,b.cm_codename,d.cm_username, \n");
			strQuery.append("       d.cm_manid,g.cm_rgtcd,d.cm_status,               \n");
			strQuery.append("       d.cm_blankdts,d.cm_blankdte,d.cm_daegyul,        \n");
			strQuery.append("       to_char(SYSDATE,'yyyymmdd') sysday               \n");
			strQuery.append("  from cmm0043 g,cmm0040 d,cmm0020 b,cmm0045 a          \n");
			strQuery.append(" where a.cm_userid=? and a.cm_signnm=?             	 \n");
			strQuery.append("   and a.cm_signuser=d.cm_userid              		     \n");
			strQuery.append("   and d.cm_active='1'                   		         \n");
			strQuery.append("   and (d.cm_project in (select cm_deptcd               \n");
			strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
			strQuery.append("                         start with cm_deptcd in (select cm_project from cmm0040 \n");
			strQuery.append("                                                   where cm_userid=?) \n");
			strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd) \n");
			strQuery.append("    or d.cm_project2 in (select cm_deptcd           \n");
			strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
			strQuery.append("                         start with cm_deptcd in (select cm_project from cmm0040 \n");
			strQuery.append("                                                   where cm_userid=?) \n");
			strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd)) \n");
			if (RgtCd != null && RgtCd != "" && PosCd != null && PosCd != "") {
				strQuery.append("and (instr(?,g.cm_rgtcd)>0 or instr(?,g.cm_rgtcd)>0)\n");
			} else if (RgtCd != null && RgtCd != "") {
				strQuery.append("  and instr(?,g.cm_rgtcd)>0         	     	     \n");
			} else if (PosCd != null && PosCd != "") {
				strQuery.append("  and instr(?,g.cm_rgtcd)>0         		         \n");
			}
			strQuery.append("  and b.cm_macode='RGTCD' and b.cm_micode=g.cm_rgtcd    \n");
			strQuery.append("order by d.cm_username                 		         \n");

            pstmt = conn.prepareStatement(strQuery.toString());
       //     pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(++parmCnt, UserID);
            pstmt.setString(++parmCnt, SignNm);
            pstmt.setString(++parmCnt, UserID);
            pstmt.setString(++parmCnt, UserID);
			if (RgtCd != null && RgtCd != "" && PosCd != null && PosCd != "") {
				pstmt.setString(++parmCnt, RgtCd);
				pstmt.setString(++parmCnt, PosCd);
			} else if (RgtCd != null && RgtCd != "") {
				pstmt.setString(++parmCnt, RgtCd);
			} else if (PosCd != null && PosCd != "") {
				pstmt.setString(++parmCnt, PosCd);
			}
        //    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("rgtcd", rs.getString("cm_codename"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
				rst.put("cm_signuser", rs.getString("cm_signuser"));
				rst.put("cm_manid", rs.getString("cm_manid"));
				rst.put("cm_daegyul", rs.getString("cm_signuser"));
				if (rs.getString("cm_status").equals("9")) {
					if (rs.getInt("cm_blankdts")<=rs.getInt("sysday") &&
						rs.getInt("cm_blankdte")>=rs.getInt("sysday")) {
						rst.put("cm_daegyul", rs.getString("cm_daegyul"));
					}
				}
				rsval.add(rst);
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.error(ecmmtb.xml_toStr());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getSignInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getSignInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getSignInfo() method statement
	public Object[] getSignInfo(String UserID,String SignNm,String RgtCd,String PosCd,String JobCd,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int parmCnt = 0;
		try {

			conn = connectionContext.getConnection();

			strQuery.append("select distinct a.cm_signuser,b.cm_codename,d.cm_username, \n");
			strQuery.append("       d.cm_manid,g.cm_rgtcd,d.cm_status,               \n");
			strQuery.append("       d.cm_blankdts,d.cm_blankdte,d.cm_daegyul,        \n");
			strQuery.append("       to_char(SYSDATE,'yyyymmdd') sysday               \n");
			strQuery.append("  from cmm0044 c,cmm0043 g,cmm0040 d,cmm0020 b,cmm0045 a  \n");
			strQuery.append(" where a.cm_userid=? and a.cm_signnm=?             	 \n");
			strQuery.append("   and a.cm_signuser=d.cm_userid              		     \n");
			strQuery.append("   and d.cm_active='1'                   		         \n");
			strQuery.append("   and d.cm_userid=c.cm_userid              		     \n");
			strQuery.append("   and c.cm_syscd=?                       		         \n");
			strQuery.append("   and instr(?,c.cm_jobcd)>0              		         \n");
			if (RgtCd != null && RgtCd != "" && PosCd != null && PosCd != "") {
				strQuery.append("and (instr(?,g.cm_rgtcd)>0 or instr(?,g.cm_rgtcd)>0)\n");
			} else if (RgtCd != null && RgtCd != "") {
				strQuery.append("  and instr(?,g.cm_rgtcd)>0         	     	     \n");
			} else if (PosCd != null && PosCd != "") {
				strQuery.append("  and instr(?,g.cm_rgtcd)>0         		         \n");
			}
			strQuery.append("  and b.cm_macode='RGTCD' and b.cm_micode=g.cm_rgtcd    \n");
			strQuery.append("order by d.cm_username                 		         \n");

            pstmt = conn.prepareStatement(strQuery.toString());
       //     pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(++parmCnt, UserID);
            pstmt.setString(++parmCnt, SignNm);
            pstmt.setString(++parmCnt, SysCd);
            pstmt.setString(++parmCnt, JobCd);
			if (RgtCd != null && RgtCd != "" && PosCd != null && PosCd != "") {
				pstmt.setString(++parmCnt, RgtCd);
				pstmt.setString(++parmCnt, PosCd);
			} else if (RgtCd != null && RgtCd != "") {
				pstmt.setString(++parmCnt, RgtCd);
			} else if (PosCd != null && PosCd != "") {
				pstmt.setString(++parmCnt, PosCd);
			}
        //    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("rgtcd", rs.getString("cm_codename"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
				rst.put("cm_signuser", rs.getString("cm_signuser"));
				rst.put("cm_manid", rs.getString("cm_manid"));
				rst.put("cm_daegyul", rs.getString("cm_signuser"));
				if (rs.getString("cm_status").equals("9")) {
					if (rs.getInt("cm_blankdts")<=rs.getInt("sysday") &&
						rs.getInt("cm_blankdte")>=rs.getInt("sysday")) {
						rst.put("cm_daegyul", rs.getString("cm_daegyul"));
					}
				}
				rsval.add(rst);
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.error(ecmmtb.xml_toStr());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getSignInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getSignInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSignInfo() method statement

	/** RgtCd or RgtCd2 권한을 가진 결재자로 추가 가능한 결재자 조회 
	 * @param UserId : 사용자ID
	 * @param RgtCd : 팀내책임자[3]에 지정된 권한
	 * @param SysCd : 시스템코드
	 * @param JobCd : 업무코드
	 * @param RgtCd2 : 업무책임자[6]에 지정된 권한
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getSignLst_dept(String UserId,String RgtCd,String SysCd,String JobCd,String RgtCd2) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String WkUser                 = "";
		String WkRgtCd                = "";
//		String WkJobCd                = "";
		boolean findSw                = false;
		String[] svRgtCd              = null;
		String[] svJobCd              = null;
		int parmCnt = 0;
		int i = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			if (RgtCd != null && RgtCd != "") {
				svRgtCd = RgtCd.split(",");
				strQuery.setLength(0);
				strQuery.append("select d.cm_userid,b.cm_codename,d.cm_username,   \n");
				strQuery.append("       d.cm_manid,g.cm_rgtcd,d.cm_status,         \n");
				strQuery.append("       d.cm_blankdts,d.cm_blankdte,d.cm_daegyul,  \n");
				strQuery.append("       to_char(SYSDATE,'yyyymmdd') sysday         \n");
				strQuery.append(" from cmm0043 g,cmm0040 d,cmm0020 b               \n");
				strQuery.append("where (d.cm_project in (select cm_deptcd          \n");
				strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
				strQuery.append("                         start with cm_deptcd in (select cm_project from cmm0040 \n");
				strQuery.append("                                                   where cm_userid=?) \n");
				strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd \n");
				strQuery.append("                         union \n");
				strQuery.append("                         select cm_deptcd          \n");
				strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
				strQuery.append("                         start with cm_deptcd in (select cm_project2 from cmm0040 \n");
				strQuery.append("                                                   where cm_userid=?) \n");
				strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd) \n");
				strQuery.append("  		or d.cm_project2 in (select cm_deptcd           \n");
				strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
				strQuery.append("                         start with cm_deptcd in (select cm_project from cmm0040 \n");
				strQuery.append("                                                   where cm_userid=?) \n");
				strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd \n");
				strQuery.append("                         union \n");
				strQuery.append("                         select cm_deptcd          \n");
				strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
				strQuery.append("                         start with cm_deptcd in (select cm_project2 from cmm0040 \n");
				strQuery.append("                                                   where cm_userid=?) \n");
				strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd)) \n");
				strQuery.append("  and d.cm_userid=g.cm_userid \n");
				strQuery.append("  and d.cm_userid not in (?)  \n");
				strQuery.append("  and d.cm_active='1'                    	       \n");				
				strQuery.append("  and g.cm_rgtcd in (                        	   \n");
				for (i=0;svRgtCd.length>i;i++) {
					if (i>0) strQuery.append(",? ");
					else strQuery.append("? ");
				}
				strQuery.append(")	   \n");
				strQuery.append("  and b.cm_macode='RGTCD'                         \n");
				strQuery.append("  and b.cm_micode=g.cm_rgtcd                      \n");
				strQuery.append("order by d.cm_userid                 		       \n");
	
//	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(++parmCnt, UserId);
	            pstmt.setString(++parmCnt, UserId);
	            pstmt.setString(++parmCnt, UserId);
	            pstmt.setString(++parmCnt, UserId);
	            pstmt.setString(++parmCnt, UserId);
	            for (i=0;svRgtCd.length>i;i++) {
	            	pstmt.setString(++parmCnt, svRgtCd[i]);
				}
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            //System.out.println("+++++ rsval++++"+((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	
//	            rsval.clear();
				while (rs.next()){
					findSw = false;
					if (WkUser == null || WkUser == "") findSw = true;
					else {
						if (!WkUser.equals(rs.getString("cm_userid"))) {
							findSw = true;						
						} else {
							WkRgtCd = WkRgtCd + "," + rs.getString("cm_rgtcd");
						}
					}
					if (findSw) {
						if (WkUser != null && WkUser != "") {
							rst = rsval.get(rsval.size()-1);
							rst.put("cm_rgtcd", WkRgtCd);
							rsval.set(rsval.size()-1, rst);
							rst = null;
						}
						rst = new HashMap<String, String>();
						rst.put("ID", Integer.toString(rs.getRow()));
						rst.put("rgtcd", rs.getString("cm_codename"));
						rst.put("cm_username", rs.getString("cm_username"));
						rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
						rst.put("cm_signuser", rs.getString("cm_userid"));
						rst.put("cm_manid", rs.getString("cm_manid"));
						rst.put("cm_daegyul", rs.getString("cm_userid"));
						rst.put("gubun", "3");				
						if (rs.getString("cm_blankdts") != null && rs.getString("cm_blankdte") != null && rs.getString("cm_daegyul") != null) {
							if (rs.getInt("cm_blankdts")<=rs.getInt("sysday") &&
								rs.getInt("cm_blankdte")>=rs.getInt("sysday")) {
								rst.put("cm_daegyul", rs.getString("cm_daegyul"));
								
								strQuery.setLength(0);
								strQuery.append("select cm_username from cmm0040    \n");
								strQuery.append(" where cm_userid=?                 \n");
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, rs.getString("cm_daegyul"));
								rs2 = pstmt2.executeQuery();
								if (rs2.next()) {
									rst.put("cm_username", rs2.getString("cm_username")+"["+rs.getString("cm_username")+"]");
								}
								rs2.close();
								pstmt2.close();
							}
						}
						WkUser = rs.getString("cm_userid");
						WkRgtCd = rs.getString("cm_rgtcd");
						rsval.add(rst);
						rst = null;
					}
				}//end of while-loop statement
				rs.close();
				pstmt.close();
				
				if (WkRgtCd.indexOf(",")>0) {
					rst = rsval.get(rsval.size()-1);
					rst.put("cm_rgtcd", WkRgtCd);
					rsval.set(rsval.size()-1, rst);
					rst = null;
				}
			}
			
			if (RgtCd2 != null && RgtCd2 != "") {
				svRgtCd = RgtCd2.split(",");
				if (JobCd != null && JobCd != "") {
					svJobCd = JobCd.split(",");
				} else {
					svJobCd = null;
				}
				WkUser = "";
				WkRgtCd = "";
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("select d.cm_userid,b.cm_codename,d.cm_username,   \n");
				strQuery.append("       d.cm_manid,g.cm_rgtcd,d.cm_status,         \n");
				strQuery.append("       d.cm_blankdts,d.cm_blankdte,d.cm_daegyul,  \n");
				strQuery.append("       to_char(SYSDATE,'yyyymmdd') sysday         \n");
				strQuery.append(" from cmm0043 g,cmm0040 d,cmm0020 b               \n");
				strQuery.append("where d.cm_active='1'                    	       \n");
				strQuery.append("  and d.cm_userid<>?                              \n");
				strQuery.append("  and d.cm_userid=g.cm_userid                     \n");				
				strQuery.append("  and g.cm_rgtcd in (                        	   \n");
				for (i=0;svRgtCd.length>i;i++) {
					if (i>0) strQuery.append(",? ");
					else strQuery.append("? ");
				}
				strQuery.append(")	   \n");
				strQuery.append("  and b.cm_macode='RGTCD'                         \n");
				strQuery.append("  and b.cm_micode=g.cm_rgtcd                      \n");
				if (svJobCd != null && svJobCd.length>0) {
					strQuery.append("and exists (select 1 from cmm0044             \n");
					strQuery.append("             where cm_syscd=?                 \n");
					strQuery.append("               and cm_userid=d.cm_userid      \n");
					strQuery.append("               and cm_closedt is null         \n");
					strQuery.append("               and cm_jobcd in (              \n");
					for (i=0;svJobCd.length>i;i++) {
						if (i>0) strQuery.append(",? ");
						else strQuery.append("? ");
					}
					strQuery.append("))	   \n");
				} else {
					strQuery.append("and exists (select 1 from cmm0044 y,cmm0044 x \n");
					strQuery.append("             where x.cm_userid=?              \n");
					if (SysCd != null && SysCd != "" && !SysCd.equals("99999")) {
						strQuery.append("               and x.cm_syscd=?           \n");
					}
					strQuery.append("               and x.cm_closedt is null       \n");
					strQuery.append("               and x.cm_syscd=y.cm_syscd      \n");
					strQuery.append("               and x.cm_jobcd=y.cm_jobcd      \n");
					strQuery.append("               and y.cm_userid=d.cm_userid    \n");
					strQuery.append("               and y.cm_closedt is null)      \n");
				}
				strQuery.append("order by d.cm_username                 		   \n");
	
	            pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(++parmCnt, UserId);
	            for (i=0;svRgtCd.length>i;i++) {
	            	pstmt.setString(++parmCnt, svRgtCd[i]);
				}
	            if (svJobCd != null && svJobCd.length>0) {
	            	pstmt.setString(++parmCnt, SysCd);
	            	for (i=0;svJobCd.length>i;i++) {
		            	pstmt.setString(++parmCnt, svJobCd[i]);
					}
	            } else {
	            	pstmt.setString(++parmCnt, UserId);
	            	if (SysCd != null && SysCd != "" && !SysCd.equals("99999")) pstmt.setString(++parmCnt, SysCd);
	            }
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
				while (rs.next()){
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("rgtcd", rs.getString("cm_codename"));
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
					rst.put("cm_signuser", rs.getString("cm_userid"));
					rst.put("cm_manid", rs.getString("cm_manid"));
					rst.put("cm_daegyul", rs.getString("cm_userid"));
					rst.put("gubun", "6");				
					if (rs.getString("cm_blankdts") != null && rs.getString("cm_blankdte") != null && rs.getString("cm_daegyul") != null) {
						if (rs.getInt("cm_blankdts")<=rs.getInt("sysday") &&
							rs.getInt("cm_blankdte")>=rs.getInt("sysday")) {
							rst.put("cm_daegyul", rs.getString("cm_daegyul"));
							
							strQuery.setLength(0);
							strQuery.append("select cm_username from cmm0040    \n");
							strQuery.append(" where cm_userid=?                 \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(1, rs.getString("cm_daegyul"));
							rs2 = pstmt2.executeQuery();
							if (rs2.next()) {
								rst.put("cm_username", rs2.getString("cm_username")+"["+rs.getString("cm_username")+"]");
							}
							rs2.close();
							pstmt2.close();
						}
					}
					WkUser = rs.getString("cm_userid");
					WkRgtCd = rs.getString("cm_rgtcd");
					rsval.add(rst);
					rst = null;
				}//end of while-loop statement
				rs.close();
				pstmt.close();
			}
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;
			
//			ecamsLogger.error(rsval.toString());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getSignInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getSignInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSignInfo() method statement

	public Object[] getSignLst(String UserId,String RgtCd,String PosCd,String SysCd,String JobCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int parmCnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select d.cm_userid,b.cm_codename,d.cm_username,   \n");
			strQuery.append("       d.cm_manid,g.cm_rgtcd,d.cm_status,         \n");
			strQuery.append("       d.cm_blankdts,d.cm_blankdte,d.cm_daegyul,  \n");
			strQuery.append("       to_char(SYSDATE,'yyyymmdd') sysday         \n");
			strQuery.append(" from cmm0044 a,cmm0043 g,cmm0040 d,cmm0020 b     \n");
			strQuery.append("where a.cm_syscd=? and instr(?,a.cm_jobcd)>0      \n");
			strQuery.append("  and a.cm_closedt is null                  	   \n");
			strQuery.append("  and a.cm_userid=d.cm_userid                 	   \n");
			strQuery.append("  and d.cm_userid=g.cm_userid                 	   \n");
			strQuery.append("  and d.cm_active='1'                    	       \n");
			if (RgtCd != null && RgtCd != "" && PosCd != null && PosCd != "") {
				strQuery.append("  and (instr(?,g.cm_rgtcd)>0 or       		   \n");
				strQuery.append("       instr(?,g.cm_rgtcd)>0)       		   \n");
			} else if (RgtCd != null && RgtCd != "") {
				strQuery.append("  and instr(?,g.cm_rgtcd)>0         		   \n");
			} else {
				strQuery.append("  and instr(?,g.cm_rgtcd)>0         		   \n");
			}
			strQuery.append("  and b.cm_macode='RGTCD'                         \n");
			strQuery.append("  and b.cm_micode=g.cm_rgtcd                      \n");
			strQuery.append("order by d.cm_username                 		   \n");


            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(++parmCnt, SysCd);
            pstmt.setString(++parmCnt, JobCd);
            if (RgtCd != null && RgtCd != "") {
            	pstmt.setString(++parmCnt, RgtCd);
            }
            if (PosCd != null && PosCd != "") {
            	pstmt.setString(++parmCnt, PosCd);
            }

            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("rgtcd", rs.getString("cm_codename"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
				rst.put("cm_signuser", rs.getString("cm_userid"));
				rst.put("cm_manid", rs.getString("cm_manid"));
				rst.put("cm_daegyul", rs.getString("cm_userid"));
				if (rs.getString("cm_status").equals("9")) {
					if (rs.getInt("cm_blankdts")<=rs.getInt("sysday") &&
						rs.getInt("cm_blankdte")>=rs.getInt("sysday")) {
						rst.put("cm_daegyul", rs.getString("cm_daegyul"));
					}
				}
				rsval.add(rst);
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.error(ecmmtb.xml_toStr());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getSignInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getSignInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getSignInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getSignInfo() method statement
	public Object[] getDeptUser (String AddCd,String AddName,String DeptCd,String DeptName,String UserName,String SysCd,String JobCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String WkUser                 = "";
		String WkRgtCd                = "";
		String WkJobCd                = "";
		int    parmCnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_userid,a.cm_project,a.cm_username,a.cm_position,a.cm_manid, \n");
			strQuery.append("       b.cm_codename position,c.cm_rgtcd,d.cm_codename duty,d.cm_seqno, \n");
			strQuery.append("       e.cm_syscd,e.cm_jobcd                                            \n");
			strQuery.append("  from cmm0044 e,cmm0043 c,cmm0040 a,cmm0020 b,cmm0020 d                \n");
			strQuery.append("where a.cm_active='1'                                                   \n");
			if (UserName != "") {
				strQuery.append("and a.cm_username like '%' || ? || '%'                              \n");
			} else if (DeptCd != "") {
				strQuery.append("and a.cm_project=?                                                  \n");
			} else if (DeptName != "") {
			    strQuery.append("and a.cm_project in (select cm_deptcd from cmm0100                  \n");
			    strQuery.append("                        where cm_useyn='Y'                          \n");
			    strQuery.append("                          and cm_deptname like '%' || ? || '%')     \n");
			} else {
				strQuery.append("and a.cm_project in (select cm_deptcd from cmm0100                  \n");
			    strQuery.append("                      where cm_useyn='Y')                           \n");
			}
			strQuery.append("  and b.cm_macode='POSITION' and b.cm_micode=a.cm_position              \n");
			strQuery.append("  and d.cm_macode='DUTY' and d.cm_micode=a.cm_duty                      \n");
			strQuery.append("  and a.cm_userid=c.cm_userid                                           \n");
			strQuery.append("  and a.cm_userid=e.cm_userid                                           \n");
			strQuery.append("  and e.cm_closedt is null                                              \n");
			strQuery.append("  and e.cm_syscd=? and e.cm_jobcd=?                                     \n");
			strQuery.append("order by d.cm_seqno,a.cm_userid                                         \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            if (UserName != "") {
            	pstmt.setString(++parmCnt, UserName);
			} else if (DeptCd != "") {
				pstmt.setString(++parmCnt, DeptCd);
			} else if (DeptName != "") {
				pstmt.setString(++parmCnt, DeptName);
			}
            pstmt.setString(++parmCnt, SysCd);
            pstmt.setString(++parmCnt, JobCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
			while (rs.next()){
				if (WkUser.equals(rs.getString("cm_userid"))) {
					WkRgtCd = WkRgtCd + "," + rs.getString("cm_rgtcd");
					WkJobCd = WkJobCd + "," + rs.getString("cm_syscd") + rs.getString("cm_jobcd");
				} else {
					if (WkUser != "") {
						rst.put("cm_rgtcd", WkRgtCd);
						rst.put("cm_jobcd", WkJobCd);
						rsval.add(rst);
						WkRgtCd = "";
					}
					WkUser = rs.getString("cm_userid");
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("sgncd",AddName);
					rst.put("position", rs.getString("position"));
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("duty", rs.getString("duty"));
					rst.put("cm_duty", rs.getString("cm_position"));
					rst.put("cm_signuser", rs.getString("cm_userid"));
					WkRgtCd =  rs.getString("cm_rgtcd");
					WkJobCd = rs.getString("cm_syscd") + rs.getString("cm_jobcd");
					rst.put("cm_signcd", AddCd);
					rst.put("cm_manid", rs.getString("cm_manid"));
					rst.put("cm_seqno", rs.getString("cm_seqno"));
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			if (WkRgtCd != "") {
				rst.put("cm_rgtcd", WkRgtCd);
				rst.put("cm_jobcd", WkJobCd);
				rsval.add(rst);
				rst = null;
			}
			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.error(rsval.toString());
			return rsval.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getDeptUser() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getDeptUser() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getDeptUser() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getDeptUser() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getDeptUser() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getSignInfo() method statement
	public Object[] getSignUser (String UserName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int    parmCnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_userid,a.cm_username,a.cm_position, \n");
			strQuery.append("       d.cm_codename duty                       \n");
			strQuery.append("  from cmm0040 a,cmm0020 d                      \n");
			strQuery.append("where a.cm_active='1'                           \n");
			strQuery.append("  and a.cm_username like '%' || ? || '%'        \n");
			strQuery.append("  and d.cm_macode='POSITION' and d.cm_micode=a.cm_position \n");
			strQuery.append("order by d.cm_seqno,a.cm_userid                 \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++parmCnt, UserName);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_signuser", rs.getString("cm_userid"));
				rst.put("cm_daegyul", rs.getString("cm_userid"));
				rst.put("rgtcd", rs.getString("duty"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			
			//ecamsLogger.error(rsval.toString());
			return rsval.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignUser() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getSignUser() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getSignUser() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getSignUser() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getSignUser() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getSignInfo() method statement
	public String getConfTime (String RgtCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		Holiday_Check     holichk = new Holiday_Check();
		String            HoliSw  = "0";
		try {
			conn = connectionContext.getConnection();

			HoliSw = holichk.SelectHoli();
			holichk = null;
			strQuery.append("select cm_comsttime,cm_comedtime,cm_wedsttime,cm_wededtime,cm_holsttime,cm_holedtime, \n");
			strQuery.append(" to_char(SYSDATE,'hh24mi') hhmm,to_char(SYSDATE,'d') wday  from cmm0061               \n");
			strQuery.append("where cm_rgtcd=?                                                                      \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
           	pstmt.setString(1, RgtCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getString("wday").equals("1") || HoliSw == "1") {
					if (Integer.parseInt(rs.getString("hhmm")) < Integer.parseInt(rs.getString("cm_holsttime")) ||
						Integer.parseInt(rs.getString("hhmm")) > Integer.parseInt(rs.getString("cm_holedtime"))) HoliSw = "1";
					else HoliSw = "0";
				} else if (rs.getString("wday").equals("7")) {
					if (Integer.parseInt(rs.getString("hhmm")) < Integer.parseInt(rs.getString("cm_wedsttime")) ||
						Integer.parseInt(rs.getString("hhmm")) > Integer.parseInt(rs.getString("cm_wededtime"))) HoliSw = "1";
				} else  {
					if (Integer.parseInt(rs.getString("hhmm")) < Integer.parseInt(rs.getString("cm_comsttime")) ||
						Integer.parseInt(rs.getString("hhmm")) > Integer.parseInt(rs.getString("cm_comedtime"))) HoliSw = "1";
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			return HoliSw;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SignInfo.getConfTime() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SignInfo.getConfTime() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SignInfo.getConfTime() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SignInfo.getConfTime() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SignInfo.getConfTime() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getSignInfo() method statement
}//end of SignInfo class statement
