package org.cmdbuild.auth.user;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptySet;
import java.util.EnumSet;
import org.cmdbuild.auth.DummyPrivilegeSubject;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_ON_FILTER_MISMATCH;
import org.cmdbuild.auth.grant.GrantImpl;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.GrantMode.GM_WRITE;
import org.cmdbuild.auth.grant.GrantPrivilege;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import static org.cmdbuild.auth.grant.GrantUtils.expandPrivileges;
import static org.cmdbuild.auth.grant.GrantUtils.modeToPrivileges;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class UserPrivilegesImplTest {

    @Test
    public void testUserPrivilegesBuilder() {
        Role role = RoleImpl.builder()
                .withName("MyRole")
                .withPrivileges(list(
                        GrantImpl.builder().withObjectType(POT_CLASS).withObject(new DummyPrivilegeSubject("one")).withPrivileges(modeToPrivileges(GM_WRITE)).build(),
                        GrantImpl.builder().withObjectType(POT_CLASS).withObject(new DummyPrivilegeSubject("two")).withPrivileges(modeToPrivileges(GM_READ)).build()
                ))
                .build();

        UserPrivileges userPrivileges = UserPrivilegesImpl.builder()
                .withGroups(role)
                .build();

        assertTrue(userPrivileges.hasReadAccess(new DummyPrivilegeSubject("one")));
        assertTrue(userPrivileges.hasWriteAccess(new DummyPrivilegeSubject("one")));

        assertTrue(userPrivileges.hasReadAccess(new DummyPrivilegeSubject("two")));
        assertFalse(userPrivileges.hasWriteAccess(new DummyPrivilegeSubject("two")));

        assertFalse(userPrivileges.hasReadAccess(new DummyPrivilegeSubject("three")));
        assertFalse(userPrivileges.hasWriteAccess(new DummyPrivilegeSubject("three")));
    }

    @Test
    public void testUserPrivilegesBuilder1() {
        Role role = RoleImpl.builder()
                .withName("MyRole")
                .withPrivileges(list(
                        GrantImpl.builder().withObjectType(POT_CLASS)
                                .withObject(new DummyPrivilegeSubject("one"))
                                .withPrivileges(modeToPrivileges(GM_WRITE))
                                .withPrivilegeFilter("{\"attribute\":{\"simple\":{\"attribute\":\"Code\",\"operator\":\"equal\",\"parameterType\":\"fixed\",\"value\":[\"filterme\"]}}}")
                                .build()
                ))
                .build();

        UserPrivileges userPrivileges = UserPrivilegesImpl.builder()
                .withGroups(role)
                .build();

        UserPrivilegesForObject privilegesForObject = userPrivileges.getPrivilegesForObject(new DummyPrivilegeSubject("one"));

        assertTrue(privilegesForObject.hasPrivilegesWithFilter());

        assertEquals(expandPrivileges(GP_WRITE), privilegesForObject.getMaxPrivilegesForSomeRecords().getServicePrivileges());
        assertEquals(expandPrivileges(GP_WRITE), getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getServicePrivileges());
        assertEquals(emptySet(), privilegesForObject.getMinPrivilegesForAllRecords().getServicePrivileges());

        assertEquals(EnumSet.allOf(GrantPrivilege.class), privilegesForObject.getMaxPrivilegesForSomeRecords().getUiPrivileges());
        assertEquals(EnumSet.allOf(GrantPrivilege.class), getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getUiPrivileges());
        assertEquals(emptySet(), privilegesForObject.getMinPrivilegesForAllRecords().getUiPrivileges());
    }

    @Test
    public void testUserPrivilegesBuilder2() {
        Role role = RoleImpl.builder()
                .withName("MyRole")
                .withPrivileges(list(
                        GrantImpl.builder().withObjectType(POT_CLASS)
                                .withObject(new DummyPrivilegeSubject("one"))
                                .withPrivileges(modeToPrivileges(GM_WRITE))
                                .withCustomPrivileges(map(GDCP_ON_FILTER_MISMATCH, serializeEnum(GM_READ)))
                                .withPrivilegeFilter("{\"attribute\":{\"simple\":{\"attribute\":\"Code\",\"operator\":\"equal\",\"parameterType\":\"fixed\",\"value\":[\"filterme\"]}}}")
                                .build()
                ))
                .build();

        UserPrivileges userPrivileges = UserPrivilegesImpl.builder()
                .withGroups(role)
                .build();

        UserPrivilegesForObject privilegesForObject = userPrivileges.getPrivilegesForObject(new DummyPrivilegeSubject("one"));

        assertTrue(privilegesForObject.hasPrivilegesWithFilter());

        assertEquals(expandPrivileges(GP_WRITE), privilegesForObject.getMaxPrivilegesForSomeRecords().getServicePrivileges());
        assertEquals(expandPrivileges(GP_WRITE), getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getServicePrivileges());
        assertEquals(expandPrivileges(GP_READ), privilegesForObject.getMinPrivilegesForAllRecords().getServicePrivileges());

        assertEquals(EnumSet.allOf(GrantPrivilege.class), privilegesForObject.getMaxPrivilegesForSomeRecords().getUiPrivileges());
        assertEquals(EnumSet.allOf(GrantPrivilege.class), getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getUiPrivileges());
        assertEquals(EnumSet.allOf(GrantPrivilege.class), privilegesForObject.getMinPrivilegesForAllRecords().getUiPrivileges());

        assertNull(privilegesForObject.getMaxPrivilegesForSomeRecords().getCustomPrivileges().get("custom"));
        assertNull(getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getCustomPrivileges().get("custom"));
        assertNull(privilegesForObject.getMinPrivilegesForAllRecords().getCustomPrivileges().get("custom"));

        assertNull(privilegesForObject.getMaxPrivilegesForSomeRecords().getAttributePrivileges().get(ATTR_CODE));
        assertNull(getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getAttributePrivileges().get(ATTR_CODE));
        assertNull(privilegesForObject.getMinPrivilegesForAllRecords().getAttributePrivileges().get(ATTR_CODE));
    }

    @Test
    public void testUserPrivilegesBuilder3() {
        Role role = RoleImpl.builder()
                .withName("MyRole")
                .withPrivileges(list(
                        GrantImpl.builder().withObjectType(POT_CLASS)
                                .withObject(new DummyPrivilegeSubject("one"))
                                .withPrivileges(modeToPrivileges(GM_WRITE))
                                .withCustomPrivileges(map(GDCP_ON_FILTER_MISMATCH, serializeEnum(GM_READ), "custom", true))
                                .withAttributePrivileges(map(ATTR_CODE, GAP_READ))
                                .withPrivilegeFilter("{\"attribute\":{\"simple\":{\"attribute\":\"Code\",\"operator\":\"equal\",\"parameterType\":\"fixed\",\"value\":[\"filterme\"]}}}")
                                .build()
                ))
                .build();

        UserPrivileges userPrivileges = UserPrivilegesImpl.builder()
                .withGroups(role)
                .build();

        UserPrivilegesForObject privilegesForObject = userPrivileges.getPrivilegesForObject(new DummyPrivilegeSubject("one"));

        assertTrue(privilegesForObject.hasPrivilegesWithFilter());

        assertEquals(expandPrivileges(GP_WRITE), privilegesForObject.getMaxPrivilegesForSomeRecords().getServicePrivileges());
        assertEquals(expandPrivileges(GP_WRITE), getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getServicePrivileges());
        assertEquals(expandPrivileges(GP_READ), privilegesForObject.getMinPrivilegesForAllRecords().getServicePrivileges());

        assertEquals(true, toBoolean(privilegesForObject.getMaxPrivilegesForSomeRecords().getCustomPrivileges().get("custom")));
        assertEquals(true, toBoolean(getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getCustomPrivileges().get("custom")));
        assertEquals(true, toBoolean(privilegesForObject.getMinPrivilegesForAllRecords().getCustomPrivileges().get("custom")));

        assertEquals(expandPrivileges(GAP_READ), privilegesForObject.getMaxPrivilegesForSomeRecords().getAttributePrivileges().get(ATTR_CODE));
        assertEquals(expandPrivileges(GAP_READ), getOnlyElement(privilegesForObject.getPrivilegeGroupsWithFilter()).getAttributePrivileges().get(ATTR_CODE));
        assertEquals(expandPrivileges(GAP_READ), privilegesForObject.getMinPrivilegesForAllRecords().getAttributePrivileges().get(ATTR_CODE));
    }
}
