package tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

import tiw.beans.StudentiAppelloBean;
import tiw.beans.User;
import tiw.dao.AppelloDAO;
import tiw.dao.DocenteDAO;
import tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class SortOrder
 */
@WebServlet("/SortOrder")
public class SortOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SortOrder() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
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

    	
		@SuppressWarnings("unchecked")
		Map<String, String> sortOrders = (Map<String, String>) s.getAttribute("sortOrders");

    	String sortColumn = StringEscapeUtils.escapeJava(request.getParameter("sort"));
    	if (sortColumn == null) {
            // Handle the case where the parameter is not present
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }
    	
    	String currentOrder = sortOrders.get(sortColumn);

	    // Toggle the sorting order
	    String newOrder = "ASC".equals(currentOrder) ? "DESC" : "ASC";
	    sortOrders.put(sortColumn, newOrder);
	    s.setAttribute("sortOrders", sortOrders);
    	
	    List<StudentiAppelloBean> DettaglioAppello = null;
    	AppelloDAO aDao = new AppelloDAO(connection, selectedAppelloId);
		User u = (User) s.getAttribute("user");

    	try {
    		
    		DocenteDAO dDao = new DocenteDAO(connection, u.getId());
            if (!dDao.hasAppello(selectedAppelloId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
                return;
            }
            
			DettaglioAppello = aDao.findStudenti(sortColumn, newOrder, selectedAppelloId);
			
			if (DettaglioAppello == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage(); 
		    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage); 
			return;
		}
		        	
		String path = "/WEB-INF/DettaglioAppelloDocente.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("sortedData", DettaglioAppello);
		ctx.setVariable("selectedAppelloId", selectedAppelloId);
		ctx.setVariable("sortOrders", sortOrders);
		ctx.setVariable("infoAppello", DettaglioAppello.get(0).getAppelloBean());
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
