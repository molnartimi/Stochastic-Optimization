package algorithms;

import spdn.SPDNResult;
import spdn.SpdnModel;
import spdn.SpdnModelAnalyzer;

public abstract class Optimizer<HyperParam extends HyperParameters> {
	protected SpdnModelAnalyzer spdn;
	protected SpdnModel model;
	
	public Optimizer(SpdnModel model) {
		this.model = model;
		this.spdn = new SpdnModelAnalyzer(model);
	}
	
	public abstract SPDNResult optimize(HyperParam hyperParams);
}
