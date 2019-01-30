
/*****************************************************************************************
	1. program ID	: test_Tree2DAO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: test_Tree2DAO
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

//import oracle.sql.*;
//import oracle.jdbc.*;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import app.eCmd.Cmd0100;
/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



public class Cmd1700{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 게시판을 조회합니다.(구분값을 주어) 
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	
    public Object[] getFileList_excel(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			String _syscd = dataObj.get("cm_syscd");
			//String _sysmsg = dataObj.get("cm_sysmsg");
			String _sourcename = "";
			String _jobcd = "";
			//String _rsrccd = "";
			//String _langcd = "";
			String _editor = "";
			String _dirpath = "";
			String _dsncd = "";
			String errMsg = "";
			String strInfo = "";
			String gbnCd   = "";
			boolean errSw = false;
			//String _baseItem = "";
			boolean tmpYN = false;
			
			int errCnt = 0;
			rsval.clear();
			for (int i=0 ; i<fileList.size() ; i++)
			{
				if (dataObj.get("gbncd") != null && dataObj.get("gbncd") != "") {
					gbnCd = dataObj.get("gbncd");
				} else gbnCd = "adm";
				
				rst = new HashMap<String, String>();
				rst.put("NO",Integer.toString(i+1));
				rst.put("sysmsg", fileList.get(i).get("sysmsg").trim());
				rst.put("jobcd", fileList.get(i).get("jobcd").trim());
				rst.put("userid", fileList.get(i).get("userid").trim());
				rst.put("rsrcname", fileList.get(i).get("rsrcname").trim());
				rst.put("story", fileList.get(i).get("story").trim());
				rst.put("dirpath", fileList.get(i).get("dirpath").trim());
				rst.put("jawon", fileList.get(i).get("jawon").trim());
				if(fileList.get(i).get("type") != "" ){		
					if(fileList.get(i).get("type") != null ){				
						rst.put("type", fileList.get(i).get("type").trim());
					}else{
						rst.put("type", "");						
					}
				}else{
					rst.put("type", "");
				}
				rst.put("_syscd", _syscd);
				
				errMsg = "";
				errSw = false;
				_sourcename = fileList.get(i).get("rsrcname").trim();
				//_rsrccd = "";
				_jobcd = "";
				//_langcd = "";
				_editor = "";
				_dirpath = "";
				_dsncd = "";
				
								
				if (fileList.get(i).get("jawon") == "" && fileList.get(i).get("jawon") == null){
					errMsg = "프로그램종류 입력없음/";
					errSw = true;
				}else{
					strQuery.setLength(0);
					strQuery.append("select a.cm_rsrccd,a.cm_info from cmm0036 a,cmm0020 b ");
	                strQuery.append("where a.cm_syscd=? ");
	                strQuery.append("  and b.cm_macode='JAWON' and upper(b.cm_codename)=upper(?) ");
	                strQuery.append("  and a.cm_rsrccd=b.cm_micode ");
	                
	                pstmt = conn.prepareStatement(strQuery.toString());
	                pstmt.setString(1, _syscd);
	                pstmt.setString(2, fileList.get(i).get("jawon").trim());
	                rs = pstmt.executeQuery();
	                
	                if(rs.next()){
	                	//_rsrccd = rs.getString("cm_rsrccd");
	                	strInfo = rs.getString("cm_info");
	                	rst.put("_rsrccd",rs.getString("cm_rsrccd"));
	                	rst.put("_info",rs.getString("cm_info"));
	                	
	                	if (strInfo.substring(4,5).equals("1")) {
	                		if (fileList.get(i).get("service") == null || fileList.get(i).get("service") == "") {
	                			errMsg = errMsg + "서비스명미입력/";
	    	                	errSw = true;
	                		} else if (fileList.get(i).get("service").trim().length() == 0) {
	                			errMsg = errMsg + "서비스명미입력/";
	    	                	errSw = true;
	                		} else {
	                			rst.put("service", fileList.get(i).get("service").trim());
	                		}
	                	}
	                }else{
	                	errMsg = errMsg + "미등록 프로그램종류코드/";
	                	errSw = true;
	                }
					rs.close();
					pstmt.close();
				}
				
				
				if (fileList.get(i).get("jobcd") == "" && fileList.get(i).get("jobcd") == null){
					errMsg = errMsg + "업무코드  입력없음/";
					errSw = true;
				}else{
					strQuery.setLength(0);
					strQuery.append("select cm_jobcd,cm_jobname from cmm0102 where ");
					strQuery.append("cm_jobcd=? or cm_jobname=? ");
					
	                pstmt = conn.prepareStatement(strQuery.toString());
	                pstmt.setString(1, fileList.get(i).get("jobcd").trim());
	                pstmt.setString(2, fileList.get(i).get("jobcd").trim());
	                rs = pstmt.executeQuery();
	                
	                if(rs.next()){
	                	_jobcd = rs.getString("cm_jobcd");
	                    rst.put("_jobcd",rs.getString("cm_jobcd"));
	                    tmpYN = true;
	                }else{
	                	tmpYN = false;
	                	errMsg = errMsg + "미등록 업무코드/";
	                	errSw = true;
	                }
					rs.close();
					pstmt.close();
					
					if (tmpYN){
		            	strQuery.setLength(0);
		            	strQuery.append("select count(*) as cnt from cmm0034 where ");
		            	strQuery.append("cm_syscd=? and cm_jobcd=? ");
		            	
		                pstmt = conn.prepareStatement(strQuery.toString());
		                pstmt.setString(1, _syscd);
		                pstmt.setString(2, _jobcd);
		                rs = pstmt.executeQuery();
		                
		                if (rs.next()){
		                	if (rs.getInt("cnt")==0){
		                		if (gbnCd.equals("adm")) {
			                		strQuery.setLength(0);
			                		strQuery.append("insert into cmm0034 (CM_SYSCD,CM_JOBCD,CM_CREATDT,CM_LASTDT,CM_EDITOR) ");
			                		strQuery.append("values (?,?,SYSDATE,SYSDATE,?) ");
			                		
			                		pstmt2 = conn.prepareStatement(strQuery.toString());
			                		pstmt2.setString(1,_syscd);
			                		pstmt2.setString(2,_jobcd);
			                		pstmt2.setString(3,fileList.get(i).get("userid"));
			                		pstmt2.executeUpdate();
			                		
			                		pstmt2.close();
		                		} else {
		                			errMsg = errMsg + "시스템내 미등록 업무코드";
		                		}
		                	}
		                }
		                rs.close();
		                pstmt.close();
					}
				}
				
				
				strQuery.setLength(0);
				strQuery.append("select cm_userid from cmm0040 where ");
				strQuery.append("cm_userid=? ");
				
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1, fileList.get(i).get("userid").trim());
                rs = pstmt.executeQuery();
                
                if(rs.next()){
                	_editor = rs.getString("cm_userid");
                }else{
                	errMsg = errMsg + "미등록 사용자/";
                	_editor = "admin";
                	errSw = true;
                }
                rst.put("_editor",_editor);
				rs.close();
				pstmt.close();
				
				/*  서정모_20091029_임시주석처리
				if (_sourcename.indexOf("(")>-1){
					errMsg = errMsg + "프로그램명에 괄호있음/";
					errSw = true;
				}
				*/
				if (_sourcename.indexOf(" ")>-1){
					errMsg = errMsg + "프로그램명에 SPACE있음/";
					errSw = true;
				}
				if (_sourcename.indexOf(",")>-1){
					errMsg = errMsg + "프로그램명에 컴마있음/";
					errSw = true;
				}
				/*  서정모_20091029_임시주석처리
				if (_sourcename.indexOf(")")>-1){
					errMsg = errMsg + "프로그램명에 괄호있음/";
					errSw = true;
				}
				*/
				//_baseItem = "";
				
				
				_dirpath = fileList.get(i).get("dirpath").trim();
				rst.put("_dirpath", fileList.get(i).get("dirpath").trim());
				
				if (_dirpath == "" & _dirpath == null){
					errMsg = errMsg + "프로그램경로 입력없음/";
					errSw = true;
				}
				if ( _dirpath.substring(_dirpath.length()-1).indexOf("/")>-1 ){
					_dirpath = _dirpath.substring(0, _dirpath.length()-1);
				}
				
				if(errSw == true){
					strQuery.setLength(0);
					strQuery.append("select count(*) as cnt from cmm0044      \n");
					strQuery.append(" where cm_userid=? and cm_syscd=?        \n");
					strQuery.append("   and cm_jobcd=?                        \n");
	
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, _editor);
					pstmt.setString(2, _syscd);
					pstmt.setString(3, _jobcd);
					rs  = pstmt.executeQuery();
					if (rs.next()){
						if (rs.getInt("cnt")==0) {
							if (gbnCd.equals("adm")) {
								strQuery.setLength(0);
								strQuery.append("insert into cmm0044                      \n");
								strQuery.append("(cm_userid,cm_syscd,cm_jobcd,cm_creatdt) \n");
								strQuery.append("values (?, ?, ?, SYSDATE)                \n");
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, _editor);
								pstmt2.setString(2, _syscd);
								pstmt2.setString(3, _jobcd);
								pstmt2.executeUpdate();
								pstmt2.close();
							} else {
								errMsg = errMsg + "업무권한없음/";
							}
						}
					}
					rs.close();
					pstmt.close();
				}
				
				if (errSw == false) {
					rst.put("_dirpath", _dirpath);
					for (int j=0;rsval.size()>j;j++) {
						if (rsval.get(j).get("_dirpath").equals(_dirpath) &&
							rsval.get(j).get("rsrcname").equals(_sourcename)) {
							++errCnt;
							errMsg = errMsg + "중복Data/";
							errSw = true;
							break;
						}
					}
				}

				if (errSw == true){//에러 있음
					++errCnt;
					rst.put("errsw", "1");
					rst.put("errmsg", errMsg);
				}else{
					strQuery.setLength(0);
					strQuery.append("select cm_dsncd from cmm0070 where cm_syscd=? ");
					strQuery.append("and cm_dirpath=? ");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, _syscd);
					pstmt.setString(2, _dirpath);
					rs  = pstmt.executeQuery();
					if (rs.next()){
						_dsncd = rs.getString("cm_dsncd");
					}else{
						_dsncd = "0000001";
						
						strQuery.setLength(0);
	                    strQuery.append("select lpad(max(cm_dsncd)+1,7,'0') max from cmm0070 where cm_syscd=? ");
	                    pstmt2 = conn.prepareStatement(strQuery.toString());
	                    pstmt2.setString(1, _syscd);
	                    rs2 = pstmt2.executeQuery();
	                    if (rs2.next()){
	                        if (rs2.getString("max") != null){
	                        	_dsncd = rs2.getString("max");
	        				}
	                    }
	                    rs2.close();
	                    pstmt2.close();
	                    
	                    strQuery.setLength(0);
	                    strQuery.append("insert into cmm0070 (CM_SYSCD,CM_DSNCD,CM_DIRPATH,CM_EDITOR,CM_OPENDT,CM_LASTUPDT) ");
	                    strQuery.append("values (?,?,?,'MASTER',SYSDATE,SYSDATE)");
	                    pstmt2 = conn.prepareStatement(strQuery.toString());
	                    pstmt2.setString(1, _syscd);
	                    pstmt2.setString(2, _dsncd);
	                    pstmt2.setString(3, _dirpath);
	                    pstmt2.executeUpdate();
	                    pstmt2.close();
	                    
					}
					rst.put("_dsncd",_dsncd);
					rs.close();
					pstmt.close();
					
					rst.put("story", fileList.get(i).get("story").replace("'", "''"));
					rst.put("_syscd",_syscd);
					
					
					//_baseItem = Cmr0020_Insert(conn,rst);
					
					strQuery.setLength(0);
					strQuery.append("select cr_itemid,cr_status,cr_lstver from cmr0020    ");
					strQuery.append(" where cr_syscd=? and cr_dsncd=? ");
					strQuery.append("   and cr_rsrcname=?             ");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, _syscd);
					pstmt.setString(2, _dsncd);
					pstmt.setString(3, rst.get("rsrcname"));
					rs  = pstmt.executeQuery();
					if (rs.next()){
						if (gbnCd.equals("adm")) {
							if (_syscd.equals("00005") || _syscd.equals("00006")) {
								if (strInfo.substring(26,27).equals("1")) {
						        	if (rs.getInt("cr_lstver") >0 || !rs.getString("cr_status").equals("3")){
										rst.put("errmsg","버전:"+rs.getString("cr_lstver")+",상태:"+rs.getString("cr_status"));
										rst.put("errsw", "1");
										++errCnt;
										errSw = true;
						        	} 
								}
							}
						} else {
							rst.put("errmsg","기등록프로그램");
							rst.put("errsw", "1");
							++errCnt;
							errSw = true;
						}
						if (errSw == false) {
			        		//_baseItem = rs.getString("cr_itemid");
							rst.put("_itemid", rs.getString("cr_itemid"));
							rst.put("errmsg","정상");
							rst.put("errsw", "0");
							
				        	strQuery.setLength(0);
			        		strQuery.append("update cmr0020 set CR_RSRCCD=?,CR_JOBCD=?,		");
			        		strQuery.append("                   CR_LANGCD=?,CR_STORY=?,		");
			        		strQuery.append("                   CR_LASTDATE=SYSDATE, 		");
			        		strQuery.append("                   CR_EDITOR=?,CR_NOMODIFY=0,	");
			        		strQuery.append("                   CR_PGMTYPE=?            	");			        		
			        		strQuery.append("where cr_itemid=? \n");
			        		pstmt2 = conn.prepareStatement(strQuery.toString());
			        		pstmt2.setString(1, rst.get("_rsrccd"));
			        		pstmt2.setString(2, rst.get("_jobcd"));
			        		pstmt2.setString(3, rst.get("_langcd"));
			        		pstmt2.setString(4, rst.get("story"));
			        		pstmt2.setString(5, rst.get("_editor"));
			        		pstmt2.setString(6, rst.get("type"));
			        		pstmt2.setString(7, rs.getString("cr_itemid"));
			        		pstmt2.executeUpdate();
			        		pstmt2.close();
						}
					}else{
						rst.put("_itemid", "insert");
						rst.put("errmsg","정상");
						rst.put("errsw", "0");
					}
			        rs.close();
			        pstmt.close();
				}
				rsval.add(rst);
				rst = null;
			}			
			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
						
			
			return rsval.toArray();
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1600.getFileList_excel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1600.getFileList_excel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1600.getFileList_excel() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1600.getFileList_excel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1600.getFileList_excel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public Object[] request_cmr0020(ArrayList<HashMap<String,String>> chkInList,HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		HashMap<String, String> rst3  = null;
		String            retMsg      = "";
		int				  i;
		Cmd0100 cmd0100 = new Cmd0100();
		try {
			conn = connectionContext.getConnection();
			//ecamsLogger.debug("++++++++++List++"+etcData);	
			rsval.clear();
	        for (i=0;i<chkInList.size();i++){  
	        	rst = new HashMap<String, String>();
	        	rst = chkInList.get(i);
	        			//cmr0020_Insert(UserId,SysCd,strAftDsn,strRsrcName,strRsrcCd,JobCd,ProgTit,BaseItem,CM_info,conn) ;
	        	//retMsg = cmd0100.cmr0020_Insert(chkInList.get(i).get("_editor"),etcData.get("cm_syscd"),chkInList.get(i).get("_dsncd"),chkInList.get(i).get("rsrcname"),
	        			//chkInList.get(i).get("_rsrccd"),chkInList.get(i).get("_jobcd"),chkInList.get(i).get("story"),"",chkInList.get(i).get("_info"),conn);

	        	rst3 = new HashMap<String, String>();
	        	rst3.put("userid",chkInList.get(i).get("_editor"));
	        	rst3.put("syscd",etcData.get("cm_syscd"));
	        	rst3.put("dsncd",chkInList.get(i).get("_dsncd"));
	        	rst3.put("rsrcname",chkInList.get(i).get("rsrcname"));
	        	rst3.put("rsrccd",chkInList.get(i).get("_rsrccd"));
	        	rst3.put("jobcd",chkInList.get(i).get("_jobcd"));
	        	rst3.put("grpcd",chkInList.get(i).get("_jobcd"));
	        	rst3.put("story",chkInList.get(i).get("story"));
	        	rst3.put("baseitem","");
	        	rst3.put("info",chkInList.get(i).get("_info"));
	        	retMsg = cmd0100.cmr0020_Insert(rst3,conn);
	        	rst3 = null;
	        	
	        	if (retMsg.substring(0,1).equals("0")) {
	        		rst.put("cr_itemid",retMsg.substring(1));
	        		rst.put("errmsg","정상");
	        		rst.put("errsw","0");
	        	} else {
	        		rst.put("cr_itemid","");
	        		rst.put("errmsg",retMsg);
	        		rst.put("errsw","1");
	        	}
	        	rsval.add(rst);
        	}
	        conn.close();
			rs = null;
			pstmt = null;
			conn = null;
        	
        	//ecamsLogger.debug("+++++++++Request E N D+++");
        	return rsval.toArray();
			
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1600.request_cmr0020() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm1600.request_cmr0020() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1600.request_cmr0020() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm1600.request_cmr0020() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{					
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1600.request_cmr0020() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		
	}
	
}//end of test_Tree2DAO class statement
