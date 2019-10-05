package vuelos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

//import batallas.VentanaBarcos;




@SuppressWarnings("serial")
public class MainWindow extends javax.swing.JFrame 
{

	private JDesktopPane jDesktopPane1;
	private JMenuBar jMenuBar1;
	private JMenu menuBar;
	private JMenuItem consultaSql;
	private JMenuItem vuelosDisponibles;
	private ConsultasWindow consultasWindow;
	private VuelosWindow vuelosWindow;


	public static void main (String args[]) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow inst = new MainWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public MainWindow()
	{
		super();
		initGUI();


		// ventanas de consultas y demas ventanas
		this.consultasWindow = new ConsultasWindow();
		consultasWindow.setLocation(0, -12);
		this.consultasWindow.setVisible(false);
		this.jDesktopPane1.add(this.consultasWindow);

		this.vuelosWindow = new VuelosWindow();
		vuelosWindow.setLocation(0, -12);
		this.vuelosWindow.setVisible(false);
		this.jDesktopPane1.add(this.vuelosWindow);
	}


	public void initGUI() 
	{
		try 
		{
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		try 
		{
			this.setTitle("Java y MySQL");
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			jDesktopPane1 = new JDesktopPane();
			getContentPane().add(jDesktopPane1, BorderLayout.CENTER);
			jDesktopPane1.setPreferredSize(new java.awt.Dimension(800, 600));

			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
			}
			{
				menuBar = new JMenu();
				jMenuBar1.add(menuBar);
				menuBar.setText("Menu");
			}
				{
					consultaSql = new JMenuItem();
					menuBar.add(consultaSql);
					consultaSql.setText("Consulta SQL (DBTable)");
					consultaSql.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							consultasSqlActionPerformed(evt);
						}
					});
				}
				{
					vuelosDisponibles = new JMenuItem();
					menuBar.add(vuelosDisponibles);
					vuelosDisponibles.setText("Disponibilidad de vuelos (DBTable");
					vuelosDisponibles.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							disponibilidadVuelosActionPerformed(evt);
						}
					});
				}

			this.setSize(800, 600);
			pack();
		}

		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}



	//-------------------------------------
	//-----------OYENTES-------------------
	//-------------------------------------

	private void consultasSqlActionPerformed(ActionEvent evt) {
		try
		{
			this.consultasWindow.setMaximum(true);
		}
		catch (PropertyVetoException e) {}
		this.consultasWindow.setVisible(true);
	}
	private void disponibilidadVuelosActionPerformed(ActionEvent evt) 
	{
		try
		{
			this.vuelosWindow.setMaximum(true);
		}
		catch (PropertyVetoException e) {}
		this.vuelosWindow.setVisible(true);      
	}


}
