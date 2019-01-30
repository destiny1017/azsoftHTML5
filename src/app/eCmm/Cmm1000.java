
/*****************************************************************************************
	1. program ID	: eCmm1800.java
	2. create date	: 
	3. auth		    : 
	4. update date	: 
	5. auth		    : 
	6. description	: eCmm1800
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import app.common.LoggableStatement;




/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm1000{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * project 등록
	 * @param  pPrjno,pDocname,pDocPath
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
		
    public Object[] getHoliDay(String nyear) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection(); 
			
			strQuery.append("SELECT a.cm_gubuncd,b.cm_codename as holigb_nm, a.cm_msgcd, \n");
			strQuery.append("       c.cm_codename as holi_nm,  \n");
			strQuery.append("       substr(a.cm_holiday,1,4)||'-'||substr(a.cm_holiday,5,2)||'-'||substr(a.cm_holiday,7,2) as cm_holiday2, \n");
			strQuery.append("       substr(a.cm_holiday,1,4)||'년 '||substr(a.cm_holiday,5,2)||'월 '||substr(a.cm_holiday,7,2)|| '일' as cm_holiday1 \n");
			strQuery.append("FROM cmm0050 a, cmm0020 b, cmm0020 c \n");
			strQuery.append("WHERE substr(a.cm_holiday, 1, 4) = ? \n");
			strQuery.append("and   a.cm_closedt is null \n");
			strQuery.append("and   a.cm_msgcd = c.cm_micode \n");
			strQuery.append("and   c.cm_macode = 'HOLIDAY' \n");
			strQuery.append("and   a.cm_gubuncd = b.cm_micode \n");
			strQuery.append("and   b.cm_macode = 'HOLICD' \n");
			strQuery.append("ORDER BY a.cm_holiDay \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, nyear);
			
			
            rs = pstmt.executeQuery();
            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_gubuncd", rs.getString("cm_gubuncd")); 		
				rst.put("holigb_nm", rs.getString("holigb_nm")); 	
				rst.put("cm_msgcd", rs.getString("cm_msgcd"));		
				rst.put("holi_nm", rs.getString("holi_nm"));	
				rst.put("cm_holiday1", rs.getString("cm_holiday1"));
				rst.put("cm_holiday2", rs.getString("cm_holiday2"));
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
            return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception END ##");				
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
					ecamsLogger.error("## Cmm1000.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		
	}//end of sql_Qry() method statement
    
    public int chkHoliDay(String nDate) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rowcnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection(); 
			
			strQuery.append("SELECT count(*) cnt \n");
			strQuery.append("FROM cmm0050 \n");
			strQuery.append("WHERE cm_holiday = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, nDate);
			
			
            rs = pstmt.executeQuery();
            
            rowcnt = 0;
            
            if (rs.next()){
            	rowcnt = rs.getInt("cnt");
			}
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
            
            return rowcnt;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1000.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		
	}//end of sql_Qry() method statement    
    
    public String delHoliday(String nDate) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		int				  qryCnt      = 0;
		StringBuffer      strQuery    = new StringBuffer();

		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection(); 
			conn.setAutoCommit(false);
			
			strQuery.append("DELETE cmm0050 WHERE cm_holiday = ? \n");

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, nDate);
			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			qryCnt = pstmt.executeUpdate();
			
			
            if (qryCnt > 0){
            	conn.commit();
            	pstmt.close();
            	conn.close();
            	conn = null;
            	return "폐기처리가 완료되었습니다.";
            }
            else{
            	conn.rollback();
            	pstmt.close();
            	conn.close();
            	conn = null;
            	return "["+nDate+"] 는 휴일로 지정되어 있지 않습니다.";
            }
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1000.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement
    
    public String addHoliday(String nDate,String holigb,String holi,int ntype) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		int				  qryCnt      = 0;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection(); 
			conn.setAutoCommit(false);
			
			if (ntype == 1){
				strQuery.append("UPDATE cmm0050 SET \n");
				strQuery.append("cm_gubuncd = ? , \n");
				strQuery.append("cm_msgcd = ? , \n");
				strQuery.append("cm_creatdt = sysdate \n");
				strQuery.append("where cm_holiday = ? \n");
			}
			else{
				strQuery.append("INSERT INTO cmm0050 (cm_gubuncd, cm_msgcd, \n");
				strQuery.append("                     cm_holiday, cm_creatdt) \n");
				strQuery.append(" values( ?, ?, ?, sysdate) \n");
			}
			
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, holigb);
			pstmt.setString(2, holi);
			pstmt.setString(3, nDate);
			
			
			qryCnt = pstmt.executeUpdate();
			
            if (qryCnt > 0){
            	conn.commit();
            	pstmt.close();
            	conn.close();
            	conn = null;
            	return "등록처리가 완료되었습니다.";
            }
            else{
            	conn.rollback();
            	pstmt.close();
            	conn.close();
            	conn = null;
            	return "["+nDate+"] 등록처리 실패하였습니다.";
            }
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1000.sql_Qry() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1000.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		
	}//end of sql_Qry() method statement        
    
}//end of Cmm1000 class statement
