<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
 
<%
BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
<head>
<title>Upload a picture</title>
</head>
<body>
<hr/>
<h2>Upload a picture!</h2>
<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
File :
<input type="file" name="myFile"/>
<input type="submit" value="Submit"/>
</form>
</body>
</html>