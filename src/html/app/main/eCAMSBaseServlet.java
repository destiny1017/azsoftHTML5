package html.app.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecams.service.list.LoginManager;
import com.ecams.service.list.User;
import com.ecams.service.msg.dao.MsgDAO;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import app.common.MenuList;
import app.common.UserInfo;
import html.app.common.ParsingCommon;

/**
 * Servlet implementation class eCAMSBase_Servlet
 */
@WebServlet("/webPage/main/eCAMSBaseServlet")
public class eCAMSBaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	MenuList menuList = new MenuList();
	UserInfo userinfo = new UserInfo();
	LoginManager loginManager = LoginManager.getInstance();
	MsgDAO msgDao = new MsgDAO();
	User user = new User();
	
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
				case "MenuList":
					response.getWriter().write( getMenu(jsonElement) );
					break;
				case "UserInfo":
					response.getWriter().write( getUserInfo(jsonElement) );
					break;	
				case "GETSESSIONUSERDATA":
					response.getWriter().write( getSessionUserData(jsonElement) );
					break;
				case "LOG_OUT":
					response.getWriter().write( logOut(jsonElement) );
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
	
	private String getSessionUserData(JsonElement jsonElement) throws SQLException, Exception {
		String sessionID = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "sessionID") );
		 
		HashMap<String, String> userInfoMap = new HashMap<String, String>();
		userInfoMap.put("userId", 	loginManager.getUserID(sessionID));
		userInfoMap.put("userName", loginManager.getUserName(userInfoMap.get("userId")));
		userInfoMap.put("adminYN",  userinfo.isAdmin(loginManager.getUserID(sessionID)).toString());
		HashMap<String, String> deptInfoMap = loginManager.getUserTeam(userInfoMap.get("userId"));
		userInfoMap.put("deptName",deptInfoMap.get("deptName"));
		userInfoMap.put("deptCd",deptInfoMap.get("deptCd"));
		return gson.toJson(userInfoMap);
	}
	
	private String getMenu(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		
		return gson.toJson(menuList.secuMenuList(userId));
	}
	
	private String getUserInfo(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String logOut(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		if(msgDao.updateLogOut(userId) == 1) user.setDelUser(userId);
		return gson.toJson( "LOG_OUT" );
	}
}
