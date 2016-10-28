package uk.ithis.mule.interceptor;

import org.junit.Test;
import org.mule.tck.junit4.FunctionalTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxInterceptorTestCase extends FunctionalTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TxInterceptorTestCase.class);

    @Override
    public String getConfigFile() {
        return "tx-interceptor-test.xml";
    }

    @Test
    public void testStuffTestCase() throws Exception {
        testFlow("http-testFlow");
    }
}
