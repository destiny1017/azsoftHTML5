
/*****************************************************************************************
	1. program ID	: TeamInfo
	2. create date	: 2008.05. 26
	3. auth		    : 
	4. update date	: 
	5. auth		    : 
	6. description	: TeamInfo
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;



/**
 * @author bigeyes
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class TeamInfo{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 조직도 트리
	 * @param  
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
 
    public Object[] getTeamInfoGrid2(String SelMsg,String cm_useyn,String gubun,String itYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>	rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
		String            strSelMsg   = "";
		int				  index = 0;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			if (SelMsg != "" && SelMsg != null) {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if(SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg   = "";
				}	
			}
			
			strQuery.setLength(0);
			strQuery.append("select cm_deptcd,cm_deptname \n");
			
			if (itYn.equals("Y")) {
				strQuery.append(" from (Select cm_deptcd,cm_updeptcd,cm_deptname,cm_useyn \n");
				strQuery.append("         from (Select * from cmm0100 where cm_useyn='Y') \n");
				strQuery.append("        start with cm_updeptcd=' 000000100'  				  \n");	//TODO updeptcd 셋팅
				strQuery.append("       connect by prior cm_deptcd = cm_updeptcd) 		  \n");
				strQuery.append(" where cm_useyn='Y'                          		      \n");
			} else if (gubun != "" && gubun != null) {
				if (gubun.equals("DEPT")) {
					strQuery.append(" from (Select cm_deptcd,cm_updeptcd,cm_deptname,cm_useyn \n");
					strQuery.append("         from (Select * from cmm0100  \n");
					strQuery.append("                where cm_useyn='Y'    \n");
					strQuery.append("                  and cm_updeptcd is not null) \n");
					strQuery.append("        start with cm_deptcd in (select cm_project from cmm0040  \n");
					strQuery.append("                                  where cm_active='1'    \n");
					strQuery.append("                                  group by cm_project)   \n");
					strQuery.append("       connect by prior cm_updeptcd = cm_deptcd) 		  \n");
					strQuery.append(" where cm_useyn='Y'                          		      \n");
				} else if (gubun.equals("main")){
					strQuery.append("  from cmm0100 \n");
					strQuery.append(" where cm_useyn='Y' \n");
					strQuery.append(" and CM_UPDEPTCD is null \n");
					//if (itYn.equals("Y")) strQuery.append("and cm_deptcd like '10%'    \n");
				} else if (gubun.equals("sub")) {
					strQuery.append("  from cmm0100 \n");
					strQuery.append(" where cm_useyn='Y' \n");
					strQuery.append("   and cm_deptcd in (select cm_project from cmm0040  \n");
					strQuery.append("                      where cm_active='1'            \n");
					strQuery.append("                      group by cm_project)           \n");
				} else if (gubun.equals("req")){
					strQuery.append("  from cmm0100 \n");
					strQuery.append(" where cm_useyn='Y' \n");
					strQuery.append("   and cm_deptcd not in (select cm_updeptcd from cmm0100 \n");
					strQuery.append("                     where cm_useyn='Y'			  	  \n");
					strQuery.append("                      and cm_updeptcd is not null	  	  \n");
					strQuery.append("					  group by cm_updeptcd           )	  \n");	
				} else {
					strQuery.append("  from cmm0100 \n");
					strQuery.append(" where cm_useyn='Y' \n");
					strQuery.append(" and CM_UPDEPTCD = ?                               \n");
				}
			} else {
				strQuery.append("  from cmm0100 \n");
				strQuery.append(" where cm_useyn='Y' \n");
			}
			strQuery.append(" group by cm_deptcd,cm_deptname \n"); 	
			strQuery.append(" order by cm_deptcd,cm_deptname \n"); 			

            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            if(gubun != "" && gubun != null) {
            	if (!gubun.equals("main") && !gubun.equals("sub") && !gubun.equals("req") && !gubun.equals("DEPT")){
            		pstmt.setString(1, gubun);
            	}
            }
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());  
            rs = pstmt.executeQuery();
            
            rtList.clear();
            
			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
					rst = new HashMap<String, String>();
					rst.put("ID", "0");
					rst.put("cm_deptname", strSelMsg);
					rst.put("cm_deptcd", "0");
					rtList.add(rst);
					rst = null;
				}	
				index = 0;
				if(rs.getString("cm_deptname").indexOf(" ") != -1){
					index = rs.getString("cm_deptname").indexOf(" ")+1;
				}
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_deptcd", rs.getString("cm_deptcd"));
				rst.put("cm_deptname", rs.getString("cm_deptname").substring(index));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return rtList.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamInfoGrid() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTeamInfoGrid() method statement	
    
	
	public Object[] getTeamInfoGrid(String SelMsg,String cm_useyn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>	rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
		String            strSelMsg   = "";
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			if (SelMsg != "" && SelMsg != null) {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if(SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg   = "";
				}
			}			
			
			strQuery.append("select cm_deptcd,cm_deptname \n");
			strQuery.append("  from cmm0100               \n");
			strQuery.append(" where cm_useyn='Y'          \n");
			strQuery.append("   and cm_deptcd in (select cm_project from cmm0040  \n");
			strQuery.append("                      where cm_active='1'            \n");
			strQuery.append("                      group by cm_project)           \n");
			strQuery.append("order by cm_deptname         \n"); 			

            pstmt = conn.prepareStatement(strQuery.toString());	
            
            rs = pstmt.executeQuery();
            
            rtList.clear();
            
			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
					rst = new HashMap<String, String>();
					rst.put("ID", "0");
					rst.put("cm_deptname", strSelMsg);
					rst.put("cm_deptcd", "");
					rtList.add(rst);
					rst = null;
				}	
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_deptcd", rs.getString("cm_deptcd"));
				rst.put("cm_deptname", rs.getString("cm_deptname"));
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
			rtList = null;
			return rtObj;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoGrid() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamInfoGrid() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		

		
	}//end of getTeamInfoGrid() method statement	

	public Document getTeamInfoTree_old() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml		  myXml		  = new CreateXml();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.append("Select cm_deptcd, cm_updeptcd, cm_deptname        \n");
			strQuery.append("from (Select * from cmm0100 where cm_useyn='Y')   \n");
			strQuery.append("start with cm_updeptcd is null  				   \n");
			strQuery.append("connect by prior cm_deptcd = cm_updeptcd 		   \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            
            myXml.init_Xml("ID","cm_deptcd","cm_deptname","cm_updeptcd");
            
            rs = pstmt.executeQuery();
            
            while (rs.next()){
				myXml.addXML(rs.getString("cm_deptcd"),
						rs.getString("cm_deptcd"), 
						rs.getString("cm_deptname"),
						rs.getString("cm_updeptcd"));
			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			conn = null;
			
            return myXml.getDocument();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTree() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTree() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTree() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTree() Exception END ##");
			throw exception;
		}finally{
			if (myXml != null)	myXml = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamInfoTree() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		
	}//end of getTeamInfoTree() method statement

	
	public Document getTeamInfoTree() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml		  myXml		  = new CreateXml();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.append("Select cm_deptcd, cm_updeptcd, cm_deptname        \n");
			strQuery.append("from (Select * from cmm0100 where cm_useyn='Y')   \n");
			strQuery.append("start with cm_updeptcd is null  				   \n");
			strQuery.append("connect by prior cm_deptcd = cm_updeptcd 		   \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            myXml.init_Xml("ID","cm_deptcd","cm_deptname","cm_updeptcd"); 
            rs = pstmt.executeQuery();
            
            while (rs.next()){
				myXml.addXML(rs.getString("cm_deptcd"),
						rs.getString("cm_deptcd"), 
						rs.getString("cm_deptname"),
						rs.getString("cm_updeptcd"));
			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			conn = null;
			
            return myXml.getDocument();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTreeCheckBox() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTreeCheckBox() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTreeCheckBox() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTreeCheckBox() Exception END ##");
			throw exception;
		}finally{
			if (myXml != null)	myXml = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamInfoTreeCheckBox() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTeamInfoTreeCheckBox() method statement
	
	public ArrayList<HashMap<String, String>> getTeamInfoTree_new(boolean subSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		HashMap<String, String> teamInfoMap 			= null;
		ArrayList<HashMap<String, String>> teamInfoArr 	= new ArrayList<HashMap<String, String>>();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.append("SELECT CM_DEPTCD AS id,                         		\n");
			strQuery.append("		NVL(CM_UPDEPTCD,'-1') AS pid, 					\n");
			strQuery.append("		CM_DEPTNAME AS text                             \n");
			strQuery.append("  FROM cmm0100                                     	\n");
			strQuery.append(" WHERE CM_USEYN = 'Y'                              	\n");
			strQuery.append(" START WITH cm_updeptcd IS NULL                    	\n");
			strQuery.append(" CONNECT BY PRIOR cm_deptcd = CM_UPDEPTCD          	\n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            rs = pstmt.executeQuery();
            
            while (rs.next()){
            	teamInfoMap = new HashMap<String, String>();
            	teamInfoMap.put("id", rs.getString("id"));
            	teamInfoMap.put("pid", rs.getString("pid"));
            	teamInfoMap.put("text", rs.getString("text"));
            	teamInfoMap.put("division", "depart");
            	teamInfoMap.put("imagesrc"			, "/img/folderDefaultClosed.gif");
				if (subSw) {
					strQuery.setLength(0);
					strQuery.append("select b.cm_codename,a.cm_username,a.cm_userid \n");
					strQuery.append("  from cmm0020 b,cmm0040 a          \n");
					strQuery.append(" where a.cm_project=?               \n");
					strQuery.append("   and a.cm_active='1'              \n");
					strQuery.append("   and b.cm_macode='POSITION'       \n");
					strQuery.append("   and b.cm_micode=a.cm_position    \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());	
					pstmt2.setString(1,rs.getString("id"));
		            rs2 = pstmt2.executeQuery(); 
		            while (rs2.next()) {
		            	teamInfoArr.add(teamInfoMap);
		            	teamInfoMap = new HashMap<String, String>();
		            	
		            	teamInfoMap.put("id", rs2.getString("cm_userid"));
		            	teamInfoMap.put("pid", rs.getString("id"));
		            	teamInfoMap.put("text", "["+rs2.getString("cm_codename")+"]"+rs2.getString("cm_username"));
		            	teamInfoMap.put("division", "user");
		            	teamInfoMap.put("imagesrc"			, "/img/person.png");
		            }
		            rs2.close();
		            pstmt2.close();
				}
				teamInfoArr.add(teamInfoMap);
			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			
			return teamInfoArr;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTeamInfoTree_new() method statement

	/*public Document getTeamInfoTree_new(boolean subSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml		  myXml		  = new CreateXml();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.append("Select cm_deptcd, cm_updeptcd, cm_deptname        \n");
			strQuery.append("from (Select * from cmm0100 where cm_useyn='Y')   \n");
			strQuery.append("start with cm_updeptcd is null  				   \n");
			strQuery.append("connect by prior cm_deptcd = cm_updeptcd 		   \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            myXml.init_Xml("ID","cm_deptcd","cm_deptname","cm_userid","cm_username","deptname","isBranch","cm_updeptcd"); 
            rs = pstmt.executeQuery();
            
            while (rs.next()){
				myXml.addXML("D"+rs.getString("cm_deptcd"),
						rs.getString("cm_deptcd"), 
						rs.getString("cm_deptname"),
						"",
						"", 
						rs.getString("cm_deptname"),
						"true", 
						"D"+rs.getString("cm_updeptcd"));
				if (subSw) {
					strQuery.setLength(0);
					strQuery.append("select b.cm_codename,a.cm_username,a.cm_userid \n");
					strQuery.append("  from cmm0020 b,cmm0040 a          \n");
					strQuery.append(" where a.cm_project=?               \n");
					strQuery.append("   and a.cm_active='1'              \n");
					strQuery.append("   and b.cm_macode='POSITION'       \n");
					strQuery.append("   and b.cm_micode=a.cm_position    \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());	
					pstmt2.setString(1,rs.getString("cm_deptcd"));
		            rs2 = pstmt2.executeQuery(); 
		            while (rs2.next()) {
		            	myXml.addXML("U"+rs2.getString("cm_userid"),
		            			rs.getString("cm_deptcd"),
								"["+rs2.getString("cm_codename")+"]"+rs2.getString("cm_username"),
								rs2.getString("cm_userid"),
								rs2.getString("cm_username"), 
								rs.getString("cm_deptname"),
								"false", 
								"D"+rs.getString("cm_deptcd"));
		            }
		            rs2.close();
		            pstmt2.close();
				}
			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			
            return myXml.getDocument();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() Exception END ##");
			throw exception;
		}finally{
			if (myXml != null)	myXml = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamInfoTree_new() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTeamInfoTree_new() method statement
*/	
	///////////////////////////////////////////////////////////////////////
	// 조직도 트리의 노드 클릭시 부서에 포함된 사원조회 쿼리 
	// UserName,DeptCd,DeptName
	// 1) UserName,"",""  => 성명으로 검색 
	// 2) "",DeptCd,""    => 조직도 tree's node(부서) click 시 부서의 구성원 검색 
	// 3) "","",DeptName  => 부서명으로 검색
	///////////////////////////////////////////////////////////////////////
	public Object[] getTeamNodeInfo(String UserName,String DeptCd,String DeptName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>	rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst	   = null;
		int				  Cnt         = 0;
		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.append("Select A.CM_USERID,A.CM_PROJECT,A.CM_USERNAME,A.CM_DUTY, \n");
			strQuery.append("B.CM_codeNAME POSITION,D.cm_seqno,C.CM_DEPTNAME,D.CM_CODENAME DUTY \n");
			strQuery.append("FROM cmm0040 A,CMM0020 B,CMM0100 C,CMM0020 D \n");
			strQuery.append("WHERE A.CM_ACTIVE = '1' \n");
		    if (UserName != null && UserName != "")	strQuery.append("AND A.CM_USERNAME LIKE ? \n");  //UserName
		    else if (DeptCd != null && DeptCd != "")	strQuery.append("and A.CM_PROJECT = ? \n");  //DeptCd
		    else if (DeptName != null && DeptName != "") {
		    	strQuery.append("and A.CM_PROJECT in (select CM_DEPTCD from cmm0100 \n");
		    	strQuery.append("                      where  CM_USEYN='Y' \n");
		    	strQuery.append("                        AND  CM_DEPTNAME LIKE ?) \n");  //DeptName
		    }
		    else	strQuery.append("and A.CM_PROJECT in (select CM_DEPTCD from cmm0100 where  CM_USEYN='Y') \n");
		    strQuery.append("AND b.cm_macode='POSITION' and b.cm_micode=A.CM_POSITION  \n");
		    strQuery.append("AND D.CM_MACODE='DUTY' AND D.CM_MICODE=A.CM_DUTY \n");
		    strQuery.append("AND A.CM_PROJECT=C.CM_DEPTCD \n");
		    strQuery.append("ORDER BY B.CM_SEQNO,a.cm_userid \n");
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
		    
            Cnt = 0;
            if (UserName != null && UserName != "")	pstmt.setString(++Cnt, "%"+UserName+"%");   	
            else if (DeptCd != null && DeptCd != "")	pstmt.setString(++Cnt, DeptCd);
            else if (DeptName != null && DeptName != "")	pstmt.setString(++Cnt, "%"+DeptName+"%");
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());  
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("rows", Integer.toString(rs.getRow()));
				rst.put("POSITION",rs.getString("POSITION"));		//직급명
				rst.put("CM_USERNAME",rs.getString("CM_USERNAME"));	//사용자명
				rst.put("DUTY",rs.getString("DUTY"));				//호칭
				rst.put("TEAMNAME",rs.getString("CM_DEPTNAME"));	//팀이름
				rst.put("CM_USERID",rs.getString("CM_USERID"));		//사용자ID
				rst.put("TEAMCD",rs.getString("CM_PROJECT"));		//팀코드
				rst.put("CM_SEQNO",rs.getString("CM_SEQNO"));		//직급명코드
				rst.put("JIKMOO","");								//참여직무
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
			rtList = null;
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamNodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## TeamInfo.getTeamNodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TeamInfo.getTeamNodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## TeamInfo.getTeamNodeInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TeamInfo.getTeamNodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		

		
	}//end of getTeamNodeInfo() method statement
	
}//end of TeamInfo class statement
