/*****************************************************************************************
	1. program ID	: Cmr0200.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import app.common.LoggableStatement;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmm0200_Item{


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
    public Object[] getItemList(String SysCd) throws SQLException, Exception {
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
			strQuery.setLength(0);
			strQuery.append("select a.cm_svrname,a.cm_svrip,a.cm_svrcd,a.cm_seqno, \n");
			strQuery.append("       a.cm_portno,b.cm_codename svrcd,c.cm_codename, \n");
			strQuery.append("       d.cm_rsrccd,d.cm_volpath                       \n");
			strQuery.append("  from cmm0020 c,cmm0020 b,cmm0031 a,cmm0038 d        \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='SERVERCD'                         \n");
			strQuery.append("   and b.cm_micode=a.cm_svrcd                         \n");
			strQuery.append("   and a.cm_syscd=d.cm_syscd and a.cm_svrcd=d.cm_svrcd\n");
			strQuery.append("   and a.cm_seqno=d.cm_seqno                          \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=d.cm_rsrccd\n");
			strQuery.append(" order by a.cm_svrcd                                  \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("rsrccd", rs.getString("cm_codename"));
				rst.put("svrcd", rs.getString("svrcd"));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_portno", Integer.toString(rs.getInt("cm_portno")));
				if (rs.getString("cm_volpath") != null) rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_seqno", Integer.toString(rs.getInt("cm_seqno")));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;

			rs = null;


			returnObjectArray = rsval.toArray();
			rsval = null;
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.getItemList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Item.getItemList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.getItemList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Item.getItemList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)  returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Item.getItemList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
    public String itemInfo_Ins(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		int               i           = 0;
		int               j           = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			String[] strSvrCd = etcData.get("cm_seqno").split(",");
			String[] strRsrcCd = etcData.get("cm_rsrccd").split(",");

			for (i=0;strSvrCd.length>i;i++) {
				for (j=0;strRsrcCd.length>j;j++) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmm0038        \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
					strQuery.append("   and cm_seqno=? and cm_rsrccd=?       \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, etcData.get("cm_svrcd"));
					pstmt.setInt(3, Integer.parseInt(strSvrCd[i]));
					pstmt.setString(4, strRsrcCd[j]);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						strQuery.setLength(0);
						strQuery.append("delete cmm0038                      \n");
						strQuery.append(" where cm_syscd=? and cm_svrcd=?    \n");
						strQuery.append("   and cm_seqno=? and cm_rsrccd=?   \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, etcData.get("cm_syscd"));
						pstmt.setString(2, etcData.get("cm_svrcd"));
						pstmt.setInt(3, Integer.parseInt(strSvrCd[i]));
						pstmt.setString(4, strRsrcCd[j]);
						pstmt.executeUpdate();
					}
					rs.close();
					pstmt.close();

					strQuery.setLength(0);
					parmCnt = 0;
					strQuery.append("insert into cmm0038                     \n");
					strQuery.append("   (CM_SYSCD,CM_SVRCD,CM_SEQNO,         \n");
					strQuery.append("    CM_RSRCCD,CM_VOLPATH)               \n");
					strQuery.append("values                                  \n");
					strQuery.append("(?, ?, ?, ?, ?)                         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setInt(++parmCnt, Integer.parseInt(strSvrCd[i]));
					pstmt.setString(++parmCnt, strRsrcCd[j]);
					pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
					////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Ins() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Ins() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Ins() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Ins() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Item.itemInfo_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

    public String itemInfo_Close(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               i           = 0;
		int               j           = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			String[] strSvrCd = etcData.get("cm_seqno").split(",");
			String[] strRsrcCd = etcData.get("cm_rsrccd").split(",");

			for (i=0;strSvrCd.length>i;i++) {
				for (j=0;strRsrcCd.length>j;j++) {
					strQuery.setLength(0);
					strQuery.append("delete cmm0038                      \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?    \n");
					strQuery.append("   and cm_seqno=? and cm_rsrccd=?   \n");
					pstmt = conn.prepareStatement(strQuery.toString());
			//		pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, etcData.get("cm_svrcd"));
					pstmt.setInt(3, Integer.parseInt(strSvrCd[i]));
					pstmt.setString(4, strRsrcCd[j]);
				//	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Close() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Close() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Item.itemInfo_Close() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Item.itemInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

    public Object[] getSvrList(String SysCd) throws SQLException, Exception {
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
			strQuery.append("select b.cm_micode,b.cm_codename                      \n");
			strQuery.append("  from cmm0031 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='SERVERCD'                         \n");
			strQuery.append("   and b.cm_micode=a.cm_svrcd                         \n");
			strQuery.append("group by b.cm_micode,b.cm_codename                    \n");
			strQuery.append("order by b.cm_micode                                  \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			returnObjectArray = rsval.toArray();
			rsval = null;
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.getSvrList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Item.getSvrList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Item.getSvrList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Item.getSvrList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)  returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Item.getSvrList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
}//end of Cmm0200_Item class statement
