package vuelos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.sql.Types;
//import java.sql.ResultSet;
import java.sql.SQLException;
import quick.dbtable.*; 

@SuppressWarnings("serial")
public class ConsultasWindow extends javax.swing.JInternalFrame  
{

	private JPanel pnlConsulta;
	private JTextArea txtConsulta;
	private JButton botonBorrar;
	private JButton btnEjecutar;
	private DBTable tabla;    
	private JScrollPane scrConsulta;
	private JPanel panelContraseña;

	
	
	public ConsultasWindow() 
	{
		super();

		initGUI();
	}

	private void initGUI() 
	{
		try {
			setPreferredSize(new Dimension(800, 600));
			this.setBounds(0, 0, 800, 600);
			setVisible(true);
			BorderLayout thisLayout = new BorderLayout();
			this.setTitle("Consultas (Utilizando DBTable)");
			getContentPane().setLayout(thisLayout);
			this.setClosable(true);
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.setMaximizable(true);
			this.addComponentListener(new ComponentAdapter() {
				public void componentHidden(ComponentEvent evt) {
					desconectarBDAction(evt);
				}
				public void componentShown(ComponentEvent evt,String pw) {
					conectarBDAction(evt,pw);
				}
			});
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
						txtConsulta.setText("SELECT * \n" +
								"FROM * \n" +
								"WHERE * \n" +
								"AND * \n" +
								"ORDER * \n");
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
			}
			{
				// crea la tabla  
				tabla = new DBTable();
				// Agrega la tabla al frame (no necesita JScrollPane como Jtable)
				getContentPane().add(tabla, BorderLayout.CENTER);           
				// setea la tabla para sólo lectura (no se puede editar su contenido)  
				tabla.setEditable(false);        
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void conectarBD(String pw) 
	{
		try
		{
			String driver ="com.mysql.cj.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "vuelos"; 
			String usuario = "admin";
			String uriConexion = "jdbc:mysql://" + servidor + "/" + 
					baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";

			//establece una conexión con la  B.D. "batallas"  usando directamante una tabla DBTable    
			tabla.connectDatabase(driver, uriConexion, usuario, pw);

		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" 
							+ ex.getMessage(),
							"Error",
							JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void desconectarBD() 
	{
		try
        {
           tabla.close();            
        }
        catch (SQLException ex)
        {
           System.out.println("SQLException: " + ex.getMessage());
           System.out.println("SQLState: " + ex.getSQLState());
           System.out.println("VendorError: " + ex.getErrorCode());
        }  
	}

	private void refrescarTabla()
	{
		try
	      {    
	    	 tabla.setSelectSql(this.txtConsulta.getText().trim());
	    	  tabla.createColumnModelFromQuery();    	    
	    	  for (int i = 0; i < tabla.getColumnCount(); i++)
	    	  { 	   		  
	    		 if	 (tabla.getColumn(i).getType()==Types.TIME)  
	    		 {    		 
	    		    tabla.getColumn(i).setType(Types.CHAR);  
	  	       	 }
	    		 if	 (tabla.getColumn(i).getType()==Types.DATE)
	    		 {
	    		    tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
	    		 }
	          }   	     	  
	    	  tabla.refresh();
	       }
	      catch (SQLException ex)
	      {
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
	                                       ex.getMessage() + "\n", 
	                                       "Error al ejecutar la consulta.",
	                                       JOptionPane.ERROR_MESSAGE);
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

	private void btnEjecutarActionPerformed(ActionEvent evt) 
	{
		this.refrescarTabla();      
	}
}
