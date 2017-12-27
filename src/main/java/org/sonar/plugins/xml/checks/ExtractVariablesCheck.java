/*
 * Copyright 2017 Credit Mutuel Arkea
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.sonar.plugins.xml.checks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sonar.check.Rule;
import org.sonar.plugins.xml.checks.XmlSourceCode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.arkea.satd.sonar.xml.ApigeeXmlSensor;


/**
 * ExtractVariables XML or JSON Payload
 * A check for a body element must be performed before policy execution.
 * Code : PO003 & PO004 & PO005
 * @author Nicolas Tisserand
 */
@Rule(key = "ExtractVariablesCheck")
public class ExtractVariablesCheck extends AbstractXmlCheck {

	@Override
	public void validate(XmlSourceCode xmlSourceCode) {
	    setWebSourceCode(xmlSourceCode);

	    Document document = getWebSourceCode().getDocument(false);
	    
	    XPathFactory xPathfactory = XPathFactory.newInstance();
	    XPath xpath = xPathfactory.newXPath();
	    
	    Pattern pattern = Pattern.compile("(response.content|response.form|request.content|request.form|message.content|message.form|message.verb|request.verb|request.header.Content-Length|response.header.Content-Length)");

	    try {
			String nameAttr = (String)xpath.evaluate("/ExtractVariables/@name", document, XPathConstants.STRING);
			Boolean hasPayloadExtraction = (Boolean)xpath.evaluate("count(/ExtractVariables/*[(name()='JSONPayload' or name()='XMLPayload')]/Variable) + count(/ExtractVariables/FormParam) > 0", document, XPathConstants.BOOLEAN);

		    if(nameAttr!=null && !nameAttr.isEmpty() && hasPayloadExtraction.booleanValue()) {
			    // Search for the associated step in the full storage
		    	
			    List<XmlSourceCode> listProxiesEndpoint = BundleRecorder.searchByStepName(nameAttr);
			    
			    if(listProxiesEndpoint.isEmpty()) {
			    	// It means that the policy is unused in no condition at all
					// Create a violation for the root node
			    }
			    
			    // Now check the Condition of the matching Steps
			    for(XmlSourceCode currentXml : listProxiesEndpoint) {
			    	
					XPathExpression exprSteps = xpath.compile("//Step[Name[text() = '"+nameAttr+"']]");
					NodeList stepNodes = (NodeList)exprSteps.evaluate(currentXml.getDocument(false), XPathConstants.NODESET);
					
					for(int i=0; i<stepNodes.getLength(); i++) {
						Node currentStep = stepNodes.item(i);
						
						XPathExpression exprCondition = xpath.compile("Condition/text()");
						String condition = (String)exprCondition.evaluate(currentStep, XPathConstants.STRING);
						
						boolean hasIssue = true;
						if(condition!=null && !condition.isEmpty()) {
							// Analyse the content of the condition
							Matcher matcher = pattern.matcher(condition);	    
							if(matcher.find()) {
								hasIssue = false;
							}
						} 
						
						// Check also on flow condition :
						// if the parent is a flow we might revert the decision if it has an appropriate condition
						if(hasIssue) {
							
							// Search the condition of the parent Node (Flow, but not PreFlow or PostFlow
							XPathExpression exprFlowCondition = xpath.compile("../../../*[name() = 'Flow']/Condition/text()");
							String flowCondition = (String)exprFlowCondition.evaluate(currentStep, XPathConstants.STRING);
							
							Matcher matcher = pattern.matcher(flowCondition);	    
							if(matcher.find()) {
								hasIssue = false;
							}
						}
						
						// Finally : Create issue if needed
						if(hasIssue) {
							
							XmlIssue issue = new XmlIssue(getRuleKey(), currentXml.getLineForNode(currentStep), "An appropriate check for a message body was not found on the enclosing Step or Flow.");
							currentXml.addViolation(issue); // Useful for JUnit test
							ApigeeXmlSensor.saveIssue(ApigeeXmlSensor.getContext(), currentXml); // Mandatory to "commit" the issue in the final report
						}
					}
			    }
		    }
		} catch (XPathExpressionException e) {
			// Nothing to do
		}
	}
	
}
