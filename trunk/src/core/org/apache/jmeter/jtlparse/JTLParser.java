package org.apache.jmeter.jtlparse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * parse the jtl file
 * 
 * @author chenchao.yecc
 * @since jex001A
 */
public class JTLParser extends DefaultHandler {

	private String SPLIT = ",";
	private long maxRsTime = Long.MIN_VALUE;
	private long minRsTime = Long.MAX_VALUE;
	private double sumRsTime = 0;
	private double maxTps = Double.MIN_VALUE;
	private double minTps = Double.MAX_VALUE;
	private long firstTime = 0;
	private long endTime = 0;
	private long error = 0;
	private long count = 0;
	private File jtlFile = null;
	private File saveFile = null;
	boolean firstTimeInit = false;
	private List<Long> tmpList=new LinkedList<Long>();
	private Map<String,Integer> header=new HashMap<String,Integer>();
	private int tsPos=-1;
	private int rtPos=-1;
	private int resPos=-1;

	public void setJmeterLogFile(String path) {
		this.jtlFile = new File(path);
	}

	public void setSaveFile(String path) {
		this.saveFile = new File(path);
		File parent=saveFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		} 
		if (!saveFile.exists()) {
			saveFile=new File(path);
		} 
	}

	public void parse() throws Exception {
		// 判断文件种类
		FileReader fr=new FileReader(jtlFile.getAbsolutePath());
		char[] buf=new char[5];
		fr.read(buf);
		String sb=new StringBuilder().append(buf).toString();
		fr.close();
		// line文件，则开始解析
		if(!sb.toString().equals("<?xml")){
			// 初始化列的索引
			BufferedReader br=new BufferedReader(new FileReader(jtlFile.getAbsolutePath()));
			String line=null;
			line=br.readLine();
			String[] flds=line.split(SPLIT);
			// csv文件,取出表头
			for (int i = 0; i < flds.length; i++) {
				header.put(flds[i],i);
			}
			tsPos=header.get("timeStamp");
			rtPos=header.get("elapsed");
			resPos=header.get("success");
			
			while((line=br.readLine())!=null){
				analyseJtlNode(createJtlNodeForLine(line));
			}
			writeResultToTemplate();
		} else {

			// xml文件，开始解析
			SAXParserFactory sf = SAXParserFactory.newInstance();
			try {
				SAXParser sp = sf.newSAXParser();
				sp.parse(new InputSource(jtlFile.getAbsolutePath()), this);
				writeResultToTemplate();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeResultToTemplate() {
		BufferedWriter bw = null;
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(saveFile);
			bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);
			pw.println("采样次数：" + count);
			pw.println("平均响应时间：" + sumRsTime / count);
			pw.println("最大响应时间：" + maxRsTime);
			pw.println("最小响应时间：" + minRsTime);
			Collections.sort(tmpList);
			pw.println("50%响应时间：" + tmpList.get((int)(tmpList.size()*0.5)));
			pw.println("90%响应时间：" + tmpList.get((int)(tmpList.size()*0.9)));
			long howLongRunning = endTime - firstTime;
			double throughput = ((double) (count) / (double) howLongRunning) * 1000.0;
			pw.println("平均TPS：" + throughput);
			pw.println("最大TPS：" + maxTps);
			if (minTps < 0) {
				minTps = 0;
			}
			pw.println("最小TPS：" + minTps);
			java.text.DecimalFormat df = new java.text.DecimalFormat(
					"#0.0000000000");
			double rate = ((double) error / (double) count) * 100;
			pw.println("失败率：" + df.format(rate) + "%");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attrs) {
		JtlNode node = createJtlNode(attrs);
		analyseJtlNode(node);
	}

	private void analyseJtlNode(JtlNode node) {
		if (node.getState() == JtlNode.UNAVAILABLE) {
			return;
		}
		endTime = node.getTimeStamp();
		if (!firstTimeInit) {
			firstTime = endTime;
			firstTimeInit = true;
		}
		long howLongRunning = endTime - firstTime;
		count = count + 1;
		minRsTime = Math.min(node.getAttTime(), minRsTime);
		maxRsTime = Math.max(node.getAttTime(), maxRsTime);
		addSortedValue(node.getAttTime());
		sumRsTime = sumRsTime + (double) node.getAttTime();
		if (howLongRunning != 0) {
			double tps = ((double) count / (double) howLongRunning) * 1000.0;
			minTps = Math.min(tps, minTps);
			maxTps = Math.max(tps, maxTps);
		}
		if (!node.isSuccess()) {
			error = error + 1;
		}
	}

    private void addSortedValue(Long val) {
        	tmpList.add(val);
    }
    
	private JtlNode createJtlNodeForLine(String line) {
		String[] tmp = line.split(SPLIT);
		JtlNode node = new JtlNode();

		// 时间戳
		node.setTimeStamp(Long.parseLong(tmp[tsPos]));

		// 响应时间
		node.setAttTime(Long.parseLong(tmp[rtPos]));

		// 执行结果
		node.setSuccess((Boolean.parseBoolean(tmp[resPos])));
		return node;
	}
	
	private JtlNode createJtlNode(Attributes attrs) {
		long newValue;
		boolean newResult;
		JtlNode node = new JtlNode();
		int len = attrs.getLength();
		for (int i = 0; i < len; i++) {
			switch (attrs.getQName(i).hashCode()) {
			// t ATT_TIME
			case 116:
				newValue = Long.parseLong(attrs.getValue(i));
				node.setAttTime(newValue);
				break;
			// lt LATENCY
			case 3464:
//				newValue = Long.parseLong(attrs.getValue(i));
//				node.setLatency(newValue);
				break;
			// ts TIME_STAMP
			case 3711:
				newValue = Long.parseLong(attrs.getValue(i));
				node.setTimeStamp(newValue);
				break;
			// s SUCCESS
			case 115:
				newResult = Boolean.parseBoolean(attrs.getValue(i));
				node.setSuccess(newResult);
				break;
			// lb LABEL
			case 3446:
				break;
			// rc RESPONSE_CODE
			case 3633:
				break;
			// rm RESPONSE_MESSAGE
			case 3643:
				break;
			// tn THREADNAME
			case 3706:
				break;
			// dt DATA_TYPE
			case 3216:
				break;
			// by BYTES
			case 3159:
				break;
			// de ENCODING
			// case 3159:
			// break;
			// ec ERROR_COUNT
			// case 3159:
			// break;
			// hn HOSTNAME
			// case 3159:
			// break;
			// na ALL_THRDS
			// case 3159:
			// break;
			// ng GRP_THRDS
			// case 3159:
			// break;
			// rs RESPONSE_CODE_OLD
			// case 3159:
			// break;
			// sc SAMPLE_COUNT
			// case 3159:
			// break;
			default:
				node.setState(JtlNode.UNAVAILABLE);
				return node;
			}
		}
		return node;
	}

	private static class JtlNode {

		private static int UNAVAILABLE = -1;
		private static int AVAILABLE = 0;
		private int state = AVAILABLE;
		// t ATT_TIME
		private long attTime;
		// lt LATENCY
//		private long latency;
		// ts TIME_STAMP
		private long timeStamp;
		// s SUCCESS
		private boolean success = false;
		// lb LABEL
//		private String label;
//		// rc RESPONSE_CODE
//		private String responseCode;
//		// rm RESPONSE_MESSAGE
//		private String responseMessage;
//		// tn THREADNAME
//		private String threadName;
//		// dt DATA_TYPE
//		private String dataType;
//		// by BYTES
//		private long bytes;
//		
//		private long startTime;
//		
//		private long endTime;

		// de ENCODING
		// ec ERROR_COUNT
		// hn HOSTNAME
		// na ALL_THRDS
		// ng GRP_THRDS
		// rs RESPONSE_CODE_OLD
		// sc SAMPLE_COUNT
		
//		public long getStartTime() {
//			return startTime;
//		}
//
//		public void setStartTime(long startTime) {
//			this.startTime = startTime;
//		}
//
//		public long getEndTime() {
//			return endTime;
//		}
//
//		public void setEndTime(long endTime) {
//			this.endTime = endTime;
//		}
		
		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public long getAttTime() {
			return attTime;
		}

		public void setAttTime(long attTime) {
			this.attTime = attTime;
		}

//		public long getLatency() {
//			return latency;
//		}
//
//		public void setLatency(long latency) {
//			this.latency = latency;
//		}

		public long getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

//		public String getLabel() {
//			return label;
//		}
//
//		public void setLabel(String label) {
//			this.label = label;
//		}
//
//		public String getResponseCode() {
//			return responseCode;
//		}
//
//		public void setResponseCode(String responseCode) {
//			this.responseCode = responseCode;
//		}
//
//		public String getResponseMessage() {
//			return responseMessage;
//		}
//
//		public void setResponseMessage(String responseMessage) {
//			this.responseMessage = responseMessage;
//		}
//
//		public String getThreadName() {
//			return threadName;
//		}
//
//		public void setThreadName(String threadName) {
//			this.threadName = threadName;
//		}
//
//		public String getDataType() {
//			return dataType;
//		}
//
//		public void setDataType(String dataType) {
//			this.dataType = dataType;
//		}
//
//		public long getBytes() {
//			return bytes;
//		}
//
//		public void setBytes(long bytes) {
//			this.bytes = bytes;
//		}
	}

	public static void main(String[] args) throws Exception {
		Long start=System.currentTimeMillis();
		final JTLParser parser = new JTLParser();
//		parser.setJmeterLogFile("D:\\Tools\\jakarta-jmeter-2.3.4\\bin\\q20.jtl");
		parser.setJmeterLogFile("D:\\Project\\Jmeter-Ex\\res.xml");
		parser.setSaveFile("d:\\res.txt");
		parser.parse();
		System.out.println(System.currentTimeMillis()-start);
		System.out.println("Over");
	}
}
