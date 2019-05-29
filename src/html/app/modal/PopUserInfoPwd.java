package html.app.modal;

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
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "Cmm1700" :
					response.getWriter().write( setPwdReset(jsonElement) );
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

	private String setPwdReset(JsonElement jsonElement) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "userId") );
		String userPwd = null;
		userPwd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "userPwd") );
		return gson.toJson(cmm1700.PassWd_reset(userId, userPwd));
	}

}
