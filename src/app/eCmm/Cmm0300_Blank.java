/*****************************************************************************************
	1. program ID	: Cmm0300_Blank.java
	2. create date	: 2008.12.08
	3. auth		    : is.choi
	4. update date	: 2008.12.24
	5. auth		    : No Name
	6. description	: [관리자] -> [결재정보] -> [대결가능범위등록]
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
public class Cmm0300_Blank{


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
    public Object[] getBlankList(String GbnCd,String PosCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_gbncd,a.cm_teamck,a.cm_sposition,         \n");
			strQuery.append("       b.cm_codename rpos,c.cm_codename spos          \n");
			strQuery.append("  from cmm0020 c,cmm0020 b,cmm0046 a                  \n");
			strQuery.append(" where b.cm_macode=decode(a.cm_gbncd,'0','POSITION','RGTCD') \n");
			strQuery.append("   and b.cm_micode=a.cm_rposition                     \n");
			strQuery.append("   and c.cm_macode=decode(a.cm_gbncd,'0','POSITION','RGTCD') \n");
			strQuery.append("   and c.cm_micode=a.cm_sposition                     \n");
			if (GbnCd != null && GbnCd != "") strQuery.append("and a.cm_gbncd=?    \n");
			if (PosCd != null && PosCd != "") strQuery.append("and a.cm_rposition=?\n");
			strQuery.append("order by b.cm_codename,c.cm_codename                  \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            if (GbnCd != null && GbnCd != "") pstmt.setString(1, GbnCd);
            if (PosCd != null && PosCd != "") pstmt.setString(2, PosCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_sposition", rs.getString("cm_sposition"));
				if (rs.getString("cm_gbncd").equals("0")) rst.put("gbncd", "직위");
				else rst.put("gbncd", "직무");
				rst.put("cm_teamck", rs.getString("cm_teamck"));
				rst.put("cm_rpos", rs.getString("rpos"));
				rst.put("cm_spos", rs.getString("spos"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Blank.getBlankList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_Blank.getBlankList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Blank.getBlankList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_Blank.getBlankList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300_Blank.getBlankList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String blankUpdt(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();


		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("delete cmm0046 where cm_gbncd=? and cm_rposition=? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_gbncd"));
			pstmt.setString(2, etcData.get("cm_rposition"));
			pstmt.executeUpdate();
			pstmt.close();

			String[] toPos = etcData.get("cm_sposition").split(",");
			int i = 0;
			for (i=0;toPos.length>i;i++) {
				strQuery.setLength(0);
				strQuery.append("insert into cmm0046                       \n");
				strQuery.append("   (cm_rposition,cm_sposition,cm_gbncd,   \n");
				strQuery.append("    cm_teamck) values                     \n");
				strQuery.append("   (?, ?, ?, ?)                           \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_rposition"));
				pstmt.setString(2, toPos[i]);
				pstmt.setString(3, etcData.get("cm_gbncd"));
				pstmt.setString(4, etcData.get("cm_teamck"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Blank.blankUpdt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_Blank.blankUpdt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Blank.blankUpdt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_Blank.blankUpdt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300_Blank.blankUpdt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String blankClose(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			int i = 0;
			String[] toPos = etcData.get("cm_sposition").split(",");
			for (i=0;toPos.length>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0046                       \n");
				strQuery.append(" where cm_rposition=?                \n");
				strQuery.append("   and cm_sposition=?                    \n");
				strQuery.append("   and cm_gbncd=?                    \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, etcData.get("cm_rposition"));
				pstmt.setString(2, toPos[i]);
				pstmt.setString(3, etcData.get("cm_gbncd"));
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Blank.blankClose() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_Blank.blankClose() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Blank.blankClose() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_Blank.blankClose() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300_Blank.blankClose() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

}//end of Cmm0300_Blank class statement
