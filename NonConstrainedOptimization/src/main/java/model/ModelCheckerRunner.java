package model;

import java.util.List;

public interface ModelCheckerRunner<R extends ModelCheckerResult> {
	void setTolerance(double tolerance);
	R run(List<Double> parameterValues);
}
