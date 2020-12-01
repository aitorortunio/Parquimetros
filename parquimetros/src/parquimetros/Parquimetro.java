package parquimetros;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class Parquimetro extends JFrame{
	
	protected Connection cnx;
	protected Login login;
	protected JComboBox<String> comboBoxUbicaciones, comboBoxParquimetros, comboBoxTarjetas;
	protected DefaultComboBoxModel<String> modeloCB, modeloP, modeloT;
	protected String calle;
	protected String altura;
	protected JButton botonConfirmarUbicacion, botonConfirmarParquimetros, botonConfirmarTarjetas, botonConectarTarjeta, botonRecargar;
	protected Parquimetro parq;
	
	public Parquimetro(Connection cnx, Login login) {
		getContentPane().setBackground(Color.LIGHT_GRAY);
		this.cnx = cnx;
		this.login = login;
		parq = this;
		getContentPane().setLayout(null);
		
		
		modeloCB= new DefaultComboBoxModel<String>();
		modeloP = new DefaultComboBoxModel<String>();
		modeloT = new DefaultComboBoxModel<String>();
		
		JPanel panelParquimetro = new JPanel();
		panelParquimetro.setBackground(Color.LIGHT_GRAY);
		panelParquimetro.setBounds(0, 0, 693, 325);
		getContentPane().add(panelParquimetro);
		panelParquimetro.setLayout(null);
		
		JLabel txtUbicaciones = new JLabel("Ubicaciones");
		txtUbicaciones.setBounds(95, 36, 73, 14);
		panelParquimetro.add(txtUbicaciones);
		
		JLabel txtParquimetros = new JLabel("Parquimetros");
		txtParquimetros.setBounds(95, 98, 79, 14);
		panelParquimetro.add(txtParquimetros);
		
		comboBoxUbicaciones = new JComboBox();
		comboBoxUbicaciones.setBounds(178, 32, 147, 22);
		panelParquimetro.add(comboBoxUbicaciones);
		
		comboBoxParquimetros = new JComboBox();
		comboBoxParquimetros.setBounds(178, 94, 147, 22);
		panelParquimetro.add(comboBoxParquimetros);
		comboBoxParquimetros.setEnabled(false);
		
		JLabel txtTarjetas = new JLabel("Tarjetas");
		txtTarjetas.setBounds(95, 161, 73, 14);
		panelParquimetro.add(txtTarjetas);
		
		comboBoxTarjetas = new JComboBox();
		comboBoxTarjetas.setBounds(178, 157, 147, 22);
		panelParquimetro.add(comboBoxTarjetas);
		comboBoxTarjetas.setEnabled(false);
		
		botonConectarTarjeta = new JButton("Apoyar tarjeta");
		botonConectarTarjeta.setEnabled(false);
		botonConectarTarjeta.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {		
				ejecutarConectarTarjeta();
				botonConectarTarjeta.setEnabled(false);
			}
		});
		botonConectarTarjeta.setBounds(203, 229, 122, 23);
		panelParquimetro.add(botonConectarTarjeta);
		
		botonConfirmarUbicacion = new JButton("Confirmar Ubicacion");
		botonConfirmarUbicacion.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				botonConfirmarUbicacion.setEnabled(false);
				comboBoxUbicaciones.setEnabled(false);
				botonConfirmarParquimetros.setEnabled(true);
				comboBoxParquimetros.setEnabled(true);
				String[]  aux = obtenerCalleAltura();
				cargarParquimetros(aux[0], aux[1]);
			}
		});
		botonConfirmarUbicacion.setBounds(362, 32, 173, 23);
		panelParquimetro.add(botonConfirmarUbicacion);
		
		botonConfirmarParquimetros = new JButton("Confirmar Parquimetro");
		botonConfirmarParquimetros.setEnabled(false);
		botonConfirmarParquimetros.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				botonConfirmarParquimetros.setEnabled(false);
				comboBoxParquimetros.setEnabled(false);
				botonConfirmarTarjetas.setEnabled(true);
				comboBoxTarjetas.setEnabled(true);
				cargarTarjetas();
			}
		});
		botonConfirmarParquimetros.setBounds(362, 94, 173, 23);
		panelParquimetro.add(botonConfirmarParquimetros);
		
		botonConfirmarTarjetas = new JButton("Confirmar Tarjeta");
		botonConfirmarTarjetas.setEnabled(false);
		botonConfirmarTarjetas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				botonConfirmarTarjetas.setEnabled(false);
				comboBoxTarjetas.setEnabled(false);
				botonConectarTarjeta.setEnabled(true);			
			}
		});
		botonConfirmarTarjetas.setBounds(362, 157, 173, 23);
		panelParquimetro.add(botonConfirmarTarjetas);
		
		botonRecargar = new JButton("Reiniciar datos");
		botonRecargar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				comboBoxUbicaciones.setEnabled(true);
				botonConfirmarUbicacion.setEnabled(true);
			}
		});
		botonRecargar.setBounds(362, 229, 173, 23);
		panelParquimetro.add(botonRecargar);
		
		JButton botonAtras = new JButton("Volver");
		botonAtras.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				volverLogin();
			}
		});
		botonAtras.setBounds(50, 268, 89, 23);
		panelParquimetro.add(botonAtras);
		
		cargarUbicaciones();
		
	}
	
	public void volverLogin() {
		this.dispose();
		login = new Login();
		login.setVisible(true);
		login.setSize(500, 350);
		login.setResizable(false);
		login.setLocationRelativeTo(null);
	}
	
	
	
	public void ejecutarConectarTarjeta() {
		try {
			int p = Integer.parseInt(comboBoxTarjetas.getSelectedItem().toString());
			int t = Integer.parseInt(comboBoxParquimetros.getSelectedItem().toString());
			Statement S = cnx.createStatement();
			ResultSet rs = S.executeQuery("CALL conectar(" + p + ", " + t + ")");
			
			while(rs.next()) {
				String tipoOperacion = rs.getString("tipo_operacion");
				if(!rs.getString("tipo_operacion").equals("SQLEXCEPTION, la transaccion se aborto")) {
					if(!tipoOperacion.equals("ERROR: ID de tarjeta o ID de parquimetro erroneo.")) {
						if(tipoOperacion.equals("Apertura de estacionamiento")) {//Apertura
							if(rs.getString("resultado").equals("Saldo insuficiente.")) {
								JOptionPane.showMessageDialog(null,"Saldo insuficiente" , "Error", JOptionPane.ERROR_MESSAGE);
							}else {
								String aux = "Apertura de estacionamiento" + " "+ rs.getString("resultado") + "  Cantidad de minutos disponibles:" + rs.getString("Cantidad_Total_Minutos");
								JOptionPane.showMessageDialog(null,aux , "Aviso", JOptionPane.INFORMATION_MESSAGE);
							}
						}							
						if(tipoOperacion.equals("Cierre de estacionamiento")){//Cierre
							String mensaje = "Cierre de estacionamiento " + "Cantidad de minutos transcurridos: " + rs.getString("Tiempo_Transcurrido") + " Saldo actual: " + rs.getString("Saldo");
							JOptionPane.showMessageDialog(null,mensaje , "Aviso", JOptionPane.INFORMATION_MESSAGE);

						}								
					}else {
						JOptionPane.showMessageDialog(null,"ID de tarjeta o ID de parquimetro erroneo." , "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					JOptionPane.showMessageDialog(null,"Transaccion abortada" , "Error", JOptionPane.ERROR_MESSAGE);

				}
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public String[] obtenerCalleAltura() {
		String[] calleAltura = new String[2];
		
		String aux = (String) comboBoxUbicaciones.getSelectedItem();
		String [] calle_altura =  aux.split(" ");
		calle = "";
		
		for(int i=0;i < calle_altura.length-1; i++) {
			calle+= calle_altura[i];
			calle+=" ";
		}
		calle = calle.substring(0, calle.length() - 1);
		altura = calle_altura[calle_altura.length-1];
		
		calleAltura[0] = calle;
		calleAltura[1] = altura;
		
		return calleAltura;
	}
	

	public void cargarUbicaciones() { //Actualiza el comboBox de ubicaciones, con las ubicaciones mostrando la calle y la altura.
		try {
            Statement stmt = cnx.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT calle,altura FROM Parquimetros");
            modeloCB = new DefaultComboBoxModel<String>();
            
            while (rs.next()) {
            	modeloCB.addElement(rs.getString("calle") + " "+ rs.getString("Altura"));
            
            }
            comboBoxUbicaciones.setModel(modeloCB);
            
            	
            stmt.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	
	public void cargarParquimetros(String calle, String altura) {//Actualiza el comboBox de parquimetros, para la ubicacion seleccionada mostrando el idparq correspondiente
		try {
            Statement stmt = cnx.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT id_parq  FROM Parquimetros WHERE calle = " + "'" + calle + "'" + " AND altura= " + altura);
            modeloP = new DefaultComboBoxModel<String>();
            
            while (rs.next()) {
            	modeloP.addElement(rs.getString("id_parq"));
            
            }
            comboBoxParquimetros.setModel(modeloP);
            
            	
            stmt.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	
	public void cargarTarjetas() {
		try {
            Statement stmt = cnx.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id_tarjeta FROM Tarjetas");
            modeloT = new DefaultComboBoxModel<String>();
            
            while (rs.next()) {
            	modeloT.addElement(rs.getString("id_tarjeta"));
            }
            comboBoxTarjetas.setModel(modeloT);
            	
            stmt.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
}
