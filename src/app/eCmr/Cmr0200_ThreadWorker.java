package app.eCmr;

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
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

//import app.common.LoggableStatement;
//import app.thread.*;

public class Cmr0200_ThreadWorker implements Runnable{

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	private HashMap<String,String> myfile = null;
	private HashMap<String,String> myetcData = null;
	private String mybinPath = "";
	private String mytmpPath = "";
	private ArrayList<HashMap<String, String>> parrent_rtList = null;


	Cmr0200_ThreadWorker(HashMap<String,String> file,HashMap<String,String> etcData,String binPath,String tmpPath,ArrayList<HashMap<String, String>> rtList){
		myfile = file;
		myetcData = etcData;
		mybinPath = binPath;
		mytmpPath = tmpPath;
		parrent_rtList = rtList;
	}


	public void run() {
		ArrayList<HashMap<String,String>> tmpRtList = new ArrayList<HashMap<String,String>>();
		try {
			tmpRtList = getDownFileList_save(myfile,myetcData,mybinPath,mytmpPath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parrent_rtList.addAll((ArrayList<HashMap<String,String>>)tmpRtList);

	}

	public String Cmr0020_Copy(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String LangCd,String BaseItem,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strItemId   = null;
		int               cnt         = 0;

		try {
			cnt = 0;
			strQuery.setLength(0);
			strQuery.append("insert into cmr0020 (cr_syscd,cr_itemid,cr_dsncd,   									\n");
			strQuery.append("        cr_rsrcname,cr_rsrccd,cr_jobcd,cr_langcd,   									\n");
			strQuery.append("        cr_status,cr_creator,cr_story,cr_opendate,  									\n");
			strQuery.append("        cr_lastdate,cr_lstver,cr_editor,            									\n");
			strQuery.append("        CR_NOMODIFY,cr_lstusr,cr_lstdat)            									\n");
			strQuery.append("		 (select cr_syscd, 							 									\n");
			strQuery.append("  		   (select decode(nvl(max(cr_itemid),'0'),'0', 									\n");
			strQuery.append(" 		      to_char(SYSDATE,'yyyymm')||'000001',to_number(max(cr_itemid)) +1) as cr_itemid	\n");
			strQuery.append(" 		    from cmr0020 where substr(cr_itemid,1,6)=to_char(SYSDATE,'yyyymm') ),  		\n");
			strQuery.append(" 		  ?, ?, ?, cr_jobcd, ?, '3',     \n");
			strQuery.append("         ?, cr_story, SYSDATE, SYSDATE, 0, ?, '0',   									\n");
			strQuery.append("         ?, SYSDATE								     								\n");
			strQuery.append("   	  from cmr0020 where cr_itemid=?)                  								\n");
			pstmt2 = conn.prepareStatement(strQuery.toString());
			pstmt2.setString(++cnt, DsnCd);
			pstmt2.setString(++cnt, RsrcName);
			pstmt2.setString(++cnt, RsrcCd);
			pstmt2.setString(++cnt, LangCd);
			pstmt2.setString(++cnt, UserId);
			pstmt2.setString(++cnt, UserId);
			pstmt2.setString(++cnt, UserId);
			pstmt2.setString(++cnt, BaseItem);
			cnt = pstmt2.executeUpdate();
            conn.commit();

			pstmt2.close();

            cnt = 0;
    		strQuery.setLength(0);
    		strQuery.append("select cr_itemid \n");
			strQuery.append("  from cmr0020		 		                       \n");
			strQuery.append(" where	cr_syscd=? and cr_dsncd=? and cr_rsrcname=? \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(++cnt, SysCd);
			pstmt.setString(++cnt, DsnCd);
			pstmt.setString(++cnt, RsrcName);

            rs = pstmt.executeQuery();
            if (rs.next() == true){
            	strItemId = rs.getString("cr_itemid");
            }
            rs.close();
            pstmt.close();
            conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return strItemId;

		} catch (SQLException sqlexception) {
			if (sqlexception.getMessage().indexOf("ORA-00001") != -1){
				return "ORA-00001";
			}else{
				sqlexception.printStackTrace();
				conn.rollback();
				ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() SQLException START ##");
				ecamsLogger.error("## Error DESC : ", sqlexception);
				ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() SQLException END ##");
				throw sqlexception;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
		}
	}

	public String DsnCdSel(String UserId,String SysCd,String Dirpath,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strDsnCd    = null;
		int               cnt         = 0;
		try {
			cnt = 0;

			strQuery.append("select cm_dsncd from cmm0070                      \n");
			strQuery.append(" where cm_syscd=? and cm_dirpath=?				   \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++cnt, SysCd);
            pstmt.setString(++cnt, Dirpath);

            rs = pstmt.executeQuery();

			if (rs.next() == true){
				strDsnCd = rs.getString("cm_dsncd");
			} else {
            	strQuery.setLength(0);
        		strQuery.append("insert into cmm0070                               \n");
        		strQuery.append("  (CM_SYSCD,CM_DSNCD,CM_DIRPATH,CM_EDITOR,CM_OPENDT,CM_LASTUPDT) \n");
        		strQuery.append("  ( select ?, decode(nvl(max(cm_dsncd),'0'),'0','00001',lpad(to_number(max(cm_dsncd)) + 1,5,'0')), \n");
        		strQuery.append("    ?, ?, SYSDATE,SYSDATE \n");
        		strQuery.append("    from cmm0070 \n");
        		strQuery.append("    where cm_syscd = ? ) \n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(1, SysCd);
        		pstmt2.setString(2, Dirpath);
        		pstmt2.setString(3, UserId);
        		pstmt2.setString(4, SysCd);
        		pstmt2.executeUpdate();
        		pstmt2.close();

        		strQuery.setLength(0);
    			strQuery.append("select cm_dsncd from cmm0070                           \n");
    			strQuery.append(" where cm_syscd=?                                      \n");
    			strQuery.append(" and cm_dirpath=?                                      \n");
                pstmt2 = conn.prepareStatement(strQuery.toString());
                pstmt2.setString(1, SysCd);
                pstmt2.setString(2, Dirpath);

                rs2 = pstmt2.executeQuery();

                if (rs2.next()){
                	strDsnCd = rs2.getString("cm_dsncd");
                }

                rs2.close();
                pstmt2.close();

			}
			rs.close();
			pstmt.close();
			//conn.close();
			rs2 = null;
			pstmt2 = null;
			rs = null;
			pstmt = null;
			//conn = null;

			return strDsnCd;


		} catch (SQLException sqlexception) {
			if (sqlexception.getMessage().indexOf("ORA-00001") != -1){
				return "ORA-00001";
			}
			else{
				sqlexception.printStackTrace();
				ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() SQLException START ##");
				ecamsLogger.error("## Error DESC : ", sqlexception);
				ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() SQLException END ##");
				throw sqlexception;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200_ThreadWorker.DsnCdSel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex4){ex4.printStackTrace();}
		}

	}

	public String langSel(String SysCd,String RsrcCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strRsrcCd   = "";
		int               cnt         = 0;
		try {
			cnt = 0;
			strQuery.append("select cm_langcd from cmm0032                     \n");
			strQuery.append(" where cm_syscd=? and cm_rsrccd=?				   \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++cnt, SysCd);
            pstmt.setString(++cnt, RsrcCd);

            rs = pstmt.executeQuery();

			if (rs.next() == true){
				strRsrcCd = rs.getString("cm_langcd");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			return strRsrcCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.langSel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.langSel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.langSel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.langSel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}

	}

	public String bldcdChk(String SysCd,String JobCd,String RsrcCd,String ItemId,boolean InCd,boolean BldCd,boolean RelCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErrMsg   = "";
		int               cnt         = 0;
		try {
			cnt = 0;
			strQuery.append("select count(*) as cnt from cmm0033               \n");
			strQuery.append(" where cm_syscd= ? and cm_rsrccd= ?               \n");
			strQuery.append("   and cm_jobcd= ?         					   \n");
			if (InCd == true) strQuery.append("   and cm_incd != '00'   	   \n");
			if (BldCd == true) strQuery.append("  and cm_bldcd != '00'   	   \n");
			if (RelCd == true) strQuery.append("  and cm_relcd != '00'   	   \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++cnt, SysCd);
            pstmt.setString(++cnt, RsrcCd);
            pstmt.setString(++cnt, JobCd);

            rs = pstmt.executeQuery();

			if (rs.next() == false){
				strErrMsg = "프로그램/업무에 실행할 스크립트정보가 등록되지 않았습니다.";
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			return strErrMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.bldcdChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.bldcdChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.bldcdChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.bldcdChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}

	public String rsrccdSel(String SysCd,String RsrcName,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strRsrcCd   = "";
		int               cnt         = 0;
		try {

			cnt = 0;
			strQuery.append("select a.cm_rsrccd                                \n");
			strQuery.append("  from cmm0032 a,cmm0023 b,cmm0036 c              \n");
			strQuery.append(" where a.cm_syscd=?         					   \n");
			strQuery.append("   and a.cm_langcd=b.cm_langcd					   \n");
			strQuery.append("   and instr(b.cm_exename,substr(?,instr(?,'.')) || ',')>0 \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd  					   \n");
			strQuery.append("   and substr(c.cm_info,26,1)='1'				   \n");
			strQuery.append("   and c.cm_closedt is null 					   \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(++cnt, SysCd);
            pstmt.setString(++cnt, RsrcName);
            pstmt.setString(++cnt, RsrcName);

            rs = pstmt.executeQuery();

			if (rs.next() == true){
				strRsrcCd = rs.getString("cm_rsrccd");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			return strRsrcCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.rsrccdSel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.rsrccdSel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.rsrccdSel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.rsrccdSel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}

	public ArrayList<HashMap<String,String>> getDownFileList_save(HashMap<String,String> file,HashMap<String,String> etcData,String strBinPath,String strTmpPath) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		HashMap<String, String>			  rst		  = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               reqCnt      = 0;
		int               addCnt      = 0;
		boolean            InCd       = false;
		boolean            BldCd      = false;
		int               svCnt       = 0;
		boolean            RelCd      = false;
		boolean            ErrSw      = false;
		String            strErr      = "";
		String            strFile     = "";
		String            makeFile    = "";
		String            strWork1    = null;
		String            strWork3    = null;

		String            RsrcCd      = "";
		String            LangCd      = "";
		String            DsnCd       = "";
		String            ItemId      = "";

		String            strRsrcCd   = null;
		String            strRsrcName = null;

		int					nRet1 = 0;
		int					nRet2 = 0;
		int					qryFlag = 0;

		ArrayList<String>	qryAry = null;

		File shfile=null;
		File mFile= null;
		OutputStreamWriter writer = null;
		String[] strAry = null;

		Runtime  mrun = null;
		Process mp = null;

		int               j           = 0;
		ArrayList<HashMap<String,String>> rtList = new ArrayList<HashMap<String,String>>();

		try {
			conn = connectionContext.getConnection();
			rtList.clear();


			rst = new HashMap<String,String>();
			rst.put("cm_dirpath",file.get("cm_dirpath"));
			rst.put("cr_rsrcname",file.get("cr_rsrcname"));
			rst.put("cr_story",file.get("cr_story"));
			rst.put("cm_jobname", file.get("cm_jobname"));
			rst.put("jawon", file.get("jawon"));

			strQuery.setLength(0);
			strQuery.append("select cm_codename from cmm0020 where cm_macode='CHECKIN' and cm_micode=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, file.get("reqcd"));

            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            if (rs.next()) {
            	rst.put("checkin", rs.getString("cm_codename"));
            }
            rs.close();
            pstmt.close();

			//rst.put("checkin", etcData.get("QryName"));
			rst.put("prcseq", file.get("prcseq"));
			rst.put("cr_lstver",file.get("cr_lstver"));
			rst.put("cr_itemid",file.get("cr_itemid"));
			rst.put("sysgb", etcData.get("SysGb"));
			rst.put("cr_syscd", file.get("cr_syscd"));
			rst.put("cr_rsrccd",file.get("cr_rsrccd"));
			rst.put("cr_langcd",file.get("cr_langcd"));
			rst.put("cr_dsncd",file.get("cr_dsncd"));
			rst.put("cr_jobcd",file.get("cr_jobcd"));
			rst.put("baseitem",file.get("cr_itemid"));
			rst.put("cm_info",file.get("cm_info"));
			rst.put("cr_acptno",file.get("cr_acptno"));
			rst.put("cr_aftver",file.get("cr_aftver"));
			if (file.get("cr_sayu") != null && file.get("cr_sayu") != ""){
				rst.put("cr_sayu",file.get("cr_sayu"));
			}
			else{
				rst.put("cr_sayu",etcData.get("sayu"));
			}
			rst.put("reqcd",file.get("reqcd"));
			reqCnt = addCnt + 1;
			rst.put("seq", Integer.toString(reqCnt));
			rtList.add(addCnt++, rst);
			rst = null;
			svCnt = addCnt - 1;
			InCd = false;
			BldCd = false;
			RelCd = false;
			ErrSw = false;
			if (!file.get("reqcd").equals("05")) {
				if (file.get("cm_info").substring(0,1).equals("1") &&
					file.get("cm_info").substring(6,7).equals("0")) BldCd = true;
				if (file.get("cm_info").substring(21,22).equals("1")) InCd = true;
				if (file.get("cm_info").substring(20,21).equals("1")) RelCd = true;


				if (InCd == true || BldCd == true || RelCd == true) {
					strErr = bldcdChk(file.get("cr_syscd"),file.get("cr_jobcd"),file.get("cr_rsrccd"),file.get("cr_itemid"),InCd,BldCd,RelCd,conn);

					if (strErr != "") {
						for (j=rtList.size()-1;j>=svCnt;j--) {
							rtList.remove(j);
						}
				    	rst = new HashMap<String,String>();
				    	rst.put("cr_itemid","ERROR");
						rst.put("cm_dirpath",strErr);
						rst.put("cr_rsrcname",file.get("cr_rsrcname"));
						rtList.add(svCnt, rst);
						rst = null;
						ErrSw = true;
				    }
				}
			}
			//ecamsLogger.debug("+++++++++cm_info 1+++"+file.get("cr_rsrcname")+","+file.get("cm_info"));
			if (ErrSw == false && (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")) && file.get("cm_info").substring(26,27).equals("1") && (etcData.get("ReqCd").equals("03") || etcData.get("TstSw").equals("0"))) {
				//ecamsLogger.debug("+++++++++SCM_LIST CALL START+++"+file.get("cr_rsrcname"));
				makeFile = "list" + file.get("cr_rsrcname");
				strFile = strTmpPath + makeFile;

				shfile=null;
				shfile = new File(strTmpPath + makeFile+".sh");              //File 불러온다.
				if( !(shfile.isFile()) )              //File이 없으면
				{
					shfile.createNewFile();          //File 생성
				}
				//FileOutputStream fos= new FileOutputStream(file);   //File을 불러옴(덮어씀.)
				//fos.write(str.getBytes());              //File List(에러부분)을 Wirte
				//fos.close();

				writer = new OutputStreamWriter( new FileOutputStream(strTmpPath + makeFile+".sh"));
				writer.write("cd "+strBinPath +"\n");
				writer.write("./PFHttp_Call " + file.get("cr_syscd") + " SCM_LIST " + file.get("cr_rsrcname") + " \"\" " + makeFile +"\n");
				writer.write("exit $?\n");
				writer.close();

				strAry = new String[3];

				strAry[0] = "chmod";
				strAry[1] = "777";
				strAry[2] = strTmpPath + makeFile+".sh";

				mrun = Runtime.getRuntime();

				//ecamsLogger.debug("====chmod===="+"chmod "+"777 "+strTmpPath + makeFile+".sh"+" \n");
				mp = mrun.exec(strAry);
				mp.waitFor();

				//ecamsLogger.debug("====chmod return===="+Integer.toString(p.exitValue())+" \n");

				mFile = new File(strFile);
		        if (!mFile.isFile() || !mFile.exists()) {
		        	mFile.delete();
		        }
		        mFile = null;

				mrun = Runtime.getRuntime();

				strAry = new String[2];

				strAry[0] = "/bin/sh";
				strAry[1] = strTmpPath + makeFile+".sh";
				//strAry[2] = ">"+strTmpPath + makeFile+".log";
				//ecamsLogger.debug("+++++++strAry 0++++"+strAry[0]+ " "+ strAry[1]);
				mp = mrun.exec(strAry);
				mp.waitFor();

				//ecamsLogger.debug("====return===="+Integer.toString(p.exitValue())+" \n");
				if (mp.exitValue() != 0) {
					for (j=rtList.size()-1;j>=svCnt;j--) {
						rtList.remove(j);
					}
					//strFile = "c:\\eCAMS\\list"+file.get("cr_rsrcname");
					mFile = new File(strFile);
			        if (!mFile.isFile() || !mFile.exists()) {
					   strErr = "["+file.get("cr_rsrcname")+"]에 대하여 Tmax FrameWork에 체크인목록을 요구한 결과 오류가 리턴되었습니다.";
			        } else {
			        	BufferedReader in = null;
			        	in = new BufferedReader(new InputStreamReader(new FileInputStream(mFile),"euc-kr"));
			        	strErr = "["+file.get("cr_rsrcname")+"] Tmax FrameWork 체크인목록요구 결과 오류 리턴 : ";
			            String str = null;
			            while ((str = in.readLine()) != null) {
			                if (str.length() > 0) {
			                	strErr = strErr + str;
			                }
			            }
			        }
					rst = new HashMap<String,String>();
			    	rst.put("cr_itemid","ERROR");
					rst.put("cm_dirpath",strErr);
					rst.put("cr_rsrcname",file.get("cr_rsrcname"));
					rtList.add(svCnt, rst);
					rst = null;
					ErrSw = true;

					shfile.delete();
				}
				else{
					//ecamsLogger.debug("++++++++++shell file no delete+++++++++"+ shfile);
					shfile.delete();
				}
				//strFile = "c:\\eCAMS\\list"+file.get("cr_rsrcname");
                if (ErrSw == false) {
					//ecamsLogger.debug("+++++++++SCM_LIST CALL E N D+++"+file.get("cr_rsrcname"));
					mFile = new File(strFile);
			        if (!mFile.isFile() || !mFile.exists()) {
						for (j=rtList.size()-1;j>=svCnt;j--) {
							rtList.remove(j);
						}
						//ecamsLogger.debug("++++++++strFile++++++++++"+strFile);
						strErr = "["+file.get("cr_rsrcname")+"]에 대한 파일을 읽을 수 없습니다.";
				    	rst = new HashMap<String,String>();
						rst.put("cr_itemid","ERROR");
						rst.put("cm_dirpath",strErr);
						rst.put("cr_rsrcname",file.get("cr_rsrcname"));
						rtList.add(svCnt, rst);
						rst = null;
						//ecamsLogger.debug(strErr);

						ErrSw = true;
			        } else {
				        BufferedReader in = null;
				        //PrintWriter out = null;
			            in = new BufferedReader(new FileReader(mFile));

			            String str = null;
			            while ((str = in.readLine()) != null) {
			                if (str.length() > 0) {
			                	//ecamsLogger.debug("====str===="+str);
			                	String strPath = null;
			                	String strName = null;
			                	strWork1 = str;
			                	int    x = 0;

			                	x = strWork1.lastIndexOf("/");
			                	if (x >= 0){
			                		strPath = strWork1.substring(0, x);
			                		strName = strWork1.substring(x+1);
			                	}
			                    if (strPath == null || strName == null) {
									for (j=rtList.size()-1;j>=svCnt;j--) {
										rtList.remove(j);
									}
									strErr = "["+file.get("cr_rsrcname")+"]에 대한 파일목록정보가 정확하지 않습니다.";
							    	rst = new HashMap<String,String>();
									rst.put("cr_itemid","ERROR");
									rst.put("cm_dirpath",strErr);
									rst.put("cr_rsrcname",file.get("cr_rsrcname"));
									rtList.add(svCnt, rst);
									rst = null;

									ErrSw = true;
			                    }
			                    if (ErrSw == false) {
				                    RsrcCd = rsrccdSel(file.get("cr_syscd"),strName,conn);
				                    if (RsrcCd == null) {
										for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}

										strErr = "["+strName+"]에 대한 프로그램종류정보가 등록되지 않았습니다. [관리자연락]";
								    	rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",file.get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										//ecamsLogger.debug(strErr);
										ErrSw = true;
				                	}
			                    }
			                    if (ErrSw == false) {
				                    LangCd = langSel(file.get("cr_syscd"),RsrcCd,conn);
				                    if (LangCd == null) {
										for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}
										strErr = "["+strName+"]에 대한 사용언어정보가 등록되지 않았습니다. [관리자연락]";
								    	rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",file.get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										//ecamsLogger.debug(strErr);
										ErrSw = true;
				                	}
			                    }
			                    if (ErrSw == false) {
			                    	do{
			                    		DsnCd = DsnCdSel(etcData.get("UserId"),file.get("cr_syscd"),strPath,conn);

			                    		if (DsnCd == null){
			                    			DsnCd = "null";
			                    		}
			                    	}while(DsnCd.equals("ORA-00001"));

				                    if (DsnCd.equals("null")) {

										for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}

										strErr = "["+strPath+"]에 대한 디렉토리정보가 등록되지 않았습니다. [관리자연락]";
								    	rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",file.get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										//ecamsLogger.debug(strErr);
										ErrSw = true;
				                    }
			                    }
			                    //(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String LangCd,String BaseItem)

			                    if (ErrSw == false) {
			                    	ItemId = null;
			                    	strQuery.setLength(0);
			            			strQuery.append("select cr_itemid from cmr0020                      \n");
			            			strQuery.append(" where	cr_syscd=? and cr_dsncd=? and cr_rsrcname=? \n");
			            			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			                        pstmt = conn.prepareStatement(strQuery.toString());
			                        pstmt.setString(1, file.get("cr_syscd"));
			                        pstmt.setString(2, DsnCd);
			                        pstmt.setString(3, strName);
			                        //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
			                        rs = pstmt.executeQuery();
			                        if (rs.next()) {
			                        	ItemId = rs.getString("cr_itemid");
			                        }
			                        rs.close();
			                        pstmt.close();

			                        if (ItemId == null) {
				                    	do{
				                    		ItemId = Cmr0020_Copy(etcData.get("UserId"),file.get("cr_syscd"),DsnCd,strName,RsrcCd,LangCd,file.get("cr_itemid"),conn);

				                    		if (ItemId == null){
				                    			ItemId = "null";
				                    		}

				                    	}while (ItemId.equals("ORA-00001"));
			                        }

				                    if (ItemId.equals("null")) {

										for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}

										strErr = "["+strName+"]에 대한 프로그램등록에 실패하였습니다.";
								    	rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",file.get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										//ecamsLogger.debug(strErr);
										ErrSw = true;
				                    }
			                    }
			                    if (ErrSw == false) {
					                strQuery.setLength(0);
									strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
									strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,C.CM_CODENAME as jawon,   \n");
									strQuery.append("       a.cr_story,D.CM_INFO,lpad(d.cm_prcstep,4,'0') prcseq,         \n");
									strQuery.append("       nvl(d.cm_vercnt,50) vercnt,e.cm_codename checkin            \n");
									strQuery.append("from CMM0036 D,cmm0070 b,cmr0020 a,CMM0020 C,cmm0020 e             \n");
									strQuery.append("WHERE a.cr_itemid = ?                                              \n");
									strQuery.append("  and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd            \n");
									strQuery.append("  and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd              \n");
									strQuery.append("  and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd              \n");
									strQuery.append("  and e.cm_macode='CHECKIN' and e.cm_micode=decode(a.cr_lstver,0,'03','04') \n");

									pstmt = conn.prepareStatement(strQuery.toString());
									//pstmt =  new LoggableStatement(conn, strQuery.toString());

						            pstmt.setString(1, ItemId);

						            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
						            rs = pstmt.executeQuery();

						            if (rs.next()) {
						            	rst = new HashMap<String,String>();
						    			rst.put("cm_dirpath",rs.getString("cm_dirpath"));
						    			rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
						    			rst.put("cr_story",rs.getString("cr_story"));
						    			rst.put("cm_jobname", file.get("cm_jobname"));
						    			rst.put("jawon", rs.getString("jawon"));
						    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04"))
						    				rst.put("checkin", rs.getString("checkin"));
						    			else
						    				rst.put("checkin", file.get("checkin"));
						    			rst.put("prcseq", rs.getString("prcseq"));
						    			rst.put("cr_lstver",rs.getString("cr_lstver"));
						    			rst.put("cr_itemid",ItemId);
						    			rst.put("sysgb", etcData.get("SysGb"));
						    			rst.put("cr_syscd", file.get("cr_syscd"));
						    			rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
						    			rst.put("cr_langcd",rs.getString("cr_langcd"));
						    			rst.put("cr_dsncd",rs.getString("cr_dsncd"));
						    			rst.put("cr_jobcd",file.get("cr_jobcd"));
						    			rst.put("baseitem",file.get("cr_itemid"));
						    			rst.put("cm_info",rs.getString("cm_info"));
						    			rst.put("cr_acptno",file.get("cr_acptno"));
						    			if (file.get("cr_sayu") != null && file.get("cr_sayu") != ""){
						    				rst.put("cr_sayu",file.get("cr_sayu"));
						    			}
										else{
											rst.put("cr_sayu",etcData.get("sayu"));
										}
						    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")) {
						    				if (rs.getInt("cr_lstver")== 0){
						    					rst.put("reqcd","03");
						    				}
						    				else{
						    					rst.put("reqcd","04");
						    				}
						    			}
						    			else{
						    				rst.put("reqcd",file.get("reqcd"));
						    			}

										if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
											rst.put("cr_aftver", "1");
										}
										else{
											rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
										}
										reqCnt = addCnt + 1;
										rst.put("seq", Integer.toString(reqCnt));
						    			rtList.add(addCnt++, rst);
						    			rst = null;
						            }
						            else {

										for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}

										strErr = "["+strName+"]에 대한 프로그램정보를 찾을 수가 없습니다.";
								    	rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",file.get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										//ecamsLogger.debug(strErr);
										ErrSw = true;
						            }
						            rs.close();
						            pstmt.close();
			                    }
			                }
			                if (ErrSw == true) break;
			            }
			            if (in != null){
			            	in.close();
			            }
			        }
                }
			}
			//ecamsLogger.debug("+++++++++CHECK-IN MAKE E N D+++"+file.get("cr_rsrcname"));
			//ecamsLogger.debug("+++++++++cm_info 2+++"+file.get("cr_rsrcname")+","+file.get("cm_info"));
			if (ErrSw == false && file.get("cm_info").substring(3,4).equals("1") && (etcData.get("ReqCd").equals("03") || etcData.get("TstSw").equals("0"))) {
				strQuery.setLength(0);
				strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
				strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
				strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
				strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
				strQuery.append("   and b.cm_factcd='04'                                  \n");
				strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
				strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, file.get("cr_syscd"));
		        pstmt.setString(2, file.get("cr_rsrccd"));

	            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();

		        while (rs.next()) {
		        	if (file.get("cr_rsrcname").indexOf(".") > -1) {
		        		strWork1 = file.get("cr_rsrcname").substring(0,file.get("cr_rsrcname").indexOf("."));
		        	}
		        	else{
		        		strWork1 = file.get("cr_rsrcname");
		        	}
		        	//ecamsLogger.debug("+++++++++++++++strWork1=========>"+strWork1);
		        	if (!rs.getString("cm_basename").equals("*")) {
		        		strWork3 = rs.getString("cm_basename");
		        		while (strWork3 == "") {
		        			j = strWork3.indexOf("*");
		        			if (j > -1) {
		        				strWork3 = strWork3.substring(j + 1);
		        				if(strWork3.equals("*")){
		        					strWork3 = "";
		        				}
		        			} else {
		        				strWork3 = "";
		        			}
		        			if (strWork3 == ""){
		        				break;
		        			}
		        		}
		        	}

		        	/*
		        	if (rs.getString("cm_cmdyn").equals("Y")) {

		        		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
		        		ecamsLogger.debug("++++++++strWork1++++++"+strWork1);
		        		if (strWork1.substring(0,1).equals("'")) strWork1 = strWork1.substring(1);
			        	if (strWork1.substring(strWork1.length() - 1).equals("'")) strWork1 = strWork1.substring(0,strWork1.length() -1);
		        		strQuery.setLength(0);
		        		strQuery.append("select ? relatId  from dual                           \n");
		        		pstmt = conn.prepareStatement(strQuery.toString());
		        		pstmt =  new LoggableStatement(conn, strQuery.toString());
		        		pstmt.setString(1, strWork1);
		        		ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
		    	        rs2 = pstmt.executeQuery();
		    	        if (rs2.next()) {
		    	        	strWork1 = rs2.getString("relatId");
		    	        }
		    	        if (rs.getString("cm_samersrc").equals("71")){
		    	        	strWork1 = "libpfmDbio" + strWork1.substring(0,strWork1.length() - 7) + ".so";
		    	        }
		    	        ecamsLogger.debug("++++++++strWork1  2++++++"+strWork1);
		        	} else strWork1 = rs.getString("cm_samename").replace("*",strWork1);
		        	strRsrcName = strWork1;
		        	strRsrcCd = rs.getString("cm_samersrc");
		        	ecamsLogger.debug("++++++++strRsrcName 3++++++"+strWork1);
		        	*/

		        	strRsrcCd = rs.getString("cm_samersrc");

		        	strQuery.setLength(0);
					strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
					strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon,a.cr_story, \n");
					strQuery.append("       d.CM_INFO,lpad(d.cm_prcstep,4,'0') prcseq,                   \n");
					strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_codename checkin             \n");
				   	strQuery.append("  from cmm0070 b,cmr0020 a,cmr0022 c,cmm0036 d,cmm0020 e,cmm0020 f  \n");
				   	strQuery.append(" where a.cr_syscd=? and a.cr_rsrccd=?                     \n");
				   	strQuery.append("   and upper(a.cr_rsrcname)= upper( \n");
				   	if (rs.getString("cm_cmdyn").equals("Y")){
				   		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
				   		qryAry = new ArrayList<String>();
				   		nRet1 = 0;
				   		nRet2 = 0;
				   		while( (nRet2 = strWork1.indexOf("'")) != -1){
				   			if (qryFlag == 0){
				   				strQuery.append(strWork1.substring(0, nRet2)+ " \n");
				   				strWork1 = strWork1.substring(nRet2+1);
				   				qryFlag = 1;
				   			}
				   			else{
				   				qryAry.add(strWork1.substring(0, nRet2));
				   				strWork1 = strWork1.substring(nRet2+1);
				   				strQuery.append(" ? \n");
				   				qryFlag = 0;
				   			}
				   		}
				   		strQuery.append(strWork1+ " ) \n");
				   	}
				   	else{
				   		strQuery.append(" ? ) \n");
				   	}
				   	//strQuery.append("   and upper(a.cr_rsrcname)=upper(?)                      \n");
				   	strQuery.append("   and a.cr_itemid=c.cr_itemid                            \n");
				   	strQuery.append("   and c.cr_baseitem=?                                    \n");
				   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
				   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
					strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
					strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=decode(a.cr_lstver,0,'03','04') \n");
				   	//pstmt =  new LoggableStatement(conn, strQuery.toString());
				   	nRet1 = 1;
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            pstmt2.setString(nRet1++, file.get("cr_syscd"));
		            pstmt2.setString(nRet1++, strRsrcCd);
		        	if (rs.getString("cm_cmdyn").equals("Y")){
		        		for (nRet2 = 0;nRet2<qryAry.size();nRet2++){
		        			pstmt2.setString(nRet1++,qryAry.get(nRet2));
		        		}
		        	}
		        	else{
				   		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
				   		pstmt2.setString(nRet1++,strWork1);
		        	}
		            //pstmt.setString(nRet1++, strRsrcName);

		            pstmt2.setString(nRet1++, file.get("cr_itemid"));

		            //ecamsLogger.debug(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();

		            if (rs2.next()) {
		            	boolean fileSw = false;
		            	for (int k=0;rtList.size()>k;k++) {
		            		if (rtList.get(k).get("cr_itemid").equals(rs2.getString("cr_itemid"))) {
		            			fileSw = true;
		            		}
		            	}
		            	if (fileSw == false) {
			            	rst = new HashMap<String,String>();
			    			rst.put("cm_dirpath",rs2.getString("cm_dirpath"));
			    			rst.put("cr_rsrcname",rs2.getString("cr_rsrcname"));
			    			rst.put("cr_story",rs2.getString("cr_story"));
			    			rst.put("cm_jobname", file.get("cm_jobname"));
			    			rst.put("jawon", rs2.getString("jawon"));
			    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")){
			    				rst.put("checkin", rs2.getString("checkin"));
			    			}
			    			else{
			    				rst.put("checkin", file.get("checkin"));
			    			}
			    			rst.put("prcseq", rs2.getString("prcseq"));
			    			rst.put("cr_lstver",rs2.getString("cr_lstver"));
			    			rst.put("cr_itemid",rs2.getString("cr_itemid"));
			    			rst.put("sysgb", etcData.get("SysGb"));
			    			rst.put("cr_syscd", file.get("cr_syscd"));
			    			rst.put("cr_rsrccd",rs2.getString("cr_rsrccd"));
			    			rst.put("cr_langcd",rs2.getString("cr_langcd"));
			    			rst.put("cr_dsncd",rs2.getString("cr_dsncd"));
			    			rst.put("cr_jobcd",file.get("cr_jobcd"));
			    			rst.put("baseitem",file.get("cr_itemid"));
			    			if (file.get("cr_sayu") != null && file.get("cr_sayu") != ""){
			    				rst.put("cr_sayu",file.get("cr_sayu"));
			    			}
							else{
								rst.put("cr_sayu",etcData.get("sayu"));
							}
			    			rst.put("cm_info",rs2.getString("cm_info"));
			    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")) {
			    				if (rs2.getInt("cr_lstver")== 0){
			    					rst.put("reqcd","03");
			    				}
			    				else{
			    					rst.put("reqcd","04");
			    				}
			    			}
			    			else{
			    				rst.put("reqcd",file.get("reqcd"));
			    			}
			    			rst.put("cr_acptno",file.get("cr_acptno"));
			    			if (rs2.getInt("cr_lstver") >= rs2.getInt("vercnt")) {
							   rst.put("cr_aftver", "1");
							}
			    			else{
			    				rst.put("cr_aftver", Integer.toString(rs2.getInt("cr_lstver")+1));
			    			}
			    			reqCnt = addCnt + 1;
							rst.put("seq", Integer.toString(reqCnt));
			    			rtList.add(addCnt++, rst);
			    			rst = null;
		            	}
		            }
		            else {
						for (j=rtList.size()-1;j>=svCnt;j--) {
							rtList.remove(j);
						}
		            	strErr = "["+strRsrcName+"]에 대한 프로그램정보를 찾을 수가 없습니다.";
						rst = new HashMap<String,String>();
						rst.put("cr_itemid","ERROR");
						rst.put("cm_dirpath",strErr);
						rst.put("cr_rsrcname",strRsrcName);
						rtList.add(svCnt, rst);
						rst = null;
						//ecamsLogger.debug(strErr);




						ErrSw = true;
		            }
		            pstmt2.close();
		            rs2.close();
		        }
		        rs.close();
		        pstmt.close();
			}    // 동시신청파일 처리 end
			if (ErrSw == false && etcData.get("ReqCd").equals("04") && etcData.get("TstSw").equals("1")) {
			   	strQuery.setLength(0);
				strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
				strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon,a.cr_story, \n");
				strQuery.append("       d.CM_INFO,lpad(d.cm_prcstep,4,'0') prcseq,                   \n");
				strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_codename checkin             \n");
			   	strQuery.append("  from cmm0070 b,cmr0020 a,cmr1010 c,cmm0036 d,cmm0020 e,cmm0020 f  \n");
			   	strQuery.append(" where c.cr_acptno=? and c.cr_status<>'3'                 \n");
			   	strQuery.append("   and c.cr_baseitem=? and c.cr_itemid<>?                 \n");
			   	strQuery.append("   and c.cr_itemid=a.cr_itemid                            \n");
			   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
			   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
				strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
				strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=decode(a.cr_lstver,0,'03','04') \n");
			   	//pstmt =  new LoggableStatement(conn, strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, file.get("cr_acptno"));
	            pstmt.setString(2, file.get("cr_itemid"));
	            pstmt.setString(3, file.get("cr_itemid"));

	    //        ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

	            while (rs.next()) {
	            	boolean fileSw = false;
	            	for (int k=0;rtList.size()>k;k++) {
	            		if (rtList.get(k).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
	            			fileSw = true;
	            		}
	            	}
	            	if (fileSw == false) {
		            	rst = new HashMap<String,String>();
		    			rst.put("cm_dirpath",rs.getString("cm_dirpath"));
		    			rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
		    			rst.put("cr_story",rs.getString("cr_story"));
		    			rst.put("cm_jobname", file.get("cm_jobname"));
		    			rst.put("jawon", rs.getString("jawon"));
		    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")){
		    				rst.put("checkin", rs.getString("checkin"));
		    			}
		    			else{
		    				rst.put("checkin", file.get("checkin"));
		    			}
		    			rst.put("prcseq", rs.getString("prcseq"));
		    			rst.put("cr_lstver",rs.getString("cr_lstver"));
		    			rst.put("cr_itemid",rs.getString("cr_itemid"));
		    			rst.put("sysgb", etcData.get("SysGb"));
		    			rst.put("cr_syscd", file.get("cr_syscd"));
		    			rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
		    			rst.put("cr_langcd",rs.getString("cr_langcd"));
		    			rst.put("cr_dsncd",rs.getString("cr_dsncd"));
		    			rst.put("cr_jobcd",file.get("cr_jobcd"));
		    			rst.put("baseitem",file.get("cr_itemid"));
		    			rst.put("cm_info",rs.getString("cm_info"));
		    			rst.put("cr_acptno",file.get("cr_acptno"));
		    			if (file.get("cr_sayu") != null && file.get("cr_sayu") != ""){
		    				rst.put("cr_sayu",file.get("cr_sayu"));
		    			}
						else{
							rst.put("cr_sayu",etcData.get("sayu"));
						}
		    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")) {
		    				if (rs.getInt("cr_lstver")== 0){
		    					rst.put("reqcd","03");
		    				}
		    				else{
		    					rst.put("reqcd","04");
		    				}
		    			}
		    			else{
		    				rst.put("reqcd",file.get("reqcd"));
		    			}
		    			if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
						   rst.put("cr_aftver", "1");
						}
		    			else{
		    				rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
		    			}
		    			reqCnt = addCnt + 1;
						rst.put("seq", Integer.toString(reqCnt));
		    			rtList.add(addCnt++, rst);
		    			rst = null;
	            	}
	            }
	            rs.close();
	            pstmt.close();
			}    // 테스트요청건에 대한 real요청
			if (ErrSw == false && file.get("cm_info").substring(8,9).equals("1")) {
		        int readCnt = 0;
			   	strQuery.setLength(0);
				strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
				strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon,a.cr_story, \n");
				strQuery.append("       d.CM_INFO,lpad(d.cm_prcstep,4,'0') prcseq,                   \n");
				strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_codename checkin             \n");
			   	strQuery.append("  from cmm0070 b,cmr0020 a,cmd0011 c,cmm0036 d,cmm0020 e,cmm0020 f  \n");
			   	strQuery.append(" where c.cd_itemid=?                                      \n");
			   	strQuery.append("   and c.cd_prcitem=a.cr_itemid                          \n");
			   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
			   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
				strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
				strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=decode(a.cr_lstver,0,'03','04') \n");
			   	//pstmt =  new LoggableStatement(conn, strQuery.toString());
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, file.get("cr_itemid"));

	            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

	            while (rs.next()) {
	            	boolean fileSw = false;
	            	++readCnt;
	            	for (int k=0;rtList.size()>k;k++) {
	            		if (rtList.get(k).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
	            			fileSw = true;
	            		}
	            	}
	            	if (fileSw == false) {
		            	if (rs.getString("cm_info").substring(0,1).equals("1") && rs.getString("cm_info").substring(6,7).equals("0")) {
		            		strErr = bldcdChk(file.get("cr_syscd"),file.get("cr_jobcd"),file.get("cr_rsrccd"),file.get("cr_itemid"),InCd,BldCd,RelCd,conn);
		            		if (strErr != "") {
								for (j=rtList.size()-1;j>=svCnt;j--) {
									rtList.remove(j);
								}
						    	rst = new HashMap<String,String>();
								rst.put("cm_dirpath",strErr);
								rst.put("cr_rsrcname",file.get("cr_rsrcname"));
								rtList.add(svCnt, rst);
								ErrSw = true;
						    }
		            	}
		            	if (ErrSw == true) break;

		            	rst = new HashMap<String,String>();
		    			rst.put("cm_dirpath",rs.getString("cm_dirpath"));
		    			rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
		    			if (rs.getString("cr_story") != null && rs.getString("cr_story") != "")
		    				rst.put("cr_story",rs.getString("cr_story"));
		    			rst.put("cm_jobname", file.get("cm_jobname"));
		    			rst.put("jawon", rs.getString("jawon"));
		    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")){
		    				rst.put("checkin", rs.getString("checkin"));
		    			}
		    			else{
		    				rst.put("checkin", file.get("checkin"));
		    			}
		    			rst.put("prcseq", rs.getString("prcseq"));
		    			rst.put("cr_lstver",rs.getString("cr_lstver"));
		    			rst.put("cr_itemid",rs.getString("cr_itemid"));
		    			rst.put("sysgb", etcData.get("SysGb"));
		    			rst.put("cr_syscd", file.get("cr_syscd"));
		    			rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
		    			rst.put("cr_langcd",rs.getString("cr_langcd"));
		    			rst.put("cr_dsncd",rs.getString("cr_dsncd"));
		    			rst.put("cr_jobcd",file.get("cr_jobcd"));
		    			rst.put("baseitem",file.get("cr_itemid"));
		    			rst.put("cm_info",rs.getString("cm_info"));
		    			rst.put("cr_acptno",file.get("cr_acptno"));
		    			if (file.get("cr_sayu") != null && file.get("cr_sayu") != ""){
		    				rst.put("cr_sayu",file.get("cr_sayu"));
		    			}
						else{
							rst.put("cr_sayu",etcData.get("sayu"));
						}
		    			if (file.get("reqcd").equals("03") || file.get("reqcd").equals("04")) {
		    				if (rs.getInt("cr_lstver")== 0){
		    					rst.put("reqcd","03");
		    				}
		    				else{
		    					rst.put("reqcd","04");
		    				}
		    			}
		    			else{
		    				rst.put("reqcd",file.get("reqcd"));
		    			}
		    			if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
						   rst.put("cr_aftver", "1");
						}
		    			else{
		    				rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
		    			}
		    			reqCnt = addCnt + 1;
						rst.put("seq", Integer.toString(reqCnt));
		    			rtList.add(addCnt++, rst);
		    			rst = null;
	            	}
	            }
	            pstmt.close();
	            rs.close();

	            if (readCnt == 0) {
					for (j=rtList.size()-1;j>=svCnt;j--) {
						rtList.remove(j);
					}
					strErr = "["+strRsrcName+"]에 대한 실행모듈정보를 찾을 수가 없습니다.";
			    	rst = new HashMap<String,String>();
					rst.put("cr_itemid","ERROR");
					rst.put("cm_dirpath",strErr);
					rst.put("cr_rsrcname",strRsrcName);
					rtList.add(svCnt, rst);
					rst = null;
					//ecamsLogger.debug(strErr);
					ErrSw = true;
	            }
			}    // 실행모듈체크 처리 end

			//ecamsLogger.debug("++++++++CHECK-IN MAKE E N D+++"+file.get("cr_rsrcname"));

			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getDownFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.getDownFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getDownFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getDownFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.getDownFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getDownFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getDownFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
