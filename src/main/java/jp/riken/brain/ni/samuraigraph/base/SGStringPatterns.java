package jp.riken.brain.ni.samuraigraph.base;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that manages text patterns.
 *
 */
public class SGStringPatterns {

	// The map of characters.
	private static final Map<String, Character> CHAR_MAP = new HashMap<String, Character>();
	
	// Initializes the map.
	static {
		// Greek alphabets
		CHAR_MAP.put("alpha", '\u03B1');
		CHAR_MAP.put("a", '\u03B1');
		CHAR_MAP.put("beta", '\u03B2');
		CHAR_MAP.put("b", '\u03B2');
		CHAR_MAP.put("Gamma", '\u0393');
		CHAR_MAP.put("G", '\u0393');
		CHAR_MAP.put("gamma", '\u03B3');
		CHAR_MAP.put("g", '\u03B3');
		CHAR_MAP.put("Delta", '\u0394');
		CHAR_MAP.put("D", '\u0394');
		CHAR_MAP.put("delta", '\u03B4');
		CHAR_MAP.put("d", '\u03B4');
		CHAR_MAP.put("epsilon", '\u03F5');
		CHAR_MAP.put("e", '\u03F5');
		CHAR_MAP.put("varepsilon", '\u03B5');
		CHAR_MAP.put("ve", '\u03B5');
		CHAR_MAP.put("zeta", '\u03B6');
		CHAR_MAP.put("z", '\u03B6');
		CHAR_MAP.put("eta", '\u03B7');
		CHAR_MAP.put("h", '\u03B7');
		CHAR_MAP.put("Theta", '\u0398');
		CHAR_MAP.put("Q", '\u0398');
		CHAR_MAP.put("theta", '\u03B8');
		CHAR_MAP.put("q", '\u03B8');
		CHAR_MAP.put("vartheta", '\u03D1');
		CHAR_MAP.put("J", '\u03D1');
		CHAR_MAP.put("iota", '\u03B9');
		CHAR_MAP.put("i", '\u03B9');
		CHAR_MAP.put("kappa", '\u03BA');
		CHAR_MAP.put("k", '\u03BA');
		CHAR_MAP.put("Lambda", '\u039B');
		CHAR_MAP.put("L", '\u039B');
		CHAR_MAP.put("lambda", '\u03BB');
		CHAR_MAP.put("l", '\u03BB');
		CHAR_MAP.put("mu", '\u03BC');
		CHAR_MAP.put("m", '\u03BC');
		CHAR_MAP.put("nu", '\u03BD');
		CHAR_MAP.put("n", '\u03BD');
		CHAR_MAP.put("Xi", '\u039E');
		CHAR_MAP.put("X", '\u039E');
		CHAR_MAP.put("xi", '\u03BE');
		CHAR_MAP.put("x", '\u03BE');
		CHAR_MAP.put("Pi", '\u03A0');
		CHAR_MAP.put("P", '\u03A0');
		CHAR_MAP.put("pi", '\u03C0');
		CHAR_MAP.put("p", '\u03C0');
		CHAR_MAP.put("varpi", '\u03D6');
		CHAR_MAP.put("v", '\u03D6');
		CHAR_MAP.put("rho", '\u03C1');
		CHAR_MAP.put("r", '\u03C1');
		CHAR_MAP.put("varrho", '\u03F1');
		CHAR_MAP.put("vr", '\u03F1');
		CHAR_MAP.put("Sigma", '\u03A3');
		CHAR_MAP.put("S", '\u03A3');
		CHAR_MAP.put("sigma", '\u03C3');
		CHAR_MAP.put("s", '\u03C3');
		CHAR_MAP.put("varsigma", '\u03C2');
		CHAR_MAP.put("V", '\u03C2');
		CHAR_MAP.put("tau", '\u03C4');
		CHAR_MAP.put("t", '\u03C4');
		CHAR_MAP.put("Upsilon", '\u03A5');
		CHAR_MAP.put("U", '\u03A5');
		CHAR_MAP.put("upsilon", '\u03C5');
		CHAR_MAP.put("u", '\u03C5');
		CHAR_MAP.put("Phi", '\u03A6');
		CHAR_MAP.put("F", '\u03A6');
		CHAR_MAP.put("phi", '\u03D5');
		CHAR_MAP.put("f", '\u03D5');
		CHAR_MAP.put("varphi", '\u03C6');
		CHAR_MAP.put("j", '\u03C6');
		CHAR_MAP.put("chi", '\u03C7');
		CHAR_MAP.put("c", '\u03C7');
		CHAR_MAP.put("Psi", '\u03A8');
		CHAR_MAP.put("Y", '\u03A8');
		CHAR_MAP.put("psi", '\u03C8');
		CHAR_MAP.put("y", '\u03C8');
		CHAR_MAP.put("Omega", '\u03A9');
		CHAR_MAP.put("W", '\u03A9');
		CHAR_MAP.put("omega", '\u03C9');
		CHAR_MAP.put("w", '\u03C9');
		
		// mathematical symbols
		CHAR_MAP.put("infty", '\u221E');
		CHAR_MAP.put("leqq", '\u2266');
		CHAR_MAP.put("geqq", '\u2267');
		CHAR_MAP.put("pm", '\u00B1');
		CHAR_MAP.put("mp", '\u2213');
		CHAR_MAP.put("times", '\u00D7');
		CHAR_MAP.put("div", '\u00F7');
		CHAR_MAP.put("cap", '\u2229');
		CHAR_MAP.put("cup", '\u222A');
		CHAR_MAP.put("subset", '\u2282');
		CHAR_MAP.put("subseteq", '\u2286');
		CHAR_MAP.put("supset", '\u2283');
		CHAR_MAP.put("supseteq", '\u2287');
		CHAR_MAP.put("in", '\u2208');
		CHAR_MAP.put("ni", '\u220B');
		CHAR_MAP.put("equiv", '\u2261');
		CHAR_MAP.put("sim", '\u223C');
		CHAR_MAP.put("simeq", '\u2243');
		CHAR_MAP.put("approx", '\u2248');
		CHAR_MAP.put("cong", '\u2245');
		CHAR_MAP.put("neq", '\u2260');
		CHAR_MAP.put("propto", '\u221D');
		CHAR_MAP.put("fallingdotseq", '\u2252');
		
		CHAR_MAP.put("ast", '\u002A');
		CHAR_MAP.put("star", '\u22C6');
		CHAR_MAP.put("circ", '\u2218');
		CHAR_MAP.put("bullet", '\u2022');
		CHAR_MAP.put("cdot", '\u00B7');
		CHAR_MAP.put("uplus", '\u228E');
		CHAR_MAP.put("sqcap", '\u2293');
		CHAR_MAP.put("sqcup", '\u2294');
		CHAR_MAP.put("vee", '\u2228');
		CHAR_MAP.put("wedge", '\u2227');
		CHAR_MAP.put("setminus", '\u2216');
		CHAR_MAP.put("wr", '\u2240');
		CHAR_MAP.put("oplus", '\u2295');
		CHAR_MAP.put("ominus", '\u2296');
		CHAR_MAP.put("otimes", '\u2297');
		CHAR_MAP.put("odot", '\u2299');
		CHAR_MAP.put("bigcirc", '\u25EF');
		CHAR_MAP.put("diamond", '\u22C4');
		CHAR_MAP.put("bigtriangleup", '\u25B3');
		CHAR_MAP.put("bigtriangledown", '\u25BD');
		CHAR_MAP.put("triangleleft", '\u25C3');
		CHAR_MAP.put("triangleright", '\u25B9');
		CHAR_MAP.put("dagger", '\u2020');
		CHAR_MAP.put("ddagger", '\u2021');
		CHAR_MAP.put("amalg", '\u2A3F');
		
		CHAR_MAP.put("le", '\u2264');
		CHAR_MAP.put("leq", '\u2264');
		CHAR_MAP.put("ge", '\u2265');
		CHAR_MAP.put("geq", '\u2265');
		CHAR_MAP.put("prec", '\u227A');
		CHAR_MAP.put("succ", '\u227B');
		CHAR_MAP.put("preceq", '\u2AAF');
		CHAR_MAP.put("succeq", '\u2AB0');
		CHAR_MAP.put("ll", '\u226A');
		CHAR_MAP.put("gg", '\u226B');
		CHAR_MAP.put("sqsubseteq", '\u2291');
		CHAR_MAP.put("sqsupseteq", '\u2292');
		CHAR_MAP.put("vdash", '\u22A2');
		CHAR_MAP.put("dashv", '\u22A3');
		CHAR_MAP.put("notin", '\u2209');
		CHAR_MAP.put("asymp", '\u224D');
		CHAR_MAP.put("doteq", '\u2250');
		CHAR_MAP.put("models", '\u22A7');
		CHAR_MAP.put("perp", '\u22A5');
		CHAR_MAP.put("mid", '\u2223');
		CHAR_MAP.put("parallel", '\u2225');
		CHAR_MAP.put("bowtie", '\u22C8');
		CHAR_MAP.put("smile", '\u2323');
		CHAR_MAP.put("frown", '\u2322');

		CHAR_MAP.put("forall", '\u2200');
		CHAR_MAP.put("exists", '\u2203');
		CHAR_MAP.put("nabla", '\u2207');
		CHAR_MAP.put("Re", '\u211C');
		CHAR_MAP.put("Im", '\u2111');
		CHAR_MAP.put("wp", '\u2118');
		CHAR_MAP.put("emptyset", '\u2205');
		CHAR_MAP.put("hbar", '\u210F');

		CHAR_MAP.put("degree", '\u00B0');

		CHAR_MAP.put("sect", '\u00A7');
		CHAR_MAP.put("para", '\u00B6');
		CHAR_MAP.put("copyright", '\u00A9');
		CHAR_MAP.put("pounds", '\u00A3');
		CHAR_MAP.put("angle", '\u2220');
		CHAR_MAP.put("partial", '\u2202');
		CHAR_MAP.put("int", '\u222B');
		CHAR_MAP.put("oint", '\u222E');
		CHAR_MAP.put("lfloor", '\u230A');
		CHAR_MAP.put("rfloor", '\u230B');
		CHAR_MAP.put("lceil", '\u2308');
		CHAR_MAP.put("rceil", '\u2309');
		CHAR_MAP.put("aleph", '\u2135');
		CHAR_MAP.put("prime", '\u2032');
		
		// arrows
		CHAR_MAP.put("rightarrow", '\u2192');
		CHAR_MAP.put("leftarrow", '\u2190');
		CHAR_MAP.put("uparrow", '\u2191');
		CHAR_MAP.put("downarrow", '\u2193');
		CHAR_MAP.put("Rightarrow", '\u21D2');
		CHAR_MAP.put("Leftarrow", '\u21D0');
		CHAR_MAP.put("Uparrow", '\u21D1');
		CHAR_MAP.put("Downarrow", '\u21D3');
		CHAR_MAP.put("longrightarrow", '\u27F6');
		CHAR_MAP.put("longleftarrow", '\u27F5');
		CHAR_MAP.put("Longrightarrow", '\u27F9');
		CHAR_MAP.put("Longleftarrow", '\u27F8');
		CHAR_MAP.put("nearrow", '\u2197');
		CHAR_MAP.put("searrow", '\u2198');
		CHAR_MAP.put("swarrow", '\u2199');
		CHAR_MAP.put("nwarrow", '\u2196');
		CHAR_MAP.put("leftrightarrow", '\u2194');
		CHAR_MAP.put("Leftrightarrow", '\u21D4');
		CHAR_MAP.put("longleftrightarrow", '\u27F7');
		CHAR_MAP.put("Longleftrightarrow", '\u27FA');
		CHAR_MAP.put("rightleftharpoons", '\u21CC');
		
		CHAR_MAP.put("mapsto", '\u21A6');
		CHAR_MAP.put("longmapsto", '\u27FC');
	}
	
	/**
	 * Returns the character of given pattern.
	 * 
	 * @param pattern
	 *           a text string of the pattern
	 * @return the character
	 */
	public static Character getChar(final String pattern) {
		return CHAR_MAP.get(pattern);
	}

}
