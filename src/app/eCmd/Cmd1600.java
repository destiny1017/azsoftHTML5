/*****************************************************************************************
	1. program ID	: eCmd1600.java
	2. create date	: 2008.09. 29
	3. auth		    : noname
	4. update date	:
	5. auth		    : [문서관리]->[신규등록]
	6. description	: eCmd1600
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.HashMap;
import org.apache.log4j.Logger;

import app.common.AutoSeq;
import app.common.CodeInfo;
import app.common.UserInfo;
//import app.common.LoggableStatement;
import app.eCmr.Cmr0200;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd1600{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] dirAllList(String PrjNo,String DocSeq,String HomePath) throws SQLException, Exception {
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
			int              paramIndex   = 0;
			String           strDocSeq    = DocSeq;
			String           strParent    = HomePath;
			String           strPath      = "";

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cd_docseq,cd_updocseq,cd_dirname              \n");
            strQuery.append("  from (select cd_docseq,cd_updocseq,cd_dirname      \n");
            strQuery.append("          from cmd0303 WHERE CD_PRJNO = ?)           \n");//PrjNo
        	strQuery.append(" START WITH CD_updocseq=?                            \n");//DocSeq
        	strQuery.append("CONNECT BY PRIOR CD_DOCSEQ=CD_UPDOCSEQ               \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			paramIndex = 0;
            pstmt.setString(++paramIndex, PrjNo);
            pstmt.setString(++paramIndex, DocSeq);
            rs = pstmt.executeQuery();

            rsval.clear();
            rst = new HashMap<String, String>();
    		rst.put("fullpath", HomePath);
    		rsval.add(rst);
            if (!HomePath.substring(HomePath.length()-1).equals("/")){
            	HomePath = HomePath + "/";
            }
            strParent = HomePath;
            while (rs.next()){
            	if (strDocSeq.equals(rs.getString("cd_updocseq"))) {
            		if (!strParent.substring(strParent.length()-1).equals("/")) strPath = strParent + "/";
            		else strPath = strParent;
            		strPath = strPath + rs.getString("cd_dirname");

                    strPath.replace("//", "/");

            		rst = new HashMap<String, String>();
            		rst.put("fullpath", strPath);
            		//ecamsLogger.debug("+++++++fullpath1+++"+strPath);
            		rsval.add(rst);
            	} else {
            		strParent = "";
            		strQuery.setLength(0);
        			strQuery.append("select cd_docseq,cd_dirname                          \n");
                    strQuery.append("  from (select cd_docseq,cd_updocseq,cd_dirname      \n");
                    strQuery.append("          from cmd0303 WHERE CD_PRJNO = ?)           \n");//PrjNo
                	strQuery.append(" START WITH CD_docseq=?                              \n");//DocSeq
                	strQuery.append("CONNECT BY PRIOR CD_upDOCSEQ=CD_DOCSEQ               \n");

        			pstmt2 = conn.prepareStatement(strQuery.toString());
        			//pstmt = new LoggableStatement(conn,strQuery.toString());
        			paramIndex = 0;
                    pstmt2.setString(++paramIndex, PrjNo);
                    pstmt2.setString(++paramIndex, rs.getString("cd_updocseq"));
                    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                    rs2 = pstmt2.executeQuery();

                    while (rs2.next()) {
                    	if (rs2.getString("cd_docseq").equals(DocSeq)) break;
                    	if (strParent != "" && strParent != null) strParent = "/" + strParent;
                    	strParent = rs2.getString("cd_dirname")+strParent;
                    }
                    rs2.close();
                    pstmt2.close();

                    strParent = HomePath + strParent;
                    if (!strParent.substring(strParent.length()-1).equals("/")){
                    	strPath = strParent + "/";
                    }else{
                    	strPath = strParent;
                    }
                    strPath = strPath + rs.getString("cd_dirname");
            		rst = new HashMap<String, String>();
            		rst.put("fullpath", strPath);
            		rsval.add(rst);
            		strDocSeq  = rs.getString("cd_updocseq");
            	}
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
			ecamsLogger.error("## Cmd1600.dirAllList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.dirAllList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.dirAllList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.dirAllList() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.dirAllList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of dirAllList() method statement

	public Object[] dirFullList(String PrjNo,String DocSeq,String HomePath,String selPath) throws SQLException, Exception {
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
			int              paramIndex   = 0;
			String           strDocSeq    = DocSeq;
			String           strPath      = "";
			String           strParent    = "";

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select cd_docseq,cd_updocseq,cd_dirname              \n");
            strQuery.append("  from (select cd_docseq,cd_updocseq,cd_dirname      \n");
            strQuery.append("          from cmd0303                               \n");
        	strQuery.append("         WHERE CD_PRJNO = ?)                         \n");//PrjNo
        	strQuery.append(" START WITH CD_updocseq=?                            \n");//DocSeq
        	strQuery.append("CONNECT BY PRIOR CD_DOCSEQ=CD_UPDOCSEQ               \n");
        	strQuery.append("UNION                                                \n");
        	strQuery.append("select cd_docseq,cd_updocseq,cd_dirname              \n");
        	strQuery.append("  from cmd0303                                       \n");
        	strQuery.append(" where cd_prjno=? and cd_docseq=?                    \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			paramIndex = 0;
            pstmt.setString(++paramIndex, PrjNo);
            pstmt.setString(++paramIndex, DocSeq);
            pstmt.setString(++paramIndex, PrjNo);
            pstmt.setString(++paramIndex, DocSeq);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
            while (rs.next()){
            	if (strDocSeq.equals(rs.getString("cd_docseq"))) {
            		if (rs.getString("cd_updocseq") != null) strPath = selPath;
            		else strPath = "";
            		strParent = selPath;
            	} else if (strDocSeq.equals(rs.getString("cd_updocseq"))) {
            		strPath = strParent + "/" + rs.getString("cd_dirname");
            	} else {
            		strParent = "";
            		strQuery.setLength(0);
        			strQuery.append("select cd_dirname                                    \n");
                    strQuery.append("  from (select cd_docseq,cd_updocseq,cd_dirname      \n");
                    strQuery.append("          from cmd0303                               \n");
                	strQuery.append("         WHERE CD_PRJNO = ?)                         \n");//PrjNo
                	strQuery.append(" START WITH CD_docseq=?                              \n");//DocSeq
                	strQuery.append("CONNECT BY PRIOR CD_upDOCSEQ=CD_DOCSEQ               \n");
                	pstmt2 = conn.prepareStatement(strQuery.toString());
        			//pstmt2 = new LoggableStatement(conn,strQuery.toString());
                	paramIndex = 0;
                    pstmt2.setString(++paramIndex, PrjNo);
                    pstmt2.setString(++paramIndex, rs.getString("cd_updocseq"));
                    //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
                    rs2 = pstmt2.executeQuery();
                    while (rs2.next()){
                    	if (strParent != null && strParent != "")
                    	   strParent = rs2.getString("cd_dirname")+"/"+strParent;
                    	else
                           strParent  = rs2.getString("cd_dirname");
                    	//ecamsLogger.debug("+++++++++dirname++++++++"+strParent+","+rs2.getString("cd_dirname"));
                    }
                    rs2.close();
                    pstmt2.close();
                    strParent = HomePath + "/" + strParent;
            		strDocSeq  = rs.getString("cd_updocseq");
            		strPath = strParent+"/"+rs.getString("cd_dirname");
            	}

            	if (strPath != "" && strPath != null) {
            		//ecamsLogger.error("strPath:"+strPath+"\n");
	            	rst = new HashMap<String, String>();
	        		rst.put("fullpath", strPath);
	        		rsval.add(rst);
            	}
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
			ecamsLogger.error("## Cmd1600.dirFullList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.dirFullList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.dirFullList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.dirFullList() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.dirFullList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of dirFullList() method statement

	public Object[] fileOpenChk(String PrjNo,String fullPath,String DocSeq,ArrayList<String> fileList,String strDevHome,String UserId)
		throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs          = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;
		int                 i = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			rsval.clear();
			if (fullPath != null && fullPath != ""){
				if (fullPath.substring(0,1).equals("/")){
					fullPath = fullPath.substring(1);
				}
			}
			String strWork[] = fullPath.split("/");
			String tmpUpdocseq = "";
			tmpUpdocseq = "00001";
			for (i=1 ; i<strWork.length ; i++){
				strQuery.setLength(0);
				strQuery.append("select cd_docseq from cmd0303 	\n");
	            strQuery.append(" where cd_prjno=?         		\n");
	            strQuery.append("   and cd_dirname=?            \n");
	        	strQuery.append("   and cd_updocseq=?           \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, PrjNo);
	            pstmt.setString(2, strWork[i]);
	            pstmt.setString(3, tmpUpdocseq);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	tmpUpdocseq = rs.getString("cd_docseq");
	            }
				rs.close();
				pstmt.close();
			}

			if (!DocSeq.equals(tmpUpdocseq)) DocSeq = tmpUpdocseq;
			else tmpUpdocseq = null;

			if (strWork.length>1) {
				for (i=0;fileList.size()>i;i++) {
					strQuery.setLength(0);
					strQuery.append("select b.cr_docid,b.cr_status,b.cr_editor,b.cr_lstver, \n");
					strQuery.append("       b.cr_defyn,b.cr_devstep,b.cr_ccbyn,c.cr_docseq  \n");
		            strQuery.append("  from cmr0030 b,cmr0031 c                             \n");
		            strQuery.append(" where c.cr_prjno=? and c.cr_docseq=?                  \n");
		            strQuery.append("   and c.cr_docid=b.cr_docid                           \n");
		        	strQuery.append("   and upper(b.cr_docfile)=upper(?)                    \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
		            pstmt.setString(1, PrjNo);
		            pstmt.setString(2, DocSeq);
		            pstmt.setString(3, fileList.get(i));
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
		            if (rs.next()) {
		            	if (rs.getInt("cr_lstver") == 0 && rs.getString("cr_status").equals("3") && rs.getString("cr_editor").equals(UserId)) {
			            	rst = new HashMap<String, String>();
			            	rst.put("cr_devstep", rs.getString("cr_devstep"));
			            	rst.put("cr_docseq", DocSeq);
			        		rst.put("devstep", strWork[1]);
			        		rst.put("docfile", fileList.get(i));
			        		rst.put("fullpath", strDevHome+"/"+fullPath);
			        		rst.put("checked",rs.getString("cr_ccbyn"));
			        		rsval.add(rst);
		            	}
		            } else {
		            	rst = new HashMap<String, String>();
		            	strQuery.setLength(0);
		            	strQuery.append("select cm_micode from cmm0020 where cm_macode='DEVSTEP'");
		            	strQuery.append("  and cm_closedt is null and cm_codename=? ");
		            	strQuery.append("  and cm_micode <> '****' ");
		            	pstmt2 = conn.prepareStatement(strQuery.toString());
		            	pstmt2.setString(1,strWork[1]);
		            	rs2 = pstmt2.executeQuery();
		            	if (rs2.next()){
		            		rst.put("cr_devstep", rs2.getString("cm_micode"));
		            	}else{
		            		throw new Exception("단계명이 등록되어 있지 않습니다.");
		            	}
		            	rs2.close();
		            	pstmt2.close();
		            	rst.put("cr_docseq", DocSeq);
		        		rst.put("devstep", strWork[1]);
		        		rst.put("docfile", fileList.get(i));
		        		rst.put("fullpath", strDevHome+"/"+fullPath);
		        		rst.put("checked", "N");
		        		rsval.add(rst);
		            }
		            rst = null;
		            rs.close();
		            pstmt.close();
				}
			}
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1600.fileOpenChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.fileOpenChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.fileOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.fileOpenChk() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex4){ex4.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.fileOpenChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of fileOpenChk() method statement

	public Object[] dirOpenChk(String PrjNo,String fullPath,String DocSeq,
			ArrayList<String> fileList)	throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2       = null;
		ResultSet         	rs2          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			int                 i = 0;
			int                 j = 0;
			String              strPath1 = "";
			//String              strPath2 = "";
			//String              svPath   = "";
			String              strSeq   = "";
			String              strUpSeq   = "";
			String              dirPath = "";
			conn = connectionContext.getConnection();

			rsval.clear();
			strQuery.setLength(0);
			strQuery.append("select cd_dirpath from cmd0303              \n");
			strQuery.append(" where cd_prjno=? and cd_docseq=?           \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, PrjNo);
            pstmt.setString(2, DocSeq);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	dirPath = rs.getString("cd_dirpath");
            }
            rs.close();
            pstmt.close();

			for (i=0;fileList.size()>i;i++) {
				strPath1 = fileList.get(i);
				strPath1 = strPath1.replace(fullPath, "");
				if (strPath1.substring(0,1).equals("/")) strPath1 = strPath1.substring(1);
				strUpSeq = DocSeq;
				if (strPath1 != "" && strPath1 != null) {
					String strWork[] = strPath1.split("/");

					for (j=0 ; strWork.length>j ; j++){
						dirPath = dirPath + "\\" + strWork[j];

						strQuery.setLength(0);
						strQuery.append("select cd_docseq from cmd0303              \n");
						strQuery.append(" where cd_prjno=? and cd_updocseq=?        \n");
						strQuery.append("   and cd_dirname=?                        \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
			            pstmt.setString(1, PrjNo);
			            pstmt.setString(2, strUpSeq);
			            pstmt.setString(3, strWork[j]);
			            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			            rs = pstmt.executeQuery();
			            if (rs.next()) {
			            	strUpSeq = rs.getString("cd_docseq");
			            } else {
			            	if (!strUpSeq.equals("00001")){
				            	strQuery.setLength(0);
				            	strQuery.append("select lpad(to_number(nvl(max(cd_docseq),0))+1,5,'0') seq \n");
				            	strQuery.append("  from cmd0303 where cd_prjno=?         \n");
				            	pstmt2 = conn.prepareStatement(strQuery.toString());
					            pstmt2.setString(1, PrjNo);
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) strSeq = rs2.getString("seq");
					            pstmt2.close();
					            rs2.close();

				            	strQuery.setLength(0);
				            	strQuery.append("insert into cmd0303 (CD_PRJNO,CD_DOCSEQ, \n");
				            	strQuery.append("   CD_DIRNAME,CD_UPDOCSEQ,CD_DIRPATH)    \n");
				            	strQuery.append("values   (?, ?, ?, ?, ?)         \n");
				            	pstmt2 = conn.prepareStatement(strQuery.toString());
				            	pstmt2.setString(1, PrjNo);
				            	pstmt2.setString(2, strSeq);
				            	pstmt2.setString(3, strWork[j]);
				            	pstmt2.setString(4, strUpSeq);
				            	pstmt2.setString(5, dirPath);
				            	pstmt2.executeUpdate();
				            	pstmt2.close();

					            rst = new HashMap<String, String>();
					            rst.put("docseq", strSeq);
					            rst.put("dirname", strWork[j]);
					            rst.put("updocseq", strUpSeq);
					            rsval.add(rst);
					            rst = null;
			            	}
			            }
			            pstmt.close();
			            rs.close();
					}
				}
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
			ecamsLogger.error("## Cmd1600.dirOpenChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.dirOpenChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.dirOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.dirOpenChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.dirOpenChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of dirOpenChk() method statement

	public String subNewDir(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			String 			  TKey 		  = "00001";
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String UserId = dataObj.get("UserId");
			String PrjNo = dataObj.get("PrjNo");
			String Docseq = dataObj.get("Docseq");//선택한 노드코드
			//String Updocseq = dataObj.get("Updocseq");
			String Dirname = dataObj.get("Dirname").replace("'", "''");//새폴더명
			String FullPath = dataObj.get("FullPath");//선택한 전체경로
			//String existYN = dataObj.get("existYN");//산출물 존재여부(하위폴더포함)

			if (Docseq.equals("00001")){
				strQuery.setLength(0);
	            strQuery.append("select a.* from cmd0301 a, cmm0020 b ");
	            strQuery.append("Where a.cd_prjno=? ");//PrjNo
	            strQuery.append("  and a.cd_devstep=b.cm_micode ");
	            strQuery.append("  and b.cm_codename=? ");//Dirname
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, PrjNo);
	        	pstmt.setString(2, Dirname);
	        	rs = pstmt.executeQuery();
	        	if (rs.next()){
	        		throw new Exception("단계에 해당하는 폴더 위치입니다. [문서관리>프로젝트등록]에서 단계를 추가해 주십시오.");
	        	}
	        	rs.close();
	        	pstmt.close();
			}

	        String WkFullDir = FullPath + "\\" + Dirname;
	        WkFullDir = WkFullDir.replace("'", "''").replace("\\", "/");
	        if (WkFullDir.substring(0,1).equals("/")) WkFullDir = WkFullDir.substring(1);

	        strQuery.setLength(0);
	        strQuery.append("select cd_dirname from cmd0303              \n");
	        strQuery.append(" where cd_prjno=?                           \n");//PrjNo
	        strQuery.append("   and upper(cd_dirname)=upper(?)           \n");//Dirname
	        strQuery.append("   and cd_updocseq=?                        \n");//Updocseq
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, PrjNo);
        	pstmt.setString(2, Dirname);
        	pstmt.setString(3, Docseq);
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		throw new Exception(Dirname + "은(는) 이미 등록된 폴더입니다.");
        	}
        	rs.close();
        	pstmt.close();

	        strQuery.setLength(0);
	        strQuery.append("select lpad(to_number(nvl(max(cd_docseq),0)) + 1,5,'0') max \n");
	        strQuery.append("  from cmd0303                                              \n");
	        strQuery.append(" where cd_prjno=?                                           \n");//PrjNo
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, PrjNo);
        	rs = pstmt.executeQuery();
        	if (rs.next()) TKey = rs.getString("max");
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
	        strQuery.append("insert into cmd0303 (cd_prjno,cd_docseq,cd_dirname,cd_updocseq,cd_dirpath) ");
	        strQuery.append("values (?,?,?,?,?) ");//PrjNo,TKey,Dirname,Docseq
        	pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
        	pstmt.setString(1, PrjNo);
        	pstmt.setString(2, TKey);
        	pstmt.setString(3, Dirname);
        	pstmt.setString(4, Docseq);
        	pstmt.setString(5, WkFullDir.replace("/", "\\"));
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();


	        strQuery.setLength(0);
	        strQuery.append("select count(*) as cnt from cmd0305 ");
	        strQuery.append("where cd_prjno=? ");//PrjNo
	        strQuery.append("  and cd_dsncd=? ");//TKey
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, PrjNo);
        	pstmt.setString(2, TKey);
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		strQuery.setLength(0);
 	           	if (rs.getInt("cnt") == 0){
			        strQuery.append("insert into cmd0305 (CD_DIRPATH,CD_EDITOR,CD_PRJNO,CD_DSNCD,CD_OPENDT,CD_LASTUPDT,CD_CLSDT) ");
			        strQuery.append("values (?,?,?,?,SYSDATE, SYSDATE,'') ");//WkFullDir,UserId,CD_PRJNO,TKey,
 	           	}else{
 			        strQuery.append("update cmd0305 set cd_dirpath=?, ");//WkFullDir
			        strQuery.append("cd_editor=?, ");//UserId
			        strQuery.append("cd_lastupdt=SYSDATE, cd_clsdt='' ");
			        strQuery.append("where cd_prjno=? ");//PrjNo
			        strQuery.append("  and cd_dsncd=? ");//TKey
 	           	}
		        pstmt2 = conn.prepareStatement(strQuery.toString());
		        pstmt2.setString(1, WkFullDir);
		        pstmt2.setString(2, UserId);
		        pstmt2.setString(3, PrjNo);
		        pstmt2.setString(4, TKey);
		        pstmt2.executeUpdate();
		        pstmt2.close();
        	}
        	rs.close();
        	pstmt.close();

        	rs = null;
        	pstmt = null;

        	return TKey;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subNewDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subNewDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subNewDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subNewDir() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subNewDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public int subDelDir(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
	        strQuery.append("delete cmd0303 Where cd_prjno=?                             \n");//PrjNo
	        strQuery.append("  and cd_docseq in (select cd_docseq from (Select cd_docseq,\n");
	        strQuery.append("                  cd_updocseq from cmd0303 where cd_prjno=?)\n");//PrjNo
	        strQuery.append("                     start with cd_updocseq=?               \n");//Docseq
	        strQuery.append("                     connect by prior cd_docseq=cd_updocseq \n");
	        strQuery.append("                     union                                  \n");
	        strQuery.append("                     select ? from dual)                    \n");//Docseq
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, dataObj.get("PrjNo"));
	    	pstmt.setString(2, dataObj.get("PrjNo"));
	    	pstmt.setString(3, dataObj.get("Docseq"));
	    	pstmt.setString(4, dataObj.get("Docseq"));
	    	pstmt.executeUpdate();
	    	pstmt.close();

	    	conn.commit();
	    	conn.close();
	    	pstmt = null;
	    	conn = null;

	    	return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subDelDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subDelDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subDelDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subDelDir() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subDelDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public int subRename(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
	        strQuery.append("update cmd0303 set cd_dirname=?,                   \n");
	        strQuery.append("       cd_dirpath=substr(cd_dirpath,1,instrev(cd_dirpath,'\\')) || ? \n");
	        strQuery.append(" where cd_prjno=? and cd_docseq=?                  \n");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, dataObj.get("dirname"));
	    	pstmt.setString(2, dataObj.get("dirname"));
	    	pstmt.setString(3, dataObj.get("PrjNo"));
	    	pstmt.setString(4, dataObj.get("Docseq"));
	    	pstmt.executeUpdate();
	    	pstmt.close();

	    	conn.commit();
	    	conn.close();
	    	pstmt = null;
	    	conn = null;

	    	return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subRename() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subRename() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subRename() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subRename() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subRename() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String getNodeInfo(String PrjNo,String docseq) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

        	strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmr0031    			\n");
			strQuery.append("where cr_prjno=?									\n");//PrjNo
			strQuery.append("  and cr_docseq in         						\n");
			strQuery.append("		(select cd_docseq from (Select cd_docseq, 	\n");
			strQuery.append("       cd_updocseq from cmd0303 where cd_prjno=? )	\n");//PrjNo
			strQuery.append("			start with cd_updocseq=?				\n");//cr_docseq
			strQuery.append("			connect by prior cd_docseq=cd_updocseq 	\n");
			strQuery.append("			union select ? from dual)				\n");//cr_docseq
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, PrjNo);
            pstmt.setString(2, PrjNo);
            pstmt.setString(3, docseq);
            pstmt.setString(4, docseq);
		    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	if (rs.getInt("cnt") >0) return "Y";
            	else return "N";
            }
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;

            return "N";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1600.getNodeInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.getNodeInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.getNodeInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.getNodeInfo() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.getNodeInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getNodeInfo() method statement

	public String confSelect(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               rsCnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_gubun,a.cm_rsrccd,a.cm_position        \n");
			strQuery.append("  from cmm0060 a,cmm0040 b                         \n");
			strQuery.append(" where a.cm_syscd=? 							    \n");
			strQuery.append("   and a.cm_reqcd=?                    			\n");
			strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid   \n");
			strQuery.append("   and b.cm_userid=?                               \n");
			if (dataObj.get("CCB_YN").equals("Y")){
	        	strQuery.append(" and (a.cm_rsrccd is null or (a.cm_rsrccd is not null and a.cm_rsrccd='Y')) \n");
			}
	        else{
	        	strQuery.append(" and a.cm_rsrccd is null \n");
	        }
			strQuery.append(" and a.cm_gubun not in ('1','2','P','4') \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, dataObj.get("SysCd"));
            pstmt.setString(2, dataObj.get("SinCd"));
            pstmt.setString(3, dataObj.get("UserId"));
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	++rsCnt;
				if (rs.getString("cm_gubun").equals("4")) {
					if (rs.getString("cm_position").indexOf("16")>=0) {
						rsCnt = 99;
						break;
					}
				}
            }
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;

            return Integer.toString(rsCnt);

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1600.confSelect() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.confSelect() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.confSelect() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.confSelect() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of confSelect() method statement

	public String request_Check_In(ArrayList<HashMap<String,String>> chkInList,
			HashMap<String,String> dataObj,
			ArrayList<HashMap<String,Object>> gyulData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		CodeInfo 		  codeInfo 	  = new CodeInfo();
		String			  AcptNo	  = null;
		int				  i			  = 0;
		int				  paramIndex  = 0;
		HashMap<String,String>	rData = null;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String UserId = dataObj.get("UserId");
			String PrjNo = dataObj.get("PrjNo");
			String PrjName = dataObj.get("PrjName");
			String SinCd = dataObj.get("SinCd");//반입코드 = 34
			String Sayu = dataObj.get("Sayu");
			String ChgCd = dataObj.get("ChgCd");
			String ChgTxt = dataObj.get("ChgTxt");
			String SysCd = dataObj.get("SysCd");
			String JobCd = dataObj.get("JobCd");
			String SysGb = dataObj.get("SysGb");

	    	String DocID_NUM = "";
	    	String MethCd = "";

        	strQuery.setLength(0);
        	strQuery.append("select distinct cd_methcd from cmd0301 ");
            strQuery.append("where cd_prjno = ? ");//PrjNo
            pstmt = conn.prepareStatement(strQuery.toString());
            paramIndex = 0;
            pstmt.setString(++paramIndex,PrjNo);
            rs = pstmt.executeQuery();
            if (rs.next()){
            	MethCd = rs.getString("cd_methcd") ;
            }else {
            	throw new Exception(PrjName+" 프로젝트에 방법론이 등록되어 있지 않습니다.");
            }
        	rs.close();
        	pstmt.close();

	        for (i=0;i<chkInList.size();i++){
	        	DocID_NUM = "";
	        	strQuery.setLength(0);
	        	strQuery.append("select cr_docid from cmr0030 ");
	            strQuery.append("where cr_prjno = ? ");//PrjNo
                strQuery.append("  and cr_docfile = ? ");//DocFile
                strQuery.append("  and cr_docseq = ? ");//dirpath
                pstmt = conn.prepareStatement(strQuery.toString());
                //pstmt = new LoggableStatement(conn,strQuery.toString());
                paramIndex = 0;
                pstmt.setString(++paramIndex,PrjNo);
                pstmt.setString(++paramIndex,chkInList.get(i).get("docfile"));
                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_docseq"));
                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
                if (rs.next()){
                	DocID_NUM = rs.getString("cr_docid");
                	chkInList.get(i).put("cr_docid",DocID_NUM);
                }
	        	rs.close();
	        	pstmt.close();

	            if (DocID_NUM == ""){
		        	strQuery.setLength(0);
		        	strQuery.append("insert into cmr0030 (CR_DOCID,CR_DOCFILE,CR_STATUS,CR_CREATDT, ");
		        	strQuery.append("CR_LASTDT,CR_CREATOR,CR_EDITOR,CR_LSTVER,CR_PRJNO,CR_DIRPATH, ");
		        	strQuery.append("CR_METHCD,CR_DEVSTEP,CR_CCBYN,CR_DOCSEQ) values ");
		        	strQuery.append("(lpad(docid_seq.nextval,19,'0'),?,'3',SYSDATE,SYSDATE,?,?,'0',?,?,?,?,?,?) ");
	                pstmt = conn.prepareStatement(strQuery.toString());
	                paramIndex = 0;
	                pstmt.setString(++paramIndex,chkInList.get(i).get("docfile"));
	                pstmt.setString(++paramIndex,UserId);
	                pstmt.setString(++paramIndex,UserId);
	                pstmt.setString(++paramIndex,PrjNo);
	                pstmt.setString(++paramIndex,chkInList.get(i).get("fullpath").replace("'","''"));
	                pstmt.setString(++paramIndex,MethCd);
	                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_devstep"));
	                pstmt.setString(++paramIndex,chkInList.get(i).get("checked"));
	                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_docseq"));
	    	    	pstmt.executeUpdate();
	    	    	pstmt.close();

	    	    	strQuery.setLength(0);
	          		strQuery.append("select cr_docid from cmr0030                  \n");
	    			strQuery.append(" where cr_prjno=? and cr_docfile=?             \n");
	    			strQuery.append("   and cr_docseq=?                            \n");
	    	        pstmt = conn.prepareStatement(strQuery.toString());
	    	        pstmt.setString(1, PrjNo);
	                pstmt.setString(2,chkInList.get(i).get("docfile"));
	                pstmt.setString(3,chkInList.get(i).get("cr_docseq"));
	    	        rs = pstmt.executeQuery();

	    	        if (rs.next()) {
	    	        	DocID_NUM = rs.getString("cr_docid");
	    	        }
	    	        rs.close();
	    	        pstmt.close();

	    	        chkInList.get(i).put("cr_docid",DocID_NUM);
	    	    	strQuery.setLength(0);
	    	    	strQuery.append("insert into cmr0031 (CR_PRJNO,CR_DOCID,CR_CREATDT,CR_LASTDT, ");
	    	    	strQuery.append("CR_EDITOR,CR_DOCSEQ) values ");
	    	    	strQuery.append("(?,?,SYSDATE,SYSDATE,?,?) ");
	                pstmt = conn.prepareStatement(strQuery.toString());
	                paramIndex = 0;
	                pstmt.setString(++paramIndex,PrjNo);
	                pstmt.setString(++paramIndex,DocID_NUM);
	                pstmt.setString(++paramIndex,UserId);
	                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_docseq"));
	    	    	pstmt.executeUpdate();
	    	    	pstmt.close();

	            }else{
	            	strQuery.setLength(0);
	            	strQuery.append("Update cmr0030 set ");
                    strQuery.append("CR_STATUS='3', ");
                    strQuery.append("CR_LASTDT=SYSDATE, ");
                    strQuery.append("CR_EDITOR=?, ");//UserId
                    strQuery.append("CR_LSTVER='0', ");
                    strQuery.append("CR_PRJNO=?, ");//Prjno
                    strQuery.append("CR_DIRPATH=?, ");//replace(dirpath,"'","''")
                    strQuery.append("CR_CCBYN=? ");//ccbyn
                    strQuery.append("Where CR_DOCID = ? ");//DocID_NUM
	                pstmt = conn.prepareStatement(strQuery.toString());
	                paramIndex = 0;
	                pstmt.setString(++paramIndex,UserId);
	                pstmt.setString(++paramIndex,PrjNo);
	                pstmt.setString(++paramIndex,chkInList.get(i).get("fullpath").replace("'","''"));
	                pstmt.setString(++paramIndex,chkInList.get(i).get("checked"));
	                pstmt.setString(++paramIndex,DocID_NUM);
	    	    	pstmt.executeUpdate();
	    	    	pstmt.close();
	            }
	            //conn.commit();
	        }
	        if (dataObj.get("DocInfoYN").equals("true")) return "";

	        ////  신청번호    ////
	        AcptNo = autoseq.getSeqNo(conn,SinCd);
	        autoseq = null;

	        ////	insert Cmr1000 start
	        strQuery.setLength(0);
	        strQuery.append("select count(*) as cnt from cmr1000 ");
	    	strQuery.append("where cr_acptno= ? ");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, AcptNo);
	    	rs = pstmt.executeQuery();
	    	if (rs.next()){
	    		if (rs.getInt("cnt")>0) throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
	    	}
	    	rs.close();
	    	pstmt.close();

	    	strQuery.setLength(0);
	    	strQuery.append("insert into cmr1000 ");
	    	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD, ");
	    	strQuery.append("CR_QRYCD,CR_PASSOK,CR_PASSCD,CR_EDITOR,CR_SAYU,CR_EMGCD,CR_DOCNO, ");
	    	strQuery.append("CR_PRJNO,CR_PRJNAME) values ( ");
	    	strQuery.append("?,?,?,?,sysdate,'0',?,?,'0',?,?,?,?,?,?,?) ");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	paramIndex = 0;
	    	pstmt.setString(++paramIndex, AcptNo);
	    	pstmt.setString(++paramIndex, SysCd);
	    	pstmt.setString(++paramIndex, SysGb);
	    	pstmt.setString(++paramIndex, JobCd);
	    	rData = (HashMap<String, String>) userInfo.getUserInfo(UserId)[0];
	    	pstmt.setString(++paramIndex, rData.get("teamcd"));
	    	pstmt.setString(++paramIndex, SinCd);
	    	rData = null;

	    	Object[] uInfo = codeInfo.getCodeInfo("REQUEST", "", "n");
	    	for (i=0 ; i<uInfo.length ; i++){
	    		rData = (HashMap<String, String>) uInfo[i];
	    		if (rData.get("cm_micode").equals(SinCd)){
	    			pstmt.setString(++paramIndex, rData.get("cm_codename"));
	    			rData = null;
	    			break;
	    		}
	    		rData = null;
	    	}
	    	uInfo = null;

	    	pstmt.setString(++paramIndex, UserId);
	    	pstmt.setString(++paramIndex, Sayu);
	    	pstmt.setString(++paramIndex, ChgCd);
	    	pstmt.setString(++paramIndex, ChgTxt);
	    	pstmt.setString(++paramIndex, PrjNo);
	    	pstmt.setString(++paramIndex, PrjName);
	    	pstmt.executeUpdate();
	    	pstmt.close();
	    	////	insert Cmr1000 end


	    	////   	insert Cmr1100 start
	    	for (i=0 ; i<chkInList.size() ; i++){
	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr1100 ");
	        	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_PRJNO,");
	        	strQuery.append("CR_VERSION,CR_BASENO,CR_PCDIR,CR_EDITOR,CR_CCBYN,CR_DOCSEQ) values (");
	        	strQuery.append("?,?,'0','03',?,?,1,?,?,?,?,?)");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	paramIndex = 0;
	        	pstmt.setString(++paramIndex, AcptNo);
	        	pstmt.setInt(++paramIndex, i+1);
	        	pstmt.setString(++paramIndex, chkInList.get(i).get("cr_docid"));
	        	pstmt.setString(++paramIndex, PrjNo);
	        	pstmt.setString(++paramIndex, AcptNo);
	        	pstmt.setString(++paramIndex, chkInList.get(i).get("fullpath").replace("'", "''"));
	        	pstmt.setString(++paramIndex, UserId);
	        	pstmt.setString(++paramIndex, chkInList.get(i).get("checked"));
	        	pstmt.setString(++paramIndex, chkInList.get(i).get("cr_docseq"));
	        	pstmt.executeUpdate();
	        	pstmt.close();

	        	////	docfile 상태값 변경
	        	strQuery.setLength(0);
	        	strQuery.append("update cmr0030 set ");
	            if (SinCd.equals("31")){//반출
	            	strQuery.append("cr_status='4', ");
	            }
	            else if (SinCd.equals("34")){//반입
	               strQuery.append("cr_status='7', ");
	            }
	            else {//반출취소
	               strQuery.append("cr_status='6', ");
	            }
	            strQuery.append("cr_editor=? ");//UserId
	            strQuery.append("where cr_docid=? ");//DocId
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	paramIndex = 0;
	        	pstmt.setString(++paramIndex, UserId);
	        	pstmt.setString(++paramIndex, chkInList.get(i).get("cr_docid"));
	        	pstmt.executeUpdate();
	        	pstmt.close();

	    	}

	    	Cmr0200  cmr0200 = new Cmr0200();
	    	String retMsg = "";
	    	if (gyulData.size() > 0){
        	    retMsg = cmr0200.request_Confirm(AcptNo,SysCd,SinCd,UserId,true,gyulData,conn);
	    	} else {
	    		retMsg = cmr0200.request_Confirm(AcptNo,SysCd,SinCd,UserId,false,null,conn);
	    	}
        	if (!retMsg.equals("OK")) {
        		AcptNo = "ERROR결재정보작성 중 오류가 발생하였습니다.";
        		conn.rollback();
        	} else {
        		conn.commit();
        	}

        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;
	    	userInfo = null;
	    	codeInfo = null;

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
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.request_Check_In() SQLException END ##");
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
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.request_Check_In() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.request_Check_In() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}//end of Cmd1600 class statement
