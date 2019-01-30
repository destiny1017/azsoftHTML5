package html.app.administrator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import app.common.CodeInfo;
import app.eCmm.Cmm0100;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/administrator/CodeInfo")
public class CodeInfoServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	CodeInfo codeInfo = new CodeInfo();
	Cmm0100 cmm0100 = new Cmm0100();
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
				case "GETCODEINFO" :
					response.getWriter().write( getCodeInfo(request) );
					break;
				case "GETCODELIST" :
					response.getWriter().write( getCodeList(request) );
					break;
				case "GETCODENAME" :
					response.getWriter().write( getCodeName(request) );
					break;
				case "SETCODEVALUE" :
					response.getWriter().write( setCodeValue(request) );
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> codeInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "codeInfoData");
		return gson.toJson(codeInfo.getCodeInfo(codeInfoMap.get("cm_macode"),codeInfoMap.get("selMsg"), codeInfoMap.get("closeYN")));
	}
	
	private String getCodeList(HttpServletRequest request) throws SQLException, Exception {
		System.out.println("getCodeList");
		HashMap<String, String> searchInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "searchInfoData");
		System.out.println(searchInfoMap);
		return gson.toJson(cmm0100.getCodeList(searchInfoMap));
	}
	
	private String getCodeName(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> codeNameInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "codeNameInfoData");
		return gson.toJson(cmm0100.getCodeName(codeNameInfoMap.get("macode")));
	}
	private String setCodeValue(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> codeValueInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "codeValueInfoData");
		return gson.toJson(cmm0100.setCodeValue(codeValueInfoMap));
	}
}
