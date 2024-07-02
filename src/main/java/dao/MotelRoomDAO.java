package dao;

import model.MotelRoom;
import context.DBcontext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Account.Account;
public class MotelRoomDAO {
    private Connection connection;
    public MotelRoomDAO() throws SQLException {
        connection = DBcontext.getConnection();
    }

    public List<MotelRoom> getFavoriteRooms(int accountId) {
        List<MotelRoom> rooms = new ArrayList<>();
        String query = "SELECT mr.*, m.detail_address, m.ward, m.district, m.province " +
                "FROM motel_room mr " +
                "JOIN motels m ON mr.motel_id = m.motel_id " +
                "ORDER BY mr.create_date DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boolean check = isFavoriteRoom(accountId, rs.getInt("motel_room_id"));
                if (check){
                    MotelRoom room = new MotelRoom();
                    room.setMotelRoomId(rs.getInt("motel_room_id"));
                    room.setDescription(rs.getString("descriptions"));
                    room.setLength(rs.getDouble("length"));
                    room.setWidth(rs.getDouble("width"));
                    room.setRoomPrice(rs.getDouble("room_price"));
                    room.setElectricityPrice(rs.getDouble("electricity_price"));
                    room.setWaterPrice(rs.getDouble("water_price"));
                    room.setWifiPrice(rs.getDouble("wifi_price"));
                    room.setImage(getImagesForRoom(rs.getInt("motel_room_id")));
                    room.setDetailAddress(rs.getString("detail_address"));
                    room.setWard(rs.getString("ward"));
                    room.setDistrict(rs.getString("district"));
                    room.setProvince(rs.getString("province"));
                    room.setFavorite(check);  // Correct use
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean addFavoriteRoom(int accountId, int roomId) {
        String query = "INSERT INTO favourite_room (account_id, motel_room_id, create_date) VALUES (?, ?, GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFavoriteRoom(int accountId, int roomId) {
        String query = "SELECT COUNT(*) FROM favourite_room WHERE account_id = ? AND motel_room_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Returns true if the count is more than 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeFavoriteRoom(int accountId, int roomId) {
        String query = "DELETE FROM favourite_room WHERE account_id = ? AND motel_room_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MotelRoom> getAllMotelRooms(int page, int pageSize, Account acc) {  // Add accountId here
        List<MotelRoom> rooms = new ArrayList<>();
        String query = "SELECT mr.*, m.detail_address, m.ward, m.district, m.province, cr.descriptions as category " +
                "FROM motel_room mr " +
                "JOIN motels m ON mr.motel_id = m.motel_id " +
                "JOIN category_room cr ON mr.category_room_id = cr.category_room_id " +
                "ORDER BY mr.create_date DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MotelRoom room = new MotelRoom();
                room.setMotelRoomId(rs.getInt("motel_room_id"));
                room.setDescription(rs.getString("descriptions"));
                room.setLength(rs.getDouble("length"));
                room.setWidth(rs.getDouble("width"));
                room.setRoomPrice(rs.getDouble("room_price"));
                room.setElectricityPrice(rs.getDouble("electricity_price"));
                room.setWaterPrice(rs.getDouble("water_price"));
                room.setWifiPrice(rs.getDouble("wifi_price"));
                room.setImage(getImagesForRoom(rs.getInt("motel_room_id")));
                room.setDetailAddress(rs.getString("detail_address"));
                room.setWard(rs.getString("ward"));
                room.setDistrict(rs.getString("district"));
                room.setProvince(rs.getString("province"));
                if(acc!=null)
                room.setFavorite(isFavoriteRoom(acc.getAccountId(), rs.getInt("motel_room_id")));  // Correct use
                room.setCategory(rs.getString("category"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    //Get motel rooms by motel id
    public static List<MotelRoom> getMotelRoomsByMotelId(int motelId) {
        List<MotelRoom> rooms = new ArrayList<>();
        String query = "SELECT mr.*, cr.descriptions as category, cr.category_room_id FROM motel_room mr JOIN category_room cr ON mr.category_room_id = cr.category_room_id WHERE motel_id = ?";
        try {
            PreparedStatement ps =DBcontext.getConnection().prepareStatement(query);
            ps.setInt(1, motelId);
            ResultSet rs = ps.executeQuery();
            MotelRoomDAO motelRoomDAO = new MotelRoomDAO();
            while (rs.next()) {
                MotelRoom room = new MotelRoom();
                room.setMotelRoomId(rs.getInt("motel_room_id"));
                room.setDescription(rs.getString("descriptions"));
                room.setName(rs.getString("name"));
                room.setLength(rs.getDouble("length"));
                room.setWidth(rs.getDouble("width"));
                room.setRoomPrice(rs.getDouble("room_price"));
                room.setElectricityPrice(rs.getDouble("electricity_price"));
                room.setWaterPrice(rs.getDouble("water_price"));
                room.setWifiPrice(rs.getDouble("wifi_price"));
                room.setRoomStatus(rs.getBoolean("room_status"));
                room.setCategory(rs.getString("category"));
                room.setCategoryRoomId(rs.getInt("category_room_id"));
                room.setMotelId(rs.getInt("motel_id"));
                room.setAccountId(rs.getInt("account_id"));
                room.setImage(motelRoomDAO.getImagesForRoom(rs.getInt("motel_room_id")));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public int getTotalMotelRooms() {
        String query = "SELECT COUNT(*) FROM motel_room";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static MotelRoom getMotelRoomById(int id) {
        MotelRoom room = null;
        String query = "SELECT mr.*, cr.descriptions as category, cr.category_room_id, a.fullname, a.phone, m.detail_address, m.ward, m.district, m.province " +
                "FROM motel_room mr " +
                "JOIN category_room cr ON mr.category_room_id = cr.category_room_id " +
                "JOIN accounts a ON mr.account_id = a.account_id " +
                "JOIN motels m ON mr.motel_id = m.motel_id " +
                "WHERE mr.motel_room_id = ?";
        try (PreparedStatement ps = DBcontext.getConnection().prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            MotelRoomDAO motelRoomDAO = new MotelRoomDAO();
            if (rs.next()) {
                room = new MotelRoom();
                room.setMotelRoomId(rs.getInt("motel_room_id"));
                room.setName(rs.getString("name"));
                room.setDescription(rs.getString("descriptions"));
                room.setLength(rs.getDouble("length"));
                room.setWidth(rs.getDouble("width"));
                room.setRoomPrice(rs.getDouble("room_price"));
                room.setElectricityPrice(rs.getDouble("electricity_price"));
                room.setWaterPrice(rs.getDouble("water_price"));
                room.setWifiPrice(rs.getDouble("wifi_price"));
                room.setAccountFullname(rs.getString("fullname"));
                room.setAccountPhone(rs.getString("phone"));
                room.setDetailAddress(rs.getString("detail_address"));
                room.setWard(rs.getString("ward"));
                room.setDistrict(rs.getString("district"));
                room.setProvince(rs.getString("province"));
                room.setCategory(rs.getString("category"));
                room.setRoomStatus(rs.getBoolean("room_status"));
                room.setCategoryRoomId(rs.getInt("category_room_id"));
                room.setImage(motelRoomDAO.getImagesForRoom(rs.getInt("motel_room_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    public List<String> getImagesForRoom(int motelRoomId) {
        List<String> images = new ArrayList<>();
        String query = "SELECT name FROM image WHERE motel_room_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, motelRoomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }

    public void addMotelRoom(MotelRoom room) throws SQLException {
        String sql = "INSERT INTO motel_room (create_date, descriptions, length, width, room_price, electricity_price, water_price, wifi_price, room_status, category_room_id, motel_id, account_id, name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false); // Bắt đầu transaction
            stmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(2, room.getDescription());
            stmt.setDouble(3, room.getLength());
            stmt.setDouble(4, room.getWidth());
            stmt.setDouble(5, room.getRoomPrice());
            stmt.setDouble(6, room.getElectricityPrice());
            stmt.setDouble(7, room.getWaterPrice());
            stmt.setDouble(8, room.getWifiPrice());
            stmt.setBoolean(9, room.isRoomStatus());
            stmt.setInt(10, room.getCategoryRoomId());
            stmt.setInt(11, room.getMotelId());
            stmt.setInt(12, room.getAccountId());
            stmt.setString(13, room.getName());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating room failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int roomId = generatedKeys.getInt(1);
                    // Thêm thông tin ảnh
                    String imageSql = "INSERT INTO image (name, motel_room_id) VALUES (?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(imageSql)) {
                        for (String imageName : room.getImage()) {
                            pstmt.setString(1, imageName);
                            pstmt.setInt(2, roomId);
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                } else {
                    throw new SQLException("Creating room failed, no ID obtained.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error adding motel room: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void updateMotelRoom(MotelRoom room) throws SQLException {
        int motelRoomId = room.getMotelRoomId();
        if (isMotelRoomExists(motelRoomId)) {

            String sql = "UPDATE motel_room SET name = ?, descriptions = ?, length = ?, width = ?, room_price = ?, electricity_price = ?, water_price = ?, wifi_price = ?, room_status = ?, category_room_id = ? WHERE motel_room_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                stmt.setString(1, room.getName());
                stmt.setString(2, room.getDescription());
                stmt.setDouble(3, room.getLength());
                stmt.setDouble(4, room.getWidth());
                stmt.setDouble(5, room.getRoomPrice());
                stmt.setDouble(6, room.getElectricityPrice());
                stmt.setDouble(7, room.getWaterPrice());
                stmt.setDouble(8, room.getWifiPrice());
                stmt.setBoolean(9, room.isRoomStatus());
                stmt.setInt(10, room.getCategoryRoomId());
                stmt.setInt(11, motelRoomId);
                stmt.executeUpdate();
                // Chỉ cập nhật ảnh nếu có ảnh mới được chọn
                if (room.getImage() != null && !room.getImage().isEmpty()) {
                    // Xóa các ảnh cũ
                    String deleteImagesSql = "DELETE FROM image WHERE motel_room_id = ?";
                    PreparedStatement pstmt = connection.prepareStatement(deleteImagesSql);
                    pstmt.setInt(1, room.getMotelRoomId());
                    pstmt.executeUpdate();

                    // Thêm các ảnh mới
                    String insertImagesSql = "INSERT INTO image (name, motel_room_id) VALUES (?, ?)";
                    pstmt = connection.prepareStatement(insertImagesSql);
                    for (String imageName : room.getImage()) {
                        pstmt.setString(1, imageName);
                        pstmt.setInt(2, room.getMotelRoomId());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    pstmt.close();
                }

                connection.commit();
            }catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Error updating motel room: " + e.getMessage());
            }
        } else {
            System.out.println("Motel room with id " + motelRoomId + " does not exist.");
        }
    }

    public void deleteMotelRoom(int motelRoomId) throws SQLException {
        if (isMotelRoomExists(motelRoomId)) {
            connection.setAutoCommit(false);
            try {
                // First, delete associated favourite room entries
                String deleteFavouritesSql = "DELETE FROM favourite_room WHERE motel_room_id = ?";
                try (PreparedStatement favouritesStmt = connection.prepareStatement(deleteFavouritesSql)) {
                    favouritesStmt.setInt(1, motelRoomId);
                    favouritesStmt.executeUpdate();
                }

                // Second, delete associated images
                String deleteImagesSql = "DELETE FROM image WHERE motel_room_id = ?";
                try (PreparedStatement imageStmt = connection.prepareStatement(deleteImagesSql)) {
                    imageStmt.setInt(1, motelRoomId);
                    imageStmt.executeUpdate();
                }

                // Finally, delete the motel room
                String deleteRoomSql = "DELETE FROM motel_room WHERE motel_room_id = ?";
                try (PreparedStatement roomStmt = connection.prepareStatement(deleteRoomSql)) {
                    roomStmt.setInt(1, motelRoomId);
                    roomStmt.executeUpdate();
                }

                connection.commit();
                System.out.println("Motel room, associated images, and favourite entries deleted successfully.");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Error deleting motel room, images, and favourite entries: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } else {
            System.out.println("Motel room with id " + motelRoomId + " does not exist.");
        }
    }

    private boolean isMotelRoomExists(int motelRoomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM motel_room WHERE motel_room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, motelRoomId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }


    public List<MotelRoom> searchMotelRooms(String description, Double minPrice, Double maxPrice, Boolean status) {
        List<MotelRoom> rooms = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT mr.*, m.detail_address, m.ward, m.district, m.province " +
                "FROM motel_room mr " +
                "JOIN motels m ON mr.motel_id = m.motel_id " +
                "WHERE 1=1");

        if (description != null && !description.isEmpty()) {
            query.append(" AND mr.descriptions LIKE ?");
        }
        if (minPrice != null) {
            query.append(" AND mr.room_price >= ?");
        }
        if (maxPrice != null) {
            query.append(" AND mr.room_price <= ?");
        }
        if (status != null) {
            query.append(" AND mr.room_status = ?");
        }

        try {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            int paramIndex = 1;

            if (description != null && !description.isEmpty()) {
                ps.setString(paramIndex++, "%" + description + "%");
            }
            if (minPrice != null) {
                ps.setDouble(paramIndex++, minPrice);
            }
            if (maxPrice != null) {
                ps.setDouble(paramIndex++, maxPrice);
            }
            if (status != null) {
                ps.setBoolean(paramIndex++, status);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MotelRoom room = new MotelRoom();
                room.setMotelRoomId(rs.getInt("motel_room_id"));
                room.setDescription(rs.getString("descriptions"));
                room.setLength(rs.getDouble("length"));
                room.setWidth(rs.getDouble("width"));
                room.setRoomPrice(rs.getDouble("room_price"));
                room.setElectricityPrice(rs.getDouble("electricity_price"));
                room.setWaterPrice(rs.getDouble("water_price"));
                room.setWifiPrice(rs.getDouble("wifi_price"));
                room.setImage(getImagesForRoom(rs.getInt("motel_room_id")));
                room.setDetailAddress(rs.getString("detail_address"));
                room.setWard(rs.getString("ward"));
                room.setDistrict(rs.getString("district"));
                room.setProvince(rs.getString("province"));
                room.setRoomStatus(rs.getBoolean("room_status"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
}
