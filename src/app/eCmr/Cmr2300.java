/*****************************************************************************************
	1. program ID	: Cmr2300.java
	2. create date	: 2010. 03. 23
	3. auth		    : is.choi
	4. update date	:
	5. auth		    : No Name
	6. description	: 1. User Deploy List
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

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
public class Cmr2300{


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
	public Object[] getReqList(String UserId,String stDate,String edDate,String sta) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int parmCnt = 0;
		try {
			conn = connectionContext.getConnection();
			UserInfo userinfo = new UserInfo();
			boolean adminSw = userinfo.isAdmin_conn(UserId, conn);

			strQuery.append("select substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,   \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,        \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,          \n");
			strQuery.append("       a.cr_acptno,a.cr_syscd,a.cr_editor,a.cr_sayu,a.cr_prcdate,   \n");
			strQuery.append("       b.cm_sysmsg,c.cm_username,d.cr_confusr,a.cr_status           \n");
			strQuery.append("  from cmr1000 a,cmm0030 b,cmm0040 c,cmr9900 d,cmm0040 e            \n");
			strQuery.append(" where a.cr_qrycd='04' and a.cr_passok='6'                          \n");
			if (adminSw == false) {
				strQuery.append("and (a.cr_editor=? or a.cr_teamcd=e.cm_project)                 \n");
			}
			if (sta.equals("0")) {
				strQuery.append("and a.cr_status='0'                                             \n");
			} else {
				strQuery.append("and (a.cr_status='0' or              \n");
				strQuery.append("     (a.cr_status<>'0' and d.cr_confdate is not null            \n");
				strQuery.append("      and to_char(a.cr_acptdate,'yyyymmdd')>=?                  \n");
				strQuery.append("      and to_char(a.cr_acptdate,'yyyymmdd')<=?))                \n");
			}
			strQuery.append("   and a.cr_acptno=d.cr_acptno and d.cr_locat='00'                  \n");
			strQuery.append("   and d.cr_team='SYSED'                                            \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_editor=c.cm_userid            \n");
			strQuery.append("   and e.cm_userid=?                                                \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			if (adminSw == false) pstmt.setString(++parmCnt,UserId);
			if (sta.equals("9")) {
				pstmt.setString(++parmCnt, stDate);
				pstmt.setString(++parmCnt, edDate);
			}
			pstmt.setString(++parmCnt,UserId);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("acptno"));
				rst.put("cr_acptdate",rs.getString("acptdate"));
				rst.put("prcdate",rs.getString("prcdate"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_confusr",rs.getString("cr_confusr"));
				if (rs.getString("cr_sayu") != null){
				   rst.put("cr_sayu",rs.getString("cr_sayu"));
				}
				rst.put("cr_status", rs.getString("cr_status"));
				if (rs.getString("cr_status").equals("3")) {
					rst.put("sta", "반려");
					rst.put("colorsw", "3");
				}
				else if (rs.getString("cr_status").equals("9")) {
					rst.put("sta", "완료");
					rst.put("colorsw", "9");
				}
				else {
					if (rs.getString("cr_prcdate") != null) rst.put("colorsw", "9");
					else rst.put("colorsw", "0");
					String tmpTeam = "";
					String tmpTeamCd = "";
					strQuery.setLength(0);
					strQuery.append("select cr_confname,cr_teamcd,cr_team from cmr9900 \n");
					strQuery.append(" where cr_acptno=? and cr_locat='00'              \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_acptno"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("sta", rs2.getString("cr_confname"));
						tmpTeam = rs2.getString("cr_team");
						tmpTeamCd = rs2.getString("cr_teamcd");
					}
					rs2.close();
					pstmt2.close();

					if (tmpTeamCd.equals("1")) {
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmr1010 a,cmr1011 b \n");
						strQuery.append(" where a.cr_acptno=? and a.cr_prcdate is null\n");
						strQuery.append("   and a.cr_acptno=b.cr_acptno               \n");
						strQuery.append("   and a.cr_serno=b.cr_serno                 \n");
						strQuery.append("   and b.cr_prcsys=?                         \n");
						strQuery.append("   and nvl(b.cr_prcrst,'0000')<>'0000'       \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, rs.getString("cr_acptno"));
						pstmt2.setString(2, tmpTeam);
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							if (rs2.getInt("cnt")>0) {
								rst.put("colorsw", "5");
							}
						}
						rs2.close();
						pstmt2.close();
					}
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

			returnObjectArray = rsval.toArray();
			//ecamsLogger.debug(rsval.toString());
			rsval.clear();
			rsval = null;

			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2300.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2300.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2300.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2300.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2300.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public Object[] getFileList(String UserId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			Cmr2200 cmr2200 = new Cmr2200();
			strQuery.append("select a.cm_svrname,b.cm_dirpath,c.cr_rsrcname,c.cr_syscd,          \n");
			strQuery.append("       c.cr_rsrccd,c.cr_serno,d.cm_codename,g.cm_volpath,           \n");
			strQuery.append("       a.cm_svrip,a.cm_portno,a.cm_seqno,a.cm_svrcd,c.cr_status,    \n");
			strQuery.append("       to_char(f.cr_reqdate,'yyyy/mm/dd hh24:mi') reqdate,          \n");
			strQuery.append("       to_char(f.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,          \n");
			strQuery.append("       e.cm_codename prcrst,f.cr_prcdate,nvl(f.cr_errcode,'0000') cr_errcode\n");
			strQuery.append("  from cmm0031 a,cmm0070 b,cmr1010 c,cmm0020 d,cmr1012 f,cmm0020 e,cmm0038 g \n");
			strQuery.append(" where c.cr_acptno=? and c.cr_acptno=f.cr_acptno                    \n");
			strQuery.append("   and c.cr_serno=f.cr_serno and c.cr_syscd=a.cm_syscd              \n");
			strQuery.append("   and a.cm_svrcd=f.cr_svrcd and a.cm_seqno=f.cr_svrseq             \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd and c.cr_dsncd=b.cm_dsncd              \n");
			strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=c.cr_rsrccd              \n");
			strQuery.append("   and e.cm_macode='ERRACCT' and e.cm_micode=nvl(f.cr_errcode,'ING1') \n");
			strQuery.append("   and a.cm_syscd=g.cm_syscd and a.cm_svrcd=g.cm_svrcd              \n");
			strQuery.append("   and a.cm_seqno=g.cm_seqno and c.cr_rsrccd=g.cm_rsrccd            \n");
			strQuery.append(" order by f.cr_reqdate,a.cm_svrname,c.cr_rsrcname                   \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("reqdate",rs.getString("reqdate"));
				rst.put("prcdate",rs.getString("prcdate"));
				rst.put("cm_svrname",rs.getString("cm_svrname"));
				rst.put("cm_dirpath",cmr2200.getChgVolPath(rs.getString("cr_syscd"),rs.getString("cr_rsrccd"),rs.getString("cm_volpath"),rs.getString("cm_dirpath"),conn));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cm_svrip",rs.getString("cm_svrip"));
				rst.put("cm_svrcd",rs.getString("cm_svrcd"));
				rst.put("prcrst",rs.getString("prcrst"));
				rst.put("cm_portno",Integer.toString(rs.getInt("cm_portno")));
				rst.put("cr_serno",Integer.toString(rs.getInt("cr_serno")));
				rst.put("cm_seqno",Integer.toString(rs.getInt("cm_seqno")));

				if (rs.getString("cr_status").equals("3")) rst.put("colorsw", "3");
				else if (rs.getString("cr_prcdate") != null) {
					if (rs.getString("cr_errcode").equals("0000")) rst.put("colorsw", "9");
					else rst.put("colorsw", "5");
				} else {
					if (rs.getString("cr_errcode").equals("0000")) rst.put("colorsw", "0");
					else rst.put("colorsw", "5");
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

			returnObjectArray = rsval.toArray();
			//ecamsLogger.debug(rsval.toString());
			rsval.clear();
			rsval = null;


			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2300.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2300.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2300.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2300.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2300.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement



}//end of Cmr2300 class statement
