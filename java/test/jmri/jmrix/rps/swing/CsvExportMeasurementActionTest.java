package jmri.jmrix.rps.swing;

import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;

import jmri.jmrix.rps.RpsSystemConnectionMemo;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class CsvExportMeasurementActionTest {

    private RpsSystemConnectionMemo memo = null;

    @Test
    public void testCTor() {
        CsvExportMeasurementAction t = new CsvExportMeasurementAction(memo);
        Assert.assertNotNull("exists",t);
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
        memo = new RpsSystemConnectionMemo();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(CsvExportMeasurementActionTest.class);

}
