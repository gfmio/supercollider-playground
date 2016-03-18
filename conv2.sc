{
	var a, b, volume, sound, diff;

	//a = 100 * MouseX.kr(0.0,1.0);
	//b = 20 * MouseY.kr(0.0,1.0);
	a = 1000 * MouseX.kr(0.0,1.0);
	b = 0.5 * a;
	volume = 5.0 / (a + b) * MouseY.kr(0.0,1.0);

	sound = volume * (a * SinOsc.ar(b, 0, 1.0) - b * SinOsc.ar(a, 0, 1.0));

	Out.ar(1,sound);
	Out.ar(0,sound);
}.freqscope

{
	var a = 32;
	var input = SinOsc.ar(20, 0, 0.5);
	var kernel = SinOsc.ar(100, 0, 0.1);
	var sound = Convolution.ar(input,kernel, 2*a, 0.30);
	Out.ar(1, sound);
	Out.ar(0, sound);
}.freqscope;


{
	var sound = (
		   0.5*SinOsc.ar(220,0,1.0) +
		   0.5*SinOsc.ar(220*[0.5, 1.0, 1.5, 2.0, 2.5, 3.0],0,1.0)
	    ) *
	    SinOsc.ar(220,0,1.0);

	Out.ar(1, sound);
	Out.ar(0, sound);
}.freqscope