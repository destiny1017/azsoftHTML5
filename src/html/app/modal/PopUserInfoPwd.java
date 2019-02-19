package html.app.modal;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import app.eCmm.Cmm1700;

import html.app.common.ParsingCommon;

@WebServlet("/PopUserInfoPwd")
public class PopUserInfoPwd extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	Cmm1700 cmm1700 = new Cmm1700();

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
				case "Cmm1700" :
					response.getWriter().write( setPwdReset(request) );
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

	private String setPwdReset(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "userId");
		String userPwd = null;
		userPwd = ParsingCommon.parsingRequestJsonParamToString(request, "userPwd");
		return gson.toJson(cmm1700.PassWd_reset(userId, userPwd));
	}

}
