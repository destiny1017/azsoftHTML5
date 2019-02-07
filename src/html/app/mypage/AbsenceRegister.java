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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(request) );
					break;
				case "Cmm1100":
					response.getWriter().write( getCbo_User(request) );
					break;
				case "Cmm1100_1":
					response.getWriter().write( getCbo_User_Click(request) );
					break;
				case "Cmm1100_2":
					response.getWriter().write( getDaegyulList(request) );
					break;
				case "Cmm1100_3":
					response.getWriter().write( getDaegyulState(request) );
					break;
				case "Cmm1100_4":
					response.getWriter().write( get_Update(request) );
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
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("DAEGYUL","sel","n"));
	}
	
	private String getCbo_User(HttpServletRequest request) throws SQLException, Exception {
		String UserId = null;
		UserId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(cmm1100.getCbo_User(UserId,"Y"));
	}
	
	private String getCbo_User_Click(HttpServletRequest request) throws SQLException, Exception {
		String cm_userid = null;
		cm_userid = ParsingCommon.parsingRequestJsonParamToString(request, "cm_userid");
		return gson.toJson(cmm1100.getCbo_User_Click(cm_userid,"Y"));
	}
	
	private String getDaegyulList(HttpServletRequest request) throws SQLException, Exception {
		String cm_userid = null;
		cm_userid = ParsingCommon.parsingRequestJsonParamToString(request, "cm_userid");
		return gson.toJson(cmm1100.getDaegyulList(cm_userid));
	}
	
	private String getDaegyulState(HttpServletRequest request) throws SQLException, Exception {
		String cm_userid = null;
		cm_userid = ParsingCommon.parsingRequestJsonParamToString(request, "cm_userid");
		return gson.toJson(cmm1100.getDaegyulState(cm_userid));
	}
	
	private String get_Update(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> dataObj = null;
		dataObj = ParsingCommon.parsingRequestJsonParamToHashMap(request, "dataObj");
		return gson.toJson(cmm1100.get_Update(dataObj));
	}
	
}
