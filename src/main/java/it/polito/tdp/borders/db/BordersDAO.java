package it.polito.tdp.borders.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.borders.model.Border;
import it.polito.tdp.borders.model.Country;

public class BordersDAO {

	public void loadAllCountries(Map<Integer,Country> idMap, int anno) {

		String sql = "SELECT ccode, StateAbb, StateNme FROM country ORDER BY StateAbb";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("ccode"))) {
					Country c = new Country(rs.getString("StateAbb"),rs.getInt("ccode"), rs.getString("StateNme")); 
					idMap.put(c.getCode(), c);
				}
			}
			
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Border> getCountryPairs(Map<Integer,Country> idMap, int anno) {

		String sql = "SELECT state1no AS state1, state2no AS state2 "
				+ "		FROM contiguity "
				+ "		WHERE year<=? AND conttype=1 "
				+ "		GROUP BY state1no, state2no";
		
		List<Border> result = new ArrayList<Border>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Country c1 = idMap.get(rs.getInt("state1"));
				Country c2 = idMap.get(rs.getInt("state2"));
				
				if(c1!=null && c2!=null) {
					Border b = new Border(c1,c2);
					result.add(b);
				}
				else {
					System.out.println("Errore nella ricerca dei confini!");
				}
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Country> getVertices(Map<Integer,Country> idMap, int anno) {
		
		String sql = "SELECT c.CCode AS id "
				+ "FROM country c, contiguity co "
				+ "WHERE (c.CCode = co.state1no OR c.CCode = co.state2no) "
				+ "AND co.year <= ? AND co.conttype = 1 "
				+ "GROUP BY c.CCode";
		List<Country> result = new LinkedList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(idMap.get(rs.getInt("id")));
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}

}
