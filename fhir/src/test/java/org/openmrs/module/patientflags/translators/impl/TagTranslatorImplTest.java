/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientflags.translators.impl;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patienttflags.translators.impl.TagTranslatorImpl;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
public class TagTranslatorImplTest {

    private static final String TAG_UUID = "0f97e14e-cdc2-49ac-9255-b5126f8a5147";

    private static final String TAG_NAME = "tag";

    TagTranslatorImpl tagTranslator;

    CodeableConcept codeableConcept;

    Tag tag;

    @BeforeEach
    public void setup() {
        tagTranslator = new TagTranslatorImpl();
    }

    @Test
    public void shouldTranslateTagToFhirCodeableConcept() {
        tag = new Tag();
        tag.setUuid(TAG_UUID);
        tag.setName(TAG_NAME);
        tag.setRoles(Collections.emptySet());

       codeableConcept = tagTranslator.toFhirResource(tag);

        assertThat(codeableConcept, notNullValue());
        assertThat(codeableConcept.getCoding().get(0).getCode(), equalTo(TAG_NAME));
    }

}
