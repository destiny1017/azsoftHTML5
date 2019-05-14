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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) { 
				case "MenuList":
					response.getWriter().write( getMenu(request) );
					break;
				case "UserInfo":
					response.getWriter().write( getUserInfo(request) );
					break;	
				case "GETSESSIONUSERDATA":
					response.getWriter().write( getSessionUserData(request) );
					break;
				case "LOG_OUT":
					response.getWriter().write( logOut(request) );
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
	
	private String getSessionUserData(HttpServletRequest request) throws SQLException, Exception {
		String sessionID = ParsingCommon.parsingRequestJsonParamToString(request, "sessionID");
		HashMap<String, String> userInfoMap = new HashMap<String, String>();
		userInfoMap.put("userId", 	loginManager.getUserID(sessionID));
		userInfoMap.put("userName", loginManager.getUserName(userInfoMap.get("userId")));
		userInfoMap.put("adminYN",  userinfo.isAdmin(loginManager.getUserID(sessionID)).toString());
		HashMap<String, String> deptInfoMap = loginManager.getUserTeam(userInfoMap.get("userId"));
		userInfoMap.put("deptName",deptInfoMap.get("deptName"));
		userInfoMap.put("deptCd",deptInfoMap.get("deptCd"));
		return gson.toJson(userInfoMap);
	}
	
	private String getMenu(HttpServletRequest request) throws SQLException, Exception {
		String sysMap = null;
		sysMap = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(menuList.secuMenuList(sysMap));
	}
	
	private String getUserInfo(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String logOut(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		if(msgDao.updateLogOut(userId) == 1) user.setDelUser(userId);
		return gson.toJson( "LOG_OUT" );
	}
}
