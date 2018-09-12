package lu.uni.bpmn.ui.dataprotection;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

public class PrOntoParser {

	public static final String OWL_PATH = "/resources";
	public static final String OWL_FILE = OWL_PATH + "/pronto.owl";
	protected static Ontology ontology;

	public PrOntoParser() {
		URL url = PrOntoParser.class.getResource(OWL_FILE);
		ontology = new Ontology(url, "https://w3id.org/ontology/pronto#");
	}

	public PrOntoParser(boolean local) {
		URL url = null;
		if (local)
			try {
				url = new File(System.getProperty("user.dir") + OWL_FILE).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		else
			url = PrOntoParser.class.getResource(OWL_FILE);
		ontology = new Ontology(url, "https://w3id.org/ontology/pronto#");
	}

	public String[] getActions() {
		OWLClass actionClass = ontology.getClassByFullLabel("Action", "http://purl.org/spar/pwo/");
		Set<OWLClass> subClasses = ontology.getDirectSubClasses(actionClass);
		return ontology.getLabels(subClasses);
	}

	public static void main(String[] args) {
		PrOntoParser p = new PrOntoParser(true);
		String[] actions = p.getActions();
		for (String a : actions)
			System.out.println(a);
	}

}
