package com.sherchen.heartrate.views.animator;

import android.animation.TypeEvaluator;

import java.util.ArrayList;

/**
 * The description of use:
 * <br />
 * Created time:2014/7/8 9:25
 * Created by Dave
 */
public class LinearEvaluator implements TypeEvaluator<Number> {
    private Translate m_Translate;
    private float m_YStart;
    private float m_YEnd;
    private ArrayList<EvaluatorListener> m_Listeners = new ArrayList<EvaluatorListener>();

    public LinearEvaluator(Translate translate) {
        m_Translate = translate;
    }

    public LinearEvaluator(float yStart, float yEnd) {
        m_YStart = yStart;
        m_YEnd = yEnd;
    }

    public void addListener(EvaluatorListener listener) {
        m_Listeners.add(listener);
    }

    public void addListener(EvaluatorListener... listeners) {
        for (EvaluatorListener listener : listeners) {
            m_Listeners.add(listener);
        }
    }

    public void removeListener(EvaluatorListener listener) {
        m_Listeners.remove(listener);
    }

    public void clear() {
        m_Listeners.clear();
    }

    @Override
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float start = startValue.floatValue();
        float end = endValue.floatValue();
        float xCurrent = EvaluateUtil.getEvaluate(fraction, start, end);
        float yCurrent = getY(xCurrent, start, end);
        for (EvaluatorListener listener : m_Listeners) {
            listener.onEvalutor(xCurrent, yCurrent);
        }
        return xCurrent;
    }

    private float getY(float current, float start, float end) {
//        return (current - start) * (m_YStart - m_YEnd) / (end - start) + m_YEnd;

        return m_YStart - (start - current) * (m_YStart - m_YEnd) / (start - end);
    }

    public interface EvaluatorListener {
        void onEvalutor(float x, float y);
    }
}
