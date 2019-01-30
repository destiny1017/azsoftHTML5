/*****************************************************************************************
	1. program ID	: Cmm0130.java
	2. create date	: 2014.07.10
	3. auth		    : NO name
	4. update date	:
	5. auth		    :
	6. description	: [관리자] -> [시스템정보] -> 모니터링체크리스트
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

import app.common.LoggableStatement;

public class Cmm0130{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


    /** 시스템별 등록된 체크리스트 항목 조회
     * @param dataObj
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public Object[] getMonitorValues(HashMap<String,String> dataObj) throws SQLException, Exception {
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("SELECT cm_gbncd,cm_uppgbncd,cm_seq,cm_reqyn \n");
			strQuery.append("  FROM CMM0130 \n");
			strQuery.append(" where cm_syscd = ? \n");
			strQuery.append(" and cm_closedt is null \n");
	        strQuery.append("order by cm_gbncd \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
    		pstmt.setString(1, dataObj.get("cm_syscd"));
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_gbncd", rs.getString("cm_gbncd"));
				rst.put("cm_uppgbncd", rs.getString("cm_uppgbncd"));
				rst.put("cm_seq", rs.getString("cm_seq"));
				if ( rs.getString("cm_reqyn") == null || rs.getString("cm_reqyn") == "" ) {
					rst.put("cm_reqyn", "N");
				} else {
					rst.put("cm_reqyn", rs.getString("cm_reqyn"));
				}
				rsval.add(rst);
				rst = null;
            }
		    rs.close();
            pstmt.close();
            conn.close();
            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;
			rs = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0130.getMonitorValues() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0130.getMonitorValues() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0130.getMonitorValues() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0130.getMonitorValues() Exception END ##");
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
					ecamsLogger.error("## Cmm0130.getMonitorValues() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    
    /** 시스템별 모니터링 체크리스트 항목에 대한 정보(필수여부) 변경 작업
     *  1. 해당 시스템에 GBNCD가 존재하는지 조회
     *  2. 기등록된 정보가 있으면 UPDATE, 없으면 INSERT
     * @param dataObj
     * @return String "OK":정상
     * @throws SQLException
     * @throws Exception
     */
    public String updateItemInfo(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT cm_gbncd     \n");
			strQuery.append("  FROM CMM0130      \n");
			strQuery.append(" where cm_syscd = ? \n");
	        strQuery.append("   and cm_gbncd = ? \n");
            pstmt = conn.prepareStatement(strQuery.toString());
    		pstmt.setString(1, dataObj.get("cm_syscd"));
    		pstmt.setString(2, dataObj.get("cm_gbncd"));
            rs = pstmt.executeQuery();
            strQuery.setLength(0);
            if( rs.next() ){
            	pstmt.close();
    			strQuery.append("UPDATE CMM0130		  \n");
    			strQuery.append("SET    cm_reqyn = ?  \n");
    			strQuery.append("WHERE  cm_syscd = ?  \n");
    			strQuery.append("  and  cm_gbncd = ?  \n");
                pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, dataObj.get("cm_reqyn"));
            	pstmt.setString(2, dataObj.get("cm_syscd"));
            	pstmt.setString(3, dataObj.get("cm_gbncd"));
            } else {
            	pstmt.close();
        		strQuery.append("INSERT INTO CMM0130 (cm_syscd,cm_gbncd,cm_uppgbncd,cm_reqyn,cm_seq,cm_creatdt) \n");
        		strQuery.append("VALUES(?,?,?,?,?,sysdate)\n");
                pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, dataObj.get("cm_syscd"));
            	pstmt.setString(2, dataObj.get("cm_gbncd"));
            	pstmt.setString(3, dataObj.get("cm_uppgbncd"));
            	pstmt.setString(4, dataObj.get("cm_reqyn"));
            	pstmt.setString(5, dataObj.get("cm_seq"));
            }
            pstmt.executeUpdate();
		    rs.close();
            pstmt.close();
            conn.close();
            rs = null;
        	pstmt = null;
        	conn = null;
        	
        	return "OK";
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm0130.updateItemInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0130.updateItemInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm0130.updateItemInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0130.updateItemInfo() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0130.updateItemInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
    
    /** 시스템별 모니터링 체크리스트 셋팅 작업
     * @param chkList : 리스트
     * @param dataObj : 기타정보
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public String setMonitorValues(ArrayList<HashMap<String,String>> chkList,HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		PreparedStatement pstmt3      = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet		  rs		  = null;
		int               i           = 0;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			//CMM0130 테이블에서 있는 시스템 값에 closedt 값에 sysdate 넣기.
			strQuery.setLength(0);
			strQuery.append("UPDATE CMM0130 SET \n");
    		strQuery.append(" cm_closedt = sysdate   \n");
            strQuery.append(" WHERE cm_syscd = ?    \n");
    		//pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());	    
        	pstmt.setString(1, dataObj.get("cm_syscd"));
        	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
	    	pstmt.close();
	    	
        	for ( i=0 ; i<chkList.size() ; i++  ){
        		
        		strQuery.setLength(0);
    			//CMM0130 테이블에서 시스템에 대한 값 카운트
    			strQuery.append("SELECT count(*) as cnt                     \n");
    			strQuery.append(" FROM CMM0130		                        \n");
    			strQuery.append(" WHERE CM_SYSCD  = ?						\n");
    			strQuery.append(" AND CM_GBNCD  = ?							\n");
    			pstmt = conn.prepareStatement(strQuery.toString());
    			//pstmt = new LoggableStatement(conn,strQuery.toString());
    			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
    			pstmt.setString(1, dataObj.get("cm_syscd"));
    			pstmt.setString(2, chkList.get(i).get("cm_gbncd"));
    			rs = pstmt.executeQuery();
    			
    			strQuery.setLength(0);
            	if ( rs.next() && rs.getInt("cnt") == 0 ){
            		pstmt.close();
	        		strQuery.append("INSERT INTO CMM0130 (cm_syscd,cm_gbncd,cm_uppgbncd,cm_reqyn,cm_seq,cm_creatdt) \n");
	        		strQuery.append("VALUES(?,?,?,?,?,sysdate)\n");
	                //pstmt = conn.prepareStatement(strQuery.toString());
	                pstmt = new LoggableStatement(conn,strQuery.toString());
	            	pstmt.setString(1, dataObj.get("cm_syscd"));
	            	pstmt.setString(2, chkList.get(i).get("cm_gbncd"));
	            	pstmt.setString(3, chkList.get(i).get("cm_uppgbncd"));
	            	pstmt.setString(4, chkList.get(i).get("cm_reqyn"));
	            	pstmt.setString(5, chkList.get(i).get("cm_seq"));
            	} else {
            		pstmt.close();
	        		strQuery.append("UPDATE CMM0130 SET					   	   	  	\n");
	        		strQuery.append(" cm_reqyn = ?, cm_seq = ?, cm_closedt = '' 	\n");
	                strQuery.append(" WHERE cm_syscd = ? AND cm_gbncd = ? 		    \n");
	        		//pstmt = conn.prepareStatement(strQuery.toString());
	                pstmt = new LoggableStatement(conn,strQuery.toString());	    
	            	pstmt.setString(1, chkList.get(i).get("cm_reqyn"));
	            	pstmt.setString(2, chkList.get(i).get("cm_seq"));
	            	pstmt.setString(3, dataObj.get("cm_syscd"));
	            	pstmt.setString(4, chkList.get(i).get("cm_gbncd"));
            	}
            	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
    	    	pstmt.close();
    	    	rs.close();
			} 

        	//14.07.28-29 siruen 시스템 체크리스트가 변경될 시, 모니터링 중 상태인 sr의 cmc0202 테이블의 값도 변경

			strQuery.setLength(0);
			strQuery.append("SELECT DISTINCT a.cc_srid			\n");
			strQuery.append(" FROM CMC0202 a, CMC0100 b			\n");
			strQuery.append(" WHERE a.CC_SYSCD = ?				\n");
			strQuery.append(" AND a.CC_SRID = b.CC_SRID			\n");
			strQuery.append(" AND b.CC_STATUS = '5'				\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, dataObj.get("cm_syscd"));
			rs = pstmt.executeQuery();
			while ( rs.next() ){
				for ( i=0 ; i<chkList.size() ; i++  ){
					strQuery.setLength(0);
					strQuery.append("UPDATE CMC0202 SET	CC_REQYN = ?,CC_EVAL = '',CC_EDTDT=SYSDATE \n");
					strQuery.append(" WHERE CC_SYSCD = ? AND CC_GBNCD = ? AND cc_srid = ? \n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, chkList.get(i).get("cm_reqyn"));
					pstmt2.setString(2, dataObj.get("cm_syscd"));
					pstmt2.setString(3, chkList.get(i).get("cm_gbncd"));
			    	pstmt2.setString(4, rs.getString("cc_srid"));
			    	ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			    	pstmt2.executeUpdate();
			    	pstmt2.close();
			    	
			    	strQuery.setLength(0);
					strQuery.append("DELETE CMC0202										 \n");
					strQuery.append(" WHERE CC_GBNCD = (SELECT A.CC_GBNCD				 \n");
					strQuery.append(" 					 FROM CMC0202 A, CMM0130 B		 \n");
					strQuery.append(" 					 WHERE A.CC_SRID = ?			 \n");
					strQuery.append(" 					  AND A.CC_SYSCD = B.CM_SYSCD	 \n");
					strQuery.append(" 					  AND A.CC_GBNCD = B.CM_GBNCD	 \n");
					strQuery.append(" 					  AND B.CM_CLOSEDT IS NOT NULL ) \n");
					strQuery.append(" AND CC_SRID = ?									 \n");
					strQuery.append(" AND CC_SYSCD = ?									 \n");
					//pstmt3 = conn.prepareStatement(strQuery.toString());
					pstmt3 = new LoggableStatement(conn,strQuery.toString());
					pstmt3.setString(1, rs.getString("cc_srid"));
					pstmt3.setString(2, rs.getString("cc_srid"));
					pstmt3.setString(3, dataObj.get("cm_syscd"));
					ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
		        	pstmt3.executeUpdate();
		        	pstmt3.close();
				}
			}
			pstmt.close();	 
			rs.close();

        	conn.commit();
        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;
        	
        	return "OK";
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm0130.setMonitorValues() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0130.setMonitorValues() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm0130.setMonitorValues() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0130.setMonitorValues() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0130.setMonitorValues() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}