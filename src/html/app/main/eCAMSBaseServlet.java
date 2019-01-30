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
		return gson.toJson(userInfoMap);
	}
	
	
	private String getMenu(HttpServletRequest request) throws SQLException, Exception {
		String sysMap = null;
		sysMap = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(menuList.secuMenuList(sysMap));
	}
	
	private String getUserInfo(HttpServletRequest request) throws SQLException, Exception {
		String sysMap = null;
		sysMap = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.isAdmin(sysMap));
	}
}
