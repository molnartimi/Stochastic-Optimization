package algorithms.bayes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.Sample;
import algorithms.ToleranceExceededException;

public class GaussProcess {
	
	protected static final Logger logger = LoggerFactory.getLogger(GaussProcess.class);
	
	private List<Sample<List<Double>>> samples = new ArrayList<Sample<List<Double>>>();
	private Sample<List<Double>> bestPosition;
	
	private RealVector valueVector;
	private List<Double> valueList = new ArrayList<Double>();
	private List<List<Double>> vectorList = new ArrayList<List<Double>>();
	private RealMatrix kernelMatrixInverse;
	private RealMatrix sampleMatrix;
	
	private double alpha0;
	private RealVector alpha;
	private double tolerance;
	
	public GaussProcess(double alpha0, List<Double> alpha, double tolerance) {
		this.alpha0 = alpha0;
		this.alpha = listToVector(alpha);
		this.tolerance = tolerance;
	}
	
	public Sample<List<Double>> getBestPosition() {
		return bestPosition;
	}

	public void updateWithSample(Sample<List<Double>> point) throws ToleranceExceededException {
		samples.add(point);
		vectorList.add(point.point);
		
		if (bestPosition == null || point.compareTo(bestPosition) < 0) {
			bestPosition = point;
			logger.info("New best value " + bestPosition.value + " in point " + bestPosition.point.toString());
			if (bestPosition.value < tolerance) throw new ToleranceExceededException();
		}
		
		valueList.add(point.value);
		valueVector = listToVector(valueList);
		
		updateSampleMatrix();
		kernelMatrixInverse = new LUDecomposition(getKernelMatrix(sampleMatrix, sampleMatrix)).getSolver().getInverse();
	}
	
	public double getMean(RealVector point) {
		RealMatrix rowMtx = getKernelMatrix(MatrixUtils.createColumnRealMatrix(point.toArray()), sampleMatrix);
		RealMatrix productMtx = rowMtx.multiply(kernelMatrixInverse);
		assert productMtx.getRowDimension() == 1;
		RealVector productVector = productMtx.getRowVector(0);
		return productVector.dotProduct(valueVector);
	}
	
	public double getVariance(RealVector point) {
		RealMatrix productRowMtx = getKernelMatrix(MatrixUtils.createColumnRealMatrix(point.toArray()), sampleMatrix).multiply(kernelMatrixInverse);
		assert productRowMtx.getRowDimension() == 1;
		RealVector productVector = productRowMtx.getRowVector(0);
		
		RealMatrix colKernelMtx = getKernelMatrix(sampleMatrix, MatrixUtils.createColumnRealMatrix(point.toArray()));
		assert colKernelMtx.getColumnDimension() == 1;
		RealVector colKernelVector = colKernelMtx.getColumnVector(0);

		double squareVariance = gaussianKernel(point, point) - productVector.dotProduct(colKernelVector);
		return Math.sqrt(squareVariance);
	}
	
	private RealMatrix getKernelMatrix(RealMatrix m1, RealMatrix m2) {
		int n = m1.getColumnDimension();
		int m = m2.getColumnDimension();
		double[][] primitiveArray = new double[n][m];
		for (int i = 0; i < n; i++) {
			RealVector col = m1.getColumnVector(i);
			for (int j = 0; j < m; j++) {
				primitiveArray[i][j] = gaussianKernel(col, m2.getColumnVector(j));
			}
			
			// to improve the numerical stability
			if (i < m) primitiveArray[i][i] += 1e-6;
		}
		return MatrixUtils.createRealMatrix(primitiveArray);
	}
	
	private double gaussianKernel(RealVector p1, RealVector p2) {
		RealVector p1MinusP2 = p1.subtract(p2);
		RealVector alphaMultiplyDiff = p1MinusP2.copy();
		for (int i = 0; i < p1.getDimension(); i++) {
			alphaMultiplyDiff.setEntry(i, p1MinusP2.getEntry(i) * alpha.getEntry(i));
		}
		double norm = p1MinusP2.dotProduct(alphaMultiplyDiff);
		return alpha0 * Math.exp(-norm);
	}
	
	private RealVector listToVector(List<Double> point) {
		return MatrixUtils.createRealVector(ArrayUtils.toPrimitive(point.toArray(new Double[0])));
	}
	
	private void updateSampleMatrix() {
		int n = vectorList.size();
		int d = vectorList.get(0).size();
		double[][] primitiveArray = new double[d][n];
		for (int i = 0; i < n; i++) {
			List<Double> l = vectorList.get(i);
			for (int j = 0; j < d; j++) {
				primitiveArray[j][i] = l.get(j);
			}
		}
		sampleMatrix = MatrixUtils.createRealMatrix(primitiveArray);
	}
}
