s = Server.default.boot;

// create a new analyzer
FreqScope.new(400, 200, 0);
Stethoscope.new(s,2);

(
    SynthDef("kick", {
        arg outBus=0, amp=1.0;
        var env0, env1, env1m, out;

        env0 =  EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.06, 0.26], [-4, -2, -4]), doneAction:2);
        env1 = EnvGen.ar(Env.new([220, 59, 29], [0.005, 0.29], [-4, -5]));
        env1m = env1.midicps;

        out = LFPulse.ar(env1m, 0, 0.5, 1, -0.5);
        out = out + WhiteNoise.ar(1);
        out = LPF.ar(out, env1m*1.5, env0);
        out = out + SinOsc.ar(env1m, 0.5, env0);

        out = out * 1.2 * amp;
        out = out.clip2(1);

        Out.ar(outBus, out.dup);
    }).send(s);

    SynthDef("snare", {
        arg outBus=0, amp=0.8;
        var env0, env1, env2, env1m, oscs, noise, out;

        env0 = EnvGen.ar(Env.new([0.5, 1, 0.5, 0], [0.005, 0.03, 0.10], [-4, -2, -4]));
        env1 = EnvGen.ar(Env.new([110, 60, 49], [0.005, 0.1], [-4, -5]));
        env1m = env1.midicps;
        env2 = EnvGen.ar(Env.new([1, 0.4, 0], [0.05, 0.13], [-2, -2]), doneAction:2);

        oscs = LFPulse.ar(env1m, 0, 0.5, 1, -0.5) + LFPulse.ar(env1m * 1.6, 0, 0.5, 0.5, -0.25);
        oscs = LPF.ar(oscs, env1m*1.2, env0);
        oscs = oscs + SinOsc.ar(env1m, 0.8, env0);

        noise = WhiteNoise.ar(0.2);
        noise = HPF.ar(noise, 200, 2);
        noise = BPF.ar(noise, 6900, 0.6, 3) + noise;
        noise = noise * env2;

        out = oscs + noise;
        out = out.clip2(1) * amp;

        Out.ar(outBus, out.dup);
    }).send(s);

    SynthDef("clap", {
        arg outBus=0, amp = 0.5;
        var env1, env2, out, noise1, noise2;

        env1 = EnvGen.ar(Env.new([0, 1, 0, 1, 0, 1, 0, 1, 0], [0.001, 0.013, 0, 0.01, 0, 0.01, 0, 0.03], [0, -3, 0, -3, 0, -3, 0, -4]));
        env2 = EnvGen.ar(Env.new([0, 1, 0], [0.02, 0.3], [0, -4]), doneAction:2);

        noise1 = WhiteNoise.ar(env1);
        noise1 = HPF.ar(noise1, 600);
        noise1 = BPF.ar(noise1, 2000, 3);

        noise2 = WhiteNoise.ar(env2);
        noise2 = HPF.ar(noise2, 1000);
        noise2 = BPF.ar(noise2, 1200, 0.7, 0.7);

        out = noise1 + noise2;
        out = out * 2;
        out = out.softclip * amp;

        Out.ar(outBus, out.dup);
    }).send(s);

    SynthDef("hat", {
        arg outBus=0, amp=0.3;
        var env1, env2, out, oscs1, noise, n, n2;

        n = 5;
        thisThread.randSeed = 4;

        env1 = EnvGen.ar(Env.new([0, 1.0, 0], [0.001, 0.2], [0, -12]));
        env2 = EnvGen.ar(Env.new([0, 1.0, 0.05, 0], [0.002, 0.05, 0.03], [0, -4, -4]), doneAction:2);

        oscs1 = Mix.fill(n, {|i|
            SinOsc.ar(
                ( i.linlin(0, n-1, 42, 74) + rand2(4.0) ).midicps,
                SinOsc.ar( (i.linlin(0, n-1, 78, 80) + rand2(4.0) ).midicps, 0.0, 12),
                1/n
            )
        });

        oscs1 = BHiPass.ar(oscs1, 1000, 2, env1);
        n2 = 8;
        noise = WhiteNoise.ar;
        noise = Mix.fill(n2, {|i|
            var freq;
            freq = (i.linlin(0, n-1, 40, 50) + rand2(4.0) ).midicps.reciprocal;
            CombN.ar(noise, 0.04, freq, 0.1)
        }) * (1/n) + noise;
        noise = BPF.ar(noise, 6000, 0.9, 0.5, noise);
        noise = BLowShelf.ar(noise, 3000, 0.5, -6);
        noise = BHiPass.ar(noise, 1000, 1.5, env2);

        out = noise + oscs1;
        out = out.softclip;
        out = out * amp;

        Out.ar(outBus, out.dup);
    }).send(s);

    SynthDef("bass", {
        arg outBus=0, amp=1.0, freq = 55;
        var env0, env1, env1m, out;
    	var a, b, volume, sound, diff;

        env0 =  EnvGen.ar(Env.new([0.5, 1, 0.5, 0], 0.75*[0.005, 0.06, 0.26], [-4, -2, -4]), doneAction:2);
        env1 = EnvGen.ar(Env.new([110, 59, 29], [0.005, 0.29], [-4, -5]));
        env1m = env1.midicps;

	    a = freq;
	    //a = 100 * MouseX.kr(0.0,1.0);
	    //b = 20 * MouseY.kr(0.0,1.0);
	    // a = 1000 * MouseX.kr(0.0,1.0);
	    b = 0.25 * a;
	    volume = 5 / (a + b);

	   ~freqm = [1,2,4,8];

	    out = volume * ((
		a * SinOsc.ar(b*~freqm, 0, 1.0) -
			b * SinOsc.ar(a*~freqm, 0, 1.0)
	    ) * (
			a * SinOsc.ar(b*~freqm, 2 * pi / 2, 1.0) -
					b * SinOsc.ar(a*~freqm, 2 * pi /2, 1.0)
	    ));

	    //out = LPF.ar(out, env1m*1.5, env0);
        //out = out + SinOsc.ar(env1m, 0.5, env0);

	    out = HPF.ar(
		    LPF.ar(
			   out,	// the source to be filtered
			min(16 * freq,20000)
		    ),
		max(60, 0.25 * freq));

	    out = out * 5.0 * amp;
        out = out.clip2(1);

        Out.ar(outBus, out.dup);
    }).send(s);
)

(
    ~cs3 = 138.59;
    ~c3 = 130.81;
    ~f3 = 174.61;
    ~f2 = 0.5 * ~f3;

    ~dseq = [
        3.5*[1,0,1,0, 1,0,0,2, 1,0,0,0, 1,0,0,0, 1,0,1,0, 1,0,1,0, 1,1,1,1, 1,1,1,1],
        3.5*[0,0,0,0, 4,0,0,2, 0,0,0,0, 4,0,0,0],
        3.5*[1,0,1,0, 2,0,1,0, 2,0,1,1, 4,0,1,0],
        3.5*[1,2,4,0, 1,0,4,0, 1,2,4,2, 1,0,4,2],
        0.5*[~cs3,0,~cs3,0, ~cs3,0,~cs3,0, ~c3,0,0,0, ~c3,0,0,0, ~f3,0,0,~f3, ~f3,0,0,0, ~f2,0,0,0, ~f2,0,~f2,~c3]
    ];

    ~bpm = 125;
    ~clock = TempoClock(~bpm/60);
    ~counter = 0;
    ~clock.schedAbs(0.0, {
	    if ((~item = ~dseq[0][(~counter % ~dseq[0].size)]) != 0) {
		    Synth("kick", ["amp", (~item/4).squared*0.7]);
        };
        if ((~item = ~dseq[1][~counter % ~dseq[1].size]) != 0) {
            Synth("snare", ["amp", (~item / 4).squared*0.7]);
        };
        if ((~item = ~dseq[2][~counter % ~dseq[2].size]) != 0) {
            Synth("clap", ["amp", (~item / 4).squared*0.7]);
        };
        if ((~item = ~dseq[3][~counter % ~dseq[3].size]) != 0) {
            Synth("hat", ["amp", (~item / 4).squared*0.7]);
        };
        if ((~item = ~dseq[4][~counter % ~dseq[4].size]) != 0) {
            Synth("bass", ["freq", ~item]);
        };
        ~counter = (~counter + 1);
        0.25
    });
)

(
    SynthDef("hbass", {
        arg outBus=0, amp=1.0, freq = 55;
        var out;
    	var a, b, volume, sound, diff;

        a = freq;
	    //a = 100 * MouseX.kr(0.0,1.0);
	    //b = 20 * MouseY.kr(0.0,1.0);
	    // a = 1000 * MouseX.kr(0.0,1.0);
	    b = 0.25 * a;
	    volume = 5 / (a + b);

	   ~freqm = [1,1.5,2];

	    out = volume * ((
		a * SinOsc.ar(b*~freqm, 0, 1.0) -
			b * SinOsc.ar(a*~freqm, 0, 1.0)
	    ) * (
			a * SinOsc.ar(b*~freqm, 2 * pi / 2, 1.0) -
					b * SinOsc.ar(a*~freqm, 2 * pi /2, 1.0)
	    ));

	    out = HPF.ar(
		    LPF.ar(
			   out,	// the source to be filtered
			min(16 * freq, 20000)
		    ),
		max(60, 0.25 * freq));

	    out = out * 0.12 * amp;
        out = out.clip2(1);

        Out.ar(outBus, out.dup);
    }).send(s);
)

({
    var in, amp, freq, hasFreq, out, volume;
    in = Mix.new(SoundIn.ar([0,1]));

	in = HPF.ar(
        LPF.ar(
    	    in,	// the source to be filtered
 		    100
 	    ),
    40);

	amp = Amplitude.kr(in, 0.05, 0.05);
    # freq, hasFreq = Pitch.kr(in, ampThreshold: 0.01, median: 99);

	a = freq;
	b = 0.25 * a;
	volume = 5 / (a + b);

	~freqm = [1,2,4,8];

	out = volume * ((
		a * SinOsc.ar(b*~freqm, 0, 1.0) -
			b * SinOsc.ar(a*~freqm, 0, 1.0)
	    ) * (
			a * SinOsc.ar(b*~freqm, 2 * pi / 2, 1.0) -
					b * SinOsc.ar(a*~freqm, 2 * pi /2, 1.0)
	    ));

	out = out * 0.12 * amp;
    out = out.clip2(1);

	Out.ar([0,1],out);
}.play)
