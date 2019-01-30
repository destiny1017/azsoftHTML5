package app.eCmd;

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

public class Cmd0200 {
	
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
 
	public Object[] getJaWonList(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select distinct a.cm_codename as jawon                      \n");        		 
			strQuery.append("  from cmm0020 a,cmm0036 c \n");
			strQuery.append(" where c.cm_syscd = ? and c.cm_closedt is null \n");    					  						 
			strQuery.append("   and a.cm_micode = c.cm_rsrccd            				 \n"); 
			strQuery.append("	and a.cm_macode='JAWON'            						 \n");
			strQuery.append("	and c.cm_rsrccd not in (select cm_samersrc from cmm0037  \n");
			strQuery.append(" 							 where cm_syscd =?               \n");
			strQuery.append("                              and cm_rsrccd<>cm_samersrc)   \n");
			strQuery.append("  order by a.cm_seqno                                       \n"); 

            pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			pstmt.setString(pstmtcount++, SysCd);
			pstmt.setString(pstmtcount++, SysCd);

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("jawon", rs.getString("jawon"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement	
			
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0200.getJaWonList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0200.getJaWonList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0200.getJaWonList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0200.getJaWonList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0200.getJaWonList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
 
	
	public Object[] getJobList(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append(" select distinct CM_JOBNAME			\n");
			strQuery.append(" from cmm0102 a, cmm0034 b             \n");
			strQuery.append(" where b.cm_syscd = ?   				\n");
			strQuery.append(" and b.CM_JOBCD = a.CM_JOBCD			\n");
			strQuery.append(" order by CM_JOBNAME                   \n"); 

            pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			pstmt.setString(pstmtcount++, SysCd);

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_jobname", rs.getString("CM_JOBNAME"));

				rtList.add(rst);
				rst = null;
			}//end of while-loop statement	
			
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

				
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0200.getJaWonList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0200.getJaWonList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0200.getJaWonList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0200.getJaWonList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0200.getJaWonList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	 
}
