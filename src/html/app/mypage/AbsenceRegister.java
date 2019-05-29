package html.app.mypage;

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
import app.eCmm.Cmm1100;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/mypage/AbsenceRegister")
public class AbsenceRegister extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	CodeInfo codeinfo = new CodeInfo();
	Cmm1100 cmm1100 = new Cmm1100();
	
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
				case "Cmm1100":
					response.getWriter().write( getCbo_User(jsonElement) );
					break;
				case "Cmm1100_1":
					response.getWriter().write( getCbo_User_Click(jsonElement) );
					break;
				case "Cmm1100_2":
					response.getWriter().write( getDaegyulList(jsonElement) );
					break;
				case "Cmm1100_3":
					response.getWriter().write( getDaegyulState(jsonElement) );
					break;
				case "Cmm1100_4":
					response.getWriter().write( get_Update(jsonElement) );
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
		return gson.toJson(codeinfo.getCodeInfo("DAEGYUL","sel","n"));
	}
	
	private String getCbo_User(JsonElement jsonElement) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(cmm1100.getCbo_User(UserId,"Y"));
	}
	
	private String getCbo_User_Click(JsonElement jsonElement) throws SQLException, Exception {
		String cm_userid = null;
		cm_userid = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "cm_userid") );
		return gson.toJson(cmm1100.getCbo_User_Click(cm_userid,"Y"));
	}
	
	private String getDaegyulList(JsonElement jsonElement) throws SQLException, Exception {
		String cm_userid = null;
		cm_userid = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "cm_userid") );
		return gson.toJson(cmm1100.getDaegyulList(cm_userid));
	}
	
	private String getDaegyulState(JsonElement jsonElement) throws SQLException, Exception {
		String cm_userid = null;
		cm_userid = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "cm_userid") );
		return gson.toJson(cmm1100.getDaegyulState(cm_userid));
	}
	
	private String get_Update(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> dataObj = null;
		dataObj = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "dataObj") );
		return gson.toJson(cmm1100.get_Update(dataObj));
	}
	
}
