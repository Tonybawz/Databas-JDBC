package dbtLab3;

import java.sql.*;

import javax.swing.DefaultListModel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * Database is a class that specifies the interface to the movie database. Uses
 * JDBC and the MySQL Connector/J driver.
 */
public class Database {
	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
	}

	/**
	 * Open a connection to the database, using the specified user name and
	 * password.
	 * 
	 * @param userName
	 *            The user name.
	 * @param password
	 *            The user's password.
	 * @return true if the connection succeeded, false if the supplied user name
	 *         and password were not recognized. Returns false also if the JDBC
	 *         driver isn't found.
	 */
	public boolean openConnection(String userName, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(
					"jdbc:mysql://puccini.cs.lth.se/" + userName, userName,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Close the connection to the database.
	 */
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
		conn = null;
	}

	public void excecuteSQLquery(String sql,
			DefaultListModel<String> listModel, String column) {
		/* --- insert own code here --- */
		listModel.removeAllElements();
		Connection conn = (Connection) this.getConnection();
		try {
			PreparedStatement ps = (PreparedStatement) conn
					.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String temp = null;
			while (rs.next()) {
				temp = rs.getString(column);
				listModel.addElement(temp);
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String excecuteSqlquery(String movieName, String column){
/* --- insert own code here --- */
		
		String sql = "select theatername from performance where moviename = '"
				+ movieName + "';";
		Connection conn = (Connection) this.getConnection();
		try {
			PreparedStatement ps = (PreparedStatement) conn
					.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String tName = null;
			while (rs.next()) {
				tName = rs.getString(column);
			}
			ps.close();
			return tName;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public String excecuteSqlseatsQuery(String mName, String tName){
		/* --- insert own code here --- */
		String sql = "select count(moviename) from reservation where moviename = '"
				+ mName + "' group by showdate;";
		String sql2 = "select seats from theater where theatername = '" + tName
				+ "';";
		Connection conn = (Connection) this.getConnection();

		try {
			PreparedStatement ps = (PreparedStatement) conn
					.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String nbrSeats = null;
			nbrSeats = "0";

			while (rs.next()) {
				nbrSeats = rs.getString("count(moviename)");
			}

			int booked = Integer.parseInt(nbrSeats);
			PreparedStatement ps2 = (PreparedStatement) conn
					.prepareStatement(sql2);
			ResultSet rs2 = ps2.executeQuery();
			String nbrSeats2 = null;
			while (rs2.next()) {
				nbrSeats2 = rs2.getString("seats");
			}
			ps2.close();
			int max = Integer.parseInt(nbrSeats2);
			return String.valueOf(max - booked);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void excecuteSqlbooking(String uName, String showdate, String movieName, String tName){
		String sql = "insert into reservation(username, showdate, moviename) VALUES('"
				+ uName + "', '" + showdate + "', '" + movieName + "');";
		try {
			Connection conn = (Connection) this.getConnection();
			PreparedStatement ps = (PreparedStatement) conn
					.prepareStatement(sql);
			if (Integer.parseInt(excecuteSqlseatsQuery(movieName, tName)) > 0){
				ps.executeUpdate();
				ps.close();
			} else {
				System.err.println("No available seats left.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if the connection to the database has been established
	 * 
	 * @return true if the connection has been established
	 */
	public boolean isConnected() {
		return conn != null;
	}

	/* --- insert own code here --- */
	public Connection getConnection() {
		if (isConnected()) {
			return conn;
		} else {
			return null;
		}
	}
}
