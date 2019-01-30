/*****************************************************************************************
	1. program ID	: Cmr3200.java
	2. create date	: 2008.04.08
	3. auth		    : m.s.kang
	4. update date	: 2009.02.21
	5. auth		    : no name
	6. description	: Request List
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import java.util.ArrayList;
import java.util.HashMap;

//import app.common.LoggableStatement;
//import app.common.SystemPath;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr2400{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 요청현황을 조회합니다.
	 * @param  syscd,reqcd,teamcd,sta,requser,startdt,enddt,userid
	 * @return json
	 * @throws SQLException
	 * @throws Exception
	 */								//strSys,strJob,strSta,strStD,strEdD,strReqUsr,ResultCk,strUserId
    public Object[] get_SelectList(String pSysCd,String pJobCd,String pStateCd,String pStartDt,String pEndDt,String pReqUser,String ResultCk,String pUserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		//PreparedStatement pstmt3      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		//ResultSet         rs3         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] 		  returnObject= null;
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			rtList.clear();
	        Integer           Cnt         = 0;

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("Select a.*,c.cm_sysmsg,e.cm_username,f.cm_codename sta,					 \n");
			strQuery.append("       g.cm_codename sin,b.cm_jobname,										 \n");
			strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) ||	     \n");
			strQuery.append("       '-' || substr(a.cr_acptno,7,6) as acptno					         \n");
			strQuery.append("from cmm0102 b,cmm0020 g,cmm0030 c,cmm0020 f,cmm0040 e,cmr9900 d,cmr1000 a  \n");
			strQuery.append("where 																		 \n");
			  if(pStateCd.equals("00")){
				  strQuery.append("((a.cr_prcdate is not null and \n");
				  strQuery.append("to_char(a.cr_acptdate,'yyyymmdd')>=? and \n");
				  strQuery.append("to_char(a.cr_acptdate,'yyyymmdd')<=? and \n");
				  strQuery.append("d.cr_locat<>'00' and rtrim(d.cr_team) in ('SYSCB','SYSED')) or \n");
				  strQuery.append("(d.cr_status='0' and d.cr_locat<>'00' and rtrim(d.cr_team) in ('SYSCB','SYSED'))) and \n");
			  }else if(pStateCd.equals("01")){
				  strQuery.append("a.cr_status='0'  and d.cr_locat<>'00' and rtrim(d.cr_team) in ('SYSCB','SYSED') and \n");
			  }else if(pStateCd.equals("02")){
				  strQuery.append("a.cr_prcdate is not null and \n");
				  strQuery.append("to_char(a.cr_acptdate,'yyyymmdd')>=? and \n");
				  strQuery.append("to_char(a.cr_acptdate,'yyyymmdd')<=? and \n");
				  strQuery.append("d.cr_locat<>'00' and rtrim(d.cr_team) in ('SYSCB','SYSED') and \n");
			  }
			  strQuery.append("a.cr_acptno=d.cr_acptno and \n");
			  if(ResultCk.equals("0")){
				  strQuery.append("a.cr_qrycd='04' and \n");
			  }else if(ResultCk.equals("1")){
				  strQuery.append("a.cr_qrycd='03' and \n");
			  }
			  if(pSysCd != "" && pSysCd != null){
				  strQuery.append("a.cr_syscd=? and \n");//ll.L_Syscd
			  }
			  if(pJobCd != "" && pJobCd != null){
				  strQuery.append("a.cr_jobcd=? and \n");//cbo_jobcd
			  }
			  if(pReqUser!="" && pReqUser!=null){
				  //strQuery.append("e.cm_username=? and \n");//Txt_UserId
				  strQuery.append("a.cr_editor in(select cm_userid from cmm0040 where cm_username= ? and \n");
			  }

			  strQuery.append("a.cr_syscd=c.cm_syscd and \n");
			  strQuery.append("a.cr_jobcd=b.cm_jobcd and \n");
			  strQuery.append("f.cm_macode='CMR1000' and a.cr_status=f.cm_micode and \n");
			  strQuery.append("g.cm_macode='REQUEST' and a.cr_qrycd=g.cm_micode and \n");
			  strQuery.append("a.cr_editor=e.cm_userid \n");
			  strQuery.append("order by a.cr_acptdate desc \n");

			pstmt = conn.prepareStatement(strQuery.toString());
		//	pstmt = new LoggableStatement(conn,strQuery.toString());
            Cnt = 0;

            if (pStateCd.equals("00") || pStateCd.equals("02")) {
				if(!pStartDt.equals("")){
	            pstmt.setString(++Cnt, pStartDt);
	            pstmt.setString(++Cnt, pEndDt);
				}
            }

            if (pSysCd != "" && pSysCd != null) {
            	pstmt.setString(++Cnt, pSysCd);
            }
			if (pJobCd != "" && pJobCd != null)  {
            	pstmt.setString(++Cnt, pJobCd);
            }
			if (pReqUser != null && pReqUser !=""){
	           pstmt.setString(++Cnt, pReqUser);
			}

	//		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();

				rst.put("Sysmsg",  rs.getString("cm_sysmsg"));
				rst.put("acptno",  rs.getString("acptno"));
				rst.put("acptdate",  rs.getString("cr_acptdate"));
				rst.put("username",  rs.getString("cm_username"));
				rst.put("jobN",  rs.getString("cm_jobname"));
				rst.put("sta",  rs.getString("sta"));
				//rst.put("sta",  rs.getString("sta"));
				rst.put("prcdate",  rs.getString("cr_prcdate"));
				rst.put("sayu",  rs.getString("cr_sayu"));
				rtList.add(rst);
        		rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			returnObject = rtList.toArray();
			rtList.clear();
			rtList = null;

			return returnObject;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3200.SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3200.SelectList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3200.SelectList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3200.SelectList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObject != null)	returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3200.SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectList() method statement


}//end of Cmr3200 class statement
