
/*****************************************************************************************
	1. program ID	: eCmd0400.java
	2. create date	: 2006.08. 08
	3. auth		    : NoName
	4. update date	:
	5. auth		    :
	6. description	: eCmd1500 [문서관리]->[상세정보]
*****************************************************************************************/

package app.eCmd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import oracle.jdbc.internal.OracleResultSet;
import oracle.sql.BLOB;

import org.apache.log4j.Logger;

import sun.security.action.GetBooleanAction;
import app.common.AutoSeq;
import app.common.SysInfo;
import app.common.SystemPath;
import app.common.UserInfo;
import app.common.eCAMSInfo;
import app.eCmr.Cmr0200;
import app.eCmr.Cmr0250;
//import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Cmd1500{
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


	public Object[] getPrjNo(String DocId,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		//String            strSelMsg   = "";
		//String            svPrjNo     = "";
		//boolean           findSw      = false;
		ArrayList<HashMap<String, String>> rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			   rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			//strSelMsg = "선택하세요";

			strQuery.setLength(0);
			strQuery.append("Select a.cd_prjno,a.cd_prjname,'N' cd_edityn      \n");
			strQuery.append("  from cmd0300 a,cmr0031 b                        \n");
			strQuery.append(" Where b.cr_docid=? and b.cr_prjno=a.cd_prjno     \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, DocId);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cd_prjno",rs.getString("cd_prjno"));
				rst.put("cd_prjname",rs.getString("cd_prjname"));
				rst.put("prjnoname",rs.getString("cd_prjno") + "   " +
						rs.getString("cd_prjname"));
				rst.put("lastPrj", "N");

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmd0304          \n");
				strQuery.append(" where cd_prjno=? and cd_edityn='Y'          \n");
				strQuery.append("   and cd_prjuser=? and cd_closedt is null   \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("cd_prjno"));
				pstmt2.setString(2, UserId);
				rs2 = pstmt2.executeQuery();
				if(rs2.next()) {
				   if (rs2.getInt("cnt") > 0) rst.put("cd_edityn", "Y");
				   else rst.put("cd_edityn", "N");
				}
				rs2.close();
				pstmt2.close();

				strQuery.setLength(0);
				strQuery.append("Select count(cr_docid) as cnt from cmr0030 where cr_prjno=? ");//prjno
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("cd_prjno"));
				rs2 = pstmt2.executeQuery();
				if(rs2.next()) {
					if (rs2.getInt("cnt") > 0) rst.put("docFileYN", "Y");
					else rst.put("docFileYN", "N");
				}
				rs2.close();
				pstmt2.close();
				rtList.add(rst);
				rst = null;

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			rs2 = null;
			pstmt = null;
			pstmt2 = null;
			conn = null;

			return rtList.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getPrjNo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getPrjNo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getPrjNo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getPrjNo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}

	}//end of getPrjNo() method statement
    public Object[] getHistList(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			String DocId = dataObj.get("DocId");
			String ReqCd = dataObj.get("ReqCd");
			String CondCd = dataObj.get("CondCd");
			String CondWord = dataObj.get("CondWord");

			strQuery.setLength(0);
		    strQuery.append("select a.cr_acptno,a.cr_version,a.cr_qrycd,a.cr_unittit,a.cr_status,a.cr_errcd, \n");
		    strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') cr_prcdate,   \n");
		    strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi') cr_acptdate, \n");
    	    strQuery.append("       b.cm_username,c.cr_sayu,c.cr_emgcd,c.cr_docno,           \n");
    	    strQuery.append("       c.cr_qrycd qrycd,d.cm_codename,f.cr_docfile              \n");
    	    strQuery.append("  from cmr0030 f,cmm0020 d,cmr1100 a,cmm0040 b,cmr1000 c        \n");
    	    strQuery.append(" where a.cr_docid=? and a.cr_acptno=c.cr_acptno                 \n"); //PrjNo
    	    if (ReqCd != "" && ReqCd != null)//Cbo_ReqCd 전체가 아니면
    	       strQuery.append("and c.cr_qrycd=?                                             \n");//Cbo_ReqCd

    	    if (CondCd != "" && CondCd != null){
    	    	if (CondCd.equals("02"))                      //요청번호
    	    		strQuery.append("and c.cr_acptno like ?                                  \n");// % Replace(Txt_Cond, "-", "") %
    	    	else if (CondCd.equals("03")) strQuery.append("and c.cr_sayu like ?          \n");//%Txt_Cond%
    	    	else if (CondCd.equals("04")) {               //요청자
    	    		strQuery.append("and (c.cr_editor like ? or b.cm_username like ?)        \n");//%Txt_Cond%
    	    	}else if (CondCd.equals("06")) {              //변경근거
    	    		strQuery.append("and c.cr_docno is not null                              \n");
    	        	strQuery.append("and c.cr_docno like ?                                   \n");//%Txt_Cond%
    	    	}
    	    }
    	    strQuery.append("  and a.cr_editor=b.cm_userid                                   \n");
    	    strQuery.append("  and a.cr_docid=f.cr_docid                                     \n");
    	    strQuery.append("  and d.cm_macode='REQUEST' and d.cm_micode=c.cr_qrycd          \n");
    	    strQuery.append("order by c.cr_acptdate desc                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            int Cnt = 0;
            pstmt.setString(++Cnt, DocId);
            if (ReqCd != "" && ReqCd != null)//Cbo_ReqCd 전체가 아니면
     	       pstmt.setString(++Cnt, ReqCd);
     	    if (CondCd != "" && CondCd != null){
     	    	if (CondCd.equals("02"))                      //요청번호
     	    		pstmt.setString(++Cnt,CondWord);
     	    	else if (CondCd.equals("03")) pstmt.setString(++Cnt,CondWord);
     	    	else if (CondCd.equals("04")) {               //요청자
     	    		pstmt.setString(++Cnt,CondWord);
     	    		pstmt.setString(++Cnt,CondWord);
     	    	}else if (CondCd.equals("06")) {              //변경근거
     	    		pstmt.setString(++Cnt,CondWord);
     	    		pstmt.setString(++Cnt,CondWord);
     	    	}
     	    }
           	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
            String Tmp = "";
            String SubItems1 = "";
            String CodeName_Tmp1 = "";
            String CodeName_Tmp2 = "";
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("NO", Integer.toString(rs.getRow()));

            	SubItems1 = rs.getString("cm_codename");
                if (!rs.getString("qrycd").equals("31")){
                    if (!Tmp.equals(rs.getString("cr_qrycd"))){
                    	strQuery.setLength(0);
                    	strQuery.append("select cm_codename from cmm0020 \n");
                    	strQuery.append("where cm_macode='CHECKIN' and cm_micode=? \n");//rs.getString("cr_qrycd")
                        pstmt2 = conn.prepareStatement(strQuery.toString());
                        pstmt2.setString(1, rs.getString("cr_qrycd"));
                        rs2 = pstmt2.executeQuery();
                        if (rs2.next()){
                            CodeName_Tmp1 = rs2.getString("cm_codename");
                            if (CodeName_Tmp1 != null && CodeName_Tmp1 != "")
                            	SubItems1 = SubItems1 + "[" + CodeName_Tmp1 + "]";
                        }
                        rs2.close();
                        pstmt2.close();
                    }else{
                    	if (CodeName_Tmp1 != null)
                    		SubItems1 = SubItems1 + "[" + CodeName_Tmp1 + "]";
                    }
                }
                rst.put("SubItems1", SubItems1);
                rst.put("cm_username", rs.getString("cm_username"));
                //ecamsLogger.debug("+++++++++++cr_status++++++"+rs.getString("cr_status"));

                if (rs.getString("cr_prcdate") == null) rst.put("cr_prcdate", "진행중");
                else {
                	if (rs.getString("cr_status").equals("3")) rst.put("cr_prcdate", "[반송]"+rs.getString("cr_prcdate"));
                	else  rst.put("cr_prcdate", rs.getString("cr_prcdate"));
                }
                rst.put("cr_docfile", rs.getString("cr_docfile"));
                rst.put("cr_acptno", rs.getString("cr_acptno"));
                rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+
                		             rs.getString("cr_acptno").substring(4,6)+"-"+
                		             rs.getString("cr_acptno").substring(6,12));
                rst.put("cr_version", rs.getString("cr_version"));
                if (rs.getString("cr_errcd") == null){
                	rst.put("cr_errcd", "");
                }else{
                	rst.put("cr_errcd", rs.getString("cr_errcd"));
                }

                if (rs.getString("cr_emgcd") != null){
                	strQuery.setLength(0);
                	strQuery.append("select cm_codename from cmm0020 \n");
                	strQuery.append("where cm_macode='REQGBN' and cm_micode=? \n");//cr_emgcd
                	pstmt2 = conn.prepareStatement(strQuery.toString());
                	pstmt2.setString(1,rs.getString("cr_emgcd"));
                	rs2 = pstmt2.executeQuery();
                	if (rs2.next()){
                		CodeName_Tmp2 = "[" + rs2.getString("cm_codename") + "] ";
                	}
                	rs2.close();
                	pstmt2.close();
                    if (rs.getString("cr_docno") != null){
                    	CodeName_Tmp2 = CodeName_Tmp2 + rs.getString("cr_docno");
                    }
                    rst.put("SubItems7", CodeName_Tmp2);
                }else rst.put("SubItems7", "");

                if (rs.getString("cr_unittit") != null)
                   rst.put("SubItems8", rs.getString("cr_unittit"));
                else
                   rst.put("SubItems8", rs.getString("cr_sayu"));

                rst.put("cr_acptno", rs.getString("cr_acptno"));
                rst.put("cr_qrycd", rs.getString("cr_qrycd"));
                rst.put("qrycd", rs.getString("qrycd"));
                rst.put("cr_status", rs.getString("cr_status"));
                Tmp = rs.getString("cr_qrycd");
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
			ecamsLogger.error("## Cmd1500.getHistList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getHistList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getHistList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getHistList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getHistList() method statement

//    public Object[] getDocList(String UserId,String PrjNo,String CondCd,String CondWord,String dirYn,String DocSeq,String DocId) throws SQLException, Exception {
    public Object[] getDocList(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2       = null;
		ResultSet         	rs2          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;
		int                 Cnt         = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			String UserId = dataObj.get("UserId");
			String PrjNo = dataObj.get("PrjNo");
			String CondCd = dataObj.get("CondCd");
			String CondWord = dataObj.get("CondWord");
			String dirYn = dataObj.get("dirYn");
			String strDocSeq = dataObj.get("strDocSeq");
			String strDocId = dataObj.get("strDocId");

			strQuery.setLength(0);
	        strQuery.append("select a.cr_docid,a.cr_docfile,a.cr_lstver,a.cr_status,         \n");
	        strQuery.append("       c.cr_prjno,d.cm_codename sta,f.cm_username creator,      \n");
	        strQuery.append("       to_char(a.cr_creatdt,'yyyy/mm/dd hh24:mi') cr_creatdt,   \n");
	        strQuery.append("       to_char(a.cr_lastdt,'yyyy/mm/dd hh24:mi') cr_lastdt,     \n");
	        strQuery.append("       a.cr_ccbyn,g.cm_username editor,a.cr_devstep,a.cr_docsta \n");
	        strQuery.append("  from cmr0030 a,cmr0031 c,cmm0020 d,cmm0040 f,cmm0040 g        \n");
	        strQuery.append(" where c.cr_prjno=? and c.cr_docid=a.cr_docid                   \n");//PrjNo
	        if (strDocId != null && strDocId != "") strQuery.append("and c.cr_docid=?        \n");//docid
	        else {
	        	if (!CondCd.equals("00")) {
		        	if (CondCd.equals("04")) {
		        		strQuery.append("and a.cr_devstep in (select cm_micode from cmm0020  \n");
		        		strQuery.append("                  where cm_macode='DEVSTEP'         \n");
		        		strQuery.append("                  and cm_codename like '%' || ? || '%') \n");//devstepname
		        	} else if (CondCd.equals("05")) {
		        		strQuery.append("and a.cr_docfile like '%' || ? || '%'               \n");//docfile
		        	}
		        }

		        if (strDocSeq != "" && strDocSeq != null){
	                if (dirYn.equals("true")){//하위폴더포함
	                	strQuery.append("and c.cr_docseq in (SELECT CD_DOCSEQ                 \n");
	                	strQuery.append("   FROM (select CD_DOCSEQ,CD_UPDOCSEQ from CMD0303   \n");
	                	strQuery.append("   WHERE CD_PRJNO = ?)                               \n");//PrjNo
	                	strQuery.append("   START WITH CD_DOCSEQ=?                            \n");//DocSeq
	                	strQuery.append("   CONNECT BY PRIOR CD_DOCSEQ = CD_UPDOCSEQ          \n");
	                	strQuery.append("   union select ? from dual)         \n");//DocSeq
	                }else
	                	strQuery.append("and c.cr_docseq=? \n");//DocSeq
	            }
	        }
	        strQuery.append("  and a.cr_creator=f.cm_userid and a.cr_editor=g.cm_userid      \n");
	        strQuery.append("  and d.cm_macode='CMR0020' and d.cm_micode=a.cr_status         \n");
	        if (dataObj.get("Chk_Abandon").equals("false")){
	        	strQuery.append("  and a.cr_closedt is null \n");
	        }
	        strQuery.append("  order by cr_docfile \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(++Cnt, PrjNo);
            if (strDocId != null && strDocId != "") pstmt.setString(++Cnt,strDocId);
            else {
            	if (!CondCd.equals("00")) {
    	        	if (CondCd.equals("04")) {
    	        		pstmt.setString(++Cnt, CondWord);
    	        	} else if (CondCd.equals("05")) {
    	        		pstmt.setString(++Cnt, CondWord);
    	        	}
    	        }

                if (strDocSeq != "" && strDocSeq != null){
                    if (dirYn.equals("true")){//하위폴더포함
                    	pstmt.setString(++Cnt, PrjNo);
                    	pstmt.setString(++Cnt, strDocSeq);
                    	pstmt.setString(++Cnt, strDocSeq);
                    }else
                    	pstmt.setString(++Cnt, strDocSeq);
                }
            }

           	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("cr_creatdt", rs.getString("cr_creatdt"));
            	rst.put("creator", rs.getString("creator"));
            	rst.put("cr_lastdt", rs.getString("cr_lastdt"));
            	rst.put("editor", rs.getString("editor"));
            	rst.put("sta", rs.getString("sta"));
            	rst.put("cr_docfile", rs.getString("cr_docfile"));
            	rst.put("cr_docid", rs.getString("cr_docid"));
            	rst.put("cr_ccbyn", rs.getString("cr_ccbyn"));
            	rst.put("cr_status", rs.getString("cr_status"));
            	rst.put("cr_lstver", rs.getString("cr_lstver"));
            	rst.put("cr_devstep", rs.getString("cr_devstep"));
            	rst.put("cr_docsta", rs.getString("cr_docsta"));

            	strQuery.setLength(0);
            	strQuery.append("select count(*) cnt from cmr0031 a,cmd0304 b               \n");
            	strQuery.append(" where a.cr_docid=?                                        \n");
            	strQuery.append("   and a.cr_prjno=b.cd_prjno                               \n");
            	strQuery.append("   and b.cd_edityn='Y' and b.cd_prjuser=?                  \n");
            	pstmt2 = conn.prepareStatement(strQuery.toString());

                //pstmt =  new LoggableStatement(conn, strQuery.toString());
                pstmt2.setString(1, rs.getString("cr_docid"));
                pstmt2.setString(2, UserId);
               	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                	if (rs2.getInt("cnt") > 0) rst.put("edityn", "Y");
                	else  rst.put("edityn", "N");
                }
                rs2.close();
                pstmt2.close();

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
			ecamsLogger.error("## Cmd1500.getDocList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getDocList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDocList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getDocList() Exception END ##");
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
					ex3.printStackTrace();
				}
			}
		}

	}//end of getDocList() method statement
    public Object[] getEditor(String PrjNo) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
	        strQuery.append("select b.cm_userid,b.cm_username                           \n");
	        strQuery.append("  from cmd0304 a,cmm0040 b                                 \n");
	        strQuery.append(" where a.cd_prjno=? and a.cd_closedt is null               \n");
	        strQuery.append("  and a.cd_prjuser=b.cm_userid and b.cm_active='1'         \n");
	        pstmt = conn.prepareStatement(strQuery.toString());

            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, PrjNo);
           	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
            while (rs.next()){
            	if (rs.getRow() == 1) {
            		rst = new HashMap<String, String>();
                	rst.put("cm_userid", "00000000");
                	rst.put("cm_username", "선택하세요.");
                	rsval.add(rst);
                	rst = null;
            	}
            	rst = new HashMap<String, String>();
            	rst.put("cm_userid", rs.getString("cm_userid"));
            	rst.put("cm_username", rs.getString("cm_username"));
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
			ecamsLogger.error("## Cmd1500.getEditor() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getEditor() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getEditor() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getEditor() Exception END ##");
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
					ex3.printStackTrace();
				}
			}
		}
	}//end of getEditor() method statement

    public int cmr0030Updt(String DocId,String Editor,String ccbYn,String docSta,String UserId) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int parmCnt = 0;
		try {
			conn = connectionContext.getConnection();

			if (ccbYn.equals("true")) ccbYn = "Y";
			else ccbYn = "N";

            strQuery.setLength(0);
            strQuery.append("UPDATE CMR0030 SET                 \n");
           	strQuery.append("         CR_CCBYN=?,               \n");
            strQuery.append("         CR_LASTDT=SYSDATE         \n");
            if (Editor != null && Editor != "" && Editor.length()>0)
            	strQuery.append("         ,CR_EDITOR=?          \n");//UserId
            if (docSta != null && docSta != "" && docSta.length()>0) {
            	strQuery.append(",CR_DOCSTA=?,CR_STAUSER=?,CR_STADATE=SYSDATE \n");
            }
            strQuery.append(" WHERE   CR_DOCID=?                \n");//docid
        	pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());

		    pstmt.setString(++parmCnt, ccbYn);
		    if (Editor != null && Editor != "" && Editor.length()>0)
		    	pstmt.setString(++parmCnt, Editor);
		    if (docSta != null && docSta != "" && docSta.length()>0) {
		    	pstmt.setString(++parmCnt, docSta.replace("0", ""));
		    	pstmt.setString(++parmCnt, UserId);
		    }
		    pstmt.setString(++parmCnt, DocId);
		    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();

        	if (Editor != null && Editor != "" && Editor.length()>0) {
	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMR1100 SET                \n");
	            strQuery.append("       CR_EDITOR=?                \n");//UserId
	            strQuery.append(" WHERE CR_DOCID=?                 \n");//docid
	            strQuery.append("   AND substr(CR_acptno,5,2)='31' \n");//docid
	            strQuery.append("   AND cr_confno is null          \n");//docid
	        	pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, Editor);
			    pstmt.setString(2, DocId);
	        	pstmt.executeUpdate();
	        	pstmt.close();
        	}

        	conn.commit();
        	conn.close();

        	pstmt = null;
        	conn = null;

        	return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.cmr0030Updt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.cmr0030Updt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.cmr0030Updt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.cmr0030Updt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0030Updt() method statement
    public int cmr0030Dlet(String DocId) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

            strQuery.setLength(0);
            strQuery.append("delete CMR1100 where cr_docid=?    \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, DocId);
        	pstmt.executeUpdate();
        	pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete CMR0034 where cr_docid=?    \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, DocId);
        	pstmt.executeUpdate();
        	pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete CMR0033 where cr_docid=?    \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, DocId);
        	pstmt.executeUpdate();
        	pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete CMR0032 where cr_docid=?    \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, DocId);
        	pstmt.executeUpdate();
        	pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete CMR0031 where cr_docid=?    \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, DocId);
        	pstmt.executeUpdate();
        	pstmt.close();

            strQuery.setLength(0);
            strQuery.append("delete CMR0030 where cr_docid=?    \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
		    pstmt.setString(1, DocId);
        	pstmt.executeUpdate();
        	pstmt.close();
        	conn.commit();
        	conn.close();

        	pstmt = null;
        	conn = null;

        	return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.cmr0030Dlet() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.cmr0030Dlet() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.cmr0030Dlet() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.cmr0030Dlet() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0030Dlet() method statement
	public Object[] getFileList_1600(String userid, String syscd, String baseSvr,String svrcd, String svrseq, String rsrcname, String DsnCd, String strdate, String rsrccd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>> svrBaseHome = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> svrChgHome = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			   rst	   = null;

		ConnectionContext connectionContext = new ConnectionResource();
		
		int    paramcnt = 0;
		String szDsnCD = "";
		int i = 0;
		int j = 0;
		
		try {
			conn = connectionContext.getConnection();
			
			if (!svrcd.equals(baseSvr)) {
				SysInfo sysinfo = new SysInfo();
				svrBaseHome = sysinfo.getSvrHomePath_conn(userid,syscd,baseSvr,"", conn);
				svrChgHome = sysinfo.getSvrHomePath_conn(userid,syscd,svrcd,svrseq,conn);
			}
			if (!DsnCd.equals("")){
				if (DsnCd.length() == 7 && DsnCd.indexOf("/") < 0){
					szDsnCD = DsnCd;
				}
			}
			strQuery.setLength(0);
			strQuery.append(" select b.cm_dirpath,a.cr_rsrcname,c.cm_codename rsrccd,d.cm_codename jawon,       \n");
			strQuery.append("        a.cr_lstver,to_char(a.cr_lstdat,'yyyy/mm/dd') outdate,e.cm_username,       \n");
			strQuery.append("        a.cr_editor, a.cr_status, a.cr_itemid, f.cm_info, a.cr_rsrccd, a.cr_jobcd, \n");
			strQuery.append("        a.cr_syscd, a.cr_dsncd, a.cr_md5sum,a.cr_ckoutuser                         \n");
			strQuery.append("   from cmm0036 f,cmm0040 e,cmm0020 d,cmm0020 c,cmm0070 b,cmr0020 a                \n");
			if(strdate != null && strdate != "") {
				strQuery.append(" ,(select distinct cr_itemid from cmr0021                                      \n");
				strQuery.append("    where to_char(cr_acptdate,'yyyymmdd')>=?) g                                \n");
			}
			strQuery.append("  where a.cr_syscd=?                                                               \n");
			strQuery.append("    and a.cr_status not in ('3','9')                                               \n");
			if(rsrcname != null && rsrcname != ""){
				strQuery.append("    and upper(a.cr_rsrcname) like upper(?)                                     \n");
			}
			if(strdate != null && strdate != "") {
				strQuery.append("    and a.cr_itemid=g.cr_itemid                                                \n");
			}
			if (!DsnCd.equals("")){
				if (!DsnCd.substring(0, 1).equals("F")){
					strQuery.append(" and a.cr_dsncd= ?                                                         \n");
				}
				else{
					strQuery.append(" and b.cm_dirpath like ?                                                   \n");
				}
			}
			if (!rsrccd.equals("")){
				strQuery.append(" and a.cr_rsrccd=?                                                             \n");
			}
			strQuery.append("    and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd                            \n");
			strQuery.append("    and d.cm_macode='CMR0020' and d.cm_micode=a.cr_status                          \n");
			strQuery.append("    and a.cr_lstver> 0                                                              \n");
			strQuery.append("    and a.cr_lstver> 0                                                              \n");
			strQuery.append("    and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd                            \n");
			strQuery.append("    and a.cr_editor=e.cm_userid                                                    \n");
			strQuery.append("    and f.cm_syscd=?                                                               \n");
			strQuery.append("    and a.cr_rsrccd=f.cm_rsrccd                                                    \n");
			strQuery.append("    and substr(f.cm_info,27,1)='0'                                                 \n");
			strQuery.append("    and substr(f.cm_info,12,1)='1'                                                 \n");
			strQuery.append("    and f.cm_rsrccd not in (select cm_samersrc from cmm0037                        \n");
			strQuery.append("                             where cm_syscd=?                                      \n");
			strQuery.append("                               and cm_factcd in ('04','09'))                       \n");
	        
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			if(strdate != null && strdate != "") pstmt.setString(++paramcnt, strdate);

			pstmt.setString(++paramcnt, syscd);
	        if(rsrcname != null && rsrcname != "") pstmt.setString(++paramcnt, "%"+rsrcname+"%");
	        if (!DsnCd.equals("")){
				if (!DsnCd.substring(0, 1).equals("F")){
					pstmt.setString(++paramcnt, szDsnCD);
				}
				else{
					pstmt.setString(++paramcnt, DsnCd.substring(1)+ "%");
				}
			}
	        if (!rsrccd.equals("")){
	        	pstmt.setString(++paramcnt, rsrccd);
			}
	        pstmt.setString(++paramcnt, syscd);
	        pstmt.setString(++paramcnt, syscd);
		    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();

            while (rs.next()){
            	rst = new HashMap<String,String>();
    			strQuery.setLength(0);

            	rst.put("view_dirpath", rs.getString("cm_dirpath"));

				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("codename", rs.getString("jawon"));
				rst.put("jawon", rs.getString("rsrccd"));
				rst.put("cr_lstver", rs.getString("cr_lstver"));
				rst.put("lastdt", rs.getString("outdate"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cr_editor", rs.getString("cr_editor"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("cr_md5sum", rs.getString("cr_md5sum"));
				rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
				rst.put("baseitemid",rs.getString("cr_itemid"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cr_ckoutuser",rs.getString("cr_ckoutuser"));
				rst.put("downflg", "1");
				rst.put("enabled", "1");
				if (!svrcd.equals(baseSvr)) {
					for (i=0;svrBaseHome.size()>i;i++) {
						if (svrBaseHome.get(i).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
							for (j=0;svrChgHome.size()>j;j++) {
								if (svrChgHome.get(j).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
									rst.put("view_dirpath", rs.getString("cm_dirpath").replace(svrBaseHome.get(i).get("cm_volpath"), svrChgHome.get(j).get("cm_samedir")));
									break;
								}
							}
							break;
						}
					}
				}
				rtList.add(rst);
				
				rst = null;
				
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			return rtList.toArray();
						
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1500.getFileList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getFileList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1500.getFileList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtList != null)	rtList = null;
			if (svrBaseHome != null) svrBaseHome = null;
			if (svrChgHome != null)	svrChgHome = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement
	public Object[] getFileList(String userid, String syscd, String svrcd, String svrseq, String rsrcname, String DsnCd, String strdate, String rsrccd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>> svrList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> rtList  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			   rst	   = null;

		ConnectionContext connectionContext = new ConnectionResource();
		
		int    paramcnt = 0;
		String szDsnCD = "";
		String strDevHome = "";
		String strVolPath = "";
		String strDirPath = "";
//		String strChgPath = "";
		
        Cmr0250 cmr0250 = new Cmr0250();
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cd_devhome from cmd0300               \n");
			strQuery.append(" where cd_syscd=? and cd_userid=?            \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, syscd);
			pstmt.setString(2, userid);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				strDevHome = rs.getString("cd_devhome") + "\\";
			}
			rs.close();
			pstmt.close();

			if(strDevHome != ""){
				SysInfo sysinfo = new SysInfo();
				svrList = sysinfo.getHomePath_conn(syscd, conn);
				
				if (!DsnCd.equals("")){
					if (DsnCd.length() == 7 && DsnCd.indexOf("/") < 0){
						szDsnCD = DsnCd;
					}
				}
				
				strQuery.setLength(0);
				strQuery.append(" select b.cm_dirpath,a.cr_rsrcname,c.cm_codename rsrccd,d.cm_codename sta,         \n");
				strQuery.append("        a.cr_lstver,to_char(a.cr_lstdat,'yyyy/mm/dd') outdate,e.cm_username,       \n");
				strQuery.append("        a.cr_editor, a.cr_status, a.cr_itemid, f.cm_info, a.cr_rsrccd, a.cr_jobcd  \n");
				strQuery.append("   from cmm0036 f,cmm0040 e,cmm0020 d,cmm0020 c,cmm0070 b,cmr0020 a                \n");
				if(strdate != null && strdate != "") {
					strQuery.append(" ,(select distinct cr_itemid from cmr0021                                      \n");
					strQuery.append("    where to_char(cr_acptdate,'yyyymmdd')>=?) g                                \n");
				}
				strQuery.append("  where a.cr_syscd=?                                                               \n");
				strQuery.append("    and a.cr_status not in ('3','9')                                               \n");
				if(rsrcname != null && rsrcname != ""){
					strQuery.append("    and upper(a.cr_rsrcname) like upper(?)                                     \n");
				}
				if(strdate != null && strdate != "") {
					strQuery.append("    and a.cr_itemid=g.cr_itemid                                                \n");
				}
				if (!DsnCd.equals("")){
					if (!DsnCd.substring(0, 1).equals("F")){
						strQuery.append(" and a.cr_dsncd= ?                                                         \n");
					}
					else{
						strQuery.append(" and b.cm_dirpath like ?                                                   \n");
					}
				}
				if (!rsrccd.equals("")){
					strQuery.append(" and a.cr_rsrccd=?                                                             \n");
				}
				strQuery.append("    and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd                            \n");
				strQuery.append("    and d.cm_macode='CMR0020' and d.cm_micode=a.cr_status                          \n");
				strQuery.append("    and a.cr_lstver>0                                                              \n");
				strQuery.append("    and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd                            \n");
				strQuery.append("    and a.cr_editor=e.cm_userid                                                    \n");
				strQuery.append("    and f.cm_syscd=?                                                               \n");
				strQuery.append("    and a.cr_rsrccd=f.cm_rsrccd                                                    \n");
				strQuery.append("    and ( substr(f.cm_info,38,1)='1'                                               \n"); //서버/로컬동시 체크아웃이거나
				strQuery.append("    	   or substr(f.cm_info,45,1)='1' )                                          \n"); //로컬에서개발에 체크되어있는것만 조회				
				strQuery.append("    and f.cm_rsrccd not in (select cm_samersrc from cmm0037                        \n");
				strQuery.append("                             where cm_syscd=?                                      \n");
				strQuery.append("                               and cm_factcd in ('04','09'))                       \n");
		        
				pstmt = conn.prepareStatement(strQuery.toString());
		        //pstmt =  new LoggableStatement(conn, strQuery.toString());
				if(strdate != null && strdate != "") pstmt.setString(++paramcnt, strdate);
		        pstmt.setString(++paramcnt, syscd);
		        if(rsrcname != null && rsrcname != "") pstmt.setString(++paramcnt, "%"+rsrcname+"%");
		        if (!DsnCd.equals("")){
					if (!DsnCd.substring(0, 1).equals("F")){
						pstmt.setString(++paramcnt, szDsnCD);
					}
					else{
						pstmt.setString(++paramcnt, DsnCd.substring(1)+ "%");
					}
				}
		        if (!rsrccd.equals("")){
		        	pstmt.setString(++paramcnt, rsrccd);
				}
		        pstmt.setString(++paramcnt, syscd);
		        pstmt.setString(++paramcnt, syscd);
		        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            rtList.clear();
	            
				while (rs.next()){
					rst = new HashMap<String,String>();
					String chgdir = cmr0250.chgVolPath(syscd,rs.getString("cm_dirpath"),svrcd,rs.getString("cr_rsrccd"),Integer.parseInt(svrseq),rs.getString("cr_jobcd"),conn);
					
					rst.put("dirpath", chgdir);
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("rsrccd", rs.getString("rsrccd"));
					rst.put("sta", rs.getString("sta"));
					rst.put("cr_lstver", rs.getString("cr_lstver"));
					rst.put("outdate", rs.getString("outdate"));
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("cr_editor", rs.getString("cr_editor"));
					rst.put("cr_status", rs.getString("cr_status"));
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					
					for (int i=0;svrList.size()>i;i++) {
						if (svrList.get(i).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
							strVolPath = svrList.get(i).get("cm_volpath");
							strDirPath = rs.getString("cm_dirpath");	
							ecamsLogger.error("++++ dirpath,volpath++++"+strVolPath+", "+strDirPath);						
							if (strVolPath != null && strVolPath != "") {
								if(strDirPath.length() == strVolPath.length()){
									rst.put("viewpcdir", strDevHome.substring(0,strDevHome.length()-1));
								} else if(strDirPath.length() >= strVolPath.length()){
									if (strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
										rst.put("viewpcdir", strDevHome + strDirPath.substring(strVolPath.length()+1).replace("/","\\"));
									}
								} else {
									
								}
							} else {
								rst.put("viewpcdir", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
							}
						}
					}
					if(rst.get("viewpcdir") != null)
					rst.put("pcdir", rst.get("viewpcdir").replace("\\", "\\\\"));
					rst.put("strDevHome", strDevHome);
					
					if(userid.equals(rs.getString("cr_editor")) && !rs.getString("cr_status").equals("0")){
						rst.put("downflg", "0");
						rst.put("enabled", "0");
					}else{
						rst.put("downflg", "1");
						rst.put("enabled", "1");
					}
					rtList.add(rst);
					rst = null;
					
				}//end of while-loop statement
				
				rs.close();
				pstmt.close();
			}else{
				rst = new HashMap<String,String>();
				rst.put("strDevHome", "로컬 홈디렉토리를 지정하지 않았습니다. 기본관리>사용자환경설정에서 홈디렉토리지정 후 처리하시기 바랍니다.");
				rtList.add(rst);
				rst = null;
			}
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return rtList.toArray();
						
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1500.getFileList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getFileList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1500.getFileList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement
	
	public String getDownFile(String ItemId, String UserId, String Version, String preFileName, boolean isFinish) throws Exception {
		
		String			  tmpPath     		= "";
		String  shFileName = "";
		String	fileName = "";
		String  strParm  = "";
		int     ret      = 0;

		try {
			
			if( (ItemId == null || ItemId.equals("")) ) {
				return "ERROR:프로그램번호 미입력";
			}
			if( (UserId == null || UserId.equals("")) ) {
				return "ERROR:사용자번호 미입력";
			}
			if( (Version == null || Version.equals("")) ) {
				return "ERROR:버전 미입력";
			}
			
			SystemPath		  cTempGet	  		= new SystemPath();
			tmpPath = cTempGet.getTmpDir("99");
			cTempGet = null;
			
			Cmr0200 cmr0200 = new Cmr0200();
			shFileName = ItemId +"_genLocalSrc.sh";
			fileName = tmpPath + "/" + ItemId + "." + UserId + "." + Version;
			strParm = "./FileRead_cmr0025 "+ ItemId + " " + fileName + " " + Version;
			ret = cmr0200.execShell(shFileName, strParm, false);
			if (ret != 0) {
				throw new Exception("해당 소스 생성  실패. run=["+strParm +"]" + " return=[" + ret + "]" );
			}
			cmr0200 = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDownFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getDownFile() SQLException END ##");
			throw sqlexception;
		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	   		throw exception;
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDownFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getDownFile() Exception END ##");
			throw exception;
		}finally{
		}
	}//end of while-loop statement

	
	/** 형상DB CMR0025 테이블에서 프로그램 추출
	 * @param ItemId
	 * @param UserId
	 * @param Version
	 * @param preFileName
	 * @param isFinish
	 * @param tmpDir
	 * @author neo.
	 * @return
	 * @throws Exception
	 */
	public String getDownFile(String ItemId, String UserId, String Version, String preFileName, boolean isFinish,String tmpDir) throws Exception {
//		String  shFileName = "";
//		String	fileName = "";
//		String  strParm  = "";
//		int     ret      = 0;
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			if( (ItemId == null || ItemId.equals("")) ) {
				return "ERROR:프로그램번호 미입력";
			}
			if( (UserId == null || UserId.equals("")) ) {
				return "ERROR:사용자번호 미입력";
			}
			if( (Version == null || Version.equals("")) ) {
				return "ERROR:버전 미입력";
			}
			
		    File binaryFile = new File(tmpDir + "/" + ItemId + "." + UserId + "." + Version);
		    FileOutputStream outStream = new FileOutputStream(binaryFile);
//			Blob myBlob = null;
			
			conn = connectionContext.getConnection();
			strQuery.append("SELECT cr_file \n");
			strQuery.append("FROM cmr0025 \n");
			strQuery.append("WHERE cr_itemid = ? \n");
			strQuery.append("AND cr_ver = ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = (PreparedStatement) conn.createStatement();
			pstmt.setString(1, ItemId);
			pstmt.setString(2, Version);
			rs = pstmt.executeQuery();
			if (rs.next()){
//				myBlob = rs.getBlob("cr_file");
				oracle.sql.BLOB blob = ((OracleResultSet)rs).getBLOB(1);
				ecamsLogger.error("blob.length() = " + blob.length());
				ecamsLogger.error("blob.getBytes().length = " + blob.getBytes().length);
				ecamsLogger.error("decop.length = " + getDecompressedByte(blob.getBytes()).length);
				byte[] bytesBlob = blob.getBytes(48, (int) blob.length() ) ;
//				ecamsLogger.error(new String( bytesBlob ));
				
//				byte[] decomp = getDecompressedByte(blob.getBytes(48, (int) blob.length()));
//				ecamsLogger.error(decomp.toString());
//				byte[] buffer = new byte[1024];
//				
//				BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decomp)));
//				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version));
//		    	
//		    	String s;
//		    	ecamsLogger.error("TEST55555555555555\n");
//		    	while(null != (s = in.readLine())) {
//		    		out.write(s.getBytes());
//		    		out.write("\n".getBytes());
//		    	}
//		    	ecamsLogger.error("TEST66666666666666666\n");
//		    	in.close();
//		    	out.close();
				
				//outStream.write(decomp);
				
//				BufferedInputStream bis = new BufferedInputStream(blob.getBinaryStream());
//				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version + ".gz"),4096);
////				int nFileSize = (int)blob.length();
////				byte[] buf = new byte[nFileSize];
////				int nReadSize = in.read(buf, 0, nFileSize);
//				
//				
//				ecamsLogger.error("TEST1111111111\n");
//				// Transfer the data
//			    //while ((nReadSize = in.read(buf)) != -1)
//				int readBlob = bis.read();
//				while ( readBlob != -1 )
//			    {
////			    	bos.write(buf, 0, nReadSize);
////			    	bos.flush();
//			    	bos.write(readBlob);
//			    	readBlob = bis.read();
//			    }
			    ecamsLogger.error("TEST2222222222\n");
			    
			    byte[] buffer = new byte[1024];
			    try{
			    	
			    	String gzipname = tmpDir + "/" + ItemId + "." + UserId + "." + Version + ".gz"; //tmpDir + "/" + "000000002614_gensrc.file.gz";//
			    	
			    	GZIPInputStream gzipInputStream = null;
					ByteArrayInputStream bis = null;
					ByteArrayOutputStream bos = null;
					int len=0;
					byte[] rtbyte = null;
					byte[] buf = new byte[1024];
					ecamsLogger.error("TEST333333333333\n");
					
					bis = new ByteArrayInputStream( bytesBlob );
					
					ecamsLogger.error("[4]\n");
					
					gzipInputStream = new GZIPInputStream(bis);
					bos = new ByteArrayOutputStream();
					
					while ((len = gzipInputStream.read(buf)) > 0) {
						bos.write(buf, 0, len);
					}
					
					gzipInputStream.close();
					bis.close();
					
					rtbyte = bos.toByteArray();
					
					bos.close();
						
//			    	BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gzipname))));
			    	ecamsLogger.error("TEST444444444444\n");
			    	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version));
			    	
			    	String s;
			    	ecamsLogger.error("TEST55555555555555\n");
//			    	while(null != (s = in.readLine())) {
//			    		out.write(s.getBytes());
//			    		out.write("\n".getBytes());
//			    	}
//			    	ecamsLogger.error("TEST66666666666666666\n");
//			    	in.close();
			    	out.close();
//			    	
//			    	
//			    	
////			    	ecamsLogger.error("TEST333333333333\n");
////			    	BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version + ".gz"))));
////			    	ecamsLogger.error("TEST444444444444\n");
////			    	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version));
////			    	
////			    	String s;
////			    	ecamsLogger.error("TEST55555555555555\n");
////			    	while(null != (s = in.readLine())) {
////			    		out.write(s.getBytes());
////			    		out.write("\n".getBytes());
////			    	}
////			    	ecamsLogger.error("TEST66666666666666666\n");
////			    	in.close();
////			    	out.close();
//			    	
////			    	ecamsLogger.error("TEST333333333333\n");
////			    	FileInputStream a = new FileInputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version + ".gz");
////			    	ecamsLogger.error("TEST3908908\n");
////			    	GZIPInputStream gzis = new GZIPInputStream(a);
////			    	ecamsLogger.error("TEST39899\n");
////			    	
//////			    	GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version + ".gz"));
////			    	ecamsLogger.error("TEST444444444444444444444\n");
////			    	FileOutputStream out = new FileOutputStream(tmpDir + "/" + ItemId + "." + UserId + "." + Version);
////			    	ecamsLogger.error("TEST55555555555555\n");
////			    	int len;
////			    	
////			    	while ((len = gzis.read(buffer)) != -1) {
////			    		out.write(buffer, 0, len);
////			    	}
////			    	ecamsLogger.error("TEST66666666666666666\n");
////			    	gzis.close();
////			    	out.close();
			    }catch (Exception e) {
			    	e.printStackTrace();
			    }
			    

			    
//			    bis.close();
//			    bos.close();
			    outStream.close();
//			    bis = null;
//			    bos = null;
//			    buf = null;
			    
			}
//		    InputStream inStream = myBlob.getBinaryStream();
//
//		    // Get the optimum buffer size and use this to create the read/write buffer
//		    int size = ((BLOB) myBlob).getBufferSize();
//		    byte[] buffer = new byte[size];
//		    int length = -1;
//
//		    // Transfer the data
//		    while ((length = inStream.read(buffer)) != -1)
//		    {
//		      outStream.write(buffer, 0, length);
//		      outStream.flush();
//		    }
//		    // Close everything down
//		    inStream.close();
//		    outStream.close();
		    
//		    myBlob = null;
//		    buffer = null;
//		    inStream = null;
		    outStream = null;
		    binaryFile = null;
		    
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
//			Cmr0200 cmr0200 = new Cmr0200();
//			shFileName = ItemId +"_genLocalSrc.sh";
//			fileName = tmpDir + "/" + ItemId + "." + UserId + "." + Version;
//			strParm = "./FileRead_cmr0025 "+ ItemId + " " + fileName + " " + Version;
//			ret = cmr0200.execShell(shFileName, strParm, false);
//			if (ret != 0) {
//				throw new Exception("해당 소스 생성  실패. run=["+strParm +"]" + " return=[" + ret + "]" );
//			}
//			cmr0200 = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDownFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getDownFile() SQLException END ##");
			throw sqlexception;
		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	   		throw exception;
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDownFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getDownFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1500.getDownFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end getDownFile() 
	
	public byte[] getDecompressedByte(byte[] data){
		
		GZIPInputStream gzipInputStream = null;
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		int len=0;
		byte[] rtbyte = null;
		byte[] buf = new byte[1024];
		
		try {
			bis = new ByteArrayInputStream(data);
			gzipInputStream = new GZIPInputStream(bis);
			bos = new ByteArrayOutputStream();
			
			while ((len = gzipInputStream.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			
			gzipInputStream.close();
			bis.close();
			
			rtbyte = bos.toByteArray();
			
			bos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtbyte = null;
		}
		finally{
			try {
				gzipInputStream.close();
				bis.close();
				bos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				rtbyte = null;
			}			
		}
		
		return rtbyte;
	}

	
	/**
	 * @param ItemId
	 * @param UserId
	 * @param Version
	 * @param preFileName
	 * @param isFinish 
	 * @param tmpDir   : 임시경로
	 * @param fileList : 대상파일리스트
	 * @return
	 * @throws Exception
	 */
	public String getDownFiles(String UserId, String tmpDir,ArrayList<HashMap<String,String>> fileList) throws Exception {
		
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		File shellFile = null;
		OutputStreamWriter writer = null;
		
		String  shellFileName = "";//작업용 쉘 파일명
		String	outPutFileName = "";//프로그램 출력파일명
		String	strBinPath = "";//bin 디렉토리 경로

		try {
			
			shellFileName = UserId +"_createLocalSrc.sh";
			int i = 0;
			shellFile = new File(tmpDir+"/"+shellFileName);
			if( !(shellFile.isFile()) )//File존재여부
				shellFile.createNewFile();//File생성
			strBinPath = new SystemPath().getTmpDir("14");
			
			//쉘파일 내용 작성
			writer = new OutputStreamWriter( new FileOutputStream(shellFile));
			writer.write("cd "+strBinPath +"\n");
			for ( i = 0 ; i < fileList.size() ; i++ ) {
				outPutFileName = tmpDir + "/" + fileList.get(i).get("cr_itemid") + "." + UserId + "." + fileList.get(i).get("cr_lstver");
				writer.write("./FileRead_cmr0025 "+ fileList.get(i).get("cr_itemid") + " " + outPutFileName + " " + fileList.get(i).get("cr_lstver")+"\n");
			}
			writer.write("exit 0\n");
			writer.close();
			writer = null;
			
			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = tmpDir+"/"+shellFileName;
			run = Runtime.getRuntime();
			p = run.exec(strAry);
			p.waitFor();
			
			strAry = new String[3];
			strAry[0] = "/bin/sh";
			strAry[1] = tmpDir+"/"+shellFileName;
			strAry[2] = "&";
			run = Runtime.getRuntime();
			p = run.exec(strAry);
			p.waitFor();
			
//			if (p.exitValue() == 0) {
////				shellFile.delete();
//			} else {
//				throw new Exception("해당 소스 생성  실패. run=["+tmpDir+"/"+shellFileName +"]" + " return=[" + p.exitValue() + "]" );
//			}
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDownFiles() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1500.getDownFiles() SQLException END ##");
			throw sqlexception;
		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	   		throw exception;
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getDownFiles() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getDownFiles() Exception END ##");
			throw exception;
		}finally{
			if ( p != null ) p = null;
			if ( run != null ) run = null;
			if ( strAry != null ) strAry = null;
			if ( shellFile != null ) shellFile = null;
		}
	}//end of while-loop statement
	
	public Boolean setTranResult_dummy() throws Exception {
		try {
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.setTranResult_dummy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.setTranResult_dummy() Exception END ##");
			throw exception;
		}finally{
		}
		return true;
	}//end of setTranResult_dummy() method statement
	
	
	public Object[] getFileInf(String UserID,String SysCd,String baseSvr,HashMap<String, String> etcData,ArrayList<HashMap<String, String>> diffList) throws Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;

		HashMap<String, String>			  rst		  = null;
		String[]          pathDepth   = null;
		String            strMd5sum   = null;
		String            strFile     = null;
		String            errMsg      = "";
		boolean           ErrSw       = false;
		String            strBinPath  = "";
		String            strTmpPath  = "";
		String            strItemId   = "";
		int               maxSeq      = 0;
		File difffile=null;
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		String diffFileName = "";
		int i = 0;
		int               ret = 0;
		String       shFileName = "";
		String       strParm = "";
        String SvrIp = etcData.get("cm_svrip");
        String SvrPort = etcData.get("cm_portno");
        String AgentDir = etcData.get("cm_dir");
        String SvrBuffSize = etcData.get("cm_buffsize");
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
				diffFileName = "difffile" + UserID + ".file";
				difffile=null;
				difffile = new File(strTmpPath + diffFileName);              //File 불러온다.
				if( !(difffile.isFile()) )              //File이 없으면
				{
					difffile.createNewFile();          //File 생성
				}
		        
				writer = new OutputStreamWriter( new FileOutputStream(strTmpPath + diffFileName));
				for (i=0;diffList.size()>i;i++) {
					writer.write(diffList.get(i).get("view_dirpath") +"/" + diffList.get(i).get("cr_rsrcname") +";" + 
							diffList.get(i).get("cr_itemid")  + "\n" );
				}
				writer.close();
				strAry = new String[3];
				strAry[0] = "chmod";
				strAry[1] = "777";
				strAry[2] = strTmpPath + diffFileName;
				run = Runtime.getRuntime();
				p = run.exec(strAry);
				p.waitFor();
 
				Cmr0200 cmr0200 = new Cmr0200();
				shFileName = "fileinf"+ UserID + ".sh";
				strFile = strTmpPath + "fileinf"+ SysCd+UserID;
				strParm = "./ecams_fileinf_snd " + SvrIp + " " + SvrPort + " " +  SvrBuffSize + " " + AgentDir + " F F "+ difffile + " " + strFile;
				ret = cmr0200.execShell(shFileName, strParm, false);

				if (ret != 0) {
				    ErrSw = true;	
					if (strAry != null) strAry = null;
					if (run != null) run = null;
					if (p != null) p = null;	
					throw new Exception("불일치건 조회를 위한 작업에 실패하였습니다");
				}
			} catch (Exception e) {
				if (strAry != null) strAry = null;
				if (run != null) run = null;
				if (p != null) p = null;
				throw new Exception(e);
			}

			//strFile = resultFile";
            if (ErrSw == false) {
				File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("파일추출을 위한 작업에 실패하였습니다 [작성된 파일 없음]");
		        } else {
			        BufferedReader in = null;
			        //PrintWriter out = null;

	        try {
	        	in = new BufferedReader(new FileReader(strFile)); //mFile
	        	
	            String str = null;
	            //strBaseDir = BaseDir;
	            while ((str = in.readLine()) != null) {	            	
	                if (str.length() > 0) {
	                	pathDepth = str.split(";"); 
	                	errMsg = pathDepth[2];
	                	strMd5sum = pathDepth[5];
	                	strItemId = pathDepth[1]; // 2
	                	
	                	for (i=0;diffList.size()>i;i++) {
	                		if (diffList.get(i).get("cr_itemid").equals(strItemId)) {
	                			rst = new HashMap<String,String>();
	                			rst = diffList.get(i);
	                			if (!errMsg.equals("OK")) {
	                				rst.put("diffyn","N");
	                				if (errMsg.equals("FNER")) {
	                					rst.put("filecomp","파일없음");
	                				} else if (errMsg.equals("FAER")) {
	                					rst.put("filecomp","권한없음");	                					
	                				}
	                			} else {
	                				ecamsLogger.error("+++ cmr0020.md5,svrmd5 +++++"+diffList.get(i).get("cr_md5sum")+", "+strMd5sum);
		                			if (!diffList.get(i).get("cr_md5sum").equals(strMd5sum)) {
		                				rst.put("filecomp","불일치");
		                				rst.put("diffyn","N");
		                				System.out.println("md5sum comp not equal");
		                			} else {
		                				rst.put("filecomp","일치");
		                				rst.put("diffyn","Y");
		                			}
		    					    rst.put("cr_md5sum", strMd5sum);
	     			
		                			diffList.set(i, rst);
		                			rst = null;
	                			}
	                			break;
	                		}
	                	}
                	
   	        			maxSeq = maxSeq + 1;
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
            return diffList.toArray();

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1500.getFileInf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1500.getFileInf() Exception END ##");
			throw exception;
		}finally{
			if (strAry != null) strAry = null;
			if (run != null) run = null;
			if (p != null) p = null;
			if (diffList != null)	diffList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getFileInf() method statement

	public String request_Check_Out(ArrayList<HashMap<String,String>> chkOutList,HashMap<String,String> etcData, ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
//		HashMap<String, String>			  rst		  = null;
		//CodeInfo		  codeInfo	  = new CodeInfo();
		String			  AcptNo	  = null;
		int				  i;
		int				  j;
		//HashMap<String,String>	rData = null;

		try {
			conn = connectionContext.getConnection();
			int totcnt = 0;

	        String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
	        userInfo = null;

	        Cmr0200 cmr0200 = new Cmr0200();
	        totcnt = chkOutList.size();
	        
	        int wkC = totcnt/300;
	        int wkD = totcnt%300;
	        if (wkD>0) wkC = wkC + 1;
            String svAcpt[] = null;
            svAcpt = new String [wkC];
            for (j=0;wkC>j;j++) {
            	do {
    		        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));

    		        i = 0;
    		        strQuery.setLength(0);
    		        strQuery.append("select count(*) as cnt from cmr1000 \n");
    	        	strQuery.append(" where cr_acptno= ?                 \n");

    	        	pstmt = conn.prepareStatement(strQuery.toString());
    	        	pstmt.setString(1, AcptNo);

    	        	rs = pstmt.executeQuery();

    	        	if (rs.next()){
    	        		i = rs.getInt("cnt");
    	        	}
    	        	rs.close();
    	        	pstmt.close();
    	        } while(i>0);
            	svAcpt[j] = AcptNo;
            }
        	int    seq = 0;
        	int    reqcnt = 0;
        	String retMsg = "";
            autoseq = null;
            conn.setAutoCommit(false);
        	boolean insSw = false;
        	for (i=0 ; i<chkOutList.size() ; i++){
        		insSw = false;

        		if (i == 0) insSw = true;
        		else {
        			wkC = reqcnt%300;
        			if (wkC == 0) insSw = true;
        		}

        		if (insSw == true) {
        			if (reqcnt>=300) {
        				retMsg = cmr0200.request_Confirm(AcptNo,chkOutList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
        				if (!retMsg.equals("OK")) {
        					conn.rollback();
        					conn.close();
        					throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
        				}
        			}
        			wkC = reqcnt/300;
        			AcptNo = svAcpt[wkC];

        			strQuery.setLength(0);
                	strQuery.append("insert into cmr1000 ");
                	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, \n");
                	strQuery.append("CR_PASSOK,CR_PASSCD,CR_EMGCD,CR_EDITOR,CR_ECLIPSE) values ( \n");
                	strQuery.append("?,?,?,?,sysdate,'0',?,?,  '0',?,'0',?,'R' )");
                	pstmt = conn.prepareStatement(strQuery.toString());
                	//pstmt = new LoggableStatement(conn, strQuery.toString());
                	pstmtcount = 1;
                	pstmt.setString(pstmtcount++, AcptNo);//CR_ACPTNO
                	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_syscd"));//CR_SYSCD
                	pstmt.setString(pstmtcount++, etcData.get("cm_sysgb"));//CR_SYSGB
                	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_jobcd"));//CR_JOBCD

                	pstmt.setString(pstmtcount++, strTeam);//CR_TEAMCD
                	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));//CR_QRYCD


                	pstmt.setString(pstmtcount++, etcData.get("Sayu"));//CR_PASSCD
                	pstmt.setString(pstmtcount++, etcData.get("UserID"));//CR_EDITOR

                	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                	pstmt.executeUpdate();

                	pstmt.close();
                	seq = 0;
        		}

        		strQuery.setLength(0);
            	strQuery.append("insert into cmr1010 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_STATUS,CR_QRYCD, ");
            	strQuery.append("CR_RSRCCD,CR_DSNCD,CR_RSRCNAME,CR_CHGCD,CR_TSTCHG,");
            	strQuery.append("CR_VERSION,CR_EDITOR,CR_BASEITEM,CR_ITEMID,CR_SVRYN,CR_VERYN) values ( ");
            	strQuery.append("?, ?, ?, ?, ?, '0', ?  , ?, ?, ?, '0', '0',   ?, ?, ?, ?, 'Y','Y')");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, ++seq);
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_syscd"));
            	pstmt.setString(pstmtcount++, etcData.get("cm_sysgb"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_jobcd"));
            	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
            	
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrccd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_dsncd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));

            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_lstver"));
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("baseitemid"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));

            	pstmt.executeUpdate();
            	pstmt.close();

        		++reqcnt;
        	}

        	retMsg = cmr0200.request_Confirm(AcptNo,chkOutList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
			if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
        	conn.commit();
        	conn.close();

        	rs = null;
    		pstmt = null;
    		conn = null;

        	return AcptNo;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0100.request_Check_Out() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.request_Check_Out() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0100.request_Check_Out() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.request_Check_Out() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}	
}//end of Cmd1500 class statement
