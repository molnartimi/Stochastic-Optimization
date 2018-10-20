package algorithms;

import spdn.SPDNResult;
import spdn.analyzer.SpdnModelAnalyzer;
import spdn.model.SpdnModel;

public abstract class Optimizer<HyperParam extends HyperParameters> {
	protected SpdnModelAnalyzer spdn;
	protected SpdnModel model;
	
	public Optimizer(SpdnModel model) {
		this.model = model;
		this.spdn = new SpdnModelAnalyzer(model);
	}
	
	public abstract SPDNResult optimize(HyperParam hyperParams);
}
