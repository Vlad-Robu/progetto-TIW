package tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

import tiw.beans.StudentiAppelloBean;
import tiw.beans.User;
import tiw.dao.AppelloDAO;
import tiw.dao.DocenteDAO;
import tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class ModificaVoto
 */
@WebServlet("/ModificaVoto")
public class ModificaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModificaVoto() {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer selectedAppelloId = null; 
        Integer selectedStudentId = null;   
		try {
            selectedAppelloId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("selectedAppelloId")));
        	selectedStudentId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("studentId")));
			
		} catch (NumberFormatException | NullPointerException e) {
            // Handle the case where the parameter is not present
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
		}
		        	    	    
		StudentiAppelloBean student = null;
    	AppelloDAO aDao = new AppelloDAO(connection, selectedAppelloId);
		User u = (User) s.getAttribute("user");

    	try {
    		
    		DocenteDAO dDao = new DocenteDAO(connection, u.getId());
            if (!dDao.hasAppello(selectedAppelloId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
                return;
            }
           
			if ( !aDao.hasStudente(selectedStudentId) ) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
			    return;
			}
    		
			student = aDao.findDatiStud(selectedStudentId);
			
			if (student == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage(); 
		    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage); 
			return;
		}

		List<String> votoValues = new ArrayList<>();
		votoValues.add(" ");
		votoValues.add("assente");
		votoValues.add("rimandato");
		votoValues.add("riprovato");
		votoValues.add("18");
		votoValues.add("19");
		votoValues.add("20");
		votoValues.add("21");
		votoValues.add("22");
		votoValues.add("23");
		votoValues.add("24");
		votoValues.add("25");
		votoValues.add("26");
		votoValues.add("27");
		votoValues.add("28");
		votoValues.add("29");
		votoValues.add("30");
		votoValues.add("30 e lode");


		String path = "/WEB-INF/ModificaVoto.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("votoValues", votoValues);
		ctx.setVariable("student", student);
		ctx.setVariable("selectedAppelloId", selectedAppelloId);
		ctx.setVariable("infoAppello", student.getAppelloBean());
		templateEngine.process(path, ctx, response.getWriter());	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession s = request.getSession();
		
		Integer selectedAppelloId = null; 
        Integer selectedStudentId = null;   
        String votoStr = null;
		try {
            selectedAppelloId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("selectedAppelloId")));
        	selectedStudentId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("idstudente")));
        	votoStr = StringEscapeUtils.escapeJava(request.getParameter("voto"));
        				
		} catch (NumberFormatException | NullPointerException e) {
            // Handle the case where the parameter is not present
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
		}
                
    	AppelloDAO aDao = new AppelloDAO(connection, selectedAppelloId);
		User u = (User) s.getAttribute("user");

    	try {
    		
    		DocenteDAO dDao = new DocenteDAO(connection, u.getId());
            if (!dDao.hasAppello(selectedAppelloId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
                return;
            }
           
			if ( !aDao.hasStudente(selectedStudentId) ) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
			    return;
			}
			
    		aDao.UpdateVotoStud(selectedAppelloId, selectedStudentId, votoStr);
    		
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage(); 
		    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage); 
			return;
		}
    	String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/ModificaVoto?studentId=" + selectedStudentId + "&selectedAppelloId=" + selectedAppelloId;
		response.sendRedirect(path);
		
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
