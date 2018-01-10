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
package com.arkea.satd.sonar.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import com.arkea.satd.sonar.xml.ApigeeXmlRulesDefinition;
import com.arkea.satd.sonar.xml.checks.CheckRepository;

public class ApigeeXmlRulesDefinitionTest {

	@Test
	public void test() {
		ApigeeXmlRulesDefinition rulesDefinition = new ApigeeXmlRulesDefinition();
		RulesDefinition.Context context = new RulesDefinition.Context();
		rulesDefinition.define(context);
		RulesDefinition.Repository repository = context.repository("apigee-xml");

		assertThat(repository.name()).isEqualTo("Apigee XML");
		assertThat(repository.language()).isEqualTo("xml");
		assertThat(repository.rules()).hasSize(CheckRepository.getChecks().size());

		RulesDefinition.Rule alertUseRule = repository.rule("DescriptionCheck");
		assertThat(alertUseRule).isNotNull();
		assertThat(alertUseRule.name()).isEqualTo("Description tags should meet minimum length requirements");

		for (Rule rule : repository.rules()) {
			for (RulesDefinition.Param param : rule.params()) {
				assertThat(param.description()).as("description for " + param.key()).isNotEmpty();
			}
		}
	}

}
