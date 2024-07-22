package org.openmrs.module.patientflags.dao.impl;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import org.openmrs.module.fhir2.TestFhirSpringConfiguration;
import org.openmrs.module.patienttflags.dao.impl.FhirFlagDaoImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestFhirSpringConfiguration.class, inheritLocations = false)
public class FhirFlagDaoImplTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

    private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "patientflagtest-dataset.xml";

    @Autowired
    private SessionFactory sessionFactory;

    private FhirFlagDaoImpl fhirFlagDao;

    @Before
    public void setup() throws Exception {

        fhirFlagDao = new FhirFlagDaoImpl();
        fhirFlagDao.setSessionFactory(sessionFactory);
//        executeDataSet(TEST_DATASET_FILE);
    }

    @Test
    public void test() {

    }
}
