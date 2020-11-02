package com.askisi.web;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import org.sqlite.JDBC;
import org.sqlite.SQLiteException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/addnew")
public class Addnew extends HttpServlet {
       

    public Addnew() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String barcodeParam = request.getParameter("barcode");
		String nameParam = request.getParameter("name");
		String colorParam = request.getParameter("color");
		String descriptionParam = request.getParameter("description");
		
		try {
			addTodb(barcodeParam,nameParam,colorParam,descriptionParam);
			RequestDispatcher view = request.getRequestDispatcher("result.jsp");
			view.forward(request, response);
		}catch (SQLException e) {
			if(e.getMessage().contains("UNIQUE constraint failed")) {
				showExistsError(response,barcodeParam);
			}else {
				e.printStackTrace();
			}
		}

		
	}
	
	
	private void showExistsError(HttpServletResponse response,String barcode) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("Error<br>Product with barcode "+barcode+" already exists<br><a href=\"/askisi2\">back</a>");
	}
	
	private synchronized void addTodb(String barcode,String name,String color,String description) throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		String dburl="jdbc:sqlite:products.db";

		File file = new File ("products.db");
		if(!file.exists()) {
			createNewDatabase(dburl);
		}
		Connection conn = DriverManager.getConnection(dburl);
		String sql = "INSERT INTO products(barcode,name,color,desc) VALUES(?,?,?,?)";  
		PreparedStatement pstmt = conn.prepareStatement(sql);  
		pstmt.setString(1, barcode);  
		pstmt.setString(2, name);
		pstmt.setString(3, color);  
		pstmt.setString(4, description);
		pstmt.executeUpdate();
		conn.close();
	}
	
	
	
	private static void createNewDatabase(String dburl) {  
        try {  
            Connection conn = DriverManager.getConnection(dburl);  
            if (conn != null) {  
             // SQL statement for creating a new table  
                String sql = "CREATE TABLE \"products\" ("  
                        + " \"barcode\" text PRIMARY KEY,"  
                        + " \"name\" text NOT NULL,"  
                        + " \"color\" text NOT NULL,"    
                        + " \"desc\" text NOT NULL"
                        + ");";  
                Statement stmt = conn.createStatement();  
                stmt.execute(sql);
                conn.close();
            }  
   
        } catch (SQLException e) {  
        	e.printStackTrace(); 
        }  
    }  
	

}
