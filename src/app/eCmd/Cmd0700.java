/*****************************************************************************************
	1. program ID	: Cmd0700.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	: 2009.03
	5. auth		    : No Name
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

//import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import java.util.ArrayList;
import java.util.HashMap;
import app.common.UserInfo;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmd0700{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	public Object[] getRsrcBld(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_micode,a.cm_codename,b.cm_info   \n");
			strQuery.append("  from cmm0020 a,cmm0036 b                   \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_closedt is null \n");
			strQuery.append("   and substr(b.cm_info,48,1)='1'            \n");
			strQuery.append("   and a.cm_macode='JAWON' and a.cm_micode=b.cm_rsrccd  \n");
			strQuery.append(" order by a.cm_seqno                         \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_info", rs.getString("cm_info"));
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
			ecamsLogger.error("## Cmd0700.getRsrcBld() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0700.getRsrcBld() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0700.getRsrcBld() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0700.getRsrcBld() Exception END ##");
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
					ecamsLogger.error("## Cmd0700.getRsrcBld() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getRsrcBld() method statement


	public Object[] getRsrcMod(String SysCd,String SelMsg,String RsrcCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.equals("ALL")) strSelMsg = "전체";
				else if (SelMsg.equals("SEL")) strSelMsg = "선택하세요";
			}
			strQuery.setLength(0);
			strQuery.append("select a.cm_micode,a.cm_codename,b.cm_info,b.cm_exename \n");
			strQuery.append("       ,c.cm_basename,c.cm_basedir,                     \n");
			strQuery.append("       c.cm_samename,c.cm_samedir                       \n");
			strQuery.append("  from cmm0020 a,cmm0036 b,cmm0037 c                    \n");
			strQuery.append(" where c.cm_syscd=? and c.cm_rsrccd=?                   \n");
			strQuery.append("   and c.cm_factcd='04'                                 \n");
			strQuery.append("   and c.cm_syscd=b.cm_syscd and c.cm_samersrc=b.cm_rsrccd\n");
			strQuery.append("   and a.cm_macode='JAWON' and a.cm_micode=b.cm_rsrccd  \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, RsrcCd);

	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg != "") {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_micode", "0000");
				   rst.put("cm_codename", strSelMsg);
				   rst.put("cm_info", "");
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cm_basename", rs.getString("cm_basename"));
				rst.put("cm_samename", rs.getString("cm_samename"));
				rst.put("cm_basedir", rs.getString("cm_basedir"));
				rst.put("cm_samedir", rs.getString("cm_samedir"));
				if (rs.getString("cm_exename") != null && rs.getString("cm_exename") != "") {
					if (rs.getString("cm_exename").substring(rs.getString("cm_exename").length() - 1).equals(","))
						rst.put("cm_exename", rs.getString("cm_exename"));
					else rst.put("cm_exename", rs.getString("cm_exename")+",");
				} else rst.put("cm_exename", "");
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
			ecamsLogger.error("## Cmd0700.getRsrcMod() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0700.getRsrcMod() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0700.getRsrcMod() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0700.getRsrcMod() Exception END ##");
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
					ecamsLogger.error("## Cmd0700.getRsrcMod() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getRsrcMod() method statement
	public Object[] getFileList(String UserId,String SysCd,String RsrcCd,String RsrcName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray = null;
		int               parmCnt  = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			UserInfo userinfo = new UserInfo();
			boolean secuSw = userinfo.isAdmin(UserId);

			rsval.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cr_syscd,a.cr_dsncd,a.cr_rsrccd,a.cr_langcd, \n");
			strQuery.append("       a.cr_rsrcname,a.cr_story,a.cr_jobcd,           \n");
			strQuery.append("       to_char(a.cr_lastdate,'yyyy-mm-dd') lastdt,    \n");
			strQuery.append("       a.cr_itemid,a.cr_jobcd,f.cr_cmdname,f.cr_modname, \n");
			strQuery.append("       e.cm_dirpath,c.cm_codename jawon,            \n");
			strQuery.append("       f.cr_samersrc,f.cr_samedir,f.cr_prcsys       \n");
			strQuery.append("  from cmr0026 f,cmm0070 e,cmm0020 c,cmr0020 a      \n");
			if (secuSw == false) {
				   strQuery.append(",cmm0044 h                           \n");
			}
			strQuery.append(" where a.cr_syscd=? and a.cr_rsrccd=?       \n");
			if (secuSw == false) {
				strQuery.append("and h.cm_userid=? and h.cm_closedt is null        \n");
				strQuery.append("and a.cr_syscd=h.cm_syscd and a.cr_jobcd=h.cm_jobcd \n");
			}
			if (RsrcName != "" && RsrcName != "") {
				strQuery.append("and upper(a.cr_rsrcname) like upper(?)              \n");
			}
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd  \n");
			strQuery.append("   and a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd  \n");
			strQuery.append("   and a.cr_itemid=f.cr_itemid(+)                       \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());

	        pstmt.setString(++parmCnt, SysCd);
	        pstmt.setString(++parmCnt, RsrcCd);
	        if (secuSw == false) {
	        	pstmt.setString(++parmCnt,UserId);
	        }
	        if (RsrcName != "" && RsrcName != "") {
	        	pstmt.setString(++parmCnt, "%"+RsrcName+"%");
			}

	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID",Integer.toString(rs.getRow()));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_story",rs.getString("cr_story"));
				rst.put("jawon_code",rs.getString("jawon"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_lastdate",rs.getString("lastdt"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_cmdname",rs.getString("cr_cmdname"));
				rst.put("cr_modname",rs.getString("cr_modname"));
				rst.put("cr_samersrc",rs.getString("cr_samersrc"));
				rst.put("cr_samedir",rs.getString("cr_samedir"));
				if (rs.getString("cr_prcsys") != null) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020  \n");
					strQuery.append(" where cm_macode='SYSGBN'         \n");
					strQuery.append("   and cm_micode=?               \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_prcsys"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("prcsys", rs2.getString("cm_codename"));
						rst.put("cr_prcsys", rs.getString("cr_prcsys"));
					}
					rs2.close();
					pstmt2.close();
				}
				if (rs.getString("cr_samersrc") != null) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020  \n");
					strQuery.append(" where cm_macode='JAWON'         \n");
					strQuery.append("   and cm_micode=?               \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_samersrc"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("samejawon", rs2.getString("cm_codename"));
					}
					rs2.close();
					pstmt2.close();
				}
				if (rs.getString("cr_samedir") != null) {
					strQuery.setLength(0);
					strQuery.append("select cm_dirpath from cmm0070   \n");
					strQuery.append(" where cm_syscd=? and cm_dsncd=? \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_syscd"));
					pstmt2.setString(2, rs.getString("cr_samedir"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("samedir", rs2.getString("cm_dirpath"));
					}
					rs2.close();
					pstmt2.close();
				}
				rst.put("enabled", "1");
				rst.put("selected", "0");

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
			ecamsLogger.error("## Cmd0700.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0700.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0700.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0700.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0700.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement
	public String CMR0026_Update(String UserId,ArrayList<HashMap<String,String>> dataList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

//			String ItemId = "";
			for (int i=0 ; i<dataList.size() ; i++){
				//ItemId = dataList.get(i).get("cr_itemid");
				strQuery.setLength(0);
	        	strQuery.append("delete cmr0026     \n");
	        	strQuery.append(" where cr_itemid=? \n");
	        	strQuery.append("   and cr_prcsys=? \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, dataList.get(i).get("cr_itemid"));
	        	pstmt.setString(2, dataList.get(i).get("prcsys"));
	        	pstmt.executeUpdate();

	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr0026    \n");
	        	strQuery.append("  (CR_ITEMID,CR_CMDNAME,CR_PRCSYS,CR_MODNAME,CR_SAMERSRC,CR_SAMEDIR,CR_CREATDT,CR_LASTDT,CR_EDITOR) \n");
	        	strQuery.append(" values (?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE,?) \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, dataList.get(i).get("cr_itemid"));
	        	pstmt.setString(2, dataList.get(i).get("cmdname"));
	        	pstmt.setString(3, dataList.get(i).get("prcsys"));
	        	pstmt.setString(4, dataList.get(i).get("modname"));
	        	pstmt.setString(5, dataList.get(i).get("samersrc"));
	        	pstmt.setString(6, dataList.get(i).get("samedir"));
	        	pstmt.setString(7, UserId);
	        	pstmt.executeUpdate();
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
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0700.CMR0026_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0700.CMR0026_Update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0700.CMR0026_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0700.CMR0026_Update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0700.CMR0026_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of CMR0026_Update() method statement

	/**
	 * <PRE>
	 * 1. MethodName	: CMR0026_Delete
	 * 2. ClassName		: Cmd0700
	 * 3. Commnet			: 사용자정의빌드정보 삭제
	 * 4. 작성자				: Administrator
	 * 5. 작성일				: 2011. 2. 8. 오후 7:53:08
	 * </PRE>
	 * 		@return String
	 * 		@param UserId
	 * 		@param dataList : 삭제 대상 리스트
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public String CMR0026_Delete(String UserId,ArrayList<HashMap<String,String>> dataList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			for (int i=0 ; i<dataList.size() ; i++){
				strQuery.setLength(0);
	        	strQuery.append("delete cmr0026       \n");
	        	strQuery.append(" where cr_itemid=?   \n");
	        	strQuery.append("   and cr_prcsys=?   \n");
	            if (dataList.get(i).get("cr_samersrc") != null && dataList.get(i).get("cr_samersrc") != ""){
	            	strQuery.append("   and cr_samersrc=? \n");
	            }
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, dataList.get(i).get("cr_itemid"));
	        	pstmt.setString(2, dataList.get(i).get("cr_prcsys"));
	            if (dataList.get(i).get("cr_samersrc") != null && dataList.get(i).get("cr_samersrc") != ""){
	            	pstmt.setString(3, dataList.get(i).get("cr_samersrc"));
	            }
	        	pstmt.executeUpdate();
	        	pstmt.close();
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
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0700.CMR0026_Delete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0700.CMR0026_Delete() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0700.CMR0026_Delete() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0700.CMR0026_Delete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0700.CMR0026_Delete() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0700.CMR0026_Delete() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0700.CMR0026_Delete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of CMR0026_Delete() method statement

}//end of Cmd0700 class statement
