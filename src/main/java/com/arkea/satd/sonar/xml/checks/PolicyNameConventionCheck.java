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
package com.arkea.satd.sonar.xml.checks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.check.Rule;
import org.sonarsource.analyzer.commons.xml.XmlFile;
import org.sonarsource.analyzer.commons.xml.checks.SonarXmlCheck;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Policy Naming Conventions - type indication
 * It is recommended that the policy name include an indicator of the policy type.
 * Code : PO007
 * @author Nicolas Tisserand
 */
@Rule(key = "PolicyNameConventionCheck")
@SuppressWarnings("squid:S1192")
public class PolicyNameConventionCheck extends SonarXmlCheck {

	/**
	 * Definition of supported policies, and naming convention
	 */
	private static Map<String, List<String>> supportedPolicies = new HashMap<>();

	static {
		supportedPolicies.put("AccessControl", Arrays.asList("accesscontrol", "ac", "accessc") );
		supportedPolicies.put("AccessEntity", Arrays.asList("accessentity", "ae", "accesse") );
		supportedPolicies.put("AssignMessage", Arrays.asList("assignmessage", "am", "assign", "build", "set", "response", "send", "add") );
		supportedPolicies.put("BasicAuthentication", Arrays.asList("encode", "basicauth", "ba", "auth") );
		supportedPolicies.put("InvalidateCache", Arrays.asList("invalidatecache", "invalidate", "ic", "cache") );
		supportedPolicies.put("LookupCache", Arrays.asList("lookup", "lu", "lucache", "cache", "lc") );
		supportedPolicies.put("PopulateCache", Arrays.asList("populate", "pop", "populatecache", "pc", "cache") );
		supportedPolicies.put("ResponseCache", Arrays.asList("responsecache", "rc", "cache") );
		supportedPolicies.put("ConcurrentRatelimit", Arrays.asList("concurrentratelimit", "crl", "cr") );
		supportedPolicies.put("ConnectorCallout", Arrays.asList("connectorcallout", "cc") );		
		supportedPolicies.put("ExtractVariables", Arrays.asList("extract", "ev", "vars") );
		supportedPolicies.put("FlowCallout", Arrays.asList("flowcallout", "flow", "fc", "sf") );
		supportedPolicies.put("JavaCallout", Arrays.asList("javacallout", "java", "javac") );
		supportedPolicies.put("Javascript", Arrays.asList("jsc", "js", "javascript") );
		supportedPolicies.put("JSONThreatProtection", Arrays.asList("jsonthreat", "threat", "jtp", "tp") );
		supportedPolicies.put("JSONToXML", Arrays.asList("jsontoxml", "j2x", "jtox") );
		supportedPolicies.put("GenerateJWS", Arrays.asList("jws", "gjws", "gj") );
		supportedPolicies.put("VerifyJWS", Arrays.asList("jws", "vjws", "vj") );
		supportedPolicies.put("DecodeJWS", Arrays.asList("jws", "djws", "dj") );
		supportedPolicies.put("GenerateJWT", Arrays.asList("jwt", "gjwt", "gj") );
		supportedPolicies.put("VerifyJWT", Arrays.asList("jwt", "vjwt", "vj") );
		supportedPolicies.put("DecodeJWT", Arrays.asList("jwt", "djwt", "dj") );		
		supportedPolicies.put("KeyValueMapOperations", Arrays.asList("keyvaluemapoperations", "kvm", "kvmops", "kv") );
		supportedPolicies.put("Ldap", Arrays.asList("ldap") );
		supportedPolicies.put("MessageLogging", Arrays.asList("messagelogging", "logging", "ml") );
		supportedPolicies.put("OAuthV2", Arrays.asList("oauthv2", "oauth", "oa", "accesstoken", "verify") );
		supportedPolicies.put("GetOAuthV2Info", Arrays.asList("oauthv2info", "oauthinfo", "oai", "accesstoken", "getoauth", "getoa", "go") );
		supportedPolicies.put("SetOAuthV2Info", Arrays.asList("oauthv2info", "oauthinfo", "oai", "accesstoken", "setoauth", "setoa", "so") );
		supportedPolicies.put("DeleteOAuthV2Info", Arrays.asList("oauthv2info", "oauthinfo", "oai", "accesstoken", "deloauth", "deloa", "do") );
		supportedPolicies.put("OAuthV1", Arrays.asList("oauthv1", "oauth", "oa", "accesstoken", "verify") );
		supportedPolicies.put("GetOAuthV1Info", Arrays.asList("oauthv1", "getoauth", "getoa", "go") );
		supportedPolicies.put("DeleteOAuthV1Info", Arrays.asList("oauthv1", "deloauth", "deloa", "do") );
		supportedPolicies.put("Script", Arrays.asList("script", "scr", "py") );
		supportedPolicies.put("Quota", Arrays.asList("quota", "q", "qu") );
		supportedPolicies.put("RaiseFault", Arrays.asList("raisefault", "rf", "fault") );
		supportedPolicies.put("RegularExpressionProtection", Arrays.asList("regex", "re", "tp") );
		supportedPolicies.put("ResetQuota", Arrays.asList("quota", "resetq", "rq") );
		supportedPolicies.put("GenerateSAMLAssertion", Arrays.asList("saml", "sa", "gsaml", "gs") );
		supportedPolicies.put("ValidateSAMLAssertion", Arrays.asList("saml", "sa", "vsaml", "vs") );
		supportedPolicies.put("ServiceCallout", Arrays.asList("callout", "sc") );
		supportedPolicies.put("MessageValidation", Arrays.asList("messagevalidation", "mv", "messval") );
		supportedPolicies.put("SpikeArrest", Arrays.asList("spikearrest", "spike", "sa") );
		supportedPolicies.put("SharedFlow", Arrays.asList("sf") );
		supportedPolicies.put("StatisticsCollector", Arrays.asList("stats", "statcoll", "sc", "___collect") );
		supportedPolicies.put("VerifyAPIKey", Arrays.asList("verifyapikey", "apikey", "va", "verify") );
		supportedPolicies.put("XMLThreatProtection", Arrays.asList("xmltp", "tp") );
		supportedPolicies.put("XMLToJSON", Arrays.asList("xmltojson", "x2j", "xtoj") );
		supportedPolicies.put("XSL", Arrays.asList("xsl") );				

	}
	
	
	@Override
	public void scanFile(XmlFile xmlFile) {
		
	    Document document = xmlFile.getDocument();
	    Node rootNode = document.getDocumentElement();
	    if (rootNode != null) {
	    	
	    	String policyName = rootNode.getNodeName();
	    	if(supportedPolicies.containsKey(policyName)) {
	    		
	    		String nameAttr = rootNode.getAttributes().getNamedItem("name").getNodeValue();
	    		List<String> knownPrefixes = supportedPolicies.get(policyName);

	    		boolean isCompliant = false;
	    		for(String prefix : knownPrefixes) {
	    			Pattern pattern = Pattern.compile(prefix + "[_-].*", Pattern.CASE_INSENSITIVE);   // A separator is mandatory after the prefix  : either '-' or '_' 
	    			Matcher  matcher = pattern.matcher(nameAttr);
	    			
	    			if(matcher.matches()) {
	    				isCompliant = true;
	    				break;
	    			}
	    		}
	    		
	    		if(!isCompliant) {
    				// Create a violation for the root node
    				reportIssue(rootNode, "Policy " + policyName + " should have an indicative prefix. Typical prefixes include : " + knownPrefixes);
	    		}
	    	}
	    }
	}

}
