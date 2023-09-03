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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tiw.beans.CorsoBean;
import tiw.beans.User;
import tiw.dao.DocenteDAO;
import tiw.dao.StudenteDAO;
import tiw.utils.ConnectionHandler;


@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GoToHome() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			String loginpath = request.getServletContext().getContextPath();
			response.setStatus(403);
			response.setHeader("Location", loginpath);
			return;
		}

		
		User u = (User) s.getAttribute("user");
		List<CorsoBean> corsi = null;

		try {
			
			if (u.getRole().equals("docente")) {
				DocenteDAO aDao = new DocenteDAO(connection, u.getId());
				corsi = aDao.findCorsi(u);
			} else if (u.getRole().equals("studente")) {
				StudenteDAO aDao = new StudenteDAO(connection, u.getId());
				corsi = aDao.findCorsi();
			}
			
			if (corsi == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("Resource not found");
				return;
			}
			
		} catch (SQLException e) {
			// throw new ServletException(e);
			String errorMessage = e.getMessage(); 
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(errorMessage);
			return;
		}

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
		String json = gson.toJson(corsi);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(json);
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
