package model;

import java.sql.*;

import javax.swing.DefaultListModel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Database is a class that specifies the interface to the movie database. Uses
 * JDBC and the MySQL Connector/J driver.
 */
public class Database {
	/**
	 * The database connection.
	 */
	private Connection conn;
	private int nbrOfProduced;

	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
		nbrOfProduced = 0;
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

	public Pallet palletSearch(long palletId) {
		String sql = "Select * from pallets where Id = ? ";
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setLong(1, palletId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Pallet returnPallet = null;
		try {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				returnPallet = getPallet(rs);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return returnPallet;
	}

	//help method for dealing with resultset
	private Pallet getPallet(ResultSet rs) throws SQLException {
		Pallet returnPallet = null;
		Date date = rs.getDate("creationDateAndTime");
		String recipeName = rs.getString("recipeName");
		long shipmentId = rs.getLong("shipmentId");
		boolean isBlocked = rs.getBoolean("isBLocked");
		returnPallet = new Pallet(rs.getLong("Id"), date, isBlocked,
				rs.getLong("orderId"), recipeName);
		return returnPallet;
	}

	//searches for pallets in creation interval
	public ArrayList<Pallet> palletSearch(Date fromDate, Date toDate) {
		ArrayList<Pallet> pallets = new ArrayList<Pallet>();

		String sql = "Select * from pallets where creationDateAndTime BETWEEN ? and ? ";
		PreparedStatement ps = null;
		
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setDate(1, fromDate);
			ps.setDate(2, toDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				pallets.add(getPallet(rs));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return pallets;
	}
	
	//searches for pallets based on recipe and creation interval
	public ArrayList<Pallet> palletSearch(String recipeName, Date fromDate, Date toDate) {
		ArrayList<Pallet> pallets = new ArrayList<Pallet>();

		String sql = "Select * from pallets where recipeName LIKE ? and creationDateAndTime BETWEEN ? and ? ";
		PreparedStatement ps = null;
		
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, recipeName);
			ps.setDate(2, fromDate);
			ps.setDate(3, toDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					pallets.add(getPallet(rs));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		return pallets;
	}
	
	//searches for pallets based on recipe
	public ArrayList<Pallet> palletSearch(String recipeName) {
		ArrayList<Pallet> pallets = new ArrayList<Pallet>();

		String sql = "Select * from pallets where recipeName LIKE ?";
		PreparedStatement ps = null;
		
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, recipeName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					pallets.add(getPallet(rs));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		return pallets;
	}
	
	//returns an int representing the number of blocked pallets in an interval
	public int blockPallets(String recipeName, Date fromDate, Date toDate) {
		String sql = 
				"update pallets "+
				"SET isBlocked = true "+
				"where recipeName LIKE ? and creationDateAndTime BETWEEN ? and ? ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, recipeName);
			ps.setDate(2, fromDate);
			ps.setDate(3, toDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int nbrOfblockedPallets = 0;
		try {
			nbrOfblockedPallets = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return nbrOfblockedPallets;
	}
	
	//returns an int representing the number of unblocked pallets in an interval
		public int unblockPallets(String recipeName, Date fromDate, Date toDate) {
			String sql = 
					"update pallets "+
					"SET isBlocked = false "+
					"where recipeName LIKE ? and creationDateAndTime BETWEEN ? and ? ";

			PreparedStatement ps = null;
			try {
				ps = (PreparedStatement) conn.prepareStatement(sql);
				ps.setString(1, recipeName);
				ps.setDate(2, fromDate);
				ps.setDate(3, toDate);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			int nbrOfunblockedPallets = 0;
			try {
				nbrOfunblockedPallets = ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return nbrOfunblockedPallets;
		}
	
	public Customer getCustomerForPallet(Pallet pallet) {
		String sql = 
				"Select cName, address "+
				"from orders,customers "+
				"where orders.customerName = cName and orders.Id = ? ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setLong(1, pallet.orderId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Customer customer = null;
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					customer = new Customer(rs.getString("cName"),rs.getString("address"));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		return customer;
	}
	
	public int getIngredientQuantity(String ingredient) {
		String sql = "select quantity from rawMaterials where rmName = ?";
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, ingredient);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int nbr = 0;
		try {
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
				nbr = rs.getInt("quantity");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		return nbr;
	}
	
	public Recipe getRecipeForPallet(Pallet pallet) {
		String sql = 
				"Select quantity, rawMaterialName, recipeName "+
				"from ingredients "+
				"where ingredients.recipeName like ? ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, pallet.recipeName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Recipe recipe = null;
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					if (recipe == null) {
						recipe = new Recipe(rs.getString("recipeName"), new ArrayList<Ingredient>());
					}
					recipe.ingredients.add(new Ingredient(rs.getInt("quantity"),new RawMaterial(rs.getString("rawMaterialName"))));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		return recipe;
	}
	
	public int getPalletQuantity(String recipeName, Date fromDate, Date toDate) {
		String sql = 
				"Select count(*) "+
				"from pallets "+
				"where recipeName like ? and creationDateAndTime between ? and ? ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, recipeName);
			ps.setDate(2, fromDate);
			ps.setDate(3, toDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int quantity = 0;
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					quantity = rs.getInt(1);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		return quantity;
	}
	
	public Pallet getNextPalletToProduce() {
		String sql = 
		"select pallets.*"+
		"from pallets, orders "+
		"where pallets.orderId = orders.Id and pallets.creationDateAndTime is NULL "+
		"ORDER BY requestedDeliveryDate ASC "+
		"LIMIT 1 ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Pallet pallet = null;
		try {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				pallet = getPallet(rs);
				
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if(ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return pallet;
	}
	//fick aldrig att funka
	public boolean isEnoughRawMaterials(Pallet pallet) {
		return true;
	}
	
	public void orderPallet(String company, Date date) {
		nbrOfProduced++;
		String sql = 
				"INSERT INTO Orders VALUES (?, ?, ?)";
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setInt(1, nbrOfProduced);
			ps.setDate(2, date);
			ps.setString(3, company);
			System.out.println("ordered pallet with id: " + nbrOfProduced);
		} catch (SQLException e) {
			e.printStackTrace();
			}
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertPallet(String kookieType) {
		String sql = 
				 "Insert INTO Pallets VALUES (?, ? , NULL, ?, NULL, FALSE)";
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setInt(1, nbrOfProduced);
			ps.setInt(2, nbrOfProduced);
			ps.setString(3, kookieType);
			System.out.println("produced pallet with id: " + nbrOfProduced);
		} catch (SQLException e) {
			e.printStackTrace();
			}
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setNbrOfProduced() {
		String sql = "select count(*) from pallets;";
		
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int nbr = 0;
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					nbr = rs.getInt(1);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		nbrOfProduced = nbr;
	}
	
	public int getNbrOfProduced() {
		return nbrOfProduced;
	}
	
	//updates ingredients quantity when producing pallet
	public void produceWithIngredients(String recipeName) {
		ArrayList<Ingredient> list = new ArrayList<Ingredient>();
		String sql = 
				"SELECT quantity, rawMaterialName FROM ingredients WHERE recipeName = ?";
	
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, recipeName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			
			int quantity = 0;
			String rawMaterialName = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next ()) {
				quantity = rs.getInt("quantity");
				rawMaterialName = rs.getString("rawMaterialName");
				RawMaterial rawMaterial = new RawMaterial(rawMaterialName);
				Ingredient ingredient = new Ingredient(quantity, rawMaterial);
				list.add(ingredient);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i<list.size(); i++) {
		String updateSql = "UPDATE RawMaterials SET quantity = quantity - ?  WHERE rmName = ?";
		PreparedStatement ps2 = null;
		try {
			ps2 = (PreparedStatement) conn.prepareStatement(updateSql);
			ps2.setInt(1, list.get(i).quantity);
			ps2.setString(2, list.get(i).rMaterial.rmName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			int rs = ps2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}
	
	public void createOrderedPallet(Pallet p) {
		
		//if (isEnoughRawMaterials(p)) {
			
			produceWithIngredients(p.recipeName);
			
		String sql = 
				"update pallets "+
				"SET creationDateAndTime = ? "+
				"where id = ? ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setDate(1, new Date(Calendar.getInstance().getTimeInMillis()));
			ps.setLong(2, p.Id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			int rs = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	public ArrayList<Pallet> getPalletsWithBlockStatus(boolean status) {
		ArrayList<Pallet> pallets = new ArrayList<Pallet>();

		String sql = 
				"Select * from pallets where isBLocked = ? ";

		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setBoolean(1, status);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					pallets.add(getPallet(rs));
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		return pallets;
	}
	
	public void updateTestPallets(int i) {
		
		String sql = 
				"update pallets set creationDateAndTime = NULL WHERE Id = ?";
		
		PreparedStatement ps = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setInt(1, i);
		} catch (SQLException e) {
			e.printStackTrace();
			}
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
