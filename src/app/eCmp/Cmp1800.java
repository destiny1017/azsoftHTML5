/*****************************************************************************************
	1. program ID	: Cmp1800.java
	2. create date	: 2012. 01. 02
	3. auth		    : no name
	4. update date	: 
	5. auth		    : 
	6. description	: 보고서1->담당자별지연사유
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author no name
 *
 */
public class Cmp1800 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
	/** 보고서1->담당자별지연사유 조회 쿼리
	 * @param Sdate 기안시작일자 20120101
	 * @param Edate 기안종료일자 20120101
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getList(String Sdate,String Edate) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();		
		int TotCnt=0,TstDefYNCnt=0,EndDefYNCnt=0,DEFRSNDVLP1=0,DEFRSNDVLP2=0,DEFRSNDVLP3=0,DEFRSNDVLP4=0,DEFRSNDVLP5=0,DEFRSNDVLP6=0,DEFRSNDVLP7=0,DEFRSNDVLP8=0,DEFRSNDVLP9=0;
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
		    strQuery.append("SELECT  DDAGMN    \n");
		    strQuery.append("      , COUNT(*) TotCnt \n");
		    strQuery.append("      , SUM(DECODE(TRIM(NVL(PRCGB, 'N')), '프로그램개발', \n");
		    strQuery.append("                  DECODE(TRIM(NVL(TstDefYN , 'N')), 'Y', 1, 0), 0)) TstDefYNCnt \n");
		    strQuery.append("      , SUM(DECODE(TRIM(NVL(PRCGB, 'N')), '프로그램개발', \n");
		    strQuery.append("                  DECODE(TRIM(NVL(EndDefYN , 'N')), 'Y', 1, 0), 0)) EndDefYNCnt \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '1'), 0, 0, 1)) DEFRSNDVLP1 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '2'), 0, 0, 1)) DEFRSNDVLP2 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '3'), 0, 0, 1)) DEFRSNDVLP3 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '4'), 0, 0, 1)) DEFRSNDVLP4 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '5'), 0, 0, 1)) DEFRSNDVLP5 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '6'), 0, 0, 1)) DEFRSNDVLP6 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '7'), 0, 0, 1)) DEFRSNDVLP7 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '8'), 0, 0, 1)) DEFRSNDVLP8 \n");
		    strQuery.append("      , SUM(DECODE(INSTR(TRIM(NVL(DEFRSNDVLP, 'N')), '9'), 0, 0, 1)) DEFRSNDVLP9 \n");
		    strQuery.append("  FROM  KPCOMPRCREQDOC \n");
		    strQuery.append(" WHERE  ReqDt BETWEEN ? AND ? \n");
		    strQuery.append("   AND  DOCGB = '1' \n");
		    strQuery.append("   AND  PRCGB = '프로그램개발' \n");
		    strQuery.append(" GROUP  BY DDAGMN      \n");
		    strQuery.append(" ORDER  BY DDAGMN      \n");
	        
	        pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, Sdate);
	        pstmt.setString(2, Edate);
	        rs = pstmt.executeQuery();
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rsval.clear();
	        
	        
	        //int TotCnt,TstDefYNCnt,EndDefYNCnt,DEFRSNDVLP1,DEFRSNDVLP2,DEFRSNDVLP3,DEFRSNDVLP4,DEFRSNDVLP5,DEFRSNDVLP6,DEFRSNDVLP7,DEFRSNDVLP8,DEFRSNDVLP9;
			while (rs.next()){
				rst = new HashMap<String, String>();
        		rst.put("DDAGMN",rs.getString("DDAGMN"));
        		rst.put("TotCnt",rs.getString("TotCnt"));
        		rst.put("TstDefYNCnt",rs.getString("TstDefYNCnt"));
        		rst.put("EndDefYNCnt",rs.getString("EndDefYNCnt"));
        		rst.put("DEFRSNDVLP1",rs.getString("DEFRSNDVLP1"));
        		rst.put("DEFRSNDVLP2",rs.getString("DEFRSNDVLP2"));
        		rst.put("DEFRSNDVLP3",rs.getString("DEFRSNDVLP3"));
        		rst.put("DEFRSNDVLP4",rs.getString("DEFRSNDVLP4"));
        		rst.put("DEFRSNDVLP5",rs.getString("DEFRSNDVLP5"));
        		rst.put("DEFRSNDVLP6",rs.getString("DEFRSNDVLP6"));
        		rst.put("DEFRSNDVLP7",rs.getString("DEFRSNDVLP7"));
        		rst.put("DEFRSNDVLP8",rs.getString("DEFRSNDVLP8"));
        		rst.put("DEFRSNDVLP9",rs.getString("DEFRSNDVLP9"));
        		rst.put("colorsw","black");
				rsval.add(rst);
				rst = null;
				
				TotCnt = TotCnt + rs.getInt("TotCnt");
				TstDefYNCnt = TstDefYNCnt + rs.getInt("TstDefYNCnt");
				EndDefYNCnt = EndDefYNCnt + rs.getInt("EndDefYNCnt");
				DEFRSNDVLP1 = DEFRSNDVLP1 + rs.getInt("DEFRSNDVLP1");
				DEFRSNDVLP2 = DEFRSNDVLP2 + rs.getInt("DEFRSNDVLP2");
				DEFRSNDVLP3 = DEFRSNDVLP3 + rs.getInt("DEFRSNDVLP3");
				DEFRSNDVLP4 = DEFRSNDVLP4 + rs.getInt("DEFRSNDVLP4");
				DEFRSNDVLP5 = DEFRSNDVLP5 + rs.getInt("DEFRSNDVLP5");
				DEFRSNDVLP6 = DEFRSNDVLP6 + rs.getInt("DEFRSNDVLP6");
				DEFRSNDVLP7 = DEFRSNDVLP7 + rs.getInt("DEFRSNDVLP7");
				DEFRSNDVLP8 = DEFRSNDVLP8 + rs.getInt("DEFRSNDVLP8");
				DEFRSNDVLP9 = DEFRSNDVLP9 + rs.getInt("DEFRSNDVLP9");
			}
			
			if (rsval.size()>0){
				rst = new HashMap<String, String>();
        		rst.put("DDAGMN","총계");
        		rst.put("TotCnt",Integer.toString(TotCnt));
        		rst.put("TstDefYNCnt",Integer.toString(TstDefYNCnt));
        		rst.put("EndDefYNCnt",Integer.toString(EndDefYNCnt));
        		rst.put("DEFRSNDVLP1",Integer.toString(DEFRSNDVLP1));
        		rst.put("DEFRSNDVLP2",Integer.toString(DEFRSNDVLP2));
        		rst.put("DEFRSNDVLP3",Integer.toString(DEFRSNDVLP3));
        		rst.put("DEFRSNDVLP4",Integer.toString(DEFRSNDVLP4));
        		rst.put("DEFRSNDVLP5",Integer.toString(DEFRSNDVLP5));
        		rst.put("DEFRSNDVLP6",Integer.toString(DEFRSNDVLP6));
        		rst.put("DEFRSNDVLP7",Integer.toString(DEFRSNDVLP7));
        		rst.put("DEFRSNDVLP8",Integer.toString(DEFRSNDVLP8));
        		rst.put("DEFRSNDVLP9",Integer.toString(DEFRSNDVLP9));
        		rst.put("colorsw","blue");
        		rst.put("color","blue");
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
			ecamsLogger.error("## Cmp1800.getList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmp1800.getList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1800.getList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmp1800.getList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp1800.getList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getList() method statement
	
}//end of Cmp1800 class statement