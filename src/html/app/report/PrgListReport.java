package html.app.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecams.service.list.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import app.common.SysInfo;
import app.common.UserInfo;
import app.eCmd.Cmd3100;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/report/PrgListReport")
public class PrgListReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	SysInfo sysinfo = new SysInfo();
	Cmd3100 cmd3100 = new Cmd3100();
	
	
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
					response.getWriter().write( isAdmin(jsonElement) );
					break;
				case "getSysInfo" :
					response.getWriter().write( getSysInfo(jsonElement) );
					break;
				case "getSysInfo2" :
					response.getWriter().write( getSysInfo(jsonElement) );
					break;
				case "getJogun" :
					response.getWriter().write( getJogun(jsonElement) );
					break;
				case "getCode" :
					response.getWriter().write( getCode(jsonElement) );
					break;
				case "getSql_Qry" :
					response.getWriter().write( getSql_Qry(jsonElement) );
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
	
	private String isAdmin(JsonElement jsonElement) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(userinfo.isAdmin(user));
	}
	
	private String getSysInfo(JsonElement jsonElement) throws SQLException, Exception {
		String user = null;
		String tmp = null;
		user = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		tmp = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "tmp") );
		return gson.toJson(sysinfo.getSysInfo(user,tmp,"","N","04"));
	}
	
	private String getJogun(JsonElement jsonElement) throws SQLException, Exception {
		String cnt = null;
		cnt = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "cnt") );
		return gson.toJson(cmd3100.getJogun(Integer.parseInt(cnt)));
	}
	
	private String getCode(JsonElement jsonElement) throws SQLException, Exception {
		String cnt = null;
		String L_SysCd = null;
		L_SysCd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "L_SysCd") );
		cnt = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "cnt") );
		return gson.toJson(cmd3100.getCode(L_SysCd, Integer.parseInt(cnt)));
	}
	
	private String getSql_Qry(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String>	DataInfoMap = null;
		DataInfoMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "prjData") );
		return gson.toJson( cmd3100.getSql_Qry(DataInfoMap) );
	}
}
