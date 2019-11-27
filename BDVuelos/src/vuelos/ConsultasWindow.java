package vuelos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import quick.dbtable.DBTable;

@SuppressWarnings("serial")
public class ConsultasWindow extends javax.swing.JInternalFrame{

	private String pass; 
	private JPanel pnlConsulta;
	private JScrollPane scrConsulta;
	private JTextArea txtConsulta;
	private JList<String> listaTablas;
	private JList<String> listaCampos;
	private JButton botonBorrar;
	private JButton btnEjecutar;
	private String fieldSelected;
	private DBTable tabla;    

	protected Connection conexionBD;

	public ConsultasWindow() {
		initGUI();
		if(conexionBD != null)
			llenarTabla();
	}

	public boolean setPassword(char[] passw) {
		pass = new String(passw);
		return conectarBD();
	}

	public void initGUI() {
		setPreferredSize(new Dimension(800, 600));
		this.setBounds(0, 0, 800, 600);
		setVisible(true);
		BorderLayout thisLayout = new BorderLayout();
		this.setTitle("Consultas en SQL");
		getContentPane().setLayout(thisLayout);
		this.setClosable(true);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setMaximizable(true);
		this.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent evt) {
				thisComponentHidden(evt);
			}
			public void componentShown(ComponentEvent evt) {
				thisComponentShown(evt);
			}
		});
		Vector<String> temp = new Vector<String>();
		listaTablas= new JList<String>(temp);
		getContentPane().add(listaTablas,BorderLayout.WEST);
		tabla = new DBTable();
		getContentPane().add(tabla, BorderLayout.CENTER);           
		tabla.setEditable(false);    
		{
			pnlConsulta = new JPanel();
			getContentPane().add(pnlConsulta, BorderLayout.NORTH);
			{
				scrConsulta = new JScrollPane();
				pnlConsulta.add(scrConsulta);

				{
					txtConsulta = new JTextArea();
					scrConsulta.setViewportView(txtConsulta);
					txtConsulta.setTabSize(3);
					txtConsulta.setColumns(80);
					txtConsulta.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
					txtConsulta.setText("SELECT * FROM AEROPUERTOS");
					txtConsulta.setFont(new java.awt.Font("Monospaced",0,12));
					txtConsulta.setRows(10);
				}
			}
			{
				btnEjecutar = new JButton();
				pnlConsulta.add(btnEjecutar);
				btnEjecutar.setText("Ejecutar");
				btnEjecutar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						btnEjecutarActionPerformed(evt);
					}
				});
			}
			{
				botonBorrar = new JButton();
				pnlConsulta.add(botonBorrar);
				botonBorrar.setText("Borrar");            
				botonBorrar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						txtConsulta.setText("");            			
					}
				});
			}	
			{
				listaCampos = new JList<String>();
				getContentPane().add(listaCampos,BorderLayout.EAST);
			}
		}       
	}

	private void llenarTabla() {
		try {
			Statement stmt = this.conexionBD.createStatement();
			String sql = "SHOW TABLES;";
			ResultSet res = stmt.executeQuery(sql);
			Vector<String> temp = new Vector<String>();
			while (res.next()) {
				temp.add(res.getString("Tables_in_vuelos"));
			}
			listaTablas.setListData(temp);
			listaTablas.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if(!e.getValueIsAdjusting())
						fieldSelected = listaTablas.getSelectedValue().toString();
					refrescarCampos();	
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void refrescarCampos() {
		try {
			if(fieldSelected != null) {
				Statement stmt = this.conexionBD.createStatement();
				String sql = "DESCRIBE " + fieldSelected;
				ResultSet res = stmt.executeQuery(sql);
				Vector<String> temp = new Vector<String>();
				while (res.next()) {
					temp.add(res.getString("Field"));
				}
				listaCampos.setListData(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void refrescarTabla()
	{
		try{    
			tabla.setSelectSql(this.txtConsulta.getText().trim());
			tabla.createColumnModelFromQuery();    	    
			for (int i = 0; i < tabla.getColumnCount(); i++){ 
				if	 (tabla.getColumn(i).getType()==Types.TIME){    		 
					tabla.getColumn(i).setType(Types.CHAR);  
				}
				if	 (tabla.getColumn(i).getType()==Types.DATE){
					tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
				}
			}  
			tabla.refresh();	    	  
		}
		catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			String msj = traducir(ex.getErrorCode(), ex.getMessage());
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), 
					msj,                     
					"Error al ejecutar la consulta.",
					JOptionPane.ERROR_MESSAGE);
		}
	}



	private String traducir(int error, String mes) {
		String toRet = "";
		String aux ="";

		switch (error) {
		case 1146:
			aux = mes.substring((mes.indexOf("'")),mes.indexOf("'", 9));
			toRet = "La tabla " + aux + "' no existe.";
			break;
		case 1054:
			aux = mes.substring((mes.indexOf("'")),mes.indexOf("'", 16));
			toRet = "La columna " + aux + "' no existe";
			break;
		case 1052:
			aux = mes.substring((mes.indexOf("'")),mes.indexOf("'", 8));
			toRet = "La columna " + aux + "' es ambigüa.";
			break;
		case 1064:
			aux = mes.substring(mes.indexOf("'"),mes.lastIndexOf("'"));
			toRet = "Hay un error en la sintaxis, cerca de " + aux + "'.";
			break;
		}
		if (toRet == "")
			toRet = mes;
		return toRet;
	}

	private void thisComponentShown(ComponentEvent evt){
		this.conectarBD();
		if(conexionBD != null)
			this.llenarTabla();
	}

	private void thisComponentHidden(ComponentEvent evt){
		this.desconectarBD();
	}

	private void desconectarBD(){
		if (this.conexionBD != null){
			try{
				this.conexionBD.close();
				this.conexionBD = null;
			}
			catch (SQLException ex){
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
	}

	private boolean conectarBD(){
		try{
			String driver ="com.mysql.cj.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "vuelos"; 
			String usuario = "admin";
			String clave = pass;
			String uriConexion = "jdbc:mysql://" + servidor + "/" + 
					baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
			this.conexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
			tabla.connectDatabase(driver, uriConexion, usuario, clave);
		}
		catch (SQLException ex)
		{
			if(ex.getErrorCode() == 1045) {
				JOptionPane.showMessageDialog(this,
						"La contraseña es incorrecta.",
						"Error",
						JOptionPane.ERROR_MESSAGE);

			}
			else {
				JOptionPane.showMessageDialog(this,
						"Se produjo un error al intentar conectarse a la base de datos.\n" 
								+ ex.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;

	}
	private void btnEjecutarActionPerformed(ActionEvent evt) 
	{
		String consulta = this.txtConsulta.getText().trim();
		String [] array = consulta.split(" ");
		String primerP = array[0].toLowerCase();
		if (primerP.equals("select"))
			this.refrescarTabla();
		else
			this.ejecutarSQL();
	}

	private void ejecutarSQL() {
		String sql = this.txtConsulta.getText().trim();
		Statement stmt;
		try {
			stmt = this.conexionBD.createStatement();
			boolean res = stmt.execute(sql);
			llenarTabla();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, 
						e.getMessage(),
						"Error",
						JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
		}

	}
}
