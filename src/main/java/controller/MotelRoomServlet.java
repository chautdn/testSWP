package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.MotelRoom;
import dao.MotelRoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/motel-rooms")
public class MotelRoomServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MotelRoomDAO motelRoomDAO;

    public void init() {
        try {
            motelRoomDAO = new MotelRoomDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            listRooms(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "create";
        }

        switch (action) {
            case "create":
                createMotelRoom(request, response);
                break;
            case "update":
                // updateMotelRoom(request, response);
                break;
            case "delete":
                // deleteMotelRoom(request, response);
                break;
        }
    }

    private void listRooms(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        int pageSize = 9;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        List<MotelRoom> rooms = motelRoomDAO.getAllMotelRooms(page, pageSize);
        int totalRooms = motelRoomDAO.getTotalMotelRooms();
        int totalPages = (int) Math.ceil((double) totalRooms / pageSize);

        request.setAttribute("rooms", rooms);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/listRooms.jsp").forward(request, response);
    }

    private void createMotelRoom(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MotelRoom room = new MotelRoom();
        room.setDescription(request.getParameter("descriptions"));
        room.setLength(Double.parseDouble(request.getParameter("length")));
        room.setWidth(Double.parseDouble(request.getParameter("width")));
        room.setRoomStatus(Boolean.parseBoolean(request.getParameter("status")));
        room.setCategoryRoomId(Integer.parseInt(request.getParameter("categoryRoomId")));
        room.setMotelId(Integer.parseInt(request.getParameter("motelId")));

        try {
            motelRoomDAO.addMotelRoom(room);
            response.sendRedirect("motel-rooms");
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
}
