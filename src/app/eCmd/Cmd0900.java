/*****************************************************************************************
	1. program ID	: Cmd0900.java
	2. create date	: 2008.04.08
	3. auth		    : m.s.kang
	4. update date	: 2009.05.28
	5. auth		    : no name
	6. description	: Request List
*****************************************************************************************/

package app.eCmd;

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

import app.common.*;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Cmd0900{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 요청현황을 조회합니다.
	 * @param  syscd,reqcd,teamcd,sta,requser,startdt,enddt,userid
	 * @return json
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getSysInfo(String UserId,String SecuYn,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		String            strSelMsg   = "";
		String            strSysCd    = "";
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}
			boolean adminYn = false;
			if (UserId != null && UserId != "") {
				UserInfo     userinfo = new UserInfo();
				adminYn = userinfo.isAdmin(UserId);
				if(adminYn) SecuYn = "N";
			}

			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysfc1, \n");
			strQuery.append("       a.cm_dirbase,a.cm_sysinfo,                     \n");
			strQuery.append("       sign(nvl(cm_stopst,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff1,\n");
			strQuery.append("       sign(nvl(cm_stoped,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff2 \n");
			strQuery.append("from cmm0030 a \n");
			if (SecuYn.toUpperCase().equals("Y")) {
				strQuery.append("where a.cm_syscd in (select cm_syscd from cmm0044 \n");
				strQuery.append("                      where cm_userid=?           \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				strQuery.append("and a.cm_closedt is null                      	   \n");
			} else {
				strQuery.append("where a.cm_closedt is null                    	   \n");
			}
			strQuery.append("  and a.cm_syscd in (select cm_syscd from cmm0036     \n");
			strQuery.append("                      where substr(cm_info,9,1)='1'  \n");
			strQuery.append("                      and cm_closedt is null)  \n");
			strQuery.append("order by a.cm_sysmsg \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());

            if (SecuYn.toUpperCase().equals("Y")){
            	pstmt.setString(1, UserId);
            }
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg.length() > 0 &&strSelMsg != "" && !strSelMsg.equals("")) {
					rst = new HashMap<String,String>();
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", strSelMsg);
					rst.put("cm_sysgb", "0");
					rst.put("cm_sysfc1","00");
					rst.put("cm_dirbase","00");
					rst.put("cm_sysinfo", "0");
					rst.put("TstSw", "0");
					rst.put("setyn", "N");
					rtList.add(rst);
					rst = null;
				}
				String tstInfo = rs.getString("cm_sysinfo");
				rst = new HashMap<String,String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				rst.put("cm_dirbase",rs.getString("cm_dirbase"));

				if (rs.getString("cm_sysinfo").substring(4,5).equals("1")) {
					if (rs.getInt("diff1")<0 && rs.getInt("diff2")>0 && adminYn == false) {
						rst.put("cm_stopsw", "1");
						tstInfo = tstInfo.substring(0,4) + "1";
					} else {
						rst.put("cm_stopsw", "0");
						tstInfo = tstInfo.substring(0,4) + "0";
					}
				} else rst.put("cm_stopsw", "0");
				rst.put("cm_sysinfo",tstInfo);
				rst.put("TstSw", "0");
				rst.put("websw", "N");
				rst.put("setyn", "N");
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getSysInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.getSysInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getSysInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.getSysInfo() Exception END ##");
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
					ecamsLogger.error("## Cmd0900.getSysInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

    public Object[] getProgList(String UserId,String SysCd,String ProgId,boolean subSw,String rsrcCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        int              parmCnt      = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {

			rtList.clear();
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT b.cm_dirpath,c.cm_codename,a.cr_rsrcname,        \n");
			strQuery.append("       a.cr_lstver,a.cr_itemid,                         \n");
			strQuery.append("       (select count(*) from cmd0011                    \n");
			strQuery.append("         where cd_itemid=a.cr_itemid) modcnt            \n");
			strQuery.append("  from cmr0020 a,cmm0070 b,cmm0020 c                    \n");
			strQuery.append(" where a.cr_syscd=? and a.cr_status<>'9'                \n");
			if (ProgId != null && ProgId != "") {
				if (subSw) {
					strQuery.append("and instr(?,a.cr_itemid)>0                      \n");
				} else {
					strQuery.append("and a.cr_rsrcname like '%' || ? || '%'          \n");
				}
			}
			if (!subSw) {
				strQuery.append("   and a.cr_rsrccd=?                                \n");
			}
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd  \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd  \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			if (ProgId != null && ProgId != "") pstmt.setString(++parmCnt, ProgId);
			if (!subSw) pstmt.setString(++parmCnt, rsrcCd);

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while (rs.next()){
	            rst = new HashMap<String, String>();
	            //rst.put("ID", Integer.toString(rs.getRow()));
        		rst.put("cm_dirpath", rs.getString("cm_dirpath"));  //경로
        		rst.put("cr_rsrcname",rs.getString("cr_rsrcname")); //프로그램명
        		rst.put("jawon",rs.getString("cm_codename"));       //프로그램종류
        		rst.put("cr_lstver",Integer.toString(rs.getInt("cr_lstver"))); //버전
        		rst.put("cr_itemid",rs.getString("cr_itemid"));     //itemid
        		rst.put("modcnt", rs.getString("modcnt"));
        		rtList.add(rst);
        		rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.getProgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getProgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.getProgList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgList() method statement

    public Object[] getModList(String UserId,String SysCd,String ProgId,boolean subSw,String rsrcCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        int              parmCnt      = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {

			rtList.clear();
			UserInfo     userinfo = new UserInfo();
			boolean adminYn = userinfo.isAdmin(UserId);

			conn = connectionContext.getConnection();
			strQuery.setLength(0);

			strQuery.append("SELECT b.cm_dirpath,c.cm_codename,a.cr_rsrcname,        \n");
			strQuery.append("       a.cr_lstver,a.cr_itemid,                         \n");
			strQuery.append("       (select count(*) from cmd0011                    \n");
			strQuery.append("         where cd_prcitem=a.cr_itemid) srccnt           \n");
			strQuery.append("  from cmr0020 a,cmm0070 b,cmm0020 c                    \n");
			strQuery.append(" where a.cr_syscd=? and a.cr_status<>'9'                \n");
			if (ProgId != null && ProgId != "") {
				if (!subSw) {
					strQuery.append("and a.cr_rsrcname like '%' || ? || '%'          \n");
				}
			}
			if (subSw) {
				strQuery.append("   and a.cr_rsrccd in (select cm_samersrc from cmm0037 b,cmr0020 a \n");
				strQuery.append("                        where b.cm_syscd=?                \n");
				strQuery.append("                          and b.cm_factcd = '09'          \n");
				strQuery.append("                          and b.cm_samersrc is not null   \n");
				strQuery.append("                          and b.cm_syscd=a.cr_syscd       \n");
				strQuery.append("                          and b.cm_rsrccd=a.cr_rsrccd     \n");
				strQuery.append("                          and instr(?,a.cr_itemid)>0)     \n");
			} else {
				strQuery.append("   and instr(?,a.cr_rsrccd)>0                       \n");
			}
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd  \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd  \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			if (ProgId != null && ProgId != "" && !subSw){
				pstmt.setString(++parmCnt, ProgId);
			}
			
			if (subSw) {
				pstmt.setString(++parmCnt, SysCd);
				pstmt.setString(++parmCnt, ProgId);
			} else {
				pstmt.setString(++parmCnt, rsrcCd);
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();

			while (rs.next()){
	            rst = new HashMap<String, String>();
	           // rst.put("ID", Integer.toString(rs.getRow()));
        		rst.put("cm_dirpath", rs.getString("cm_dirpath"));  //경로
        		rst.put("cr_rsrcname",rs.getString("cr_rsrcname")); //프로그램명
        		rst.put("jawon",rs.getString("cm_codename"));       //프로그램종류
        		rst.put("cr_lstver",Integer.toString(rs.getInt("cr_lstver"))); //버전
        		rst.put("cr_itemid",rs.getString("cr_itemid"));     //itemid
        		rst.put("checked","false");     //itemid
        		rst.put("srccnt", rs.getString("srccnt"));
        		rtList.add(rst);
        		rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getModList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.getModList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getModList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.getModList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.getModList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getModList() method statement

    public Object[] getRelatList(String UserId,String SysCd,String GbnCd,String ProgId,boolean subSw) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        int              parmCnt      = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {

			rtList.clear();

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT b.cm_dirpath,c.cm_codename,a.cr_rsrcname,            \n");
			strQuery.append("       f.cm_dirpath dirpath2,g.cm_codename codename2,       \n");
			strQuery.append("       e.cr_rsrcname rsrcname2,                             \n");
			strQuery.append("       d.cd_itemid,d.cd_prcitem                             \n");
			strQuery.append("  from cmr0020 a,cmm0070 b,cmm0020 c,cmd0011 d,             \n");
			strQuery.append("       cmr0020 e,cmm0070 f,cmm0020 g                        \n");
			strQuery.append(" where a.cr_syscd=?                                         \n");
			if (subSw) {
				strQuery.append("and instr(?,d.cd_itemid)>0                              \n");
			} else {
				if (GbnCd.equals("P") && ProgId != null && ProgId != "")
					strQuery.append("and a.cr_rsrcname like '%' || ? || '%'              \n");
				else if (GbnCd.equals("I") && ProgId != null && ProgId != "")
					strQuery.append("and a.cr_itemid=?                                   \n");
			}
			strQuery.append("   and a.cr_itemid=d.cd_itemid                              \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd      \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd      \n");
			strQuery.append("   and e.cr_syscd=?                                         \n");
			strQuery.append("   and e.cr_itemid=d.cd_prcitem                             \n");
			if (!subSw) {
				if (GbnCd.equals("M") && ProgId != null && ProgId != "")
					strQuery.append("and e.cr_rsrcname like '%' || ? || '%'              \n");
			}
			strQuery.append("   and e.cr_syscd=f.cm_syscd and e.cr_dsncd=f.cm_dsncd      \n");
			strQuery.append("   and g.cm_macode='JAWON' and g.cm_micode=e.cr_rsrccd      \n");
			if (!subSw) {
				if (GbnCd.equals("A") && ProgId != null && ProgId != "") {
					strQuery.append("and (a.cr_rsrcname like '%' || ? || '%' or          \n");
					strQuery.append("     e.cr_rsrcname like '%' || ? || '%')            \n");
				}
			}
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			if (subSw) pstmt.setString(++parmCnt, ProgId);
			else if (GbnCd.equals("P")  && ProgId != null && ProgId != "")
				pstmt.setString(++parmCnt, ProgId);
			else if (GbnCd.equals("I") && ProgId != null && ProgId != "")
				pstmt.setString(++parmCnt, ProgId);
			pstmt.setString(++parmCnt, SysCd);
			if (!subSw) {
				if (GbnCd.equals("M")  && ProgId != null && ProgId != "")
					pstmt.setString(++parmCnt, ProgId);
				if (GbnCd.equals("A") && ProgId != null && ProgId != "") {
					pstmt.setString(++parmCnt, ProgId);
					pstmt.setString(++parmCnt, ProgId);
				}
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();

			while (rs.next()){
	            rst = new HashMap<String, String>();
	           // rst.put("ID", Integer.toString(rs.getRow()));
        		rst.put("cm_dirpath", rs.getString("cm_dirpath"));  //경로
        		rst.put("cr_rsrcname",rs.getString("cr_rsrcname")); //프로그램명
        		rst.put("jawon",rs.getString("cm_codename"));       //프로그램종류
        		rst.put("cm_dirpath2", rs.getString("dirpath2"));   //경로
        		rst.put("cr_rsrcname2",rs.getString("rsrcname2"));  //프로그램명
        		rst.put("jawon2",rs.getString("codename2"));        //프로그램종류
        		rst.put("cd_itemid",rs.getString("cd_itemid"));     //프로그램itemid
        		rst.put("cd_prcitem",rs.getString("cd_prcitem"));   //모듈itemid
        		rtList.add(rst);
        		rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getRelatList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.getRelatList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getRelatList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.getRelatList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.getRelatList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRelatList() method statement

    public Object[] getProgRelat(String UserId,String SysCd,ArrayList<HashMap<String,String>> progList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        String           strItemId    = "";
        String           strProgId    = "";
        boolean          findSw       = false;

		ConnectionContext connectionContext = new ConnectionResource();
		try {

			rtList.clear();

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT b.cm_dirpath,c.cm_codename,a.cr_rsrcname,            \n");
			strQuery.append("       d.cd_itemid,d.cd_prcitem                             \n");
			strQuery.append("  from cmr0020 a,cmm0070 b,cmm0020 c,cmd0011 d              \n");
			strQuery.append(" where d.cd_itemid in (\n");
			for (int i=0;progList.size()>i;i++) {
				if (i == 0) strQuery.append("?");
				else strQuery.append(",?");
			}
			strQuery.append(") \n");
			strQuery.append("   and d.cd_prcitem=a.cr_itemid                             \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd      \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd      \n");
			strQuery.append(" order by b.cm_dirpath,a.cr_rsrcname                        \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			for (int i=0;progList.size()>i;i++) {
				pstmt.setString(i+1, progList.get(i).get("cr_itemid"));
			}
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();

			while (rs.next()){
				findSw = false;
				if (strItemId == "" || strItemId == null) {
					findSw = true;
					strItemId = rs.getString("cd_prcitem");
					strProgId = "";
				}
				else {
					if (!strItemId.equals(rs.getString("cd_prcitem"))) {
						findSw = true;
						rst.put("cd_itemid",strProgId);     //프로그램itemid
						rtList.add(rst);
						rst = null;
						strItemId = rs.getString("cd_prcitem");
						strProgId = "";
					}
				}
				if (findSw == true) {
					rst = new HashMap<String, String>();
		           //rst.put("ID", Integer.toString(rs.getRow()));
	        		rst.put("cm_dirpath", rs.getString("cm_dirpath"));  //경로
	        		rst.put("cr_rsrcname",rs.getString("cr_rsrcname")); //프로그램명
	        		rst.put("jawon",rs.getString("cm_codename"));       //프로그램종류
	        		rst.put("cd_prcitem",rs.getString("cd_prcitem"));   //모듈itemid
				}
				if (strProgId.length()>0) strProgId = strProgId + ",";
				strProgId = strProgId + rs.getString("cd_itemid");

			}//end of while-loop statement
			rs.close();
			pstmt.close();

			if (strItemId != "" && strItemId != null) rtList.add(rst);
			rst = null;
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getProgRelat() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.getProgRelat() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getProgRelat() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.getProgRelat() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.getProgRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRelatList() method statement

    public String updtRelat(String UserId, String SysCd,ArrayList<HashMap<String,String>> progList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

            for (int i=0;i<progList.size();i++){
    			if(progList.get(i).get("check").equals("false")){
		    		strQuery.setLength(0);
				   	strQuery.append("delete from cmd0011            \n");
				   	strQuery.append(" where cd_itemid = ?           \n");
				   	strQuery.append("   and cd_prcitem = ?          \n");
				    pstmt = conn.prepareStatement(strQuery.toString());
				    pstmt.setString(1,progList.get(i).get("cr_itemid"));
				    pstmt.setString(2,progList.get(i).get("cr_prcitem"));
				  	pstmt.executeUpdate();
				   	pstmt.close();
    			}else{
	    			strQuery.setLength(0);
					strQuery.append("delete from cmd0011            \n");
					strQuery.append(" where cd_itemid = ?           \n");
					strQuery.append("   and cd_prcitem = ?          \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1,progList.get(i).get("cr_itemid"));
					pstmt.setString(2,progList.get(i).get("cr_prcitem"));
					pstmt.executeUpdate();
					pstmt.close();

				   	strQuery.setLength(0);
					strQuery.append("insert into cmd0011(cd_itemid,cd_prcitem,cd_editor,cd_lastdt)     \n");
					strQuery.append("values(?, ?, ?, SYSDATE)                           \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1,progList.get(i).get("cr_itemid"));
					pstmt.setString(2,progList.get(i).get("cr_prcitem"));
					pstmt.setString(3,UserId);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();
    			}
            }

            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

    		return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.updtRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0900.updtRelat() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.updtRelat() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.updtRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0900.updtRelat() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.updtRelat() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.updtRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtRelat() method statement


    public String delRelat(String UserId,ArrayList<HashMap<String,String>> progList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            for (int i=0;i<progList.size();i++){
            	strQuery.setLength(0);
            	strQuery.append("delete cmd0011                                     \n");
            	strQuery.append(" where cd_itemid=? and cd_prcitem=?                \n");
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1,progList.get(i).get("cr_itemid"));
    			pstmt.setString(2,progList.get(i).get("cr_prcitem"));
                pstmt.executeUpdate();
                pstmt.close();
            }
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
					ecamsLogger.error("## Cmd0900.delRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0900.delRelat() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.delRelat() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.delRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0900.delRelat() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.delRelat() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.delRelat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delRelat() method statement

	public Object[] getRsrcList(String SysCd,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		String            strSelMsg   = "";
		String            strSameRsrc = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.equals("ALL")) strSelMsg = "전체";
				else if (SelMsg.equals("SEL")) strSelMsg = "선택하세요";
			}
			strQuery.setLength(0);
			strQuery.append("select a.cm_micode,a.cm_codename                        \n");
			strQuery.append("  from cmm0020 a,cmm0036 b                              \n");
			strQuery.append(" where b.cm_syscd=?                                     \n");
			strQuery.append("   and b.cm_closedt is null                             \n");
			strQuery.append("   and substr(b.cm_info,9,1)='1'                        \n");
			strQuery.append("   and exists (select y.cm_rsrccd from cmm0037 x,cmm0036 y \n");
			strQuery.append("                where x.cm_syscd=b.cm_syscd             \n");
			strQuery.append("                  and x.cm_rsrccd=b.cm_rsrccd           \n");
			strQuery.append("                  and x.cm_factcd='09'                  \n");
			strQuery.append("                  and x.cm_syscd=y.cm_syscd             \n");
			strQuery.append("                  and x.cm_samersrc=y.cm_rsrccd         \n");
			strQuery.append("                  and y.cm_closedt is null              \n");
			strQuery.append("                  and substr(y.cm_info,26,1)='0')       \n");
			strQuery.append("   and a.cm_macode='JAWON' and a.cm_micode=b.cm_rsrccd  \n");
			strQuery.append(" order by a.cm_codename                                 \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt = new LoggableStatement(conn,strQuery.toString());
			
	        pstmt.setString(1, SysCd);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        rsval.clear();
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg != "") {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_micode", "0000");
				   rst.put("cm_codename", strSelMsg);
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				
				strSameRsrc = "";
				strQuery.setLength(0);
				strQuery.append("select a.cm_rsrccd from cmm0036 a,cmm0037 b      \n");
				strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?            \n");
				strQuery.append("   and b.cm_factcd='09'                          \n");
				strQuery.append("   and b.cm_syscd=a.cm_syscd                     \n");
				strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                 \n");
				strQuery.append("   and a.cm_closedt is null                      \n");
				strQuery.append("   and substr(a.cm_info,26,1)='0'                \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, SysCd);
				pstmt2.setString(2,rs.getString("cm_micode"));
				rs2 = pstmt2.executeQuery();
				while (rs2.next()) {
					strSameRsrc = strSameRsrc + ","+rs2.getString("cm_rsrccd");
				}
				pstmt2.close();
				rs2.close();
				rst.put("samersrc", strSameRsrc);
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getRsrcList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0900.getRsrcList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0900.getRsrcList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0900.getRsrcList() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.getRsrcOpen() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of SelectRsrcOpen() method statement

}//end of Cmd0900 class statement
