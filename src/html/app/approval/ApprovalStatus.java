package html.app.approval;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecams.service.list.LoginManager;
import com.google.gson.Gson;

import app.common.CodeInfo;
import app.common.SysInfo;
import app.common.TeamInfo;
import app.common.UserInfo;
import app.eCmr.Cmr3100;
import html.app.common.ParsingCommon;


@WebServlet("/webPage/approval/ApprovalStatus")
public class ApprovalStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	SysInfo sysinfo = new SysInfo();
	CodeInfo codeinfo = new CodeInfo();
	TeamInfo teaminfo = new TeamInfo();
	Cmr3100 cmr3100 = new Cmr3100();
	 
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
				case "UserInfochk" :
					response.getWriter().write( getUserInfo(request) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSysInfo(request) );
					break;
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(request) );
					break;
				case "TeamInfo" :
					response.getWriter().write( getTeamInfoGrid2(request) );
					break;	
				case "get_SelectList" :
					response.getWriter().write( get_SelectList(request) );
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
	
	private String getUserInfo(HttpServletRequest request) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.getUserInfo(user));
	}
	
	private String getSysInfo(HttpServletRequest request) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(sysinfo.getSysInfo(user,"Y","ALL","N",""));
	}
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("REQUEST,APPROVAL","ALL","N"));
	}
	
	private String getTeamInfoGrid2(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(teaminfo.getTeamInfoGrid2("All","Y","sub","N"));
	}
	
	private String get_SelectList(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String>	prjDataInfoMap = null;
		prjDataInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "prjData");
		
		return gson.toJson( cmr3100.get_SelectList(prjDataInfoMap.get("strSys"),prjDataInfoMap.get("strGbn"),
												   prjDataInfoMap.get("strQry"),prjDataInfoMap.get("strTeam"),
												   prjDataInfoMap.get("strSta"),prjDataInfoMap.get("txtUser"),
												   prjDataInfoMap.get("strStD"),prjDataInfoMap.get("strEdD"),
												   prjDataInfoMap.get("strUserId"),prjDataInfoMap.get("dategbn"),
												   prjDataInfoMap.get("txtSpms"),prjDataInfoMap.get("strProc")) );
	}
}
