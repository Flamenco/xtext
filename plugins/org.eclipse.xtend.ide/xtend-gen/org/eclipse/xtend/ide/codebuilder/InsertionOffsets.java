/**
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.ide.codebuilder;

import com.google.common.collect.Iterables;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.core.xtend.XtendClass;
import org.eclipse.xtend.core.xtend.XtendConstructor;
import org.eclipse.xtend.core.xtend.XtendField;
import org.eclipse.xtend.core.xtend.XtendMember;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;

/**
 * Calculates where to insert new members into exisitng Xtend code.
 * 
 * @author Jan Koehnlein
 */
@SuppressWarnings("all")
public class InsertionOffsets {
  public int getNewFieldInsertOffset(final EObject call, final XtendClass ownerClass) {
    EList<XtendMember> _members = ownerClass.getMembers();
    boolean _isEmpty = _members.isEmpty();
    if (_isEmpty) {
      return this.inEmpty(ownerClass);
    }
    EList<XtendMember> _members_1 = ownerClass.getMembers();
    Iterable<XtendField> _filter = Iterables.<XtendField>filter(_members_1, XtendField.class);
    final XtendField lastDefinedField = IterableExtensions.<XtendField>last(_filter);
    boolean _equals = ObjectExtensions.operator_equals(lastDefinedField, null);
    if (_equals) {
      EList<XtendMember> _members_2 = ownerClass.getMembers();
      XtendMember _head = IterableExtensions.<XtendMember>head(_members_2);
      return this.before(_head);
    } else {
      return this.after(lastDefinedField);
    }
  }
  
  public int getNewMethodInsertOffset(final EObject call, final XtendClass ownerClass) {
    final XtendMember callingMember = EcoreUtil2.<XtendMember>getContainerOfType(call, XtendMember.class);
    boolean _equals = ObjectExtensions.operator_equals(callingMember, null);
    if (_equals) {
      IllegalStateException _illegalStateException = new IllegalStateException("Missing method not called from Xtend member");
      throw _illegalStateException;
    }
    EList<XtendMember> _members = ownerClass.getMembers();
    boolean _contains = _members.contains(callingMember);
    if (_contains) {
      return this.after(callingMember);
    } else {
      EList<XtendMember> _members_1 = ownerClass.getMembers();
      boolean _isEmpty = _members_1.isEmpty();
      if (_isEmpty) {
        return this.inEmpty(ownerClass);
      } else {
        EList<XtendMember> _members_2 = ownerClass.getMembers();
        XtendMember _last = IterableExtensions.<XtendMember>last(_members_2);
        return this.after(_last);
      }
    }
  }
  
  public int getNewConstructorInsertOffset(final EObject call, final XtendClass ownerClass) {
    EList<XtendMember> _members = ownerClass.getMembers();
    Iterable<XtendConstructor> _filter = Iterables.<XtendConstructor>filter(_members, XtendConstructor.class);
    final XtendConstructor lastDefinedConstructor = IterableExtensions.<XtendConstructor>last(_filter);
    boolean _equals = ObjectExtensions.operator_equals(lastDefinedConstructor, null);
    if (_equals) {
      return this.getNewFieldInsertOffset(call, ownerClass);
    } else {
      return this.after(lastDefinedConstructor);
    }
  }
  
  protected int before(final EObject element) {
    ICompositeNode _findActualNodeFor = NodeModelUtils.findActualNodeFor(element);
    int _offset = _findActualNodeFor.getOffset();
    return _offset;
  }
  
  protected int after(final EObject element) {
    int _xblockexpression = (int) 0;
    {
      final ICompositeNode node = NodeModelUtils.findActualNodeFor(element);
      int _offset = node.getOffset();
      int _length = node.getLength();
      int _plus = (_offset + _length);
      _xblockexpression = (_plus);
    }
    return _xblockexpression;
  }
  
  protected int inEmpty(final XtendClass ownerClass) {
    int _xblockexpression = (int) 0;
    {
      final ICompositeNode classNode = NodeModelUtils.findActualNodeFor(ownerClass);
      Iterable<ILeafNode> _leafNodes = classNode.getLeafNodes();
      final Function1<ILeafNode,Boolean> _function = new Function1<ILeafNode,Boolean>() {
          public Boolean apply(final ILeafNode it) {
            String _text = it.getText();
            boolean _equals = ObjectExtensions.operator_equals(_text, "{");
            return Boolean.valueOf(_equals);
          }
        };
      final ILeafNode openingBraceNode = IterableExtensions.<ILeafNode>findFirst(_leafNodes, _function);
      int _xifexpression = (int) 0;
      boolean _notEquals = ObjectExtensions.operator_notEquals(openingBraceNode, null);
      if (_notEquals) {
        int _offset = openingBraceNode.getOffset();
        int _plus = (_offset + 1);
        _xifexpression = _plus;
      } else {
        int _offset_1 = classNode.getOffset();
        int _length = classNode.getLength();
        int _plus_1 = (_offset_1 + _length);
        _xifexpression = _plus_1;
      }
      _xblockexpression = (_xifexpression);
    }
    return _xblockexpression;
  }
}