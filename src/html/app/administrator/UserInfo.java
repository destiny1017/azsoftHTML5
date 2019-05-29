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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import app.common.CodeInfo;
import app.common.SysInfo;
import app.eCmm.Cmm0400;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/administrator/UserInfo")
public class UserInfo extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	CodeInfo codeinfo = new CodeInfo();
	SysInfo sysinfo = new SysInfo();
	Cmm0400 cmm0400 = new Cmm0400();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(jsonElement) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSysInfo(jsonElement) );
					break;
				case "SysInfo_1" :
					response.getWriter().write( getJobInfo(jsonElement) );
					break;
				case "Cmm0400" :
					response.getWriter().write( getUserInfo(jsonElement) );
					break;
				case "Cmm0400_1" :
					response.getWriter().write( getListDuty(jsonElement) );
					break;
				case "Cmm0400_2" :
					response.getWriter().write( getjobList(jsonElement) );
					break;
				case "Cmm0400_3" :
					response.getWriter().write( getUserRgtDept(jsonElement) );
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


	private String getCodeInfo(JsonElement jsonElement) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("POSITION,DUTY,RGTCD","SEL","N"));
	}
	
	private String getSysInfo(JsonElement jsonElement) throws SQLException, Exception {
		return gson.toJson(sysinfo.getSysInfo("","N","SEL","N",""));
	}
	
	private String getJobInfo(JsonElement jsonElement) throws SQLException, Exception {
		String sysCd = null;
		sysCd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "sysCd") );
		return gson.toJson(sysinfo.getJobInfo("",sysCd,"N","N","","CD"));
	}
	
	private String getUserInfo(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "userId") );
		String userName = null;
		userName = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "userName") );
		System.out.println("Ω√¿€"+userId+","+userName);
		return gson.toJson(cmm0400.getUserInfo(userId,userName));
	}
	
	private String getListDuty(JsonElement jsonElement) throws SQLException, Exception {
		String txtUserId = null;
		txtUserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "txtUserId") );
		return gson.toJson(cmm0400.getUserRGTCD(txtUserId));
	}
	
	private String getjobList(JsonElement jsonElement) throws SQLException, Exception {
		String txtUserId = null;
		txtUserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "txtUserId") );
		return gson.toJson(cmm0400.getUserJobList("USER", txtUserId));
	}
	
	private String getUserRgtDept(JsonElement jsonElement) throws SQLException, Exception {
		String txtUserId = null;
		txtUserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "txtUserId") );
		return gson.toJson(cmm0400.getUserRgtDept(txtUserId));
	}
}
