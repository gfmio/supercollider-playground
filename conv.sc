(
	//must have power of two framesize- FFT size will be sorted by Convolution to be double this
	//maximum is currently a=8192 for FFT of size 16384
	a = 32;
	s = Server.local;
	//kernel buffer
    // g = Buffer.alloc(s,a,1);
)

(
	{ var input, kernel;

	input=AudioIn.ar(2);
	//kernel= Mix.ar(LFSaw.ar([300,500,800,1000]*MouseX.kr(1.0,2.0),0,1.0));
	kernel = SinOsc.ar(220, 0, 0.2);
	//must have power of two framesize
	Out.ar(1,Convolution.ar(input,kernel, 1024, 0.5));
	Out.ar(0,Convolution.ar(input,kernel, 1024, 0.5));
	 }.play;
)

(
	//random impulse response
	g.set(0,1.0);
	100.do({arg i; g.set(a.rand, 1.0.rand)});


	{ var input, kernel, kernel2;

	//input=AudioIn.ar(1);
	//kernel= PlayBuf.ar(1,g.bufnum,BufRateScale.kr(g.bufnum),1,0,1);
	input = SinOsc.ar(20*MouseX.kr(0.0,1.0), 0, 0.2);
	kernel = SinOsc.ar(400, 0, 0.2);
	kernel2 = SinOsc.ar(400*MouseY.kr(0.0,1.0), 0, 0.2);

	Out.ar(1,Convolution.ar(Convolution.ar(input,kernel, 2*a, 0.30), kernel2, 2*a, 0.2));
	Out.ar(0,Convolution.ar(Convolution.ar(input,kernel, 2*a, 0.30), kernel2, 2*a, 0.2));
}.play;

)

(
	//random impulse response
	//g.set(0,1.0);
	//100.do({arg i; g.set(a.rand, 1.0.rand)});


	{ var input, kernel, kernel2, kernel3;

	// input=AudioIn.ar(1);
	// kernel= PlayBuf.ar(1,g.bufnum,BufRateScale.kr(g.bufnum),1,0,1);
	// input = SinOsc.ar(220*MouseX.kr(0.0,1.0), 0, 0.2);
	// kernel = SinOsc.ar(10*MouseY.kr(0.0,1.0), 0, 0.2);
	// kernel2 = SinOsc.ar(2*220*MouseX.kr(0.0,1.0), 0, 0.2);
	// kernel3 = SinOsc.ar(20*MouseY.kr(0.0,1.0), 0, 0.2);

	input = SinOsc.ar(220*MouseX.kr(0.0,1.0), 0, 0.2);
	kernel3 = SinOsc.ar(10*MouseY.kr(0.0,1.0), 0, 0.2);
	kernel2 = SinOsc.ar(10*MouseY.kr(0.0,1.0), 0, 0.2);
    kernel = SinOsc.ar(220*MouseX.kr(0.0,1.0), 0, 0.2);

	Out.ar(1,Convolution.ar(
		Convolution.ar(
			Convolution.ar(
				input,kernel, 2*a, 0.30),
			kernel2, 2*a, 0.2),
		kernel3, 2*a, 0.2));
	Out.ar(0,Convolution.ar(
	    Convolution.ar(
		    Convolution.ar(
			   input,kernel, 2*a, 0.30),
		    kernel2, 2*a, 0.2),
		kernel3, 2*a, 0.2));
}.play;

)

(
	//random impulse response
	//g.set(0,1.0);
	//100.do({arg i; g.set(a.rand, 1.0.rand)});

	{ var input, kernel, kernel2;

	a = 1024;

	//input=AudioIn.ar(1);
	//kernel= PlayBuf.ar(1,g.bufnum,BufRateScale.kr(g.bufnum),1,0,1);
	input = SinOsc.ar(0.5*0.5*0.5*0.5*440*MouseX.kr(0.0,1.0), 0, 0.2);
	kernel = SinOsc.ar(0.5*0.5*0.5*0.5*440*MouseY.kr(0.0,1.0), 0, 0.2);

	Out.ar(1,Convolution.ar(input,kernel, 2*a, 0.5));
	Out.ar(0,Convolution.ar(input,kernel, 2*a, 0.5));
}.play;
)


({
	var sound1, sound2, a, output;

	a = 32;

	//sound1 = SinOsc.ar(220, 0, 0.2);
	sound1 = {
        ({arg i;
                var j = i + 1;
			SinOsc.ar(1000 * MouseX.kr(0.0,1.0) * j, 0, 0.6/j) // try pi in the phase argument
        } ! 100).sum // we sum this function 30 times
	};


	sound2 = {
        ({arg i;
                var j = i + 1;
			SinOsc.ar(1000 * MouseY.kr(0.0,1.0) * j, 0, 0.6/j) // try pi in the phase argument
        } ! 100).sum // we sum this function 30 times
	};
	output = Convolution.ar(sound1, sound2, 2*a, 0.01);
	Out.ar(1,output);
	Out.ar(0,output);
}.freqscope;
)