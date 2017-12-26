<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>AoAoGo Search Result</title>

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
<%@ page import = "java.util.Arrays" %>
<%@ page import = "java.util.ArrayList" %>

<% 
String query = (String)request.getAttribute("query"); 
String timeUsed = (String)request.getAttribute("timeUsed"); 

ArrayList <String> result = new ArrayList<String>();
result = (ArrayList <String>)request.getAttribute("result");
%>
  	<nav class="navbar navbar-default">
  		<div class="container-fluid">
    		<div class="navbar-header">
      			<a class="navbar-brand" ><img src="img/icon.png"></a>
      			<a class="navbar-brand" href="#">AoAoGo</a>
    		</div>
    		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1 ">
    		<ul class="nav navbar-nav navbar-right">
        		<li><a href="index.jsp">Search <span class="sr-only">(current)</span></a></li>
      		</ul>
    		</div>
  		</div>
	</nav><!-- navbar navbar-default -->
  
  	<div class="row">
  		<div class="col-md-1"> </div>
  		<div class="col-md-6"> 
  			
  			<div class="row">

				<div class="container">
    			<form class="input-group input-group-lg" action="/Step3/Search"  method="POST">
      				<input type="text" class="form-control"  name="query" placeholder="">
      				<span class="input-group-btn">
        			<button class="btn btn-default" type="submit">  
        			<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 
        			</button>
      				</span>
    			</form><!-- /form -->
    			</div>
			</div><!-- /.row -->

        		
        	</div>
  		<div class="col-md-5"></div>
  	</div><!-- /.row -->

	<div class="row">
	  	<div class="col-md-1"> </div>
	  	<div class="col-md-8"> 
			<div class="page-header">
  					<h1><small>Search result for</small>   <%out.println(query); %></h1>
  					<p>Time used: <%out.println(timeUsed); %> seconds</p>
  					<p><%out.println(result.size()); %> results in total</p>
			</div>
			<div id="result">
				<%
				if(result.size()==0){
					out.print("No such word and please try another word");
				}
				else{
					for(int i=0;i<result.size()&&!result.get(i).equals("");i++){
						String[] results = result.get(i).split("\t");
						out.print("</br><a href="+results[0]+">" + results[0]+"</a>");
						out.print("<p>"+results[1]+"</p>");
						out.print("<HR style='margin-top:40px'/>");
					}
				}
				%>
			</div>
		</div>
		<div class="col-md-3"></div>
	</div>
	<script> 
function highlight(idVal, keyword) { 
var textbox = document.getElementById(idVal); 
if ("" == keyword) return; 
//获取所有文字内容 
var temp = textbox.innerHTML; 
console.log(temp); 
var htmlReg = new RegExp("\<.*?\>", "i"); 
var arr = new Array(); 

//替换HTML标签 
for (var i = 0; true; i++) { 
//匹配html标签 
var tag = htmlReg.exec(temp); 
if (tag) { 
arr[i] = tag; 
} else { 
break; 
} 
temp = temp.replace(tag, "{[(" + i + ")]}"); 
} 


// 讲关键词拆分并入数组 
words = decodeURIComponent(keyword.replace(/\,/g, ' ')).split(/\s+/); 

//替换关键字 
for (w = 0; w < words.length; w++) { 
// 匹配关键词，保留关键词中可以出现的特殊字符 
var r = new RegExp("(" + words[w].replace(/[(){}.+*?^$|\\\[\]]/g, "\\$&") + ")", "ig"); 
temp = temp.replace(r, "<b style='color:Red;'>$1</b>"); 
} 

//恢复HTML标签 
for (var i = 0; i < arr.length; i++) { 
temp = temp.replace("{[(" + i + ")]}", arr[i]); 
} 
textbox.innerHTML = temp; 
} 
var s = "<%=query%>";
var split = s.split(" ");
for(var i = 0;i<split.length;i++){
	if (!(split[i]==("") || split[i]==(" "))) highlight("result",split[i]); 
}
</script> 
  </body>
</html>