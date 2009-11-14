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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

class CsvDataBean extends DataBean {

	private final File	file;

	public CsvDataBean(
			File file)
			throws IOException,
			InputMismatchException {
		this.file = file;
		loadData();
	}

	@Override
	public String toString() {
		return file.getName();
	}

	private void loadData() throws IOException, InputMismatchException {
		FileReader reader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(reader);
		final Scanner scanner = new Scanner(bufferedReader);
		scanner.useDelimiter("\\s*(;\\s*|\\r\\n|\\n)");
		scanner.useLocale(Locale.ROOT);
		List<Double> numbers = new LinkedList<Double>();
		try {
			while (scanner.hasNext()) {
				numbers.add(scanner.nextDouble());
			}
		} finally {
			bufferedReader.close();
		}
		final Double[] tmp = new Double[numbers.size()];
		numbers.toArray(tmp);
		double[] result = new double[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = tmp[i];
		}
		setData(result);
	}
}
