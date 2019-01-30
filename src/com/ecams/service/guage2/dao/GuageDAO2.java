
/*****************************************************************************************
	1. program ID	: MenuDAO.java
	2. create date	: 2006.02. 01
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: 1. 메인화면 및  메뉴 DAO
*****************************************************************************************/

package com.ecams.service.guage2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.service.guage2.valueobject.GuageVO2;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GuageDAO2{
	

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * @param user_id
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<GuageVO2> selectGuage(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		GuageVO2          mvo         = null;
		ArrayList<GuageVO2>         arraylist   = new ArrayList<GuageVO2>();

		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();	
			//1.금일결재건수, 2.체크아웃건수, 3:체크인건수, 4:체크인 건수 중 반려/취소건수
			strQuery.append(" SELECT SUM(DECODE(GB,1,CNT,0)) as cnt1, SUM(DECODE(GB,2,CNT,0)) as cnt2,SUM(DECODE(GB,3,CNT,0)) as cnt3,SUM(DECODE(GB,4,CNT,0)) as cnt4 ");
			strQuery.append(" from(                                                                                                                                   ");         
			strQuery.append("     select '1' as gb, count(*) as cnt from cmr1000 c, cmr9900 a                                                              ");
			strQuery.append("       where trim(a.cr_confusr) =? and                                                                                          ");        
			strQuery.append("             a.cr_locat = '00' and                                                                                             ");         
			strQuery.append("             c.cr_status = '0'                                                                                                   ");         
			strQuery.append("     union                                                                                                                               ");         
			strQuery.append("     select '2' as gb, count(*) as cnt from cmr1000 a                                                                         ");
			strQuery.append("       where a.cr_qrycd = '01' and                                                                                         ");         
			strQuery.append("             a.cr_editor = ?                                                                                                 ");        
			strQuery.append("     union                                                                                                                               ");         
			strQuery.append("     select '3' as gb, count(*) as cnt from cmr1000 a                                                                         ");
			strQuery.append("       where a.cr_qrycd = '04' and                                                                                         ");         
			strQuery.append("             a.cr_editor = ?                                                                                                 ");         
			strQuery.append("     union                                                                                                                               ");         
			strQuery.append("     select '4' as gb, count(*) as cnt from cmr1000 a                                                                         ");
			strQuery.append("       where a.cr_status ='3' and                                                                                                        ");         
			strQuery.append("             a.cr_editor = ?                                                                                                 ");     
			strQuery.append(" )                                                                                                                           ");
            
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);		
            pstmt.setString(2, user_id);		
            pstmt.setString(3, user_id);		
            pstmt.setString(4, user_id);		
			//ecamsLogger.error(strQuery);
            rs = pstmt.executeQuery();
			
			while (rs.next()){
				mvo = new GuageVO2();
				
				mvo.setCnt1(rs.getString("cnt1"));
				mvo.setCnt2(rs.getString("cnt2"));
				mvo.setCnt3(rs.getString("cnt3"));
				mvo.setCnt4(rs.getString("cnt4"));
				
				arraylist.add(mvo);	
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## GuageDAO2.selectGuage() SQLException START ##");
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");	
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## GuageDAO2.selectGuage() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## GuageDAO2.selectGuage() Exception START ##");	
			ecamsLogger.error("## access user_id : " + "[" + user_id +"]");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## GuageDAO2.selectGuage() Exception END ##");				
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## GuageDAO2.selectGuage() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		return arraylist;
		
	}//end of selectMenuList() method statement
}//end of MenuDAO class statement
