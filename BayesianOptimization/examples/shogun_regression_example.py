# http://www.shogun-toolbox.org/notebook/latest/gaussian_processes.html
"""Example of how to use Shogun Machine learning toolbox for Bayesian Regression with Gaussian Processes"""

from modshogun import *
import numpy as np
import matplotlib.pyplot as plt
from math import exp
import time



# How to make a well known gaussian bell curve
def gaussian_likelihood_example():
    # noise level
    sigma = 1

    # likelihood instance - well known bell curve
    lik = GaussianLikelihood()

    # A set of labels to consider, from-to-num
    lab = RegressionLabels(np.linspace(-4.0, 4.0, 200))

    # A single 1D Gaussian response function, repeated once for each label
    # this avoids doing a loop in python which would be slow
    F = np.zeros(lab.get_num_labels())

    # set observation noise, this is squared internally
    lik.set_sigma(sigma)
    # compute log-likelihood for all labels
    log_liks = lik.get_log_probability_f(lab, F)

    # plot likelihood function, exponentiate since they were computed in log-domain
    plt.plot(lab.get_labels(), map(exp, log_liks))
    plt.show()

# How to get random functions from the GP posterior
def functions_from_gp_example():
    X_train, y_train, X_test, y_test = _generate_regression_toy_data()

    # bring data into shogun representation (features are 2d-arrays, organised as column vectors)
    feats_train = RealFeatures(X_train.reshape(1, len(X_train)))
    feats_test = RealFeatures(X_test.reshape(1, len(X_test)))
    labels_train = RegressionLabels(y_train)

    # compute covariances for different kernel parameters
    tau = 4

    plt.figure(figsize=(16, 5))
    plt.suptitle("Random Samples from GP prior")
    # create inference method instance with very small observation noise to make
    kernel = GaussianKernel(10, tau)
    mean = ZeroMean()
    gauss = GaussianLikelihood()
    inf = ExactInferenceMethod(kernel, feats_train, mean, labels_train, gauss)
    C_post = inf.get_posterior_covariance()
    m_post = inf.get_posterior_mean()

    # sample a bunch of latent functions from the Gaussian Process
    # note these vectors are stored row-wise
    F = Statistics.sample_from_gaussian(m_post, C_post, 5)

    for j in range(len(F)):
        # sort points to connect the dots with lines
        # sort points to connect the dots with lines
        sorted_idx = sorted(range(len(X_train)), key=lambda x: X_train[x])
        plt.plot(X_train[sorted_idx], F[j, sorted_idx], '-', markersize=6)
        plt.plot(X_train, y_train, 'ro')
    plt.show()

# How to plot the discrtibutions over the GP
def distributions_over_gp_example():
    X_train, y_train, X_test, y_test = _generate_regression_toy_data()

    # bring data into shogun representation (features are 2d-arrays, organised as column vectors)
    feats_train = RealFeatures(X_train.reshape(1, len(X_train)))
    feats_test = RealFeatures(X_test.reshape(1, len(X_test)))
    labels_train = RegressionLabels(y_train)

    # compute covariances for different kernel parameters
    tau = 4

    plt.figure(figsize=(16, 5))
    plt.suptitle("Random Samples from GP prior")
    # create inference method instance with very small observation noise to make
    kernel = GaussianKernel(10, tau)
    mean = ZeroMean()
    gauss = GaussianLikelihood()
    inf = ExactInferenceMethod(kernel, feats_train, mean, labels_train, gauss)

    plt.figure(figsize=(18, 10))

    # create GP instance using inference method and train
    # use Shogun objects from above
    #inf.set_kernel(GaussianKernel(10, tau))
    gp = GaussianProcessRegression(inf)

    gp.train()
    # predict labels for all test data (note that this produces the same as the below mean vector)
    #means = gp.apply(feats_test)

    # extract means and variance of predictive distribution for all test points
    means = gp.get_mean_vector(feats_test)
    variances = gp.get_variance_vector(feats_test)
    # note: y_predicted == means

    # plot predictive distribution and training data
    _plot_predictive_regression(X_train, y_train, X_test, y_test, means, variances)

# How to calculate the best hiperparams for your model
def calculate_best_hiperparams_example():
    X_train, y_train, X_test, y_test = _generate_regression_toy_data()

    # bring data into shogun representation (features are 2d-arrays, organised as column vectors)
    feats_train = RealFeatures(X_train.reshape(1, len(X_train)))
    feats_test = RealFeatures(X_test.reshape(1, len(X_test)))
    labels_train = RegressionLabels(y_train)

    tau = 4

    # re-create inference method and GP instance to start from scratch, use other Shogun structures from above
    kernel = GaussianKernel(10, tau)
    mean = ZeroMean()
    gauss = GaussianLikelihood()
    inf = ExactInferenceMethod(kernel, feats_train, mean, labels_train, gauss)
    gp = GaussianProcessRegression(inf)

    # evaluate our inference method for its derivatives
    gradcalc = GradientCriterion()
    grad = GradientEvaluation(gp, feats_train, labels_train, gradcalc, False)
    grad.set_function(inf)

    # handles all of the above structures in memory
    grad_search = GradientModelSelection(grad)

    # search for best parameters and store them
    best_combination = grad_search.select_model()

    # apply best parameters to GP, train
    best_combination.apply_to_machine(gp)

    # we have to "cast" objects to the specific kernel interface we used (soon to be easier)
    best_width = GaussianKernel.obtain_from_generic(inf.get_kernel()).get_width()
    best_scale = inf.get_scale()
    best_sigma = GaussianLikelihood.obtain_from_generic(inf.get_model()).get_sigma()

    print("Selected tau (kernel bandwidth):", best_width)
    print("Selected gamma (kernel scaling):", best_scale)
    print("Selected sigma (observation noise):", best_sigma)

    # train gp
    gp.train()

    # extract means and variance of predictive distribution for all test points
    means = gp.get_mean_vector(feats_test)
    variances = gp.get_variance_vector(feats_test)

    # plot predictive distribution
    plt.figure(figsize=(18, 5))
    _plot_predictive_regression(X_train, y_train, X_test, y_test, means, variances)
    _ = plt.title("Maximum Likelihood II based inference")

# helper functions for generate a sin function
def _generate_regression_toy_data(n=50, n_test=100, x_range=15, x_range_test=20, noise_var=0.4):
    # training and test sine wave (test is the real model), test one has more points
    X_train = np.random.rand(n) * x_range
    X_test = np.linspace(0, x_range_test, 500)

    # add noise to training observations
    y_test = np.sin(X_test)
    y_train = np.sin(X_train) + np.random.randn(n) * 0.2

    return X_train, y_train, X_test, y_test


# helper function that plots predictive distribution and data
def _plot_predictive_regression(X_train, y_train, X_test, y_test, means, variances):
    # evaluate predictive distribution in this range of y-values and preallocate predictive distribution
    y_values = np.linspace(-3, 3)
    D = np.zeros((len(y_values), len(X_test)))

    # evaluate normal distribution at every prediction point (column)
    print(np.shape(D))
    for j in range(np.shape(D)[1]):
        # create gaussian distribution instance, expects mean vector and covariance matrix, reshape
        gauss = GaussianDistribution(np.array(means[j]).reshape(1, ), np.array(variances[j]).reshape(1, 1))
        # evaluate predictive distribution for test point, method expects matrix
        D[:, j] = np.exp(gauss.log_pdf_multiple(y_values.reshape(1, len(y_values))))

    plt.pcolor(X_test, y_values, D)
    plt.colorbar()
    plt.contour(X_test, y_values, D)
    plt.plot(X_test, y_test, 'b', linewidth=3)
    plt.plot(X_test, means, 'm--', linewidth=3)
    plt.plot(X_train, y_train, 'ro')
    plt.legend(["Truth", "Prediction", "Data"])
    plt.show()

def large_scale_regression():
    X_train, y_train, X_test, y_test = _generate_regression_toy_data()
    feats_train = RealFeatures(X_train.reshape(1, len(X_train)))
    labels_train = RegressionLabels(y_train)
    # re-create inference method and GP instance to start from scratch, use other Shogun structures from above
    kernel = GaussianKernel(10, 4)
    mean = ZeroMean()
    gauss = GaussianLikelihood()
    inf = ExactInferenceMethod(kernel, feats_train, mean, labels_train, gauss)
    gp = GaussianProcessRegression(inf)
    gradcalc = GradientCriterion()
    grad = GradientEvaluation(gp, feats_train, labels_train, gradcalc, False)
    grad.set_function(inf)
    grad_search = GradientModelSelection(grad)
    best_combination = grad_search.select_model()
    best_combination.apply_to_machine(gp)

    # we have to "cast" objects to the specific kernel interface we used (soon to be easier)
    best_width = GaussianKernel.obtain_from_generic(inf.get_kernel()).get_width()
    best_scale = inf.get_scale()
    best_sigma = GaussianLikelihood.obtain_from_generic(inf.get_model()).get_sigma()


    # simple regression data
    X_train, y_train, X_test, y_test = _generate_regression_toy_data(n=1000)

    # bring data into shogun representation (features are 2d-arrays, organised as column vectors)
    feats_train = RealFeatures(X_train.reshape(1, len(X_train)))
    feats_test = RealFeatures(X_test.reshape(1, len(X_test)))
    labels_train = RegressionLabels(y_train)

    # inducing features (here: a random grid over the input space, try out others)
    n_inducing = 10
    # X_inducing=linspace(X_train.min(), X_train.max(), n_inducing)
    X_inducing = np.random.rand(int(np.asscalar(X_train.min() + n_inducing))) * X_train.max()
    feats_inducing = RealFeatures(X_inducing.reshape(1, len(X_inducing)))

    # create FITC inference method and GP instance
    inf = FITCInferenceMethod(GaussianKernel(10, best_width), feats_train, ZeroMean(), labels_train, \
                              GaussianLikelihood(best_sigma), feats_inducing)
    gp = GaussianProcessRegression(inf)

    start = time.time()
    gp.train()
    means = gp.get_mean_vector(feats_test)
    variances = gp.get_variance_vector(feats_test)
    print("FITC inference took %.2f seconds" % (time.time() - start))

    # exact GP
    start = time.time()
    inf_exact = ExactInferenceMethod(GaussianKernel(10, best_width), feats_train, ZeroMean(), labels_train, \
                                     GaussianLikelihood(best_sigma))
    inf_exact.set_scale(best_scale)
    gp_exact = GaussianProcessRegression(inf_exact)
    gp_exact.train()
    means_exact = gp_exact.get_mean_vector(feats_test)
    variances_exact = gp_exact.get_variance_vector(feats_test)
    print("Exact inference took %.2f seconds" % (time.time() - start))

    # comparison plot FITC and exact inference, plot 95% confidence of both predictive distributions
    plt.figure(figsize=(18, 5))
    plt.plot(X_test, y_test, color="black", linewidth=3)
    plt.plot(X_test, means, 'r--', linewidth=3)
    plt.plot(X_test, means_exact, 'b--', linewidth=3)
    plt.plot(X_train, y_train, 'ro')
    plt.plot(X_inducing, np.zeros(len(X_inducing)), 'g*', markersize=15)

    # tube plot of 95% confidence
    error = 1.96 * np.sqrt(variances)
    plt.plot(X_test, means - error, color='red', alpha=0.3, linewidth=3)
    plt.fill_between(X_test, means - error, means + error, color='red', alpha=0.3)
    error_exact = 1.96 * np.sqrt(variances_exact)
    plt.plot(X_test, means_exact - error_exact, color='blue', alpha=0.3, linewidth=3)
    plt.fill_between(X_test, means_exact - error_exact, means_exact + error_exact, color='blue', alpha=0.3)

    # plot upper confidence lines later due to legend
    plt.plot(X_test, means + error, color='red', alpha=0.3, linewidth=3)
    plt.plot(X_test, means_exact + error_exact, color='blue', alpha=0.3, linewidth=3)

    plt.legend(["True", "FITC prediction", "Exact prediction", "Data", "Inducing points", "95% FITC", "95% Exact"])
    _ = plt.title("Comparison FITC and Exact Regression")
    plt.show()


if __name__ == '__main__':
    # You can try only one of these at a time because of the plotting

    #gaussian_likelihood_example()
    #functions_from_gp_example()
    #distributions_over_gp_example()
    #calculate_best_hiperparams_example()
    large_scale_regression()


