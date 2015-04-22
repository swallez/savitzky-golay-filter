Savitzky-Golay filter is well-known method for smoothing data. The most accurate description I've found is in [Numerical Recipes](http://www.nrbook.com/a/bookcpdf/c14-8.pdf). My implementation does not follow exactly theirs - it does not use FFT for instance, but still gives fairly impressive results. I've also prepared extensive documentation and few unit tests. In the library you can also find few helpers for data pre-processing, which you can attach to filter, to assist with common data preparation chores such as:

  * linearizing,
  * padding,
  * eliminating zeros,
  * de-trending

Please refer to docs for more.
Filter usage is fairly simple:
```
float[] data = new float[] { 8916.81f, 8934.24f, 9027.06f, 9160.79f, 7509.14f };
float[] leftPad = new float[] { 8915.06f, 8845.53f, 9064.17f, 8942.09f, 8780.87f };
double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
ContinuousPadder padder1 = new ContinuousPadder();
SGFilter sgFilter = new SGFilter(5, 5);
sgFilter.appendPreprocessor(padder1);
float[] smooth = sgFilter.smooth(data, leftPad, new float[0], coeffs);
MeanValuePadder padder2 = new MeanValuePadder(10, false, true);
sgFilter.removePreprocessor(padder1);
sgFilter.appendPreprocessor(padder2);
smooth = sgFilter.smooth(data, leftPad, new float[0], coeffs);
```
This library comes with dependency on [commons-math](http://commons.apache.org/math/). Code that depends on aforementioned, excellent lib is used only for calculating Savitzky-Golay coefficients (which involves solving set of linear equations) - so if someone is willing to provide replacement for this, he will be gladly welcome.
If you have any problems or notice any bug, please open an issue. I will respond as quickly as possible. Enjoy :-)

You may also like to download plotting application, called SGFilterVis. It enables you to visualize the effect of the filter on your data. It also gives you the ability to apply all preprocessors and observe how they work