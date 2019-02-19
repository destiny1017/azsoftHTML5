<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>형상관리 로그인</title>

<c:import url="/webPage/common/common.jsp" />

</head>
<body>
	<section>
		<div class="login-form">
		    <form id="ecamsLoginForm" method="post">
		        <h2 class="text-center">로그인</h2>       
		        <div class="form-group">
		        	<sbux-input 
						id="idx_input_id" 
						name="idx_input_id"
						class="width-100" 
						uitype="text" 
						datastore-id="idxData1" 
						sbux-popover="<font color='blue' style='font-weight:bold;'>아이디</font>를 입력"
						sbux-popover-placement="bottom" 
						sbux-popover-html="true"
						placeholder="아이디를 입력하세요."
						required="required"
						>
					</sbux-input>
		        </div>
		        <div class="form-group">
		            <sbux-input 
						id="idx_input_pwd" 
						name="idx_input_pwd"
						class="width-100" 
						uitype="password" 
						datastore-id="idxData1" 
						sbux-popover="<font color='blue' style='font-weight:bold;'>비밀번호</font>를 입력"
						sbux-popover-placement="bottom" 
						sbux-popover-html="true"
						placeholder="비밀번호를 입력하세요."
						required="required"
						>
					</sbux-input>
		        </div>
		        <div class="form-group">
		            <sbux-button 
						id="idx_login_btn" 
						name="idx_login_btn" 
						class="btn btn-primary btn-block"
						uitype="submit" 
						text="로그인"
						button-color="blue"
						>
					</sbux-button>
		        </div>
		        <div class="clearfix">
		            <sbux-checkbox 
						id="chkbox_remember" 
						name="chkbox_remember"
						uitype="normal" 
						text="아이디/비밀번호 저장"
						style="color:blue;">
					</sbux-checkbox>
		            <a href="#" class="pull-right">Forgot Password?</a>
		        </div>        
		    </form>
		    <p class="text-center"><a href="#">Create an Account</a></p>
		</div>
	</section>
	
	<c:import url="/js/ecams/common/commonscript.jsp" />
	<script type="text/javascript" src="<c:url value="/js/ecams/login/login.js"/>"></script>
</body>
</html>