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

import app.common.UserInfo;
import app.common.SysInfo;
import app.eCmd.Cmd1300;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/mypage/UserConfig")
public class UserConfig extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	SysInfo sysinfo = new SysInfo();
	Cmd1300 cmd1300= new Cmd1300();
	
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
				case "UserInfo" :
					response.getWriter().write( checkAdmin(jsonElement) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSelSysname(jsonElement) );
					break;
				case "Cmd1300" :
					response.getWriter().write( getGrid1(jsonElement) );
					break;
				case "Cmd1300_1" :
					response.getWriter().write( getUpdateDir(jsonElement) );
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


	private String checkAdmin(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String getSelSysname(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		String secuYn = null;
		secuYn = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "SecuYn") );
		return gson.toJson(sysinfo.getSysInfo(userId, secuYn, "SEL", "N", ""));
	}
	
	private String getGrid1(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(cmd1300.getSql_Qry(userId));
	}
	
	private String getUpdateDir(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		String selSysCd = null;
		selSysCd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "selSysCd") );
		String txtDir = null;
		txtDir = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "txtDir") );
		return gson.toJson(cmd1300.getTblUpdate(userId, selSysCd, txtDir));
	}
}
