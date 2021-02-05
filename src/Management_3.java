import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Management_3 extends JFrame {
	private LinkedList<Double> fifo;

	public Management_3() {
	}
	
	public Management_3(LinkedList<Double> fifo) {
		this.fifo = fifo;
	}

	MySwingWorker mySwingWorker;
	SwingWrapper<XYChart> sw;
	XYChart chart;

	public static void main(String[] args) throws Exception {

		 Management_3 swingWorkerRealTime = new Management_3();
		swingWorkerRealTime.go();
	}

	void go() {

		// Create Chart
		chart = QuickChart.getChart("실시간 CPU 사용량", "Time", "CPU ( 0 ~ 100 % )", "randomWalk", new double[] { 0 },
				new double[] { 0 });
		chart.getStyler().setLegendVisible(false);
		chart.getStyler().setXAxisTicksVisible(false);
		chart.getStyler().setYAxisMax(100.0);
		chart.getStyler().setYAxisMin(0.0);
		chart.getStyler().setYAxisTicksVisible(false);

		// Show it
		sw = new SwingWrapper<XYChart>(chart);
		sw.displayChart();

		mySwingWorker = new MySwingWorker();
		mySwingWorker.execute();
	}

	private class MySwingWorker extends SwingWorker<Boolean, double[]> {

		public MySwingWorker() {
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			//fifo = Main.getSocketList().get(Management_2.getSelectedInstanceNumber()).getCpuHistory();
			while (!isCancelled()) {
				double[] array = new double[fifo.size()];
				for (int i = 0; i < fifo.size(); i++) {
					array[i] = fifo.get(i);
				}
				publish(array);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// eat it. caught when interrupt is called
					System.out.println("MySwingWorker shut down.");
				}
			}

			return true;
		}

		@Override
		protected void process(List<double[]> chunks) {

			System.out.println("number of chunks: " + chunks.size());

			double[] mostRecentDataSet = chunks.get(chunks.size() - 1);

			chart.updateXYSeries("randomWalk", null, mostRecentDataSet , null);
			sw.repaintChart();

			long start = System.currentTimeMillis();
			long duration = System.currentTimeMillis() - start;
			try {
				Thread.sleep(40 - duration); // 40 ms ==> 25fps
				// Thread.sleep(400 - duration); // 40 ms ==> 2.5fps
			} catch (InterruptedException e) {
				System.out.println("InterruptedException occurred.");
			}
		}
		
		protected void done() {
			System.out.println("Hi");
			Management_2.t.interrupt();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}