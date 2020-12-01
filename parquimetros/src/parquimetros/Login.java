package parquimetros;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mysql.cj.protocol.Resultset;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.security.MessageDigest;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame{
	protected Connection cnx;
	protected Admin Gui_admin;
	protected Inspector insp;
	protected Parquimetro parq;
	protected JPanel InicioSesionAdministrador;
	protected JPanel InicioSesionInspector;
	protected JTextPane txtNroRegistroInspector, txtUsuarioAdmin;
	protected JPasswordField txtContraseniaAdmin, contraseñaInspector;
	protected JPanel Logeo;
	protected static Login inst;

	public static void main(String[] args) 
	   {
	      SwingUtilities.invokeLater(new Runnable() {
	         public void run() {
	            inst = new Login();
	            inst.setVisible(true);
	            inst.setSize(500, 350);
	            inst.setResizable(false);
	            inst.setLocationRelativeTo(null);
	         }
	      });
	   }
	
	public static void infoBox(String infoMessage, String titleBar){
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }	
	
	public Login() {
		getContentPane().setBackground(Color.LIGHT_GRAY);
		
		getContentPane().setLayout(null);
		
		
		InicioSesionInspector = new JPanel();
		InicioSesionInspector.setBackground(Color.LIGHT_GRAY);
		InicioSesionInspector.setBounds(0, 0, 434, 250);
		getContentPane().add(InicioSesionInspector);
		InicioSesionInspector.setLayout(null);
		InicioSesionInspector.setVisible(false);
		
		JLabel lblNewLabel_1 = new JLabel("Numero de registro");
		lblNewLabel_1.setBounds(47, 72, 123, 14);
		InicioSesionInspector.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Contrase\u00F1a");
		lblNewLabel_2.setBounds(47, 138, 73, 14);
		InicioSesionInspector.add(lblNewLabel_2);
		
		txtNroRegistroInspector = new JTextPane();
		txtNroRegistroInspector.setBounds(180, 72, 124, 20);
		InicioSesionInspector.add(txtNroRegistroInspector);
		
		contraseñaInspector = new JPasswordField();
		contraseñaInspector.setBounds(181, 138, 123, 20);
		InicioSesionInspector.add(contraseñaInspector);
		
		JButton btnNewButton_2 = new JButton("Iniciar sesion");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Verificar q el numero de registro y la contraseña del inspector son validas.
				
				conectarBD("inspector", "inspector"); //Primero me conecto como inspector a la base de datos y luego verifico si existen los datos ingresados.
				conectarBDInspector(txtNroRegistroInspector.getText(), contraseñaInspector.getText());//Aca verifico si existe una usuario, con ese numero de legajo y contraseña.
				
				if(cnx != null) { //Hubo conexion
					JOptionPane.showMessageDialog(null,"Conexion exitosa" , "Aviso", JOptionPane.INFORMATION_MESSAGE);
					cargarVentanaInspector();
					dispose();
					
				}else {
					JOptionPane.showMessageDialog(null,"El numero de registro o la contraseña son incorrectos" , "Error", JOptionPane.ERROR_MESSAGE);	
					}
			}
		});
		btnNewButton_2.setBounds(167, 191, 123, 23);
		InicioSesionInspector.add(btnNewButton_2);
		
		JButton botonAtrasInspector = new JButton("Volver");
		botonAtrasInspector.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				txtNroRegistroInspector.setText("");
				contraseñaInspector.setText("");
				
				InicioSesionInspector.setVisible(false);
				Logeo.setVisible(true);
			}
		});
		botonAtrasInspector.setBounds(20, 216, 89, 23);
		InicioSesionInspector.add(botonAtrasInspector);
		
		
		
		InicioSesionAdministrador = new JPanel();
		InicioSesionAdministrador.setBackground(Color.LIGHT_GRAY);
		InicioSesionAdministrador.setBounds(0, 0, 424, 250);
		getContentPane().add(InicioSesionAdministrador);
		InicioSesionAdministrador.setLayout(null);
		InicioSesionAdministrador.setVisible(false);
		
		JLabel lblNewLabel_3 = new JLabel("Usuario");
		lblNewLabel_3.setBounds(65, 75, 46, 14);
		InicioSesionAdministrador.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Contrase\u00F1a");
		lblNewLabel_4.setBounds(52, 139, 74, 14);
		InicioSesionAdministrador.add(lblNewLabel_4);
		
		txtUsuarioAdmin = new JTextPane();
		txtUsuarioAdmin.setBounds(147, 69, 126, 20);
		InicioSesionAdministrador.add(txtUsuarioAdmin);
		
		txtContraseniaAdmin = new JPasswordField();
		txtContraseniaAdmin.setBounds(147, 139, 126, 20);
		InicioSesionAdministrador.add(txtContraseniaAdmin);
		
		JButton btnNewButton_3 = new JButton("Iniciar sesion");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				conectarBD(txtUsuarioAdmin.getText(), txtContraseniaAdmin.getText());
				if(cnx != null) { //Hubo conexion
					JOptionPane.showMessageDialog(null,"Conexion exitosa" , "Aviso", JOptionPane.INFORMATION_MESSAGE);
					dispose();
					cargarVentanaAdmin();
				}else {
					JOptionPane.showMessageDialog(null,"El usuario o la contraseña son incorrectos" , "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton_3.setBounds(157, 196, 126, 23);
		InicioSesionAdministrador.add(btnNewButton_3);
		
		JButton botonAtrasAdmin = new JButton("Volver");
		botonAtrasAdmin.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				txtContraseniaAdmin.setText("");
				txtUsuarioAdmin.setText("");
				InicioSesionAdministrador.setVisible(false);
				Logeo.setVisible(true);
				
			}
		});
		botonAtrasAdmin.setBounds(24, 216, 89, 23);
		InicioSesionAdministrador.add(botonAtrasAdmin);
		
		
		
		Logeo = new JPanel();
		Logeo.setBackground(Color.LIGHT_GRAY);
		Logeo.setBounds(10, 0, 424, 261);
		getContentPane().add(Logeo);
		Logeo.setLayout(null);
		Logeo.setVisible(true);
		
		JButton btnNewButton = new JButton("Administrador");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logeo.setVisible(false);
				InicioSesionInspector.setVisible(false);
				InicioSesionAdministrador.setVisible(true);
			}
		});
		btnNewButton.setBounds(39, 86, 158, 50);
		Logeo.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Inspector");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logeo.setVisible(false);
				InicioSesionAdministrador.setVisible(false);
				InicioSesionInspector.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(228, 86, 158, 50);
		Logeo.add(btnNewButton_1);
		
		JLabel lblNewLabel = new JLabel("Seleccione el usuario");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(76, 25, 273, 50);
		Logeo.add(lblNewLabel);
		
		JButton botonUsuarioParquimetro = new JButton("Parquimetro");
		botonUsuarioParquimetro.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Logeo.setVisible(false);
				conectarBD("parquimetro", "parq");
				if(cnx != null) {//Hubo conexion
					JOptionPane.showMessageDialog(null,"Conexion exitosa" , "Aviso", JOptionPane.INFORMATION_MESSAGE);
					dispose();
					cargarVentanaParquimetros();
					InicioSesionAdministrador.setVisible(false);
					InicioSesionInspector.setVisible(true);			
					
				}else {
				JOptionPane.showMessageDialog(null,"El usuario o la contraseña son incorrectos" , "Error", JOptionPane.ERROR_MESSAGE);
			}
				
				
				
			}
		});
		botonUsuarioParquimetro.setBounds(138, 176, 146, 50);
		Logeo.add(botonUsuarioParquimetro);
	}
	
	
	public String getNumLegajo() {
		return txtNroRegistroInspector.getText();
	}
	
	public void cargarVentanaInspector() {
		insp = new Inspector(cnx, this, Logeo);
		insp.setTitle("Parquimetros-Inspector");
		insp.setSize(1000, 600);
		insp.setVisible(true);
		insp.setLocationRelativeTo(null);
		insp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inst.dispose();
	}
	
	public void cargarVentanaAdmin() {
		Gui_admin = new Admin(cnx, this);
		Gui_admin.setTitle("Parquimetros-Administrador");
		Gui_admin.setSize(1000, 600);
		Gui_admin.setVisible(true);
		Gui_admin.setLocationRelativeTo(null);
		Gui_admin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inst.dispose();
	}
	
	public void cargarVentanaParquimetros() {
		parq = new Parquimetro(cnx, this);
		parq.setTitle("Parquimetros");
		parq.setSize(1000, 600);
		parq.setVisible(true);
		parq.setLocationRelativeTo(null);
		parq.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inst.dispose();
	}
	
	
	private void conectarBDInspector(String Numero_registro, String contraseñaInspector) {	
		try {
			
			Statement sentencia = cnx.createStatement();
			ResultSet respuesta = sentencia.executeQuery("SELECT legajo FROM inspectores where legajo = " + Numero_registro + " AND password=md5('" + contraseñaInspector + "')");
			
			if(!respuesta.next()) { //No hay inspector con ese numero de registro y contraseña.
				cnx = null;
			}
				sentencia.close();
				respuesta.close();
			
		} catch (SQLException ex) {
			cnx = null;
		}
	}
	
	
	private void conectarBD(String Usuario, String contrasenia) {
		DriverManager.setLoginTimeout(10);
		String servidor = "localhost:3306";
			String baseDatos = "parquimetros";
			String url = "jdbc:mysql://" + servidor + "/" +baseDatos+
			"?serverTimezone=America/Argentina/Buenos_Aires";
			
			try {
				cnx = DriverManager.getConnection(url, Usuario, contrasenia);
				
			} catch (SQLException ex) {
				cnx = null;
			}
	}
	
	
	public void desconectarBD() {
		try {
			this.cnx.close();
			this.cnx = null;
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
}
