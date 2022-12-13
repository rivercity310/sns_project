<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-17
  Time: 오전 3:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="java.io.File" %>
<%@ page import="util.FileUtil" %>
<%@ page import="dao.UserDAO" %>

<%
  request.setCharacterEncoding("utf-8");

  String uid = null;
  String jsonstr = null;
  String ufname = null;
  byte ufile[] = null;

  ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
  List items = sfu.parseRequest(request);
  Iterator iter = items.iterator();

  while (iter.hasNext()) {
    FileItem item = (FileItem)iter.next();
    String name = item.getFieldName();

    if (item.isFormField()) {
      String value = item.getString("utf-8");
      if (name.equals("id")) uid = value;
      if (name.equals("jsonstr")) jsonstr = value;
    }
    else {
      if (name.equals("image")) {
        ufname = item.getName();
        ufile = item.get();
        String root = application.getRealPath(File.separator);
        FileUtil.saveImage(root, ufname, ufile);
      }
    }
  }

  UserDAO dao = new UserDAO();
  if (dao.update(uid, jsonstr)) out.print("OK");
  else out.print("ER");
%>