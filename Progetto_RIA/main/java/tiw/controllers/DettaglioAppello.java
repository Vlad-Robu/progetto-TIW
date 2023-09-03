package tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tiw.beans.StudentiAppelloBean;
import tiw.beans.User;
import tiw.dao.AppelloDAO;
import tiw.dao.DocenteDAO;
import tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class DettaglioAppello
 */
@WebServlet("/DettaglioAppello")
public class DettaglioAppello extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DettaglioAppello() {
        super();
        // TODO Auto-generated constructor stub
    }


	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
    	HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			String loginpath = request.getServletContext().getContextPath();
			response.setStatus(403);
			response.setHeader("Location", loginpath);
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
		User u = (User) s.getAttribute("user");

    	try {
    		
    		if (u.getRole().equals("docente")) {
    			DocenteDAO dDao = new DocenteDAO(connection, u.getId());
                if (!dDao.hasAppello(selectedAppelloId)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
                    return;
                }
        		List<StudentiAppelloBean> DettaglioAppello = null;
    			DettaglioAppello = aDao.findStudenti();	
    			
    			if (DettaglioAppello == null) {
    				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
    				return;
    			}
    			
    	    	Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
    	       	String json = gson.toJson(DettaglioAppello);
    	    	response.setContentType("application/json");
    			response.setCharacterEncoding("UTF-8");
    			response.setStatus(HttpServletResponse.SC_OK);
    			response.getWriter().write(json);

			} else if (u.getRole().equals("studente")) {
				if ( !aDao.hasStudente(u.getId()) ) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				    return;
				}
				StudentiAppelloBean DettaglioAppello = null;
				DettaglioAppello = aDao.findDatiStud(u.getId());
				
				if (DettaglioAppello == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
					return;
				}
				
		    	Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		       	String json = gson.toJson(DettaglioAppello);
		    	response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write(json);
				
			}

		
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage(); 
		    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage); 
			return;
		}
    	
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
