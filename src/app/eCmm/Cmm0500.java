/*****************************************************************************************
	1. program ID	: Cmm0500.java
	2. create date	: 2008.12. 11
	3. auth		    : NO name
	4. update date	:
	5. auth		    :
	6. description	: [관리자] -> 메뉴관리
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

import app.common.CreateXml;
//import app.common.LoggableStatement;


public class Cmm0500{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 등록된 메뉴를 조회합니다.
	 * @param  String sqlGB > NEW:신규, LOW:하위메뉴
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getMenuList(String sqlGB) throws SQLException, Exception{
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
			strQuery.append("select cm_menucd,cm_maname,cm_filename,cm_reqcd from cmm0081 ");
			if (sqlGB.equals("LOW")){
				strQuery.append("where cm_befmenu=0 ");
				strQuery.append("order by cm_order ");
			}else if (sqlGB.equals("NEW")){
		        strQuery.append("order by cm_maname ");
			}else if (sqlGB.equals("999")){
				strQuery.append("where cm_befmenu=999 ");
				strQuery.append("order by cm_maname ");
			}

            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            if(sqlGB.equals("NEW")){
				rst = new HashMap<String, String>();
				rst.put("ID", sqlGB);
				rst.put("cm_menucd", "000");
				rst.put("cm_maname", "신규");
				rst.put("cm_filename", "");
				rst.put("cm_reqcd", "");
				rsval.add(rst);
				rst = null;
            }

            while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", sqlGB);
				rst.put("cm_menucd", rs.getString("cm_menucd"));
				rst.put("cm_maname", rs.getString("cm_maname"));
				rst.put("cm_filename", rs.getString("cm_filename"));
				rst.put("cm_reqcd", rs.getString("cm_reqcd"));
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
			ecamsLogger.error("## Cmm0500.getMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.getMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0500.getMenuList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.getMenuList() Exception END ##");
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
					ecamsLogger.error("## Cmm0500.getMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	/**
	 * 하위메뉴 조회합니다.
	 * @param  int
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getLowMenuList(String Cbo_Menu) throws SQLException, Exception{
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
			strQuery.append("select cm_menucd,cm_maname from cmm0081 ");
			strQuery.append("where cm_befmenu=? ");
			strQuery.append("order by cm_order ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setInt(1, Integer.parseInt(Cbo_Menu));
            rs = pstmt.executeQuery();

            while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_menucd", rs.getString("cm_menucd"));
				rst.put("cm_maname", rs.getString("cm_maname"));
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
			ecamsLogger.error("## Cmm0500.getLowMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.getLowMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0500.getLowMenuList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.getLowMenuList() Exception END ##");
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
					ecamsLogger.error("## Cmm0500.getLowMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	/**
	 * 메뉴리스트 트리구조 조회
	 * @param
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getMenuAllList() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("Select cm_menucd,cm_maname from cmm0081 ");
			strQuery.append("where cm_befmenu=0 ");
			strQuery.append("order by cm_order ");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            while (rs.next()){
            	// 트리에 부모가되는 데이터 추가
            	rst = new HashMap<String, String>();
            	rst.put("cm_menucd", rs.getString("cm_menucd"));
            	rst.put("cm_maname", rs.getString("cm_maname"));
            	rst.put("colorsw", ""); // 컬럼 라인 색상 변경용 추가 테스트
            	rsval.add(rst);
				rst = null;
				
            	strQuery.setLength(0);
    			strQuery.append("Select cm_menucd,cm_maname,cm_filename from cmm0081 ");
    			strQuery.append("where cm_befmenu=? ");
    			strQuery.append("order by cm_order ");
                pstmt2 = conn.prepareStatement(strQuery.toString());
                pstmt2.setInt(1, rs.getInt("cm_menucd"));
                rs2 = pstmt2.executeQuery();
                
                while (rs2.next()){
					rst = new HashMap<String, String>();
					rst.put("maname", rs.getString("cm_maname"));
					rst.put("parentMenucd", rs.getString("cm_menucd"));
					rst.put("cm_menucd", rs2.getString("cm_menucd"));
					rst.put("cm_maname", rs2.getString("cm_maname"));
					rst.put("cm_filename", rs2.getString("cm_filename"));
					rst.put("colorsw", "");	// 컬럼 라인 색상 변경용 추가 테스트
					rsval.add(rst);
					rst = null;
                }
                rs2.close();
                pstmt2.close();
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
			ecamsLogger.error("## Cmm0500.getMenuAllList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.getMenuAllList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0500.getMenuAllList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.getMenuAllList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0500.getMenuAllList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	/**
	 * 메뉴리스트 트리구조 조회
	 * @param
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getMenuTree() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_menucd,cm_order,cm_befmenu,cm_maname,cm_filename ");
			strQuery.append("from (Select cm_menucd,cm_order,cm_befmenu,cm_maname,cm_filename from cmm0081) ");
			strQuery.append("start with cm_befmenu=0 ");
			strQuery.append("connect by prior cm_menucd = cm_befmenu ");			
            pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();
            rtList.clear();

			rst = new HashMap<String, String>();
			rst.put("idKey", "0");
			rst.put("cm_menucd", "0");
			rst.put("cm_order", "1");
			rst.put("cm_maname", "메뉴체계");
			rst.put("cm_filename", "");
			rst.put("cm_befmenu", "-1");
			rtList.add(rst);
			rst = null;
			String menucd = "";
			
            while (rs.next()){
            	rst = new HashMap<String, String>();  	
    			rst.put("cm_menucd", rs.getString("cm_menucd"));
    			rst.put("cm_order", rs.getString("cm_order"));
    			rst.put("cm_maname", rs.getString("cm_maname"));
    			rst.put("cm_filename", rs.getString("cm_filename"));
    			    			
            	if(rs.getString("cm_befmenu").equals("0")) {
            		rst.put("idKey", rs.getString("cm_order"));
            		menucd = rs.getString("cm_order");
            		rst.put("cm_befmenu", "0");
            	}else {            		
            		rst.put("idKey", menucd+"_"+rs.getString("cm_order"));
            		rst.put("cm_befmenu", menucd);
            	}
            	
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

			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0500.getMenuTree() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.getMenuTree() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0500.getMenuTree() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.getMenuTree() Exception END ##");
			throw exception;
		}finally{
			if (rtObj != null)	rtObj = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0500.getMenuTree() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	/**
	 * 신규 또는 기존메뉴 저장
	 * @param  String Cbo_MaCode,String Txt_MaCode,String Txt_MaFile
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String setMenuInfo(String Cbo_MaCode,String Txt_MaCode,String Txt_MaFile, String reqcd) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			int MenuSeq = 0;
			int parmCnt = 0;

			boolean insertFg = false;

		    if (Cbo_MaCode.equals("000")){
		    	strQuery.setLength(0);
		    	strQuery.append("select count(*) as cnt from cmm0081 where ");
		    	strQuery.append("cm_maname=? ");//Txt_MaCode

		    	pstmt = conn.prepareStatement(strQuery.toString());
		    	pstmt.setString(1, Txt_MaCode);
		    	rs = pstmt.executeQuery();

		    	if (rs.next()){
		    	   if (rs.getInt("cnt")==0){
		    		   insertFg = true;
		    		   strQuery.setLength(0);
		    		   strQuery.append("select max(cm_menucd) as max from cmm0081 ");
		    		   pstmt2 = conn.prepareStatement(strQuery.toString());
		    		   rs2 = pstmt2.executeQuery();
		    		   if(rs2.next()){
		    			   if (rs2.getString("max") != null){
		    				   MenuSeq = Integer.parseInt(rs2.getString("max")) + 1;
		    			   }else{
		    				   MenuSeq = 1;
		    			   }
		    		   }
		    		   rs2.close();
		    		   pstmt2.close();
		    	   }else{
		    		   throw new Exception("기 등록한 메뉴명과 동일합니다. 확인 후 다시 하십시오.");
		    	   }
		    	}
		    	rs.close();
		    	pstmt.close();
		    }else{
		       MenuSeq = Integer.parseInt(Cbo_MaCode);
		    }

		    conn.setAutoCommit(false);

		    strQuery.setLength(0);
		    if (insertFg){
		    	strQuery.append("insert into cmm0081 (CM_BEFMENU,CM_MANAME,CM_FILENAME ");
		    	if(!reqcd.equals("00")){
		    		strQuery.append(",CM_REQCD ");
		    	}
		    	strQuery.append(",CM_MENUCD)");

		    	strQuery.append("values (999,?,? ");//Txt_MaCode Txt_MaFile MenuSeq

		    	if(!reqcd.equals("00")){
		    		strQuery.append(",? ");
		    	}
		    	strQuery.append(",?)");
		    }else{
		    	strQuery.append("update cmm0081 set cm_maname=? ");//Txt_MaCode
		    	strQuery.append(",cm_filename=? ");//Txt_MaFile
		    	if(!reqcd.equals("00")){
		    		strQuery.append(",cm_reqcd=? ");//Cbo_reqcd
		    	}
		    	strQuery.append("where cm_menucd=? ");//MenuSeq
		    }
		    pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt =  new LoggableStatement(conn, strQuery.toString());
	    	pstmt.setString(++parmCnt, Txt_MaCode);
		    pstmt.setString(++parmCnt, Txt_MaFile);
		    if(!reqcd.equals("00")){
		    	pstmt.setString(++parmCnt, reqcd);
	    	}
		    pstmt.setInt(++parmCnt, MenuSeq);
		    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    pstmt.executeUpdate();

		    conn.commit();
		    conn.setAutoCommit(true);

            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return "";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0500.setMenuInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.setMenuInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0500.setMenuInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.setMenuInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0500.setMenuInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	/**
	 * 메뉴리스트 저장
	 * @param  String, String, ArrayList
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String setMenuList(String Cbo_selMenu,String Cbo_Menu,
			ArrayList<HashMap<String,String>> Lst_DevStep) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			conn.setAutoCommit(false);

			int X=0;
		    if (Cbo_selMenu.equals("상위메뉴")){
		    	for (X=0 ; X<Lst_DevStep.size() ; X++){
		    		strQuery.setLength(0);
		    		strQuery.append("update cmm0081 set cm_order=?, ");
		    		strQuery.append("cm_befmenu=0 ");
		    		strQuery.append("where cm_menucd=? ");
		    		pstmt = conn.prepareStatement(strQuery.toString());
		//    		pstmt =  new LoggableStatement(conn, strQuery.toString());
		    		pstmt.setInt(1, X+1);
		    		pstmt.setInt(2, Integer.parseInt(Lst_DevStep.get(X).get("cm_menucd")));
		  //  		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    		pstmt.executeUpdate();
		    		pstmt.close();
		    	}
		    }else{
		    	strQuery.setLength(0);
		    	strQuery.append("update cmm0081 set cm_befmenu=999 ");
		    	strQuery.append("where cm_befmenu=? ");
	    		pstmt = conn.prepareStatement(strQuery.toString());
	    		pstmt.setInt(1, Integer.parseInt(Cbo_Menu));
	    		pstmt.executeUpdate();
	    		pstmt.close();

		    	for (X=0 ; X<Lst_DevStep.size() ; X++){
		    		strQuery.setLength(0);
		    		strQuery.append("update cmm0081 set cm_order=?, ");
		    		strQuery.append("cm_befmenu=? ");
		    		strQuery.append("where cm_menucd=? ");
		    		pstmt = conn.prepareStatement(strQuery.toString());
		    	//	pstmt =  new LoggableStatement(conn, strQuery.toString());
		    		pstmt.setInt(1, X+1);
		    		pstmt.setInt(2, Integer.parseInt(Cbo_Menu));
		    		pstmt.setInt(3, Integer.parseInt(Lst_DevStep.get(X).get("cm_menucd")));
		    	//	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    		pstmt.executeUpdate();
		    		pstmt.close();
		    	}
		    }

		    conn.commit();
		    conn.setAutoCommit(true);

            pstmt.close();
            conn.close();

            strQuery = null;
            pstmt = null;
            conn = null;

			return "";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0500.setMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.setMenuList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0500.setMenuList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.setMenuList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0500.setMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	/**
	 * 메뉴정보 삭제
	 * @param  String Cbo_MaCode
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String delMenuInfo(String Cbo_MaCode) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmm0081 where cm_befmenu=? ");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, Cbo_MaCode);
	    	rs = pstmt.executeQuery();
	    	if (rs.next()){
	    		if (rs.getInt("cnt") > 0){
	    			throw new Exception("해당메뉴를 상위메뉴로 사용하는 하위메뉴가 있습니다. 하위메뉴 먼저 삭제 후 처리하십시오.");
		    	}
	    	}
	    	rs.close();
	    	pstmt.close();

	    	conn.setAutoCommit(false);
	    	strQuery.setLength(0);
	    	strQuery.append("delete cmm0090 where cm_menucd=? ");
	    	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, Cbo_MaCode);
		    pstmt.executeUpdate();

	    	strQuery.setLength(0);
		    strQuery.append("delete cmm0081 where cm_menucd=? ");
		    pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, Cbo_MaCode);
		    pstmt.executeUpdate();

		    conn.commit();
		    conn.setAutoCommit(true);
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return "";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0500.setMenuInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0500.setMenuInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0500.setMenuInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0500.setMenuInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0500.setMenuInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}