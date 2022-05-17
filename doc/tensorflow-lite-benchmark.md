# TFLite Model Benchmark Tool with C++ Binary

## Reference

- [TFLite Model Benchmark Tool with C++ Binary - tensorflow/tensorflow](https://github.com/tensorflow/tensorflow/blob/v2.8.0/tensorflow/lite/tools/benchmark/README.md)

## How to
Build sample on qemueriscv64 (core-image-full-cmdline).

### Clone repositories and oe-init-build-env.
```
git clone https://github.com/openembedded/bitbake.git
git clone https://github.com/openembedded/openembedded-core.git
git clone https://github.com/openembedded/meta-openembedded.git
git clone https://github.com/riscv/meta-riscv.git
git clone https://github.com/NobuoTsukamoto/meta-tensorflow-lite.git
source openembedded-core/oe-init-build-env build
```

### Add layer
```
bitbake-layers add-layer ../meta-openembedded/meta-oe/
bitbake-layers add-layer ../meta-openembedded/meta-python/
bitbake-layers add-layer ../meta-openembedded/meta-networking/
bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
bitbake-layers add-layer ../meta-riscv/
bitbake-layers add-layer ../meta-tensorflow-lite/
```

### Create conf/auto.conf file and write config
Add `tensorflow-lite-benchmark` recipes to `conf/auto.conf` file.
```
FORTRAN:forcevariable = ",fortran"
IMAGE_INSTALL:append = " tensorflow-lite-benchmark"
```

### Bitbake
```
MACHINE=qemuriscv64 bitbake core-image-full-cmdline
```

### Run QEMU
```
MACHINE=qemuriscv64 runqemu nographic
```

### Run benchmark tool.
login `root` and run benchmark tool.
```
cd /usr/share/tensorflow/lite/tools/benchmark/
./benchmark_model \
  --graph=./mobilenet_v1_1.0_224.tflite \
  --num_threads=4 \
  --enable_op_profiling=true
```

The following results can be obtained.
```
STARTING!
Log parameter values verbosely: [0]
Num threads: [4]
Graph: [./mobilenet_v1_1.0_224.tflite]
Enable op profiling: [1]
#threads used for CPU inference: [4]
Loaded model ./mobilenet_v1_1.0_224.tflite
The input model file size (MB): 16.9008
Initialized session in 17.187ms.
Running benchmark for at least 1 iterations and at least 0.5 seconds but terminate if exceeding 150 seconds.
count=1 curr=3512391

Running benchmark for at least 50 iterations and at least 1 seconds but terminate if exceeding 150 seconds.
count=50 first=2990275 curr=2913402 min=2727568 max=3189954 avg=2.95034e+06 std=109798

Inference timings in us: Init: 17187, First inference: 3512391, Warmup (avg): 3.51239e+06, Inference (avg): 2.95034e+06
Note: as the benchmark tool itself affects memory footprint, the following is only APPROXIMATE to the actual memory footprint of the model at runtime. Take the information at your discretion.
Memory footprint delta from the start of the tool (MB): init=4.21094 overall=46.0859
Profiling Info for Benchmark Initialization:
============================== Run Order ==============================
	             [node type]	          [start]	  [first]	 [avg ms]	     [%]	  [cdf%]	  [mem KB]	[times called]	[Name]
	         AllocateTensors	            0.000	    1.356	    1.356	100.000%	100.000%	     0.000	        1	AllocateTensors/0

============================== Top by Computation Time ==============================
	             [node type]	          [start]	  [first]	 [avg ms]	     [%]	  [cdf%]	  [mem KB]	[times called]	[Name]
	         AllocateTensors	            0.000	    1.356	    1.356	100.000%	100.000%	     0.000	        1	AllocateTensors/0

Number of nodes executed: 1
============================== Summary by node type ==============================
	             [Node type]	  [count]	  [avg ms]	    [avg %]	    [cdf %]	  [mem KB]	[times called]
	         AllocateTensors	        1	     1.356	   100.000%	   100.000%	     0.000	        1

Timings (microseconds): count=1 curr=1356
Memory (bytes): count=0
1 nodes observed



Operator-wise Profiling Info for Regular Benchmark Runs:
============================== Run Order ==============================
	             [node type]	          [start]	  [first]	 [avg ms]	     [%]	  [cdf%]	  [mem KB]	[times called]	[Name]
	                 CONV_2D	            0.066	   87.754	   60.957	  2.066%	  2.066%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_0/Relu6]:0
	       DEPTHWISE_CONV_2D	           61.033	   53.628	   49.870	  1.691%	  3.757%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_1_depthwise/Relu6]:1
	                 CONV_2D	          110.912	  134.346	  147.808	  5.011%	  8.768%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_1_pointwise/Relu6]:2
	       DEPTHWISE_CONV_2D	          258.730	   27.363	   34.010	  1.153%	  9.921%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_2_depthwise/Relu6]:3
	                 CONV_2D	          292.750	  125.424	  135.911	  4.607%	 14.528%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_2_pointwise/Relu6]:4
	       DEPTHWISE_CONV_2D	          428.672	   38.643	   47.423	  1.608%	 16.135%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_3_depthwise/Relu6]:5
	                 CONV_2D	          476.101	  233.972	  242.524	  8.221%	 24.357%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_3_pointwise/Relu6]:6
	       DEPTHWISE_CONV_2D	          718.645	   35.876	   19.039	  0.645%	 25.002%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_4_depthwise/Relu6]:7
	                 CONV_2D	          737.691	  144.354	  130.485	  4.423%	 29.426%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_4_pointwise/Relu6]:8
	       DEPTHWISE_CONV_2D	          868.186	   32.953	   30.232	  1.025%	 30.450%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_5_depthwise/Relu6]:9
	                 CONV_2D	          898.426	  263.724	  277.374	  9.403%	 39.853%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_5_pointwise/Relu6]:10
	       DEPTHWISE_CONV_2D	         1175.813	   13.673	   11.954	  0.405%	 40.258%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_6_depthwise/Relu6]:11
	                 CONV_2D	         1187.773	  166.067	  137.816	  4.672%	 44.930%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_6_pointwise/Relu6]:12
	       DEPTHWISE_CONV_2D	         1325.600	   19.176	   15.824	  0.536%	 45.467%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_7_depthwise/Relu6]:13
	                 CONV_2D	         1341.430	  238.119	  230.753	  7.822%	 53.289%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_7_pointwise/Relu6]:14
	       DEPTHWISE_CONV_2D	         1572.193	   20.572	   16.234	  0.550%	 53.839%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_8_depthwise/Relu6]:15
	                 CONV_2D	         1588.434	  230.877	  249.023	  8.442%	 62.281%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_8_pointwise/Relu6]:16
	       DEPTHWISE_CONV_2D	         1837.471	   14.557	   22.527	  0.764%	 63.045%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_9_depthwise/Relu6]:17
	                 CONV_2D	         1860.007	  276.404	  243.619	  8.259%	 71.303%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_9_pointwise/Relu6]:18
	       DEPTHWISE_CONV_2D	         2103.639	   24.506	   16.815	  0.570%	 71.873%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_10_depthwise/Relu6]:19
	                 CONV_2D	         2120.467	  224.358	  248.456	  8.423%	 80.296%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_10_pointwise/Relu6]:20
	       DEPTHWISE_CONV_2D	         2368.936	   27.001	   21.415	  0.726%	 81.022%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_11_depthwise/Relu6]:21
	                 CONV_2D	         2390.360	  239.657	  242.949	  8.236%	 89.258%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_11_pointwise/Relu6]:22
	       DEPTHWISE_CONV_2D	         2633.321	   16.929	    5.845	  0.198%	 89.456%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_12_depthwise/Relu6]:23
	                 CONV_2D	         2639.171	  109.905	  114.411	  3.878%	 93.334%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_12_pointwise/Relu6]:24
	       DEPTHWISE_CONV_2D	         2753.591	    4.318	    5.736	  0.194%	 93.529%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_13_depthwise/Relu6]:25
	                 CONV_2D	         2759.331	  173.079	  177.589	  6.020%	 99.549%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_13_pointwise/Relu6]:26
	         AVERAGE_POOL_2D	         2936.930	    0.828	    0.869	  0.029%	 99.578%	     0.000	        1	[MobilenetV1/Logits/AvgPool_1a/AvgPool]:27
	                 CONV_2D	         2937.801	   11.354	   12.185	  0.413%	 99.992%	     0.000	        1	[MobilenetV1/Logits/Conv2d_1c_1x1/BiasAdd]:28
	                 SQUEEZE	         2949.993	    0.013	    0.017	  0.001%	 99.992%	     0.000	        1	[MobilenetV1/Logits/SpatialSqueeze]:29
	                 SOFTMAX	         2950.011	    0.230	    0.233	  0.008%	100.000%	     0.000	        1	[MobilenetV1/Predictions/Reshape_1]:30

============================== Top by Computation Time ==============================
	             [node type]	          [start]	  [first]	 [avg ms]	     [%]	  [cdf%]	  [mem KB]	[times called]	[Name]
	                 CONV_2D	          898.426	  263.724	  277.374	  9.403%	  9.403%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_5_pointwise/Relu6]:10
	                 CONV_2D	         1588.434	  230.877	  249.023	  8.442%	 17.845%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_8_pointwise/Relu6]:16
	                 CONV_2D	         2120.467	  224.358	  248.456	  8.423%	 26.267%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_10_pointwise/Relu6]:20
	                 CONV_2D	         1860.007	  276.404	  243.619	  8.259%	 34.526%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_9_pointwise/Relu6]:18
	                 CONV_2D	         2390.360	  239.657	  242.949	  8.236%	 42.761%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_11_pointwise/Relu6]:22
	                 CONV_2D	          476.101	  233.972	  242.524	  8.221%	 50.983%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_3_pointwise/Relu6]:6
	                 CONV_2D	         1341.430	  238.119	  230.753	  7.822%	 58.805%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_7_pointwise/Relu6]:14
	                 CONV_2D	         2759.331	  173.079	  177.589	  6.020%	 64.825%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_13_pointwise/Relu6]:26
	                 CONV_2D	          110.912	  134.346	  147.808	  5.011%	 69.836%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_1_pointwise/Relu6]:2
	                 CONV_2D	         1187.773	  166.067	  137.816	  4.672%	 74.508%	     0.000	        1	[MobilenetV1/MobilenetV1/Conv2d_6_pointwise/Relu6]:12

Number of nodes executed: 31
============================== Summary by node type ==============================
	             [Node type]	  [count]	  [avg ms]	    [avg %]	    [cdf %]	  [mem KB]	[times called]
	                 CONV_2D	       15	  2651.853	    89.897%	    89.897%	     0.000	       15
	       DEPTHWISE_CONV_2D	       13	   296.919	    10.065%	    99.962%	     0.000	       13
	         AVERAGE_POOL_2D	        1	     0.869	     0.029%	    99.992%	     0.000	        1
	                 SOFTMAX	        1	     0.232	     0.008%	    99.999%	     0.000	        1
	                 SQUEEZE	        1	     0.016	     0.001%	   100.000%	     0.000	        1

Timings (microseconds): count=50 first=2989660 curr=2912978 min=2727203 max=3189476 avg=2.9499e+06 std=109770
Memory (bytes): count=0
31 nodes observed
```

