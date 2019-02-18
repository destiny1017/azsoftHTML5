package app.eCmc;

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

public class Test {
	/**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
    public Object[] getFileList() throws SQLException, Exception{
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
			strQuery.append("select CM_USERID, CM_USERNAME, CM_POSITION, CM_DUTY, DUTY.CM_CODENAME CM_DUTYNAME, POSITION.CM_CODENAME CM_POSITIONNAME");
			strQuery.append(" from ECAMS_DEV.CMM0040, ECAMS_DEV.CMM0020 DUTY, ECAMS_DEV.CMM0020 POSITION");
			strQuery.append(" where DUTY.CM_MACODE='DUTY' AND CMM0040.CM_DUTY = DUTY.CM_MICODE AND CMM0040.CM_POSITION = POSITION.CM_MICODE");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
            
            boolean userYN = true;
            while(rs.next()){
            	userYN = false;
				rst = new HashMap<String, String>();
				rst.put("ID", "");
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));			         
				rst.put("cm_position",rs.getString("cm_position"));
				rst.put("cm_duty",rs.getString("cm_duty"));
				rst.put("cm_dutyname",rs.getString("cm_dutyname"));
				rst.put("cm_positionname",rs.getString("cm_positionname"));
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
            
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0400.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    
    public Object[] getFileList2(String serchTxt, int ifNum) throws SQLException, Exception{
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
			strQuery.append("select CM_USERID, CM_USERNAME, CM_POSITION, CM_DUTY, DUTY.CM_CODENAME CM_DUTYNAME, POSITION.CM_CODENAME CM_POSITIONNAME");
			strQuery.append(" from ECAMS_DEV.CMM0040, ECAMS_DEV.CMM0020 DUTY, ECAMS_DEV.CMM0020 POSITION");
			if(ifNum == 1){
				strQuery.append(" where CMM0040.CM_USERNAME = ?");
			} else if(ifNum == 2) {
				strQuery.append(" where CMM0040.CM_USERID = ?");
			} else if(ifNum == 3){
				strQuery.append(" where CMM0040.CM_POSITION = ?");
			} else if(ifNum == 4){
				strQuery.append(" where CMM0040.CM_DUTY = ?");
			}
			strQuery.append(" AND DUTY.CM_MACODE='DUTY' AND CMM0040.CM_DUTY = DUTY.CM_MICODE AND CMM0040.CM_POSITION = POSITION.CM_MICODE");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, serchTxt);
            rs = pstmt.executeQuery();
            
            boolean userYN = true;
            while(rs.next()){
            	userYN = false;
				rst = new HashMap<String, String>();
				rst.put("ID", "");
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));			         
				rst.put("cm_position",rs.getString("cm_position"));
				rst.put("cm_duty",rs.getString("cm_duty"));
				rst.put("cm_dutyname",rs.getString("cm_dutyname"));
				rst.put("cm_positionname",rs.getString("cm_positionname"));
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
            
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0400.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    public Object[] getFileList3(String serchTxt1, String serchTxt2, int ifNum) throws SQLException, Exception{
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
			strQuery.append("select CM_USERID, CM_USERNAME, CM_POSITION, CM_DUTY, DUTY.CM_CODENAME CM_DUTYNAME, POSITION.CM_CODENAME CM_POSITIONNAME");
			strQuery.append(" from ECAMS_DEV.CMM0040, ECAMS_DEV.CMM0020 DUTY, ECAMS_DEV.CMM0020 POSITION");
			if(ifNum == 1){
				strQuery.append(" where CM_USERID = ? AND CM_USERNAME = ?");
			}
			strQuery.append(" AND DUTY.CM_MACODE='DUTY' AND CMM0040.CM_DUTY = DUTY.CM_MICODE AND CMM0040.CM_POSITION = POSITION.CM_MICODE");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, serchTxt1);
            pstmt.setString(2, serchTxt2);
            rs = pstmt.executeQuery();
            
            boolean userYN = true;
            while(rs.next()){
            	userYN = false;
				rst = new HashMap<String, String>();
				rst.put("ID", "");
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));			         
				rst.put("cm_position",rs.getString("cm_position"));
				rst.put("cm_duty",rs.getString("cm_duty"));
				rst.put("cm_dutyname",rs.getString("cm_dutyname"));
				rst.put("cm_positionname",rs.getString("cm_positionname"));
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
            
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0400.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    public Object[] getPosLst() throws SQLException, Exception{
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
			strQuery.append("select CM_MACODE, CM_MICODE, CM_CODENAME");
			strQuery.append(" from ECAMS_DEV.CMM0020");
			strQuery.append(" where CM_MACODE='DUTY' OR CM_MACODE='POSITION' order by CM_MICODE asc");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
            
            boolean userYN = true;
            while(rs.next()){
            	userYN = false;
				rst = new HashMap<String, String>();
				rst.put("ID", "");		
				rst.put("cm_macode",rs.getString("CM_MACODE"));
				rst.put("cm_micode",rs.getString("CM_MICODE"));
				rst.put("cm_codename",rs.getString("CM_CODENAME"));
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
            
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0400.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    
    public Object[] getTab3Lst(String dutycCode) throws SQLException, Exception{
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
			strQuery.append("select CM_USERID, CM_USERNAME, CM_POSITION, CM_DUTY, DUTY.CM_CODENAME CM_DUTYNAME, POSITION.CM_CODENAME CM_POSITIONNAME");
			strQuery.append(" from ECAMS_DEV.CMM0040, ECAMS_DEV.CMM0020 DUTY, ECAMS_DEV.CMM0020 POSITION");
			strQuery.append(" where CMM0040.CM_DUTY = ?");
			strQuery.append(" And DUTY.CM_MACODE='DUTY' AND CMM0040.CM_DUTY = DUTY.CM_MICODE AND CMM0040.CM_POSITION = POSITION.CM_MICODE");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, dutycCode);
            rs = pstmt.executeQuery();
            
            boolean userYN = true;
            while(rs.next()){
            	userYN = false;
				rst = new HashMap<String, String>();
				rst.put("ID", "");
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));			         
				rst.put("cm_position",rs.getString("cm_position"));
				rst.put("cm_duty",rs.getString("cm_duty"));
				rst.put("cm_dutyname",rs.getString("cm_dutyname"));
				rst.put("cm_positionname",rs.getString("cm_positionname"));
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
            
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0400.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
}
