package controller;

import dao.NotificationDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/sendNotification"})
public class NotificationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message");
        int motelRoomId = Integer.parseInt(request.getParameter("motelRoomId"));

        NotificationDAO dao = new NotificationDAO();
        String status;
        try {
            dao.addNotification(message, motelRoomId);
            status = "Notification sent successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            status = "Failed to send notification: " + e.getMessage();
        }

        response.setContentType("text/plain");
        response.getWriter().write(status);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
