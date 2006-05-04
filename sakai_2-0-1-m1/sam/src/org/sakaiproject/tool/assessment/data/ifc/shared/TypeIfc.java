package org.sakaiproject.tool.assessment.data.ifc.shared;
import java.util.Date;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface TypeIfc extends java.io.Serializable {
  // This has the exact same list as TypeFacade. Please keep both list updated
  public static Long MULTIPLE_CHOICE = new Long(1);
  public static Long MULTIPLE_CORRECT = new Long(2);
  public static Long MULTIPLE_CHOICE_SURVEY = new Long(3);
  public static Long TRUE_FALSE = new Long(4);
  public static Long ESSAY_QUESTION = new Long(5);
  public static Long FILE_UPLOAD = new Long(6);
  public static Long AUDIO_RECORDING = new Long(7);
  public static Long FILL_IN_BLANK = new Long(8);
  public static Long MATCHING = new Long(9);
  // these are section type available in this site,
  public static Long DEFAULT_SECTION = new Long(21);
  // these are assessment template type available in this site,
  public static Long TEMPLATE_QUIZ = new Long(41);
  public static Long TEMPLATE_HOMEWORK = new Long(42);
  public static Long TEMPLATE_MIDTERM = new Long(43);
  public static Long TEMPLATE_FINAL = new Long(44);
  // these are assessment type available in this site,
  public static Long QUIZ = new Long(61);
  public static Long HOMEWORK = new Long(62);
  public static Long MIDTERM = new Long(63);
  public static Long FINAL = new Long(64);
  public static String SITE_AUTHORITY = "stanford.edu";
  public static String DOMAIN_ASSESSMENT_ITEM = "assessment.item";

  Long getTypeId();

  void setTypeId(Long typeId);

  String getAuthority();

  void setAuthority(String authority);

  String getDomain();

  void setDomain(String domain);

  String getKeyword();

  void setKeyword(String keyword);

  String getDescription();

  void setDescription(String description);

  int getStatus();

  void setStatus(int status);

  String getCreatedBy();

  void setCreatedBy (String createdBy);

  Date getCreatedDate();

  void setCreatedDate (Date createdDate);

  String getLastModifiedBy();

  void setLastModifiedBy (String lastModifiedBy);

  Date getLastModifiedDate();

  void setLastModifiedDate (Date lastModifiedDate);

}