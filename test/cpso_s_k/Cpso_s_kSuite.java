/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpso_s_k;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Peter
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({cpso_s_k.ParticleTest.class, cpso_s_k.CoordinatesTest.class, cpso_s_k.CPSO_S_kTest.class, cpso_s_k.MainTest.class, cpso_s_k.SwarmTest.class})
public class Cpso_s_kSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
