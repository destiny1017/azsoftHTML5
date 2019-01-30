
/*****************************************************************************************
	1. program ID	: MainDAO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package com.ecams.service.main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ArrayList;

import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
//import com.ecams.service.main.valueobject.MainVO;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainDAO{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 미결내역건수조회(메인화면)
	 * @param user_id
	 * @return int
	 * @throws SQLException
	 * @throws Exception
	 */
	public String selectAprvCnt(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int aprvcnt              = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) APRVCNT                            ");
			strQuery.append("  from cmr9900 a      							    ");
			strQuery.append(" where a.cr_status='0'								");
			strQuery.append("   and (substr(a.CR_ACPTNO,5,2)<'30' or substr(a.CR_ACPTNO,5,2) in ('71','75'))");
			strQuery.append("   and a.cr_locat='00'								");
			strQuery.append("   and a.cr_teamcd in('2','3','5','6','7')			");
			strQuery.append("   and a.cr_team = ?       		                ");
			strQuery.append("union all                         			        ");
			strQuery.append("select count(*) APRVCNT                            ");
			strQuery.append("  from cmr9900 a,cmm0043 b						    ");
			strQuery.append(" where a.cr_status='0'								");
			strQuery.append("   and (substr(a.CR_ACPTNO,5,2)<'30' or substr(a.CR_ACPTNO,5,2) in ('71','75'))");
			strQuery.append("   and a.cr_locat='00'								");
			strQuery.append("   and a.cr_teamcd in('4')   		                ");
			strQuery.append("   and instr(a.cr_team,b.cm_rgtcd)>0               ");
			strQuery.append("   and b.cm_userid = ?             ");
			/*
			strQuery.append("union all                         			        ");
			strQuery.append("select count(*) APRVCNT                            ");
			strQuery.append("  from cmr9900 a,cmd0304 b,cmr1000 c  		        ");
			strQuery.append(" where a.cr_status='0'								");
			strQuery.append("   and a.cr_locat='00'								");
			strQuery.append("   and a.cr_teamcd in('P')   		                ");
			strQuery.append("   and a.cr_acptno=c.cr_acptno                     ");
			strQuery.append("   and instr(a.cr_team,b.cd_prjjik)>0              ");
			strQuery.append("   and b.cd_prjuser=? and b.cd_closedt is null     ");
			strQuery.append("   and c.cr_prjno=b.cd_prjno                       ");
			*/
			strQuery.append("union all                     			            ");
			strQuery.append("select count(*) APRVCNT                            ");
			strQuery.append("  from cmr9900 a,cmm0043 b,cmm0040 c    		    ");
			strQuery.append(" where a.cr_status='0'								");
			strQuery.append("   and (substr(a.CR_ACPTNO,5,2)<'30' or substr(a.CR_ACPTNO,5,2) in ('71','75'))");
			strQuery.append("   and a.cr_locat='00'								");
			strQuery.append("   and a.cr_teamcd = '9'       		            ");
			strQuery.append("   and instr(a.cr_sgngbn,b.cm_rgtcd)>0             ");
			strQuery.append("   and b.cm_userid = ?             ");
			strQuery.append("   and b.cm_userid = c.cm_userid                   ");
			strQuery.append("   and a.cr_team = c.cm_project                   ");
			strQuery.append("union all                         			            ");
			strQuery.append("select count(*) APRVCNT                            ");
			strQuery.append("  from cmr9900 a      							    ");
			strQuery.append(" where a.cr_status='0'								");
			strQuery.append(" and (substr(a.CR_ACPTNO,5,2)<'30' or substr(a.CR_ACPTNO,5,2) in ('71','75'))");
			strQuery.append("   and a.cr_locat>=(select substr(cr_confusr,1,2) from cmr9900   ");
			strQuery.append("                     where cr_acptno=a.cr_acptno               ");
			strQuery.append("   and (substr(a.CR_ACPTNO,5,2)<'30' or substr(a.CR_ACPTNO,5,2) in ('71','75'))");
			strQuery.append("                       and cr_locat='00')                      ");
			strQuery.append("   and a.cr_locat<=(select substr(cr_confusr,3,2) from cmr9900   ");
			strQuery.append("                     where cr_acptno=a.cr_acptno               ");
			strQuery.append("   and (substr(a.CR_ACPTNO,5,2)<'30' or substr(a.CR_ACPTNO,5,2) in ('71','75'))");
			strQuery.append("                       and cr_locat='00')                      ");
			strQuery.append("   and a.cr_teamcd='8'			                    ");
			strQuery.append("   and a.cr_team = ?       		");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, user_id);
            pstmt.setString(2, user_id);
            pstmt.setString(3, user_id);
            pstmt.setString(4, user_id);
            //pstmt.setString(5, user_id);

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				aprvcnt = aprvcnt + rs.getInt("APRVCNT");
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MainDAO.selectAprvCnt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MainDAO.selectAprvCnt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MainDAO.selectAprvCnt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MainDAO.selectAprvCnt() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MainDAO.selectAprvCnt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return Integer.toString(aprvcnt);

	}//end of selectUserName() method statement



}//end of MainDAO class statement
