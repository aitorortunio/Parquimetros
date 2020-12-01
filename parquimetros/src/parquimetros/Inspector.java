package parquimetros;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class Inspector extends JFrame{

	protected Connection cnx;
	protected JList<String> Patentes;
	protected JTextArea nuevaPatente;
	protected DefaultListModel<String> modeloM;
	protected DefaultComboBoxModel<String> modeloCB;
	protected DefaultComboBoxModel<String> modeloP;
	protected Login login;
	protected DefaultListModel<String> modeloPatentesInfraccionadas;
	protected JPanel panelCargaPatentes;
	protected JPanel panelMultas;
	protected JList<String> multasLabradas;
	protected DefaultListModel listaMultasM;
	protected String calle;
	protected String altura;
	protected String fechaActual;
	protected String horarioActual;
	protected JComboBox<String> comboBoxUbicaciones;
	protected JComboBox<String> comboBoxParquimetro;
	protected Inspector insp;
	protected JPanel logeo;
	protected JButton btnNewButton_GenerarMultas, btnNewButton_2;
	private JButton btnVolverMultas;
	private JButton botonConfirmarPaquimetros, botonConfirmarCargaPatentes;
	
	public Inspector(Connection cnx, Login login, JPanel logeo) {
		getContentPane().setBackground(Color.LIGHT_GRAY);
		this.cnx = cnx;
		this.login = login;
		insp = this;
		this.logeo = logeo;
		
		modeloPatentesInfraccionadas = new DefaultListModel();
		listaMultasM = new DefaultListModel();
		
		getContentPane().setLayout(null);
		modeloM= new DefaultListModel<String>();
		modeloCB= new DefaultComboBoxModel<String>();
		modeloP = new DefaultComboBoxModel<String>();
		
		panelCargaPatentes = new JPanel();
		panelCargaPatentes.setBackground(Color.LIGHT_GRAY);
		panelCargaPatentes.setBounds(0, 0, 732, 447);
		getContentPane().add(panelCargaPatentes);
		panelCargaPatentes.setVisible(true);
		panelCargaPatentes.setEnabled(true);
		
		comboBoxUbicaciones = new JComboBox();
		comboBoxUbicaciones.setBounds(511, 90, 157, 22);
		
			comboBoxUbicaciones.setEnabled(true);
			panelCargaPatentes.setLayout(null);
			comboBoxParquimetro = new JComboBox();
			comboBoxParquimetro.setBounds(511, 218, 157, 22);
			comboBoxParquimetro.setEnabled(true);
			panelCargaPatentes.add(comboBoxParquimetro);
			panelCargaPatentes.add(comboBoxUbicaciones);
			
				nuevaPatente = new JTextArea();
				nuevaPatente.setBounds(20, 11, 183, 62);
				panelCargaPatentes.add(nuevaPatente);
				
				Patentes = new JList();
				Patentes.setBounds(20, 93, 183, 292);
				panelCargaPatentes.add(Patentes);
				
				JButton btnNewButton = new JButton("Agregar patente");
				btnNewButton.setBounds(230, 11, 194, 23);
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(formatoPatenteValida(nuevaPatente.getText())) {//True, si formato patente = "LLLNNN"
							if(estaPatenteBD()) {
								if(!estaPatente()) { //Si la patente no fue ingresada antes la agrega, caso contrario muestra un mensaje de erro
									botonConfirmarCargaPatentes.setEnabled(true);
									refrescarPatente();
								}else {
									JOptionPane.showMessageDialog(null,"La patente ya fue ingresada" , "Error", JOptionPane.ERROR_MESSAGE);
								}
							}else {
								JOptionPane.showMessageDialog(null,"La patente no se encuentra en la base de datos" , "Error", JOptionPane.ERROR_MESSAGE);
							}			
						}else {//No cumple con el formato "LLLNNN"
							JOptionPane.showMessageDialog(null,"La patente ingresada no cumple con el formato LLLNNN" , "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				panelCargaPatentes.add(btnNewButton);
				
				
				
				botonConfirmarCargaPatentes = new JButton("Confirmar carga patentes");
				botonConfirmarCargaPatentes.setEnabled(false);
				botonConfirmarCargaPatentes.setBounds(230, 49, 194, 23);
				botonConfirmarCargaPatentes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(Patentes.getModel().getSize() > 0) {
							btnNewButton_2.setEnabled(true);
							btnNewButton.setEnabled(false);
							botonConfirmarCargaPatentes.setEnabled(false);
							cargarUbicaciones();
						}else {
							JOptionPane.showMessageDialog(null,"No ingreso ninguna patente" , "Error", JOptionPane.ERROR_MESSAGE);
						}
						
					}
				});
				panelCargaPatentes.add(botonConfirmarCargaPatentes);
				
				JLabel lblNewLabel = new JLabel("Seleccione una ubicacion");
				lblNewLabel.setBounds(501, 49, 171, 23);
				lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
				panelCargaPatentes.add(lblNewLabel);
				
				btnNewButton_2 = new JButton("Confirmar ubicacion");
				btnNewButton_2.setBounds(230, 129, 194, 23);
				btnNewButton_2.setEnabled(false);
				btnNewButton_2.addMouseListener(new MouseAdapter() {
					//Hay que verificar que el inspector este habilitado en la ubicacion que selecciono.
					public void mouseClicked(MouseEvent e) {
						if(btnNewButton_2.isEnabled()) { //Si el boton esta habilitado
								String aux = (String) comboBoxUbicaciones.getSelectedItem();
								String [] calle_altura =  aux.split(" ");
								calle = "";
								
								for(int i=0;i < calle_altura.length-1; i++) {
									calle+= calle_altura[i];
									calle+=" ";
								}
								calle = calle.substring(0, calle.length() - 1);
								altura = calle_altura[calle_altura.length-1];
								
								try {
									Statement st = cnx.createStatement();
									ResultSet rs = st.executeQuery("SELECT legajo,calle,altura FROM asociado_con");//Tal vez no es necesari el Resulset porque no necesito el resultado de la consulta
									
									boolean estaAutorizado = false;
									
									while(rs.next() && !estaAutorizado) {
										if(login.getNumLegajo().equals(rs.getString("legajo")) && rs.getString("calle").equals(calle) && rs.getString("altura").equals(altura)) {
											//Esta habilitado el inspector en la ubicacion que selecciono
											estaAutorizado = true;
											JOptionPane.showMessageDialog(null,"El inspector esta autorizado para esta ubicacion" , "Aviso", JOptionPane.INFORMATION_MESSAGE);
										}
									}
									
									if(!estaAutorizado) {//No esta autorizado el inspector para esta ubicacion
										JOptionPane.showMessageDialog(null,"El inspector no esta autorizado para esta ubicacion" , "Error", JOptionPane.ERROR_MESSAGE);
									}else {//estaAutorizado
										//verifico si en asociado_con coniciden el num de legajo y la calle y altura
										rs = st.executeQuery("SELECT legajo,calle,altura FROM asociado_con");
										if(!login.getNumLegajo().equals("legajo") && calle.equals("calle") && altura.equals("altura")) {
											JOptionPane.showMessageDialog(null,"El inspector no esta autorizado para esta ubicacion" , "Error", JOptionPane.ERROR_MESSAGE);
										}else {//como el num de legajo y la calle y altura coinciden, ahora verifico si cumple con la fecha y dia
											Date date = new Date();
											horarioActual = new SimpleDateFormat("HH:mm:ss").format(date);//Obtengo la hora actual 
											fechaActual = new SimpleDateFormat("yyyy/MM/dd").format(date);//Obtengo la fecha actual
											try {
												date = new SimpleDateFormat("yyyy/MM/dd").parse(fechaActual);//Convierto la fecha String a tipo date
												Calendar calendar = Calendar.getInstance();
												calendar.setTime(date);
												String diaActual = convertirDia(calendar.get(Calendar.DAY_OF_WEEK));
												if(comprobarFechaHora(diaActual,horarioActual)) {//Metodo que verifica si el inspector cumple con la fecha y hora
													botonConfirmarPaquimetros.setEnabled(true);
													cargarParquimetros();
													btnNewButton_2.setEnabled(false);
													btnNewButton_GenerarMultas.setEnabled(true);
													
												}else {
													JOptionPane.showMessageDialog(null,"El dia u horario no estan permitidos para este numero de legajo" , "Error", JOptionPane.ERROR_MESSAGE);
													
												}
											} catch (ParseException e1) {
												e1.printStackTrace();
											}
										}
									}
									
									st.close();
									rs.close();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						}
					});
				panelCargaPatentes.add(btnNewButton_2);
				
				btnNewButton_GenerarMultas = new JButton("GenerarMultas");
				btnNewButton_GenerarMultas.setBounds(230, 332, 156, 37);
				btnNewButton_GenerarMultas.setEnabled(false);
				btnNewButton_GenerarMultas.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {		
						cargarPanelMultas(calle,altura,fechaActual,horarioActual, listaMultasM);
					}
				});
				panelCargaPatentes.add(btnNewButton_GenerarMultas);
				
				JLabel lblNewLabel_2 = new JLabel("Seleccione un parquimetro");
				lblNewLabel_2.setBounds(528, 164, 163, 14);
				panelCargaPatentes.add(lblNewLabel_2);
				
				JButton botonVolverCargaPatentes = new JButton("Volver");
				botonVolverCargaPatentes.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						volverLogin();
					}
				});
				botonVolverCargaPatentes.setBounds(37, 413, 89, 23);
				panelCargaPatentes.add(botonVolverCargaPatentes);
				
				botonConfirmarPaquimetros = new JButton("Confirmar parquimetro");
				botonConfirmarPaquimetros.setEnabled(false);
				botonConfirmarPaquimetros.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if(botonConfirmarPaquimetros.isEnabled()) {
							botonConfirmarPaquimetros.setEnabled(false);
							String id_parq = (String) comboBoxParquimetro.getSelectedItem();
							String [] parq =  id_parq.split(" ");
							id_parq = parq[parq.length-1];
							registrarAccedeActual(Integer.parseInt(id_parq), horarioActual,fechaActual);//Registro el nuevo acceso del inspector
							JOptionPane.showMessageDialog(null,"Se selecciono el parquimetro correctamente" , "Aviso", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				});
				botonConfirmarPaquimetros.setBounds(485, 324, 194, 23);
				panelCargaPatentes.add(botonConfirmarPaquimetros);
		
		
		panelMultas = new JPanel();
		panelMultas.setBackground(Color.LIGHT_GRAY);
		panelMultas.setBounds(0, 0, 732, 447);
		getContentPane().add(panelMultas);
		panelMultas.setLayout(null);
		panelMultas.setVisible(false);
		panelMultas.setEnabled(false);
		
		multasLabradas = new JList();
		multasLabradas.setModel(listaMultasM);
		multasLabradas.setBounds(27, 100, 679, 240);
		panelMultas.add(multasLabradas);
		
		
		JLabel lblNewLabel_1 = new JLabel("Multas labradas");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(276, 34, 183, 43);
		panelMultas.add(lblNewLabel_1);
		
		btnVolverMultas = new JButton("Volver");
		btnVolverMultas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				panelMultas.setEnabled(false);
				panelMultas.setVisible(false);
				panelCargaPatentes.setEnabled(true);
				panelCargaPatentes.setVisible(true);
			}
		});
		btnVolverMultas.setBounds(50, 389, 89, 23);
		panelMultas.add(btnVolverMultas);
		panelMultas.setVisible(false);
			
	}
	
	public void volverLogin() {
		this.dispose();
		login = new Login();
		login.setVisible(true);
		login.setSize(500, 350);
		login.setResizable(false);
		login.setLocationRelativeTo(null);
	}
	
	public Boolean formatoPatenteValida(String patente) {
		Boolean respuesta = true;
		
		if(patente.length() == 6) {
			for(int i = 0; i < 3; i++) {
				if(!(patente.charAt(i)>= 65 && patente.charAt(i)<=90)) {
					respuesta = false;
				}
			}
			
			for(int j = 3; j < 6; j++) {
				if(!(patente.charAt(j)>= 48 && patente.charAt(j)<= 57)) {
					respuesta = false;
				}
			}
			
		}else {
			respuesta = false;
		}
		return respuesta;
	}
	
	public Boolean estaPatenteBD() { //Deveulve verdadero si la patente ingresada esta en la base de datos, falso en caso contrario
		List<String> patentesBD = obtenerPatentesBD();
		for(String p:patentesBD) {
			if(p.equals(nuevaPatente.getText())) {//Si la patente esta esta en la base de datos devuelvo verdadero
				return true;
			}
		}
		return false;
	}
	
	public List<String> obtenerPatentesBD() { //Devuelve una lista con las patentes que estan cargadas en la base de datos
		List<String> patentesBD = new LinkedList<String>();
		try {
			Statement st = cnx.createStatement();
			ResultSet rs = st.executeQuery("SELECT patente FROM automoviles");
			
			while(rs.next()) {
				patentesBD.add(rs.getString("patente"));
			}
			
			st.close();
			rs.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return patentesBD;
	}
	
	
	
	public Boolean estaPatente(){ //Devuelve verdadero si la patente igresada ya fue ingresada antes, falso en caso contrario	
		for(int i=0; i< Patentes.getModel().getSize(); i++) {			
			if(Patentes.getModel().getElementAt(i).equals(nuevaPatente.getText())) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public void cargarParquimetros() {//Muestra los IDparquimetro en el comboBox de parquimetros
		try {
            Statement st = cnx.createStatement();                                                                     
            ResultSet rs = st.executeQuery("SELECT DISTINCT id_parq FROM parquimetros NATURAL JOIN estacionados WHERE calle = " + "'" + calle + "'" + " AND altura= " + altura);
			
            
            while (rs.next()) {
       	
            	modeloP.addElement("IDParquimetro: " + rs.getString("id_parq"));
            }
            comboBoxParquimetro.setModel(modeloP);
            
            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	
	
	public void cargarPanelMultas(String calle, String altura, String fechaActual, String horarioActual, DefaultListModel listaMultasM){
		panelCargaPatentes.setVisible(false);
		panelCargaPatentes.setEnabled(false);
		panelMultas.setVisible(true);
		refrescarMultasLabradas(calle, altura, fechaActual, horarioActual, listaMultasM);
		
	}
	
	public void refrescarMultasLabradas(String calle, String altura, String fechaActual, String horarioActual, DefaultListModel listaMultasM){//Actualiza la lista de multas labradas
		JList patentesEnInfraccion = patentesEnInfraccion(calle, altura);
		String [] infoMulta = new String [4];	
		String informacionMulta = "";
		
		for(int i=0;i < patentesEnInfraccion.getModel().getSize() ; i++  ) {
			infoMulta = generarMulta((String)patentesEnInfraccion.getModel().getElementAt(i), fechaActual, horarioActual);
			informacionMulta = ("num_multa: " + infoMulta[0] + "- fecha: " + infoMulta[1] + "- Hora: " + infoMulta[2] + "- calle: " + 
							    calle + "- altura: " + altura + "- Patente: " + infoMulta[3] + "- Legajo: " + login.getNumLegajo());

			this.listaMultasM.addElement(informacionMulta);
		}
		  patentesEnInfraccion.setModel(listaMultasM);
	}
	
	public String [] generarMulta(String patenteEnInfraccion, String fechaActual, String horarioActual) {//Metodo que genera y inserta en el panel las multas labradas de aquellas patentes con infraccion
		try {
			Statement	st = cnx.createStatement();
			 ResultSet rs = st.executeQuery("SELECT MAX(numero) as num FROM multa");
			 
			 if(rs.next()) {
				 String multa = rs.getString("num");
				 int numMulta = Integer.parseInt(multa);//Este es el ultimo numero de multa de la base de datos
				 numMulta++;//Le sumo uno al numMulta para agregarle como numero de multa a la nueva multa	
				 
				 rs = st.executeQuery("SELECT id_asociado_con  FROM asociado_con WHERE legajo =" + login.getNumLegajo());
				 if(rs.next()) {
					 int id_asociado_con = Integer.parseInt(rs.getString("id_asociado_con"));
					 st.executeUpdate("INSERT INTO Multa (fecha,hora,patente,id_asociado_con) VALUES(" + "'" + fechaActual + "' ,"  + "'" + horarioActual+ "' ," + "'" + patenteEnInfraccion + "' ," + id_asociado_con + ");");
						String [] infoMulta = new String[4];
						infoMulta[0] = numMulta + "";
						infoMulta[1] = fechaActual;
						infoMulta[2] = horarioActual;
						infoMulta[3] = patenteEnInfraccion;
						
						st.close();
						rs.close();
						return infoMulta;
				 }
				 
			 }
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String convertirDia(int num) { // Devuelve el dia en base al int pasado por parametro
		String dia = "";
		
		switch (num) {
		case 1:
			dia = "Do";
			break;
			
		case 2:
			dia = "Lu";
			break;
			
		case 3:
			dia = "Ma";
			break;	
			
		case 4:
			dia = "Mi";
			break;
			
		case 5:
			dia = "Ju";
			break;
			
		case 6:
			dia = "Vi";
			break;
			
		case 7:
			dia = "Sa";
			break;
		}
		return dia;
	}
	
	public void registrarAccedeActual(int id_parq, String fechaActual, String horarioActual) {//Registro el dia y horario del nuevo acceso del inspector
		
		try {
			Statement st = this.cnx.createStatement();
			st.executeUpdate("INSERT INTO accede VALUES ("+login.getNumLegajo()+"," + id_parq +", '"+horarioActual+"', '"+fechaActual+"');");		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	
	public Boolean comprobarFechaHora(String diaActual, String horarioActual) {//Verifica si se encuentra el inspector en el dia y turno correspondiente
		
			Boolean estaEnFechaHora = false;
			try {
				Statement st = this.cnx.createStatement();
				ResultSet rs = st.executeQuery("SELECT dia,turno FROM asociado_con WHERE legajo = " + login.getNumLegajo());				
				
					//Ahora hay que verificar la hora y fecha.					
					while(rs.next() && !estaEnFechaHora) {
						String turno = rs.getString("turno");
						String dia = rs.getString("dia");
						if(cumpleDia(dia, diaActual) && cumpleHorario(turno,horarioActual)) {
							estaEnFechaHora = true;
						}
					}
			
					rs.close();
					return estaEnFechaHora;
			}catch (SQLException e) {
				e.printStackTrace();
			}
					
			return null;
	}
	
	public Boolean cumpleDia(String dia, String diaActual) {//Verifico que cumple el dia con el dia que tiene asignado para esa ubicacion
		return diaActual.equals(dia);
	}
	
	public Boolean cumpleHorario(String turno, String horarioActual) {//Verifico que cumple la hora con el turno que tiene asignado para esa ubicacion
		Boolean respuesta = false;
		
		if(turno.equals("M")) {//Turno mañana
			if(horarioActual.compareTo("08:00:00") >= 0 && horarioActual.compareTo("14:00:00") < 0) {//Si esta dentro de ese rango horario cumple el horario y fecha
				respuesta = true;
			}
		}else {//Turno tarde
			if(horarioActual.compareTo("14:00:00") >= 0 && horarioActual.compareTo("20:00:00") < 0) {
				respuesta = true;
			}
		}
		return respuesta;
	}
	
	public void refrescarPatente() { //Actualiza para que se vizualicen las nuevas multas agregadas a la lista.
		
		modeloM.addElement(nuevaPatente.getText());
		Patentes.setModel(modeloM);
		this.panelCargaPatentes.add(Patentes);
	}
	
	public void cargarUbicaciones() { //Actualiza el comboBox de ubicaciones, con las ubicaciones mostrando la calle y la altura.
		try {
            Statement stmt = cnx.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT calle,altura FROM Parquimetros");
            
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
	
	public JList patentesEnInfraccion(String calle, String altura) {//Metodoque devuelve una lista con cada multa labrada.
		
		JList patentesEnInfraccion = new JList();
		
		try {
			Statement st = cnx.createStatement();
			ResultSet rs;
			Boolean patenteEstacionada = false;
			
		for(int i=0; i < Patentes.getModel().getSize();i++) {
			 rs = st.executeQuery("SELECT patente FROM Estacionados where calle = "+ "'" + calle + "'" + " AND altura = " + "'" + altura + "'");
				while(rs.next() && !patenteEstacionada) {
				
					if(rs.getString("patente").equals(Patentes.getModel().getElementAt(i))) {//Esta en la lista de patentes con estacionamiento abierto en la ubicacion seleccionada.
						patenteEstacionada = true;
					}
				}
				if(!patenteEstacionada) {//Agrego la patente que esta con estacionamiento abierto
					modeloPatentesInfraccionadas.addElement(Patentes.getModel().getElementAt(i));
				}
					patenteEstacionada = false;
					
					rs.close();
			}
		patentesEnInfraccion.setModel(modeloPatentesInfraccionadas);
		
		
		
		st.close();
		
		return patentesEnInfraccion;
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return null;
	}
}