package tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class RifiutaVoto
 */
@WebServlet("/RifiutaVoto")
public class RifiutaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RifiutaVoto() {
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			String loginpath = request.getServletContext().getContextPath();
			response.setStatus(403);
			response.setHeader("Location", loginpath);
			return;
		}
		
		User u = (User) s.getAttribute("user");
		if (!u.getRole().equals("studente")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403
            return;
        }

		Integer selectedAppelloId = null;
		Integer selectedStudentId = null;
		try {
			selectedAppelloId = Integer
					.parseInt(StringEscapeUtils.escapeJava(request.getParameter("selectedAppelloId")));
			selectedStudentId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("idstudente")));

		} catch (NumberFormatException | NullPointerException e) {
			// Handle the case where the parameter is not present
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		AppelloDAO aDao = new AppelloDAO(connection, selectedAppelloId);

		try {
			if (!aDao.hasStudente(selectedStudentId) || !(u.getId() == (selectedStudentId))) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}

			aDao.rifiutaVoto(selectedStudentId);
			
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

		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, errorMessage);
			return;
		}
		// String ctxpath = getServletContext().getContextPath();
		// String path = ctxpath + "/DettaglioAppelloStudente?&selectedAppelloId=" + selectedAppelloId;
		// response.sendRedirect(path);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
