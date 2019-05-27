package html.app.modal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import app.common.CodeInfo;
import app.common.TeamInfo;
import app.eCmm.Cmm0100;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/modal/TreeOrganization")
public class TreeOrganization extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	TeamInfo teamInfo = new TeamInfo();
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
				case "GET_TREE_INFO" :
					response.getWriter().write( getTreeInfo(paramMap) );
					break;
				case "GET_ZTREE_INFO" :
					response.getWriter().write( getTreeInfo_zTree(paramMap) );
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	//
	private String getTreeInfo(HashMap paramMap) throws SQLException, Exception {
		String treeInfoData = (String)paramMap.get("treeInfoData");
		return gson.toJson( teamInfo.getTeamInfoTree_new(Boolean.parseBoolean(treeInfoData)) );
	}
	
	private String getTreeInfo_zTree(HashMap paramMap) throws SQLException, Exception {
		String treeInfoData = (String)paramMap.get("treeInfoData");
		return gson.toJson( teamInfo.getTeamInfoTree_zTree(Boolean.parseBoolean(treeInfoData)) );
	}
	
	
}
