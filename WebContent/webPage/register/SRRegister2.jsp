<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/webPage/common/common.jsp" />



<style>
	.show-grid {
	    margin-bottom: 15px;
	}
	
	.show-grid [class^=col-] {
	    padding-top: 10px;
	    padding-bottom: 10px;
	    background-color: #eee;
	    background-color: rgba(86,61,124,.15);
	    border: 1px solid #ddd;
	    border: 1px solid rgba(86,61,124,.2);
	}
	
	/* USAGE
	<div class="row">
	  <div class="row-height">
	    <div class="col-xs-2 col-xs-height col-xs-middle">
	      <div class="inside"></div>
	    </div>
	    <div class="col-xs-4 col-lg-5 col-xs-height col-xs-middle">
	      <div class="inside"></div>
	    </div>
	  </div>
	</div>
	*/
	
	/* content styles */
	
	.inside {
	  margin-top: 20px;
	  margin-bottom: 20px;
	  background: #ededed;
	  background: -webkit-gradient(linear, left top, left bottom,color-stop(0%, #f4f4f4), color-stop(100%, #ededed));
	  background: -moz-linear-gradient(top, #f4f4f4 0%, #ededed 100%);
	  background: -ms-linear-gradient(top, #f4f4f4 0%, #ededed 100%);
	}
	.inside-full-height {
	  /*
	  // if you want to give content full height give him height: 100%;
	  // with content full height you can't apply margins to the content
	  // content full height does not work in ie http://stackoverflow.com/questions/27384433/ie-display-table-cell-child-ignores-height-100
	  */
	  height: 100%;
	  margin-top: 0;
	  margin-bottom: 0;
	}
	
	/* columns of same height styles */
	
	.row-height {
	  display: table;
	  table-layout: fixed;
	  height: 100%;
	  width: 100%;
	}
	.col-height {
	  display: table-cell;
	  float: none;
	  height: 100%;
	}
	.col-top {
	  vertical-align: top;
	}
	.col-middle {
	  vertical-align: middle;
	}
	.col-bottom {
	  vertical-align: bottom;
	}
	
	@media (min-width: 480px) {
	  .row-xs-height {
	    display: table;
	    table-layout: fixed;
	    height: 100%;
	    width: 100%;
	  }
	  .col-xs-height {
	    display: table-cell;
	    float: none;
	    height: 100%;
	  }
	  .col-xs-top {
	    vertical-align: top;
	  }
	  .col-xs-middle {
	    vertical-align: middle;
	  }
	  .col-xs-bottom {
	    vertical-align: bottom;
	  }
	}
	
	@media (min-width: 768px) {
	  .row-sm-height {
	    display: table;
	    table-layout: fixed;
	    height: 100%;
	    width: 100%;
	  }
	  .col-sm-height {
	    display: table-cell;
	    float: none;
	    height: 100%;
	  }
	  .col-sm-top {
	    vertical-align: top;
	  }
	  .col-sm-middle {
	    vertical-align: middle;
	  }
	  .col-sm-bottom {
	    vertical-align: bottom;
	  }
	}
	
	@media (min-width: 992px) {
	  .row-md-height {
	    display: table;
	    table-layout: fixed;
	    height: 100%;
	    width: 100%;
	  }
	  .col-md-height {
	    display: table-cell;
	    float: none;
	    height: 100%;
	  }
	  .col-md-top {
	    vertical-align: top;
	  }
	  .col-md-middle {
	    vertical-align: middle;
	  }
	  .col-md-bottom {
	    vertical-align: bottom;
	  }
	}
	
	@media (min-width: 1200px) {
	  .row-lg-height {
	    display: table;
	    table-layout: fixed;
	    height: 100%;
	    width: 100%;
	  }
	  .col-lg-height {
	    display: table-cell;
	    float: none;
	    height: 100%;
	  }
	  .col-lg-top {
	    vertical-align: top;
	  }
	  .col-lg-middle {
	    vertical-align: middle;
	  }
	  .col-lg-bottom {
	    vertical-align: bottom;
	  }
	}
	Rest
</style>

<div class="container">
	<h1>Auto resize columns</h1>
	<div class="row show-grid">
		<div class="row-height">
			<div class="col-md-7 col-height">.col-md-4<p>Zombie ipsum reversus ab viral inferno, nam rick grimes malum cerebro.</p></div>
			<div class="col-md-4 col-height">.col-md-4</div>
			<div class="col-md-4 col-height">.col-md-4
				<p>De apocalypsi gorger omero undead survivor dictum mauris. Hi mindless mortuis soulless creaturas, imo evil stalking monstra adventus resi dentevil vultus comedat cerebella viventium. Qui animated corpse, cricket bat max brucks terribilem incessu zomby. The voodoo sacerdos flesh eater, suscitat mortuos comedere carnem virus. </p>
			</div>
			<div class="col-md-4 col-height">.col-md-4</div>
			<div class="col-md-4 col-height">.col-md-4</div>
		</div>
	</div>
	<br>
	<h2>using height inside -sm- media query</h2>
	<div class="row show-grid">
		<div class="row-sm-height">
			<div class="col-xs-12 col-sm-6 col-sm-height">
				<div class="inside">
					<div class="content">col-xs-12 col-sm-6 col-sm-height<br><br><br><br><br><br><br></div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-3 col-sm-height col-sm-top">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-3 col-sm-height col-sm-top</div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-2 col-sm-height col-sm-middle">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-2 col-sm-height col-sm-middle</div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-1 col-sm-height col-sm-bottom">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-1 col-sm-height col-sm-bottom"</div>
				</div>
			</div>
		</div>
	</div>
	  
	<h2>using height inside mixed media query</h2>
	<div class="row show-grid">
		<div class="row-sm-height">
			<div class="col-xs-12 col-sm-6 col-sm-height col-md-3">
				<div class="inside">
					<div class="content">col-xs-12 col-sm-6 col-sm-height col-md-3<br><br><br><br><br><br><br></div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-3 col-sm-height col-sm-top col-md-3">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-3 col-sm-height col-sm-top col-md-3</div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-2 col-sm-height col-sm-middle col-md-3">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-2 col-sm-height col-sm-middle col-md-3</div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-1 col-sm-height col-sm-bottom col-md-3">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-1 col-sm-height col-sm-bottom col-md-3"</div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-2 col-sm-height col-sm-middle col-md-3">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-2 col-sm-height col-sm-middle col-md-3</div>
				</div>
			</div>
			<div class="col-xs-6 col-sm-1 col-sm-height col-sm-bottom col-md-3">
				<div class="inside">
					<div class="content">col-xs-6 col-sm-1 col-sm-height col-sm-bottom col-md-3"</div>
				</div>
			</div>
		</div>
	</div>
</div>



