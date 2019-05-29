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
import com.ecams.service.member.dao.MemberDAO;
import com.ecams.service.passwd.dao.PassWdDAO;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/mypage/PwdChange")
public class PwdChange extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	MemberDAO memberdao = new MemberDAO();
	PassWdDAO passwddao = new PassWdDAO();
	
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
				case "MemberDAO" :
					response.getWriter().write(getUserName(jsonElement));
					break;
				case "PassWdDAO":
					response.getWriter().write( selectPassWd(jsonElement) );
					break;
				case "PassWdDAO_1":
				case "PassWdDAO_3":
				case "PassWdDAO_4":
					response.getWriter().write( encryptPassWd(jsonElement) );
					break;
				case "PassWdDAO_2":
					response.getWriter().write( selectLastPassWord(jsonElement) );
					break;
				case "PassWdDAO_5":
					response.getWriter().write( updtPassWd(jsonElement) );
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
	
	private String getUserName(JsonElement jsonElement) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(memberdao.selectUserName(UserId));
	}
	
	private String selectPassWd(JsonElement jsonElement) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(passwddao.selectPassWd(UserId));
	}
	
	private String encryptPassWd(JsonElement jsonElement) throws SQLException, Exception {
		String usr_passwd = null;
		ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		usr_passwd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "usr_passwd") );
		return gson.toJson(passwddao.encryptPassWd(usr_passwd));
	}
	
	private String selectLastPassWord(JsonElement jsonElement) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		return gson.toJson(passwddao.selectLastPassWord(UserId));
	}
	
	private String updtPassWd(JsonElement jsonElement) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "UserId") );
		String usr_passwd = null;
		usr_passwd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "usr_passwd") );
		return gson.toJson(passwddao.updtPassWd(UserId,usr_passwd));
	}

}
