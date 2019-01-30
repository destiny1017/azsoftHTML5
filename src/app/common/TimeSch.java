
/*****************************************************************************************
	1. program ID	: TimeSch.java
	2. create date	: 2008.05. 16
	3. auth		    : k.m.s
	4. update date	:
	5. auth		    :
	6. description	: Time Check
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

public class TimeSch{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 요일별 결재시간을 조회합니다.
	 * @param
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

	public String SelectTimeSch() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strHhmm     = null;
		String            strHoli    = "";
		Holiday_Check  holiChk = new Holiday_Check();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strHoli = holiChk.SelectHoli();
			holiChk = null;

			strQuery.append("select cm_comsttime,cm_comedtime,cm_wedsttime,cm_wededtime,cm_holsttime,cm_holedtime, \n");
			strQuery.append("       to_char(SYSDATE,'d') week,to_char(SYSDATE,'hh24mi') hhmm                       \n");
			strQuery.append("  from cmm0061 where cm_rgtcd='112'    				                               \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				strHhmm = rs.getString("hhmm");
				if (strHoli.equals("1") || rs.getString("week").equals("1")) {
				   if (Integer.parseInt(rs.getString("cm_holsttime")) <= Integer.parseInt(strHhmm) &&
				       Integer.parseInt(rs.getString("cm_holedtime")) >= Integer.parseInt(strHhmm)) {
				       strHoli = "0";
				   } else strHoli = "1";
				} else {
					if (rs.getString("week").equals("7")) {
						   if (Integer.parseInt(rs.getString("cm_wedsttime")) <= Integer.parseInt(strHhmm) &&
						       Integer.parseInt(rs.getString("cm_wededtime")) >= Integer.parseInt(strHhmm)) {
						       strHoli = "0";
						   } else strHoli = "1";
					} else {
						   if (Integer.parseInt(rs.getString("cm_comsttime")) <= Integer.parseInt(strHhmm) &&
						       Integer.parseInt(rs.getString("cm_comedtime")) >= Integer.parseInt(strHhmm)) {
						       strHoli = "0";
						   } else strHoli = "1";
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return strHoli;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TimeSch.SelectTimeSch() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## TimeSch.SelectTimeSch() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TimeSch.SelectTimeSch() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## TimeSch.SelectTimeSch() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TimeSch.SelectTimeSch() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectTimeSch() method statement

	public String reqTimeGb(Connection conn,String passOk) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strHoli    = "0";//0:일반+업무중     1:긴급+업무중       2:일반+업무후     3:긴급+업무후

		try {
			
			Holiday_Check  holiChk = new Holiday_Check();
			strHoli = holiChk.SelectHoli();
			holiChk = null;
			
			strQuery.append("select a.cm_sttime,a.cm_edtime,                      \n");
			strQuery.append("       to_char(SYSDATE,'hh24mi') hhmm                \n");
			strQuery.append("  from cmm0014 a                                     \n");
			strQuery.append(" where a.cm_stno='ECAMS'                             \n");
			strQuery.append("   and decode(to_char(SYSDATE,'d'),1,'03',6,'02',7,'03','01') = a.cm_timecd \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				//20160412 neo. 일반/긴급 신청 구분하고,  업무중/업무후 구분하도록 로직 수정. 윤정호과장 요청
				/* 수정 전 :
				if (!strHoli.equals("0")) strHoli = "2";
				else {
					if (Integer.parseInt(rs.getString("hhmm")) < Integer.parseInt(rs.getString("cm_sttime")) ||
						Integer.parseInt(rs.getString("hhmm")) > Integer.parseInt(rs.getString("cm_edtime"))){
						if (passOk.equals("2")) strHoli = "3";
						else strHoli = "2";
					} else if (passOk.equals("2")) strHoli = "1";
				}
				*/
				// 수정 후 :
				if ( passOk.equals("2") ) {//긴급 일때
					if ( strHoli.equals("0") ){//평일
						//업무 시간 인지 아닌지 비교
						if ( Integer.parseInt(rs.getString("hhmm")) < Integer.parseInt(rs.getString("cm_sttime")) ||
							 Integer.parseInt(rs.getString("hhmm")) > Integer.parseInt(rs.getString("cm_edtime"))){//업무후 시간
							strHoli = "3";//긴급+업무후
						} else {//업무중 시간
							strHoli = "1";//긴급+업무중
						}
					} else {//휴일
						strHoli = "3";//긴급+업무후
					}
				} else {//일반 일때
					if ( strHoli.equals("0") ){//평일
						//업무 시간 인지 아닌지 비교
						if ( Integer.parseInt(rs.getString("hhmm")) < Integer.parseInt(rs.getString("cm_sttime")) ||
							 Integer.parseInt(rs.getString("hhmm")) > Integer.parseInt(rs.getString("cm_edtime"))){//업무후 시간
							strHoli = "2";//평일+업무후
						} else {//업무중 시간
							strHoli = "0";//평일+업무중
						}
					} else {//휴일
						strHoli = "2";//평일+업무후
					}
				}
			} else {
				//20160412 neo. 일반/긴급 체크 로직 추가
				if ( passOk.equals("2") ) {//긴급
					if ( strHoli.equals("0") ){//업무중(평일)
						strHoli = "1";
					} else {//업무후(휴일)
						strHoli = "3";
					}
				} else {//일반
					if ( strHoli.equals("0") ){//업무중(평일)
						strHoli = "0";
					} else {//업무후(휴일)
						strHoli = "2";
					}
				}
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			return strHoli;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TimeSch.reqTimeGb() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## TimeSch.reqTimeGb() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TimeSch.reqTimeGb() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## TimeSch.reqTimeGb() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of reqTimeGb() method statement

	public String NowDatetime() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  rtJson	  = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select to_char(SYSDATE,'yyyymmddhh24miss') sysdate1 from dual   \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rtJson = rs.getString("sysdate1");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rtJson;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## TimeSch.NowDatetime() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## TimeSch.NowDatetime() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## TimeSch.NowDatetime() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## TimeSch.NowDatetime() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## TimeSch.NowDatetime() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of NowDatetime() method statement

}//end of TimeSch class statement
