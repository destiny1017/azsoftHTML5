package html.app.approval;

import java.io.IOException;
import java.sql.SQLException;

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
import app.eCmr.Cmr3200;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/approval/RequestStatus")
public class RequestStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	SysInfo sysinfo = new SysInfo();
	CodeInfo codeinfo = new CodeInfo();
	TeamInfo teaminfo = new TeamInfo();
	Cmr3200 cmr3200 = new Cmr3200();
	
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
				case "UserPMOInfo" :
					response.getWriter().write( getPMOInfo(request) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSysInfo(request) );
					break;
				case "CodeInfo_1" :
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
	
	private String getPMOInfo(HttpServletRequest request) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.getPMOInfo(user));
	}
	
	private String getSysInfo(HttpServletRequest request) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(sysinfo.getSysInfo(user,"Y","ALL","N",""));
	}
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("REQUEST,R3200STA","ALL","N"));
	}
	
	private String getTeamInfoGrid2(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(teaminfo.getTeamInfoGrid2("All","Y","sub","N"));
	}
	
	private String get_SelectList(HttpServletRequest request) throws SQLException, Exception {
		String strSys = ParsingCommon.parsingRequestJsonParamToString(request, "strSys");
		String strQry = ParsingCommon.parsingRequestJsonParamToString(request, "strQry");
		String strTeam = ParsingCommon.parsingRequestJsonParamToString(request, "strTeam");
		String strSta = ParsingCommon.parsingRequestJsonParamToString(request, "strSta");
		String txtUser = ParsingCommon.parsingRequestJsonParamToString(request, "txtUser");
		String strStD = ParsingCommon.parsingRequestJsonParamToString(request, "strStD");
		String strEdD = ParsingCommon.parsingRequestJsonParamToString(request, "strEdD");
		String strUserId = ParsingCommon.parsingRequestJsonParamToString(request, "strUserId");
		String cboGbn = ParsingCommon.parsingRequestJsonParamToString(request, "cboGbn");
		String strJob = ParsingCommon.parsingRequestJsonParamToString(request, "strJob");
		String dategbn = ParsingCommon.parsingRequestJsonParamToString(request, "dategbn");
		String txtSpms = ParsingCommon.parsingRequestJsonParamToString(request, "txtSpms");

		return gson.toJson( cmr3200.get_SelectList(strSys,strQry,strTeam,strSta,txtUser,strStD,strEdD,strUserId,cboGbn,strJob,dategbn,txtSpms) );
	}
}
