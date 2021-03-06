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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sonar.check.Rule;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Discourage the declaration of multiple proxy endpoints in a same proxy.
 * Code : PD501
 * @author Nicolas Tisserand
 */
@Rule(key = "TooMuchProxyEndpointsCheck")
public class TooMuchProxyEndpointsCheck extends AbstractXmlCheck {

	@Override
	public void validate(XmlSourceCode xmlSourceCode) {
	    setWebSourceCode(xmlSourceCode);

	    Document document = getWebSourceCode().getDocument(false);
	    if (document.getDocumentElement() != null && "APIProxy".equals(document.getDocumentElement().getNodeName())) {

	    	
	    	// Search for targets definitions of an APIProxy document
		    XPathFactory xPathfactory = XPathFactory.newInstance();
		    XPath xpath = xPathfactory.newXPath();
		    
		    try {
		    XPathExpression exprDisplayName = xpath.compile("count(/APIProxy/ProxyEndpoints/ProxyEndpoint)");
		    double proxiesCount = (double)exprDisplayName.evaluate(document, XPathConstants.NUMBER);
		    
	    	// If there are more than 2 ProxyEndpoint, this is a violation.
	    	if(proxiesCount > 2) {
	    
	    		int lineNumber = 1; // By default

	    		// Search for the <ProxyEndpoints> node (it's a better location to indicate the violation
	    		NodeList proxyEndpointsNodeList = document.getDocumentElement().getElementsByTagName("ProxyEndpoints");
	    		if(proxyEndpointsNodeList!=null && proxyEndpointsNodeList.getLength()>0) {
	    			lineNumber = getWebSourceCode().getLineForNode(proxyEndpointsNodeList.item(0));
	    		}
	    		
	    		createViolation(lineNumber, "Discourage the declaration of multiple proxy endpoints in a same proxy.");
	    	}
	    	
			} catch (XPathExpressionException e) {
				// Nothing to do
			}	    	
	    	
    	}
	}

}
