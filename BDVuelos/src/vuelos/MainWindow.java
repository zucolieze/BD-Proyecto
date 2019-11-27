package vuelos;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;


import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class MainWindow extends javax.swing.JFrame 
{
	private VuelosWindow ventanaVuelos;
	private ConsultasWindow ventanaConsultas;

	private JDesktopPane panel;
	private JMenuBar barraMenu;
	private JMenuItem mniSalir;
	private JSeparator jSeparator1;
	private JMenuItem mniEmpleados;
	private JMenuItem mniConsultas;
	private JMenu mnuEjercicios;


	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow inst = new MainWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public MainWindow(){
		super();
		initGUI();
		this.ventanaVuelos = new VuelosWindow();
		ventanaVuelos.setLocation(0, -12);
		this.ventanaVuelos.setVisible(false);
		this.panel.add(this.ventanaVuelos);

		this.ventanaConsultas = new ConsultasWindow();
		this.ventanaConsultas.setVisible(false);
		this.panel.add(this.ventanaConsultas);

	}

	private void initGUI(){
		try{
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		try{
			{
				this.setTitle("Vuelos");
				this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			}
			{
				panel = new JDesktopPane();
				getContentPane().add(panel, BorderLayout.CENTER);
				panel.setPreferredSize(new java.awt.Dimension(1500, 600));
			}
			{
				barraMenu = new JMenuBar();
				setJMenuBar(barraMenu);
				{
					mnuEjercicios = new JMenu();
					barraMenu.add(mnuEjercicios);
					mnuEjercicios.setText("Consultas");
					{
						mniEmpleados = new JMenuItem();
						mnuEjercicios.add(mniEmpleados);
						mniEmpleados.setText("Vuelos disponibles");
						mniEmpleados.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								mostrarDialogosEmpleados();
							}
						});
					}
					{
						mniConsultas = new JMenuItem();
						mnuEjercicios.add(mniConsultas);
						mniConsultas.setText("SQL Consultas");
						mniConsultas.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								mostrarDialogoConsultas();
							}
						});
					}
					{
						jSeparator1 = new JSeparator();
						mnuEjercicios.add(jSeparator1);
					}
					{
						mniSalir = new JMenuItem();
						mnuEjercicios.add(mniSalir);
						mniSalir.setText("Salir");
						mniSalir.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								mniSalirActionPerformed(evt);
							}
						});
					}
				}
			}
			this.setSize(800, 600);
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void mniSalirActionPerformed(ActionEvent evt){
		this.dispose();
	}

	private void mniEmpleadosActionPerformed(){
		try
		{
			this.ventanaVuelos.setMaximum(true);
		}
		catch (PropertyVetoException e) {}
		this.ventanaVuelos.setVisible(true);      
	}

	private void mniConsultasActionPerformed(){
		try
		{
			this.ventanaConsultas.setMaximum(true);
		}
		catch (PropertyVetoException e) {}
		this.ventanaConsultas.setVisible(true);      
	}

	private void mostrarDialogoConsultas(){
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Ingrese la contraseña:");
		JPasswordField pass = new JPasswordField(12);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[]{"Aceptar", "Cancelar"};
		int option = JOptionPane.showOptionDialog(null, panel, "Acceso restringido",
				JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[1]);
		if(option == 0){
			char[] password = pass.getPassword();
			if(!ventanaConsultas.setPassword(password)) {
				mostrarDialogoConsultas();
			}
			else
				mniConsultasActionPerformed();
		}
	}

	private void mostrarDialogosEmpleados(){
		String legajo = JOptionPane.showInputDialog("Ingrese su legajo");
		if(legajo != null) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Ingrese la contraseña:");
			JPasswordField pass = new JPasswordField(12);
			panel.add(label);
			panel.add(pass);
			String[] options = new String[]{"Aceptar", "Cancelar"};
			int option = JOptionPane.showOptionDialog(null, panel, "Acceso restringido",
					JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, options, options[0]);
			if(option == 0){
				char[] password = pass.getPassword();
				if(!ventanaVuelos.login(legajo,password)) {
					mostrarDialogosEmpleados();
				}
				else
					mniEmpleadosActionPerformed();
			}
		}
	}

}
