/**
 * CodeInfo 가져오는 공통 서블릿
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-02-07
 */

package html.app.common;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;

import app.common.CodeInfo;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/common/CommonCodeInfo")
public class CommonCodeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	CodeInfo codeInfo = new CodeInfo();

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");*/
		
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "CODE_INFO" :
					response.getWriter().write( getCodeInfo(jsonElement) );
					break;
				default: 
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}//end of getSysInfo() method statement
	
	private String getCodeInfo(JsonElement jsonElement) throws SQLException, Exception {
		ArrayList<HashMap<String, String>> codeInfoArr = ParsingCommon.jsonArrToArr(ParsingCommon.jsonEtoStr(jsonElement, "codeInfoData"));
		return gson.toJson( codeInfo.getCodeInfoWithArray( codeInfoArr ));
	}

}
