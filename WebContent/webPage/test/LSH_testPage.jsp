<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="/webPage/common/common.jsp" />
<c:import url="/js/ecams/common/commonscript.jsp" />

<!-- Vendor scripts -->
	<script type="text/javascript" src="<c:url value="/vendor/jquery/dist/jquery.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/jquery-ui/jquery-ui.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/slimScroll/jquery.slimscroll.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/bootstrap/dist/js/bootstrap.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/metisMenu/dist/metisMenu.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/iCheck/icheck.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/peity/jquery.peity.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/sparkline/index.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/vendor/nestable/jquery.nestable.js"/>"></script>

	<script src="<c:url value="/scripts/ax5select.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/homer.js"/>"></script>
<script src="<c:url value="/scripts/charts.js"/>"></script>
<style>
#toast-container {
}

#toast-container.toast-bottom-center > div, #toast-container.toast-top-center > div {
    margin: 10px auto 0;
}

#toast-container > .toast-info,
#toast-container > .toast-error,
#toast-container > .toast-warning,
#toast-container > .toast-success{
    background-image: none;
}

#toast-container > div {
    background: #fff;
    padding: 20px;
    color: #6a6c6f;
    box-shadow: 0 0 1px rgba(0, 0, 0, 0.1), 0 2px 4px rgba(0, 0, 0, 0.2);
    opacity: 1;
}


#toast-container > div:hover {
    box-shadow: 0 0 1px rgba(0, 0, 0, 0.1), 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toast-close-button {
    color: #000;
    opacity: 0.2;
}

.toast-info {
    background: #fff;
    border-left: 6px solid #3498db;
}
.toast-success {
    background: #fff;
    border-left: 6px solid #62cb31;
}
.toast-warning {
    background: #fff;
    border-left: 6px solid #ffb606;
}
.toast-error {
    background: #fff;
    border-left: 6px solid #e74c3c;
}

.toast-progress {
    opacity: 0.6;
}

.toast-info .toast-progress {
    background-color: #3498db;
}
.toast-success .toast-progress {
    background-color: #62cb31;
}
.toast-warning .toast-progress {
    background-color: #ffb606;
}
.toast-error .toast-progress {
    background-color: #e74c3c;
}

/* Nestable list */

.dd {
    position: relative;
    display: block;
    margin: 0;
    padding: 0;
    list-style: none;
    font-size: 13px;
    line-height: 20px;
}

.dd-list {
    display: block;
    position: relative;
    margin: 0;
    padding: 0;
    list-style: none;
}

.dd-list .dd-list {
    padding-left: 30px;
}

.dd-collapsed .dd-list {
    display: none;
}

.dd-item,
.dd-empty,
.dd-placeholder {
    display: block;
    position: relative;
    margin: 0;
    padding: 0;
    min-height: 20px;
    font-size: 13px;
    line-height: 20px;
}

.dd-handle {
    display: block;
    margin: 5px 0;
    padding: 5px 10px;
    color: #333;
    text-decoration: none;
    border: 1px solid #e4e5e7;
    background: #f7f9fa;
    -webkit-border-radius: 3px;
    border-radius: 3px;
    box-sizing: border-box;
    -moz-box-sizing: border-box;
}

.dd-handle span {
    font-weight: bold;
}

.dd-handle:hover {
    background: #f0f0f0;
    cursor: pointer;
    font-weight: bold;
}

.dd-item > button {
    display: block;
    position: relative;
    cursor: pointer;
    float: left;
    width: 25px;
    height: 20px;
    margin: 5px 0;
    padding: 0;
    text-indent: 100%;
    white-space: nowrap;
    overflow: hidden;
    border: 0;
    background: transparent;
    font-size: 12px;
    line-height: 1;
    text-align: center;
    font-weight: bold;
}

.dd-item > button:before {
    content: '+';
    display: block;
    position: absolute;
    width: 100%;
    text-align: center;
    text-indent: 0;
}

.dd-item > button[data-action="collapse"]:before {
    content: '-';
}


.dd-placeholder,
.dd-empty {
    margin: 5px 0;
    padding: 0;
    min-height: 30px;
    background: #f2fbff;
    border: 1px dashed #e4e5e7;
    box-sizing: border-box;
    -moz-box-sizing: border-box;
}

.dd-empty {
    border: 1px dashed #bbb;
    min-height: 100px;
    background-color: #e5e5e5;
    background-image: -webkit-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff), -webkit-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff);
    background-image: -moz-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff), -moz-linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff);
    background-image: linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff), linear-gradient(45deg, #fff 25%, transparent 25%, transparent 75%, #fff 75%, #fff);
    background-size: 60px 60px;
    background-position: 0 0, 30px 30px;
}

.dd-dragel {
    position: absolute;
    z-index: 9999;
    pointer-events: none;
}

.dd-dragel > .dd-item .dd-handle {
    margin-top: 0;
}

.dd-dragel .dd-handle {
    -webkit-box-shadow: 2px 4px 6px 0 rgba(0, 0, 0, .1);
    box-shadow: 2px 4px 6px 0 rgba(0, 0, 0, .1);
}

/**
* Nestable Extras
*/
.nestable-lists {
    display: block;
    clear: both;
    padding: 30px 0;
    width: 100%;
    border: 0;
    border-top: 2px solid #e4e5e7;
    border-bottom: 2px solid #e4e5e7;
}

#nestable-output,
#nestable2-output {
    width: 100%;
    font-size: 0.75em;
    line-height: 1.333333em;
    font-family: open sans, lucida grande, lucida sans unicode, helvetica, arial, sans-serif;
    padding: 5px;
    box-sizing: border-box;
    -moz-box-sizing: border-box;
}

#nestable-output,
#nestable2-output {
    font-size: 12px;
    padding: 25px;
    box-sizing: border-box;
    -moz-box-sizing: border-box;
}
		
</style>

<script>

    $(function () {

        var updateOutput = function (e) {
            var list = e.length ? e : $(e.target),
                    output = list.data('output');
            if (window.JSON) {
                output.val(window.JSON.stringify(list.nestable('serialize')));//, null, 2));
            } else {
                output.val('JSON browser support required for this demo.');
            }
        };
        // activate Nestable for list 1
        $('#nestable').nestable({
            group: 1
        }).on('change', updateOutput);

        // activate Nestable for list 2
        $('#nestable2').nestable({
            group: 1
        }).on('change', updateOutput);

        // output initial serialised data
        updateOutput($('#nestable').data('output', $('#nestable-output')));
        updateOutput($('#nestable2').data('output', $('#nestable2-output')));
    });
</script>
	
<section>
<div class="content">
	<div class="row">
		<div class="col-sm-6">
			<div class="col-sm-2 no-padding">
				<label  id="lbDept" name="lbDept" >신청부서</label>
			</div>
			<div class="form-group col-sm-5 no-padding">
				 <div id="Cbo_selMenu" data-ax5select="Cbo_selMenu" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;" onchange="cbo_selMenu_click();"></div>
			</div>
			<div class="form-group col-sm-5 no-padding">
				 <div id="Cbo_Menu" data-ax5select="Cbo_Menu" data-ax5select-config="{size:'sm',theme:'primary'}" style="width:100%;" onchange="Cbo_Menu_Click();"></div>
			</div>
		</div>
		<div class="col-sm-6">
			<div class="form-group">
				<button class="btn btn-default" onclick="Cmd_Ip_Click();">적용</button>
			</div>
		</div>
	</div>
	<div class="row">
	    <div class="col-lg-6">
	        <div class="hpanel">
	            <div class="panel-body">
	                <div class="dd" id="nestable2" style="overflow-y:auto; height:300px;">
	                    <ol id="tmpTest" class="dd-list">
	                    </ol>
	                </div>
	            </div>
	        </div>
	    </div>
	    <div class="col-lg-6">
	        <div class="hpanel">
	            <div class="panel-body">
	                <div class="dd" id="nestable" style="overflow-y:auto; height:300px;">
	                    <ol id="tmpTest2" class="dd-list">
	                    </ol>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	</div>
</section>

<section>
	<div class="container-fluid" >
		<div data-ax5grid="first-grid" data-ax5grid-config="{}" style="height: 500px;"></div>
	</div>
</section>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.8.18/themes/base/jquery-ui.css" type="text/css" />

<script type="text/javascript" src="<c:url value="/js/ecams/dev/LSH_testPage.js"/>"></script>
