package biz.pavonis.hexameter.example;

import java.text.NumberFormat;

import biz.pavonis.hexameter.HexagonGridLayout;
import biz.pavonis.hexameter.HexagonOrientation;
import biz.pavonis.hexameter.HexagonalGridBuilder;

public class Benchmark {

	private static final NumberFormat nf = NumberFormat.getInstance();
	static {
		nf.setMinimumFractionDigits(1);
		nf.setMaximumFractionDigits(1);
		nf.setGroupingUsed(false);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int size = 100; size < 5000; size += 100) {
			Long start = System.nanoTime();
			new HexagonalGridBuilder().setGridWidth(size).setGridHeight(size).setRadius(20).setOrientation(HexagonOrientation.POINTY_TOP).setGridLayout(HexagonGridLayout.RECTANGULAR)
					.build();
			double timeMs = (System.nanoTime() - start) / 1000 / 1000;
			System.out.println(size + ";" + nf.format(timeMs) + ";" + nf.format(timeMs / size));
		}

	}
}
