
/*****************************************************************************************
	1. program ID	: Holiday_Check.java
	2. create date	: 2008.05. 16
	3. author       : k.m.s
	4. update date	:
	5. auth		    :
	6. description	: Holiday Check
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Holiday_Check{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 오늘이 휴일인지 체크
	 * @param
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String SelectHoli() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  HoliSw	  = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) as cnt from cmm0050                     \n");
			strQuery.append(" where cm_holiday=to_char(SYSDATE,'yyyymmdd')           \n");
			strQuery.append("   and cm_closedt is null                               \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			if (rs.next()){
			   HoliSw = Integer.toString(rs.getInt("cnt"));
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return HoliSw;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Holiday_Check.SelectHoli() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Holiday_Check.SelectHoli() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Holiday_Check.SelectHoli() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Holiday_Check.SelectHoli() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Holiday_Check.SelectHoli() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectHoli() method statement

	/**
	 *  근무 일수 or 계획지연 일수 계산
	 *  cmm0050 휴일정보 테이블과 토/일 제외한 평일 일수 계산
	 * @param Sdate
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String WorkDayAccount(String Sdate) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  workDay	  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("    WITH POLARIS AS                                             \n");
			strQuery.append("    (                                                           \n");
			strQuery.append("     SELECT cm_holiday as dt FROM cmm0050                       \n");
			strQuery.append("    )                                                           \n");
			strQuery.append("    SELECT COUNT(*) day                                         \n");
			strQuery.append("      FROM (SELECT TO_CHAR(sdt + LEVEL - 1, 'yyyymmdd') dt      \n");
			strQuery.append("                 , TO_CHAR(sdt + LEVEL - 1, 'd') day            \n");
			strQuery.append("              FROM (SELECT TO_DATE(?, 'yyyymmdd') sdt  \n");
			strQuery.append("                         , sysdate edt                          \n");
			strQuery.append("                      FROM dual)                                \n");
			strQuery.append("            CONNECT BY LEVEL <= edt - sdt                       \n");
			strQuery.append("            ) a                                                 \n");
			strQuery.append("         , cmm0050 b                                            \n");
			strQuery.append("     WHERE a.dt = b.cm_holiday(+)                               \n");
			strQuery.append("       AND a.day NOT IN ('1', '7')                              \n");
			strQuery.append("       AND b.cm_holiday IS NULL                                 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, Sdate);
            rs = pstmt.executeQuery();
			if (rs.next()){
				workDay = Integer.toString(rs.getInt("day"));
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return workDay;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Holiday_Check.WorkDayAccount() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Holiday_Check.WorkDayAccount() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Holiday_Check.WorkDayAccount() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Holiday_Check.WorkDayAccount() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Holiday_Check.WorkDayAccount() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectHoli() method statement

	/** 영업일이 맞는지 체크
	 * @param (String)inputDay : 입력날짜 ex)20121125
	 * @return 0:영업일  1:주말 또는 휴일  2:주말 이면서 공휴일
	 * @throws SQLException
	 * @throws Exception
	 */
	public int chkBusinessDay(String inputDay) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               returnCount = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select SUM(cnt1 + cnt2) cnt from \n");
			strQuery.append("(select count(*) cnt1, decode(to_char(to_date(?,'YYYYMMDD'),'D'),1,1,7,1,0) cnt2 \n");
			strQuery.append("   from cmm0050 where cm_holiday=?) \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, inputDay);
			pstmt.setString(2, inputDay);
            rs = pstmt.executeQuery();
			if (rs.next())
			{
				returnCount = rs.getInt("cnt");
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return returnCount;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Holiday_Check.chkBusinessDay() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Holiday_Check.chkBusinessDay() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Holiday_Check.chkBusinessDay() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Holiday_Check.chkBusinessDay() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Holiday_Check.chkBusinessDay() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of chkBusinessDay() method statement

}//end of Holiday_Check class statement
