/*****************************************************************************************
	1. program ID	: Cmp6100.java
	2. create date	: 2014. 08. 29
	3. auth		    : sykim
	4. update date	:
	5. auth		    :
	6. description	: 
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmp6100{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * getRowList  개발자별 현황
	 * @param String selMonth,String deptCd, String rateCd, String userId
	 * @return List Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getRowList(String selMonth,String deptCd, String rateCd, String userId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			parmCnt = 0;
			
			//사용자리스트	
			strQuery.setLength(0);
			strQuery.append("select distinct a.cm_userid userid, a.cm_username username, a.cm_project deptcd,  \n");
			strQuery.append(" b.cm_deptname deptname, count(*) ratecnt										   \n");
			strQuery.append(" from cmm0040 a, cmm0100 b, cmc0100 c,     		 		  					   \n");
			strQuery.append(" (select cc_srid, cc_rate, cc_userid from cmc0110 where cc_rate is not null) d    \n");
			strQuery.append("   where to_char(c.cc_compdate,'yyyymm')=? 		  							   \n");
			strQuery.append("   and c.cc_status = '9'					 		  							   \n");
			strQuery.append("	and a.cm_project = b.cm_deptcd   				 		  					   \n");
			if( deptCd != "" ){
				strQuery.append(" and a.cm_project=? \n");
			}
			if( userId != "" ){
				strQuery.append(" and (a.cm_userid=? or a.cm_username=? )\n");
			}
			strQuery.append("	and a.cm_userid = d.cc_userid   				 		  					   \n");
			strQuery.append("	and d.cc_srid = c.cc_srid  				 		  							   \n");
			strQuery.append(" group by a.cm_userid, a.cm_username, a.cm_project, b.cm_deptname				   \n");
			strQuery.append(" order by userid 			 				 		  							   \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, selMonth);
			if( deptCd != "" ){
				pstmt.setString(++parmCnt, deptCd);
			}
			if( userId != "" ){
				pstmt.setString(++parmCnt, userId);
				pstmt.setString(++parmCnt, userId);
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        
	        while ( rs.next() ){
	        	rst = new HashMap<String, String>();
	        	rst.put("userid",rs.getString("userid"));
	        	rst.put("username",rs.getString("username"));
	        	rst.put("deptcd",rs.getString("deptcd"));
	        	rst.put("deptname",rs.getString("deptname"));
	        	rst.put("ratecnt",rs.getString("ratecnt"));
	        	rsval.add(rst);
	        	rst = null;
			}//end of while-loop statement
	        rs.close();
	        pstmt.close();
	        
	        
	        //사용자별 등급 조회
	        parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("select a.cm_userid userid, e.cm_codename ratename,d.cc_rate rate, count(*) cnt 	 	 \n");
			strQuery.append(" from cmm0040 a, cmm0100 b, cmc0100 c,     		 		  			 		 \n");
			strQuery.append(" (select cc_srid, cc_rate, cc_userid from cmc0110 where cc_rate is not null) d, \n");
			strQuery.append(" (select cm_micode, cm_codename from cmm0020 where cm_macode='DEVRATE') e 		 \n");
			strQuery.append("   where to_char(c.cc_compdate,'yyyymm')=?		  		  					 \n");
			strQuery.append("   and c.cc_status = '9'					 		  							 \n");
			strQuery.append("   and d.cc_rate = e.cm_micode					 		  						 \n");
			strQuery.append("   and a.cm_project = b.cm_deptcd					 		  					 \n");
			if( deptCd != "" ){
				strQuery.append(" and a.cm_project=? \n");
			}
			if( !rateCd.equals("00") ){
				strQuery.append(" and d.cc_rate = ? \n");
			}
			if( userId != "" ){
				strQuery.append(" and (a.cm_userid=? or a.cm_username=? ) \n");
			}
			
			strQuery.append("	and a.cm_userid = d.cc_userid   				 		  					 \n");
			strQuery.append("	and d.cc_srid = c.cc_srid  				 		  							 \n");
			strQuery.append(" group by a.cm_userid,a.cm_username,e.cm_codename,d.cc_rate									 \n");
			strQuery.append(" order by a.cm_userid,a.cm_username,e.cm_codename,d.cc_rate									 \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, selMonth);
			if( deptCd != "" ){
				pstmt.setString(++parmCnt, deptCd);
			}
			if( !rateCd.equals("00") ){
				pstmt.setString(++parmCnt, rateCd);
			}
			if( userId != "" ){
				pstmt.setString(++parmCnt, userId);
				pstmt.setString(++parmCnt, userId);
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        int i = 0 ;        
	        while ( rs.next() ){
	        	for (i=0 ; i<rsval.size() ; i++){
        			if ( rsval.get(i).get("userid").equals(rs.getString("userid")) ) {
        	        	rst = new HashMap<String, String>();
        				rst = rsval.get(i);
        				rst.put(rs.getString("rate"),rs.getString("cnt"));
        				rsval.set(i, rst);
        	        	rst = null;
        				break;
        			}
        		}
			}//end of while-loop statement
	        rs.close();
	        pstmt.close();

	        //사용자별 프로그램 갯수
	        parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("select d.cm_userid userid,b.cr_qrycd grycd, count(*) cnt 				\n");
			strQuery.append(" from cmc0100 a,cmr1010 b,cmm0040 d,   		 		  				\n");
			strQuery.append(" (select cc_srid, cc_userid from cmc0110 where cc_rate is not null) c 	\n");
			strQuery.append("   where to_char(a.cc_compdate,'yyyymm')=? 		  					\n");
			strQuery.append("   and a.cc_status = '9'					 		  					\n");
			strQuery.append("	and c.cc_srid = a.cc_srid   				 		  				\n");
			strQuery.append("	and b.cr_acptno in (   				 		  						\n");
			strQuery.append("	 select cr_acptno from cmr1000 where CR_ITSMID=a.cc_srid			\n");
			strQuery.append("	 and cr_qrycd='04' and cr_status='9' )						 		\n");
			strQuery.append("	and b.cr_itemid = cr_baseitem						 				\n");
			strQuery.append("	and b.cr_status = '9'						 						\n");
			if( deptCd != "" ){
				strQuery.append(" and d.cm_project=? \n");
			}
			if( userId != "" ){
				strQuery.append(" and (d.cm_userid=? or d.cm_username=? ) \n");
			}
			strQuery.append("	and d.cm_userid = c.cc_userid   				 		  			\n");
			strQuery.append("	group by d.cm_userid,d.cm_username,b.cr_qrycd						 				\n");
			strQuery.append("	order by d.cm_userid,d.cm_username,b.cr_qrycd						 				\n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, selMonth);
			if( deptCd != "" ){
				pstmt.setString(++parmCnt, deptCd);
			}
			if( userId != "" ){
				pstmt.setString(++parmCnt, userId);
				pstmt.setString(++parmCnt, userId);
			}
	        rs = pstmt.executeQuery();
	        while ( rs.next() ){
	        	for (i=0 ; i<rsval.size() ; i++){
        			if ( rsval.get(i).get("userid").equals(rs.getString("userid")) ) {
        	        	rst = new HashMap<String, String>();
        				rst = rsval.get(i);
        				rst.put("Q"+rs.getString("grycd"),rs.getString("cnt"));
        				rsval.set(i, rst);
        	        	rst = null;
        				break;
        			}
        		}
			}//end of while-loop statement
			rst = null;
			
			rs.close();
			pstmt.close();
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;
//			ecamsLogger.error(rsval.toString());
			
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getRowList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp6000.getRowList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp6000.getRowList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp6000.getRowList() Exception END ##");
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
					ecamsLogger.error("## Cmp6000.getRowList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRowList() method statement

	

}//end of Cmp6000 class statement
