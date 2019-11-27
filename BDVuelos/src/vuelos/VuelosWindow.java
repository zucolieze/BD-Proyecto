package vuelos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import quick.dbtable.DBTable;

@SuppressWarnings("serial")
public class VuelosWindow extends javax.swing.JInternalFrame{

	protected Connection conexionBD;
	private JPanel pnlOpciones;
	private JPanel pnlTablaIda;
	private JPanel pnlTablaVuelta;
	private JPanel pnlReserva;
	private JLabel lblOrigen;
	private JLabel lblDestino;
	private JLabel lblTipo;
	private JLabel lblFechaIda;
	private JLabel lblFechaVuelta;
	private JLabel lblVueloIda;
	private JLabel lblVueloVuelta;
	private JLabel lblPasajero;
	private JLabel lblClaseI;
	private JLabel lblClaseV;
	private JTextField txtTipoP;
	private JTextField txtNroP;
	private JComboBox<String> cmbOrigen;
	private JComboBox<String> cmbDestino;
	private JComboBox<String> cmbTipo;
	private JComboBox<String> cmbTipoV;
	private JComboBox<String> cmbClasesIda;
	private JComboBox<String> cmbClasesVuelta;
	private JButton btnBuscar;
	private JButton btnReservar;
	private DBTable tablaIda;
	private DBTable tablaVuelta;
	private JTable tablaInfo;
	private JFormattedTextField fechaIda;
	private JFormattedTextField fechaVuelta;
	private DefaultTableModel modelo;
	private JScrollPane scrollP;
	private String legajoE;

	public VuelosWindow() {
		initGUI();
		conectarBD();
		llenarCombos();
	}

	private void initGUI() {
		setPreferredSize(new Dimension(1000, 800));
		setVisible(true);
		BorderLayout thisLayout = new BorderLayout();
		this.setTitle("Disponibilidad en vuelos");
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
		{
			pnlOpciones = new JPanel();
			pnlOpciones.setPreferredSize(new Dimension(180,80));
			getContentPane().add(pnlOpciones, BorderLayout.NORTH);
			{
				lblOrigen = new JLabel("Seleccione origen: ");
				pnlOpciones.add(lblOrigen);
				cmbOrigen = new JComboBox<String>();
				pnlOpciones.add(cmbOrigen);
			}
			{
				lblDestino = new JLabel("Seleccione destino: ");
				pnlOpciones.add(lblDestino);
				cmbDestino = new JComboBox<String>();
				pnlOpciones.add(cmbDestino);
			}
			{
				lblTipo = new JLabel("Seleccione tipo consulta:");
				pnlOpciones.add(lblTipo);
				String [] tipo = {"Sólo ida", "Ida y vuelta"};
				cmbTipo = new JComboBox<String>(tipo);
				ActionListener alTipo = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fechaVuelta.setVisible(cmbTipo.getSelectedIndex() == 1);
						lblFechaVuelta.setVisible(cmbTipo.getSelectedIndex() == 1);
					}
				};
				cmbTipo.addActionListener(alTipo);
				pnlOpciones.add(cmbTipo);
			}
			{
				lblFechaIda = new JLabel("Fecha ida: ");
				pnlOpciones.add(lblFechaIda);
				try {
					fechaIda = new JFormattedTextField(new MaskFormatter("##'/##'/####"));
					pnlOpciones.add(fechaIda);
				} catch (ParseException e) {
					e.printStackTrace();
				}		
				lblFechaVuelta = new JLabel("Fecha vuelta: ");
				pnlOpciones.add(lblFechaVuelta);
				try {
					fechaVuelta = new JFormattedTextField(new MaskFormatter("##'/##'/####"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				pnlOpciones.add(fechaVuelta);
				fechaVuelta.setVisible(false);
				lblFechaVuelta.setVisible(false);
			}
			{
				btnBuscar = new JButton("Buscar");
				btnBuscar.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(controlCampos()) {
							cargarTabla();
						}
					}
				});
				pnlOpciones.add(btnBuscar);        		
			}
			{
				btnReservar = new JButton("Reservar");
				btnReservar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						mostrarReservar();
					}
				});
				pnlOpciones.add(btnReservar);
			}
		}
		{
			pnlTablaIda = new JPanel();
			getContentPane().add(pnlTablaIda, BorderLayout.CENTER);
			{
				tablaIda = new DBTable();      
				tablaIda.addMouseListener(new MouseAdapter() {
                   public void mouseClicked(MouseEvent evt) {
                      tablaMouseClicked(evt, tablaIda);
                   }
            	});
				tablaIda.setEditable(false);
				tablaIda.setVisible(false);
				pnlTablaIda.add(tablaIda);
			}
		}
		{
			pnlTablaVuelta = new JPanel();
			getContentPane().add(pnlTablaVuelta, BorderLayout.EAST);
			{
				tablaVuelta = new DBTable();
				tablaVuelta.addMouseListener(new MouseAdapter() {
                   public void mouseClicked(MouseEvent evt) {
                      tablaMouseClicked(evt, tablaVuelta);
                   }
            	});
				tablaVuelta.setEditable(false);
				tablaVuelta.setVisible(false);
				pnlTablaVuelta.add(tablaVuelta);
			}
		}
		{
			scrollP = new JScrollPane();
			scrollP.setPreferredSize(new Dimension(300,60));
			getContentPane().add(scrollP,BorderLayout.WEST);
			{
				modelo = new DefaultTableModel();
				tablaInfo = new JTable(modelo);
				modelo.addColumn("NOMBRE CLASE");
				modelo.addColumn("ASIENTOS DISP.");
				modelo.addColumn("PRECIO");
				tablaInfo.setVisible(false);
				scrollP.setViewportView(tablaInfo);
			}
		}
		{
			pnlReserva = new JPanel(new GridLayout(0,1));
			{
				String [] tipo = {"Ida", "Ida y vuelta" };
				cmbTipoV = new JComboBox<String>(tipo);
				cmbTipoV.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(cmbTipoV.getSelectedIndex() == 0) { //Solo ida
							lblVueloVuelta.setVisible(false);
							lblClaseV.setVisible(true);
							cmbClasesVuelta.setVisible(false);
						}
						else {
							lblVueloVuelta.setVisible(true);
							lblClaseV.setVisible(true);
							cmbClasesVuelta.setVisible(true);
							int filaVuelta = tablaVuelta.getSelectedRow();
							String vueltaNombre = tablaVuelta.getValueAt(filaVuelta, 0).toString();
							java.sql.Date dFecha = Fechas.convertirStringADateSQL(fechaVuelta.getText().trim());
							getClases(vueltaNombre, dFecha, 1);
						}
					}
				});
				pnlReserva.add(cmbTipoV);
			}
			{
				lblClaseI = new JLabel("Clase para ida:");
				pnlReserva.add(lblClaseI);
			}
			{
				cmbClasesIda = new JComboBox<String>();
				pnlReserva.add(cmbClasesIda);
			}
			{
				lblClaseV = new JLabel("Clase para vuelta:");
				pnlReserva.add(lblClaseV);
			}
			{
				cmbClasesVuelta = new JComboBox<String>();
				pnlReserva.add(cmbClasesVuelta);
			}
			{
				lblVueloIda = new JLabel();
				pnlReserva.add(lblVueloIda);
			}
			{
				lblVueloVuelta = new JLabel();
				pnlReserva.add(lblVueloVuelta);
			}
			{
				lblPasajero = new JLabel("Tipo y nro de DNI del pasajero:");
				txtNroP = new JTextField(); 
				txtTipoP = new JTextField();
				pnlReserva.add(lblPasajero);
				pnlReserva.add(txtTipoP);
				pnlReserva.add(txtNroP);
			}
		}
		
	}

	private void llenarCombos() {
		try {
			Statement stmt = this.conexionBD.createStatement();
			String sql = "SELECT * FROM ubicaciones";
			ResultSet res = stmt.executeQuery(sql);
			String pais, ciudad, estado, aux;
			while(res.next()) {
				pais = res.getString("pais");
				ciudad = res.getString("ciudad");
				estado = res.getString("estado");
				aux = pais + " - " + ciudad + " - " + estado;
				cmbOrigen.addItem(aux);
				cmbDestino.addItem(aux);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public boolean login(String legajo, char[] password) {
		legajoE = legajo;
		if(conexionBD == null)
			conectarBD();
		try {
			Statement stmt = this.conexionBD.createStatement();
			String sql = "SELECT * FROM empleados WHERE legajo = " + legajo + " AND password = MD5('" + new String(password) + "');";
			ResultSet res = stmt.executeQuery(sql);
			if(res.next()) 
				return true;
			else {
				JOptionPane.showMessageDialog(this,
						"La contraseña y/o legajo son incorrectos.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	private void thisComponentShown(ComponentEvent evt){
		this.conectarBD();
	}

	private void thisComponentHidden(ComponentEvent evt){
		this.desconectarBD();
	}

	private boolean controlCampos() {
		String msj = "";
		if(!Fechas.validar(this.fechaIda.getText().trim())) {
			msj = "Ingrese una fecha válida para la fecha de ida";
		}
		if (cmbTipo.getSelectedIndex() == 1)
			if (!Fechas.validar(this.fechaVuelta.getText().trim()))
				msj = "Ingrese una fecha válida para la fecha de vuelta";
		java.util.Date ida = Fechas.convertirStringADate(this.fechaIda.getText().trim());
		java.util.Date vuelta = (this.fechaVuelta.getText().trim().equals(""))?null:Fechas.convertirStringADate(this.fechaVuelta.getText().trim());
		if((vuelta != null) && (vuelta.before(ida)))
			msj = "La fecha de vuelta debe ser posterior a la de ida";
		if(msj != "") {
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
				msj,
				"Error",
				JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	private void cargarTabla() {
		String origen = cmbOrigen.getSelectedItem().toString();		
		String[] aux = origen.split("-");
		String paisO = aux[0].trim();
		String ciudadO = aux[1].trim();
		String estadoO = aux[2].trim();
		String destino = cmbDestino.getSelectedItem().toString();		
		aux = destino.split("-");
		String paisD = aux[0].trim();
		String ciudadD = aux[1].trim();
		String estadoD = aux[2].trim();
		java.sql.Date dFechaIda = Fechas.convertirStringADateSQL(this.fechaIda.getText().trim());
		java.sql.Date dFechaVuelta = null ;
		if(fechaVuelta.getText().trim().length() > 4)
			dFechaVuelta = Fechas.convertirStringADateSQL(this.fechaVuelta.getText().trim());
		try {
			PreparedStatement stmt,stmt1;
			String sql = "SELECT DISTINCT " + 
				"	Numero, " + 
				"	ASalida_nombre AS AEROP_SALIDA," + 
				"	HoraSalida AS HORA_SALIDA," + 
				"	ALlegada_nombre AS AEROP_LLEGADA," + 
				"	HoraLlegada AS HORA_LLEGADA," + 
				"	Modelo AS MODELO_AVION," + 
				"	duracion AS TIEMPO_ESTIMADO,"
				+ " Fecha AS FECHA " + 
				"FROM " + 
				"	vuelos_disponibles " + 
				"WHERE " +
				" (ASalida_pais = ?) " +
				" AND (ASalida_ciudad = ?) " +
				" AND (ASalida_estado = ?) " +
				" AND (ALlegada_pais = ?)" +
				" AND (ALlegada_ciudad = ?)" +
				" AND (ALlegada_estado = ?)" +
				" AND (Fecha = ?)";
			
			stmt = this.conexionBD.prepareStatement(sql);
			stmt.setString(1, paisO);
			stmt.setString(2, ciudadO);
			stmt.setString(3, estadoO);
			stmt.setString(4, paisD);
			stmt.setString(5, ciudadD);
			stmt.setString(6, estadoD);
			stmt.setDate(7, dFechaIda);
				
			String sqlAux = (stmt.toString()).substring(stmt.toString().indexOf(":") + 1);
			tablaIda.setSelectSql(sqlAux);
			tablaIda.createColumnModelFromQuery();    	    
			for (int i = 0; i < tablaIda.getColumnCount(); i++){ 
				if	 (tablaIda.getColumn(i).getType()==Types.TIME){    		 
					tablaIda.getColumn(i).setType(Types.CHAR);  
				}
				if	 (tablaIda.getColumn(i).getType()==Types.DATE){
					tablaIda.getColumn(i).setDateFormat("dd/MM/YYYY");
				}
			}  
			tablaIda.refresh();	
			tablaIda.setVisible(true);
			if (cmbTipo.getSelectedIndex() == 1) {
				stmt1 = this.conexionBD.prepareStatement(sql);
				stmt1.setString(1, paisD);
				stmt1.setString(2, ciudadD);
				stmt1.setString(3, estadoD);
				stmt1.setString(4, paisO);
				stmt1.setString(5, ciudadO);
				stmt1.setString(6, estadoO);
				stmt1.setDate(7, dFechaVuelta);
				sqlAux = (stmt1.toString()).substring(stmt1.toString().indexOf(":") + 1);
				tablaVuelta.setSelectSql(sqlAux);
				tablaVuelta.createColumnModelFromQuery();    	    
				for (int i = 0; i < tablaVuelta.getColumnCount(); i++){ 
					if	 (tablaVuelta.getColumn(i).getType()==Types.TIME){    		 
						tablaVuelta.getColumn(i).setType(Types.CHAR);  
					}
					if	 (tablaVuelta.getColumn(i).getType()==Types.DATE){
						tablaVuelta.getColumn(i).setDateFormat("dd/MM/YYYY");
					}
				}
				tablaVuelta.refresh();	
				tablaVuelta.setVisible(true);
			}
		}
		catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					ex.getMessage() + "\n",         
					"Error al ejecutar la consulta.",
					JOptionPane.ERROR_MESSAGE);

		} 
	}
	
	private void tablaMouseClicked(MouseEvent evt, DBTable tabla) {
      if ((tabla.getSelectedRow() != -1) && (evt.getClickCount() == 2))
      {
         //this.seleccionarFila();
    	 int indice = tabla.getSelectedRow();
    	 String codigo = tabla.getValueAt(indice, 0).toString();
    	 JFormattedTextField fAux = (tabla.equals(tablaIda))?this.fechaIda: this.fechaVuelta;
    	 java.sql.Date fecha = Fechas.convertirStringADateSQL(fAux.getText().trim());
    	 mostrarInfo(codigo, fecha);
      }
	}
	
	private void mostrarInfo(String codigo, java.sql.Date fecha) {
		try {
			this.tablaInfo.setVisible(true);
			modelo.setRowCount(0);
			this.tablaInfo.revalidate();
			PreparedStatement stmt;
			String sql = "SELECT NombreClase, asientos_disponibles, Precio FROM vuelos_disponibles WHERE Numero = ? AND Fecha = ?";
			stmt = this.conexionBD.prepareStatement(sql);
			stmt.setString(1, codigo);
			stmt.setDate(2, fecha);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
			   // Se crea un array que será una de las filas de la tabla.
			   Object [] fila = new Object[3]; // Hay tres columnas en la tabla

			   // Se rellena cada posición del array con una de las columnas de la tabla en base de datos.
			   for (int i=0;i<3;i++)
			      fila[i] = rs.getObject(i+1); // El primer indice en rs es el 1, no el cero, por eso se suma 1.

			   // Se añade al modelo la fila completa.
			   modelo.addRow(fila);
			}
			this.tablaInfo.setModel(modelo);
			this.tablaInfo.revalidate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void mostrarReservar() {
		String idaNombre = "";
		java.sql.Date idaFecha = null;
		String vueltaNombre = "";
		java.sql.Date vueltaFecha = null;
		int filaIda = tablaIda.getSelectedRow();
		int cantIda = tablaIda.getRowCount();
		int filaVuelta = tablaVuelta.getSelectedRow();
		int cantVuelta = tablaVuelta.getRowCount();
		if(cantIda > 0) {
			//Mostrar reserva de ida
			idaNombre = tablaIda.getValueAt(filaIda, 0).toString();
			idaFecha = Fechas.convertirStringADateSQL(this.fechaIda.getText().trim());
			lblVueloIda.setText("Vuelo ida: " + idaNombre + " - " + idaFecha);
			if(cantVuelta > 0) {
				//Mostrar reserva de vuelta
				vueltaNombre = tablaVuelta.getValueAt(filaVuelta, 0).toString();
				vueltaFecha = Fechas.convertirStringADateSQL(this.fechaVuelta.getText().trim());
				lblVueloVuelta.setText("Vuelo vuelta: " + vueltaNombre + " - " + vueltaFecha);
			}
		}
		if(cmbTipoV.getSelectedIndex() == 1) {
			java.sql.Date dFecha = Fechas.convertirStringADateSQL(this.fechaVuelta.getText().trim());
			getClases(vueltaNombre, dFecha,1);
		}
		java.sql.Date dFechaIda = Fechas.convertirStringADateSQL(this.fechaIda.getText().trim());
		getClases(idaNombre, dFechaIda, 0);
			
		String[] options = new String[]{"Aceptar", "Cancelar"};
		int option = JOptionPane.showOptionDialog(null, pnlReserva, "Reserva",
				JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);
		if (option == 0) {
			String claseEleg = cmbClasesIda.getSelectedItem().toString();
			String res = "";
			if (verificar())
				if(cmbTipoV.getSelectedIndex() == 0)
					res = hacerReservaIda(idaNombre, idaFecha, claseEleg, txtTipoP.getText().trim(), txtNroP.getText().trim(), legajoE);
				else {
					String claseVuelta = cmbClasesVuelta.getSelectedItem().toString();
					res = hacerReservaIdaVuelta(idaNombre, vueltaNombre, idaFecha, vueltaFecha, claseEleg, claseVuelta, txtTipoP.getText().trim(), txtNroP.getText().trim(), legajoE);
				}

			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), 
					res,                     
					"Reserva",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void getClases(String numVuelo, java.sql.Date fechaVuelo, int combo) {
		PreparedStatement stmt;
		String sql = "SELECT NombreClase FROM vuelos_disponibles WHERE Numero = ? AND Fecha = ?";
		if(combo == 0)
			cmbClasesIda.removeAllItems();
		else
			cmbClasesVuelta.removeAllItems();
		try {
			stmt = this.conexionBD.prepareStatement(sql);
			stmt.setString(1, numVuelo);
			stmt.setDate(2, fechaVuelo);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if(combo == 0)
					cmbClasesIda.addItem(rs.getString("NombreClase"));
				else
					cmbClasesVuelta.addItem(rs.getString("NombreClase"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean verificar() {
		String msj = "";;
		if (txtNroP.getText().equals(""))
			msj = "Ingrese el nro de documento del pasajero";
		if (txtTipoP.getText().equals(""))
			msj = "Ingrese el tipo de documento del pasajero";
		if (cmbClasesIda.getSelectedIndex() == -1)
			msj = "Seleccione una clase";
		if(!msj.equals("")) {
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), 
					msj,                     
					"Faltan datos",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	private String hacerReservaIda(String idaNombre, java.sql.Date idaFecha, String claseEleg, String tipoDoc, String nroDoc, String legajo) {
		PreparedStatement stmt;
		String toRet = "";
		String sql = "CALL reservaVueloIda(?,?,?,?,?,?)";
		try {
			int nro = Integer.parseInt(nroDoc);
			int leg = Integer.parseInt(legajo);
			stmt = this.conexionBD.prepareStatement(sql);
			stmt.setString(1, idaNombre);
			stmt.setDate(2, idaFecha);
			stmt.setString(3, claseEleg);
			stmt.setString(4, tipoDoc);
			stmt.setInt(5, nro);
			stmt.setInt(6, leg);
			System.out.println(stmt.toString());
			ResultSet res = stmt.executeQuery();
			while(res.next()) 
				toRet = res.getString("Resultado");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}
	
	private String hacerReservaIdaVuelta(String idaNombre, String vueltaNombre, java.sql.Date idaFecha, java.sql.Date vueltaFecha, String claseIda, String claseVuelta, String tipoDoc, String nroDoc, String legajo) {
		PreparedStatement stmt;
		String toRet = "";
		String sql = "CALL reservaVueloIdaVuelta(?,?,?,?,?,?,?,?,?) ";
		try {		
			int nro = Integer.parseInt(nroDoc);
			int leg = Integer.parseInt(legajo);
			stmt = this.conexionBD.prepareStatement(sql);
			stmt.setString(1, idaNombre);
			stmt.setString(2, vueltaNombre);
			stmt.setDate(3, idaFecha);
			stmt.setDate(4, vueltaFecha);
			stmt.setString(5, claseIda);
			stmt.setString(6, claseVuelta);
			stmt.setString(7, tipoDoc);
			stmt.setInt(8, nro);
			stmt.setInt(9, leg);
			System.out.println(stmt.toString());
			ResultSet res = stmt.executeQuery();
			while(res.next()) 
				toRet = res.getString("Resultado");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
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

	private void conectarBD(){
		try{
			String driver ="com.mysql.cj.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "vuelos"; 
			String usuario = "empleado";
			String clave = "empleado";
			String uriConexion = "jdbc:mysql://" + servidor + "/" + 
					baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
			this.conexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
			tablaIda.connectDatabase(driver, uriConexion, usuario, clave);
			tablaVuelta.connectDatabase(driver,uriConexion, usuario, clave);
		}
		catch (SQLException ex){
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" 
							+ ex.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
