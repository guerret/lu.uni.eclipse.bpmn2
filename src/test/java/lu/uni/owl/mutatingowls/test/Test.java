package lu.uni.owl.mutatingowls.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import lu.uni.bpmn.ui.dataprotection.OWLInputDialog;

public class Test {

	private static final String DEFAULT_ONTOLOGY = "dataprotection";

	private static String OWL_PATH = System.getProperty("user.dir") + OWLInputDialog.OWL_PATH;
	private static String ontologyFile = OWL_PATH + "/" + DEFAULT_ONTOLOGY + "-rdf.owl";
	private static String testFile = OWL_PATH + "/" + DEFAULT_ONTOLOGY + "-tests.rq";

	private OntModel model;

	private List<ResultSetRewindable> results = new ArrayList<ResultSetRewindable>();

	private String[] queryStrings;

	public Test() {
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		FileManager.get().readModel(model, ontologyFile);
		try (FileReader fileReader = new FileReader(testFile);
				BufferedReader bufferedReader = new BufferedReader(fileReader);) {
			// FileReader fileReader = new FileReader(testFile);
			// BufferedReader bufferedReader = new BufferedReader(fileReader);
			String lines = "";
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				lines += line + System.lineSeparator();
			}
			bufferedReader.close();
			queryStrings = lines.split(System.lineSeparator() + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runTests() {
		Query[] queries = new Query[queryStrings.length];
		QueryExecution[] qes = new QueryExecution[queryStrings.length];
		for (int i = 0; i < queryStrings.length; i++) {
			queries[i] = QueryFactory.create(queryStrings[i]);
			qes[i] = QueryExecutionFactory.create(queries[i], model);
			ResultSetRewindable rSet = ResultSetFactory.copyResults(qes[i].execSelect());
			results.add(ResultSetFactory.copyResults(rSet));
		}
		for (ResultSetRewindable rSet : results) {
			System.out.println(ResultSetFormatter.asText(rSet));
		}
		for (QueryExecution qe : qes)
			qe.close();
	}

	public static void main(String[] args) {
		Test testRunner = new Test();
		testRunner.runTests();
	}

}
