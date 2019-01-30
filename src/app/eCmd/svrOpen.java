/*****************************************************************************************
	1. program ID	: svrOpen.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import app.common.CreateXml;
import app.common.LoggableStatement;
import app.common.eCAMSInfo;
import app.thread.ThreadPool;
import app.eCmr.Cmr0200;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class svrOpen{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getSvrDir(String UserID,String SysCd,String SvrIp,String SvrPort,String BaseDir,String AgentDir,
		String SysOs,String HomeDir,String svrName,String buffSize) throws SQLException, Exception {
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
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
		String            strBaseDir  = "";
		int               upSeq       = 0;
		int               maxSeq      = 0;
		boolean           dirSw       = false;
		ArrayList<Document> list = null;
		Object[] returnObjectArray = null;
		int               ret = 0;
		int               j = 0;
		String       shFileName = "";
		String       strParm = "";

		rsval.clear();

		try {
			strBinPath = ecamsinfo.getFileInfo("14");
			ErrSw = false;
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			if (HomeDir == null) HomeDir = "";
			HomeDir = HomeDir.replace("##USER##", UserID);
			Cmr0200 cmr0200 = new Cmr0200();
			shFileName = "dir"+ UserID + ".sh";
			strFile = strTmpPath + "dir"+ UserID;
			if (SysOs.equals("03")) {
				BaseDir = BaseDir.replace("/", "\\");
				BaseDir = BaseDir.replace("\\\\", "\\");
				BaseDir = BaseDir.replace("\\", "\\\\");
			}
			
			strParm = "./ecams_dir " + SvrIp + " " + SvrPort + " " + buffSize + " " + BaseDir + " dir" + UserID;
			ret = cmr0200.execShell(shFileName, strParm, false);
			//ecamsLogger.error("====return 2===="+Integer.toString(p.exitValue())+" \n");
			if (ret != 0) {
				if (ret == 1) {
					throw new Exception("추출 디렉토리가 없습니다. run=["+strParm +"]" + " return=[" + ret + "]" );
				}else if (ret == 2) {
					throw new Exception("디렉토리추출을 위한 분석작업 실패하였습니다. run=["+strParm +"]" + " return=[" + ret + "]" );
				}else if (ret == 3) {
					throw new Exception("해당서버에서 Tmp파일 삭제  실패하였습니다. run=["+strParm +"]" + " return=[" + ret + "]" );
				}

				ErrSw = true;

			}
			//strFile = "c:\\dir20020206";
            if (ErrSw == false) {
    			BaseDir = BaseDir.replace("\\\\", "/");
    			
    			ecamsLogger.error("+++ ecamsinfo   +++\n");
    			eCAMSInfo ecamsinf = new eCAMSInfo();
    			String  noNameAry[] = ecamsinf.getNoName();

    			File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("디렉토리추출을 위한 작업에 실패하였습니다 [작성된 파일 없음] ["+ strFile+"]");
		        } else {
			        BufferedReader in = null;
			        //PrintWriter out = null;

			        try {
			            //in = new BufferedReader(new FileReader(mFile));
			            in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"MS949"));
			            ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");
			            String str = null;
			            //strBaseDir = BaseDir;

						maxSeq = maxSeq + 1;

						rst = new HashMap<String,String>();
						rst.put("cm_dirpath","["+svrName+"]"+HomeDir);
						rst.put("cm_fullpath",HomeDir);
						rst.put("cm_upseq","0");
						rst.put("cm_seqno",Integer.toString(maxSeq));
						rsval.add(maxSeq - 1, rst);
						upSeq = maxSeq;

			            while ((str = in.readLine()) != null) {
			                if (str.length() > 0) {
			                	dirSw = false;
			                	if (SysOs.equals("03")) {
			                		str = str.trim();
			                		str = str.replace("\\", "/");
			                		if (str.indexOf("디렉터리")>0) {
			                			if (str.substring(0,BaseDir.length()).equals(BaseDir)) {
			                				strBaseDir = str.substring(0,str.indexOf("디렉터리"));
				                			strBaseDir = strBaseDir.trim();
				                			str = strBaseDir;
				                			dirSw = true;
			                			}
			                		} else {
			                		}

			                	} else {
				                	if (str.substring(str.length() - 1).equals(":")) {
				                		strBaseDir = str.substring(0,str.length() - 1);
				                		str = strBaseDir;
				                		dirSw = true;
				                	}
			                	}
			                	if (dirSw == true) {
			                		if (HomeDir.length() < str.length()){
				                		str = str.substring(HomeDir.length());
				                		findSw = false;
				                		if (str.length() != 0 ) {
				                			findSw = true;
				                			for (j=0;noNameAry.length>j;j++) {	
												if (str.indexOf(noNameAry[j])>=0){
													findSw = false;
													break;
												}
											}
				                		}
				                		if (findSw) {
					                		pathDepth = str.substring(1).split("/");
					                		strDir = HomeDir;
											upSeq = 1;
											findSw = false;											
											for (int i = 0;pathDepth.length > i;i++) {
												if (pathDepth[i].length() > 0) {
													if (strDir.length() > 1 ) {
														strDir = strDir + "/";
													}
													strDir = strDir + pathDepth[i];
													findSw = false;
													if (rsval.size() > 0) {
														for (j = 0;rsval.size() > j;j++) {
															if (rsval.get(j).get("cm_fullpath").equals(strDir)) {
																upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));
																findSw = true;
															}
														}
													} else {
														findSw = false;
													}
													if (findSw == false) {
														maxSeq = maxSeq + 1;

														//ecamsLogger.debug("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
														rst = new HashMap<String,String>();
														rst.put("cm_dirpath",pathDepth[i]);
														rst.put("cm_fullpath",strDir);
														rst.put("cm_upseq",Integer.toString(upSeq));
														rst.put("cm_seqno",Integer.toString(maxSeq));
														rsval.add(maxSeq - 1, rst);
														upSeq = maxSeq;
													}
												}
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
            }
            String strBran = "";
            if (rsval.size() > 0) {
				for (int i = 0;rsval.size() > i;i++) {
					strBran = "false";
					for (j=0;rsval.size()>j;j++) {
						if (i != j) {
							if (rsval.get(i).get("cm_seqno").equals(rsval.get(j).get("cm_upseq"))) {
								strBran = "true";
								break;
							}
						}
					}
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cm_dsncd"),
							strBran,rsval.get(i).get("cm_upseq"));
				}
			}

    		list = new ArrayList<Document>();
    		list.add(ecmmtb.getDocument());
    		returnObjectArray = list.toArray();

    		list = null;
    		//ecamsLogger.error(ecmmtb.xml_toStr());
    		return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getSvrDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getSvrDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getSvrDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getSvrDir() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
		}

	}//end of getDirPath() method statement
	public Object[] getFileList(String UserID,String SysCd,String SvrIp,String SvrPort,String BaseDir,String SvrCd,String SvrSeq,String GbnCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		boolean           findSw      = false;
		boolean           ErrSw      = false;
		String            strTmpPath  = "";
		String            strFile     = "";
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();
		rsval.clear();
		int               ret = 0;
		String       shFileName = "";
		String       strParm = "";

		try {
			conn = connectionContext.getConnection();
			ErrSw = false;
			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");
			
			Cmr0200 cmr0200 = new Cmr0200();
			shFileName = "filelist"+ UserID + ".sh";
			strFile = strTmpPath + "/filelist"+ UserID;
			strParm = "./ecams_ih_cs " + SysCd + " " + SvrIp + " "+ BaseDir + " "  + SvrPort + " filelist" + UserID + " " + GbnCd;
			ret = cmr0200.execShell(shFileName, strParm, false);
			//ecamsLogger.error("====return 2===="+Integer.toString(p.exitValue())+" \n");
			if (ret != 0) {
				throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다. run=["+strParm +"]" + " return=[" + ret + "]" );
			}

			//strFile = "c:\\eCAMS\\filelist" + UserID + "."+SysCd+".ih.cs";
            if (ErrSw == false) {
				File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다 [작성된 파일 없음] ["+strFile+"]");
		        } else {
			        BufferedReader in = null;
			        //PrintWriter out = null;

			        try {
			            in = new BufferedReader(new FileReader(mFile));
			            String str = null;
			            String wkF = "";
			            String wkB = "";
			            String wkDir = "";
			            String wkDsnCd = null;
			            String wkJobCd = null;
			            String wkRsrcCd = null;
			            while ((str = in.readLine()) != null) {
			                if (str.length() > 0) {
			                	if (!str.substring(0,1).equals("d")) {
			                		if (str.substring(0,1).equals("/")) {
			                			wkDir = str.substring(0, str.length() - 1);
			                		} else if (str.substring(0,1).equals("-")) {
			                			wkF = str;
			                			int Y = 0;
			                			int X = 0;
			                			while (wkF.length() > 0) {
			                				Y = Y + 1;
			                				X = wkF.indexOf(" ");
			                				if (X >= 0) {
			                					wkB = wkF.substring(0,X).trim();
			                					wkF = wkF.substring(X).trim();
			                				} else {
			                					wkB = wkF.trim();
			                					wkF = "";
			                				}
			                				if (Y == 9) {
			                					wkRsrcCd = "";
			                					wkJobCd = "";
			                					wkDsnCd = "";
			                					strQuery.setLength(0);
			                					strQuery.append("select cm_dsncd from cmm0070            \n");
			                					strQuery.append(" where cm_syscd=? and cm_dirpath=?      \n");
			                					pstmt = conn.prepareStatement(strQuery.toString());
			                		            pstmt.setString(1, SysCd);
			                		            pstmt.setString(2, wkDir);

			                		            rs = pstmt.executeQuery();
			                		            if (rs.next()) {
			                		            	wkDsnCd = rs.getString("cm_dsncd");
			                		            }
			                		            rs.close();
			                		            pstmt.close();

			                		            findSw = false;
			                		            if (wkDsnCd != null && wkDsnCd != "") {
			                		            	strQuery.setLength(0);
			                		            	strQuery.append("select count(*) cnt from cmr0020        \n");
				                					strQuery.append(" where cr_syscd=? and cr_dsncd=?        \n");
				                					strQuery.append("   and cr_rsrcname=?                    \n");
				                					pstmt = conn.prepareStatement(strQuery.toString());
				                					//pstmt = new LoggableStatement(conn,strQuery.toString());
				                		            pstmt.setString(1, SysCd);
				                		            pstmt.setString(2, wkDsnCd);
				                		            pstmt.setString(3, wkB);
				                		            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				                		            rs = pstmt.executeQuery();
				                		            if (rs.next()) {
				                		            	if (rs.getInt("cnt") > 0) findSw = true;
				                		            }
				                		            rs.close();
				                		            pstmt.close();
			                		            }

			                		            if (findSw == false && wkDsnCd != null && wkDsnCd != "") {
			                		            	strQuery.setLength(0);
			                		            	strQuery.append("select cm_rsrccd from cmm0072           \n");
				                					strQuery.append(" where cm_syscd=? and cm_dsncd=?        \n");
				                					pstmt = conn.prepareStatement(strQuery.toString());
				                					//pstmt = new LoggableStatement(conn,strQuery.toString());
				                		            pstmt.setString(1, SysCd);
				                		            pstmt.setString(2, wkDsnCd);
				                		            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				                		            rs = pstmt.executeQuery();
				                		            while (rs.next()) {
				                		            	if (wkRsrcCd.length()>0) wkRsrcCd = wkRsrcCd+",";
				                		            	wkRsrcCd = wkRsrcCd+rs.getString("cm_rsrccd");
				                		            }
				                		            rs.close();
				                		            pstmt.close();

			                		            	strQuery.setLength(0);
			                		            	strQuery.append("select cm_jobcd from cmm0073           \n");
				                					strQuery.append(" where cm_syscd=? and cm_dsncd=?        \n");
				                					pstmt = conn.prepareStatement(strQuery.toString());
				                		            pstmt.setString(1, SysCd);
				                		            pstmt.setString(2, wkDsnCd);

				                		            rs = pstmt.executeQuery();
				                		            while (rs.next()) {
				                		            	if (wkJobCd.length()>0) wkJobCd = wkJobCd+",";
				                		            	wkJobCd = wkJobCd+rs.getString("cm_jobcd");
				                		            }
				                		            rs.close();
				                		            pstmt.close();

			                		            }

			                		            if (findSw == false) {
			                		            	String strRsrc = "";
			                		            	String strExt = "";
			                		            	int    i = 0;
			                		            	strQuery.setLength(0);
			                		            	strQuery.append("select cm_rsrccd from cmm0038                     \n");
			                		    			strQuery.append("where cm_syscd=? and cm_svrcd=? and cm_seqno=?    \n");
			                		    			strQuery.append("  and instr(?,cm_volpath)>0                       \n");
			                		                pstmt = conn.prepareStatement(strQuery.toString());
			                		    			//pstmt = new LoggableStatement(conn,strQuery.toString());
			                		    			pstmt.setString(1, SysCd);
			                		                pstmt.setString(2, SvrCd);
			                		                pstmt.setInt(3, Integer.parseInt(SvrSeq));
			                		                pstmt.setString(4, wkDir);
			                		                ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			                		                rs = pstmt.executeQuery();
			                		    			while (rs.next()){
			                		    				if (wkRsrcCd != "" && wkRsrcCd != null) {
			                		    					if (wkRsrcCd.indexOf(rs.getString("cm_rsrccd")) >= 0) {
			                		    						if (strRsrc != null && strRsrc != "") {
			                		    							strRsrc = strRsrc+","+rs.getString("cm_rsrccd");
			                		    						} else strRsrc = rs.getString("cm_rsrccd");
			                		    					} wkRsrcCd = wkRsrcCd+","+rs.getString("cm_rsrccd");
			                		    				} else {
		                		    						if (strRsrc != null && strRsrc != "") {
		                		    							strRsrc = strRsrc+","+rs.getString("cm_rsrccd");
		                		    						} else strRsrc = rs.getString("cm_rsrccd");
			                		    				}
			                		    			}//end of while-loop statement
			                		    			rs.close();
			                		    			pstmt.close();
			                		    			if (strRsrc == null || strRsrc == "")
			                		    				if (wkRsrcCd != null && wkRsrcCd != "") strRsrc = wkRsrcCd;
			                		    			//if (strRsrc.indexOf(",") >= 0 || strRsrc == null || strRsrc == "") {
			                		    				wkRsrcCd = "";
			                		    				String svRsrc = "";
				                		    			if (wkB.indexOf(".") >= 0) {
				                							i = wkB.lastIndexOf(".");
				                							if (i>=0) strExt = wkB.substring(i);
				                						}
				                						strQuery.setLength(0);
				                						strQuery.append("select a.cm_rsrccd from cmm0032 a,cmm0023 b        \n");
				                						strQuery.append(" where a.cm_syscd=? and a.cm_langcd=b.cm_langcd    \n");
				                						strQuery.append("and a.cm_rsrccd not in (select cm_samersrc from cmm0037  \n");
				                						strQuery.append("                         where cm_syscd=?          \n");
				                						strQuery.append("                           and cm_factcd='04')     \n");
				                						if (strExt != null && strExt != "")
				                							strQuery.append("   and instr(b.cm_exename,?)>0                 \n");
				                						else
				                							strQuery.append("   and b.cm_exeno='Y'                          \n");
				                						pstmt = conn.prepareStatement(strQuery.toString());
				                						//pstmt = new LoggableStatement(conn,strQuery.toString());
				                			            pstmt.setString(1, SysCd);
				                			            pstmt.setString(2, SysCd);
				                			            if (strExt != null && strExt != "") pstmt.setString(3, strExt);
				                			            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				                			            rs = pstmt.executeQuery();
				                						while (rs.next()){
				                		    				if (strRsrc != "" && strRsrc != null) {
				                		    					if (strRsrc.indexOf(rs.getString("cm_rsrccd")) >= 0) {
				                		    						if (wkRsrcCd != null && wkRsrcCd != "") {
				                		    							wkRsrcCd = wkRsrcCd+","+rs.getString("cm_rsrccd");
				                		    						} else wkRsrcCd = rs.getString("cm_rsrccd");
				                		    					}
				                		    					svRsrc = svRsrc+","+rs.getString("cm_rsrccd");
				                		    				} else {
			                		    						if (wkRsrcCd != null && wkRsrcCd != "") {
			                		    							wkRsrcCd = wkRsrcCd+","+rs.getString("cm_rsrccd");
			                		    						} else wkRsrcCd = rs.getString("cm_rsrccd");
				                		    				}

				                						}//end of while-loop statement
				                						rs.close();
				                						pstmt.close();
				                						if (svRsrc != null && svRsrc != "") {
				                							if (wkRsrcCd == null || wkRsrcCd == "") wkRsrcCd = svRsrc;
				                						}
			                		    			//} else wkRsrcCd = strRsrc;
				                					if (wkRsrcCd == null || wkRsrcCd == "") {
				                						strQuery.setLength(0);
				                						strQuery.append("select cm_rsrccd from cmm0036   \n");
				                						strQuery.append(" where cm_syscd=?               \n");
				                						strQuery.append("  and cm_closedt is null        \n");
				                						pstmt = conn.prepareStatement(strQuery.toString());
				                						pstmt.setString(1, SysCd);
				                						rs = pstmt.executeQuery();
				                						if (rs.next()) {
				                							wkRsrcCd = rs.getString("cm_rsrccd");
				                						}
				                						rs.close();
				                						pstmt.close();
				                					}
			                		    			if (wkRsrcCd != null && wkRsrcCd != "") {
				                		            	rst = new HashMap<String, String>();
					                					rst.put("cm_dirpath", wkDir);
					                					rst.put("filename", wkB);
					                					if (wkDsnCd != null && wkDsnCd != "") rst.put("cm_dsncd", wkDsnCd);
					                					if (wkRsrcCd != null && wkRsrcCd != "") rst.put("cm_rsrccd", wkRsrcCd);
					                					if (wkJobCd != null && wkJobCd != "") rst.put("cm_jobcd", wkJobCd);
					                					rsval.add(rst);
			                		    			}
			                		            }
			                		            break;
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
            }
            conn.close();
			rs = null;
			pstmt = null;
			conn = null;
    		returnObjectArray = rsval.toArray();
    		//ecamsLogger.debug(rsval.toString());
    		rsval = null;
    		return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0101.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0101.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0101.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement
	
	public Object[] getFileList_thread(String UserID,String SysCd,String SvrIp,String SvrPort,String HomeDir,String BaseDir,
			String SvrCd,String GbnCd,String exeName1,String exeName2,String SysInfo,String AgentDir,String SysOs,
			String buffSize,String svrInfo,String svrSeq) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		boolean           findSw      = false;
		boolean           fileSw      = false;
		boolean           ErrSw      = false;
		String            strFile     = "";
		int               i           = 0;
		int               svCnt       = 0;
		boolean           overSw      = false;
		int               ret = 0;
		String       	shFileName = "";
		String       	strParm = "";
		String       	svrHome = "";
		String       	sysHome = "";
		int             j = 0;
		
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			eCAMSInfo ecamsinf = new eCAMSInfo();
			String  noNameAry[] = ecamsinf.getNoName();
			
			conn = connectionContext.getConnection();
			String  strTmpPath  = "";
			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");
			
			Cmr0200 cmr0200 = new Cmr0200();
			shFileName = "filelist"+ UserID + "." + SysCd + ".ih.cs.sh";
			strFile = strTmpPath + "/filelist"+ UserID + "." + SysCd + ".ih.cs";
			strTmpPath = null;
			if (SysOs.equals("03")) {
				BaseDir = BaseDir.replace("/", "\\");
				BaseDir = BaseDir.replace("\\\\", "\\");
				BaseDir = BaseDir.replace("\\", "\\\\");
			}
			strParm = "./ecams_ih_cs " + SysCd + " " + SvrIp + " "  + SvrPort + " " + buffSize + " "+ BaseDir +  " filelist" + UserID + " " + GbnCd + " " + AgentDir;
			ret = cmr0200.execShell(shFileName, strParm, false);
			cmr0200 = null;
			//ecamsLogger.error("====return 2===="+Integer.toString(p.exitValue())+" \n");
			if (ret != 0) {
				throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다. run=["+strParm +"]" + " return=[" + ret + "]" );
			}
			
            if ( ErrSw == false ) {
				File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다 [작성된 파일 없음] ["+ strFile+"]");
		        } else {
			        BufferedReader in = null;
			        fileSw = true;
			        //PrintWriter out = null;

			        try {
			            //in = new BufferedReader(new FileReader(mFile));
			            in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"MS949"));
			            String str = null;
			            String wkF = "";
			            String wkB = "";
			            String wkDir = "";
			            String wkExe   = "";
			            ThreadPool pool = new ThreadPool(10);
			            String[] strExe1 = exeName1.split(",");
			            String[] strExe2 = exeName2.split(",");
			            int z = 0;
			            int k = 0;
			            if (svrInfo.substring(4,5).equals("1")) {
			            	svrHome = HomeDir.replace("##USER##", UserID);
			            	sysHome = HomeDir.replace("/##USER##","");
			            } else {
			            	svrHome = HomeDir;
			            	sysHome = HomeDir;
			            }
			            String baseSvr = "01";
			            
			            strQuery.setLength(0);
			            strQuery.append("select nvl(cm_dirbase,'01') dirbase \n");
			            strQuery.append("  from cmm0030                      \n");
			            strQuery.append(" where cm_syscd=?                   \n");
			            pstmt = conn.prepareStatement(strQuery.toString());
			            pstmt.setString(1, SysCd);
			            rs = pstmt.executeQuery();
			            if (rs.next()) {
			            	baseSvr = rs.getString("dirbase");
			            }
			            rs.close();
			            pstmt.close();
			            
			            while ( (str = in.readLine()) != null ) {
			                fileSw = false;
			                //ecamsLogger.error("+++++++str[00]:"+str);
			            	if (str.length() > 0) {
			            		//SysOs:03 = Windows
			                	if ( SysOs.equals("03") ) {
			                		if (str.indexOf("디렉터리")>0) {
			                			wkDir = str.substring(0,str.indexOf("디렉터리"));
			                			wkDir = wkDir.trim();
			                			do {
			                				wkDir = wkDir.replace("\\", "/");
			                			} while (wkDir.indexOf("\\")>=0);
			                		} else if (str.indexOf("<DIR>")<0 && !str.substring(0,1).equals(" ")) {
			                			//ecamsLogger.error("++++++++wkDir,str1++++"+wkDir + " " + str);
			                			for (k=0;4>k;k++) {
			                				if (str.indexOf(" ")>=0) {
			                					str = str.substring(str.indexOf(" ")).trim();
			                				} else str = "";
			                			}

			                			//ecamsLogger.error("++++++++wkDir,str2++++"+wkDir + " " + str);
			                			if (str.length()>0) {
				                			wkF = str;
				                			wkB = str;
				                			fileSw = true;
			                			}
			                		}
			                	} else if ( SysOs.equals("04") ) {//04=Linux
//			                		ecamsLogger.error("+++++++str++++++++[1]"+str);
//			                		if ( !str.substring(0,1).equals("d") ) {
				                		if ( str.substring(0,1).equals("/") ) {
				                			//ecamsLogger.error("[00]wkDir:"+wkDir); 
				                			wkDir = str.substring(0, str.length() - 1);
				                			//ecamsLogger.error("[01]wkDir:"+wkDir);
				                		} else if ( str.substring(0,1).equals("-") ) {
				                			wkF = str;
				                			int Y = 0;
				                			int X = 0;
				                			/*
				                			while (wkF.length() > 0) {
				                				Y = Y + 1;
				                				X = wkF.indexOf(" ");
				                				if (X >= 0) {
				                					wkB = wkF.substring(0,X).trim();
				                					wkF = wkF.substring(X).trim();
				                				} else {
				                					wkB = wkF.trim();
				                					wkF = "";
				                				}
				                				//ecamsLogger.error("[1]wkDir:"+wkDir+",wkB:"+wkB + ",Y:" + Y);
				                				if (Y == 8 && wkDir.length()>0 && wkB.length()>0) {
				                					//ecamsLogger.error("+++++++str,wkDir,wkB++++++++[2]"+wkDir+" "+wkB);
				                					fileSw = true;
				                					break;
				                				}
				                			}
				                			*/
				                			//20151112 neo. 마지막 탭을 파일명으로 사용하도록 로직 수정
				                			wkB = str.substring(str.lastIndexOf(" "));
//				                			ecamsLogger.error("+++++++wkB++++++++[2]"+wkB);
				                			fileSw = true;
				                		}
//			                		}
			                	} else {
			                		//ecamsLogger.error("+++++++str++++++++"+str);
			                		if (!str.substring(0,1).equals("d")) {
				                		if (str.substring(0,1).equals("/")) {
				                			wkDir = str.substring(0, str.length() - 1);
				                		} else if (str.substring(0,1).equals("-")) {
				                			wkF = str;
				                			int Y = 0;
				                			int X = 0;
				                			while (wkF.length() > 0) {
				                				Y = Y + 1;
				                				X = wkF.indexOf(" ");
				                				if (X >= 0) {
				                					wkB = wkF.substring(0,X).trim();
				                					wkF = wkF.substring(X).trim();
				                				} else {
				                					wkB = wkF.trim();
				                					wkF = "";
				                				}
				                				//ecamsLogger.error("+++++++str,wkDir,wkB++++++++[3]"+wkDir+" "+wkB);
				                				if (Y == 9 && wkDir.length()>0 && wkB.length()>0) {
				                					//ecamsLogger.error("+++++++str,wkDir,wkB++++++++[4]"+wkDir+" "+wkB);
				                					fileSw = true;
				                					break;
				                				}
				                			}
				                		}
			                		}
			                	}
			                	
			                	if ( fileSw ) {
                					findSw = false;
                					if (exeName1 == null || exeName1 == "") {
                						if (exeName2 == null || exeName2 == "") findSw = true;
                						else {
                							findSw = true;
                							wkExe = "";
                    						if (wkB.indexOf(".")>0) {
                    							wkExe = wkB.substring(wkB.lastIndexOf("."));
                    						}
    	                					for (z=0;strExe2.length>z;z++) {
    	                						if (strExe2[z] == null || strExe2[z].equals("")) {
    	                							if (wkExe == null || wkExe.equals("")) {
    		                							findSw = false;
    		                							break;
    	                							}
    	                						} else {
    	                							if (strExe2[z].equals(wkExe)) {
    		                							findSw = false;
    		                							break;
    		                						}
    	                						}
    	                					}
                						}
                					} else {
                						wkExe = "";
                						if (wkB.indexOf(".")>0) {
                							wkExe = wkB.substring(wkB.lastIndexOf("."));
                						}
	                					for (z=0;strExe1.length>z;z++) {
	                						if (strExe1[z] == null || strExe1[z].equals("")) {
	                							if (wkExe == null || wkExe.equals("")) {
		                							findSw = true;
		                							break;
	                							}
	                						} else {
	                							if (strExe1[z].equals(wkExe)) {
		                							findSw = true;
		                							break;
		                						}
	                						}
	                					}
                					}
                					if ( findSw ) {
	                					rst = new HashMap<String, String>();
	                					rst.put("syscd", SysCd);
	                					rst.put("cm_dirpath", wkDir.replace(svrHome, ""));
	                					rst.put("filename", wkB);
	                					if (baseSvr.equals(SvrCd)) {
	                						rst.put("svrchg", "N");
	                					} else {
	                						rst.put("svrchg", "Y");
	                						rst.put("basesvr", baseSvr);
	                						rst.put("svrcd", SvrCd);
	                						rst.put("svrseq", svrSeq);
	                					}
	                					if (svrInfo.substring(4,5).equals("1")) {	                						
	                						rst.put("dirpath", wkDir.replace(svrHome, sysHome));
	                					} else {
	                						rst.put("dirpath", wkDir);
	                					}
	                					for (j=0;noNameAry.length>j;j++) {
											if (wkDir.indexOf(noNameAry[j])>=0){
								            	rst.put("enable1", "0");
												rst.put("selected", "0");
												rst.put("error", "1");
												rst.put("errmsg", "경로명에 ["+noNameAry[j]+"] 존재");
												rst.put("cm_dirpath", rst.get("dirpath"));
												rst.put("dirpath", rst.get("cm_dirpath"));
												rsval.add(rst);
												findSw = false;
											}
										}
	                					if (findSw) {
	                						for (j=0;noNameAry.length>j;j++) {
												if (wkB.indexOf(noNameAry[j])>=0){
									            	rst.put("enable1", "0");
													rst.put("selected", "0");
													rst.put("error", "1");
													rst.put("errmsg", "파일명에 ["+noNameAry[j]+"] 존재");
													rst.put("cm_dirpath", rst.get("dirpath"));
													rst.put("dirpath", rst.get("cm_dirpath"));
													rsval.add(rst);
													findSw = false;
												}
	                						}
	                					}
	                					if (findSw) {
		                					++svCnt;
		                					//ecamsLogger.error("++++fileList+++++"+wkDir+", "+rst.toString());
	                						pool.assign( new svrOpen_thread_file(rst,rsval,conn) );
	                						if ( svCnt%10 == 0 ){
	                							Thread.sleep(30);
	                						}
	                						if ( rsval.size()>1000 ) {
	                							overSw = true;
	                							rst = null;
	                							break;
	                						}
	                					}
                						rst = null;
                					}
			                	}
			                }
			            }
			            if (svCnt>0) {
			            	pool.complete();
			            }
			        } finally {
			            if (in != null)
			                in.close();
			        }
		        }
		        // if (mFile.isFile() && mFile.exists()) mFile.delete();
            }
            
            conn.close();
            String wkExe1 = "";
            String wkExe2 = "";
            String wkB1 = "";
            String wkB2 = "";
            for (i=0;rsval.size()>i;i++) {
            	if (i >= rsval.size()) break;
            	if ( null != rsval.get(i).get("filename")  && "" != rsval.get(i).get("filename") ) {
	            	wkExe1 = "";
	            	wkB1 = rsval.get(i).get("filename");
					if (wkB1.indexOf(".")>0) {
						wkExe1 = wkB1.substring(wkB1.lastIndexOf("."));
						wkB1 = wkB1.substring(0,wkB1.lastIndexOf("."));
					} 
					//ecamsLogger.error("+++ base name ++"+wkB1+", "+ wkExe1);
					if (wkExe1.equals(".c") || wkExe1.equals(".pc")) {
		            	for (j=i+1;rsval.size()>j;j++) {
		            		if (j>=rsval.size()) break;
		            		if (!rsval.get(i).get("cm_dirpath").equals(rsval.get(j).get("cm_dirpath"))) break;
		            		wkExe2 = "";
		                	wkB2 = rsval.get(j).get("filename");
		    				if (wkB2.indexOf(".")>0) {
		    					wkExe2 = wkB2.substring(wkB2.lastIndexOf("."));
		    					wkB2 = wkB2.substring(0,wkB2.lastIndexOf("."));
		    				}
		    				//ecamsLogger.error("+++ same name ++"+wkB1+", "+ wkExe1+"==>"+wkB2+", "+ wkExe2);
		    				if (wkB1.equals(wkB2) && (wkExe2.equals(".c") || wkExe2.equals(".pc"))) {
		    					if (wkExe1.equals(".c")) {
		    						rsval.remove(i--);
		    						break;
		    					}
		    					if (wkExe2.equals(".c")) {
		    						rsval.remove(j);
		    						break;
		    					}
		    				}
		            	}
					}
            	} else {
            		rsval.remove(i--);
            	}
            }
            if ( overSw ) {
            	rst = new HashMap<String, String>();
				rst.put("cm_dirpath", "등록대상 파일의 갯수가 많아서 일부만 먼저 추출하였습니다. 등록하신 후 계속 추출하여 등록하시기 바랍니다.");
				rst.put("filename", "");
				rst.put("cm_dsncd", "");
				rst.put("enable1", "1");
				rst.put("selected", "0");
				rst.put("error", "W");
				rsval.add(0,rst);
				rst = null;
            }
            //ecamsLogger.error("++++ rsval ++++"+rsval.toString());
    		conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen.getFileList_thread() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen.getFileList_thread() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen.getFileList_thread() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen.getFileList_thread() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen.getFileList_thread() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList_thread() method statement


	public Object[] cmr0020_Insert_thread(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();
		Cmd0100 cmd0100 = new Cmd0100();
		HashMap<String, String> rst = null;
		HashMap<String, String> rst2 = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		try {
			conn = connectionContext.getConnection();
			rsval.clear();
	        String strDsnCd = "";
	        String strJobCd = "";
	        String strRsrcCd = "";
	        String strRsrcInfo = "";
	        String baseHome  = "";
	        String svrHome   = "";
	        String chgPath   = "";
	        int j = 0;
	        rtList.clear();
			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String, String>();
				rst = fileList.get(i);
				if (fileList.get(i).get("cm_dsncd") == null || fileList.get(i).get("cm_dsncd") == "") {
					if (fileList.get(i).get("rsrccd") != null && fileList.get(i).get("rsrccd") != "") strRsrcCd = fileList.get(i).get("rsrccd");
					else strRsrcCd = etcData.get("rsrccd");
					if (fileList.get(i).get("jobcd") != null && fileList.get(i).get("jobcd") != "") strJobCd = fileList.get(i).get("jobcd");
					else strJobCd = etcData.get("jobcd");
					if (fileList.get(i).get("cm_info") != null && fileList.get(i).get("cm_info") != "") strRsrcInfo = fileList.get(i).get("cm_info");
					else strRsrcInfo = etcData.get("cm_info");
					chgPath = fileList.get(i).get("cm_dirpath");
					if (etcData.get("basesvr") != null && etcData.get("basesvr") != "") {
						if (!etcData.get("basesvr").equals(etcData.get("svrcd"))) {
							strQuery.setLength(0);
			            	strQuery.append("select b.cm_volpath from cmm0031 a,cmm0038 b  \n");
			            	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd=?          \n");
			            	strQuery.append("   and a.cm_closedt is null                   \n");
			            	strQuery.append("   and a.cm_syscd=b.cm_syscd                  \n");
			            	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                  \n");
			            	strQuery.append("   and a.cm_seqno=b.cm_seqno                  \n");
			            	strQuery.append("   and b.cm_rsrccd=?                          \n");
			            	pstmt = conn.prepareStatement(strQuery.toString());
							//pstmt = new LoggableStatement(conn,strQuery.toString());
				            pstmt.setString(1, etcData.get("syscd"));
				            pstmt.setString(2, etcData.get("basesvr"));
				            pstmt.setString(3, strRsrcCd);
				            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				            rs = pstmt.executeQuery();
				            if (rs.next()) {
				            	baseHome = rs.getString("cm_volpath");
				            }
				            rs.close();
				            pstmt.close();
				            
			            	strQuery.setLength(0);
			            	strQuery.append("select b.cm_volpath from cmm0031 a,cmm0038 b  \n");
			            	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd=?          \n");
			            	strQuery.append("   and a.cm_seqno=?                           \n");
			            	strQuery.append("   and a.cm_closedt is null                   \n");
			            	strQuery.append("   and a.cm_syscd=b.cm_syscd                  \n");
			            	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                  \n");
			            	strQuery.append("   and a.cm_seqno=b.cm_seqno                  \n");
			            	strQuery.append("   and b.cm_rsrccd=?                          \n");
			            	pstmt = conn.prepareStatement(strQuery.toString());
							//pstmt = new LoggableStatement(conn,strQuery.toString());
				            pstmt.setString(1, etcData.get("syscd"));
				            pstmt.setString(2, etcData.get("svrcd"));
				            pstmt.setString(3, etcData.get("seqno"));
				            pstmt.setString(4, strRsrcCd);
				            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				            rs = pstmt.executeQuery();
				            if (rs.next()) {
				            	svrHome = rs.getString("cm_volpath");
				            }
				            rs.close();
				            pstmt.close();
				            chgPath = fileList.get(i).get("cm_dirpath").replace(svrHome, baseHome);
				            //ecamsLogger.error("+++++ svrHome,baseHome+++"+svrHome+", "+baseHome+", "+fileList.get(i).get("cm_dirpath")+", "+chgPath);
						}
					}
					strDsnCd = cmd0100.cmm0070_Insert(etcData.get("userid"),etcData.get("syscd"),fileList.get(i).get("filename"),strRsrcCd,strJobCd,chgPath,false,conn);
					rst.put("cm_dsncd", strDsnCd);

					if ((i + 2) < fileList.size()) {
						for (j=i+1;fileList.size()>j;j++) {
							if (fileList.get(j).get("cm_dsncd") == null || fileList.get(j).get("cm_dsncd") == "") {
								if (fileList.get(i).get("cm_dirpath").equals(fileList.get(j).get("cm_dirpath"))) {
									rst2 = new HashMap<String, String>();
									rst2 = fileList.get(j);
									rst2.put("cm_dsncd", strDsnCd);
									fileList.set(j, rst2);
								}
							}
						}
					}
				}
				rtList.add(rst);
			}//end of while-loop statement

			//ecamsLogger.error("+++++++++fileList+++"+rtList.toString());
			String retMsg = "";
			for (int i=0;i<rtList.size();i++){
				rst = new HashMap<String, String>();
				rst = rtList.get(i);
				if (fileList.get(i).get("rsrccd") != null && fileList.get(i).get("rsrccd") != "") strRsrcCd = fileList.get(i).get("rsrccd");
				else strRsrcCd = etcData.get("rsrccd");
				if (fileList.get(i).get("jobcd") != null && fileList.get(i).get("jobcd") != "") strJobCd = fileList.get(i).get("jobcd");
				else strJobCd = etcData.get("jobcd");
				if (fileList.get(i).get("cm_info") != null && fileList.get(i).get("cm_info") != "") strRsrcInfo = fileList.get(i).get("cm_info");
				else strRsrcInfo = etcData.get("cm_info");
				
				
				retMsg = statusCheck(etcData.get("syscd"),rtList.get(i).get("cm_dsncd"),rtList.get(i).get("filename"),etcData.get("userid"),conn);
	        	if (retMsg.equals("0")) {
	        		rst2 = new HashMap<String, String>();
		        	rst2.put("userid",etcData.get("userid"));
		        	rst2.put("syscd",etcData.get("syscd"));
		        	rst2.put("dsncd",rtList.get(i).get("cm_dsncd"));
		        	rst2.put("rsrcname",rtList.get(i).get("filename"));
		        	rst2.put("rsrccd",strRsrcCd);
		        	rst2.put("jobcd",strJobCd);
		        	rst2.put("story",rtList.get(i).get("story"));
		        	rst2.put("baseitem","");
		        	rst2.put("info",strRsrcInfo);
		        	rst2.put("srid", etcData.get("srid"));
		        	//rst2.put("todsn",etcData.get("todsn"));
		        	//rst2.put("scrno",etcData.get("scrno"));
		        	retMsg = cmd0100.cmr0020_Insert(rst2,conn);
		        	rst2 = null;

	        		if (retMsg.substring(0,1).equals("0")) {
	        			rst.put("cr_itemid", retMsg.substring(1));
	        			rst.put("baseitem", retMsg.substring(1));
	        			rst.put("cm_info",strRsrcInfo);
		        	} else {
		        		rst.put("error", "1");
		        		rst.put("errmsg", "등록실패");
		        	}
	        	} else {
	        		rst.put("error", "1");
	        		rst.put("errmsg", retMsg);
	        	}
			}//end of while-loop statement
			conn.close();
			conn = null;
			returnObjectArray =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return returnObjectArray;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_svr.cmr0020_Insert_thread() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_svr.cmr0020_Insert_thread() Exception END ##");
			throw exception;
		}finally{
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen_svr.cmr0020_Insert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0020_Insert_thread() method statement


	public String statusCheck(String SysCd, String DsnCd, String RsrcNm, String UserId,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		String wkB   = "0";

		try {
		    strQuery.append("select cr_status,cr_lstver,cr_editor from cmr0020 \n");
		    strQuery.append(" where cr_syscd = ? and cr_dsncd = ?              \n");
		    strQuery.append("   and cr_rsrcname = ?                            \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, SysCd);
            pstmt.setString(2, DsnCd);
            pstmt.setString(3, RsrcNm);

            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();

			if (rs.next()){
				if(rs.getInt("cr_lstver") > 0){
					wkB = "기 등록된 프로그램ID입니다.";
				} else if(rs.getString("cr_status").equals("3")){
					if(!UserId.equals(rs.getString("cr_editor"))){
						wkB = "다른개발자가 기 등록한 프로그램ID입니다.";
					}
				} else{
					wkB = "기 등록된 프로그램ID입니다.";
				}

			}//end of while-loop statement
			rs.close();
			pstmt.close();
            rs = null;
            pstmt = null;

			return wkB;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen.statusCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen.statusCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen.statusCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen.statusCheck() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of statusCheck() method statement


	/**
	 * getHomeDirList : cmm0031 과 cmm0038 테이블에서 homedir 리스트 조회
	 * @param String SysCd
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getHomeDirList(String UserId,String SysCd,String svrCd,String seqNo,String svrInfo,String svrHome) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String           userHome    = "";
		String           sysHome     = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			if (svrInfo.substring(4,5).equals("1")) {
				UserId = "k"+UserId;
				userHome = svrHome.replace("##USER##", UserId);
				sysHome = svrHome.replace("/##USER##", "");
			}
			strQuery.append("select a.cm_volpath             \n");
			strQuery.append("  from cmm0038 a,cmm0031 b,cmm0036 c \n");
			strQuery.append(" where b.cm_syscd=?             \n");			
			strQuery.append("   and b.cm_svrcd=?             \n");			
			strQuery.append("   and b.cm_seqno=?             \n");
			strQuery.append("   and b.cm_closedt is null     \n");
			strQuery.append("   and a.cm_volpath is not null \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd    \n");
			strQuery.append("   and b.cm_svrcd=a.cm_svrcd    \n");
			strQuery.append("   and b.cm_seqno=a.cm_seqno    \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd    \n");
			strQuery.append("   and a.cm_rsrccd=c.cm_rsrccd  \n");
			strQuery.append("   and substr(c.cm_info,45,1)='0'\n");
			strQuery.append("   and substr(c.cm_info,26,1)='0'\n");
			strQuery.append("   and substr(c.cm_info,27,1)='0'\n");
			strQuery.append("   and c.cm_rsrccd not in (select cm_samersrc from cmm0037 \n");
			strQuery.append("                            where cm_syscd=?               \n");
			strQuery.append("                              and cm_factcd='04')          \n");
			strQuery.append(" group by a.cm_volpath          \n");
			strQuery.append(" order by a.cm_volpath          \n");
			pstmt = conn.prepareStatement(strQuery.toString());
           	pstmt.setString(1, SysCd);
           	pstmt.setString(2, svrCd);
           	pstmt.setString(3, seqNo);
           	pstmt.setString(4, SysCd);
            rs = pstmt.executeQuery();
            rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				if (svrInfo.substring(4,5).equals("1")) {
					rst.put("cm_volpath", rs.getString("cm_volpath").replace(sysHome, userHome));
				} else {
					rst.put("cm_volpath", rs.getString("cm_volpath"));
				}
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen.getHomeDirList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen.getHomeDirList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen.getHomeDirList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen.getHomeDirList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen.getHomeDirList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getHomeDirList() method statement

	
	public Object[] getFileList_thread_MASTER(String SysName,String strTmpPath,String tDir,String exeName1,String exeName2,String SysOs,boolean fileMakeYN,String editor) throws SQLException, Exception {
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		boolean           findSw      = false;
		boolean           fileSw      = false;

		try {
			
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("임시파일생성위치값 누락");
			
			File               shfile = null;
			OutputStreamWriter writer = null;
			String[]           strAry = null;
			Runtime            run    = null;
			Process            p      = null;
			
			String            strShellName = "filelistUp." + SysName + ".sh";//ls 명령어를 실행할 쉘명
			String            strMakeFileName = "filelistUp." + SysName;//ls 결과 파일명
			String            strCMD = "ls -lLR " + tDir +  " > " + strTmpPath + "/" + strMakeFileName;//ls 명령어
			String            strShellFullPath = strTmpPath + "/" + strShellName;//ls 실행할 쉘의 경로+쉘명
			String            strMakeFileFullPath = strTmpPath + "/" + strMakeFileName;//임시경로 + ls결과파일명
				
			try {

				if ( fileMakeYN ) {
					shfile = new File(strShellFullPath);
					if( !(shfile.isFile()) )              //File존재여부
					{
						shfile.createNewFile();          //File생성
					}
					
					writer = new OutputStreamWriter( new FileOutputStream(strShellFullPath));
					writer.write("cd "+ tDir +"\n");
					writer.write(strCMD+"\n");
					writer.write("exit $?\n");
					writer.close();
					
					strAry = new String[3];
					strAry[0] = "chmod";
					strAry[1] = "777";
					strAry[2] = strShellFullPath;			
					run = Runtime.getRuntime();
					p = run.exec(strAry);
					p.waitFor();
					
					strAry = new String[2];
					strAry[0] = "/bin/sh";
					strAry[1] = strShellFullPath;
					run = Runtime.getRuntime();
					p = run.exec(strAry);
					p.waitFor();
	
					if (p.exitValue() != 0) {
						throw new Exception("ls명령어 실행 실패!!![쉘파일:"+strShellFullPath+"]");
					}
				}
				
			} catch (Exception exception) {
				exception.printStackTrace();
				ecamsLogger.error("## svrOpen.getFileList_thread_MASTER() Exception START ##");				
				ecamsLogger.error("## Error DESC : ", exception);	
				ecamsLogger.error("## svrOpen.getFileList_thread_MASTER() Exception END ##");				
				throw exception;
			}
			
			
			File mFile = new File(strMakeFileFullPath);
	        if (!mFile.isFile() || !mFile.exists()) {
				throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다 [작성된 파일 없음] ["+ strMakeFileFullPath+"]");
	        } else {
		        BufferedReader in = null;
		        fileSw = true;
		        //PrintWriter out = null;

		        try {
		        	
		        	
			        Connection        conn        ;
					PreparedStatement pstmt       ;
					ResultSet         rs          ;
					StringBuffer      strQuery    = new StringBuffer();

					ConnectionContext connectionContext = new ConnectionResource();
					conn = connectionContext.getConnection();
					
					
		            //in = new BufferedReader(new FileReader(mFile));
		            in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"MS949"));
		            String str = null;
		            String wkF = "";
		            String wkB = "";
		            String wkDir = "";
		            String wkExe   = "";
		            String[] strExe1 = exeName1.split(",");
		            String[] strExe2 = exeName2.split(",");
		            int z = 0;
		            int k = 0;
		            
		            while ((str = in.readLine()) != null) {
		                fileSw = false;
		                //ecamsLogger.error("+++++++str[00]:"+str);
		            	if (str.length() > 0) {
		            		//SysOs:03 = Windows
		                	if ( SysOs.equals("03") ) {
		                		if (str.indexOf("디렉터리")>0) {
		                			wkDir = str.substring(0,str.indexOf("디렉터리"));
		                			wkDir = wkDir.trim();
		                			wkDir = wkDir.substring(wkDir.indexOf(":")+1);
		                			do {
		                				wkDir = wkDir.replace("\\", "/");
		                			} while (wkDir.indexOf("\\")>=0);
		                		} else if (str.indexOf("<DIR>")<0 && !str.substring(0,1).equals(" ")) {
		                			//ecamsLogger.error("++++++++wkDir,str1++++"+wkDir + " " + str);
		                			for (k=0;4>k;k++) {
		                				if (str.indexOf(" ")>=0) {
		                					str = str.substring(str.indexOf(" ")).trim();
		                				} else str = "";
		                			}

		                			//ecamsLogger.error("++++++++wkDir,str2++++"+wkDir + " " + str);
		                			if (str.length()>0) {
			                			wkF = str;
			                			wkB = str;
			                			fileSw = true;
		                			}
		                		}
		                	} else if ( SysOs.equals("04") ) {//04=Linux
		                		//ecamsLogger.error("+++++++str++++++++"+str);
		                		if (!str.substring(0,1).equals("d")) {
			                		if (str.substring(0,1).equals("/")) {
			                			//ecamsLogger.error("[00]wkDir:"+wkDir); 
			                			wkDir = str.substring(0, str.length() - 1);
			                			//ecamsLogger.error("[01]wkDir:"+wkDir);
			                		} else if (str.substring(0,1).equals("-")) {
			                			wkF = str;
			                			int Y = 0;
			                			int X = 0;
			                			while (wkF.length() > 0) {
			                				Y = Y + 1;
			                				if ( Y != 8 ) {
				                				X = wkF.indexOf(" ");			                					
			                				} else {
			                					X = -1;
//			                					ecamsLogger.error("+++++++ wkF ++++++++:"+wkF);
			                				}
			                				if (X >= 0) {
			                					wkB = wkF.substring(0,X).trim();
			                					wkF = wkF.substring(X).trim();
			                				} else {
			                					wkB = wkF.trim();
			                					wkF = "";
			                				}
			                				//ecamsLogger.error("[1]wkDir:"+wkDir+",wkB:"+wkB + ",Y:" + Y);
			                				if (Y == 8 && wkDir.length()>0 && wkB.length()>0) {
//			                					ecamsLogger.error("+++++++str,wkDir,wkB++++++++[2]"+wkDir+" "+wkB);
			                					fileSw = true;
			                					break;
			                				}
			                			}
			                		}
		                		}
		                	} else {
		                		//ecamsLogger.error("+++++++str++++++++"+str);
		                		if (!str.substring(0,1).equals("d")) {
			                		if (str.substring(0,1).equals("/")) {
			                			wkDir = str.substring(0, str.length() - 1);
			                		} else if (str.substring(0,1).equals("-")) {
			                			wkF = str;
			                			int Y = 0;
			                			int X = 0;
			                			while (wkF.length() > 0) {
			                				Y = Y + 1;
			                				X = wkF.indexOf(" ");
			                				if (X >= 0) {
			                					wkB = wkF.substring(0,X).trim();
			                					wkF = wkF.substring(X).trim();
			                				} else {
			                					wkB = wkF.trim();
			                					wkF = "";
			                				}
			                				//ecamsLogger.error("+++++++str,wkDir,wkB++++++++[3]"+wkDir+" "+wkB);
			                				if (Y == 9 && wkDir.length()>0 && wkB.length()>0) {
			                					//ecamsLogger.error("+++++++str,wkDir,wkB++++++++[4]"+wkDir+" "+wkB);
			                					fileSw = true;
			                					break;
			                				}
			                			}
			                		}
		                		}
		                	}
		                	
		                	if ( fileSw ) {
            					findSw = false;
            					if (exeName1 == null || exeName1 == "") {
            						if (exeName2 == null || exeName2 == "") findSw = true;
            						else {
            							findSw = true;
            							wkExe = "";
                						if (wkB.indexOf(".")>0) {
                							wkExe = wkB.substring(wkB.lastIndexOf("."));
                						}
	                					for (z=0;strExe2.length>z;z++) {
	                						if (strExe2[z] == null || strExe2[z].equals("")) {
	                							if (wkExe == null || wkExe.equals("")) {
		                							findSw = false;
		                							break;
	                							}
	                						} else {
	                							if (strExe2[z].equals(wkExe)) {
		                							findSw = false;
		                							break;
		                						}
	                						}
	                					}
            						}
            					} else {
            						wkExe = "";
            						if (wkB.indexOf(".")>0) {
            							wkExe = wkB.substring(wkB.lastIndexOf("."));
            						}
                					for (z=0;strExe1.length>z;z++) {
                						if (strExe1[z] == null || strExe1[z].equals("")) {
                							if (wkExe == null || wkExe.equals("")) {
	                							findSw = true;
	                							break;
                							}
                						} else {
                							if (strExe1[z].equals(wkExe)) {
	                							findSw = true;
	                							break;
	                						}
                						}
                					}
            					}
            					
            					//ecamsLogger.error("##########         wkExe:"+ wkB.substring(wkB.lastIndexOf(".")) );
            					
            					if ( findSw ) {
                					rst = new HashMap<String, String>();
                					rst.put("cm_sysmsg", SysName);
                					rst.put("cm_jobname", SysName);
                					rst.put("cm_username", editor);
                					rst.put("cm_dirpath", wkDir);
                					rst.put("filename", wkB);
                					
                					if ( wkB.lastIndexOf(".")<0 ) {
                						
                						if ( wkB.toUpperCase().equals("MAKEFILE") ) {
                        					rst.put("rsrccdname", "Makefile");
                						}else {
                        					rst.put("rsrccdname", "기타");
                						}
                					} else {
                						wkExe = wkB.substring(wkB.lastIndexOf("."));
                						wkExe = wkExe.toUpperCase();
                						
                    					strQuery.setLength(0);
                    					strQuery.append("SELECT cm_codename,cm_micode FROM CMM0020_LANG \n");
                    				    strQuery.append(" WHERE upper ( cm_exename ) like ? \n");
                    			        pstmt = conn.prepareStatement(strQuery.toString());
                    			        //pstmt = new LoggableStatement(conn,strQuery.toString());
                    		            pstmt.setString(1, "%"+wkExe+"%" );
                    		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                    		            rs = pstmt.executeQuery();
                    					if ( rs.next() ){
                    						if ( rs.getString("cm_micode").equals("62") && wkB.indexOf("$")>0  ){//[62]class 이면서 파일명에 $가 있을경우 InnerClass 로 셋팅
                    							rst.put("rsrccdname", "InnerClass");
                    						}else {
                    							rst.put("rsrccdname", rs.getString("cm_codename") );
                    						}
                    					} else {
                    						rst.put("rsrccdname", "기타");
                    					}
                    					rs.close();
                    					pstmt.close();
                					}
                					rsval.add(rst);
            						rst = null;
            					}
		                	}
		                }
		            }

			        pstmt = null;
			        rs = null;
			        conn = null;
			        
		        } finally {
		            if (in != null)
		                in.close();
		        }
		        
	        }
	        // if (mFile.isFile() && mFile.exists()) mFile.delete();
	        
	        
    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen.getFileList_thread_MASTER() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen.getFileList_thread_MASTER() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen.getFileList_thread_MASTER() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen.getFileList_thread_MASTER() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)	rsval = null;
		}
	}//end of getFileList_thread_MASTER() method statement
	
}//end of svrOpen class statement
