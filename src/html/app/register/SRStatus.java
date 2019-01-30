package html.app.register;

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
import app.common.PrjInfo;
import app.common.TeamInfo;
import html.app.common.ParsingCommon;

/**
 * Servlet implementation class eCmc1100
 */
@WebServlet("/webPage/regist/SRStatus")
public class SRStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	TeamInfo teaminfo = new TeamInfo();
	CodeInfo codeinfo = new CodeInfo();
	PrjInfo prjinfo = new PrjInfo();
   
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
				case "TeamInfo":
					response.getWriter().write( getTeamInfo(request) );
					break;
				case "TeamInfo2":
					response.getWriter().write( getTeamInfo2(request) );
					break;
				case "CodeInfo":
					response.getWriter().write( getCodeInfo(request) );
					break;
				case "PrjInfo":
					response.getWriter().write( getResearch(request) );
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

	private String getTeamInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(teaminfo.getTeamInfoGrid2("ALL","Y","req","N"));
	}
	
	private String getTeamInfo2(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(teaminfo.getTeamInfoGrid2("ALL","Y","DEPT","N"));
	}
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("ISRSTA,ISRSTAUSR","ALL","N"));
	}
	
	private String getResearch(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>				 childrenMap = null;
		childrenMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "prjData");
		return gson.toJson( prjinfo.get_SelectList(childrenMap) );	
	}
}
