package ru.toir.mobile.rfid;

import android.text.TextUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class TagStructureTest {
    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence a = (CharSequence) invocation.getArguments()[0];
                return !(a != null && a.length() > 0);
            }
        });
    }

    @Test
    public void test1() throws Exception {
        TagStructure tag = new TagStructure();
        tag.uuid = "00010203-0405-0607-0809-0a0b0c0d0e0f";
        tag.taskId = 0x10111213;
        tag.taskTypeId = 0x14151617;
        tag.start = 0x18191a1b;
        tag.end = 0x1c1d1e1f;
        tag.status = 0x20212223;
        tag.verdictId = 0x24252627;
        tag.userId = 0x28292a2b;
        tag.equipmentStatusId = 0x2c2d2e2f;
        tag.phone = "+73519008475";
        tag.controlCode = 0x3c3d3e3f;

        byte[] data = tag.getBinary();
        assertNotNull(data);

        for (int i = 0; i < TagStructure.OFFSET_PHONE; i++) {
            assertEquals(i, data[i]);
        }

        for (int i = TagStructure.OFFSET_PHONE, j = 0; i < TagStructure.OFFSET_PHONE + TagStructure.SIZE_PHONE; i++, j++) {
            assertEquals((byte)tag.phone.charAt(j), data[i]);
        }

        for (int i = TagStructure.OFFSET_CONTROLCODE; i < TagStructure.OFFSET_CONTROLCODE + TagStructure.SIZE_CONTROLCODE; i++) {
            assertEquals(i, data[i]);
        }

    }
}