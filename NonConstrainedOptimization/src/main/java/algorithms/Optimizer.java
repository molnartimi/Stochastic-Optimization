package algorithms;

import models.Model;
import spdn.SPDN;
import spdn.SPDNResult;

public abstract class Optimizer<HyperParam extends HyperParameters> {
	protected SPDN spdn;
	protected Model model;
	
	public Optimizer(Model model) {
		this.model = model;
		this.spdn = new SPDN(model);
	}
	
	public abstract SPDNResult optimize(HyperParam hyperParams);
}
