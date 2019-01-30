package com.ecams.service.cmr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;



/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CmrDAO{
	

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
    
	/**
	 * @param  cm_stno
	 * @return String 
	 * @throws Exception
	 */
	public String selectCmm0010(String cm_stno) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            rtn_val     = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT * FROM CMM0010 WHERE CM_STNO = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, cm_stno);		
			rs = pstmt.executeQuery();
			
			if (rs.next()){
				rtn_val = rs.getString(1);
			}else{
				rtn_val = "none";
			}
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## LoginCO.selectCmm0010() SQLException START ##");
			ecamsLogger.error("## access ip : " + "[" + cm_stno +"]");	
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## LoginCO.selectCmm0010() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## LoginCO.selectCmm0010() Exception START ##");	
			ecamsLogger.error("## access ip : " + "[" + cm_stno +"]");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## LoginCO.selectCmm0010() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## LoginCO.selectCmm0010() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		return rtn_val;
		
	}//end of selectCmm0010() method statement
	
}//end of CmrDao class statement
