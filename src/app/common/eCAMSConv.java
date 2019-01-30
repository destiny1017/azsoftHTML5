
/*****************************************************************************************
	1. program ID	: eCAMSConv.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: eCAMSConv
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
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


public class eCAMSConv{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */

	public ArrayList<HashMap<String, String>> getTblList() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		
		ConnectionContext connectionContext = new ConnectionResource(false,"OLDECAMS");
		try {
			conn = connectionContext.getConnection();
		
			strQuery.append("select table_name,comments from user_tab_comments \n");
            pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            
            rs = pstmt.executeQuery();
            
            rtList.clear();
            
			while (rs.next()){			
				rst = new HashMap<String, String>();
				rst.put("table_name", rs.getString("table_name"));
				rst.put("comments", rs.getString("comments"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return rtList;			

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## eCAMSConv.getTblList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## eCAMSConv.getTblList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## eCAMSConv.getTblList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## eCAMSConv.getTblList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## eCAMSConv.getTblList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		
	}//end of geteCAMSConv() method statement
	
}//end of eCAMSConv class statement
