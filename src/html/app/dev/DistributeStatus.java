package html.app.dev;

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
import app.common.SystemPath;
import app.common.UserInfo;
import app.eCmr.Cmr1100;
import html.app.common.ParsingCommon;

/**
 * Servlet implementation class eCmr1100
 */
@WebServlet("/webPage/dev/DistributeStatus")
public class DistributeStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	CodeInfo codeinfo = new CodeInfo();
	SystemPath syspath = new SystemPath();
	SysInfo sysinfo = new SysInfo();
	Cmr1100 cmr1100 = new Cmr1100();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "UserInfochk":
					response.getWriter().write( getUserInfoChk(jsonElement) );
					break;
				case "CodeInfoSet":
					response.getWriter().write( getCodeInfo(jsonElement) );
					break;
				case "SystemPathSet":
					response.getWriter().write( getSyspath(jsonElement) );
					break;
				case "getSysInfo":
					response.getWriter().write( getSysInfo(jsonElement) );
					break;
				case "getFileList":
					response.getWriter().write( getFileList(jsonElement) );
					break;
				default : 
					break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			requestType = null;
		}
	}
	
	private String getUserInfoChk(JsonElement jsonElement) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(userinfo.getUserInfo(user));
	}
	
	private String getCodeInfo(JsonElement jsonElement) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("REQUEST","all","n"));
	}
	
	private String getSyspath(JsonElement jsonElement) throws SQLException, Exception {
		return gson.toJson(syspath.getTmpDir("99"));
	}
	
	private String getSysInfo(JsonElement jsonElement) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(sysinfo.getSysInfo(user, "Y","ALL","N",""));
	}
	
	private String getFileList (JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>				 childrenMap = null;
		childrenMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement, "prjData") );
		return gson.toJson( cmr1100.getFileList(childrenMap) );
	}
}
