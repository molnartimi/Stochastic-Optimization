# http://www.shogun-toolbox.org/notebook/latest/gaussian_processes.html
"""Example of how to use Shogun Machine learning toolbox for Bayesian Classification with Gaussian Processes"""

from modshogun import *
import numpy as np
import matplotlib.pyplot as plt
# for building combinations of arrays
from itertools import product
# for measuring runtime
import time

def plot_class_likelihoods():
    # two classification likelihoods in Shogun
    logit = LogitLikelihood()
    probit = ProbitLikelihood()

    # A couple of Gaussian response functions, 1-dimensional here
    F = np.linspace(-5.0, 5.0)

    # Single observation label with +1
    lab = BinaryLabels(np.array([1.0]))

    # compute log-likelihood for all values in F
    log_liks_logit = np.zeros(len(F))
    log_liks_probit = np.zeros(len(F))
    for i in range(len(F)):
        # Shogun expects a 1D array for f, not a single number
        f = np.array(F[i]).reshape(1, )
        log_liks_logit[i] = logit.get_log_probability_f(lab, f)
        log_liks_probit[i] = probit.get_log_probability_f(lab, f)

    # in fact, loops are slow and Shogun offers a method to compute the likelihood for many f. Much faster!
    log_liks_logit = logit.get_log_probability_fmatrix(lab, F.reshape(1, len(F)))
    log_liks_probit = probit.get_log_probability_fmatrix(lab, F.reshape(1, len(F)))

    # plot the sigmoid functions, note that Shogun computes it in log-domain, so we have to exponentiate
    plt.figure(figsize=(12, 4))
    plt.plot(F, np.exp(log_liks_logit))
    plt.plot(F, np.exp(log_liks_probit))
    plt.ylabel("$p(y_i|f_i)$")
    plt.xlabel("$f_i$")
    plt.title("Classification Likelihoods")
    _ = plt.legend(["Logit", "Probit"])
    plt.show()

def generate_classification_toy_data(n_train=100, mean_a=np.asarray([0, 0]), std_dev_a=1.0, mean_b=3, std_dev_b=0.5):
    # positive examples are distributed normally
    X1 = (np.random.randn(n_train, 2)*std_dev_a+mean_a).T

    # negative examples have a "ring"-like form
    r = np.random.randn(n_train)*std_dev_b+mean_b
    angle = np.random.randn(n_train)*2*np.pi
    X2 = np.array([r*np.cos(angle)+mean_a[0], r*np.sin(angle)+mean_a[1]])

    # stack positive and negative examples in a single array
    X_train = np.hstack((X1,X2))

    # label positive examples with +1, negative with -1
    y_train = np.zeros(n_train*2)
    y_train[:n_train] = 1
    y_train[n_train:] = -1

    return X_train, y_train

def plot_binary_data(X_train, y_train):
    plt.plot(X_train[0, np.argwhere(y_train == 1)], X_train[1, np.argwhere(y_train == 1)], 'ro')
    plt.plot(X_train[0, np.argwhere(y_train == -1)], X_train[1, np.argwhere(y_train == -1)], 'bo')

def plot_classification_toy_data():
    X_train, y_train = generate_classification_toy_data()
    plot_binary_data(X_train, y_train)
    _ = plt.title("2D Toy classification problem")
    plt.show()

def plot_decision_classes():
    X_train, y_train = generate_classification_toy_data()
    # convert training data into Shogun representation
    train_features = RealFeatures(X_train)
    train_labels = BinaryLabels(y_train)

    # generate all pairs in 2d range of testing data (full space), discretisation resultion is n_test
    n_test = 50
    x1 = np.linspace(X_train[0, :].min() - 1, X_train[0, :].max() + 1, n_test)
    x2 = np.linspace(X_train[1, :].min() - 1, X_train[1, :].max() + 1, n_test)
    X_test = np.asarray(list(product(x1, x2))).T

    # convert testing features into Shogun representation
    test_features = RealFeatures(X_test)

    # create Gaussian kernel with width = 2.0
    kernel = GaussianKernel(10, 2)

    # create zero mean function
    zero_mean = ZeroMean()

    # you can easily switch between probit and logit likelihood models
    # by uncommenting/commenting the following lines:

    # create probit likelihood model
    #lik = ProbitLikelihood()

    # create logit likelihood model
    lik = LogitLikelihood()

    # you can easily switch between Laplace and EP approximation by
    # uncommenting/commenting the following lines:

    # specify Laplace approximation inference method
    inf = SingleLaplaceInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # specify EP approximation inference method
    #inf = EPInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # EP might not converge, we here allow that without errors
    #inf.set_fail_on_non_convergence(False)

    # create and train GP classifier, which uses Laplace approximation
    gp = GaussianProcessClassification(inf)
    gp.train()

    test_labels = gp.apply(test_features)

    # plot data and decision boundary
    plot_binary_data(X_train, y_train)
    plt.pcolor(x1, x2, test_labels.get_labels().reshape(n_test, n_test))
    _ = plt.title('Decision boundary')
    plt.show()

def plot_decision_distributions():
    X_train, y_train = generate_classification_toy_data()
    # convert training data into Shogun representation
    train_features = RealFeatures(X_train)
    train_labels = BinaryLabels(y_train)

    # generate all pairs in 2d range of testing data (full space), discretisation resultion is n_test
    n_test = 50
    x1 = np.linspace(X_train[0, :].min() - 1, X_train[0, :].max() + 1, n_test)
    x2 = np.linspace(X_train[1, :].min() - 1, X_train[1, :].max() + 1, n_test)
    X_test = np.asarray(list(product(x1, x2))).T

    # create Gaussian kernel with width = 2.0
    kernel = GaussianKernel(10, 2)
    zero_mean = ZeroMean()


    # create probit likelihood model
    # lik = ProbitLikelihood()

    # create logit likelihood model
    lik = LogitLikelihood()


    # specify Laplace approximation inference method
    inf = SingleLaplaceInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # specify EP approximation inference method
    # inf = EPInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # EP might not converge, we here allow that without errors
    # inf.set_fail_on_non_convergence(False)

    gp = GaussianProcessClassification(inf)
    gp.train()

    # convert testing features into Shogun representation
    test_features = RealFeatures(X_test)
    # obtain probabilities for
    p_test = gp.get_probabilities(test_features)

    # create figure
    plt.title('Training data, predictive probability and decision boundary')

    # plot training data
    plot_binary_data(X_train, y_train)

    # plot decision boundary
    plt.contour(x1, x2, np.reshape(p_test, (n_test, n_test)), levels=[0.5], colors=('black'))

    # plot probabilities
    plt.pcolor(x1, x2, p_test.reshape(n_test, n_test))
    _ = plt.colorbar()
    plt.show()

def plot_marginal_likelihood():
    X_train, y_train = generate_classification_toy_data()

    train_features = RealFeatures(X_train)
    train_labels = BinaryLabels(y_train)

    n_test = 50
    x1 = np.linspace(X_train[0, :].min() - 1, X_train[0, :].max() + 1, n_test)
    x2 = np.linspace(X_train[1, :].min() - 1, X_train[1, :].max() + 1, n_test)
    X_test = np.asarray(list(product(x1, x2))).T

    test_features = RealFeatures(X_test)

    kernel = GaussianKernel(10, 2)
    zero_mean = ZeroMean()

    # lik = ProbitLikelihood()
    lik = LogitLikelihood()

    inf = SingleLaplaceInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)
    # inf = EPInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)
    # EP might not converge, we here allow that without errors
    # inf.set_fail_on_non_convergence(False)

    gp = GaussianProcessClassification(inf)
    gp.train()

    # generate some non-negative kernel widths
    widths = 2 ** np.linspace(-5, 6, 20)

    # compute marginal likelihood under Laplace apprixmation for every width
    # use Shogun objects from above
    marginal_likelihoods = np.zeros(len(widths))
    for i in range(len(widths)):
        # note that GP training is automatically done/updated if a parameter is changed. No need to call train again
        kernel.set_width(widths[i])
        marginal_likelihoods[i] = -inf.get_negative_log_marginal_likelihood()

    # plot marginal likelihoods as a function of kernel width
    plt.plot(np.log2(widths), marginal_likelihoods)
    plt.title("Log Marginal likelihood for different kernels")
    plt.xlabel("Kernel Width in log-scale")
    _ = plt.ylabel("Log-Marginal Likelihood")
    plt.show()

    print("Width with largest marginal likelihood:", widths[marginal_likelihoods.argmax()])

def calculate_best_hyperparams():
    X_train, y_train = generate_classification_toy_data()

    train_features = RealFeatures(X_train)
    train_labels = BinaryLabels(y_train)

    n_test = 50
    x1 = np.linspace(X_train[0, :].min() - 1, X_train[0, :].max() + 1, n_test)
    x2 = np.linspace(X_train[1, :].min() - 1, X_train[1, :].max() + 1, n_test)
    X_test = np.asarray(list(product(x1, x2))).T

    test_features = RealFeatures(X_test)

    kernel = GaussianKernel(10, 2)
    zero_mean = ZeroMean()

    # lik = ProbitLikelihood()
    lik = LogitLikelihood()

    # re-create inference method and GP instance to start from scratch, use other Shogun structures from above
    inf = EPInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # EP might not converge, we here allow that without errors
    inf.set_fail_on_non_convergence(False)

    gp = GaussianProcessClassification(inf)

    # evaluate our inference method for its derivatives
    grad = GradientEvaluation(gp, train_features, train_labels, GradientCriterion(), False)
    grad.set_function(inf)

    # handles all of the above structures in memory
    grad_search = GradientModelSelection(grad)

    # search for best parameters and store them
    best_combination = grad_search.select_model()

    # apply best parameters to GP
    best_combination.apply_to_machine(gp)

    # we have to "cast" objects to the specific kernel interface we used (soon to be easier)
    best_width = GaussianKernel.obtain_from_generic(inf.get_kernel()).get_width()
    best_scale = inf.get_scale()
    print("Selected kernel bandwidth:", best_width)
    print("Selected kernel scale:", best_scale)

    # train gp
    gp.train()

    # visualise predictive distribution
    p_test = gp.get_probabilities(test_features)
    plot_binary_data(X_train, y_train)
    plt.contour(x1, x2, np.reshape(p_test, (n_test, n_test)), levels=[0.5], colors=('black'))
    plt.pcolor(x1, x2, p_test.reshape(n_test, n_test))
    _ = plt.colorbar()
    plt.show()

def plot_params_log_likelihood():
    X_train, y_train = generate_classification_toy_data()

    train_features = RealFeatures(X_train)
    train_labels = BinaryLabels(y_train)

    n_test = 50
    x1 = np.linspace(X_train[0, :].min() - 1, X_train[0, :].max() + 1, n_test)
    x2 = np.linspace(X_train[1, :].min() - 1, X_train[1, :].max() + 1, n_test)
    X_test = np.asarray(list(product(x1, x2))).T

    test_features = RealFeatures(X_test)

    kernel = GaussianKernel(10, 2)
    zero_mean = ZeroMean()

    # lik = ProbitLikelihood()
    lik = LogitLikelihood()

    # re-create inference method and GP instance to start from scratch, use other Shogun structures from above
    inf = EPInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # EP might not converge, we here allow that without errors
    inf.set_fail_on_non_convergence(False)

    gp = GaussianProcessClassification(inf)

    # evaluate our inference method for its derivatives
    grad = GradientEvaluation(gp, train_features, train_labels, GradientCriterion(), False)
    grad.set_function(inf)

    # handles all of the above structures in memory
    grad_search = GradientModelSelection(grad)

    # search for best parameters and store them
    best_combination = grad_search.select_model()

    # apply best parameters to GP
    best_combination.apply_to_machine(gp)

    # we have to "cast" objects to the specific kernel interface we used (soon to be easier)
    best_width = GaussianKernel.obtain_from_generic(inf.get_kernel()).get_width()
    best_scale = inf.get_scale()
    print("Selected kernel bandwidth:", best_width)
    print("Selected kernel scale:", best_scale)

    # train gp
    gp.train()

    # parameter space, increase resolution if you want finer plots, takes long though
    resolution = 5
    widths = 2 ** np.linspace(-4, 10, resolution)
    scales = 2 ** np.linspace(-5, 10, resolution)

    # re-create inference method and GP instance to start from scratch, use other Shogun structures from above
    inf = EPInferenceMethod(kernel, train_features, zero_mean, train_labels, lik)

    # EP might not converge, we here allow that without errors
    inf.set_fail_on_non_convergence(False)

    gp = GaussianProcessClassification(inf)
    inf.set_tolerance(1e-3)
    # compute marginal likelihood for every parameter combination
    # use Shogun objects from above
    marginal_likelihoods = np.zeros((len(widths), len(scales)))
    for i in range(len(widths)):
        for j in range(len(scales)):
            kernel.set_width(widths[i])
            inf.set_scale(scales[j])
            marginal_likelihoods[i, j] = -inf.get_negative_log_marginal_likelihood()

    # contour plot of marginal likelihood as a function of kernel width and scale
    plt.contour(np.log2(widths), np.log2(scales), marginal_likelihoods)
    plt.colorbar()
    plt.xlabel("Kernel width (log-scale)")
    plt.ylabel("Kernel scale (log-scale)")
    _ = plt.title("Log Marginal Likelihood")

    # plot our found best parameters
    _ = plt.plot([np.log2(best_width)], [np.log2(best_scale)], 'r*', markersize=20)
    plt.show()

if __name__ == '__main__':
    #plot_class_likelihoods()
    #plot_classification_toy_data()
    #plot_decision_classes()
    #plot_decision_distributions()
    #plot_marginal_likelihood()
    calculate_best_hyperparams()
    #plot_params_log_likelihood()