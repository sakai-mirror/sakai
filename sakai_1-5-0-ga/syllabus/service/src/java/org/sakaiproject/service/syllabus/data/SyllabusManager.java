
/**
 * @author jlannan
 * @version $Id: SyllabusManager.java,v 1.6 2004/12/14 20:32:37 cwen.iupui.edu Exp $
 */
package org.sakaiproject.service.syllabus.data;

import java.util.Set;


public interface SyllabusManager
{
  /**
   * creates an SyllabusItem
   */
  public SyllabusItem createSyllabusItem(String userId, String contextId,
      String redirectURL);

  public SyllabusItem getSyllabusItemByUserAndContextIds(final String userId,
      final String contextId);

  public void saveSyllabusItem(SyllabusItem item);
  
  public void addSyllabusToSyllabusItem(final SyllabusItem syllabusItem, final SyllabusData syllabusData);
  
  public void removeSyllabusFromSyllabusItem(final SyllabusItem syllabusItem, final SyllabusData syllabusData);
  
  public SyllabusData createSyllabusDataObject(String title, Integer position,
      String assetId, String view, String status, String emailNotification);
  
  public Set getSyllabiForSyllabusItem(final SyllabusItem syllabusItem);
  
  public void swapSyllabusDataPositions(final SyllabusItem syllabusItem, final SyllabusData d1, final SyllabusData d2);
  
  public void saveSyllabus(SyllabusData data);
  
  public Integer findLargestSyllabusPosition(final SyllabusItem syllabusItem);
  
  public SyllabusItem getSyllabusItemByContextId(final String contextId);
}