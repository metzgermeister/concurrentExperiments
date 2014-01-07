package util.concurrent;

import org.junit.Test;

public class ValidateTest {

    @Test
    public void shouldCheckState() throws Exception {
        Validate.checkState(true, "some message");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateException() throws Exception {
        Validate.checkState(false, "error message");
    }

    @Test
    public void shouldCheckArgument() throws Exception {
        Validate.checkArgument(true, "some message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException() throws Exception {
        Validate.checkArgument(false, "error message");
    }
}
