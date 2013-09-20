package biz.pavonis.hexameter.example;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	/**
	 * Simple sample usage of the hexameter framework.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);

		// params for grid
		final int shellWidth = 1280;
		final int shellHeight = 768;

		shell.setSize(shellWidth, shellHeight);
		shell.setLayout(new GridLayout(1, false));

		new DemoComposite(shell, SWT.NONE, shellWidth, shellHeight);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
