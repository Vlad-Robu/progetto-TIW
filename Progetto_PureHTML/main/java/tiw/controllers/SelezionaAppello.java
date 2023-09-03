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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import tiw.beans.AppelloBean;
import tiw.beans.CorsoBean;
import tiw.beans.User;
import tiw.dao.AppelloDAO;
import tiw.dao.DocenteDAO;
import tiw.dao.StudenteDAO;
import tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class SelezionaAppello
 */
@WebServlet("/SelezionaAppello")
public class SelezionaAppello extends HttpServlet {
	private Connection connection = null;
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SelezionaAppello() {
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

		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer selectedCorsoId = null;
		
		try {
			selectedCorsoId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("corso")));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<CorsoBean> corsi = (List<CorsoBean>) request.getSession().getAttribute("corsi");
		List<AppelloBean> appelli = null;
		User u = (User) s.getAttribute("user");
		
		AppelloDAO aDao = new AppelloDAO(connection, selectedCorsoId);

		try {
			
			if (u.getRole().equals("docente")) {
				DocenteDAO dDao = new DocenteDAO(connection, u.getId());
				if ( !dDao.hasCorso(selectedCorsoId) ) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				    return;
				}
				
				appelli = aDao.findAppelli(selectedCorsoId);
					
			} else if (u.getRole().equals("studente")) {
				StudenteDAO sDao = new StudenteDAO(connection, u.getId());
				if ( !sDao.hasCorso(selectedCorsoId) ) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				    return;
				}
				
				appelli = aDao.findAppelliStud(selectedCorsoId, u.getId());

			}
			
			if (appelli == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}

			
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage);
			return;
		}

		
		String path = (u.getRole().equals("docente")) ? "/WEB-INF/HomeDocente.html" : "/WEB-INF/HomeStudente.html";

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("selectedCorsoId", selectedCorsoId);
		ctx.setVariable("corsi", corsi);
		ctx.setVariable("appelli", appelli);
		if ( appelli.size() == 0 ) {
			ctx.setVariable("errorMsg", "Non ci sono appelli");
		}
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
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
