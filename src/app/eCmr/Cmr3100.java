/*****************************************************************************************
	1. program ID	: eCmr3100.java
	2. create date	:
	3. auth		    : [결재확인] -> [결재현황]
	4. update date	: 090825
	5. auth		    : no name
	6. description	: eCmr3100.java 결재현황
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
//import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr3100{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 결재리스트 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */

    public Object[] get_SelectList(String syscd, String gbn, String pReqCd,String pTeamCd,String pStateCd,
    								String pReqUser,String pStartDt,String pEndDt,String pUserId, String dategbn, String txtspms, String pProc)
    																		throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
//        Object[]		 rtObj		  = null;

		String			  AcptNo      = "";
		String			  ConfName    = null;
		String			  ColorSw     = null;
        Integer           FindFg      = 0;
        Integer           Cnt         = 0;
        String            ConfTeamCd  = "";
        String			  Sunhang	  = "";
        String			  DocYn  	  = "";
        String            reqName     = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno, '['||h.cc_srid||']'||h.cc_reqtitle spms,h.cc_srid, \n");
			strQuery.append("       a.cr_editor,a.cr_qrycd,a.cr_syscd,a.cr_status,a.cr_sayu,        \n");
			strQuery.append("       a.cr_prcdate,b.cr_locat,b.cr_team,b.cr_status sta,a.cr_closeyn, \n");
			strQuery.append("       b.cr_confusr,b.cr_congbn,b.cr_confname,c.cm_deptname,           \n");
			strQuery.append("       to_char(b.cr_confdate,'yyyy/mm/dd hh24:mi') confdate,           \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,        \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') as prcdate,          \n");
			strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) ||    \n");
			strQuery.append("       '-' || substr(a.cr_acptno,7,6) as acptno,a.cr_prcreq,           \n");
			strQuery.append("       d.cm_codename qrycd,e.cm_sysmsg,f.cm_username,a.cr_sayucd,a.cr_passcd,a.cr_passok \n");
			strQuery.append("   from cmc0100 h,cmm0040 f,cmm0030 e,cmm0020 d,cmm0100 c,cmr9900 b,cmr1000 a where \n");
			//strQuery.append("       (A.cr_qrycd<'30' or a.cr_qrycd in ('71','75')) and             \n");
			if (pStateCd.equals("0")) {
				strQuery.append("      b.cr_locat<>'00' and ((b.cr_status<>'0'	\n");
				strQuery.append("  and b.cr_confdate is not null and b.cr_confusr is not null       \n");
				if(dategbn.equals("0")){
					strQuery.append("  and to_char(a.cr_acptdate,'yyyymmdd')>=?                     \n");
					strQuery.append("  and to_char(a.cr_acptdate,'yyyymmdd')<=?                     \n");
				}else if(dategbn.equals("1")){
					strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')>=?                     \n");
					strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')<=?                     \n");
				}else{
					strQuery.append("  and substr(a.cr_prcreq,0,8)>=?                               \n");
					strQuery.append("  and substr(a.cr_prcreq,0,8)<=?                               \n");
				}
				strQuery.append("  and b.cr_confusr=?) or                                           \n");
				strQuery.append("     (b.cr_status='0' and b.cr_teamcd not in ('1','4','5','9')     \n");
				strQuery.append("      and b.cr_team=?))                                            \n");
			} else if (pStateCd.equals("01")){//결재대기
				//strQuery.append("      b.cr_locat='00' and b.cr_status='0'                        \n");
				strQuery.append("      b.cr_status='0'                                              \n");
				strQuery.append("and   b.cr_teamcd not in ('1','4','5','9') and b.cr_team=?         \n");
				strQuery.append("and b.cr_locat>=(select substr(cr_confusr,1,2) from cmr9900        \n");
				strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
				strQuery.append("and b.cr_locat<=(select substr(cr_confusr,3,2) from cmr9900        \n");
				strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
			} else if (pStateCd.equals("02")){
				strQuery.append("      b.cr_locat<>'00' and b.cr_status='0'                         \n");
				strQuery.append("and   b.cr_teamcd not in ('1','4','5','9') and b.cr_team=?         \n");
			} else if (pStateCd.equals("03")){
				strQuery.append("    b.cr_locat<>'00' and b.cr_status='3'                         \n");
				strQuery.append("and b.cr_confusr is not null and b.cr_confusr=?                  \n");
				strQuery.append("and b.cr_confdate is not null                                    \n");
				if(dategbn.equals("0")){
					strQuery.append("  and to_char(a.cr_acptdate,'yyyymmdd')>=?                   \n");
					strQuery.append("  and to_char(a.cr_acptdate,'yyyymmdd')<=?                   \n");
				}else if(dategbn.equals("1")){
					strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')>=?                   \n");
					strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')<=?                   \n");
				}else{
					strQuery.append("  and substr(a.cr_prcreq,0,8)>=?                             \n");
					strQuery.append("  and substr(a.cr_prcreq,0,8)<=?                             \n");
				}
			} else if (pStateCd.equals("04")){
				strQuery.append("    b.cr_locat<>'00' and b.cr_status='9'                         \n");
				strQuery.append("and b.cr_confusr is not null and b.cr_confusr=?                  \n");
				strQuery.append("and b.cr_confdate is not null                                    \n");
				if(dategbn.equals("0")){
					strQuery.append("  and to_char(a.cr_acptdate,'yyyymmdd')>=?                   \n");
					strQuery.append("  and to_char(a.cr_acptdate,'yyyymmdd')<=?                   \n");
				}else if(dategbn.equals("1")){
					strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')>=?                   \n");
					strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')<=?                   \n");
				}else{
					strQuery.append("  and substr(a.cr_prcreq,0,8)>=?                             \n");
					strQuery.append("  and substr(a.cr_prcreq,0,8)<=?                             \n");
				}
			}
			if (pProc.equals("0")) {//전체일때
				strQuery.append("and (A.CR_PRCDATE IS NOT NULL or   a.cr_status='0')  				\n");
			} else if (pProc.equals("1") || pProc.equals("2") || pProc.equals("4")){//1:미완료(에러+진행중) 2:시스템에러 4:진행중
				strQuery.append("and   a.cr_status='0'                                              \n");
			} else if (pProc.equals("3")){//반려
				strQuery.append("and   a.cr_status='3'                                              \n");
				strQuery.append("and   A.CR_PRCDATE IS NOT NULL                                     \n");
			}else if (pStateCd.equals("9")){//완료
				strQuery.append("and   A.CR_PRCDATE IS NOT NULL and a.cr_status<>'3'                \n");
			}
			if (!pReqCd.equals("0")) {
				strQuery.append("and a.cr_qrycd = ?                        \n");
				if(pReqCd.equals("93") || pReqCd.equals("94")) {
					strQuery.append("and a.cr_closeyn = 'Y'                        \n");
				}
			}
			if (!pTeamCd.equals("0"))  strQuery.append("and a.cr_teamcd = ?                       \n");
			if (pReqUser != null && pReqUser.length() > 0) {
					strQuery.append("and a.cr_editor in (select cm_userid from cmm0040            \n");
					strQuery.append("                     where cm_username=?)                    \n");
			}
			strQuery.append("and b.cr_acptno = a.cr_acptno                                        \n");
			strQuery.append("and a.cr_teamcd = c.cm_deptcd                                        \n");
			strQuery.append("and a.cr_syscd=e.cm_syscd and a.cr_editor=f.cm_userid                \n");
			strQuery.append("and d.cm_macode='REQUEST' and d.cm_micode=a.cr_qrycd                 \n");
			//strQuery.append("and g.cm_jobcd = a.cr_jobcd                                          \n");//13.11.12 주석처리
			if(!syscd.equals("0")){
				strQuery.append("and a.cr_syscd=?                                                 \n");
			}
			if(!gbn.equals("0")){
				strQuery.append("and a.cr_passok=?                                                \n");
			}
			strQuery.append("and a.cr_itsmid=h.cc_srid(+)                                         \n");
			if(txtspms != null && txtspms.length() > 0){
				strQuery.append("and (h.cc_srid like ? or upper(h.cc_reqtitle) like upper(?))     \n");
			}
			if (pStateCd.equals("0") || pStateCd.equals("01") || pStateCd.equals("02")) {//전체,결재대기,결재진행
				strQuery.append("union                \n");
				//strQuery.append("select a.cr_acptno, '['||a.cr_itsmid||']'||a.cr_itsmtitle spms, a.cr_itsmid, h.cm_jobname, \n");//13.11.12 주석처리
				strQuery.append("select a.cr_acptno, '['||h.cc_srid||']'||h.cc_reqtitle spms,h.cc_srid, \n");
				strQuery.append("       a.cr_editor,a.cr_qrycd,a.cr_syscd,a.cr_status,a.cr_sayu,        \n");
				strQuery.append("       a.cr_prcdate,b.cr_locat,b.cr_team,b.cr_status sta,a.cr_closeyn, \n");
				strQuery.append("       b.cr_confusr,b.cr_congbn,b.cr_confname,c.cm_deptname,           \n");
				strQuery.append("       to_char(b.cr_confdate,'yyyy/mm/dd hh24:mi') confdate,           \n");
				strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,        \n");
				strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') as prcdate,          \n");
				strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) ||    \n");
				strQuery.append("       '-' || substr(a.cr_acptno,7,6) as acptno,a.cr_prcreq,           \n");
				strQuery.append("       d.cm_codename qrycd,e.cm_sysmsg,f.cm_username,a.cr_sayucd,a.cr_passcd,a.cr_passok \n");
				strQuery.append("  from cmc0100 h,cmm0043 g,cmm0040 f,cmm0030 e,cmm0020 d,cmm0100 c,cmr9900 b,cmr1000 a where \n");
				if (pStateCd.equals("0")) {
					strQuery.append("      b.cr_locat<>'00' and b.cr_status='0'                         \n");
					strQuery.append("      and b.cr_teamcd in ('4','5','9')                             \n");
					strQuery.append("      and g.cm_userid=?                                            \n");
					strQuery.append("      and instr(b.cr_team,g.cm_rgtcd)>0                            \n");
				} else if (pStateCd.equals("01")){//결재대기
					//strQuery.append("      b.cr_locat='00' and b.cr_status='0'                        \n");
					strQuery.append("      b.cr_status='0'                                              \n");
					strQuery.append("      and b.cr_teamcd in ('4','5','9')                             \n");
					strQuery.append("      and g.cm_userid=?                                            \n");
					strQuery.append("      and instr(b.cr_team,g.cm_rgtcd)>0                            \n");
					strQuery.append("and b.cr_locat>=(select substr(cr_confusr,1,2) from cmr9900        \n");
					strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
					strQuery.append("and b.cr_locat<=(select substr(cr_confusr,3,2) from cmr9900        \n");
					strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
				} else if (pStateCd.equals("02")){
					strQuery.append("      b.cr_locat<>'00' and b.cr_status='0'                         \n");
					strQuery.append("      and b.cr_teamcd in ('4','5','9')                             \n");
					strQuery.append("      and g.cm_userid=?                                            \n");
					strQuery.append("      and instr(b.cr_team,g.cm_rgtcd)>0                            \n");
				}
				if (pProc.equals("0")) {//전체일때
					strQuery.append("and (A.CR_PRCDATE IS NOT NULL or   a.cr_status='0')  				\n");
				} else if (pProc.equals("1") || pProc.equals("2") || pProc.equals("4")){//1:미완료(에러+진행중) 2:시스템에러 4:진행중
					strQuery.append("and   a.cr_status='0'                                              \n");
				} else if (pProc.equals("3")){//반려
					strQuery.append("and   a.cr_status='3'                                              \n");
					strQuery.append("and   A.CR_PRCDATE IS NOT NULL                                     \n");
				}else if (pStateCd.equals("9")){//완료
					strQuery.append("and   A.CR_PRCDATE IS NOT NULL and a.cr_status<>'3'                \n");
				}
				if (!pReqCd.equals("0")) {
					strQuery.append("and a.cr_qrycd = ?                        \n");
					if(pReqCd.equals("93") || pReqCd.equals("94")) {
						strQuery.append("and a.cr_closeyn = 'Y'                        \n");
					}					
				}
				if (!pTeamCd.equals("0"))  strQuery.append("and a.cr_teamcd = ?                       \n");
				if (pReqUser != null && pReqUser.length() > 0) {
						strQuery.append("and a.cr_editor in (select cm_userid from cmm0040            \n");
						strQuery.append("                     where cm_username=?)                    \n");
				}
				strQuery.append("and b.cr_acptno = a.cr_acptno                                        \n");
				strQuery.append("and a.cr_teamcd = c.cm_deptcd                                        \n");
				strQuery.append("and a.cr_syscd=e.cm_syscd and a.cr_editor=f.cm_userid                \n");
				strQuery.append("and d.cm_macode='REQUEST' and d.cm_micode=a.cr_qrycd                 \n");
				//strQuery.append("and h.cm_jobcd = a.cr_jobcd                                          \n");//13.11.12 주석처리
				if(!syscd.equals("0")){
					strQuery.append("and a.cr_syscd=?                                                 \n");
				}
				if(!gbn.equals("0")){
					strQuery.append("and a.cr_passok=?                                                \n");
				}
				strQuery.append("and a.cr_itsmid=h.cc_srid(+)                                         \n");
				if(txtspms != null && txtspms.length() > 0){
					strQuery.append("and (h.cc_srid like ? or upper(h.cc_reqtitle) like upper(?))     \n");
				}
			}
			if (!dategbn.equals("2")) {
				strQuery.append("union  \n");
				strQuery.append("select b.cr_acptno, '['||h.cc_srid||']'||h.cc_reqtitle spms,h.cc_srid, \n");
				strQuery.append("       a.cc_editor cr_editor,a.cc_qrycd cr_qrycd,'99999' cr_syscd,     \n");
				strQuery.append("       a.cc_status cr_status,h.cc_reqtitle cr_sayu,                    \n");
				strQuery.append("       a.cc_eddate cr_prcdate,b.cr_locat,b.cr_team,b.cr_status sta,    \n");
				strQuery.append("       'N' cr_closeyn,b.cr_confusr,b.cr_congbn,b.cr_confname,c.cm_deptname,\n");
				strQuery.append("       to_char(b.cr_confdate,'yyyy/mm/dd hh24:mi') confdate,           \n");
				strQuery.append("       to_char(a.cc_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,        \n");
				strQuery.append("       to_char(a.cc_eddate,'yyyy/mm/dd hh24:mi') as prcdate,           \n");
				strQuery.append("       substr(a.cc_acptno,1,4) || '-' || substr(a.cc_acptno,5,2) ||    \n");
				strQuery.append("       '-' || substr(a.cc_acptno,7,6) as acptno,'' cr_prcreq,          \n");
				strQuery.append("       d.cm_codename qrycd,'' cm_sysmsg,f.cm_username,'00' cr_sayucd,  \n");
				strQuery.append("       '' cr_passcd,'' cr_passok                                       \n");
				strQuery.append("   from cmc0100 h,cmm0040 f,cmm0020 d,cmm0100 c,cmr9900 b,cmc0300 a where \n");
				//strQuery.append("       (A.cr_qrycd<'30' or a.cr_qrycd in ('71','75')) and             \n");
				if (pStateCd.equals("0")) {
					strQuery.append("      b.cr_locat<>'00' and ((b.cr_status<>'0'	\n");
					strQuery.append("  and b.cr_confdate is not null and b.cr_confusr is not null       \n");
					if(dategbn.equals("0")){
						strQuery.append("  and to_char(a.cc_acptdate,'yyyymmdd')>=?                     \n");
						strQuery.append("  and to_char(a.cc_acptdate,'yyyymmdd')<=?                     \n");
					}else if(dategbn.equals("1")){
						strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')>=?                     \n");
						strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')<=?                     \n");
					}
					strQuery.append("  and b.cr_confusr=?) or                                           \n");
					strQuery.append("     (b.cr_status='0' and b.cr_teamcd not in ('1','4','5','9')     \n");
					strQuery.append("      and b.cr_team=?))                                            \n");
				} else if (pStateCd.equals("01")){//결재대기
					strQuery.append("      b.cr_status='0'                                              \n");
					strQuery.append("and   b.cr_teamcd not in ('1','4','5','9') and b.cr_team=?         \n");
					strQuery.append("and b.cr_locat>=(select substr(cr_confusr,1,2) from cmr9900        \n");
					strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
					strQuery.append("and b.cr_locat<=(select substr(cr_confusr,3,2) from cmr9900        \n");
					strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
				} else if (pStateCd.equals("02")){
					strQuery.append("      b.cr_locat<>'00' and b.cr_status='0'                         \n");
					strQuery.append("and   b.cr_teamcd not in ('1','4','5','9') and b.cr_team=?         \n");
				} else if (pStateCd.equals("03")){
					strQuery.append("    b.cr_locat<>'00' and b.cr_status='3'                         \n");
					strQuery.append("and b.cr_confusr is not null and b.cr_confusr=?                  \n");
					strQuery.append("and b.cr_confdate is not null                                    \n");
					if(dategbn.equals("0")){
						strQuery.append("  and to_char(a.cc_acptdate,'yyyymmdd')>=?                   \n");
						strQuery.append("  and to_char(a.cc_acptdate,'yyyymmdd')<=?                   \n");
					}else if(dategbn.equals("1")){
						strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')>=?                   \n");
						strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')<=?                   \n");
					}
				} else if (pStateCd.equals("04")){
					strQuery.append("    b.cr_locat<>'00' and b.cr_status='9'                         \n");
					strQuery.append("and b.cr_confusr is not null and b.cr_confusr=?                  \n");
					strQuery.append("and b.cr_confdate is not null                                    \n");
					if(dategbn.equals("0")){
						strQuery.append("  and to_char(a.cc_acptdate,'yyyymmdd')>=?                   \n");
						strQuery.append("  and to_char(a.cc_acptdate,'yyyymmdd')<=?                   \n");
					}else if(dategbn.equals("1")){
						strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')>=?                   \n");
						strQuery.append("  and to_char(b.cr_confdate,'yyyymmdd')<=?                   \n");
					}
				}
				if (pProc.equals("0")) {//전체일때
					strQuery.append("and (A.CC_edDATE IS NOT NULL or   a.cc_status='0')  				\n");
				} else if (pProc.equals("1") || pProc.equals("2") || pProc.equals("4")){//1:미완료(에러+진행중) 2:시스템에러 4:진행중
					strQuery.append("and   a.cc_status='0'                                              \n");
				} else if (pProc.equals("3")){//반려
					strQuery.append("and   a.cc_status='3'                                              \n");
					strQuery.append("and   A.Cc_edDATE IS NOT NULL                                      \n");
				}else if (pStateCd.equals("9")){//완료
					strQuery.append("and   A.Cc_edDATE IS NOT NULL and a.cc_status<>'3'                \n");
				}
				if (!pReqCd.equals("0")) {
					strQuery.append("and a.cc_qrycd = ?                                               \n");
				}
				if (!pTeamCd.equals("0"))  strQuery.append("and a.cc_teamcd = ?                       \n");
				if (pReqUser != null && pReqUser.length() > 0) {
						strQuery.append("and a.cc_editor in (select cm_userid from cmm0040            \n");
						strQuery.append("                     where cm_username=?)                    \n");
				}
				strQuery.append("and b.cr_acptno = a.cc_acptno                                        \n");
				strQuery.append("and a.cc_teamcd = c.cm_deptcd                                        \n");
				strQuery.append("and a.cc_editor=f.cm_userid                                          \n");
				strQuery.append("and d.cm_macode='REQUEST' and d.cm_micode=a.cc_qrycd                 \n");
				strQuery.append("and a.cc_srid=h.cc_srid                                              \n");
				if(txtspms != null && txtspms.length() > 0){
					strQuery.append("and (h.cc_srid like ? or upper(h.cc_reqtitle) like upper(?))     \n");
				}
				if (pStateCd.equals("0") || pStateCd.equals("01") || pStateCd.equals("02")) {//전체,결재대기,결재진행
					strQuery.append("union                \n");
					strQuery.append("select a.cc_acptno, '['||h.cc_srid||']'||h.cc_reqtitle spms,h.cc_srid,\n");
					strQuery.append("       a.cc_editor cr_editor,a.cc_qrycd cr_qrycd,'99999' cr_syscd,     \n");
					strQuery.append("       a.cc_status cr_status,h.cc_reqtitle cr_sayu,                    \n");
					strQuery.append("       a.cc_eddate cr_prcdate,b.cr_locat,b.cr_team,b.cr_status sta,    \n");
					strQuery.append("       'N' cr_closeyn,b.cr_confusr,b.cr_congbn,b.cr_confname,c.cm_deptname,\n");
					strQuery.append("       to_char(b.cr_confdate,'yyyy/mm/dd hh24:mi') confdate,           \n");
					strQuery.append("       to_char(a.cc_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,        \n");
					strQuery.append("       to_char(a.cc_eddate,'yyyy/mm/dd hh24:mi') as prcdate,           \n");
					strQuery.append("       substr(a.cc_acptno,1,4) || '-' || substr(a.cc_acptno,5,2) ||    \n");
					strQuery.append("       '-' || substr(a.cc_acptno,7,6) as acptno,'' cr_prcreq,          \n");
					strQuery.append("       d.cm_codename qrycd,'' cm_sysmsg,f.cm_username,'00' cr_sayucd,  \n");
					strQuery.append("       '' cr_passcd,'' cr_passok                                       \n");
					strQuery.append("   from cmc0100 h,cmm0043 g,cmm0040 f,cmm0020 d,cmm0100 c,cmr9900 b,cmc0300 a where \n");
					if (pStateCd.equals("0")) {
						strQuery.append("      b.cr_locat<>'00' and b.cr_status='0'                         \n");
						strQuery.append("      and b.cr_teamcd in ('4','5','9')                             \n");
						strQuery.append("      and g.cm_userid=?                                            \n");
						strQuery.append("      and instr(b.cr_team,g.cm_rgtcd)>0                            \n");
					} else if (pStateCd.equals("01")){//결재대기
						//strQuery.append("      b.cr_locat='00' and b.cr_status='0'                        \n");
						strQuery.append("      b.cr_status='0'                                              \n");
						strQuery.append("      and b.cr_teamcd in ('4','5','9')                             \n");
						strQuery.append("      and g.cm_userid=?                                            \n");
						strQuery.append("      and instr(b.cr_team,g.cm_rgtcd)>0                            \n");
						strQuery.append("and b.cr_locat>=(select substr(cr_confusr,1,2) from cmr9900        \n");
						strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
						strQuery.append("and b.cr_locat<=(select substr(cr_confusr,3,2) from cmr9900        \n");
						strQuery.append("                  where cr_acptno=b.cr_acptno and cr_locat='00')   \n");
					} else if (pStateCd.equals("02")){
						strQuery.append("      b.cr_locat<>'00' and b.cr_status='0'                         \n");
						strQuery.append("      and b.cr_teamcd in ('4','5','9')                             \n");
						strQuery.append("      and g.cm_userid=?                                            \n");
						strQuery.append("      and instr(b.cr_team,g.cm_rgtcd)>0                            \n");
					}
					if (pProc.equals("0")) {//전체일때
						strQuery.append("and (A.Cc_edDATE IS NOT NULL or   a.cc_status='0')  				\n");
					} else if (pProc.equals("1") || pProc.equals("2") || pProc.equals("4")){//1:미완료(에러+진행중) 2:시스템에러 4:진행중
						strQuery.append("and   a.cc_status='0'                                              \n");
					} else if (pProc.equals("3")){//반려
						strQuery.append("and   a.cc_status='3'                                              \n");
						strQuery.append("and   A.Cc_edDATE IS NOT NULL                                     \n");
					}else if (pStateCd.equals("9")){//완료
						strQuery.append("and   A.Cc_edDATE IS NOT NULL and a.cc_status<>'3'                \n");
					}
					if (!pReqCd.equals("0")) {
						strQuery.append("and a.cc_qrycd = ?                                               \n");
					}
					if (!pTeamCd.equals("0"))  strQuery.append("and a.cc_teamcd = ?                       \n");
					if (pReqUser != null && pReqUser.length() > 0) {
							strQuery.append("and a.cc_editor in (select cm_userid from cmm0040            \n");
							strQuery.append("                     where cm_username=?)                    \n");
					}
					strQuery.append("and b.cr_acptno = a.cc_acptno                                        \n");
					strQuery.append("and a.cc_teamcd = c.cm_deptcd                                        \n");
					strQuery.append("and a.cc_editor=f.cm_userid                                          \n");
					strQuery.append("and d.cm_macode='REQUEST' and d.cm_micode=a.cc_qrycd                 \n");
					strQuery.append("and a.cc_srid=h.cc_srid                                              \n");
					if(txtspms != null && txtspms.length() > 0){
						strQuery.append("and (h.cc_srid like ? or upper(h.cc_reqtitle) like upper(?))  \n");
					}
				}
			}
			strQuery.append("order by cr_acptno                                                       \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            Cnt = 0;
            if (pStateCd.equals("0")) {
	            pstmt.setString(++Cnt, pStartDt);
	            pstmt.setString(++Cnt, pEndDt);
	            pstmt.setString(++Cnt, pUserId);
	            pstmt.setString(++Cnt, pUserId);
            } else if (pStateCd.equals("01")){
            	pstmt.setString(++Cnt, pUserId);
            } else if (pStateCd.equals("02")){
            	pstmt.setString(++Cnt, pUserId);
            }else if (pStateCd.equals("03") || pStateCd.equals("04")){
            	pstmt.setString(++Cnt, pUserId);
                pstmt.setString(++Cnt, pStartDt);
                pstmt.setString(++Cnt, pEndDt);
            }
            if (!pReqCd.equals("0"))  {
            	if(pReqCd.equals("93")) pReqCd = "03"; //테스트폐기
            	if(pReqCd.equals("94")) pReqCd = "04"; //운영폐기
            	
            	pstmt.setString(++Cnt, pReqCd);
            }
			if (!pTeamCd.equals("0")) {
            	pstmt.setString(++Cnt, pTeamCd);
            }
			if (pReqUser != null && pReqUser.length() > 0){
	           pstmt.setString(++Cnt, pReqUser);
			}
			if(!syscd.equals("0")){
				 pstmt.setString(++Cnt, syscd);
			}
			if(!gbn.equals("0")){
				 pstmt.setString(++Cnt, gbn);
			}
			if(txtspms != null && txtspms.length() > 0){
				 pstmt.setString(++Cnt, "%"+txtspms+"%");
				 pstmt.setString(++Cnt, "%"+txtspms+"%");
			}

			if (pStateCd.equals("0") || pStateCd.equals("01") || pStateCd.equals("02")) {
		        pstmt.setString(++Cnt, pUserId);
				if (!pReqCd.equals("0"))  {
	            	if(pReqCd.equals("93")) pReqCd = "03"; //테스트폐기
	            	if(pReqCd.equals("94")) pReqCd = "04"; //운영폐기
	            	
	            	pstmt.setString(++Cnt, pReqCd);
	            }
				if (!pTeamCd.equals("0")) {
	            	pstmt.setString(++Cnt, pTeamCd);
	            }
				if (pReqUser != null && pReqUser.length() > 0){
		           pstmt.setString(++Cnt, pReqUser);
				}
				if(!syscd.equals("0")){
					 pstmt.setString(++Cnt, syscd);
				}
				if(!gbn.equals("0")){
					 pstmt.setString(++Cnt, gbn);
				}
				if(txtspms != null && txtspms.length() > 0){
					 pstmt.setString(++Cnt, "%"+txtspms+"%");
					 pstmt.setString(++Cnt, "%"+txtspms+"%");
				}
			}
			if (!dategbn.equals("2")) {
				if (pStateCd.equals("0")) {
		            pstmt.setString(++Cnt, pStartDt);
		            pstmt.setString(++Cnt, pEndDt);
		            pstmt.setString(++Cnt, pUserId);
		            pstmt.setString(++Cnt, pUserId);
	            } else if (pStateCd.equals("01")){
	            	pstmt.setString(++Cnt, pUserId);
	            } else if (pStateCd.equals("02")){
	            	pstmt.setString(++Cnt, pUserId);
	            }else if (pStateCd.equals("03") || pStateCd.equals("04")){
	            	pstmt.setString(++Cnt, pUserId);
	                pstmt.setString(++Cnt, pStartDt);
	                pstmt.setString(++Cnt, pEndDt);
	            }
	            if (!pReqCd.equals("0"))  {	            	
	            	pstmt.setString(++Cnt, pReqCd);
	            }
				if (!pTeamCd.equals("0")) {
	            	pstmt.setString(++Cnt, pTeamCd);
	            }
				if (pReqUser != null && pReqUser.length() > 0){
		           pstmt.setString(++Cnt, pReqUser);
				}
				if(txtspms != null && txtspms.length() > 0){
					 pstmt.setString(++Cnt, "%"+txtspms+"%");
					 pstmt.setString(++Cnt, "%"+txtspms+"%");
				}

				if (pStateCd.equals("0") || pStateCd.equals("01") || pStateCd.equals("02")) {
			        pstmt.setString(++Cnt, pUserId);
					if (!pReqCd.equals("0"))  {
		            	pstmt.setString(++Cnt, pReqCd);
		            }
					if (!pTeamCd.equals("0")) {
		            	pstmt.setString(++Cnt, pTeamCd);
		            }
					if (pReqUser != null && pReqUser.length() > 0){
			           pstmt.setString(++Cnt, pReqUser);
					}
					if(txtspms != null && txtspms.length() > 0){
						 pstmt.setString(++Cnt, "%"+txtspms+"%");
						 pstmt.setString(++Cnt, "%"+txtspms+"%");
					}
				}
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				ConfName = "";
				ConfTeamCd = "";
				Sunhang = "";
			    ColorSw = rs.getString("cr_status");
			    FindFg = 0;
				if (FindFg == 0) {
					if (rs.getString("cr_acptno").equals(AcptNo)) FindFg = 1;
				}
				if (FindFg == 0) {
					AcptNo = rs.getString("cr_acptno");

		            if (rs.getString("sta").equals("3")){
		            	ConfName = "반려";
		            }
		            else if (rs.getString("sta").equals("9")){
		            	ConfName = "결재완료";
		            }
		            else {
		            	strQuery.setLength(0);
						strQuery.append("select cr_teamcd,cr_confname,cr_congbn from cmr9900   \n");
					    strQuery.append(" where cr_acptno=? and cr_locat='00'        \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
			            //pstmt2.setString(1, rs.getString("cr_acptno"));
						pstmt2.setString(1, AcptNo);
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()){
			            	ConfTeamCd = rs2.getString("cr_teamcd");
			            	if (rs2.getString("cr_teamcd").equals("1")){
			            		ConfName = rs2.getString("cr_confname") + "중";
			            	}
			            	else{
			            		ConfName = rs2.getString("cr_confname") + "결재대기중";
			            	}

			            	if (rs2.getString("cr_congbn").equals("4")) {
			            		ConfName = "[후결]"+ConfName;
			            	}
			            }
			            rs2.close();
			            pstmt2.close();

		            }
		            if (Integer.parseInt(rs.getString("cr_qrycd")) < 30) {
		            	if (rs.getString("cr_prcdate") == null) {
		            		strQuery.setLength(0);
							strQuery.append("select count(*) as cnt from cmr1010   \n");
						    strQuery.append(" where cr_acptno=?                    \n");
						    strQuery.append("   and cr_putcode is not null         \n");
						    strQuery.append("   and cr_putcode<>'0000' and cr_prcdate is null \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
				            //pstmt2.setString(1, rs.getString("cr_acptno"));
				            pstmt2.setString(1, AcptNo);
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next() && !rs.getString("cr_qrycd").equals("46")){
				            	if (rs2.getInt("cnt") > 0) ColorSw = "5";
				            }
				            rs2.close();
				            pstmt2.close();

				            if (!ColorSw.equals("5")) {
				            	strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmr1011 a,cmr1010 b \n");
							    strQuery.append(" where b.cr_acptno=? and b.cr_prcdate is null   \n");
							    strQuery.append("   and b.cr_acptno=a.cr_acptno                  \n");
							    strQuery.append("   and b.cr_serno=a.cr_serno                    \n");
							    strQuery.append("   and nvl(cr_prcrst,'0000')<>'0000'            \n");
							    pstmt2 = conn.prepareStatement(strQuery.toString());
							    //pstmt2.setString(1, rs.getString("cr_acptno"));
					            pstmt2.setString(1, AcptNo);
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()){
					            	if (rs2.getInt("cnt") > 0) ColorSw = "5";
					            }
					            rs2.close();
					            pstmt2.close();

			            	}
		            	}
		            	strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmr1030 					 \n");
					    strQuery.append(" where cr_acptno=? or cr_befact=?                       \n");
					    pstmt2 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn,strQuery.toString());
			            pstmt2.setString(1, rs.getString("cr_acptno"));
			            pstmt2.setString(2, rs.getString("cr_acptno"));
			            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()){
			            	if (rs2.getInt("cnt") > 0) Sunhang = "유";
			            	else Sunhang = "무";
			            }
			            rs2.close();
			            pstmt2.close();

			            strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmr1001      \n");
					    strQuery.append(" where cr_acptno=?                       \n");
					    pstmt2 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn,strQuery.toString());
			            pstmt2.setString(1, rs.getString("cr_acptno"));
			            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()){
			            	if (rs2.getInt("cnt") > 0) DocYn = "첨부";
			            	else DocYn = "미 첨부";
			            }
			            rs2.close();
			            pstmt2.close();
		            }

		            if (rs.getString("cr_status").equals("3")){
		            	ColorSw = "3";
		            }
		            else if (rs.getString("cr_status").equals("9")){
		            	ColorSw = "9";
		            }
		            else if (rs.getString("cr_prcdate") != null){
		            	ColorSw = "9";
		            }

					rst = new HashMap<String, String>();
					rst.put("cm_sysmsg",   rs.getString("cm_sysmsg"));      //시스템
					//rst.put("cm_jobname", rs.getString("cm_jobname"));
	        		rst.put("acptno",  rs.getString("acptno"));         //요청번호
	        		rst.put("deptname",rs.getString("cm_deptname"));    //팀명
	        		rst.put("editor",  rs.getString("cm_username"));    //요청자
	        		if (rs.getString("cr_closeyn").equals("Y")) {
						if (rs.getString("cr_qrycd").equals("03")) reqName = "테스트폐기";
						else if (rs.getString("cr_qrycd").equals("04")) reqName = "테스트폐기"; //"운영폐기"; //운영 반영시 주석 해제
						else reqName = rs.getString("qrycd");
						if (rs.getString("cr_passok").equals("2")){ 
							rst.put("qrycd","[긴급]" + reqName);
						} else{
							rst.put("qrycd",reqName);
						}
					} else if (rs.getString("cr_passok") != null) {
						if (rs.getString("cr_passok").equals("2")){ 
							rst.put("qrycd","[긴급]" + rs.getString("qrycd"));
						} else{
							rst.put("qrycd",rs.getString("qrycd"));
						}
					} else {
						rst.put("qrycd",rs.getString("qrycd"));
					}
	        		//rst.put("qrycd",   rs.getString("qrycd"));			//요청구분
	        		//rst.put("sayu",    rs.getString("cr_sayu"));	    //요청내용

	        		//체크인 처리구분 조회(일반/수시/긴급) 여부
	        		rst.put("REQPASS","");
	        		if (rs.getString("cr_passok") != null && rs.getString("cr_passok") != ""){
			            strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020 \n");
					    strQuery.append(" where cm_macode='REQPASS' and cm_micode=? \n");
					    pstmt2 = conn.prepareStatement(strQuery.toString());
			            pstmt2.setString(1, rs.getString("cr_passok"));
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()){
			            	if (rs2.getString("cm_codename") != null && rs2.getString("cm_codename") != ""){
			            		rst.put("REQPASS", rs2.getString("cm_codename"));//체크인 처리구분 조회(일반/수시/긴급) 여부
			            	}
			            }
			            rs2.close();
			            pstmt2.close();
	        		}
	        		rst.put("sayu",rs.getString("cr_passcd"));//신청사유
	        		if(rs.getString("cc_srid")!=null) {
	        			rst.put("spms",rs.getString("spms"));
	        			rst.put("cc_srid", rs.getString("cc_srid"));
	        		} else {
	        			rst.put("spms", "");
	        			rst.put("cc_srid", "");
	        		}
	        		rst.put("acptdate",rs.getString("acptdate"));       //요청일시
	        		rst.put("signteamcd", ConfTeamCd);
	        		rst.put("sta",     ConfName);                       //상태
	        		rst.put("Sunhang",  Sunhang);    					 //선행작업 유무
	        		rst.put("DocYn",  DocYn);    					 //문서 첨부 유무
	        		rst.put("cr_qrycd",  rs.getString("cr_qrycd"));       //Qrycd
	        		rst.put("cr_editor",  rs.getString("cr_editor"));     //Editor
	        		rst.put("endyn",  rs.getString("cr_status"));       //처리완료여부
	        		rst.put("cr_syscd",  rs.getString("cr_syscd"));       //SysCd
	        		rst.put("cr_congbn", rs.getString("cr_congbn"));
	        		//rst.put("acptno2",  rs.getString("cr_acptno"));     //AcptNo
	        		rst.put("cr_acptno",  rs.getString("cr_acptno"));     //AcptNo
	        		if (rs.getString("confdate") != null) {
	        			rst.put("confdate", rs.getString("confdate"));
	        		}
					if (rs.getString("prcdate") != null) rst.put("prcdate", rs.getString("prcdate"));
					if (rs.getString("cr_prcreq") != null)
						rst.put("prcreq", rs.getString("cr_prcreq").substring(0, 4) + "/" +
								rs.getString("cr_prcreq").substring(4, 6) + "/" +
								rs.getString("cr_prcreq").substring(6, 8) + " " +
								rs.getString("cr_prcreq").substring(8, 10) + ":" +
								rs.getString("cr_prcreq").substring(10, 12));
	        		rst.put("colorsw",  ColorSw);

	        		switch (Integer.parseInt(ColorSw)){//진행상태
		        		case 3:
		        			rst.put("acptStatus", "반려 또는 취소");
		        			break;
		        		case 5:
		        			rst.put("acptStatus", "시스템오류");
		        			break;
		        		case 9:
		        			rst.put("acptStatus", "처리완료");
		        			break;
		        		default:
		        			rst.put("acptStatus", "진행중");
		        			break;
	        		}

	        		if (rs.getString("cr_sayucd") != null){
				    	strQuery.setLength(0);
				    	strQuery.append("select cm_codename from cmm0020 \n");
				    	strQuery.append("where cm_macode='REQSAYU' and \n");
				    	strQuery.append("cm_micode= ? \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1,rs.getString("cr_sayucd"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()){
							rst.put("sayucd", rs2.getString("cm_codename"));
						}else{
							rst.put("sayucd", "");
						}
						rs2.close();
						pstmt2.close();
					}else{
						rst.put("sayucd", "");
					}


	        		/*
	        		 * Cmr3100.gyulChk(gridLst.selectedItem.cr_acptno,strUserId);
	        		 */
	        		CallableStatement cs;
	    			cs = conn.prepareCall("{call GYUL_CHECK(?,?,?)}");
	                cs.setString(1, AcptNo);
	                cs.setString(2, pUserId);
	                cs.registerOutParameter(3,java.sql.Types.INTEGER);
	                cs.execute();
	                if (cs.getInt(3) == 0){//결재가능건
	                	rst.put("enabled1", "true");
	                	rst.put("checked", "false");
	                }
	                cs.close();
	                cs = null;

	        		rtList.add(rst);
	        		rst = null;
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
            rs2 = null;
            pstmt2 = null;
			conn = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3100.SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3100.SelectList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3100.SelectList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3100.SelectList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3100.SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectList() method statement

    public String gyulChk(String AcptNo,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String            retMsg      = "";

		try {
			conn = connectionContext.getConnection();
			ecamsLogger.error("++gyulChk++"+AcptNo+", "+UserId);
			CallableStatement cs = null;
			cs = conn.prepareCall("{call GYUL_CHECK(?,?,?)}");
            cs.setString(1, AcptNo);
            cs.setString(2, UserId);
            cs.registerOutParameter(3,java.sql.Types.INTEGER);
            cs.execute();

            retMsg = Integer.toString(cs.getInt(3));
            ecamsLogger.error("++gyulChk++"+AcptNo+", "+UserId+", "+retMsg);
            cs.close();
            conn.close();
            cs = null;
            conn = null;

    		return retMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3200.gyulChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3200.gyulChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3200.gyulChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3200.gyulChk() Exception END ##");
			throw exception;
		}finally{
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3200.gyulChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of gyulChk() method statement

    public String nextConf(String AcptNo,String UserId,String conMsg,String Cd,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retmsg      = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, ?, ?, '9', ?, ? ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1,AcptNo);
        	pstmt.setString(2,UserId);
        	pstmt.setString(3,conMsg);
        	pstmt.setString(4,ReqCd);
        	pstmt.setString(5,Cd);
        	pstmt.executeUpdate();
        	pstmt.close();
        	retmsg = "0";
        	/*
        	if(Cd.equals("3")){//신청건 반려 일때 백업파일 삭제 처리
        		Process p = null;
        		Runtime run = Runtime.getRuntime();
        		String[] chkAry = new String[3];
				chkAry[0] = "/bin/sh";
				chkAry[1] = new SystemPath().getTmpDir_conn("14",conn)+"/ecams_acct_bakdel";
				chkAry[2] = AcptNo;
				p = run.exec(chkAry);
				p.waitFor();
				p.exitValue();

				run = null;
				p = null;
				chkAry = null;
        	}
			*/
        	//pstmt1 = new LoggableStatement(conn,strQuery.toString());
			//ecamsLogger.debug(((LoggableStatement)pstmt1).getQueryString());

        	if (!Cd.equals("1")) {
        		/* 선행작업 반려 시 후행작업 자동반려 예외
        		strQuery.setLength(0);
        		strQuery.append("select a.cr_acptno,a.cr_confusr      \n");
        		strQuery.append("  from (Select cr_acptno             \n");
        		strQuery.append("          from cmr1030               \n");
        		strQuery.append("          start with cr_befact=?     \n");
        		strQuery.append("        connect by prior             \n");
        		strQuery.append("             cr_acptno=cr_befact) b, \n");
        		strQuery.append("  cmr9900 a                          \n");
        		strQuery.append(" where b.cr_acptno=a.cr_acptno       \n");
        		strQuery.append("   and a.cr_locat='00'               \n");
        		strQuery.append("   and a.cr_status='0'               \n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(1, AcptNo);
        		rs = pstmt2.executeQuery();
        		while (rs.next()) {
        			strQuery.setLength(0);
                	strQuery.append("Begin CMR9900_STR ( ");
                	strQuery.append("?, ?, ?, '9', ?, ? ); End;");
                	pstmt = conn.prepareStatement(strQuery.toString());
                	pstmt.setString(1, rs.getString("cr_acptno"));
                	pstmt.setString(2,UserId);
                	pstmt.setString(3,"선행작업 반려으로 인한 자동반려처리");
                	pstmt.setString(4,rs.getString("cr_confusr"));
                	pstmt.setString(5,"9");

                	pstmt.executeUpdate();
                	pstmt.close();
        		}
        		rs.close();
        		pstmt2.close();
        		*/
        	}
        	conn.close();
        	pstmt = null;
        	conn = null;

    		return retmsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3100.nextConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3100.nextConf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3100.nextConf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3100.nextConf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3100.nextConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of nextConf() method statement


    public void DelayTime(int delayTime)
    {
        long saveTime = System.currentTimeMillis();
        long currTime = 0;
        while( currTime - saveTime < delayTime){
            currTime = System.currentTimeMillis();
        }
    }

    public String nextAllConf(String UserId,String conMsg,String Cd,
    		ArrayList<HashMap<String,String>> AcptNoList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			for (int i=0 ; i<AcptNoList.size() ; i++)
			{
	        	strQuery.setLength(0);
	        	strQuery.append("Begin CMR9900_STR ( ");
	        	strQuery.append("?, ?, ?, '9', ?, ? ); End;");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1,AcptNoList.get(i).get("cr_acptno"));
	        	pstmt.setString(2,UserId);
	        	pstmt.setString(3,conMsg);
	        	pstmt.setString(4,AcptNoList.get(i).get("cr_qrycd"));
	        	pstmt.setString(5,Cd);
	        	pstmt.executeUpdate();
	        	pstmt.close();

	        	DelayTime(1000);//1초 delay
			}
			conn.commit();
        	conn.close();
        	pstmt = null;
        	conn = null;

    		return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.nextAllConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr3100.nextAllConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3100.nextAllConf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0900.nextAllConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr3100.nextAllConf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3100.nextAllConf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3100.nextAllConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of nextAllConf() method statement


    public String nextConf_ISR(String AcptNo,String UserId,String conMsg,String Cd,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();
		try {

			conn = connectionContext.getConnection();
        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, ?, ?, '9', ?, ? ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.setString(2,UserId);
        	pstmt.setString(3,conMsg);
        	pstmt.setString(4,ReqCd);
        	pstmt.setString(5,Cd);
        	pstmt.executeUpdate();
        	pstmt.close();

        	conn.close();
        	pstmt = null;
        	conn = null;

    		return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3100.nextConf_ISR() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3100.nextConf_ISR() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3100.nextConf_ISR() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3100.nextConf_ISR() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3100.nextConf_ISR() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of nextConf() method statement

}//end of Cmr3100 class statement