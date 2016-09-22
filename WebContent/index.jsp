<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%@ page language="java" import="java.util.*,java.lang.*" %> 
  <%@ page import="org.json.simple.JSONObject"%>
  <%@ page import="org.json.simple.JSONArray"%>
<%@ page import="java4s.TennisDAO" %>




                  				<%try{
									TennisDAO obj=new TennisDAO();
									JSONObject jsonObj=new JSONObject();
									JSONArray jsonArray=obj.findCurrentMatches();
									%>
									<form action="login" method="post">
									<input list="browsers" name="browser">
									<datalist id="browsers">
									<%for (int i = 0; i < jsonArray.size(); i++) {
										JSONObject str=(JSONObject)jsonArray.get(i);
										String tmp=(String)str.get("name");
									%>
                                    <option value="<%=tmp%>">
 									 <%}}
        catch(Exception e)
        {
             out.println("wrong entry"+e);
        }%>
  </datalist>
  <input type="submit" value="Login">
</form>

</body>
</html>