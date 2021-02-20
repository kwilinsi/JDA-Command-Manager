package botUtils.tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

public class Num {
    /**
     * Rounds the given input to the specified number of places
     * @param num the input number to round
     * @param places the number of decimals to round to
     * @return the rounded result
     */
    public static double round(double num, int places) {
        return Math.round(num * Math.pow(10, places)) / Math.pow(10, places);
    }

    /**
     * Rounds a decimal to have the given number of significant figures using BigDecimal
     *
     * @param input   the input number to round
     * @param sigFigs the maximum number of significant figures on the output
     * @return the rounded number
     */
    public static double sigFigs(double input, int sigFigs) {
        BigDecimal b = new BigDecimal(input);
        return b.round(new MathContext(sigFigs)).doubleValue();
    }

    /**
     * Rounds an integer to have the given number of significant figures using BigDecimal cause BigInteger was
     * stupid and didn't work right
     *
     * @param input   the input number to round
     * @param sigFigs the maximum number of significant figures on the output
     * @return the rounded number
     */
    public static int sigFigs(int input, int sigFigs) {
        BigDecimal b = new BigDecimal(input);
        return b.round(new MathContext(sigFigs)).intValue();
    }

    /**
     * Converts a standard deviation into a variance by just squaring it
     * @param stdev the input standard deviation
     * @return the output variance
     */
    public static double stdevToVariance(double stdev) {
        return Math.pow(stdev, 2);
    }

    /**
     * Returns the inverse of a probability. For input p, return value is 1-p
     * @param probability the input probability
     * @return the output probability (inverse of the input)
     */
    public static double inverse(double probability) {
        return 1 - probability;
    }

    /**
     * Computes the mean of a set of data. If the array is empty or null 0 is returned.
     * @param data the data to compute the mean of
     * @return the mean of the data set
     */
    public static double mean(double[] data) {
        if (data == null || data.length == 0)
            return 0;
        return sum(data) / data.length;
    }

    /**
     * Computes the sum of a set of data. If the list is empty or null, 0 is returned.
     * @param data the input array
     * @return the sum of the array
     */
    public static double sum(double[] data) {
        if (data == null)
            return 0;
        double sum = 0;
        for (double d : data)
            sum += d;
        return sum;
    }

    /**
     * Computes quartiles 1, 2, and 3 from the specified array and returns them as an array of size three
     * @param data the dataset
     * @return an array of quartiles {Q1, Q2, Q3}
     * @throws Exception if the data array is null or empty
     */
    public static double[] quartiles(double[] data) throws Exception {
        if (data == null || data.length == 0)
            throw new Exception("Invalid parameters for quartile function. Not enough data provided.");

        // Code below breaks for arrays with less than 3 numbers, so here's code to catch that
        if (data.length < 3)
            return new double[]{data[0], mean(data), data[data.length - 1]};

        // The following code was stolen from stack overflow and I don't really know how it works
        double[] ans = new double[3];

        for (int quartileType = 1; quartileType < 4; quartileType++) {
            float length = data.length + 1;
            double quartile;
            float newArraySize = (length * ((float) (quartileType) * 25 / 100)) - 1;
            Arrays.sort(data);
            if (newArraySize % 1 == 0) {
                quartile = data[(int) (newArraySize)];
            } else {
                int newArraySize1 = (int) (newArraySize);
                quartile = (data[newArraySize1] + data[newArraySize1 + 1]) / 2;
            }
            ans[quartileType - 1] =  quartile;
        }

        return ans;
    }

    /**
     * Determines if the given integer is even or not
     * @param i the integer in question
     * @return true if it is even; false if it is odd
     */
    public static boolean isEven(int i) {
        double h = i / 2.0;
        return (int) h == (h);
    }

    /**
     * Computes the cumulative sum of a set of data as an array of the same size. If the input is null, the output
     * is null. If the input is empty, the output is empty. If the input is {1, 4, 2, 3}, the output is {1, 5, 7, 10}
     * @param data the input array
     * @return the sum of the array
     */
    public static double[] cumulativeSum(double[] data) {
        if (data == null)
            return null;

        double[] result = new double[data.length];
        double cumSum = 0;

        for (int i = 0; i < data.length; i++)
            result[i] = cumSum += data[i];

        return result;
    }

    /**
     * Computes the standard deviation of a set of data. If the list is empty, 0 is returned.
     * If sampleAlgorithm is true, the sample standard deviation is returned. Otherwise the population standard
     * deviation is used. The only difference is that in the sample algorithm the sum of all the deviations is
     * divided by n-1 instead of just n.
     *
     * Note that if the input data array has less than 2 items in it zero will be returned immediately.
     * This prevents runtime errors like array out of bounds and divide by zero.
     *
     * @param data the array of input data
     * @param sampleAlgorithm true if the sample algorithm should be used; false for the population algorithm
     * @return the standard deviation
     */
    public static double stdev(double[] data, boolean sampleAlgorithm) {
        return Math.sqrt(variance(data, sampleAlgorithm));
    }

    /**
     * Computes the variance of a set of data. If sampleAlgorithm is true, the sample variance is returned.
     * Otherwise the population variance is used. The only difference is that in the sample algorithm the sum of all
     * the variations is divided by n-1 instead of just n.
     *
     * Note that if the input data array has less than 2 items in it zero will be returned immediately.
     * This prevents runtime errors like array out of bounds and divide by zero.
     *
     * @param data the array of input data
     * @param sampleAlgorithm true if the sample algorithm should be used; false for the population algorithm
     * @return the variance of the data (guaranteed to be at least 0, never negative)
     */
    public static double variance(double[] data, boolean sampleAlgorithm) {
        if (data == null || data.length < 2)
            return 0;

        double variance = 0;
        double mean = mean(data);

        for (double d : data)
            variance += Math.pow(d - mean, 2);

        // If the sample algorithm, divide by n-1; otherwise divide by n
        variance /= sampleAlgorithm ? data.length - 1 : data.length;
        return variance;
    }
}