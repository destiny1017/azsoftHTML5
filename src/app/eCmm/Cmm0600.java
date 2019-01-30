
/*****************************************************************************************
	1. program ID	: Cmm0600.java
	2. create date	:
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: 메뉴화면
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
//import org.w3c.dom.Document;
//import app.common.CreateXml;
//import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Cmm0600{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/**
	 * 권한에 따른 메뉴를 조회합니다.
	 * @param  String rgtcd
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getRgtMenuList(String rgtcd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_menucd from cmm0090 ");
			strQuery.append("where cm_rgtcd=? ");
			strQuery.append("order by cm_menucd ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, rgtcd);
            rs = pstmt.executeQuery();

            while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", rs.getString("cm_menucd"));
				rst.put("cm_menucd", rs.getString("cm_menucd"));
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
			ecamsLogger.error("## Cmm0600.getRgtMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0600.getRgtMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0600.getRgtMenuList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0600.getRgtMenuList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0600.getRgtMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	/**
	 * 권한리스트 저장
	 * @param  String, String, ArrayList
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String setRgtMenuList(ArrayList<HashMap<String,String>> Lst_Duty,
			ArrayList<HashMap<String,String>> treeMenu) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			conn.setAutoCommit(false);

		    for (int X=0 ; X<Lst_Duty.size() ; X++)
		    {
		    	strQuery.setLength(0);
		    	strQuery.append("delete cmm0090 where cm_rgtcd=? ");
	    		pstmt = conn.prepareStatement(strQuery.toString());
	    		pstmt.setString(1, Lst_Duty.get(X).get("cm_micode"));
	    		pstmt.executeUpdate();
	    		pstmt.close();

	            for (int Y=0 ; Y<treeMenu.size() ; Y++){
	            	strQuery.setLength(0);
	            	strQuery.append("insert into cmm0090 (cm_rgtcd,cm_menucd) values (?,?)");
		    		pstmt = conn.prepareStatement(strQuery.toString());
		    		pstmt.setString(1, Lst_Duty.get(X).get("cm_micode"));
		    		pstmt.setInt(2, Integer.parseInt(treeMenu.get(Y).get("cm_menucd")));
		    		pstmt.executeUpdate();
		    		pstmt.close();
	            }
		    }

		    conn.commit();
		    conn.setAutoCommit(true);

            conn.close();

            strQuery = null;
            pstmt = null;
            conn = null;

			return "";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0600.setRgtMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0600.setRgtMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0600.setRgtMenuList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0600.setRgtMenuList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0600.setRgtMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}//end of Cmm0061 class statement
