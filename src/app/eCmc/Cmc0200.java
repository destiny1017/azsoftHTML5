/*****************************************************************************************
	1. program ID	: Cmr0020.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	: 
	5. auth		    : 
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.InputStreamReader;

import app.common.LoggableStatement;

//import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;


import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
import app.common.*;

import app.thread.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmc0200{    
	

    /**
     * Logger Class Instance Creation
     * logger
     */
	
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

    public Object[] getSelectList(String SrId,String UserId,String ReqCd) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		int				  pstmtcount  = 1;
		Object[] returnObjectArray		 = null;
		
		try {
			
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cc_userid,        			                   \n");
			strQuery.append("       nvl(c.cnt,0) cnt, 			                       \n");
			strQuery.append("		to_char(a.cc_createdate,'yyyy/mm/dd') creatdt,	   \n");
			strQuery.append("		to_char(a.cc_lastdate,'yyyy/mm/dd') lastdt,		   \n");
			strQuery.append("		a.cc_devstday,a.cc_devedday,		  		       \n");
			strQuery.append("       a.cc_devtime,a.cc_devmm,          		           \n");
			strQuery.append("       b.cm_username,a.cc_status,d.cm_codename, a.cc_rate \n");				
			strQuery.append("  from cmm0020 d,cmc0110 a,cmm0040 b 		               \n");
			strQuery.append("      ,(select cc_userid,count(*) cnt from cmc0111        \n");				
			strQuery.append("         where cc_srid=?   	 			               \n");				
			strQuery.append("        group by cc_userid) c 			                   \n");
			strQuery.append(" where a.cc_srid=?       	        		  	           \n");
			if (UserId != null && UserId != "") {
				strQuery.append("  and a.cc_userid=?					        	   \n");
			}
			strQuery.append("  and a.cc_userid=b.cm_userid			         		   \n");
			strQuery.append("  and nvl(a.cc_status,'0')<>'3'					 	   \n");
			strQuery.append("  and d.cm_macode='ISRSTAUSR'  	                       \n");
			strQuery.append("  and d.cm_micode=a.cc_status 	                           \n");
			strQuery.append(" and a.cc_userid=c.cc_userid(+)			       		   \n");
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++,SrId);
			pstmt.setString(pstmtcount++,SrId);
			if (UserId != null && UserId != "") {
				pstmt.setString(pstmtcount++,UserId);
			}

			if (UserId != null && UserId != "") pstmt.setString(pstmtcount++,UserId);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        rsval.clear();
			while (rs.next()){			
				rst = new HashMap<String, String>();
				
				rst.put("cc_userid",rs.getString("cc_userid"));
				//rst.put("cc_status",rs.getString("CC_STATUS"));
				rst.put("creatdt",rs.getString("creatdt"));
				rst.put("lastdt",rs.getString("lastdt"));
				rst.put("cc_devstday",rs.getString("cc_devstday"));
				rst.put("cc_devedday",rs.getString("cc_devedday"));
				if (rs.getString("cc_devstday") != null) {
					rst.put("devstday", rs.getString("cc_devstday").substring(0,4)+"/"+
							            rs.getString("cc_devstday").substring(4,6)+"/"+
							            rs.getString("cc_devstday").substring(6,8));
				}
				if (rs.getString("cc_devedday") != null) {
					rst.put("devedday", rs.getString("cc_devedday").substring(0,4)+"/"+
							            rs.getString("cc_devedday").substring(4,6)+"/"+
							            rs.getString("cc_devedday").substring(6,8));
				}
				rst.put("devtime",rs.getString("cc_devtime"));
				rst.put("cc_status",rs.getString("cc_status"));
				if (rs.getBigDecimal("cc_devmm") != null){
					rst.put("devmm",rs.getBigDecimal("cc_devmm").toString());
				} else{
					rst.put("devmm", "");
				}
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cnt",rs.getString("cnt"));
				rst.put("cc_status", rs.getString("cc_status"));	
				rst.put("cm_codename", rs.getString("cm_codename"));				
				rst.put("cc_rate", rs.getString("cc_rate"));				
				
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			if (rsval.size() > 0 && !ReqCd.equals("42") && !ReqCd.equals("49")&& !ReqCd.equals("47")) {
				rst = new HashMap<String, String>();
				rst.put("cc_scmuser","");
				rst.put("cm_username","전체");
				rsval.add(0,rst);
				rst = null;
			}
			
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			return returnObjectArray;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0200.getSelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.getSelectList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.getSelectList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.getSelectList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0200.getSelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement
    
    public Object[] get_Worktime(String IsrId) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap>  rsval  = new ArrayList<HashMap>();
		HashMap			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		int				  pstmtcount  = 1;
		Object[] returnObjectArray		 = null;
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cc_userid,a.cc_workday,      \n");	
			strQuery.append("       a.cc_worktime,b.cm_username	   \n");				
			strQuery.append("  from cmc0111 a,cmm0040 b			   \n");
			strQuery.append(" where a.cc_srid=?                    \n");
			strQuery.append("   and a.cc_userid=b.cm_userid	       \n");
			pstmt = conn.prepareStatement(strQuery.toString());	
//			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++,IsrId);
	//        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        rsval.clear();
			while (rs.next()){			
				rst = new HashMap();
				rst.put("cc_userid",rs.getString("cc_userid"));
				rst.put("cc_workday", rs.getString("cc_workday").substring(0,4)+"/"+
			               rs.getString("cc_workday").substring(4,6)+"/"+
			               rs.getString("cc_workday").substring(6));
				rst.put("cc_worktime",rs.getDouble("cc_worktime"));
				rst.put("cm_username",rs.getString("cm_username"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement	
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			return returnObjectArray;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0200.get_Worktime() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.get_Worktime() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.get_Worktime() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.get_Worktime() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0200.get_Worktime() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement
    
	public String setInsertList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		boolean           insFg       = true;
		int				  pstmtcount  = 1;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);              		
    		strQuery.append("update cmc0110 set cc_lastdate=SYSDATE,  \n");              		
    		strQuery.append("       CC_DEVSTDAY=?,CC_DEVEDDAY=?,	  \n");
    		strQuery.append("       CC_DEVTIME=?,CC_DEVMM=?, 	      \n");
    		strQuery.append("       cc_status=decode(cc_status,'1','2',cc_status),cc_rate=?   \n");
    		strQuery.append(" where CC_SRID=? and cc_userid=?		  \n");    		
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmtcount = 1;
      	    pstmt.setString(pstmtcount++, etcData.get("devstday"));
      	    pstmt.setString(pstmtcount++, etcData.get("devedday"));
      	    pstmt.setString(pstmtcount++, etcData.get("devtime"));
      	    pstmt.setString(pstmtcount++, etcData.get("devmm"));
      	    pstmt.setString(pstmtcount++, etcData.get("rate"));
      	    pstmt.setString(pstmtcount++, etcData.get("srid"));
      	    pstmt.setString(pstmtcount++, etcData.get("userid"));
      	    
      	  //pstmt.setString(pstmtcount++, etcData.get(""));
      		pstmt.executeUpdate();           		
	        pstmt.close();
			
	        strQuery.setLength(0);              		
    		strQuery.append("update cmc0100 set                       \n"); 
    		strQuery.append("       cc_status=decode(cc_status,'2','4',cc_status)   \n");
    		strQuery.append(" where CC_SRID=?                   	  \n");    		
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmtcount = 1;
      	    pstmt.setString(pstmtcount++, etcData.get("srid"));
      	  //pstmt.setString(pstmtcount++, etcData.get(""));
      		pstmt.executeUpdate();           		
	        pstmt.close();
	        conn.close();
			conn = null;
			pstmt = null;
	        
	        return retMsg;
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setInsertList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.setInsertList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setInsertList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.setInsertList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0200.setInsertList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmc0200_Insert() method statement
	
	public String setTimeInsertList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		boolean           insFg       = true;
		int				  pstmtcount  = 1;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
        	strQuery.setLength(0);  
        	strQuery.append("delete cmc0111      \n");
    		strQuery.append(" where cc_srid=?    \n");
    		strQuery.append("   and cc_userid=?  \n");
    		strQuery.append("   and cc_workday=? \n");
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmtcount = 1;
      	    pstmt.setString(pstmtcount++, etcData.get("srid"));
      	    pstmt.setString(pstmtcount++, etcData.get("userid"));
      	    pstmt.setString(pstmtcount++, etcData.get("workday"));
      	    pstmt.executeUpdate();           		
	        pstmt.close();
	        
        	strQuery.setLength(0);                		
    		strQuery.append("insert into cmc0111 		        \n");
    		strQuery.append("  (CC_SRID,CC_USERID,CC_WORKDAY,   \n");
    		strQuery.append("   CC_CREATDT,CC_WORKTIME)         \n");
    		strQuery.append(" values(?,?,?,SYSDATE,?)			\n");
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmtcount = 1;
      	    pstmt.setString(pstmtcount++, etcData.get("srid"));
      	    pstmt.setString(pstmtcount++, etcData.get("userid"));
      	    pstmt.setString(pstmtcount++, etcData.get("workday"));
      	    pstmt.setString(pstmtcount++, etcData.get("worktime"));
//      	    pstmt.setString(pstmtcount++, etcData.get(""));
      		pstmt.executeUpdate();           		
	        pstmt.close();
		        
		    retMsg = "0";
	        
	        conn.close();
			conn = null;
			pstmt = null;
	        
	        return retMsg;
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setTimeInsertList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.setTimeInsertList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setTimeInsertList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.setTimeInsertList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0200.setTimeInsertList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmc0200_Insert() method statement
	
	public String setTimeDeleteList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			
			int	pstmtcount  = 1;

        	strQuery.setLength(0);  
        	strQuery.append("delete cmc0111      \n");
    		strQuery.append(" where cc_srid=?    \n");
    		strQuery.append("   and cc_userid=? \n");
    		strQuery.append("   and cc_workday=? \n");
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmtcount = 1;
      	    pstmt.setString(pstmtcount++, etcData.get("srid"));
      	    pstmt.setString(pstmtcount++, etcData.get("userid"));
      	    pstmt.setString(pstmtcount++, etcData.get("workday"));
      	    pstmt.executeUpdate();           		
	        pstmt.close();
	        
	        conn.close();
			conn = null;
			pstmt = null;
	        
	        return "0";
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setTimeInsertList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.setTimeInsertList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setTimeInsertList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.setTimeInsertList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0200.setTimeInsertList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmc0200_Insert() method statement
	public String getWorkDays(String strYear) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "";
		boolean           insFg       = true;
		int				  pstmtcount  = 1;
		ConnectionContext connectionContext = new ConnectionResource();		
			
		try {
			conn = connectionContext.getConnection();
			
        	strQuery.setLength(0);  
        	strQuery.append("select cm_monthday from cmm0017      \n");
    		strQuery.append(" where cm_year=?   \n");
    		pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(pstmtcount++,strYear);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				retMsg = rs.getString("cm_monthday");
			}else{
				retMsg = "No";
			}
			rs.close();
			pstmt.close();
	        
	        conn.close();
			conn = null;
			pstmt = null;
			rs = null;
	        
	        return retMsg;
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setTimeInsertList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.setTimeInsertList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.setTimeInsertList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.setTimeInsertList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0200.setTimeInsertList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmc0200_Insert() method statement
	
	//14.07.30 siruen 실적 등급 코드 가져오는 쿼리 추가
	public Object[] getRatecd() throws SQLException, Exception {
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
			strQuery.append("SELECT CM_MICODE, CM_CODENAME   \n");
			strQuery.append("  FROM CMM0020         	  	 \n");
			strQuery.append(" WHERE CM_MACODE='DEVRATE'   	 \n");
			strQuery.append("  AND CM_MICODE <> '****'   	 \n");
			strQuery.append("  AND CM_CLOSEDT IS NULL    	 \n");
			strQuery.append(" ORDER BY CM_MICODE   			 \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rst = new HashMap<String, String>();
			rst.put("cm_micode", "00");
			rst.put("cm_codename", "선택하세요");
			rsval.add(rst);	
			rst = null;
	        while ( rs.next() ) {
	        	rst = new HashMap<String, String>();
				rst.put("cm_micode", rs.getString("CM_MICODE"));
				rst.put("cm_codename", rs.getString("CM_CODENAME"));
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
			ecamsLogger.error("## Cmc0200.getSyscd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0200.getSyscd() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0200.getSyscd() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0200.getSyscd() Exception END ##");				
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
					ecamsLogger.error("## Cmc0200.getSyscd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRatecd() method statement
	
}//end of Cmc0200 class statement