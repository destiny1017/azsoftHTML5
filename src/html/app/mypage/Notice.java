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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "UserInfo" :
					response.getWriter().write( checkAdmin(request) );
					break;
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(request) );
					break;
				case "Cmm2100" :
					response.getWriter().write( getSqlQry(request) );
					break;
				case "SystemPath" :
					response.getWriter().write( getSysPass(request) );
					break;
				case "insertNoticeFileInfo" :
					response.getWriter().write( insertNoticeFileInfo(request) );
					break;
				case "getFileList" :
					response.getWriter().write( getFileList(request) );
					break;
				case "getNoticeFolderPath" :
					response.getWriter().write( getNoticeFolderPath(request) );
					break;
				case "deleteNoticeFile" :
					response.getWriter().write( deleteNoticeFile(request) );
					break;
				case "BIG_DATA_LOADING_TEST" :
					response.getWriter().write( getBigDataTest(request) );
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
	
	private String checkAdmin(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("FIND","","N"));
	}
	
	private String getSqlQry(HttpServletRequest request) throws SQLException, Exception {
		String cboFindMicode = null;
		cboFindMicode = ParsingCommon.parsingRequestJsonParamToString(request, "CboFind_micode");
		String txtFindText = null;
		txtFindText = ParsingCommon.parsingRequestJsonParamToString(request, "TxtFind_text");
		String dateStD = null;
		dateStD = ParsingCommon.parsingRequestJsonParamToString(request, "strStD");
		String dateEdD = null;
		dateEdD = ParsingCommon.parsingRequestJsonParamToString(request, "strEdD");
		return gson.toJson(cmm2100.get_sql_Qry(cboFindMicode, txtFindText, dateStD, dateEdD));
	}
	
	private String getSysPass(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(systempath.getTmpDir("99"));
	}
	
	private String insertNoticeFileInfo(HttpServletRequest request) throws SQLException, Exception {
		ArrayList<HashMap<String, String>> tmpList = ParsingCommon.parsingRequestJsonParamToArrayList(request, "fileInfo");
		
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
	
	private String getFileList(HttpServletRequest request) throws SQLException, Exception {
		String acptno = ParsingCommon.parsingRequestJsonParamToString(request, "acptno");
		return gson.toJson(cmm2100.getFileList(acptno));
	}
	
	
	private String getNoticeFolderPath(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(systempath.getTmpDir("01"));
	}

	private String deleteNoticeFile(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> fileData = ParsingCommon.parsingRequestJsonParamToHashMap(request, "fileData");
		return gson.toJson(cmm2101.removeDocFileHtml(fileData));
	}
	
	private String getBigDataTest(HttpServletRequest request) throws SQLException, Exception {
		
		
		for(long i=0; i<1000000000; i++) {
			if(i%10000 ==0 ) System.out.println("TEST  [i] = " + i);
		}
		
		return gson.toJson("OK");
	}
	
}
