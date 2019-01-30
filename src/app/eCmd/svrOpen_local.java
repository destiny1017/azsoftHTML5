/*****************************************************************************************
	1. program ID	: svrOpen_wcms.java
	2. create date	: 2009. 05. 07
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import app.common.CreateXml;
import app.common.DeepCopy;
import app.common.LoggableStatement;
//import app.common.UserInfo;
import app.common.eCAMSInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
//import app.thread.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class svrOpen_local{

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
	public Object[] getSvrDir(String UserID,String SysCd,String SvrIp,String SvrPort,String BaseDir,String HomeDir,String SysMsg) throws SQLException, Exception {

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
		File shfile=null;
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		ArrayList<Document> list = null;
		Object[] returnObjectArray = null;

		rsval.clear();

		try {
			strBinPath = ecamsinfo.getFileInfo("14");
			ErrSw = false;
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			try {
				makeFile = "dir" + UserID;
				strFile = strTmpPath + makeFile;

				shfile=null;
				shfile = new File(strTmpPath + makeFile+".sh");              //File 불러온다.
				if( !(shfile.isFile()) )             //File이 없으면
				{
					shfile.createNewFile();          //File 생성
				}
				writer = new OutputStreamWriter( new FileOutputStream(strTmpPath + makeFile+".sh"));
				writer.write("cd "+strBinPath +"\n");
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
					throw new Exception("디렉토리추출을 위한 작업에 실패하였습니다");
				}
				else{
					shfile.delete();
				}
			} catch (Exception e) {
				throw new Exception(e);
			}

			//strFile = "c:\\dirBK06168566";
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
			                		str = str.replace(HomeDir, "/" + SysMsg);
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
			        } finally {
			            if (in != null)
			                in.close();
			        }
		        }

		        if (mFile.isFile() && mFile.exists()) mFile.delete();

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
	public Object[] getFileList(String UserID,String SysCd,String SvrIp,String SvrPort,String HomeDir,String BaseDir,String SvrCd,String GbnCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		boolean           findSw      = false;
		boolean           ErrSw      = false;
		String            strBinPath  = "";
		String            strTmpPath  = "";
		String            strFile     = "";
		String            makeFile    = "";
		String            strExt      = "";
		String            wkRsrcCd    = "";
		String            wkCodeName  = "";
		String            wkInfo      = "";
		int               i           = 0;
		File shfile=null;
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();
		rsval.clear();

		try {
			conn = connectionContext.getConnection();
			strBinPath = ecamsinfo.getFileInfo("14");
			ErrSw = false;
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			try {
				makeFile = "filelist" + UserID;
				strFile = strBinPath + makeFile + "." + SysCd + ".ih.cs";

				shfile=null;
				shfile = new File(strTmpPath + makeFile+".sh");              //File 불러온다.
				if( !(shfile.isFile()) )              //File이 없으면
				{
					shfile.createNewFile();          //File 생성
				}

				writer = new OutputStreamWriter( new FileOutputStream(strTmpPath + makeFile+".sh"));
				writer.write("cd "+strBinPath +"\n");
				writer.write("./ecams_ih_cs " + SysCd + " " + SvrIp + " "+ BaseDir + " "  + SvrPort + " " + makeFile + " " + GbnCd + "\n");
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
					shfile.delete();
				}
			} catch (Exception e) {
				throw new Exception(e);
			}

			//strFile = "c:\\dirBK06168566_src";
            if (ErrSw == false) {
				File mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
					ErrSw = true;
					throw new Exception("신규대상목록 추출을 위한 작업에 실패하였습니다 [작성된 파일 없음]");
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
			            while ((str = in.readLine()) != null) {
			                if (str.length() > 0) {
			                	if (!str.substring(0,1).equals("d")) {
			                		if (str.substring(0,1).equals("/")) {
			                			wkDir = str.substring(0, str.length() - 1);
	                					if (wkDir.substring(0,HomeDir.length()).equals(HomeDir)) {
	                						wkDir = wkDir.substring(HomeDir.length());
	                						if (wkDir.length() > 0) {
		                						if (!wkDir.substring(0,1).equals("/")) {
		                							wkDir = "/" + wkDir;
		                						}
	                						} else wkDir = "/";
	                					}
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
			                					wkDsnCd = "";
			                					wkCodeName = "";
			                					wkInfo = "";

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
				                		            pstmt.setString(1, SysCd);
				                		            pstmt.setString(2, wkDsnCd);
				                		            pstmt.setString(3, wkB);

				                		            rs = pstmt.executeQuery();
				                		            if (rs.next()) {
				                		            	if (rs.getInt("cnt") > 0) findSw = true;
				                		            }
				                		            rs.close();
				                		            pstmt.close();
			                		            }

			                		            if (findSw == false) {
		                		    				wkRsrcCd = "";
			                		    			if (wkB.indexOf(".") >= 0) {
			                							i = wkB.lastIndexOf(".");
			                							if (i>=0) strExt = wkB.substring(i);
			                						}
			                						strQuery.setLength(0);
			                						strQuery.append("select a.cm_rsrccd from cmm0032 a,cmm0023 b          \n");
			                						strQuery.append(" where a.cm_syscd = ? and a.cm_langcd=b.cm_langcd    \n");
			                						strQuery.append("and a.cm_rsrccd not in (select cm_samersrc           \n");
			                						strQuery.append("                          from cmm0037               \n");
			                						strQuery.append("                         where cm_syscd = ?          \n");
			                						strQuery.append("                           and cm_factcd='04')       \n");
			                						if (strExt != null && strExt != "")
			                							strQuery.append("   and instr(b.cm_exename,?)>0                   \n");
			                						pstmt = conn.prepareStatement(strQuery.toString());
			                			            pstmt.setString(1, SysCd);
			                			            pstmt.setString(2, SysCd);
			                			            if (strExt != null && strExt != "") pstmt.setString(3, strExt);
			                			            rs = pstmt.executeQuery();
			                						if (rs.next()){
			                							wkRsrcCd = rs.getString("cm_rsrccd");
			                						}//end of while-loop statement
			                						rs.close();
			                						pstmt.close();

			                						if (wkRsrcCd != "" && wkRsrcCd != null) {
			                							strQuery.setLength(0);
			                							strQuery.append("select b.cm_info,a.cm_codename               \n");
			                							strQuery.append("  from cmm0036 b,cmm0020 a                   \n");
			                							strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?        \n");
			                							strQuery.append("   and a.cm_macode='JAWON'                   \n");
			                							strQuery.append("   and a.cm_micode=b.cm_rsrccd               \n");
			                							pstmt = conn.prepareStatement(strQuery.toString());
			                							pstmt.setString(1, SysCd);
			                							pstmt.setString(2, wkRsrcCd);
			                							rs = pstmt.executeQuery();
			                							if (rs.next()) {
			                								wkCodeName = rs.getString("cm_codename");
			                								wkInfo = rs.getString("cm_info");
			                							}
			                							rs.close();
			                							pstmt.close();
			                						}

			                						rst = new HashMap<String, String>();
				                					rst.put("cm_dirpath", wkDir);
				                					rst.put("filename", wkB);
				                					rst.put("cm_dsncd", wkDsnCd);
				                					rst.put("cm_rsrccd", wkRsrcCd);
				                					rst.put("cm_codename", wkCodeName);
				                					rst.put("cm_info", wkInfo);
				                					if (wkRsrcCd != "" && wkRsrcCd != null) {
				                						 rst.put("enable1", "1");
				                					} else rst.put("enable1", "0");
				                					rsval.add(rst);
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

		        /*  테스트 위해 임시로 막음 테스트 완료 후 풀어야 함
		         * if (mFile.isFile() && mFile.exists()) mFile.delete();
		         */
		        conn.close();
		        conn = null;
            }

    		returnObjectArray = rsval.toArray();
    		//ecamsLogger.debug(rsval.toString());
    		rsval = null;
    		return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen_wcms.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen_wcms.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_wcms.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_wcms.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen_wcms.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getFileList() method statement

	public String statusCheck(String SysCd, String DsnCd, String RsrcNm, String UserId,String rsrcInfo,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String wkB   = "0";

		try {
	        int parmCnt = 0;
		    strQuery.append("select cr_status,cr_lstver,cr_editor from cmr0020 \n");
		    strQuery.append(" where cr_syscd = ?                               \n");
		    if (rsrcInfo.substring(22,23).equals("0"))
		    	strQuery.append("and cr_dsncd=?                                \n");
		    strQuery.append("   and cr_rsrcname = ?                            \n");
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++parmCnt, SysCd);
            if (rsrcInfo.substring(22,23).equals("0")) pstmt.setString(++parmCnt, DsnCd);
            pstmt.setString(++parmCnt, RsrcNm);
//            ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
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
			ecamsLogger.error("## Cmd4100.statusCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd4100.statusCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd4100.statusCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd4100.statusCheck() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of statusCheck() method statement


	public Object[] cmr0020_Insert_total(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		boolean           tmaxfg      = false;
		int               i           = 1;
		int               j           = 0;
	    String            strDsnCD    = "";
	    Cmd0100           cmd0100     = new Cmd0100();
		HashMap<String,String> rst = null;
		HashMap<String,String> rst2 = null;
		HashMap<String,String> rst3 = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			rst = etcData;
			rsval.clear();
	        for (i=0;i<fileList.size();i++){
	        	rst2 = null;
	        	rst2 = fileList.get(i);
	        	
	        	if (fileList.get(i).get("cm_dsncd") == "" || fileList.get(i).get("cm_dsncd") == null){
	        		strDsnCD = cmd0100.cmm0070_Insert(etcData.get("userid"),etcData.get("syscd"),etcData.get("rsrcname"),etcData.get("rsrccd"),etcData.get("jobcd"),fileList.get(i).get("cm_dirpath"), tmaxfg, conn);

	               	rst.put("dsncd", strDsnCD);
	                rst.put("dirpath",fileList.get(i).get("cm_dirpath"));

	                for (j=i+1;fileList.size()>j;j++) {
	                	if (fileList.get(i).get("cm_dirpath").equals(fileList.get(j).get("cm_dirpath"))) {
	                		rst3 = (HashMap<String, String>) DeepCopy.deepCopy(fileList.get(j));
	                		rst3.put("cm_dsncd", strDsnCD);
	                		fileList.set(j, rst3);
	                	}
	                }
	        	} else {
	        		strDsnCD = fileList.get(i).get("cm_dsncd");
	        		rst.put("dsncd", strDsnCD);
	        		rst.put("dirpath",fileList.get(i).get("cm_dirpath"));
	        	}

	        	rst.put("rsrcname", fileList.get(i).get("filename"));
	        	rst.put("rsrccd", etcData.get("rsrccd"));
	        	rst2.put("error", "0");
	        	rst2.put("errmsg", "정상");
	        	//ecamsLogger.debug("+++++++rst+++++"+rst.toString()+","+strDsnCD+","+fileList.get(i).get("cm_dirpath")+","+etcData.get("dsncd"));
	        	retMsg = statusCheck(etcData.get("syscd"),strDsnCD,fileList.get(i).get("filename"),etcData.get("userid"),etcData.get("cm_info"),conn);
	        	if (retMsg.equals("0")) {
	        		rst.put("baseitem", "");
		        	//retMsg = cmd0100.cmr0020_Insert(etcData.get("userid"),etcData.get("syscd"),strDsnCD,fileList.get(i).get("filename"),etcData.get("rsrccd"),etcData.get("jobcd"),etcData.get("story"),"",fileList.get(i).get("cm_info"),conn);

		        	rst3 = new HashMap<String, String>();
		        	rst3.put("userid",etcData.get("userid"));
		        	rst3.put("syscd",etcData.get("syscd"));
		        	rst3.put("dsncd",strDsnCD);
		        	rst3.put("rsrcname",fileList.get(i).get("filename"));
		        	rst3.put("rsrccd",etcData.get("rsrccd"));
		        	rst3.put("jobcd",etcData.get("jobcd"));
		        	rst3.put("story",fileList.get(i).get("story"));
		        	rst3.put("baseitem","");
		        	rst3.put("info",etcData.get("cm_info"));
		        	rst3.put("srid",etcData.get("srid"));
		        	retMsg = cmd0100.cmr0020_Insert(rst3,conn);
		        	rst3 = null;

		        	//ecamsLogger.debug("++++filename,result++"+file.get("filename")+","+retMsg);
		        	if (retMsg.substring(0,1).equals("0")) {
	        			rst2.put("itemid", retMsg.substring(1));
	        			rst2.put("baseitem", retMsg.substring(1));
	        			rst2.put("cm_info", etcData.get("cm_info"));
	        			rst2.put("filename", fileList.get(i).get("filename"));
		        	} else {
		        		rst2.put("error", "1");
		        		rst2.put("errmsg", "등록실패");
		        	}
	        	} else {
	        		rst2.put("error", "1");
	        		rst2.put("errmsg", retMsg);
	        	}				
				rsval.add(rst2);
				
				rs = null;
				pstmt = null;
			}

        	conn.close();
        	conn = null;
	        returnObjectArray = rsval.toArray();
    		//ecamsLogger.debug(rsval.toString());
    		rsval = null;
    		return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen_local.cmr0020_Insert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen_wcms.cmr0020_Insert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_local.cmr0020_Insert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_local.cmr0020_Insert() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrOpen_local.cmr0020_Insert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0020_Insert() method statement

	public Object[] dirOpenChk(String SysCd,ArrayList<String> fileList,String DevHome)	throws SQLException, Exception {
		CreateXml         ecmmtb      = new CreateXml();
		ArrayList<Document> list = new ArrayList<Document>();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;

		try {
			int                 i = 0;
			int                 j = 0;
			int                 k = 0;
			String              strPath1 = "";
			String              strPath2   = "";
			int               upSeq       = 0;
			int               maxSeq      = 100;
			boolean           findSw      = false;
			String[]          pathDepth   = null;
			DevHome = DevHome.replace("\\\\","/");
			rsval.clear();
			upSeq = 1;
			maxSeq = 1;
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","isBranch");
			rst = new HashMap<String,String>();
			rst.put("cm_dirpath","Home");
			rst.put("cm_fullpath",DevHome);
			rst.put("cm_upseq",Integer.toString(upSeq));
			rst.put("cm_seqno",Integer.toString(maxSeq));
			rsval.add(maxSeq - 1, rst);
			strPath2 = DevHome;
			rst = null;
			upSeq = maxSeq;
			//ecamsLogger.debug("++++fileList++++"+fileList.toString());
			for (i=0;fileList.size()>i;i++) {
				strPath1 = fileList.get(i);
				if(strPath1.length()>=DevHome.length()) strPath1 = strPath1.substring(DevHome.length());
				if (strPath1.substring(0,1).equals("/")) strPath1 = strPath1.substring(1);
				//ecamsLogger.debug("+++++strPath1+++++"+strPath1);
				strPath2 = "";
				if (strPath1.length() > 0){
					pathDepth = strPath1.split("/");

					strPath1 = "/";
					findSw = false;

					for (k = 0;pathDepth.length > k;k++) {
						if (strPath1.length() > 1 ) {
							strPath1 = strPath1 + "/";
						}
						strPath1 = strPath1 + pathDepth[k];
						//ecamsLogger.debug("strDir====>" + strDir);
						findSw = false;
						if (rsval.size() > 0) {
							for (j = 0;rsval.size() > j;j++) {
								if (rsval.get(j).get("cm_fullpath").equals(DevHome + strPath1)) {
									findSw = true;
									break;
								}
							}
							if (findSw == false) {
								for (j = 0;rsval.size() > j;j++) {
									//ecamsLogger.debug("DevHome + strPath1====>" + fileList.get(i)+ ", " + rsval.get(j).get("cm_fullpath") + ", " + DevHome + strPath1);
									if (rsval.get(j).get("cm_fullpath").equals(DevHome+strPath2)) {
										upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));

										//rsval.set(j, element)
										//rst.put("branch","true");
										findSw = false;
										break;
									}
								}
							}
						} else {
							findSw = false;
						}
						strPath2 = strPath2 + "/" + pathDepth[k];
						if (findSw == false) {
							maxSeq = maxSeq + 1;

							//ecamsLogger.debug("dirpath,full,upseq,seq====>" + pathDepth[k] + "  , " + strPath1  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
							rst = new HashMap<String,String>();
							rst.put("cm_dirpath",pathDepth[k]);
							rst.put("cm_fullpath",DevHome+strPath1);
							rst.put("cm_upseq",Integer.toString(upSeq));
							rst.put("cm_seqno",Integer.toString(maxSeq));
							rsval.add(maxSeq - 1, rst);
							rst = null;
							upSeq = maxSeq;
						}
					}

				}//end of while-loop statement
			}
			if (rsval.size() > 0) {

			    String strBran = "";
				for (i = 0;rsval.size() > i;i++) {
					strBran = "false";
					for (j=0;rsval.size()>j;j++) {
						if (i != j) {
							if (rsval.get(i).get("cm_seqno").equals(rsval.get(j).get("cm_upseq"))) {
								strBran = "true";
								break;
							}
						}
					}
				//ecamsLogger.error("ecmmtb.addXML");
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath").replace("/","\\\\"),
							strBran,rsval.get(i).get("cm_upseq"));
				}
			}
			list.add(ecmmtb.getDocument());
			returnObjectArray = list.toArray();
			list = null;
			ecmmtb = null;

			return returnObjectArray;

		}  catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_local.dirOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_local.dirOpenChk() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)  	rsval = null;
		}
	}//end of dirOpenChk() method statement


	/**
	 * <PRE>
	 * 1. MethodName	: fileOpenChk
	 * 2. ClassName		: svrOpen_local
	 * 3. Commnet			: 로컬에있는 파일들 정보 조회
	 * 4. 작성자				: Administrator
	 * 5. 작성일				: 2010. 12. 8. 오후 1:01:22
	 * </PRE>
	 * 		@return Object[]
	 * 		@param SysCd
	 * 		@param fullPath
	 * 		@param fileList
	 * 		@param strDevHome
	 * 		@param exeName
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public Object[] fileOpenChk(String SysCd,String fullPath,String JobCd,ArrayList<String> fileList,String strDevHome,String exeName)
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
		String              wkDsnCd     = "";
		String              wkVolPath   = "";
		String              wkDirPath   = "";
		String              wkInfo      = "";
		boolean             findSw      = false;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			fullPath = fullPath.replace("\\\\", "/");
            strDevHome = strDevHome.replace("\\\\","/");
            String[] strExe = exeName.split(",");
            //ecamsLogger.error("+++++fullpath,strdevhome,exename++++"+fullPath+", "+strDevHome+", "+exeName+", "+Integer.toString(fileList.size()));
			strQuery.setLength(0);
			strQuery.append("select a.cm_volpath                    	                    \n");
			strQuery.append("  from cmm0030 d,cmm0031 a,cmm0038 b,cmm0036 c                 \n");
			strQuery.append(" where d.cm_syscd = ? and d.cm_syscd=a.cm_syscd      			\n");
			strQuery.append("   and d.cm_dirbase=a.cm_svrcd                                 \n");
			strQuery.append("   and a.cm_syscd = b.cm_syscd and a.CM_SEQNO = b.CM_SEQNO     \n");
			strQuery.append("   and b.cm_rsrccd = c.cm_rsrccd and a.cm_svrcd = b.cm_svrcd   \n");
			strQuery.append("   and substr(c.cm_info,45,1)='1' and a.cm_syscd = c.cm_syscd	\n");
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);

            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	wkVolPath = rs.getString("cm_volpath");
            }
            rs.close();
            pstmt.close();

            String wkExe = "";
            String wkB = "";
            int z = 0;
            wkDirPath = wkVolPath + fullPath.substring(strDevHome.length());
            strQuery.setLength(0);
			strQuery.append("select cm_dsncd from cmm0070            \n");
			strQuery.append(" where cm_syscd=?                       \n");
			strQuery.append("   and upper(cm_dirpath)=upper(?)       \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, wkDirPath);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	wkDsnCd = rs.getString("cm_dsncd");
            }
            rs.close();
            pstmt.close();
            
			rsval.clear();
			for (i=0;fileList.size()>i;i++) {
				findSw = false;
				wkB = fileList.get(i);
				
				if (exeName == null || exeName.equals("")) findSw = true;
				else {
					wkExe = "";
					if (wkB.indexOf(".")>0) {
						wkExe = wkB.substring(wkB.lastIndexOf("."));
					}
					for (z=0;strExe.length>z;z++) {
						if (strExe[z] == null || strExe[z].equals("")) {
							if (wkExe == null || wkExe.equals("")) {
								findSw = true;
								break;
							}
						} else {
							if (strExe[z].toUpperCase().equals(wkExe.toUpperCase())) {
								findSw = true;
								break;
							}
						}
					}
				}

				if (findSw) {
					findSw = false;
					//ecamsLogger.error("++++ wkB++++"+wkB);
		            if (wkDsnCd != null && wkDsnCd != "") {
		            	strQuery.setLength(0);
		            	strQuery.append("select count(*) cnt from cmr0020        \n");
						strQuery.append(" where cr_syscd=? and cr_dsncd=?        \n");
						strQuery.append("   and upper(cr_rsrcname)=upper(?)      \n");

						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
			            pstmt.setString(1, SysCd);
			            pstmt.setString(2, wkDsnCd);
			            pstmt.setString(3, fileList.get(i));
			            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			            rs = pstmt.executeQuery();

			            if (rs.next()) {
			            	if (rs.getInt("cnt") > 0) findSw = true;
			            }
			            rs.close();
			            pstmt.close();
		            }
		            if (!findSw) {
		            	rst = new HashMap<String, String>();
						rst.put("cm_dirpath", wkDirPath);
						rst.put("pcdir", fullPath);
						rst.put("filename", fileList.get(i));
						rst.put("cm_dsncd", wkDsnCd);
						rst.put("selected", "0");
		    			rsval.add(rst);
		            }
				}
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrOpen_local.fileOpenChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrOpen_local.fileOpenChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrOpen_local.fileOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrOpen_local.fileOpenChk() Exception END ##");
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
					ecamsLogger.error("## svrOpen_local.fileOpenChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of fileOpenChk() method statement


}//end of svrOpen_local class statement
