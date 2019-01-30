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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

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
public class Cmp5100{


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
    public Object[] getReqList(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;

		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cd_downdir,a.cd_diffecams,a.cd_svrcd,a.cd_svrname,    \n");
			strQuery.append("       a.cd_svrusr,a.cd_baseip,a.cd_qrycd,a.cd_svrseq,         \n");
			strQuery.append("       c.cd_svrip,c.cd_svrusr svrusr2,c.cd_svrcd as svrcd2,    \n");
			strQuery.append("       c.cd_svrname svrname2,c.cd_svrseq svrseq2,b.cm_codename \n");
			strQuery.append("  from cmd0021 a,cmd0022 c,cmm0020 b                           \n");
			strQuery.append(" where a.cd_acptno=?                                           \n");
			strQuery.append("   and a.cd_acptno=c.cd_acptno                                 \n");
			strQuery.append("   and b.cm_macode='SERVERCD' and b.cm_micode=c.cd_svrcd       \n");
			strQuery.append(" order by h.cr_syscd,h.cr_qrycd,h.cr_prcdate desc                              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("qrycd",rs.getString("cd_qrycd"));
				rst.put("diffecams",rs.getString("cd_diffecams"));
				rst.put("downdir",rs.getString("cd_downdir"));
				if (rs.getRow()==1) {
					if (rs.getString("cd_diffecams").equals("N")) {
						rst.put("cd_svrcd", rs.getString("cd_svrcd"));
						rst.put("cd_svrip", rs.getString("cd_baseip"));
						rst.put("cd_svrusr", rs.getString("cd_svrusr"));
						rst.put("cd_svrname", rs.getString("cd_svrname"));
						rst.put("cd_svrseq", Integer.toString(rs.getInt("cd_svrseq")));
						rst.put("checked", "true");
						rst.put("chkbase", "true");
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020             \n");
						strQuery.append(" where cm_macode='SERVERCD' and cm_micode=? \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, rs.getString("cd_svrcd"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) rst.put("cm_codename", rs2.getString("cm_codename"));
						rs2.close();
						pstmt2.close();
					}
					rsval.add(rst);
					rst = null;

					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("qrycd",rs.getString("cd_qrycd"));
					rst.put("diffecams",rs.getString("cd_diffecams"));
					rst.put("downdir",rs.getString("cd_downdir"));
				}
				rst.put("cd_svrcd", rs.getString("svrcd2"));
				rst.put("cd_svrip", rs.getString("cd_svrip"));
				rst.put("cd_svrusr", rs.getString("svrusr2"));
				rst.put("cd_svrname", rs.getString("svrname2"));
				rst.put("cd_svrseq", Integer.toString(rs.getInt("svrseq2")));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("checked", "true");
				rst.put("chkbase", "false");
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
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp5100.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp5100.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmp5100.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getReqList() method statement

    public Object[] getBefReq(String UserId,String StDate,String EdDate) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		String            AcptNo      = null;
		String            strSvrName  = null;
		boolean           findSw      = false;

		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			UserInfo     userinfo = new UserInfo();
			boolean adminYn = userinfo.isAdmin(UserId);
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cd_downdir,a.cd_diffecams,a.cd_svrcd,a.cd_svrname,    \n");
			strQuery.append("       a.cd_svrusr,a.cd_baseip,a.cd_qrycd,a.cd_acptno,         \n");
			strQuery.append("       to_date(a.cd_basicdt,'yyyymmddhh24miss') basicdt,       \n");
			strQuery.append("       c.cm_sysmsg,b.cm_username,                              \n");
			strQuery.append("       d.cd_svrip,d.cd_svrcd svrcd2,d.cd_svrusr svrusr2,       \n");
			strQuery.append("       d.cd_svrname svrname2,e.cm_codename                     \n");
			strQuery.append("  from cmm0030 c,cmm0040 b,cmd0021 a,cmd0022 d,cmm0020 e       \n");
			strQuery.append(" where substr(a.cd_basicdt,1,8)>=?                             \n");
			strQuery.append("   and substr(a.cd_basicdt,1,8)<=?                             \n");
			if (adminYn == false) {
				strQuery.append("and a.cd_editor=?                                          \n");
			}
			strQuery.append("   and a.cd_syscd=c.cm_sysmsg and a.cd_editor=b.cm_userid      \n");
			strQuery.append("   and a.cd_acptno=d.cd_acptno                                 \n");
			strQuery.append("   and e.cm_macode='SERVERCD' and e.cm_micode=d.cd_svrcd       \n");
			strQuery.append(" order by a.cd_acptno                                          \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, EdDate);
			if (adminYn == false) pstmt.setString(++parmCnt, UserId);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				findSw = false;
				if (AcptNo != null && AcptNo != "") {
					if (!AcptNo.equals(rs.getString("cd_acptno"))) {
						rst.put("svrname", strSvrName);
						rsval.add(rst);
						rst = null;
						findSw = true;
					}
				}else findSw = true;

				if (findSw == true) {
					strSvrName = null;
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("cd_basicdt",rs.getString("basicdt"));
					rst.put("cd_dirpath",rs.getString("cd_dirpath"));
					rst.put("cd_downdir",rs.getString("cd_downdir"));
					rst.put("cd_qrycd",rs.getString("cd_qrycd"));
					if (rs.getString("cd_diffecams").equals("Y")) rst.put("basesvr", "형상관리");
					else rst.put("basesvr", rs.getString("cd_svrname")+"("+rs.getString("cd_baseip")+":"+rs.getString("cd_svrusr")+")");
					if (rs.getString("cd_qrycd").equals("D")) rst.put("qrycd","디렉토리");
					else rst.put("qrycd", "파일");
					strSvrName = rs.getString("cd_svrname") + "(" + rs.getString("cm_codename") + ")";
				} else strSvrName = strSvrName + "," + rs.getString("cd_svrname") + "(" + rs.getString("cm_codename") + ")";
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rst = new HashMap<String, String>();
			if (strSvrName != "" && strSvrName != null) {
				rst.put("svrname", strSvrName);
				rsval.add(rst);
				rst = null;
			}
			returnObjectArray = rsval.toArray();

			rsval = null;
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getBefReq() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp5100.getBefReq() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getBefReq() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp5100.getBefReq() Exception END ##");
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
					ecamsLogger.error("## Cmp5100.getBefReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getBefReq() method statement
    public Object[] getSvrList(String SysCd,String DirPath) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;

		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_svrcd,a.cm_svrip,a.cm_svrusr,a.cm_svrname,         \n");
			strQuery.append("       a.cm_seqno,d.cm_codename                                \n");
			if (DirPath != null && DirPath != "") {
				   strQuery.append("  from cmm0031 a,cmm0038 b,cmm0038 c,cmm0020 d          \n");
			} else {
			   strQuery.append("  from cmm0031 a,cmm0020 d                                  \n");
			}
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null                   \n");
			strQuery.append("   and a.cm_svrcd<>'90'                                        \n");
			strQuery.append("   and d.cm_macode='SERVERCD' and d.cm_micode=a.cm_svrcd       \n");
			if (DirPath != null && DirPath != "") {
				strQuery.append("and a.cm_syscd=c.cm_syscd and a.cm_svrcd=c.cm_svrcd        \n");
				strQuery.append("and a.cm_seqno=c.cm_seqno                                  \n");
				strQuery.append("and c.cm_syscd=b.cm_syscd and c.cm_rsrccd=b.cm_rsrccd      \n");
				strQuery.append("and b.cm_svrcd='01'                                        \n");
				strQuery.append("and ((length(b.cm_volpath)>length(?)                       \n");
				strQuery.append("     and b.cm_volpath like ? || '%') or                    \n");
				strQuery.append("     (length(b.cm_volpath)=length(?)                       \n");
				strQuery.append("     and b.cm_volpath=?) or                                \n");
				strQuery.append("     (length(b.cm_volpath)<length(?)                       \n");
				strQuery.append("     and substr(b.cm_volpath,1,length(?))=?))              \n");
			}
			strQuery.append("group by a.cm_svrcd,a.cm_svrip,a.cm_svrusr,a.cm_svrname,a.cm_seqno,d.cm_codename \n");
			strQuery.append("order by a.cm_svrcd,a.cm_svrip                                 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			if (DirPath != null && DirPath != "") {
				pstmt.setString(++parmCnt, DirPath);
				pstmt.setString(++parmCnt, DirPath);
				pstmt.setString(++parmCnt, DirPath);
				pstmt.setString(++parmCnt, DirPath);
				pstmt.setString(++parmCnt, DirPath);
				pstmt.setString(++parmCnt, DirPath);
				pstmt.setString(++parmCnt, DirPath);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cd_svrcd", rs.getString("cm_svrcd"));
				rst.put("cd_svrip", rs.getString("cm_svrip"));
				rst.put("cd_svrusr", rs.getString("cm_svrusr"));
				rst.put("cd_svrname", rs.getString("cm_svrname"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cd_svrseq", Integer.toString(rs.getInt("cm_seqno")));
				rst.put("checked", "false");
				rst.put("chkbase", "false");
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
			ecamsLogger.error("## Cmp5100.getSvrList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp5100.getSvrList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getSvrList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp5100.getSvrList() Exception END ##");
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
					ecamsLogger.error("## Cmp5100.getSvrList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSvrList() method statement

	public String diffReq_Ins(ArrayList<HashMap<String,String>> svrList,HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  parmCnt     = 0;
		String			  AcptNo	  = null;
		int				  i;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("select nvl(max(cd_acptno)+1,to_char(SYSDATE,'yyyymm') || '000001') max \n");
			strQuery.append("  from cmd0021                                                 \n");
			strQuery.append(" where substr(cd_acptno,1,6)=to_char(SYSDATE,'yyyymm')         \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) AcptNo = rs.getString("max");

			parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("insert into cmd0021 (CD_ACPTNO,CD_SYSCD,CD_EDITOR,CD_BASICDT,  \n");
			strQuery.append("CD_DIRPATH,CD_DIFFECAMS,CD_DOWNDIR,CD_QRYCD                    \n");
			if (etcData.get("diffecams").equals("N")) {
				strQuery.append(",CD_BASEIP,CD_SVRCD,CD_SVRUSR,CD_SVRNAME,CD_SVRSEQ         \n");
			}
			strQuery.append(") values                                                       \n");
			strQuery.append("(?,?,?,to_char(SYSDATE,'yyyymmddhh24miss'),?,?,?,?,            \n");
			if (etcData.get("diffecams").equals("N")) {
				strQuery.append("?,?,?,?,?)                                                 \n");
			} else strQuery.append(")                                                       \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
			pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
			pstmt.setString(++parmCnt, etcData.get("userid"));
			pstmt.setString(++parmCnt, etcData.get("dirpath"));
			pstmt.setString(++parmCnt, etcData.get("diffecams"));
			pstmt.setString(++parmCnt, etcData.get("downdir"));
			pstmt.setString(++parmCnt, etcData.get("qrycd"));
			if (etcData.get("diffecams").equals("N")) {
				//ecamsLogger.debug("++++++base check+++++++++++");
				for (i=0;i<svrList.size();i++){
					//ecamsLogger.debug("++++++chkbase+++++++++++"+svrList.get(i).get("chkbase"));
					if (svrList.get(i).get("chkbase").equals("true")) {
						pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrip"));
						pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrcd"));
						pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrusr"));
						pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrname"));
						pstmt.setInt(++parmCnt, Integer.parseInt(svrList.get(i).get("cd_svrseq")));
						break;
					}
				}

			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
        	pstmt.close();

			for (i=0;i<svrList.size();i++){
				if (svrList.get(i).get("chkbase").equals("false")) {
					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("insert into cmd0022 (CD_ACPTNO,CD_SYSCD,CD_SVRIP,          \n");
					strQuery.append("  CD_SVRCD,CD_SVRUSR,CD_SVRNAME,CD_SVRSEQ) values          \n");
					strQuery.append("(?, ?, ?, ?, ?, ?, ?)                                         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(++parmCnt, AcptNo);
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrip"));
					pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrcd"));
					pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrusr"));
					pstmt.setString(++parmCnt, svrList.get(i).get("cd_svrname"));
					pstmt.setInt(++parmCnt, Integer.parseInt(svrList.get(i).get("cd_svrseq")));
					pstmt.executeUpdate();
					pstmt.close();
				}
	        }
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp5100.diffReq_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmp5100.diffReq_Ins() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp5100.diffReq_Ins() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp5100.diffReq_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmp5100.diffReq_Ins() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp5100.diffReq_Ins() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp5100.diffReq_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    public Object[] getDiffList(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		String            strEnd      = "0";

		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmd0020                            \n");
			strQuery.append(" where cd_acptno=? and cd_diffdate is not null                 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	if (rs.getInt("cnt") > 0) strEnd = "9";
	        }
	        rs.close();
	        pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select a.cd_svrname,a.cd_svrusr,a.cd_rsrcname,a.cd_diffrst,    \n");
			strQuery.append("       a.cd_status,a.cd_diffdt,a.cd_dirpath,b.cm_codename      \n");
			strQuery.append("  from cmm0020 b,cmd0020 a                                     \n");
			strQuery.append(" where a.cd_acptno=?                                           \n");
			strQuery.append("   and e.cm_macode='SERVERCD' and e.cm_micode=d.cd_svrcd       \n");
			strQuery.append(" order by a.cd_syscd,a.cd_dirpath,a.cd_rsrcname,a.cd_diffdt      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("endsw", strEnd);
				rst.put("cd_svrname", rs.getString("cd_svrname"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cd_svrusr", rs.getString("cd_svrusr"));
				rst.put("cd_dirpath", rs.getString("cd_dirpath"));
				if (rs.getString("cd_rsrcname") != null)
				   rst.put("cd_rsrcname", rs.getString("cd_rsrcname"));
				if (rs.getString("cd_diffrst") != null && rs.getString("cd_diffrst") != "") {
					if (rs.getString("cd_diffrst").equals("0")) rst.put("diffrst", "일치");
					else if (rs.getString("cd_diffrst").equals("I")) rst.put("diffrst", "추가");
					else if (rs.getString("cd_diffrst").equals("D")) rst.put("diffrst", "삭제");
					else if (rs.getString("cd_diffrst").equals("U")) rst.put("diffrst", "불일치");
					rst.put("diffcd", rs.getString("cd_diffrst"));
				} else {
					rst.put("diffrst", "비교 중");
					rst.put("diffcd", "1");
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
			rsval = null;
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getDiffList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp5100.getDiffList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp5100.getDiffList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp5100.getDiffList() Exception END ##");
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
					ecamsLogger.error("## Cmp5100.getDiffList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDiffList() method statement

	public boolean diffRun(String AcptNo) throws Exception {
		String			   tmpPath = "";
		String			   strBinPath = "";
		File 			   shfile=null;
		String  		   shFileName = "";
		OutputStreamWriter writer = null;
		String[] 		   strAry = null;
		Runtime  		   run = null;
		Process 		   p = null;

		try {
			SystemPath		   cTempGet	  = new SystemPath();
			tmpPath = cTempGet.getTmpDir("99");
			strBinPath = cTempGet.getTmpDir("14");
			cTempGet = null;

			shFileName = tmpPath + "/" + AcptNo +"_Cmp5100.sh";

			shfile = new File(shFileName);

			if( !(shfile.isFile()) )              //File이 없으면
			{
				shfile.createNewFile();          //File 생성
			}

			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
			writer.write("cd "+strBinPath +"\n");
			writer.write("./eacms_diffhnb " + AcptNo + "\n");
			writer.write("exit $?\n");
			writer.close();

			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = shFileName;
			run = Runtime.getRuntime();
			//ecamsLogger.debug("====chmod===="+"chmod "+"777 "+shFileName+" \n");
			p = run.exec(strAry);
			p.waitFor();

			//ecamsLogger.debug("====chmod return===="+Integer.toString(p.exitValue())+" \n");

			run = Runtime.getRuntime();
			strAry = new String[2];
			strAry[0] = "/bin/sh";
			strAry[1] = shFileName;
			p = run.exec(strAry);
			p.waitFor();

			//ecamsLogger.debug("====return===="+Integer.toString(p.exitValue())+" \n");
			if (p.exitValue() != 0) {
				shfile.delete();
				if (shfile != null) shfile = null;
				if (strAry != null) strAry = null;
				if (run != null) run = null;
				if (p != null) p = null;
				return false;
				//throw new Exception("해당 소스 생성  실패. run=["+shFileName +"]" + " return=[" + p.exitValue() + "]" );
			}
			else{
				shfile.delete();
				if (shfile != null) shfile = null;
				if (strAry != null) strAry = null;
				if (run != null) run = null;
				if (p != null) p = null;
				return true;
			}


		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp5100.diffRun() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp5100.diffRun() Exception END ##");
			throw exception;
		}finally{
			if (shfile != null) shfile = null;
			if (strAry != null) strAry = null;
			if (run != null) run = null;
			if (p != null) p = null;
		}
	}
}//end of Cmp5100 class statement
