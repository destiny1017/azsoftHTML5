<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:import url="/webPage/common/common.jsp" />

<section>
	<div class="border-style-pull">
		<div class="row margin-5-top">
			<div class="col-xs-3">
				<div class="col-xs-6 no-padding">
					<sbux-button 
						id="btnTreeAllExp"
						name="btnTreeAllExp"
						uitype="normal"
						class="width-95 float-right"
						text="전체 확장"
		             	onclick="fnExpandAll">
	             	</sbux-button>
				</div>
				<div class="col-xs-6 no-padding">
					<sbux-button 
						id="btnTreeAllRedu"
						name="btnTreeAllRedu"
						class="width-95 float-left"
						uitype="normal" 
						text="전체 축소"
		             	onclick="fnExpandAll">
	             	</sbux-button>
				</div>
			</div>
			<div class="col-xs-3">
			</div>
			<div class="col-xs-6">
				<sbux-input 
					id="txtSearch" 
					name="txtSearch"
					style="width:100%;"
					uitype="search"
					placeholder="검색하고자 하는 이름을 입력하세요" 
					button-back-text="조회"
					autocomplete-ref="treeOrganizationAndPersonData"
					button-back-event="searchFileTree()"
					onkeyenter="searchFileTree()">
				</sbux-input>
			</div>
		</div>
		
		
		<div class="row margin-5-top">
			<div class="col-sm-12 file-tree-organization">
				<sbux-tree 
				  	id="treeOrganization" 
				  	name="treeOrganization" 
				  	uitype="normal" 
				  	jsondata-ref="treeOrganizationData" 
				  	empty-message="등록된 조직이 없습니다[조직도]."
				  	>
				</sbux-tree>
				
				<sbux-tree 
				  	id="treeOrganizationAndPerson" 
				  	name="treeOrganizationAndPerson" 
				  	uitype="normal" 
				  	jsondata-ref="treeOrganizationAndPersonData" 
				  	empty-message="등록된 조직이 없습니다.[사람찾기]"
				  	>
				</sbux-tree>
			</div>
		</div>
		
		<div class="row">
			<div class="col-xs-3 col-xs-offset-9">
				<div class="col-xs-6 no-padding">
					<sbux-button 
						id="btnUpdate"
						name="btnUpdate"
						uitype="normal"
						class="width-95 float-right"
						text="적용"
		             	onclick="updateTree()">
	             	</sbux-button>
				</div>
				<div class="col-xs-6 no-padding">
					<sbux-button 
						id="btnCancle"
						name="btnCancle"
						class="width-95 float-left"
						uitype="normal" 
						text="취소"
		             	onclick="cancleTree()">
	             	</sbux-button>
				</div>
			</div>
		</div>
	</div>
	
</section>

<script type="text/javascript" src="<c:url value="/js/ecams/modal/TreeOrganization.js"/>"></script>
