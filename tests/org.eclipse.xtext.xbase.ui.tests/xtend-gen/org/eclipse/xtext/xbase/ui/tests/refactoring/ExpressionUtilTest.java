package org.eclipse.xtext.xbase.ui.tests.refactoring;

import com.google.common.base.Objects;
import javax.inject.Inject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.tests.XbaseInjectorProvider;
import org.eclipse.xtext.xbase.ui.refactoring.ExpressionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jan Koehnlein
 */
@RunWith(value = XtextRunner.class)
@InjectWith(value = XbaseInjectorProvider.class)
@SuppressWarnings("all")
public class ExpressionUtilTest {
  @Inject
  private ExpressionUtil util;
  
  @Inject
  private ParseHelper<XExpression> parseHelper;
  
  @Inject
  private ValidationTestHelper validationHelper;
  
  @Inject
  private ILocationInFileProvider locationInFileProvider;
  
  @Test
  public void testSelectedExpressions() {
    this.assertExpressionSelected("$$123+456", "123");
    this.assertExpressionSelected("$1$23+456", "123");
    this.assertExpressionSelected("$12$3+456", "123");
    this.assertExpressionSelected("$123$+456", "123");
    this.assertExpressionSelected("1$$23+456", "123");
    this.assertExpressionSelected("12$$3+456", "123");
    this.assertExpressionSelected("123$$+456", "123");
    this.assertExpressionSelected("123+$$456", "456");
    this.assertExpressionSelected("123$+$456", "123+456");
    this.assertExpressionSelected("12$3+$456", "123+456");
    this.assertExpressionSelected("123$+4$56", "123+456");
    this.assertExpressionSelected("if($$true) null", "true");
    this.assertExpressionSelected("if(true$$) null", "true");
    this.assertExpressionSelected("if(true)$$ null", "if(true) null");
    this.assertExpressionSelected("if(true) null$$ else null", "null");
    this.assertExpressionSelected("if(true) null $$else null", "if(true) null else null");
  }
  
  @Test
  public void testInsertionPoint() {
    this.assertInsertionPoint("{ val x = 1 $2+3 }", "2+3");
    this.assertInsertionPoint("{ val x = 1 2$+3 }", "2+3");
    this.assertInsertionPoint("{ val x = 1 2+$3 }", "2+3");
    this.assertInsertionPoint("{ val x = $1 2+$3 }", "val x = 1");
    this.assertInsertionPoint("{ val x = 1$ 2+$3 }", "val x = 1");
  }
  
  @Test
  public void testInsertionPointIf() {
    this.assertInsertionPoint("if($1==2) true", null);
    this.assertInsertionPoint("{ if($1==2) true }", "if(1==2) true");
    this.assertInsertionPoint("if(1==2) $true", "true");
    this.assertInsertionPoint("if(1==2) true else $false", "false");
    this.assertInsertionPoint("if(1==2) { val x = 7 + $8 }", "val x = 7 + 8");
  }
  
  @Test
  public void testInsertionPointSwitch() {
    this.assertInsertionPoint("switch 1 { case 1: 2+$3 }", "2+3");
    this.assertInsertionPoint("switch 1 { case 2: true default: 2+$3 }", "2+3");
  }
  
  @Test
  public void testInsertionPointWhile() {
    this.assertInsertionPoint("while(true) new $String()", "new String()");
    this.assertInsertionPoint("while($true) new $String()", null);
    this.assertInsertionPoint("do new $String() while(true)", "new String()");
    this.assertInsertionPoint("do new String() while($true)", null);
  }
  
  @Test
  public void testInsertionPointFor() {
    this.assertInsertionPoint("for(i: 1..2) new $String()", "new String()");
    this.assertInsertionPoint("for(i: $1..2) new $String()", null);
  }
  
  @Test
  public void testInsertionPointClosure() {
    this.assertInsertionPoint("[|2+$3]", "2+3");
  }
  
  @Test
  public void testInsertionPointTry() {
    this.assertInsertionPoint("try 2+$3 catch(Exception e) true", "2+3");
    this.assertInsertionPoint("try true catch(Exception e) new $String()", "new String()");
    this.assertInsertionPoint("try true finally new $String()", "new String()");
  }
  
  protected void assertExpressionSelected(final String modelWithSelectionMarkup, final String expectedSelection) {
    final String cleanedModel = modelWithSelectionMarkup.replaceAll("\\$", "");
    final XExpression expression = this.parse(cleanedModel);
    final int selectionOffset = modelWithSelectionMarkup.indexOf("$");
    int _lastIndexOf = modelWithSelectionMarkup.lastIndexOf("$");
    int _minus = (_lastIndexOf - selectionOffset);
    final int selectionLength = (_minus - 1);
    Resource _eResource = expression.eResource();
    TextSelection _textSelection = new TextSelection(selectionOffset, selectionLength);
    final XExpression selectedExpression = this.util.findSelectedExpression(((XtextResource) _eResource), _textSelection);
    final ITextRegion selectedRegion = this.locationInFileProvider.getFullTextRegion(selectedExpression);
    int _offset = selectedRegion.getOffset();
    int _offset_1 = selectedRegion.getOffset();
    int _length = selectedRegion.getLength();
    int _plus = (_offset_1 + _length);
    String _substring = cleanedModel.substring(_offset, _plus);
    Assert.assertEquals(expectedSelection, _substring);
  }
  
  protected void assertInsertionPoint(final String modelWithInsertionMarkup, final String expectedSuccessor) {
    final String cleanedModel = modelWithInsertionMarkup.replaceAll("\\$", "");
    final XExpression expression = this.parse(cleanedModel);
    final int selectionOffset = modelWithInsertionMarkup.indexOf("$");
    Resource _eResource = expression.eResource();
    TextSelection _textSelection = new TextSelection(selectionOffset, 0);
    final XExpression selectedExpression = this.util.findSelectedExpression(((XtextResource) _eResource), _textSelection);
    final Pair<XBlockExpression,XExpression> point = this.util.findInsertionPointForVariableDeclaration(selectedExpression);
    boolean _equals = Objects.equal(expectedSuccessor, null);
    if (_equals) {
      Assert.assertNull(point);
    } else {
      Assert.assertNotNull(point);
      XExpression _second = point.getSecond();
      final ITextRegion selectedRegion = this.locationInFileProvider.getFullTextRegion(_second);
      int _offset = selectedRegion.getOffset();
      int _offset_1 = selectedRegion.getOffset();
      int _length = selectedRegion.getLength();
      int _plus = (_offset_1 + _length);
      String _substring = cleanedModel.substring(_offset, _plus);
      Assert.assertEquals(expectedSuccessor, _substring);
    }
  }
  
  protected XExpression parse(final CharSequence string) {
    try {
      XExpression _xblockexpression = null;
      {
        final XExpression expression = this.parseHelper.parse(string);
        this.validationHelper.assertNoErrors(expression);
        _xblockexpression = (expression);
      }
      return _xblockexpression;
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}