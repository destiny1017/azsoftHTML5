package app.common;

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

public class DocFile {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public String getDocFile(String AcptNo,String GuBun) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String            retSeq      = "0";
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select nvl(max(cr_seqno),0) max from cmr1001 \n");
			strQuery.append(" where cr_acptno=? and cr_gubun=?            \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, GuBun);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	retSeq = rs.getString("max");
            }	
            rs.close();
            pstmt.close();
			conn.close();
			
			rs=null;
			pstmt = null;
			conn = null;
			
			return retSeq;
	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## DocFile.getDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## DocFile.getDocFile() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## DocFile.getDocFile() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## DocFile.getDocFile() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## DocFile.getDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDocFile() method statement
	
	
	public String setDocFile(ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
        	int    seq = 0;
        	
			for (int i=0;i<fileList.size();i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmr1001 (cr_acptno,cr_gubun,cr_seqno,cr_filename,cr_reldoc) values ( \n");
				strQuery.append(" ? , ? , ? , ? , ? ) \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());				
				
				pstmt.setString(1, fileList.get(i).get("acptno"));
				pstmt.setString(2, fileList.get(i).get("filegb"));
				pstmt.setInt(3, ++seq);				
				pstmt.setString(4, fileList.get(i).get("realName"));
				pstmt.setString(5, fileList.get(i).get("saveName"));
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());    
	            pstmt.executeUpdate();
	            pstmt.close();
			}
			conn.close();
		
			pstmt = null;
			conn = null;
			
			return "ok";
	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## DocFile.setDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## DocFile.setDocFile() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## DocFile.setDocFile() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## DocFile.setDocFile() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## DocFile.setDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setDocFile() method statement
	
	
	public String setDocFile(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> sayuObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
        	int    seq = 0;
        	
			for (int i=0;i<fileList.size();i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmr1001 (cr_acptno,cr_gubun,cr_seqno,cr_filename,cr_reldoc,cr_context) values ( \n");
				strQuery.append(" ? , ? , ? , ? , ? , ?) \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());				
				
				pstmt.setString(1, fileList.get(i).get("acptno"));
				pstmt.setString(2, fileList.get(i).get("filegb"));
				pstmt.setInt(3, ++seq);				
				pstmt.setString(4, fileList.get(i).get("realName"));
				pstmt.setString(5, fileList.get(i).get("saveName"));
				if(fileList.get(i).get("gubunName").equals("planFile")) pstmt.setString(6, sayuObj.get("TestPlanSayu"));
				else if (fileList.get(i).get("gubunName").equals("resultFile")) pstmt.setString(6, sayuObj.get("TestResultSayu"));
				else pstmt.setString(6, "");
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());    
	            pstmt.executeUpdate();
	            pstmt.close();
			}
			conn.close();
			
			pstmt = null;
			conn = null;
			
			return "ok";
	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## DocFile.setDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## DocFile.setDocFile() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## DocFile.setDocFile() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## DocFile.setDocFile() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## DocFile.setDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
	public String delDocFile(String AcptNo,String GuBun,int SeqNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("Delete from cmr1001 ");
			strQuery.append("where cr_acptno=? and cr_gubun=? and cr_seqno=? ");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, GuBun);
			pstmt.setInt(3, SeqNo);
            pstmt.executeUpdate();
            pstmt.close();
			conn.close();
			
			pstmt = null;
			conn = null;
			
			return "ok";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## DocFile.delDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## DocFile.delDocFile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## DocFile.delDocFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## DocFile.delDocFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## DocFile.delDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delDocFile() method statement
	
}
