package ai.houzi.custom;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.println("cos(30)="+cos(30));
        System.out.println("cos(150)="+cos(150));
        System.out.println("cos(270)="+cos(270));
        System.out.println("sin(30)="+sin(30));
        System.out.println("sin(150)="+sin(150));
        System.out.println("sin(270)="+sin(270));
    }

    private float cos(int angle) {
        return (float) Math.cos(angle * Math.PI / 180);
    }

    private float sin(int angle) {
        return (float) Math.sin(angle * Math.PI / 180);
    }
}