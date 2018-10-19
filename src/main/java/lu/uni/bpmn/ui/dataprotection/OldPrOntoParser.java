package lu.uni.bpmn.ui.dataprotection;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

public class OldPrOntoParser {

	public static final String OWL_PATH = "/resources";
	public static final String OWL_FILE = OWL_PATH + "/pronto.owl";
	protected static Ontology ontology;

	public OldPrOntoParser() {
		URL url = OldPrOntoParser.class.getResource(OWL_FILE);
		ontology = new Ontology(url, "https://w3id.org/ontology/pronto#");
	}

	public OldPrOntoParser(boolean local) {
		URL url = null;
		if (local)
			try {
				url = new File(System.getProperty("user.dir") + OWL_FILE).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		else
			url = OldPrOntoParser.class.getResource(OWL_FILE);
		ontology = new Ontology(url, "https://w3id.org/ontology/pronto#");
	}

	public String[] getActions() {
		OWLClass actionClass = ontology.getClassByFullLabel("Action", "http://purl.org/spar/pwo/");
		Set<OWLClass> subClasses = ontology.getDirectSubClasses(actionClass);
		return ontology.getLabels(subClasses);
	}

	public static void main(String[] args) {
		OldPrOntoParser p = new OldPrOntoParser(true);
		String[] actions = p.getActions();
		for (String a : actions)
			System.out.println(a);
	}

}
