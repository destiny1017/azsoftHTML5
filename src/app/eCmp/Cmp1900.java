/*****************************************************************************************
	1. program ID	: Cmp1900.java
	2. create date	: 2012. 01. 10
	3. auth		    : no name
	4. update date	:
	5. auth		    :
	6. description	: 보고서1->개인별처리현황
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

//import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author UBIQUITOUS
 *
 */
public class Cmp1900 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/** 보고서1->개인별처리현황 조회 쿼리
	 * @param Sdate 기안시작일자 20120101
	 * @param Edate 기안종료일자 20120101
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getList(HashMap<String,String> etcInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			UserInfo userinfo = new UserInfo();
			boolean adminSw = userinfo.isAdmin_conn(etcInfo.get("userid"), conn);
			String strName = "";
			if (adminSw == false) {
				strName = userinfo.getUserName(conn, etcInfo.get("userid"));
			}
			strQuery.setLength(0);
		        strQuery.append("SELECT  reqdt,docno,tit,reqmn,trim(impvfrctdt) impvfrctdt,trim(impvdt) impvdt,impvdocno    \n");
		        strQuery.append("  FROM  KPCOMPRCREQDOC \n");
		        strQuery.append(" WHERE  ReqDt BETWEEN ? AND ? \n");
		        strQuery.append("   AND  DOCGB = '2'           \n");
		        strQuery.append("   AND  PRCGB = '데이터변경'   \n");
		        if (!etcInfo.get("gubun").equals("0")) {
		        	strQuery.append(" and (TRIM(IMPVFRCTDT) IS NOT NULL OR TRIM(IMPVFRCTDT) != '') \n");
		        	if (etcInfo.get("reelection").equals("1")) {
		        		strQuery.append(" and (TRIM(IMPVDT) IS     NULL OR TRIM(IMPVDT) = '') \n");
		        	} else if (etcInfo.get("reelection").equals("2")) {
		        		strQuery.append(" and (TRIM(IMPVDT) IS NOT NULL OR TRIM(IMPVDT) != '') \n");
		        	}
		        }
		        if (etcInfo.get("search") != null && etcInfo.get("search") != "") {
		        	strQuery.append(" and  (   REQDT      LIKE ?          \n");
		        	strQuery.append("       OR DOCNO      LIKE ?          \n");
		        	strQuery.append("       OR TIT        LIKE ?          \n");
		        	strQuery.append("       OR REQMN      LIKE ?          \n");
		        	strQuery.append("       OR IMPVFRCTDT LIKE ?          \n");
		        	strQuery.append("       OR IMPVDT     LIKE ?          \n");
		        	strQuery.append("       OR IMPVDOCNO  LIKE ?)         \n");
		        }
		        strQuery.append(" ORDER  BY REQDT, DOCNO                  \n");

		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
		        pstmt.setString(1, etcInfo.get("datStD"));
		        pstmt.setString(2, etcInfo.get("datEdD"));
		        if (etcInfo.get("search") != null && etcInfo.get("search") != "") {
		        	pstmt.setString(3, "%"+etcInfo.get("search")+"%");
		        	pstmt.setString(4, "%"+etcInfo.get("search")+"%");
		        	pstmt.setString(5, "%"+etcInfo.get("search")+"%");
		        	pstmt.setString(6, "%"+etcInfo.get("search")+"%");
		        	pstmt.setString(7, "%"+etcInfo.get("search")+"%");
		        	pstmt.setString(8, "%"+etcInfo.get("search")+"%");
		        	pstmt.setString(9, "%"+etcInfo.get("search")+"%");
		        }
		        rs = pstmt.executeQuery();
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rsval.clear();

				while (rs.next()){
					rst = new HashMap<String, String>();
	        		rst.put("reqdt",  rs.getString("reqdt").substring(0,4)+"-"+rs.getString("reqdt").substring(4,6)+"-"+rs.getString("reqdt").substring(6,8));
	        		rst.put("docno",rs.getString("docno"));
	        		rst.put("tit",rs.getString("tit"));
	        		rst.put("reqmn",rs.getString("reqmn"));
	        		if (rs.getString("impvfrctdt") != null)
	        			rst.put("impvfrctdt",  rs.getString("impvfrctdt").substring(0,4)+"-"+rs.getString("impvfrctdt").substring(4,6)+"-"+rs.getString("impvfrctdt").substring(6,8));
	        		else
	        			rst.put("impvfrctdt","");
	        		if (rs.getString("impvdt") != null)
	        			rst.put("impvdt",  rs.getString("impvdt").substring(0,4)+"-"+rs.getString("impvdt").substring(4,6)+"-"+rs.getString("impvdt").substring(6,8));
	        		else
	        			rst.put("impvdt","");
	        		if (rs.getString("impvdocno") != null)
	        			rst.put("impvdocno",rs.getString("impvdocno"));
	        		else
	        			rst.put("impvdocno","");
	        		if (rs.getString("impvfrctdt") != null && rs.getString("impvfrctdt") != "") {
		        		if (adminSw == true) rst.put("editsw", "Y");
		        		else {
		        			if (rs.getString("reqmn").equals(strName)) rst.put("editsw", "Y");
		        			else rst.put("editsw", "N");
		        		}
	        		} else rst.put("editsw", "N");
					rsval.add(rst);
					rst = null;
				}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp1900.getList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1900.getList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1900.getList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1900.getList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1900.getList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getList() method statement

	public String updtDoc(HashMap<String,String> etcInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
	        strQuery.append("update  KPCOMPRCREQDOC    \n");
	        strQuery.append("   set  ImpvDt=?,         \n");
	        strQuery.append("        ImpvDocNo=?       \n");
	        strQuery.append(" WHERE  DOCNO=?           \n");
	        strQuery.append("   AND  REQDT=?           \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, etcInfo.get("impvdt").replaceAll("-", ""));
	        pstmt.setString(2, etcInfo.get("impvdocno"));
	        pstmt.setString(3, etcInfo.get("docno"));
	        pstmt.setString(4, etcInfo.get("reqdt").replaceAll("-", ""));
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			pstmt = null;
			conn = null;

			return "Y";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp1900.updtDoc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1900.updtDoc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1900.updtDoc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1900.updtDoc() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1900.updtDoc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtDoc() method statement
}//end of Cmp1900 class statement