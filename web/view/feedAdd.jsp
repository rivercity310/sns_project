<%--
  Created by IntelliJ IDEA.
  User: Seungsu
  Date: 2022-10-07
  Time: 오후 7:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.File" %>
<%@ page import="dao.FeedDAO" %>

<%
  request.setCharacterEncoding("utf-8");

  String uid = null, ucon = null, ufname = null;
  byte[] ufile = null;

  ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
  List items = sfu.parseRequest(request);
  Iterator iter = items.iterator();

  while (iter.hasNext()) {
    FileItem item = (FileItem)iter.next();
    String name = item.getFileIdName();

    if (item.isFormField()) {
      String value = item.getString("utf-8");
      if (name.equals("id")) uid = value;
      else if (name.equals("content")) ucon = value;
    }
    else {
      if (name.equals("image")) {
        ufname = item.getName();
        ufile = item.get();
        String root = application.getRealPath(java.io.File.separator);
        FileUtil.saveImage(root, ufname, ufile);
      }
    }
  }

  FeedDAO dao = new FeedDAO();
  if (dao.insert(uid, ucon, ufname))
    response.sendRedirect("main.jsp");
  else
    out.print("업로드 중 오류 발생");
%>
