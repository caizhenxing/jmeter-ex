package org.apache.jmeter.engine;

/**
 * 用于多次执行测试清理现场
 * @author chenchao.yecc
 * @since jex005A
 *
 */
public interface JmeterLoopTestContext {
	public void setUpTest();
	public void tearDownTest();
}
