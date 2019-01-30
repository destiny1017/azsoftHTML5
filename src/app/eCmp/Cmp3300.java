/*****************************************************************************************
	1. program ID	: Cmp3300.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 
	5. auth		    : 
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmp;

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
public class Cmp3300{    
	

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
	 *///strUserId,strStD,datEnD,cboReq
	public Object[] getReqList(String UserId,String StDate,String EdDate,String Req) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		Object[] returnObjectArray = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,b.cm_codename,c.cm_username,d.cm_deptname, \n");
			strQuery.append("       a.cr_prjno,a.cr_devno,e.cm_codename reqcd,             \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,  \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,    \n");
			strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno \n");
			strQuery.append("  from cmm0020 e,cmm0020 b,cmm0100 d,cmm0040 c,cmr1000 a      \n");
			strQuery.append(" where to_char(a.cr_acptdate,'yyyymmdd')>=?	        \n");
			strQuery.append("   and to_char(a.cr_acptdate,'yyyymmdd')<=? 	        \n");
			strQuery.append("   and a.cr_status<>'3' and a.cr_prcdate is not null   \n");
			if (Req != null && Req != "") {
				strQuery.append("and a.cr_emgcd=?                                   \n");
			}
			strQuery.append("   and a.cr_editor=c.cm_userid                         \n");
			strQuery.append("   and a.cr_teamcd=d.cm_deptcd                         \n");
			strQuery.append("   and b.cm_macode='REQUEST' and b.cm_micode=a.cr_qrycd\n");
			strQuery.append("   and e.cm_macode='REQCD' and e.cm_micode=a.cr_emgcd  \n");
			strQuery.append(" order by a.cr_acptdate                                \n");

			pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, EdDate);
			if (Req != null && Req != "") pstmt.setString(++parmCnt, Req);
			
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("acptno",rs.getString("acptno"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				rst.put("prcdate",rs.getString("prcdate"));
				rst.put("chgcd",rs.getString("reqcd"));
				
				if (rs.getString("cr_prjno") != null) {
					if (rs.getString("cr_prjno").length()>10) {
						rst.put("prjno", rs.getString("cr_prjno")+"-"+rs.getString("cr_devno"));
					}
				}			
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			returnObjectArray = rsval.toArray();
			
			rsval = null;
			//ecamsLogger.debug(rsval.toString());		
			return returnObjectArray;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3300.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp3300.getReqList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3300.getReqList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp3300.getReqList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}		
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp3300.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement	
}//end of Cmp3300 class statement
