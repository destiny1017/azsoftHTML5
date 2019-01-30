/*****************************************************************************************
	1. program ID	: Cmr2200.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 09.04.16
	5. auth		    : No Name
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

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
public class Cmr2200{


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
	public Object[] getReqList(String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno,   \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,        \n");
			strQuery.append("       a.cr_acptno,a.cr_syscd,a.cr_editor,a.cr_sayu,                \n");
			strQuery.append("       b.cm_sysmsg,c.cm_username,d.cr_confusr                       \n");
			strQuery.append("  from cmr1000 a,cmm0030 b,cmm0040 c,cmr9900 d,cmm0040 e            \n");
			strQuery.append(" where a.cr_status='0' and a.cr_qrycd='04' and a.cr_passok='6'      \n");
			strQuery.append("   and (a.cr_editor=? or e.cm_admin='1')                            \n");
			strQuery.append("   and a.cr_acptno=d.cr_acptno and d.cr_locat='00'                  \n");
			strQuery.append("   and d.cr_status='0' and d.cr_team='SYSED'                        \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_editor=c.cm_userid            \n");
			strQuery.append("   and e.cm_userid=?                                                \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,UserId);
			pstmt.setString(2,UserId);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("acptno"));
				rst.put("cr_acptdate",rs.getString("acptdate"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_confusr",rs.getString("cr_confusr"));
				if (rs.getString("cr_sayu") != null){
				   rst.put("cr_sayu",rs.getString("cr_sayu"));
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
			ecamsLogger.error("## Cmr2200.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2200.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2200.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2200.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmr2200.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement


	public Object[] getSvrList(String UserId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select b.cm_svrname,b.cm_svrip,b.cm_portno,b.cm_svrcd,b.cm_seqno    \n");
			strQuery.append("  from cmr1010 a,cmm0031 b,cmm0038 c,cmm0036 d                      \n");
			strQuery.append(" where a.cr_acptno=? and a.cr_status='0' and a.cr_prcdate is null   \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd            \n");
			strQuery.append("   and substr(d.cm_info,11,1)='1'                                   \n");
			strQuery.append("   and d.cm_syscd=c.cm_syscd and d.cm_rsrccd=c.cm_rsrccd            \n");
			strQuery.append("   and c.cm_svrcd='05'                                              \n");
			strQuery.append("   and c.cm_syscd=b.cm_syscd and c.cm_svrcd=b.cm_svrcd              \n");
			strQuery.append("   and c.cm_seqno=b.cm_seqno and b.cm_closedt is null               \n");
			strQuery.append(" group by b.cm_svrname,b.cm_svrip,b.cm_portno,b.cm_svrcd,b.cm_seqno \n");
			strQuery.append(" order by b.cm_svrname,b.cm_svrip,b.cm_portno,b.cm_svrcd,b.cm_seqno \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_svrname",rs.getString("cm_svrname"));
				rst.put("cm_svrip",rs.getString("cm_svrip"));
				rst.put("cm_portno",Integer.toString(rs.getInt("cm_portno")));
				rst.put("cm_seqno",Integer.toString(rs.getInt("cm_seqno")));
				rst.put("cm_svrcd",rs.getString("cm_svrcd"));
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
			ecamsLogger.error("## Cmr2200.getSvrList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2200.getSvrList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2200.getSvrList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2200.getSvrList() Exception END ##");
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
					ecamsLogger.error("## Cmr2200.getSvrList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSvrList() method statement


	public Object[] getFileList(String UserId,String AcptNo,String SvrCd,String SeqNo,String EndYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           findSw      = false;
		Object[] returnObjectArray	  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_svrname,b.cm_dirpath,c.cr_rsrcname,c.cr_syscd,          \n");
			strQuery.append("       c.cr_rsrccd,c.cr_serno,d.cm_codename,f.cm_volpath,           \n");
			strQuery.append("       a.cm_svrip,a.cm_portno,a.cm_seqno,a.cm_svrcd                 \n");
			strQuery.append("  from cmm0031 a,cmm0070 b,cmr1010 c,cmm0020 d,cmm0036 e,cmm0038 f  \n");
			if (EndYn.equals("E") || EndYn.equals("Y")) strQuery.append(",cmr1011 g              \n");
			strQuery.append(" where c.cr_acptno=? and c.cr_prcdate is null                       \n");
			if (EndYn.equals("E")) {
				strQuery.append(" and c.cr_status='0' and nvl(c.cr_putcode,'0000')<>'0000'       \n");
			} else if (EndYn.equals("0")) {
				strQuery.append(" and c.cr_status='0' and c.cr_prcdate is null                   \n");
			}
			strQuery.append("   and c.cr_syscd=e.cm_syscd and c.cr_rsrccd=e.cm_rsrccd            \n");
			strQuery.append("   and (substr(e.cm_info,11,1)='1' or substr(e.cm_info,21,1)='1')   \n");
			strQuery.append("   and e.cm_syscd=f.cm_syscd and e.cm_rsrccd=f.cm_rsrccd            \n");
			strQuery.append("   and f.cm_svrcd=? and instr(?,f.cm_seqno)>0                       \n");
			strQuery.append("   and f.cm_syscd=a.cm_syscd and f.cm_svrcd=a.cm_svrcd              \n");
			strQuery.append("   and f.cm_seqno=a.cm_seqno                                        \n");
			strQuery.append("   and a.cm_closedt is null                                         \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd and c.cr_dsncd=b.cm_dsncd              \n");
			strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=c.cr_rsrccd              \n");
			if (EndYn.equals("E") || EndYn.equals("Y")) {
				strQuery.append("   and c.cr_acptno=g.cr_acptno and c.cr_serno=g.cr_serno        \n");
				strQuery.append("   and g.cr_prcsys='SYSED'                                      \n");
				strQuery.append("   and g.cr_syscd=a.cm_syscd and g.cr_svrcd=a.cm_svrcd          \n");
				strQuery.append("   and g.cr_svrseq=a.cm_seqno                                   \n");
				if (EndYn.equals("E")) strQuery.append("   and nvl(g.cr_prcrst,'0000')<>'0000'   \n");
				else strQuery.append("   and nvl(g.cr_prcrst,'0000')='0000'                      \n");
			} else if (EndYn.equals("A")) {
				strQuery.append("union \n");
				strQuery.append("select a.cm_svrname,b.cm_dirpath,c.cr_rsrcname,c.cr_syscd,      \n");
				strQuery.append("       c.cr_rsrccd,c.cr_serno,d.cm_codename,f.cm_volpath,       \n");
				strQuery.append("       a.cm_svrip,a.cm_portno,a.cm_seqno,a.cm_svrcd             \n");
				strQuery.append("  from cmr1011 g,cmm0031 a,cmm0070 b,cmr1010 c,cmm0020 d,cmm0036 e,cmm0038 f  \n");
				strQuery.append(" where c.cr_acptno=? and c.cr_prcdate is null                   \n");
				strQuery.append("   and c.cr_status='0' and c.cr_prcdate is null                 \n");
				strQuery.append("   and c.cr_syscd=e.cm_syscd and c.cr_rsrccd=e.cm_rsrccd        \n");
				strQuery.append("   and (substr(e.cm_info,11,1)='1' or substr(e.cm_info,21,1)='1')   \n");
				strQuery.append("   and e.cm_syscd=f.cm_syscd and e.cm_rsrccd=f.cm_rsrccd        \n");
				strQuery.append("   and f.cm_svrcd=? and instr(?,f.cm_seqno)>0                   \n");
				strQuery.append("   and f.cm_syscd=a.cm_syscd and f.cm_svrcd=a.cm_svrcd          \n");
				strQuery.append("   and f.cm_seqno=a.cm_seqno                                    \n");
				strQuery.append("   and a.cm_closedt is null                                     \n");
				strQuery.append("   and c.cr_syscd=b.cm_syscd and c.cr_dsncd=b.cm_dsncd          \n");
				strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=c.cr_rsrccd          \n");
				strQuery.append("   and c.cr_acptno=g.cr_acptno and c.cr_serno=g.cr_serno        \n");
				strQuery.append("   and g.cr_prcsys='SYSED'                                      \n");
				strQuery.append("   and g.cr_syscd=a.cm_syscd and g.cr_svrcd=a.cm_svrcd          \n");
				strQuery.append("   and g.cr_svrseq=a.cm_seqno                                   \n");
			}
			strQuery.append(" order by cm_svrname,cr_rsrcname                                \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, SvrCd);
			pstmt.setString(3, SeqNo);
			if (EndYn.equals("A")) {
				pstmt.setString(4, AcptNo);
				pstmt.setString(5, SvrCd);
				pstmt.setString(6, SeqNo);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				findSw = true;
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_svrname",rs.getString("cm_svrname"));
				rst.put("cm_dirpath",getChgVolPath(rs.getString("cr_syscd"),rs.getString("cr_rsrccd"),rs.getString("cm_volpath"),rs.getString("cm_dirpath"),conn));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cm_svrip",rs.getString("cm_svrip"));
				rst.put("cm_svrcd",rs.getString("cm_svrcd"));
				rst.put("cm_portno",Integer.toString(rs.getInt("cm_portno")));
				rst.put("cr_serno",Integer.toString(rs.getInt("cr_serno")));
				rst.put("cm_seqno",Integer.toString(rs.getInt("cm_seqno")));

				strQuery.setLength(0);
				strQuery.append("select nvl(a.cr_prcrst,'ING1') prcrst,b.cm_codename,cr_prcdate,     \n");
				strQuery.append("       to_char(a.cr_prcdate,'yyyy-mm-dd hh24:mi') prcdate           \n");
				strQuery.append("  from cmr1011 a,cmm0020 b                                          \n");
				strQuery.append(" where a.cr_acptno=? and a.cr_serno=? and a.cr_prcsys='SYSED'       \n");
				strQuery.append("   and a.cr_ipaddr=? and a.cr_svrseq=? and a.cr_svrcd='05'          \n");
				strQuery.append("   and b.cm_macode='ERRACCT' and b.cm_micode=nvl(a.cr_prcrst,'ING1') \n");

				pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, AcptNo);
				pstmt2.setInt(2, rs.getInt("cr_serno"));
				pstmt2.setString(3, rs.getString("cm_svrip"));
				pstmt2.setString(4, Integer.toString(rs.getInt("cm_seqno")));
		        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
		        if (rs2.next()) {
		        	if (rs2.getString("cr_prcdate") != null && EndYn.equals("0") && rs2.getString("prcrst").equals("0000")) {
		        		findSw = false;
		        	} else {
		        	    rst.put("cr_prcrst",rs2.getString("cm_codename"));
		        	    rst.put("cr_prcdate",rs2.getString("prcdate"));
		        	}
		        }
		        rs2.close();
		        pstmt2.close();
		        rs2 = null;
		        pstmt2 = null;

		        if (findSw == true){
		        	rsval.add(rst);
		        	rst = null;
		        }
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
			ecamsLogger.error("## Cmr2200.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2200.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2200.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2200.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr2200.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement


	public String getChgVolPath(String SysCd,String RsrcCd,String VolPath,String DirPath,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
	    String            chgPath     = "";
	    String            strPath     = "";

		try {
			strQuery.append("select a.cm_volpath,a.cm_svrcd                                      \n");
			strQuery.append("  from cmm0038 a,cmm0030 b                                          \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_rsrccd=?                               \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd and a.cm_svrcd=b.cm_dirbase            \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			if (rs.next()){
				if (VolPath.equals(rs.getString("cm_volpath"))) chgPath = DirPath;
				else {
					chgPath = VolPath;
					if (!chgPath.substring(chgPath.length()-1).equals("/")){
						chgPath = chgPath + "/";
					}
					strPath = rs.getString("cm_volpath");
					if (!strPath.substring(strPath.length()-1).equals("/")){
						strPath = strPath + "/";
					}

					chgPath = chgPath + DirPath.substring(strPath.length());
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return chgPath;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2200.getChgVolPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2200.getChgVolPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2200.getChgVolPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2200.getChgVolPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getChgVolPath() method statement

	public String putDeploy(String AcptNo,String SysCd,String UserId,ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "0";
		String            strBinPath  = "";
		String            strTmpPath  = "";
		String            makeFile    = "";
		int               seqNo       = 0;
		File shfile=null;
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String[] chkAry;
		int		cmdrtn;
		try {
			conn = connectionContext.getConnection();

			strBinPath = ecamsinfo.getFileInfo_conn("14",conn);
			strTmpPath = ecamsinfo.getFileInfo_conn("99",conn);
			ecamsinfo = null;

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = strBinPath+"/procck2";
			chkAry[2] = "ecams_dpt";
			chkAry[3] = AcptNo;


			p = run.exec(chkAry);
			p.waitFor();

			cmdrtn = p.exitValue();
			if (cmdrtn > 1) {
	        	pstmt = null;
	        	conn = null;

				return "2";
			}
			conn.setAutoCommit(false);

			for (int i=0;i<fileList.size();i++){
				strQuery.setLength(0);
				strQuery.append("delete cmr1011                                              \n");
				strQuery.append(" where cr_acptno=? and cr_ipaddr=? and cr_svrseq=?          \n");
				strQuery.append("   and (cr_serno=? or cr_serno=0) and cr_prcsys='SYSED'     \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2,fileList.get(i).get("cm_svrip"));
				pstmt.setString(3,fileList.get(i).get("cm_seqno"));
				pstmt.setInt(4,Integer.parseInt(fileList.get(i).get("cr_serno")));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();


				strQuery.setLength(0);
				strQuery.append("update cmr1010                                              \n");
				strQuery.append("   set cr_putcode='',cr_pid='',cr_sysstep=0,cr_srccmp='Y'   \n");
				strQuery.append(" where cr_acptno=? and cr_serno=?                           \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1,AcptNo);
				pstmt.setInt(2,Integer.parseInt(fileList.get(i).get("cr_serno")));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();


		        strQuery.setLength(0);
		        strQuery.append("select nvl(max(cr_seqno),0) max from cmr1012    \n");
		        strQuery.append("where cr_acptno=?                               \n");

		        pstmt = conn.prepareStatement(strQuery.toString());
		        //pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					seqNo = rs.getInt("max");
				}
				rs.close();
				pstmt.close();

				strQuery.setLength(0);
		        strQuery.append("insert into cmr1012 (CR_ACPTNO,CR_SEQNO,                    \n");
				strQuery.append("   CR_SERNO,CR_REQDATE,CR_SVRCD,CR_SVRSEQ,CR_SYSCD)         \n");
				strQuery.append(" values                                                     \n");
				strQuery.append("   (?, ?, ?, SYSDATE, ?, ?, ?)                              \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setInt(2,++seqNo);
				pstmt.setInt(3,Integer.parseInt(fileList.get(i).get("cr_serno")));
				pstmt.setString(4,fileList.get(i).get("cm_svrcd"));
				pstmt.setInt(5,Integer.parseInt(fileList.get(i).get("cm_seqno")));
				pstmt.setString(6,SysCd);
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        pstmt.executeUpdate();
		        pstmt.close();

			}
			conn.commit();
			conn.setAutoCommit(true);

        	makeFile = AcptNo + UserId + ".sh";
			shfile=null;
			shfile = new File(strTmpPath + makeFile);              //File 불러온다.
			if( !(shfile.isFile()) )              //File이 없으면
			{
				shfile.createNewFile();          //File 생성
			}
			writer = new OutputStreamWriter( new FileOutputStream(strTmpPath + makeFile));
			writer.write("cd "+strBinPath +"\n");
			writer.write("./ecams_dpt " + AcptNo + "\n");
			writer.write("exit $?\n");
			writer.close();
			writer = null;

			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = strTmpPath + makeFile;
			run = Runtime.getRuntime();
			//ecamsLogger.debug("====chmod==== : "+"chmod "+"777 "+strTmpPath + makeFile+" \n");
			p = run.exec(strAry);
			p.waitFor();

			strAry = null;
			strAry = new String[2];
			strAry[0] = "/bin/sh";
			strAry[1] = strTmpPath + makeFile + " &";
			run = Runtime.getRuntime();
			//ecamsLogger.debug("====/bin/sh ==== : "+strTmpPath + makeFile+" \n");
			p = run.exec(strAry);
			p.waitFor();

			//ecamsLogger.debug("====return===="+Integer.toString(p.exitValue())+" \n");

			if (p.exitValue() != 0) {
				if (p.exitValue() == 2 ){
					strErMsg = "9현재 해당신청번호로 배포 처리 중인 프로세스가 있습니다. 종료된 후 처리하십시오";
				}
				else{
					strErMsg = "9배포처리 실행 중 오류가 발생하였습니다 - (" + Integer.toString(p.exitValue())+ ")";
				}
			}else{
				//strErMsg = "배포처리를 시작하였습니다. 잠시 후 조회버튼을 클릭하여 처리결과를 확인하여 주시기 바랍니다.";
				strErMsg = chkEndYn_conn(AcptNo,conn);
			}
			shfile.delete();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2200.putDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2200.putDeploy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2200.putDeploy() SQLException END ##");
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
					ecamsLogger.error("## Cmr2200.putDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2200.putDeploy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2200.putDeploy() Exception END ##");
			throw exception;
		}finally{
			if (shfile != null) shfile = null;
			if (strAry != null) strAry = null;
			if (run != null) run = null;
			if (p != null) p = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2200.putDeploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of putDeploy() method statement


	public String chkEndYn_conn(String AcptNo,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";
		int               reqCnt      = 0;
		int               prcCnt      = 0;

		try {
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt                                                 \n");
			strQuery.append("  from cmm0031 a,cmr1010 c,cmm0036 e,cmm0038 f                      \n");
			strQuery.append(" where c.cr_acptno=? and c.cr_status<>'3'                           \n");
			strQuery.append("   and c.cr_syscd=e.cm_syscd and c.cr_rsrccd=e.cm_rsrccd            \n");
			strQuery.append("   and (substr(e.cm_info,11,1)='1' or substr(e.cm_info,21,1)='1')   \n");
			strQuery.append("   and e.cm_syscd=f.cm_syscd and e.cm_rsrccd=f.cm_rsrccd            \n");
			strQuery.append("   and f.cm_svrcd='05' and f.cm_syscd=a.cm_syscd                    \n");
			strQuery.append("   and f.cm_svrcd=a.cm_svrcd  and f.cm_seqno=a.cm_seqno             \n");
			strQuery.append("   and a.cm_closedt is null                                         \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
	        rs = pstmt.executeQuery();
			if (rs.next()){
				reqCnt = rs.getInt("cnt");
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt                                                 \n");
			strQuery.append("  from cmr1011                                                      \n");
			strQuery.append(" where cr_acptno=? and cr_prcsys='SYSED'                            \n");
			strQuery.append("   and cr_prcdate is not null and nvl(cr_prcrst,'ERR')='0000'       \n");
			strQuery.append("   and cr_serno>0                                                   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
	        rs = pstmt.executeQuery();
			if (rs.next()){
				prcCnt = rs.getInt("cnt");
			}
			rs.close();
			pstmt.close();

			rs = null;
			pstmt = null;

			if (reqCnt > prcCnt){
				strErMsg = "1";
			}
			else{
				strErMsg = "0";
			}

			//conn.close();
			//conn = null;

			return strErMsg;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of chkEndYn() method statement


	public String chkEndYn(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";
		int               reqCnt      = 0;
		int               prcCnt      = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select count(*) cnt                                                 \n");
			strQuery.append("  from cmm0031 a,cmr1010 c,cmm0036 e,cmm0038 f                      \n");
			strQuery.append(" where c.cr_acptno=? and c.cr_status<>'3'                           \n");
			strQuery.append("   and c.cr_syscd=e.cm_syscd and c.cr_rsrccd=e.cm_rsrccd            \n");
			strQuery.append("   and (substr(e.cm_info,11,1)='1' or substr(e.cm_info,21,1)='1')   \n");
			strQuery.append("   and e.cm_syscd=f.cm_syscd and e.cm_rsrccd=f.cm_rsrccd            \n");
			strQuery.append("   and f.cm_svrcd='05' and f.cm_syscd=a.cm_syscd                    \n");
			strQuery.append("   and f.cm_svrcd=a.cm_svrcd  and f.cm_seqno=a.cm_seqno             \n");
			strQuery.append("   and a.cm_closedt is null                                         \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			while (rs.next()){
				reqCnt = rs.getInt("cnt");
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt                                                 \n");
			strQuery.append("  from cmr1011                                                      \n");
			strQuery.append(" where cr_acptno=? and cr_prcsys='SYSED' and cr_svrcd='05'          \n");
			strQuery.append("   and cr_prcdate is not null and nvl(cr_prcrst,'ERR')='0000'       \n");
			strQuery.append("   and cr_serno>0                                                   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			while (rs.next()){
				prcCnt = rs.getInt("cnt");
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			if (reqCnt > prcCnt){
				strErMsg = "1";
			}
			else{
				strErMsg = "0";
			}


			return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2200.chkEndYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2200.chkEndYn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2200.chkEndYn() SQLException END ##");
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
					ecamsLogger.error("## Cmr0200.chkEndYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2200.chkEndYn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2200.chkEndYn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2200.chkEndYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of chkEndYn() method statement

}//end of Cmr2200 class statement
