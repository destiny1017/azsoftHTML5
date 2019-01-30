
/*****************************************************************************************
	1. program ID	: BlankCd.java
	2. create date	: 2008.05. 16
	3. auth		    : k.m.s
	4. update date	: 
	5. auth		    : 
	6. description	: Time Check 
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

public class BlankCd{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 요일별 결재시간을 조회합니다.
	 * @param  
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public ArrayList<HashMap<String, String>> SelectBlank(String SgnCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;		
		
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select cm_micode,cm_codename from cmm0020            \n");	
			strQuery.append(" where cm_macode='SGNCD'  				              \n");	
			if (!SgnCd.equals("6")) {
				strQuery.append(" and cm_micode=?  				                  \n");	
			} else {
				strQuery.append(" and cm_micode in ('3','4')		              \n");	
			}
			strQuery.append("   and cm_micode<>'****'  				              \n");
			strQuery.append("   and cm_closedt is null 				              \n");	
			strQuery.append(" order by cm_micode    				              \n");	
			
			pstmt = conn.prepareStatement(strQuery.toString());	
			if (!SgnCd.equals("6")) pstmt.setString(1, SgnCd); 
			
            rs = pstmt.executeQuery();
            rsval.clear();
            
			while (rs.next()){
				if (rs.getRow()==1) {
					rst = new HashMap<String, String>();
					rst.put("ID", "0");
					rst.put("cm_micode", "00");
					rst.put("cm_codename", "선택하세요");
					rsval.add(rst);	
					rst = null;
				}
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
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return rsval;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## BlankCd.SelectBlank() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## BlankCd.SelectBlank() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## BlankCd.SelectBlank() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## BlankCd.SelectBlank() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## BlankCd.SelectBlank() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
				
	}//end of SelectBlank() method statement	
	
}//end of BlankCd class statement
