package uk.ac.cam.caret.sakai.rwiki.model.test;

import uk.ac.cam.caret.sakai.rwiki.model.NameHelper;
import junit.framework.TestCase;

public class NameHelperTest extends TestCase {

    String localName = "Foo";
    String realm = "bar";
    String globalName = "bar.Foo";
    String otherRealm = "realm";

    String unnormalizedName = "name with spaces";
    String normalizedName = "Name With Spaces";
    
    public NameHelperTest(String name) {
        super(name);
    }
    
    /*
     * Test method for 'uk.ac.cam.caret.sakai.rwiki.bean.helper.NameHelper.globaliseName(String, String)'
     */
    public void testGlobaliseName() {
        
        String globalised = NameHelper.globaliseName(localName,realm);
        assertTrue("GlobaliseName doesn't globalise local names properly: ",globalName.equals(globalised));
        
        globalised = NameHelper.globaliseName(globalName, otherRealm);
        assertTrue("GlobaliseName doesn't globalise glocal names properly: ", globalName.equals(globalised));
    }

    /*
     * Test method for 'uk.ac.cam.caret.sakai.rwiki.bean.helper.NameHelper.localizeName(String, String)'
     */
    public void testLocalizeName() {
        String localized = NameHelper.localizeName(globalName,realm);
        assertTrue("LocalizeName doesn't localize global names in the same realm properly", localName.equals(localized));
        
        localized = NameHelper.localizeName(globalName, otherRealm);
        assertTrue("LocalizeName doesn't localize global names in other realms properly", globalName.equals(localized));

        localized = NameHelper.localizeName(localName, otherRealm);
        assertTrue("LocalizeName doesn't localize local names in other realms properly", localName.equals(localized));
    }

    /*
     * Test method for 'uk.ac.cam.caret.sakai.rwiki.bean.helper.NameHelper.normalizeName(String)'
     */
    public void testNormalizeName() {
        String normalized = NameHelper.normalizeName(unnormalizedName);
        assertTrue("NormalizeName doesn't normalize non-normal names properly", normalizedName.equals(normalized));
        
        normalized = NameHelper.normalizeName(normalizedName);
        assertTrue("NormalizeName doesn't normalize normal names properly",normalizedName.equals(normalized));

    }

    /*
     * Test method for 'uk.ac.cam.caret.sakai.rwiki.bean.helper.NameHelper.localizeRealm(String, String)'
     */
    public void testLocalizeRealm() {
        String localized = NameHelper.localizeSpace(globalName,otherRealm);
        assertTrue("LocalizeRealm doesn't localize globalName realms properly",realm.equals(localized));
        
        localized = NameHelper.localizeSpace(localName, otherRealm);
        assertTrue("LocalizeRealm doesn't localize localName realms properly",otherRealm.equals(localized));

    }

}
