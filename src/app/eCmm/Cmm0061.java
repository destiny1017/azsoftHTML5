
/*****************************************************************************************
	1. program ID	: Cmm0061.java
	2. create date	: 
	3. auth		    : 
	4. update date	: 
	5. auth		    : 
	6. description	: Time Check 
*****************************************************************************************/

package app.eCmm;

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

public class Cmm0061{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * @param  
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	
    public Object[] get_SelectTimeSch(String rgtcd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		rtObj		  = null;			
		StringBuffer    strQuery      = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select cm_comsttime,cm_comedtime,cm_wedsttime, \n");
			strQuery.append(" cm_wededtime,cm_holsttime,cm_holedtime from cmm0061 \n");
			strQuery.append(" where cm_rgtcd=? \n"); //rgtcd
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, rgtcd);
            rs = pstmt.executeQuery();            
            rtList.clear();            
			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", "Time_select");
				rst.put("ComSTTime" , rs.getString("cm_comsttime"));
				rst.put("ComEDTime" , rs.getString("cm_comedtime"));
				rst.put("WedSTTime" , rs.getString("cm_wedsttime"));
				rst.put("WedEDTime" , rs.getString("cm_wededtime"));
				rst.put("HolSTTime" , rs.getString("cm_holsttime"));
				rst.put("HolEDTime" , rs.getString("cm_holedtime"));
				rtList.add(rst);
				rst = null;
			}
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
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();				
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
					ex3.printStackTrace();
				}
			}
		}
	}//end of get_SelectTimeSch() method statement
    
    public int get_Update_time(String ComSTTime, String ComEDTime, String WedSTTime, String WedEDTime, String HolSTTime, String HolEDTime,String rgtcd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;

		StringBuffer    strQuery      = new StringBuffer();
		int				  Cnt         = 0;
        int               rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.append(" update cmm0061 set																					\n");
			strQuery.append(" cm_comsttime = ?,cm_comedtime = ?,cm_wedsttime = ?,cm_wededtime = ?,cm_holsttime = ?,cm_holedtime = ? \n");
			strQuery.append(" where cm_rgtcd=?  																					\n");	
			
			pstmt = conn.prepareStatement(strQuery.toString());
			
			pstmt.setString(++Cnt, ComSTTime);
			pstmt.setString(++Cnt, ComEDTime);
			pstmt.setString(++Cnt, WedSTTime);
			pstmt.setString(++Cnt, WedEDTime);
			pstmt.setString(++Cnt, HolSTTime);
			pstmt.setString(++Cnt, HolEDTime);
			pstmt.setString(++Cnt, rgtcd);
            
        	rtn_cnt = pstmt.executeUpdate();
        	pstmt.close();
        	conn.commit();
        	conn.close();
			conn = null;
			pstmt = null;

    		return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of get_Update_time() method statement
    
}//end of Cmm0061 class statement
