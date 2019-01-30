/*****************************************************************************************
	1. program ID	: Cmr0270.java
	2. create date	: 2012.10.15
	3. auth		    : no name
	4. update date	:
	5. auth		    :
	6. description	: 산출물등록 사전점검
*****************************************************************************************/
package app.eCmr;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.common.LoggableStatement;
import app.common.SystemPath;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author no name
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr0270{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/** 등록된 산출물조회
	 * @param etcData [AcptNo:신청번호 , UserId:로그인사용자ID]
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getSubList(String syscd,String jobcd) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select distinct a.cr_itemid, a.cr_rsrcname, a.cr_dsncd, b.cm_codename  \n");
			strQuery.append("  from cmr0020 a, cmm0020 b \n");
			strQuery.append(" where a.cr_syscd = ? \n");
			strQuery.append("   and a.cr_jobcd = ? \n");
			strQuery.append("   and a.cr_lstver > 0 \n");
			strQuery.append("   and a.cr_status = '0' \n");
			strQuery.append("   and b.cm_macode = 'JAWON' \n");
			strQuery.append("   and a.cr_rsrccd = b.cm_micode \n");
			strQuery.append("   and a.cr_rsrccd in ('85', '90') \n");//프로그램자원 픽스됨
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, syscd);
			pstmt.setString(2, jobcd);
			rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cr_dsncd", rs.getString("cr_dsncd"));
				rst.put("cm_codename", rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cmr0270.getSubList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0270.getSubList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0270.getSubList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0270.getSubList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0270.getSubList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSubList() method statement

	public String setMappingInfo(String UserId,String AcptNo,String SysCd,
			ArrayList<HashMap<String,Object>> addProgFiles,Connection conn) throws SQLException, Exception
	{
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		try{

	    	for(int i=0; i<addProgFiles.size(); i++){
	    		strQuery.setLength(0);
	    		strQuery.append("insert into cmr2700 (cr_acptno, cr_status, cr_syscd,             \n");
	    		strQuery.append("		cr_prcdate, cr_baseitem, cr_itemid, cr_dsncd, cr_editor)  \n");
	    		strQuery.append("values (?, '0', ?, sysdate, ?, ?, ?, ?)                          \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt = new LoggableStatement(conn, strQuery.toString());
		        pstmt.setString(1, AcptNo);
		        pstmt.setString(2, SysCd);
	        	pstmt.setString(3, addProgFiles.get(i).get("cr_baseitem").toString());
	        	pstmt.setString(4, addProgFiles.get(i).get("cr_itemid").toString());
	        	pstmt.setString(5, addProgFiles.get(i).get("cr_dsncd").toString());
	        	pstmt.setString(6, UserId);
                ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();
	    	}
	    	pstmt = null;

	    	return "OK";

		}  catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0270.setSubProg() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0270.setSubProg() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0270.setSubProg() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0270.setSubProg() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)  try{rs.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//setDocfile

}//end of Cmr0270 class statement
