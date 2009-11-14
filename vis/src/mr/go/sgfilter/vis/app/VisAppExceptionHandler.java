/*
 * Copyright [2009] [Marcin Rzeźnicki]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package mr.go.sgfilter.vis.app;

import java.util.ResourceBundle;

import javax.swing.JOptionPane;

final class VisAppExceptionHandler implements Thread.UncaughtExceptionHandler {

	private static String getErrorMessage(Throwable e) {
		String result = application.getString(e.getMessage().toUpperCase());
		if (result == null) {
			result = application.getString(e
					.getClass()
					.getSimpleName()
					.toUpperCase());
			if (result == null) {
				result = application.getString("GENERIC_ERROR");
			}
		}
		return (result + '\n');
	}

	private final Application	visAppInstance;

	public VisAppExceptionHandler(
			Class<? extends Application> visAppClass) {
		visAppInstance = org.jdesktop.application.Application
				.getInstance(visAppClass);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		String message = getErrorMessage(e);
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			message += e.getCause().getMessage();
		}
		JOptionPane.showMessageDialog(
				visAppInstance.getMainFrame(),
				message,
				application.getString("ERROR"),
				JOptionPane.ERROR_MESSAGE);
	}

	private static final ResourceBundle	application	= ResourceBundle
															.getBundle("mr/go/sgfilter/vis/app/resources/Application");
}
