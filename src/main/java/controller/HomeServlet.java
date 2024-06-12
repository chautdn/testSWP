
package controller;

import dao.MotelRoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MotelRoom;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author PC
 */
@WebServlet(name="HomeServlet", urlPatterns={"/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        MotelRoomDAO motelRoomDAO = null;
        try {
            motelRoomDAO = new MotelRoomDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<MotelRoom> rooms = motelRoomDAO.getAllMotelRooms(1, 9);
        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    } 


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
