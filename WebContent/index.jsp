<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>AoAoGo Search Engine</title>

    <!-- Bootstrap -->
    
    <link href="css/bootstrap.min.css" rel="stylesheet">
    

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
    
    <link rel="SHORTCUT ICON" href="img/iconb.ico"> 
  </head>
  
  
  <body>
  	<nav class="navbar navbar-default">
  		<div class="container-fluid">
    		<div class="navbar-header">
      			<a class="navbar-brand" ><img src="img/icon.png"></a>
      			<a class="navbar-brand" href="result.html">AoAoGo</a>
    		</div>
    		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1 ">
    		<ul class="nav navbar-nav navbar-right">
        		<li class="active"><a href="#">Search <span class="sr-only">(current)</span></a></li>
      		</ul>
    		</div>
  		</div>
	</nav><!-- navbar navbar-default -->
  
  	<div class="row">
  		<div class="col-md-3"> </div>
  		<div class="col-md-6"> 
  			
  			<div class="row">
  			
  				<div class="jumbotron">
  					<div class="container">
  					<h1>Hello, U!</h1>
  						<p>Welcome to AoAoGo2.0.</p>
  						<p>Now let's get started!</p>
					</div>
				</div>
					

    			<form class="input-group input-group-lg" action="/Step3/Search" method="POST">
      				<input type="text" class="form-control" name="query" placeholder="Search for...">
      				<span class="input-group-btn">
        			<button class="btn btn-default" type="submit">  
        			<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 
        			</button>
      				</span>
    			</form><!-- /input-group -->
    			


    			
			</div><!-- /.row -->

        		
        	</div>

  		<div class="col-md-3"></div>
  	</div><!-- /.row -->


    
  </body>
</html>