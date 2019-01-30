package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import app.common.LoggableStatement;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0100_selectVer {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getVerList(String itemID,String ReqCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;


		try {
			conn = connectionContext.getConnection();

			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi')  as acptdate,\n");
			strQuery.append("       a.cr_passcd as passcd,a.cr_itsmid,              \n");
			strQuery.append("       c.cm_username,d.cr_ver,                         \n");
			strQuery.append("       a.cr_acptno,b.cr_prcdate,d.cr_acptno baseacpt   \n");
			strQuery.append("  from cmm0040 c, cmr1010 b, cmr1000 a,cmr0025 d       \n");
			if (ReqCD.equals("06")) {
				strQuery.append(" where b.cr_itemid=?                               \n");
				strQuery.append("   and b.cr_status<>'3'                            \n");
				strQuery.append("   and b.cr_prcdate is not null                    \n");
				strQuery.append("   and b.cr_acptno=a.cr_acptno                     \n");
				strQuery.append("   and a.cr_qrycd='04'                             \n");
				strQuery.append("   and a.cr_status<>'3'                            \n");
				strQuery.append("   and b.cr_itemid=d.cr_itemid                     \n");
				strQuery.append("   and b.cr_befver=d.cr_ver                        \n");
				strQuery.append("   and a.cr_acptdate>d.cr_prcdate                  \n");
			} else {
				strQuery.append(" where d.cr_itemid = ?                             \n");
				strQuery.append("   and d.cr_acptno = b.cr_acptno                   \n");
				strQuery.append("   and d.cr_itemid=b.cr_itemid                     \n");
				strQuery.append("   and b.cr_acptno=a.cr_acptno                     \n");
			}
			strQuery.append("   and a.cr_editor = c.cm_userid                       \n");
			strQuery.append(" order by acptdate desc                                \n");


            //pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());

			pstmt.setString(pstmtcount++, itemID);

			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				if (rs.getRow()>1) {
					rst = new HashMap<String,String>();
					//rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("acptdate", rs.getString("acptdate"));
					rst.put("prcdate", rs.getString("cr_prcdate"));
					rst.put("passcd", rs.getString("passcd"));
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("cr_ver",rs.getString("cr_ver"));
					rst.put("cr_acptno",rs.getString("cr_acptno"));
					rst.put("baseacpt",rs.getString("baseacpt"));
					rst.put("srid",rs.getString("cr_itsmid"));
					rst.put("acptno",rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6,12));
					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			rtObj =  rtList.toArray();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100_selectVer.getVerList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100_selectVer.getVerList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100_selectVer.getVerList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100_selectVer.getVerList() Exception END ##");
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
					ecamsLogger.error("## Cmr0100_selectVer.getVerList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getRollVerList(String itemID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;


		try {
			conn = connectionContext.getConnection();

			strQuery.append("select to_char(a.cr_acptdate,'yyyy-mm-dd hh24:mi') acptdate,\n");
			strQuery.append("       a.cr_passcd as passcd,                               \n");
			strQuery.append("       to_char(b.cr_prcdate,'yyyy-mm-dd hh24:mi') prcdate,  \n");
			strQuery.append("       c.cm_username,d.cr_ver,d.cr_acptno                   \n");
			strQuery.append("  from cmm0040 c,cmr1010 b,cmr1000 a,cmr0021 d 			 \n");
			strQuery.append(" where d.cr_itemid=?										 \n");
			strQuery.append("   and d.cr_acptno=b.cr_acptno 						 	 \n");
			strQuery.append("   and d.cr_itemid=b.cr_itemid 							 \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno  							 \n");
			strQuery.append("   and a.cr_editor=c.cm_userid 							 \n");
			strQuery.append("order by a.cr_acptdate desc,b.cr_prcdate desc \n");


            pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(pstmtcount++, itemID);

            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				if (rs.getRow() > 1) {
					if (rs.getRow() > 10) break;

					rst = new HashMap<String,String>();
					//rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("acptdate", rs.getString("acptdate"));
					rst.put("passcd", rs.getString("passcd"));
					rst.put("prcdate",rs.getString("prcdate"));
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("cr_ver",rs.getString("cr_ver"));
					rst.put("cr_acptno",rs.getString("cr_acptno"));
					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rtList.toArray();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100_selectVer.getRollVerList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100_selectVer.getRollVerList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100_selectVer.getRollVerList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100_selectVer.getRollVerList() Exception END ##");
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
					ecamsLogger.error("## Cmr0100_selectVer.getRollVerList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
