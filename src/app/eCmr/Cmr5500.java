package app.eCmr;

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

public class Cmr5500 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getFileVer(String ItemID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           errSw       = false;
		String            errMsg      = "";
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String strStatus = "";
			strQuery.setLength(0);
			strQuery.append("select a.cr_lstver,a.cr_status,b.cm_info,a.cr_rsrcname,  \n");
			strQuery.append("       a.cr_acptno,c.cm_dirpath,d.cm_sysmsg,             \n");
			strQuery.append("       to_char(sysdate,'yy/mm/dd hh24:mi') as nowdt      \n");
			strQuery.append("  from cmm0036 b,cmr0020 a,cmm0070 c,cmm0030 d           \n");
			strQuery.append(" where a.cr_itemid= ?                                    \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd \n");
			strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd   \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd                             \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, ItemID);
            rs = pstmt.executeQuery();
            rtList.clear();

			if (rs.next()){
				if (rs.getString("cr_lstver").equals("0") && rs.getString("cr_status").equals("3") && rs.getString("cm_info").substring(44,45).equals("1")){
					errSw = true;
					errMsg = "현재 개발 중인 소스 입니다. 체크인 이후에 소스View를 하실수 있습니다.";
				} else {
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", ItemID);
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
					rst.put("cr_status", rs.getString("cr_status"));
					rtList.add(rst);
					rst = null;
					/*
					if (!rs.getString("cm_info").substring(44,45).equals("1")) {
						rst = new HashMap<String,String>();
						rst.put("cr_itemid", ItemID);
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("cm_dirpath", rs.getString("cm_dirpath"));
						rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
						rst.put("filedt",rs.getString("nowdt"));
						rst.put("cr_acptno","DEV");
						rst.put("cr_ver", "T");
						rst.put("cr_qrycd", "T");
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("labelmsg", "개발서버소스확인");
						rtList.add(rst);
						strStatus = rs.getString("cr_status");
						rst = null;
					}
					if (rs.getString("cr_status").equals("A")) {
						rst = new HashMap<String,String>();
						rst.put("cr_itemid", ItemID);
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("cm_dirpath", rs.getString("cm_dirpath"));
						rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
						rst.put("cr_status", rs.getString("cr_status"));
						if (rs.getString("cr_status").equals("A")){
							rst.put("cr_ver", "");

							strQuery.setLength(0);
							strQuery.append("select a.cr_acptno,b.cr_qrycd,                      \n");
							strQuery.append("       nvl(a.cr_editcon,b.cr_sayu) sayu,            \n");
							strQuery.append("       to_char(b.cr_acptdate,'yy/mm/dd hh24:mi') as nowdt \n");
							strQuery.append("  from cmr1010 a,cmr1000 b                          \n");
							strQuery.append(" where a.cr_itemid=? and a.cr_status='0'            \n");
							strQuery.append("   and a.cr_acptno=b.cr_acptno                      \n");
							strQuery.append("   and b.cr_qrycd='07'                              \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
							//pstmt2 =  new LoggableStatement(conn, strQuery.toString());
				            pstmt2.setString(1, ItemID);
				            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
							if (rs2.next()){
								rst.put("cr_acptno",rs2.getString("cr_acptno"));
								rst.put("cr_qrycd", rs2.getString("cr_qrycd"));
								if (rs2.getString("sayu") != null) rst.put("cr_sayu", rs2.getString("sayu"));
								rst.put("labelmsg", "체크인 중:" + rs2.getString("nowdt")+" / 신청번호:"+
										rs2.getString("cr_acptno").substring(0,4)+"-"+
										rs2.getString("cr_acptno").substring(4,6)+"-"+
										rs2.getString("cr_acptno").substring(6,12));
							}
							rs2.close();
							pstmt2.close();
						}
						rtList.add(rst);
						rst = null;
					}
					*/
				 }
			}
			else{
				errMsg = "해당 자원의 정보가 없습니다1.";
				errSw = true;
			}
			rs.close();
			pstmt.close();

			if (errSw == true) {
				rst = new HashMap<String,String>();
				rst.put("cr_itemid", "ERROR");
				rst.put("cr_sayu", errMsg);
				rtList.add(rst);
				rst = null;
			} else {
				strQuery.setLength(0);
				strQuery.append("SELECT b.cr_acptno,b.cr_ver,b.cr_qrycd,                     \n");
				strQuery.append("       to_char(b.cr_acptdate,'yy/mm/dd hh24:mi') as opendt, \n");
				strQuery.append("       a.cr_sayu                                            \n");
				strQuery.append("  FROM cmr0021 b,cmr1000 a                                  \n");
				strQuery.append(" where b.cr_itemid= ?                                       \n");
				strQuery.append("   and b.cr_acptno=a.cr_acptno                              \n");
				strQuery.append(" order by opendt desc                                       \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, ItemID);
	            rs = pstmt.executeQuery();
	            while(rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", ItemID);
					rst.put("cr_acptno",rs.getString("cr_acptno"));
					rst.put("cr_status", strStatus);
					rst.put("cr_ver", rs.getString("cr_ver"));
					rst.put("cr_qrycd", rs.getString("cr_qrycd"));
					if (rs.getString("cr_sayu") != null) rst.put("cr_sayu", rs.getString("cr_sayu"));
					else rst.put("cr_sayu", "");
					rst.put("labelmsg", "체크인:" +rs.getString("opendt")+" / 버전:"+rs.getString("cr_ver"));

					rtList.add(rst);
					rst = null;
	            }
	            rs.close();
	            pstmt.close();
			}
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
    		return rtList.toArray();

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5500.getFileVer() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5500.getFileVer() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5500.getFileVer() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5500.getFileVer() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5500.getFileVer() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getRelatFile(String ItemID,String AcptNo,String QryCd,boolean chkFg,String Status) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			rtList.clear();
			if (Status.equals("3") && QryCd.equals("T")) {
				strQuery.setLength(0);
				strQuery.append("select a.cr_rsrcname,a.cr_lstver ver,a.cr_itemid,        \n");
				strQuery.append("       b.cm_dirpath,d.cm_info                            \n");
				strQuery.append("  from cmr0020 a,cmm0070 b,cmm0036 d                     \n");
				strQuery.append(" where a.cr_itemid=?                                     \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
				strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, ItemID);
	            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
				while (rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_ver", "DEV");
					rst.put("cm_info", rs.getString("cm_info"));
					rtList.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
			} else if (QryCd.equals("T")) {
				strQuery.setLength(0);
				strQuery.append("select a.cr_rsrcname,a.cr_lstver ver,a.cr_itemid,        \n");
				strQuery.append("       b.cm_dirpath,d.cm_info                            \n");
				strQuery.append("  from cmr0020 a,cmm0070 b,cmm0036 d,cmr1010 e,cmr0021 f,\n");
				strQuery.append("       (select max(cr_prcdate) prcdate from cmr0021      \n");
				strQuery.append("         where cr_itemid=?) g                            \n");
				strQuery.append(" where a.cr_itemid=?                                     \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
				strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd \n");
				strQuery.append("   and a.cr_itemid=f.cr_itemid                           \n");
				strQuery.append("   and f.cr_prcdate=g.prcdate and f.cr_acptno=e.cr_acptno \n");
				strQuery.append("   and f.cr_itemid=e.cr_itemid                           \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, ItemID);
	            pstmt.setString(2, ItemID);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
				while (rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_ver", "DEV");
					rst.put("cm_info", rs.getString("cm_info"));
					rtList.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
			} else {
				strQuery.setLength(0);
				strQuery.append("select cr_status from cmr1010      \n");
				strQuery.append(" where cr_acptno=? and cr_itemid=? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, ItemID);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					Status = rs.getString("cr_status");
				}
				rs.close();
				pstmt.close();

				strQuery.setLength(0);
				strQuery.append("select a.cr_rsrcname,a.cr_version ver,a.cr_itemid,       \n");
				strQuery.append("       b.cm_dirpath,d.cm_info                            \n");
				if (Status.equals("0")) {
					strQuery.append("  from cmr1010 a,cmm0070 b,cmr0027 c,cmm0036 d       \n");
				} else {
					strQuery.append("  from cmr1010 a,cmm0070 b,cmr0025 c,cmm0036 d       \n");
				}
				strQuery.append(" where a.cr_acptno= ? and a.cr_baseitem=?                \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
				strQuery.append("   and a.cr_itemid=c.cr_itemid                           \n");
				strQuery.append("   and a.cr_acptno=c.cr_acptno                           \n");
				strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd \n");
				strQuery.append("   and substr(d.cm_info,10,1)='0'                        \n");
				strQuery.append("   and substr(d.cm_info,27,1)='0'                        \n");
				strQuery.append("   and substr(d.cm_info,12,1)='1'                        \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, AcptNo);
	            pstmt.setString(2, ItemID);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
				while (rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_ver", rs.getString("ver"));
					rst.put("cm_info", rs.getString("cm_info"));
					rtList.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
			}

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtList.toArray();
			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5500.getRelatFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5500.getRelatFile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5500.getRelatFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5500.getRelatFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5500.getRelatFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
}
