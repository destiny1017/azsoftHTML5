/*****************************************************************************************
	1. program ID	: Cmp3100.java
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
import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp3100{


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

	public Object[] getList(String StDate,String EdDate,String syscd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select distinct b.cr_itsmid, e.cm_sysmsg, b.cr_sayu, d.cm_username, b.cr_acptno, \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd') cr_prcdate,b.cr_corpnm, \n");
			strQuery.append("       b.cr_deptnm, b.cr_askempnm, substr(replace(b.cr_askregdate,'-','/'),0,10) cr_askregdate, b.cr_jobgbn \n");
			strQuery.append("  from cmr0020 a, cmr1000 b, cmr1010 c, cmm0040 d, cmm0030 e \n");
			strQuery.append(" where a.cr_itemid = c.cr_itemid \n");
			strQuery.append("   and to_char(c.cr_prcdate,'yyyymmdd') between ? and ? \n");
			if(!syscd.equals("00000")){
				strQuery.append("   and a.cr_syscd = ? \n");
			}
			strQuery.append("   and b.cr_qrycd ='04' \n");
			strQuery.append("   and b.cr_acptno = c.cr_acptno \n");
			strQuery.append("   and b.cr_editor = d.cm_userid \n");
			strQuery.append("   and d.cm_project = 'IT110' \n");
			strQuery.append("   and b.cr_prcdate is not null \n");
			strQuery.append("   and b.cr_status <> '3' \n");
			strQuery.append("   and a.cr_syscd = e.cm_syscd \n");
			strQuery.append("   and e.cm_closedt is null \n");
			strQuery.append(" order by e.cm_sysmsg, d.cm_username \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, StDate);
			pstmt.setString(2, EdDate);
			if(!syscd.equals("00000")){
				pstmt.setString(3, syscd);
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();

				if(rs.getString("cr_itsmid")!=null) rst.put("cr_itsmid",rs.getString("cr_itsmid"));
				else rst.put("cr_itsmid","");
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				if(rs.getString("cr_jobgbn")!=null) rst.put("cr_jobgbn",rs.getString("cr_jobgbn"));
				else rst.put("cr_jobgbn","");
				rst.put("cr_sayu",rs.getString("cr_sayu"));
				rst.put("cr_prcdate",rs.getString("cr_prcdate"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				if(rs.getString("cr_corpnm")!=null) rst.put("cr_corpnm",rs.getString("cr_corpnm"));
				else rst.put("cr_corpnm","");
				if(rs.getString("cr_deptnm")!=null) rst.put("cr_deptnm",rs.getString("cr_deptnm"));
				else rst.put("cr_deptnm","");
				if(rs.getString("cr_askempnm")!=null) rst.put("cr_askempnm",rs.getString("cr_askempnm"));
				else rst.put("cr_askempnm","");
				if(rs.getString("cr_askregdate")!=null) rst.put("cr_askregdate",rs.getString("cr_askregdate"));
				else rst.put("cr_askregdate","");

				strQuery.setLength(0);
				strQuery.append("select a.cr_team, b.cm_username 								\n");
				strQuery.append(" from cmr9900 a, cmm0040 b                                     \n");
				strQuery.append("  where a.cr_acptno = ?                                        \n");
				strQuery.append("    and a.cr_locat <> '00'                                     \n");
				strQuery.append("    and a.cr_teamcd in ('3','4','6')                           \n");
				strQuery.append("    and nvl(a.cr_confusr,a.cr_baseusr) = b.cm_userid           \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("cr_acptno"));
		        rs2 = pstmt2.executeQuery();

				while (rs2.next()){
					if(rs2.getString("cr_team").equals("53")){
						rst.put("qa",rs2.getString("cm_username"));
					}else{
						rst.put("conf",rs2.getString("cm_username"));
					}
				}
				if(rst.get("conf") == null){
					rst.put("conf",rs.getString("cm_username"));
				}
				rs2.close();
				pstmt2.close();

				//ecamsLogger.error(rst.toString());
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
			ecamsLogger.error("## Cmp3100.getList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3100.getList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3100.getList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3100.getList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp3100.getList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getList() method statement



	public Object[] getDetailList(String StDate,String EdDate,String syscd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select distinct d.cr_itsmid, a.cr_rsrcname, c.cm_codename, b.cm_dirpath, \n");
			strQuery.append("        to_char(a.cr_lastdate,'yyyy/mm/dd') cr_lastdate,d.cr_acptno, \n");
			strQuery.append("        a.cr_lstver, f.cm_username, a.cr_itemid, g.cm_sysmsg, h.cm_codename gbn \n");
			strQuery.append("   from cmr0020 a, cmm0070 b, cmm0020 c, cmr1000 d, cmr1010 e, cmm0040 f, \n");
			strQuery.append("        cmm0030 g, cmm0020 h \n");
			strQuery.append("  where f.cm_project = 'IT110' \n");
			strQuery.append("    and to_char(e.cr_prcdate,'yyyymmdd') between ? and ? \n");
			if(!syscd.equals("00000")){
				strQuery.append("   and d.cr_syscd = ?\n");
			}
			strQuery.append("    and d.cr_qrycd ='04' \n");
			strQuery.append("    and a.cr_itemid = e.cr_itemid\n");
			strQuery.append("    and a.cr_syscd = b.cm_syscd\n");
			strQuery.append("    and a.cr_dsncd = b.cm_dsncd \n");
			strQuery.append("    and a.cr_rsrccd = c.cm_micode\n");
			strQuery.append("    and c.cm_macode = 'JAWON'\n");
			strQuery.append("    and d.cr_acptno = e.cr_acptno \n");
			strQuery.append("    and d.cr_editor = f.cm_userid\n");
			strQuery.append("    and d.cr_prcdate is not null\n");
			strQuery.append("    and d.cr_status <> '3' \n");
			strQuery.append("    and d.cr_syscd = g.cm_syscd \n");
			strQuery.append("    and e.cr_qrycd = h.cm_micode\n");
			strQuery.append("    and h.cm_macode = 'CHECKIN'\n");
			strQuery.append("  order by cr_lastdate desc \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, StDate);
			pstmt.setString(2, EdDate);
			if(!syscd.equals("00000")){
				pstmt.setString(3, syscd);
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				if(rs.getString("cr_itsmid")!=null) rst.put("cr_itsmid",rs.getString("cr_itsmid"));
				else rst.put("cr_itsmid","");
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_lastdate",rs.getString("cr_lastdate"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("gbn",rs.getString("gbn"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
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
			ecamsLogger.error("## Cmp3100.getDetailList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3100.getDetailList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3100.getDetailList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3100.getDetailList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp3100.getDetailList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDetailList() method statement

}//end of Cmp3100 class statement
