package dao;

import model.CategoryRoom;
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

    public List<CategoryRoom> getAllCategoryRooms() {
        List<CategoryRoom> categoryRooms = new ArrayList<>();
        String query = "SELECT category_room_id, descriptions FROM category_room WHERE status = 1";

        try (Connection conn = DBcontext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CategoryRoom categoryRoom = new CategoryRoom();
                categoryRoom.setCategoryRoomId(rs.getInt("category_room_id"));
                categoryRoom.setDescriptions(rs.getString("descriptions"));
                categoryRooms.add(categoryRoom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryRooms;
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

    public List<MotelRoom> getAllMotelRooms(int page, int pageSize, Account acc) {
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
                if (acc != null)
                    room.setFavorite(isFavoriteRoom(acc.getAccountId(), rs.getInt("motel_room_id")));  // Correct use
                room.setCategory(rs.getString("category"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static List<MotelRoom> getMotelRoomsByMotelId(int motelId) {
        List<MotelRoom> rooms = new ArrayList<>();
        String query = "SELECT mr.*, cr.descriptions as category, cr.category_room_id FROM motel_room mr JOIN category_room cr ON mr.category_room_id = cr.category_room_id WHERE motel_id = ?";
        try {
            PreparedStatement ps = DBcontext.getConnection().prepareStatement(query);
            ps.setInt(1, motelId);
            ResultSet rs = ps.executeQuery();
            MotelRoomDAO motelRoomDAO = new MotelRoomDAO();
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
        String sql = "INSERT INTO motel_room (create_date, descriptions, length, width, room_price, electricity_price, water_price, wifi_price, room_status, category_room_id, motel_id, account_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            stmt.executeUpdate();
        }
    }

    public void updateMotelRoom(MotelRoom room) throws SQLException {
        int motelRoomId = room.getMotelRoomId();
        if (isMotelRoomExists(motelRoomId)) {
            String sql = "UPDATE motel_room SET name = ?, descriptions = ?, length = ?, width = ?, room_price = ?, electricity_price = ?, water_price = ?, wifi_price = ?, room_status = ?, category_room_id = ?, motel_id = ?, account_id = ? WHERE motel_room_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
                stmt.setInt(11, room.getMotelId());
                stmt.setInt(12, room.getAccountId());
                stmt.setInt(13, motelRoomId);
                stmt.executeUpdate();
            }
        } else {
            System.out.println("Motel room with id " + motelRoomId + " does not exist.");
        }
    }

    public void deleteMotelRoom(int motelRoomId) throws SQLException {
        if (isMotelRoomExists(motelRoomId)) {
            String sql = "DELETE FROM motel_room WHERE motel_room_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, motelRoomId);
                stmt.executeUpdate();
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

    public List<MotelRoom> searchRooms(String search, String province, String district, String town, String category, String minPrice, String maxPrice, String minArea, String maxArea, String sortPrice, String sortArea, String sortDate, int page, int pageSize, Account acc) {
        List<MotelRoom> rooms = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT mr.*, m.detail_address, m.ward, m.district, m.province, cr.descriptions as category FROM motel_room mr JOIN motels m ON mr.motel_id = m.motel_id JOIN category_room cr ON mr.category_room_id = cr.category_room_id WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            query.append(" AND (mr.descriptions LIKE ? OR m.detail_address LIKE ? OR m.ward LIKE ? OR m.district LIKE ? OR m.province LIKE ?)");
            String searchPattern = "%" + search.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (province != null && !province.equals("-1")) {
            query.append(" AND m.province_id = ?");
            params.add(Integer.parseInt(province));
        }

        if (district != null && !district.equals("-1")) {
            query.append(" AND m.district_id = ?");
            params.add(Integer.parseInt(district));
        }

        if (town != null && !town.equals("-1")) {
            query.append(" AND m.ward_id = ?");
            params.add(Integer.parseInt(town));
        }

        if (category != null && !category.equals("-1")) {
            query.append(" AND mr.category_room_id = ?");
            params.add(Integer.parseInt(category));
        }

        if (minPrice != null && !minPrice.isEmpty()) {
            query.append(" AND mr.room_price >= ?");
            params.add(Double.parseDouble(minPrice));
        }

        if (maxPrice != null && !maxPrice.isEmpty()) {
            query.append(" AND mr.room_price <= ?");
            params.add(Double.parseDouble(maxPrice));
        }

        if (minArea != null && !minArea.isEmpty()) {
            query.append(" AND (mr.length * mr.width) >= ?");
            params.add(Double.parseDouble(minArea));
        }

        if (maxArea != null && !maxArea.isEmpty()) {
            query.append(" AND (mr.length * mr.width) <= ?");
            params.add(Double.parseDouble(maxArea));
        }

        if (sortPrice != null && !sortPrice.equals("-1")) {
            query.append(" ORDER BY mr.room_price ").append(sortPrice);
        } else if (sortArea != null && !sortArea.equals("-1")) {
            query.append(" ORDER BY (mr.length * mr.width) ").append(sortArea);
        } else if (sortDate != null && sortDate.equals("newest")) {
            query.append(" ORDER BY mr.create_date DESC");
        } else if (sortDate != null && sortDate.equals("oldest")) {
            query.append(" ORDER BY mr.create_date ASC");
        } else {
            query.append(" ORDER BY mr.create_date DESC");
        }

        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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
                if (acc != null) {
                    room.setFavorite(isFavoriteRoom(acc.getAccountId(), rs.getInt("motel_room_id")));
                }
                room.setCategory(rs.getString("category"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public int getTotalSearchResults(String search, String province, String district, String town, String category, String minPrice, String maxPrice, String minArea, String maxArea) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM motel_room mr JOIN motels m ON mr.motel_id = m.motel_id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            query.append(" AND (mr.descriptions LIKE ? OR m.detail_address LIKE ? OR m.ward LIKE ? OR m.district LIKE ? OR m.province LIKE ?)");
            String searchPattern = "%" + search.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (province != null && !province.equals("-1")) {
            query.append(" AND m.province_id = ?");
            params.add(Integer.parseInt(province));
        }

        if (district != null && !district.equals("-1")) {
            query.append(" AND m.district_id = ?");
            params.add(Integer.parseInt(district));
        }

        if (town != null && !town.equals("-1")) {
            query.append(" AND m.ward_id = ?");
            params.add(town.toLowerCase());
        }

        if (category != null && !category.equals("-1")) {
            query.append(" AND mr.category_room_id = ?");
            params.add(Integer.parseInt(category));
        }

        if (minPrice != null && !minPrice.isEmpty()) {
            query.append(" AND mr.room_price >= ?");
            params.add(Double.parseDouble(minPrice));
        }

        if (maxPrice != null && !maxPrice.isEmpty()) {
            query.append(" AND mr.room_price <= ?");
            params.add(Double.parseDouble(maxPrice));
        }

        if (minArea != null && !minArea.isEmpty()) {
            query.append(" AND (mr.length * mr.width) >= ?");
            params.add(Double.parseDouble(minArea));
        }

        if (maxArea != null && !maxArea.isEmpty()) {
            query.append(" AND (mr.length * mr.width) <= ?");
            params.add(Double.parseDouble(maxArea));
        }

        try (PreparedStatement ps = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}