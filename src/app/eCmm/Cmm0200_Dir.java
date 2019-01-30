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
public class Cmm0200_Dir{


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
    public Object[] getDirList(String SysCd) throws SQLException, Exception {
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
			strQuery.append("select a.cm_dirpath,a.cm_svrip,a.cm_port,a.cm_svrusr, \n");
			strQuery.append("       a.cm_svrpass,a.cm_shell,a.cm_dircd,            \n");;
			strQuery.append("       b.cm_codename                                  \n");
			strQuery.append("  from cmm0020 b,cmm0039 a                            \n");
			strQuery.append(" where a.cm_syscd=?                                   \n");
			strQuery.append("   and b.cm_macode='SYSDIR' and b.cm_micode=a.cm_dircd\n");
			strQuery.append(" order by b.cm_codename                                  \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_port", Integer.toString(rs.getInt("cm_port")));
				rst.put("cm_svrusr", rs.getString("cm_svrusr"));
				if (rs.getString("cm_svrpass") != null) rst.put("cm_svrpass", rs.getString("cm_svrpass"));
				if (rs.getString("cm_shell") != null) rst.put("cm_shell", rs.getString("cm_shell"));
				rst.put("cm_dircd", rs.getString("cm_dircd"));
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
			ecamsLogger.error("## Cmm0200_Dir.getDirList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Dir.getDirList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Dir.getDirList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Dir.getDirList() Exception END ##");
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
					ecamsLogger.error("## Cmm0200_Dir.getDirList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
    public String dirInfo_Ins(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("delete cmm0039                      \n");
			strQuery.append(" where cm_syscd=? and cm_dircd=?    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_dircd"));
			pstmt.executeUpdate();

			strQuery.setLength(0);
			parmCnt = 0;
			strQuery.append("insert into cmm0039                     \n");
			strQuery.append("   (CM_SYSCD,CM_DIRCD,CM_DIRPATH,       \n");
			strQuery.append("    CM_SVRIP,CM_PORT,CM_SHELL,          \n");
			strQuery.append("    CM_SVRUSR,CM_SVRPASS)               \n");
			strQuery.append("values                                  \n");
			strQuery.append("(?, ?, ?, ?, ?, ?, ?, ?)                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
			pstmt.setString(++parmCnt, etcData.get("cm_dircd"));
			pstmt.setString(++parmCnt, etcData.get("cm_dirpath"));
			pstmt.setString(++parmCnt, etcData.get("cm_svrip"));
			pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_port")));
			pstmt.setString(++parmCnt, etcData.get("cm_shell"));
			pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
			pstmt.setString(++parmCnt, etcData.get("cm_svrpass"));

			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Ins() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Ins() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Ins() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Ins() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Dir.dirInfo_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

    public String dirInfo_Close(String SysCd,String DirCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("delete cmm0039                      \n");
			strQuery.append(" where cm_syscd=? and cm_dircd=?    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, DirCd);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Close() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Close() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0200_Dir.dirInfo_Close() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Dir.dirInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
}//end of Cmm0200_Dir class statement
