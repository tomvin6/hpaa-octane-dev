package com.hpe.application.automation.tools.results.parser.jenkinsjunit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.hpe.application.automation.tools.results.parser.ReportParseException;
import com.hpe.application.automation.tools.results.parser.ReportParser;
import com.hpe.application.automation.tools.results.parser.util.ParserUtil;
import com.hpe.application.automation.tools.results.service.almentities.AlmRun;
import com.hpe.application.automation.tools.results.service.almentities.AlmTest;
import com.hpe.application.automation.tools.results.service.almentities.AlmTestInstance;
import com.hpe.application.automation.tools.results.service.almentities.AlmTestInstanceImpl;
import com.hpe.application.automation.tools.results.service.almentities.AlmTestSet;
import com.hpe.application.automation.tools.results.service.almentities.AlmTestSetImpl;
import com.hpe.application.automation.tools.results.service.almentities.EntityRelation;
import com.hpe.application.automation.tools.results.service.almentities.IAlmConsts;
import com.hpe.application.automation.tools.sse.sdk.Base64Encoder;

public class JenkinsJUnitReportParserImpl implements ReportParser {

	public List<AlmTestSet> parseTestSets(InputStream reportInputStream,
                                          String testingFramework, String testingTool) throws ReportParseException {
		
		try {
			return parseTestSetsFromJenkinsPluginJUnitReport(reportInputStream, testingFramework, testingTool);
		} catch (Exception e) {
		
			//e.printStackTrace();
			throw new ReportParseException();
		}
	}	
	
	private Result parseFromJenkinsPluginJUnitReport(InputStream reportInputStream) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Result.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Result)unmarshaller.unmarshal(reportInputStream);		
	}
	
	private AlmTest createExternalTestForJenkinsPluginJUnit(Result.Suites.Suite.Cases.Case c, String testingFramework, String testingTool) {
	
		return ParserUtil.createExternalTest(c.getClassName(), c.getTestName(), testingFramework, testingTool);
	}
	
	private String getRunDetail(Result.Suites.Suite.Cases.Case c){
		String detail = ParserUtil.marshallerObject(Result.Suites.Suite.Cases.Case.class, c);
		return Base64Encoder.encode(detail.getBytes());
	}

	private ArrayList<AlmTestSet> parseTestSetsFromJenkinsPluginJUnitReport(InputStream reportInputStream, String testingFramework, String testingTool) throws JAXBException {
		Result result = parseFromJenkinsPluginJUnitReport(reportInputStream);		
		ArrayList<AlmTestSet> testSets = new ArrayList<AlmTestSet>();
		
		for (Result.Suites suites : result.getSuites()) {
			for (Result.Suites.Suite suite : suites.getSuite()) {
				AlmTestSet testSet = new AlmTestSetImpl();
				testSet.setFieldValue(AlmTestSet.TESTSET_NAME, ParserUtil.replaceInvalidCharsForTestSetName(suite.getName()));
				testSet.setFieldValue(AlmTestSet.TESTSET_SUB_TYPE_ID, EXTERNAL_TEST_SET_TYPE_ID);
				testSets.add(testSet);
				
				for (Result.Suites.Suite.Cases cases : suite.getCases()) {
					for (Result.Suites.Suite.Cases.Case c : cases.getCase()){
						AlmTestInstance testInstance = new AlmTestInstanceImpl();
						testInstance.setFieldValue(AlmTestInstance.TEST_INSTANCE_SUBTYPE_ID, EXTERNAL_TEST_INSTANCE_TYPE_ID);
						testSet.addRelatedEntity(EntityRelation.TESTSET_TO_TESTINSTANCE_CONTAINMENT_RELATION, testInstance);
						
						AlmTest test = createExternalTestForJenkinsPluginJUnit(c, testingFramework, testingTool);
						testInstance.addRelatedEntity(EntityRelation.TEST_TO_TESTINSTANCE_REALIZATION_RELATION, test);
						
						AlmRun run = ParserUtil.createRun(getRunStatus(c),
														suite.getTimestamp(),  
														c.getDuration(), 
														getRunDetail (c));
						testInstance.addRelatedEntity(EntityRelation.TESTINSTANCE_TO_RUN_REALIZATION_RELATION, run);
					}
				}
			}
		}
		
		return testSets;
	}
	
	private String getRunStatus(Result.Suites.Suite.Cases.Case c) {
		if (c.getSkipped() != null && c.getSkipped().equals("true")) {
			return IAlmConsts.IStatuses.NO_RUN;
		}
		
		if(c.getErrorStackTrace() != null && c.getErrorStackTrace().length() >0) {
			return IAlmConsts.IStatuses.FAILED;
		}
		
		if(c.getErrorDetails() != null && c.getErrorDetails().length() >0) {
			return IAlmConsts.IStatuses.FAILED;
		}
		
		if (c.getFailedSince() != null && c.getFailedSince().equals("0")) {
			return IAlmConsts.IStatuses.PASSED;
		}
		
		return IAlmConsts.IStatuses.FAILED;
	}
}
