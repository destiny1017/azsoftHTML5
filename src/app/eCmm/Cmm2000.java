package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

//import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmm2000 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	public Object[] getFileList(String UserId,String SysCd,String DsnCd,String RsrcCd,String RsrcName,boolean UpLowSw,boolean gbnSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		//ArrayList<HashMap<String, String>>		  svrList	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		//String            strDevHome = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			int				  pstmtcount  = 1;
			String			  szDsnCD = "";
			String			  strRsrcCd = "";
			String            strRsrc[] = null;
			int               i = 0;

			if (RsrcName == null || RsrcName.equals(""))
				RsrcName = "%%";

			if (!DsnCd.equals("")){
				if (DsnCd.length() == 7 && DsnCd.indexOf("/") < 0){
					szDsnCD = DsnCd;
				}
			}
			if (RsrcCd == null || RsrcCd == "") {
				strQuery.setLength(0);
				strQuery.append("select cm_rsrccd from cmm0036                     \n");
				strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
				strQuery.append("   and substr(cm_info, 2, 1)='1'                  \n");
				strQuery.append("   and substr(cm_info, 26, 1)='0'                 \n");
				strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
				strQuery.append("                           from cmm0037           \n");
				strQuery.append("                          where cm_syscd=?        \n");
				strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, SysCd);
	            pstmt.setString(2, SysCd);

	            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	            	if (strRsrcCd != "") strRsrcCd = strRsrcCd + ",";
	            	strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd");
	            }
				rs.close();
				pstmt.close();
				strRsrc = strRsrcCd.split(",");
			}

			strQuery.setLength(0);
			strQuery.append("select /*+ ALL ROWS */                                    \n");
			strQuery.append("       a.cr_rsrcname,a.cr_rsrccd,a.cr_lstver,             \n");
			strQuery.append("       TO_CHAR(a.CR_LASTDATE,'yyyy-mm-dd') as lastdt,     \n");
			strQuery.append("       a.cr_syscd,a.cr_dsncd,a.cr_itemid,a.cr_status,     \n");
			strQuery.append("       c.CM_CODENAME as codename,                         \n");
			strQuery.append("       d.cm_username,e.cm_dirpath, f.CM_CODENAME as jawon,\n");
			strQuery.append("       g.cm_jobname                                       \n");
			strQuery.append("  from cmm0020 f,cmm0070 e,cmm0040 d,cmm0020 c,           \n");
			strQuery.append("       cmr0020 a ,cmm0102 g                               \n");
			strQuery.append("where a.cr_syscd=? and a.cr_status not in ('9','C')       \n");
            if (!RsrcCd.equals("")){
				strQuery.append(" and a.cr_rsrccd=?                                    \n");
			}
			if (!RsrcName.equals("")){
				if (!RsrcName.equals("*")){
					if (RsrcName.indexOf(",") >= 0){
						strQuery.append("and a.cr_dsncd= ? \n");
						if (UpLowSw == true) strQuery.append("and a.cr_rsrcname like ?  \n");
						else strQuery.append("and upper(a.cr_rsrcname) like upper(?)    \n");
					}
					else{
						if (UpLowSw == true) strQuery.append("and a.cr_rsrcname like ?  \n");
						else strQuery.append("and upper(a.cr_rsrcname) like upper(?)    \n");
					}
				}
			}
			strQuery.append("and a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd        \n");
			if (!DsnCd.equals("")){
				if (!DsnCd.substring(0, 1).equals("F")){
					strQuery.append("and a.cr_dsncd= ?                            \n");
				}
				else{
					strQuery.append("and e.cm_dirpath like ?                      \n");
				}
			}
			if (gbnSw == true){
				strQuery.append("and nvl(a.cr_pgmtype,'0')='0'                    \n");
			} else {
				strQuery.append("and nvl(a.cr_pgmtype,'0')='1'                    \n");
			}
			if (RsrcCd == "" || RsrcCd == null) {
				strQuery.append("and a.cr_rsrccd in (");
            	for (i=0;strRsrc.length>i;i++) {
            		if (i == 0) strQuery.append("? ");
            		else strQuery.append(", ? ");
            	}
            	strQuery.append(") ");
        	}
			strQuery.append("and a.cr_editor=d.cm_userid and a.cr_jobcd = g.cm_jobcd    \n");
			strQuery.append("and c.cm_macode='CMR0020' and a.cr_status=c.cm_micode      \n");
			strQuery.append("and f.cm_macode='JAWON' and a.cr_rsrccd=f.cm_micode        \n");
			//strQuery.append("and a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd      \n");

            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(pstmtcount++, SysCd);
            if (!RsrcCd.equals("")) pstmt.setString(pstmtcount++, RsrcCd);
			if (!RsrcName.equals("")){
				if (!RsrcName.equals("*")){
					if (RsrcName.indexOf(",") >= 0){
						pstmt.setString(pstmtcount++, RsrcName.substring(0, 5));
						pstmt.setString(pstmtcount++, RsrcName.substring(6));
					}
					else{
						pstmt.setString(pstmtcount++, "%"+RsrcName.replaceAll("[*]","")+"%");
					}
				}
			}
			if (!DsnCd.equals("")){
				if (!DsnCd.substring(0, 1).equals("F")){
					pstmt.setString(pstmtcount++, szDsnCD);
				}
				else{
					pstmt.setString(pstmtcount++, DsnCd.substring(1)+ "%");
				}
			}
        	if (RsrcCd == "" || RsrcCd == null) {
            	for (i=0;strRsrc.length>i;i++) {
            		pstmt.setString(pstmtcount++, strRsrc[i]);
            	}
        	}

            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("jobname", rs.getString("cm_jobname"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("lastdt",rs.getString("lastdt"));
				rst.put("codename",rs.getString("codename"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("checked","0");
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
			ecamsLogger.error("## Cmm2000.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2000.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2000.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2000.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2000.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String progUpdt(ArrayList<HashMap<String,String>> fileList,boolean gbnSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

	        for (int i=0;i<fileList.size();i++){
	        	strQuery.setLength(0);
	        	strQuery.append("update cmr0020 set cr_pgmtype=?     \n");
	        	strQuery.append(" where cr_itemid = ?                \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	if (gbnSw == true) pstmt.setString(1, "1");
	        	else pstmt.setString(1, "0");
	        	pstmt.setString(2, fileList.get(i).get("cr_itemid"));
	        	pstmt.executeUpdate();
	        }
	        conn.commit();
        	conn.close();

    		pstmt = null;
    		conn = null;


        	return "OK";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2000.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm2000.request_Check_Out() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2000.request_Check_Out() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2000.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm2000.request_Check_Out() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2000.request_Check_Out() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2000.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}

