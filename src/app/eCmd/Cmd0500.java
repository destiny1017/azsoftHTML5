/*****************************************************************************************
	1. program ID	: eCmd0500.java
	2. create date	: 2008.07. 10
	3. auth		    : no name
	4. update date	: 09.07.16
	5. auth		    :
	6. description	: [프로그램]->[프로그램정보] 화면
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd0500{
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

    public Object[] Cmd0500_Cbo_Set(String L_Syscd,String SecuYn,String UserId,String ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		int             parmCnt       = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("SELECT c.cm_jobcd,c.cm_jobname from cmm0102 c,cmm0034 a   \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_jobcd=c.cm_jobcd             \n");
			strQuery.append("   and a.cm_closedt is null                               \n");
			if (SecuYn.equals("N")) {
				strQuery.append("and a.cm_jobcd in (select cm_jobcd from cmm0044       \n");
				strQuery.append("                    where cm_syscd=? and cm_userid=?  \n");
				strQuery.append("                      and cm_closedt is null)         \n");
			}
			if (ItemId != null && ItemId != "" && SecuYn.equals("N")) {
				strQuery.append("union                                                   \n");
				strQuery.append("select d.cm_jobcd,d.cm_jobname from cmm0102 d,cmr0020 b \n");
				strQuery.append(" where b.cr_itemid=? and b.cr_jobcd=d.cm_jobcd          \n");
				strQuery.append(" group by cm_jobcd,cm_jobname \n");
				strQuery.append(" order by cm_jobcd,cm_jobname \n");
			} else {
				strQuery.append(" group by c.cm_jobcd,c.cm_jobname \n");
				strQuery.append(" order by c.cm_jobcd,c.cm_jobname \n");
			}

			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(++parmCnt, L_Syscd);
		    if (SecuYn.equals("N")) {
		    	pstmt.setString(++parmCnt, L_Syscd);
		    	pstmt.setString(++parmCnt, UserId);
		    }
		    if (ItemId != null && ItemId != "" && SecuYn.equals("N"))
		    	pstmt.setString(++parmCnt, ItemId);
		    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", "Cbo_Job");
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
	           	rsval.add(rst);
	           	rst = null;
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("SELECT c.cm_micode,c.cm_codename from cmm0020 c,cmm0036 a where \n");
			strQuery.append(" a.cm_syscd=? and \n");	//L_Syscd
			strQuery.append(" c.cm_macode='JAWON' and \n");
			strQuery.append(" a.cm_rsrccd=c.cm_micode \n");
			strQuery.append(" and a.cm_closedt is null \n");
			strQuery.append(" and a.cm_rsrccd not in (select cm_samersrc from cmm0037 \n");
			strQuery.append("           			   where cm_syscd=a.cm_syscd	  \n");
			//strQuery.append("          				     and cm_factcd <> '09' 		  \n");
			strQuery.append("          				     and cm_factcd = '26' 		  \n");
			strQuery.append("             				 and cm_samersrc is not null) \n");
			//strQuery.append(" order by c.cm_seqno,c.cm_micode,c.cm_codename           \n");
			strQuery.append(" order by c.cm_codename,c.cm_micode,c.cm_seqno           \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, L_Syscd);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", "Cbo_RsrcCd");
				rst.put("cm_micode", rs.getString("cm_micode"));
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

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;


		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCbo_Set() method statement

    public Object[] getCbo_SysCd(String UserId,String SecuYn,String WkSys) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<Hashtable<String, String>>  rsval = new ArrayList<Hashtable<String, String>>();
		Hashtable<String, String>			  rst  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb \n");
	        if (!SecuYn.equals("Y") && (WkSys == null || WkSys == "")){
	        	strQuery.append("from cmm0044 b, cmm0030 a where \n");
	        	strQuery.append("b.cm_userid=? and \n");//UserId
	        	strQuery.append("a.cm_syscd=b.cm_syscd and \n");
	        	strQuery.append("b.cm_closedt is null and \n");
	        //}else if (!Sv_Admin.toUpperCase().equals("Y") && ViewFg.equals("true")){
	        }else if (WkSys != null && WkSys != ""){
	        	strQuery.append("from cmm0030 a where \n");
	           	strQuery.append(" a.cm_syscd=? and \n");//WkSys
	        }else{
	        	strQuery.append("from cmm0030 a where \n");
	        }
	        strQuery.append("a.cm_closedt is null and substr(a.cm_sysinfo,1,1)='0' \n");
	        strQuery.append("group by a.cm_syscd,a.cm_sysmsg,a.cm_sysgb \n");
	        strQuery.append("order by a.cm_sysmsg,a.cm_syscd,a.cm_sysgb \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt = new LoggableStatement(conn,strQuery.toString());

		    int Cnt = 0;
	        if (!SecuYn.equals("Y") && (WkSys == null || WkSys == "")){
	        	pstmt.setString(++Cnt, UserId);
	        }else if (WkSys != null && WkSys != ""){
	        	pstmt.setString(++Cnt, WkSys);
	        }

	        //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
		    rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new Hashtable<String, String>();
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb", rs.getString("cm_sysgb"));
				rst.put("cm_syscd", rs.getString("cm_syscd"));
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
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCbo_SysCd() method statement

    public Object[] getCbo_LangCd(String L_SysCd,String RsrcCd) throws SQLException, Exception {
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
			strQuery.append("SELECT b.cm_micode,b.cm_codename from cmm0020 b,cmm0032 a where \n");
			strQuery.append(" a.cm_syscd=? and \n");	//L_Syscd
			strQuery.append(" a.cm_rsrccd=? and \n");	//L_Rsrccd
			strQuery.append(" b.cm_macode='LANGUAGE' and \n");
			strQuery.append(" a.cm_langcd=b.cm_micode \n");
			strQuery.append(" order by b.cm_micode,b.cm_codename \n");
            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt =  new LoggableStatement(conn, strQuery.toString());
	    	pstmt.setString(1, L_SysCd);
	    	pstmt.setString(2, RsrcCd);
	    	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", "Cbo_LangCd");
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
	           	rsval.add(rst);
	           	rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs=null;
			pstmt=null;
			conn=null;

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCbo_LangCd() method statement


    public Object[] getDir_Check(String UserId,String SecuYn,String L_Syscd,
			String L_ItemId,String RsrcCd,String L_DsnCd,String FindFg) throws SQLException, Exception {
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
			strQuery.append("select a.cm_dirpath,a.cm_dsncd \n");
		    if (!SecuYn.equals("Y")){
		        strQuery.append("from cmm0070 a,cmm0073 b,cmm0072 c,cmm0044 d,cmr0020 e \n");
		        strQuery.append("where e.cr_itemid=?                                    \n");
		        strQuery.append("  and e.cr_syscd=d.cm_syscd and e.cr_jobcd=d.cm_jobcd  \n");
		        strQuery.append("  and d.cm_userid=?                                    \n");
		        strQuery.append("  and d.cm_syscd=b.cm_syscd and d.cm_jobcd=b.cm_jobcd  \n");
		    } else{
		        strQuery.append(" from cmm0070 a,cmm0073 b,cmm0072 c,cmr0020 e          \n");
		        strQuery.append("where e.cr_itemid=?                                    \n");
		        strQuery.append("  and e.cr_syscd=b.cm_syscd and e.cr_jobcd=b.cm_jobcd  \n");
		    }
	        strQuery.append("and a.cm_syscd=b.cm_syscd and a.cm_dsncd=b.cm_dsncd        \n");
	        strQuery.append("and a.cm_syscd=c.cm_syscd and a.cm_dsncd=c.cm_dsncd        \n");
	        strQuery.append("and c.cm_rsrccd=?                                          \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
	        int CNT = 0;
	        pstmt.setString(++CNT, L_ItemId);
		    if (!SecuYn.equals("Y")){
		    	pstmt.setString(++CNT, UserId);
		    }
			pstmt.setString(++CNT, RsrcCd);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			while (rs.next()){
				if (L_DsnCd != "" && FindFg.equals("false")){
					if (L_DsnCd.equals(rs.getString("cm_dsncd"))) FindFg = "true";
				}
				rst = new HashMap<String, String>();
				rst.put("ID", "Cbo_Dir");
				rst.put("cm_dsncd", rs.getString("cm_dsncd"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
	           	rsval.add(rst);
	           	rst = null;
			}
			rs.close();
			pstmt.close();

			if (FindFg.equals("false")){
				strQuery.setLength(0);
	    	   	strQuery.append("select cm_dirpath from cmm0070 where \n");
	    	   	strQuery.append(" cm_syscd=? and \n");	//L_Syscd
	    	   	strQuery.append(" cm_dsncd=? \n");	//L_DsnCd
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, L_Syscd);
				pstmt.setString(2, L_DsnCd);
				rs = pstmt.executeQuery();
				if (rs.next()){
					rst = new HashMap<String, String>();
					rst.put("ID", "Cbo_Dir");
					rst.put("cm_dsncd", L_DsnCd);
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
		           	rsval.add(rst);
		           	rst = null;
				}
				rs.close();
				pstmt.close();

			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCbo_Set() method statement


    //검색창에 공백 제거
    public static String allTrim(String s)
    {
        if (s == null)
            return null;
        else if (s.length() == 0)
            return "";

        int len = s.length();
        int i = 0;
        int j = len;

        for (i = 0; i < len; i++) {
            if ( s.charAt(i) != ' ' && s.charAt(i) != 't' && s.charAt(i) != 'r' && s.charAt(i) != 'n' )
                break;
        }
        if (i == len)
            return "";

        for (j = len - 1; j >= i; j--) {
            if ( s.charAt(i) != ' ' && s.charAt(i) != 't' && s.charAt(i) != 'r' && s.charAt(i) != 'n' )
                break;
        }
        return s.substring(i, j + 1);
    }


	public Object[] getSql_Qry(String UserId,String SecuYn,String ViewFg, String L_Syscd,String Txt_ProgId,String DsnCd,String Rsrccd, String DirPath)
    	throws SQLException, Exception {

		allTrim(Txt_ProgId);
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<Hashtable<String, String>>  rtList = new ArrayList<Hashtable<String, String>>();
		Hashtable<String, String>			  rst    = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			String			  strRsrcCd = "";
//			int               i = 0;
			boolean           findSw = false;

			strQuery.setLength(0);
			strQuery.append("select cm_rsrccd from cmm0036                     \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
			strQuery.append("   and cm_rsrccd not in (select distinct cm_samersrc \n");
			strQuery.append("                           from cmm0037           \n");
			strQuery.append("                          where cm_syscd=?        \n");
//			strQuery.append("                            and cm_factcd='04'    \n");
			strQuery.append("                            and cm_factcd='26'    \n");
			strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, L_Syscd);
            pstmt.setString(2, L_Syscd);

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	if (strRsrcCd != "") strRsrcCd = strRsrcCd + ",";
            	strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd");
            }
			rs.close();
			pstmt.close();

			//strRsrc = strRsrcCd.split(",");

			if (strRsrcCd.length() > 0) {
				strQuery.setLength(0);
				strQuery.append("select a.cr_syscd,a.cr_dsncd,a.cr_rsrcname,a.cr_jobcd,a.cr_itemid,   \n");
				strQuery.append("       a.cr_lstver,b.cm_dirpath,c.cm_jobname,a.cr_status,            \n");
				//strQuery.append("       a.cr_teststa, a.cr_realsta, 			             		  \n");
				strQuery.append("       a.cr_rsrccd,d.cm_codename,e.cm_codename as sta,     		  \n");
				//strQuery.append("       h.cm_codename as teststa, i.cm_codename as realsta, 		  \n");
				strQuery.append("       g.cm_info      												  \n");
		        strQuery.append("from cmr0020 a,cmm0070 b,cmm0102 c,cmm0020 d,cmm0020 e,cmm0036 g     \n");
		        
		        if (!SecuYn.equals("Y") && ViewFg.equals("false")){
					strQuery.append(",cmm0044 k \n");
				}
		        strQuery.append("where a.cr_syscd=?                                                     \n");
		        if (!SecuYn.equals("Y") && ViewFg.equals("false")){
		        	strQuery.append("and a.cr_syscd=k.cm_syscd and k.cm_userid=? and k.cm_closedt is null and k.cm_jobcd=a.cr_jobcd \n");
		        }
		        if (DsnCd != "" && DsnCd != null) {
			    	strQuery.append("and a.cr_dsncd=?                                  \n");	//L_Dsncd
			    	strQuery.append("and a.cr_rsrcname=?                               \n");	// %Txt_ProgId%
			    } else strQuery.append("and upper(a.cr_rsrcname) like upper(?)         \n");	// %Txt_ProgId%
		        if(!"ALL".equals(Rsrccd)){
		        	strQuery.append("and a.cr_rsrccd=?                                 \n");
		        }
		        if (!"".equals(DirPath) && null != DirPath){
		        	strQuery.append("and upper(b.cm_dirpath) like upper(?)			   \n");
		        }
			    strQuery.append("and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
			    strQuery.append("and a.cr_jobcd=c.cm_jobcd                             \n");
			    strQuery.append("and d.cm_macode='JAWON' and a.cr_rsrccd=d.cm_micode   \n");
			    strQuery.append("and e.cm_macode='CMR0020' and a.cr_status=e.cm_micode \n");
			    //strQuery.append("and h.cm_macode='CMR0020' and a.cr_teststa=h.cm_micode \n");
			    //strQuery.append("and i.cm_macode='CMR0020' and a.cr_realsta=i.cm_micode \n");
			    strQuery.append("and a.cr_syscd=g.cm_syscd and a.cr_rsrccd=g.cm_rsrccd \n");

			    //pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt =  new LoggableStatement(conn, strQuery.toString());
		        int CNT = 0;
			    pstmt.setString(++CNT, L_Syscd);
			    if (!SecuYn.equals("Y") && ViewFg.equals("false")){
			    	pstmt.setString(++CNT, UserId);
			    }
			    if (DsnCd != "" && DsnCd != null) {
			    	pstmt.setString(++CNT, DsnCd);
			    	pstmt.setString(++CNT, Txt_ProgId);
			    } else pstmt.setString(++CNT, "%"+Txt_ProgId+"%");
			    if(!"ALL".equals(Rsrccd)){
			    	pstmt.setString(++CNT, Rsrccd);
			    }
		        if (!"".equals(DirPath) && null != DirPath){
		        	pstmt.setString(++CNT, "%"+DirPath+"%");
		        }
		        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

	            rtList.clear();
				while (rs.next()){
					if (strRsrcCd.indexOf(rs.getString("cr_rsrccd"))>=0) findSw = true;
					else findSw = false;

					if (findSw) {
						rst = new Hashtable<String,String>();
						rst.put("cm_jobname",rs.getString("cm_jobname"));
						rst.put("cm_dirpath",rs.getString("cm_dirpath"));
						rst.put("cm_codename",rs.getString("cm_codename"));
						rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
						rst.put("cr_lstver",rs.getString("cr_lstver"));
						rst.put("sta",rs.getString("sta"));
						//rst.put("teststa",rs.getString("teststa"));
						//rst.put("realsta",rs.getString("realsta"));
						rst.put("cr_dsncd",rs.getString("cr_dsncd"));
						rst.put("cr_syscd",rs.getString("cr_syscd"));
						rst.put("cr_jobcd",rs.getString("cr_jobcd"));
						rst.put("cr_itemid",rs.getString("cr_itemid"));
						rst.put("baseitem",rs.getString("cr_itemid"));
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("subset", "N");
						rst.put("base","0");
						rtList.add(rst);
						rst = null;
					}
				}//end of while-loop statement
				rs.close();
				pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (rtObj != null)	rtObj = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSql_Qry() method statement


	public Object[] getSql_Qry_Sub(String UserId,String ItemId,String SysCd)
    	throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cr_syscd,a.cr_dsncd,a.cr_rsrcname,a.cr_jobcd,  \n");
			strQuery.append("       a.cr_itemid,a.cr_lstver,b.cm_dirpath,c.cm_jobname\n");
			strQuery.append("       ,d.cm_codename,e.cm_codename as sta,g.cm_info    \n");
	        strQuery.append("from cmr0020 a,cmm0070 b,cmm0102 c,cmm0020 d,cmm0020 e, \n");
	        strQuery.append("     cmm0036 g,cmr1010 f,cmr0020 h                      \n");
	        strQuery.append("where h.cr_itemid=? and h.cr_acptno=f.cr_acptno         \n");
	        strQuery.append("  and h.cr_itemid=f.cr_baseitem                      \n");
	        strQuery.append("  and f.cr_itemid<>?                                    \n");
	        strQuery.append("  and f.cr_rsrccd not in (select cm_samersrc            \n");
	        strQuery.append("                            from cmm0037                \n");
	        strQuery.append("                           where cm_syscd=?             \n");
			strQuery.append("                             and cm_rsrccd<>cm_samersrc) \n");
	        strQuery.append("  and f.cr_itemid=a.cr_itemid                           \n");
		    strQuery.append("  and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
		    strQuery.append("  and a.cr_jobcd=c.cm_jobcd                             \n");
		    strQuery.append("  and d.cm_macode='JAWON' and a.cr_rsrccd=d.cm_micode   \n");
		    strQuery.append("  and e.cm_macode='CMR0020' and a.cr_status=e.cm_micode \n");
		    strQuery.append("  and a.cr_syscd=g.cm_syscd and a.cr_rsrccd=g.cm_rsrccd \n");

		    pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
		    pstmt.setString(1,ItemId );
		    pstmt.setString(2,ItemId );
		    pstmt.setString(3,SysCd );
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("sta",rs.getString("sta"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("baseitem",ItemId);
				rst.put("base","1");
				rst.put("subset", "N");
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;
			return rtObj;


		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0500.getSql_Qry_Sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0500.getSql_Qry_Sub() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0500.getSql_Qry_Sub() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0500.getSql_Qry_Sub() Exception END ##");
			throw exception;
		}finally{
			if (rtObj != null)	rtObj = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0500.getSql_Qry_Sub() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSql_Qry_Sub() method statement

	public String getProgInfo(String ItemId) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strMsg      = "";
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cr_syscd,cr_rsrcname,cr_dsncd from cmr0020     \n");
		    strQuery.append(" where cr_itemid=?                                    \n");

	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, ItemId);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			if (rs.next()){
				strMsg = rs.getString("cr_syscd");
				strMsg = strMsg+rs.getString("cr_dsncd");
				strMsg = strMsg+rs.getString("cr_rsrcname");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return strMsg;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSql_Qry() method statement

    public Object[] getItemId(String ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cr_syscd, cr_rsrcname from cmr0020 \n");
			strQuery.append("where cr_itemid=? \n");//ItemId
	        pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, ItemId);
	        rs = pstmt.executeQuery();
			if (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getItemId() method statement

    public Object[] Cmd0500_Lv_File_ItemClick(String UserId,String SecuYn,String L_SysCd,String L_JobCd,
    		String L_ItemId)	throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

    		strQuery.setLength(0);
    		strQuery.append("select a.cr_rsrcname,a.cr_story,a.cr_langcd,a.cr_editor,      \n");
    		strQuery.append("       to_char(a.cr_opendate,'yyyy/mm/dd hh24:mi') cr_opendate, \n");
    		strQuery.append("       to_char(a.cr_lastdate,'yyyy/mm/dd hh24:mi') cr_lastdate, \n");
    		strQuery.append("       a.cr_jobcd,a.cr_creator,a.cr_status,a.cr_lstver,         \n");
    		strQuery.append("       a.cr_rsrccd,a.cr_lstusr,b.cm_info,a.cr_syscd,            \n");
    		strQuery.append("       a.cr_testusr, a.cr_realusr, a.cr_isrid,					 \n");
    		strQuery.append("       to_char(a.cr_testdate,'yyyy/mm/dd hh24:mi') cr_testdate, \n");
    		strQuery.append("       to_char(a.cr_realdate,'yyyy/mm/dd hh24:mi') cr_realdate, \n");
    		strQuery.append("       to_char(a.cr_lstdat,'yyyy/mm/dd hh24:mi') cr_lstdat,c.cm_codename \n");
    		strQuery.append("  from cmm0020 c,cmm0036 b,cmr0020 a                            \n");
    		strQuery.append(" where a.cr_itemid=?                                            \n");
    		strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd        \n");
    		strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd \n");

		    //pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt =  new LoggableStatement(conn, strQuery.toString());
	    	pstmt.setString(1, L_ItemId);
	    	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	rs = pstmt.executeQuery();

	    	if (rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("ID","Sql_Qry_Prog1");
				rst.put("Lbl_ProgName",rs.getString("cr_story"));
				rst.put("Lbl_CreatDt",rs.getString("cr_opendate"));
				rst.put("Lbl_LastDt",rs.getString("cr_lastdate"));
				rst.put("WkJobCd",rs.getString("cr_jobcd"));
				rst.put("WkSta",rs.getString("cr_status"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("WkVer",Integer.toString(rs.getInt("cr_lstver")));
				rst.put("WkRsrcCd",rs.getString("cr_rsrccd"));
				rst.put("Lbl_LstDat",rs.getString("cr_lstdat"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("Lbl_LstDatTest",rs.getString("cr_testdate"));
				rst.put("cr_testusr",rs.getString("cr_testusr"));
				rst.put("Lbl_LstDatReal",rs.getString("cr_realdate"));
				rst.put("cr_realusr",rs.getString("cr_realusr"));
				rst.put("RsrcName", rs.getString("cm_codename"));
				rst.put("cr_isrid", rs.getString("cr_isrid"));
				
				if (SecuYn.equals("Y") || UserId.equals(rs.getString("cr_editor"))) rst.put("WkSecu","true");
				else {
					rst.put("WkSecu","false");
					strQuery.setLength(0);
	        	   	strQuery.append("select count(*) cnt from cmm0044 \n");
	        	   	strQuery.append(" where cm_userid=?               \n");
	        	   	strQuery.append("   and cm_syscd=? and cm_jobcd=? \n");
	        	   	strQuery.append("   and cm_closedt is null        \n");
	        	   	pstmt2 = conn.prepareStatement(strQuery.toString());
	        	   	pstmt2.setString(1, UserId);
	        	   	pstmt2.setString(2, rs.getString("cr_syscd"));
	        	   	pstmt2.setString(3, rs.getString("cr_jobcd"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getInt("cnt") > 0) rst.put("WkSecu","true");
						else rst.put("WkSecu","false");
					}
					rs2.close();
					pstmt2.close();
				}

				if (rs.getString("cr_creator") != null){
					strQuery.setLength(0);
	        	   	strQuery.append("select cm_username from cmm0040 where cm_userid=? \n");
	        	   	pstmt2 = conn.prepareStatement(strQuery.toString());
	        	   	pstmt2.setString(1, rs.getString("cr_creator"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) rst.put("Lbl_Creator",rs2.getString("cm_username"));
					rs2.close();
					pstmt2.close();
				}
				if (rs.getString("cr_editor") != null){
	        	   	strQuery.setLength(0);
	        	   	strQuery.append("select cm_username from cmm0040 where cm_userid=? \n");
	        	   	pstmt2 = conn.prepareStatement(strQuery.toString());
	        	   	pstmt2.setString(1, rs.getString("cr_editor"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) rst.put("Lbl_Editor",rs2.getString("cm_username"));
					rs2.close();
					pstmt2.close();
		        }
				if (rs.getString("cr_testusr") != null){
					if (rs.getString("cr_testusr").length() > 0 ){
		        	   	strQuery.setLength(0);
		        	   	strQuery.append("select cm_username from cmm0040 where cm_userid=? \n");
		        	   	pstmt2 = conn.prepareStatement(strQuery.toString());
		        	   	pstmt2.setString(1, rs.getString("cr_testusr"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) rst.put("Lbl_LstTestUsr",rs2.getString("cm_username"));
						rs2.close();
						pstmt2.close();
					}
				}
				if (rs.getString("cr_realusr") != null){
					if (rs.getString("cr_realusr").length() > 0 ){
		        	   	strQuery.setLength(0);
		        	   	strQuery.append("select cm_username from cmm0040 where cm_userid=? \n");
		        	   	pstmt2 = conn.prepareStatement(strQuery.toString());
		        	   	pstmt2.setString(1, rs.getString("cr_realusr"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) rst.put("Lbl_LstRealUsr",rs2.getString("cm_username"));
						rs2.close();
						pstmt2.close();
					}
				}
				if (rs.getString("cr_lstusr") != null){
					if (rs.getString("cr_lstusr").length() > 0 ){
		        	   	strQuery.setLength(0);
		        	   	strQuery.append("select cm_username from cmm0040 where cm_userid=? \n");
		        	   	pstmt2 = conn.prepareStatement(strQuery.toString());
		        	   	pstmt2.setString(1, rs.getString("cr_lstusr"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) rst.put("Lbl_LstUsr",rs2.getString("cm_username"));
						rs2.close();
						pstmt2.close();
					}
				}

	    		rtList.add(rst);
	    		rst = null;
		    }else{
				rst = new HashMap<String,String>();
				rst.put("ID","Sql_Qry_Prog2");
		    	rst.put("WkSecu","false");
		    	rtList.add(rst);
		    	rst = null;
		    }
	    	pstmt2 = null;

	    	rs.close();
	    	pstmt.close();
	    	conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getGrid1_Click() method statement

    public Object[] getSql_Qry_Hist(String UserId,String L_SysCd,String L_JobCd,
    		String Cbo_ReqCd, String L_ItemId)	throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2      = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

		    strQuery.setLength(0);
	    	strQuery.append("select a.cr_acptno,a.cr_aplydate,a.cr_status, \n");
	    	strQuery.append(" a.cr_rsrccd,a.cr_qrycd,b.cm_username,c.cm_codename, \n");
	    	strQuery.append(" d.cr_qrycd qrycd,d.cr_sayu,d.cr_passok,d.cr_passcd \n");
	    	strQuery.append(" , to_char(d.cr_acptdate,'yyyy-mm-dd hh24:mi:ss') cr_acptdate \n");
	    	strQuery.append(" , to_char(a.cr_prcdate,'yyyy-mm-dd hh24:mi:ss') cr_prcdate \n");
	    	strQuery.append(" , d.cr_itsmtitle , d.cr_itsmid \n");
	    	strQuery.append(" from cmr1010 a,cmr1000 d,cmm0040 b,cmm0020 c \n");
	    	strQuery.append(" where a.cr_itemid=? and \n"); //L_ItemId
            if (!Cbo_ReqCd.equals("ALL")){
            	strQuery.append(" d.cr_qrycd=? and \n"); //Cbo_ReqCd
            }
            strQuery.append(" a.cr_acptno=d.cr_acptno and \n");
            strQuery.append(" a.cr_editor=b.cm_userid and \n");
            strQuery.append(" c.cm_macode='REQUEST' and d.cr_qrycd=c.cm_micode \n");
            if (Cbo_ReqCd.equals("ALL") || Cbo_ReqCd.equals("04")){
	            strQuery.append(" union \n");
	            strQuery.append(" select b.cr_acptno,'' as cr_aplydate,'9' cr_status,a.cr_rsrccd, \n");
	            strQuery.append("        decode(b.cr_qrycd,'03','최초이행','추가이행') as cr_qrycd,c.cm_username,  \n");
	            strQuery.append("        decode(b.cr_qrycd,'03','최초이행','추가이행') as cm_codename,             \n");
	            strQuery.append("        '04' as qrycd,            \n");
	            strQuery.append("        '형상관리 일괄이행' as cr_passcd, '0' as cr_passok,'형상관리 일괄이행' as cr_passcd \n"); 
	            strQuery.append("        , to_char(b.cr_acptdate,'yyyy-mm-dd hh24:mi:ss') as cr_acptdate \n");
	            strQuery.append("        , to_char(b.cr_acptdate,'yyyy-mm-dd hh24:mi:ss') as cr_prcdate  \n");
	            strQuery.append(" , '' as cr_itsmtitle , '' as cr_itsmid\n");
	            strQuery.append(" from cmr0020 a,cmr0021 b,cmm0040 c \n");
	            strQuery.append(" where a.cr_itemid=? and \n"); //L_ItemId
	            strQuery.append(" b.cr_qrycd in ('03','05') and \n");
	            strQuery.append(" a.cr_itemid = b.cr_itemid and \n");
	            strQuery.append(" b.cr_editor=c.cm_userid \n");
            }
            strQuery.append(" order by cr_prcdate desc \n");

		    //pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt =  new LoggableStatement(conn, strQuery.toString());
		    int CNT = 0;
	    	pstmt.setString(++CNT, L_ItemId);
	    	if (!Cbo_ReqCd.equals("ALL")) pstmt.setString(++CNT, Cbo_ReqCd);
	    	if (Cbo_ReqCd.equals("ALL") || Cbo_ReqCd.equals("04")) pstmt.setString(++CNT, L_ItemId);
	    	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	rs = pstmt.executeQuery();
		    while (rs.next()){
				rst = new HashMap<String,String>();
				//rst.put("NO",Integer.toString(rs.getRow()));
				rst.put("SubItems1",rs.getString("cr_acptdate"));//rs.getString("cr_acptdate").substring(0,rs.getString("cr_acptdate").length()-2)
				rst.put("SubItems2",rs.getString("cm_username"));

				rst.put("SubItems3",rs.getString("cm_codename"));
				if (rs.getString("cr_acptno").substring(4,6).equals("04")){
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020 where cm_macode='CHECKIN' and cm_micode=? \n");
				    pstmt2 = conn.prepareStatement(strQuery.toString());
				    pstmt2.setString(1, rs.getString("cr_qrycd"));
			    	rs2 = pstmt2.executeQuery();
			    	if (rs2.next())
			    		rst.put("SubItems3",rs.getString("cm_codename") + "[" + rs2.getString("cm_codename") + "]");
			    	rs2.close();
			    	pstmt2.close();
                }
                rst.put("qrycd", rs.getString("qrycd"));
                rst.put("SubItems4",rs.getString("cr_acptno").substring(6));

                if (!rs.getString("qrycd").equals("04")){
	                rst.put("SubItems5","");
                }else{
                	strQuery.setLength(0);
                	strQuery.append("select cm_codename from cmm0020 \n");
                	strQuery.append("where cm_macode='REQPASS' and cm_micode=? \n");//cr_passok
                	pstmt2 = conn.prepareStatement(strQuery.toString());
                	pstmt2.setString(1, rs.getString("cr_passok"));
			    	rs2 = pstmt2.executeQuery();
			    	if (rs2.next())
			    		rst.put("SubItems5",rs2.getString("cm_codename"));
			    	rs2.close();
			    	pstmt2.close();

					if (rs.getString("cr_aplydate") != null){
	                    if (rs.getString("cr_aplydate").equals("9"))
	                    	rst.put("SubItems5","적용제외");
	                    else
	                    	rst.put("SubItems5","적용일시적용["+rs.getString("cr_aplydate").substring(0,4)
	                    			+"/"+rs.getString("cr_aplydate").substring(4,6)+"/"+rs.getString("cr_aplydate").substring(6,8)
	                    			+" "+rs.getString("cr_aplydate").substring(8,10)+":"+rs.getString("cr_aplydate").substring(10,12)+"]");
					}
                }

                if (rs.getString("cr_prcdate") != null){
	                if (rs.getString("cr_prcdate").length() > 0){
	                	if (rs.getString("cr_status").equals("3"))
		                	   rst.put("SubItems6", "[반송]" + rs.getString("cr_prcdate"));//rs.getString("cr_prcdate").substring(5,rs.getString("cr_prcdate").length()-2)
	                	else
	                	   rst.put("SubItems6", rs.getString("cr_prcdate"));//rs.getString("cr_prcdate").substring(5,rs.getString("cr_prcdate").length()-2)
	                	//rst.put("SubItems6", rs.getString("cr_prcdate").substring(5,7) + "/" +
	                		//rs.getString("cr_prcdate").substring(8,10) + "  " +
	                		//rs.getString("cr_prcdate").substring(10,12) + ":" +
	                		//rs.getString("cr_prcdate").substring(12,14) + ":" +
	                		//rs.getString("cr_prcdate").substring(14));
	                }
                } else {
                	rst.put("SubItems6","진행중");
                }
                if ( rs.getString("cr_sayu") !=null ){
                	rst.put("SubItems7", rs.getString("cr_sayu"));//신청사유
                } else {
                	rst.put("SubItems7", "");//신청사유
                }
                
                if ( rs.getString("cr_itsmid") != null ) {
                	rst.put("srinfo", "[" + rs.getString("cr_itsmid") + "]" + rs.getString("cr_itsmtitle") );
                } else {
                	rst.put("srinfo", "" );
                }
                rst.put("SubItems8", rs.getString("cr_acptno"));
                rst.put("SubItems9", rs.getString("cr_status"));
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
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getSql_Qry_Hist() method statement

    public Object[] getSql_Info(String L_SysCd,String WkRsrcCd,String WkVer,int Lv_Src_Cnt) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_info from cmm0036 where \n");
			strQuery.append(" cm_syscd=? and \n");  //L_SysCd
			strQuery.append(" cm_rsrccd=? \n");  //WkRsrcCd

			//pstmt =  new LoggableStatement(conn, strQuery.toString());
		    pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, L_SysCd);
			pstmt.setString(2, WkRsrcCd);

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("ID","getSql_Info");
				if (rs.getString("cm_info").substring(11,12).equals("1") &&	(Integer.parseInt(WkVer)>0 || Lv_Src_Cnt>0)){
					rst.put("Lbl_Tab2","true");
					if (Integer.parseInt(WkVer)>1) rst.put("Lbl_Tab3","true");
					else rst.put("Lbl_Tab3","false");
				}else{
					rst.put("Lbl_Tab2","false");
					rst.put("Lbl_Tab3","false");
				}
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
		    rs = null;
		    pstmt = null;
		    conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getSql_Info() method statement

    public Object[] getCbo_ReqCd_Add() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_micode,cm_codename from cmm0020 \n");
			strQuery.append(" where cm_macode='REQUEST' and cm_micode<'10' \n");
			strQuery.append(" and cm_micode<>'****' and cm_closedt is null \n");
			strQuery.append(" order by cm_micode \n");
		    pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();
			rst = new HashMap<String,String>();
			rst.put("cm_micode","ALL");
			rst.put("cm_codename","전체");
			rtList.add(rst);
			rst = null;
			while (rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rtList.add(rst);
				rst = null;
			}

			rs.close();
			pstmt.close();
			conn.close();
		    rs = null;
		    pstmt = null;
		    conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCbo_ReqCd_Add() method statement

    public int getTbl_Update(String L_ItemId,String L_JobCd,String L_RsrcCd,
    		String Txt_ProgName,String Cbo_Dir_Code,
    		String Cbo_Editor,String SvRsrc,String svDsnCd, String srid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;
		int               parmCnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			//String      strJob[] = L_JobCd.split(",");
			strQuery.setLength(0);
			strQuery.append("update cmr0020 set cr_jobcd=?, \n");//L_JobCd
			strQuery.append(" cr_rsrccd=?,cr_story=?,       \n");//Txt_ProgName
		    if (Cbo_Editor.length() > 0) strQuery.append(" cr_editor=?, \n");//Cbo_Editor
		    if (!"N".equals(srid)) strQuery.append(" cr_isrid=?, \n");//cboSr
		    strQuery.append(" cr_lastdate=SYSDATE               \n");
		    strQuery.append(" where cr_itemid=?                 \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
			parmCnt = 0;
			pstmt.setString(++parmCnt, L_JobCd);
            pstmt.setString(++parmCnt, L_RsrcCd);
            pstmt.setString(++parmCnt, Txt_ProgName);
            
            if (Cbo_Editor.length() > 0) pstmt.setString(++parmCnt, Cbo_Editor);
            if (!"N".equals(srid)) {
            	if("P".equals(srid)) pstmt.setString(++parmCnt, "");
            	else pstmt.setString(++parmCnt, srid);
            }
            pstmt.setString(++parmCnt, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

            if (!svDsnCd.equals(Cbo_Dir_Code)) {
		    	strQuery.setLength(0);
		    	strQuery.append("update cmr0020 set cr_dsncd=? \n");//Cbo_Dir_code
		    	strQuery.append(" where cr_itemid=?            \n");//L_ItemId
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, Cbo_Dir_Code);
	            pstmt.setString(2, L_ItemId);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rtn_cnt = pstmt.executeUpdate();
	            pstmt.close();

		    	strQuery.setLength(0);
		    	strQuery.append("update cmr1010 set cr_dsncd=? \n");//Cbo_Dir_code
		    	strQuery.append(" where cr_itemid=?            \n");//L_ItemId
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, Cbo_Dir_Code);
	            pstmt.setString(2, L_ItemId);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rtn_cnt = pstmt.executeUpdate();
	            pstmt.close();
            }

            conn.commit();
            conn.close();
		    pstmt = null;
		    conn = null;

            return rtn_cnt;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.rollback();
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0500.getTbl_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0Cmd0500200.getTbl_Update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.rollback();
				conn.close();conn = null;
			}
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0500.getTbl_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0500.getTbl_Update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0500.getTbl_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }//end of getTbl_Update() method statement

    public int getItem_Delete(String L_ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            SysCd       = "";
		String            DsnCd       = "";
		String            RsrcName    = "";
		String            RsrcCd      = "";
		String            strItemId   = null;
		String            strBefDsn   = null;
		String            strAftDsn   = null;
		String            strWork1    = null;
		String            strWork3    = null;
		String            strDevPath  = null;
		String            strRsrcCd   = null;
		String            strRsrcName = null;
		int               j           = 0;
		ArrayList<String> qryAry = null;
		int				  nRet1 = 0;
		int				  nRet2 = 0;
		int				  qryFlag = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cr_syscd,a.cr_rsrccd,a.cr_rsrcname,b.cm_dirpath \n");
			strQuery.append("  from cmm0070 b,cmr0020 a                               \n");
			strQuery.append(" where a.cr_itemid=? and a.cr_syscd=b.cm_syscd           \n");
			strQuery.append("   and a.cr_dsncd=b.cm_dsncd                             \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, L_ItemId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				SysCd = rs.getString("cr_syscd");
				DsnCd = rs.getString("cm_dirpath");
				RsrcName = rs.getString("cr_rsrcname");
				RsrcCd = rs.getString("cr_rsrccd");
			}
			rs.close();
			pstmt.close();

        	Cmd0100 cmd0100 = new Cmd0100();

            strQuery.setLength(0);
	        strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
			strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
			strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
			strQuery.append("   and b.cm_factcd='04'                                  \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
			strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, RsrcCd);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	        	strBefDsn = "";
	        	strAftDsn = "";

	        	if (rs.getString("cm_basedir") != null) strBefDsn = rs.getString("cm_basedir");
	        	if (RsrcName.indexOf(".") > -1) {
	        		strWork1 = RsrcName.substring(0,RsrcName.indexOf("."));
	        	}
	        	else strWork1 = RsrcName;
	        	//ecamsLogger.debug("+++++++++++++++cm_basedir,strWork1=========>"+strBefDsn+ ","+strWork1);
	        	if (!rs.getString("cm_basename").equals("*")) {
	        		strWork3 = rs.getString("cm_basename");
	        		while (strWork3 == "") {
	        			j = strWork3.indexOf("*");
	        			if (j > -1) {
	        				//strWork2 = strWork3.substring(0, j);
	        				strWork3 = strWork3.substring(j + 1);
	        				if (strWork3.equals("*")) strWork3 = "";
	        			} else {
	        				//strWork2 = strWork3;
	        				strWork3 = "";
	        			}
	        			if (strWork3 == "") break;
	        		}
	        	}
	        	strQuery.setLength(0);
	        	strQuery.append("select \n");
	        	if (rs.getString("cm_cmdyn").equals("Y")) {
	        		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
			   		qryAry = new ArrayList<String>();
			   		nRet1 = 0;
			   		nRet2 = 0;

			   		while( (nRet2 = strWork1.indexOf("'")) != -1){
			   			if (qryFlag == 0){
			   				strQuery.append(strWork1.substring(0, nRet2)+ " \n");
			   				strWork1 = strWork1.substring(nRet2+1);
			   				qryFlag = 1;
			   			}
			   			else{
			   				qryAry.add(strWork1.substring(0, nRet2));
			   				strWork1 = strWork1.substring(nRet2+1);
			   				strQuery.append(" ? \n");
			   				qryFlag = 0;
			   			}
			   		}
			   		strQuery.append(strWork1+ " \n");
	        	}
	        	else{
	        		strQuery.append(" ? \n");
	        	}
	        	strQuery.append("as relatId  from dual \n");

	        	//pstmt2 = conn.prepareStatement(strQuery.toString());
	        	pstmt2 = new LoggableStatement(conn, strQuery.toString());

			   	nRet1 = 1;
				if (rs.getString("cm_cmdyn").equals("Y")){
	        		for (nRet2 = 0;nRet2<qryAry.size();nRet2++){
	        			pstmt2.setString(nRet1++,qryAry.get(nRet2));
	        		}
	        	}
	        	else{
			   		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
			   		pstmt2.setString(nRet1++,strWork1);
	        	}

				ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	strWork1 = rs2.getString("relatId");
    	        }
    	        else{
    	        	strWork1 = "";
    	        }
    	        rs2.close();
    	        pstmt2.close();

	        	strRsrcName = strWork1;
	        	strRsrcCd = rs.getString("cm_samersrc");
	        	if (rs.getString("cm_samedir") != null) strAftDsn = rs.getString("cm_samedir");

	        	if (rs.getString("cm_samersrc").equals("52")) {
	        		strQuery.setLength(0);
	        		strQuery.append("select a.cm_volpath  from cmm0038 a,cmm0031 b  \n");
	        		strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='01'        \n");
	        		strQuery.append("   and a.cm_rsrccd=? and a.cm_syscd=b.cm_syscd \n");
	        		strQuery.append("   and a.cm_svrcd=b.cm_svrcd and a.cm_seqno=b.cm_seqno  \n");
	        		strQuery.append("   and b.cm_closedt is null                    \n");
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		pstmt2.setString(1, SysCd);
	        		pstmt2.setString(2, RsrcCd);
	    	        rs2 = pstmt2.executeQuery();
	    	        if (rs2.next()) {
	    	        	strDevPath = rs2.getString("cm_volpath");
	    	        }

	    	        rs2.close();
	    	        pstmt2.close();
	        	} else strDevPath = DsnCd.replace(strBefDsn, strAftDsn);
	        	//ecamsLogger.debug("+++++++++++++++strRsrcCd,strDevPath=========>"+strRsrcCd+ ","+strDevPath);

	        	strQuery.setLength(0);
        		strQuery.append("select cm_dsncd from cmm0070            \n");
        		strQuery.append(" where cm_syscd=? and cm_dirpath=?      \n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(1, SysCd);
        		pstmt2.setString(2, strDevPath);
    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	strAftDsn = rs2.getString("cm_dsncd");
    	        }
    	        rs2.close();
    	        pstmt2.close();

    	        strItemId = "";
    	        strQuery.setLength(0);
    	        strQuery.append("select cr_itemid from cmr0020        \n");
    	        strQuery.append(" where cr_syscd=?                    \n");
    	        strQuery.append("   and upper(cr_rsrcname)=upper(?)   \n");
    	        if (strAftDsn != "" && strAftDsn != null) {
    	        	strQuery.append("and cr_dsncd=?                   \n");
    	        } else {
    	        	strQuery.append("and cr_rsrccd=?                  \n");
    	        }
    	        strQuery.append("   and cr_status='3'                 \n");
    	        pstmt2 = conn.prepareStatement(strQuery.toString());
    	        //pstmt2 =  new LoggableStatement(conn, strQuery.toString());
    	        pstmt2.setString(1, SysCd);
        		pstmt2.setString(2, strRsrcName);
        		if (strAftDsn != "" && strAftDsn != null)
        			pstmt2.setString(3, strAftDsn);
        		else pstmt2.setString(3, strRsrcCd);
        		//ecamsLogger.debug(((LoggableStatement)pstmt2).getQueryString());
    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	strItemId = rs2.getString("cr_itemid");
    	        }
    	        rs2.close();
    	        pstmt2.close();

    	        if (strItemId !="" && strItemId != null) {
    	        	cmd0100.cmr0020_Delete_sub("",strItemId,conn);
    	        }
	        }
	        rs.close();
	        pstmt.close();

			cmd0100.cmr0020_Delete_sub("", L_ItemId, conn);

            conn.commit();
            conn.close();
		    pstmt = null;
		    conn = null;

            return 1;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getItem_delete() method statement

    public int getTbl_Delete(String L_ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			/*
	        strQuery.setLength(0);
	    	strQuery.append("delete cmr0025 \n");
	    	strQuery.append(" where cr_itemid=? \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

	        strQuery.setLength(0);
	    	strQuery.append("delete cmr0021 \n");
	    	strQuery.append(" where cr_itemid=? \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

	        strQuery.setLength(0);
	    	strQuery.append("delete cmr1010 \n");
	    	strQuery.append(" where cr_itemid=? \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

	        strQuery.setLength(0);
	    	strQuery.append("delete cmr0022 \n");
	    	strQuery.append(" where cr_itemid=? \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

	        strQuery.setLength(0);
	    	strQuery.append("delete cmr0022 \n");
	    	strQuery.append(" where cr_baseitem=? \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();

	        strQuery.setLength(0);
	    	strQuery.append("delete cmr0020 \n");
	    	strQuery.append(" where cr_itemid=? \n");//L_ItemId
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            */
			
			strQuery.setLength(0);
	    	strQuery.append("update cmr0020 set cr_status='9', cr_clsdate=SYSDATE	\n");
	    	strQuery.append(" where cr_itemid=?										\n");
	    	pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, L_ItemId);
            rtn_cnt = pstmt.executeUpdate();
            
            pstmt.close();
            conn.commit();
            conn.close();
		    pstmt = null;
		    conn = null;

            return rtn_cnt;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getTbl_Delete() method statement

    public Object[] getCbo_Editor_Add(String ItemId,String Editor) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_username,a.cm_userid          \n");
			strQuery.append("  from cmm0040 a,cmm0044 b,cmr0020 c      \n");
			strQuery.append(" where c.cr_itemid=?                      \n");//Txt_Editor
			strQuery.append("   and c.cr_syscd=b.cm_syscd              \n");
			strQuery.append("   and c.cr_jobcd=b.cm_jobcd              \n");
			strQuery.append("   and b.cm_closedt is null               \n");
			strQuery.append("   and b.cm_userid=a.cm_userid            \n");
			strQuery.append("   and a.cm_active='1'                    \n");
			strQuery.append("union                                     \n");
			strQuery.append("select cm_username,cm_userid              \n");
			strQuery.append("  from cmm0040                            \n");
			strQuery.append(" where cm_userid=?                        \n");//Txt_Editor
			strQuery.append("order by cm_username                      \n");
		    pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, ItemId);
		    pstmt.setString(2, Editor);
			rs = pstmt.executeQuery();
			//boolean listFg = true;

			rst = new HashMap<String,String>();
			rst.put("userid","선택하세요");
			rtList.add(rst);

			while (rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("userid","[" + rs.getString("cm_userid") + "] " + rs.getString("cm_username"));
				rst.put("cm_userid",rs.getString("cm_userid"));
				rst.put("cm_username",rs.getString("cm_username"));
				rtList.add(rst);
				rst = null;
				//listFg = false;
			}
			rs.close();
			pstmt.close();
			conn.close();
		    rs = null;
		    pstmt = null;
		    conn = null;

			rtObj = rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			if (conn != null){
				conn.close();conn = null;
			}
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			if (conn != null){
				conn.close();conn = null;
			}
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
    }//end of getCbo_ReqCd_Add() method statement

}//end of Cmd0500 class statement
