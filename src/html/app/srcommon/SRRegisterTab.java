/**
 * PrjListTab 화면 서블릿
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-02-07
 */

package html.app.srcommon;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;

import app.common.CodeInfo;
import app.common.PrjInfo;
import app.common.TeamInfo;
import app.eCmc.Cmc0100;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/srcommon/SRRegisterTab")
public class SRRegisterTab extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	Cmc0100 cmc0100 = new Cmc0100();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "GET_USER_COMBO" :
					response.getWriter().write( getUserCombo(paramMap) );
					break;
				
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	private String getUserCombo(HashMap paramMap) throws SQLException, Exception {
		String userId = (String)paramMap.get("userInfoData");
		//cmc0100.getUserCombo("DEVUSER", "", "", userId)
		return gson.toJson(cmc0100.getUserCombo("REQUSER", "", "", userId));
	}

}
