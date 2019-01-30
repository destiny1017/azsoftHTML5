
/*****************************************************************************************
	1. program ID	: MenuDAO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 1. 메인화면 및  메뉴 DAO
*****************************************************************************************/

package com.ecams.service.ref.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.ref.valueobject.RefAnalRsltVO;
import com.ecams.service.ref.valueobject.RefAnalVO;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RefAnalDAO{
	

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * @param  req_str
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList selectRefAnalList(String req_str) throws SQLException, Exception {
		Connection        conn          = null;
		Connection        conn2          = null;
		PreparedStatement pstmt         = null;
		ResultSet         rs            = null;
		StringBuffer      strQuery      = new StringBuffer();
		RefAnalVO         refanalvo     = null;
		RefAnalRsltVO     refanalrsltvo = null;
		ArrayList         arraylist     = new ArrayList();
		ArrayList         arraylist_2   = new ArrayList();
		
		 
		ecamsLogger.debug("ConnectionResource() Start");
		ConnectionContext connectionContext = new ConnectionResource();
		ecamsLogger.debug("ConnectionResource() End");	
		try {
			
			conn = connectionContext.getConnection();
				//조회한 리스트를 TEMP에서 PATCH
				strQuery.append("SELECT * FROM CMD0012 WHERE CD_SEQNO = ?");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, req_str);	
	            rs = pstmt.executeQuery();
			
				while (rs.next()){
					refanalvo = new RefAnalVO();
					refanalvo.setCD_SEQNO(rs.getString("CD_SEQNO"));					
					refanalvo.setCD_SYSCD(rs.getString("CD_SYSCD"));
					refanalvo.setCD_JOBCD(rs.getString("CD_JOBCD"));
					refanalvo.setCD_PROGNAME(rs.getString("CD_PROGNAME"));	
					refanalvo.setCD_DIRPATH(rs.getString("CD_DIRPATH"));		
	
					arraylist.add(refanalvo);	
				}//end of while-loop statement	    
				
				
				
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn2 = DriverManager.getConnection("jdbc:oracle:thin:@9.11.1.155:2521:comm", "apan", "apan");							
			for (int i = 0 ; i < arraylist.size(); i++) {
				pstmt    = null;
				rs       = null;
		        strQuery.setLength(0); //STRINGBUFFER INITIALIZED

				/* 진짜 */
				strQuery.append("SELECT DISTINCT ? CD_SEQ    \n");
				strQuery.append("     , ?          CD_SYSCD  \n");
				strQuery.append("     , ?          CD_JOBCD  \n");
				strQuery.append("     , A.SRC_FILE_NM SRC_FILE_NM_1           \n");
				strQuery.append("     , A.ORG_FILE_PTH         ORG_FILE_PTH_1 \n");
				strQuery.append("     , C.SRC_FILE_NM          SRC_FILE_NM_2  \n");
				strQuery.append("     , C.ORG_FILE_PTH         ORG_FILE_PTH_2 \n");
				strQuery.append("  FROM apan.TBCN1064 A      \n");
				strQuery.append("     , apan.TBCN1028 B  \n");
				strQuery.append("     , apan.TBCN1064 C      \n");
				strQuery.append("  WHERE A.PRJ_CD      = ?      -- 업무코드 \n");
				strQuery.append("    AND A.SRC_FILE_NM = ?      -- 화일명   \n");
				strQuery.append("    AND (A.ORG_FILE_PTH = ? or A.ORG_FILE_PTH = substr(?,26,length(?) - 25)) \n");
				strQuery.append("    AND A.FILE_LST_FLG = 'Y'    \n");
				strQuery.append("    AND B.PRJ_CD = A.PRJ_CD     \n");
				strQuery.append("    AND B.FILE_SEQ = A.FILE_SEQ \n");
				strQuery.append("    AND C.PRJ_CD = B.PRJ_CD     \n");
				strQuery.append("    AND C.FILE_SEQ = B.UM_FILE  \n");
				strQuery.append("  UNION \n");
				strQuery.append("SELECT DISTINCT ? CD_SEQ                     \n");
				strQuery.append("     , ?          CD_SYSCD                   \n");
				strQuery.append("     , ?          CD_JOBCD                   \n");
				strQuery.append("     , A.SRC_FILE_NM SRC_FILE_NM_1           \n");
				strQuery.append("     , A.ORG_FILE_PTH         ORG_FILE_PTH_1 \n");
				strQuery.append("     , C.SRC_FILE_NM          SRC_FILE_NM_2  \n");
				strQuery.append("     , C.ORG_FILE_PTH         ORG_FILE_PTH_2 \n");
				strQuery.append("  FROM apan.TBCN1064 A         \n");
				strQuery.append("     , apan.TBCN1039 B  \n");
				strQuery.append("     , apan.TBCN1064 C      \n");
				strQuery.append("  WHERE A.PRJ_CD      = ?      -- 업무코드 \n");
				strQuery.append("    AND A.SRC_FILE_NM = ? -- 화일명 \n");
				strQuery.append("    AND (A.ORG_FILE_PTH = ? or A.ORG_FILE_PTH = substr(?,26,length(?) - 25)) \n");
				strQuery.append("    AND A.FILE_LST_FLG = 'Y'    \n");
				strQuery.append("    AND B.PRJ_CD = A.PRJ_CD     \n");
				strQuery.append("    AND B.FILE_SEQ = A.FILE_SEQ \n");
				strQuery.append("    AND C.PRJ_CD = B.PRJ_CD     \n");
				strQuery.append("    AND C.FILE_SEQ = B.UM_FILE  \n");
				strQuery.append("  UNION \n");
				strQuery.append("SELECT DISTINCT ? CD_SEQ                     \n");
				strQuery.append("     , ?          CD_SYSCD                   \n");
				strQuery.append("     , ?          CD_JOBCD                   \n");
				strQuery.append("     , A.SRC_FILE_NM          SRC_FILE_NM_1  \n");
				strQuery.append("     , A.ORG_FILE_PTH         ORG_FILE_PTH_1 \n");
				strQuery.append("     , C.SRC_FILE_NM          SRC_FILE_NM_2  \n");
				strQuery.append("     , C.ORG_FILE_PTH         ORG_FILE_PTH_2 \n");
				strQuery.append("  FROM apan.TBCN1064 A          \n");
				strQuery.append("     , apan.TBCN1018 B  \n");
				strQuery.append("     , apan.TBCN1064 C          \n");
				strQuery.append("  WHERE A.PRJ_CD      = ? -- 업무코드 \n");
				strQuery.append("    AND A.SRC_FILE_NM = ? -- 화일명   \n");
				strQuery.append("    AND (A.ORG_FILE_PTH = ? or A.ORG_FILE_PTH = substr(?,26,length(?) - 25)) \n");
				strQuery.append("    AND A.FILE_LST_FLG = 'Y'    \n");
				strQuery.append("    AND B.PRJ_CD = A.PRJ_CD     \n");
				strQuery.append("    AND B.FILE_SEQ = A.FILE_SEQ \n");
				strQuery.append("    AND C.PRJ_CD = B.PRJ_CD     \n");
				strQuery.append("    AND C.FILE_SEQ = B.UM_FILE  \n"); 
	
				ecamsLogger.debug(strQuery.toString());
				refanalvo = (RefAnalVO)arraylist.get(i);
				
	            pstmt = conn2.prepareStatement(strQuery.toString());
/*	            
	            pstmt.setString(1, refanalvo.getCD_SEQNO());
	            pstmt.setString(2, refanalvo.getCD_SYSCD());	
	            pstmt.setString(3, refanalvo.getCD_JOBCD());
	            pstmt.setString(4, refanalvo.getCD_SEQNO());
	            pstmt.setString(5, refanalvo.getCD_SYSCD());	
	            pstmt.setString(6, refanalvo.getCD_JOBCD());
	            pstmt.setString(7, refanalvo.getCD_SEQNO());
	            pstmt.setString(8, refanalvo.getCD_SYSCD());	
	            pstmt.setString(9, refanalvo.getCD_JOBCD());
*/	            

	            ecamsLogger.debug(refanalvo.getCD_SEQNO());
	            ecamsLogger.debug(refanalvo.getCD_SYSCD());	
	            ecamsLogger.debug(refanalvo.getCD_JOBCD());	
	            ecamsLogger.debug(refanalvo.getCD_PROGNAME());	
	            ecamsLogger.debug(refanalvo.getCD_DIRPATH());		  
	            
				/* 진짜 */   
	            pstmt.setString(1, refanalvo.getCD_SEQNO());
	            pstmt.setString(2, refanalvo.getCD_SYSCD());	
	            pstmt.setString(3, refanalvo.getCD_JOBCD());	
	            pstmt.setString(4, refanalvo.getCD_JOBCD());	
	            pstmt.setString(5, refanalvo.getCD_PROGNAME());	
	            pstmt.setString(6, refanalvo.getCD_DIRPATH());	
	            pstmt.setString(7, refanalvo.getCD_DIRPATH());
	            pstmt.setString(8, refanalvo.getCD_DIRPATH());
	            pstmt.setString(9, refanalvo.getCD_SEQNO());
	            pstmt.setString(10, refanalvo.getCD_SYSCD());	
	            pstmt.setString(11, refanalvo.getCD_JOBCD());	            
	            pstmt.setString(12, refanalvo.getCD_JOBCD());	
	            pstmt.setString(13, refanalvo.getCD_PROGNAME());  
	            pstmt.setString(14, refanalvo.getCD_DIRPATH());	  
	            pstmt.setString(15, refanalvo.getCD_DIRPATH());	  
	            pstmt.setString(16, refanalvo.getCD_DIRPATH());	 
	            pstmt.setString(17, refanalvo.getCD_SEQNO());
	            pstmt.setString(18, refanalvo.getCD_SYSCD());	
	            pstmt.setString(19, refanalvo.getCD_JOBCD());	            
	            pstmt.setString(20, refanalvo.getCD_JOBCD());	
	            pstmt.setString(21, refanalvo.getCD_PROGNAME()); 
	            pstmt.setString(22, refanalvo.getCD_DIRPATH());	  
	            pstmt.setString(23, refanalvo.getCD_DIRPATH());	  
	            pstmt.setString(24, refanalvo.getCD_DIRPATH());	 	   

	            rs = pstmt.executeQuery();

				//ecamsLogger.debug("00000000000000");
				while (rs.next()){
					refanalrsltvo = new RefAnalRsltVO();
					refanalrsltvo.setCD_SEQNO(rs.getString("CD_SEQ"));	
					refanalrsltvo.setCD_SYSCD(rs.getString("CD_SYSCD"));	
					refanalrsltvo.setCD_JOBCD(rs.getString("CD_JOBCD"));						
					refanalrsltvo.setSRC_FILE_NM_1(rs.getString("SRC_FILE_NM_1"));
					refanalrsltvo.setORG_FILE_PTH_1(rs.getString("ORG_FILE_PTH_1"));
					refanalrsltvo.setSRC_FILE_NM_2(rs.getString("SRC_FILE_NM_2"));
					refanalrsltvo.setORG_FILE_PTH_2(rs.getString("ORG_FILE_PTH_2"));	
   				    //ecamsLogger.debug("11111111111111111");
   				    arraylist_2.add(refanalrsltvo);	
				}//end of while-loop statement	    
   				    //ecamsLogger.debug("2222222222222222222");	
				
				rs.close();
				pstmt.close();
				conn.close();
				rs = null;
				pstmt = null;
				conn = null;
				
			}//end of for-loop statement 
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## RefAnalDAO.selectRefAnalList() SQLException START ##");
			ecamsLogger.error("## access vector size : " + "[" + arraylist.size() +"]");	
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## RefAnalDAO.selectRefAnalList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## RefAnalDAO.selectRefAnalList() Exception START ##");	
			ecamsLogger.error("## access vector size : " + "[" + arraylist.size() +"]");		
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## RefAnalDAO.selectRefAnalList() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## RefAnalDAO.selectRefAnalList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			if (conn2 != null){
				try{
					conn2.close();
				}catch(Exception ex3){
					ecamsLogger.error("## RefAnalDAO.selectRefAnalList() connection release exception ##");
					ex3.printStackTrace();
				}
			}			
		}
		
		return arraylist_2; 
		
	}//end of selectRefAnalList() method statement

	/**
	 * @param  arraylist
	 * @param  chck_str
	 * @return int
	 * @throws SQLException
	 * @throws Exception
	 */
	public int insertRefAnalList(ArrayList arraylist, String[] chck_str) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        int               rtn_cnt     = 0;
        Connection        conn        = null;        
		RefAnalRsltVO     refanalrsltvo = null;
		ConnectionContext connectionContext = new ConnectionResource();
 		
		try { 
			conn = connectionContext.getConnection();	
			
			if (arraylist.size() > 0)
				refanalrsltvo = (RefAnalRsltVO)arraylist.get(0);	
			else
				throw new Exception("CD_SEQNO 없다");

			strQuery.append("DELETE FROM CMD0013 WHERE CD_SEQNO = ? ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, refanalrsltvo.getCD_SEQNO());	 	            
            rtn_cnt = pstmt.executeUpdate();
            //ecamsLogger.debug(rtn_cnt+"건 DELETE " + refanalrsltvo.getCD_SEQNO());
	        pstmt = null;
	        int   chck_str_cnt = chck_str.length;
	        int   loop_seq     = 0;
	        for (int i = 0; i < arraylist.size(); i++) {
	        	if (chck_str_cnt > 0 && chck_str[loop_seq].equals(i+"")){
					refanalrsltvo = (RefAnalRsltVO)arraylist.get(i);	            
		            strQuery.setLength(0);
					strQuery.append("INSERT INTO CMD0013 VALUES (?, ?, ?, ?, ?, ?) \n");
		            pstmt = conn.prepareStatement(strQuery.toString());
		            pstmt.setString(1, refanalrsltvo.getCD_SEQNO());	
		            pstmt.setInt(2,i);	
		            pstmt.setString(3, refanalrsltvo.getCD_SYSCD());	
		            pstmt.setString(4, refanalrsltvo.getCD_JOBCD());	
		            pstmt.setString(5, refanalrsltvo.getSRC_FILE_NM_1());     
		            pstmt.setString(6, refanalrsltvo.getSRC_FILE_NM_2());   

				    //ecamsLogger.debug(strQuery.toString());		            
		            rtn_cnt = pstmt.executeUpdate();
		            chck_str_cnt--;
		            loop_seq    ++;
	        	}
			}//end of for-loop statement
	        
	        pstmt.close();
	        conn.close();
	        pstmt = null;
	        conn = null;
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## RefAnalDAO.insertRefAnalList() SQLException START ##");
			ecamsLogger.error("## access vector size : " + "[" + refanalrsltvo.toString() +"]");	
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## RefAnalDAO.insertRefAnalList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## RefAnalDAO.insertRefAnalList() Exception START ##");	
			ecamsLogger.error("## access vector size : " + "[" + refanalrsltvo.toString() +"]");		
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## RefAnalDAO.insertRefAnalList() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## RefAnalDAO.insertRefAnalList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		return rtn_cnt;
		
	}//end of insertRefAnalList() method statement
	
}//end of RefAnalDAO class statement
