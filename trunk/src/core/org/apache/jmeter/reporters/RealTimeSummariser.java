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
 * 
 * @author chenchao.yecc
 * @version jex002A
 */
public class RealTimeSummariser extends Summariser {
	private static final long serialVersionUID = 1L;
	private transient volatile PrintWriter writer= null;
	private WriteTimer myTask=null;
	private Map<String,SummariserSamplingStatCalculator> labelMap = new HashMap<String,SummariserSamplingStatCalculator>();
	public RealTimeSummariser(String s) {
		super(s);
		try {
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("JmeterDetail",
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
		SummariserSamplingStatCalculator sv = labelMap.get(s.getSampleLabel());
		if (sv==null){
			sv = new SummariserSamplingStatCalculator(s.getSampleLabel());
			labelMap.put(s.getSampleLabel(), sv);
		}

		// 将新的结果加至SampleVisualizer
		synchronized (sv) {
			if (s != null) {
				sv.addSample(s);
			}
		}
	}
	
	public void testEnded(String host) {
		for (Iterator<String> iterator = labelMap.keySet().iterator(); iterator
				.hasNext();) {
			SummariserSamplingStatCalculator sv = labelMap.get(iterator.next());
			synchronized (sv) {
				writer.println(format(sv));
			}
		}
		writer.flush();
		writer.close();
		myTask.cancel();
}
    
	protected String format(SummariserSamplingStatCalculator sv) {
		StringBuilder sb = new StringBuilder();
		synchronized (sv) {
			// 时间戳
			sb.append(System.currentTimeMillis()).
			// 标签
			append(",").append(sv.getLabel()).
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
			append(",").append(sv.getStandardDeviation())
			// 平方和
			.append(",").append(sv.getSqurSum());
		}
		return sb.toString();
	}
    
	private class WriteTimer extends TimerTask {

		@Override
		public void run() {
			for (Iterator<String> iterator = labelMap.keySet().iterator(); iterator
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
