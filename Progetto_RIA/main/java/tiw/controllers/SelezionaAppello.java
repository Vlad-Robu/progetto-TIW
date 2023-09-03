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

import tiw.beans.AppelloBean;
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

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SelezionaAppello() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
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
		
		Integer selectedCorsoId = null;
		
		try {
			selectedCorsoId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("corso")));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
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

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		String json = gson.toJson(appelli);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(json);
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
