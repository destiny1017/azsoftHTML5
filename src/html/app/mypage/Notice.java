package html.app.mypage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import app.common.UserInfo;
import app.common.CodeInfo;
import app.common.SystemPath;
import app.eCmm.Cmm2100;
import app.eCmm.Cmm2101;
import html.app.common.ParsingCommon;
import sun.security.ssl.HandshakeMessage;

@WebServlet("/webPage/mypage/Notice")
public class Notice extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	CodeInfo codeinfo = new CodeInfo();
	SystemPath systempath = new SystemPath();
	Cmm2100 cmm2100 = new Cmm2100();
	Cmm2101 cmm2101 = new Cmm2101();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "UserInfo" :
					response.getWriter().write( checkAdmin(paramMap) );
					break;
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(paramMap) );
					break;
				case "Cmm2100" :
					response.getWriter().write( getSqlQry(paramMap) );
					break;
				case "SystemPath" :
					response.getWriter().write( getSysPass(paramMap) );
					break;
				case "insertNoticeFileInfo" :
					response.getWriter().write( insertNoticeFileInfo(paramMap) );
					break;
				case "getFileList" :
					response.getWriter().write( getFileList(paramMap) );
					break;
				case "getNoticeFolderPath" :
					response.getWriter().write( getNoticeFolderPath(paramMap) );
					break;
				case "deleteNoticeFile" :
					response.getWriter().write( deleteNoticeFile(paramMap) );
					break;
				case "BIG_DATA_LOADING_TEST" :
					response.getWriter().write( getBigDataTest(paramMap) );
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			requestType = null;
		}
		
	}
	
	private String checkAdmin(HashMap paramMap) throws SQLException, Exception {
		String userId = null;
		userId = (String)paramMap.get("UserId");
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String getCodeInfo(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("FIND","","N"));
	}
	
	private String getSqlQry(HashMap paramMap) throws SQLException, Exception {
		String cboFindMicode = null;
		cboFindMicode = (String)paramMap.get("CboFind_micode");
		String txtFindText = null;
		txtFindText = (String)paramMap.get("TxtFind_text");
		String dateStD = null;
		dateStD = (String)paramMap.get("strStD");
		String dateEdD = null;
		dateEdD = (String)paramMap.get("strEdD");
		return gson.toJson(cmm2100.get_sql_Qry(cboFindMicode, txtFindText, dateStD, dateEdD));
	}
	
	private String getSysPass(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(systempath.getTmpDir("99"));
	}
	
	private String insertNoticeFileInfo(HashMap paramMap) throws SQLException, Exception {
		ArrayList<HashMap<String, String>> tmpList = ParsingCommon.parsingRequestJsonParamToArrayList((String)paramMap.get("fileInfo").toString());
		
		ArrayList<HashMap<String, String>> fileList = new  ArrayList<HashMap<String,String>>();
		for(int i=0; i<tmpList.size(); i++) {
			HashMap<String, String> addMap = new HashMap<>();
			addMap.put("acptno", tmpList.get(i).get("noticeAcptno"));
			addMap.put("filegb", "1");
			addMap.put("realName", tmpList.get(i).get("fileName"));
			addMap.put("saveName", tmpList.get(i).get("noticeAcptno")+"."+(i + 1));
			fileList.add(addMap);
		}
		return gson.toJson(cmm2101.setDocFile(fileList));
	}
	
	private String getFileList(HashMap paramMap) throws SQLException, Exception {
		String acptno = (String)paramMap.get("acptno");
		return gson.toJson(cmm2100.getFileList(acptno));
	}
	
	
	private String getNoticeFolderPath(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(systempath.getTmpDir("01"));
	}

	private String deleteNoticeFile(HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String> fileData = ParsingCommon.parsingRequestJsonParamToHashMap((String)paramMap.get("fileData").toString());
		return gson.toJson(cmm2101.removeDocFileHtml(fileData));
	}
	
	private String getBigDataTest(HashMap paramMap) throws SQLException, Exception {
		
		
		for(long i=0; i<1000000000; i++) {
			if(i%10000 ==0 ) System.out.println("데이터가져오는중...  [i] = " + i);
		}
		for(long i=0; i<1000000000; i++) {
			if(i%10000 ==0 ) System.out.println("데이터가져오는중...  [i] = " + i);
		}
		for(long i=0; i<1000000000; i++) {
			if(i%10000 ==0 ) System.out.println("데이터가져오는중...  [i] = " + i);
		}
		for(long i=0; i<1000000000; i++) {
			if(i%10000 ==0 ) System.out.println("데이터가져오는중...  [i] = " + i);
		}
		for(long i=0; i<1000000000; i++) {
			
			if(i%10000 ==0 ) System.out.println("데이터가져오는중...  [i] = " + i);
		}
		
		return gson.toJson("OK");
	}
	
}
