/*****************************************************************************************
	1. program ID	: Cmp1100.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.io.BufferedReader;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
//import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp1300{


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

    /*
	public Object[] getReqList(String UserId,String SysCd,String JobCd,String txtPath,String txtUser,String StDate,String EdDate,String Ilsu,String baseCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		String			dayTerm			= null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		UserInfo         secuinfo     = new UserInfo();
		Object[] returnObjectArray = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			secuinfo = null;
			strQuery.setLength(0);
			strQuery.append("  select a.*,b.cm_username,c.cm_codename JAWON,d.cm_sysmsg,e.cm_jobname,f.cm_dirpath, 								\n");
			strQuery.append("      g.cr_passcd,g.cr_acptdate,h.cm_deptname,  																	\n");
			strQuery.append("      substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno, 			\n");
			strQuery.append("      TRUNC(SYSDATE - cr_acptdate) dayTerm 																		\n");
			strQuery.append("      from cmm0100 h,cmm0070 f,cmm0102 e,cmm0030 d,cmm0020 c,cmm0040 b,cmr1000 g,cmr1010 a,cmr0020 r  				\n");
			strQuery.append("      where r.cr_status in ('4','5','A','B','C','E') and  															\n");
			strQuery.append("      r.cr_syscd=a.cr_syscd and r.cr_dsncd=a.cr_dsncd and r.cr_rsrcname=a.cr_rsrcname and  						\n");
			strQuery.append("      a.cr_acptno=g.cr_acptno and  																				\n");
			strQuery.append("      a.cr_confno is null and  																					\n");
			    if (SysCd != "" && SysCd != null) strQuery.append(" g.cr_syscd=?  and                         	 								\n");
				if (JobCd != "" && JobCd != null) strQuery.append(" a.cr_jobcd=?  and                          								\n");
			strQuery.append("      g.cr_qrycd in ('01','02','03','05','08') and  																\n");
			strQuery.append("      r.cr_syscd=f.cm_syscd and   																					\n");
			strQuery.append("      r.cr_dsncd=f.cm_dsncd and  																					\n");
			    if (txtPath != "" && txtPath != null) strQuery.append(" f.cm_dirpath like ? and                           	 					\n");
			    if (txtUser != "" && txtUser != null) strQuery.append(" b.cm_username =? and                           	 						\n");
			    if (Ilsu != "" && Ilsu != null) strQuery.append("TRUNC(SYSDATE - cr_acptdate) >=?  			and									\n");
		 		if (baseCd.equals("1")) {
					   strQuery.append("    to_char(g.cr_acptdate,'yyyymmdd')>=? and							  								\n");
					   strQuery.append("    to_char(g.cr_acptdate,'yyyymmdd')<=? and                               								\n");
				}
			strQuery.append("      g.cr_editor=b.cm_userid and  																				\n");
			strQuery.append("      c.cm_macode='JAWON' and a.cr_rsrccd=c.cm_micode and  														\n");
			strQuery.append("      a.cr_jobcd=e.cm_jobcd and  																					\n");
			strQuery.append("      g.cr_syscd=d.cm_syscd and  																					\n");
			strQuery.append("      g.cr_teamcd=h.cm_deptcd  																					\n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        if (SysCd != "" && SysCd != null) pstmt.setString(++parmCnt, SysCd);
			if (JobCd != "" && JobCd != null) pstmt.setString(++parmCnt, JobCd);
			if (txtPath != "" && txtPath != null) pstmt.setString(++parmCnt, txtPath);
			if (txtUser != "" && txtUser != null) pstmt.setString(++parmCnt, txtUser);
			if (Ilsu != null && Ilsu != "") pstmt.setInt(++parmCnt, Integer.parseInt(Ilsu));
			if (baseCd.equals("1")) {
				pstmt.setString(++parmCnt, StDate);
				pstmt.setString(++parmCnt, EdDate);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
			while (rs.next()){
				dayTerm = null;
				dayTerm = rs.getString("dayTerm");
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				rst.put("acptdate",rs.getString("cr_acptdate"));
				rst.put("dayTerm",dayTerm);
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("acptno"));
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
			//ecamsLogger.debug(rsval.toString());
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp1300.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1300.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1300.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1300.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1300.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	*/



	public Object[] getReqList(String UserId,String SysCd,String JobCd,String txtPath,String txtUser,String StDate,String EdDate,String Ilsu,String baseCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		String			dayTerm			= null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
//		UserInfo         secuinfo     = new UserInfo();
		Object[] returnObjectArray = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
//			String strSecu = secuinfo.getSecuInfo(UserId);
//			secuinfo = null;
			strQuery.setLength(0);
			strQuery.append("  select /*+ ALL_ROWS */ a.*,b.cm_username,c.cm_codename JAWON,d.cm_sysmsg,e.cm_jobname, \n");
			strQuery.append("      g.cr_passcd,g.cr_acptdate,r.cr_story,g.cr_itsmid, \n");
			strQuery.append("      substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,\n");
			strQuery.append("      TRUNC(SYSDATE - cr_acptdate) dayTerm, a.cr_itemid, f.cm_dirpath, b.cm_project,h.cm_deptname \n");
			strQuery.append("      from cmm0102 e,cmm0030 d,cmm0020 c,cmm0040 b,cmr1000 g,cmr1010 a,cmr0020 r,cmm0070 f,cmm0100 h \n");
			strQuery.append("      where r.cr_status in ('4','5','A','B','C','E') and  															\n");
			strQuery.append("      r.cr_syscd=a.cr_syscd and r.cr_dsncd=a.cr_dsncd and r.cr_rsrcname=a.cr_rsrcname and  						\n");
			strQuery.append("      a.cr_acptno=g.cr_acptno and  																				\n");
			strQuery.append("      a.cr_confno is null and a.cr_itemid=a.cr_baseitem and													\n");
			if (SysCd != "" && SysCd != null) strQuery.append(" g.cr_syscd=?  and                         	 								    \n");
			if (JobCd != "" && JobCd != null) strQuery.append(" a.cr_jobcd=?  and                          								        \n");
			strQuery.append("      g.cr_qrycd in ('01','02','03') and  		        														    \n");
			if (txtPath != "" && txtPath != null) strQuery.append(" f.cm_dirpath like ? and                           	 				     	\n");
			strQuery.append("      f.cm_dsncd = r.cr_dsncd and  		        														    \n");
			strQuery.append("      f.cm_syscd = r.cr_syscd and  		        														    \n");
			if (txtUser != "" && txtUser != null) strQuery.append(" b.cm_username =? and                           	 					    	\n");
			if (Ilsu != "" && Ilsu != null) strQuery.append("TRUNC(SYSDATE - cr_acptdate) >=?  			and								    	\n");
		 	if (baseCd.equals("1")) {
			   strQuery.append("    to_char(g.cr_acptdate,'yyyymmdd')>=? and							  								        \n");
			    strQuery.append("    to_char(g.cr_acptdate,'yyyymmdd')<=? and                               							    	\n");
			}
			strQuery.append("      g.cr_editor=b.cm_userid and \n");
			strQuery.append("      c.cm_macode='JAWON' and a.cr_rsrccd=c.cm_micode and \n");
			strQuery.append("      a.cr_jobcd=e.cm_jobcd and \n");
			strQuery.append("      g.cr_syscd=d.cm_syscd and \n");
			strQuery.append("      b.cm_project=h.cm_deptcd \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        if (SysCd != "" && SysCd != null) pstmt.setString(++parmCnt, SysCd);
			if (JobCd != "" && JobCd != null) pstmt.setString(++parmCnt, JobCd);
			if (txtPath != "" && txtPath != null) pstmt.setString(++parmCnt, "%"+txtPath+"%");
			if (txtUser != "" && txtUser != null) pstmt.setString(++parmCnt, txtUser);
			if (Ilsu != null && Ilsu != "") pstmt.setInt(++parmCnt, Integer.parseInt(Ilsu));
			if (baseCd.equals("1")) {
				pstmt.setString(++parmCnt, StDate);
				pstmt.setString(++parmCnt, EdDate);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
			while (rs.next()){
			//	ecamsLogger.error("1");
				dayTerm = null;
				dayTerm = rs.getString("dayTerm");
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_story",rs.getString("cr_story"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("acptdate",rs.getString("cr_acptdate"));
				rst.put("dayTerm",dayTerm);
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("acptno"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				if(rs.getString("cr_itsmid")!=null) rst.put("cr_itsmid", rs.getString("cr_itsmid"));
				else rst.put("cr_itsmid", "");

				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			conn = null;
			pstmt = null;
			rs = null;

			returnObjectArray = rsval.toArray();
			rsval = null;

			//
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp1300.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1300.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1300.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1300.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1300.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getReqList() method statement
}//end of Cmp1300 class statement
