/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtend.ide.tests.contentassist

import org.junit.Test

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
class Bug402876Test extends AbstractXtendContentAssistBugTest {
	
	@Test def void testExtensionParameter_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int i, extension String s) {
					i.
		''')
			.assertProposal('subSequence()')
			.withDisplayString('subSequence(int endIndex) : CharSequence - String')
	}
	
	@Test def void testExtensionParameter_02() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int i, extension String s) {
					i.
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	@Test def void testExtensionParameterWithImplicitArgument_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int it, extension String s) {
					
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	@Test def void testExtensionLocal_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int i) {
					val extension String s = null
					i.
		''')
			.assertProposal('subSequence()')
			.withDisplayString('subSequence(int endIndex) : CharSequence - String')
	}
	
	@Test def void testExtensionLocal_02() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int i) {
					val extension String s = null
					i.
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	@Test def void testExtensionLocalWithImplicitArgument_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int it) {
					val extension String s = null
					
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	@Test def void testExtensionLambdaParameter_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int it) {
					[ extension String s |
					
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	@Test def void testExtensionForLoopParameter_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int it) {
					for(extension String s : newArrayList) 
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	@Test def void testExtensionCatchParameter_01() throws Exception {
		newBuilder.append('''
			class C {
				def void m(int it) {
					// illegal catch param does not matter
					try {} catch(extension String s) 
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
	
	@Test def void testInheritedExtensionField_01() throws Exception {
		newBuilder.append('''
			class A {
				protected extension String s
			}
			class C extends A {
				def void m(int it) {
					
		''')
			.assertProposal('charAt')
			.withDisplayString('charAt : char - String.charAt()')
	}
	
}