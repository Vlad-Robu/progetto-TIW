package tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import tiw.beans.CorsoBean;
import tiw.beans.User;
import tiw.dao.StudenteDAO;
import tiw.utils.ConnectionHandler;

@WebServlet("/GoToHomeStudente")
public class GoToHomeStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToHomeStudente() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		User u = (User) s.getAttribute("user");
		StudenteDAO aDao = new StudenteDAO(connection, u.getId());
		List<CorsoBean> corsi = null;

		try {
			corsi = aDao.findCorsi();
			
			if (corsi == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage(); 
		    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage); 
			return;
		}

		request.getSession().setAttribute("corsi", corsi);
		String path = "/WEB-INF/HomeStudente.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("corsi", corsi);
		if ( corsi.size() == 0 ) {
			ctx.setVariable("errorMsg", "Non ci sono corsi");
		}
		templateEngine.process(path, ctx, response.getWriter());
	}
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
