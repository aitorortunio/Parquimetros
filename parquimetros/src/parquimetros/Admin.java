package parquimetros;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javax.swing.JTable;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionListener;

import quick.dbtable.DBTable;

import javax.swing.event.ListSelectionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JPanel;


public class Admin extends JFrame{
	protected Connection cnx;
	protected JList<String> listaAtributos, listaTablas;
	protected Statement sentencia;
	protected JTextArea textArea;
	protected DBTable tabla;
	protected Login login;
	protected Admin admin;
	
	public Admin(Connection cnx, Login login) {
		this.cnx = cnx;
		this.login = login;
		admin = this;	
		
		getContentPane().setForeground(Color.LIGHT_GRAY);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		getContentPane().setLayout(null);
		
		
		listaTablas = new JList();
		listaTablas.setBackground(Color.WHITE);
		listaTablas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listarAtributos(listaTablas.getSelectedValue().toString());
			}
		});
		
		listaTablas.setBounds(650, 106, 151, 321);
		getContentPane().add(listaTablas);
		
		textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);
		textArea.setBackground(Color.WHITE);
		textArea.setBounds(10, 11, 301, 84);
		getContentPane().add(textArea);
		
		JButton btnNewButton = new JButton("Ejecutar");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setBounds(360, 35, 89, 23);
		getContentPane().add(btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					ejecutarSentencia(textArea.getText());
					refrescarTabla(textArea.getText());
					listarTablas();
				}
			});
		
		
		listaAtributos = new JList();
		listaAtributos.setBackground(Color.WHITE);
		listaAtributos.setBounds(811, 106, 152, 321);
		getContentPane().add(listaAtributos);
		
		JLabel lblNewLabel = new JLabel("Atributos");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(801, 70, 190, 23);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Tablas");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(628, 67, 196, 28);
		getContentPane().add(lblNewLabel_1);
		
		tabla = new DBTable();
		tabla.setEditable(false);
		tabla.setBounds(10, 106, 621, 321);
		getContentPane().add(tabla);
		
		JButton botonAtras = new JButton("Volver");
		botonAtras.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				login.txtContraseniaAdmin.setText("");
				login.txtUsuarioAdmin.setText("");
				login.setVisible(true);
				admin.setVisible(false);
				
			}
		});
		botonAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login.InicioSesionAdministrador.setVisible(true);
			}
		});
		botonAtras.setBounds(43, 483, 107, 23);
		getContentPane().add(botonAtras);
		
		listarTablas();
	}
	
	public void ejecutarSentencia(String sent) {//Metodo que ejecuta la sentencia ingresada.
		try {
			cnx.createStatement().execute(sent);
			JOptionPane.showMessageDialog(null,"La consulta o actualizacion fue ejecutada correctamente" , "Aviso", JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"La consulta o actualizacion es incorrecta" , "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	public void refrescarTabla(String sent) { // Actualiza la tabla mostrando el resultado de la sentencia ejecutada. 	  
		try
	      {    
			  Statement statement = cnx.createStatement();
			  ResultSet res = statement.executeQuery(sent); 
			  tabla.refresh(res);
	    	  
	    	  for (int i = 0; i < tabla.getColumnCount(); i++){ 		   		  
	    		 if	 (tabla.getColumn(i).getType()==Types.TIME){    		 
	    		    tabla.getColumn(i).setType(Types.CHAR);
	  	       	 }
	    		 if	 (tabla.getColumn(i).getType()==Types.DATE){
	    		    tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
	    		 }
	          }  
	   	     	  
	    	  statement.close();
	    	  res.close();
	       }
	      catch (SQLException ex){
	    	 // ex.printStackTrace();
	      }
	}
   	
	
	public void listarTablas() {//Muestra las tablas que se encuentran en la base de datos.
		DefaultListModel<String> modeloLT = new DefaultListModel<String>();

        try {
            Statement stmt = this.cnx.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            while (rs.next()) {
                modeloLT.addElement(rs.getString(1));
            }

            listaTablas.setModel(modeloLT);
            getContentPane().add(listaTablas);

            stmt.close();
            rs.close();

        } catch (SQLException e) {
            //e.printStackTrace();
        }
	
	}
	
	public void listarAtributos(String nombreTabla) {//Muestra los atributos que se encuentran en la tabla pasada por parametro.
		DefaultListModel<String> listaAtributosM = new DefaultListModel<String>();
		
		try {
			sentencia = cnx.createStatement();
			ResultSet res = sentencia.executeQuery("describe " + nombreTabla);
			boolean resp = res.next();
			
			while(resp) {
				listaAtributosM.addElement(res.getString("FIELD"));
			    resp = res.next(); 
			}
			
			listaAtributos.setModel(listaAtributosM);			
			getContentPane().add(listaAtributos);
			sentencia.close();
			res.close();
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		
	}
}


	


