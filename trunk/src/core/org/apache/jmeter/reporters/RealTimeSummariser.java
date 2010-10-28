package org.apache.jmeter.reporters;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.visualizers.SummariserSamplingStatCalculator;

/**
 * 用于计算指定间隔时间内的响应时间，平方差，平方和等，并将结果输出至JmeterRealTime.tmp
 * @author chenchao.yecc
 * @version jex002A
 */
public class RealTimeSummariser extends Summariser {
	private static final long serialVersionUID = 1L;
	private transient volatile PrintWriter writer= null;
	private transient volatile PrintWriter labelWriter= null;
	private WriteTimer myTask=null;
	private Map<Integer,SummariserSamplingStatCalculator> labelMap = new HashMap<Integer,SummariserSamplingStatCalculator>();
	private Map<Integer,String> labelCode = new HashMap<Integer,String>();
	public RealTimeSummariser(String s) {
		super(s);
		try {
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("JmeterRealTime.tmp",
			        false)), SaveService.getFileEncoding("UTF-8")), true);
			labelWriter = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("JmeterSample.tmp",
					false)), SaveService.getFileEncoding("UTF-8")), true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		myTask = new WriteTimer();
		Timer timer = new Timer(true);
		timer.schedule(myTask,5000,5000);
	}

	public void sampleOccurred(SampleEvent e) {
		SampleResult s = e.getResult();
		// 取得Sample的名字
		String name = s.getSampleLabel();
		// 取得名字的hash码
		int code = name.hashCode();
		SummariserSamplingStatCalculator sv = labelMap.get(code);
		if (sv == null) {
			labelCode.put(code, name);
			sv = new SummariserSamplingStatCalculator(name);
			labelMap.put(code, sv);
			labelWriter.print(code);
			labelWriter.print(",");
			labelWriter.println(name);
			labelWriter.flush();
		}

		// 将新的结果加至SampleVisualizer
		synchronized (sv) {
			if (s != null) {
				sv.addSample(s);
			}
		}
	}
	
	public void testEnded(String host) {
		for (Iterator<Integer> iterator = labelMap.keySet().iterator(); iterator
				.hasNext();) {
			SummariserSamplingStatCalculator sv = labelMap.get(iterator.next());
			synchronized (sv) {
				writer.println(format(sv));
			}
		}
		writer.flush();
		writer.close();
		labelWriter.close();
		myTask.cancel();
}
    
	protected String format(SummariserSamplingStatCalculator sv) {
		StringBuilder sb = new StringBuilder();
		synchronized (sv) {
			// 时间戳
			sb.append(System.currentTimeMillis()).
			// 标签
			append(",").append(sv.getLabel().hashCode()).
			// 个数
			append(",").append(sv.getCount()).
			// 平均响应时间
			append(",").append(sv.getMean()).
			// 最大响应时间
			append(",").append(sv.getMax()).
			// 最小响应时间
			append(",").append(sv.getMin()).
			// 错误数
			append(",").append(sv.getErrorCount()).
			// 标准方差
			append(",").append(sv.getStandardDeviation()).
			// 平方和
			append(",").append(sv.getSqurSum());
		}
		return sb.toString();
	}
    
	private class WriteTimer extends TimerTask {

		@Override
		public void run() {
			for (Iterator<Integer> iterator = labelMap.keySet().iterator(); iterator
					.hasNext();) {
				SummariserSamplingStatCalculator sv = labelMap.get(iterator
						.next());
				synchronized (sv) {
					writer.println(format(sv));
					sv.clear();
				}
			}
			writer.flush();
		}
	}
}
