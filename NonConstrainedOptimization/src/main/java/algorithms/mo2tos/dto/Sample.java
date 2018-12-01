package algorithms.mo2tos.dto;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sample implements Comparable<Sample>{
	public final List<Double> values;
	public final double lowResult;
	private Double heighResult;
	private final static Logger logger = LoggerFactory.getLogger(Sample.class);
	
	public Sample(List<Double> values, double lowResult) {
		this.values = values;
		this.lowResult = lowResult;
	}

	@Override
	public int compareTo(Sample o) {
		double diff = this.lowResult - o.lowResult;
		return (diff > 0) ? 1 : (diff < 0) ?  -1 : 0;
	}
	
	public void setHeighResult(double heighResult) {
		logger.info("Sample: " + values.toString() + " => " + heighResult);
		this.heighResult = heighResult;
		
	}
	
	public double getHeighResult() {
		return this.heighResult;
	}
	
	public boolean isComputed() {
		return heighResult != null;
	}
}
