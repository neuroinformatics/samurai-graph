package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGIStringModifier;

/**
 * The modifier class to insert an escape character before each brace that is not used
 * for subscript nor superscript.
 *
 */
public class SGStringBraceModifier implements SGIStringModifier {

	/**
	 * Modifies a given text string.
	 * 
	 * @param str
	 *           a text string
	 * @return modified result
	 */
	@Override
	public String modify(String str) {
		// when given version number is older than 2.0.0, returns a modified text string
		DrawingElementString2DExtendedOlder el = new DrawingElementString2DExtendedOlder(str);
		return el.generateString();
	}

	/**
	 * A string class that implements older algorithm of string object of version 2.0.0.
	 *
	 */
    private static class DrawingElementString2DExtendedOlder extends SGDrawingElementString2DExtended {

        /**
         * Construct a string element with given text.
         */
        public DrawingElementString2DExtendedOlder(final String str) {
            super(str);
        }

        /**
         * Construct a string element with given text and font information.
         */
        public DrawingElementString2DExtendedOlder(final String str,
                final String fontName, final int fontStyle, final float fontSize,
                final Color color, final float mag, final float angle) {
            super(str, fontName, fontStyle, fontSize, color, mag, angle);
        }
    	
    	/**
    	 * Overrode to implements the older algorithm of version 2.0.0.
    	 */
    	@Override
        protected boolean getSubscriptAndSuperscriptInfo(final String line,
                final List<String> baseList, final List<String> superList,
                final List<String> subList) {
            if (line == null || baseList == null || superList == null
                    || subList == null) {
                return false;
            }
            if (line.length() == 0) {
                return false;
            }

            // working variables
            boolean in_super = false;
            boolean in_sub = false;
            boolean in_brace = false;
            boolean in_escape = false;
            int in_brace_stack = 0;
            StringBuffer cache_base = new StringBuffer();
            StringBuffer cache_super = new StringBuffer();
            StringBuffer cache_sub = new StringBuffer();
            for (int i = 0; i < line.length(); i++) {
                final char c = line.charAt(i);
                if (in_super || in_sub) {
                    if (in_brace) {
                        if (in_escape) {
                            if (in_super) {
                                cache_super.append('\\');
                                cache_super.append(c);
                            } else {
                                cache_sub.append('\\');
                                cache_sub.append(c);
                            }
                            in_escape = false;
                        } else {
                            if (c == '\\') {
                                in_escape = true;
                            } else {
                                if (c == '}' && in_brace_stack == 0) {
                                    in_brace = false;
                                    in_super = false;
                                    in_sub = false;
                                } else {
                                    if (c == '}')
                                        in_brace_stack--;
                                    if (c == '{')
                                        in_brace_stack++;
                                    if (in_super) {
                                        cache_super.append(c);
                                    } else {
                                        cache_sub.append(c);
                                    }
                                }
                            }
                        }
                    } else if (c == '{') {
                        in_brace = true;
                        in_brace_stack = 0;
                    } else {
                        if (in_escape == false) {
                            if (c == '\\') {
                                in_escape = true;
                            } else {
                                if (in_super) {
                                    cache_super.append(c);
                                    in_super = false;
                                } else {
                                    cache_sub.append(c);
                                    in_sub = false;
                                }
                            }
                        } else {
                            if (in_super) {
                                cache_super.append('\\');
                                cache_super.append(c);
                                in_super = false;
                            } else {
                                cache_sub.append('\\');
                                cache_sub.append(c);
                                in_sub = false;
                            }
                            in_escape = false;
                        }
                    }
                } else {
                    if (c == '^' && in_escape == false) {
                        if (cache_base.length() == 0 || cache_super.length() != 0) {
                            return false;
                        }
                        in_super = true;
                    } else if (c == '_' && in_escape == false) {
                        if (cache_base.length() == 0 || cache_sub.length() != 0) {
                            return false;
                        }
                        in_sub = true;
                    } else {
                        if (c == '\\' && in_escape == false) {
                            in_escape = true;
                        } else {
                            if (cache_super.length() != 0
                                    || cache_sub.length() != 0) {
                                // store cache string
                                baseList.add(cache_base.toString());
                                if (cache_super.length() != 0) {
                                    superList.add(cache_super.toString());
                                } else {
                                    superList.add(null);
                                }
                                if (cache_sub.length() != 0) {
                                    subList.add(cache_sub.toString());
                                } else {
                                    subList.add(null);
                                }
                                // reset string cache
                                cache_base.setLength(0);;
                                cache_super.setLength(0);
                                cache_sub.setLength(0);
                            }
                            cache_base.append(c);
                            in_escape = false;
                        }
                    }
                }
            }
            if (in_super || in_sub || in_brace || in_escape) {
                return false;
            }

            // flush cache strings..
            if (cache_base.length() != 0) {
                // store cache string
                baseList.add(cache_base.toString());
                if (cache_super.length() != 0) {
                    superList.add(cache_super.toString());
                } else {
                    superList.add(null);
                }
                if (cache_sub.length() != 0) {
                    subList.add(cache_sub.toString());
                } else {
                    subList.add(null);
                }
            }
            return true;
        }
    	
    	/**
    	 * Generates a single text string from base, subscript and superscript strings.
    	 * 
    	 * @return generated text string
    	 */
    	private String generateString() {
    		StringBuffer sb = new StringBuffer();
    		for (int ii = 0; ii < this.mBaseElementList.size(); ii++) {
    			SGDrawingElementString2D base = this.mBaseElementList.get(ii);
    			sb.append(this.insertEscapeCharacter(base.getString()));
    			DrawingElementString2DExtendedOlder sub
    					= (DrawingElementString2DExtendedOlder) this.mSubscriptElementList.get(ii);
    			if (sub != null) {
    				sb.append("_{");
    				sb.append(sub.generateString());
    				sb.append("}");
    			}
    			DrawingElementString2DExtendedOlder sup
    					= (DrawingElementString2DExtendedOlder) this.mSuperscriptElementList.get(ii);
    			if (sup != null) {
    				sb.append("^{");
    				sb.append(sup.generateString());
    				sb.append("}");
    			}
    		}
    		return sb.toString();
    	}
    	
    	/**
    	 * Inserts an escape character before braces into a given text string.
    	 * 
    	 * @param str
    	 *           a text string
    	 * @return modified text string
    	 */
    	private String insertEscapeCharacter(final String str) {
    		StringBuffer sb = new StringBuffer();
    		final char[] cArray = str.toCharArray();
    		for (int ii = 0; ii < cArray.length; ii++) {
    			final char c = cArray[ii];
    			if (c == '{' || c == '}') {
    				if (ii == 0) {
    					sb.append('\\');
    				} else {
    					final char cPrev = cArray[ii - 1];
    					if (cPrev != '_' && cPrev != '^' && cPrev != '\\') {
    	    				sb.append('\\');
    					}
    				}
    			}
    			sb.append(c);
    		}
    		return sb.toString();
    	}
    	
    	@Override
        protected boolean createStringElementWithoutIndex() {
    		return this.createStringElementsDirectly(this.getString());
        }
    	
    	@Override
    	protected SGDrawingElementString2D createIndexInstance(final String str,
    			final String name, final int style, final float subsize,
    			final Color cl, final float mag, final float angle) {
    		return new DrawingElementString2DExtendedOlder(str, name, style, subsize,
    				cl, mag, angle);
    	}
    }

}
