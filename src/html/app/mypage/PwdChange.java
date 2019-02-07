package html.app.mypage;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "MemberDAO" :
					response.getWriter().write(getUserName(request));
					break;
				case "PassWdDAO":
					response.getWriter().write( selectPassWd(request) );
					break;
				case "PassWdDAO_1":
				case "PassWdDAO_3":
				case "PassWdDAO_4":
					response.getWriter().write( encryptPassWd(request) );
					break;
				case "PassWdDAO_2":
					response.getWriter().write( selectLastPassWord(request) );
					break;
				case "PassWdDAO_5":
					response.getWriter().write( updtPassWd(request) );
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
	
	private String getUserName(HttpServletRequest request) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(memberdao.selectUserName(UserId));
	}
	
	private String selectPassWd(HttpServletRequest request) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(passwddao.selectPassWd(UserId));
	}
	
	private String encryptPassWd(HttpServletRequest request) throws SQLException, Exception {
		String usr_passwd = null;
		usr_passwd = ParsingCommon.parsingRequestJsonParamToString(request, "usr_passwd");
		return gson.toJson(passwddao.encryptPassWd(usr_passwd));
	}
	
	private String selectLastPassWord(HttpServletRequest request) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(passwddao.selectLastPassWord(UserId));
	}
	
	private String updtPassWd(HttpServletRequest request) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		String usr_passwd = null;
		usr_passwd = ParsingCommon.parsingRequestJsonParamToString(request, "usr_passwd");
		return gson.toJson(passwddao.updtPassWd(UserId,usr_passwd));
	}

}
