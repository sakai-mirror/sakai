package org.sakaiproject.tool.assessment.data.ifc.assessment;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

public interface ItemTextIfc
    extends java.io.Serializable
{
  Long getId();

  void setId(Long id);

  ItemDataIfc getItem();

  void setItem(ItemDataIfc item);

  Long getSequence();

  void setSequence(Long sequence);

  String getText();

  void setText(String text);

  Set getAnswerSet();

  void setAnswerSet(Set answerSet);

  ArrayList getAnswerArray();
 
  ArrayList getAnswerArraySorted();
}
