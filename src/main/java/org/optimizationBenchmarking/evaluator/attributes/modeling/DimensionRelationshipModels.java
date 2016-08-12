package org.optimizationBenchmarking.evaluator.attributes.modeling;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.impl.EListSequenceMode;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentSequenceable;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.ml.fitting.impl.DefaultFunctionFitter;
import org.optimizationBenchmarking.utils.ml.fitting.models.CubicModel;
import org.optimizationBenchmarking.utils.ml.fitting.models.ExpLinearModelOverLogX;
import org.optimizationBenchmarking.utils.ml.fitting.models.ExponentialDecayModel;
import org.optimizationBenchmarking.utils.ml.fitting.models.GompertzModel;
import org.optimizationBenchmarking.utils.ml.fitting.models.LogisticModelWithOffsetOverLogX;
import org.optimizationBenchmarking.utils.ml.fitting.quality.WeightedRootMeanSquareError;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFunctionFitter;
import org.optimizationBenchmarking.utils.ml.fitting.spec.ParametricUnaryFunction;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** This class provides models for relationships between dimensions. */
public final class DimensionRelationshipModels {

  /** the hidden constructor */
  private DimensionRelationshipModels() {
    ErrorUtils.doNotCall();
  }

  /**
   * Check whether the given modeling constellation is viable
   *
   * @param isXTime
   *          is the {@code x}-dimension a time dimension ({@code true}) or
   *          an objective dimension ({@code false})
   * @param isYTime
   *          is the {@code y}-dimension a time dimension ({@code true}) or
   *          an objective dimension ({@code false})
   */
  static final void _checkDimensions(final boolean isXTime,
      final boolean isYTime) {
    if (isYTime && (!isXTime)) {
      throw new IllegalArgumentException(//
          "Only time-objective, objective-objective, and time-time relatioships can be modeled, but you specified an objective-time relationship."); //$NON-NLS-1$
    }
  }

  /**
   * Obtain the models for a given dimension relationship
   *
   * @param x
   *          the input dimension
   * @param y
   *          the output dimension
   * @return the list of models
   */
  public static final ArrayListView<ParametricUnaryFunction> getModels(
      final IDimension x, final IDimension y) {
    return DimensionRelationshipModels.getModels(
        x.getDimensionType().isTimeMeasure(),
        y.getDimensionType().isTimeMeasure());
  }

  /**
   * Obtain the models for a given dimension relationship
   *
   * @param isXTime
   *          is the {@code x}-dimension a time dimension ({@code true}) or
   *          an objective dimension ({@code false})
   * @param isYTime
   *          is the {@code y}-dimension a time dimension ({@code true}) or
   *          an objective dimension ({@code false})
   * @return the list of models
   */
  public static final ArrayListView<ParametricUnaryFunction> getModels(
      final boolean isXTime, final boolean isYTime) {
    DimensionRelationshipModels._checkDimensions(isXTime, isYTime);
    if (isXTime == isYTime) {
      return __EqualType.MODELS;
    }
    return __TimeObjective.MODELS;
  }

  /**
   * Obtain the function fitters
   *
   * @return the function fitters
   */
  static final ArrayListView<IFunctionFitter> _getFitters() {
    return DefaultFunctionFitter.getAllInstance();
  }

  /**
   * Describe the applied function fitting procedure
   *
   * @param textOut
   *          the text output device
   * @param textCase
   *          the text case
   * @param isXTime
   *          is the first dimension a time dimension?
   * @param isYTime
   *          is the second dimension a time dimension?
   * @return the next text case
   */
  public static final ETextCase printModelingDescription(
      final ITextOutput textOut, final ETextCase textCase,
      final boolean isXTime, final boolean isYTime) {
    final ArrayListView<IFunctionFitter> fitters;
    final ArrayListView<ParametricUnaryFunction> models;
    final int fitterSize, modelSize;

    ETextCase next;
    fitters = DimensionRelationshipModels._getFitters();
    switch (fitterSize = fitters.size()) {
      case 0: {
        textCase.appendWords("no function fitters are found", //$NON-NLS-1$
            textOut);
        textOut.append('.');
        return textCase.nextAfterSentenceEnd();
      }
      case 1: {
        next = textCase.appendWord("a", textOut);//$NON-NLS-1$
        textOut.append(' ');
        next = fitters.get(0).printLongName(textOut, next);
        textOut.append(' ');
        next = next.appendWord("is", textOut);//$NON-NLS-1$
        break;
      }
      default: {
        next = InTextNumberAppender.INSTANCE.appendTo(fitterSize, textCase,
            textOut);
        textOut.append(' ');
        next = textCase.appendWords("different algorithms", textOut);//$NON-NLS-1$
        textOut.append(',');
        textOut.append(' ');
        next = textCase.appendWord("namely", textOut);//$NON-NLS-1$
        next = EListSequenceMode.ENUMERATION.appendSequence(next,
            SemanticComponentSequenceable.wrap(fitters, false, true,
                false),
            textOut, ESequenceMode.AND);
        textOut.append(' ');
        next = textCase.appendWord("are", textOut);//$NON-NLS-1$
      }
    }
    next = next.appendWord(" applied to fit", textOut);//$NON-NLS-1$
    textOut.append(' ');

    models = DimensionRelationshipModels.getModels(isXTime, isYTime);
    switch (modelSize = models.size()) {
      case 0: {
        textCase.appendWords("no models", //$NON-NLS-1$
            textOut);
        textOut.append('.');
        return textCase.nextAfterSentenceEnd();
      }
      case 1: {
        next = models.get(0).printLongName(textOut, textCase);
        break;
      }
      default: {
        next = InTextNumberAppender.INSTANCE.appendTo(modelSize, textCase,
            textOut);
        textOut.append(' ');
        next = textCase.appendWords("different models", textOut);//$NON-NLS-1$
        textOut.append(',');
        textOut.append(' ');
        next = textCase.appendWord("namely", textOut);//$NON-NLS-1$
        next = EListSequenceMode.ENUMERATION.appendSequence(next,
            SemanticComponentSequenceable.wrap(models, false, true, false),
            textOut, ESequenceMode.AND);
      }
    }

    textOut.append(
        " and the best fitting among all models, i.e., the parameterization with the smallest ");//$NON-NLS-1$
    WeightedRootMeanSquareError.printName(textOut, next);
    textOut.append(
        ", is chosen for a given algorithm setup/benchmark instance combination.");//$NON-NLS-1$

    return next.nextAfterSentenceEnd();
  }

  /** the time-objective relationship holder */
  private static final class __TimeObjective {

    /** the models */
    static final ArrayListView<ParametricUnaryFunction> MODELS = //
    new ArrayListView<>(new ParametricUnaryFunction[] { //
        new LogisticModelWithOffsetOverLogX(), //
        new ExponentialDecayModel(), //
        new ExpLinearModelOverLogX(), //
        new GompertzModel(), //
        new CubicModel(), //
    }, false);
  }

  /** the holder for relationships of equally-typed dimensions */
  private static final class __EqualType {

    /** The models attempted for relationships of equal-type dimensions */
    static final ArrayListView<ParametricUnaryFunction> MODELS = //
    new ArrayListView<>(new ParametricUnaryFunction[] { //
        new CubicModel(),//
    }, false);
  }

}
