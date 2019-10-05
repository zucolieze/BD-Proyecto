package vuelos;

import java.awt.*;
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

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;



@SuppressWarnings("serial")
public class VuelosWindow extends javax.swing.JInternalFrame 
{

	private JPanel pnlFiltroNombre;
	private JScrollPane scrTabla;
	private JTextField txtNombre;
	private JTable tabla;
	private JLabel lblNombre;
	protected Connection conexionBD = null;

	public VuelosWindow() 
	{
		super();

		initGUI();
	}

	private void initGUI() 
	{
		try {
			this.setPreferredSize(new java.awt.Dimension(640, 480));
			this.setBounds(0, 0, 640, 480);
			this.setTitle("Diponibilidad de Vuelos (JTable)");
			this.setClosable(true);
			this.setResizable(true);
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.setMaximizable(true);
			BorderLayout thisLayout = new BorderLayout();
			this.setVisible(true);
			getContentPane().setLayout(thisLayout);
			this.addComponentListener(new ComponentAdapter() {
				public void componentHidden(ComponentEvent evt) {
					desconectarBDAction(evt);
				}
				public void componentShown(ComponentEvent evt, String pw) {
					conectarBDAction(evt,pw);
				}
			});
			{
				pnlFiltroNombre = new JPanel();
				getContentPane().add(pnlFiltroNombre, BorderLayout.NORTH);
				{
					lblNombre = new JLabel();
					pnlFiltroNombre.add(lblNombre);
					lblNombre.setText("Búsqueda incremental del nombre");
				}
				{
					txtNombre = new JTextField();               
					pnlFiltroNombre.add(txtNombre);
					txtNombre.setColumns(30);
					txtNombre.addCaretListener(new CaretListener() {
						public void caretUpdate(CaretEvent evt) {
							txtNombreCaretUpdate(evt);
						}
					});
				}
			}
			{  //se crea un JScrollPane para poder mostrar la tabla en su interior 
				scrTabla = new JScrollPane();
				getContentPane().add(scrTabla, BorderLayout.CENTER);

				// se define el modelo de la tabla donde se almacenarán las tuplas 
				// extendiendo DefaultTableModel 
				final class TablaVuelosModel extends DefaultTableModel{
					// define la clase java asociada a cada columna de la tabla
					private Class[] types;
					// define si una columna es editable
					private boolean[] canEdit;

					TablaVuelosModel(){
						super(new String[][] {},
								new String[]{"-----", "-----", "-----"});
						types = new Class[] {java.lang.String.class,
								java.lang.Integer.class, java.lang.String.class };
						canEdit= new boolean[] { false, false, false };
					};             	

					// recupera la clase java de cada columna de la tabla
					public Class getColumnClass(int columnIndex) 
					{
						return types[columnIndex];
					}
					// determina si una celda es editable
					public boolean isCellEditable(int rowIndex, int columnIndex) 
					{
						return canEdit[columnIndex];
					}         	          	            	
				};

				TableModel VuelosModel = 	new TablaVuelosModel();

				tabla = new JTable(); // Crea una tabla
				scrTabla.setViewportView(tabla); //setea la tabla dentro del JScrollPane srcTabla               
				tabla.setModel(VuelosModel); // setea el modelo de la tabla  
				tabla.setAutoCreateRowSorter(true); // activa el ordenamiento por columnas, para
				// que se ordene al hacer click en una columna

			}
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void conectarBD(String pwEmpleado) 
	{
		if (this.conexionBD == null)
		{             
			try
			{  //se genera el string que define los datos de la conexión 
				String servidor = "localhost:3306";
				String baseDatos = "vuelos";
				String usuario = "empleado";
				String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos + 
						"?serverTimezone=America/Argentina/Buenos_Aires";
				//se intenta establecer la conexión
				this.conexionBD = DriverManager.getConnection(uriConexion, usuario, pwEmpleado);
			}
			catch (SQLException ex)
			{
				JOptionPane.showMessageDialog(this,
						"Se produjo un error al intentar conectarse a la base de datos.\n" + 
								ex.getMessage(),
								"Error",
								JOptionPane.ERROR_MESSAGE);
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
	}

	private void desconectarBD() 
	{
		if (this.conexionBD != null)
		{
			try
			{
				this.conexionBD.close();
				this.conexionBD = null;
			}
			catch (SQLException ex)
			{
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}  
	}

	private void refrescarTabla()
	{
		try
		{
			Statement stmt = this.conexionBD.createStatement();
			String sql = "SELECT nombre_barco, id, capitan " + 
					"FROM barcos " +
					"WHERE LOWER(nombre_barco) LIKE '%" + 
					this.txtNombre.getText().trim().toLowerCase() + "%' " +
					"ORDER BY nombre_barco";

			ResultSet rs = stmt.executeQuery(sql);
			((DefaultTableModel) this.tabla.getModel()).setRowCount(0);
			int i = 0;
			while (rs.next())
			{
				((DefaultTableModel) this.tabla.getModel()).setRowCount(i + 1);
				this.tabla.setValueAt(rs.getString("nombre_barco"), i, 0);
				this.tabla.setValueAt(rs.getInt("id"), i, 1);            
				this.tabla.setValueAt(rs.getString("capitan"), i, 2);
				i++;
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}


	//-------------------------------------
	//-----------OYENTES-------------------
	//-------------------------------------	
	private void conectarBDAction(ComponentEvent evt, String pw) 
	{
		this.conectarBD(pw);
	}

	private void desconectarBDAction(ComponentEvent evt) 
	{
		this.desconectarBD();
	}	

	private void txtNombreCaretUpdate(CaretEvent evt) 
	{
		this.refrescarTabla();
	}

}
