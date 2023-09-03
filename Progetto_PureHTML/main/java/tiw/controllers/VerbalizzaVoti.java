package tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import tiw.beans.User;
import tiw.beans.VerbaleBean;
import tiw.dao.AppelloDAO;
import tiw.dao.DocenteDAO;
import tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class VerbalizzaVoti
 */
@WebServlet("/VerbalizzaVoti")
public class VerbalizzaVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VerbalizzaVoti() {
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		HttpSession s = request.getSession();
		VerbaleBean verbale = (VerbaleBean) s.getAttribute("verbaleData");
		if (verbale == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return;
		}

		String path = "/WEB-INF/Verbale.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("verbale", verbale);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		Integer selectedAppelloId = null; 
		try {
            selectedAppelloId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("selectedAppelloId")));
			
		} catch (NumberFormatException | NullPointerException e) {
            // Handle the case where the parameter is not present
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
		}
		
		AppelloDAO aDao = new AppelloDAO(connection, selectedAppelloId);
		VerbaleBean verbale = null;
		User u = (User) s.getAttribute("user");

		try {
			DocenteDAO dDao = new DocenteDAO(connection, u.getId());
            if (!dDao.hasAppello(selectedAppelloId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
                return;
            }
            
			verbale = aDao.VerbalizzaVoti(selectedAppelloId);
			
			if (verbale == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage);
			return;
		}

		s.setAttribute("verbaleData", verbale);
	    String redirectURL = request.getContextPath() + "/VerbalizzaVoti";
		response.sendRedirect(redirectURL);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
