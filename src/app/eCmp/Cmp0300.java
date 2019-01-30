/*****************************************************************************************
	1. program ID	: Cmp3300.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.io.BufferedReader;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp0300{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/**  프로그램갯수
	 * @param UserId : 사용자ID
	 * @param SysCd : 시스템코드
	 * @param JobCd : 업무코드
	 * @param StDate : 조회기준일
	 * @param jobSw : 업무상세구분코드
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getProgList(String UserId,String SysCd,String JobCd,String StDate,boolean jobSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		String            svSys       = null;
		String            svJob       = null;
		boolean           rowSw       = false;
		boolean           sysSw       = false;
		int               i           = 0;
		long             rowCnt       = 0;
		long             subCnt       = 0;
		boolean          firstSw      = true;
//		UserInfo         secuinfo     = new UserInfo();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;
		ArrayList<String>  rstot1 = new ArrayList<String>();
		ArrayList<Long>  rstot = new ArrayList<Long>();
		ArrayList<Long>  subtot = new ArrayList<Long>();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			
			conn = connectionContext.getConnection();
//			secuinfo = null;
			strQuery.setLength(0);
			if (jobSw == false) strQuery.append("select c.cm_sysmsg,");
			else strQuery.append("select c.cm_sysmsg,a.cm_jobname, ");
			strQuery.append("b.cr_rsrccd,count(*) as cnt                                        \n");
			if (jobSw == true) strQuery.append("from cmm0030 c,cmm0102 a,cmr0020 b              \n");
			else strQuery.append("from cmm0030 c,cmr0020 b                                      \n");
			strQuery.append("where b.cr_lstver>0                                                \n");
			
			//파라메터로 넘어온 시스템에 대해서 조회
			String[] listSysCd = SysCd.split(",");
			strQuery.append("  and b.cr_syscd in (\n");
			if (listSysCd.length == 1)
				strQuery.append(" ? ");
			else{
				for (i=0;i<listSysCd.length;i++){
					if (i == listSysCd.length-1)
						strQuery.append(" ? ");
					else
						strQuery.append(" ? ,");
				}
			}
			strQuery.append(")\n");
			
			if (JobCd != null && JobCd != "") strQuery.append("and b.cr_jobcd=?                 \n");
			strQuery.append("  and to_char(b.cr_opendate,'yyyymmdd')<=?                         \n");
			strQuery.append("  and (b.cr_status<>'9' or                                         \n");
			strQuery.append("       b.cr_status='9' and to_char(b.cr_clsdate,'yyyymmdd')<=?)    \n");
			strQuery.append("  and b.cr_syscd=c.cm_syscd                                        \n");
			strQuery.append("  and c.cm_closedt is null                                     	\n");
			if (jobSw == true) strQuery.append("and b.cr_jobcd=a.cm_jobcd                       \n");
			if (jobSw == false) strQuery.append("group by c.cm_sysmsg,b.cr_rsrccd               \n");
			else strQuery.append("group by c.cm_sysmsg,a.cm_jobname,b.cr_rsrccd                 \n");
			if (jobSw == false) strQuery.append("order by c.cm_sysmsg,b.cr_rsrccd               \n");
			else strQuery.append("order by c.cm_sysmsg,a.cm_jobname,b.cr_rsrccd                 \n");

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			for (i=0;i<listSysCd.length;i++){
				pstmt.setString(++parmCnt, listSysCd[i]);
			}
			if (JobCd != null && JobCd != "") pstmt.setString(++parmCnt, JobCd);
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, StDate);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

//	        rsval.clear();
//	        rstot.clear();
//	        rstot1.clear();
//	        subtot.clear();
			while (rs.next()){
				rowSw = false;
				if (svSys == null) {
					rowSw = true;
					sysSw = true;
				} else {
					if (!svSys.equals(rs.getString("cm_sysmsg"))) {
						rowSw = true;
						sysSw = true;
					} else if (jobSw == true) {
						if (!svJob.equals(rs.getString("cm_jobname"))) {
							rowSw = true;
						}
					}
				}

			    //System.out.println("check : "+rowSw+ " " + chgSw);
				if (rowSw == true && firstSw == false) {
					rst.put("rowhap" , Long.toString(rowCnt));
					rsval.add(rst);
                	rst = null;
                    rowCnt = 0;

                    if (jobSw == true && sysSw == true) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							rst.put(rstot1.get(i), Long.toString(rstot.get(i)));
							subCnt = subCnt + rstot.get(i);
							rstot.set(i, rowCnt);
						}
                    	rst.put("cm_sysmsg" , svSys+" 합계");
                    	rst.put("rowhap" , Long.toString(subCnt));
                    	rsval.add(rst);
                    	rst = null;
                    }
				}

				//System.out.println("firstSw : "+firstSw);
				if (rowSw == true) {
					rst = new HashMap<String, String>();
					svSys = rs.getString("cm_sysmsg");
					if (jobSw == true) svJob = rs.getString("cm_jobname");
					if (sysSw == true) rst.put("cm_sysmsg", svSys);
					if (jobSw == true) rst.put("cm_jobname", svJob);
					firstSw = false;
				}

				rst.put("col" + rs.getString("cr_rsrccd"), Integer.toString(rs.getInt("cnt")));
				rowCnt = rowCnt + rs.getInt("cnt");
			    rowSw = false;
				sysSw = false;
		 		for (i = 0;rstot1.size() > i;i++) {
					 if (rstot1.get(i).equals("col" + rs.getString("cr_rsrccd"))) {
				 		 rowSw = true;
				 		 rstot.set(i, rstot.get(i) + rs.getInt("cnt"));
				 		 subtot.set(i, subtot.get(i) + rs.getLong("cnt"));
				 		 break;
					 }
				}
		 		if (rowSw == false) {
		 			rstot1.add("col" + rs.getString("cr_rsrccd"));
		 			rstot.add(rs.getLong("cnt"));
		 			subtot.add(rs.getLong("cnt"));
		 		}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;

			if (firstSw == false) {
				rst.put("rowhap" , Long.toString(rowCnt));
				rsval.add(rst);
                rowCnt = 0;

                if (jobSw == true) {
                	rst = new HashMap<String, String>();
                	subCnt = 0;
                	for (i = 0;rstot1.size() > i;i++) {
						rst.put(rstot1.get(i), Long.toString(rstot.get(i)));
						subCnt = subCnt + rstot.get(i);
					}
                	rst.put("cm_sysmsg" , svSys+" 합계");
                	rst.put("rowhap" , Long.toString(subCnt));
                	rsval.add(rst);
                	rst = null;
                }

            	rst = new HashMap<String, String>();

            	subCnt = 0;
            	for (i = 0;rstot1.size() > i;i++) {
					rst.put(rstot1.get(i), Long.toString(subtot.get(i)));
					subCnt = subCnt + subtot.get(i);
				}
            	rst.put("cm_sysmsg" , "총계");
            	rst.put("rowhap" , Long.toString(subCnt));
            	rsval.add(rst);
            	rst = null;
			}

			//ecamsLogger.debug(rsval.toString());

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3300.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3300.getProgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3300.getProgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3300.getProgList() Exception END ##");
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
					ecamsLogger.error("## Cmp3300.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgList() method statement


	public String excelDataMake(ArrayList<HashMap<String,String>> fileList,ArrayList<HashMap<String,String>> prjStep,String UserId,String exlName) throws SQLException, Exception {

		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ArrayList<String> headerDef = new ArrayList<String>();
		excelUtil excelutil = new excelUtil();
		SystemPath systempath = new SystemPath();
		int j     = 0;
		String retMsg         = "";

		try {
			headerDef.add("cd_prjno");
			headerDef.add("cd_prjname");
			headerDef.add("rowhap");
			for (j=0;prjStep.size()>j;j++) {
				headerDef.add("col"+prjStep.get(j).get("cm_micode"));
			}

			rtList.clear();
			rst = new HashMap<String,String>();
			rst.put("cd_prjno", "프로젝트번호");
			rst.put("cd_prjname", "프로젝트명");
			rst.put("rowhap", "합계");

			for (j=0;prjStep.size()>j;j++) {
				rst.put("col"+prjStep.get(j).get("cm_micode"), prjStep.get(j).get("cm_codename"));
			}
			rtList.add(rst);
			rst = null;

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				rst.put("cd_prjno", fileList.get(i).get("cd_prjno"));
				rst.put("cd_prjname", fileList.get(i).get("cd_prjname"));
				rst.put("rowhap", fileList.get(i).get("rowhap"));
				for (j=0;prjStep.size()>j;j++) {
					rst.put("col"+prjStep.get(j).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(j).get("cm_micode")));
				}
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			String strPath = systempath.getTmpDir("99");
            //ecamsLogger.debug("++++++excel++"+rtList.toString());
			retMsg = excelutil.setExcel(strPath+"/"+exlName, headerDef, rtList);
			excelutil = null;
			systempath = null;
			rtList = null;
			return retMsg;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3300.excelDataMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3300.excelDataMake() Exception END ##");
			throw exception;
		}finally{

		}
	}

	public String excelDataMakeDetail(ArrayList<HashMap<String,String>> fileList,ArrayList<HashMap<String,String>> prjStep,String UserId,String exlName,boolean jobSw) throws SQLException, Exception {
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ArrayList<String> headerDef = new ArrayList<String>();
		excelUtil excelutil = new excelUtil();
		SystemPath systempath = new SystemPath();
		int j     = 0;
		String retMsg         = "";

		try {
			headerDef.add("cm_sysmsg");
			if (jobSw == true) headerDef.add("cm_jobname");
			headerDef.add("rowhap");
			for (j=0;prjStep.size()>j;j++) {
				headerDef.add("col"+prjStep.get(j).get("cm_micode"));
			}

			rtList.clear();
			rst = new HashMap<String,String>();
			rst.put("cm_sysmsg", "시스템");
			if (jobSw == true) rst.put("cm_jobname", "업무명");
			rst.put("rowhap", "합계");

			for (j=0;prjStep.size()>j;j++) {
				rst.put("col"+prjStep.get(j).get("cm_micode"), prjStep.get(j).get("cm_codename"));
			}
			rtList.add(rst);
			rst = null;

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				rst.put("cm_sysmsg", fileList.get(i).get("cm_sysmsg"));
				if (jobSw == true) rst.put("cm_jobname", fileList.get(i).get("cm_jobname"));
				rst.put("rowhap", fileList.get(i).get("rowhap"));
				for (j=0;prjStep.size()>j;j++) {
					rst.put("col"+prjStep.get(j).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(j).get("cm_micode")));
				}
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			String strPath = systempath.getTmpDir("99");
            //ecamsLogger.debug("++++++excel++"+rtList.toString());
			retMsg = excelutil.setExcel(strPath+"/"+exlName, headerDef, rtList);
			excelutil = null;
			systempath = null;
			rtList = null;

			return retMsg;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3300.excelDataMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3300.excelDataMake() Exception END ##");
			throw exception;
		}finally{

		}
	}
	
	
	/** 사용자가 속한 조직에서 사용되는 시스템 조회
	 * @param UserId : 사용자ID
	 * @param SelMsg : 구분
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getSysInfoOfDeptCd(String UserId,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		String            strSelMsg   = "";
		boolean           secuYn      = false;
		boolean           SilJangYn   = false;
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			
			conn = connectionContext.getConnection();

			UserInfo userinfo    = new UserInfo();
			secuYn = userinfo.isAdmin_conn(UserId, conn);//관리자여부 확인
			if ( !secuYn && userinfo.getUserRGTCD(UserId, "40", "N").indexOf("40")>-1 )//실장[40]여부 확인
				SilJangYn = true;
			userinfo = null;
			
			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}

			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysfc1,a.cm_dirbase,a.cm_sysinfo,cm_prjname \n");
			strQuery.append("  from cmm0030 a \n");
			strQuery.append(" where a.cm_closedt is null \n");//폐기된 시스템 제외
			if ( !secuYn ) {//일반사용자 일때
				if ( SilJangYn ){//실장일때
					strQuery.append("  and a.cm_syscd in (select distinct c.cm_syscd \n");
					strQuery.append("                       from cmm0040 a , cmm0040 b , cmm0044 c \n");
					strQuery.append("                      where a.cm_userid = ? \n");
					strQuery.append("                        and ( a.cm_project = b.cm_project or a.cm_project = b.cm_project2 or a.cm_project2 = b.cm_project or a.cm_project2 = b.cm_project2 ) \n");
					strQuery.append("                        and b.cm_clsdate is null \n");
					strQuery.append("                        and c.cm_userid = b.cm_userid \n");
					strQuery.append("                        and c.cm_closedt is null)  \n");
				} else {//기본사용자 일때
					strQuery.append("  and a.cm_syscd in (select cm_syscd from cmm0044 \n");
					strQuery.append("                where cm_userid=?                 \n");
					strQuery.append("                  and cm_closedt is null)         \n");
				}
			}
			strQuery.append("order by a.cm_sysmsg \n");
            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            if ( !secuYn ) pstmt.setString(1, UserId);
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg.length() > 0 &&strSelMsg != "" && !strSelMsg.equals("")) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", strSelMsg);
					rst.put("cm_sysgb", "0");
					rst.put("cm_sysinfo", "0");
					rst.put("cm_prjname", "");
					rtList.add(rst);
					rst = null;
				}
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cm_sysinfo",rs.getString("cm_sysinfo"));
				rst.put("cm_prjname", rs.getString("cm_prjname"));
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp0300.getSysInfoOfDeptCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp0300.getSysInfoOfDeptCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp0300.getSysInfoOfDeptCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp0300.getSysInfoOfDeptCd() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp0300.getSysInfoOfDeptCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfoOfDeptCd() method statement

	/** 시스템에 포함 된 모든 프로그램종류[JAWON]을 조회
	 * @param UserId : 사용자ID
	 * @param SysCd : 시스템코드
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getRsrcCd(String UserId,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			int i = 0 ;
			conn = connectionContext.getConnection();

			strQuery.append("select b.cm_micode,b.cm_codename from cmm0036 a,cmm0020 b    \n");
			strQuery.append(" where a.cm_closedt is null                                  \n");
			//파라메터로 넘어온 시스템에 대해서 조회
			String[] listSysCd = SysCd.split(",");
			strQuery.append("  and a.cm_syscd in (\n");
			if (listSysCd.length == 1)
				strQuery.append(" ? ");
			else{
				for (i=0;i<listSysCd.length;i++){
					if (i == listSysCd.length-1)
						strQuery.append(" ? ");
					else
						strQuery.append(" ? ,");
				}
			}
			strQuery.append("       )\n");
			strQuery.append("   and substr(a.cm_info,26,1)='0' \n");
			strQuery.append("   and b.cm_macode='JAWON' \n");
			strQuery.append("   and b.cm_micode <> '****' \n");
			strQuery.append("   and a.cm_rsrccd = b.cm_micode \n");
			strQuery.append(" group by b.cm_micode,b.cm_codename \n");
			strQuery.append(" order by b.cm_micode,b.cm_codename \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			for (i=0;i<listSysCd.length;i++){
				pstmt.setString(i+1, listSysCd[i]);
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp0300.getRsrcCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp0300.getRsrcCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp0300.getRsrcCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp0300.getRsrcCd() Exception END ##");
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
					ecamsLogger.error("## Cmp0300.getRsrcCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcCd() method statement

	
	/**  차트 데이터 조회, ALL:전체 시스템에 대한 프로그램 총 개수,  그외:해당 시스템의 프로그램종류 별 개수표시
	 * @param SysCd : ALL:전체
	 * @param closeYN : 폐기된 프로그램 포함[Y] / 미포함[N]
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getChartData(String SysCd,String closeYN) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  colorList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		try {
			
			conn = connectionContext.getConnection();
			
			//{sysMsg:"USA",totalCnt:4252,color:0xFF0F00}
			
			strQuery.setLength(0);
			strQuery.append("select cm_codename \n");
			strQuery.append("  from cmm0020 \n");
			strQuery.append(" where cm_macode='COLOR' \n");
			strQuery.append("   and cm_micode <> '****' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	        rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("color", rs.getString("cm_codename") );
				colorList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			
			if ( colorList.size()==0 ){
				conn.close();
				throw new Exception("Color 정보가 없습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
			
			if ( "ALL".equals(SysCd) ) {
				strQuery.setLength(0);
				strQuery.append("select cm_syscd,cm_sysmsg \n");
				strQuery.append("  from cmm0030 \n");
				strQuery.append(" where cm_closedt is null \n");
				strQuery.append(" order by cm_sysmsg \n");
				pstmt = conn.prepareStatement(strQuery.toString());
		        rs = pstmt.executeQuery();
				while (rs.next()){
					rst = new HashMap<String, String>();
					rst.put("sysMsg",rs.getString("cm_sysmsg"));
					
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt \n");
					strQuery.append("  from cmr0020 \n");
					strQuery.append(" where cr_syscd = ? \n");
					//폐기건 미포함일때
					if ( "N".equals(closeYN) ){
						strQuery.append("   and cr_status <> '9' \n");
					}
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cm_syscd") );
			        rs2 = pstmt2.executeQuery();
					if ( rs2.next() ){
						rst.put("totalCnt", rs2.getString("cnt"));
					}
					rs2.close();
					pstmt2.close();
					
					rst.put("color", colorList.get(rs.getRow()-1).get("color") );
					
					rtList.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
			} else {
				
				//1. 해당 시스템에 등록된 프로그램종류[JAWON] 리스트를 구한다.
				strQuery.setLength(0);
				strQuery.append("select b.cm_codename,b.cm_micode \n");
				strQuery.append(" from cmm0036 a, cmm0020 b \n");
				strQuery.append("where a.cm_syscd = ? \n");
				strQuery.append("  and a.cm_closedt is null \n");
				strQuery.append("  and b.cm_macode='JAWON' \n");
				strQuery.append("  and b.cm_closedt is null \n");
				strQuery.append("  and a.cm_rsrccd = b.cm_micode \n");
				strQuery.append("order by b.cm_codename  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, SysCd );
		        rs = pstmt.executeQuery();
				while (rs.next()){
					rst = new HashMap<String, String>();
					rst.put("sysMsg",rs.getString("cm_codename"));
					
					//2. 프로그램종류 별 카운트를 구한다.
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt \n");
					strQuery.append("  from cmr0020 \n");
					strQuery.append(" where cr_syscd = ? \n");
					strQuery.append("   and cr_rsrccd = ? \n");
					//폐기건 미포함일때
					if ( "N".equals(closeYN) ){
						strQuery.append("   and cr_status <> '9' \n");
					}
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, SysCd );
					pstmt2.setString(2, rs.getString("cm_micode") );
			        rs2 = pstmt2.executeQuery();
					if ( rs2.next() ){
						rst.put("totalCnt", rs2.getString("cnt"));
					}
					rs2.close();
					pstmt2.close();
					
					rst.put("color", colorList.get(rs.getRow()-1).get("color") );
					
					rtList.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
			}
			
			conn.close();
			conn = null;
			rs = null;
			pstmt2 = null;
			rs2 = null;
			
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp0300.getChartData() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp0300.getChartData() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp0300.getChartData() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp0300.getChartData() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp0300.getChartData() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
}//end of Cmp0300 class statement
