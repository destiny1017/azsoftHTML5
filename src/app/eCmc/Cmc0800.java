package app.eCmc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.*;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmc0800 {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getApplyInfo(String srID,String reqCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT a.cc_confmsg,b.cm_username,a.cc_editor,          \n");
			strQuery.append("       to_char(a.cc_lastdt,'yyyy/mm/dd hh24:mi') lastdt \n");
			strQuery.append("  FROM cmm0040 b, cmc0290 a							 \n");
			strQuery.append(" WHERE a.cc_srid=? and a.cc_gbncd=decode(?,'61','D','S')\n");
			strQuery.append("  	AND a.cc_editor=b.cm_userid							 \n");	
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        pstmt.setString(2, reqCD);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	rst = new HashMap<String, String>();
				rst.put("cc_confmsg", rs.getString("cc_confmsg"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cc_editor", rs.getString("cc_editor"));
				rst.put("lastdt", rs.getString("lastdt"));				
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
			ecamsLogger.error("## Cmc0800.getApplyInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0800.getApplyInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0800.getApplyInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0800.getApplyInfo() Exception END ##");				
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
					ecamsLogger.error("## Cmc0800.getApplyInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getApplyInfo() method statement
	
	public String insertApply(HashMap<String,String> tmp_obj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet		  rs		  = null;
		StringBuffer      strQuery    = new StringBuffer();
		int 			  param_cnt	  = 1;
		int               i           = 0;
		String            inUpGubun   = "";
		boolean           endSw       = false;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT count(*) as cnt                     \n");
			strQuery.append("  FROM cmc0290		                        \n");
			strQuery.append(" WHERE cc_srid = ?							\n");
			strQuery.append("   AND cc_gbncd=decode(?,'61','D','S')     \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
		 	pstmt.setString(1, tmp_obj.get("cc_srid"));
		 	pstmt.setString(2, tmp_obj.get("cc_gbncd"));
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();			
        	if (rs.next()){
        		i = rs.getInt("cnt");
        	}			
			rs.close();
			pstmt.close();
			if ( i == 0 ) {
				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMC0290                       \n");
				strQuery.append("    (cc_srid,cc_gbncd,cc_seqno,cc_lastdt, \n");
				strQuery.append("	  cc_editor,cc_confmsg,cc_status)	   \n");
				strQuery.append("(SELECT ?,?,nvl(max(cc_seqno),0)+1,SYSDATE,\n");
				strQuery.append("		 ?,?,'9'             		       \n");
				strQuery.append("	FROM CMC0290             		       \n");
				strQuery.append("  WHERE CC_SRID=?)            		       \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				param_cnt = 1;				
				pstmt.setString(param_cnt++, tmp_obj.get("cc_srid"));
				if (tmp_obj.get("cc_gbncd").equals("61")) pstmt.setString(param_cnt++, "D");
				else pstmt.setString(param_cnt++, "S");
				pstmt.setString(param_cnt++, tmp_obj.get("cc_editor"));
				pstmt.setString(param_cnt++, tmp_obj.get("cc_confmsg"));
				pstmt.setString(param_cnt++, tmp_obj.get("cc_srid"));
				pstmt.executeUpdate();
				inUpGubun = "INSERT";
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.close();
			} else {
	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMC0290 SET                      \n");
	            strQuery.append("       CC_LASTDT =SYSDATE,CC_CONFMSG=?, \n");
	            strQuery.append("       CC_STATUS ='9'                   \n");
	            strQuery.append(" WHERE CC_SRID=?                        \n");//
	            strQuery.append("   AND CC_GBNCD=?                       \n");//
	        	pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, tmp_obj.get("cc_confmsg"));
			    pstmt.setString(2, tmp_obj.get("cc_srid"));
				if (tmp_obj.get("cc_gbncd").equals("61")) pstmt.setString(3, "D");
				else pstmt.setString(3, "S");
	        	pstmt.executeUpdate();
	        	inUpGubun = "UPDATE";
	        	pstmt.close();
        	}
			if (tmp_obj.get("cc_gbncd").equals("62")) {
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmc0100    \n");
				strQuery.append(" where cc_srid=?                    \n");
				strQuery.append("   and cc_realdatause='Y'           \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, tmp_obj.get("cc_srid"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					if (rs.getInt("cnt")==0) {
						endSw = true;
					}
				}
				rs.close();
				pstmt.close();
			}
			if (!endSw) {
				//보안점검인 경우 DATA 점검확인 여부 체크
				//DATA점검인 경우 보안점검 확인여부 체크
	            strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmc0290   \n");
				strQuery.append(" where cc_srid=? and cc_status='9' \n");
				strQuery.append("   and cc_gbncd=decode(?,'61','S','D') \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, tmp_obj.get("cc_srid"));
				pstmt.setString(2, tmp_obj.get("cc_gbncd"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					if (rs.getInt("cnt")>0) {
						endSw = true;
					}
				}
				rs.close();
				pstmt.close();
			}
			if (endSw) {
				//적용확인이 완료된 경우 상태 update (적용확인완료)
				strQuery.setLength(0);
				strQuery.append("update cmc0100 set cc_status='6'  \n");
				strQuery.append(" where cc_srid=?                  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, tmp_obj.get("cc_srid"));
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				//적용확인중으로set
				strQuery.setLength(0);
				strQuery.append("update cmc0100 set cc_status='5'   \n");
				strQuery.append(" where cc_srid=? and cc_status='4' \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, tmp_obj.get("cc_srid"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
//			return SR_ID;
			return inUpGubun;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0800.insertApply() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0800.insertPrjInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0800.insertApply() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0800.insertApply() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0800.insertApply() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of insertPrjInfo() method statement	
}
