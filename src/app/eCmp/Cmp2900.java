package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;
import app.common.UserInfo;
//import app.eCmr.Cmr0260;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmp2900 {
	
	Logger ecamsLogger = EcamsLogger.getLoggerInstance();

	public Object[] CmdQry(String chk,String strstd,String stredd,String errday,String userid)throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strQuery = new StringBuffer();
		ArrayList<HashMap<String, String>> rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst = null;
		int cnt = 0;
		boolean adminYn = false;
	
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			UserInfo     userinfo = new UserInfo();
			adminYn = userinfo.isAdmin_conn(userid,conn);
			strQuery.append("  SELECT /*+ RULE */ A.CD_SYSCD,   \n");
			strQuery.append("    A.CD_SVRCD,                    \n");
			strQuery.append("    A.CD_DSNCD,                    \n");
			strQuery.append("    D.CM_SYSMSG SYSNM ,            \n");
			strQuery.append("    A.CD_SVRIP ,                   \n");
			strQuery.append("    A.CD_PORTNO,                   \n");
			strQuery.append("    A.CD_SVRNAME ,                 \n");
			strQuery.append("    A.CD_RSRCNAME ,				\n");
			strQuery.append("    nvl(B.CM_DIRPATH, a.CD_DIRPATH) CM_DIRPATH,\n");
			strQuery.append("    C.CM_CODENAME ,		  	    \n");
			strQuery.append(" 	A.CD_DIFFDT ,			        \n");
			strQuery.append("    A.CD_ERRCLSDT ,			    \n");
			strQuery.append("    A.CD_ERRDAYCNT AS CD_ERRDAYCNT,\n");
			strQuery.append("    A.CD_SAYU,					    \n");
			strQuery.append("   (SELECT CM_USERNAME FROM CMM0040 WHERE CM_USERID =(SELECT CR_EDITOR FROM CMR0020 WHERE CR_SYSCD = A.CD_SYSCD AND CR_DSNCD = A.CD_DSNCD AND CR_RSRCNAME = A.CD_RSRCNAME)) OWNER_NM,  \n");
			strQuery.append("   (SELECT CM_USERID FROM CMM0040 WHERE CM_USERID =(SELECT CR_EDITOR FROM CMR0020 WHERE CR_SYSCD = A.CD_SYSCD AND CR_DSNCD = A.CD_DSNCD AND CR_RSRCNAME = A.CD_RSRCNAME)) OWNER_ID    \n");			
			strQuery.append("  FROM CMD0029 A ,			        \n");
			strQuery.append("       CMM0070 B ,			        \n");
			strQuery.append(" 	    CMM0020 C ,		            \n");
			strQuery.append(" 		CMM0030 D 				    \n");
			if(chk.equals("0")){
				strQuery.append(" WHERE A.CD_DIFFDT BETWEEN ? AND ?   \n");
			}else{
				strQuery.append(" WHERE A.CD_DIFFDT = ?         \n");
			}
			if(!adminYn){
				strQuery.append("AND a.cd_syscd in (select cm_syscd from cmm0044    \n");
				strQuery.append("                      where cm_userid=?            \n");
				strQuery.append("                        and cm_closedt is null)    \n");
			}			
			strQuery.append("   AND NVL(A.CD_ERRDAYCNT, 0) >= ? \n");
			strQuery.append("   AND A.CD_SYSCD = B.CM_SYSCD(+)	\n");
			strQuery.append("   AND A.CD_DSNCD = B.CM_DSNCD(+)	\n");
			strQuery.append("	AND C.CM_MACODE = 'DIFFRST'     \n");
			strQuery.append("   AND C.CM_MICODE = A.CD_DIFFRST	\n");
			strQuery.append("   AND A.CD_SYSCD = D.CM_SYSCD(+)  \n");
			strQuery.append(" order by CD_DIFFDT desc,CD_ERRDAYCNT desc	\n");

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			if (chk.equals("0")) {
				pstmt.setString(++cnt, strstd);
				pstmt.setString(++cnt, stredd);
			}else{
				pstmt.setString(++cnt, strstd);
			}
			if(!adminYn)	{
				pstmt.setString(++cnt, userid);				
			}
			pstmt.setString(++cnt, errday);  //두번째 유니온전

			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			rsval.clear();
			while (rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cd_syscd", rs.getString("cd_syscd"));
				rst.put("cd_svrcd", rs.getString("cd_svrcd"));
				rst.put("cd_dsncd", rs.getString("cd_dsncd"));
				rst.put("sysnm", rs.getString("sysnm"));
				rst.put("cd_svrip", rs.getString("cd_svrip"));
				rst.put("cd_portno", rs.getString("cd_portno"));
				rst.put("cd_svrname", rs.getString("cd_svrname"));
				rst.put("cd_rsrcname", rs.getString("cd_rsrcname"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				if(rs.getString("cd_diffdt")!= null){
					rst.put("cd_diffdt", rs.getString("cd_diffdt").substring(0, 4)+"/"+rs.getString("cd_diffdt").subSequence(4, 6)+"/"+rs.getString("cd_diffdt").subSequence(6, 8));
				}else{
					rst.put("cd_diffdt", "");
				}
				if(rs.getString("cd_errclsdt")!= null){
					rst.put("cd_errclsdt", rs.getString("cd_errclsdt").substring(0, 4)+"/"+rs.getString("cd_errclsdt").subSequence(4, 6)+"/"+rs.getString("cd_errclsdt").subSequence(6, 8));
				}else{
					rst.put("cd_errclsdt", "");
				}
				
				rst.put("cd_errdaycnt", rs.getString("cd_errdaycnt"));
				rst.put("cd_sayu", rs.getString("cd_sayu"));
				rst.put("owner_nm", rs.getString("owner_nm"));
				rst.put("owner_id", rs.getString("owner_id"));
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rsval.clear();

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## cmp2900.get_get_Result() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## cmp2900.get_Result() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## cmp2900.get_Result() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## cmp2900.get_Result() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rsval != null)
				rsval = null;
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## cmp2900.get_Result() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}// end of get_Result() method statement
	
	public String CmdInsert(ArrayList<HashMap<String,String>> dataList) throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer strQuery = new StringBuffer();
		int cnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			for (int i=0 ; i<dataList.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("UPDATE  CMD0029 	\n");
				strQuery.append("SET  CD_SAYU  = ? 	\n");
				strQuery.append("WHERE CD_SYSCD= ?	\n");
				if(dataList.get(i).get("cd_dsncd").length()==0){
					strQuery.append("AND CD_DIRPATH=?\n");
				}else{
					strQuery.append("AND CD_dsncd=?  \n");
				}
		        strQuery.append("   AND CD_DIFFDT=?  \n");
	       		strQuery.append("   AND CD_RSRCNAME=?\n");
				strQuery.append("   AND CD_SVRIP=? 	 \n");
				strQuery.append("   AND CD_PORTNO=?  \n");
				strQuery.append("   AND CD_SVRCD=? 	 \n");
									
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());

				cnt = 0;	
				pstmt.setString(++cnt, dataList.get(i).get("cd_sayu"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_syscd"));
				if(dataList.get(i).get("cd_dsncd").length()==0){
					pstmt.setString(++cnt, dataList.get(i).get("cm_dirpath"));
				}else{
					pstmt.setString(++cnt, dataList.get(i).get("cd_dsncd"));
				}
				pstmt.setString(++cnt, dataList.get(i).get("cd_diffdt").replaceAll("/", ""));
				pstmt.setString(++cnt, dataList.get(i).get("cd_rsrcname"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_svrip"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_portno"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_svrcd"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			
			
			conn.close();

			pstmt = null;
			conn = null;

			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CMP2900.CmdInsert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CMP2900.CmdInsert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CMP2900.CmdInsert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CMP2900.CmdInsert() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## CMP2900.CmdInsert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String CmdDelete(ArrayList<HashMap<String,String>> dataList) throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer strQuery = new StringBuffer();
		int cnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			
			for (int i=0 ; i<dataList.size() ; i++){
	        	strQuery.setLength(0);
	        	strQuery.append("delete cmd0029       \n");
	        	strQuery.append(" where cd_diffdt=?   \n");
	        	strQuery.append("   and cd_svrip=?    \n");
	        	strQuery.append("   and cd_portno=?   \n");
	        	strQuery.append("   and cd_syscd=?    \n");
	        	strQuery.append("   and cd_rsrcname=? \n");	
	        	strQuery.append("   and cd_svrcd=?    \n");	        	
	        	pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				cnt =0;
	        	pstmt.setString(++cnt, dataList.get(i).get("cd_diffdt").replaceAll("/", ""));
				pstmt.setString(++cnt, dataList.get(i).get("cd_svrip"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_portno"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_syscd"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_rsrcname"));
				pstmt.setString(++cnt, dataList.get(i).get("cd_svrcd"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());				
				pstmt.executeUpdate();
	        	pstmt.close();
			}
			
			pstmt.close();
			conn.close();

			pstmt = null;
			conn = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CMP2900.CmdInsert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CMP2900.CmdInsert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CMP2900.CmdInsert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CMP2900.CmdInsert() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## CMP2900.CmdInsert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public String CmdMailSend(ArrayList<HashMap<String,String>> dataList, String userid) throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer strQuery = new StringBuffer();
		Object[] rtObj = null;
		int cnt = 0;
		String	strErMsg    = "";		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			for (int i=0 ; i<dataList.size() ; i++){
				cnt =0;	
				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMR9910  	\n");
				strQuery.append(" (cr_conusr, cr_senddate, cr_status, cr_acptno, cr_sendusr,cr_sgnmsg,cr_title,cr_linkurl)  \n");
		        strQuery.append("  values	                                                                                \n");
	       		strQuery.append(" (  ?, sysdate,'0','MAILFILEDIFF',?,'filediff Mail send 용','filediff Mail send 용','') 	        \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++cnt, dataList.get(i).get("owner_id"));
				pstmt.setString(++cnt, userid);				
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
			}
			conn.close();
			pstmt = null;
			conn = null;
			
        	if (strErMsg.length()==0) strErMsg = "OK";
			
			return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## CMP2900.CmdInsert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## CMP2900.CmdInsert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## CMP2900.CmdInsert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## CMP2900.CmdInsert() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rtObj != null)
				rtObj = null;
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## CMP2900.CmdInsert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}	
}
