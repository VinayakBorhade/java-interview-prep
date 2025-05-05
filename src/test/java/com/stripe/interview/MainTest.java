package com.stripe.interview;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

class Answer {
    Integer i;
    String s;
	Boolean flag;
};

public class MainTest {
    @Test
    public void testUseGuavaForSomeReason() throws Exception {
        String actual = Main.useGuavaForSomeReason("hello!<a href>");
        Assert.assertEquals("uh oh, the tests failed!", "hello!&lt;a href&gt;", actual);

    }

	@Test
	public void testGson() {
		String json = """
                {
                	"i" : 1,
                	"s" : "hello",
                	"flag" : true
                }
                """;
		Answer ans = new Gson().fromJson(json, new TypeToken<Answer>() {}.getType());
		System.out.println("ans.i = " + ans.i);
		System.out.println("ans.s = " + ans.s);
		System.out.println("ans.flag = " + ans.flag);
		assertTrue(1 == ans.i);
	}
}