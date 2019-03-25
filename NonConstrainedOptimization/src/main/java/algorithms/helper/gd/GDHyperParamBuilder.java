package algorithms.helper.gd;

import org.apache.commons.math3.linear.RealVector;

import algorithms.HyperParamsBuilder;
import model.Model;

public class GDHyperParamBuilder extends HyperParamsBuilder {
	private double _gamma = 1;
	private double _tolerance = 0.001;
	private RealVector _initPoint;
	private int _restart = 0;
	
	public GDHyperParamBuilder(Model model) {
		_initPoint = model.randomVector();
	}
	
	public GDHyperParamBuilder(RealVector initPoint) {
		this._initPoint = initPoint;
	}
	
	@Override
	public GDHyperParam build() {
		return new GDHyperParam(_gamma, _tolerance, _initPoint, _restart);
	}
	
	public GDHyperParamBuilder gamma(double gamma) {
		_gamma = gamma;
		return this;
	}
	
	public GDHyperParamBuilder tolerance(double tolerance) {
		_tolerance = tolerance;
		return this;
	}

	public GDHyperParamBuilder initPoint(RealVector initPoint) {
		_initPoint = initPoint;
		return this;
	}
	
	public GDHyperParamBuilder restart(int restart) {
		_restart = restart;
		return this;
	}

}
