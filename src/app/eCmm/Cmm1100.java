/*****************************************************************************************
	1. program ID	: eCmm1100.java
	2. create date	: 
	3. auth		    : 
	4. update date	: 090605
	5. auth		    : no name
	6. description	: 기본관리 -> 부재등록 eCmm1100 
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import app.common.UserInfo;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Cmm1100{
	 
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
    
	public Object[] get_grid_select(String user_id) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtnObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			strQuery.append(" select cm_userid,cm_username,cm_blankdts,cm_blankdte	\n");
			//strQuery.append(" from cmm0040 where cm_status='9'						\n");
			strQuery.append(" from cmm0040 where  cm_daegyul= ?						\n");//대결지정자사번
			strQuery.append(" and cm_blankdts is not null							\n");
//			strQuery.append(" and cm_blankdts>=to_date(SYSDATE, 'yyyy/mm/dd')		\n");
			strQuery.append(" and cm_blankdts<=to_char(SYSDATE, 'yyyymmdd')		\n");
			strQuery.append(" and cm_blankdte>=to_char(SYSDATE, 'yyyymmdd')		\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();
            strQuery.setLength(0);
            
			if (rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_blankdts", rs.getString("cm_blankdts"));
				rst.put("cm_blankdte", rs.getString("cm_blankdte"));
				rst.put("sedate", rs.getString("cm_blankdts")+ " - " +rs.getString("cm_blankdte"));
				rsList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			rtnObj = rsList.toArray();
			rsList.clear();
			rsList = null;
			
			return rtnObj;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1100.get_grid_select() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.get_grid_select() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.get_grid_select() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.get_grid_select() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtnObj != null)	rtnObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.get_grid_select() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
//	public int get_Update(String Frm_User, String DaeSign,String Cbo_Sayu,String Txt_Sayu, String sdate,String edate, String Opt_cd0) throws SQLException, Exception
	public int get_Update(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               rtn_cnt     = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			strQuery.setLength(0);
			strQuery.append("update cmm0040 set \n");
	        if (dataObj.get("Opt_Cd0").equals("true")) {
	        	if (dataObj.get("DaeSign") != "" && dataObj.get("DaeSign") != null) strQuery.append(" cm_daegyul = ? , \n"); //DaeSign
	           	if (dataObj.get("Cbo_Sayu") != "" && dataObj.get("Cbo_Sayu") != null) strQuery.append(" cm_daegmsg=? , \n"); //Cbo_Sayu
	           	if (dataObj.get("Txt_Sayu") != "" && dataObj.get("Txt_Sayu") != null) strQuery.append(" cm_daesayu=?, \n");  //Txt_Sayu	           
	           	//strQuery.append(" cm_Status = '9', \n");
	           	strQuery.append(" cm_blankdts = ?,				    \n"); //DTPK(0)
	           	strQuery.append(" cm_blankdte = ?				    \n"); //DTPK(1)
	        } else{
		        strQuery.append(" cm_daegyul 	= '', 				\n");
		        strQuery.append(" cm_daegmsg	= '', 				\n");
		        strQuery.append(" cm_blankdts 	= '', 				\n");
		        strQuery.append(" cm_blankdte 	= '' 				\n");
	        }
		    strQuery.append(" where cm_userID = ? \n");//Frm_User
			
		    pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt =  new LoggableStatement(conn, strQuery.toString());
		    
			int CNT = 0;
			if (dataObj.get("Opt_Cd0").equals("true")) {
				if (dataObj.get("DaeSign") != "" && dataObj.get("DaeSign") != null) pstmt.setString(++CNT, dataObj.get("DaeSign"));
				pstmt.setString(++CNT, dataObj.get("Cbo_Sayu"));
				pstmt.setString(++CNT, dataObj.get("Txt_Sayu"));
				pstmt.setString(++CNT, dataObj.get("sdate").replace("/", ""));
				pstmt.setString(++CNT, dataObj.get("edate").replace("/", ""));
			}
			pstmt.setString(++CNT, dataObj.get("Frm_User"));
			
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
			rtn_cnt= pstmt.executeUpdate();
			pstmt.close();
			
			
			if (dataObj.get("Opt_Cd0").equals("true")) {
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from dual          \n");
				strQuery.append(" where to_char(SYSDATE,'yyyymmdd')>=?  \n");
				strQuery.append("   and to_char(SYSDATE,'yyyymmdd')<=?  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1,dataObj.get("sdate").replace("/",""));
				pstmt.setString(2,dataObj.get("edate").replace("/", ""));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
				rs = pstmt.executeQuery();
				if (rs.next()) {
					rtn_cnt = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
				
				if (rtn_cnt>0) {
					strQuery.setLength(0);
					strQuery.append("update cmr9900       \n");
					strQuery.append("   set cr_team=?     \n");
					strQuery.append(" where cr_status='0' \n");
					strQuery.append("   and cr_team=?     \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt =  new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, dataObj.get("DaeSign"));
					pstmt.setString(2,dataObj.get("Frm_User"));
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
					pstmt.executeUpdate();
					pstmt.close();
				}
			} else {
				strQuery.setLength(0);
				strQuery.append("update cmr9900              \n");
				strQuery.append("   set cr_team=?            \n");
				strQuery.append(" where cr_status='0'        \n");
				strQuery.append("   and cr_baseusr=?         \n");
				strQuery.append("   and cr_baseusr<>cr_team  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1,dataObj.get("Frm_User"));
				pstmt.setString(2,dataObj.get("Frm_User"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.commit();
			conn.close();
			pstmt = null;
			conn = null;
			
			
			rtn_cnt = 2;
			if (dataObj.get("Opt_Cd0").equals("true")) {
				rtn_cnt = 1;
			}
			dataObj.clear();
			dataObj = null;
			
			return rtn_cnt;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1100.get_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.get_Update() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.get_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.get_Update() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.get_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public String Check_Confirm(String UserId,boolean daeSw) throws SQLException, Exception {
		Connection        conn        = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String               rtnMsg   = "OK";
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			if (daeSw == true) {
				strQuery.setLength(0);
				strQuery.append("select GYULCNT(?) msg from dual \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, UserId);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					rtnMsg = rs.getString("msg");
				}
				rs.close();
				pstmt.close();
			}
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			return rtnMsg;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1100.Check_Confirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.Check_Confirm() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.Check_Confirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.Check_Confirm() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null) 	try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.Check_Confirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getCbo_User(String UserId,String Sv_Admin) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>	rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cm_userid,cm_username   \n");
			strQuery.append("  from cmm0040                 \n");
		    if (!Sv_Admin.equals("Y")) {
		    	strQuery.append(" where cm_userid=?         \n");	//UserId
		    } else {
		    	strQuery.append(" where cm_active='1'       \n");	//UserId
		    }
		    strQuery.append(" order by cm_username \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			if (!Sv_Admin.equals("Y")) pstmt.setString(1, UserId);
            rs = pstmt.executeQuery();            
            rtList.clear();
            /*
            if (Sv_Admin.equals("Y")){
            rst = new HashMap<String,String>();
			rst.put("cm_userid", "0");
			rst.put("cm_username", "선택하세요");
			rst.put("cm_codename", "0");
			rtList.add(rst);
            }*/
            
            while (rs.next()) {
				rst = new HashMap<String,String>();
				//rst.put("ID", "Cbo_User");
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("username", rs.getString("cm_username") + "  [" + rs.getString("cm_userid") + "]");
				rtList.add(rst);
				rst = null;
			}
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
			ecamsLogger.error("## Cmm1100.get_Update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.get_Update() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.get_Update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.get_Update() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.get_Update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	

	public Object[] getDaegyulList(String UserId)  throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>	rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
		ConnectionContext connectionContext = new ConnectionResource();	
		
		try {
			conn = connectionContext.getConnection();
			
            strQuery.setLength(0);
		    strQuery.append("select b.cm_userid,b.cm_username,a.cm_blankdts,a.cm_blankdte,a.cm_daegmsg,a.cm_daegyul,a.cm_daesayu \n");
		    strQuery.append("  from cmm0040 a, cmm0040 b \n");
		    //strQuery.append(" where a.cm_status='9' \n");
		    strQuery.append(" where  a.cm_userid = ? \n");
		    strQuery.append("  and  a.cm_daegyul= b.cm_userid(+) \n");
		    strQuery.append("  and  a.cm_blankdts is not null \n");
//			strQuery.append(" and a.cm_blankdts>=to_date(SYSDATE, 'yyyy/mm/dd')		\n");
		    //strQuery.append("  and a.cm_blankdts<=to_char(SYSDATE, 'yyyymmdd') \n");
		    strQuery.append("  and a.cm_blankdte>=to_char(SYSDATE,'yyyymmdd') \n");
			
		    //pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserId);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("sedate", rs.getString("cm_blankdts")+ " - " +rs.getString("cm_blankdte"));
				rst.put("cm_daegmsg", rs.getString("cm_daegmsg"));
				if (rs.getString("cm_daegyul") != null && rs.getString("cm_daegyul") != "")
				    rst.put("cm_daegyul", rs.getString("cm_daegyul"));
				rst.put("cm_daesayu", rs.getString("cm_daesayu"));				
				rtList.add(rst);
				rst = null;
			}
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
			ecamsLogger.error("## Cmm1100.getDaegyulList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.getDaegyulList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.getDaegyulList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.getDaegyulList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.getDaegyulList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public Object[] getDaegyulState(String UserId)  throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		Object[]		  rtObj		  = null;
		String 			  SysDt		  = null;
		ArrayList<HashMap<String, String>>	rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
		UserInfo		  userInfo	  = new UserInfo();
		HashMap<String,String>	rData = null;
		Object[] uInfo = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
            strQuery.append("select to_char(SYSDATE, 'yyyymmdd') as sysdt from dual 	\n");
            pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();
            if (rs.next()) {
            	SysDt = rs.getString("sysdt");
            }
            rs.close();
            pstmt.close();
            strQuery.setLength(0);
            strQuery.append("select NVL(cm_status,'0') as ustate,cm_daegyul,cm_blankdts,cm_blankdte  from cmm0040 	\n");
            strQuery.append(" where cm_userID=? 												\n");  //Frm_User
            strQuery.append("   and cm_active='1' 												\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	rst = new HashMap<String,String>();
            	rst.put("cm_status", rs.getString("ustate"));
            	rst.put("status", "0");
            	if (rs.getString("cm_blankdts") != null){
	            	if (rs.getString("cm_blankdts").compareTo(SysDt) <=0 && rs.getString("cm_blankdte").compareTo(SysDt) >=0){
	            		rst.put("status", "9");
	            		//ecamsLogger.error("2222 : " +rs.getString("cm_blankdts").compareTo(SysDt));
	            		if (rs.getString("cm_daegyul") != null && rs.getString("cm_daegyul") != "" && !rs.getString("cm_daegyul").equals("")){
				        	uInfo = userInfo.getUserInfo(rs.getString("cm_daegyul"));
				        	rData = (HashMap<String, String>) uInfo[0];
							rst.put("Lbl_Tit", "부재등록 상태입니다. (대결인 : " + rData.get("cm_username") + ")");
							rData = null;
							uInfo = null;
						}
						else{
							rst.put("Lbl_Tit", "부재등록 상태입니다.");
						}            		
	            	}
            	}
               	rst.put("cm_blankdts", rs.getString("cm_blankdts"));
                rst.put("cm_blankdte", rs.getString("cm_blankdte"));
                
				rtList.add(rst);
				rst = null;
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            userInfo = null;
            rs = null;
            pstmt = null;
            conn = null;
            
    		rtObj =  rtList.toArray();
    		rtList.clear();
    		rtList = null;
    		
    		return rtObj;
    		
    		
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1100.getDaegyulState() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.getDaegyulState() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.getDaegyulState() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.getDaegyulState() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.getDaegyulState() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
	public Object[] getCbo_User_Click(String UserId, String cm_manid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>	rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        
		ConnectionContext connectionContext = new ConnectionResource();		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cm_userid,cm_username from cmm0040  \n");
		    strQuery.append(" where cm_project in (select cm_deptcd     \n");	
		    strQuery.append("                        from (select * from cmm0100 where cm_useyn='Y') \n");	
		    strQuery.append("                       start with cm_deptcd=(select cm_project from cmm0040  \n");	
		    strQuery.append("                                              where cm_userid=?) \n");		
		    strQuery.append("                       connect by prior cm_deptcd = cm_updeptcd) \n");	
		    strQuery.append("   and cm_active='1' \n");	
		    strQuery.append("   and cm_userid<>?  \n");
		    strQuery.append(" order by cm_username \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			pstmt.setString(1, UserId);
			pstmt.setString(2, UserId);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
            rs = pstmt.executeQuery();
            
            rst = new HashMap<String,String>();
			rst.put("cm_userid", "0");
			rst.put("cm_username", "선택하세요");
			rst.put("username", "선택하세요");
			rtList.add(rst);
			
			while(rs.next()) {
				rst = new HashMap<String,String>();
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("username", rs.getString("cm_username") +"  ["+ rs.getString("cm_userid") +"]");
				rtList.add(rst);
				rst = null;
			}
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
			ecamsLogger.error("## Cmm1100.getCbo_User_Click() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1100.getCbo_User_Click() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1100.getCbo_User_Click() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1100.getCbo_User_Click() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1100.getCbo_User_Click() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
}//end of Cmm1100 class statement
