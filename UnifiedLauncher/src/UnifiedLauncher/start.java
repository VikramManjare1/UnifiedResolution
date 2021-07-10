package UnifiedLauncher;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 */
public class start {
	private static final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	// public static final int SHELL_TRIM = CLOSE | TITLE | MIN | MAX | RESIZE;
	private static String KBFilePath="", QueryFilePath="", MaxStepsCount="", isUnitPreference="";

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shlUnifiedresolution = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlUnifiedresolution.setText("UnifiedResolution");
		shlUnifiedresolution.setSize(541, 312);

		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shlUnifiedresolution.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shlUnifiedresolution.setLocation(x, y);

		TabFolder tabFolder = new TabFolder(shlUnifiedresolution, SWT.NONE);
		tabFolder.setBounds(0, 0, 535, 283);

		TabItem home = new TabItem(tabFolder, SWT.NONE);
		home.setText("HOME");

		Form HOME = formToolkit.createForm(tabFolder);
		home.setControl(HOME);
		formToolkit.paintBordersFor(HOME);
		HOME.getHead().setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		HOME.getHead().setBackground(SWTResourceManager.getColor(153, 0, 204));
		HOME.setImage(SWTResourceManager.getImage(start.class, "/cont.png"));
		HOME.setText("Select/Enter parameters then click on RUN");
		HOME.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));

		Label lblKbfile = new Label(HOME.getBody(), SWT.NONE);
		lblKbfile.setBounds(20, 20, 55, 15);
		formToolkit.adapt(lblKbfile, true, true);
		lblKbfile.setText("KB File");

		Button btnSelectKbFile = new Button(HOME.getBody(), SWT.PUSH);
		btnSelectKbFile.setBounds(140, 10, 162, 25);
		formToolkit.adapt(btnSelectKbFile, true, true);
		btnSelectKbFile.setText("Select KB File");
		btnSelectKbFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(shlUnifiedresolution, SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.xml" });
				String fileName = dlg.open();
				if (fileName != null) {
					System.out.println(fileName);
					KBFilePath = fileName;
					btnSelectKbFile.setText("KB file selected!");
					btnSelectKbFile.setToolTipText(fileName);
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		});

		Label lblQueryFile = new Label(HOME.getBody(), SWT.NONE);
		lblQueryFile.setText("Query File");
		lblQueryFile.setBounds(20, 60, 55, 15);
		formToolkit.adapt(lblQueryFile, true, true);

		Button btnQueryFile = new Button(HOME.getBody(), SWT.NONE);
		btnQueryFile.setText("Select Query File");
		btnQueryFile.setBounds(140, 51, 162, 25);
		formToolkit.adapt(btnQueryFile, true, true);
		btnQueryFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(shlUnifiedresolution, SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.xml" });
				String fileName = dlg.open();
				if (fileName != null) {
					System.out.println(fileName);
					QueryFilePath = fileName;
					btnQueryFile.setText("Query file selected!");
					btnQueryFile.setToolTipText(fileName);
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		});

		Label lblMaxSteps = new Label(HOME.getBody(), SWT.NONE);
		lblMaxSteps.setText("Max Steps");
		lblMaxSteps.setBounds(20, 100, 55, 15);
		formToolkit.adapt(lblMaxSteps, true, true);

		Label lblUseUnitPreference = new Label(HOME.getBody(), SWT.NONE);
		lblUseUnitPreference.setText("UseUnitPreference");
		lblUseUnitPreference.setBounds(20, 140, 97, 15);
		formToolkit.adapt(lblUseUnitPreference, true, true);

		Spinner spinnerMaxSteps = new Spinner(HOME.getBody(), SWT.BORDER);
		spinnerMaxSteps.setMaximum(10000);
		spinnerMaxSteps.setMinimum(100);
		spinnerMaxSteps.setBounds(140, 100, 47, 22);
		formToolkit.adapt(spinnerMaxSteps);
		formToolkit.paintBordersFor(spinnerMaxSteps);

		Combo comboUseUnitPreference = new Combo(HOME.getBody(), SWT.DROP_DOWN | SWT.READ_ONLY);
		comboUseUnitPreference.setBounds(140, 140, 91, 23);
		formToolkit.adapt(comboUseUnitPreference);
		formToolkit.paintBordersFor(comboUseUnitPreference);
		comboUseUnitPreference.setItems("true", "false");
		comboUseUnitPreference.select(0);

		TabItem about = new TabItem(tabFolder, SWT.NONE);
		about.setText("ABOUT");

		Form ABOUT = formToolkit.createForm(tabFolder);
		about.setControl(ABOUT);
		formToolkit.paintBordersFor(ABOUT);
		ABOUT.getHead().setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		ABOUT.getHead().setBackground(SWTResourceManager.getColor(153, 0, 204));
		ABOUT.setImage(SWTResourceManager.getImage(start.class, "/lg.png"));
		ABOUT.setText("About UnifiedResolution");
		ABOUT.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));

		Label desc = new Label(ABOUT.getBody(), SWT.NONE);
		desc.setBounds(30, 15, 487, 25);
		formToolkit.adapt(desc, true, true);
		desc.setText("UnifiedResolution");
		desc.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));

		Label desc1 = new Label(ABOUT.getBody(), SWT.NONE);
		desc1.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		desc1.setBounds(30, 50, 487, 155);
		formToolkit.adapt(desc1, true, true);
		desc1.setText(
				"Version: 1.0\r\n\r\nDetails:\r\nKRR (Programming Assignment)\r\nImplementation of Resolution Method\r\n\r\nAuthor\t\tVikram Manjare (CS20M070)\r\nRelease Date\t01-05-2021");

		Button btnRun = new Button(HOME.getBody(), SWT.NONE);
		btnRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(KBFilePath.length()>0 && QueryFilePath.length()>0) {
					try {
						MaxStepsCount = spinnerMaxSteps.getText();
						System.out.println("Steps: "+MaxStepsCount);
						
						isUnitPreference = comboUseUnitPreference.getText();
						System.out.println("isUnitPreference: "+isUnitPreference);
						
						/*ClassLoader classLoader = getClass().getClassLoader();
						File file = new File(classLoader.getResource("UnifiedResolutionEXE.jar").getFile());
						System.out.println(file.getAbsolutePath());*/
						
						String jarStr = "java -jar UnifiedResolutionEXE.jar \""+KBFilePath+"\" \""+QueryFilePath+"\" "+MaxStepsCount+" "+isUnitPreference;
						Runtime.getRuntime().exec("cmd.exe /c start cmd.exe /k " + jarStr);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					MessageBox messageBox = new MessageBox(shlUnifiedresolution, SWT.ICON_ERROR | SWT.OK);
			        
			        messageBox.setText("Error");
			        messageBox.setMessage("KB/Query File(s) not selected!");
			        messageBox.open();
				}
			}
		});
		btnRun.setText("RUN");
		btnRun.setBounds(215, 184, 115, 25);
		formToolkit.adapt(btnRun, true, true);
		
		
		shlUnifiedresolution.open();
		shlUnifiedresolution.layout();
		while (!shlUnifiedresolution.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
	}
}
