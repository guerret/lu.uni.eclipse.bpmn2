package lu.uni.bpmn.test;

import lu.uni.bpmn.ui.dataprotection.PrOntoParser;
import lu.uni.dapreco.bpmn2.AKNParser;
import lu.uni.dapreco.bpmn2.LRMLParser;
// import lu.uni.dapreco.bpmn2.PrOntoParser;

public class TestParsing {

	private final static String resDir = "resources";

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;
	// private final static String lrmlLocal = resDir +l "/" + lrmlName;

	private final static String aknName = "akn-act-gdpr-full.xml";
	private static final String aknURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.bpmn2/master/"
			+ resDir + "/" + aknName;
	// private static final String aknLocal = resDir + "/" + aknName;

	private static String aknPrefix = "GDPR";
	private static String ontoPrefix = "prOnto";
	// private static String predicate = "Transmit";

	public static void main(String[] args) {
		PrOntoParser p = new PrOntoParser(true);
		LRMLParser lParser = new LRMLParser(lrmlURI);
		AKNParser aParser = new AKNParser(aknURI);
		String[] actions = p.getActions();
		for (String predicate : actions) {
			System.out.println("PROVISIONS FOR ACTION: " + predicate);
			String[] articles = lParser.findArticles(ontoPrefix + ":" + predicate);
			for (String s : articles) {
				s = s.substring((aknPrefix + ":").length());
				System.out.println(aParser.getTextFromEId(s));
				System.out.println();
			}
		}
	}

}
