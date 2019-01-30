
/******************************************************************************
   프로젝트명    : 형상관리 시스템
   서브시스템명  : Logger Print Assist 
   파일명       : DebugPrint.java      
   수정내역
   수정일         담당자       수정내용
-------------------------------------------------------------------------------
  2006. 08. 08.  TEOK.KANG     최초생성
******************************************************************************/

package com.ecams.common.logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.ecams.common.logger.EcamsLogger;

/**
 * 	Logger Print Assist 
 */
public class DebugPrintAssist {
	
	/**
	 * RESULTSET 로그를 출력합니다.
	 * @param  val_str 임의의 구분값(SQL SCRIPT)
	 * @param  rs      출력대상 ResultSet
	 * @param  rsmd    출력대상 ResultSetMetaData
	 * @return void
	 * @throws SQLException 
	 */
	public static synchronized void loggerPrint(String val_str, ResultSet rs, ResultSetMetaData rsmd) throws SQLException{
		Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
		ecamsLogger.debug(" ");		
		ecamsLogger.debug("★★DEBUGPRINT START★★    [" + val_str + "] ");
		ecamsLogger.debug("==TOTAL ROW COUNT     = [" + rs.getRow() + "]");
		ecamsLogger.debug("==TOTAL COL COUNT     = [" + rsmd.getColumnCount() + "]");		
		ecamsLogger.debug("==ResultSet UNIQUE ID = [" + rs.toString() + "]");
		
		int i = 0;
		while (rs.next()){
			ecamsLogger.debug("==ROW INDEX["+i+"]==[" + val_str + "]");
			for (int j = 0; j < rsmd.getColumnCount(); j++){
				ecamsLogger.debug(rsmd.getColumnLabel(j)+"=> [" + rs.getObject(rsmd.getColumnName(j))+"]");
			}//end of inner for-loop statement
			i++;
		}//end of for-loop statement
		ecamsLogger.debug("★★DEBUGPRINT END★★ [" + val_str + "]");
		ecamsLogger.debug(" ");		
	}//end of loggerPrint method statement

	/**
	 * TRANSACTION QUERY PRINT. (SQL EXECUTION)
	 * @param val_str 임의의 구분값
	 * @param ds      출력대상 DATASET
	 */
	public static synchronized void loggerPrint_tr(String val_str, String sql, Object[] param){
		Logger ecamsLogger  = EcamsLogger.getLoggerInstance();		
		ecamsLogger.debug(" ");		
		ecamsLogger.debug("## DEBUGPRINT TR. START ## [" + val_str + "] ");
		ecamsLogger.debug("== START OF SQL QUERY ==");	
		ecamsLogger.debug("[" + sql + "]");
		ecamsLogger.debug("== END OF SQL QUERY ==");			
		ecamsLogger.debug("== TOTAL PARAMETERS COUNT== [" + param.length + "]");	
		for (int i = 0; i < param.length; i++){
			ecamsLogger.debug("["+val_str+"]==PARAMETERS INDEX["+i+"], VALUE=[" + param[i] + "]");
		}//end of for-loop statement
		ecamsLogger.debug("## DEBUGPRINT TR. END ## [" + val_str + "]");
		ecamsLogger.debug(" ");		
	}//end of loggerPrint method statement

}//end of class DebugPrintAssist statement
