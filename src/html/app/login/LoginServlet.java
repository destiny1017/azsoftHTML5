package html.app.login;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ecams.service.list.LoginManager;
import com.google.gson.*;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/login/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	LoginManager loginManager = LoginManager.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		//requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		System.out.println();
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "ISVALIDLOGIN" :
					response.getWriter().write( isValidLoginUser(paramMap) );
					break;
				case "SETSESSION" :
					response.getWriter().write( getUserName(paramMap, request)  );
					break;	
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	private String isValidLoginUser(HashMap paramMap) throws SQLException, Exception {
		String userId  = (String) paramMap.get("userId");
		String userPwd = (String) paramMap.get("userPwd");
		return gson.toJson(loginManager.isValid(userId, userPwd));
	}
	
	private String getUserName(HashMap paramMap, HttpServletRequest request) throws SQLException, Exception {
		
		
		String userId  = (String) paramMap.get("userId");
		
		HttpSession session = request.getSession();
		
		session.setAttribute("userId", userId);
		session.setAttribute("userName", loginManager.getUserName(userId));
		loginManager.setSession(session, userId);
		
		return gson.toJson( session.getId() );
	}
}
