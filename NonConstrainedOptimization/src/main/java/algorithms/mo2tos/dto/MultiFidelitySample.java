package algorithms.mo2tos.dto;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.Sample;

public class MultiFidelitySample extends Sample<List<Double>> {
	public final List<Double> point;
	public final double lowResult;
	private Double heighResult;
	private final static Logger logger = LoggerFactory.getLogger(MultiFidelitySample.class);
	
	public MultiFidelitySample(List<Double> point, double lowResult) {
		super(point, lowResult);
		this.point = point;
		this.lowResult = lowResult;
	}

	@Override
	public int compareTo(Sample<List<Double>> o) {
		double diff = this.lowResult - ((MultiFidelitySample) o).lowResult;
		return (diff > 0) ? 1 : (diff < 0) ?  -1 : 0;
	}
	
	public void setHeighResult(double heighResult) {
		logger.info("Sample: " + point.toString() + " => " + heighResult);
		this.heighResult = heighResult;
		
	}
	
	public double getHeighResult() {
		return this.heighResult;
	}
	
	public boolean isComputed() {
		return heighResult != null;
	}
}
