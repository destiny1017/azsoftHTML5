/*****************************************************************************************
	1. program ID	: Cmd0101.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import app.common.AutoSeq;
import app.common.CodeInfo;
import app.common.CreateXml;
import app.common.LoggableStatement;
import app.common.StreamGobbler;
import app.common.UserInfo;
import app.common.eCAMSInfo;
import app.common.PrjInfo;
//import app.thread.ThreadPool;
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
public class Cmd1900{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

	public Object[] getSvrDir(String UserID,String SvrIp,String SvrPort,String AgentDir,String SysOs,String HomeDir) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
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
		String            makeFile    = "";
		String            strBaseDir  = "";
		int               upSeq       = 0;
		int               maxSeq      = 0;
		boolean           dirSw       = false;
		File shfile=null;
		//OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		ArrayList<Document> list = null;
		Object[] returnObjectArray = null;

		rsval.clear();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			strBinPath = ecamsinfo.getFileInfo("14");
			ErrSw = false;
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			try {

				shfile = new File(strTmpPath);

				if (!shfile.isFile()){
					shfile.mkdirs();
				}
				HomeDir = HomeDir.replace("\\", "/");

				shfile=null;
				makeFile = "dir" + UserID;
				strFile = strTmpPath + makeFile;
				shfile = new File(strTmpPath + makeFile+".sh");              //File 불러온다.
				if( !(shfile.isFile()) )              //File이 없으면
				{
					shfile.createNewFile();          //File 생성
				}
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(strTmpPath + makeFile+".sh"),"MS949"));
				writer.write("cd "+strBinPath +"\n");
				//\"\"
				writer.write("./ecams_dir " + SvrIp + " " + SvrPort + " \"" + HomeDir + "\" " + makeFile +"\n");
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
				StreamGobbler outgb = new StreamGobbler(p.getInputStream());
				StreamGobbler errgb = new StreamGobbler(p.getErrorStream());

				outgb.start();
				errgb.start();
				p.waitFor();

				//ecamsLogger.debug("====return===="+Integer.toString(p.exitValue())+" \n");
				if (p.exitValue() != 0) {
					if (p.exitValue() == 1) {
						throw new Exception("추출 디렉토리가 없습니다");
					}else if (p.exitValue() == 2) {
						throw new Exception("디렉토리추출을 위한 분석작업 실패하였습니다");
					}else if (p.exitValue() == 3) {
						throw new Exception("해당서버에서 Tmp파일 삭제  실패하였습니다");
					}

					ErrSw = true;

				}
				else{
					//shfile.delete();
				}
				outgb = null;
				errgb = null;
			} catch (Exception e) {
				throw new Exception(e);
			}
			conn = connectionContext.getConnection();
			//strFile = "c:\\eCAMS\\dir"+UserID;
            if (ErrSw == false) {
            	if (HomeDir.indexOf(":") >= 0) {
            		HomeDir = HomeDir.substring(HomeDir.indexOf(":")+1);
        		}
            	HomeDir = HomeDir.replace("\\", "");
    			if (HomeDir.substring(HomeDir.length()-1).equals("/")) HomeDir = HomeDir.substring(0,HomeDir.length()-1);
				File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("디렉토리추출을 위한 작업에 실패하였습니다 [작성된 파일 없음]");
		        } else {
			        BufferedReader in = null;
			        //fileSw = true;
			        //PrintWriter out = null;

			        try {
			            //in = new BufferedReader(new FileReader(mFile));
			            in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"MS949"));
			            ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");
			            String str = null;
			            //strBaseDir = BaseDir;

						maxSeq = maxSeq + 1;

						//ecamsLogger.error("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
						rst = new HashMap<String,String>();
						rst.put("cm_dirpath","HomeDir_"+ HomeDir);
						rst.put("cm_fullpath",HomeDir);
						rst.put("cm_upseq","0");
						rst.put("cm_seqno",Integer.toString(maxSeq));
						rsval.add(maxSeq - 1, rst);
						upSeq = maxSeq;
						String wkDir = "";
//						String wkBaseDir = "";
						String wkHome = "";
						String wkDirDepth = "";
						int depTh = 5;
			            while ((str = in.readLine()) != null) {
			            	wkDir = "";
			                if (str.length() > 0) {
			                	dirSw = false;
			                	if (SysOs.equals("03")) {
			                		str = str.trim();
			                		str = str.replace("\\", "/");
			                		if (str.indexOf(":") >= 0) {
			                			str = str.substring(str.indexOf(":")+1);
			                			dirSw = true;
			                		}

			                		if (str.indexOf("디렉터리")>0 && dirSw == true) {
			                			//ecamsLogger.error("+++++++str,basedir+++++++++"+str+", "+ BaseDir);
			                			wkHome = HomeDir;

			                			strBaseDir = str.substring(0,str.indexOf("디렉터리"));
			                			strBaseDir = strBaseDir.trim();

			                			//ecamsLogger.error("+++++++str,basedir,homedir+++++++++"+strBaseDir+", "+ wkBaseDir+", "+wkHome);
			                			if (strBaseDir.substring(0,wkHome.length()).equals(wkHome)) {
				                			str = strBaseDir;
				                			dirSw = true;
				                			wkDir = "";
				                			if (strBaseDir.length() > wkHome.length()) {
					                			wkDirDepth = strBaseDir.substring(wkHome.length());
					                			if (wkDirDepth.substring(0,1).equals("/")) wkDirDepth = wkDirDepth.substring(1);
					                			for (int k=0;depTh>k;k++) {
					                				//ecamsLogger.error("[wkDirDepth,wkDir]=>"+wkDirDepth+", "+wkDir);
					                				if (wkDirDepth.indexOf("/")>0) {
					                					wkDir = wkDir + wkDirDepth.substring(0,wkDirDepth.indexOf("/")+1);
					                					wkDirDepth = wkDirDepth.substring(wkDirDepth.indexOf("/")+1);
					                				} else {
					                					wkDir = wkDir + wkDirDepth;
					                					break;
					                				}
					                			}
					                			wkDir = wkHome + "/" + wkDir;
					                			if (wkDir.equals(wkDirDepth)) dirSw = false;
				                			} else dirSw = false;
			                			}
			                		} else {
			                			dirSw = false;
			                		}

			                	} else {
				                	if (str.substring(str.length() - 1).equals(":")) {
				                		strBaseDir = str.substring(0,str.length() - 1);
				                		//if (!strBaseDir.substring(strBaseDir.length() - 1).equals("/")) strBaseDir = strBaseDir + "/";
				                		str = strBaseDir;
				                		dirSw = true;
				                	}
			                	}
			                	if (dirSw == true) {
			                		wkDirDepth = wkDir;
			                		//ecamsLogger.error("+++++++str1,wkHome+++++++++"+str+", "+wkHome);
			                		if (wkHome.length() < wkDir.length()){
			                			wkDir = wkDir.substring(wkHome.length()+1);
				                		//ecamsLogger.error("+++++++str2+++++++++"+str);
				                		if (wkDir.length() != 0 ) {
					                		//ecamsLogger.error("+++++++str3+++++++++"+str);
				                			if (wkDir.substring(0,1).equals("/")) str = wkDir.substring(1);
					                		pathDepth = wkDir.split("/");
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
														for (int j = 0;rsval.size() > j;j++) {
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
		        //if (mFile.isFile() && mFile.exists()) mFile.delete();
            }
            //ecamsLogger.error("+++++++rsval+++++++"+rsval.toString());
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
    		returnObjectArray = list.toArray();
    		conn.close();
    		conn = null;
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
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen_svr.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getDirPath() method statement

	public Object[] getFileList(String UserID,ArrayList<HashMap<String, String>> dirList,String SvrIp,String SvrPort,String HomeDir,String SvrCd,String AgentDir,String SysOs) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		boolean           ErrSw      = false;
		String            strBinPath  = "";
		String            strTmpPath  = "";
		String            strFile     = "";
		String            makeFile    = "";
		String            strPrjNo    = "";
		String            strPrjName  = "";
		String            BaseDir     = "";
		int               i           = 0;
		String            wkExe       = "";
		//ecamsLogger.error("++++getfilelist start1+++++");
		File shfile=null;
		//OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		//ecamsLogger.error("++++getfilelist start2+++++");
		ConnectionContext connectionContext = new ConnectionResource();
		rsval.clear();

		try {
			strBinPath = ecamsinfo.getFileInfo("14");
			ErrSw = false;
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");
			//ecamsLogger.error("++++strDrive+++++"+strDrive);
			conn = connectionContext.getConnection();
			for (i=0;dirList.size()>i;i++) {
				strPrjNo = "";
				strPrjName = dirList.get(i).get("cm_dirpath");
				if (strPrjName.indexOf(".") > 0) strPrjName = strPrjName.substring(strPrjName.indexOf(".")+1);
				strQuery.setLength(0);
				strQuery.append("select cd_prjno,cd_prjname from cmd0300     \n");
				strQuery.append(" where cd_eddate is null                    \n");
				strQuery.append("   and cd_closedt is null                   \n");
				strQuery.append("   and replace(cd_prjname,' ','')=replace(?,' ','')\n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, strPrjName);
				//ecamsLogger.error(new String((((LoggableStatement)pstmt).getQueryString()).getBytes("MS949")));

				rs = pstmt.executeQuery();
				if (rs.next()) {
					strPrjNo = rs.getString("cd_prjno");
					strPrjName = rs.getString("cd_prjname");
				}
				rs.close();
				pstmt.close();

				if (strPrjNo.length()== 0) {
					ecamsLogger.error("++++++PrjNo not exist+++++"+ new String(dirList.get(i).get("cm_dirpath").getBytes("MS949")));

				} else {
					try {
						makeFile = "file" + strPrjNo + UserID.replace("*", "S");
						strFile = strBinPath + makeFile + "." + strPrjNo + ".ih.cs";

						shfile=null;
						shfile = new File(strTmpPath + makeFile+".sh");              //File 불러온다.

						if( !(shfile.isFile()) )              //File이 없으면
						{
							shfile.createNewFile();          //File 생성
						}
						BaseDir = HomeDir.substring(0,HomeDir.indexOf("\\")) + dirList.get(i).get("cm_fullpath").replace("\\","/");
						BaseDir = BaseDir.replace("//", "/");
						//if (SysOs.equals("03")) BaseDir = AgentDir + BaseDir;
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(strTmpPath + makeFile+".sh"),"MS949"));
						writer.write("cd "+strBinPath +"\n");
						writer.write("./ecams_ih_cs " + strPrjNo + " " + SvrIp + " \""+ BaseDir + "\" "  + SvrPort + " " + makeFile + " 9 \n");
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
							throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다 ");
						}
						else{
							//shfile.delete();
						}
					} catch (Exception e) {
						throw new Exception(e);
					}

		            if (ErrSw == false) {
						File mFile = new File(strFile);
				        if (!mFile.isFile() || !mFile.exists()) {
							ErrSw = true;
							throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다 [작성된 파일 없음]");
				        } else {
					        BufferedReader in = null;
					        //PrintWriter out = null;

					        try {
					            //in = new BufferedReader(new FileReader(mFile));
					            in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"MS949"));
					            String str = null;
					            String wkF = "";
					            String wkB = "";
					            String wkDir = "";
					            String wkSvrDir = "";
					            String wkDevStep = "";
					            String wkMethCd = "";
					            String wkUpSeq = "";
					            String wkSeq = "";
					            String wkC = "";
					            boolean findSw = false;
					            PrjInfo prjinfo = new PrjInfo();
					            while ((str = in.readLine()) != null) {
					            	wkF = "";
					            	findSw = false;
					            	wkUpSeq = "";
					            	wkSeq = "";
					            	wkMethCd = "06";

					                if (str.length() > 0) {
					                	//ecamsLogger.error("+++ str ++++"+str+"/n");
					                	if (SysOs.equals("03")) {
					                		if (str.indexOf("디렉터리")>0 && str.indexOf("\\")>0) {
					                			wkDir = str.substring(0,str.indexOf("디렉터리")).trim();
					                			if (wkDir.equals(BaseDir.replace("/", "\\"))) wkDir = "";
					                			else {
						                			wkDir = wkDir.substring(BaseDir.length()+1);
						                			wkSvrDir = wkDir;
						                			if (wkDir.indexOf("\\")>0) wkB = wkDir.substring(0,wkDir.indexOf("\\"));
						                			else wkB = wkDir;

						                			if (wkB.indexOf(".")>0) {
						                				wkSeq = wkB.substring(0,wkB.indexOf("."));
						                				wkB = wkB.substring(wkB.indexOf(".")+1);
						                			}
						                			wkB = wkB.trim();

						                			if (wkDir.indexOf("\\")>0) wkDir = wkB + wkDir.substring(wkDir.indexOf("\\"));
						                			else wkDir = wkB;

						                			strQuery.setLength(0);
						                			strQuery.append("select a.cm_micode from cmm0020 a             \n");
						                			strQuery.append(" where a.cm_macode='DEVSTEP'                  \n");
						                			strQuery.append("   and replace(a.cm_codename,' ','')=replace(?,' ','') \n");
						                			pstmt = conn.prepareStatement(strQuery.toString());
						                			//pstmt = new LoggableStatement(conn,strQuery.toString());
						                			pstmt.setString(1, wkB);
						                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						                			rs = pstmt.executeQuery();
						                			if (rs.next()) {
						                				wkDevStep = rs.getString("cm_micode");
						                			} else {
						                				wkDevStep = "";
						                			}
						                			rs.close();
						                			pstmt.close();

						                			if (wkDevStep.length() == 0) {
						                				strQuery.setLength(0);
						                				strQuery.append("insert into cmm0020 (cm_macode,cm_micode,cm_codename, \n");
						                				strQuery.append("  cm_creatdt,cm_lastupdt,cm_seqno)                    \n");
						                				strQuery.append("(select 'DEVSTEP',max(cm_micode)+1,?,SYSDATE,SYSDATE,0\n");
						                				strQuery.append("   from cmm0020                                       \n");
						                				strQuery.append("  where cm_macode='DEVSTEP' and cm_micode<>'****')    \n");
						                				pstmt = conn.prepareStatement(strQuery.toString());
						                				//pstmt = new LoggableStatement(conn,strQuery.toString());
							                			pstmt.setString(1, wkB);
							                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							                			pstmt.executeUpdate();

							                			strQuery.setLength(0);
							                			strQuery.append("select a.cm_micode from cmm0020 a             \n");
							                			strQuery.append(" where a.cm_macode='DEVSTEP'                  \n");
							                			strQuery.append("   and a.cm_codename=?                        \n");
							                			pstmt = conn.prepareStatement(strQuery.toString());
							                			//pstmt = new LoggableStatement(conn,strQuery.toString());
							                			pstmt.setString(1, wkB);
							                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							                			rs = pstmt.executeQuery();
							                			if (rs.next()) {
							                				wkDevStep = rs.getString("cm_micode");
							                			} else {
							                				wkDevStep = "";
							                			}
							                			rs.close();
							                			pstmt.close();
						                			}

						                			if (wkDevStep.length()>0) {
						                				findSw = true;
							                			strQuery.setLength(0);
							                			strQuery.append("select count(*) cnt from cmd0301 b            \n");
							                			strQuery.append(" where b.cd_prjno=?                           \n");
							                			strQuery.append("   and b.cd_devstep=?                         \n");
							                			pstmt = conn.prepareStatement(strQuery.toString());
							                			//pstmt = new LoggableStatement(conn,strQuery.toString());
							                			pstmt.setString(1, strPrjNo);
							                			pstmt.setString(2, wkDevStep);
							                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							                			rs = pstmt.executeQuery();
							                			if (rs.next()) {
							                				if (rs.getInt("cnt") == 0) {
							                					findSw = false;
							                				}
							                			}
							                			rs.close();
							                			pstmt.close();

							                			if (findSw == false) {
							                				strQuery.setLength(0);
								                			strQuery.append("select cd_methcd from cmd0300  \n");
								                			strQuery.append(" where cd_prjno=?              \n");
								                			pstmt = conn.prepareStatement(strQuery.toString());
								                			//pstmt = new LoggableStatement(conn,strQuery.toString());
								                			pstmt.setString(1, strPrjNo);
								                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
								                			rs = pstmt.executeQuery();
								                			if (rs.next()) {
								                				wkMethCd = rs.getString("cd_methcd");
								                			}
								                			rs.close();
								                			pstmt.close();
								                			//CD_PRJNO,CD_DOCCD,CD_BASECD,CD_DEFYN,CD_METHCD,CD_DEVSTEP,CD_CCBYN,CD_CREATDT,CD_LASTDT,CD_EDITOR,CD_SEQNO
							                				strQuery.setLength(0);
								                			strQuery.append("insert into cmd0301                             \n");
								                			strQuery.append("  (CD_PRJNO,CD_METHCD,CD_DEVSTEP,CD_CREATDT,    \n");
								                			strQuery.append("         CD_LASTDT,CD_EDITOR,CD_SEQNO)          \n");
								                			strQuery.append(" (select ?,?,?,SYSDATE,SYSDATE,?,               \n");
								                			strQuery.append("         nvl(max(CD_SEQNO),0)+1                 \n");
								                			strQuery.append("    from cmd0301                                \n");
								                			strQuery.append("   where cd_prjno=?)                            \n");
								                			pstmt = conn.prepareStatement(strQuery.toString());
								                			//pstmt = new LoggableStatement(conn,strQuery.toString());
								                			pstmt.setString(1, strPrjNo);
								                			pstmt.setString(2, wkMethCd);
								                			pstmt.setString(3, wkDevStep);
								                			pstmt.setString(4, UserID);
								                			pstmt.setString(5, strPrjNo);
								                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
								                			pstmt.executeUpdate();
								                			pstmt.close();
							                			} else if (wkSeq.length()>0) {
							                				try {
								                				strQuery.setLength(0);
								                				strQuery.append("update cmd0301 set cd_seqno=?        \n");
								                				strQuery.append(" where cd_prjno=? and cd_devstep=?   \n");
								                				pstmt = conn.prepareStatement(strQuery.toString());
								                				//pstmt = new LoggableStatement(conn,strQuery.toString());
									                			pstmt.setInt(1, Integer.parseInt(wkSeq));
									                			pstmt.setString(2, strPrjNo);
									                			pstmt.setString(3, wkDevStep);
									                			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
									                			pstmt.executeUpdate();
								                				pstmt.close();
							                				} catch (SQLException sqlexception) {
							                				} catch (Exception exception) {
							                				}
							                			}
						                			}

					                			}
					                		} else if (str.indexOf("<DIR>")<0 && !str.substring(0,1).equals(" ")) {
					                			wkF = str;
					                			int Y = 0;
					                			int X = 0;
					                			while (wkF.length() > 0) {
					                				Y = Y + 1;
					                				X = wkF.indexOf(" ");
					                				if (X >= 0) {
					                					wkF = wkF.substring(X).trim();
					                				} else {
					                					wkB = wkF.trim();
					                					wkF = "";
					                				}
					                				if (Y == 4) break;
					                			}
					                		}
					                	} else {
					                		if (!str.substring(0,1).equals("d")) {
						                		if (str.substring(0,1).equals("/")) {
						                			wkDir = str.substring(0, str.length() - 1);
				                					if (wkDir.substring(0,BaseDir.length()).equals(BaseDir)) {
				                						wkDir = wkDir.substring(BaseDir.length());
				                						if (wkDir.length() > 0) {
					                						if (!wkDir.substring(0,1).equals("/")) {
					                							wkDir = "/" + wkDir;
					                						}
				                						} else wkDir = "/";
				                					}
				                					wkDir = wkDir.trim();
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
						                					wkF = wkB;
						                					break;
						                				}
						                			}
						                		}
					                		}
					                	}

					                	//ecamsLogger.error("+++ wkDir,wkF ++++"+wkDir+","+wkF);
					                	if (wkF.length()>0 && wkDir.length()>0) {

					                		rst = new HashMap<String, String>();
					                		rst.put("cd_prjno", strPrjNo);
					                		rst.put("cd_prjname", strPrjName);
		                					rst.put("fullpath", wkDir);
		                					rst.put("basedir", BaseDir);
		                					rst.put("pcdir", BaseDir.replace("/", "\\") + "\\" + wkSvrDir);
		                					rst.put("cr_pcfile", wkF);
		                					wkB = wkF;
		                					if (wkB.lastIndexOf(".")>0) {
		                						wkExe = wkB.substring(wkB.lastIndexOf("."));
		                						wkB = wkB.substring(0,wkB.lastIndexOf("."));

		                						if (wkB.lastIndexOf("_v")>0 && wkB.lastIndexOf("_v")>(wkB.length()-10)) {
		                							wkB = wkB.substring(0,wkB.lastIndexOf("_v")) + wkExe;
		                						} else wkB = wkB + wkExe;
		                						wkB = wkB.trim();
		                					}
		                					rst.put("cr_docfile", wkB);
		                					rst.put("selected", "1");
		                					rst.put("ccbyn", "Y");
		                					rst.put("colorsw", "0");
		                					rst.put("cr_devstep", wkDevStep);
		                					wkC = BaseDir.replace("/", "\\") + "\\" + wkSvrDir;
		                					if (wkC.indexOf(",")>0) rst.put("colorsw", "3");
		                					else {
		                						wkC = wkF;
			                					if (wkC.indexOf(",")>0) rst.put("colorsw", "3");
		                					}
		                					rst.put("selected", "1");
		                					//ecamsLogger.error("+++++BaseDir,wkDir++++++"+BaseDir+", "+wkDir);
		                					wkUpSeq = prjinfo.chkDocseq(strPrjNo, BaseDir, wkDir, false, conn);

		                					if (wkUpSeq != null && wkUpSeq != "") {
		                						rst.put("cr_docseq", wkUpSeq);
		                						findSw = false;
			                					strQuery.setLength(0);
			                					strQuery.append("select a.cr_docid,a.cr_lstver,a.cr_ccbyn,b.cm_codename,c.cr_docseq,a.cr_devstep  \n");
			                					strQuery.append("  from cmm0020 b,cmr0030 a,cmr0031 c  \n");
			                		            strQuery.append(" where c.cr_prjno=? and c.cr_docseq=? \n");//PrjNo
			                	                strQuery.append("   and c.cr_docid=a.cr_docid          \n");//DocFile
			                	                strQuery.append("   and a.cr_docfile = ?               \n");//DocFile
			                	                strQuery.append("   and b.cm_macode='DEVSTEP'          \n");//
			                	                strQuery.append("   and b.cm_micode=a.cr_devstep       \n");//
			                	                pstmt = conn.prepareStatement(strQuery.toString());
			                	                //pstmt = new LoggableStatement(conn,strQuery.toString());

			                	                pstmt.setString(1, strPrjNo);
			                	                pstmt.setString(2, wkUpSeq);
			                		            pstmt.setString(3, wkB);
			                		           //ecamsLogger.error(new String((((LoggableStatement)pstmt).getQueryString()).getBytes("MS949")));
			                		            rs = pstmt.executeQuery();
			                		            if (rs.next()) {
			                		            	findSw = true;
			                		            	rst.put("cr_docid", rs.getString("cr_docid"));
			                		            	rst.put("cr_docseq", rs.getString("cr_docseq"));
			                		            	rst.put("devstep", rs.getString("cm_codename"));
			                		            	if (rs.getString("cr_ccbyn") == null)
			                		            		rst.put("ccbyn","Y");
			                		            	else
			                		            		rst.put("ccbyn",rs.getString("cr_ccbyn"));
			                		            	rst.put("cr_devstep",rs.getString("cr_devstep"));
			                		            	if (rs.getInt("cr_lstver")>0) {
			                		            		rst.put("reqname", "수정");
			                		            		rst.put("reqcd", "04");
			                		            	} else {
			                		            		rst.put("reqname", "신규");
			                		            		rst.put("reqcd", "03");
			                		            	}
			                		            	//ecamsLogger.error("+++++cr_docid+++++"+rs.getString("cr_docid"));
			                		            }
			                		            rs.close();
			                		            pstmt.close();
		                					}

		                					if (findSw == false) {
		                						if (wkDir.indexOf("\\")>0)
		                							rst.put("devstep", wkDir.substring(0,wkDir.indexOf("\\")));
		                						else
		                							rst.put("devstep", wkDir);
		                		            	rst.put("reqname", "신규");
		            		            		rst.put("reqcd", "03");
		                					}

		                					rsval.add(rst);
					                	}
					                }
					            }
					        } finally {
					            if (in != null)
					                in.close();
					        }
				        }

				      //  if (mFile.isFile() && mFile.exists()) mFile.delete();
		            }
				}
			}

    		conn.close();
    		conn = null;

    		return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen_svr.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen_svr.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_svr.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_svr.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen_svr.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getFileList() method statement
	public Object[] dirOpenChk(String PrjNo,String devHome,ArrayList<String> dirList)	throws SQLException, Exception {
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
			String              fullPath = "";

			conn = connectionContext.getConnection();

			rsval.clear();
			for (i=0;dirList.size()>i;i++) {
				strPath1 = dirList.get(i);
				strPath1 = strPath1.replace(devHome, "");
				if (strPath1.substring(0,1).equals("/")) strPath1 = strPath1.substring(1);
				if (strPath1 != "" && strPath1 != null) {
					String strWork[] = strPath1.split("/");

					for (j=0 ; strWork.length>j ; j++){
						if (j == 0) fullPath = strWork[j];
						else fullPath = fullPath + "\\" + strWork[j];
						strQuery.setLength(0);
						strQuery.append("select cd_docseq from cmd0303              \n");
						strQuery.append(" where cd_prjno=? and cd_updocseq=?        \n");
						if (j == 0) strQuery.append("and cd_updocseq is null        \n");
						else strQuery.append("and cd_updocseq=?                     \n");
						strQuery.append("   and cd_dirname=?                        \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
			            pstmt.setString(1, PrjNo);
			            if (j > 0)pstmt.setString(2, strUpSeq);
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
				            	strQuery.append("   CD_DIRNAME,CD_UPDOCSEQ,CD_DIRPATH) values        \n");
				            	strQuery.append("   (?, ?, ?, ?, ?)         \n");
				            	pstmt2 = conn.prepareStatement(strQuery.toString());
				            	pstmt2.setString(1, PrjNo);
				            	pstmt2.setString(2, strSeq);
				            	pstmt2.setString(3, strWork[j]);
				            	pstmt2.setString(4, strUpSeq);
				            	pstmt2.setString(5, fullPath);
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
	public Object[] fileOpenChk(String PrjNo,ArrayList<HashMap<String,String>> fileList,String strDevHome,String UserId)
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

			for (i=0;fileList.size()>i;i++) {
				String[] strWork = fileList.get(i).get("cr_fullpath").split("/");
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
	            pstmt.setString(2, fileList.get(i).get("cr_docseq"));
	            pstmt.setString(3, fileList.get(i).get("cr_docfile"));
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	rst = new HashMap<String, String>();
	            	rst.put("cr_devstep", rs.getString("cr_devstep"));
	            	rst.put("cr_docseq", rs.getString("cr_docseq"));
	        		rst.put("devstep", strWork[1]);
	        		rst.put("docfile", fileList.get(i).get("cr_docfile"));
	        		rst.put("fullpath", fileList.get(i).get("cr_fullpath"));
	        		rst.put("checked",rs.getString("cr_ccbyn"));
	        		rsval.add(rst);
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
	        		rsval.add(rst);
	            }
	            rst = null;
	            rs.close();
	            pstmt.close();
	            conn.close();

	            rs2 = null;
	            pstmt2 = null;
	            rs = null;
	            pstmt = null;
	            conn = null;
			}
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

	public Object request_Check_In(ArrayList<HashMap<String,String>> chkInList,
			HashMap<String,String> dataObj,
			ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		String			  AcptNo	  = null;
		int				  i			  = 0;
		int				  paramIndex  = 0;
		HashMap<String,String>	rData = null;
		ArrayList<HashMap<String,String>> rsval = new ArrayList<HashMap<String,String>>();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			PrjInfo prjinfo = new PrjInfo();

			String UserId = dataObj.get("UserId");
			String SinCd = dataObj.get("SinCd");//반입코드 = 34
			String Sayu = dataObj.get("Sayu");
			String SysCd = dataObj.get("SysCd");
			String JobCd = dataObj.get("JobCd");
			String SysGb = dataObj.get("SysGb");
	    	//String CCB_YN = dataObj.get("CCB_YN");
	    	String ReqCd = "";

	    	String DocID_NUM = "";
	    	String DocSeq = "";
	    	String MethCd = "";
	    	String PrjNo = "";
	    	String PrjName = "";
	    	String errMsg = "";
	    	boolean findSw = false;
	    	int    lstVer = 0;
	    	rsval.clear();

	    	for (i=0;chkInList.size()>i;i++) {
	    		findSw = false;
	    		errMsg = "";
	    		if (PrjNo.length() == 0) findSw = true;
	    		else if (!PrjNo.equals(chkInList.get(i).get("cd_prjno"))) findSw = true;

	    		if (findSw == true) {
	    			MethCd = "";
	    			PrjNo = chkInList.get(i).get("cd_prjno");
	    			PrjName = chkInList.get(i).get("cd_prjname");
	    			strQuery.setLength(0);
		        	strQuery.append("select distinct cd_methcd from cmd0301 ");
		            strQuery.append("where cd_prjno = ? ");//PrjNo
		            pstmt = conn.prepareStatement(strQuery.toString());
		            pstmt.setString(1,PrjNo);
		            rs = pstmt.executeQuery();
		            if (rs.next()){
		            	MethCd = rs.getString("cd_methcd") ;
		            }else {
		            	errMsg = "프로젝트 개발방법론 등록 무";
		            }
		        	rs.close();
		        	pstmt.close();
	    		} else {
	    			if (MethCd.length() == 0) {
	    				errMsg = "프로젝트 개발방법론 등록 무";
	    			}
	    		}
	    		if (errMsg.length()>0) {
	    			chkInList.get(i).put("errMsg",errMsg);
	    			rData = new HashMap<String,String>();
	    			rData = chkInList.get(i);
	    			rData.put("errMsg", errMsg);
	    			rsval.add(rData);
	    		} else {
	    			chkInList.get(i).put("errMsg","");
	    			DocID_NUM = "";
		        	DocSeq = "";
		        	ReqCd = "";
		        	if (chkInList.get(i).get("cr_docseq") != null && chkInList.get(i).get("cr_docseq") != "") {
		        		DocSeq = chkInList.get(i).get("cr_docseq");
			        	strQuery.setLength(0);
			        	strQuery.append("select a.cr_docid,a.cr_lstver from cmr0030 a,cmr0031 b \n");
			            strQuery.append("where b.cr_prjno=? and b.cr_docseq=?    \n");//PrjNo
		                strQuery.append("  and b.cr_docid=a.cr_docid             \n");//DocFile
		                strQuery.append("  and a.cr_docfile=?                    \n");//DocFile
		                pstmt = conn.prepareStatement(strQuery.toString());
		                //pstmt = new LoggableStatement(conn,strQuery.toString());
		                paramIndex = 0;
		                pstmt.setString(++paramIndex,PrjNo);
		                pstmt.setString(++paramIndex,DocSeq);
		                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_docfile"));
		                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		                rs = pstmt.executeQuery();
		                if (rs.next()){
		                	DocID_NUM = rs.getString("cr_docid");
		                	if (rs.getInt("cr_lstver")== 0) {
		                		ReqCd = "03";
		                		lstVer = 1;
		                	} else {
		                		ReqCd = "04";
		                		if (rs.getInt("cr_lstver")>=10) {
		                			lstVer = 1;
		                		} else {
		                			lstVer = rs.getInt("cr_lstver")+1;
		                		}
		                	}
		                }
			        	rs.close();
			        	pstmt.close();
		        	} else {
		        		DocSeq = prjinfo.chkDocseq(PrjNo, chkInList.get(i).get("basedir"), chkInList.get(i).get("fullpath"), true, conn);
		        		if (DocSeq == null || DocSeq == "") {
		        			throw new Exception("폴더등록에 실패하였습니다. ["+ chkInList.get(i).get("fullpath")+"]");
		        		}
		        	}
	        		paramIndex = 0;
		            if (DocID_NUM == "" || DocID_NUM == null){
			        	strQuery.setLength(0);
			        	strQuery.append("insert into cmr0030 (CR_DOCID,CR_DOCFILE,CR_STATUS,CR_CREATDT, ");
			        	strQuery.append("CR_LASTDT,CR_CREATOR,CR_EDITOR,CR_LSTVER,CR_PRJNO,CR_DIRPATH, ");
			        	strQuery.append("CR_METHCD,CR_DEVSTEP,CR_CCBYN,CR_DOCSEQ) values ");
			        	strQuery.append("(lpad(docid_seq.nextval,19,'0'),?,'3',SYSDATE,SYSDATE,?,?,0,?,?,  ?,?,nvl(?,'N'),?) ");
		                pstmt = conn.prepareStatement(strQuery.toString());
		                //pstmt = new LoggableStatement(conn,strQuery.toString());

		                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_docfile"));
		                pstmt.setString(++paramIndex,UserId);
		                pstmt.setString(++paramIndex,UserId);
		                pstmt.setString(++paramIndex,PrjNo);
		                pstmt.setString(++paramIndex,chkInList.get(i).get("fullpath").replace("'","''"));
		                pstmt.setString(++paramIndex,MethCd);
		                pstmt.setString(++paramIndex,chkInList.get(i).get("cr_devstep"));
		                pstmt.setString(++paramIndex,chkInList.get(i).get("ccbyn"));
		                pstmt.setString(++paramIndex,DocSeq);
		                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    	    	pstmt.executeUpdate();
		    	    	pstmt.close();

		    	    	strQuery.setLength(0);
		          		strQuery.append("select a.CR_DOCID from cmr0030 a              \n");
		    			strQuery.append(" where a.cr_prjno=? and a.cr_docseq=?         \n");
		    			strQuery.append("   and a.cr_docfile=?                         \n");
		    	        pstmt = conn.prepareStatement(strQuery.toString());
		    	        //pstmt = new LoggableStatement(conn,strQuery.toString());
		    	        pstmt.setString(1, PrjNo);
		                pstmt.setString(2,DocSeq);
		                pstmt.setString(3,chkInList.get(i).get("cr_docfile"));
		                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    	        rs = pstmt.executeQuery();

		    	        if (rs.next()) {
		    	        	DocID_NUM = rs.getString("CR_DOCID");
		    	        }
		    	        rs.close();
		    	        pstmt.close();

		    	    	strQuery.setLength(0);
		    	    	strQuery.append("insert into cmr0031 (CR_PRJNO,CR_DOCID,CR_CREATDT,CR_LASTDT, ");
		    	    	strQuery.append("CR_EDITOR,CR_DOCSEQ) values ");
		    	    	strQuery.append("(?,?,SYSDATE,SYSDATE,?,?) ");
		                pstmt = conn.prepareStatement(strQuery.toString());
		                //pstmt = new LoggableStatement(conn,strQuery.toString());
		                paramIndex = 0;
		                pstmt.setString(++paramIndex,PrjNo);
		                pstmt.setString(++paramIndex,DocID_NUM);
		                pstmt.setString(++paramIndex,UserId);
		                pstmt.setString(++paramIndex,DocSeq);
		               // ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		    	    	pstmt.executeUpdate();
		    	    	pstmt.close();
		    	    	lstVer = 1;
		    	    	ReqCd = "03";
		            } else {
		            	strQuery.setLength(0);
			        	strQuery.append("update cmr0030 set cr_ccbyn=nvl(?,'N')   \n");
			        	strQuery.append(" where cr_docid=?                        \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
		                pstmt.setString(++paramIndex,chkInList.get(i).get("ccbyn"));
			        	pstmt.setString(++paramIndex,DocID_NUM);
			        	pstmt.executeUpdate();
		    	    	pstmt.close();
		            }

	        		//ecamsLogger.error("+++++++docid,docseq2++++++, "+DocID_NUM+", "+DocSeq);
		            chkInList.get(i).put("cr_docid",DocID_NUM);
		            chkInList.get(i).put("cr_docseq",DocSeq);
		            chkInList.get(i).put("reqcd",ReqCd);
		            chkInList.get(i).put("version",Integer.toString(lstVer));
		            //conn.commit();
	    		}

	    	}
        	CodeInfo codeInfo = new CodeInfo();
        	PrjNo = "";
        	PrjName = "";
        	for (i=0;chkInList.size()>i;i++) {
	    		findSw = false;
	    		errMsg = "";
	    		if (chkInList.get(i).get("errMsg").length() == 0) {
		    		if (PrjNo.length() == 0) findSw = true;
		    		else if (!PrjNo.equals(chkInList.get(i).get("cd_prjno"))) findSw = true;
	    		}

	    		if (findSw == true) {
	    			if (AcptNo != "" && AcptNo != null) {
	    				strQuery.setLength(0);
	    	        	strQuery.append("insert into cmr1100 ");
	    	        	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_PRJNO,   \n");
	    	        	strQuery.append("CR_VERSION,CR_BASENO,CR_EDITOR,CR_CCBYN,CR_DOCSEQ)          \n");
	    	        	strQuery.append("(select ?,(select max(cr_serno) from cmr1100 where cr_acptno=?)+rownum, \n");
	    	        	strQuery.append("  '0','05',a.cr_docid,?,a.cr_lstver,?,?,a.cr_ccbyn,a.cr_docseq \n");
	    	        	strQuery.append("   from cmr0030 a,                                          \n");
	    	        	strQuery.append("       (select a.cr_docid from cmr0030 a,cmr0031 b          \n");
	    	        	strQuery.append("         where b.cr_prjno=? and b.cr_docid=a.cr_docid       \n");
	    	        	strQuery.append("           and a.cr_lstver>0 and a.cr_status<>'9'           \n");
	    	        	strQuery.append("         minus    \n");
	    	        	strQuery.append("        select cr_docid from cmr1100 where cr_acptno=?) b   \n");
	    	        	strQuery.append("  where a.cr_docid=b.cr_docid)                              \n");
	    	        	pstmt = conn.prepareStatement(strQuery.toString());
	    	    		//pstmt = new LoggableStatement(conn,strQuery.toString());
	    	    		pstmt.setString(1, AcptNo);
	    	    		pstmt.setString(2, AcptNo);
	    	    		pstmt.setString(3, PrjNo);
	    	    		pstmt.setString(4, AcptNo);
	    	    		pstmt.setString(5, UserId);
	    	    		pstmt.setString(6, PrjNo);
	    	    		pstmt.setString(7, AcptNo);
	    	    		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	    		pstmt.executeUpdate();
	    	    		pstmt.close();

	    	    		strQuery.setLength(0);
	    	        	strQuery.append("update cmr0030 set      \n");
	    	            strQuery.append("       cr_status='7',   \n");
	    	            strQuery.append("       cr_editor=?      \n");//UserId
	    	            strQuery.append(" where cr_docid in (select cr_docid from cmr1100 \n");//DocId
	    	            strQuery.append("                     where cr_acptno=?)          \n");//DocId
	    	        	pstmt = conn.prepareStatement(strQuery.toString());
	    	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	    	        	paramIndex = 0;
	    	        	pstmt.setString(1, UserId);
	    	        	pstmt.setString(2, AcptNo);
	    	        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	        	pstmt.executeUpdate();
	    	        	pstmt.close();

	    	        	///	    	체크아웃 상태값 변경
	    	        	strQuery.setLength(0);
	    	        	strQuery.append("update cmr1100 set          \n");
	    	            strQuery.append("       cr_confno=cr_acptno  \n");
	    	            strQuery.append(" where cr_docid in (select cr_docid from cmr1100 \n");//DocId
	    	            strQuery.append("                     where cr_acptno=?)          \n");//DocId
	    	            strQuery.append("   and cr_confno is null    \n");//DocId
	    	        	pstmt = conn.prepareStatement(strQuery.toString());
	    	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	    	        	paramIndex = 0;
	    	        	pstmt.setString(1, AcptNo);
	    	        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	        	pstmt.executeUpdate();
	    	        	pstmt.close();


	    		    	Cmr0200  cmr0200 = new Cmr0200();
	    	        	String retMsg = cmr0200.request_Confirm(AcptNo,dataObj.get("SysCd"),SinCd,UserId,true,ConfList,conn);
	    	        	if (!retMsg.equals("OK")) {
	    	        		conn.rollback();
	    	        		throw new Exception("결재정보작성 중 오류가 발생하였습니다." );
	    	        	}
	    			}
	    			PrjNo = chkInList.get(i).get("cd_prjno");
	    			PrjName = chkInList.get(i).get("cd_prjname");
	    			////  신청번호    ////
			        AcptNo = autoseq.getSeqNo(conn,SinCd);

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
			    	strQuery.append("CR_PRJNO,CR_PRJNAME,CR_SVRCD,CR_SVRSEQ) values ( ");
			    	strQuery.append("?,?,?,?,sysdate,'0',?,?,'0',?,?,?,?,?,?,?,?,?) ");
			    	pstmt = conn.prepareStatement(strQuery.toString());
			    	//pstmt = new LoggableStatement(conn,strQuery.toString());
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
			    	for (int j=0 ; j<uInfo.length ; j++){
			    		rData = (HashMap<String, String>) uInfo[j];
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
			    	pstmt.setString(++paramIndex, "1");
			    	pstmt.setString(++paramIndex, "");
			    	pstmt.setString(++paramIndex, PrjNo);
			    	pstmt.setString(++paramIndex, PrjName);
			    	pstmt.setString(++paramIndex, dataObj.get("cm_svrcd"));
			    	pstmt.setString(++paramIndex, dataObj.get("cm_seqno"));
			    	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			    	pstmt.executeUpdate();
			    	pstmt.close();
	    		}
	    		if (chkInList.get(i).get("errMsg").length() == 0) findSw = true;
	    		else findSw = false;

	    		if (findSw == true) {
	        		//ecamsLogger.error("+++++++docid,docseq3++++++, "+chkInList.get(i).get("cr_docid")+", "+chkInList.get(i).get("docseq"));
		        	strQuery.setLength(0);
		        	strQuery.append("insert into cmr1100 ");
		        	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_PRJNO,");
		        	strQuery.append("CR_VERSION,CR_BASENO,CR_PCDIR,CR_EDITOR,CR_CCBYN,CR_DOCSEQ,CR_PCFILE) ");
		        	strQuery.append("values (?,?,'0',?,?,?,    ?,?,?,?,nvl(?,'N'),?,?)");
		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	//pstmt = new LoggableStatement(conn,strQuery.toString());
		        	paramIndex = 0;
		        	pstmt.setString(++paramIndex, AcptNo);
		        	pstmt.setInt(++paramIndex, i+1);
		        	pstmt.setString(++paramIndex, chkInList.get(i).get("reqcd"));
		        	pstmt.setString(++paramIndex, chkInList.get(i).get("cr_docid"));
		        	pstmt.setString(++paramIndex, PrjNo);

		        	pstmt.setString(++paramIndex, chkInList.get(i).get("version"));
		        	pstmt.setString(++paramIndex, AcptNo);
		        	pstmt.setString(++paramIndex, chkInList.get(i).get("pcdir").replace("'", "''"));
		        	pstmt.setString(++paramIndex, UserId);
		        	pstmt.setString(++paramIndex, chkInList.get(i).get("ccbyn"));
		        	pstmt.setString(++paramIndex, chkInList.get(i).get("cr_docseq"));
		        	pstmt.setString(++paramIndex, chkInList.get(i).get("cr_pcfile"));
		        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        	pstmt.executeUpdate();
		        	pstmt.close();
		    	}


        	}
        	if (AcptNo != null && AcptNo != "") {
	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr1100 ");
	        	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_PRJNO,   \n");
	        	strQuery.append("CR_VERSION,CR_BASENO,CR_EDITOR,CR_CCBYN,CR_DOCSEQ)          \n");
	        	strQuery.append("(select ?,(select max(cr_serno) from cmr1100 where cr_acptno=?)+rownum, \n");
	        	strQuery.append("  '0','05',a.cr_docid,?,a.cr_lstver,?,?,a.cr_ccbyn,a.cr_docseq \n");
	        	strQuery.append("   from cmr0030 a,                                          \n");
	        	strQuery.append("       (select a.cr_docid from cmr0030 a,cmr0031 b          \n");
	        	strQuery.append("         where b.cr_prjno=? and b.cr_docid=a.cr_docid       \n");
	        	strQuery.append("           and nvl(a.cr_ccbyn,'N')='Y' and a.cr_lstver>0    \n");
	        	strQuery.append("           and a.cr_status<>'9'                             \n");
	        	strQuery.append("         minus    \n");
	        	strQuery.append("        select cr_docid from cmr1100 where cr_acptno=?) b   \n");
	        	strQuery.append("  where a.cr_docid=b.cr_docid)                              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	    		//pstmt = new LoggableStatement(conn,strQuery.toString());
	    		pstmt.setString(1, AcptNo);
	    		pstmt.setString(2, AcptNo);
	    		pstmt.setString(3, PrjNo);
	    		pstmt.setString(4, AcptNo);
	    		pstmt.setString(5, UserId);
	    		pstmt.setString(6, PrjNo);
	    		pstmt.setString(7, AcptNo);
	    		//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    		pstmt.executeUpdate();
	    		pstmt.close();

	    		strQuery.setLength(0);
	        	strQuery.append("update cmr0030 set      \n");
	            strQuery.append("       cr_status='7',   \n");
	            strQuery.append("       cr_editor=?      \n");//UserId
	            strQuery.append(" where cr_docid in (select cr_docid from cmr1100 \n");//DocId
	            strQuery.append("                     where cr_acptno=?)          \n");//DocId
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	paramIndex = 0;
	        	pstmt.setString(1, UserId);
	        	pstmt.setString(2, AcptNo);
	        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();

	        	///	    	체크아웃 상태값 변경
	        	strQuery.setLength(0);
	        	strQuery.append("update cmr1100 set          \n");
	            strQuery.append("       cr_confno=cr_acptno  \n");
	            strQuery.append(" where cr_docid in (select cr_docid from cmr1100 \n");//DocId
	            strQuery.append("                     where cr_acptno=?)          \n");//DocId
	            strQuery.append("   and cr_confno is null    \n");//DocId
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	//pstmt = new LoggableStatement(conn,strQuery.toString());
	        	paramIndex = 0;
	        	pstmt.setString(1, AcptNo);
	        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();


		    	Cmr0200  cmr0200 = new Cmr0200();
	        	String retMsg = cmr0200.request_Confirm(AcptNo,dataObj.get("SysCd"),SinCd,UserId,true,ConfList,conn);
	        	if (!retMsg.equals("OK")) {
	        		conn.rollback();
	        		throw new Exception("결재정보작성 중 오류가 발생하였습니다.");
	        	}
        	}
        	conn.commit();
        	conn.close();
	    	userInfo = null;
	    	codeInfo = null;
	    	autoseq = null;
	    	conn = null;
	    	return rsval.toArray();

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
			ecamsLogger.error("## Cmd1900.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1900.request_Check_In() SQLException END ##");
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
			ecamsLogger.error("## Cmd1900.request_Check_In() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1900.request_Check_In() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1900.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}//end of Cmd0101 class statement