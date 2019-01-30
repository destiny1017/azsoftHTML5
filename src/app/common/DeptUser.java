
/*****************************************************************************************
	1. program ID	: DeptUser.java
	2. create date	: 2008.04. 17
	3. author       : ms.kang
	4. update date	: 
	5. description	: 1. 프로젝트목록
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
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeptUser{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * @param  user_id,QryCd,AdminYn,EndYn
	 *         사용자,화면구분,관리자여부,종료건포함여부
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
    public ArrayList<HashMap<String, String>> selectDeptUser(String DeptCd,String DeptName,String UnderYn,String UserName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
        Integer           Cnt         = 0;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;	
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
		    strQuery.append("select A.CM_USERID,A.CM_PROJECT,A.CM_USERNAME,A.CM_DUTY,A.CM_POSITION,     \n");
		    strQuery.append("       B.CM_POSNAME POSITION,B.CM_SEQNO,C.CM_DEPTNAME                      \n");
		    strQuery.append("  FROM cmm0040 A,CMM0103 B,CMM0100 C                                       \n");
		    strQuery.append(" WHERE A.CM_ACTIVE = '1'                                                   \n");
		    if (UserName != null && UserName.length() > 0)
		    	strQuery.append("   AND A.CM_USERNAME LIKE '%?%'                                        \n");
		    if (DeptCd != null && DeptCd.length() > 0) 
		    	strQuery.append("   and A.cm_project=?                                                  \n");
		    if (DeptName != null && DeptName.length() > 0) {  
		       strQuery.append("   and A.cm_project in (select cm_deptcd from cmm0100                   \n");
			   strQuery.append("                         where  cm_useyn='Y'                            \n");
			   strQuery.append("                           AND  cm_DEPTNAME LIKE '%?%')                 \n");
		    }
		    strQuery.append("   and A.cm_project in (select cm_deptcd from cmm0100 where  cm_useyn='Y') \n");
		    strQuery.append("   AND A.CM_POSITION=B.CM_POSCD                                            \n");
		    strQuery.append("   AND A.CM_PROJECT=C.CM_DEPTCD                                            \n");
		    strQuery.append(" ORDER BY B.CM_SEQNO                                                       \n");
            pstmt = conn.prepareStatement(strQuery.toString());	
            if (UserName != null && UserName.length() > 0)
            	pstmt.setString(++Cnt, UserName);
		    if (DeptCd != null && DeptCd.length() > 0) 
		    	pstmt.setString(++Cnt, DeptCd);
		    if (DeptName != null && DeptName.length() > 0) {  
		    	pstmt.setString(++Cnt, DeptName);
		    }		    
            rs = pstmt.executeQuery();
            rsval.clear();
            
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("rows", Integer.toString(rs.getRow()));
				rst.put("userid", rs.getString("cm_userid"));
				rst.put("deptcd", rs.getString("cm_project"));
				rst.put("username", rs.getString("cm_username"));
				rst.put("duty", rs.getString("cm_duty"));
				rst.put("position", rs.getString("cm_position"));
				rst.put("posname", rs.getString("position"));
				rst.put("deptname", rs.getString("cm_deptname"));
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
			ecamsLogger.error("## DeptUser.selectDeptUser() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## DeptUser.selectDeptUser() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## DeptUser.selectDeptUser() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## DeptUser.selectDeptUser() Exception END ##");				
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
					ecamsLogger.error("## DeptUser.selectDeptUser() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		
	}//end of selectDeptUser() method statement
	
}//end of DeptUser class statement
