package cc.ayakurayuki.spring.components.starter.http.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;

public class MonitorPingServlet extends HttpServlet {

  @Serial
  private static final long serialVersionUID = -1718533547751798132L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType("text/plain; charset=utf-8");
    try (PrintWriter writer = resp.getWriter()) {
      writer.write("success");
      writer.flush();
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    super.doGet(req, resp);
  }

}
