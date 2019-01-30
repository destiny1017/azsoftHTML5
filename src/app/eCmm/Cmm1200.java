/*****************************************************************************************
	1. program ID	: eCmm1200.java
	2. create date	:
	3. auth		    :
	4. update date	: 09.05.02
	5. auth		    : No Name
	6. description	: eCmm1200
*****************************************************************************************/

package app.eCmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import app.common.CreateXml;
import app.common.eCAMSInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm1200{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * project 등록
	 * @param  pPrjno,pDocname,pDocPath
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

    public Object[] getPathList(String sysCD,String spath) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		StringBuffer      strQuery2    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String			  jobNames = "";
		String			  jobLs = "";
		String			  jawonNames = "";
		String			  jawonLs  = "";
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_dirpath,cm_dsncd from cmm0070 \n");
			strQuery.append("Where cm_syscd = ? \n");
			if (spath != null){
				if (!spath.equals("")){
					strQuery.append("AND cm_dirpath like ? \n");
				}
			}
			strQuery.append("AND cm_clsdt is null \n");
			strQuery.append("order by cm_dirpath \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, sysCD);
			if (spath != null){
				if (!spath.equals("")){
					pstmt.setString(2, "%"+spath+"%");
				}
			}
			rs = pstmt.executeQuery();

            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cm_dsncd", rs.getString("cm_dsncd"));
				rst.put("selected", "0");

				strQuery2.setLength(0);
				strQuery2.append("select a.cm_micode,a.cm_codename \n");
				strQuery2.append("from cmm0020 a,cmm0072 b \n");
				strQuery2.append("where b.cm_syscd = ? \n");
				strQuery2.append("and b.cm_dsncd = ? \n");
				strQuery2.append("and a.cm_macode = 'JAWON' \n");
				strQuery2.append("and a.cm_micode = b.cm_rsrccd \n");


				pstmt2 = conn.prepareStatement(strQuery2.toString());

				pstmt2.setString(1, sysCD);
				pstmt2.setString(2, rs.getString("cm_dsncd"));


				rs2 = pstmt2.executeQuery();

				jawonNames = "";
				jawonLs	= "";
				while (rs2.next()){
					if (rs2.getRow() == 1){
						jawonNames = rs2.getString("cm_codename");
						jawonLs = rs2.getString("cm_micode");
					}
					else{
						jawonNames = jawonNames+ ", " + rs2.getString("cm_codename");
						jawonLs = jawonLs + ", " + rs2.getString("cm_micode");
					}
				}
				rs2.close();
				pstmt2.close();

				rst.put("jawonNames", jawonNames);
				rst.put("jawonLs", jawonLs);


				strQuery2.setLength(0);
				strQuery2.append("select a.cm_jobname,a.cm_jobcd \n");
				strQuery2.append("from cmm0073 b, cmm0102 a \n");
				strQuery2.append("where b.cm_syscd = ? \n");
				strQuery2.append("and b.cm_dsncd = ? \n");
				strQuery2.append("and b.cm_jobcd = a.cm_jobcd \n");


				pstmt2 = conn.prepareStatement(strQuery2.toString());

				pstmt2.setString(1, sysCD);
				pstmt2.setString(2, rs.getString("cm_dsncd"));


				rs2 = pstmt2.executeQuery();

				jobNames = "";
				jobLs	= "";
				while (rs2.next()){
					if (rs2.getRow() == 1){
						jobNames = rs2.getString("cm_jobname");
						jobLs = rs2.getString("cm_jobcd");
					}
					else{
						jobNames = jobNames+ ", " + rs2.getString("cm_jobname");
						jobLs = jobLs + ", " + rs2.getString("cm_jobcd");
					}
				}
				rs2.close();
				pstmt2.close();

				rst.put("jobNames", jobNames);
				rst.put("jobLs", jobLs);

				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

            rtObj =  rsval.toArray();
            rsval.clear();
            rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1200.getPathList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1200.getPathList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1200.getPathList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.getPathList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (strQuery2 != null) 	strQuery2 = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.getPathList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPathList() method statement


    public HashMap<String,ArrayList<HashMap<String,String>>> getBaseInfo(String sysCD,String baseCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = null;
		HashMap<String, String>			  rst		  = null;
		HashMap<String,ArrayList<HashMap<String,String>>> rth = null;


		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();



			/************************업무******************************/
			rth = new HashMap<String,ArrayList<HashMap<String,String>>>();


			strQuery.append("select b.cm_jobcd,b.cm_jobname \n");
			strQuery.append("from cmm0102 b,cmm0034 a \n");
			strQuery.append("where a.cm_syscd = ? \n");
			strQuery.append("and a.cm_jobcd=b.cm_jobcd \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, sysCD);

            rs = pstmt.executeQuery();

            rsval = new ArrayList<HashMap<String, String>>();
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("selected", "0");
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();

            rth.put("job", rsval);
            rsval = null;


            /************************자원******************************/
            rsval = new ArrayList<HashMap<String, String>>();

            strQuery.setLength(0);
			strQuery.append("select b.cm_micode,b.cm_codename \n");
			strQuery.append("from cmm0020 b,cmm0036 a \n");
			strQuery.append("where a.cm_syscd = ? \n");
			strQuery.append("and b.cm_macode ='JAWON' \n");
			strQuery.append("and b.cm_closedt is null \n");
			strQuery.append("and a.cm_rsrccd=b.cm_micode \n");
			strQuery.append("order by b.cm_micode \n");


			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, sysCD);

            rs = pstmt.executeQuery();

            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("selected", "0");
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();

            rth.put("jawon", rsval);
            rsval = null;


            /************************PATH******************************/
            rsval = new ArrayList<HashMap<String, String>>();

            strQuery.setLength(0);
			strQuery.append("select cm_volpath \n");
			strQuery.append("from cmm0031 \n");
			strQuery.append("where cm_syscd = ? \n");
			strQuery.append("and cm_svrcd = ? \n");
			strQuery.append("and cm_closedt is null \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, sysCD);
			pstmt.setString(2, baseCD);

            rs = pstmt.executeQuery();

            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();

            rth.put("path", rsval);
            rsval = null;

            conn.close();
            conn = null;

            return rth;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1200.getBaseInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1200.getBaseInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1200.getBaseInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.getBaseInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rth != null) rth = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.getBaseInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getBaseInfo() method statement


    public HashMap<String, String> removePath(String sysCD,String spath,String dsnCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int			      cnt0020 = 0;

		try {
			conn = connectionContext.getConnection();


			if (dsnCD == null){
				rst = new HashMap<String, String>();
				rst.put("retVal", "0");
				rst.put("retMsg", "삭제처리가 완료되었습니다.");
				conn.close();
				conn = null;
				return rst;
			}

			if (dsnCD.equals("")){
				rst = new HashMap<String, String>();
				rst.put("retVal", "0");
				rst.put("retMsg", "삭제처리가 완료되었습니다.");
				conn.close();
				conn = null;
				return rst;
			}

			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmr0020 \n");
			strQuery.append("where cr_syscd= ? \n");
			strQuery.append("and cr_dsncd= ? \n");


			pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, sysCD);
            pstmt.setString(2, dsnCD);


			rs = pstmt.executeQuery();


			cnt0020 = 0;
			if (rs.next()){
            	cnt0020 = rs.getInt("cnt");
			}
            rs.close();
            pstmt.close();

            if (cnt0020> 0){
            	rst = new HashMap<String, String>();
            	rst.put("retVal", "1");
            	rst.put("retMsg", "["+spath+"]로  등록된 프로그램이 존재하여 삭제가 불가능합니다.");
            	conn.close();
            	conn = null;
            	return rst;
            }

            conn.setAutoCommit(false);
            strQuery.setLength(0);
			strQuery.append("delete cmm0072 \n");
			strQuery.append("where cm_syscd= ? \n");
			strQuery.append("and cm_dsncd= ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, sysCD);
            pstmt.setString(2, dsnCD);


            pstmt.executeUpdate();

            pstmt.close();

            strQuery.setLength(0);
			strQuery.append("delete cmm0073 \n");
			strQuery.append("where cm_syscd= ? \n");
			strQuery.append("and cm_dsncd= ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, sysCD);
            pstmt.setString(2, dsnCD);


            pstmt.executeUpdate();

            pstmt.close();


            strQuery.setLength(0);
			strQuery.append("delete cmm0070 \n");
			strQuery.append("where cm_syscd= ? \n");
			strQuery.append("and cm_dsncd= ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());

            pstmt.setString(1, sysCD);
            pstmt.setString(2, dsnCD);


            pstmt.executeUpdate();

            pstmt.close();

            rst = new HashMap<String, String>();
        	rst.put("retVal", "0");
        	rst.put("retMsg", "삭제처리가 완료되었습니다.");

            conn.commit();
            conn.close();
            pstmt = null;
            conn = null;


            return rst;
            //ecamsLogger.error(rsval.toString());


		} catch (SQLException sqlexception) {
			if (rs != null){
				try{
					rs.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			if (pstmt != null){
				try{
					pstmt.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1200.removePath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1200.removePath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			if (rs != null){
				try{
					rs.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			if (pstmt != null){
				try{
					pstmt.close();
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1200.removePath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.removePath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.removePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of removePath() method statement

	public HashMap<String,String> savePath(String sysCD,String UserId,ArrayList<HashMap<String,String>> saveList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		int				  i,j;
		HashMap<String,String>	rData = null;

		boolean			  nFlag = false;
		String			  strDsnCd = "";

		String[]  tmpAry =  {};


		try {
			conn = connectionContext.getConnection();

	        conn.setAutoCommit(false);

        	for (i=0;i<saveList.size();i++){
        		nFlag = false;
        		if (saveList.get(i).get("cm_dsncd") == null){
        			nFlag = true;
        		}

        		if (saveList.get(i).get("cm_dsncd").equals("")){
        			nFlag = true;
        		}

        		if (nFlag == false){
        			strDsnCd = saveList.get(i).get("cm_dsncd");

	        		strQuery.setLength(0);
	    			strQuery.append("delete cmm0072 \n");
	    			strQuery.append("where cm_syscd= ? \n");
	    			strQuery.append("and cm_dsncd= ? \n");
	    			pstmt = conn.prepareStatement(strQuery.toString());

	                pstmt.setString(1, sysCD);
	                pstmt.setString(2, strDsnCd);


	                pstmt.executeUpdate();

	                pstmt.close();

	                strQuery.setLength(0);
	    			strQuery.append("delete cmm0073 \n");
	    			strQuery.append("where cm_syscd= ? \n");
	    			strQuery.append("and cm_dsncd= ? \n");
	    			pstmt = conn.prepareStatement(strQuery.toString());

	                pstmt.setString(1, sysCD);
	                pstmt.setString(2, strDsnCd);


	                pstmt.executeUpdate();

	                pstmt.close();
        		}

                if (nFlag == true){
                	strQuery.setLength(0);
            		strQuery.append("insert into cmm0070                               \n");
            		strQuery.append("  (CM_SYSCD,CM_DSNCD,CM_DIRPATH,CM_EDITOR,CM_OPENDT,CM_LASTUPDT) \n");
            		strQuery.append("  ( select ?, decode(nvl(max(cm_dsncd),'0'),'0','0000001',lpad(to_number(max(cm_dsncd)) + 1,7,'0')), \n");
            		strQuery.append("    ?, ?, SYSDATE,SYSDATE \n");
            		strQuery.append("    from cmm0070 \n");
            		strQuery.append("    where cm_syscd = ? ) \n");
            		pstmt = conn.prepareStatement(strQuery.toString());
            		pstmt.setString(1, sysCD);
            		pstmt.setString(2, saveList.get(i).get("cm_dirpath"));
            		pstmt.setString(3, UserId);
            		pstmt.setString(4, sysCD);
            		pstmt.executeUpdate();
            		pstmt.close();

            		strQuery.setLength(0);
        			strQuery.append("select cm_dsncd from cmm0070                           \n");
        			strQuery.append(" where cm_syscd=?                                      \n");
        			strQuery.append(" and cm_dirpath=?                                      \n");
                    pstmt = conn.prepareStatement(strQuery.toString());
                    pstmt.setString(1, sysCD);
                    pstmt.setString(2, saveList.get(i).get("cm_dirpath"));

                    rs = pstmt.executeQuery();

                    if (rs.next()){
                    	strDsnCd = rs.getString("cm_dsncd");
                    }

                    rs.close();
                    pstmt.close();
                }
                else{
                	strQuery.setLength(0);
        			strQuery.append("update cmm0070 set cm_editor = ?, \n");
        			strQuery.append("					cm_lastupdt=SYSDATE, \n");
        			strQuery.append("					cm_clsdt='', \n");
        			strQuery.append("					cm_runsta='0' \n");
        			strQuery.append("		where cm_syscd= ? 		 \n");
        			strQuery.append("		and cm_dsncd = ? \n");

        			pstmt = conn.prepareStatement(strQuery.toString());

        			pstmt.setString(1, UserId);
        			pstmt.setString(2, sysCD);
        			pstmt.setString(3, strDsnCd);
        			pstmt.executeUpdate();
        			pstmt.close();

                }

                if (strDsnCd.equals("")){
                	rData = new HashMap<String,String>();
                	rData.put("retVal", "1");
                	rData.put("retMsg", "디렉토리 등록 실패하였습니다.");
                	conn.rollback();
                	conn.close();
                	conn = null;
                	return rData;
                }

                //ecamsLogger.error(saveList.get(i).get("jawonLs"));
                tmpAry = saveList.get(i).get("jawonLs").split(", ");

                for (j=0;j<tmpAry.length;j++){
                	if (tmpAry[j] == null){
                		continue;
                	}

                	if (tmpAry[j].equals("")){
                		continue;
                	}
                   	strQuery.setLength(0);
        			strQuery.append("insert into cmm0072 (cm_syscd,cm_dsncd,cm_rsrccd) values ( \n");
        			strQuery.append(" ?, ? , ? ) \n");

        			pstmt = conn.prepareStatement(strQuery.toString());

        			pstmt.setString(1, sysCD);
        			pstmt.setString(2, strDsnCd);
        			pstmt.setString(3, tmpAry[j]);
        			pstmt.executeUpdate();
        			pstmt.close();
                }

                tmpAry = null;

                tmpAry = saveList.get(i).get("jobLs").split(", ");

                for (j=0;j<tmpAry.length;j++){
                	if (tmpAry[j] == null){
                		continue;
                	}

                	if (tmpAry[j].equals("")){
                		continue;
                	}
                   	strQuery.setLength(0);
        			strQuery.append("insert into cmm0073 (cm_syscd,cm_dsncd,cm_jobcd) values ( \n");
        			strQuery.append(" ?, ? , ? ) \n");

        			pstmt = conn.prepareStatement(strQuery.toString());

        			pstmt.setString(1, sysCD);
        			pstmt.setString(2, strDsnCd);
        			pstmt.setString(3, tmpAry[j]);
        			pstmt.executeUpdate();
        			pstmt.close();
                }

                tmpAry = null;

        	}




        	conn.commit();
        	conn.close();
        	pstmt = null;
        	conn = null;

        	rData = new HashMap<String,String>();
        	rData.put("retVal", "0");
        	rData.put("retMsg", "디렉토리를 등록처리 하였습니다.");

        	return rData;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.savePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm1200.savePath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1200.savePath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.savePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm1200.savePath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.savePath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.savePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public HashMap<String,String> savePath2(String sysCD,String UserId,ArrayList<HashMap<String,String>> saveList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		HashMap<String,String>	rData = null;
		boolean			  nFlag = false;
		String			  strDsnCd = "";

		try {
			conn = connectionContext.getConnection();
	        conn.setAutoCommit(false);

	        int	i=0;
        	for (i=0;i<saveList.size();i++){
        		nFlag = false;

        		strDsnCd = "";

        		//ecamsLogger.error(saveList.get(i).get("cm_dirpath"));
        		strQuery.setLength(0);
        		strQuery.append("select cm_dsncd from cmm0070 \n");
        		strQuery.append("where cm_syscd = ? \n");
        		strQuery.append("and cm_dirpath = ? \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
        		pstmt.setString(1, sysCD);
        		pstmt.setString(2, saveList.get(i).get("cm_dirpath"));

        		rs = pstmt.executeQuery();

        		if (rs.next()){
        			strDsnCd = rs.getString("cm_dsncd");
        		}

        		rs.close();
        		pstmt.close();

        		if (strDsnCd == null){
        			nFlag = true;
        		}

        		if (strDsnCd.equals("")){
        			nFlag = true;
        		}


                if (nFlag == true){
                	strQuery.setLength(0);
            		strQuery.append("insert into cmm0070                               \n");
            		strQuery.append("  (CM_SYSCD,CM_DSNCD,CM_DIRPATH,CM_EDITOR,CM_OPENDT,CM_LASTUPDT) \n");
            		strQuery.append("  ( select ?, decode(nvl(max(cm_dsncd),'0'),'0','0000001',lpad(to_number(max(cm_dsncd)) + 1,7,'0')), \n");
            		strQuery.append("    ?, ?, SYSDATE,SYSDATE \n");
            		strQuery.append("    from cmm0070 \n");
            		strQuery.append("    where cm_syscd = ? ) \n");
            		pstmt = conn.prepareStatement(strQuery.toString());
            		pstmt.setString(1, sysCD);
            		pstmt.setString(2, saveList.get(i).get("cm_dirpath"));
            		pstmt.setString(3, UserId);
            		pstmt.setString(4, sysCD);
            		pstmt.executeUpdate();
            		pstmt.close();

            		strQuery.setLength(0);
        			strQuery.append("select cm_dsncd from cmm0070                           \n");
        			strQuery.append(" where cm_syscd=?                                      \n");
        			strQuery.append(" and cm_dirpath=?                                      \n");
                    pstmt = conn.prepareStatement(strQuery.toString());
                    pstmt.setString(1, sysCD);
                    pstmt.setString(2, saveList.get(i).get("cm_dirpath"));

                    rs = pstmt.executeQuery();

                    if (rs.next()){
                    	strDsnCd = rs.getString("cm_dsncd");
                    }

                    rs.close();
                    pstmt.close();
                }
                else{
                	strQuery.setLength(0);
        			strQuery.append("update cmm0070 set cm_editor = ?, \n");
        			strQuery.append("					cm_lastupdt=SYSDATE, \n");
        			strQuery.append("					cm_clsdt='', \n");
        			strQuery.append("					cm_runsta='0' \n");
        			strQuery.append("		where cm_syscd= ? 		 \n");
        			strQuery.append("		and cm_dsncd = ? \n");

        			pstmt = conn.prepareStatement(strQuery.toString());

        			pstmt.setString(1, UserId);
        			pstmt.setString(2, sysCD);
        			pstmt.setString(3, strDsnCd);
        			pstmt.executeUpdate();
        			pstmt.close();

                }

                if (strDsnCd.equals("")){
                	rData = new HashMap<String,String>();
                	rData.put("retVal", "1");
                	rData.put("retMsg", "디렉토리 등록 실패하였습니다.");
                	conn.rollback();
                	conn.close();
                	conn = null;
                	return rData;
                }


        	}



        	conn.commit();
        	conn.close();
        	conn = null;
        	pstmt = null;

        	rData = new HashMap<String,String>();
        	rData.put("retVal", "0");
        	rData.put("retMsg", "등록처리가 완료되었습니다.");

        	return rData;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.savePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm1200.savePath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1200.savePath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.savePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm1200.savePath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.savePath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.savePath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	//public Object[] getSvrDir(String UserID,String SysCd,String SvrIp,String SvrPort,String BaseDir,String svrnm) throws SQLException, Exception {
	public Object[] getSvrDir(String UserID,String SysCd,String SvrIp,String SvrPort,String BaseDir) throws Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;

		CreateXml         ecmmtb      = new CreateXml();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String[]          pathDepth   = null;
		String            strDir      = null;
		boolean           findSw      = false;
		boolean           ErrSw      = false;
		String            strBinPath  = "";
		String            strTmpPath  = "";
		String            strFile     = "";
		String            makeFile    = "";
		String            strBaseDir  = "";
		int               upSeq       = 0;
		int               maxSeq      = 0;
		File shfile=null;
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		ArrayList<Document> list = null;

		rsval.clear();

		try {

			eCAMSInfo         ecamsinfo   = new eCAMSInfo();
			strBinPath = ecamsinfo.getFileInfo("14");
			ErrSw = false;
			if (strBinPath == "" || strBinPath == null){
				ecamsinfo = null;
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");
			}

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null){
				ecamsinfo = null;
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");
			}
			ecamsinfo = null;

			try {
				makeFile = "dir" + UserID;
				strFile = strTmpPath + makeFile;

				shfile=null;
				shfile = new File(strTmpPath + makeFile+".sh");              //File 불러온다.
				if( !(shfile.isFile()) )              //File이 없으면
				{
					shfile.createNewFile();          //File 생성
				}
				writer = new OutputStreamWriter( new FileOutputStream(strTmpPath + makeFile+".sh"));
				writer.write("cd "+strBinPath +"\n");
				//writer.write("rtval=`./ecams_dir " + SvrIp + " " + SvrPort + " " + buffsize + " " + BaseDir + " " + makeFile +"`\n");
				writer.write("./ecams_dir " + SvrIp + " " + SvrPort + " " + BaseDir + " " + makeFile +"\n");
				writer.write("exit $?\n");
				writer.close();

				strAry = new String[3];
				strAry[0] = "chmod";
				strAry[1] = "777";
				strAry[2] = strTmpPath + makeFile+".sh";
				run = Runtime.getRuntime();
				p = run.exec(strAry);
				p.waitFor();

				run = Runtime.getRuntime();
				strAry = new String[2];
				strAry[0] = "/bin/sh";
				strAry[1] = strTmpPath + makeFile+".sh";
				p = run.exec(strAry);
				p.waitFor();

				//ecamsLogger.debug("====return===="+Integer.toString(p.exitValue())+" \n");
				if (p.exitValue() != 0) {
				    ErrSw = true;
				    shfile.delete();
					if (shfile != null) shfile = null;
					if (strAry != null) strAry = null;
					if (run != null) run = null;
					if (p != null) p = null;
					throw new Exception("디렉토리추출을 위한 작업에 실패하였습니다");
				}
				else{
					shfile.delete();
				}
			} catch (Exception e) {
				if (shfile != null) shfile = null;
				if (strAry != null) strAry = null;
				if (run != null) run = null;
				if (p != null) p = null;
				throw new Exception(e);
			}

			//strFile = "c:\\eCAMS\\temp\\dir9812705";
            if (ErrSw == false) {
				File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("디렉토리추출을 위한 작업에 실패하였습니다 [작성된 파일 없음]");
		        } else {
			        BufferedReader in = null;
			        //PrintWriter out = null;

			        try {
			            in = new BufferedReader(new FileReader(mFile));
			            ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");
			            String str = null;
			            //strBaseDir = BaseDir;
			            while ((str = in.readLine()) != null) {
			                if (str.length() > 0) {
			                	if (str.substring(str.length() - 1).equals(":")) {
			                		strBaseDir = str.substring(0,str.length() - 1);
			                		if (!strBaseDir.substring(strBaseDir.length() - 1).equals("/")) strBaseDir = strBaseDir + "/";
			                	}
			                	else {
			                		if (str.substring(str.length() - 1).equals("/")) str = str.substring(0,str.length() - 1);
			                		str = strBaseDir + str;
			                		pathDepth = str.substring(1).split("/");
			                		strDir = "/";
									upSeq = 0;
									findSw = false;
									for (int i = 0;pathDepth.length > i;i++) {
										if (pathDepth[i].length() > 0) {
											if (strDir.length() > 1 ) {
												strDir = strDir + "/";
											}
											strDir = strDir + pathDepth[i];
											findSw = false;
											if (rsval.size() > 0) {
												for (int j = 0;rsval.size() > j;j++) {
													if (rsval.get(j).get("cm_fullpath").equals(strDir)) {
														upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));
														findSw = true;
														break;
													}
												}
											} else {
												findSw = false;
											}
											if ( !findSw ) {
												maxSeq = maxSeq + 1;

												//ecamsLogger.debug("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
												rst = new HashMap<String,String>();
												rst.put("cm_dirpath",pathDepth[i]);
												rst.put("cm_fullpath",strDir);
												rst.put("cm_upseq",Integer.toString(upSeq));
												rst.put("cm_seqno",Integer.toString(maxSeq));
												rsval.add(maxSeq - 1, rst);
												rst = null;
												upSeq = maxSeq;
											}
										}
									}
			                	}
			                }
			            }
			        } finally {
			            if (in != null)
			                in.close();
			        }
		        }
		        if (mFile.isFile() && mFile.exists()) mFile.delete();
		        if (mFile != null) mFile = null;
            }
            if (rsval.size() > 0) {
				for (int i = 0;rsval.size() > i;i++) {
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cm_dsncd"),
							"true",rsval.get(i).get("cm_upseq"));
				}
			}

    		list = new ArrayList<Document>();
    		list.add(ecmmtb.getDocument());

    		return list.toArray();

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1200.getSvrDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.getSvrDir() Exception END ##");
			throw exception;
		}finally{
			if (ecmmtb != null) ecmmtb = null;
			if (shfile != null) shfile = null;
			if (strAry != null) strAry = null;
			if (run != null) run = null;
			if (p != null) p = null;
			if (list != null)	list = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getSvrDir() method statement

    public Object[] chkDirPath(ArrayList<HashMap<String,String>> DirList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int               i = 0;
		boolean           findSw = false;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			rsval.clear();
			for (i=0;DirList.size()>i;i++) {
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt             \n");
				strQuery.append("  from cmm0070                     \n");
				strQuery.append(" where cm_syscd=? and cm_dirpath=? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, DirList.get(i).get("cm_syscd"));
				pstmt.setString(2, DirList.get(i).get("cm_dirpath"));
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	if (rs.getInt("cnt")>0) findSw = true;
	            	else findSw = false;
	            }
	            rs.close();
	            pstmt.close();

	            if (findSw == false) {
	            	rst = new HashMap<String, String>();
	            	rst.put("cm_dirpath", DirList.get(i).get("cm_dirpath"));
	            	rst.put("cm_syscd", DirList.get(i).get("cm_syscd"));
	            	rst.put("cm_userid", DirList.get(i).get("strUserId"));
	            	rst.put("selected", "1");
	            	rsval.add(rst);
	            	rst = null;
	            }
			}
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

            returnObjectArray = rsval.toArray();
            rsval.clear();
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1200.chkDirPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1200.chkDirPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1200.chkDirPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1200.chkDirPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1200.chkDirPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of chkDirPath() method statement

}//end of Cmm1200 class statement
